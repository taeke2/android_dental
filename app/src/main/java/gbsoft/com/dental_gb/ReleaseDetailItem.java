package gbsoft.com.dental_gb;

public class ReleaseDetailItem {
    private String mProductName;
    private String mDeadlineDateTime;
    private String mOrderFinishTime;
    private String mDentalFormula;

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        mProductName = productName;
    }

    public String getDeadlineDateTime() {
        return mDeadlineDateTime;
    }

    public void setDeadlineDateTime(String deadlineDateTime) {
        mDeadlineDateTime = deadlineDateTime;
    }

    public String getOrderFinishTime() {
        return mOrderFinishTime;
    }

    public void setOrderFinishTime(String orderFinishTime) {
        mOrderFinishTime = orderFinishTime;
    }

    public String getDentalFormula() {
        return mDentalFormula;
    }

    public void setDentalFormula(String dentalFormula) {
        mDentalFormula = dentalFormula;
    }
}
