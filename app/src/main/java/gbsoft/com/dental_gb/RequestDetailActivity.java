package gbsoft.com.dental_gb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import gbsoft.com.dental_gb.databinding.ActivityRequestDetailBinding;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestDetailActivity extends Activity {
    private ActivityRequestDetailBinding mBinding;

    private String mRequestCode = "";

    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        mBinding = ActivityRequestDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
//        setContentView(R.layout.activity_request_detail);

        if (!CommonClass.ic.GetInternet(RequestDetailActivity.this.getApplicationContext())) {
            if (!RequestDetailActivity.this.isFinishing())
                CommonClass.showDialog(RequestDetailActivity.this, getString(R.string.error_title), getString(R.string.internet_check), () -> finish(), false);
        } else {
            initialSet();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", MODE_PRIVATE);

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);
        mServerPath = sharedPreferences.getString("address", "");

        if (CommonClass.sApiService == null)
            CommonClass.getApiService(mServerPath);

        Intent intent = getIntent();
        mRequestCode = intent.getStringExtra("reqNum");

        mBinding.iconTxtImage.setOnClickListener(imageButtonClick);

        // Todo: ED 임시로 SM와 같이함 추후에 개발시 수정필요
        if (mCode == CommonClass.SM || mCode == CommonClass.ED)
            mBinding.iconTxtImage.setVisibility(View.VISIBLE);
        else if (mCode == CommonClass.TS)
            mBinding.iconTxtImage.setVisibility(View.GONE);
        else if (mCode == CommonClass.PDL)
            mBinding.iconTxtImage.setVisibility(View.GONE);
        else if (mCode == CommonClass.UI)
            mBinding.iconTxtImage.setVisibility(View.GONE);


        if (mCode == CommonClass.PDL)
            getShutdownCheck();
        else
            getRequestDetailData();
    }

    View.OnClickListener imageButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intentImage = new Intent(RequestDetailActivity.this, RequestImageActivity.class);
            intentImage.putExtra("reqNum", mRequestCode);
            intentImage.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intentImage);
        }
    };

    public void getRequestDetailData() {
        Call<ResponseBody> call = CommonClass.sApiService.getRequestDetail(mId, mRequestCode, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.none_data), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        mBinding.txtClientName.setText(jsonObject.getString("client"));
                        mBinding.editPatientName.setText(jsonObject.getString("patient"));
                        mBinding.txtOrderDate.setText(String.format("%s ~ ", jsonObject.getString("ordDate")));
                        mBinding.txtDeadlineDate.setText(jsonObject.getString("deadDate"));

                        // Todo: ED 임시로 SM와 같이함 추후에 개발시 수정필요
                        if (mCode == CommonClass.SM || mCode == CommonClass.ED) {
//                                mBinding.iconTxtImage.setVisibility(View.VISIBLE);
                            request_SIMMI(jsonArray);
                        } else if (mCode == CommonClass.TS) {
//                                mBinding.iconTxtImage.setVisibility(View.GONE);
                            request_THESTYLE(jsonArray);
                        } else if (mCode == CommonClass.PDL) {
//                                mBinding.iconTxtImage.setVisibility(View.GONE);
                            request_PARTNERS(jsonArray);
                        } else if (mCode == CommonClass.UI) {
//                                mBinding.iconTxtImage.setVisibility(View.GONE);
                            request_UNI(jsonArray);
                        }

                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!RequestDetailActivity.this.isFinishing())
                            CommonClass.showDialog(RequestDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!RequestDetailActivity.this.isFinishing())
                            CommonClass.showDialog(RequestDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    if (response.code() == 409) {
                        Log.v(CommonClass.TAG_ERR, "Fail, " + response.code());
                        if(!RequestDetailActivity.this.isFinishing())
                            CommonClass.showDialog(RequestDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                    } else {
                        Log.v(CommonClass.TAG_ERR, "Fail, " + String.valueOf(response.code()));
                        if(!RequestDetailActivity.this.isFinishing())
                            CommonClass.showDialog(RequestDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!RequestDetailActivity.this.isFinishing())
                    CommonClass.showDialog(RequestDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    public void request_SIMMI(JSONArray jsonArray) {
        ArrayList<RequestDetailItem> list = new ArrayList<RequestDetailItem>();

        String prosthesis_1[] = {"Custom abut", "기성 abut", "Hybrid", "Link", "My Link", "기타"};  // 임플란트1
        String prosthesis_2[] = {"Hole 형성", "Hole 없음", "Hole 위치에 따라 선택"};   // 임플란트2
        String prosthesis_3[] = {"Zig까지", "Temporary까지"};   // 임플란트3
        String prosthesis_4[] = {"Temporary", "Pink Porcelain", "Collarless", "덴처지대치"}; // 보철2


        String denture_1[] = {"임플란트 오버덴처"}; // 임플란트
        String denture_2[] = {"풀덴처", "파샬덴처", "flexible 덴처", "hybrid 덴처"};   // 덴처
        String denture_3[] = {"풀덴처", "파샬덴처"};   // 임시덴처
        String denture_4[] = {"덴처용", "임플란트용", "미백용"};   // 트레이
        String denture_5[] = {"Mesh Type", "A-P bar", "full-plate", "single-plate"}; // 프레임
        String denture_6[] = {"프레임베이스", "메쉬베이스", "레진베이스"};  // 왁스림
        String denture_7[] = {"일반치", "Endura"}; // 배열
        String denture_8[] = {"광중합큐링"}; // 큐링
        String denture_9[] = {"리베이스&릴라이닝", "인공치수리", "클라스프수리", "레진파절수리"};    // 덴처수리
        String denture_10[] = {"와이어가의치", "Splint", "Stent"};    // 기타
        String denture_11[] = {"상악", "하악"}; // 상/하악

        String productName = "", productPart = "";
        String dentalFormula = "", shade = "", implant1 = "", implant2 = "", implant3 = "", prosthesis1 = "", prosthesis2 = "",
                denture = "", tmpDenture = "", frame = "", tray = "", waxrim = "", array = "", quering = "",
                dentureRepairing = "", etc = "", ag = "", memo = "", divisions = "";

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                shade = jsonObject.getString("shade");
                productName = jsonObject.getString("prdName");
                productPart = jsonObject.getString("prdPart");
                dentalFormula = jsonObject.getString("dent");

                String checkList[] = jsonObject.getString("chkList").split(",");

                if (productPart.equals("일반보철")) {
                    String str_implant1 = "", str_implant2 = "", str_implant3 = "", str_prosthesis2 = "";

                    int idx = 0;
                    int len = 0;

                    // 임플란트I
                    len = prosthesis_1.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_implant1 += prosthesis_1[j] + "  ";
                        }
                        idx++;
                    }
                    implant1 = str_implant1;

                    // 임플란트II
                    len = prosthesis_2.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_implant2 += prosthesis_2[j] + "  ";
                        }
                        idx++;
                    }
                    implant2 = str_implant2;

                    // 임플란트III
                    len = prosthesis_3.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_implant3 += prosthesis_3[j] + "  ";
                        }
                        idx++;
                    }
                    implant3 = str_implant3;

                    // 보철II
                    len = prosthesis_4.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_prosthesis2 += prosthesis_4[j] + "  ";
                        }
                        idx++;
                    }
                    prosthesis2 = str_prosthesis2;


                    String[] _p = checkList[idx].split(";");

                    // 보철I
                    for (int j = 1; j < 5; j++) {
                        if (!_p[j].equals("선택안함")) {
                            prosthesis1 += _p[j] + "  ";
                        }
                    }

                    // 메모
                    if (_p.length > 5) {
                        memo = _p[5];
                    }
                } else if (productPart.equals("덴처")) {
                    String str_implant = "", str_denture = "", str_tmpDenture = "", str_tray = "", str_frame = "", str_waxrim = "", str_array = "", str_quering = "", str_repairing = "", str_etc = "", str_ag = "";

                    int idx = 0;
                    int len = 0;

                    // 임플란트
                    len = denture_1.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_implant += denture_1[j] + "  ";
                        }
                        idx++;
                    }
                    implant1 = str_implant;

                    // 덴처
                    len = denture_2.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_denture += denture_2[j] + "  ";
                        }
                        idx++;
                    }
                    denture = str_denture;

                    // 임시덴처
                    len = denture_3.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_tmpDenture += denture_3[j] + "  ";
                        }
                        idx++;
                    }
                    tmpDenture = str_tmpDenture;

                    // 트레이
                    len = denture_4.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_tray += denture_4[j] + "  ";
                        }
                        idx++;
                    }
                    tray = str_tray;

                    // 프레임
                    len = denture_5.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_frame += denture_5[j] + "  ";
                        }
                        idx++;
                    }
                    frame = str_frame;

                    // 왁스림
                    len = denture_6.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_waxrim += denture_6[j] + "  ";
                        }
                        idx++;
                    }
                    waxrim = str_waxrim;

                    // 배열
                    len = denture_7.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_array += denture_7[j] + "  ";
                        }
                        idx++;
                    }
                    array = str_array;

                    // 큐링
                    len = denture_8.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_quering += denture_8[j] + "  ";
                        }
                        idx++;
                    }
                    quering = str_quering;

                    // 덴처수리
                    len = denture_9.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_repairing += denture_9[j] + "  ";
                        }
                        idx++;
                    }
                    dentureRepairing = str_repairing;

                    // 기타
                    len = denture_10.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_etc += denture_10[j] + "  ";
                        }
                        idx++;
                    }
                    etc = str_etc;

                    // 상/하악
                    len = denture_11.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_ag += denture_11[j] + "  ";
                        }
                        idx++;
                    }
                    ag = str_ag;

                    String[] _m = checkList[idx].split(";");
                    memo = (_m.length == 0) ? "" : _m[1];

                } else {
                    String[] _m = checkList[0].split(";");
                    memo = (_m.length == 0) ? "" : _m[1];
                }

                divisions = jsonObject.getString("divs");

                list.add(new RequestDetailItem(productName, productPart, dentalFormula, shade, implant1, implant2, implant3, prosthesis1,
                        prosthesis2, denture, tmpDenture, frame, tray, waxrim, array,
                        quering, dentureRepairing, etc, ag, memo, divisions));
            }

            final RequestDetailAdapter_Simmi adapter = new RequestDetailAdapter_Simmi(list);
            mBinding.listRequestList.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void request_THESTYLE(JSONArray jsonArray) {
        ArrayList<RequestDetailItem> arrList = new ArrayList<>();

        // 의뢰서 리스트
        String productName = "", productPart = "", dentalFormula = "", memo = "", divisions = "";

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                productName = c.getString("prdName");
                productPart = c.getString("prdPArt");
                dentalFormula = c.getString("dent");

                String checkList[] = c.getString("chkList").split(";");

                // 메모
                if (checkList.length == 0) {
                    memo = "";
                } else {
                    memo = checkList[1];
                }

                divisions = c.getString("divs");

                arrList.add(new RequestDetailItem(productName, productPart, dentalFormula, memo, divisions));
            }

            final RequestDetailAdapter_TheStyle adapter = new RequestDetailAdapter_TheStyle(arrList);
            mBinding.listRequestList.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void request_PARTNERS(JSONArray jsonArray) {
        ArrayList<RequestDetailItem> list = new ArrayList<RequestDetailItem>();

        String crown_1[] = {"Cement type(hole 형성안함)", "SCRP type(hole 형성)"};
        String crown_2[] = {"지대치삭제", "대합치삭제"};

        String porcelain_1[] = {"Cement type(hole 형성안함)", "SCRP type(hole 형성)"};
        String porcelain_2[] = {"Ovate pontic", "Hygenic pontic"};
        String porcelain_3[] = {"Metal occl.", "Collarless"};
        String porcelain_4[] = {"지대치삭제", "대합치삭제", "Metal 교합면"};

        String denture_1[] = {"Frame", "Resin base", "Frame-Metal", "Frame-Mesh", "배열", "Curing", "Attachment"};
        String denture_2[] = {"Flexible denture", "Combination denture"};
        String denture_3[] = {"상악", "하악"};
        String denture_4[] = {"Frame", "배열", "Curing", "Attachment"};
        String denture_5[] = {"Wire 가의치", "Temp-denture", "Denture repair", "Relining", "개인 Tray"};

        // 의뢰서 리스트
        String productPart = "", productName = "", dentalFormula = "", implantCrown = "", spaceLack = "",
                ponticDesign = "", metalDesign = "",
                fullDenture = "", flexibleDenture = "", ag = "", partialDenture = "", dentureExtra = "", memo = "";

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                productPart = c.getString("prdPart");
                productName = c.getString("prdName");
                dentalFormula = c.getString("dent");
                String shade = c.getString("shade");
                String type = c.getString("divs");

                String checkList[] = c.getString("chkList").split(",");

                // Crown
                if (productPart.equals("Crown")) {
                    String str_crown1 = "", str_crown2 = "";

                    int idx = 0;
                    int len = 0;

                    // Implant Crown
                    len = crown_1.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_crown1 += crown_1[j] + "  ";
                        }
                        idx++;
                    }
                    implantCrown = str_crown1;

                    // 대합치 공간부족시
                    len = crown_2.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_crown2 += crown_2[j] + "  ";
                        }
                        idx++;
                    }
                    spaceLack = str_crown2;

                    String[] _m = checkList[idx].split(";", 2);
                    memo = _m[1];

                } else if (productPart.equals("Porcelain")) {
                    String str_Porcelain1 = "", str_Porcelain2 = "", str_Porcelain3 = "", str_Porcelain4 = "";

                    int idx = 0;
                    int len = 0;

                    // Implant Crown
                    len = porcelain_1.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_Porcelain1 += porcelain_1[j] + "  ";
                        }
                        idx++;
                    }
                    implantCrown = str_Porcelain1;

                    // Pontic Design
                    len = porcelain_2.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_Porcelain2 += porcelain_2[j] + "  ";
                        }
                        idx++;
                    }
                    ponticDesign = str_Porcelain2;

                    // Metal Design
                    len = porcelain_3.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_Porcelain3 += porcelain_3[j] + "  ";
                        }
                        idx++;
                    }
                    metalDesign = str_Porcelain3;

                    // 대합치 공간부족시
                    len = porcelain_4.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_Porcelain4 += porcelain_4[j] + "  ";
                        }
                        idx++;
                    }
                    spaceLack = str_Porcelain4;

                    // Memo
                    String[] _m = checkList[idx].split(";", 2);
                    memo = _m[1];

                } else if (productPart.equals("Denture")) {
                    String str_Denture1 = "", str_Denture2 = "", str_Denture3 = "", str_Denture4 = "", str_Denture5 = "";

                    int idx = 0;
                    int len = 0;

                    // denture full denture
                    len = denture_1.length;
                    for (int j = 0; j < len; j++) {

                        if (checkList[idx].equals("1")) {
                            str_Denture1 += denture_1[j] + "  ";
                        }
                        idx++;
                    }
                    fullDenture = str_Denture1;

                    // denture flexible denture
                    len = denture_2.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_Denture2 += denture_2[j] + "  ";
                        }
                        idx++;
                    }
                    flexibleDenture = str_Denture2;

                    // denture jaw
                    len = denture_3.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_Denture3 += denture_3[j] + "  ";
                        }
                        idx++;
                    }
                    ag = str_Denture3;

                    // denture partial denture
                    len = denture_4.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_Denture4 += denture_4[j] + "  ";
                        }
                        idx++;
                    }
                    partialDenture = str_Denture4;

                    // denture extra
                    len = denture_5.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_Denture5 += denture_5[j] + "  ";
                        }
                        idx++;
                    }
                    dentureExtra = str_Denture5;

                    // Memo
                    String[] _m = checkList[idx].split(";", 2);
                    memo = _m[1];
                }

                list.add(new RequestDetailItem(productPart, productName, dentalFormula, implantCrown, spaceLack, ponticDesign,
                        metalDesign, fullDenture, flexibleDenture, ag, partialDenture, dentureExtra, memo, shade, type));
            }

            RequestDetailAdapter_Partners adapter = new RequestDetailAdapter_Partners(list);
            mBinding.listRequestList.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void request_UNI(JSONArray jsonArray) {
        ArrayList<RequestDetailItem> list = new ArrayList<RequestDetailItem>();

        String prosthesis_1[] = {"Custom abut", "기성 abut", "기타"};  // 임플란트1
        String prosthesis_2[] = {"Hole 형성", "Hole 없음", "Hole 위치에 따라 선택"};   // 임플란트2
        String prosthesis_3[] = {"Zig까지", "Temporary까지"};   // 임플란트3
        String prosthesis_4[] = {"Temporary", "Pink Porcelain", "덴처지대치"}; // 보철2


        String denture_1[] = {"임플란트 오버덴처"}; // 임플란트
        String denture_2[] = {"풀덴처", "파샬덴처"};   // 덴처
        String denture_3[] = {"풀덴처", "파샬덴처"};   // 임시덴처
        String denture_4[] = {"덴처용", "임플란트용", "미백용"};   // 트레이
        String denture_5[] = {"프레임베이스", "메쉬베이스", "레진베이스"};  // 왁스림
        String denture_6[] = {"일반치", "경질치"}; // 배열
        String denture_7[] = {"광중합큐링"}; // 큐링
        String denture_8[] = {"리베이스&릴라이닝", "인공치수리", "클라스프수리", "레진파절수리"};    // 덴처수리
        String denture_9[] = {"와이어가의치", "Splint", "Stent"};    // 기타
        String denture_10[] = {"상악", "하악"}; // 상/하악

        // 의뢰서 리스트
        String productName = "", productPart = "";
        String dentalFormula = "", shade = "", implant1 = "", implant2 = "", implant3 = "", prosthesis1 = "", prosthesis2 = "",
                denture = "", tmpDenture = "", frame = "", tray = "", waxrim = "", array = "", quering = "",
                dentureRepairing = "", etc = "", ag = "", memo = "";

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                shade = c.getString("shade");
                productName = c.getString("prdName");
                productPart = c.getString("prdPart");
                dentalFormula = c.getString("dent");

                String checkList[] = c.getString("chkList").split(",");

                if (productPart.equals("일반보철")) {
                    String str_implant1 = "", str_implant2 = "", str_implant3 = "", str_prosthesis2 = "";

                    int idx = 0;
                    int len = 0;

                    // 임플란트I
                    len = prosthesis_1.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_implant1 += prosthesis_1[j] + "  ";
                        }
                        idx++;
                    }
                    implant1 = str_implant1;

                    // 임플란트II
                    len = prosthesis_2.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_implant2 += prosthesis_2[j] + "  ";
                        }
                        idx++;
                    }
                    implant2 = str_implant2;

                    // 임플란트III
                    len = prosthesis_3.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_implant3 += prosthesis_3[j] + "  ";
                        }
                        idx++;
                    }
                    implant3 = str_implant3;

                    // 보철II
                    len = prosthesis_4.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_prosthesis2 += prosthesis_4[j] + "  ";
                        }
                        idx++;
                    }
                    prosthesis2 = str_prosthesis2;


                    String[] _p = checkList[idx].split(";");

                    // 보철I
                    for (int j = 1; j < 5; j++) {
                        if (!_p[j].equals("선택안함")) {
                            prosthesis1 += _p[j] + "  ";
                        }
                    }

                    // 메모
                    if (_p.length > 5) {
                        memo = _p[5];
                    }
                } else if (productPart.equals("덴처")) {
                    String str_implant = "", str_denture = "", str_tmpDenture = "", str_tray = "", str_waxrim = "", str_array = "", str_quering = "", str_repairing = "", str_etc = "", str_ag = "";

                    int idx = 0;
                    int len = 0;

                    // 임플란트
                    len = denture_1.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_implant += denture_1[j] + "  ";
                        }
                        idx++;
                    }
                    implant1 = str_implant;

                    // 덴처
                    len = denture_2.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_denture += denture_2[j] + "  ";
                        }
                        idx++;
                    }
                    denture = str_denture;

                    // 임시덴처
                    len = denture_3.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_tmpDenture += denture_3[j] + "  ";
                        }
                        idx++;
                    }
                    tmpDenture = str_tmpDenture;

                    // 트레이
                    len = denture_4.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_tray += denture_4[j] + "  ";
                        }
                        idx++;
                    }
                    tray = str_tray;

                    // 왁스림
                    len = denture_5.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_waxrim += denture_5[j] + "  ";
                        }
                        idx++;
                    }
                    waxrim = str_waxrim;

                    // 배열
                    len = denture_6.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_array += denture_6[j] + "  ";
                        }
                        idx++;
                    }
                    array = str_array;

                    // 큐링
                    len = denture_7.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_quering += denture_7[j] + "  ";
                        }
                        idx++;
                    }
                    quering = str_quering;

                    // 덴처수리
                    len = denture_8.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_repairing += denture_8[j] + "  ";
                        }
                        idx++;
                    }
                    dentureRepairing = str_repairing;

                    // 기타
                    len = denture_9.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_etc += denture_9[j] + "  ";
                        }
                        idx++;
                    }
                    etc = str_etc;

                    // 상/하악
                    len = denture_10.length;
                    for (int j = 0; j < len; j++) {
                        if (checkList[idx].equals("1")) {
                            str_ag += denture_10[j] + "  ";
                        }
                        idx++;
                    }
                    ag = str_ag;

                    String[] _m = checkList[idx].split(";");
                    memo = (_m.length == 0) ? "" : _m[1];

                } else {
                    String[] _m = checkList[0].split(";");
                    memo = (_m.length == 0) ? "" : _m[1];
                }


                list.add(new RequestDetailItem(productName, productPart, dentalFormula, shade, implant1, implant2, implant3, prosthesis1,
                        prosthesis2, denture, tmpDenture, frame, tray, waxrim, array,
                        quering, dentureRepairing, etc, ag, memo));
            }

            RequestDetailAdapter_Uni adapter = new RequestDetailAdapter_Uni(list);
            mBinding.listRequestList.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getShutdownCheck() {
        Call<ResponseBody> call = CommonClass.sApiService.getShutdownCheck(mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        int mShutdownYn = Integer.parseInt(jsonObject.getString("down"));

                        if (mShutdownYn == 1) {
                            if (!RequestDetailActivity.this.isFinishing())
                                CommonClass.showDialog(RequestDetailActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                        } else {
                            getRequestDetailData();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!RequestDetailActivity.this.isFinishing())
                            CommonClass.showDialog(RequestDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!RequestDetailActivity.this.isFinishing())
                            CommonClass.showDialog(RequestDetailActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!RequestDetailActivity.this.isFinishing())
                        CommonClass.showDialog(RequestDetailActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!RequestDetailActivity.this.isFinishing())
                    CommonClass.showDialog(RequestDetailActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }
}
