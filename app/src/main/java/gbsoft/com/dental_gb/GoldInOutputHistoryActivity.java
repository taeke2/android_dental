package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ActivityGoldInOutputHistoryBinding;

public class GoldInOutputHistoryActivity extends AppCompatActivity {

    private ActivityGoldInOutputHistoryBinding mBinding;

    private GoldInOutputHistoryAdapter mAdapter;

    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityGoldInOutputHistoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialize(){
        Intent intent = getIntent();
        pos = intent.getIntExtra("pos", -1);
        GoldDTO goldDTO = CommonClass.sGoldDTO.get(pos);

        mBinding.txtAccount.setText(goldDTO.getClientName());
        mBinding.txtGoldName.setText(goldDTO.getGoldName());

        String[] spinnerFilter = {"전체", "입고", "사용"};

        ArrayAdapter<String> adapter_spinner = new ArrayAdapter<>(GoldInOutputHistoryActivity.this, R.layout.support_simple_spinner_dropdown_item, spinnerFilter);

        mBinding.spinner.setAdapter(adapter_spinner);

        String[] mGoldInOutputHistoryDate = {"2021.07.01", "2021.06.30"};
        String[] mGoldInOutputHistory = {"임플란트", "입고"};
        double[] mGoldCountHistory = {3, 15};
        double[] mStockGold = {19.8, 23.0};

        goldDTO.setHistoryDTOS(new ArrayList<>());

        for(int i = 0; i < 2; i++){
            GoldDTO.GoldInOutputHistoryDTO goldInOutputHistoryDTO = goldDTO.new GoldInOutputHistoryDTO();
            goldInOutputHistoryDTO.setId(i);
            goldInOutputHistoryDTO.setDate(mGoldInOutputHistoryDate[i]);
            goldInOutputHistoryDTO.setHistory(mGoldInOutputHistory[i]);
            goldInOutputHistoryDTO.setConsumption(mGoldCountHistory[i]);
            goldInOutputHistoryDTO.setStockGold(mStockGold[i]);

            goldDTO.getHistoryDTOS().add(goldInOutputHistoryDTO);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);
        mBinding.btnGoldRegistry.setOnClickListener(btnGoldRegistryOnClickListener);

        mAdapter = new GoldInOutputHistoryAdapter(pos);

        mBinding.listGold.setAdapter(mAdapter);
        mBinding.listGold.setLayoutManager(new LinearLayoutManager(GoldInOutputHistoryActivity.this, LinearLayoutManager.VERTICAL, false));
    }

    View.OnClickListener btnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    View.OnClickListener btnGoldRegistryOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(GoldInOutputHistoryActivity.this, GoldInOutputRegistryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };
}