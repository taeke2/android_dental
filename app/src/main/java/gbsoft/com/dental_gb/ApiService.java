package gbsoft.com.dental_gb;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

// Retrofit api Services
public interface ApiService {
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 기공소 리스트

    /**
     * 기공소 리스트 가져오기
     */
    @FormUrlEncoded
    @POST("/company/getCompanyList")
    Call<ResponseBody> getCompanyList(@Field("idx") String idx);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 로그인

    /**
     * 사용자 로그인
     */
    @FormUrlEncoded
    @POST("/auth/login_app")
    Call<ResponseBody> login(@Field("id") String userId, @Field("password") String userPw, @Field("ip") String ip);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 공통 기능
        String COMMON_PATH = "/divide/COMMON_APP";

    /**
     * 메뉴 사용자 권한 가져오기
     */
    @GET(COMMON_PATH + "/getMenuAuth")
    Call<ResponseBody> getMenuAuth(@Query("userId") String userId);

    /**
     * DB명 가져오기
     */
    @FormUrlEncoded
    @POST(COMMON_PATH + "/getDbName")
    Call<ResponseBody> getDbName(@Field("tmp") String tmp, @Field("ip") String ip);

    /**
     * 셧다운 유무 확인
     */
    @FormUrlEncoded
    @POST(COMMON_PATH + "/shutdownCheck")
    Call<ResponseBody> getShutdownCheck(@Field("userId") String userId, @Field("ip") String ip);

    /**
     * 사용자 접근 권한 확인
     */
    @FormUrlEncoded
    @POST(COMMON_PATH + "/userAccessCheck")
    Call<ResponseBody> userAccessCheck(@Field("userId") String userId, @Field("ip") String ip);

    /**
     * 설치된 디바이스 정보 저장
     */
    @FormUrlEncoded
    @POST(COMMON_PATH + "/deviceRecord")
    Call<ResponseBody> deviceRecord(@Field("userId") String userId, @Field("serialNumber") String serialNumber, @Field("ip") String ip);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 의뢰서 조회
//    String NOTICE_PATH = "/divide/NOTICE";
//
//    /**
//     * 공지사항 가져오기
//     */
//    @GET(NOTICE_PATH + "/getNotice")
//    Call<ResponseBody> getNotice(@Query("title") String title);
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 의뢰서 조회
//    String CLIENT_PATH = "/divide/CLIENT";
//
//    /**
//     * 거래처 가져오기
//     */
//    @GET(CLIENT_PATH + "/getClient")
//    Call<ResponseBody> getClient(@Query("client") String client);
//
//
//    @DELETE(CLIENT_PATH + "/deleteClient/{key}")
//    Call<ResponseBody> deleteClient(@Path("key") int key);


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 의뢰서 조회
    String REQUEST_PATH = "/divide/REQUEST";

