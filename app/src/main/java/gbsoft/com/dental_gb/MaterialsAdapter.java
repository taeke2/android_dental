package gbsoft.com.dental_gb;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import gbsoft.com.dental_gb.databinding.ItemMaterialsBinding;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.MaterialsViewHolder> {
    ArrayList<MaterialsListItem> mMaterialsListItems;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public MaterialsAdapter(ArrayList<MaterialsListItem> mMaterialsListItems) {
        this.mMaterialsListItems = mMaterialsListItems;
    }

    public class MaterialsViewHolder extends RecyclerView.ViewHolder {
        private ItemMaterialsBinding mBinding;
        public MaterialsViewHolder(@NonNull ItemMaterialsBinding binding) {
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
    public MaterialsAdapter.MaterialsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MaterialsAdapter.MaterialsViewHolder(ItemMaterialsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialsAdapter.MaterialsViewHolder holder, int position) {
        ItemMaterialsBinding binding = holder.mBinding;

        binding.txtClientName.setText(mMaterialsListItems.get(position).getClientName());
        binding.txtMaterialsName.setText(mMaterialsListItems.get(position).getMaterialsName());
        binding.txtInDate.setText(mMaterialsListItems.get(position).getInDate());
        binding.txtQuantity.setText(mMaterialsListItems.get(position).getQuantity());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mMaterialsListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
