package gbsoft.com.dental_gb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderProcessAdapter extends BaseAdapter {
    String[] mProcessNames;
    int mProcessState;

    TextView txt_pro;

    public OrderProcessAdapter(String[] processNames, int processState) {
        this.mProcessNames = processNames;
        this.mProcessState = processState;
    }

    @Override
    public int getCount() {
        return mProcessNames.length;
    }

    @Override
    public Object getItem(int position) {
        return mProcessNames[position];
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
        txt_pro.setText(mProcessNames[position]);

        if (position <= mProcessState) {
//            txt_pro.setBackgroundColor(Color.rgb(226, 240, 203));
            txt_pro.setBackground(context.getDrawable(R.drawable.background_shape3));
        }

        return convertView;
    }
}
