package gbsoft.com.dental_gb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ItemRequestImageBinding;

public class RequestImageAdapter extends RecyclerView.Adapter<RequestImageAdapter.ViewHolder> {

    private String mServerPath;
    private final RequestManager mGlide;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnLongItemClickListener {
        void onLongItemClick(int pos);
    }

    private OnLongItemClickListener onLongItemClickListener = null;

    public void setOnLongItemClickListener(OnLongItemClickListener listener) {
        this.onLongItemClickListener = listener;
    }

    private ArrayList<String> mBitmaps = null;

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemRequestImageBinding mBinding;

        public ViewHolder(@NonNull ItemRequestImageBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

            binding.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(pos);
                        }
                    }
                }
            });

            binding.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (onLongItemClickListener != null) {
                            onLongItemClickListener.onLongItemClick(pos);
                            return true;
                        }
                    }
                    return false;
                }
            });

        }
    }

    public RequestImageAdapter(ArrayList<String> bitmaps, String serverPath, RequestManager glide) {
        this.mBitmaps = bitmaps;
        this.mServerPath = serverPath;
        this.mGlide = glide;
    }

    public String imgReturnUrl(int position){
        return mServerPath + "/uploads_android/" + mBitmaps.get(position) + ".jpg";
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemRequestImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestImageAdapter.ViewHolder holder, int position) {
        ItemRequestImageBinding binding = holder.mBinding;
        mGlide.load(mServerPath + "/uploads_android/" + mBitmaps.get(position) + ".jpg")
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerInside()
                .into(binding.imageView);
//        holder.imageView.setImageBitmap(mBitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return mBitmaps.size();
    }

}

