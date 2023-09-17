package gbsoft.com.dental_gb;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import gbsoft.com.dental_gb.dto.AlarmDTO;
import gbsoft.com.dental_gb.dto.ArticulatorDTO;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommonClass {
    public static final int TE = 1; //test
    public static final int UI = 2; //uni
    public static final int PDL = 3;    //partners dental lab
    public static final int DH = 4; // dental hive
    public static final int SM = 5; //simmi
    public static final int TS = 6; //the style
    public static final int ED = 7; //Eudon
    public static final int YK = 1; // YooKoung

    public static final String TAG_ERR = "ErrorMessage";
    public static final InternetCheck ic = new InternetCheck();

    public static String MASTER_URL = "http://company.dentalmes.com";
    public static ApiService sMasterApiService = null;
    public static ApiService sApiService = null;

    // 알람 그룹 아이디
    public static int sNotificationNoticeId = 1000;
    public static int sNotificationClientId = 2000;
    public static int sNotificationRequestId = 3000;
    public static int sNotificationMaterialId = 6000;
    public static int sNotificationEquipErrId = 8000;


    // DTO List
    public static ArrayList<AlarmDTO> sAlarmDTO = new ArrayList<>();  // 알람 리스트

    public static ArrayList<ArticulatorDTO> sArticulatorDTO = new ArrayList<>();   // 교합기 리스트
    public static LinkedHashMap<Integer, String> sArticulatorSpinner = new LinkedHashMap<>();  // 교합기 리스트
    public static ArrayList<PlantListDTO> sPlantListDTO = new ArrayList<>(); // 설비목록 리스트 | 설비on/off 리스트
    public static ArrayList<ReleaseDTO> sReleaseDTO = new ArrayList<>(); // 출고 리스트
    public static ArrayList<RequestDTO> sRequestDTO = new ArrayList<>(); // 의뢰서 리스트
    public static ArrayList<MaterialDTO> sMaterialDTO = new ArrayList<>(); // 자재 리스트
    public static ArrayList<GoldDTO> sGoldDTO = new ArrayList<>(); // 골드 리스트
    public static ArrayList<String> sFaultyType = new ArrayList<>();

    public static void getMasterApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MASTER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sMasterApiService = retrofit.create(ApiService.class);
    }

    // Retrofit2
    public static void getApiService(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sApiService = retrofit.create(ApiService.class);
    }

    static class InternetCheck {
        public boolean GetInternet(Context context) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                return true;
            }
            return false;
        }
    }

    public static String getCurrentTime(SimpleDateFormat format) {
        Calendar date = Calendar.getInstance();
        return format.format(date.getTime());
    }


    public static void ShowToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showDialog(Context context, String title, String content, CheckDialogClickListener listener, boolean isCancelOn) {
        try {
            CheckDialog dialog = new CheckDialog(context, listener, title, content, isCancelOn);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false); // 다이얼로그 밖에 터치 시 종료
            dialog.setCancelable(false); // 다이얼로그 취소 가능 (back key)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 다이얼로그 background 적용하기 위한 코드 (radius 적용)
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG_ERR, "Common showDialog error. maybe BadTokenException");
        }
    }

    public static int getDigitIndex(String str) {
        int s = -1;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                s = i;
                break;
            }
        }
        return s;
    }
}