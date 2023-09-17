package gbsoft.com.dental_gb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ActivityRequestDetail2Binding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDetailActivity2 extends AppCompatActivity {

    private ActivityRequestDetail2Binding mBinding;
    private int index;
    private RequestDetailAdapter_YK mAdapter;
    private RequestDTO mRequestDTO;
    private float scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRequestDetail2Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialize(){
        Intent intent = getIntent();
        index = intent.getIntExtra("pos", -1);
        if(index < 0) finish();

        mRequestDTO = CommonClass.sRequestDTO.get(index);

        mBinding.txtClientName.setText(mRequestDTO.getClientName());
        mBinding.txtPatientName.setText(mRequestDTO.getPatientName());
        mBinding.txtOrderDate.setText(mRequestDTO.getOrderDate().substring(0, 10));
        mBinding.txtDeadlineDate.setText(mRequestDTO.getDeadlineDate() + " " + mRequestDTO.getDeadlineTime());

        mRequestDTO.setRequestDetailDTOS(new ArrayList<>());

        // 화면 크기 가져오기
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        scale = outMetrics.density;

        requestProductState();



//        String[] mProductName = {"임플란트", "교정", "틀니"};
//        String[] mProductPart = {"일반보철", "덴처", "교정"};
//        String[] mWorkOrderDate = {"2021-06-28 PM 16:54:23", "2021-12-12 AM 10:12:15", "2022-02-01 PM 18:00:23"};
//        String[] mProcessProgressDate = {"2021-06-29 PM 16:22:23", "2021-12-13 AM 10:12:15", "2022-02-02 PM 18:00:23"};
//        String[] mProgressCompleteDate = {"2021-06-30 PM 12:54:23", "2021-12-14 AM 10:12:15", ""};
//        String[] mProductOutDate = {"", "2021-12-15 AM 10:12:15", ""};
//        String[] mDent1 = {"87654321", "631", "321"};
//        String[] mDent2 = {"12345678", "123", "278"};
//        String[] mDent3 = {"87654321", "752", "321"};
//        String[] mDent4 = {"12345678", "257", "123"};
//
//        String[] mShade1 = { "1", "0", "1"};
//        String[] mShade2 = { "0", "1", "1"};
//        String[] mShade3 = { "1", "0", "1"};
//        String[] mShade4 = { "1", "1", "0"};
//        String[] mShade5 = { "1", "0", "1"};
//        String[] mShade6 = { "0", "1", "1"};
//        String[] mShade7 = { "1", "0", "1"};
//        String[] mShade8 = { "0", "1", "0"};
//        String[] mShade9 = { "1", "0", "1"};
//
//        String[] mImplantCrown = {"","",""};
//        String[] mSpaceLack = {"","",""};
//        String[] mPonticDesign = {"","",""};
//        String[] mMetalDesign = {"","",""};
//        String[] mFullDesign = {"","",""};
//        String[] mFlexibleDenture = {"","",""};
//        String[] mJaw = {"","",""};
//        String[] mPartialDenture = {"","",""};
//        String[] mDentureExtra = {"","",""};
//        String[] mMemo = {"","",""};
//        String[] mType = {"일반", "일반", "일반"};
//
//
//        for(int i = 0; i < 3; i++){
//            RequestDTO.RequestDetailDTO requestDetailDTO = requestDTO.new RequestDetailDTO();
//
//            requestDetailDTO.setProductName(mProductName[i]);
//            requestDetailDTO.setProductPart(mProductPart[i]);
//            requestDetailDTO.setWorkOrderDate(mWorkOrderDate[i]);
//            requestDetailDTO.setProcessProgressDate(mProcessProgressDate[i]);
//            requestDetailDTO.setProgressCompleteDate(mProgressCompleteDate[i]);
//            requestDetailDTO.setProductOutDate(mProductOutDate[i]);
//            requestDetailDTO.setDent1(mDent1[i]);
//            requestDetailDTO.setDent2(mDent2[i]);
//            requestDetailDTO.setDent3(mDent3[i]);
//            requestDetailDTO.setDent4(mDent4[i]);
//            requestDetailDTO.setShade1(mShade1[i]);
//            requestDetailDTO.setShade2(mShade2[i]);
//            requestDetailDTO.setShade3(mShade3[i]);
//            requestDetailDTO.setShade4(mShade4[i]);
//            requestDetailDTO.setShade5(mShade5[i]);
//            requestDetailDTO.setShade6(mShade6[i]);
//            requestDetailDTO.setShade7(mShade7[i]);
//            requestDetailDTO.setShade8(mShade8[i]);
//            requestDetailDTO.setShade9(mShade9[i]);
//            requestDetailDTO.setImplantCrown(mImplantCrown[i]);
//            requestDetailDTO.setSpaceLack(mSpaceLack[i]);
//            requestDetailDTO.setPonticDesign(mPonticDesign[i]);
//            requestDetailDTO.setMetalDesign(mMetalDesign[i]);
//            requestDetailDTO.setFullDesign(mFullDesign[i]);
//            requestDetailDTO.setFlexibleDenture(mFlexibleDenture[i]);
//            requestDetailDTO.setJaw(mJaw[i]);
//            requestDetailDTO.setPartialDenture(mPartialDenture[i]);
//            requestDetailDTO.setDentureExtra(mDentureExtra[i]);
//            requestDetailDTO.setMemo(mMemo[i]);
//            requestDetailDTO.setType(mType[i]);
//
//            requestDTO.getRequestDetailDTOS().add(requestDetailDTO);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);

        mAdapter = new RequestDetailAdapter_YK(index, scale, RequestDetailActivity2.this);

        mBinding.listRequestDetail.setAdapter(mAdapter);
        mBinding.listRequestDetail.setLayoutManager(new LinearLayoutManager(RequestDetailActivity2.this, LinearLayoutManager.VERTICAL, false));

    }


    androidx.recyclerview.widget.RecyclerView.OnScrollListener onScrollListener = new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
            int totalItemCount = layoutManager.getItemCount();
            int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();

            if (lastVisible >= totalItemCount - 1) {
                Log.d("test:", "lastVisibled");
            }
        }
    };


    View.OnClickListener btnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void requestProductState() {
        Call<ResponseBody> call = CommonClass.sApiService.requestProductState(mRequestDTO.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        if (jsonArray_len == 0) {

                        } else {
                            StringBuilder builder = new StringBuilder();
                            for(int i = 0; i < jsonArray_len; i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                RequestDTO.RequestDetailDTO requestDetailDTO = mRequestDTO.new RequestDetailDTO();

                                requestDetailDTO.setProductName(jsonObject.getString("productName"));
                                requestDetailDTO.setProductPart(jsonObject.getString("part"));
                                requestDetailDTO.setOrderCk(jsonObject.getInt("orderCk"));
                                requestDetailDTO.setProcessCk(jsonObject.getInt("processCk"));
                                requestDetailDTO.setOutmntCk(jsonObject.getInt("outmntCk"));
                                requestDetailDTO.setWorkOrderDate(jsonObject.getString("orderDate"));
                                requestDetailDTO.setProcessProgressDate(jsonObject.getString("startTime"));
                                requestDetailDTO.setProgressCompleteDate(jsonObject.getString("endTime"));
                                requestDetailDTO.setProductOutDate(jsonObject.getString("outDateTime"));
                                requestDetailDTO.setType(jsonObject.getString("remark"));

                                String[] mDentArr = jsonObject.getString("dentalFormula").split("/");

                                for(int j = 0; j < mDentArr.length; j++){
                                    String[] tempArr = mDentArr[j].split(",");
                                    for (String name : tempArr) {
                                        builder.append(name).append("");
                                    }

                                    switch (j){
                                        case 0:
                                            requestDetailDTO.setDent1(builder.toString());
                                            break;
                                        case 1:
                                            requestDetailDTO.setDent2(builder.toString());
                                            break;
                                        case 2:
                                            requestDetailDTO.setDent3(builder.toString());
                                            break;
                                        case 3:
                                            requestDetailDTO.setDent4(builder.toString());
                                            break;
                                    }
                                    builder.delete(0, builder.toString().length());
                                }

                                mRequestDTO.getRequestDetailDTOS().add(requestDetailDTO);
                                mAdapter.notifyItemChanged(i);
                            }
                        }
                    } catch (JSONException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: JSON Parsing Error - findId");
                        Log.e("RequestDetailActivity2", "ERROR: JSON Parsing Error - requestProductState");
                        if (!RequestDetailActivity2.this.isFinishing())
                            CommonClass.showDialog(RequestDetailActivity2.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: IOException - findId");
                        Log.e("RequestDetailActivity2", "ERROR: IOException - requestProductState");
                        if (!RequestDetailActivity2.this.isFinishing())
                            CommonClass.showDialog(RequestDetailActivity2.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }

                } else {
                    if (!RequestDetailActivity2.this.isFinishing())
                        CommonClass.showDialog(RequestDetailActivity2.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!RequestDetailActivity2.this.isFinishing())
                    CommonClass.showDialog(RequestDetailActivity2.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }
}
