package gbsoft.com.dental_gb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.DialogOrderCommitBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class OrderCommitDialog extends BottomSheetDialogFragment {
    private DialogOrderCommitBinding mBinding;
    private static Context mContext;
    private static OrderItem mOrderItem;
    private static OrderCommitDialogClickListener mClickListener;
    private int mType;  // 0 : processCommit, 1 : processFaulty
    private String mIp;
    private String mId;
    private int mCode;
    private int mProcessState;
    private String mFaultyType;
    private int mArtiCode = -1;
    private int mPreArtiPos = 0;
    private boolean isFinish = false;

    public OrderCommitDialog() {
    }

    public OrderCommitDialog(Context context, OrderItem item, OrderCommitDialogClickListener listener, int type) {
        this.mContext = context;
        this.mOrderItem = item;
        this.mClickListener = listener;
        this.mType = type;
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(OrderCommitDialog.STYLE_NORMAL, getTheme());
        mBinding = DialogOrderCommitBinding.inflate(inflater, container, false);

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("auto", MODE_PRIVATE);
        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);

        if (savedInstanceState != null)
            mType = savedInstanceState.getInt("type");

        return mBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("type", mType);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        isFinish = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isFinish = true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] productProcess = mOrderItem.getProductProcess().split("→");
        String[] memoList = mOrderItem.getMemo().split(";", -1);
        mProcessState = Integer.parseInt(mOrderItem.getProcessState());

        mBinding.layoutChkInfo.setVisibility(View.GONE);
        mBinding.layoutArticulator.setVisibility(View.GONE);

        switch (mType) {
            case 0 :
                mBinding.processFaulty.setVisibility(View.GONE);
                mBinding.processCommit.setVisibility(View.VISIBLE);

                mBinding.txtProductName2.setText(mOrderItem.getProductName());
                mBinding.txtProcess.setText(productProcess[mOrderItem.getProcessIdx()]);
                mBinding.editRemark.setText(memoList[mOrderItem.getProcessIdx()]);

                if (mCode == CommonClass.PDL) {
                    mBinding.layoutChkInfo.setVisibility(View.VISIBLE);
                    getCheckInfo();
                } else if (mCode == CommonClass.YK) {
                    mBinding.layoutArticulator.setVisibility(View.VISIBLE);
                    if (CommonClass.sArticulatorSpinner.size() > 0) {
                        mBinding.txtNoneArti.setVisibility(View.GONE);
                        mBinding.spnArticulator.setVisibility(View.GONE);
                        mBinding.checkboxArti.setChecked(false);
                        mBinding.checkboxArti.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            if (isChecked) {
                                mBinding.spnArticulator.setVisibility(View.VISIBLE);
                                mBinding.spnArticulator.setEnabled(true);
                                mBinding.spnArticulator.setSelection(mPreArtiPos);
                            } else {
                                mBinding.spnArticulator.setVisibility(View.GONE);
                                mBinding.spnArticulator.setEnabled(false);
                                mArtiCode = -1;
                            }

                        });
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, CommonClass.sArticulatorSpinner.values().toArray(new String[CommonClass.sArticulatorSpinner.size()]));
                        mBinding.spnArticulator.setAdapter(adapter);
                        mBinding.spnArticulator.setSelection(mPreArtiPos);
                        mBinding.spnArticulator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mArtiCode = CommonClass.sArticulatorDTO.get(position).getCode();
                                mPreArtiPos = position;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    } else {
                        mBinding.spnArticulator.setVisibility(View.GONE);
                        mBinding.txtNoneArti.setVisibility(View.VISIBLE);
                    }
                }

                mBinding.btnCancel2.setOnClickListener(v -> dismiss());
                mBinding.btnProcessCommit.setOnClickListener(v -> {
                    mClickListener.processCommitClick(mOrderItem.getProductInfoCode(),
                            mBinding.editRemark.getText().toString(), mProcessState, mOrderItem.getProcessIdx(),
                            mArtiCode);
                    dismiss();
                });
                break;
            case 1 :
                mBinding.processCommit.setVisibility(View.GONE);
                mBinding.processFaulty.setVisibility(View.VISIBLE);

                mBinding.txtProductName1.setText(mOrderItem.getProductName());
                mBinding.txtFaultyProcess.setText(productProcess[mOrderItem.getProcessIdx()]);

                getFaultyType();

                mBinding.btnCancel1.setOnClickListener(v -> dismiss());
                mBinding.btnFaultyCommit.setOnClickListener(v -> {
                    mClickListener.processFaultyClick(mOrderItem.getProductInfoCode(), mFaultyType, mBinding.txtFaultyHistory.getText().toString(), mOrderItem.getProcessIdx());
                    dismiss();
                });
                break;
        }
    }



    public void getCheckInfo() {
        mBinding.progressbar.setVisibility(View.VISIBLE);
        mBinding.rvChkInfo.setVisibility(View.GONE);

        String[] processNames = mOrderItem.getProductProcess().split("→");
        int ps = (mProcessState > mOrderItem.getProcessIdx()) ? mProcessState : mOrderItem.getProcessIdx() + 1;
//        ArrayList<String> pNames = new ArrayList<>();
//        for (int i = ps; i <= mOrderItem.getProcessIdx(); i++) {
//            pNames.add(processNames[i]);
//        }
        Call<ResponseBody> call = CommonClass.sApiService.getCheckInfo(mOrderItem.getProductCode(), Integer.toString(ps), Integer.toString(mOrderItem.getProcessIdx()), mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);

                        int jsonArr_len = jsonArray.length();
                        ArrayList<CheckInfoItem> list = new ArrayList<>();
                        for (int i = 0; i < jsonArr_len; i++) {
                            CheckInfoItem item = new CheckInfoItem();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String text = jsonObject.getString("chkList");
                            String chkInfo = (text.equals("null")) ? "-" : text;
                            item.setProcessName(processNames[i]);
                            item.setChkInfo(chkInfo);
                            list.add(item);
                        }

                        CheckInfoAdapter checkInfoAdapter = new CheckInfoAdapter(list);
                        mBinding.rvChkInfo.setAdapter(checkInfoAdapter);
                        mBinding.rvChkInfo.setLayoutManager(new LinearLayoutManager(mContext));

                        mBinding.progressbar.setVisibility(View.GONE);
                        mBinding.rvChkInfo.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error getCheckInfo");
                        if (!isFinish)
                            CommonClass.showDialog(mContext, getString(R.string.error_title), getString(R.string.catch_error_content), () -> {}, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!isFinish)
                            CommonClass.showDialog(mContext, getString(R.string.error_title), getString(R.string.catch_error_content), () -> {}, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!isFinish)
                        CommonClass.showDialog(mContext, getString(R.string.error_title), getString(R.string.response_error_content), () -> {}, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!isFinish)
                    CommonClass.showDialog(mContext, getString(R.string.error_title), getString(R.string.connect_error_content), () -> {}, false);
            }
        });
    }

    public void getFaultyType() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, CommonClass.sFaultyType);
        mBinding.spnFaultyType.setAdapter(arrayAdapter);
        mBinding.spnFaultyType.setSelection(0);
        // mBinding.txtFaultyType.setText(mBinding.spnFaultyType.getSelectedItem().toString());
        // mBinding.txtFaultyType.setOnClickListener(v -> mBinding.spnFaultyType.performClick());
        mBinding.spnFaultyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFaultyType = mBinding.spnFaultyType.getSelectedItem().toString();
                // mBinding.txtFaultyType.setText(mBinding.spnFaultyType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mFaultyType = "";
                // mBinding.txtFaultyType.setText("");
            }
        });
    }
}
