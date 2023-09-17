package gbsoft.com.dental_gb.dto;

public class NoticeDTO {
    private int mCode;
    private String mManager;
    private String mTitle;
    private String mContent;
    private String mDate;
    private String mEndDate;
    private boolean mImportantYn;
    private String mTarget;

    public NoticeDTO(
            int code, String manager, String title,
            String content, String date, String endDate,
            boolean importantYn, String target) {
        this.mCode = code;
        this.mManager = manager;
        this.mTitle = title;
        this.mContent = content;
        this.mDate = date;
        this.mEndDate = endDate;
        this.mImportantYn = importantYn;
        this.mTarget = target;
    }

    public int getCode() {
        return mCode;
    }
    public String getManager() { return mManager; }
    public String getTitle() {
        return mTitle;
    }
    public String getContent() {
        return mContent;
    }
    public String getDate() { return mDate; }
    public String getEndDate() { return mEndDate; }
    public boolean getImportantYn() {
        return mImportantYn;
    }
    public String getTarget() {
        return mTarget;
    }
}
