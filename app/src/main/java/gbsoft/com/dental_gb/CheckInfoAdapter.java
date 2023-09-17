package gbsoft.com.dental_gb;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ActivityCheckInfoBinding;

public class CheckInfoAdapter extends RecyclerView.Adapter<CheckInfoAdapter.ViewHolder> {
    ArrayList<CheckInfoItem> mList = new ArrayList<>();

    public CheckInfoAdapter(ArrayList<CheckInfoItem> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ActivityCheckInfoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CheckInfoAdapter.ViewHolder holder, int position) {
        ActivityCheckInfoBinding binding = holder.binding;
        CheckInfoItem item = mList.get(position);

        binding.txtChkInfo.setText(item.getChkInfo());
        binding.txtProcessName.setText(item.getProcessName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ActivityCheckInfoBinding binding;

        public ViewHolder(@NonNull ActivityCheckInfoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
