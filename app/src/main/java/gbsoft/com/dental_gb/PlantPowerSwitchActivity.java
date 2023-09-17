package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import gbsoft.com.dental_gb.databinding.ActivityPlantPowerSwitchBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantPowerSwitchActivity extends AppCompatActivity {

    private ActivityPlantPowerSwitchBinding mBinding;
    private PlantPowerSwitchAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPlantPowerSwitchBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialize(){
        CommonClass.sPlantListDTO.clear();

        equipList();

//        String[] mPlantNames = {"설비명", "테스트1", "테스트테스트"};
//        int[] mPlantStates = {0, 0, 1};
//        int[] mPlantPowers = {0, 0, 1};
//
//        for(int i = 0; i < 3; i++){
//            PlantListDTO plantListDTO = new PlantListDTO();
//            plantListDTO.setId(i);
//            plantListDTO.setPlantName(mPlantNames[i]);
//            plantListDTO.setPlantState(mPlantStates[i]);
//            plantListDTO.setPower(mPlantPowers[i]);
//            CommonClass.sPlantListDTO.add(plantListDTO);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);

        mAdapter = new PlantPowerSwitchAdapter();

        mBinding.listPlant.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new PlantPowerSwitchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, Boolean checked) {
                // dialog 추가
                CheckDialogClickListener checkDialogClickListener = new CheckDialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        equipStateUpdate(pos, checked);
                    }
                };
                String mContent = "";
                if(checked){
                    mContent = "시작";
                } else {
                    mContent = "중지";
                }

                CommonClass.showDialog(PlantPowerSwitchActivity.this, "안내", CommonClass.sPlantListDTO.get(pos).getPlantName()+ "을 가동을 " + mContent + " 하시겠습니까?", checkDialogClickListener, true);
            }
        });

        mBinding.listPlant.setLayoutManager(new LinearLayoutManager(PlantPowerSwitchActivity.this, LinearLayoutManager.VERTICAL, false));
    }

    View.OnClickListener btnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
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
                        mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
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
                                mAdapter.notifyItemChanged(i);
                            }
                        }
                    } catch (JSONException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: JSON Parsing Error - findId");
                        Log.e("plantManagement", "ERROR: JSON Parsing Error - equipList");
                        if (!PlantPowerSwitchActivity.this.isFinishing())
                            CommonClass.showDialog(PlantPowerSwitchActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: IOException - findId");
                        Log.e("plantManagement", "ERROR: IOException - equipList");
                        if (!PlantPowerSwitchActivity.this.isFinishing())
                            CommonClass.showDialog(PlantPowerSwitchActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }

                } else {
                    if (!PlantPowerSwitchActivity.this.isFinishing())
                        CommonClass.showDialog(PlantPowerSwitchActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!PlantPowerSwitchActivity.this.isFinishing())
                    CommonClass.showDialog(PlantPowerSwitchActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }



    // plantPowerState update api
    private void equipStateUpdate(int pos, Boolean checked) {
        Call<ResponseBody> call = CommonClass.sApiService.equipStateUpdate(CommonClass.sPlantListDTO.get(pos).getId(), CommonClass.sPlantListDTO.get(pos).getPersonInChargeOfVendor(), checked);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);

                        equipList();
//                        int jsonArray_len = jsonArray.length();
//                        if (jsonArray_len == 0) {
//
//                        } else {
//
//                        }
                    } catch (JSONException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: JSON Parsing Error - findId");
                        Log.e("plantManagement", "ERROR: JSON Parsing Error - equipList");
                        if (!PlantPowerSwitchActivity.this.isFinishing())
                            CommonClass.showDialog(PlantPowerSwitchActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: IOException - findId");
                        Log.e("plantManagement", "ERROR: IOException - equipList");
                        if (!PlantPowerSwitchActivity.this.isFinishing())
                            CommonClass.showDialog(PlantPowerSwitchActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }

                } else {
                    if (!PlantPowerSwitchActivity.this.isFinishing())
                        CommonClass.showDialog(PlantPowerSwitchActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!PlantPowerSwitchActivity.this.isFinishing())
                    CommonClass.showDialog(PlantPowerSwitchActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }
    // dialog 추가
}