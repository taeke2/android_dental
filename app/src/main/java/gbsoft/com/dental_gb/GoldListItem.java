package gbsoft.com.dental_gb;

public class GoldListItem {
    private String mGoldCode;
    private String mClientCode;
    private String mClientName;
    private String mGoldDate;
    private String mInQuantity;
    private String mOutQuantity;
    private String mUseQuantity;
    private String mStockQuantity;
    private String mRequestCode;
    private String mPatientName;
    private String mOrderDate;

    public GoldListItem(String clientCode, String clientName, String stockQuantity) {
        this.mClientCode = clientCode;
        this.mClientName = clientName;
        this.mStockQuantity = stockQuantity;
    }

    public GoldListItem(String goldCode, String goldDate, String inQuantity, String outQuantity, String useQuantity) {
        this.mGoldCode = goldCode;
        this.mGoldDate = goldDate;
        this.mInQuantity = inQuantity;
        this.mOutQuantity = outQuantity;
        this.mUseQuantity = useQuantity;
    }

    public GoldListItem(String clientName, String patientName, String orderDate, String requestCode) {
        this.mClientName = clientName;
        this.mPatientName = patientName;
        this.mOrderDate = orderDate;
        this.mRequestCode = requestCode;
    }

    public String getGoldCode() {
        return mGoldCode;
    }

    public String getClientCode() {
        return mClientCode;
    }

    public String getClientName() {
        return mClientName;
    }

    public String getGoldDate() {
        return mGoldDate;
    }

    public String getInQuantity() {
        return mInQuantity;
    }

    public String getOutQuantity() {
        return mOutQuantity;
    }

    public String getUseQuantity() {
        return mUseQuantity;
    }

    public String getStockQuantity() {
        return mStockQuantity;
    }

    public String getRequestCode() {
        return mRequestCode;
    }

    public String getPatientName() {
        return mPatientName;
    }

    public String getOrderDate() {
        return mOrderDate;
    }
}
