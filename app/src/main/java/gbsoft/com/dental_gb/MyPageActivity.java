package gbsoft.com.dental_gb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

import gbsoft.com.dental_gb.databinding.ActivityMyPageBinding;

public class MyPageActivity extends AppCompatActivity {
    private ActivityMyPageBinding mBinding;
    private SharedPreferences.Editor mEditor;
    private String mIp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMyPageBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        mIp = sharedPreferences.getString("ip", "");
        String userName = sharedPreferences.getString("userName", "");

        mBinding.txtLogout.setOnClickListener(logoutClick);
        mBinding.btnBack.setOnClickListener(v -> finish());

        mBinding.txtName1.setText(userName);
        mBinding.txtName2.setText(userName);
        mBinding.txtId.setText(sharedPreferences.getString("id", ""));
        mBinding.txtBirth.setText(sharedPreferences.getString("birth", "-"));
        mBinding.txtEmail.setText(sharedPreferences.getString("email", "-"));
        mBinding.txtTel.setText(sharedPreferences.getString("contact", "-"));
        mBinding.txtDepartment.setText(sharedPreferences.getString("department", "-"));
        mBinding.txtPosition.setText(sharedPreferences.getString("position", "-"));
        mBinding.txtEmpId.setText(sharedPreferences.getString("code", "-"));
        mBinding.txtJoinDate.setText(sharedPreferences.getString("joinDate", "-"));
    }

    View.OnClickListener logoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBottomDialog bottomDialog = new CheckBottomDialog(MyPageActivity.this, () -> {
                FirebaseMessaging.getInstance().deleteToken();
                mEditor.clear();
                mEditor.putString("ip", mIp);
                mEditor.commit();

                Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }, getString(R.string.logout), getString(R.string.logout_ask), true);
            bottomDialog.show(getSupportFragmentManager(), bottomDialog.getTag());
        }
    };

}
