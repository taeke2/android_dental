package gbsoft.com.dental_gb;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemReleaseDetailBinding;

public class ReleaseDetailAdapter extends RecyclerView.Adapter<ReleaseDetailAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemReleaseDetailBinding mBinding;

        public ViewHolder(@NonNull ItemReleaseDetailBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }

    private ArrayList<ReleaseDetailItem> mList = null;

    private int mCode;

    public ReleaseDetailAdapter(ArrayList<ReleaseDetailItem> list, int code) {
        this.mList = list;
        this.mCode = code;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemReleaseDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemReleaseDetailBinding binding = holder.mBinding;

        ReleaseDetailItem item = mList.get(position);

        String productName = item.getProductName();
        String deadDateTime = item.getDeadlineDateTime();
        String orderFinishTime = item.getOrderFinishTime();
        String dental = item.getDentalFormula();

        String[] dents = (mCode == CommonClass.PDL) ? dental.split("/", 4) : dental.split("&", 4);
        String[] dentals = {"", "", "", ""};

        for (int i = 0; i < dents.length; i++) {
            dentals[i] = dents[i];
        }

        binding.txtProductName.setText(productName);
        binding.txtDeadlineDateTime.setText(deadDateTime);
        binding.txtOrderFinishTime.setText(orderFinishTime.equals("null") ? "-" : orderFinishTime);
        binding.txtDent1.setText(dentals[0]);
        binding.txtDent2.setText(dentals[1]);
        binding.txtDent3.setText(dentals[2]);
        binding.txtDent4.setText(dentals[3]);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
