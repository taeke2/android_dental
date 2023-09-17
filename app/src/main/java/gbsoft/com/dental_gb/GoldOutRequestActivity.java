package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ActivityGoldOutRequestBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoldOutRequestActivity extends Activity {
    private ActivityGoldOutRequestBinding mBinding;

    private String mClientCode, mClientName;
    private String mSearchText = "";

    private ArrayList<GoldListItem> mGoldListItems;

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityGoldOutRequestBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(GoldOutRequestActivity.this.getApplicationContext())) {
            if (!GoldOutRequestActivity.this.isFinishing())
                CommonClass.showDialog(GoldOutRequestActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
        } else {
            initialSet();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);
        mServerPath = sharedPreferences.getString("address", "");

        if (CommonClass.sApiService == null)
            CommonClass.getApiService(mServerPath);

        Intent intent = getIntent();
        mClientCode = intent.getStringExtra("cliNum");
        mClientName = intent.getStringExtra("client");

        mBinding.editPatientName.setOnEditorActionListener(searchEdit);

        mGoldListItems = new ArrayList<>();

        if (mCode == CommonClass.PDL)
            getShutdownCheck();
        else
            getGoldRequestList();
    }

    TextView.OnEditorActionListener searchEdit = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            mSearchText = mBinding.editPatientName.getText().toString();
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    mGoldListItems.clear();
                    getGoldRequestList();
                    break;
                default:
                    return false;
            }

            v.clearFocus();
            v.setFocusable(false);
            v.setText("");
            v.setFocusableInTouchMode(true);
            v.setFocusable(true);

            return false;
        }
    };

    public void getGoldRequestList() {
        Call<ResponseBody> call = CommonClass.sApiService.getGoldRequestList(mClientCode, mSearchText, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        if (jsonArray_len == 0) {
                            mBinding.noListTxt.setVisibility(View.VISIBLE);
                            mBinding.lvRequestInfo.setVisibility(View.GONE);
                        } else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String jsonClientName = c.getString("client");
                                String jsonPatientName = c.getString("patient");
                                String jsonOrderDate = c.getString("ordDate");
                                String jsonRequestCode = c.getString("reqNum");

                                mGoldListItems.add(new GoldListItem(jsonClientName, jsonPatientName, jsonOrderDate, jsonRequestCode));
                            }
                            GoldOutRequestAdapter adapter = new GoldOutRequestAdapter(mGoldListItems);
                            adapter.setOnItemClickListener(pos -> {
                                String requestCode = adapter.mGoldListItems.get(pos).getRequestCode();
                                String requestInfo = adapter.mGoldListItems.get(pos).getPatientName() + "   " + adapter.mGoldListItems.get(pos).getOrderDate();
                                Intent intent = new Intent();
                                intent.putExtra("reqNum", requestCode);
                                intent.putExtra("reqInfo", requestInfo);
                                setResult(RESULT_OK, intent);
                                finish();
                            });
                            adapter.notifyDataSetChanged();
                            mBinding.lvRequestInfo.setAdapter(adapter);
                            mBinding.lvRequestInfo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                            mBinding.noListTxt.setVisibility(View.GONE);
                            mBinding.lvRequestInfo.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!GoldOutRequestActivity.this.isFinishing())
                            CommonClass.showDialog(GoldOutRequestActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!GoldOutRequestActivity.this.isFinishing())
                            CommonClass.showDialog(GoldOutRequestActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!GoldOutRequestActivity.this.isFinishing())
                        CommonClass.showDialog(GoldOutRequestActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!GoldOutRequestActivity.this.isFinishing())
                    CommonClass.showDialog(GoldOutRequestActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    public void getShutdownCheck() {
        Call<ResponseBody> call = CommonClass.sApiService.getShutdownCheck(mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        int mShutdownYn = Integer.parseInt(jsonObject.getString("down"));

                        if (mShutdownYn == 1) {
                            if (!GoldOutRequestActivity.this.isFinishing()) {
                                CommonClass.showDialog(GoldOutRequestActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                            }
                        } else {
                            getGoldRequestList();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!GoldOutRequestActivity.this.isFinishing())
                            CommonClass.showDialog(GoldOutRequestActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!GoldOutRequestActivity.this.isFinishing())
                            CommonClass.showDialog(GoldOutRequestActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!GoldOutRequestActivity.this.isFinishing())
                        CommonClass.showDialog(GoldOutRequestActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!GoldOutRequestActivity.this.isFinishing())
                    CommonClass.showDialog(GoldOutRequestActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }
}
