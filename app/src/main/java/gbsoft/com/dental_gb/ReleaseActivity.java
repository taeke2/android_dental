package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ActivityReleaseBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReleaseActivity extends AppCompatActivity {
//    private static final int FILTER = 100;

    private ActivityReleaseBinding mBinding;

    private ArrayList<ProcessRequestReleaseListItem> mItems;
    private ReleaseAdapter mAdpater;

    private String mClientName = "";
    private String mPatientName = "";
    private String mOrderDate = "";
    private String mDeadlineDate = "";
    private String mManager = "";
    private String mDentalFormula = "";
    private String mRelease = "";

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    private int mSelectedSize = 0;
    private int mUpdateCount = 0;

    private boolean mIsReleased = false;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        mBinding = ActivityReleaseBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(ReleaseActivity.this.getApplicationContext())) {
            if (!ReleaseActivity.this.isFinishing())
                CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
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

        mBinding.iconTxtRelease.setOnClickListener(releaseButtonClick);
        mBinding.iconTxtFilter.setOnClickListener(filterButtonClick);

        mBinding.iconTxtRelease.setVisibility(View.GONE);

        mItems = new ArrayList<>();
        mBinding.recyclerRelease.setLayoutManager(new LinearLayoutManager(this));

        if (mCode == CommonClass.PDL) {
            getShutdownCheck();
            mDentalFormula = "///";
        } else {
            mDentalFormula = "&&&";
            getReleaseList();
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == FILTER) {
//            if (resultCode == RESULT_OK) {
//                mItems.clear();
//                mManager = data.getStringExtra("manager");
//                mOrderDate = data.getStringExtra("ordDate");
//                mDeadlineDate = data.getStringExtra("deadDate");
//                mClientName = data.getStringExtra("searchClient");
//                mPatientName = data.getStringExtra("searchPatient");
//                mDentalFormula = data.getStringExtra("dent");
//                mDentalFormula = mDentalFormula.replace("상", "上");
//                mDentalFormula = mDentalFormula.replace("하", "下");
//                mRelease = data.getStringExtra("release");
//            }
//        }
//
//    }

    /**
     * 로딩창 열기
     * */
    private void loadingProgressOpen(){
        mProgressDialog = new ProgressDialog(ReleaseActivity.this);
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

    View.OnClickListener filterButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ReleaseActivity.this, FilterActivity.class);
            intent.putExtra("ordDate", mOrderDate);
            intent.putExtra("deadDate", mDeadlineDate);
            intent.putExtra("manager", mManager);
            intent.putExtra("searchClient", mClientName);
            intent.putExtra("searchPatient", mPatientName);
            intent.putExtra("type", "rel");
            intent.putExtra("dent", mDentalFormula);
            intent.putExtra("release", mRelease);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            // startActivityForResult(intent, FILTER);
            startActivityResult.launch(intent);
        }
    };

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                mItems.clear();
                mManager = data.getStringExtra("manager");
                mOrderDate = data.getStringExtra("ordDate");
                mDeadlineDate = data.getStringExtra("deadDate");
                mClientName = data.getStringExtra("searchClient");
                mPatientName = data.getStringExtra("searchPatient");
                mDentalFormula = data.getStringExtra("dent");
                mDentalFormula = mDentalFormula.replace("상", "上");
                mDentalFormula = mDentalFormula.replace("하", "下");
                mRelease = data.getStringExtra("release");
            }
        }
    });

    ReleaseAdapter.OnLongItemClickListener longClick = new ReleaseAdapter.OnLongItemClickListener() {
        @Override
        public void onLongItemClick(int pos) {
            mBinding.iconTxtRelease.setVisibility(View.VISIBLE);
            mBinding.iconTxtFilter.setVisibility(View.GONE);
            mIsReleased = true;
            recyclerSetAdapter();
        }
    };

    private void recyclerSetAdapter() {
        mAdpater = new ReleaseAdapter(this, mItems, mIsReleased);
        mAdpater.setOnLongItemClickListener(longClick);
        mBinding.recyclerRelease.setAdapter(mAdpater);
        loadingProgressClose();
    }

    View.OnClickListener releaseButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int len = mItems.size();
            int count = 0;
            for (int i = 0; i < len; i++) {
                ProcessRequestReleaseListItem item = mItems.get(i);
                if (item.isChecked())
                    count++;
            }

            if (count == 0) {
                CommonClass.showDialog(ReleaseActivity.this, getString(R.string.do_out), "출고할 제품을 선택해주세요.", () -> {}, false);
            } else {
                CommonClass.showDialog(ReleaseActivity.this, getString(R.string.do_out), getString(R.string.release_product_ask), () -> {
                    for (int i = 0; i < len; i++) {
                        ProcessRequestReleaseListItem item = mItems.get(i);
                        if (item.isChecked()) {
                             updateReleaseTime(item.getRequestCode(), CommonClass.getCurrentTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())));
                            item.setChecked(false);
                        }
                    }

                    mBinding.iconTxtRelease.setVisibility(View.GONE);
                    mBinding.iconTxtFilter.setVisibility(View.VISIBLE);

                    mIsReleased = false;
                    recyclerSetAdapter();
                    getReleaseList();
                }, true);
            }

