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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import gbsoft.com.dental_gb.databinding.ActivityRequest2Binding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestActivity2 extends AppCompatActivity {

    private ActivityRequest2Binding mBinding;
    private RequestAdapter2 mAdapter;

    private String mClientName = "";
    private String mPatientName = "";
    private String mOrderDate = "";
    private String mDeadlineDate = "";
    private String mManager = "";
    private String mDentalFormula = "";
    private String mRelease = "";

    private String mId = "";
    private int mCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRequest2Binding.inflate(getLayoutInflater());
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

        requestList();

//        String[] mClientNames = {"테스트", "다사랑치과", "일품치과"};
//        String[] mPatientNames = {"홍길동", "안재욱", "조우림"};
//        String[] mProductNames = {"임플란트", "교정", "틀니"};
//        String[] mOrderDates = {"2021-06-28 PM 16:54:23", "2021-12-12 AM 10:12:15", "2022-02-01 PM 18:00:23"};
//        String[] mDeadlineDates = {"2021-07-03 PM", "2021-12-31 AM", "2022-02-10 PM"};
//        String[] mDent1 = {"87654321", "631", "321"};
//        String[] mDent2 = {"12345678", "123", "278"};
//        String[] mDent3 = {"87654321", "752", "321"};
//        String[] mDent4 = {"12345678", "257", "123"};
//
//        for(int i = 0; i < 3; i++){
//            RequestDTO requestDTO = new RequestDTO();
//            requestDTO.setId(i);
//            requestDTO.setClientName(mClientNames[i]);
//            requestDTO.setPatientName(mPatientNames[i]);
//            requestDTO.setProductName(mProductNames[i]);
//            requestDTO.setOrderDate(mOrderDates[i]);
//            requestDTO.setDeadlineDate(mDeadlineDates[i]);
//            requestDTO.setDentalFormula10(mDent1[i]);
//            requestDTO.setDentalFormula20(mDent2[i]);
//            requestDTO.setDentalFormula30(mDent3[i]);
//            requestDTO.setDentalFormula40(mDent4[i]);
//
//            CommonClass.sRequestDTO.add(requestDTO);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);
        mBinding.iconTxtFilter.setOnClickListener(filterButtonClick);

        mAdapter = new RequestAdapter2();

        mAdapter.setOnItemClickListener(requestAdapterOnItemClickListener);
        mAdapter.setBtnEtcClickListener(requestAdapterBtnEtcClickListener);

        mBinding.listRequest.setAdapter(mAdapter);
        mBinding.listRequest.setLayoutManager(new LinearLayoutManager(RequestActivity2.this, LinearLayoutManager.VERTICAL, false));
    }

    RequestAdapter2.OnItemClickListener requestAdapterOnItemClickListener = new RequestAdapter2.OnItemClickListener() {
        @Override
        public void onItemClick(int pos) {
            Intent intent = new Intent(RequestActivity2.this, RequestDetailActivity2.class);
            intent.putExtra("pos", pos);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

    RequestAdapter2.btnEtcClickListener requestAdapterBtnEtcClickListener = new RequestAdapter2.btnEtcClickListener() {
        @Override
        public void onEtcClick(int pos) {
            SubMenuBottomDialog activity = new SubMenuBottomDialog();
            Bundle bundle = new Bundle();
            // TODO: 추후 수정
            bundle.putString("reqNum", CommonClass.sRequestDTO.get(pos).getId()+"");
            bundle.putString("clientTel", CommonClass.sRequestDTO.get(pos).getTelNum());
            activity.setArguments(bundle);
            activity.show(getSupportFragmentManager(), "");
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
            mClientName = searchClient;
            mPatientName = searchPatient;
            mDentalFormula = dent;
            mDentalFormula = mDentalFormula.replace("상", "上");
            mDentalFormula = mDentalFormula.replace("하", "下");
            mRelease = release;

            requestList();
        }
    };

    View.OnClickListener filterButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FilterBottomDialog filterBottomDialog = new FilterBottomDialog(
                    RequestActivity2.this, filterBottomDialogClickListener,
                    mOrderDate, mDeadlineDate, mManager, mClientName, mPatientName,
                    "req", mDentalFormula, mRelease, mId, mCode
            );

            filterBottomDialog.show(getSupportFragmentManager(), "request");

//            Intent intent = new Intent(RequestActivity2.this, FilterActivity.class);
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

    private void requestList() {
        Call<ResponseBody> call = CommonClass.sApiService.requestList(mClientName, mDentalFormula, mOrderDate, mDeadlineDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
                        CommonClass.sRequestDTO.clear();

                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        if (jsonArray_len == 0) {

                        } else {
                            StringBuilder builder = new StringBuilder();
                            for(int i = 0; i < jsonArray_len; i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                RequestDTO requestDTO = new RequestDTO();
                                requestDTO.setId(jsonObject.getInt("requestCode"));
                                requestDTO.setClientName(jsonObject.getString("clientName"));
                                requestDTO.setProductName(jsonObject.getString("productName"));
                                requestDTO.setOrderDate(jsonObject.getString("requestDate"));
                                requestDTO.setDeadlineDate(jsonObject.getString("dueDate"));
                                requestDTO.setDeadlineTime(jsonObject.getString("dueTime"));
                                requestDTO.setTelNum(jsonObject.getString("telNum").equals("null") ? "-" : jsonObject.getString("telNum"));
                                String[] mDentArr = jsonObject.getString("dentalFormula").split("/");

                                for(int j = 0; j < mDentArr.length; j++){
                                    String[] tempArr = mDentArr[j].split(",");
                                    for (String name : tempArr) {
                                        builder.append(name).append("");
                                    }

                                    switch (j){
                                        case 0:
                                            requestDTO.setDentalFormula10(builder.toString());
                                            break;
                                        case 1:
                                            requestDTO.setDentalFormula20(builder.toString());
                                            break;
                                        case 2:
                                            requestDTO.setDentalFormula30(builder.toString());
                                            break;
                                        case 3:
                                            requestDTO.setDentalFormula40(builder.toString());
                                            break;
                                    }
                                    builder.delete(0, builder.toString().length());
                                }

                                CommonClass.sRequestDTO.add(requestDTO);
                                mAdapter.notifyItemChanged(i);
                            }
                        }
                    } catch (JSONException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: JSON Parsing Error - findId");
                        Log.e("RequestActivity2", "ERROR: JSON Parsing Error - requestList");
                        if (!RequestActivity2.this.isFinishing())
                            CommonClass.showDialog(RequestActivity2.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: IOException - findId");
                        Log.e("RequestActivity2", "ERROR: IOException - requestList");
                        if (!RequestActivity2.this.isFinishing())
                            CommonClass.showDialog(RequestActivity2.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }

                } else {
                    if (!RequestActivity2.this.isFinishing())
                        CommonClass.showDialog(RequestActivity2.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!RequestActivity2.this.isFinishing())
                    CommonClass.showDialog(RequestActivity2.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }
}