package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ActivityRequestBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestActivity extends AppCompatActivity {
//    private static final int FILTER = 100;

    private ActivityRequestBinding mBinding;

    private ArrayList<ProcessRequestReleaseListItem> mProcessRequestReleaseListItems;
    private RequestAdapter mRequestAdapter;

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

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        mBinding = ActivityRequestBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(RequestActivity.this.getApplicationContext())) {
            if (!RequestActivity.this.isFinishing())
                CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
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
        SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);
        mServerPath = sharedPreferences.getString("address", "");

        if (CommonClass.sApiService == null)
            CommonClass.getApiService(mServerPath);

        mProcessRequestReleaseListItems = new ArrayList<>();
        mProcessRequestReleaseListItems.clear();
        mRequestAdapter = new RequestAdapter(RequestActivity.this, mProcessRequestReleaseListItems);
        mRequestAdapter.setOnItemClickListener(requestItemClick);
        mBinding.listRequest.setAdapter(mRequestAdapter);
        mBinding.listRequest.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mBinding.listRequest.addOnScrollListener(requestListScroll);
        mPage = 0;

        mBinding.progressbar.setVisibility(View.GONE);
        mBinding.noListTxt.setVisibility(View.GONE);

        mBinding.iconTxtFilter.setOnClickListener(filterButtonClick);
        mBinding.iconTxtFilter.setOnTouchListener(filterButtonTouch);

        if (mCode == CommonClass.PDL) {
            getShutdownCheck();
            mDentalFormula = "///";
        } else {
            mDentalFormula = "&&&";
            getRequestListCount();
        }
        loadingProgressOpen();
    }

