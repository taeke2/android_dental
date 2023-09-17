package gbsoft.com.dental_gb.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gbsoft.com.dental_gb.CommonClass;
import gbsoft.com.dental_gb.ProcessBarcodeActivity;
import gbsoft.com.dental_gb.R;
import gbsoft.com.dental_gb.dialog.SpinnerDialog;
import gbsoft.com.dental_gb.dialog.SpinnerDialogClickListener;
import gbsoft.com.dental_gb.databinding.ItemProcessBarcodeBinding;

public class ProcessBarcodeAdapter extends RecyclerView.Adapter<ProcessBarcodeAdapter.BarcodeViewHolder>{
    private Context mContext;
    private ArrayList<String> mScanBarcodeList;
    private SparseBooleanArray mSelectedItem = new SparseBooleanArray();
    private int mPrePosition = -1;
    private int mPosition = -1;
    private int mArtiIdx = -1;

    public interface OnItemLongClickListener {
        void onItemLongClick(int pos, int selected);
    }

    private ProcessBarcodeAdapter.OnItemLongClickListener onItemLongClickListener = null;

    public void setOnItemLongClickListener(ProcessBarcodeAdapter.OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public interface OnSelectedItemDeleteListener {
        void onSelectedDeleteClick();
    }

    private ProcessBarcodeAdapter.OnSelectedItemDeleteListener onSelectedDeleteListener =  null;

    public void setOnSelectedDeleteListener(ProcessBarcodeAdapter.OnSelectedItemDeleteListener listener) {
        this.onSelectedDeleteListener = listener;
    }

    public ProcessBarcodeAdapter() {
    }

    public ProcessBarcodeAdapter(Context context, ArrayList<String> barcodeList) {
        this.mContext = context;
        this.mScanBarcodeList = barcodeList;
    }

    public class BarcodeViewHolder extends RecyclerView.ViewHolder {
        ItemProcessBarcodeBinding mBinding;
        public BarcodeViewHolder(@NonNull ItemProcessBarcodeBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

            mBinding.btnDelete.setOnClickListener( v -> {
                int pos = getAbsoluteAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (mArtiIdx >= 0) {
                        if (onSelectedDeleteListener != null)
                            onSelectedDeleteListener.onSelectedDeleteClick();
                        mSelectedItem.delete(pos);
                        setArticulatorTxt(mSelectedItem.get(pos), mBinding);
                    }

                    mScanBarcodeList.remove(pos);
                    notifyDataSetChanged();
                }
                if (getItemCount() <= 0)
                    ((ProcessBarcodeActivity) mContext).finish();
            });


        }
    }

    @NonNull
    @Override
    public ProcessBarcodeAdapter.BarcodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BarcodeViewHolder(ItemProcessBarcodeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProcessBarcodeAdapter.BarcodeViewHolder holder, int position) {
        String boxNo = mScanBarcodeList.get(position);

        holder.mBinding.txtBarcode.setText(mContext.getString(R.string.dialog_box_no, Integer.parseInt(boxNo.substring(CommonClass.getDigitIndex(boxNo)))));
        setArticulatorTxt(mSelectedItem.get(position), holder.mBinding);

        holder.mBinding.layoutItem.setOnLongClickListener(v -> {
            mPosition = position;
            if (mSelectedItem.get(mPosition)) {
                if (onItemLongClickListener != null)
                    onItemLongClickListener.onItemLongClick(-1, -1);

                mSelectedItem.delete(mPosition);
                if (mPrePosition != -1) notifyItemChanged(mPrePosition);
                notifyItemChanged(mPosition);
                mPrePosition = mPosition;
            } else {
                if (CommonClass.sArticulatorDTO.size() == 0)
                    Toast.makeText(mContext, mContext.getString(R.string.empty_arti), Toast.LENGTH_SHORT).show();
                else
                    showSpinnerDialog();
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mScanBarcodeList.size();
    }

    private void setArticulatorTxt(boolean isSelected, ItemProcessBarcodeBinding binding) {
        binding.txtBarcode.setTextColor(isSelected ? mContext.getColor(R.color.green) : mContext.getColor(R.color.dark_gray));
        binding.txtArticulator.setText(isSelected ? mContext.getString(R.string.parenthesis, CommonClass.sArticulatorSpinner.get(mArtiIdx)) : "");
        binding.txtArticulator.setVisibility(isSelected ? View.VISIBLE : View.GONE);
    }

    private SpinnerDialogClickListener spinnerDialogClickListener = (selected, dialog) -> {
        mArtiIdx = selected;
        mSelectedItem.delete(mPrePosition);
        if (onItemLongClickListener != null)
            onItemLongClickListener.onItemLongClick(mPosition, selected);

        mSelectedItem.put(mPosition, true);
        if (mPrePosition != -1) notifyItemChanged(mPrePosition);
        notifyItemChanged(mPosition);
        mPrePosition = mPosition;
        dialog.dismiss();
//        if (CommonClass.sArticulatorDTO.get(selected).getUser().equals("")) {
//            mArtiIdx = selected;
//            mSelectedItem.delete(mPrePosition);
//            if (onItemLongClickListener != null)
//                onItemLongClickListener.onItemLongClick(mPosition, selected);
//
//            mSelectedItem.put(mPosition, true);
//            if (mPrePosition != -1) notifyItemChanged(mPrePosition);
//            notifyItemChanged(mPosition);
//            mPrePosition = mPosition;
//            dialog.dismiss();
//        } else {
//            Toast.makeText(mContext, mContext.getString(R.string.arti_using, CommonClass.sArticulatorDTO.get(selected).getUser()), Toast.LENGTH_SHORT).show();
//        }
    };

    private void showSpinnerDialog() {
        SpinnerDialog dialog = new SpinnerDialog(mContext, spinnerDialogClickListener, mContext.getString(R.string.articulator_select),
                CommonClass.sArticulatorSpinner.values().toArray(new String[CommonClass.sArticulatorSpinner.size()]));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false); // 다이얼로그 밖에 터치 시 종료
        dialog.setCancelable(false); // 다이얼로그 취소 가능 (back key)
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 다이얼로그 background 적용하기 위한 코드 (radius 적용)
        dialog.show();
    }
}
