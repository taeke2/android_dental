package gbsoft.com.dental_gb;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ItemPlantListBinding;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private PlantAdapter.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(PlantAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemPlantListBinding mBinding;

        public MyViewHolder(ItemPlantListBinding binding) {
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
    public PlantAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlantAdapter.MyViewHolder(ItemPlantListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlantAdapter.MyViewHolder holder, int position) {
        // TODO: 해당 아이템으로 수정
        PlantListDTO item = CommonClass.sPlantListDTO.get(position);

        holder.mBinding.txtPlantName.setText(item.getPlantName());
        holder.mBinding.txtVendor.setText(item.getVendor());

        switch(item.getPlantState()){
            case 0:
                holder.mBinding.txtPlantState.setBackgroundResource(R.drawable.bg_plant_state_off);
                break;
            case 1:
                holder.mBinding.txtPlantState.setBackgroundResource(R.drawable.bg_plant_state_normal);
                break;
            case 2:
                holder.mBinding.txtPlantState.setBackgroundResource(R.drawable.bg_plant_state_error);
                break;
        }

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
        return CommonClass.sPlantListDTO.size();
    }

    // 리사이클러 뷰 스크롤하고 다시 올리면 뷰가 초기화 되는데, 이 메소드로 뷰를 유지시킨다.
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
