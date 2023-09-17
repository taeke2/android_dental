package gbsoft.com.dental_gb;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import gbsoft.com.dental_gb.databinding.ActivityFilterBinding;
import gbsoft.com.dental_gb.databinding.DialogCheckBottomBinding;


public class FilterBottomDialog extends BottomSheetDialogFragment {
//    private DialogCheckBottomBinding mBinding;
    private ActivityFilterBinding mBinding;
    private FilterBottomDialog mCheckBottomDialog;
    private Context mContext;
    private static CheckDialogClickListener mCheckDialogClickListener;

    private FilterBottomDialogClickListener mFilterBottomDialogClickListener;

    private String mTitle;
    private String mContent;
    private boolean mIsCancelOn;

    private StringBuilder builder;


    private String mClientName = "";
    private String mPatientName = "";
    private String mOrderDate = "";
    private String mDeadlineDate = "";
    private String mOutDate = "";
    private String mManager = "";
    private String mDentalFormula = "";
    private String mRelease = "";
    private String mType = "";

    private String mId = "";
    private int mCode = -1;

    // request, release
    public FilterBottomDialog(Context context, FilterBottomDialogClickListener filterBottomDialogClickListener,
                              String orderDate, String deadLineDate,
                              String manager, String clientName,
                              String patientName, String type,
                              String dentalFormula, String release,
                              String id, int code) {

        this.mContext = context;
        this.mFilterBottomDialogClickListener = filterBottomDialogClickListener;
        this.mOrderDate = orderDate;
        this.mDeadlineDate = deadLineDate;
        this.mManager = manager;
        this.mClientName = clientName;
        this.mPatientName = patientName;
        this.mType = type;
        this.mDentalFormula = dentalFormula;
        this.mRelease = release;

        this.mId = id;
        this.mCode = code;

    }

    // process
    public FilterBottomDialog(Context context, FilterBottomDialogClickListener filterBottomDialogClickListener,
                              String orderDate, String deadLineDate,
                              String manager, String clientName,
                              String patientName, String type,
                              String dentalFormula,
                              String id, int code) {

        this.mContext = context;
        this.mFilterBottomDialogClickListener = filterBottomDialogClickListener;
        this.mOrderDate = orderDate;
        this.mDeadlineDate = deadLineDate;
        this.mManager = manager;
        this.mClientName = clientName;
        this.mPatientName = patientName;
        this.mType = type;
        this.mDentalFormula = dentalFormula;

        this.mId = id;
        this.mCode = code;

    }


    // activity에서 호출되는 경우의 수만큼 만들어야함.

