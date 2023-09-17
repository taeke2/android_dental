package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import gbsoft.com.dental_gb.databinding.ActivityPlantSelectMonitoringBinding;

public class PlantSelectMonitoringActivity extends AppCompatActivity {

    private ActivityPlantSelectMonitoringBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPlantSelectMonitoringBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    View.OnClickListener btnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}