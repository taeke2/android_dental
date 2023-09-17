package gbsoft.com.dental_gb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import gbsoft.com.dental_gb.databinding.ActivityMaterialsLemonRegistryBinding;

public class MaterialsLemonRegistryActivity extends AppCompatActivity {

    private ActivityMaterialsLemonRegistryBinding mBinding;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMaterialsLemonRegistryBinding.inflate(getLayoutInflater());
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
        index = intent.getIntExtra("pos", -1);
        if(index < 0) finish();

        MaterialDTO materialDTO = CommonClass.sMaterialDTO.get(index);

        mBinding.txtMaterialsNameValue.setText(materialDTO.getMaterialName());

        mBinding.txtOutStockValue.setText(materialDTO.getOutStock()+"");
        mBinding.txtStockCountValue.setText(materialDTO.getStock()+"");
//        mBinding.txtClientNameValue.setText(materialDTO.getClientName());
//        mBinding.txtWarehousingDateValue.setText(materialDTO.getWarehousingDate());
        mBinding.txtWarehousingCountValue.setText(materialDTO.getInStock()+"");
        
//        String[] lemonReasons = {"불량", "파손", "기타"};

//        ArrayAdapter<String> adapterProductSpinner = new ArrayAdapter<>(MaterialsLemonRegistryActivity.this, R.layout.support_simple_spinner_dropdown_item, lemonReasons);
//        mBinding.spinnerLemonReasons.setAdapter(adapterProductSpinner);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBinding.btnBack.setOnClickListener(btnBackOnClickListener);
//        mBinding.btnSubmit.setOnClickListener(btnSubmitOnClickListener);
//        mBinding.spinnerLemonReasons.setOnItemSelectedListener(spinnerLemonReasonOnItemSelectedListener);
    }


    AdapterView.OnItemSelectedListener spinnerLemonReasonOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            if(mBinding.spinnerLemonReasons.getSelectedItem().toString().equals("기타(직접 입력)")){
//                mBinding.editLemonReason.setVisibility(View.VISIBLE);
//            } else {
//                mBinding.editLemonReason.setVisibility(View.GONE);
//            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemClickListener spinnerLemonReasonOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            if(mBinding.spinnerLemonReasons.getSelectedItem().toString().equals("기타")){
//                mBinding.editLemonReason.setVisibility(View.VISIBLE);
//            } else {
//                mBinding.editLemonReason.setVisibility(View.GONE);
//           }
        }
    };

    View.OnClickListener btnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    View.OnClickListener btnSubmitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}