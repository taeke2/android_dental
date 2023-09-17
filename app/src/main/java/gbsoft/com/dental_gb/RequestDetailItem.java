package gbsoft.com.dental_gb;

class RequestDetailItem {

    private String mProductName = "";
    private String mProductPart = "";
    private String mDentalFormula = "";
    private String mShade = "";


    private String mImplant1 = "";
    private String mImplant2 = "";
    private String mImplant3 = "";
    private String mProsthesis1 = "";
    private String mProsthesis2 = "";

    private String mDenture = "";
    private String mTmpDenture = "";
    private String mFrame = "";
    private String mTray = "";
    private String mWaxrim = "";
    private String mArray = "";
    private String mQuering = "";
    private String mDentureRepairing = "";
    private String mEtc = "";
    private String mAg = "";

    private String mImplantCrown = "";
    private String mSpaceLack = "";
    private String mPonticDesign = "";
    private String mMetalDesign = "";

    private String mFullDenture = "";
    private String mFlexibleDenture = "";
    private String mPartialDenture = "";
    private String mDentureExtra = "";

    private String mMemo = "";
    private String mDivisions = "";

    // SIMMI 21
    public RequestDetailItem(String productName, String productPart, String dentalFormula, String shade, String implant1, String implant2, String implant3,
                             String prosthesis1, String prosthesis2, String denture, String tmpDenture, String frame, String tray, String waxrim,
                             String array, String quering, String dentureRepairing, String etc, String ag, String memo, String divisions) {

        this.mProductName = productName;
        this.mProductPart = productPart;
        this.mDentalFormula = dentalFormula;
        this.mShade = shade;
        this.mImplant1 = implant1;
        this.mImplant2 = implant2;
        this.mImplant3 = implant3;
        this.mProsthesis1 = prosthesis1;
        this.mProsthesis2 = prosthesis2;
        this.mDenture = denture;
        this.mTmpDenture = tmpDenture;
        this.mFrame = frame;
        this.mTray = tray;
        this.mWaxrim = waxrim;
        this.mArray = array;
        this.mQuering = quering;
        this.mDentureRepairing = dentureRepairing;
        this.mEtc = etc;
        this.mAg = ag;
        this.mMemo = memo;
        this.mDivisions = divisions;
    }

    // THESTYLE 5
    public RequestDetailItem(String productName, String productPart, String dentalFormula, String memo, String divisions) {
        this.mProductName = productName;
        this.mProductPart = productPart;
        this.mDentalFormula = dentalFormula;
        this.mMemo = memo;
        this.mDivisions = divisions;
    }

    // PARTNERSE 14
    public RequestDetailItem(String productPart, String productName, String dentalFormula, String implantCrown, String spaceLack, String ponticDesign,
                             String metalDesign, String fullDenture, String flexibleDenture, String ag, String partialDenture, String dentureExtra,
                             String memo, String shade, String divisions) {
        this.mProductPart = productPart;
        this.mProductName = productName;
        this.mDentalFormula = dentalFormula;
        this.mImplantCrown = implantCrown;
        this.mSpaceLack = spaceLack;
        this.mPonticDesign = ponticDesign;
        this.mMetalDesign = metalDesign;
        this.mFullDenture = fullDenture;
        this.mFlexibleDenture = flexibleDenture;
        this.mAg = ag;
        this.mPartialDenture = partialDenture;
        this.mDentureExtra = dentureExtra;
        this.mMemo = memo;
        this.mShade = shade;
        this.mDivisions = divisions;
    }

    // UNI 20
    public RequestDetailItem(String productName, String productPart, String dentalFormula, String shade, String implant1, String implant2, String implant3,
                             String prosthesis1, String prosthesis2, String denture, String tmpDenture, String frame, String tray, String waxrim,
                             String array, String quering, String dentureRepairing, String etc, String ag, String memo) {

        this.mProductName = productName;
        this.mProductPart = productPart;
        this.mDentalFormula = dentalFormula;
        this.mShade = shade;
        this.mImplant1 = implant1;
        this.mImplant2 = implant2;
        this.mImplant3 = implant3;
        this.mProsthesis1 = prosthesis1;
        this.mProsthesis2 = prosthesis2;
        this.mDenture = denture;
        this.mTmpDenture = tmpDenture;
        this.mFrame = frame;
        this.mTray = tray;
        this.mWaxrim = waxrim;
        this.mArray = array;
        this.mQuering = quering;
        this.mDentureRepairing = dentureRepairing;
        this.mEtc = etc;
        this.mAg = ag;
        this.mMemo = memo;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        this.mProductName = productName;
    }

