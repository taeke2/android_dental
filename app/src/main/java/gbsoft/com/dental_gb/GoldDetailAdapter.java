package gbsoft.com.dental_gb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemGoldDetailBinding;

public class GoldDetailAdapter extends RecyclerView.Adapter<GoldDetailAdapter.GoldInoutViewHolder> {
    private Context mContext;
    private ArrayList<GoldListItem> mGoldListItems;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public GoldDetailAdapter(Context context, ArrayList<GoldListItem> goldListItems) {
        this.mContext = context;
        this.mGoldListItems = goldListItems;
    }

    public class GoldInoutViewHolder extends RecyclerView.ViewHolder {
        ItemGoldDetailBinding binding;

        public GoldInoutViewHolder(@NonNull ItemGoldDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

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
    public GoldDetailAdapter.GoldInoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoldDetailAdapter.GoldInoutViewHolder(ItemGoldDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GoldDetailAdapter.GoldInoutViewHolder holder, int position) {
        ItemGoldDetailBinding binding = holder.binding;

        binding.itemGold.setText(mGoldListItems.get(position).getGoldCode());
        binding.txtInDate.setText(mGoldListItems.get(position).getGoldDate());

        if (mGoldListItems.get(position).getInQuantity().equals("0")) {
//            layout_gold.setBackgroundColor(Color.rgb(224, 224, 224));
            binding.itemLayoutGold.setBackground(mContext.getDrawable(R.drawable.background_shape2));
            binding.itemGoldQuantity1.setVisibility(View.VISIBLE);
            binding.itemGoldQuantity2.setVisibility(View.VISIBLE);

            binding.itemGoldQuantity1.setText(mGoldListItems.get(position).getOutQuantity());
            binding.itemGoldQuantity2.setText(mGoldListItems.get(position).getUseQuantity());
            binding.itemGoldQuantity2.setBackground(mContext.getDrawable(R.drawable.text_background3));
        } else {
//            layout_gold.setBackgroundColor(Color.rgb(255, 255, 255));
            binding.itemLayoutGold.setBackground(mContext.getDrawable(R.drawable.dialog_background_shape));

            binding.itemGoldQuantity1.setVisibility(View.GONE);
            binding.itemGoldQuantity2.setVisibility(View.VISIBLE);

            binding.itemGoldQuantity2.setText(mGoldListItems.get(position).getInQuantity());

            binding.itemGoldQuantity2.setBackground(mContext.getDrawable(R.drawable.text_background3));
        }
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
