package gbsoft.com.dental_gb.dto;

public class CompanyDTO {
    private int mCompanyCode;
    private String mCompanyName;
    private String mServerPath;

    public int getCompanyCode() {
        return mCompanyCode;
    }

    public void setCompanyCode(int companyCode) {
        mCompanyCode = companyCode;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public void setCompanyName(String companyName) {
        mCompanyName = companyName;
    }

    public String getServerPath() {
        return mServerPath;
    }

    public void setServerPath(String serverPath) {
        mServerPath = serverPath;
    }
}
