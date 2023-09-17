package gbsoft.com.dental_gb;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemGoldOutRequestBinding;

public class GoldOutRequestAdapter extends RecyclerView.Adapter<GoldOutRequestAdapter.GoldRequestViewHolder> {
    ArrayList<GoldListItem> mGoldListItems;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public GoldOutRequestAdapter(ArrayList<GoldListItem> goldListItems) {
        this.mGoldListItems = goldListItems;
    }

    public class GoldRequestViewHolder extends RecyclerView.ViewHolder {
        ItemGoldOutRequestBinding mBinding;

        public GoldRequestViewHolder(@NonNull ItemGoldOutRequestBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

            binding.itemLayoutGold.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @NonNull
    @Override
    public GoldOutRequestAdapter.GoldRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoldOutRequestAdapter.GoldRequestViewHolder(ItemGoldOutRequestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GoldOutRequestAdapter.GoldRequestViewHolder holder, int position) {
        ItemGoldOutRequestBinding binding = holder.mBinding;

        binding.itemClientName.setText(mGoldListItems.get(position).getClientName());
        binding.itemPatientName.setText(mGoldListItems.get(position).getPatientName());
        binding.itemOrderDate.setText(mGoldListItems.get(position).getOrderDate());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mGoldListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}