//    AbsListView.OnScrollListener requestListScroll = new AbsListView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(AbsListView view, int scrollState) {
//            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLastItemVisibleFlag && !mLockListView) {
//                mBinding.progressbar.setVisibility(View.VISIBLE);
//                getRequestList();
//
//            }
//        }
//
//        @Override
//        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            mLastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
//        }
//    };
//
//    AdapterView.OnItemClickListener requestItemClick = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            String requestCode = mProcessRequestReleaseListItems.get(position).getRequestCode();
//            Intent intent = new Intent(RequestActivity.this, RequestDetailActivity.class);
//            intent.putExtra("reqNum", requestCode);
//            startActivity(intent);
//        }
//    };

    RecyclerView.OnScrollListener requestListScroll = new RecyclerView.OnScrollListener() {
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
                getRequestList();
            }
        }
    };

    RequestAdapter.OnItemClickListener requestItemClick = new RequestAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int pos) {
            String requestCode = mProcessRequestReleaseListItems.get(pos).getRequestCode();
            Intent intent = new Intent(RequestActivity.this, RequestDetailActivity.class);
            intent.putExtra("reqNum", requestCode);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

    View.OnClickListener filterButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RequestActivity.this, FilterActivity.class);
            intent.putExtra("ordDate", mOrderDate);
            intent.putExtra("deadDate", mDeadlineDate);
            intent.putExtra("manager", mManager);
            intent.putExtra("searchClient", mClientName);
            intent.putExtra("searchPatient", mPatientName);
            intent.putExtra("type", "req");
            intent.putExtra("dent", mDentalFormula);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            // startActivityForResult(intent, FILTER);
            startActivityResult.launch(intent);
        }
    };

    View.OnTouchListener filterButtonTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mBinding.iconTxtFilter.setBackground(ContextCompat.getDrawable(RequestActivity.this, R.drawable.border2));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mBinding.iconTxtFilter.setBackground(ContextCompat.getDrawable(RequestActivity.this, R.drawable.border));
            }
            return false;
        }
    };

    /**
     * 로딩창 열기
     * */
    private void loadingProgressOpen(){
        mProgressDialog = new ProgressDialog(RequestActivity.this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.setCanceledOnTouchOutside(false); // 다이얼로그 밖에 터치 시 종료
        mProgressDialog.setCancelable(false); // 다이얼로그 취소 가능 (back key)
        mProgressDialog.show();
    }

    /**
     * 로딩창 닫기
     * */
    public void loadingProgressClose(){
        if(mProgressDialog == null) return;

        if(mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                loadingProgressOpen();
                Intent data = result.getData();
                mPage = 0;
                mProcessRequestReleaseListItems.clear();
                mManager = data.getStringExtra("manager");
                mOrderDate = data.getStringExtra("ordDate");
                mDeadlineDate = data.getStringExtra("deadDate");
                mClientName = data.getStringExtra("searchClient");
                mPatientName = data.getStringExtra("searchPatient");
                mDentalFormula = data.getStringExtra("dent");
                mDentalFormula = mDentalFormula.replace("상", "上");
                mDentalFormula = mDentalFormula.replace("하", "下");

                getRequestListCount();
            }
        }
    });

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        loadingProgressOpen();
//        if (requestCode == FILTER) {
//            if (resultCode == RESULT_OK) {
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
//                getRequestListCount();
//            } else if(resultCode == RESULT_CANCELED) {
//                loadingProgressClose();
//            }
//        }
//    }

    private void getRequestListCount() {
        int offset = (mLimit * (mPage));
        Call<ResponseBody> call = CommonClass.sApiService.getRequestCount(mClientName, mPatientName, mOrderDate, mDeadlineDate, mLimit, offset, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        mLastRowNum = Integer.parseInt(jsonObject.getString("rowCount"));
                        getRequestList();
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!RequestActivity.this.isFinishing())
                            CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!RequestActivity.this.isFinishing())
                            CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!RequestActivity.this.isFinishing())
                        CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!RequestActivity.this.isFinishing())
                    CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    private void getRequestList() {
//        mLockListView = true;
        int offset = (mLimit * (mPage));
        Call<ResponseBody> call = CommonClass.sApiService.getRequestList(mClientName, mPatientName, mOrderDate, mDeadlineDate, mLimit, offset, mId, mIp);
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
                                mBinding.listRequest.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < jsonArray_len; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String jsonDentalFormula = jsonObject.getString("dent");
                                    if (mCode != CommonClass.UI) {
                                        String[] arrayDental = jsonDentalFormula.split("and");
                                        int arrayDental_len = arrayDental.length;

                                        ArrayList<String[]> arrayResultDental = new ArrayList<>();
                                        String[] arraySearchDental;
                                        if (mCode == CommonClass.PDL) {
                                            for (int j = 0; j < arrayDental_len; j++) {
                                                arrayResultDental.add(arrayDental[j].split("/", 4));
                                            }
                                            arraySearchDental = mDentalFormula.split("/", 4);
                                        } else {
                                            for (int j = 0; j < arrayDental_len; j++) {
                                                arrayResultDental.add(arrayDental[j].split("&", 4));
                                            }
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
                                                if (!flag) {
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    if (flag) {
                                        String jsonRequestCode = jsonObject.getString("reqNum");
                                        String jsonClientName = jsonObject.getString("client");
                                        String jsonPatientName = jsonObject.getString("patient");
                                        String jsonOrderDate = jsonObject.getString("ordDate");
                                        String jsonDeadlineDate = jsonObject.getString("deadDate");
                                        String jsonClientTel = jsonObject.getString("clientTel");
                                        String jsonProductName = jsonObject.getString("prdName");
                                        String jsonType = jsonObject.getString("divs");
                                        mProcessRequestReleaseListItems.add(new ProcessRequestReleaseListItem(jsonRequestCode, jsonClientName, jsonPatientName, jsonOrderDate, jsonDeadlineDate, jsonClientTel, jsonDentalFormula, jsonType, jsonProductName));
                                    }
                                    flag = true;
                                }
                                mPage++;
                                mRequestAdapter.notifyDataSetChanged();
                                mBinding.progressbar.setVisibility(View.GONE);
//                                    mLockListView = false;
                            }
                            loadingProgressClose();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!RequestActivity.this.isFinishing())
                            CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!RequestActivity.this.isFinishing())
                            CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!RequestActivity.this.isFinishing())
                        CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!RequestActivity.this.isFinishing())
                    CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
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
                            if (!RequestActivity.this.isFinishing()) {
                                CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                            }
                        } else {
                            getRequestListCount();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!RequestActivity.this.isFinishing())
                            CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!RequestActivity.this.isFinishing())
                            CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!RequestActivity.this.isFinishing())
                        CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!RequestActivity.this.isFinishing())
                    CommonClass.showDialog(RequestActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

}
