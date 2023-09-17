package gbsoft.com.dental_gb;

public class PlantListDTO {
    private int id; // DB: equipCode(설비코드)

    private String plantName;
    private String clientName; // DB: introductionCompany(납품업체)
    private String SN;
    private String introductionDate;
    private String vendor;
    private String personInChargeOfVendor; // DB: introductionManager(납품담당자)
    private String phoneNumber;
    private String email;
    private String AsNumber;
    private String remark;
    private int plantState; // 0: 정지, 1: 가동, 2: 에러  --> DB: workYn(가동관리 여부)
    private int power; // 0: 정지, 1: 가동 --> DB: checkYn(점검관리 여부)

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlantState() {
        return plantState;
    }

    public void setPlantState(int plantState) {
        this.plantState = plantState;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getIntroductionDate() {
        return introductionDate;
    }

    public void setIntroductionDate(String introductionDate) {
        this.introductionDate = introductionDate;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPersonInChargeOfVendor() {
        return personInChargeOfVendor;
    }

    public void setPersonInChargeOfVendor(String personInChargeOfVendor) {
        this.personInChargeOfVendor = personInChargeOfVendor;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAsNumber() {
        return AsNumber;
    }

    public void setAsNumber(String asNumber) {
        AsNumber = asNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
