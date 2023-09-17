package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ActivityFilterBinding;

public class FilterActivity extends Activity {

    private ActivityFilterBinding mBinding;

    private String mManager = "", mOrderDate = "", mDeadlineDate = "", mName = "", mDentalFormula = "", mRelease = "";

    private String mId = "";
    private int mCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBinding = ActivityFilterBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        super.onCreate(savedInstanceState);

        initialSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", MODE_PRIVATE);

        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);

        Intent intent = getIntent();
        mOrderDate = intent.getStringExtra("ordDate");
        mDeadlineDate = intent.getStringExtra("deadDate");
        mName = intent.getStringExtra("searchPatient");
        String type = intent.getStringExtra("type");
        mRelease = intent.getStringExtra("release");

        switch (type) {
            case "ord":
                mManager = intent.getStringExtra("manager");
                mBinding.layoutRelease.setVisibility(View.GONE);
                break;
            case "req":
                mBinding.layoutSb.setVisibility(View.GONE);
                mBinding.layoutRelease.setVisibility(View.GONE);
                mManager = "";
                break;
            case "rel":
                mBinding.layoutSb.setVisibility(View.GONE);
                mManager = "";
                break;
        }

        if (type.equals("rel")) {
            switch (mRelease) {
                case "":
                    mBinding.radioGroupRelease.check(R.id.radio_all);
                    break;
                case "AND outDate IS NULL":
                    mBinding.radioGroupRelease.check(R.id.radio_before);
                    break;
                case "AND outDate IS NOT NULL":
                    mBinding.radioGroupRelease.check(R.id.radio_after);
                    break;
            }
        }

        if (mCode == CommonClass.UI || type.equals("rel")) {
            mBinding.layoutDentalFormula.setVisibility(View.GONE);
        } else {
            mDentalFormula = intent.getStringExtra("dent");
            mDentalFormula = mDentalFormula.replace("上", "상");
            mDentalFormula = mDentalFormula.replace("下", "하");

            String[] dental;
            if (mCode == CommonClass.PDL) {
                dental = mDentalFormula.split("/", 4);
            } else {
                dental = mDentalFormula.split("&", 4);
            }

            mBinding.editDentalFormula10.setText(dental[0]);
            mBinding.editDentalFormula20.setText(dental[1]);
            mBinding.editDentalFormula30.setText(dental[2]);
            mBinding.editDentalFormula40.setText(dental[3]);
        }

        if (mName.equals("")) {
            mBinding.searchName.setText("");
        } else
            mBinding.searchName.setText(mName);

        mBinding.btnOrderManager.setChecked(!mManager.equals(""));

        if (!mOrderDate.equals("")) {
            mBinding.txtOrderDate2.setText(mOrderDate);
        }

        if (!mDeadlineDate.equals("")) {
            mBinding.txtDeadlineDate2.setText(mDeadlineDate);
        }

        mBinding.datePicker.setVisibility(View.GONE);

        mBinding.layoutOrderDate.setOnClickListener(orderDateClick);
        mBinding.layoutDeadlineDate.setOnClickListener(deadlineDateClick);
        mBinding.btnOrderManager.setOnCheckedChangeListener(managerSwitchChange);

        mBinding.btnReset.setOnClickListener(resetButtonClick);
        mBinding.btnSearch.setOnClickListener(searchButtonClick);
