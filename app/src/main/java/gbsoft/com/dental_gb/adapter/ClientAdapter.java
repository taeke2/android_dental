package gbsoft.com.dental_gb.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import gbsoft.com.dental_gb.dto.ClientDTO;
import gbsoft.com.dental_gb.databinding.ItemClientBinding;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.MyViewHolder> {
    private ArrayList<ClientDTO> mClientList = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemClientBinding mBinding;

        public MyViewHolder(ItemClientBinding binding) {
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClientAdapter.MyViewHolder(ItemClientBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ClientDTO item = mClientList.get(position);

        holder.mBinding.txtName.setText(item.getName());
        holder.mBinding.txtType.setText(item.getType());
    }

    @Override
    public int getItemCount() {
        return mClientList.size();
    }

    // 리사이클러 뷰 스크롤하고 다시 올리면 뷰가 초기화 되는데, 이 메소드로 뷰를 유지시킨다.
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addItem(ClientDTO item) {
        mClientList.add(item);
    }

    public void clearItem() {
        mClientList.clear();
    }

    public ClientDTO getItem(int pos) {
        return mClientList.get(pos);
    }
}

