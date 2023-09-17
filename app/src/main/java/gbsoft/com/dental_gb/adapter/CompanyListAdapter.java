package gbsoft.com.dental_gb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gbsoft.com.dental_gb.dto.CompanyDTO;
import gbsoft.com.dental_gb.R;

public class CompanyListAdapter extends BaseAdapter {
    ArrayList<CompanyDTO> mCompanies;

    public CompanyListAdapter(ArrayList<CompanyDTO> companies) {
        this.mCompanies = companies;
    }

    @Override
    public int getCount() {
        return mCompanies.size();
    }

    @Override
    public Object getItem(int position) {
        return mCompanies.get(position);
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
            convertView = inflater.inflate(R.layout.item_company_list, parent, false);
        }

        TextView txt_companyName = (TextView) convertView.findViewById(R.id.txt_companyName);
        txt_companyName.setText(mCompanies.get(position).getCompanyName());

        return convertView;
    }
}
