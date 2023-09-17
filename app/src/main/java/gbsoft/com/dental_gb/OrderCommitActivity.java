package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import gbsoft.com.dental_gb.databinding.ActivityOrderCommitBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderCommitActivity extends Activity {
    private static final String sCommitProcess = "process";
    private static final String sCommitFaulty = "faulty";

    private ActivityOrderCommitBinding mBinding;

    private String mCommit = "";
    private String mOrderBarcode = "";
    private String mProductInfoCode = "";
    private int mProcessState;
    private int mProcessTag;
    private String mRemark = "";
    private String mProductCode = "";
    private String mBoxNo = "";

    private String mFaultyType;
    private String mProductProcess;

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityOrderCommitBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(OrderCommitActivity.this.getApplicationContext())) {
            if (!OrderCommitActivity.this.isFinishing())
                CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
        } else {
            initialSet();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", MODE_PRIVATE);

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);
        mServerPath = sharedPreferences.getString("address", "");

        if (CommonClass.sApiService == null)
            CommonClass.getApiService(mServerPath);

        boolean isPartners = mCode == CommonClass.PDL;

        if (isPartners) {
            getShutdownCheck();
        }

        Intent intent = getIntent();
        mCommit = intent.getStringExtra("commit");
        mOrderBarcode = intent.getStringExtra("ordNum");
        String productName = intent.getStringExtra("prdName");
        mProductProcess = intent.getStringExtra("prdNames");
        mProductInfoCode = intent.getStringExtra("prdInfoNum");
        mProductCode = intent.getStringExtra("prdNum");
        mProcessState = intent.getIntExtra("prcState", -1);
        mProcessTag = intent.getIntExtra("prcTag", -1);
        mBoxNo = intent.getStringExtra("boxNo");

        String[] productProcesses = mProductProcess.split("→");

        mBinding.layoutChkInfo.setVisibility(View.GONE);
        mBinding.btnCancel1.setOnClickListener(v -> onBackPressed());
        mBinding.btnCancel2.setOnClickListener(v -> onBackPressed());

        if (mCommit.equals(sCommitProcess)) {
            mBinding.processFaulty.setVisibility(View.GONE);
            mBinding.processCommit.setVisibility(View.VISIBLE);

            mBinding.txtProductName2.setText(productName);
            mBinding.txtProcess.setText(productProcesses[mProcessTag]);

            if (isPartners) {
                mBinding.layoutChkInfo.setVisibility(View.VISIBLE);
                getCheckInfo();
            }

            mBinding.btnProcessCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRemark = mBinding.editRemark.getText().toString();
                    if (mProcessState >= mProcessTag) {
                        modifyProcess();
                    } else {
                        for (int i = mProcessState + 1; i <= mProcessTag; i++) {
                            processCommit(i);
                        }
                    }
                }
            });
        } else if (mCommit.equals(sCommitFaulty)) {
            mBinding.processCommit.setVisibility(View.GONE);
            mBinding.processFaulty.setVisibility(View.VISIBLE);

            mBinding.txtProductName1.setText(productName);
            mBinding.txtFaultyProcess.setText(productProcesses[mProcessTag]);

            getFaultyType();

            mBinding.btnFaultyCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRemark = mBinding.txtFaultyHistory.getText().toString();
                    faultyCommit();
                }
            });
        }
    }

    public void getFaultyType() {
        Call<ResponseBody> call = CommonClass.sApiService.getFaultyType(mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        ArrayList<String> list_faultyType = new ArrayList<>();
                        String faulty = "FaultyType";
                        if (mCode == CommonClass.PDL)   faulty = "faulty";
                        for (int i = 0; i < jsonArray_len; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            list_faultyType.add(jsonObject.getString(faulty));
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_faultyType);
                        mBinding.spnFaultyType.setAdapter(arrayAdapter);
                        mBinding.spnFaultyType.setSelection(0);
                        mBinding.txtFaultyType.setText(mBinding.spnFaultyType.getSelectedItem().toString());
                        mBinding.txtFaultyType.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               mBinding.spnFaultyType.performClick();
                            }
                        });
                        mBinding.spnFaultyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mFaultyType = mBinding.spnFaultyType.getSelectedItem().toString();
                                mBinding.txtFaultyType.setText(mBinding.spnFaultyType.getSelectedItem().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                mFaultyType = "";
                                mBinding.txtFaultyType.setText("");
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error getFaultyType");
                        if (!OrderCommitActivity.this.isFinishing())
                            CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!OrderCommitActivity.this.isFinishing())
                            CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!OrderCommitActivity.this.isFinishing())
                        CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!OrderCommitActivity.this.isFinishing())
                    CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    public void getCheckInfo() {
        String[] processNames = mProductProcess.split("→");
        int ps = (mProcessState > mProcessTag) ? mProcessTag : mProcessState + 1;
        ArrayList<String> pNames = new ArrayList<>();
        for (int i = ps; i <= mProcessTag; i++) {
            pNames.add(processNames[i]);
        }
        Call<ResponseBody> call = CommonClass.sApiService.getCheckInfo(mProductCode, Integer.toString(ps), Integer.toString(mProcessTag), mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);

                        int jsonArr_len = jsonArray.length();
                        ArrayList<CheckInfoItem> list = new ArrayList<>();
                        for (int i = 0; i < jsonArr_len; i++) {
                            CheckInfoItem item = new CheckInfoItem();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String text = jsonObject.getString("chkList");
                            String chkInfo = (text.equals("null")) ? "-" : text;
                            item.setProcessName(pNames.get(i));
                            item.setChkInfo(chkInfo);
                            list.add(item);
                        }

                        CheckInfoAdapter checkInfoAdapter = new CheckInfoAdapter(list);
                        mBinding.rvChkInfo.setAdapter(checkInfoAdapter);
                        mBinding.rvChkInfo.setLayoutManager(new LinearLayoutManager(OrderCommitActivity.this));

                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error getCheckInfo");
                        if (!OrderCommitActivity.this.isFinishing())
                            CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!OrderCommitActivity.this.isFinishing())
                            CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!OrderCommitActivity.this.isFinishing())
                        CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!OrderCommitActivity.this.isFinishing())
                    CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    public void modifyProcess() {
        Call<ResponseBody> call = CommonClass.sApiService.deleteProcess(mOrderBarcode, mProcessTag, mProcessState, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (result.equals("OK")) {
                            processCommit(mProcessTag);
                        }
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!OrderCommitActivity.this.isFinishing())
                            CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!OrderCommitActivity.this.isFinishing())
                        CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!OrderCommitActivity.this.isFinishing())
                    CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    public void processCommit(int state) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String processTime = format.format(date);

        Call<ResponseBody> call = CommonClass.sApiService.processCommit(mOrderBarcode, mProductInfoCode, "" + state, mId, processTime, mRemark, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (mProcessTag == state) {
                        Toast.makeText(getApplicationContext(), getString(R.string.order_progress), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("orderFinishTime", processTime);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else{
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!OrderCommitActivity.this.isFinishing())
                            CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!OrderCommitActivity.this.isFinishing())
                        CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!OrderCommitActivity.this.isFinishing())
                    CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    public void faultyCommit() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String faultyDateTime = format.format(date);

        Call<ResponseBody> call = CommonClass.sApiService.faultyCommit(mOrderBarcode, mProductInfoCode, mId, mProcessTag, mFaultyType, faultyDateTime, mRemark, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (result.equals("OK")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.complete_order_fault), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!OrderCommitActivity.this.isFinishing())
                            CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!OrderCommitActivity.this.isFinishing())
                        CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!OrderCommitActivity.this.isFinishing())
                    CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);
        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void getShutdownCheck() {
        Call<ResponseBody> call = CommonClass.sApiService.getShutdownCheck(mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        int mShutdownYn = Integer.parseInt(jsonObject.getString("down"));

                        if (mShutdownYn == 1) {
                            if (!OrderCommitActivity.this.isFinishing()) {
                                CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error getShutdownCheck");
                        if (!OrderCommitActivity.this.isFinishing())
                            CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!OrderCommitActivity.this.isFinishing())
                            CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!OrderCommitActivity.this.isFinishing())
                        CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!OrderCommitActivity.this.isFinishing())
                    CommonClass.showDialog(OrderCommitActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }
}