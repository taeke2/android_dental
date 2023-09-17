package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ActivityMaterialsFaultyBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MaterialsFaultyActivity extends Activity {
    private ActivityMaterialsFaultyBinding mBinding;

    private String mReceivingBarcode = "";
    private String mMaterialsCode = "";
    private String mClientCode = "";
    private String mMaterialsName = "";
    private String mClientName = "";
    private String mInDate = "";
    private String mQuantity = "";
    private int mFaultyQuantity;

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMaterialsFaultyBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(MaterialsFaultyActivity.this.getApplicationContext())) {
            if (!MaterialsFaultyActivity.this.isFinishing())
                CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
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
        mReceivingBarcode = getIntent.getStringExtra("recNum");
        mMaterialsCode = getIntent.getStringExtra("matNum");
        mClientCode = getIntent.getStringExtra("cliNum");
        mMaterialsName = getIntent.getStringExtra("matName");
        mClientName = getIntent.getStringExtra("client");
        mInDate = getIntent.getStringExtra("inDate");
        mQuantity = getIntent.getStringExtra("qua");

        mBinding.txtClientName.setText(mClientName);
        mBinding.txtMaterialsName.setText(mMaterialsName);
        mBinding.txtDate.setText(mInDate);
        mBinding.txtQuantity.setText(mQuantity);
        mBinding.faultyError.setVisibility(View.INVISIBLE);

        mBinding.btnMaterialsAdd.setOnClickListener(btnAddClick);
        mBinding.btnCancel.setOnClickListener(v -> onBackPressed());

        if (mCode == CommonClass.PDL)
            getShutdownCheck();
        else
            getFaultyQuantity();
    }

    View.OnClickListener btnAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String faultyQuantity = mBinding.edttxtFaultyQuantity.getText().toString();
            int in_quantity = Integer.parseInt(mQuantity);
            if (faultyQuantity.equals("")) {
                mBinding.faultyError.setText(getString(R.string.faulty_num_empty));
                mBinding.faultyError.setVisibility(View.VISIBLE);
            } else if ((Integer.parseInt(faultyQuantity) + mFaultyQuantity) > in_quantity) {
                mBinding.faultyError.setText(getString(R.string.wrong_in_out_num));
                mBinding.faultyError.setVisibility(View.VISIBLE);
            } else {
                if ((Integer.parseInt(faultyQuantity) + mFaultyQuantity) == in_quantity) {
                    Toast.makeText(MaterialsFaultyActivity.this, getString(R.string.all_faulty), Toast.LENGTH_SHORT).show();
                }
                setMaterialsFaulty();
            }
        }
    };

    public void getFaultyQuantity() {
        Call<ResponseBody> call = CommonClass.sApiService.getFaultyQuantity(mReceivingBarcode, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        mFaultyQuantity = jsonObject.getString("fauQua").equals("null") ? 0 : jsonObject.getInt("fauQua");
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!MaterialsFaultyActivity.this.isFinishing())
                            CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!MaterialsFaultyActivity.this.isFinishing())
                            CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!MaterialsFaultyActivity.this.isFinishing())
                        CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!MaterialsFaultyActivity.this.isFinishing())
                    CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    public void setMaterialsFaulty() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String faultyTime = format.format(date);
        String faultyQuantity = mBinding.edttxtFaultyQuantity.getText().toString();
        String faultyHistory = mBinding.editFaultyRemark.getText().toString();
        Call<ResponseBody> call = CommonClass.sApiService.setMaterialsFaulty(mMaterialsCode, mClientCode, mId, mReceivingBarcode, getString(R.string.faulty), faultyTime, faultyQuantity, faultyHistory, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    finish();
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!MaterialsFaultyActivity.this.isFinishing())
                        CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> {  }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!MaterialsFaultyActivity.this.isFinishing())
                    CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> {  }, false);
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
                            if (!MaterialsFaultyActivity.this.isFinishing()) {
                                CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                            }
                        } else {
                            getFaultyQuantity();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!MaterialsFaultyActivity.this.isFinishing())
                            CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!MaterialsFaultyActivity.this.isFinishing())
                            CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!MaterialsFaultyActivity.this.isFinishing())
                        CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!MaterialsFaultyActivity.this.isFinishing())
                    CommonClass.showDialog(MaterialsFaultyActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        alertShow();
        finish();
    }

//    public void alertShow() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(MaterialsFaultyActivity.this);
//        builder.setMessage(getString(R.string.exit_faulty_ask));
//        builder.setTitle(getString(R.string.exit_faulty)).setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.setTitle("종료");
//        alert.show();
//    }
}