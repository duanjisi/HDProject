package com.atgc.hd.comm.widget.popupwindow;

/**
 *
 * @author lianggk
 * @date 17/9/29
 */
public class PopupItem {
    private int tag;
    private int icon;
    private String title;

    public PopupItem(int tag, String title) {
        this.tag = tag;
        this.title = title;
    }

    public PopupItem(String title, int tag, int icon) {
        this(tag, title);
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

}