//        mBinding.txtClose.setOnClickListener(closeButtonClick);
        mBinding.radioGroupRelease.setOnCheckedChangeListener(radioGroupCheck);
    }

    View.OnClickListener orderDateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String today = getTimeNow();

            SpannableString content = new SpannableString(mBinding.txtOrderDate.getText().toString());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            mBinding.txtOrderDate.setText(content);

            int orderYear = 0, orderMonth = 0, orderDay = 0;
            if (mOrderDate.equals("")) {
                mBinding.txtOrderDate2.setText(today);
                orderYear = Integer.parseInt(today.substring(0, 4));
                orderMonth = Integer.parseInt(today.substring(5, 7)) - 1;
                orderDay = Integer.parseInt(today.substring(8, 10));
            } else {
                mBinding.txtOrderDate2.setText(mOrderDate);
                orderYear = Integer.parseInt(mOrderDate.substring(0, 4));
                orderMonth = Integer.parseInt(mOrderDate.substring(5, 7)) - 1;
                orderDay = Integer.parseInt(mOrderDate.substring(8, 10));
            }
            mBinding.datePicker.setVisibility(View.VISIBLE);
            mBinding.datePicker.init(orderYear, orderMonth, orderDay, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String date = year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);
                    mBinding.txtOrderDate2.setText(date);
                }
            });
        }
    };

    View.OnClickListener deadlineDateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String today = getTimeNow();

            SpannableString content = new SpannableString(mBinding.txtDeadlineDate.getText().toString());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            mBinding.txtDeadlineDate.setText(content);
            int deadlineYear = 0, deadlineMonth = 0, deadlineDay = 0;
            if (mDeadlineDate.equals("")) {
                mBinding.txtDeadlineDate2.setText(today);
                deadlineYear = Integer.parseInt(today.substring(0, 4));
                deadlineMonth = Integer.parseInt(today.substring(5, 7)) - 1;
                deadlineDay = Integer.parseInt(today.substring(8, 10));
            } else {
                mBinding.txtDeadlineDate2.setText(mDeadlineDate);
                deadlineYear = Integer.parseInt(mDeadlineDate.substring(0, 4));
                deadlineMonth = Integer.parseInt(mDeadlineDate.substring(5, 7)) - 1;
                deadlineDay = Integer.parseInt(mDeadlineDate.substring(8, 10));
            }
            mBinding.datePicker.setVisibility(View.VISIBLE);
            mBinding.datePicker.init(deadlineYear, deadlineMonth, deadlineDay, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String date = year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);
                    mBinding.txtDeadlineDate2.setText(date);
                }
            });
        }
    };

    CompoundButton.OnCheckedChangeListener managerSwitchChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mManager = (isChecked) ? mId : "";
        }
    };

    RadioGroup.OnCheckedChangeListener radioGroupCheck = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radio_all:
                    mRelease = "";
                    break;
                case R.id.radio_before:
                    mRelease = "AND outDate IS NULL";
                    break;
                case R.id.radio_after:
                    mRelease = "AND outDate IS NOT NULL";
                    break;
            }
        }
    };

    View.OnClickListener resetButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mBinding.searchName.setText("");
            mBinding.txtOrderDate.setText(getString(R.string.request_date));
            mBinding.txtDeadlineDate.setText(getString(R.string.deadlline_date));
            mBinding.txtOrderDate2.setText("");
            mBinding.txtDeadlineDate2.setText("");
            mBinding.editDentalFormula10.setText("");
            mBinding.editDentalFormula20.setText("");
            mBinding.editDentalFormula30.setText("");
            mBinding.editDentalFormula40.setText("");
            mBinding.datePicker.setVisibility(View.GONE);
            mBinding.btnOrderManager.setChecked(false);
            mRelease = "";
            mBinding.radioGroupRelease.check(R.id.radio_all);
        }
    };

    View.OnClickListener searchButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!CommonClass.ic.GetInternet(FilterActivity.this.getApplicationContext())) {
                if (!FilterActivity.this.isFinishing())
                    CommonClass.showDialog(FilterActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> { }, false);
            } else {
                Intent intent = new Intent();
                mOrderDate = mBinding.txtOrderDate2.getText().toString();
                mDeadlineDate = mBinding.txtDeadlineDate2.getText().toString();
                mName = mBinding.searchName.getText().toString();
                if (mCode == CommonClass.PDL) {
                    mDentalFormula = mBinding.editDentalFormula10.getText().toString() + "/" + mBinding.editDentalFormula20.getText().toString() + "/" + mBinding.editDentalFormula30.getText().toString() + "/" + mBinding.editDentalFormula40.getText().toString();
                } else {
                    mDentalFormula = mBinding.editDentalFormula10.getText().toString() + "&" + mBinding.editDentalFormula20.getText().toString() + "&" + mBinding.editDentalFormula30.getText().toString() + "&" + mBinding.editDentalFormula40.getText().toString();
                }
                intent.putExtra("ordDate", mOrderDate);
                intent.putExtra("deadDate", mDeadlineDate);
                intent.putExtra("manager", mManager);
                intent.putExtra("searchClient", mName);
                intent.putExtra("searchPatient", mName);
                intent.putExtra("dent", mDentalFormula);
                intent.putExtra("release", mRelease);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    View.OnClickListener closeButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public String getTimeNow() {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date.getTime());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