    public String getProductPart() {
        return mProductPart;
    }

    public void setProductPart(String productPart) {
        this.mProductPart = productPart;
    }

    public String getDentalFormula() {
        return mDentalFormula;
    }

    public void setDentalFormula(String dentalFormula) {
        this.mDentalFormula = dentalFormula;
    }

    public String getShade() {
        return mShade;
    }

    public void setShade(String shade) {
        this.mShade = shade;
    }

    public String getImplant1() {
        return mImplant1;
    }

    public void setImplant1(String implant1) {
        this.mImplant1 = implant1;
    }

    public String getImplant2() {
        return mImplant2;
    }

    public void setImplant2(String implant2) {
        this.mImplant2 = implant2;
    }

    public String getImplant3() {
        return mImplant3;
    }

    public void setImplant3(String implant3) {
        this.mImplant3 = implant3;
    }

    public String getProsthesis1() {
        return mProsthesis1;
    }

    public void setProsthesis1(String prosthesis1) {
        this.mProsthesis1 = prosthesis1;
    }

    public String getProsthesis2() {
        return mProsthesis2;
    }

    public void setProsthesis2(String prosthesis2) {
        this.mProsthesis2 = prosthesis2;
    }

    public String getDenture() {
        return mDenture;
    }

    public void setDenture(String denture) {
        this.mDenture = denture;
    }

    public String getTmpDenture() {
        return mTmpDenture;
    }

    public void setTmpDenture(String tmpDenture) {
        this.mTmpDenture = tmpDenture;
    }

    public String getFrame() {
        return mFrame;
    }

    public void setFrame(String frame) {
        this.mFrame = frame;
    }

    public String getTray() {
        return mTray;
    }

    public void setTray(String tray) {
        this.mTray = tray;
    }

    public String getWaxrim() {
        return mWaxrim;
    }

    public void setWaxrim(String waxrim) {
        this.mWaxrim = waxrim;
    }

    public String getArray() {
        return mArray;
    }

    public void setArray(String array) {
        this.mArray = array;
    }

    public String getQuering() {
        return mQuering;
    }

    public void setQuering(String quering) {
        this.mQuering = quering;
    }

    public String getDentureRepairing() {
        return mDentureRepairing;
    }

    public void setDentureRepairing(String dentureRepairing) {
        this.mDentureRepairing = dentureRepairing;
    }

    public String getEtc() {
        return mEtc;
    }

    public void setEtc(String etc) {
        this.mEtc = etc;
    }

    public String getAg() {
        return mAg;
    }

    public void setAg(String ag) {
        this.mAg = ag;
    }

    public String getMemo() {
        return mMemo;
    }

    public void setMemo(String memo) {
        this.mMemo = memo;
    }

    public String getImplantCrown() {
        return mImplantCrown;
    }

    public void setImplantCrown(String implantCrown) {
        this.mImplantCrown = implantCrown;
    }

    public String getSpaceLack() {
        return mSpaceLack;
    }

    public void setSpaceLack(String spaceLack) {
        this.mSpaceLack = spaceLack;
    }

    public String getPonticDesign() {
        return mPonticDesign;
    }

    public void setPonticDesign(String ponticDesign) {
        this.mPonticDesign = ponticDesign;
    }

    public String getMetalDesign() {
        return mMetalDesign;
    }

    public void setMetalDesign(String metalDesign) {
        this.mMetalDesign = metalDesign;
    }

    public String getFullDenture() {
        return mFullDenture;
    }

    public void setFullDenture(String fullDenture) {
        this.mFullDenture = fullDenture;
    }

    public String getFlexibleDenture() {
        return mFlexibleDenture;
    }

    public void setFlexibleDenture(String flexibleDenture) {
        this.mFlexibleDenture = flexibleDenture;
    }

    public String getPartialDenture() {
        return mPartialDenture;
    }

    public void setPartialDenture(String partialDenture) {
        this.mPartialDenture = partialDenture;
    }

    public String getDentureExtra() {
        return mDentureExtra;
    }

    public void setDentureExtra(String dentureExtra) {
        this.mDentureExtra = dentureExtra;
    }

    public String getDivisions() {
        return mDivisions;
    }

    public void setDivisions(String divisions) {
        this.mDivisions = divisions;
    }

}
