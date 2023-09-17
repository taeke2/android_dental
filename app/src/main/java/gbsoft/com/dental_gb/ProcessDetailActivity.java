package gbsoft.com.dental_gb;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ActivityProcessDetailBinding;
import gbsoft.com.dental_gb.dialog.SpinnerDialog;
import gbsoft.com.dental_gb.dto.ArticulatorDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcessDetailActivity extends AppCompatActivity {
    private ActivityProcessDetailBinding mBinding;

    private String mClientName = "";
    private String mOrderBarcode = "";
    private String mRequestCode = "";
    private String mOrderFinishTime = "";
    private String mBoxName = "";
    private String mBoxNo = "";
    private int mRequestCodeNum;

    private int mPos = -1;
    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";
    private String mName;
    private String mUserProcessName;

    private int mProductCnt = -1;
    private ArrayList<OrderItem> mList;
    private int mProductIdx = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityProcessDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(ProcessDetailActivity.this.getApplicationContext())) {
            if (!ProcessDetailActivity.this.isFinishing())
                CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
        } else {
            if (savedInstanceState != null)
                mBoxName = savedInstanceState.getString("boxNo");
            initialSet();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("boxName", mBoxName);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);
        mUserProcessName = sharedPreferences.getString("processName", "-");
        mName = sharedPreferences.getString("userName", "-");

        mServerPath = sharedPreferences.getString("address", "");

        if (CommonClass.sApiService == null)
            CommonClass.getApiService(mServerPath);

        Intent getIntent = getIntent();
        mPos = getIntent.getIntExtra("pos", -1);

        RequestDTO item = CommonClass.sRequestDTO.get(mPos);
        mClientName = item.getClientName();
        mRequestCodeNum = item.getId();
        mBoxNo = item.getBoxNo();
        mBoxName = item.getBoxName();
        mBinding.txtPatientName.setText(item.getPatientName());
        mBinding.txtBoxNo.setText(getString(R.string.parenthesis, mBoxName));

        mOrderBarcode = getIntent.getStringExtra("ordNum");
        mRequestCode = getIntent.getStringExtra("reqNum");

        mList = new ArrayList<>();
//
        SpannableString content = new SpannableString(mClientName);
        content.setSpan(new UnderlineSpan(), 0, mClientName.length(), 0);
        mBinding.txtClientName.setText(content);
        mBinding.txtDate.setText(getString(R.string.order_deadline_date,
                item.getOrderDate(), item.getDeadlineDate(), item.getDeadlineTime()));

        mBinding.btnBack.setOnClickListener(v -> onBackPressed());
        mBinding.btnRequest.setOnClickListener(requestClick);
