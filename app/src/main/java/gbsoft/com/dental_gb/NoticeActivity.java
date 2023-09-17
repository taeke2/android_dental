package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import gbsoft.com.dental_gb.adapter.NoticeAdapter;
import gbsoft.com.dental_gb.databinding.ActivityNoticeBinding;
import gbsoft.com.dental_gb.dto.NoticeDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends AppCompatActivity {
    private ActivityNoticeBinding mBinding;
    private NoticeAdapter mNoticeAdapter;
    private String mClientCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialSet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNoticeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    View.OnClickListener searchClick = v -> {
        Intent intent = new Intent(NoticeActivity.this, SearchActivity.class);
        intent.putExtra("tag", "Notice");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    };

    private void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);
        String serverPath = sharedPreferences.getString("address", "");
        mClientCode = sharedPreferences.getString("clientCode", "-1");
        mClientCode = String.format("%06d", Integer.parseInt(mClientCode));
        if (CommonClass.sApiService == null)
            CommonClass.getApiService(serverPath);

        mBinding.btnBack.setOnClickListener(v -> finish());
        mBinding.btnSearch.setOnClickListener(searchClick);
//        mBinding.btnSearch.setOnClickListener(searchClick);

        mNoticeAdapter = new NoticeAdapter();
        mNoticeAdapter.setOnItemClickListener(noticeClick);
        mBinding.recyclerNotice.setAdapter(mNoticeAdapter);
        mBinding.recyclerNotice.setLayoutManager(new LinearLayoutManager(NoticeActivity.this));

        getNoticeList("");
    }

    private NoticeAdapter.OnItemClickListener noticeClick = pos -> {
        Intent intent = new Intent(NoticeActivity.this, NoticeDetailActivity.class);
        NoticeDTO item = mNoticeAdapter.getItem(pos);
        intent.putExtra("title", item.getTitle());
        intent.putExtra("manager", item.getManager());
        intent.putExtra("date", item.getDate());
        intent.putExtra("content", item.getContent());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    };

    private void hideResultView(){
        mBinding.recyclerNotice.setVisibility(View.GONE);
        mBinding.loading.setVisibility(View.VISIBLE);
        mBinding.txtEmpty.setVisibility(View.GONE);
    }

    private void visibleResultView(){
        mBinding.recyclerNotice.setVisibility(View.VISIBLE);
        mBinding.loading.setVisibility(View.GONE);
        mBinding.txtEmpty.setVisibility(View.GONE);
    }

    private void getNoticeList(String searchTxt) {
        hideResultView();
        Call<ResponseBody> call = CommonClass.sApiService.getNotice(mClientCode, searchTxt);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        mNoticeAdapter.clearItem();
                        int arrayLen = jsonArray.length();
                        if (arrayLen == 0) {
                            mBinding.loading.setVisibility(View.GONE);
                            mBinding.txtEmpty.setVisibility(View.VISIBLE);
                        } else {
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
                                mNoticeAdapter.addItem(item);
                            }

                            mNoticeAdapter.notifyDataSetChanged();
                            visibleResultView();
                        }
                    }  catch (IOException | JSONException e) {
                        if (!NoticeActivity.this.isFinishing())
                            CommonClass.showDialog(NoticeActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    if (!NoticeActivity.this.isFinishing())
                        CommonClass.showDialog(NoticeActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!NoticeActivity.this.isFinishing())
                    CommonClass.showDialog(NoticeActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }
}