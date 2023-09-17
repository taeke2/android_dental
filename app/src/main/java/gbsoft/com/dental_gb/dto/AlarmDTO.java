package gbsoft.com.dental_gb.dto;

public class AlarmDTO {
    private int mId;
    private String mTitle;
    private String mContent;
    private String mTime;
    private long mTimeStamp;
    private boolean mIsChecked = false;

    public AlarmDTO(int id, String title, String content, String time, long timeStamp) {
        mId = id;
        mTitle = title;
        mContent = content;
        mTime = time;
        mTimeStamp = timeStamp;
    }

    public void setTitle(String title) { mTitle = title; }
    public void setContent(String content) { mContent = content; }
    public void setTime(String time) { mTime = time; }
    public void setIsChecked(boolean isChecked) { mIsChecked = isChecked; }

    public int getId() { return mId; }
    public String getTitle() { return mTitle; }
    public String getContent() { return mContent; }
    public String getTime() { return mTime; }
    public long getTimeStamp() { return mTimeStamp; }
    public boolean getIsChecked() { return mIsChecked; }
}
