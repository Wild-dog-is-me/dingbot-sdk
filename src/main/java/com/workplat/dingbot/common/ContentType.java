package com.workplat.dingbot.common;

/**
 * @Author: Odin
 * @Date: 2024/4/29 09:45
 * @Description:钉钉消息类型
 */
public enum ContentType {

    /**
     * 消息类型
     */
    Text("text"),
    Link("link"),
    Markdown("markdown"),
    ActionCard("actionCard"),
    FeedCard("feedCard"),
    ;

    private String type;
    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type = type;
    }
}
