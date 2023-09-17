package gbsoft.com.dental_gb.dto;

public class ArticulatorDTO {
    private int mCode;
    private String mName;
    private String mUser;
    private String mUseDate;

    public ArticulatorDTO(int code, String name, String user, String useDate) {
        this.mCode = code;
        this.mName = name;
        this.mUser = user;
        this.mUseDate = useDate;
    }

    public int getCode() { return mCode; }
    public String getName() { return mName; }
    public String getUser() { return mUser; }
    public String getUseDate() {return mUseDate; }
}
