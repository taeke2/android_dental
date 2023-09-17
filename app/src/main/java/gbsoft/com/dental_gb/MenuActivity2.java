package gbsoft.com.dental_gb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import gbsoft.com.dental_gb.adapter.MenuAdapter;
import gbsoft.com.dental_gb.databinding.ActivityMenu2Binding;
import gbsoft.com.dental_gb.dto.AlarmDTO;
import gbsoft.com.dental_gb.dto.MenuItem;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.firebase.messaging.FirebaseMessaging;

public class MenuActivity2 extends AppCompatActivity {
    private static ActivityMenu2Binding mBinding;
    private MenuAdapter mMenuAdapter;
    private long mBackKeyPressedTime = 0;
    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mMenuAuth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        Iconify.with(new FontAwesomeModule());
        mBinding = ActivityMenu2Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MenuActivity2.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (!permissionGranted()) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } else
            initialSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private boolean permissionGranted() {
        Set<String> sets = NotificationManagerCompat.getEnabledListenerPackages(this);
        return sets != null && sets.contains(getPackageName());
    }

    public void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);
        mMenuAuth = sharedPreferences.getString("menuAuth", "0,0,0,0,0,0,0,0,0");

        String company = sharedPreferences.getString("company", "");
        String serverPath = sharedPreferences.getString("address", "");

        if (CommonClass.sApiService == null)
            CommonClass.getApiService(serverPath);

        mBinding.txtTitle.setText(company);

        mMenuAdapter = new MenuAdapter();
        mMenuAdapter.clearItems();
        if (mCode == CommonClass.YK) {
            mBinding.txtAlarmCnt.setVisibility(View.GONE);
            mBinding.iconMyPage.setVisibility(View.GONE);
            mBinding.layoutMypage.setVisibility(View.VISIBLE);
            mBinding.txtUser.setText(sharedPreferences.getString("userName", ""));
            mBinding.txtPosition.setText(sharedPreferences.getString("position", ""));
            mBinding.btnAlarm.setOnClickListener(alarmClick);
            mBinding.layoutMypage.setOnClickListener(myPageClick);

            updateFcmToken();
            getMenuAuth();
        } else {
            mBinding.btnAlarm.setVisibility(View.GONE);
            mBinding.txtAlarmCnt.setVisibility(View.GONE);
            mBinding.iconMyPage.setVisibility(View.VISIBLE);
            mBinding.layoutMypage.setVisibility(View.GONE);
            mBinding.iconMyPage.setOnClickListener(myPage2Click);

            mMenuAdapter.addItem(new MenuItem(3, getString(R.string.request_list), "{fa-copy}", -1));
            mMenuAdapter.addItem(new MenuItem(4, getString(R.string.process_situation), "{fa-tasks}", -1));
            mMenuAdapter.addItem(new MenuItem(6, getString(R.string.material_management), "{fa-cubes}", -1));
            mMenuAdapter.addItem(new MenuItem(7, getString(R.string.gold_management), "", R.drawable.gold));
            mMenuAdapter.addItem(new MenuItem(9, getString(R.string.qr_scan), "{fa-qrcode}", -1));

            if (mCode == CommonClass.PDL) {
                getShutdownCheck();
                mMenuAdapter.addItem(new MenuItem(5, getString(R.string.release_management), "{fa-truck}", -1));
                mMenuAdapter.addItem(new MenuItem(8, getString(R.string.monitoring), "{fa-desktop}", -1));
            }

            Collections.sort(mMenuAdapter.getMenuListItems());
            mMenuAdapter.setOnItemClickListener(pos -> {menuItemClick(pos);});
            mBinding.gridMenu.setAdapter(mMenuAdapter);
            int spanCount = 1;
            if (mMenuAdapter.getItemCount() > 6)
                spanCount = 3;
            else if (mMenuAdapter.getItemCount() > 3)
                spanCount = 2;

            mBinding.gridMenu.setLayoutManager(new GridLayoutManager(MenuActivity2.this, spanCount));
        }
    }

    public static void showAlarmBadge(int alarmCnt) {
        if (alarmCnt > 0) {
            mBinding.txtAlarmCnt.setVisibility(View.VISIBLE);
            mBinding.txtAlarmCnt.setText(String.valueOf(alarmCnt));
        } else
            mBinding.txtAlarmCnt.setVisibility(View.GONE);
    }

    private View.OnClickListener alarmClick = v -> {
        Intent intent = new Intent(MenuActivity2.this, AlarmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    };

    private View.OnClickListener myPageClick = v -> {
        Intent intent = new Intent(MenuActivity2.this, MyPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    };

    private View.OnClickListener myPage2Click = v -> {
        Intent intent = new Intent(MenuActivity2.this, MyPageActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    };

    private void menuItemClick(int pos) {
        int menuId = mMenuAdapter.getItem(pos).getMenuId();
        Intent intent = null;
        switch(menuId) {
            case 1: // notice
                intent = new Intent(MenuActivity2.this, NoticeActivity.class);
                break;
            case 2: // client management
                intent = new Intent(MenuActivity2.this, ClientActivity.class);
                break;
            case 3: // request list
//                intent = new Intent(MenuActivity2.this, RequestActivity.class);
                intent = new Intent(MenuActivity2.this, RequestActivity2.class);
                break;
            case 4: // process list
                intent = new Intent(MenuActivity2.this, ProcessActivity.class);
                break;
            case 5: // release
//                intent = new Intent(MenuActivity2.this, ReleaseActivity.class);
                intent = new Intent(MenuActivity2.this, ReleaseActivity2.class);
                break;
            case 6: // materials
//                intent = new Intent(MenuActivity2.this, MaterialsActivity.class);
                intent = new Intent(MenuActivity2.this, MaterialsManagementActivity.class);
                break;
            case 7: // gold
//                intent = new Intent(MenuActivity2.this, GoldActivity.class);
                intent = new Intent(MenuActivity2.this, GoldActivity2.class);
                break;
            case 8: // monitoring
//                intent = new Intent(MenuActivity2.this, MonitoringActivity.class);
                intent = new Intent(MenuActivity2.this, PlantManagementActivity.class);
                break;
            case 9: // barcode
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MenuActivity2.this, new String[]{Manifest.permission.CAMERA}, 2);
                } else {
                    if (mCode == CommonClass.YK)
                        intent = new Intent(MenuActivity2.this, ProcessBarcodeActivity.class);
                    else
                        intent = new Intent(MenuActivity2.this, BarcodeActivity.class);
                }
                break;
        }

        if (intent != null)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1 :
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    CommonClass.showDialog(MenuActivity2.this, getString(R.string.error_title), getString(R.string.permission_check), () -> {}, false);
                }
                break;
            case 2 :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent;
                    if (mCode == CommonClass.YK)
                        intent = new Intent(MenuActivity2.this, ProcessBarcodeActivity.class);
                    else
                        intent = new Intent(MenuActivity2.this, BarcodeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else {
                    CommonClass.showDialog(MenuActivity2.this, getString(R.string.error_title), getString(R.string.permission_camera), () -> {}, false);
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void updateFcmToken() {
        Task<String> token = FirebaseMessaging.getInstance().getToken();
        token.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String fcmToken = task.getResult();
                    updateFcmTokenApi(fcmToken);
                }
            }
        });
    }

    private void updateFcmTokenApi(String fcmToken) {
        Call<ResponseBody> call = CommonClass.sApiService.updateToken(mId, fcmToken);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    Toast.makeText(MenuActivity2.this, "token updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!MenuActivity2.this.isFinishing())
                        CommonClass.showDialog(MenuActivity2.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!MenuActivity2.this.isFinishing())
                    CommonClass.showDialog(MenuActivity2.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });

    }

    private void getMenuAuth() {
        mMenuAdapter.clearItems();

        String[] menuAuth = mMenuAuth.split(",");
        for (int i = 0; i < menuAuth.length; i++) {
            switch (i + 1) {
                case 1:
                    mMenuAdapter.addItem(new MenuItem(1, getString(R.string.notice), "{fa-bullhorn}", -1));
                    break;
                case 2:
                    mMenuAdapter.addItem(new MenuItem(2, getString(R.string.client_management), "{fa-link}", -1));
                    break;
                case 3:
                    mMenuAdapter.addItem(new MenuItem(3, getString(R.string.request_list), "{fa-copy}", -1));
                    break;
                case 4:
                    mMenuAdapter.addItem(new MenuItem(4, getString(R.string.process_situation), "{fa-tasks}", -1));
                    break;
                case 5:
                    mMenuAdapter.addItem(new MenuItem(5, getString(R.string.release_management), "{fa-truck}", -1));
                    break;
                case 6:
                    mMenuAdapter.addItem(new MenuItem(6, getString(R.string.material_management), "{fa-cubes}", -1));
                    break;
                case 7:
                    mMenuAdapter.addItem(new MenuItem(7, getString(R.string.gold_management), "", R.drawable.gold));
                    break;
                case 8:
                    mMenuAdapter.addItem(new MenuItem(8, getString(R.string.equipment_management), "{fa-wrench}", -1));
                    break;
                case 9:
                    mMenuAdapter.addItem(new MenuItem(9, getString(R.string.barcode_scan), "{fa-barcode}", -1));
                    break;
                default :
                    break;
            }
        }

//        mMenuAdapter.addItem(new MenuItem(1, getString(R.string.notice), "{fa-bullhorn}", -1));
//        mMenuAdapter.addItem(new MenuItem(2, getString(R.string.client_management), "{fa-link}", -1));
//        mMenuAdapter.addItem(new MenuItem(3, getString(R.string.request_list), "{fa-copy}", -1));
//        mMenuAdapter.addItem(new MenuItem(4, getString(R.string.process_situation), "{fa-tasks}", -1));
//        mMenuAdapter.addItem(new MenuItem(5, getString(R.string.release_management), "{fa-truck}", -1));
//        mMenuAdapter.addItem(new MenuItem(6, getString(R.string.material_management), "{fa-cubes}", -1));
//        mMenuAdapter.addItem(new MenuItem(7, getString(R.string.gold_management), "", R.drawable.gold));
//        mMenuAdapter.addItem(new MenuItem(8, getString(R.string.equipment_management), "{fa-wrench}", -1));
//        mMenuAdapter.addItem(new MenuItem(9, getString(R.string.barcode_scan), "{fa-barcode}", -1));

        Collections.sort(mMenuAdapter.getMenuListItems());
        mMenuAdapter.setOnItemClickListener(pos -> {menuItemClick(pos);});
        mBinding.gridMenu.setAdapter(mMenuAdapter);
        int spanCount = 1;
        if (mMenuAdapter.getItemCount() > 6)
            spanCount = 3;
        else if (mMenuAdapter.getItemCount() > 3)
            spanCount = 2;

        mBinding.gridMenu.setLayoutManager(new GridLayoutManager(MenuActivity2.this, spanCount));
        alarmClickMenu();
    }

    private void alarmClickMenu() {
        Intent intent = getIntent();
        if (intent.getStringExtra("type") == null)
            return;

        String alarmType = intent.getStringExtra("type");
        switch (alarmType) {
            case "notice_imp" :
                menuItemClick(0);
                break;
            case "client_ref" :
                menuItemClick(1);
                break;
            case "request" :
                menuItemClick(2);
                break;
            case "material_low" :
                menuItemClick(5);
                break;
            case "equip_err" :
                menuItemClick(7);
                break;
            default :
                break;
        }
    }

    private void getShutdownCheck() {
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
                            if (!MenuActivity2.this.isFinishing())
                                CommonClass.showDialog(MenuActivity2.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!MenuActivity2.this.isFinishing())
                            CommonClass.showDialog(MenuActivity2.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!MenuActivity2.this.isFinishing())
                            CommonClass.showDialog(MenuActivity2.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!MenuActivity2.this.isFinishing())
                        CommonClass.showDialog(MenuActivity2.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!MenuActivity2.this.isFinishing())
                    CommonClass.showDialog(MenuActivity2.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
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