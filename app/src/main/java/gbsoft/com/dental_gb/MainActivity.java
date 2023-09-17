package gbsoft.com.dental_gb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import gbsoft.com.dental_gb.databinding.ActivityMainBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String mIp = "";
    private String mId = "";
    private String mUserName = "";
    private int mCode = -1;
    private String mCompany = "";
    private String mServerPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialSet() {
        mSharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mIp = getIpAddress();
        mEditor.putString("ip", mIp);
        mEditor.apply();
        mId = mSharedPreferences.getString("id", "");
        mUserName = mSharedPreferences.getString("userName", "");
        mCode = mSharedPreferences.getInt("num", -1);
        mCompany = mSharedPreferences.getString("company", "");
        mServerPath = mSharedPreferences.getString("address", "");
        Glide.with(this).load(R.raw.loading).into(mBinding.imgLoading);

        if (mId.equals("") || mUserName.equals("") || mCode == -1 || mCompany.equals("") || mServerPath.equals("")) {
            loadingHandler.sendEmptyMessageDelayed(0, 1000);
        } else {
            CommonClass.getApiService(mServerPath);
            if (mCode == CommonClass.PDL)
                getShutdownState();
            else if (mCode == CommonClass.YK)
                signIn(mId, mSharedPreferences.getString("pw", ""));
            else
                getUserInfo();
        }
    }

    public String getIpAddress() {
        StringBuilder ip = new StringBuilder();
        Enumeration<NetworkInterface> enumNetworkInterfaces = null;
        try {
            enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip.append("").append(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            ip.append("Something Wrong! ").append(e.toString()).append("\n");
        }

        return ip.toString();
    }

    public void getUserInfo() {
        Call<ResponseBody> call = CommonClass.sApiService.userAccessCheck(mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() == 0) {
//                                Toast.makeText(getApplicationContext(), getString(R.string.wrong_id_pw), Toast.LENGTH_SHORT).show();
                            loadingHandler.sendEmptyMessageDelayed(0, 1000);
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            int approveYn = jsonObject.getInt("access");
                            if (approveYn == 1) {
                                loadingHandler.sendEmptyMessageDelayed(1, 1000);
                            } else {
                                loadingHandler.sendEmptyMessageDelayed(0, 1000);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!MainActivity.this.isFinishing())
                            CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!MainActivity.this.isFinishing())
                            CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!MainActivity.this.isFinishing())
                        CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!MainActivity.this.isFinishing())
                    CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    public void getShutdownState() {
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
                        if (mShutdownYn == 1){
                            if (!MainActivity.this.isFinishing())
                                CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                        } else if (mId.equals("") || mUserName.equals("") || mCode == -1 || mCompany.equals("") || mServerPath.equals("")) {
                            loadingHandler.sendEmptyMessageDelayed(0, 1000);
                        } else {
                            getUserInfo();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!MainActivity.this.isFinishing())
                            CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!MainActivity.this.isFinishing())
                            CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!MainActivity.this.isFinishing())
                        CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!MainActivity.this.isFinishing())
                    CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    private void signIn(String id, String password) {
        Call<ResponseBody> call = CommonClass.sApiService.signIn(id, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        mEditor.putString("id", jsonObject.getString("id"));
                        mEditor.putString("pw", password);
                        mEditor.putString("userName", jsonObject.getString("name"));
                        mEditor.putString("processName", jsonObject.getString("processName").equals("null") ? "-" : jsonObject.getString("processName"));
                        mEditor.putString("code", jsonObject.getString("code").equals("null") ? "-" : String.valueOf(jsonObject.getInt("code")));
                        mEditor.putString("birth", jsonObject.getString("birth").equals("null") ? "-" : jsonObject.getString("birth"));
                        mEditor.putString("department", jsonObject.getString("department").equals("null") ? "-" : jsonObject.getString("department"));
                        mEditor.putString("position", jsonObject.getString("position").equals("null") ? "-" : jsonObject.getString("position"));
                        mEditor.putString("clientCode", jsonObject.getString("clientCode").equals("null") ? "-" : String.valueOf(jsonObject.getInt("clientCode")));
                        mEditor.putString("contact", jsonObject.getString("contact").equals("null") ? "-" : jsonObject.getString("contact"));
                        mEditor.putString("joinDate", jsonObject.getString("joinDate").equals("null") ? "-" : jsonObject.getString("joinDate"));
                        // mEditor.putString("email", jsonObject.getString("email").equals("null") ? "-" : jsonObject.getString("email"));

                        JSONObject jsonAuth = new JSONObject(jsonObject.getString("auth"));
                        String[] authList = new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0"};
                        if (jsonAuth.getBoolean("APP_001")) authList[0] = "1";
                        if (jsonAuth.getBoolean("APP_002")) authList[1] = "1";
                        if (jsonAuth.getBoolean("APP_003")) authList[2] = "1";
                        if (jsonAuth.getBoolean("APP_004")) authList[3] = "1";
                        if (jsonAuth.getBoolean("APP_005")) authList[4] = "1";
                        if (jsonAuth.getBoolean("APP_006")) authList[5] = "1";
                        if (jsonAuth.getBoolean("APP_007")) authList[6] = "1";
                        if (jsonAuth.getBoolean("APP_008")) authList[7] = "1";
                        if (jsonAuth.getBoolean("APP_009")) authList[8] = "1";

                        mEditor.putString("menuAuth", TextUtils.join(",", authList));
                        mEditor.commit();

                        loadingHandler.sendEmptyMessageDelayed(1, 1000);
                    } catch (IOException | JSONException e) {
                        if (!MainActivity.this.isFinishing())
                            CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }
                } else if (code == 401) {
                    loadingHandler.sendEmptyMessageDelayed(0, 1000);
                } else if (code == 403) {
                    loadingHandler.sendEmptyMessageDelayed(0, 1000);
                } else {
                    if (!MainActivity.this.isFinishing())
                        CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!MainActivity.this.isFinishing())
                    CommonClass.showDialog(MainActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }

    private Handler loadingHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                    mEditor.clear();
                    mEditor.apply();
                    Intent intentLogin = new Intent(getBaseContext(), LoginActivity.class);
                    intentLogin.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentLogin);
                    break;
                case 1:
//                     Intent intentMenu = new Intent(getBaseContext(), MenuActivity.class);
                    Intent alarmIntent = getIntent();
                    Intent intentMenu = new Intent(getBaseContext(), MenuActivity2.class);
                    if (alarmIntent.getStringExtra("type") != null)
                        intentMenu.putExtra("type", alarmIntent.getStringExtra("type"));
                    intentMenu.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentMenu);
                    break;
                default:
                    break;
            }
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }
}