    public FilterBottomDialog(Context context,
                              CheckDialogClickListener checkDialogClickListener,
                              String title,
                              String content,
                              boolean isCancelOn,

                              String ClientName,
                              String PatientName,
                              String OrderDate,
                              String DeadlineDate,
                              String Manager,
                              String DentalFormula,
                              String Release,
                              String Type,

                              String Id,
                              int Code) {
        this.mContext = context;
        this.mCheckDialogClickListener = checkDialogClickListener;
        this.mTitle = title;
        this.mContent = content;
        this.mIsCancelOn = isCancelOn;
        this.mCheckBottomDialog = this;

        this.mClientName = ClientName;
        this.mPatientName = PatientName;
        this.mOrderDate = OrderDate;
        this.mDeadlineDate = DeadlineDate;
        this.mManager = Manager;
        this.mDentalFormula = DentalFormula;
        this.mRelease = Release;
        this.mType = Type;

        this.mId = Id;
        this.mCode = Code;
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = ActivityFilterBinding.inflate(inflater, container, false);
        setStyle(FilterBottomDialog.STYLE_NORMAL, getTheme());


        switch (mType) {
            case "ord":
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

        if (mType.equals("rel")) {
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

        if (mCode == CommonClass.UI || mType.equals("rel")) {
            mBinding.layoutDentalFormula.setVisibility(View.GONE);
        } else if (mCode == CommonClass.YK) {
            mBinding.titleName.setText(mContext.getString(R.string.client));
            mBinding.layoutSb.setVisibility(View.GONE);
        } else {

//            mDentalFormula = intent.getStringExtra("dent");
            mDentalFormula = mDentalFormula.replace("上", "상");
            mDentalFormula = mDentalFormula.replace("下", "하");

            String[] dental;
            if (mCode == CommonClass.PDL || mCode == CommonClass.YK) {
                dental = mDentalFormula.split("/", 4);
            } else {
                dental = mDentalFormula.split("&", 4);
            }

//            mBinding.editDentalFormula10.setText(dental[0]);
//            mBinding.editDentalFormula20.setText(dental[1]);
//            mBinding.editDentalFormula30.setText(dental[2]);
//            mBinding.editDentalFormula40.setText(dental[3]);
        }

        if (mPatientName.equals("")) {
            mBinding.searchName.setText("");
        } else
            mBinding.searchName.setText(mPatientName);

        mBinding.btnOrderManager.setChecked(!mManager.equals(""));

        if (!mOrderDate.equals("")) {
            mBinding.txtOrderDate2.setText(mOrderDate);
        }

        if (!mDeadlineDate.equals("")) {
            mBinding.txtDeadlineDate2.setText(mDeadlineDate);
        }

        if(mCode == CommonClass.YK){
            mBinding.txtClientName.setText("거래처/제품명");
            if(mType.equals("rel")){
                mBinding.layoutOrderDate.setVisibility(View.GONE);
                mBinding.layoutDeadlineDate.setVisibility(View.GONE);
                mBinding.layoutRelease.setVisibility(View.GONE);
                mBinding.layoutOutDate.setVisibility(View.VISIBLE);
            } else if (mType.equals("req")){
                mBinding.layoutRelease.setVisibility(View.GONE);
                mBinding.layoutDentalFormula.setVisibility(View.VISIBLE);
                builder = new StringBuilder();
            }
        }

        mBinding.datePicker.setVisibility(View.GONE);

        mBinding.layoutOrderDate.setOnClickListener(orderDateClick);
        mBinding.layoutDeadlineDate.setOnClickListener(deadlineDateClick);
        mBinding.btnOrderManager.setOnCheckedChangeListener(managerSwitchChange);

        mBinding.btnReset.setOnClickListener(resetButtonClick);
        mBinding.btnSearch.setOnClickListener(searchButtonClick);
//        mBinding.txtClose.setOnClickListener(closeButtonClick);
        mBinding.radioGroupRelease.setOnCheckedChangeListener(radioGroupCheck);

        mBinding.layoutOutDate.setOnClickListener(outDateClick);

        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString("title");
            mContent = savedInstanceState.getString("content");
            mIsCancelOn = savedInstanceState.getBoolean("isCancelOn");
        }

//        mBinding.txtTitle.setText(mTitle);
//        mBinding.txtContent.setText(mContent);
//
//        mBinding.btnOk.setOnClickListener(v -> {
//            mCheckDialogClickListener.onPositiveClick();
//            dismiss();
//        });
//
//        if (mIsCancelOn) {
//            mBinding.btnCancel.setOnClickListener(v -> dismiss());
//            mBinding.btnCancel.setVisibility(View.VISIBLE);
//        } else {
//            mBinding.btnCancel.setVisibility(View.GONE);
//        }

        return mBinding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) { // Dialog 특정 layout GONE에서 VISIBLE로 변경되었을때 제대로 확장이 안되서 다음과 같은 코드 추가함
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
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

    View.OnClickListener outDateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String today = getTimeNow();

            SpannableString content = new SpannableString(mBinding.txtOutDate.getText().toString());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            mBinding.txtOutDate.setText(content);
            int outYear = 0, outMonth = 0, outDay = 0;
            if (mOutDate.equals("")) {
                mBinding.txtOutDateValue.setText(today);
                outYear = Integer.parseInt(today.substring(0, 4));
                outMonth = Integer.parseInt(today.substring(5, 7)) - 1;
                outDay = Integer.parseInt(today.substring(8, 10));
            } else {
                mBinding.txtOutDateValue.setText(mOutDate);
                outYear = Integer.parseInt(mOutDate.substring(0, 4));
                outMonth = Integer.parseInt(mOutDate.substring(5, 7)) - 1;
                outDay = Integer.parseInt(mOutDate.substring(8, 10));
            }
            mBinding.datePicker.setVisibility(View.VISIBLE);
            mBinding.btnReset.requestFocus();
            mBinding.datePicker.init(outYear, outMonth, outDay, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String date = year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);
                    mBinding.txtOutDateValue.setText(date);
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
            mBinding.txtOutDate.setText("출고일자");
            mBinding.txtOutDateValue.setText("");
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
            mOrderDate = mBinding.txtOrderDate2.getText().toString();
            mDeadlineDate = mBinding.txtDeadlineDate2.getText().toString();
            mOutDate = mBinding.txtOutDateValue.getText().toString();

            mClientName = mPatientName = mBinding.searchName.getText().toString();
            if (mCode == CommonClass.PDL) {
                mDentalFormula = mBinding.editDentalFormula10.getText().toString() + "/" + mBinding.editDentalFormula20.getText().toString() + "/" + mBinding.editDentalFormula30.getText().toString() + "/" + mBinding.editDentalFormula40.getText().toString();
            } else if(mCode == CommonClass.YK){
                if(!mBinding.editDentalFormula10.getText().toString().equals("")){
                    String[] dental1 = mBinding.editDentalFormula10.getText().toString().split(""); // 입력한 문자열 배열로 생성

                    for (String name : dental1) { // 배열 item 마다 ,추가
                        if(name.equals("")) continue;
//                        Log.v("filter_dental:", name);
                        builder.append(name).append(",");
                    }

                    mDentalFormula = builder.substring(0, builder.length()-1) + "/"; // 마지막 문자에 있는 , 제거하고 뒤에 / 추가
                    builder.delete(0, builder.toString().length()); // builder 초기화
                }

                if(!mBinding.editDentalFormula20.getText().toString().equals("")){
                    String[] dental2 = mBinding.editDentalFormula20.getText().toString().split("");
                    for (String name : dental2) {
                        if(name.equals("")) continue;
                        builder.append(name).append(",");
                    }

                    mDentalFormula += builder.substring(0, builder.length()-1) + "/"; // 마지막 문자에 있는 , 제거하고 뒤에 / 추가
                    builder.delete(0, builder.toString().length());
                }

                if(!mBinding.editDentalFormula30.getText().toString().equals("")){
                    String[] dental3 = mBinding.editDentalFormula30.getText().toString().split("");
                    for (String name : dental3) {
                        if(name.equals("")) continue;
                        builder.append(name).append(",");
                    }

                    mDentalFormula += builder.substring(0, builder.length()-1) + "/"; // 마지막 문자에 있는 , 제거하고 뒤에 / 추가
                    builder.delete(0, builder.toString().length());
                }

                if(!mBinding.editDentalFormula40.getText().toString().equals("")){
                    String[] dental4 = mBinding.editDentalFormula40.getText().toString().split("");
                    for (String name : dental4) {
                        if(name.equals("")) continue;
                        builder.append(name).append(",");
                    }

                    mDentalFormula += builder.substring(0, builder.length()-1) + "/"; // 마지막 문자에 있는 , 제거하고 뒤에 / 추가
                    builder.delete(0, builder.toString().length());
                }
            } else {
                mDentalFormula = mBinding.editDentalFormula10.getText().toString() + "&" + mBinding.editDentalFormula20.getText().toString() + "&" + mBinding.editDentalFormula30.getText().toString() + "&" + mBinding.editDentalFormula40.getText().toString();
            }

            mFilterBottomDialogClickListener.onSearchClick(
                    mOrderDate, mDeadlineDate, mOutDate, mManager, mClientName,
                    mPatientName, mDentalFormula, mRelease
            );

            dismiss();
        }
    };

    View.OnClickListener closeButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    public String getTimeNow() {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date.getTime());
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("title", mTitle);
        outState.putString("content", mContent);
        outState.putBoolean("isCancelOn", mIsCancelOn);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}