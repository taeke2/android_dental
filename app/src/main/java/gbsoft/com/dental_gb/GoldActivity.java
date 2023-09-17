package gbsoft.com.dental_gb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ActivityGoldBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoldActivity extends AppCompatActivity {
    private ActivityGoldBinding mBinding;

    private String mSearchText = "";
    private ArrayList<GoldListItem> mGoldListItems;

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        mBinding = ActivityGoldBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(GoldActivity.this.getApplicationContext())) {
            if (!GoldActivity.this.isFinishing())
                CommonClass.showDialog(GoldActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
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

        mBinding.searchText.setOnEditorActionListener(searchEdit);
        mBinding.btnReset.setOnClickListener(resetButtonClick);
        mBinding.btnReset.setOnTouchListener(resetButtonTouch);

        mGoldListItems = new ArrayList<>();

        if (mCode == CommonClass.PDL)
            getShutdownCheck();
        else
            getGoldData();
    }

    /**
     * 로딩창 열기
     * */
    private void loadingProgressOpen(){
        mProgressDialog = new ProgressDialog(GoldActivity.this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.setCanceledOnTouchOutside(false); // 다이얼로그 밖에 터치 시 종료
        mProgressDialog.setCancelable(false); // 다이얼로그 취소 가능 (back key)
        mProgressDialog.show();
    }

    /**
     * 로딩창 닫기
     * */
    public void loadingProgressClose(){
        if(mProgressDialog == null) return;

        if(mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    TextView.OnEditorActionListener searchEdit = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            mSearchText = mBinding.searchText.getText().toString();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(mBinding.searchText, 0);

            if (mSearchText.equals("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.enter_search), Toast.LENGTH_SHORT).show();
                v.clearFocus();
                v.setFocusable(false);
                v.setFocusableInTouchMode(true);
                v.setFocusable(true);

                return true;
            }

            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    imm.hideSoftInputFromWindow(mBinding.searchText.getWindowToken(), 0);
                    mGoldListItems.clear();
                    getGoldData();
                    break;
                default:
                    return false;
            }

            v.clearFocus();
            v.setFocusable(false);
            //v.setText("");
            v.setFocusableInTouchMode(true);
            v.setFocusable(true);

            return false;
        }
    };

    View.OnClickListener resetButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mGoldListItems.clear();
            mSearchText = "";
            getGoldData();
        }
    };

    View.OnTouchListener resetButtonTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mBinding.btnReset.setBackground(ContextCompat.getDrawable(GoldActivity.this, R.drawable.border2));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mBinding.btnReset.setBackground(ContextCompat.getDrawable(GoldActivity.this, R.drawable.border));
            }
            return false;
        }
    };

    public void getGoldData() {
        Call<ResponseBody> call = CommonClass.sApiService.getGoldList(mSearchText, mId, mIp);
        loadingProgressOpen();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        ArrayList<String> inQuantity = new ArrayList<>();
                        if (jsonArray_len == 0){
                            mBinding.noListTxt.setVisibility(View.VISIBLE);
                            mBinding.listGold.setVisibility(View.GONE);
                        } else {
                            mBinding.noListTxt.setVisibility(View.GONE);
                            mBinding.listGold.setVisibility(View.VISIBLE);
//                                Toast.makeText(getApplicationContext(), getString(R.string.none_gold_data), Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < jsonArray_len; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String jsonClientCode = jsonObject.getString("cliNum");
                                String jsonClientName = jsonObject.getString("client");
                                String jsonStockQuantity = jsonObject.getString("stockQua");
                                inQuantity.add(jsonObject.getString("inQua"));

                                mGoldListItems.add(new GoldListItem(jsonClientCode, jsonClientName, jsonStockQuantity));
                            }

                            GoldAdapter adapter = new GoldAdapter(mGoldListItems);
                            adapter.setOnItemClickListener(pos -> {
                                if (inQuantity.get(pos).equals("null")) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.none_gold_in), Toast.LENGTH_SHORT).show();
                                } else {
                                    String clientName = mGoldListItems.get(pos).getClientName();
                                    String clientCode = mGoldListItems.get(pos).getClientCode();

                                    Intent intent = new Intent(GoldActivity.this, GoldDetailActivity.class);
                                    intent.putExtra("client", clientName);
                                    intent.putExtra("cliNum", clientCode);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                            });
                            adapter.notifyDataSetChanged();
                            mBinding.listGold.setAdapter(adapter);
                            mBinding.listGold.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        }
                        loadingProgressClose();
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!GoldActivity.this.isFinishing())
                            CommonClass.showDialog(GoldActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!GoldActivity.this.isFinishing())
                            CommonClass.showDialog(GoldActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!GoldActivity.this.isFinishing())
                        CommonClass.showDialog(GoldActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!GoldActivity.this.isFinishing())
                    CommonClass.showDialog(GoldActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
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
                            if (!GoldActivity.this.isFinishing()) {
                                CommonClass.showDialog(GoldActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                            }
                        } else {
                            getGoldData();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!GoldActivity.this.isFinishing())
                            CommonClass.showDialog(GoldActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!GoldActivity.this.isFinishing())
                            CommonClass.showDialog(GoldActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!GoldActivity.this.isFinishing())
                        CommonClass.showDialog(GoldActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!GoldActivity.this.isFinishing())
                    CommonClass.showDialog(GoldActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }
}