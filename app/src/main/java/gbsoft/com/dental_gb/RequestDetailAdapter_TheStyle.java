package gbsoft.com.dental_gb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemRequestDetailThestyleBinding;

public class RequestDetailAdapter_TheStyle extends BaseAdapter {
    ArrayList<RequestDetailItem> mRequestDetailItems;

    public RequestDetailAdapter_TheStyle(ArrayList<RequestDetailItem> requestDetailItems) {
        this.mRequestDetailItems = requestDetailItems;
    }

    @Override
    public int getCount() {
        return mRequestDetailItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mRequestDetailItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RequestDetailViewHolder holder;

        if (convertView == null) {
            ItemRequestDetailThestyleBinding binding = ItemRequestDetailThestyleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            holder = new RequestDetailViewHolder(binding);
            holder.mView.setTag(holder);
        } else {
            holder = (RequestDetailViewHolder) convertView.getTag();
        }

        holder.mBinding.txtProductName.setText(mRequestDetailItems.get(position).getProductName());
        holder.mBinding.txtProductPart.setText(mRequestDetailItems.get(position).getProductPart());

        String dentalFormulaStr = mRequestDetailItems.get(position).getDentalFormula();
        String[] dentalFormula = dentalFormulaStr.split("&");
        String[] formula = {"", "", "", ""};

        for (int i = 0; i < dentalFormula.length; i++) {
            formula[i] = dentalFormula[i];
        }

        holder.mBinding.txtDentalFormula10.setText("  " + formula[0] + "  ");
        holder.mBinding.txtDentalFormula20.setText("  " + formula[1] + "  ");
        holder.mBinding.txtDentalFormula30.setText("  " + formula[2] + "  ");
        holder.mBinding.txtDentalFormula40.setText("  " + formula[3] + "  ");

        holder.mBinding.txtMemo.setText(mRequestDetailItems.get(position).getMemo());
        holder.mBinding.txtType.setText(mRequestDetailItems.get(position).getDivisions());

        return holder.mView;
    }

    private class RequestDetailViewHolder {
        private View mView;
        private ItemRequestDetailThestyleBinding mBinding;

        RequestDetailViewHolder(ItemRequestDetailThestyleBinding binding) {
            this.mView = binding.getRoot();
            this.mBinding = binding;
        }
    }
}
