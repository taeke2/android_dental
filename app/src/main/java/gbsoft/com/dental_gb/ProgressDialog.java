package gbsoft.com.dental_gb;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

public class ProgressDialog extends Dialog {
    private static ProgressDialog mProgressDialog;

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    public static ProgressDialog getInstance(Context context) {
        mProgressDialog = new ProgressDialog(context);
        return mProgressDialog;
    }

    public void showProgressDialog() {
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.setCanceledOnTouchOutside(false); // 다이얼로그 밖에 터치 시 종료
        mProgressDialog.setCancelable(false); // 다이얼로그 취소 가능 (back key)
        mProgressDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
