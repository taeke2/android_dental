package gbsoft.com.dental_gb;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemGoldBinding;

public class GoldAdapter extends RecyclerView.Adapter<GoldAdapter.GoldViewHolder> {
    private ArrayList<GoldListItem> mGoldListItems;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public GoldAdapter(ArrayList<GoldListItem> goldListItems) {
        this.mGoldListItems = goldListItems;
    }

    public class GoldViewHolder extends RecyclerView.ViewHolder{
        ItemGoldBinding mBinding;

        public GoldViewHolder(@NonNull ItemGoldBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

            binding.layoutItem.setOnClickListener(v -> {
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
    public GoldAdapter.GoldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoldAdapter.GoldViewHolder(ItemGoldBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GoldAdapter.GoldViewHolder holder, int position) {
        ItemGoldBinding binding = holder.mBinding;
        binding.itemClientName.setText(mGoldListItems.get(position).getClientName());
        binding.itemStockQuantity.setText(mGoldListItems.get(position).getStockQuantity());
    }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public int getItemCount() {
        return mGoldListItems.size();
    }
}