//            AlertDialog.Builder builder = new AlertDialog.Builder(ReleaseActivity.this);
//            builder.setMessage("해당 의뢰제품들을 출고하시겠습니까?");
//            builder.setTitle("출고하기").setCancelable(false)
//                    .setPositiveButton("출고", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            int len = mItems.size();
//                            int count = 0;
//
//                            for (int i = 0; i < len; i++) {
//                                ProcessRequestReleaseListItem item = mItems.get(i);
//                                if (item.isChecked()) {
//                                    updateReleaseTime(item.getRequestCode(), CommonClass.getCurrentTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())));
//                                    item.setChecked(false);
//                                    count++;
//                                }
//                            }
//
//                            if (count == 0) {
//                                CommonClass.ShowToast(getApplicationContext(), "No Selection");
//                            }
//
//                            mBinding.iconTxtRelease.setVisibility(View.GONE);
//                            mBinding.iconTxtFilter.setVisibility(View.VISIBLE);
//
//                            isReleased = false;
//                            recyclerSetAdapter();
//                            getReleaseList();
//                        }
//                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            }).setCancelable(false);
//            AlertDialog alert = builder.create();
//            alert.show();

        }
    };

    private void getReleaseList() {
        mItems.clear();

        Call<ResponseBody> call = CommonClass.sApiService.getReleaseList(mClientName, mPatientName, mOrderDate, mDeadlineDate, mRelease, mId, mIp);
        loadingProgressOpen();
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
                            mBinding.recyclerRelease.setVisibility(View.GONE);
                        } else {
                            for (int i = 0; i < jsonArray_len; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String jsonRequestCode = jsonObject.getString("reqNum");
                                String jsonClientName = jsonObject.getString("client");
                                String jsonPatientName = jsonObject.getString("patient");
                                String jsonOrderDate = jsonObject.getString("ordDate");
                                String jsonDeadlineDate = jsonObject.getString("deadDate");
                                String jsonOutDate = jsonObject.getString("outDate");
                                String jsonProductName = jsonObject.getString("prdName");
                                mItems.add(new ProcessRequestReleaseListItem(jsonRequestCode, jsonClientName, jsonPatientName, jsonProductName, jsonOrderDate, jsonDeadlineDate, jsonOutDate));
                            }
                            mBinding.noListTxt.setVisibility(View.GONE);
                            mBinding.recyclerRelease.setVisibility(View.VISIBLE);
                        }
                        recyclerSetAdapter();
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!ReleaseActivity.this.isFinishing())
                            CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!ReleaseActivity.this.isFinishing())
                            CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!ReleaseActivity.this.isFinishing())
                        CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!ReleaseActivity.this.isFinishing())
                    CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    private void updateReleaseTime(String requestCode, String currentTime) {
        Call<ResponseBody> call = CommonClass.sApiService.updateReleaseTime(requestCode, currentTime, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (result.equals("OK")) {
                            mUpdateCount++;
                            if (mSelectedSize == mUpdateCount) {
                                Intent intent = new Intent(getApplicationContext(), ReleaseActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            if(!ReleaseActivity.this.isFinishing())
                                CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> {  }, false);
                        }

                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!ReleaseActivity.this.isFinishing())
                            CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> {  }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!ReleaseActivity.this.isFinishing())
                        CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> {  }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!ReleaseActivity.this.isFinishing())
                    CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> {  }, false);
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
                            if (!ReleaseActivity.this.isFinishing())
                                CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                        } else {
                            getReleaseList();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!ReleaseActivity.this.isFinishing())
                            CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!ReleaseActivity.this.isFinishing())
                            CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!ReleaseActivity.this.isFinishing())
                        CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!ReleaseActivity.this.isFinishing())
                    CommonClass.showDialog(ReleaseActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mIsReleased) {
            mIsReleased = false;
            recyclerSetAdapter();
            mBinding.iconTxtFilter.setVisibility(View.VISIBLE);
            mBinding.iconTxtRelease.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
