package gbsoft.com.dental_gb.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import gbsoft.com.dental_gb.CommonClass;
import gbsoft.com.dental_gb.R;
import gbsoft.com.dental_gb.databinding.DialogSpinnerBinding;

public class SpinnerDialog extends Dialog {
    private DialogSpinnerBinding mBinding;
    private SpinnerDialog mDialog;
    private SpinnerDialogClickListener mSpinnerDialogClickListener;
    private String mContent;
    private String[] mSpinnerValues;

    public SpinnerDialog(@NonNull Context context, SpinnerDialogClickListener spinnerDialogClickListener, String content, String[] spinnerValues) {
        super(context);
        this.mSpinnerDialogClickListener = spinnerDialogClickListener;
        this.mContent = content;
        this.mSpinnerValues = spinnerValues;
        this.mDialog = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DialogSpinnerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.txtContent.setText(mContent);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, mSpinnerValues);
        mBinding.spinnerArti.setAdapter(adapter);
        mBinding.spinnerArti.setSelection(0);

        mBinding.btnOk.setOnClickListener(v -> {
            mSpinnerDialogClickListener.onPositiveClick((int) mBinding.spinnerArti.getSelectedItemId(), mDialog);
            // dismiss();
        });

        mBinding.btnCancel.setOnClickListener(v -> dismiss());
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
