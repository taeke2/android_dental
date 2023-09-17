package gbsoft.com.dental_gb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemRequestBinding;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private Context mContext;
    private ArrayList<ProcessRequestReleaseListItem> mProcessRequestReleaseListItems;

    public interface  OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public RequestAdapter(Context context, ArrayList<ProcessRequestReleaseListItem> processRequestReleaseListItems) {
        this.mContext = context;
        this.mProcessRequestReleaseListItems = processRequestReleaseListItems;
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        private ItemRequestBinding mBinding;

        public RequestViewHolder(@NonNull ItemRequestBinding binding) {
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
    public RequestAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestViewHolder(ItemRequestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Iconify.with(new FontAwesomeModule());
        ItemRequestBinding binding = holder.mBinding;

        SharedPreferences pref = mContext.getSharedPreferences("auto", Context.MODE_PRIVATE);
        int companyCode = pref.getInt("num", -1);

        String str_dentalFormula10 = "";
        String str_dentalFormula20 = "";
        String str_dentalFormula30 = "";
        String str_dentalFormula40 = "";

        boolean isPartners = companyCode == CommonClass.PDL;
        boolean isUni = companyCode == CommonClass.UI;
        boolean isSimmi = companyCode == CommonClass.SM;
        boolean isEudon = companyCode == CommonClass.ED;

        if (isUni) {
            binding.txtDentalFormula10.setVisibility(View.GONE);
            binding.txtDentalFormula20.setVisibility(View.GONE);
            binding.txtDentalFormula30.setVisibility(View.GONE);
            binding.txtDentalFormula40.setVisibility(View.GONE);
            binding.viewWidth.setVisibility(View.GONE);
            binding.viewHeight1.setVisibility(View.GONE);
            binding.viewHeight2.setVisibility(View.GONE);
            binding.txtDentalFormulaUni.setVisibility(View.VISIBLE);

            binding.txtDentalFormulaUni.setText(mProcessRequestReleaseListItems.get(position).getDentalFormula());
        } else {
            binding.txtDentalFormula10.setVisibility(View.VISIBLE);
            binding.txtDentalFormula20.setVisibility(View.VISIBLE);
            binding.txtDentalFormula30.setVisibility(View.VISIBLE);
            binding.txtDentalFormula40.setVisibility(View.VISIBLE);
            binding.viewWidth.setVisibility(View.VISIBLE);
            binding.viewHeight1.setVisibility(View.VISIBLE);
            binding.viewHeight2.setVisibility(View.VISIBLE);
            binding.txtDentalFormulaUni.setVisibility(View.GONE);

            String[] dentals = mProcessRequestReleaseListItems.get(position).getDentalFormula().split("and");
            for (String dental : dentals) {
                String[] dentalArr;
                if (isPartners) {
                    dentalArr = dental.split("/", 4);
                } else {
                    dentalArr = dental.split("&", 4);
                }
                str_dentalFormula10 += dentalArr[0] + " ";
                str_dentalFormula20 += dentalArr[1] + " ";
                str_dentalFormula30 += dentalArr[2] + " ";
                str_dentalFormula40 += dentalArr[3] + " ";
            }

            binding.txtDentalFormula10.setText(str_dentalFormula10);
            binding.txtDentalFormula20.setText(str_dentalFormula20);
            binding.txtDentalFormula30.setText(str_dentalFormula30);
            binding.txtDentalFormula40.setText(str_dentalFormula40);
        }

        binding.itemRequestCode.setText(mProcessRequestReleaseListItems.get(position).getRequestCode());
        binding.itemClientName.setText(mProcessRequestReleaseListItems.get(position).getClientName());
        binding.itemPatientName.setText(mProcessRequestReleaseListItems.get(position).getPatientName());
        binding.itemOrderDate.setText(mProcessRequestReleaseListItems.get(position).getOrderDate() + " ~ ");

        String[] deadline = mProcessRequestReleaseListItems.get(position).getDeadlineDate().split(" ");
        binding.itemDeadlineDate.setText(deadline[0]);
        binding.itemDeadlineTime.setText(deadline[1]);

        String productName = mProcessRequestReleaseListItems.get(position).getProductName();
        productName = (productName.length() > 25) ? productName.substring(0, 20) + "..." : productName;
        binding.itemProductName.setText(productName);

        binding.btnEtc.setOnClickListener(v -> {
            SubMenuBottomDialog activity = new SubMenuBottomDialog();
            Bundle bundle = new Bundle();
            bundle.putString("reqNum", mProcessRequestReleaseListItems.get(position).getRequestCode());
            bundle.putString("clientTel", mProcessRequestReleaseListItems.get(position).getClientTel());
            activity.setArguments(bundle);
            activity.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "tag");
        });

        if (isPartners || isSimmi || isEudon) {
            binding.btnEtc.setVisibility(View.VISIBLE);
        } else {
            binding.btnEtc.setVisibility(View.GONE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mProcessRequestReleaseListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
