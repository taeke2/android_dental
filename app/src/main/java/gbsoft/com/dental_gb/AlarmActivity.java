package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import gbsoft.com.dental_gb.adapter.AlarmAdapter;
import gbsoft.com.dental_gb.databinding.ActivityAlarmBinding;
import gbsoft.com.dental_gb.dto.AlarmDTO;

public class AlarmActivity extends AppCompatActivity {
    private ActivityAlarmBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetAlarm();
    }

    private void initialSet() {
        mBinding.btnBack.setOnClickListener(v -> finish());

        removeOldAlarm();
        AlarmAdapter adapter = new AlarmAdapter();
        mBinding.recyclerAlarm.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(AlarmActivity.this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        mBinding.recyclerAlarm.setLayoutManager(manager);
        adapter.notifyDataSetChanged();

        mBinding.loading.setVisibility(View.GONE);
        if (CommonClass.sAlarmDTO.size() == 0) {
            mBinding.recyclerAlarm.setVisibility(View.GONE);
            mBinding.txtEmpty.setVisibility(View.VISIBLE);
        } else {
            mBinding.recyclerAlarm.setVisibility(View.VISIBLE);
            mBinding.txtEmpty.setVisibility(View.GONE);
        }
    }

    private void removeOldAlarm() {
        int position = -1;
        for (int i = 0; i < CommonClass.sAlarmDTO.size(); i++) {
            long now = System.currentTimeMillis();
            long calDateDays = Math.abs((now - CommonClass.sAlarmDTO.get(i).getTimeStamp()) / (24*60*60*1000));
            if (calDateDays <= 7) {
                position = i;
                break;
            }
        }

        if (position < 0)
            return;
        CommonClass.sAlarmDTO.subList(0, position).clear();
    }

    private void resetAlarm() {
        SharedPreferences sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        MenuActivity2.showAlarmBadge(0);
        for (AlarmDTO item: CommonClass.sAlarmDTO)
            item.setIsChecked(true);
    }
}