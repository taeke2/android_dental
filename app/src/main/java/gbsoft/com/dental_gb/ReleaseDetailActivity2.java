package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.zxing.common.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ActivityReleaseDetail2Binding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReleaseDetailActivity2 extends AppCompatActivity {

    private ActivityReleaseDetail2Binding mBinding;
    private int index;
    private ReleaseDetailAdapter2 mAdapter;
    private ReleaseDTO mReleaseDTO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityReleaseDetail2Binding.inflate(getLayoutInflater());
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

        mReleaseDTO = CommonClass.sReleaseDTO.get(index);

        mBinding.txtClientName.setText(mReleaseDTO.getClientName());
        mBinding.txtPatientName.setText(mReleaseDTO.getPatientName());
        mBinding.txtOrderDate.setText(mReleaseDTO.getOrderDate());
        mBinding.txtDeadlineDate.setText(mReleaseDTO.getDeadlineDate() + " " + mReleaseDTO.getDueTime());

        mReleaseDTO.setReleaseProductDTOS(new ArrayList<>());

        releaseProductList();

//        String[] mProductNames = {"임플란트", "교정"};
//        String[] mManagerNames = {"조해찬", "김설기"};
//        String[] mOrderTimes = {"2021-06-28 PM 16:54:23", "2022-02-05 PM 17:50:50"};
//        String[] mOutTimes = {"2021-06-28 PM 17:50:22", "2022-02-07 PM 13:11:40"};
//        String[] mDent1 = {"87654321", "631"};
//        String[] mDent2 = {"12345678", "123"};
//        String[] mDent3 = {"87654321", "752"};
//        String[] mDent4 = {"12345678", "257"};
//
//        releaseDTO.setReleaseProductDTOS(new ArrayList<>());
//
//        for(int i = 0; i < 2; i++){
//            ReleaseDTO.ReleaseProductDTO releaseProductDTO = releaseDTO.new ReleaseProductDTO();
//            releaseProductDTO.setProductName(mProductNames[i]);
//            releaseProductDTO.setManagerName(mManagerNames[i]);
//            releaseProductDTO.setOrderDateTime(mOrderTimes[i]);
//            releaseProductDTO.setOutDateTime(mOutTimes[i]);
//            releaseProductDTO.setDent1(mDent1[i]);
//            releaseProductDTO.setDent2(mDent2[i]);
//            releaseProductDTO.setDent3(mDent3[i]);
//            releaseProductDTO.setDent4(mDent4[i]);
//
//            releaseDTO.getReleaseProductDTOS().add(releaseProductDTO);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);

        mAdapter = new ReleaseDetailAdapter2(index);

        mBinding.listReleaseDetail.setAdapter(mAdapter);
        mBinding.listReleaseDetail.setLayoutManager(new LinearLayoutManager(ReleaseDetailActivity2.this, LinearLayoutManager.VERTICAL, false));
    }

    View.OnClickListener btnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void releaseProductList() {
        Call<ResponseBody> call = CommonClass.sApiService.releaseProductList(mReleaseDTO.getId());
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
                                ReleaseDTO.ReleaseProductDTO releaseProductDTO = mReleaseDTO.new ReleaseProductDTO();
                                releaseProductDTO.setProductName(jsonObject.getString("productName"));
                                releaseProductDTO.setOrderDateTime(jsonObject.getString("requestDate"));
                                releaseProductDTO.setManagerName("미지정");
                                releaseProductDTO.setOutDateTime("-");

                                if(!jsonObject.getString("manager").equals("null"))
                                    releaseProductDTO.setManagerName(jsonObject.getString("manager"));
                                if(!jsonObject.getString("outDateTime").equals("null"))
                                    releaseProductDTO.setOutDateTime(jsonObject.getString("outDateTime"));

                                String[] mDentArr = jsonObject.getString("dentalFormula").split("/");

                                for(int j = 0; j < mDentArr.length; j++){
                                    String[] tempArr = mDentArr[j].split(",");
                                    for (String name : tempArr) {
                                        builder.append(name).append("");
                                    }

                                    switch (j){
                                        case 0:
                                            releaseProductDTO.setDent1(builder.toString());
                                            break;
                                        case 1:
                                            releaseProductDTO.setDent2(builder.toString());
                                            break;
                                        case 2:
                                            releaseProductDTO.setDent3(builder.toString());
                                            break;
                                        case 3:
                                            releaseProductDTO.setDent4(builder.toString());
                                            break;
                                    }
                                    builder.delete(0, builder.toString().length());
                                }
                                mReleaseDTO.getReleaseProductDTOS().add(releaseProductDTO);
                                mAdapter.notifyItemChanged(i);
                            }
                        }
                    } catch (JSONException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: JSON Parsing Error - findId");
                        Log.e("plantManagement", "ERROR: JSON Parsing Error - equipList");
                        if (!ReleaseDetailActivity2.this.isFinishing())
                            CommonClass.showDialog(ReleaseDetailActivity2.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: IOException - findId");
                        Log.e("plantManagement", "ERROR: IOException - equipList");
                        if (!ReleaseDetailActivity2.this.isFinishing())
                            CommonClass.showDialog(ReleaseDetailActivity2.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }

                } else {
                    if (!ReleaseDetailActivity2.this.isFinishing())
                        CommonClass.showDialog(ReleaseDetailActivity2.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!ReleaseDetailActivity2.this.isFinishing())
                    CommonClass.showDialog(ReleaseDetailActivity2.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }

}