//        mBinding.txtBoxNo.setOnClickListener(v -> startSingleBarcodeActivity(false));

        mBinding.layoutRecycler.setVisibility(View.GONE);
        mBinding.layoutProgress.setVisibility(View.VISIBLE);

        if (mCode == CommonClass.YK)
            getProductPattern();
        else
            getOrderList();
    }



    View.OnClickListener requestClick = v -> {
        Intent intent = new Intent(ProcessDetailActivity.this, RequestDetailActivity2.class);
        intent.putExtra("pos", mPos);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    };

    private void getOrderList() {
        mList.clear();
        Call<ResponseBody> call = CommonClass.sApiService.getOrderList(mOrderBarcode, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);

                        int jsonArray_len = jsonArray.length();
                        boolean isFinish = true;

                        for (int i = 0; i < jsonArray_len; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            OrderItem orderItem = new OrderItem();

                            orderItem.setProductCode(jsonObject.getString("prdNum"));
                            orderItem.setProductInfoCode(jsonObject.getString("prdInfoNum"));
                            orderItem.setProductName(jsonObject.getString("prdName"));
                            String processState = jsonObject.getString("prcState");

                            if (processState.equals("null")) {
                                processState = "-1";
                            }

                            int processLen = jsonObject.getInt("prcLen");
                            if (processLen != Integer.parseInt(processState))
                                isFinish = false;

                            orderItem.setProcessState(processState);
                            mList.add(orderItem);
                        }

                        if (isFinish) {
                            setFinishTime();
                        } else {
                            getProcessList();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    private void getProductPattern() {
        mList.clear();
        Call<ResponseBody> call = CommonClass.sApiService.getProduct(mRequestCodeNum);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);

                        int arrayLen = jsonArray.length();
                        for (int i = 0; i < arrayLen; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            OrderItem item = new OrderItem();
                            item.setRequestCodeNum(jsonObject.getInt("requestCode"));
                            item.setProductCodeNum(jsonObject.getInt("productCode"));
                            item.setProductName(jsonObject.getString("productName"));
                            item.setProductProcess(jsonObject.getString("productPattern"));
                            item.setBoxName(mBoxName);
                            item.setBoxNum(mBoxNo);

                            String[] processPattern = item.getProductProcess().split("→", -1);
                            String[] processName = jsonObject.getString("processName").split(",", -1);
                            String[] managers = jsonObject.getString("managers").split(",", -1);
                            String[] startTime = jsonObject.getString("startTime").split(",", -1);
                            String[] endTime = jsonObject.getString("endTime").split(",", -1);
                            String[] memo = jsonObject.getString("memo").split(",", -1);

                            String[] processStartManager = new String[processPattern.length];
                            String[] processStartName = new String[processPattern.length];
                            String[] processStartTime = new String[processPattern.length];
                            String[] processEndTime = new String[processPattern.length];
                            String[] processMemo = new String[processPattern.length];

                            for (int n = 0; n < processPattern.length; n++) {
                                processStartManager[n] = "";
                                processStartName[n] = "";
                                processStartTime[n] = "";
                                processEndTime[n] = "";
                                processMemo[n] = "";
                            }

                            for (int j = 0; j < processName.length; j++) {
                                for (int k = 0; k < processPattern.length; k++) {
                                    if (processPattern[k].equals(processName[j])) {
                                        processStartManager[k] = managers[j];
                                        processStartName[k] = processName[j];
                                        processStartTime[k] = startTime[j];
                                        processEndTime[k] = endTime[j];
                                        processMemo[k] = memo[j];
                                    } else {
                                        processStartManager[k] = processStartManager[k].equals("") ? "" : processStartManager[k];
                                        processStartName[k] = processStartName[k].equals("") ? "" : processStartName[k];
                                        processStartTime[k] = processStartTime[k].equals("") ? "" : processStartTime[k];
                                        processEndTime[k] = processEndTime[k].equals("") ? "" : processEndTime[k];
                                    }
                                }
                            }
                            item.setManagers(TextUtils.join(";", processStartManager));
                            item.setStartedProcessName(TextUtils.join(";", processStartName));
                            item.setStartTime(TextUtils.join(";", processStartTime));
                            item.setEndTime(TextUtils.join(";", processEndTime));
                            item.setMemo(TextUtils.join(";", processMemo));

                            mList.add(item);
                        }

                        OrderProductAdapter2 orderProductAdapter = new OrderProductAdapter2(ProcessDetailActivity.this, mList);
                        orderProductAdapter.notifyDataSetChanged();
                        orderProductAdapter.setOnItemClickListener(pos -> {mProductIdx = pos;});
                        orderProductAdapter.setOnProcessItemClickListener(processClickListener2);
                        // orderProductAdapter.setOnProcessItemLongClickListener(processLongClickListener2);

                        mBinding.productRecycler.setAdapter(orderProductAdapter);
                        mBinding.productRecycler.setLayoutManager(new LinearLayoutManager(ProcessDetailActivity.this));

                        mBinding.layoutRecycler.setVisibility(View.VISIBLE);
                        mBinding.layoutProgress.setVisibility(View.GONE);
                    } catch (IOException | JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    private void getProcessList() {
        mProductCnt = 0;
        processHandler.sendEmptyMessage(1);
    }

    private Handler processHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    // START
                    getProductProcess(mList.get(mProductCnt).getProductCode());
                    break;
                case 2:
                    // STOP
                    OrderProductAdapter2 orderProductAdapter = new OrderProductAdapter2(ProcessDetailActivity.this, mList);
                    orderProductAdapter.notifyDataSetChanged();
                    orderProductAdapter.setOnItemClickListener(pos -> {mProductIdx = pos;});
                    orderProductAdapter.setOnProcessItemClickListener(processClickListener);
                    orderProductAdapter.setOnProcessItemLongClickListener(processLongClickListener);

                    mBinding.productRecycler.setAdapter(orderProductAdapter);
                    mBinding.productRecycler.setLayoutManager(new LinearLayoutManager(ProcessDetailActivity.this));

                    mBinding.layoutRecycler.setVisibility(View.VISIBLE);
                    mBinding.layoutProgress.setVisibility(View.GONE);
                    break;
                default :
                    break;
            }
        }
    };

