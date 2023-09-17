package gbsoft.com.dental_gb;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemReleaseBinding;

public class ReleaseAdapter extends RecyclerView.Adapter<ReleaseAdapter.ReleaseViewHolder> {
    private Context mContext;
    private ArrayList<ProcessRequestReleaseListItem> mItems;
    private boolean mIsReleased;

    public ReleaseAdapter(Context context, ArrayList<ProcessRequestReleaseListItem> items, boolean isReleased) {
        this.mContext = context;
        this.mItems = items;
        this.mIsReleased = isReleased;
    }

    public interface OnLongItemClickListener {
        void onLongItemClick(int pos);
    }

    private OnLongItemClickListener onLongItemClickListener = null;

    public void setOnLongItemClickListener(OnLongItemClickListener listener) {
        this.onLongItemClickListener = listener;
    }

    public void setItems(ArrayList<ProcessRequestReleaseListItem> items) {
        this.mItems.clear();
        this.mItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReleaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReleaseViewHolder(ItemReleaseBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReleaseViewHolder holder, int position) {
        holder.bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ReleaseViewHolder extends RecyclerView.ViewHolder {
        ItemReleaseBinding mBinding;

        public ReleaseViewHolder(@NonNull ItemReleaseBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

            binding.layoutItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (onLongItemClickListener != null) {
                            onLongItemClickListener.onLongItemClick(position);
                            return true;
                        }
                    }
                    return false;
                }
            });

        }

        void bind(final ProcessRequestReleaseListItem item) {
            mBinding.itemClientName.setText(item.getClientName());
            mBinding.itemPatientName.setText(item.getPatientName());
            mBinding.itemProductName.setText(item.getProductName());
            mBinding.itemOrderDate.setText(item.getOrderDate() + " ~ ");
            mBinding.itemDeadlineDate.setText(item.getDeadlineDate() + " | ");
            String outDate = item.getOutDate();
            mBinding.itemOutDate.setText(outDate.equals("null") ? "출고 전" : outDate);
            mBinding.imgCheck.setVisibility(View.GONE);
            mBinding.imgCheckBefore.setVisibility(mIsReleased ? View.VISIBLE : View.GONE);

            mBinding.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsReleased) {
                        if (outDate.equals("null")) {
                            item.setChecked(!item.isChecked());
                            mBinding.imgCheck.setVisibility(item.isChecked() ? View.VISIBLE : View.GONE);
                            mBinding.imgCheckBefore.setVisibility(item.isChecked() ? View.GONE : View.VISIBLE);
                        } else {
                            CommonClass.showDialog(mContext, "출고 선택", "이미 출고된 의뢰 제품입니다.", () -> {}, false);
//                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                            builder.setMessage("이미 출고된 의뢰 제품입니다.");
//                            builder.setTitle("출고 선택").setCancelable(false)
//                                    .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.cancel();
//                                        }
//                                    }).setCancelable(false);
//                            AlertDialog alert = builder.create();
//                            alert.show();
                        }

                    } else {
                        Intent intent = new Intent(mContext, ReleaseDetailActivity.class);
                        intent.putExtra("reqNum", item.getRequestCode());
                        intent.putExtra("outDate", item.getOutDate());
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}