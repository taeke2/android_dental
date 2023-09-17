package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import gbsoft.com.dental_gb.databinding.ActivityPlantDetailBinding;

public class PlantDetailActivity extends AppCompatActivity {

    private ActivityPlantDetailBinding mBinding;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPlantDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void initialize(){
        Intent intent = getIntent();
        index = intent.getIntExtra("pos", -1);

        if(index < 0) finish(); // error
        PlantListDTO plantListDTO = CommonClass.sPlantListDTO.get(index);
        mBinding.txtPlantNameValue.setText(plantListDTO.getPlantName());
        mBinding.txtSerialNumberValue.setText(plantListDTO.getSN());
        mBinding.txtIntroductionDateValue.setText(plantListDTO.getIntroductionDate());
        mBinding.txtVendorValue.setText(plantListDTO.getVendor());
        mBinding.txtPersonInChargeOfVendorValue.setText(plantListDTO.getPersonInChargeOfVendor());
//        mBinding.txtPhoneNumberValue.setText(plantListDTO.getPhoneNumber());
//        mBinding.txtEmailValue.setText(plantListDTO.getEmail());
        mBinding.txtAsNumberValue.setText(plantListDTO.getAsNumber());
        mBinding.txtRemarkValue.setText(plantListDTO.getRemark());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);
    }

    View.OnClickListener btnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}