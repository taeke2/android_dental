package gbsoft.com.dental_gb;

public class OrderItem {
    ///////////////
    // 임시 추가
    private String mOrderBarcode = "";
    private String mRequestCode = "";
    private int mRequestCodeNum;
    private int mProductCodeNum;
    private int mProcessIdx = -1;
    private String mBoxNo = "";
    private String mBoxName = "";
    private String mStartedProcessName = "";
    private String mEndTime = "";
    private String mMemo = "";
    ///////////////
    private String mProductCode;
    private String mProductInfoCode;
    private String mProductName;
    private String mProductProcess;
    private String mManagers = "";
    private String mStartTime = "";
    private int mProcessLength;
    private String mProcessState = "-1";


    ///////////////
    public String getOrderBarcode() { return mOrderBarcode; }

    public void setOrderBarcode(String orderBarcode) { this.mOrderBarcode = orderBarcode; }

    public String getRequestCode() { return mRequestCode; }

    public void setRequestCode(String requestCode) { this.mRequestCode = requestCode; }

    public int getRequestCodeNum() { return mRequestCodeNum; }

    public void setRequestCodeNum(int requestCodeNum) { this.mRequestCodeNum = requestCodeNum;}

    public int getProductCodeNum() { return mProductCodeNum; }

    public void setProductCodeNum(int productCodeNum) { this.mProductCodeNum = productCodeNum; }

    public int getProcessIdx() { return mProcessIdx; }

    public void setProcessIdx(int processIdx) { this.mProcessIdx = processIdx; }

    public String getBoxNo() { return mBoxNo; }

    public void setBoxNum(String boxNo) { this.mBoxNo = boxNo; }

    public String getBoxName() { return mBoxName; }

    public void setBoxName(String boxName) { mBoxName = boxName; }

    public String getStartedProcessName() { return mStartedProcessName; }

    public void setStartedProcessName(String startedProcessName) { this.mStartedProcessName = startedProcessName; }

    public String getEndTime() { return mEndTime; }

    public void setEndTime(String endTime) { this.mEndTime = endTime; }

    public String getMemo() { return mMemo; }

    public void setMemo(String memo) { mMemo = memo; }
    ///////////////


    public String getProductCode() {
        return mProductCode;
    }

    public void setProductCode(String productCode) {
        this.mProductCode = productCode;
    }

    public String getProductInfoCode() {
        return mProductInfoCode;
    }

    public void setProductInfoCode(String productInfoCode) {
        this.mProductInfoCode = productInfoCode;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        this.mProductName = productName;
    }

    public String getProductProcess() {
        return mProductProcess;
    }

    public void setProductProcess(String productProcess) {
        this.mProductProcess = productProcess;
    }

    public String getManagers() { return mManagers; }

    public void setManagers(String managers) { this.mManagers = managers; }

    public String getStartTime() { return mStartTime; }

    public void setStartTime(String startTime) { this.mStartTime = startTime; }

    public int getProcessLength() {
        return mProcessLength;
    }

    public void setProcessLength(int processLength) {
        this.mProcessLength = processLength;
    }

    public String getProcessState() {
        return mProcessState;
    }

    public void setProcessState(String processState) {
        this.mProcessState = processState;
    }
}
