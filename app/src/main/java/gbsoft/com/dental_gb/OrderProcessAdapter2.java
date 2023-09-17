package gbsoft.com.dental_gb;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gbsoft.com.dental_gb.databinding.ItemProcessStepBinding;

public class OrderProcessAdapter2 extends RecyclerView.Adapter<OrderProcessAdapter2.OrderProcessViewHolder> {
    String[] mProcessNames;
    String[] mManagers;
    String[] mStartTime;
    String[] mEndTime;
    int mProcessState;
    Context mContext;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OrderProcessAdapter2.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OrderProcessAdapter2.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int pos);
    }

    private OrderProcessAdapter2.OnItemLongClickListener onItemLongClickListener = null;

    public void setOnItemLongClickListener(OrderProcessAdapter2.OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public OrderProcessAdapter2(Context context, String[] processNames, String[] managers, String[] startTime, String[] endTime, int processState) {
        this.mContext = context;
        this.mProcessNames = processNames;
        this.mManagers = managers;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mProcessState = processState;
    }

    public class OrderProcessViewHolder extends RecyclerView.ViewHolder {
        ItemProcessStepBinding binding;
        public OrderProcessViewHolder(@NonNull ItemProcessStepBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.layoutItem.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(position);
                }
            });

            binding.layoutItem.setOnLongClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (onItemLongClickListener != null)
                        onItemLongClickListener.onItemLongClick(position);
                }
                return false;
            });
        }
    }

    @NonNull
    @Override
    public OrderProcessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderProcessAdapter2.OrderProcessViewHolder(ItemProcessStepBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProcessViewHolder holder, int position) {
        ItemProcessStepBinding binding = holder.binding;
        binding.txtPro.setText(mProcessNames[position]);

//        if (position <= mProcessState) {
//            binding.txtPro.setBackground(mContext.getDrawable(R.drawable.background_shape3));
//        }
        if (!mManagers[position].equals("")) {
            binding.txtPro.setTextColor(mContext.getColor(R.color.green));
            binding.txtManager.setText(mManagers[position]);
            binding.txtStartTime.setText(mStartTime[position]);
        } else {
            binding.txtManager.setVisibility(View.GONE);
            binding.txtStartTime.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mProcessNames.length;
    }


}

