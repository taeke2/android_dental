package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import gbsoft.com.dental_gb.adapter.ClientAdapter;
import gbsoft.com.dental_gb.adapter.NoticeAdapter;
import gbsoft.com.dental_gb.databinding.ActivitySearchBinding;
import gbsoft.com.dental_gb.dto.ClientDTO;
import gbsoft.com.dental_gb.dto.NoticeDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding mBinding;
    private String tag;
    private MaterialsAdapter2 mMaterialsAdapter; // 자재
    private PlantAdapter mPlantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialize(){
        mBinding.txtCancel.setOnClickListener(v -> finish());

        mBinding.loading.setVisibility(View.GONE);
        mBinding.txtEmptyDate.setVisibility(View.GONE);
        mBinding.listPlant.setVisibility(View.GONE);

        Intent intent = getIntent();

        if(intent.hasExtra("tag")){
            tag = intent.getStringExtra("tag");
        } else {
            finish();
        }

        switch (tag) {
            case "Notice":
                setNotice();
                break;
            case "Client":
                setClient();
                break;
            case "Gold":
                setGold();
                break;
            case "Material":
                setMaterial();
                break;
            case "Plant":
                setPlant();
                break;
            case "Request":
                setRequest();
                break;
        }
    }

    private void hideResultView() {
        mBinding.loading.setVisibility(View.VISIBLE);
        mBinding.txtEmptyDate.setVisibility(View.GONE);
        mBinding.listPlant.setVisibility(View.GONE);
    }

    private void showResultView() {
        mBinding.listPlant.setVisibility(View.VISIBLE);
        mBinding.loading.setVisibility(View.GONE);
        mBinding.txtEmptyDate.setVisibility(View.GONE);
    }

    private void setNotice(){
        mBinding.searchView.setQueryHint(getString(R.string.search_hint, getString(R.string.title)));
        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mBinding.searchView.getWindowToken(), 0);
                getNoticeList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setClient(){
        mBinding.searchView.setQueryHint(getString(R.string.search_hint, getString(R.string.client_name)));
        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mBinding.searchView.getWindowToken(), 0);
                getClientList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setGold(){
        mBinding.searchView.setQueryHint(getString(R.string.search_hint, getString(R.string.client_name)));
    }

    private void setMaterial(){
        mMaterialsAdapter = new MaterialsAdapter2();
        mMaterialsAdapter.setOnItemClickListener(materialsAdapterOnItemClickListener);
        mBinding.listPlant.setAdapter(mMaterialsAdapter);
        mBinding.listPlant.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));

        mBinding.searchView.setQueryHint(getString(R.string.search_hint, getString(R.string.material_name)));
        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mBinding.searchView.getWindowToken(), 0);

                materialsList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setPlant(){
        mPlantAdapter = new PlantAdapter();
        mPlantAdapter.setOnItemClickListener(plantAdapterOnItemClickListener);
        mBinding.listPlant.setAdapter(mPlantAdapter);
        mBinding.listPlant.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));

        mBinding.searchView.setQueryHint(getString(R.string.search_hint, getString(R.string.equipment_name)));
        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mBinding.searchView.getWindowToken(), 0);

                equipList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setRequest(){
        mBinding.searchView.setQueryHint(getString(R.string.search_hint, getString(R.string.patient_name)));
    }


    private void getNoticeList(String searchTxt) {
        hideResultView();
        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        String clientCode = sharedPreferences.getString("clientCode", "-1");
        clientCode = String.format("%06d", Integer.parseInt(clientCode));
        Call<ResponseBody> call = CommonClass.sApiService.getNotice(clientCode, searchTxt);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int arrayLen = jsonArray.length();
                        if (arrayLen == 0) {
                            mBinding.loading.setVisibility(View.GONE);
                            mBinding.txtEmptyDate.setVisibility(View.VISIBLE);
                        } else {
                            NoticeAdapter noticeAdapter = new NoticeAdapter();
                            noticeAdapter.clearItem();
                            for (int i = 0; i < arrayLen; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                NoticeDTO item = new NoticeDTO(jsonObject.getInt("noticeCode"),
                                        jsonObject.getString("manager").equals("null") ? "-" : jsonObject.getString("manager"),
                                        jsonObject.getString("title"),
                                        jsonObject.getString("content"),
                                        jsonObject.getString("date"),
                                        jsonObject.getString("endDate").equals("") ? "-" : jsonObject.getString("endDate"),
                                        jsonObject.getBoolean("importantYn"),
                                        jsonObject.getString("target").equals("") ? "-" : jsonObject.getString("target"));
                                noticeAdapter.addItem(item);
                            }

                            noticeAdapter.setOnItemClickListener(pos -> {noticeClick(noticeAdapter.getItem(pos));});
                            mBinding.listPlant.setAdapter(noticeAdapter);
                            mBinding.listPlant.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                            noticeAdapter.notifyDataSetChanged();

                            showResultView();
                        }
                    }  catch (IOException | JSONException e) {
                        if (!SearchActivity.this.isFinishing())
                            CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    if (!SearchActivity.this.isFinishing())
                        CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!SearchActivity.this.isFinishing())
                    CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    private void noticeClick(NoticeDTO item) {
        Intent intent = new Intent(SearchActivity.this, NoticeDetailActivity.class);
        intent.putExtra("title", item.getTitle());
        intent.putExtra("manager", item.getManager());
        intent.putExtra("date", item.getDate());
        intent.putExtra("content", item.getContent());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void getClientList(String searchTxt) {
        hideResultView();
        Call<ResponseBody> call = CommonClass.sApiService.getClient(searchTxt);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int arrayLen = jsonArray.length();
                        if (arrayLen == 0) {
                            mBinding.loading.setVisibility(View.GONE);
                            mBinding.txtEmptyDate.setVisibility(View.VISIBLE);
                        } else {
                            ClientAdapter clientAdapter = new ClientAdapter();
                            clientAdapter.clearItem();
                            for (int i = 0; i < arrayLen; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ClientDTO item = new ClientDTO(
                                        jsonObject.getInt("clientCode"),
                                        jsonObject.getString("clientKey"),
                                        jsonObject.getString("clientName"),
                                        jsonObject.getString("type"),
                                        jsonObject.getString("representative").equals("null") ? "-" : jsonObject.getString("representative"),
                                        jsonObject.getString("businessLicenseNum").equals("null") ? "-" : jsonObject.getString("businessLicenseNum"),
                                        jsonObject.getString("address").equals("null") ? "-" : jsonObject.getString("address"),
                                        jsonObject.getString("telNum").equals("null") ? "-" : jsonObject.getString("telNum"),
                                        jsonObject.getString("faxNum").equals("null") ? "-" : jsonObject.getString("faxNum"),
                                        !jsonObject.getString("sysUseYn").equals("null") && jsonObject.getBoolean("sysUseYn"),
                                        jsonObject.getString("email").equals("null") ? "-" : jsonObject.getString("email"),
                                        jsonObject.getString("reference").equals("null") ? "-" : jsonObject.getString("reference"));
                                clientAdapter.addItem(item);
                            }

                            clientAdapter.setOnItemClickListener(pos -> {clientClick(clientAdapter.getItem(pos));});
                            mBinding.listPlant.setAdapter(clientAdapter);
                            mBinding.listPlant.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                            clientAdapter.notifyDataSetChanged();

                            showResultView();
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: catch Error - getClientList");
                        if (!SearchActivity.this.isFinishing())
                            CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    if (!SearchActivity.this.isFinishing())
                        CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!SearchActivity.this.isFinishing())
                    CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    private void clientClick(ClientDTO item) {
        Intent intent = new Intent(SearchActivity.this, ClientDetailActivity.class);
        intent.putExtra("code", item.getCode());
        intent.putExtra("key", item.getKey());
        intent.putExtra("name", item.getName());
        intent.putExtra("type", item.getType());
        intent.putExtra("tel", item.getTel());
        intent.putExtra("fax", item.getFax());
        intent.putExtra("address", item.getAddress());
        intent.putExtra("representative", item.getRepresentative());
        intent.putExtra("email", item.getEmail());
        intent.putExtra("refer", item.getRefer());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void materialsList(String materialsName) {
        hideResultView();
        Call<ResponseBody> call = CommonClass.sApiService.materialsList(materialsName);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        mMaterialsAdapter.notifyItemRangeRemoved(0, mMaterialsAdapter.getItemCount());
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
                                mMaterialsAdapter.notifyItemChanged(i);
                            }

                            showResultView();
                        }
                    } catch (JSONException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: JSON Parsing Error - findId");
                        Log.e("plantManagement", "ERROR: JSON Parsing Error - materialsList");
                        if (!SearchActivity.this.isFinishing())
                            CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: IOException - findId");
                        Log.e("plantManagement", "ERROR: IOException - materialsList");
                        if (!SearchActivity.this.isFinishing())
                            CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }

                } else {
                    if (!SearchActivity.this.isFinishing())
                        CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!SearchActivity.this.isFinishing())
                    CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }

    MaterialsAdapter2.OnItemClickListener materialsAdapterOnItemClickListener = new MaterialsAdapter2.OnItemClickListener() {
        @Override
        public void onItemClick(int pos) {
            Intent intent = new Intent(SearchActivity.this, MaterialsLemonRegistryActivity.class);
            intent.putExtra("pos", pos);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

    private void equipList(String equipName) {
        hideResultView();
        Call<ResponseBody> call = CommonClass.sApiService.equipList(equipName);
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
                            showResultView();
                        }
                    } catch (JSONException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: JSON Parsing Error - findId");
                        Log.e("plantManagement", "ERROR: JSON Parsing Error - equipList");
                        if (!SearchActivity.this.isFinishing())
                            CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: IOException - findId");
                        Log.e("plantManagement", "ERROR: IOException - equipList");
                        if (!SearchActivity.this.isFinishing())
                            CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }

                } else {
                    if (!SearchActivity.this.isFinishing())
                        CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!SearchActivity.this.isFinishing())
                    CommonClass.showDialog(SearchActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }

    PlantAdapter.OnItemClickListener plantAdapterOnItemClickListener = new PlantAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int pos) {
            Intent intent = new Intent(SearchActivity.this, PlantDetailActivity.class);
            intent.putExtra("pos", pos);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };
}