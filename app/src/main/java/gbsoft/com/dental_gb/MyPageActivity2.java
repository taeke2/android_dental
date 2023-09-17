package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import gbsoft.com.dental_gb.databinding.ActivityMyPage2Binding;

public class MyPageActivity2 extends Activity {
    private ActivityMyPage2Binding mBinding;

    private SharedPreferences.Editor mEditor;

    private String mIp = "";
    private String mId = "";
//    private int mCode = -1;
//    private String mServerPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMyPage2Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
//        mCode = sharedPreferences.getInt("num", -1);
//        mServerPath = sharedPreferences.getString("address", "");
        String userName = sharedPreferences.getString("userName", "");

        mBinding.btnLogout.setOnClickListener(logoutClick);

        mBinding.txtUserName.setText(userName);
        mBinding.txtUserId.setText(mId);
        // 생년월일, 이메일, 연락처 등 setText();

//        if (mCode == CommonClass.PDL)
//            getShutdownCheck();
    }

    private View.OnClickListener logoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CommonClass.showDialog(MyPageActivity2.this, getString(R.string.logout), getString(R.string.logout_ask), () -> {
                mEditor.clear();
                mEditor.putString("ip", mIp);
                mEditor.commit();

                Intent intent = new Intent(MyPageActivity2.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }, true);
        }
    };

    // myPage에 shutdown이 필요한가?
//    public void getShutdownCheck() {
//        URL url = null;
//        try {
//            url = new URL(mServerPath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (!url.equals(null)) {
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(url)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//            ApiService service = retrofit.create(ApiService.class);
//            Call<ResponseBody> call = service.getShutdownCheck(mId, mIp);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.isSuccessful()) {
//
//                        try {
//                            String result = response.body().string();
//                            JSONArray jsonArray = new JSONArray(result);
//                            JSONObject jsonObject = jsonArray.getJSONObject(0);
//                            int mShutdownYn = Integer.parseInt(jsonObject.getString("down"));
//                            if (mShutdownYn == 1) {
//                                if (!MyPageActivity.this.isFinishing())
//                                    CommonClass.showDialog(MyPageActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
//                                        finishAffinity();
//                                        System.runFinalization();
//                                        System.exit(0);
//                                    });
//                            }
//                        } catch (JSONException e) {
//                            Log.e(TAG_ERR, "ERROR: JSON Parsing Error");
//                            if (!MyPageActivity.this.isFinishing())
//                                CommonClass.showDialog(MyPageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish());
//                        } catch (IOException e) {
//                            Log.e(TAG_ERR, "ERROR: IOException");
//                            if (!MyPageActivity.this.isFinishing())
//                                CommonClass.showDialog(MyPageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish());
//                        }
//                    } else {
//                        Log.d(TAG_ERR, "response is fail, " + response.code());
//                        if (!MyPageActivity.this.isFinishing())
//                            CommonClass.showDialog(MyPageActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Log.d(TAG_ERR, "server connect fail");
//                    if (!MyPageActivity.this.isFinishing())
//                        CommonClass.showDialog(MyPageActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish());
//                }
//            });
//        } else {
//            Log.e(TAG_ERR, "url is null");
//            if (!MyPageActivity.this.isFinishing())
//                CommonClass.showDialog(MyPageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish());
//        }
//    }
}
