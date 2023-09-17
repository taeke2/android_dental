package gbsoft.com.dental_gb.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gbsoft.com.dental_gb.CommonClass;
import gbsoft.com.dental_gb.dto.NoticeDTO;
import gbsoft.com.dental_gb.databinding.ItemNoticeBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> {
    private ArrayList<NoticeDTO> mNoticeList = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemNoticeBinding mBinding;

        public MyViewHolder(ItemNoticeBinding binding) {
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoticeAdapter.MyViewHolder(ItemNoticeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NoticeDTO item = mNoticeList.get(position);

        holder.mBinding.txtTitle.setText(item.getTitle());
        if (!item.getImportantYn())
            holder.mBinding.imgViewImportant.setVisibility(View.GONE);
        if (!isNew(item.getDate(), 7))
            holder.mBinding.txtNew.setVisibility(View.GONE);
        holder.mBinding.txtWriter.setText(item.getManager());
        holder.mBinding.txtDate.setText(item.getDate());
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
        return mNoticeList.size();
    }

    // 리사이클러 뷰 스크롤하고 다시 올리면 뷰가 초기화 되는데, 이 메소드로 뷰를 유지시킨다.
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addItem(NoticeDTO item) {
        mNoticeList.add(item);
    }

    public void clearItem() {
        mNoticeList.clear();
    }

    public NoticeDTO getItem(int pos) {
        return mNoticeList.get(pos);
    }
}

