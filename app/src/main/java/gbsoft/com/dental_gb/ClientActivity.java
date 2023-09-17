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

import gbsoft.com.dental_gb.adapter.ClientAdapter;
import gbsoft.com.dental_gb.databinding.ActivityClientBinding;
import gbsoft.com.dental_gb.dto.ClientDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientActivity extends AppCompatActivity {
    private ActivityClientBinding mBinding;
    private ClientAdapter mClientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityClientBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialSet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideResultView();
        getClientList("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void initialSet() {
        if (CommonClass.sApiService == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);
            String serverPath = sharedPreferences.getString("address", "");
            CommonClass.getApiService(serverPath);
        }

        mBinding.btnBack.setOnClickListener(v -> finish());
        mBinding.btnSearch.setOnClickListener(searchClick);

        mClientAdapter = new ClientAdapter();
        mClientAdapter.setOnItemClickListener(clientClick);
        mBinding.recyclerClient.setAdapter(mClientAdapter);
        mBinding.recyclerClient.setLayoutManager(new LinearLayoutManager(ClientActivity.this));
    }

    private ClientAdapter.OnItemClickListener clientClick = pos -> {
        Intent intent = new Intent(ClientActivity.this, ClientDetailActivity.class);
        ClientDTO item = mClientAdapter.getItem(pos);
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
    };

    private void hideResultView(){
        mBinding.recyclerClient.setVisibility(View.GONE);
        mBinding.loading.setVisibility(View.VISIBLE);
        mBinding.txtEmpty.setVisibility(View.GONE);
    }

    private void visibleResultView(){
        mBinding.recyclerClient.setVisibility(View.VISIBLE);
        mBinding.loading.setVisibility(View.GONE);
        mBinding.txtEmpty.setVisibility(View.GONE);
    }

    private View.OnClickListener searchClick = v -> {
        Intent intent = new Intent(ClientActivity.this, SearchActivity.class);
        intent.putExtra("tag", "Client");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    };

    private void getClientList(String searchTxt) {
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
                            mBinding.recyclerClient.setVisibility(View.GONE);
                            mBinding.txtEmpty.setVisibility(View.VISIBLE);
                        } else {
                            mClientAdapter.clearItem();
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
                                mClientAdapter.addItem(item);
                            }
                            mClientAdapter.notifyDataSetChanged();
                            visibleResultView();
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: catch Error - getClientList");
                        if (!ClientActivity.this.isFinishing())
                            CommonClass.showDialog(ClientActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    if (!ClientActivity.this.isFinishing())
                        CommonClass.showDialog(ClientActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!ClientActivity.this.isFinishing())
                    CommonClass.showDialog(ClientActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }
}