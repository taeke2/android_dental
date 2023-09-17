package gbsoft.com.dental_gb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemRequestDetailUniBinding;

public class RequestDetailAdapter_Uni extends BaseAdapter {
    ArrayList<RequestDetailItem> mRequestDetailItems;

    public RequestDetailAdapter_Uni(ArrayList<RequestDetailItem> requestDetailItems) {
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
            ItemRequestDetailUniBinding binding = ItemRequestDetailUniBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            holder = new RequestDetailViewHolder(binding);
            holder.mView.setTag(holder);
        } else {
            holder = (RequestDetailViewHolder) convertView.getTag();
        }

        holder.mBinding.itemLayout01.setVisibility(View.VISIBLE);   // dentalFormula
        holder.mBinding.itemLayout02.setVisibility(View.VISIBLE);   // shade

        holder.mBinding.itemLayout17.setVisibility(View.VISIBLE);   // memo

        holder.mBinding.txtProductName.setText(mRequestDetailItems.get(position).getProductName());
        holder.mBinding.txtProductPart.setText(mRequestDetailItems.get(position).getProductPart());

        String dentalFormulaStr = mRequestDetailItems.get(position).getDentalFormula();
        String[] dentalFormula = dentalFormulaStr.split(", ");
        String[] formula = {"", "", "", ""};
        int len = dentalFormula.length;
        for (int i = 0; i < len; i++) {
            String tmp = dentalFormula[i].substring(0, 1);
            String tmp2 = dentalFormula[i].substring(1, dentalFormula[i].length());
            switch (tmp) {
                case "1" :
                    formula[0] += tmp2 + " ";
                    break;
                case "2" :
                    formula[1] += tmp2 + " ";
                    break;
                case "3" :
                    formula[2] += tmp2 + " ";
                    break;
                case "4" :
                    formula[3] += tmp2 + " ";
                    break;
                default :
                    break;
            }
        }

        holder.mBinding.txtDentalFormula10.setText("  " + formula[0] + "  ");
        holder.mBinding.txtDentalFormula20.setText("  " + formula[1] + "  ");
        holder.mBinding.txtDentalFormula30.setText("  " + formula[2] + "  ");
        holder.mBinding.txtDentalFormula40.setText("  " + formula[3] + "  ");

        holder.mBinding.txtShade.setText(mRequestDetailItems.get(position).getShade());

