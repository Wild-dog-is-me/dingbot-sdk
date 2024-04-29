package com.workplat.dingbot.common;

/**
 * @Author: Odin
 * @Date: 2024/4/29 09:49
 * @Description:
 */
public class DingContent {

    private String title;

    private String msgUrl;

    private String picUrl;

    public DingContent(String title, String msgUrl, String picUrl) {
        this.title = title;
        this.msgUrl = msgUrl;
        this.picUrl = picUrl;
    }

    public DingContent() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsgUrl() {
        return msgUrl;
    }

    public void setMsgUrl(String msgUrl) {
        this.msgUrl = msgUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
