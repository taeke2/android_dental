package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import gbsoft.com.dental_gb.databinding.ActivityGoldInOutputRegistryBinding;

public class GoldInOutputRegistryActivity extends AppCompatActivity {

    private ActivityGoldInOutputRegistryBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityGoldInOutputRegistryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialize(){
        String[] mGoldTypes = {"MountainGold", "CoxGold", "LostGold"};
        String[] mProducts = {"제품", "테스트1", "테스트테스트"};

        ArrayAdapter<String> adapterGoldTypeSpinner = new ArrayAdapter<>(GoldInOutputRegistryActivity.this, R.layout.support_simple_spinner_dropdown_item, mGoldTypes);
        mBinding.spinnerGold.setAdapter(adapterGoldTypeSpinner);

        ArrayAdapter<String> adapterProductSpinner = new ArrayAdapter<>(GoldInOutputRegistryActivity.this, R.layout.support_simple_spinner_dropdown_item, mProducts);
        mBinding.spinnerEquipment.setAdapter(adapterProductSpinner);

        mBinding.chkWarehousing.setChecked(true);
        mBinding.chkUse.setChecked(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);
        mBinding.btnPlantSearch.setOnClickListener(btnPlantSearchOnClickListener);
        mBinding.btnSubmit.setOnClickListener(btnSubmitOnClickListener);

        mBinding.chkUse.setOnClickListener(txtUseOnClickListener);
        mBinding.chkWarehousing.setOnClickListener(txtWarehousingOnClickListener);
    }

    View.OnClickListener txtUseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!mBinding.chkUse.isChecked()){
                mBinding.chkUse.setChecked(false);
                mBinding.chkWarehousing.setChecked(true);
                //mBinding.chkUse.setBackgroundResource(R.color.dark_gray);
                mBinding.chkUse.setTextColor(getResources().getColor(R.color.light_gray, null));
                //mBinding.chkWarehousing.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mBinding.chkWarehousing.setTextColor(getResources().getColor(android.R.color.black, null));
            } else {
                mBinding.chkWarehousing.setChecked(false);
                mBinding.chkWarehousing.setTextColor(getResources().getColor(R.color.light_gray, null));
                //mBinding.chkWarehousing.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mBinding.chkUse.setTextColor(getResources().getColor(android.R.color.black, null));
            }
        }
    };

    View.OnClickListener txtWarehousingOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!mBinding.chkWarehousing.isChecked()){
                mBinding.chkWarehousing.setChecked(false);
                mBinding.chkUse.setChecked(true);
                //mBinding.chkUse.setBackgroundResource(R.color.dark_gray);
                mBinding.chkWarehousing.setTextColor(getResources().getColor(R.color.light_gray, null));
                //mBinding.chkWarehousing.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mBinding.chkUse.setTextColor(getResources().getColor(android.R.color.black, null));
            } else {
                mBinding.chkUse.setChecked(false);
                mBinding.chkUse.setTextColor(getResources().getColor(R.color.light_gray, null));
                //mBinding.chkWarehousing.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mBinding.chkWarehousing.setTextColor(getResources().getColor(android.R.color.black, null));
            }
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
            Intent intent = new Intent(GoldInOutputRegistryActivity.this, SearchActivity.class);
            intent.putExtra("tag", "Request");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };

    View.OnClickListener btnSubmitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: 모든 문항이 입력이 되었는지 검사

        }
    };
}