package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import gbsoft.com.dental_gb.databinding.ActivityNoticeDetailBinding;

public class NoticeDetailActivity extends AppCompatActivity {
    private ActivityNoticeDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityNoticeDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void initialSet() {
        Intent intent = getIntent();
        mBinding.txtTitle.setText(intent.getStringExtra("title"));
        mBinding.txtDate.setText(intent.getStringExtra("date"));
        mBinding.txtWriter.setText(intent.getStringExtra("manager"));
        mBinding.btnBack.setOnClickListener(v -> finish());

        WebSetting setting = new WebSetting(NoticeDetailActivity.this);
        WebView webView = setting.setWebSettings(mBinding.webView);
        webView.loadData(intent.getStringExtra("content"), "text/html; charset=utf-8", "UTF-8");
    }

}