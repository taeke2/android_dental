package gbsoft.com.dental_gb;

public class ProcessRequestReleaseListItem {
    private boolean mIsChecked = false;

    private String mOrderBarcode = "";
    private String mRequestCode = "";
    private String mClientName = "";
    private String mPatientName = "";
    private String mProductName = "";
    private String mProcessState = "";
    private String mOrderDate = "";
    private String mDeadlineDate = "";
    private String mOutDate = "";
    private String mDeadlineTime = "";
    private String mClientTel = "";
    private String mDivisions = "";
    private String mDentalFormula = "";
    private String mBoxNo = "";

    // 10
    public ProcessRequestReleaseListItem(String orderBarcode, String requestCode, String clientName, String patientName, String productName, String orderDate, String deadlineDate, String deadlineTime, String clientTel, String dentalFormula) {
        this.mOrderBarcode = orderBarcode;
        this.mRequestCode = requestCode;
        this.mClientName = clientName;
        this.mPatientName = patientName;
        this.mProductName = productName;
        this.mOrderDate = orderDate;
        this.mDeadlineDate = deadlineDate;
        this.mDeadlineTime = deadlineTime;
        this.mClientTel = clientTel;
        this.mDentalFormula = dentalFormula;
    }

    // 9
    public ProcessRequestReleaseListItem(String requestCode, String clientName, String patientName, String orderDate, String deadlineDate, String clientTel, String dentalFormula, String divisions, String productName) {
        this.mRequestCode = requestCode;
        this.mClientName = clientName;
        this.mPatientName = patientName;
        this.mOrderDate = orderDate;
        this.mDeadlineDate = deadlineDate;
        this.mClientTel = clientTel;
        this.mDentalFormula = dentalFormula;
        this.mDivisions = divisions;
        this.mProductName = productName;
    }

    // 7
    public ProcessRequestReleaseListItem(String requestCode, String clientName, String patientName, String productName, String orderDate, String deadlineDate, String outDate) {
        this.mRequestCode = requestCode;
        this.mClientName = clientName;
        this.mPatientName = patientName;
        this.mProductName = productName;
        this.mOrderDate = orderDate;
        this.mDeadlineDate = deadlineDate;
        this.mOutDate = outDate;
    }

    // 유경 (boxNo)
    public ProcessRequestReleaseListItem(String orderBarcode, String requestCode, String clientName, String patientName, String productName, String orderDate, String deadlineDate, String deadlineTime, String clientTel, String dentalFormula, String boxNo) {
        this.mOrderBarcode = orderBarcode;
        this.mRequestCode = requestCode;
        this.mClientName = clientName;
        this.mPatientName = patientName;
        this.mProductName = productName;
        this.mOrderDate = orderDate;
        this.mDeadlineDate = deadlineDate;
        this.mDeadlineTime = deadlineTime;
        this.mClientTel = clientTel;
        this.mDentalFormula = dentalFormula;
        this.mBoxNo = boxNo;
    }

    public String getOrderBarcode() {
        return mOrderBarcode;
    }

    public String getRequestCode() {
        return mRequestCode;
    }

    public String getClientName() {
        return mClientName;
    }

    public String getPatientName() {
        return mPatientName;
    }

    public String getProductName() {
        return mProductName;
    }

    public String getProcessState() {
        return mProcessState;
    }

    public String getOrderDate() {
        return mOrderDate;
    }

    public String getDeadlineDate() {
        return mDeadlineDate;
    }

    public String getDeadlineTime() {
        return mDeadlineTime;
    }

    public String getClientTel() {
        return mClientTel;
    }

    public String getDivisions() {
        return mDivisions;
    }

    public String getDentalFormula() {
        return mDentalFormula;
    }

    public String getOutDate() {
        return mOutDate;
    }

    public String getBoxNo() { return mBoxNo; }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }

    public void setOutDate(String outDate) {
        mOutDate = outDate;
    }

    public void setDentalFormula(String dentalFormula) {
        this.mDentalFormula = dentalFormula;
    }

    public void setDivisions(String divisions) {
        this.mDivisions = divisions;
    }

    public void setOrderBarcode(String orderBarcode) {
        this.mOrderBarcode = orderBarcode;
    }

    public void setRequestCode(String requestCode) {
        this.mRequestCode = requestCode;
    }

    public void setClientName(String clientName) {
        this.mClientName = clientName;
    }

    public void setPatientName(String patientName) {
        this.mPatientName = patientName;
    }

    public void setProductName(String productName) {
        this.mProductName = productName;
    }

    public void setProcessState(String processState) {
        this.mProcessState = processState;
    }

    public void setOrderDate(String orderDate) {
        this.mOrderDate = orderDate;
    }

    public void setDeadlineDate(String deadlineDate) {
        this.mDeadlineDate = deadlineDate;
    }

    public void setDeadlineTime(String deadlineTime) {
        this.mDeadlineTime = deadlineTime;
    }

    public void setClientTel(String clientTel) {
        this.mClientTel = clientTel;
    }

    public void setBoxNo(String boxNo) { this.mBoxNo = boxNo; }
}
