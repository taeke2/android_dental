package gbsoft.com.dental_gb;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ItemGold2Binding;
import gbsoft.com.dental_gb.databinding.ItemPlantListBinding;

public class GoldAdapter2 extends RecyclerView.Adapter<GoldAdapter2.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private GoldAdapter2.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(GoldAdapter2.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemGold2Binding mBinding;

        public MyViewHolder(ItemGold2Binding binding) {
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
    public GoldAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoldAdapter2.MyViewHolder(ItemGold2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GoldAdapter2.MyViewHolder holder, int position) {
        // TODO: 해당 아이템으로 수정
        GoldDTO item = CommonClass.sGoldDTO.get(position);

        holder.mBinding.txtClientName.setText(item.getClientName());
        holder.mBinding.txtGoldName.setText(item.getGoldName());
        holder.mBinding.txtGoldCount.setText(Double.toString(item.getStockGold()));
    }

    @Override
    public int getItemCount() {
        // TODO: 해당 아이템으로 수정
        return CommonClass.sGoldDTO.size();
    }

    // 리사이클러 뷰 스크롤하고 다시 올리면 뷰가 초기화 되는데, 이 메소드로 뷰를 유지시킨다.
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
