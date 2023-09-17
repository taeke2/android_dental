package gbsoft.com.dental_gb;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import gbsoft.com.dental_gb.databinding.ActivityRelease2Binding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReleaseActivity2 extends AppCompatActivity {

    private ActivityRelease2Binding mBinding;
    private ReleaseAdapter2 mAdapter;

    private String mClientName = "";
    private String mPatientName = "";
    private String mOrderDate = "";
    private String mDeadlineDate = "";
    private String mOutDate = "";
    private String mManager = "";
    private String mDentalFormula = "";
    private String mRelease = "";

    private String mId = "";
    private int mCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRelease2Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialize(){
        SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);
        if (CommonClass.sApiService == null) {
            String serverPath = sharedPreferences.getString("address", "");
            CommonClass.getApiService(serverPath);
        }
        mCode = sharedPreferences.getInt("num", -1);

        releaseList();
//        String[] mClientNames = {"테스트", "다사랑치과", "일품치과"};
//        String[] mPatientNames = {"홍길동", "안재욱", "조우림"};
//        String[] mProductNames = {"임플란트", "교정", "틀니"};
//        String[] mOrderDates = {"2021-06-28 PM 16:54:23", "2021-12-12 AM 10:12:15", "2022-02-01 PM 18:00:23"};
//        String[] mDeadlineDates = {"2021-07-03 PM", "2021-12-31 AM", "2022-02-10 PM"};
//        String[] mOutDates = {"2021-07-03 PM 17:20:00", "2022-01-04 AM 11:05:24", "출고전"};
//
//        for(int i = 0; i < 3; i++){
//            ReleaseDTO releaseDTO = new ReleaseDTO();
//
//            releaseDTO.setId(i);
//            releaseDTO.setClientName(mClientNames[i]);
//            releaseDTO.setPatientName(mPatientNames[i]);
//            releaseDTO.setProductName(mProductNames[i]);
//            releaseDTO.setOrderDate(mOrderDates[i].substring(0, 10) + " ~ ");
//            releaseDTO.setDeadlineDate(mDeadlineDates[i].substring(0, 10) + " | ");
//            if(mOutDates[i].equals("출고전")){
//                releaseDTO.setOutDate(mOutDates[i]);
//            } else {
//                releaseDTO.setOutDate(mOutDates[i].substring(0, 10) + mOutDates[i].substring(13, 22));
//            }
//
//            CommonClass.sReleaseDTO.add(releaseDTO);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);
        mBinding.iconTxtFilter.setOnClickListener(filterButtonClick);

        mAdapter = new ReleaseAdapter2();

        mAdapter.setOnItemClickListener(releaseAdapterOnItemClickListener);

        mBinding.listRelease.setAdapter(mAdapter);
        mBinding.listRelease.setLayoutManager(new LinearLayoutManager(ReleaseActivity2.this, LinearLayoutManager.VERTICAL, false));
    }

    ReleaseAdapter2.OnItemClickListener releaseAdapterOnItemClickListener = new ReleaseAdapter2.OnItemClickListener() {
        @Override
        public void onItemClick(int pos) {
            Intent intent = new Intent(ReleaseActivity2.this, ReleaseDetailActivity2.class);
            intent.putExtra("pos", pos);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

    View.OnClickListener btnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    FilterBottomDialogClickListener filterBottomDialogClickListener = new FilterBottomDialogClickListener() {
        @Override
        public void onSearchClick(String ordDate, String deadDate, String outDate, String manager, String searchClient, String searchPatient, String dent, String release) {
            mManager = manager;
            mOrderDate = ordDate;
            mDeadlineDate = deadDate;
            mOutDate = outDate;
            mClientName = searchClient;
            mPatientName = searchPatient;
            mDentalFormula = dent;
            mDentalFormula = mDentalFormula.replace("상", "上");
            mDentalFormula = mDentalFormula.replace("하", "下");
            mRelease = release;

            releaseList();
        }
    };

    View.OnClickListener filterButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FilterBottomDialog filterBottomDialog = new FilterBottomDialog(
                    ReleaseActivity2.this, filterBottomDialogClickListener,
                    mOrderDate, mDeadlineDate, mManager, mClientName, mPatientName,
                    "rel", mDentalFormula, mRelease, mId, mCode
            );

            filterBottomDialog.show(getSupportFragmentManager(), "release");

//            Intent intent = new Intent(ReleaseActivity2.this, FilterActivity.class);
//            intent.putExtra("ordDate", mOrderDate);
//            intent.putExtra("deadDate", mDeadlineDate);
//            intent.putExtra("manager", mManager);
//            intent.putExtra("searchClient", mClientName);
//            intent.putExtra("searchPatient", mPatientName);
//            intent.putExtra("type", "rel");
//            intent.putExtra("dent", mDentalFormula);
//            intent.putExtra("release", mRelease);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            // startActivityForResult(intent, FILTER);
//            startActivityResult.launch(intent);
        }
    };

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
//                mItems.clear();
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

    private void releaseList() {
        Call<ResponseBody> call = CommonClass.sApiService.releaseList(mClientName, mOutDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
                        CommonClass.sReleaseDTO.clear();

                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        if (jsonArray_len == 0) {

                        } else {
                            for(int i = 0; i < jsonArray_len; i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if(!mOutDate.equals("") && jsonObject.getString("outDateTime").equals("null")){
                                    continue;
                                } else {
                                    ReleaseDTO releaseDTO = new ReleaseDTO();
//                                releaseDTO.setId(jsonObject.getInt("groupCode"));
                                    releaseDTO.setId(jsonObject.getInt("requestCode"));
                                    releaseDTO.setClientName(jsonObject.getString("clientName"));
                                    releaseDTO.setProductName(jsonObject.getString("productName"));
                                    releaseDTO.setOrderDate(jsonObject.getString("requestDate"));
                                    releaseDTO.setDeadlineDate(jsonObject.getString("dueDate"));
                                    releaseDTO.setOutDate(jsonObject.getString("outDateTime"));
//                                releaseDTO.setReleaseYn(jsonObject.getInt("releaseYn"));
                                    releaseDTO.setDueTime(jsonObject.getString("dueTime"));
                                    CommonClass.sReleaseDTO.add(releaseDTO);
                                    mAdapter.notifyItemChanged(i);
                                }
                            }
                        }
                    } catch (JSONException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: JSON Parsing Error - findId");
                        Log.e("plantManagement", "ERROR: JSON Parsing Error - equipList");
                        if (!ReleaseActivity2.this.isFinishing())
                            CommonClass.showDialog(ReleaseActivity2.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: IOException - findId");
                        Log.e("plantManagement", "ERROR: IOException - equipList");
                        if (!ReleaseActivity2.this.isFinishing())
                            CommonClass.showDialog(ReleaseActivity2.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }

                } else {
                    if (!ReleaseActivity2.this.isFinishing())
                        CommonClass.showDialog(ReleaseActivity2.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!ReleaseActivity2.this.isFinishing())
                    CommonClass.showDialog(ReleaseActivity2.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }
}