package gbsoft.com.dental_gb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ActivityMonitoringBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MonitoringActivity extends AppCompatActivity {
    private ActivityMonitoringBinding mBinding;

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMonitoringBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        this.initialSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);
        mServerPath = sharedPreferences.getString("address", "");

        if (CommonClass.sApiService == null)
            CommonClass.getApiService(mServerPath);

        if (mCode == CommonClass.PDL)
            getShutdownCheck();
        else {
            getMachineData();
            getDaily();
            getStats();
        }
    }

    private void getMachineData() {
        Call<ResponseBody> call = CommonClass.sApiService.getMachineData(mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        Log.v("dentalLog", "machine data result = " + result);
                        JSONObject jsonObject = new JSONObject(result);
                        mBinding.txtStateNow.setText(jsonObject.getString("state"));
                        mBinding.txtStateChangeTime.setText(jsonObject.getString("logTime"));
                        mBinding.txtTransId.setText(String.valueOf(jsonObject.getInt("transCode")));
                        mBinding.txtTransFile.setText(jsonObject.getString("fileName"));
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error - getMachineData");
                        if(!MonitoringActivity.this.isFinishing())
                            CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!MonitoringActivity.this.isFinishing())
                            CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);

                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!MonitoringActivity.this.isFinishing())
                        CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!MonitoringActivity.this.isFinishing())
                    CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    private void getDaily() {
        String date = CommonClass.getCurrentTime(new SimpleDateFormat("yyyy-MM", Locale.getDefault()));
        String startDate = date + "-01";
        Calendar cal = Calendar.getInstance();
        String endDate = date + "-" + cal.getMaximum(Calendar.DAY_OF_MONTH);

        Call<ResponseBody> call = CommonClass.sApiService.Daily(startDate, endDate, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        Log.v("dentalLog", "daily result = " + result);
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArr_len = jsonArray.length();
                        if (jsonArr_len == 0) {
                            mBinding.txtTodayNum.setText("0");
                            mBinding.txtAccumulateNum.setText("0");
                        } else {
                            int todayNum = 0;
                            int total = 0;
                            for (int i = 0; i < jsonArr_len; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String today = CommonClass.getCurrentTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));
                                if (today.equals(jsonObject.getString("OutDate"))) {
                                    todayNum = jsonObject.getInt("Sum");
                                }
                                total += jsonObject.getInt("Sum");
                            }
                            mBinding.txtTodayNum.setText(String.valueOf(todayNum));
                            mBinding.txtAccumulateNum.setText(String.valueOf(total));
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error - getDaily");
                        if(!MonitoringActivity.this.isFinishing())
                            CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!MonitoringActivity.this.isFinishing())
                            CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!MonitoringActivity.this.isFinishing())
                        CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!MonitoringActivity.this.isFinishing())
                    CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    private void getStats() {
        String date = CommonClass.getCurrentTime(new SimpleDateFormat("yyyy-MM", Locale.getDefault()));
        String startDate = date + "-01";
        Calendar cal = Calendar.getInstance();
        String endDate = date + "-" + cal.getMaximum(Calendar.DAY_OF_MONTH);

        Log.v("dentalLog", "startDate / endDate = " + startDate + "/" + endDate);

        Call<ResponseBody> call = CommonClass.sApiService.Stats(startDate, endDate, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        Log.v("dentalLog", "stats result = " + result);
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        int avgSec = jsonObject.getString("ex") == "null" ? 0 : (int) jsonObject.getDouble("ex");
                        int sumSec = jsonObject.getString("sum") == "null" ? 0 : jsonObject.getInt("sum");
                        mBinding.txtAverageTime.setText(get_h_m_s(avgSec));
                        mBinding.txtTotalTime.setText(get_h_m_s(sumSec));

                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error - getStats");
                        if(!MonitoringActivity.this.isFinishing())
                            CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!MonitoringActivity.this.isFinishing())
                            CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!MonitoringActivity.this.isFinishing())
                        CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!MonitoringActivity.this.isFinishing())
                    CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    private String get_h_m_s(int sec) {
        int s = sec % 60;
        int m = (sec / 60) % 60;
        int h = (sec / 60) / 60;
        return String.format("%02d", h) + ":" + String.format("%02d", m) + ":" + String.format("%02d", s);
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

                        JSONObject c = jsonArray.getJSONObject(0);
                        int mShutdownYn = Integer.parseInt(c.getString("down"));

                        if (mShutdownYn == 1) {
                            if (!MonitoringActivity.this.isFinishing())
                                CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                        } else {
                            getMachineData();
                            getDaily();
                            getStats();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!MonitoringActivity.this.isFinishing())
                            CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!MonitoringActivity.this.isFinishing())
                            CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!MonitoringActivity.this.isFinishing())
                        CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!MonitoringActivity.this.isFinishing())
                    CommonClass.showDialog(MonitoringActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }
}
