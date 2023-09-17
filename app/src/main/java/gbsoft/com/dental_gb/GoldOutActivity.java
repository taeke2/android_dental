package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ActivityGoldOutBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoldOutActivity extends AppCompatActivity {
    private ActivityGoldOutBinding mBinding;

    private String mClientName = "";
    private String mClientCode = "";
    private String mRequestCode = "";
    private String mRequestInfo = "";
    private String mProductInfoCode = "";
    private String mGoldName = "";

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    List<String> mProductInfoCodes = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityGoldOutBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(GoldOutActivity.this.getApplicationContext())) {
            if (!GoldOutActivity.this.isFinishing())
                CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
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

        Intent getIntent = getIntent();
        mClientCode = getIntent.getStringExtra("cliNum");
        mClientName = getIntent.getStringExtra("client");

        mBinding.txtClientName.setText(mClientName);

        mBinding.txtRequestInfo.setOnClickListener(requestInfoClick);
        mBinding.spnRequestProduct.setOnTouchListener(requestProductTouch);
        mBinding.btnAdd.setOnClickListener(addButtonClick);
        mBinding.btnCancel.setOnClickListener(cancelButtonClick);

        if (mCode == CommonClass.PDL)
            getShutdownCheck();
        else
            getGoldName();
    }

    View.OnClickListener requestInfoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(GoldOutActivity.this, GoldOutRequestActivity.class);
            intent.putExtra("cliNum", mClientCode);
            intent.putExtra("client", mClientName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            // startActivityForResult(intent, 1);
            startActivityResult.launch(intent);
        }
    };

    View.OnTouchListener requestProductTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == event.ACTION_UP) {
                if (!mRequestCode.equals(""))
                    getRequestProductData();
//                else {
//                    Toast.makeText(GoldOutActivity.this, getString(R.string.select_request_first), Toast.LENGTH_SHORT).show();
//                }
            }
            return false;
        }
    };

    View.OnClickListener addButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBinding.spnGold.getSelectedItem().toString().equals("")) {
                Toast.makeText(GoldOutActivity.this, getString(R.string.select_used_gold), Toast.LENGTH_SHORT).show();
            } else if (mRequestCode == null || mBinding.spnRequestProduct.getSelectedItem() == null) {
                Toast.makeText(GoldOutActivity.this, getString(R.string.select_request), Toast.LENGTH_SHORT).show();
            } else if (mBinding.spnRequestProduct.getSelectedItem().toString().equals("")) {
                Toast.makeText(GoldOutActivity.this, getString(R.string.select_product), Toast.LENGTH_SHORT).show();
            } else if (mBinding.editOutQuantity.getText().toString().equals("")) {
                Toast.makeText(GoldOutActivity.this, getString(R.string.enter_gold_using), Toast.LENGTH_SHORT).show();
            } else {
                addUsedGold();
            }
        }
    };

    View.OnClickListener cancelButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };


    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                mRequestCode = data.getStringExtra("reqNum");
                mRequestInfo = data.getStringExtra("reqInfo");
                mBinding.txtRequestInfo.setText(mRequestInfo);
                mBinding.txtRequestInfo.setBackgroundColor(Color.rgb(239, 241, 243));
                mBinding.txtRequestInfo.setTextColor(Color.BLACK);
            }
        }
    });


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                mRequestCode = data.getStringExtra("reqNum");
//                mRequestInfo = data.getStringExtra("reqInfo");
//                txt_requestInfo.setText(mRequestInfo);
//                txt_requestInfo.setBackgroundColor(Color.rgb(239, 241, 243));
//                txt_requestInfo.setTextColor(Color.BLACK);
//            }
//        }
//    }

    public void addUsedGold() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String outDate = format.format(date);
        String outQuantity = mBinding.editOutQuantity.getText().toString();
        String useQuantity = mBinding.editUseQuantity.getText().toString();
        String remark = mBinding.editRemark.getText().toString();
        Call<ResponseBody> call = CommonClass.sApiService.setUsedGold(mGoldName, mClientCode, mProductInfoCode, outDate, mId, "사용", outQuantity, useQuantity, remark, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (result.equals("OK")) {
                            finish();
                        }
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!GoldOutActivity.this.isFinishing())
                            CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!GoldOutActivity.this.isFinishing())
                        CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!GoldOutActivity.this.isFinishing())
                    CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    public void getGoldName() {
        Call<ResponseBody> call = CommonClass.sApiService.getGoldName(mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        List<String> list_gold = new ArrayList<>();
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        for (int i = 0; i < jsonArray_len; i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            list_gold.add(c.getString("Gold"));
                        }
                        ArrayAdapter<String> arrayAdapter_gold = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_gold);
                        mBinding.spnGold.setAdapter(arrayAdapter_gold);
                        mBinding.spnGold.setSelection(0);

                        mBinding.spnGold.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mGoldName = mBinding.spnGold.getSelectedItem().toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                mGoldName = "";
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!GoldOutActivity.this.isFinishing())
                            CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!GoldOutActivity.this.isFinishing())
                            CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!GoldOutActivity.this.isFinishing())
                        CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!GoldOutActivity.this.isFinishing())
                    CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    public void getRequestProductData() {
        Call<ResponseBody> call = CommonClass.sApiService.getGoldRequestProductList(mRequestCode, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        List<String> list_requestProduct = new ArrayList<String>();
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int jsonArray_len = jsonArray.length();
                        if (jsonArray_len == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.none_data), Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < jsonArray_len; i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                list_requestProduct.add(c.getString("prdName") + "  " + c.getString("dent"));
                                mProductInfoCodes.add(c.getString("prdInfoNum"));
                            }
                            ArrayAdapter<String> arrayAdapter_requestProduct = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_requestProduct);
                            mBinding.spnRequestProduct.setAdapter(arrayAdapter_requestProduct);
                            arrayAdapter_requestProduct.notifyDataSetChanged();
                            mBinding.spnRequestProduct.setSelection(0);

                            mBinding.spnRequestProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    mProductInfoCode = mProductInfoCodes.get(position);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    mProductInfoCode = "";
                                }
                            });
                        }

                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!GoldOutActivity.this.isFinishing())
                            CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!GoldOutActivity.this.isFinishing())
                            CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!GoldOutActivity.this.isFinishing())
                        CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!GoldOutActivity.this.isFinishing())
                    CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
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
                            if (!GoldOutActivity.this.isFinishing()) {
                                CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                            }
                        } else {
                            getGoldName();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!GoldOutActivity.this.isFinishing())
                            CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!GoldOutActivity.this.isFinishing())
                            CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!GoldOutActivity.this.isFinishing())
                        CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!GoldOutActivity.this.isFinishing())
                    CommonClass.showDialog(GoldOutActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

}