//    ActivityResultLauncher<Intent> startBarcodeActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//        @Override
//        public void onActivityResult(ActivityResult result) {
//            if (result.getResultCode() == Activity.RESULT_OK) {
//                Intent getData = result.getData();
//                if (getData == null)
//                    return;
//
//                mBoxNo = getData.getStringExtra("boxNo");
//                int artiBox = getData.getIntExtra("artiBox", -1);
//                int artiIdx = getData.getIntExtra("artiIdx", -1);
//
//                String artiBoxNo = "";
//                if (artiBox >= 0)
//                    artiBoxNo = mBoxNo;
//                // 공정 진행 함수
//                processStart(artiBoxNo, artiIdx);
//            }
//        }
//    });

//    ActivityResultLauncher<Intent> editBarcodeActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//        @Override
//        public void onActivityResult(ActivityResult result) {
//            if (result.getResultCode() == Activity.RESULT_OK) {
//                Intent getData = result.getData();
//                if (getData == null)
//                    return;
//
//                mBoxNo = getData.getStringExtra("boxNo");
//                int artiBox = getData.getIntExtra("artiBox", -1);
//                int artiIdx = getData.getIntExtra("artiIdx", -1);
//
//                String artiBoxNo = "";
//                if (artiBox >= 0)
//                    artiBoxNo = mBoxNo;
//                // 박스번호 update 함수
//                updateBoxNo(artiBoxNo, artiIdx);
//            }
//        }
//    });

    ActivityResultLauncher<Intent> startProcessActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                mOrderFinishTime = data.getStringExtra("orderFinishTime");
                getOrderList();
            }
        }
    });

    ActivityResultLauncher<Intent> startFaultyActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                onBackPressed();
            }
        }
    });

