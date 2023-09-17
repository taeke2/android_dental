package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import gbsoft.com.dental_gb.databinding.ActivityGold2Binding;

public class GoldActivity2 extends AppCompatActivity {

    private ActivityGold2Binding mBinding;
    private GoldAdapter2 mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityGold2Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialize(){
        CommonClass.sGoldDTO.clear();

        String[] mClientNames = {"납품업체", "테스트1", "테스트테스트"};
        String[] mGoldNames = {"MountainGold", "CoxGold", "LostGold"};
        double[] mStockGold = {1.2, 32.2, 33};

        for(int i = 0; i < 3; i++){
            GoldDTO goldDTO = new GoldDTO();
            goldDTO.setId(i);
            goldDTO.setClientName(mClientNames[i]);
            goldDTO.setGoldName(mGoldNames[i]);
            goldDTO.setStockGold(mStockGold[i]);

            CommonClass.sGoldDTO.add(goldDTO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);
        mBinding.btnPlantSearch.setOnClickListener(btnPlantSearchOnClickListener);

        mAdapter = new GoldAdapter2();

        mAdapter.setOnItemClickListener(goldAdapterOnItemClickListener);

        mBinding.listGold.setAdapter(mAdapter);
        mBinding.listGold.setLayoutManager(new LinearLayoutManager(GoldActivity2.this, LinearLayoutManager.VERTICAL, false));
    }

    GoldAdapter2.OnItemClickListener goldAdapterOnItemClickListener = new GoldAdapter2.OnItemClickListener() {
        @Override
        public void onItemClick(int pos) {
            Intent intent = new Intent(GoldActivity2.this, GoldInOutputHistoryActivity.class);
            intent.putExtra("pos", pos);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

    View.OnClickListener btnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    View.OnClickListener btnPlantSearchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(GoldActivity2.this, SearchActivity.class);
            intent.putExtra("tag", "Gold");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };
}