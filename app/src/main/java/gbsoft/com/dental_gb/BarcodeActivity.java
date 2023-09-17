package gbsoft.com.dental_gb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import gbsoft.com.dental_gb.databinding.ActivityBarcodeBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarcodeActivity extends AppCompatActivity {
    private ActivityBarcodeBinding mBinding;
    private IntentIntegrator mQrScan;
    private String mGetBarcode = "";
    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mBinding = ActivityBarcodeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(BarcodeActivity.this.getApplicationContext())) {
            if (!BarcodeActivity.this.isFinishing())
                CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
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

        mQrScan = new IntentIntegrator(this);
        mQrScan.setOrientationLocked(false);
        mQrScan.initiateScan();

        if (mCode == CommonClass.PDL)
            getShutdownCheck();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            mGetBarcode = result.getContents();
            if (mGetBarcode == null) {
                onBackPressed();
            } else {
                String tmp = mGetBarcode.substring(0, 1);
                if (tmp.equals("P") || tmp.equals("M")) {
                    getFaultyCheck();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_barcode), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getFaultyCheck() {
        Call<ResponseBody> call = CommonClass.sApiService.getBarcodeScan_f(mGetBarcode, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);

                        int jsonArray_len = jsonArray.length();
                        if (jsonArray_len >= 1) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String requestCode = jsonObject.getString("reqNum");
                            Toast.makeText(getApplicationContext(), getString(R.string.defective_product), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BarcodeActivity.this, RequestDetailActivity.class);
                            intent.putExtra("reqNum", requestCode);
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            finish();
                        } else {
                            String tmp = mGetBarcode.substring(0, 1);
                            if (tmp.equals("P")) {
                                getBarcodeScan_P();
                            } else if (tmp.equals("M")) {
                                getBarcodeScan_M();
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!BarcodeActivity.this.isFinishing())
                            CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!BarcodeActivity.this.isFinishing())
                            CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!BarcodeActivity.this.isFinishing())
                        CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!BarcodeActivity.this.isFinishing())
                    CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }

    public void getBarcodeScan_P() {
        Call<ResponseBody> call = CommonClass.sApiService.getBarcodeScan_p(mGetBarcode, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String requestCode = jsonObject.getString("reqNum");
                        String clientName = jsonObject.getString("client");
                        String orderFinishTime = jsonObject.getString("ordFinish");

                        if (orderFinishTime.equals("null")) {
                            Intent intent = new Intent(BarcodeActivity.this, OrderActivity.class);
                            intent.putExtra("ordNum", mGetBarcode);
                            intent.putExtra("reqNum", requestCode);
                            intent.putExtra("client", clientName);
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.complete_product), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BarcodeActivity.this, RequestDetailActivity.class);
                            intent.putExtra("reqNum", requestCode);
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            finish();
                        }

                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!BarcodeActivity.this.isFinishing())
                            CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!BarcodeActivity.this.isFinishing())
                            CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!BarcodeActivity.this.isFinishing())
                        CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!BarcodeActivity.this.isFinishing())
                    CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }

    public void getBarcodeScan_M() {
        Call<ResponseBody> call = CommonClass.sApiService.getBarcodeScan_m(mGetBarcode, mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String materialsCode = jsonObject.getString("matNum");
                        String clientCode = jsonObject.getString("cliNum");
                        Intent intent = new Intent(BarcodeActivity.this, MaterialsFaultyActivity.class);
                        intent.putExtra("receiveNum", mGetBarcode);
                        intent.putExtra("matNum", materialsCode);
                        intent.putExtra("cliNum", clientCode);
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!BarcodeActivity.this.isFinishing())
                            CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!BarcodeActivity.this.isFinishing())
                            CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!BarcodeActivity.this.isFinishing())
                        CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!BarcodeActivity.this.isFinishing())
                    CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
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
                            if (!BarcodeActivity.this.isFinishing())
                                CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!BarcodeActivity.this.isFinishing())
                            CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!BarcodeActivity.this.isFinishing())
                            CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!BarcodeActivity.this.isFinishing())
                        CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!BarcodeActivity.this.isFinishing())
                    CommonClass.showDialog(BarcodeActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

}