//    private void startSingleBarcodeActivity(boolean isNew) {
//        Intent intent = new Intent(ProcessDetailActivity.this, ProcessBarcodeActivity.class);
//        intent.putExtra("singleProcess", 1);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        if (isNew)
//            startBarcodeActivityResult.launch(intent);
//        else
//            editBarcodeActivityResult.launch(intent);
//    }

    private OrderCommitDialogClickListener orderCommitDialogClickListener = new OrderCommitDialogClickListener() {
        @Override
        public void processCommitClick(String productInfoCode, String remark, int processState, int processIdx, int artiCode) {
            if (mCode == CommonClass.YK) {
                OrderItem item = mList.get(mProductIdx);
                String startProcessName = item.getProductProcess().split("→")[processIdx];
                // Log.d("testttt", "processCommitClitk! " + item.getBoxNo() + ", " + item.getRequestCodeNum() + ", " + startProcessName + ", " + processIdx + ", " + mName);
                processStartApi(item.getRequestCodeNum(), item.getBoxNo(), item.getProductName(), startProcessName, artiCode, remark);
            } else {
                Log.d("testttt", "processCommitClick! " + productInfoCode + ", " + remark + ", " + processState + ", " + processIdx);
                if (processState >= processIdx) {
                    modifyProcess(productInfoCode, remark, processState, processIdx);
                } else {
                    for (int i = processState + 1; i <= processIdx; i++)
                        processCommit(productInfoCode, remark, i, i);
                }
            }

        }

        @Override
        public void processFaultyClick(String productInfoCode, String faultyType, String faultyHistory, int processIdx) {
            Log.d("testttt", "processFaultyClick! " + productInfoCode + ", " + faultyType + ", " + faultyHistory + ", " + processIdx);
//            faultyCommit(productInfoCode, faultyType, faultyHistory, processIdx);
        }
    };

    private OrderProductAdapter2.OnProcessItemClickListener processClickListener = pos -> {
//        if (mBoxNo.equals("")) {
//            startSingleBarcodeActivity(true);
//        } else {
//            Intent intent = new Intent(ProcessDetailActivity.this, OrderCommitActivity.class);
//            intent.putExtra("commit", "process");
//            intent.putExtra("ordNum", mOrderBarcode);
//            intent.putExtra("reqNum", mRequestCode);
//            intent.putExtra("prdNum", mList.get(mProductIdx).getProductCode());
//            intent.putExtra("prdName", mList.get(mProductIdx).getProductName());
//            intent.putExtra("prdNames", mList.get(mProductIdx).getProductProcess());
//            intent.putExtra("prdInfoNum", mList.get(mProductIdx).getProductInfoCode());
//            int processState = Integer.parseInt(mList.get(mProductIdx).getProcessState());
//            intent.putExtra("prcState", processState);
//            intent.putExtra("prcTag", pos);
//            intent.putExtra("boxNo", mBoxNo);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            if (processState > pos) {
////                CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.order_edit), getString(R.string.order_edit_ask), () -> {
////                    startProcessActivityResult.launch(intent);
////                }, true);
//                CheckBottomDialog bottomDialog = new CheckBottomDialog(ProcessDetailActivity.this, () -> {
//                    startProcessActivityResult.launch(intent);
//                }, getString(R.string.order_edit), getString(R.string.order_edit_ask), true);
//                bottomDialog.show(getSupportFragmentManager(), bottomDialog.getTag());
//            } else {
//                startProcessActivityResult.launch(intent);
//            }
//        }
        OrderItem item = new OrderItem();
        item.setOrderBarcode(mOrderBarcode);
        item.setRequestCode(mRequestCode);
        item.setProductCode(mList.get(mProductIdx).getProductCode());
        item.setProductName(mList.get(mProductIdx).getProductName());
        item.setProductProcess(mList.get(mProductIdx).getProductProcess());
        item.setProductInfoCode(mList.get(mProductIdx).getProductInfoCode());
        item.setProcessState(mList.get(mProductIdx).getProcessState());
        item.setProcessIdx(pos);
        item.setBoxNum(mBoxNo);
        OrderCommitDialog orderCommitDialog = new OrderCommitDialog(ProcessDetailActivity.this, item, orderCommitDialogClickListener, 0);

        if (Integer.parseInt(mList.get(mProductIdx).getProcessState()) > pos) {
            CheckBottomDialog bottomDialog = new CheckBottomDialog(ProcessDetailActivity.this, () -> {
                orderCommitDialog.show(getSupportFragmentManager(), orderCommitDialog.getTag());
            }, getString(R.string.order_edit), getString(R.string.order_edit_ask), true);
            bottomDialog.show(getSupportFragmentManager(), bottomDialog.getTag());
        } else {
            orderCommitDialog.show(getSupportFragmentManager(), orderCommitDialog.getTag());
        }
    };

    private OrderProductAdapter2.OnProcessItemClickListener processClickListener2 = pos -> {
        String[] userProcessName = mUserProcessName.split(",");
        boolean isDeny = true;
        for (String s : userProcessName) {
            if (s.equals(mList.get(mProductIdx).getProductProcess().split("→")[pos]))
                isDeny = false;
        }

        if (isDeny) {
            CheckBottomDialog dialog = new CheckBottomDialog(ProcessDetailActivity.this, () -> {}, getString(R.string.error_title), getString(R.string.process_auth_deny), false);
            dialog.show(getSupportFragmentManager(), dialog.getTag());
        } else {
            if (!mList.get(mProductIdx).getManagers().split(";", -1)[pos].equals("")) {
                CheckBottomDialog bottomDialog = new CheckBottomDialog(ProcessDetailActivity.this, () -> {
                    getArticulatorList(pos);
                }, getString(R.string.dialog_process_start), getString(R.string.process_restart), true);
                bottomDialog.show(getSupportFragmentManager(), bottomDialog.getTag());
            } else {
                getArticulatorList(pos);
//            if (Integer.parseInt(mList.get(mProductIdx).getProcessState()) > pos) {
//                CheckBottomDialog bottomDialog = new CheckBottomDialog(ProcessDetailActivity.this, () -> {
//                    orderCommitDialog.show(getSupportFragmentManager(), orderCommitDialog.getTag());
//                }, getString(R.string.order_edit), getString(R.string.order_edit_ask), true);
//                bottomDialog.show(getSupportFragmentManager(), bottomDialog.getTag());
//            } else {
//
//            }
            }
        }
    };

    private void showOrderCommitDialog(int pos) {
        OrderItem item = new OrderItem();
        item.setOrderBarcode(mOrderBarcode);
        item.setRequestCode(mRequestCode);
        item.setProductCode(mList.get(mProductIdx).getProductCode());
        item.setProductName(mList.get(mProductIdx).getProductName());
        item.setProductProcess(mList.get(mProductIdx).getProductProcess());
        item.setProductInfoCode(mList.get(mProductIdx).getProductInfoCode());
        item.setProcessState(mList.get(mProductIdx).getProcessState());
        item.setProcessIdx(pos);
        item.setBoxNum(mBoxNo);
        item.setMemo(mList.get(mProductIdx).getMemo());
        OrderCommitDialog orderCommitDialog = new OrderCommitDialog(ProcessDetailActivity.this, item, orderCommitDialogClickListener, 0);
        orderCommitDialog.show(getSupportFragmentManager(), orderCommitDialog.getTag());
    }

    private OrderProductAdapter2.OnProcessItemLongClickListener processLongClickListener = pos -> {
//        Intent intent = new Intent(ProcessDetailActivity.this, OrderCommitActivity.class);
//        intent.putExtra("commit", "faulty");
//        intent.putExtra("ordNum", mOrderBarcode);
//        intent.putExtra("reqNum", mRequestCode);
//        intent.putExtra("prdNum", mList.get(mProductIdx).getProductCode());
//        intent.putExtra("prdName", mList.get(mProductIdx).getProductName());
//        intent.putExtra("prdNames", mList.get(mProductIdx).getProductProcess());
//        intent.putExtra("prdInfoNum", mList.get(mProductIdx).getProductInfoCode());
//        intent.putExtra("prcState", mList.get(mProductIdx).getProcessState());
//        intent.putExtra("prcTag", pos);
//        intent.putExtra("boxNo", mBoxNo);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startFaultyActivityResult.launch(intent);
        OrderItem item = new OrderItem();
        item.setOrderBarcode(mOrderBarcode);
        item.setRequestCode(mRequestCode);
        item.setProductCode(mList.get(mProductIdx).getProductCode());
        item.setProductName(mList.get(mProductIdx).getProductName());
        item.setProductProcess(mList.get(mProductIdx).getProductProcess());
        item.setProductInfoCode(mList.get(mProductIdx).getProductInfoCode());
        item.setProcessState(mList.get(mProductIdx).getProcessState());
        item.setProcessIdx(pos);
        item.setBoxNum(mBoxNo);

        getFaultyType(item);
    };

    private OrderProductAdapter2.OnProcessItemLongClickListener processLongClickListener2 = pos -> {

    };

    public void getProductProcess(String productCode) {
        Call<ResponseBody> call = CommonClass.sApiService.getProductProcess(productCode, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();

                        boolean isPartners = mCode == CommonClass.PDL;
                        JSONArray jsonArray = new JSONArray(result);

                        int jsonArr_len = jsonArray.length();
                        String[] processNames = new String[jsonArr_len];
                        for (int i = 0; i < jsonArr_len; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            processNames[i] = jsonObject.getString("prcName");
                        }

                        String pName = "";
                        if (isPartners) {
                            pName = TextUtils.join("→", processNames);
                        } else {
                            pName = processNames[0];
                        }

                        mList.get(mProductCnt).setProductProcess(pName);
                        mProductCnt++;
                        if (mProductCnt < mList.size())
                            processHandler.sendEmptyMessage(1);
                        else
                            processHandler.sendEmptyMessage(2);
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    public void setFinishTime() {
        Call<ResponseBody> call = CommonClass.sApiService.setFinishTime(mOrderBarcode, mOrderFinishTime, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (result.equals("OK")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.complete_order), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }


    public void getFaultyType(OrderItem item) {
        Call<ResponseBody> call = CommonClass.sApiService.getFaultyType(mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        CommonClass.sFaultyType.clear();
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();

                        String faulty = "FaultyType";
                        if (mCode == CommonClass.PDL)   faulty = "faulty";
                        for (int i = 0; i < jsonArray_len; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CommonClass.sFaultyType.add(jsonObject.getString(faulty));
                        }

                        OrderCommitDialog orderCommitDialog = new OrderCommitDialog(ProcessDetailActivity.this, item, orderCommitDialogClickListener, 1);
                        orderCommitDialog.show(getSupportFragmentManager(), orderCommitDialog.getTag());

                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error getFaultyType");
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }


    private void processStart(String artiBoxNo, int artiIdx) {
        String artiToast = artiBoxNo.equals("") ? "" : getString(R.string.parenthesis, getString(R.string.arti_use)) ;
        CommonClass.ShowToast(ProcessDetailActivity.this, getString(R.string.single_barcode_scan, artiToast));
    }

//    private void updateBoxNo(String artiBoxNo, int artiIdx) {
//        mBinding.txtBoxNo.setText(getString(R.string.box_no, Integer.parseInt(mBoxNo.substring(CommonClass.getDigitIndex(mBoxNo)))));
//        ProcessActivity.boxNoUpdate(mPos, mBoxNo);
//        String artiToast = artiBoxNo.equals("") ? "" : getString(R.string.parenthesis, getString(R.string.arti_use)) ;
//        CommonClass.ShowToast(ProcessDetailActivity.this, getString(R.string.single_barcode_scan, artiToast));
//    }

    public void modifyProcess(String productInfoCode, String remark, int processIdx, int processState) {
        Call<ResponseBody> call = CommonClass.sApiService.deleteProcess(mOrderBarcode, processIdx, processState, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (result.equals("OK")) {
                            processCommit(productInfoCode, remark, processIdx, processIdx);
                        }
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    public void processCommit(String productInfoCode, String remark, int processIdx, int processState) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String processTime = format.format(date);

        Call<ResponseBody> call = CommonClass.sApiService.processCommit(mOrderBarcode, productInfoCode, "" + processState, mId, processTime, remark, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (processIdx == processState) {
                        Toast.makeText(getApplicationContext(), getString(R.string.order_progress), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("orderFinishTime", processTime);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    public void faultyCommit(String productInfoCode, String faultyType, String remark, int processIdx) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String faultyDateTime = format.format(date);

        Call<ResponseBody> call = CommonClass.sApiService.faultyCommit(mOrderBarcode, productInfoCode, mId, processIdx, faultyType, faultyDateTime, remark, mId, mIp);
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
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }


    private void processStartApi(int requestCode, String boxNo, String productName, String processName, int artiCode, String remark) {
        Call<ResponseBody> call = CommonClass.sApiService.processStartRequest(requestCode, boxNo, artiCode, processName,
                CommonClass.getCurrentTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())), mName, remark);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    getProductPattern();
                    Toast.makeText(ProcessDetailActivity.this, getString(R.string.process_start, productName, productName), Toast.LENGTH_SHORT).show();
                } else {
                    if (!ProcessDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!ProcessDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    private void getArticulatorList(int pos) {
        Call<ResponseBody> call = CommonClass.sApiService.getArticulator();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        CommonClass.sArticulatorDTO.clear();
                        CommonClass.sArticulatorSpinner.clear();
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int arrayLen = jsonArray.length();
                        if (arrayLen != 0) {
                            for (int i = 0; i < arrayLen; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                CommonClass.sArticulatorDTO.add(new ArticulatorDTO(
                                        jsonObject.getInt("articulatorCode"),
                                        jsonObject.getString("articulatorName"),
                                        jsonObject.getString("user").equals("null") ? "" : jsonObject.getString("user"),
                                        jsonObject.getString("useDateTime").equals("null") ? "" : jsonObject.getString("useDateTime")
                                ));
                                CommonClass.sArticulatorSpinner.put(i, jsonObject.getString("articulatorName"));
                            }
                        }
                        showOrderCommitDialog(pos);
                    } catch (IOException | JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!ProcessDetailActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessDetailActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessDetailActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}