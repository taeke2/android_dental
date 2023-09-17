package gbsoft.com.dental_gb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderProductAdapter extends BaseAdapter {
    ArrayList<OrderItem> mOrderItems;
    int mSelect;

    TextView txt_pro;

    public OrderProductAdapter(ArrayList<OrderItem> orderItems, int select) {
        this.mOrderItems = orderItems;
        this.mSelect = select;
    }

    @Override
    public int getCount() {
        return mOrderItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrderItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_order, parent, false);
        }

        txt_pro = (TextView) convertView.findViewById(R.id.txt_pro);
        txt_pro.setText(mOrderItems.get(position).getProductName());
        if (position == mSelect) {
//            txt_pro.setBackgroundColor(Color.rgb(204, 204, 204));
            txt_pro.setBackground(context.getDrawable(R.drawable.background_shape2));
        } else {
//            txt_pro.setBackgroundColor(Color.rgb(255, 255, 255));
            txt_pro.setBackground(context.getDrawable(R.drawable.dialog_background_shape));
        }
        return convertView;
    }
}
