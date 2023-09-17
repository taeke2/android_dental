package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ActivityOrderBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderActivity extends AppCompatActivity {
//    private static final int PROCESS = 100;
//    private static final int FAULTY = 200;

    private ActivityOrderBinding mBinding;

    public static Context sContext;

    private String mClientName = "";
    private String mOrderBarcode = "";
    private String mRequestCode = "";
    private String mOrderFinishTime = "";

    private int mProductSelected = 0;

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    private ArrayList<OrderItem> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        sContext = this;

        if (!CommonClass.ic.GetInternet(OrderActivity.this.getApplicationContext())) {
            if (!OrderActivity.this.isFinishing())
                CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
        } else {
            initialSet();

            Intent intent = new Intent(OrderActivity.this, RequestDetailActivity.class);
            intent.putExtra("reqNum", mRequestCode);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
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

        Intent getIntent = getIntent();
        mClientName = getIntent.getStringExtra("client");
        mOrderBarcode = getIntent.getStringExtra("ordNum");
        mRequestCode = getIntent.getStringExtra("reqNum");

        mBinding.txtClientName.setOnClickListener(clientClick);

        mList = new ArrayList<>();


        SpannableString content = new SpannableString(mClientName);
        content.setSpan(new UnderlineSpan(), 0, mClientName.length(), 0);
        mBinding.txtClientName.setText(content);

        if (mCode == CommonClass.PDL)
            getShutdownCheck();
        else
            getOrderList();
    }

    View.OnClickListener clientClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(OrderActivity.this, RequestDetailActivity.class);
            intent.putExtra("reqNum", mRequestCode);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PROCESS) {
//            if (resultCode == RESULT_OK) {
//                mOrderFinishTime = data.getStringExtra("orderFinishTime");
//                getOrderList();
//            }
//        } else if (requestCode == FAULTY) {
//            if (resultCode == RESULT_OK) {
//                onBackPressed();
//            }
//        }
//    }

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

    public void getOrderList() {
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
                            if (processLen != Integer.parseInt(processState)) {
                                isFinish = false;
                            }

                            orderItem.setProcessState(processState);

                            mList.add(orderItem);
                        }

                        if (isFinish) {
                            setFinishTime();
                        } else {
                            OrderProductAdapter orderProductAdapter = new OrderProductAdapter(mList, mProductSelected);
                            orderProductAdapter.notifyDataSetChanged();
                            mBinding.listProduct.setAdapter(orderProductAdapter);

                            OrderItem item = orderProductAdapter.mOrderItems.get(mProductSelected);
                            String productCode = item.getProductCode();
                            int processState = Integer.parseInt(item.getProcessState());
                            getProductProcess(productCode, processState);

                            mBinding.listProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    mProductSelected = position;

                                    OrderProductAdapter orderProductAdapter = new OrderProductAdapter(mList, mProductSelected);
                                    orderProductAdapter.notifyDataSetChanged();
                                    mBinding.listProduct.setAdapter(orderProductAdapter);

                                    OrderItem item = orderProductAdapter.mOrderItems.get(mProductSelected);
                                    getProductProcess(item.getProductCode(), Integer.parseInt(item.getProcessState()));

                                }
                            });
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!OrderActivity.this.isFinishing())
                            CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!OrderActivity.this.isFinishing())
                            CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!OrderActivity.this.isFinishing())
                        CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!OrderActivity.this.isFinishing())
                    CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    public void getProductProcess(String productCode, int processState) {
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

                        mList.get(mProductSelected).setProductProcess(pName);


                        OrderProcessAdapter orderProcessAdapter = new OrderProcessAdapter(pName.split("→", -1), processState);
                        orderProcessAdapter.notifyDataSetChanged();
                        mBinding.listProcess.setAdapter(orderProcessAdapter);

                        mBinding.listProcess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(OrderActivity.this, OrderCommitActivity.class);
                                intent.putExtra("commit", "process");
                                intent.putExtra("ordNum", mOrderBarcode);
                                intent.putExtra("reqNum", mRequestCode);
                                intent.putExtra("prdNum", mList.get(mProductSelected).getProductCode());
                                intent.putExtra("prdName", mList.get(mProductSelected).getProductName());
                                intent.putExtra("prdNames", mList.get(mProductSelected).getProductProcess());
                                intent.putExtra("prdInfoNum", mList.get(mProductSelected).getProductInfoCode());
                                int processState = Integer.parseInt(mList.get(mProductSelected).getProcessState());
                                intent.putExtra("prcState", processState);
                                intent.putExtra("prcTag", position);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                if (processState > position) {
//                                        alertShow(getString(R.string.order_edit_ask), getString(R.string.order_edit), intent);
                                    CommonClass.showDialog(OrderActivity.this, getString(R.string.order_edit), getString(R.string.order_edit_ask), () -> {
                                        startProcessActivityResult.launch(intent);
                                    }, true);
                                } else {
                                    // startActivityForResult(intent, PROCESS);
                                    startProcessActivityResult.launch(intent);
                                }
                            }
                        });

                        mBinding.listProcess.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(OrderActivity.this, OrderCommitActivity.class);
                                intent.putExtra("commit", "faulty");
                                intent.putExtra("ordNum", mOrderBarcode);
                                intent.putExtra("reqNum", mRequestCode);
                                intent.putExtra("prdNum", mList.get(mProductSelected).getProductCode());
                                intent.putExtra("prdName", mList.get(mProductSelected).getProductName());
                                intent.putExtra("prdNames", mList.get(mProductSelected).getProductProcess());
                                intent.putExtra("prdInfoNum", mList.get(mProductSelected).getProductInfoCode());
                                intent.putExtra("prcState", mList.get(mProductSelected).getProcessState());
                                intent.putExtra("prcTag", position);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                // startActivityForResult(intent, FAULTY);
                                startFaultyActivityResult.launch(intent);
                                return true;
                            }
                        });

                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!OrderActivity.this.isFinishing())
                            CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!OrderActivity.this.isFinishing())
                            CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!OrderActivity.this.isFinishing())
                        CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!OrderActivity.this.isFinishing())
                    CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

//    public void alertShow(String message, String title, Intent intent) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
//        builder.setMessage(message);
//        builder.setTitle(title).setCancelable(false)
//                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // startActivityForResult(intent, PROCESS);
//                        startProcessActivityResult.launch(intent);
//                    }
//                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        }).setCancelable(false);
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

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
                            if (!OrderActivity.this.isFinishing()) {
                                CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                            }
                        } else {
                            getOrderList();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!OrderActivity.this.isFinishing())
                            CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!OrderActivity.this.isFinishing())
                            CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!OrderActivity.this.isFinishing())
                        CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!OrderActivity.this.isFinishing())
                    CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
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
                        if (!OrderActivity.this.isFinishing())
                            CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!OrderActivity.this.isFinishing())
                        CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!OrderActivity.this.isFinishing())
                    CommonClass.showDialog(OrderActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}