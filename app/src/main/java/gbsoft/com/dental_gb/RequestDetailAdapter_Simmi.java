package gbsoft.com.dental_gb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemRequestDetailSimmiBinding;

class RequestDetailAdapter_Simmi extends BaseAdapter {
    ArrayList<RequestDetailItem> mRequestDetailItems;

    public RequestDetailAdapter_Simmi(ArrayList<RequestDetailItem> requestDetailItems) {
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
            ItemRequestDetailSimmiBinding binding = ItemRequestDetailSimmiBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            holder = new RequestDetailViewHolder(binding);
            holder.mView.setTag(holder);
        } else {
            holder = (RequestDetailViewHolder) convertView.getTag();
        }

        holder.mBinding.itemLayout02.setVisibility(View.VISIBLE);   // dentalFormula
        holder.mBinding.itemLayout02.setVisibility(View.VISIBLE);   // shade

        holder.mBinding.itemLayout18.setVisibility(View.VISIBLE);        // memo
        holder.mBinding.itemLayout19.setVisibility(View.VISIBLE);        // type

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

        holder.mBinding.txtShade.setText(mRequestDetailItems.get(position).getShade());

        if (mRequestDetailItems.get(position).getProductPart().equals("일반보철")) {
            holder.mBinding.itemLayout03.setVisibility(View.VISIBLE);    // implant1
            holder.mBinding.itemLayout04.setVisibility(View.VISIBLE);    // implant2
            holder.mBinding.itemLayout05.setVisibility(View.VISIBLE);    // implatn3
            holder.mBinding.itemLayout06.setVisibility(View.VISIBLE);   // prosthesis1
            holder.mBinding.itemLayout07.setVisibility(View.VISIBLE);   // prosthesis2

            holder.mBinding.itemLayout08.setVisibility(View.GONE);    // denture
            holder.mBinding.itemLayout09.setVisibility(View.GONE);     // tmpDenture
            holder.mBinding.itemLayout10.setVisibility(View.GONE);      // frame
            holder.mBinding.itemLayout11.setVisibility(View.GONE);       // tray
            holder.mBinding.itemLayout12.setVisibility(View.GONE);     // waxrim
            holder.mBinding.itemLayout13.setVisibility(View.GONE);      // array
            holder.mBinding.itemLayout14.setVisibility(View.GONE);        // quering
            holder.mBinding.itemLayout15.setVisibility(View.GONE);       // dentureRepairing
            holder.mBinding.itemLayout16.setVisibility(View.GONE);        // etc
            holder.mBinding.itemLayout17.setVisibility(View.GONE);     // ag

            holder.mBinding.txtImplant1.setText(mRequestDetailItems.get(position).getImplant1());
            holder.mBinding.txtImplant2.setText(mRequestDetailItems.get(position).getImplant2());
            holder.mBinding.txtImplant3.setText(mRequestDetailItems.get(position).getImplant3());
            holder.mBinding.txtProsthesis1.setText(mRequestDetailItems.get(position).getProsthesis1());
            holder.mBinding.txtProsthesis2.setText(mRequestDetailItems.get(position).getProsthesis2());

        } else if (mRequestDetailItems.get(position).getProductPart().equals("덴처")) {
            holder.mBinding.itemLayout03.setVisibility(View.VISIBLE);    // implant1
            holder.mBinding.itemLayout04.setVisibility(View.GONE);   // implant2
            holder.mBinding.itemLayout05.setVisibility(View.GONE);   // implant3
            holder.mBinding.itemLayout06.setVisibility(View.GONE);    // prosthesis1
            holder.mBinding.itemLayout07.setVisibility(View.GONE);      // prosthesis2

            holder.mBinding.itemLayout08.setVisibility(View.VISIBLE); // denture
            holder.mBinding.itemLayout09.setVisibility(View.VISIBLE);      // tmpDenture
            holder.mBinding.itemLayout11.setVisibility(View.VISIBLE);        // tray
            holder.mBinding.itemLayout10.setVisibility(View.VISIBLE);   // frame
            holder.mBinding.itemLayout12.setVisibility(View.VISIBLE);      // waxrim
            holder.mBinding.itemLayout13.setVisibility(View.VISIBLE);       // array
            holder.mBinding.itemLayout14.setVisibility(View.VISIBLE);     // quering
            holder.mBinding.itemLayout15.setVisibility(View.VISIBLE);        // dentureRepairing
            holder.mBinding.itemLayout16.setVisibility(View.VISIBLE);     // etc
            holder.mBinding.itemLayout17.setVisibility(View.VISIBLE);      //ag

            holder.mBinding.txtImplant1.setText(mRequestDetailItems.get(position).getImplant1());
            holder.mBinding.txtDenture.setText(mRequestDetailItems.get(position).getDenture());
            holder.mBinding.txtTmpDenture.setText(mRequestDetailItems.get(position).getTmpDenture());
            holder.mBinding.txtTray.setText(mRequestDetailItems.get(position).getTray());
            holder.mBinding.txtFrame.setText(mRequestDetailItems.get(position).getFrame());
            holder.mBinding.txtWaxrim.setText(mRequestDetailItems.get(position).getWaxrim());
            holder.mBinding.txtArray.setText(mRequestDetailItems.get(position).getArray());
            holder.mBinding.txtQuering.setText(mRequestDetailItems.get(position).getQuering());
            holder.mBinding.txtDentureRepairing.setText(mRequestDetailItems.get(position).getDentureRepairing());
            holder.mBinding.txtEtc.setText(mRequestDetailItems.get(position).getEtc());
            holder.mBinding.txtAg.setText(mRequestDetailItems.get(position).getAg());

        } else {
            holder.mBinding.itemLayout02.setVisibility(View.GONE);  // shade
            holder.mBinding.itemLayout03.setVisibility(View.GONE);   // implant1
            holder.mBinding.itemLayout04.setVisibility(View.GONE);   // implant2
            holder.mBinding.itemLayout05.setVisibility(View.GONE);   // implant3
            holder.mBinding.itemLayout06.setVisibility(View.GONE);    // prosthesis1
            holder.mBinding.itemLayout07.setVisibility(View.GONE);      // prosthesis2

            holder.mBinding.itemLayout08.setVisibility(View.GONE);    // denture
            holder.mBinding.itemLayout09.setVisibility(View.GONE);     // tmpDenture
            holder.mBinding.itemLayout10.setVisibility(View.GONE);      // frame
            holder.mBinding.itemLayout11.setVisibility(View.GONE);       // tray
            holder.mBinding.itemLayout12.setVisibility(View.GONE);     // waxrim
            holder.mBinding.itemLayout13.setVisibility(View.GONE);      // array
            holder.mBinding.itemLayout14.setVisibility(View.GONE);        // quering
            holder.mBinding.itemLayout15.setVisibility(View.GONE);      // dentureRepairing
            holder.mBinding.itemLayout16.setVisibility(View.GONE);        // etc
            holder.mBinding.itemLayout17.setVisibility(View.GONE);     // ag
        }

        holder.mBinding.txtMemo.setText(mRequestDetailItems.get(position).getMemo());
        holder.mBinding.txtType.setText(mRequestDetailItems.get(position).getDivisions());

        return holder.mView;
    }

    private class RequestDetailViewHolder {
        private View mView;
        private ItemRequestDetailSimmiBinding mBinding;

        RequestDetailViewHolder(gbsoft.com.dental_gb.databinding.ItemRequestDetailSimmiBinding binding) {
            this.mView = binding.getRoot();
            this.mBinding = binding;
        }
    }
}
