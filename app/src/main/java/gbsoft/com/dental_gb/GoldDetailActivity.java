package gbsoft.com.dental_gb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ActivityGoldDetailBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoldDetailActivity extends AppCompatActivity {
    private ActivityGoldDetailBinding mBinding;

    private String mClientCode = "";
    private String mClientName = "";
    private String mCond = "";

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityGoldDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(GoldDetailActivity.this.getApplicationContext())) {
            if (!GoldDetailActivity.this.isFinishing())
                CommonClass.showDialog(GoldDetailActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
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

        mBinding.spnGold.setOnItemSelectedListener(goldSelected);

        Intent getIntent = getIntent();
        mClientCode = getIntent.getStringExtra("cliNum");
        mClientName = getIntent.getStringExtra("client");

        mBinding.txtClientName.setText(mClientName);

        ArrayAdapter goldAdapter = ArrayAdapter.createFromResource(this, R.array.gold, android.R.layout.simple_spinner_dropdown_item);
        goldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spnGold.setAdapter(goldAdapter);
    }

    AdapterView.OnItemSelectedListener goldSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String goldInOut = mBinding.spnGold.getSelectedItem().toString();
            if (goldInOut.equals(getString(R.string.all))) {
                mCond = "";
            } else if (goldInOut.equals(getString(R.string.warehousing))) {
                mCond = " AND InQuantity IS NOT NULL AND OutQuantity IS NULL";
            } else {
                mCond = " AND InQuantity IS NULL AND OutQuantity IS NOT NULL";
            }
            if (mCode == CommonClass.PDL)
                getShutdownCheck();
            else
                getGoldInOutList();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * Gold 입출고 리스트
     */
    public void getGoldInOutList() {
        Call<ResponseBody> call = CommonClass.sApiService.getGoldInOutList(mClientCode, mCond, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        ArrayList<GoldListItem> goldListItems = new ArrayList<>();
                        if (jsonArray_len == 0) {
                            mBinding.noListTxt.setVisibility(View.VISIBLE);
                            mBinding.lvGoldInout.setVisibility(View.GONE);
                        } else {
                            for (int i = 0; i < jsonArray_len; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String jsonGoldCode = jsonObject.getString("goldNum");
                                String jsonInOutDate = jsonObject.getString("ioDate");
                                String jsonInQuantity = jsonObject.getString("inQua");
                                String jsonOutQuantity = jsonObject.getString("outQua");
                                String jsonUseQuantity = jsonObject.getString("useQua");

                                goldListItems.add(new GoldListItem(jsonGoldCode, jsonInOutDate, jsonInQuantity, jsonOutQuantity, jsonUseQuantity));
                            }

                            GoldDetailAdapter adapter = new GoldDetailAdapter(GoldDetailActivity.this, goldListItems);
                            adapter.setOnItemClickListener(pos -> {
                                String quantity = goldListItems.get(pos).getInQuantity();
                                if (!quantity.equals("0")) {
                                    Intent intent = new Intent(GoldDetailActivity.this, GoldOutActivity.class);
                                    intent.putExtra("cliNum", mClientCode);
                                    intent.putExtra("client", mClientName);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                            });
                            adapter.notifyDataSetChanged();
                            mBinding.lvGoldInout.setAdapter(adapter);
                            mBinding.lvGoldInout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                            mBinding.noListTxt.setVisibility(View.GONE);
                            mBinding.lvGoldInout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!GoldDetailActivity.this.isFinishing())
                            CommonClass.showDialog(GoldDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!GoldDetailActivity.this.isFinishing())
                            CommonClass.showDialog(GoldDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!GoldDetailActivity.this.isFinishing())
                        CommonClass.showDialog(GoldDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!GoldDetailActivity.this.isFinishing())
                    CommonClass.showDialog(GoldDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    /**
     * 셧다운 유무 확인
     */
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
                            if (!GoldDetailActivity.this.isFinishing()) {
                                CommonClass.showDialog(GoldDetailActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                            }
                        } else {
                            getGoldInOutList();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!GoldDetailActivity.this.isFinishing())
                            CommonClass.showDialog(GoldDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!GoldDetailActivity.this.isFinishing())
                            CommonClass.showDialog(GoldDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!GoldDetailActivity.this.isFinishing())
                        CommonClass.showDialog(GoldDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!GoldDetailActivity.this.isFinishing())
                    CommonClass.showDialog(GoldDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }
}
