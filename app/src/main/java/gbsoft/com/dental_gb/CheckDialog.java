package gbsoft.com.dental_gb;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import gbsoft.com.dental_gb.databinding.DialogCheckBinding;

public class CheckDialog extends Dialog {

    private DialogCheckBinding mBinding;

    private CheckDialogClickListener mCheckDialogClickListener;
    private String mTitle;
    private String mContent;
    private boolean mIsCancelOn;

    public CheckDialog(@NonNull Context context, CheckDialogClickListener checkDialogClickListener, String title, String content, boolean isCancelOn) {
        super(context);
        this.mCheckDialogClickListener = checkDialogClickListener;
        this.mTitle = title;
        this.mContent = content;
        this.mIsCancelOn = isCancelOn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DialogCheckBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.txtTitle.setText(mTitle);
        mBinding.txtContent.setText(mContent);

        mBinding.btnOk.setOnClickListener(v -> {
            mCheckDialogClickListener.onPositiveClick();
            dismiss();
        });

        if (mIsCancelOn) {
            mBinding.btnCancel.setOnClickListener(v -> dismiss());
            mBinding.btnCancel.setVisibility(View.VISIBLE);
        } else {
            mBinding.btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
