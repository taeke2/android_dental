package gbsoft.com.dental_gb;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ItemRequest2Binding;
import gbsoft.com.dental_gb.databinding.ItemRequestDetailYkBinding;

public class RequestDetailAdapter_YK extends RecyclerView.Adapter<RequestDetailAdapter_YK.MyViewHolder> {


    private RequestDTO requestDTO;
    private DynamicNewLine dynamicNewLine;
    private float scale;
    private Context mContext;


    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private RequestDetailAdapter_YK.OnItemClickListener onItemClickListener = null;


    public void setOnItemClickListener(RequestDetailAdapter_YK.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public RequestDetailAdapter_YK(int index, float scale, Context context) {
        dynamicNewLine = new DynamicNewLine();

        this.mContext = context;
        this.scale = scale;
        this.requestDTO = CommonClass.sRequestDTO.get(index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemRequestDetailYkBinding mBinding;

        public MyViewHolder(ItemRequestDetailYkBinding binding) {
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
    public RequestDetailAdapter_YK.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestDetailAdapter_YK.MyViewHolder(ItemRequestDetailYkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestDetailAdapter_YK.MyViewHolder holder, int position) {
        // TODO: 해당 아이템으로 수정
        RequestDTO.RequestDetailDTO requestDetailDTO = requestDTO.getRequestDetailDTOS().get(position);

        holder.mBinding.txtProductName.setText(requestDetailDTO.getProductName());
        holder.mBinding.txtProductPart.setText(requestDetailDTO.getProductPart());
        if(requestDetailDTO.getOrderCk() == 1){
            holder.mBinding.txtWorkOrderTime.setVisibility(View.VISIBLE);
            holder.mBinding.imgWorkOrder.setTextColor(Color.parseColor("#62983E"));
            holder.mBinding.txtWorkOrderTime.setText(requestDetailDTO.getWorkOrderDate());
        }

        if(requestDetailDTO.getProcessCk() > 0){
            holder.mBinding.txtProcessProgressTime.setVisibility(View.VISIBLE);
            holder.mBinding.imgProcessProgress.setTextColor(Color.parseColor("#4C81B1"));
            holder.mBinding.txtProcessProgressTime.setText(requestDetailDTO.getProcessProgressDate());
        }

//        if(!requestDetailDTO.getProgressCompleteDate().equals(null) && !requestDetailDTO.getProgressCompleteDate().equals("")){
//            holder.mBinding.txtProcessCompleteTime.setVisibility(View.VISIBLE);
//        }

        if(requestDetailDTO.getOutmntCk() == 1){
            holder.mBinding.txtReleaseTime.setVisibility(View.VISIBLE);
            holder.mBinding.imgRelease.setTextColor(Color.parseColor("#1E3373"));
            holder.mBinding.txtReleaseTime.setText(requestDetailDTO.getProductOutDate());
        }

        holder.mBinding.txtDentalFormula10.setText(requestDetailDTO.getDent1());
        holder.mBinding.txtDentalFormula20.setText(requestDetailDTO.getDent2());
        holder.mBinding.txtDentalFormula30.setText(requestDetailDTO.getDent3());
        holder.mBinding.txtDentalFormula40.setText(requestDetailDTO.getDent4());

//        holder.mBinding.txtShade1.setText(requestDetailDTO.getShade1());
//        holder.mBinding.txtShade2.setText(requestDetailDTO.getShade2());
//        holder.mBinding.txtShade3.setText(requestDetailDTO.getShade3());
//        holder.mBinding.txtShade4.setText(requestDetailDTO.getShade4());
//        holder.mBinding.txtShade5.setText(requestDetailDTO.getShade5());
//        holder.mBinding.txtShade6.setText(requestDetailDTO.getShade6());
//        holder.mBinding.txtShade7.setText(requestDetailDTO.getShade7());
//        holder.mBinding.txtShade8.setText(requestDetailDTO.getShade8());
//        holder.mBinding.txtShade9.setText(requestDetailDTO.getShade9());
//
//        holder.mBinding.txtImplantCrown.setText(requestDetailDTO.getImplantCrown());
//        holder.mBinding.txtSpaceLack.setText(requestDetailDTO.getSpaceLack());
//        holder.mBinding.txtPonticDesign.setText(requestDetailDTO.getPonticDesign());
//        holder.mBinding.txtMetalDesign.setText(requestDetailDTO.getMetalDesign());
//        holder.mBinding.txtFullDenture.setText(requestDetailDTO.getFullDesign());
//        holder.mBinding.txtFlexibleDenture.setText(requestDetailDTO.getFlexibleDenture());
//        holder.mBinding.txtJaw.setText(requestDetailDTO.getJaw());
//        holder.mBinding.txtPartialDenture.setText(requestDetailDTO.getPartialDenture());
//        holder.mBinding.txtDentureExtra.setText(requestDetailDTO.getDentureExtra());
//        holder.mBinding.txtMemo.setText(requestDetailDTO.getMemo());
//        holder.mBinding.txtType.setText(requestDetailDTO.getType());
        holder.mBinding.itemLayoutType.removeAllViews();

        dynamicNewLine.getNewLineLayout(requestDetailDTO.getType().split(","), 20, mContext, holder, holder.mBinding.itemLayoutType, scale);


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
        return requestDTO.getRequestDetailDTOS().size();
    }

    // 리사이클러 뷰 스크롤하고 다시 올리면 뷰가 초기화 되는데, 이 메소드로 뷰를 유지시킨다.
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
