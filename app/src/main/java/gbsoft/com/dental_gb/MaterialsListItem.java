package gbsoft.com.dental_gb;

public class MaterialsListItem {
    private String mReceivingBarcode = "";
    private String mMaterialsCode = "";
    private String mClientCode = "";
    private String mMaterialsName = "";
    private String mClientName = "";
    private String mInDate = "";
    private String mQuantity = "";

    public MaterialsListItem(String receivingBarcode, String materialsCode, String clientCode, String materialsName, String clientName, String inDate, String quantity) {
        this.mReceivingBarcode = receivingBarcode;
        this.mMaterialsCode = materialsCode;
        this.mClientCode = clientCode;
        this.mMaterialsName = materialsName;
        this.mClientName = clientName;
        this.mInDate = inDate;
        this.mQuantity = quantity;
    }

    public String getReceivingBarcode() {
        return mReceivingBarcode;
    }

    public String getMaterialsCode() {
        return mMaterialsCode;
    }

    public String getClientCode() {
        return mClientCode;
    }

    public String getMaterialsName() {
        return mMaterialsName;
    }

    public String getClientName() {
        return mClientName;
    }

    public String getInDate() {
        return mInDate;
    }

    public String getQuantity() {
        return mQuantity;
    }

    public void setReceivingBarcode(String materialsInOutCode) {
        this.mReceivingBarcode = mReceivingBarcode;
    }

    public void setMaterialsName(String materialsName) {
        this.mMaterialsName = materialsName;
    }

    public void setClientName(String clientName) {
        this.mClientName = clientName;
    }

    public void setInDate(String inDate) {
        this.mInDate = inDate;
    }

    public void setQuantity(String quantity) {
        this.mQuantity = quantity;
    }
}