        if (mRequestDetailItems.get(position).getProductPart().equals("일반보철")) {
            holder.mBinding.itemLayout03.setVisibility(View.VISIBLE);   // implant1
            holder.mBinding.itemLayout04.setVisibility(View.VISIBLE);   // implant2
            holder.mBinding.itemLayout05.setVisibility(View.VISIBLE);   // implant3
            holder.mBinding.itemLayout06.setVisibility(View.VISIBLE);     // prosthesis1
            holder.mBinding.itemLayout07.setVisibility(View.VISIBLE);      // prosthesis2

            holder.mBinding.itemLayout08.setVisibility(View.GONE);      // denture
            holder.mBinding.itemLayout09.setVisibility(View.GONE);      // tmpDenture
            holder.mBinding.itemLayout10.setVisibility(View.GONE);      // tray
            holder.mBinding.itemLayout11.setVisibility(View.GONE);      // waxrim
            holder.mBinding.itemLayout12.setVisibility(View.GONE);      // array
            holder.mBinding.itemLayout13.setVisibility(View.GONE);      // quering
            holder.mBinding.itemLayout14.setVisibility(View.GONE);      // denture repairing
            holder.mBinding.itemLayout15.setVisibility(View.GONE);      // etc
            holder.mBinding.itemLayout16.setVisibility(View.GONE);      // ag

            holder.mBinding.txtImplant1.setText(mRequestDetailItems.get(position).getImplant1());
            holder.mBinding.txtImplant2.setText(mRequestDetailItems.get(position).getImplant2());
            holder.mBinding.txtImplant3.setText(mRequestDetailItems.get(position).getImplant3());
            holder.mBinding.txtProsthesis1.setText(mRequestDetailItems.get(position).getProsthesis1());
            holder.mBinding.txtProsthesis2.setText(mRequestDetailItems.get(position).getProsthesis2());

        } else if (mRequestDetailItems.get(position).getProductPart().equals("덴처")) {
            holder.mBinding.itemLayout03.setVisibility(View.VISIBLE);   // implant1
            holder.mBinding.itemLayout04.setVisibility(View.GONE);      // implant2
            holder.mBinding.itemLayout05.setVisibility(View.GONE);      // implant3
            holder.mBinding.itemLayout06.setVisibility(View.GONE);      // prosthesis1
            holder.mBinding.itemLayout07.setVisibility(View.GONE);      // prosthesis2

            holder.mBinding.itemLayout08.setVisibility(View.VISIBLE);   // denture
            holder.mBinding.itemLayout09.setVisibility(View.VISIBLE);   // tmpDenture
            holder.mBinding.itemLayout10.setVisibility(View.VISIBLE);   // tray
            holder.mBinding.itemLayout11.setVisibility(View.VISIBLE);   // waxrim
            holder.mBinding.itemLayout12.setVisibility(View.VISIBLE);   // array
            holder.mBinding.itemLayout13.setVisibility(View.VISIBLE);   // quering
            holder.mBinding.itemLayout14.setVisibility(View.VISIBLE);   // denture repairing
            holder.mBinding.itemLayout15.setVisibility(View.VISIBLE);   // etc
            holder.mBinding.itemLayout16.setVisibility(View.VISIBLE);   // ag

            holder.mBinding.txtImplant1.setText(mRequestDetailItems.get(position).getImplant1());
            holder.mBinding.txtDenture.setText(mRequestDetailItems.get(position).getDenture());
            holder.mBinding.txtTmpDenture.setText(mRequestDetailItems.get(position).getTmpDenture());
            holder.mBinding.txtTray.setText(mRequestDetailItems.get(position).getTray());
            holder.mBinding.txtWaxrim.setText(mRequestDetailItems.get(position).getWaxrim());
            holder.mBinding.txtArray.setText(mRequestDetailItems.get(position).getArray());
            holder.mBinding.txtQuering.setText(mRequestDetailItems.get(position).getQuering());
            holder.mBinding.txtDentureRepairing.setText(mRequestDetailItems.get(position).getDentureRepairing());
            holder.mBinding.txtEtc.setText(mRequestDetailItems.get(position).getEtc());
            holder.mBinding.txtAg.setText(mRequestDetailItems.get(position).getAg());

        } else {
            holder.mBinding.itemLayout02.setVisibility(View.GONE);      // shade
            holder.mBinding.itemLayout03.setVisibility(View.GONE);      // implant1
            holder.mBinding.itemLayout04.setVisibility(View.GONE);      // implant2
            holder.mBinding.itemLayout05.setVisibility(View.GONE);      // implant3
            holder.mBinding.itemLayout06.setVisibility(View.GONE);      // prosthesis1
            holder.mBinding.itemLayout07.setVisibility(View.GONE);      // prosthesis2

            holder.mBinding.itemLayout08.setVisibility(View.GONE);      // denture
            holder.mBinding.itemLayout09.setVisibility(View.GONE);      // tmpDenture
            holder.mBinding.itemLayout10.setVisibility(View.GONE);      // tray
            holder.mBinding.itemLayout11.setVisibility(View.GONE);      // waxrim
            holder.mBinding.itemLayout12.setVisibility(View.GONE);      // array
            holder.mBinding.itemLayout13.setVisibility(View.GONE);      // quering
            holder.mBinding.itemLayout14.setVisibility(View.GONE);      // denture repairing
            holder.mBinding.itemLayout15.setVisibility(View.GONE);      // etc
            holder.mBinding.itemLayout16.setVisibility(View.GONE);      // ag
        }

        holder.mBinding.txtMemo.setText(mRequestDetailItems.get(position).getMemo());

        return holder.mView;
    }

    private class RequestDetailViewHolder {
        private View mView;
        private ItemRequestDetailUniBinding mBinding;

        RequestDetailViewHolder(ItemRequestDetailUniBinding binding) {
            this.mView = binding.getRoot();
            this.mBinding = binding;
        }
    }
}
