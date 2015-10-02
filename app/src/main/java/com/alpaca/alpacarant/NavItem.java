package com.alpaca.alpacarant;

/**
 * Created by justinyeo on 17/9/15.
 */
public class NavItem {

    private String title;
    private int resIcon;

    public NavItem(String title, int resIcon) {
        super();
        this.title = title;
        this.resIcon = resIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }
}
