package gbsoft.com.dental_gb;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ItemPlantListBinding;
import gbsoft.com.dental_gb.databinding.ItemRequest2Binding;

public class RequestAdapter2 extends RecyclerView.Adapter<RequestAdapter2.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public interface btnEtcClickListener {
        void onEtcClick(int pos);
    }

    private RequestAdapter2.OnItemClickListener onItemClickListener = null;
    private RequestAdapter2.btnEtcClickListener btnEtcClickListener = null;


    public void setOnItemClickListener(RequestAdapter2.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setBtnEtcClickListener(RequestAdapter2.btnEtcClickListener listener){
        this.btnEtcClickListener = listener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemRequest2Binding mBinding;

        public MyViewHolder(ItemRequest2Binding binding) {
            super(binding.getRoot());
            mBinding = binding;

            mBinding.layoutItem.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(position);
            });
        }
    }

    @NonNull
    @Override
    public RequestAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestAdapter2.MyViewHolder(ItemRequest2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter2.MyViewHolder holder, int position) {
        // TODO: 해당 아이템으로 수정
        RequestDTO item = CommonClass.sRequestDTO.get(position);

        holder.mBinding.itemClientName.setText(item.getClientName());
        holder.mBinding.itemPatientName.setText(item.getPatientName());
        holder.mBinding.itemProductName.setText(item.getProductName());
        if (!item.getBoxName().equals("")) {
            holder.mBinding.itemBoxNo.setText("(" + item.getBoxName() + ")");
        }
        holder.mBinding.itemOrderDate.setText(item.getOrderDate() + " ~ ");
        holder.mBinding.itemDeadlineDate.setText(item.getDeadlineDate());
        holder.mBinding.itemDeadlineTime.setText(item.getDeadlineTime());
//        holder.mBinding.itemDeadlineTime.setText(item.getDeadlineDate().substring(11, 13));
        holder.mBinding.txtDentalFormula10.setText(item.getDentalFormula10());
        holder.mBinding.txtDentalFormula20.setText(item.getDentalFormula20());
        holder.mBinding.txtDentalFormula30.setText(item.getDentalFormula30());
        holder.mBinding.txtDentalFormula40.setText(item.getDentalFormula40());

        holder.mBinding.btnEtc.setOnClickListener(v -> {
            this.btnEtcClickListener.onEtcClick(holder.getAbsoluteAdapterPosition());
        });

//        if (item.getImportantYn() == 0)
//            holder.mBinding.imgViewImportant.setVisibility(View.GONE);
//        if (!isNew(item.getUpdateDate(), 7))
//            holder.mBinding.txtNew.setVisibility(View.GONE);
//        holder.mBinding.txtWriter.setText(item.getWriter());
//        holder.mBinding.txtDate.setText(item.getUpdateDate());
    }

    private boolean isNew(String date, int day) {
        Date nowDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate = null;
        try {
            startDate = format.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.add(Calendar.DAY_OF_MONTH, day);

            Date endDate = cal.getTime();
            return nowDate.after(startDate) && nowDate.before(endDate);
        } catch (ParseException e) {
            Log.e(CommonClass.TAG_ERR, "ERROR: Parse exception - isNew");
        }
        return false;
    }

    @Override
    public int getItemCount() {
        // TODO: 해당 아이템으로 수정
        return CommonClass.sRequestDTO.size();
    }

    // 리사이클러 뷰 스크롤하고 다시 올리면 뷰가 초기화 되는데, 이 메소드로 뷰를 유지시킨다.
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
