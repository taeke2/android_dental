package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import gbsoft.com.dental_gb.databinding.ActivityClientDetailBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientDetailActivity extends AppCompatActivity {
    private ActivityClientDetailBinding mBinding;
    private int mClientCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityClientDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void initialSet() {
        Intent intent = getIntent();
        mClientCode = intent.getIntExtra("code", -1);

        mBinding.txtDelete.setVisibility(View.GONE);

        mBinding.txtName.setText(intent.getStringExtra("name"));
        mBinding.txtType.setText(intent.getStringExtra("type"));
        mBinding.txtTel.setText(intent.getStringExtra("tel"));
        mBinding.txtFax.setText(intent.getStringExtra("fax"));
        mBinding.txtAddress.setText(intent.getStringExtra("address"));
        mBinding.txtRepresentative.setText(intent.getStringExtra("representative"));
        mBinding.txtEmail.setText(intent.getStringExtra("email"));
        mBinding.txtRef.setText(intent.getStringExtra("refer"));

        mBinding.txtTel.setOnClickListener(v -> {
            Intent intentTel = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + mBinding.txtTel.getText().toString()));
            intentTel.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intentTel);
        });

        mBinding.txtAddress.setOnLongClickListener(addressClick);
        mBinding.txtEmail.setOnLongClickListener(emailClick);

        mBinding.btnBack.setOnClickListener(v -> finish());
//        mBinding.txtDelete.setOnClickListener(v -> {
//            CheckBottomDialog dialog = new CheckBottomDialog(ClientDetailActivity.this, deleteClient, getString(R.string.delete),
//                    getString(R.string.client_delete),false);
//            dialog.show(getSupportFragmentManager(), dialog.getTag());
//        });
    }


    private View.OnLongClickListener addressClick = v -> {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("address", mBinding.txtAddress.getText());
        clipboardManager.setPrimaryClip(clipData);
        return false;
    };

    private View.OnLongClickListener emailClick = v -> {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("email", mBinding.txtEmail.getText());
        clipboardManager.setPrimaryClip(clipData);
        return false;
    };


//    private CheckDialogClickListener deleteClient = () -> {
//        if (CommonClass.sApiService == null) {
//            SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);
//            String serverPath = sharedPreferences.getString("address", "");
//            CommonClass.getApiService(serverPath);
//        }
//
//        Call<ResponseBody> call = CommonClass.sApiService.deleteClient(mClientCode);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                int code = response.code();
//                if (code == 200) {
//                    finish();
//                } else {
//                    if (!ClientDetailActivity.this.isFinishing())
//                        CommonClass.showDialog(ClientDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> {}, false);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                if (!ClientDetailActivity.this.isFinishing())
//                    CommonClass.showDialog(ClientDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> {}, false);
//            }
//        });
//    };
}