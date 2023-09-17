package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import gbsoft.com.dental_gb.databinding.ActivityMemoBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MemoActivity extends Activity implements View.OnClickListener {
    private ActivityMemoBinding mBinding;

    private String mRequestCode = "";

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mBinding = ActivityMemoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        super.onCreate(savedInstanceState);

        if (!CommonClass.ic.GetInternet(MemoActivity.this.getApplicationContext())) {
            if (!MemoActivity.this.isFinishing())
                CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
        } else {
            initialSet();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", MODE_PRIVATE);

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);
        mServerPath = sharedPreferences.getString("address", "");

        if (CommonClass.sApiService == null)
            CommonClass.getApiService(mServerPath);

        mBinding.btnMemoSave.setOnClickListener(this);
        mBinding.btnMemoCancel.setOnClickListener(this);


        Intent intent = getIntent();
        mRequestCode = intent.getStringExtra("reqNum");

        if (mCode == CommonClass.PDL)
            getShutdownCheck("");
        else
            getMemo();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_memoSave:
                String text = mBinding.editMemo.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.empty_content), Toast.LENGTH_SHORT).show();
                } else if (text.length() >= 50) {
                    Toast.makeText(getApplicationContext(), getString(R.string.content_maximum), Toast.LENGTH_SHORT).show();
                } else {
                    if (mCode == CommonClass.PDL)
                        getShutdownCheck(text);
                    else
                        writeMemo(text);
                }
                break;
            case R.id.btn_memoCancel:
                finish();
                break;
            default:
                break;
        }
    }

    private void getMemo() {
        Call<ResponseBody> call;
        if(mCode == CommonClass.YK){
            call = CommonClass.sApiService.getMemoYK(mRequestCode, mId, mIp);
        } else {
            call = CommonClass.sApiService.getMemo(mRequestCode, mId, mIp);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject c = jsonArray.getJSONObject(0);
                        mBinding.editMemo.setText(c.getString("remark").equals("null") ? "" : c.getString("remark"));
                        String clientName = c.getString("client");
                        String patientName = c.has("patient") ? c.getString("patient") : "-";
                        String productName = c.getString("prdName");
                        productName = (productName.length() > 25) ? productName.substring(0, 20) + "..." : productName;
                        mBinding.txtRequestInfo.setText(clientName + " | " + patientName + " | " + productName);
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!MemoActivity.this.isFinishing())
                            CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);

                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!MemoActivity.this.isFinishing())
                            CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!MemoActivity.this.isFinishing())
                        CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!MemoActivity.this.isFinishing())
                    CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    private void writeMemo(String text) {

        Call<ResponseBody> call;
        if(mCode == CommonClass.YK){
            call = CommonClass.sApiService.writeMemoYK(mRequestCode, text, mId, mIp);;
        } else {
            call = CommonClass.sApiService.writeMemo(mRequestCode, text, mId, mIp);
        }


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!MemoActivity.this.isFinishing())
                        CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!MemoActivity.this.isFinishing())
                    CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> {  }, false);
            }
        });
    }

    public void getShutdownCheck(String memoText) {
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
                            if (!MemoActivity.this.isFinishing())
                                CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                        } else {
                            if (memoText.equals(""))    getMemo();
                            else    writeMemo(memoText);
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!MemoActivity.this.isFinishing())
                            CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!MemoActivity.this.isFinishing())
                            CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!MemoActivity.this.isFinishing())
                        CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!MemoActivity.this.isFinishing())
                    CommonClass.showDialog(MemoActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }
}
