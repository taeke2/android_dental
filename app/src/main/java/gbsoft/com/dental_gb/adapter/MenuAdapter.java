package gbsoft.com.dental_gb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemMenuBinding;
import gbsoft.com.dental_gb.dto.MenuItem;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private ArrayList<MenuItem> mMenuListItems = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ItemMenuBinding mBinding;

        public MenuViewHolder(@NonNull ItemMenuBinding binding) {
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
    public MenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuAdapter.MenuViewHolder(ItemMenuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.MenuViewHolder holder, int position) {
        Iconify.with(new FontAwesomeModule());
        ItemMenuBinding binding = holder.mBinding;

        if (mMenuListItems.get(position).getDrawable() < 0) {
            binding.imgMenu.setVisibility(View.GONE);
            binding.iconMenu.setVisibility(View.VISIBLE);
            binding.iconMenu.setText(mMenuListItems.get(position).getMenuIcon());
        } else {
            binding.imgMenu.setVisibility(View.VISIBLE);
            binding.iconMenu.setVisibility(View.GONE);
            binding.imgMenu.setImageResource(mMenuListItems.get(position).getDrawable());
        }
        binding.txtMenu.setText(mMenuListItems.get(position).getMenuTxt());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mMenuListItems.size();
    }

    public void addItem(MenuItem menuItem) {
        mMenuListItems.add(menuItem);
    }

    public MenuItem getItem(int pos) {
        return mMenuListItems.get(pos);
    }

    public void clearItems() {
        mMenuListItems.clear();
    }

    public ArrayList<MenuItem> getMenuListItems() {
        return mMenuListItems;
    }
}
