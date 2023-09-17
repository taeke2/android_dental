package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gbsoft.com.dental_gb.adapter.CompanyListAdapter;
import gbsoft.com.dental_gb.databinding.ActivityLoginBinding;
import gbsoft.com.dental_gb.dto.CompanyDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final int TOUCH_COUNT = 5;
    private ActivityLoginBinding mBinding;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private int mSelCount = 1;
    private long mBackKeyPressedTime = 0;
    private ArrayList<CompanyDTO> mCompanies;
    private String mIp = "";
    private int mCode = -1;
    private String mServerPath = "";
    private String mCompany = "";
    private boolean mIsWaiting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
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
        mIp = mSharedPreferences.getString("ip", "");
        mEditor = mSharedPreferences.edit();

        mCompanies = new ArrayList<>();

        mBinding.listDb.setVisibility(View.GONE);

        mBinding.layoutSelDb.setOnClickListener(selectLayoutClick);
        mBinding.listDb.setOnItemClickListener(companyClick);
        mBinding.btnLogin.setOnClickListener(loginClick);
    }

    View.OnClickListener selectLayoutClick = v -> {
        if (!CommonClass.ic.GetInternet(LoginActivity.this.getApplicationContext())) {
            if (!LoginActivity.this.isFinishing())
                CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> { }, false);
        } else {
            if (mSelCount < TOUCH_COUNT) {
                mSelCount++;
            } else {
                if (!mIsWaiting) {
                    getCompanyList();
                    mSelCount = 1;
                }
            }
        }
    };

    AdapterView.OnItemClickListener companyClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCompany = mCompanies.get(position).getCompanyName();
            mServerPath = mCompanies.get(position).getServerPath();
            mCode = mCompanies.get(position).getCompanyCode();

            mEditor.putInt("num", mCode);
            mEditor.putString("company", mCompany);
            mEditor.putString("address", mServerPath);
            mEditor.commit();

            // Toast.makeText(getApplicationContext(), mCompany, Toast.LENGTH_SHORT).show();
            mBinding.factoryTxt.setText(mCompany);
            mBinding.listDb.setVisibility(View.GONE);

            CommonClass.sApiService = null;
            CommonClass.getApiService(mServerPath);
        }
    };

    View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!CommonClass.ic.GetInternet(LoginActivity.this.getApplicationContext())) {
                if (!LoginActivity.this.isFinishing())
                    CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> { }, false);
            } else {
                String inputId = mBinding.editId.getText().toString();
                String inputPw = mBinding.editPw.getText().toString();

                if (inputId.isEmpty()) {
                    mBinding.errorText.setText(getString(R.string.enter_id));
                } else if (inputPw.isEmpty()) {
                    mBinding.errorText.setText(getString(R.string.enter_pw));
                } else {
                    if (mCompany.equals("") || mCode == -1 || mServerPath.equals("")) {
                        mBinding.errorText.setText(getString(R.string.select_factory));
                    } else {
                        if (mCode == CommonClass.YK)
                            signIn();
                        else
                            getUserInfo(inputId);
                    }
                }
            }
        }
    };

    public void getCompanyList() {
        mIsWaiting = true;
        mCompanies.clear();
        if (CommonClass.sMasterApiService == null)
            CommonClass.getMasterApiService();

        Call<ResponseBody> call = CommonClass.sMasterApiService.getCompanyList("1");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int len = jsonArray.length();
                        for (int i = 0; i < len; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CompanyDTO item = new CompanyDTO();
                            item.setCompanyCode(jsonObject.getInt("num"));
                            item.setCompanyName(jsonObject.getString("company"));
                            item.setServerPath(jsonObject.getString("address"));
                            mCompanies.add(item);
                            CompanyListAdapter adapter = new CompanyListAdapter(mCompanies);
                            mBinding.listDb.setAdapter(adapter);

                            mBinding.listDb.setVisibility(View.VISIBLE);
                            mIsWaiting = false;
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        mIsWaiting = false;
                        if (!LoginActivity.this.isFinishing())
                            CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        mIsWaiting = false;
                        if (!LoginActivity.this.isFinishing())
                            CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    mIsWaiting = false;
                    if (!LoginActivity.this.isFinishing())
                        CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                mIsWaiting = false;
                if (!LoginActivity.this.isFinishing())
                    CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }

    public void getUserInfo(String inputId) {
        mBinding.btnLogin.setEnabled(false);

        Call<ResponseBody> call = CommonClass.sApiService.userAccessCheck(inputId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        mBinding.btnLogin.setEnabled(true);
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() == 0) {
                            // Toast.makeText(getApplicationContext(), getString(R.string.wrong_id_pw), Toast.LENGTH_SHORT).show();
                            mBinding.errorText.setText(getString(R.string.wrong_id_pw));
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            int approveYn = jsonObject.getInt("access");
                            if (approveYn == 1) {
                                if (mCode == CommonClass.PDL)
                                    getShutdownState(inputId);
                                else
                                    getLoginData();
                            } else {
                                if (!LoginActivity.this.isFinishing())
                                    CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.no_authority), () -> { }, false);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        mBinding.btnLogin.setEnabled(true);
                        if (!LoginActivity.this.isFinishing())
                            CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        mBinding.btnLogin.setEnabled(true);
                        if (!LoginActivity.this.isFinishing())
                            CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }
                } else {
                    Log.e(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    mBinding.btnLogin.setEnabled(true);
                    if (!LoginActivity.this.isFinishing())
                        CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(CommonClass.TAG_ERR, "server connect fail");
                mBinding.btnLogin.setEnabled(true);
                if (!LoginActivity.this.isFinishing())
                    CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }

    public void getShutdownState(String userId) {
        Call<ResponseBody> call = CommonClass.sApiService.getShutdownCheck(userId, mIp);
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
                            if (!LoginActivity.this.isFinishing())
                                CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                        } else {
                            getLoginData();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        mBinding.btnLogin.setEnabled(true);
                        if (!LoginActivity.this.isFinishing())
                            CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        mBinding.btnLogin.setEnabled(true);
                        if (!LoginActivity.this.isFinishing())
                            CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.e(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    mBinding.btnLogin.setEnabled(true);
                    if (!LoginActivity.this.isFinishing())
                        CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(CommonClass.TAG_ERR, "server connect fail");
                mBinding.btnLogin.setEnabled(true);
                if (!LoginActivity.this.isFinishing())
                    CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    public void getLoginData() {
        String id = mBinding.editId.getText().toString();
        String password = mBinding.editPw.getText().toString();
        Call<ResponseBody> call = CommonClass.sApiService.login(id, password, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (result.equals("N")) {
                            mBinding.errorText.setText(getString(R.string.check_id_pw));
                        } else {
                            JSONObject jsonObject = new JSONObject(result);
                            String userName = jsonObject.getString("EmployeeName");
                            saveDeviceInfo(id);

                            mEditor.putString("id", id);
                            mEditor.putString("userName", userName);
                            mEditor.commit();

                            Intent intent = new Intent(LoginActivity.this, MenuActivity2.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        mBinding.btnLogin.setEnabled(true);
                        if (!LoginActivity.this.isFinishing())
                            CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        mBinding.btnLogin.setEnabled(true);
                        if (!LoginActivity.this.isFinishing())
                            CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }
                } else {
                    Log.e(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    mBinding.btnLogin.setEnabled(true);
                    if (!LoginActivity.this.isFinishing())
                        CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(CommonClass.TAG_ERR, "server connect fail");
                mBinding.btnLogin.setEnabled(true);
                if (!LoginActivity.this.isFinishing())
                    CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
    }

    public void saveDeviceInfo(String userId) {
        Call<ResponseBody> call = CommonClass.sApiService.deviceRecord(userId, getDeviceSerialNumber(), mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 409) {
                        Log.e(CommonClass.TAG_ERR, "이미 등록된 기기입니다.");
                    } else {
                        Log.e(CommonClass.TAG_ERR, "response is fail, " + String.valueOf(response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(CommonClass.TAG_ERR, "server connect fail");
            }
        });
    }

    private String getDeviceSerialNumber() {
        try {
            String serialNumber = "";
            serialNumber = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            return serialNumber;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void signIn() {
        mBinding.btnLogin.setEnabled(false);
        String id = mBinding.editId.getText().toString();
        String password = mBinding.editPw.getText().toString();
        Call<ResponseBody> call = CommonClass.sApiService.signIn(id, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        mBinding.errorText.setText("");
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

                        Intent intent = new Intent(LoginActivity.this, MenuActivity2.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();

                    } catch (IOException | JSONException e) {
                        if (!LoginActivity.this.isFinishing())
                            CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
                    }
                } else if (code == 401) {
                    mBinding.errorText.setText("권한이 없는 사용자 입니다.");
                } else if (code == 403) {
                    mBinding.errorText.setText("아이디 혹은 비밀번호가 일치하지 않습니다.");
                } else {
                    if (!LoginActivity.this.isFinishing())
                        CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { }, false);
                }
                mBinding.btnLogin.setEnabled(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mBinding.btnLogin.setEnabled(true);
                if (!LoginActivity.this.isFinishing())
                    CommonClass.showDialog(LoginActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { }, false);
            }
        });
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
