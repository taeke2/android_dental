package gbsoft.com.dental_gb.adapter;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gbsoft.com.dental_gb.CommonClass;
import gbsoft.com.dental_gb.databinding.ItemAlarmBinding;
import gbsoft.com.dental_gb.dto.AlarmDTO;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmHolder> {

    public class AlarmHolder extends RecyclerView.ViewHolder {
        private ItemAlarmBinding binding;
        public AlarmHolder(@NonNull ItemAlarmBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public AlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlarmAdapter.AlarmHolder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmHolder holder, int position) {
        AlarmDTO item = CommonClass.sAlarmDTO.get(position);

        holder.binding.txtTitle.setText(item.getTitle());
        holder.binding.txtContent.setText(item.getContent());
        holder.binding.txtDate.setText(item.getTime());
        if (item.getIsChecked())
            holder.binding.dotNew.setVisibility(View.INVISIBLE);
        else
            holder.binding.dotNew.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return CommonClass.sAlarmDTO.size();
    }
}
