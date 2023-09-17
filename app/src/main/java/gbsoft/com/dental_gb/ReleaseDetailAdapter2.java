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

import gbsoft.com.dental_gb.databinding.ItemRelease2Binding;
import gbsoft.com.dental_gb.databinding.ItemReleaseDetail2Binding;

public class ReleaseDetailAdapter2 extends RecyclerView.Adapter<ReleaseDetailAdapter2.MyViewHolder> {

    private ReleaseDTO releaseDTO;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private ReleaseDetailAdapter2.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(ReleaseDetailAdapter2.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ReleaseDetailAdapter2(int index) {
        this.releaseDTO = CommonClass.sReleaseDTO.get(index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemReleaseDetail2Binding mBinding;

        public MyViewHolder(ItemReleaseDetail2Binding binding) {
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
    public ReleaseDetailAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReleaseDetailAdapter2.MyViewHolder(ItemReleaseDetail2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReleaseDetailAdapter2.MyViewHolder holder, int position) {
        // TODO: 해당 아이템으로 수정
        ReleaseDTO.ReleaseProductDTO releaseProductDTO = releaseDTO.getReleaseProductDTOS().get(position);

        holder.mBinding.txtProductName.setText(releaseProductDTO.getProductName());
        holder.mBinding.txtManagerValue.setText(releaseProductDTO.getManagerName());
        holder.mBinding.txtDeadlineDateTime.setText(releaseProductDTO.getOrderDateTime());
        holder.mBinding.txtOrderFinishTime.setText(releaseProductDTO.getOutDateTime());
        holder.mBinding.txtDent1.setText(releaseProductDTO.getDent1());
        holder.mBinding.txtDent2.setText(releaseProductDTO.getDent2());
        holder.mBinding.txtDent3.setText(releaseProductDTO.getDent3());
        holder.mBinding.txtDent4.setText(releaseProductDTO.getDent4());

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
        return releaseDTO.getReleaseProductDTOS().size();
    }

    // 리사이클러 뷰 스크롤하고 다시 올리면 뷰가 초기화 되는데, 이 메소드로 뷰를 유지시킨다.
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
