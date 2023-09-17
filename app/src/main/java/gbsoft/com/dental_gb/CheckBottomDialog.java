package gbsoft.com.dental_gb;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import gbsoft.com.dental_gb.databinding.DialogCheckBottomBinding;


public class CheckBottomDialog extends BottomSheetDialogFragment {
    private DialogCheckBottomBinding mBinding;
    private CheckBottomDialog mCheckBottomDialog;
    private Context mContext;
    private static CheckDialogClickListener mCheckDialogClickListener;
    private String mTitle;
    private String mContent;
    private boolean mIsCancelOn;

    public CheckBottomDialog() {
    }

    public CheckBottomDialog(Context context,
                             CheckDialogClickListener checkDialogClickListener,
                             String title,
                             String content,
                             boolean isCancelOn) {
        this.mContext = context;
        this.mCheckDialogClickListener = checkDialogClickListener;
        this.mTitle = title;
        this.mContent = content;
        this.mIsCancelOn = isCancelOn;
        this.mCheckBottomDialog = this;
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setStyle(CheckBottomDialog.STYLE_NORMAL, getTheme());
        mBinding = DialogCheckBottomBinding.inflate(inflater, container, false);

        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString("title");
            mContent = savedInstanceState.getString("content");
            mIsCancelOn = savedInstanceState.getBoolean("isCancelOn");
        }


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

        return mBinding.getRoot();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("title", mTitle);
        outState.putString("content", mContent);
        outState.putBoolean("isCancelOn", mIsCancelOn);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}