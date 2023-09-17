package gbsoft.com.dental_gb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ActivityReleaseDetailBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReleaseDetailActivity extends AppCompatActivity {
    private ActivityReleaseDetailBinding mBinding;

    private ArrayList<ReleaseDetailItem> mReleaseDetailItems;
    private ReleaseDetailAdapter mReleaseDetailAdapter;

    private String mRequestCode = "";
    private String mOutDate = "";

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        mBinding = ActivityReleaseDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(ReleaseDetailActivity.this.getApplicationContext())) {
            if (!ReleaseDetailActivity.this.isFinishing())
                CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
        } else {
            this.initialSet();
        }
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

        Intent intent = getIntent();
        mRequestCode = intent.getStringExtra("reqNum");
        mOutDate = intent.getStringExtra("outDate");

        mReleaseDetailItems = new ArrayList<>();

        mBinding.iconTxtRelease.setOnClickListener(releaseClick);
        mBinding.iconTxtRequest.setOnClickListener(requestClick);

        if (mCode == CommonClass.PDL)
            getShutdownCheck();
        else
            getReleaseDetail();
    }

    View.OnClickListener releaseClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOutDate.equals("null")) {
                CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.do_out), getString(R.string.release_product_ask), () -> {
                    String currentTime = CommonClass.getCurrentTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));
                    updateReleaseTime(currentTime);
                }, true);
            } else {
                CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.do_out), getString(R.string.already_released), () -> {}, false);
            }

//            AlertDialog.Builder builder = new AlertDialog.Builder(ReleaseDetailActivity.this);
//            if (mOutDate.equals("null")) {
//                builder.setMessage(getString(R.string.release_product_ask));
//                builder.setTitle(getString(R.string.do_out)).setCancelable(false)
//                    .setPositiveButton(getString(R.string.out), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            String currentTime = CommonClass.getCurrentTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));
//                            updateReleaseTime(currentTime);
//                        }
//                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                }).setCancelable(false);
//            } else {
//                builder.setMessage(getString(R.string.already_released));
//                builder.setTitle(getString(R.string.do_out)).setCancelable(false)
//                        .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        }).setCancelable(false);
//            }
//
//            AlertDialog alert = builder.create();
//            alert.show();
        }
    };

    View.OnClickListener requestClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ReleaseDetailActivity.this, RequestDetailActivity.class);
            intent.putExtra("reqNum", mRequestCode);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

    private void getReleaseDetail() {
        Call<ResponseBody> call = CommonClass.sApiService.getReleaseDetail(mId, mRequestCode, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArr_len = jsonArray.length();

                        String clientName = "";
                        String patientName = "";
                        String orderDate = "";
                        String deadlineDate = "";
                        for (int i = 0; i < jsonArr_len; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            clientName = jsonObject.getString("client");
                            patientName = jsonObject.getString("patient");
                            orderDate = jsonObject.getString("ordDate");
                            deadlineDate = jsonObject.getString("deadDate");

                            ReleaseDetailItem item = new ReleaseDetailItem();
                            item.setProductName(jsonObject.getString("prdName"));
                            item.setDeadlineDateTime(jsonObject.getString("deadDateTime"));
                            item.setOrderFinishTime(jsonObject.getString("ordFinishTime"));
                            item.setDentalFormula(jsonObject.getString("dent"));

                            mReleaseDetailItems.add(item);
                        }

                        mBinding.txtClientName.setText(clientName);
                        mBinding.txtPatientName.setText(patientName);
                        mBinding.txtOrderDate.setText(orderDate);
                        mBinding.txtDeadlineDate.setText(deadlineDate);

                        mReleaseDetailAdapter = new ReleaseDetailAdapter(mReleaseDetailItems, mCode);
                        mBinding.recyclerDetailList.setAdapter(mReleaseDetailAdapter);
                        mBinding.recyclerDetailList.setLayoutManager(new LinearLayoutManager(ReleaseDetailActivity.this));
                        mBinding.recyclerDetailList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!ReleaseDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!ReleaseDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!ReleaseDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!ReleaseDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    private void updateReleaseTime(String currentTime) {
        Call<ResponseBody> call = CommonClass.sApiService.updateReleaseTime(mRequestCode, currentTime, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (result.equals("OK")) {
                            onBackPressed();
                        } else {
                            if(!ReleaseDetailActivity.this.isFinishing())
                                CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                        }

                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!ReleaseDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!ReleaseDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!ReleaseDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
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
                            if (!ReleaseDetailActivity.this.isFinishing())
                                CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                        } else {
                            getReleaseDetail();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!ReleaseDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!ReleaseDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!ReleaseDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!ReleaseDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ReleaseDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