    /**
     * 의뢰서 리스트 총 갯수
     */
    @FormUrlEncoded
    @POST(REQUEST_PATH + "/requestCount")
    Call<ResponseBody> getRequestCount(@Field("clientName") String clientName, @Field("patientName") String patientName, @Field("orderDate") String orderDate,
                                       @Field("deadlineDate") String deadlineDate, @Field("limit") int limit, @Field("offset") int offset, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 의뢰서 리스트 조회
     */
    @FormUrlEncoded
    @POST(REQUEST_PATH + "/requestList")
    Call<ResponseBody> getRequestList(@Field("clientName") String clientName, @Field("patientName") String patientName, @Field("orderDate") String orderDate,
                                      @Field("deadlineDate") String deadlineDate, @Field("limit") int limit, @Field("offset") int offset, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 의뢰서 상세정보 조회
     */
    @FormUrlEncoded
    @POST(REQUEST_PATH + "/requestDetail")
    Call<ResponseBody> getRequestDetail(@Field("userId") String userId, @Field("requestCode") String requestCode, @Field("ip") String ip);

    /**
     * 이미지 업로드
     */
    @Multipart
    @POST(REQUEST_PATH + "/uploadImg")
    Call<ResponseBody> uploadImg(@Part MultipartBody.Part file, @Field("ip") String ip);

    /**
     * 이미지 여러장 업로드
     */
    @Multipart
    @POST(REQUEST_PATH + "/uploadImages")
    Call<ResponseBody> uploadImages(@Part List<MultipartBody.Part> files, @Part("userId") RequestBody userId, @Part("requestCode") RequestBody requestCode, @Part("imgName") RequestBody imgName, @Part("ip") RequestBody ip);

    /**
     * 이미지 삭제
     */
    @FormUrlEncoded
    @POST(REQUEST_PATH + "/deleteImage")
    Call<ResponseBody> deleteImage(@Field("imageName") String imageName, @Field("userId") String userId, @Field("requestCode") String requestCode, @Field("imgName") String imgName, @Field("ip") String ip);

    /**
     * 이미지명 조회
     */
    @FormUrlEncoded
    @POST(REQUEST_PATH + "/getImgName")
    Call<ResponseBody> getImgName(@Field("requestCode") String requestCode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 메모 조회
     */
    @FormUrlEncoded
    @POST(REQUEST_PATH + "/getMemo")
    Call<ResponseBody> getMemo(@Field("requestCode") String requestCode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 메모 작성
     */
    @FormUrlEncoded
    @POST(REQUEST_PATH + "/writeMemo")
    Call<ResponseBody> writeMemo(@Field("requestCode") String requestCode, @Field("remark") String remark, @Field("userId") String userId, @Field("ip") String ip);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 공정현황
    String PROCESS_PATH = "/divide/PROCESS";

    /**
     * 공정 현황 리스트 총 갯수
     */
    @FormUrlEncoded
    @POST(PROCESS_PATH + "/processCount")
    Call<ResponseBody> getProcessCount(@Field("orderManager") String orderManager, @Field("clientName") String clientName, @Field("patientName") String patientName, @Field("orderDate") String orderDate,
                                       @Field("deadlineDate") String deadlineDate, @Field("limit") int limit, @Field("offset") int offset, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 공정 현황 리스트 조회
     */
    @FormUrlEncoded
    @POST(PROCESS_PATH + "/processList")
    Call<ResponseBody> getProcessList(@Field("orderManager") String orderManager, @Field("clientName") String clientName, @Field("patientName") String patientName, @Field("orderDate") String orderDate,
                                      @Field("deadlineDate") String deadlineDate, @Field("limit") int limit, @Field("offset") int offset, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 공정 리스트 조회
     */
    @FormUrlEncoded
    @POST(PROCESS_PATH + "/orderList")
    Call<ResponseBody> getOrderList(@Field("orderBarcode") String orderBarcode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 제품 공정 조회
     */
    @FormUrlEncoded
    @POST(PROCESS_PATH + "/productProcess")
    Call<ResponseBody> getProductProcess(@Field("productCode") String productCode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 불량 유형 조회
     */
    @FormUrlEncoded
    @POST(PROCESS_PATH + "/getFaultyType")
    Call<ResponseBody> getFaultyType(@Field("userId") String userId, @Field("ip") String ip);

    /**
     * 현재 진행된 공정
     */
    @FormUrlEncoded
    @POST(PROCESS_PATH + "/getMaxProcess")
    Call<ResponseBody> getMaxProcess(@Field("orderBarcode") String orderBarcode, @Field("productInfoCode") String productInfoCode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 진행된 공정 삭제
     */
    @FormUrlEncoded
    @POST(PROCESS_PATH + "/deleteProcess")
    Call<ResponseBody> deleteProcess(@Field("orderBarcode") String orderBarcode, @Field("processTag") int processTag, @Field("processState") int processState, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 공정별 체크사항 조회
     */
    @FormUrlEncoded
    @POST(PROCESS_PATH + "/getCheckInfo")
    Call<ResponseBody> getCheckInfo(@Field("productCode") String productCode, @Field("processState1") String processState1, @Field("processState2") String processState2, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 공정 진행 등록
     */
    @FormUrlEncoded
    @POST(PROCESS_PATH + "/processCommit")
    Call<ResponseBody> processCommit(@Field("orderBarcode") String orderBarcode, @Field("productInfoCode") String productInfoCode, @Field("processState") String processState,
                                     @Field("processManager") String processManager, @Field("processTime") String processTime, @Field("remark") String remark, @Field("userId") String userId, @Field("ip") String ip);

//    /**
//     * 공정 시작 등록 (YK)
//     */
//    @FormUrlEncoded
//    @POST(PROCESS_PATH + "/processStart")
//    Call<ResponseBody> processStart(@Field("boxNo") String boxNo, @Field("artiBoxNo") String artiBox, @Field("artiCode") int artiCode, @Field("startTime") String startTime, @Field("manager") String manager);
//
//
//    /**
//     * 공정 종료 등록 (YK)
//     */
//    @FormUrlEncoded
//    @POST(PROCESS_PATH + "/processFinish")
//    Call<ResponseBody> processFinish(@Field("boxNo") String boxNo, @Field("artiBox") String artiBox, @Field("artiCode") int artiCode, @Field("endTime") String enTime);



    /**
     * 공정 불량 처리
     */
    @FormUrlEncoded
    @POST(PROCESS_PATH + "/faultyCommit")
    Call<ResponseBody> faultyCommit(@Field("orderBarcode") String orderBarcode, @Field("productInfoCode") String productInfoCode, @Field("faultyManager") String faultyManager,
                                    @Field("faultyProcess") int faultyProcess, @Field("faultyType") String faultyType, @Field("faultyDateTime") String faultyDateTime,
                                    @Field("faultyHistory") String remark, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 마지막 공정 시간 등록
     */
    @FormUrlEncoded
    @POST(PROCESS_PATH + "/setFinishTime")
    Call<ResponseBody> setFinishTime(@Field("orderBarcode") String orderBarcode, @Field("orderFinishTime") String orderFinishTime, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * single 박스 번호 update
     */
    @FormUrlEncoded
    @PUT(PROCESS_PATH + "/updateBoxNo/{boxNo}")
    Call<ResponseBody> updateBoxNo(@Path("boxNo") String boxNo, @Field("processCode") int processCode);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 바코드(QR 코드) 스캔
    String BARCODE_PATH = "/divide/BARCODE";

    /**
     * 불량 바코드 스캔
     */
    @FormUrlEncoded
    @POST(BARCODE_PATH + "/barcodeScan_faulty")
    Call<ResponseBody> getBarcodeScan_f(@Field("getBarcode") String getBarcode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 공정 바코드 스캔
     */
    @FormUrlEncoded
    @POST(BARCODE_PATH + "/barcodeScan_process")
    Call<ResponseBody> getBarcodeScan_p(@Field("getBarcode") String getBarcode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 자재 바코드 스캔
     */
    @FormUrlEncoded
    @POST(BARCODE_PATH + "/barcodeScan_material")
    Call<ResponseBody> getBarcodeScan_m(@Field("getBarcode") String getBarcode, @Field("userId") String userId, @Field("ip") String ip);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 자재입고관리
    String MATERIAL_PATH = "/divide/MATERIAL";

    /**
     * 자재 리스트 조회
     */
    @FormUrlEncoded
    @POST(MATERIAL_PATH + "/getMaterialList")
    Call<ResponseBody> getMaterialList(@Field("searchText") String searchText, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 자재 불량 갯수 조회
     */
    @FormUrlEncoded
    @POST(MATERIAL_PATH + "/getFaultyQuantity")
    Call<ResponseBody> getFaultyQuantity(@Field("receivingBarcode") String receivingBarcode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 자재 입고 시 단가 데이터 조회
     */
    @FormUrlEncoded
    @POST(MATERIAL_PATH + "/getMaterialsPrice")
    Call<ResponseBody> getMaterialsPrice(@Field("receivingBarcode") String receivingBarcode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 자재 입고 시 단가 데이터 조회
     */
    @FormUrlEncoded
    @POST(MATERIAL_PATH + "/setMaterialsFaulty")
    Call<ResponseBody> setMaterialsFaulty(@Field("materialsCode") String materialsCode, @Field("clientCode") String clientCode, @Field("manager") String manager,
                                          @Field("ReceivingBarcode") String receivingBarcode, @Field("state") String state, @Field("date") String date, @Field("quantity") String quantity,
                                          @Field("faultyHistory") String faultyHistory, @Field("userId") String userId, @Field("ip") String ip);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 골드 관리
    String GOLD_PATH = "/divide/GOLD";

    /**
     * 골드 현황 리스트 조회
     */
    @FormUrlEncoded
    @POST(GOLD_PATH + "/getGoldList")
    Call<ResponseBody> getGoldList(@Field("searchText") String searchText, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 골드 입/출고 리스트 조회
     */
    @FormUrlEncoded
    @POST(GOLD_PATH + "/getGoldInOutList")
    Call<ResponseBody> getGoldInOutList(@Field("clientCode") String clientCode, @Field("cond") String cond, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 골드 종류 조회
     */
    @FormUrlEncoded
    @POST(GOLD_PATH + "/getGoldName")
    Call<ResponseBody> getGoldName(@Field("userId") String userId, @Field("ip") String ip);

    /**
     * 골드 사용 의뢰서 리스트 조회
     */
    @FormUrlEncoded
    @POST(GOLD_PATH + "/getGoldRequestList")
    Call<ResponseBody> getGoldRequestList(@Field("clientCode") String clientCode, @Field("patientName") String patientName, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 의뢰제품 리스트 조회
     */
    @FormUrlEncoded
    @POST(GOLD_PATH + "/getGoldRequestProductList")
    Call<ResponseBody> getGoldRequestProductList(@Field("requestCode") String requestCode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 골드 사용 등록
     */
    @FormUrlEncoded
    @POST(GOLD_PATH + "/setUsedGold")
    Call<ResponseBody> setUsedGold(@Field("goldCode") String goldName, @Field("clientCode") String clientCode, @Field("productInfoCode") String productInfoCode,
                                   @Field("inOutDate") String inOutDate, @Field("inOutManager") String inOutManager, @Field("inOutState") String inOutState,
                                   @Field("outQuantity") String outQuantity, @Field("useQuantity") String useQuantity, @Field("remark") String remark, @Field("userId") String userId, @Field("ip") String ip);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 골드 관리
    String RELEASE_PATH = "/divide/RELEASE";

    /**
     * 출고 리스트 조회
     */
    @FormUrlEncoded
    @POST(RELEASE_PATH + "/releaseList")
    Call<ResponseBody> getReleaseList(@Field("clientName") String clientName, @Field("patientName") String patientName, @Field("orderDate") String orderDate, @Field("deadlineDate") String deadlineDate,
                                      @Field("release") String release, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 출고 상세정보 조회
     */
    @FormUrlEncoded
    @POST(RELEASE_PATH + "/releaseDetail")
    Call<ResponseBody> getReleaseDetail(@Field("userId") String userId, @Field("requestCode") String requestCode, @Field("ip") String ip);

    /**
     * 출고 시간 등록
     */
    @FormUrlEncoded
    @POST(RELEASE_PATH + "/updateReleaseTime")
    Call<ResponseBody> updateReleaseTime(@Field("requestCode") String requestCode, @Field("outDate") String outDate, @Field("userId") String userId, @Field("ip") String ip);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 생산정보 모니터링
    String MONITORING_PATH = "/divide/MONITORING";

    /**
     * 머신 데이터 가져오기
     */
    @FormUrlEncoded
    @POST(MONITORING_PATH + "/getMachineData")
    Call<ResponseBody> getMachineData(@Field("userId") String userId, @Field("ip") String ip);

    /**
     * 일별 가공량 조회
     */
    @FormUrlEncoded
    @POST(MONITORING_PATH + "/Daily")
    Call<ResponseBody> Daily(@Field("startDate") String startDate, @Field("endDate") String endDate, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 가공 통계 조회
     */
    @FormUrlEncoded
    @POST(MONITORING_PATH + "/Stats")
    Call<ResponseBody> Stats(@Field("startDate") String startDate, @Field("endDate") String endDate, @Field("userId") String userId, @Field("ip") String ip);





    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // YK 새로운 API
    /**
     * 로그인
     */
    @FormUrlEncoded
    @POST("/app/user/signIn")
    Call<ResponseBody> signIn(@Field("userId") String userId, @Field("userPw") String userPw);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * FCM Token update
     */
    @FormUrlEncoded
    @POST("/app/user/token")
    Call<ResponseBody> updateToken(@Field("userId") String userId, @Field("token") String token);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 공지사항
     */
    @GET("/app/notice/get")
    Call<ResponseBody> getNotice(@Query("clientCode") String clientCode, @Query("txtTitle") String title);

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 거래처
     */
    @GET("/app/client/get")
    Call<ResponseBody> getClient(@Query("name") String clientName);

    /**
     * 거래처 삭제
     */
    @DELETE("/app/client/delete/{clientCode}")
    Call<ResponseBody> deleteClient(@Path("clientCode") int code);


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 교합기
     */
    @GET("/app/articulator/get")
    Call<ResponseBody> getArticulator();


    /**
     * 공정 시작 등록 (YK)
     */
    @FormUrlEncoded
    @POST("/app/process/start")
    Call<ResponseBody> processStart(
            @Field("boxNo") String boxNo,
            @Field("artiCode") int artiCode,
            @Field("processName") String processName,
            @Field("startTime") String startTime,
            @Field("manager") String manager);

    /**
     * 최초 종료 등록인지
     * */
    @FormUrlEncoded
    @POST("/app/process/get")
    Call<ResponseBody> getProcess(@Field("boxNo") String boxNo);

    /**
     * 공정 종료 등록 (YK)
     */
    @FormUrlEncoded
    @POST("/app/process/finish")
    Call<ResponseBody> processFinish(
            @Field("theFirst") int theFirst,
            @Field("boxNo") String boxNo,
            @Field("articulatorCode") int artiCode,
            @Field("processName") String processName,
            @Field("endTime") String endTime,
            @Field("manager") String manager);


    /**
     * 공정현황 의뢰서 조회
     */
    @FormUrlEncoded
    @POST("/app/process/request")
    Call<ResponseBody> getRequestProcess(
            @Field("clientName") String ClientName,
            @Field("requestDate") String requestDate,
            @Field("dueDate") String dueDate,
            @Field("dentalFormula") String dentalFormula);


    /**
     * 공정 제품
     */
    @FormUrlEncoded
    @POST("/app/product/get")
    Call<ResponseBody> getProduct(@Field("requestCode") int requestCode);

    /**
     * 공정 시작 등록 with 의뢰서
     */
    @FormUrlEncoded
    @POST("/app/process/startRequest")
    Call<ResponseBody> processStartRequest(
            @Field("requestCode") int requestCode,
            @Field("boxNo") String boxNo,
            @Field("artiCode") int artiCode,
            @Field("processName") String processName,
            @Field("startTime") String startTime,
            @Field("manager") String manager,
            @Field("memo") String memo);
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 설비 리스트 조회
     */
    @GET("/app/equip/list")
    Call<ResponseBody> equipList(@Query("equipName") String equipName);

    /**
     * 설비 상태 변경
     */
    @FormUrlEncoded
    @POST("/app/equip/stateUpdate")
    Call<ResponseBody> equipStateUpdate(@Field("equipCode") int equipCode, @Field("manager") String manager, @Field("workYn") Boolean workYn);

    /**
     * 자재 리스트 조회
     */
    @GET("/app/materials/list")
    Call<ResponseBody> materialsList(@Query("materialsName") String materialsName);

    /**
     * 출고 리스트 조회
     */
    @GET("/app/release/list")
    Call<ResponseBody> releaseList(@Query("clientName") String clientName, @Query("outDateTime") String outDateTime);


    /**
     * 출고 제품 조회
     */
    @FormUrlEncoded
    @POST("/app/release/productList")
    Call<ResponseBody> releaseProductList(@Field("requestCode") int requestCode);

    /**
     * 의뢰서 조회
     */
    @GET("/app/request/list")
    Call<ResponseBody> requestList(@Query("clientName") String clientName,
                                   @Query("dentalFormula") String dentalFormula,
                                   @Query("requestDate") String orderDate,
                                   @Query("dueDate") String deadlineDate);

    /**
     * 의뢰서별 제품 조회
     */
    @FormUrlEncoded
    @POST("/app/request/productState")
    Call<ResponseBody> requestProductState(@Field("requestCode") int requestCode);

    // YK 전용 이미지 업로드
    /**
     * 이미지 업로드
     */
    @Multipart
    @POST("/app/divide/REQUEST/uploadImg")
    Call<ResponseBody> uploadImgYK(@Part MultipartBody.Part file, @Field("ip") String ip);

    /**
     * 이미지 여러장 업로드
     */
    @Multipart
    @POST("/app/divide/REQUEST/uploadImages")
    Call<ResponseBody> uploadImagesYK(@Part List<MultipartBody.Part> files, @Part("userId") RequestBody userId, @Part("requestCode") RequestBody requestCode, @Part("imgName") RequestBody imgName, @Part("ip") RequestBody ip);

    /**
     * 이미지 삭제
     */
    @FormUrlEncoded
    @POST("/app/divide/REQUEST/deleteImage")
    Call<ResponseBody> deleteImageYK(@Field("imageName") String imageName, @Field("userId") String userId, @Field("requestCode") String requestCode, @Field("imgName") String imgName, @Field("ip") String ip);

    /**
     * 이미지명 조회
     */
    @FormUrlEncoded
    @POST("/app/divide/REQUEST/getImgName")
    Call<ResponseBody> getImgNameYK(@Field("requestCode") String requestCode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 메모 조회
     */
    @FormUrlEncoded
    @POST("/app/divide/REQUEST/getMemo")
    Call<ResponseBody> getMemoYK(@Field("requestCode") String requestCode, @Field("userId") String userId, @Field("ip") String ip);

    /**
     * 메모 작성
     */
    @FormUrlEncoded
    @POST("/app/divide/REQUEST/writeMemo")
    Call<ResponseBody> writeMemoYK(@Field("requestCode") String requestCode, @Field("remark") String remark, @Field("userId") String userId, @Field("ip") String ip);
}
