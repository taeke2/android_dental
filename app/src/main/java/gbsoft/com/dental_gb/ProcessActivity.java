
package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ActivityProcessBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcessActivity extends AppCompatActivity {
    private ActivityProcessBinding mBinding;

    private ArrayList<ProcessRequestReleaseListItem> mProcessRequestReleaseListItems;
    private ProcessAdapter mProcessAdapter;
    private int mPage = 0;
    private final int mLimit = 30;
    private int mLastRowNum;

    private String mClientName = "";
    private String mPatientName = "";
    private String mOrderDate = "";
    private String mDeadlineDate = "";
    private String mManager = "";
    private String mDentalFormula = "";

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        mBinding = ActivityProcessBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(ProcessActivity.this.getApplicationContext())) {
            if (!ProcessActivity.this.isFinishing())
                CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
        } else {
            initialSet();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProcessAdapter.notifyDataSetChanged();
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

        mServerPath = sharedPreferences.getString("address", "");
        if (CommonClass.sApiService == null)
            CommonClass.getApiService(mServerPath);

        mProcessRequestReleaseListItems = new ArrayList<>();
        mProcessAdapter = new ProcessAdapter(ProcessActivity.this, mProcessRequestReleaseListItems);
        mProcessAdapter.setOnItemClickListener(processItemClick);
        mBinding.lvProcess.setAdapter(mProcessAdapter);
        mBinding.lvProcess.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if (mCode == CommonClass.YK) {
            mBinding.iconTxtBracode.setVisibility(View.VISIBLE);
            mBinding.iconTxtBracode.setOnClickListener(barcodeButtonClick);
        } else {
            mBinding.iconTxtBracode.setVisibility(View.GONE);
            mBinding.lvProcess.addOnScrollListener(processListScroll);
        }
        mPage = 0;

        mBinding.progressbar.setVisibility(View.GONE);
        mBinding.noListTxt.setVisibility(View.GONE);

        mBinding.iconTxtFilter.setOnClickListener(filterButtonClick);
        mBinding.btnBack.setOnClickListener(v -> finish());

        mDentalFormula = "///";
        if (mCode == CommonClass.PDL) {
            getShutdownCheck();
        } else if (mCode == CommonClass.YK) {
            getRequestProcessList();
        } else {
            mDentalFormula = "&&&";
            getProcessListCount();
        }
    }

    private void loadingProgressOpen(){
        mBinding.progressbarLoading.setVisibility(View.VISIBLE);
        mBinding.layoutRecycler.setVisibility(View.GONE);
    }

    public void loadingProgressClose(){
        mBinding.progressbarLoading.setVisibility(View.GONE);
        mBinding.layoutRecycler.setVisibility(View.VISIBLE);
    }

    RecyclerView.OnScrollListener processListScroll = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
            if (lastVisibleItemPosition == itemTotalCount) {
                mBinding.progressbar.setVisibility(View.VISIBLE);
                getProcessList();
            }
        }
    };

    ProcessAdapter.OnItemClickListener processItemClick = new ProcessAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int pos) {

//            String patientName = mProcessRequestReleaseListItems.get(pos).getPatientName();
//            String orderDate = mProcessRequestReleaseListItems.get(pos).getOrderDate();
//            String deadlineDate = mProcessRequestReleaseListItems.get(pos).getDeadlineDate();
//            String deadlineTime = mProcessRequestReleaseListItems.get(pos).getDeadlineTime();
//            String boxNo = mProcessRequestReleaseListItems.get(pos).getBoxNo();

            Intent intent;
            if (mCode == CommonClass.YK) {
                intent = new Intent(ProcessActivity.this, ProcessDetailActivity.class);
                intent.putExtra("pos", pos);
//                intent.putExtra("patient", patientName);
//                intent.putExtra("orderDate", orderDate);
//                intent.putExtra("deadlineDate", deadlineDate);
//                intent.putExtra("deadlineTime", deadlineTime);
//                intent.putExtra("boxNo", boxNo);
            } else {
                intent = new Intent(ProcessActivity.this, OrderActivity.class);
                String orderBarcode = mProcessRequestReleaseListItems.get(pos).getOrderBarcode();
                String requestCode = mProcessRequestReleaseListItems.get(pos).getRequestCode();
                String clientName = mProcessRequestReleaseListItems.get(pos).getClientName();

                intent.putExtra("client", clientName);
                intent.putExtra("ordNum", orderBarcode);
                intent.putExtra("reqNum", requestCode);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

    View.OnClickListener barcodeButtonClick = v -> {
        Intent intent = new Intent(ProcessActivity.this, ProcessBarcodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    };

    FilterBottomDialogClickListener filterBottomDialogClickListener = new FilterBottomDialogClickListener() {
        @Override
        public void onSearchClick(String ordDate, String deadDate, String outDate, String manager, String searchClient, String searchPatient, String dent, String release) {
            mPage = 0;
            mProcessRequestReleaseListItems.clear();
            mManager = manager;
            mOrderDate = ordDate;
            mDeadlineDate = deadDate;
            mClientName = searchClient;
            mPatientName = searchPatient;
            mDentalFormula = dent;
            mDentalFormula = mDentalFormula.replace("상", "上");
            mDentalFormula = mDentalFormula.replace("하", "下");

            if (mCode == CommonClass.YK) {
                mDentalFormula = stringReplace(mDentalFormula);
                getRequestProcessList();
            } else if (mCode == CommonClass.PDL)
                getShutdownCheck();
            else
                getProcessListCount();
        }
    };

    private String stringReplace(String str) {
        if (str.equals("///"))
            return str;

        String match = "[^\uAC00-\uD7A30-9a-zA-Z]";

        String[] splitDental = str.split("/", -1);
        for (int i = 0; i < splitDental.length; i++) {
            String dentalF = splitDental[i].replaceAll(match, "");
            String[] splitF = dentalF.split("", -1);
            String joinF = TextUtils.join(",", splitF);
            if (joinF.length() > 0) {
                splitDental[i] = joinF.substring(1, joinF.length() - 1);
            } else {
                splitDental[i] = joinF;
            }
        }
        str = TextUtils.join("/", splitDental);
        return str;
    }

    View.OnClickListener filterButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FilterBottomDialog filterBottomDialog = new FilterBottomDialog(
                    ProcessActivity.this, filterBottomDialogClickListener,
                    mOrderDate, mDeadlineDate, mManager, mClientName, mPatientName,
                    "ord", mDentalFormula, mId, mCode
            );

            filterBottomDialog.show(getSupportFragmentManager(), "process");

//            Intent intent = new Intent(ProcessActivity.this, FilterActivity.class);
//            intent.putExtra("ordDate", mOrderDate);
//            intent.putExtra("deadDate", mDeadlineDate);
//            intent.putExtra("manager", mManager);
//            intent.putExtra("searchClient", mClientName);
//            intent.putExtra("searchPatient", mPatientName);
//            intent.putExtra("type", "ord");
//            intent.putExtra("dent", mDentalFormula);
//            //startActivityForResult(intent, FILTER);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivityResult.launch(intent);
        }
    };

//    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//        @Override
//        public void onActivityResult(ActivityResult result) {
//            if (result.getResultCode() == Activity.RESULT_OK) {
//                Intent data = result.getData();
//                if (data == null)
//                    return;
//
//                mPage = 0;
//                mProcessRequestReleaseListItems.clear();
//                mManager = data.getStringExtra("manager");
//                mOrderDate = data.getStringExtra("ordDate");
//                mDeadlineDate = data.getStringExtra("deadDate");
//                mClientName = data.getStringExtra("searchClient");
//                mPatientName = data.getStringExtra("searchPatient");
//                mDentalFormula = data.getStringExtra("dent");
//                mDentalFormula = mDentalFormula.replace("상", "上");
//                mDentalFormula = mDentalFormula.replace("하", "下");
//
//                getProcessListCount();
//            }
//        }
//    });


    public void getProcessListCount() {
        int offset = (mLimit * (mPage));
        Call<ResponseBody> call = CommonClass.sApiService.getProcessCount(mManager, mClientName, mPatientName, mOrderDate, mDeadlineDate, mLimit, offset, mId, mIp);
        loadingProgressOpen();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        mLastRowNum = Integer.parseInt(jsonObject.getString("rowCount"));
                        getProcessList();
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!ProcessActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!ProcessActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
            }
        });
    }

    public void getProcessList() {
//        mLockListView = true;
        int offset = (mLimit * (mPage));
        Call<ResponseBody> call = CommonClass.sApiService.getProcessList(mManager, mClientName, mPatientName, mOrderDate, mDeadlineDate, mLimit, offset, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    boolean flag = true;
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        if (offset >= mLimit && mLastRowNum < offset) {
                            mBinding.progressbar.setVisibility(View.GONE);
                        } else {
                            if (jsonArray_len == 0) {
                                mBinding.noListTxt.setVisibility(View.VISIBLE);
                                mBinding.lvProcess.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < jsonArray_len; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String jsonDentalFormula = jsonObject.getString("dent");

                                    if (mCode != CommonClass.UI) {
                                        String[] arrayDental = jsonDentalFormula.split(",");
                                        // int arrayDental_len = arrayDental.length;
                                        ArrayList<String[]> arrayResultDental = new ArrayList<>();
                                        String[] arraySearchDental;
                                        if (mCode == CommonClass.PDL) {
                                            for (String s : arrayDental)
                                                arrayResultDental.add(s.split("/", 4));
                                            arraySearchDental = mDentalFormula.split("/", 4);
                                        } else {
                                            for (String s : arrayDental)
                                                arrayResultDental.add(s.split("&", 4));
                                            arraySearchDental = mDentalFormula.split("&", 4);
                                        }

                                        if (arraySearchDental[0].equals("上")) {
                                            arraySearchDental[1] = "上";
                                            arraySearchDental[0] = "";
                                        }

                                        if (arraySearchDental[2].equals("下")) {
                                            arraySearchDental[3] = "下";
                                            arraySearchDental[2] = "";
                                        }

                                        int result_len = arrayResultDental.size();
                                        for (int j = 0; j < 4; j++) {
                                            if (!arraySearchDental[j].equals("")) {
                                                flag = false;
                                                for (int k = 0; k < result_len; k++) {
                                                    if (arraySearchDental[j].equals(arrayResultDental.get(k)[j])) {
                                                        flag = true;
                                                        break;
                                                    }
                                                }
                                                if (!flag)  break;
                                            }
                                        }
                                    }

                                    if (flag) {
                                        String jsonOrderBarcode = jsonObject.getString("ordNum");
                                        String jsonRequestCode = jsonObject.getString("reqNum");
                                        String jsonClientName = jsonObject.getString("client");
                                        String jsonPatientName = jsonObject.getString("patient");
                                        String jsonProductName = jsonObject.getString("prdName");
                                        String jsonOrderDate = jsonObject.getString("reqDate");
                                        String jsonDeadlineDate = jsonObject.getString("deadDate");
                                        String jsonDeadlineTime = jsonObject.getString("deadTime");
                                        String jsonClientTel = jsonObject.getString("clientTel");
                                        if (mCode == CommonClass.YK) {
                                            // String jsonBoxNo = jsonObject.getString("boxNo").equls("null") ? "" : jsonObject.getString("boxNo");
                                            String jsonBoxNo = "";
                                            mProcessRequestReleaseListItems.add(new ProcessRequestReleaseListItem(jsonOrderBarcode,
                                                    jsonRequestCode, jsonClientName, jsonPatientName, jsonProductName, jsonOrderDate,
                                                    jsonDeadlineDate, jsonDeadlineTime, jsonClientTel, jsonDentalFormula, jsonBoxNo));
                                        } else {
                                            mProcessRequestReleaseListItems.add(new ProcessRequestReleaseListItem(jsonOrderBarcode,
                                                    jsonRequestCode, jsonClientName, jsonPatientName, jsonProductName, jsonOrderDate,
                                                    jsonDeadlineDate, jsonDeadlineTime, jsonClientTel, jsonDentalFormula));
                                        }
                                    }
                                }
                                mPage++;
                                mProcessAdapter.notifyDataSetChanged();
                                mBinding.progressbar.setVisibility(View.GONE);
//                                        mLockListView = false;
                            }
                        }
                        loadingProgressClose();
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!ProcessActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!ProcessActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
            }
        });
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
                            if (!ProcessActivity.this.isFinishing()) {
                                CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                            }
                        } else {
                            getProcessListCount();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!ProcessActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!ProcessActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    private void getRequestProcessList() {
        loadingProgressOpen();

        Call<ResponseBody> call = CommonClass.sApiService.getRequestProcess(mClientName, mOrderDate, mDeadlineDate, mDentalFormula);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        RequestAdapter2 requestAdapter2 = new RequestAdapter2();
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        if (jsonArray_len == 0) {
                            mBinding.layoutRecycler.setVisibility(View.VISIBLE);
                            mBinding.noListTxt.setVisibility(View.VISIBLE);
                            mBinding.lvProcess.setVisibility(View.GONE);
                            mBinding.progressbarLoading.setVisibility(View.GONE);
                        } else {
                            CommonClass.sRequestDTO.clear();
                            StringBuilder builder = new StringBuilder();
                            for(int i = 0; i < jsonArray_len; i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                RequestDTO requestDTO = new RequestDTO();
                                requestDTO.setId(jsonObject.getInt("requestCode"));
                                requestDTO.setBoxNo(jsonObject.getString("boxNo"));
                                requestDTO.setBoxName(jsonObject.getString("boxName"));
                                requestDTO.setClientName(jsonObject.getString("clientName"));
                                requestDTO.setProductName(jsonObject.getString("productName"));
                                requestDTO.setOrderDate(jsonObject.getString("requestDate"));
                                requestDTO.setDeadlineDate(jsonObject.getString("dueDate"));
                                requestDTO.setDeadlineTime(jsonObject.getString("dueTime"));
                                String[] mDentArr = jsonObject.getString("dentalFormula").split("/");

                                for(int j = 0; j < mDentArr.length; j++){
                                    String[] tempArr = mDentArr[j].split(",");
                                    for (String name : tempArr) {
                                        builder.append(name).append("");
                                    }

                                    switch (j){
                                        case 0:
                                            requestDTO.setDentalFormula10(builder.toString());
                                            break;
                                        case 1:
                                            requestDTO.setDentalFormula20(builder.toString());
                                            break;
                                        case 2:
                                            requestDTO.setDentalFormula30(builder.toString());
                                            break;
                                        case 3:
                                            requestDTO.setDentalFormula40(builder.toString());
                                            break;
                                    }
                                    builder.delete(0, builder.toString().length());
                                }
                                CommonClass.sRequestDTO.add(requestDTO);
                                // mAdapter.notifyItemChanged(i);
                            }
                            mBinding.lvProcess.setAdapter(requestAdapter2);
                            mBinding.lvProcess.setLayoutManager(new LinearLayoutManager(ProcessActivity.this));
                            requestAdapter2.notifyDataSetChanged();

                            requestAdapter2.setOnItemClickListener(requestAdapterOnItemClickListener);
                            requestAdapter2.setBtnEtcClickListener(requestAdapterBtnEtcClickListener);

                            mBinding.noListTxt.setVisibility(View.GONE);
                            mBinding.lvProcess.setVisibility(View.VISIBLE);
                            loadingProgressClose();
                        }
                    } catch (JSONException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: JSON Parsing Error - findId");
                        Log.e("RequestActivity2", "ERROR: JSON Parsing Error - processActivity requestList");
                        if (!ProcessActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
//                        Log.e(Common.TAG_ERR, "ERROR: IOException - findId");
                        Log.e("RequestActivity2", "ERROR: IOException - requestList");
                        if (!ProcessActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }

                } else {
                    if (!ProcessActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!ProcessActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    RequestAdapter2.OnItemClickListener requestAdapterOnItemClickListener = pos -> {
        Intent intent = new Intent(ProcessActivity.this, ProcessDetailActivity.class);
        intent.putExtra("pos", pos);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    };

    RequestAdapter2.btnEtcClickListener requestAdapterBtnEtcClickListener = pos -> {
        SubMenuBottomDialog activity = new SubMenuBottomDialog();
        Bundle bundle = new Bundle();
        // TODO: 추후 수정
        bundle.putString("reqNum", CommonClass.sRequestDTO.get(pos).getDentalFormula30());
        bundle.putString("clientTel", CommonClass.sRequestDTO.get(pos).getDentalFormula20());
        activity.setArguments(bundle);
        activity.show(getSupportFragmentManager(), "");
    };
}
