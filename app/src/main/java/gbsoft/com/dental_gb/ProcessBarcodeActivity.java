package gbsoft.com.dental_gb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

//import gbsoft.com.dental_gb.databinding.ActivityProcessBarcodeBinding;
import gbsoft.com.dental_gb.databinding.ActivityProcessBarcodeCustomBinding;
import gbsoft.com.dental_gb.dialog.ProcessBarcodeDialog;
import gbsoft.com.dental_gb.dialog.ProcessBarcodeDialogClickListener;
import gbsoft.com.dental_gb.dialog.SpinnerDialog;
import gbsoft.com.dental_gb.dto.ArticulatorDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcessBarcodeActivity extends AppCompatActivity {
//    private ActivityProcessBarcodeBinding mBinding;
    private ActivityProcessBarcodeCustomBinding mBinding;
    private BeepManager mBeepManager;
    private String mLastText = "";
    private ArrayList<String> mScanBarcode = new ArrayList<>();
    private boolean mIsSingle = false;
    private Intent mIntent = null;

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";
    private String mProcessName = "";
    private String[] mProcessNameList;
    private String mName = "";

    private int mArtiBoxIdx;
    private int mArtiCode;
    private int mCnt = 0;
    private ProcessBarcodeDialog mProcessDialog;

    private CaptureManager mCapture;

    private BarcodeCallback mBarcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(mLastText)) {
                return;
            }

            boolean isSame = false;
            for (String b : mScanBarcode) {
                if (b.equals(result.getText())) {
                    mBinding.txtWarn.setText(getString(R.string.added_barcode, result.getText()));
                    mBinding.txtWarn.setVisibility(View.VISIBLE);
                    isSame = true;
                    break;
                }
            }

            if (!isSame) {
                String boxNo = result.getText();
//                if (boxNo.charAt(0) == 65)
//                    boxNo = boxNo.substring(1);

                if (CommonClass.getDigitIndex(boxNo) >= 0) {
                    mScanBarcode.add(boxNo);
                    mBinding.txtWarn.setVisibility(View.GONE);
                }
                //mScanBarcodeTemp.add(result.getText());
            }
            mLastText = result.getText();
            mBinding.barcodeScanner.setStatusText(result.getText());
            mBeepManager.playBeepSoundAndVibrate();
            if (mIsSingle && mScanBarcode.size() == 1) {
                ProcessBarcodeDialog bottomDialog = new ProcessBarcodeDialog(ProcessBarcodeActivity.this, singleBarcodeDialogClickListener, mScanBarcode, 1);
                bottomDialog.show(getSupportFragmentManager(), bottomDialog.getTag());
                mBinding.barcodeScanner.pause();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mBinding = ActivityProcessBarcodeBinding.inflate(getLayoutInflater());
        mBinding = ActivityProcessBarcodeCustomBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mCapture = new CaptureManager(ProcessBarcodeActivity.this, mBinding.barcodeScanner);
        mCapture.initializeFromIntent(getIntent(), savedInstanceState);
        mCapture.setShowMissingCameraPermissionDialog(false);
        mCapture.decode();

        initialSet();
    }

    private void initialSet() {
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        mBinding.barcodeScanner.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        mBinding.barcodeScanner.initializeFromIntent(getIntent());
        mBinding.barcodeScanner.decodeContinuous(mBarcodeCallback);

        mBeepManager = new BeepManager(ProcessBarcodeActivity.this);

        mIntent = getIntent();
        mIsSingle = mIntent.getIntExtra("singleProcess", -1) > 0;
        if (mIsSingle) {
            mBinding.txtWarn.setVisibility(View.GONE);
            mBinding.btnFinish.setVisibility(View.GONE);
        } else {
            mBinding.btnFinish.setOnClickListener(finishClick);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);
        String processName = sharedPreferences.getString("processName", "");
        mName = sharedPreferences.getString("userName", "");

        mProcessNameList = processName.split(",");

        mServerPath = sharedPreferences.getString("address", "");
        if (CommonClass.sApiService == null)
            CommonClass.getApiService(mServerPath);

         getArticulatorList();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mBinding.barcodeScanner.resume();
        mCapture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
//        mBinding.barcodeScanner.pause();
        mCapture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCapture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCapture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mCapture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private ProcessBarcodeDialogClickListener barcodeDialogClickListener = new ProcessBarcodeDialogClickListener() {
        @Override
        public void onStartClick(int artiBox, int artiIdx, ProcessBarcodeDialog dialog) {
            // 공정 시작
            mProcessDialog = dialog;
            mCnt = 0;
            mArtiBoxIdx = artiBox;
            mArtiCode = artiIdx < 0 ? -1 : CommonClass.sArticulatorDTO.get(artiIdx).getCode();
            processHandler.sendEmptyMessage(1);
        }

        @Override
        public void onFinishClick(int artiBox, int artiIdx, ProcessBarcodeDialog dialog) {
            mProcessDialog = dialog;
            mCnt = 0;
            mArtiBoxIdx = artiBox;
            mArtiCode = artiIdx < 0 ? -1 : CommonClass.sArticulatorDTO.get(artiIdx).getCode();

            processHandler.sendEmptyMessage(2);
        }
    };



    private ProcessBarcodeDialogClickListener singleBarcodeDialogClickListener = new ProcessBarcodeDialogClickListener() {
        @Override
        public void onStartClick(int artiBox, int artiIdx, ProcessBarcodeDialog dialog) {
            dialog.dismiss();
            mIntent.putExtra("boxNo", mScanBarcode.get(0));
            mIntent.putExtra("artiBox", artiBox);
            mIntent.putExtra("artiIdx", artiIdx);
            setResult(RESULT_OK, mIntent);
            finish();
        }

        @Override
        public void onFinishClick(int artiBox, int artiIdx, ProcessBarcodeDialog dialog) {
        }
    };

    private View.OnClickListener finishClick = v -> {
        if (mScanBarcode.size() <= 0)
            finish();
        else {
            ProcessBarcodeDialog bottomDialog = new ProcessBarcodeDialog(ProcessBarcodeActivity.this, barcodeDialogClickListener, mScanBarcode, 0);
            bottomDialog.show(getSupportFragmentManager(), bottomDialog.getTag());
            mBinding.barcodeScanner.pause();
        }
    };

    public void barcodeResume() {
        mCapture.onResume();
        mLastText = "";
        if (mIsSingle)
            mScanBarcode.clear();
    }

    private void getArticulatorList() {
        Call<ResponseBody> call = CommonClass.sApiService.getArticulator();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    try {
                        CommonClass.sArticulatorDTO.clear();
                        CommonClass.sArticulatorSpinner.clear();
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        int arrayLen = jsonArray.length();
                        if (arrayLen != 0) {
                            for (int i = 0; i < arrayLen; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                CommonClass.sArticulatorDTO.add(new ArticulatorDTO(
                                        jsonObject.getInt("articulatorCode"),
                                        jsonObject.getString("articulatorName"),
                                        jsonObject.getString("user").equals("null") ? "" : jsonObject.getString("user"),
                                        jsonObject.getString("useDateTime").equals("null") ? "" : jsonObject.getString("useDateTime")
                                ));
                                CommonClass.sArticulatorSpinner.put(i, jsonObject.getString("articulatorName"));
                            }
                        }

                    } catch (IOException | JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!ProcessBarcodeActivity.this.isFinishing())
                            CommonClass.showDialog(ProcessBarcodeActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessBarcodeActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessBarcodeActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessBarcodeActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessBarcodeActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    private Handler processHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1 :
                    // processStart
                    if (mProcessNameList.length > 1) {
                        SpinnerDialog spinnerDialog = new SpinnerDialog(ProcessBarcodeActivity.this, (selected, dialog) -> {
                            dialog.dismiss();
                            mProcessName = mProcessNameList[selected];
                            startProcessApi(mScanBarcode.get(mCnt), mCnt != mArtiBoxIdx ? -1 : mArtiCode);
                        }, getString(R.string.process_name_select,
                                getString(R.string.dialog_box_no,
                                        Integer.parseInt(mScanBarcode.get(mCnt).substring(CommonClass.getDigitIndex(mScanBarcode.get(mCnt))))))
                                 , mProcessNameList);
                        spinnerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        spinnerDialog.setCanceledOnTouchOutside(false); // 다이얼로그 밖에 터치 시 종료
                        spinnerDialog.setCancelable(false); // 다이얼로그 취소 가능 (back key)
                        spinnerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 다이얼로그 background 적용하기 위한 코드 (radius 적용)
                        spinnerDialog.show();
                    } else {
                        mProcessName = mProcessNameList[0];
                        startProcessApi(mScanBarcode.get(mCnt), mCnt != mArtiBoxIdx ? -1 : mArtiCode);
                    }
                    break;
                case 11 :
                    // processStart End
                    mProcessDialog.dismiss();
                    Toast.makeText(ProcessBarcodeActivity.this, getString(R.string.process_success, getString(R.string.dialog_process_start), mCnt, mScanBarcode.size()), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 2 :
                    // processFinish
                    getProcess(mScanBarcode.get(mCnt));
                    break;
                case 22 :
                    // processFinish End
                    mProcessDialog.dismiss();
                    Toast.makeText(ProcessBarcodeActivity.this, getString(R.string.process_success, getString(R.string.dialog_process_finish), mCnt, mScanBarcode.size()), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };

    private void startProcessApi(String scanBoxNo, int artiCode) {
        Call<ResponseBody> call = CommonClass.sApiService.processStart(
                scanBoxNo, artiCode, mProcessName, CommonClass.getCurrentTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())) ,mName);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    mCnt++;
                    if (mCnt >= mScanBarcode.size())
                        processHandler.sendEmptyMessage(11);
                    else
                        processHandler.sendEmptyMessage(1);
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessBarcodeActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessBarcodeActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessBarcodeActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessBarcodeActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    private void getProcess(String scanBoxNo) {
        Call<ResponseBody> call = CommonClass.sApiService.getProcess(scanBoxNo);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    if (mProcessNameList.length > 1) {
                        SpinnerDialog spinnerDialog = new SpinnerDialog(ProcessBarcodeActivity.this, (selected, dialog) -> {
                            dialog.dismiss();
                            mProcessName = mProcessNameList[selected];
                            finishProcess(1, mScanBarcode.get(mCnt), mCnt != mArtiBoxIdx ? -1 : mArtiCode);
                        }, getString(R.string.process_name_select,
                                getString(R.string.dialog_box_no,
                                        Integer.parseInt(mScanBarcode.get(mCnt).substring(CommonClass.getDigitIndex(mScanBarcode.get(mCnt))))))
                                , mProcessNameList);
                        spinnerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        spinnerDialog.setCanceledOnTouchOutside(false); // 다이얼로그 밖에 터치 시 종료
                        spinnerDialog.setCancelable(false); // 다이얼로그 취소 가능 (back key)
                        spinnerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 다이얼로그 background 적용하기 위한 코드 (radius 적용)
                        spinnerDialog.show();
                    } else {
                        mProcessName = mProcessNameList[0];
                        finishProcess(1, mScanBarcode.get(mCnt), mCnt != mArtiBoxIdx ? -1 : mArtiCode);
                    }
                } else if (code == 201) {
                    finishProcess(-1, mScanBarcode.get(mCnt), mCnt != mArtiBoxIdx ? -1 : mArtiCode);
                } else {
                    if (!ProcessBarcodeActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessBarcodeActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!ProcessBarcodeActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessBarcodeActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    private void finishProcess(int theFirst, String scanBoxNo, int artiCode) {
        // 공정 종료
        Call<ResponseBody> call = CommonClass.sApiService.processFinish(theFirst, scanBoxNo, artiCode, mProcessName,
                CommonClass.getCurrentTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())), mName);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == 200) {
                    mCnt++;
                    if (mCnt >= mScanBarcode.size())
                        processHandler.sendEmptyMessage(22);
                    else
                        processHandler.sendEmptyMessage(2);
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!ProcessBarcodeActivity.this.isFinishing())
                        CommonClass.showDialog(ProcessBarcodeActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!ProcessBarcodeActivity.this.isFinishing())
                    CommonClass.showDialog(ProcessBarcodeActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }
}