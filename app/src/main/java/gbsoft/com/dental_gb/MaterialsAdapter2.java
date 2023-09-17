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

import gbsoft.com.dental_gb.databinding.ItemMaterials2Binding;
import gbsoft.com.dental_gb.databinding.ItemPlantListBinding;

public class MaterialsAdapter2 extends RecyclerView.Adapter<MaterialsAdapter2.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private MaterialsAdapter2.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(MaterialsAdapter2.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemMaterials2Binding mBinding;

        public MyViewHolder(ItemMaterials2Binding binding) {
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
    public MaterialsAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MaterialsAdapter2.MyViewHolder(ItemMaterials2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialsAdapter2.MyViewHolder holder, int position) {
        // TODO: 해당 아이템으로 수정
        MaterialDTO item = CommonClass.sMaterialDTO.get(position);

        holder.mBinding.txtMaterialsName.setText(item.getMaterialName());

//        holder.mBinding.txtMaterialsAccount.setText(item.getClientName());
//        holder.mBinding.txtWarehousingDate.setText(item.getWarehousingDate());
        holder.mBinding.txtMaterialsStock.setText(item.getStock()+"");

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
        return CommonClass.sMaterialDTO.size();
    }

    // 리사이클러 뷰 스크롤하고 다시 올리면 뷰가 초기화 되는데, 이 메소드로 뷰를 유지시킨다.
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
