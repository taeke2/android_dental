package gbsoft.com.dental_gb.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import gbsoft.com.dental_gb.ProcessBarcodeActivity;
import gbsoft.com.dental_gb.adapter.ProcessBarcodeAdapter;
import gbsoft.com.dental_gb.R;
import gbsoft.com.dental_gb.databinding.DialogProcessBarcodeBinding;

public class ProcessBarcodeDialog extends BottomSheetDialogFragment {
    private DialogProcessBarcodeBinding mBinding;
    private Context mContext;
    private ProcessBarcodeDialogClickListener mDialogClickListener;
    private ArrayList<String> mBarcodeList;
    private boolean mIsReturn = true;
    private int mType;
    private int mArtiBox = -1;   // 교합기 사용 박스 번호
    private int mArtiIdx = -1;   // 교합기 번호 (ex 교합기1의 idx)
    private ProcessBarcodeDialog mDialog;

    public ProcessBarcodeDialog(Context context,
                                ProcessBarcodeDialogClickListener dialogClickListener,
                                ArrayList<String> barcodeList,
                                int dialogType) {
        this.mContext = context;
        this.mDialogClickListener = dialogClickListener;
        this.mBarcodeList = barcodeList;
        this.mType = dialogType;
        this.mDialog = this;
        // mType 0 : default
        //       1 : singleBarcodeScan
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(ProcessBarcodeDialog.STYLE_NORMAL, getTheme());
        mBinding = DialogProcessBarcodeBinding.inflate(inflater, container, false);

        ProcessBarcodeAdapter adapter = new ProcessBarcodeAdapter(mContext, mBarcodeList);
        adapter.setOnItemLongClickListener(itemLongClickListener);
        adapter.setOnSelectedDeleteListener(itemDeleteClickListener);
        adapter.notifyDataSetChanged();
        mBinding.barcodeRecycler.setAdapter(adapter);
        mBinding.barcodeRecycler.setLayoutManager(new LinearLayoutManager(mContext));

        //  박스번호 한개만 등록
        if (mType == 1) {
            mBinding.btnStart.setText(mContext.getString(R.string.box_scan));
            mBinding.btnFinish.setVisibility(View.GONE);
        }

        mBinding.btnStart.setOnClickListener(v -> {
            mDialogClickListener.onStartClick(mArtiBox, mArtiIdx, mDialog);
            mIsReturn = false;
//            dismiss();
        });

        mBinding.btnFinish.setOnClickListener(v -> {
            mDialogClickListener.onFinishClick(mArtiBox, mArtiIdx, mDialog);
            mIsReturn = false;
//            dismiss();
        });

        return mBinding.getRoot();
    }

    private ProcessBarcodeAdapter.OnItemLongClickListener itemLongClickListener = (pos, selectedArti) -> {
        mArtiBox = pos;
        mArtiIdx = selectedArti;
    };

    private ProcessBarcodeAdapter.OnSelectedItemDeleteListener itemDeleteClickListener = () -> {
        mArtiBox = -1;
        mArtiIdx = -1;
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mIsReturn)
            ((ProcessBarcodeActivity) mContext).barcodeResume();
    }
}
