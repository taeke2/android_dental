package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import gbsoft.com.dental_gb.databinding.ActivityPlantMonitoringBinding;

public class PlantMonitoringActivity extends AppCompatActivity {

    private ActivityPlantMonitoringBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPlantMonitoringBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);

        // TODO: 추후 인텐트에 웹캠 링크 추가
        mBinding.webview1.setOnTouchListener(new View.OnTouchListener() {  //터치이벤트 구현부
            private static final int MAX_CLICK_DURATION = 200;
            private long startClickTime;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if(clickDuration < MAX_CLICK_DURATION) {
                            //click event has occurred
                            Intent intent = new Intent(PlantMonitoringActivity.this, PlantSelectMonitoringActivity.class);
                            startActivity(intent);
                        }
                    }
                }
                return true;
            }
        });

        mBinding.webview2.setOnTouchListener(new View.OnTouchListener() {  //터치이벤트 구현부
            private static final int MAX_CLICK_DURATION = 200;
            private long startClickTime;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if(clickDuration < MAX_CLICK_DURATION) {
                            //click event has occurred
                            Intent intent = new Intent(PlantMonitoringActivity.this, PlantSelectMonitoringActivity.class);
                            startActivity(intent);
                        }
                    }
                }
                return true;
            }
        });
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