package gbsoft.com.dental_gb;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gbsoft.com.dental_gb.databinding.ItemGold2Binding;
import gbsoft.com.dental_gb.databinding.ItemGoldInoutHistoryBinding;

public class GoldInOutputHistoryAdapter extends RecyclerView.Adapter<GoldInOutputHistoryAdapter.MyViewHolder> {

    private GoldDTO item;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private GoldInOutputHistoryAdapter.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(GoldInOutputHistoryAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public GoldInOutputHistoryAdapter(int index) {
        this.item = CommonClass.sGoldDTO.get(index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemGoldInoutHistoryBinding mBinding;

        public MyViewHolder(ItemGoldInoutHistoryBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

            mBinding.layoutItem.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(position);
            });
        }
    }

    @NonNull
    @Override
    public GoldInOutputHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoldInOutputHistoryAdapter.MyViewHolder(ItemGoldInoutHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GoldInOutputHistoryAdapter.MyViewHolder holder, int position) {
        // TODO: 해당 아이템으로 수정
        GoldDTO.GoldInOutputHistoryDTO goldInOutputHistoryDTO = item.getHistoryDTOS().get(position);

        holder.mBinding.txtDate.setText(goldInOutputHistoryDTO.getDate());
        holder.mBinding.txtEquipmentName.setText(goldInOutputHistoryDTO.getHistory());
        holder.mBinding.txtConsumption.setText(Double.toString(goldInOutputHistoryDTO.getConsumption()));
        holder.mBinding.txtGoldStock.setText(Double.toString(goldInOutputHistoryDTO.getStockGold()));

    }

    @Override
    public int getItemCount() {
        // TODO: 해당 아이템으로 수정
        return item.getHistoryDTOS().size();
    }

    // 리사이클러 뷰 스크롤하고 다시 올리면 뷰가 초기화 되는데, 이 메소드로 뷰를 유지시킨다.
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
