package gbsoft.com.dental_gb;

import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemOrder2Binding;

public class OrderProductAdapter2 extends RecyclerView.Adapter<OrderProductAdapter2.OrderProductViewHolder> {
    ArrayList<OrderItem> mOrderItems;
    private Context mContext;
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray();
    private int mPrePosition = -1;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OrderProductAdapter2.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OrderProductAdapter2.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnProcessItemClickListener {
        void onProcessItemClick(int pos);
    }

    private OrderProductAdapter2.OnProcessItemClickListener onProcessItemClickListener = null;

    public void setOnProcessItemClickListener(OrderProductAdapter2.OnProcessItemClickListener listener) {
        this.onProcessItemClickListener = listener;
    }

    public interface OnProcessItemLongClickListener {
        void onProcessItemLongClick(int pos);
    }

    private OrderProductAdapter2.OnProcessItemLongClickListener onProcessItemLongClickListener = null;

    public void setOnProcessItemLongClickListener(OrderProductAdapter2.OnProcessItemLongClickListener listener) {
        this.onProcessItemLongClickListener = listener;
    }

    public OrderProductAdapter2(Context context, ArrayList<OrderItem> orderItems) {
        this.mContext = context;
        this.mOrderItems = orderItems;
    }

    public class OrderProductViewHolder extends RecyclerView.ViewHolder {
        ItemOrder2Binding binding;

        public OrderProductViewHolder(@NonNull ItemOrder2Binding binding) {
            super(binding.getRoot());
            Iconify.with(new FontAwesomeModule());
            this.binding = binding;
        }
    }


    @NonNull
    @Override
    public OrderProductAdapter2.OrderProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderProductAdapter2.OrderProductViewHolder(ItemOrder2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull OrderProductAdapter2.OrderProductViewHolder holder, int position) {
        ItemOrder2Binding binding = holder.binding;

        OrderItem item = mOrderItems.get(position);
        binding.txtProductName.setText(item.getProductName());

        String temp = "";
        String[] productProcess = item.getProductProcess().split("→", -1);
        if (item.getManagers().equals("") || item.getStartTime().equals("") || item.getEndTime().equals("")) {
            for (int i = 0; i < productProcess.length; i++)
                temp += ";";
        }

        OrderProcessAdapter2 mOrderProcessAdapter = new OrderProcessAdapter2(
                mContext, productProcess,
                temp.equals("") ? item.getManagers().split(";", -1) : temp.split(";", -1),
                temp.equals("") ? item.getStartTime().split(";", -1) : temp.split(";", -1),
                temp.equals("") ? item.getEndTime().split(";", -1) : temp.split(";", -1),
                Integer.parseInt(item.getProcessState()));
        mOrderProcessAdapter.setOnItemClickListener(pos -> {
            if (pos != RecyclerView.NO_POSITION) {
                if (onProcessItemClickListener != null)
                    onProcessItemClickListener.onProcessItemClick(pos);
            }
        });
        mOrderProcessAdapter.setOnItemLongClickListener(pos -> {
            if (pos != RecyclerView.NO_POSITION) {
                if (onProcessItemLongClickListener != null)
                    onProcessItemLongClickListener.onProcessItemLongClick(pos);
            }
        });
        binding.orderRecycler.setAdapter(mOrderProcessAdapter);
        binding.orderRecycler.setLayoutManager(new LinearLayoutManager(mContext));

        if (mPrePosition < 0) {
            mSelectedItems.put(0, true);
            mPrePosition = 0;
        }

        changeVisibility(mSelectedItems.get(position), binding);

        binding.txtProductName.setOnClickListener(v -> {
            if (position != RecyclerView.NO_POSITION) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position);
            }

            if (mSelectedItems.get(position)) {
                mSelectedItems.delete(position);
            } else {
                mSelectedItems.delete(mPrePosition);
                mSelectedItems.put(position, true);
            }
            if (mPrePosition != -1) notifyItemChanged(mPrePosition);
            notifyItemChanged(position);
            mPrePosition = position;
        });
    }

    @Override
    public int getItemCount() {
        return mOrderItems.size();
    }

    private void changeVisibility(boolean isExpanded, ItemOrder2Binding binding) {
        // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
        int height = binding.recyclerLayout.getHeight();
        ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
        // Animation이 실행되는 시간, n/1000초
        va.setDuration(500);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // imageView의 높이 변경
//                binding.recyclerLayout.getLayoutParams().height = (int) animation.getAnimatedValue();
//                binding.recyclerLayout.requestLayout();
                binding.iconArrow.setText(isExpanded ? "{fa-caret-up}" : "{fa-caret-down}");
                binding.recyclerLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            }
        });

        // Animation start
        va.start();
    }
}
