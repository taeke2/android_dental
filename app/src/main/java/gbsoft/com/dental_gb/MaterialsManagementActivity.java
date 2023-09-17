package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import gbsoft.com.dental_gb.databinding.ActivityMaterialsManagementBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaterialsManagementActivity extends AppCompatActivity {

    private ActivityMaterialsManagementBinding mBinding;
    private MaterialsAdapter2 mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMaterialsManagementBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialize(){
        CommonClass.sMaterialDTO.clear();


//        String[] mMaterialsName = {"일반 보철", "용해액", "테스트1"};
//        String[] mMaterialsAccounts = {"일산치과", "한마음치과", "다사랑치과"};
//        String[] mWareHousingDate = {"2021.07.01", "2022.02.03", "2022.02.05"};
//        int[] mMaterialsStock = {8, 7, 9};
//
//        for(int i = 0; i < 3; i++){
//            MaterialDTO materialDTO = new MaterialDTO();
//
//            materialDTO.setId(i);
//            materialDTO.setMaterialName(mMaterialsName[i]);
//            materialDTO.setClientName(mMaterialsAccounts[i]);
//            materialDTO.setWarehousingDate(mWareHousingDate[i]);
//            materialDTO.setWarehousingVolume(mMaterialsStock[i]);
//
//            CommonClass.sMaterialDTO.add(materialDTO);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);
        mBinding.btnPlantSearch.setOnClickListener(btnPlantSearchOnClickListener);

        mAdapter = new MaterialsAdapter2();

        mAdapter.setOnItemClickListener(materialsAdapterOnItemClickListener);

        materialsList("");

        mBinding.listMaterials.setAdapter(mAdapter);
        mBinding.listMaterials.setLayoutManager(new LinearLayoutManager(MaterialsManagementActivity.this, LinearLayoutManager.VERTICAL, false));
    }

    MaterialsAdapter2.OnItemClickListener materialsAdapterOnItemClickListener = new MaterialsAdapter2.OnItemClickListener() {
        @Override
        public void onItemClick(int pos) {
            Intent intent = new Intent(MaterialsManagementActivity.this, MaterialsLemonRegistryActivity.class);
            intent.putExtra("pos", pos);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

    View.OnClickListener btnPlantSearchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MaterialsManagementActivity.this, SearchActivity.class);
            intent.putExtra("tag", "Material");

            startActivity(intent);
        }
    };

    View.OnClickListener btnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void materialsList(String materialsName) {
        Call<ResponseBody> call = CommonClass.sApiService.materialsList(materialsName);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
                        CommonClass.sMaterialDTO.clear();

                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        if (jsonArray_len == 0) {

                        } else {
                            for(int i = 0; i < jsonArray_len; i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                MaterialDTO materialDTO = new MaterialDTO();
                                materialDTO.setId(jsonObject.getInt("materialsCode"));
                                materialDTO.setMaterialName(jsonObject.getString("materialsName"));
                                materialDTO.setInStock(jsonObject.getInt("inStock"));
                                materialDTO.setOutStock(jsonObject.getInt("outStock"));
                                materialDTO.setStock(jsonObject.getInt("stock"));
                                CommonClass.sMaterialDTO.add(materialDTO);
                                mAdapter.notifyItemChanged(i);
                            }
                        }
                    } catch (JSONException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: JSON Parsing Error - findId");
                        Log.e("plantManagement", "ERROR: JSON Parsing Error - equipList");
                        if (!MaterialsManagementActivity.this.isFinishing())
                            CommonClass.showDialog(MaterialsManagementActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: IOException - findId");
                        Log.e("plantManagement", "ERROR: IOException - equipList");
                        if (!MaterialsManagementActivity.this.isFinishing())
                            CommonClass.showDialog(MaterialsManagementActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }

                } else {
                    if (!MaterialsManagementActivity.this.isFinishing())
                        CommonClass.showDialog(MaterialsManagementActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!MaterialsManagementActivity.this.isFinishing())
                    CommonClass.showDialog(MaterialsManagementActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }
}