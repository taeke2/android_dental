package gbsoft.com.dental_gb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemRequestDetailPartnersBinding;

public class RequestDetailAdapter_Partners extends BaseAdapter {
    ArrayList<RequestDetailItem> mRequestDetailItems;

    public RequestDetailAdapter_Partners(ArrayList<RequestDetailItem> requestDetailItems) {
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
            ItemRequestDetailPartnersBinding binding = ItemRequestDetailPartnersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            holder = new RequestDetailViewHolder(binding);
            holder.mView.setTag(holder);
        } else {
            holder = (RequestDetailViewHolder) convertView.getTag();
        }

//        LinearLayout layout_type = (LinearLayout) convertView.findViewById(R.id.item_layout_13);

        ArrayList<TextView> shades = new ArrayList<>();
        shades.add(holder.mBinding.txtShade1);
        shades.add(holder.mBinding.txtShade2);
        shades.add(holder.mBinding.txtShade3);
        shades.add(holder.mBinding.txtShade4);
        shades.add(holder.mBinding.txtShade5);
        shades.add(holder.mBinding.txtShade6);
        shades.add(holder.mBinding.txtShade7);
        shades.add(holder.mBinding.txtShade8);
        shades.add(holder.mBinding.txtShade9);

        holder.mBinding.txtProductPart.setText(mRequestDetailItems.get(position).getProductPart());
        holder.mBinding.txtProductName.setText(mRequestDetailItems.get(position).getProductName());

        String dentalFormulaStr = mRequestDetailItems.get(position).getDentalFormula();
        String[] dentalFormula = dentalFormulaStr.split("/");
        String[] formula = {"", "", "", ""};
        for (int i = 0; i < dentalFormula.length; i++) {
            formula[i] = dentalFormula[i];
        }

        String str_shade = mRequestDetailItems.get(position).getShade();
        if (str_shade.equals("null") || str_shade.equals(";;;;;;;;")) {
            int i = 0;
            for (TextView shade : shades) {
                shade.setText("");
                i++;
            }
        } else {
            String[] arrShade = str_shade.split(";");
            int i = 0;
            for (TextView shade : shades) {
                shade.setText(arrShade[i]);
                i++;
            }
        }

        holder.mBinding.txtDentalFormula10.setText("  " + formula[0] + "  ");
        holder.mBinding.txtDentalFormula20.setText("  " + formula[1] + "  ");
        holder.mBinding.txtDentalFormula30.setText("  " + formula[2] + "  ");
        holder.mBinding.txtDentalFormula40.setText("  " + formula[3] + "  ");

        holder.mBinding.txtImplantCrown.setText(mRequestDetailItems.get(position).getImplantCrown());
        holder.mBinding.txtSpaceLack.setText(mRequestDetailItems.get(position).getSpaceLack());
        holder.mBinding.txtPonticDesign.setText(mRequestDetailItems.get(position).getPonticDesign());
        holder.mBinding.txtMetalDesign.setText(mRequestDetailItems.get(position).getMetalDesign());

        holder.mBinding.txtFullDenture.setText(mRequestDetailItems.get(position).getFullDenture());
        holder.mBinding.txtFlexibleDenture.setText(mRequestDetailItems.get(position).getFlexibleDenture());
        holder.mBinding.txtJaw.setText(mRequestDetailItems.get(position).getAg());
        holder.mBinding.txtPartialDenture.setText(mRequestDetailItems.get(position).getPartialDenture());
        holder.mBinding.txtDentureExtra.setText(mRequestDetailItems.get(position).getDentureExtra());

        holder.mBinding.txtMemo.setText(mRequestDetailItems.get(position).getMemo());
        holder.mBinding.txtType.setText(mRequestDetailItems.get(position).getDivisions());

        if (holder.mBinding.txtProductPart.getText().toString().equals("Crown")) {
            holder.mBinding.itemLayout01.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout02.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout03.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout04.setVisibility(View.GONE);
            holder.mBinding.itemLayout05.setVisibility(View.GONE);
            holder.mBinding.itemLayout12.setVisibility(View.GONE);

            holder.mBinding.itemLayout06.setVisibility(View.GONE);
            holder.mBinding.itemLayout07.setVisibility(View.GONE);
            holder.mBinding.itemLayout08.setVisibility(View.GONE);
            holder.mBinding.itemLayout09.setVisibility(View.GONE);
            holder.mBinding.itemLayout10.setVisibility(View.GONE);

            holder.mBinding.itemLayout11.setVisibility(View.VISIBLE);
        }

        if (holder.mBinding.txtProductPart.getText().toString().equals("Porcelain")) {
            holder.mBinding.itemLayout01.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout02.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout03.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout04.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout05.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout12.setVisibility(View.VISIBLE);

            holder.mBinding.itemLayout06.setVisibility(View.GONE);
            holder.mBinding.itemLayout07.setVisibility(View.GONE);
            holder.mBinding.itemLayout08.setVisibility(View.GONE);
            holder.mBinding.itemLayout09.setVisibility(View.GONE);
            holder.mBinding.itemLayout10.setVisibility(View.GONE);

            holder.mBinding.itemLayout11.setVisibility(View.VISIBLE);
        }

        if (holder.mBinding.txtProductPart.getText().toString().equals("Denture")) {
            holder.mBinding.itemLayout01.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout02.setVisibility(View.GONE);
            holder.mBinding.itemLayout03.setVisibility(View.GONE);
            holder.mBinding.itemLayout04.setVisibility(View.GONE);
            holder.mBinding.itemLayout05.setVisibility(View.GONE);
            holder.mBinding.itemLayout12.setVisibility(View.GONE);

            holder.mBinding.itemLayout06.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout07.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout08.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout09.setVisibility(View.VISIBLE);
            holder.mBinding.itemLayout10.setVisibility(View.VISIBLE);

            holder.mBinding.itemLayout11.setVisibility(View.VISIBLE);
        }

        return holder.mView;
    }

    private class RequestDetailViewHolder {
        private View mView;
        private ItemRequestDetailPartnersBinding mBinding;

        RequestDetailViewHolder(ItemRequestDetailPartnersBinding binding) {
            this.mView = binding.getRoot();
            this.mBinding = binding;
        }
    }
}
