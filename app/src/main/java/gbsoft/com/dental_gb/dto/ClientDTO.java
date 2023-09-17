package gbsoft.com.dental_gb.dto;

public class ClientDTO {
    private int mCode;
    private String mKey;
    private String mName;
    private String mType;
    private String mRepresentative;
    private String mRegisterNum;
    private String mAddress;
    private String mTel;
    private String mFax;
    private boolean mSysUseYn;
    private String mEmail;
    private String mRefer;

    public ClientDTO(
            int code,
            String key,
            String name,
            String type,
            String representative,
            String registerNum,
            String address,
            String tel,
            String fax,
            boolean sysUseYn,
            String email,
            String refer) {
        this.mCode = code;
        this.mKey = key;
        this.mName = name;
        this.mType = type;
        this.mRepresentative = representative;
        this.mRegisterNum = registerNum;
        this.mAddress = address;
        this.mTel = tel;
        this.mFax = fax;
        this.mSysUseYn = sysUseYn;
        this.mEmail = email;
        this.mRefer = refer;
    }

    public int getCode() { return mCode; }
    public String getKey() {
        return mKey;
    }
    public String getName() {
        return mName;
    }
    public String getType() {
        return mType;
    }
    public String getRepresentative() {
        return mRepresentative;
    }
    public String getRegisterNum() { return mRegisterNum; }
    public String getAddress() {
        return mAddress;
    }
    public String getTel() {
        return mTel;
    }
    public String getFax() {
        return mFax;
    }
    public boolean getSysUseYn() {return mSysUseYn; }
    public String getEmail() {
        return mEmail;
    }
    public String getRefer() {
        return mRefer;
    }
}

