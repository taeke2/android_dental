package gbsoft.com.dental_gb.dto;

public class MenuItem implements Comparable<MenuItem> {
    private int mMenuId;
    private String mMenuTxt;
    private String mMenuIcon;
    int mDrawable;

    public MenuItem(int menuId, String menuText, String menuIcon, int drawable) {
        this.mMenuId = menuId;
        this.mMenuTxt = menuText;
        this.mMenuIcon = menuIcon;
        this.mDrawable = drawable;
    }

    public int getMenuId() {
        return mMenuId;
    }

    public String getMenuTxt() {
        return mMenuTxt;
    }

    public String getMenuIcon() {
        return mMenuIcon;
    }

    public int getDrawable() {
        return mDrawable;
    }

    @Override
    public int compareTo(MenuItem o) {
        if (o.getMenuId() < mMenuId)
            return 1;
        else if (o.getMenuId() > mMenuId)
            return -1;
        return 0;
    }
}
