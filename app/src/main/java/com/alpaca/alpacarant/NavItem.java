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

    /**
     * Accessor for title of navigation item
     * @return  Title of navigation item
     */
    public String getTitle() {
        return title;
    }

    /**
     * Mutator for title of navigation item
     * @param title Attribute for title of navigation item
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Accessor for res icon
     * @return  Res icon
     */
    public int getResIcon() {
        return resIcon;
    }

    /**
     * Mutator for res icon
     * @param resIcon   Attribute for res icon
     */
    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }
}
