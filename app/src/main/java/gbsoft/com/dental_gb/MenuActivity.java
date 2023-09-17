package gbsoft.com.dental_gb;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import gbsoft.com.dental_gb.databinding.ActivityMenuBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private ActivityMenuBinding mBinding;
    private long mBackKeyPressedTime = 0;
    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mCompany = "";
    private String mServerPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        mBinding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        imgInit();
        initialSet();
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

        mCompany = sharedPreferences.getString("company", "");
        mServerPath = sharedPreferences.getString("address", "");

        mBinding.txtTitle.setText(mCompany);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                10.5f
        );

        mBinding.btnGold.setLayoutParams(param);

        mBinding.viewLeft.setVisibility(View.GONE);
        mBinding.btnRelease.setVisibility(View.GONE);
        mBinding.monitoringLayout.setVisibility(View.GONE);

        mBinding.btnMypage.setOnClickListener(this);
        mBinding.btnProcess.setOnClickListener(this);
        mBinding.btnRequest.setOnClickListener(this);
        mBinding.btnMaterials.setOnClickListener(this);
        mBinding.btnBarcode.setOnClickListener(this);
        mBinding.btnGold.setOnClickListener(this);
        mBinding.btnRelease.setOnClickListener(this);
        mBinding.btnMonitoring.setOnClickListener(this);

        mBinding.btnProcess.setOnTouchListener(this);
        mBinding.btnRequest.setOnTouchListener(this);
        mBinding.btnMaterials.setOnTouchListener(this);
        mBinding.btnBarcode.setOnTouchListener(this);
        mBinding.btnGold.setOnTouchListener(this);
        mBinding.btnRelease.setOnTouchListener(this);
        mBinding.btnMonitoring.setOnTouchListener(this);

        if (mCode == CommonClass.PDL) {
            getShutdownCheck();
            param = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    5f
            );

            mBinding.btnGold.setLayoutParams(param);

            mBinding.viewLeft.setVisibility(View.VISIBLE);
            mBinding.btnRelease.setVisibility(View.VISIBLE);
            mBinding.monitoringLayout.setVisibility(View.VISIBLE);
        } else if (mCode == CommonClass.YK) {
            mBinding.txtBarcode.setText(getString(R.string.barcode_scan));
        }
    }

    private void imgInit(){
        Glide.with(this).load(R.drawable.gold).into(mBinding.imgGold);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_mypage:
                intent = new Intent(MenuActivity.this, MyPageActivity.class);
                break;
            case R.id.btn_process:
                intent = new Intent(MenuActivity.this, ProcessActivity.class);
                break;
            case R.id.btn_request:
                intent = new Intent(MenuActivity.this, RequestActivity.class);
                break;
            case R.id.btn_materials:
                intent = new Intent(MenuActivity.this, MaterialsActivity.class);
                break;
            case R.id.btn_barcode:
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
                } else {
                    if (mCode == CommonClass.YK)
                        intent = new Intent(MenuActivity.this, ProcessBarcodeActivity.class);
                    else
                        intent = new Intent(MenuActivity.this, BarcodeActivity.class);
                }
                break;
            case R.id.btn_gold:
                intent = new Intent(MenuActivity.this, GoldActivity.class);
                break;
            case R.id.btn_release:
                intent = new Intent(MenuActivity.this, ReleaseActivity.class);
                break;
            case R.id.btn_monitoring:
                intent = new Intent(MenuActivity.this, MonitoringActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.btn_gold:
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mBinding.btnGold.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border2));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    mBinding.btnGold.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border));
                break;
            case R.id.btn_process:
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mBinding.btnProcess.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border2));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    mBinding.btnProcess.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border));
                break;
            case R.id.btn_request:
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mBinding.btnRequest.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border2));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    mBinding.btnRequest.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border));
                break;
            case R.id.btn_materials:
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mBinding.btnMaterials.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border2));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    mBinding.btnMaterials.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border));
                break;
            case R.id.btn_barcode:
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mBinding.btnBarcode.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border2));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    mBinding.btnBarcode.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border));
                break;
            case R.id.btn_release:
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mBinding.btnRelease.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border2));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    mBinding.btnRelease.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border));
                break;
            case R.id.btn_monitoring:
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mBinding.btnMonitoring.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border2));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    mBinding.btnMonitoring.setBackground(ContextCompat.getDrawable(MenuActivity.this, R.drawable.border));

                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1 :
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    CommonClass.showDialog(MenuActivity.this, getString(R.string.error_title), getString(R.string.permission_check), () -> {}, false);
                }
                break;
            case 2 :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent;
                    if (mCode == CommonClass.YK)
                        intent = new Intent(MenuActivity.this, ProcessBarcodeActivity.class);
                    else
                        intent = new Intent(MenuActivity.this, BarcodeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else {
                    CommonClass.showDialog(MenuActivity.this, getString(R.string.error_title), getString(R.string.permission_camera), () -> {}, false);
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void getShutdownCheck() {
        URL url = null;
        try {
            url = new URL(mServerPath);
        } catch (Exception e) {
            e.printStackTrace();
            if (!MenuActivity.this.isFinishing())
                CommonClass.showDialog(MenuActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
        }
        if (!url.equals(null)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService service = retrofit.create(ApiService.class);
            Call<ResponseBody> call = service.getShutdownCheck(mId, mIp);
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
                                if (!MenuActivity.this.isFinishing())
                                    CommonClass.showDialog(MenuActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                        finishAffinity();
                                        System.runFinalization();
                                        System.exit(0);
                                    }, false);
                            }
                        } catch (JSONException e) {
                            Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                            if (!MenuActivity.this.isFinishing())
                                CommonClass.showDialog(MenuActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                        } catch (IOException e) {
                            Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                            if (!MenuActivity.this.isFinishing())
                                CommonClass.showDialog(MenuActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                        }
                    } else {
                        Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                        if (!MenuActivity.this.isFinishing())
                            CommonClass.showDialog(MenuActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(CommonClass.TAG_ERR, "server connect fail");
                    if (!MenuActivity.this.isFinishing())
                        CommonClass.showDialog(MenuActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
                }
            });
        } else {
            Log.e(CommonClass.TAG_ERR, "url is null");
            if (!MenuActivity.this.isFinishing())
                CommonClass.showDialog(MenuActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mBackKeyPressedTime < 1500) {
            finishAffinity();
            System.runFinalization();
            System.exit(0);
        }
        mBackKeyPressedTime = System.currentTimeMillis();
        Toast.makeText(getApplicationContext(), getString(R.string.exit_app), Toast.LENGTH_SHORT).show();
    }
}
