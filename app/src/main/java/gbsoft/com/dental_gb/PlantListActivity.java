package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import gbsoft.com.dental_gb.databinding.ActivityPlantListBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantListActivity extends AppCompatActivity {

    private ActivityPlantListBinding mBinding;
    private PlantAdapter mPlantAdapter;

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPlantListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void initialize(){
        SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);
        mServerPath = sharedPreferences.getString("address", "");

        if (CommonClass.sApiService == null)
            CommonClass.getApiService(mServerPath);

        CommonClass.sPlantListDTO.clear();

//        String[] mPlantNames = {"설비명", "테스트1", "테스트테스트"};
//        String[] mClientNames = {"납품업체", "테스트1", "테스트테스트"};
//        String[] mSNs = {"1234567896", "7586422425", "46345343524"};
//        String[] mIntroductionDate = {"2021-07-02", "2022-01-02", "2022-02-13"};
//        String[] mVendors = {"지비소프트", "연돈", "더블비"};
//        String[] mPersonInChargeOfVendors = {"안재욱", "테스트", "김나리"};
//        String[] mPhoneNumbers = {"010-1234-1234", "010-1111-2222", "010-3333-3214"};
//        String[] mEmails = {"kim012@naver.com", "min34@gmail.com", "zzang55@naver.com"};
//        String[] mAsNumbers = {"02-1111-2222", "053-780-7878", "02-5556-3234"};
//        String[] mRemarks = {"비고", "비고1", "비고2"};
//        int[] mPlantStates = {0, 0, 1};
//
//
//        for(int i = 0; i < 3; i++){
//            PlantListDTO plantListDTO = new PlantListDTO();
//            plantListDTO.setId(i);
//            plantListDTO.setPlantName(mPlantNames[i]);
//            plantListDTO.setClientName(mClientNames[i]);
//            plantListDTO.setSN(mSNs[i]);
//            plantListDTO.setIntroductionDate(mIntroductionDate[i]);
//            plantListDTO.setVendor(mVendors[i]);
//            plantListDTO.setPersonInChargeOfVendor(mPersonInChargeOfVendors[i]);
//            plantListDTO.setPhoneNumber(mPhoneNumbers[i]);
//            plantListDTO.setEmail(mEmails[i]);
//            plantListDTO.setAsNumber(mAsNumbers[i]);
//            plantListDTO.setRemark(mRemarks[i]);
//            plantListDTO.setPlantState(mPlantStates[i]);
//            CommonClass.sPlantListDTO.add(plantListDTO);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBinding.btnPlantSearch.setOnClickListener(btnPlantSearchOnClickListener);
        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);

        mPlantAdapter = new PlantAdapter();

        mPlantAdapter.setOnItemClickListener(plantAdapterOnItemClickListener);

        equipList();

        mBinding.listPlant.setAdapter(mPlantAdapter);
        mBinding.listPlant.setLayoutManager(new LinearLayoutManager(PlantListActivity.this, LinearLayoutManager.VERTICAL, false));
    }

    PlantAdapter.OnItemClickListener plantAdapterOnItemClickListener = new PlantAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int pos) {
            Intent intent = new Intent(PlantListActivity.this, PlantDetailActivity.class);
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

    View.OnClickListener btnPlantSearchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PlantListActivity.this, SearchActivity.class);
            intent.putExtra("tag", "Plant");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

    private void equipList() {
        Call<ResponseBody> call = CommonClass.sApiService.equipList("");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        mPlantAdapter.notifyItemRangeRemoved(0, mPlantAdapter.getItemCount());
                        CommonClass.sPlantListDTO.clear();

                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        if (jsonArray_len == 0) {

                        } else {
                            for(int i = 0; i < jsonArray_len; i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                PlantListDTO plantListDTO = new PlantListDTO();
                                plantListDTO.setId(jsonObject.getInt("equipCode"));
                                plantListDTO.setPlantName(jsonObject.getString("equipName"));
                                plantListDTO.setSN(jsonObject.getString("serialNum"));
                                plantListDTO.setIntroductionDate(jsonObject.getString("introductionDate"));
                                plantListDTO.setClientName(jsonObject.getString("introductionCompany"));
                                plantListDTO.setPersonInChargeOfVendor(jsonObject.getString("introductionManager"));
                                plantListDTO.setAsNumber(jsonObject.getString("asTel"));
                                plantListDTO.setVendor(jsonObject.getString("manager"));
                                plantListDTO.setPlantState(jsonObject.getInt("workYn"));
                                plantListDTO.setPower(jsonObject.getInt("checkYn"));
                                plantListDTO.setRemark(jsonObject.getString("remark"));
                                CommonClass.sPlantListDTO.add(plantListDTO);
                                mPlantAdapter.notifyItemChanged(i);
                            }
                        }
                    } catch (JSONException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: JSON Parsing Error - findId");
                        Log.e("plantManagement", "ERROR: JSON Parsing Error - equipList");
                        if (!PlantListActivity.this.isFinishing())
                        CommonClass.showDialog(PlantListActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: IOException - findId");
                        Log.e("plantManagement", "ERROR: IOException - equipList");
                        if (!PlantListActivity.this.isFinishing())
                        CommonClass.showDialog(PlantListActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }

                } else {
                    if (!PlantListActivity.this.isFinishing())
                    CommonClass.showDialog(PlantListActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!PlantListActivity.this.isFinishing())
                CommonClass.showDialog(PlantListActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }
}