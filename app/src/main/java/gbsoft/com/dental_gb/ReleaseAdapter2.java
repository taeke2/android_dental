package gbsoft.com.dental_gb;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ItemPlantListBinding;
import gbsoft.com.dental_gb.databinding.ItemRelease2Binding;

public class ReleaseAdapter2 extends RecyclerView.Adapter<ReleaseAdapter2.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private ReleaseAdapter2.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(ReleaseAdapter2.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemRelease2Binding mBinding;

        public MyViewHolder(ItemRelease2Binding binding) {
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
    public ReleaseAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReleaseAdapter2.MyViewHolder(ItemRelease2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReleaseAdapter2.MyViewHolder holder, int position) {
        // TODO: 해당 아이템으로 수정
        ReleaseDTO item = CommonClass.sReleaseDTO.get(position);


        holder.mBinding.itemClientName.setText(item.getClientName());
//        holder.mBinding.itemPatientName.setText(item.getPatientName(""));
        holder.mBinding.itemProductName.setText(item.getProductName());
        holder.mBinding.itemOrderDate.setText(item.getOrderDate() + " ~ ");
        holder.mBinding.itemDeadlineDate.setText(item.getDeadlineDate() + " | ");

        if(!item.getOutDate().equals("null")){
            holder.mBinding.itemOutDate.setText(item.getOutDate());
        } else {
            holder.mBinding.itemOutDate.setText("출고전");
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
        return CommonClass.sReleaseDTO.size();
    }

    // 리사이클러 뷰 스크롤하고 다시 올리면 뷰가 초기화 되는데, 이 메소드로 뷰를 유지시킨다.
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
