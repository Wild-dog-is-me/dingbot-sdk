package com.workplat.dingbot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Odin
 * @Date: 2024/4/29 09:29
 * @Description:
 */

/**
 * ding:
 *   bot-url: https://oapi.dingtalk.com/robot/send
 *   bot-key: xxxxxx
 *   bot-token: xxxxxx
 *   bot-enabled: true
 *   trigger-words:
 *     - 异常
 *     - NullPointerException
 * @author odin
 */
@Validated
@Component
@ConfigurationProperties(prefix = "ding")
public class DingBotConfig {

    private String botUrl = "https://oapi.dingtalk.com/robot/send";

    private String botKey = "SECaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    private String botToken = "xxxxxxxxxxxxxxxxxxxxxxxxx";

    /** 是否开启机器人默认：false */
    private boolean botEnabled = false;

    /** 触发词 */
    private List<String> triggerWords = new ArrayList<>();

    public String getBotUrl() {
        return botUrl;
    }

    public void setBotUrl(String botUrl) {
        this.botUrl = botUrl;
    }

    public String getBotKey() {
        return botKey;
    }

    public void setBotKey(String botKey) {
        this.botKey = botKey;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public boolean isBotEnabled() {
        return botEnabled;
    }

    public void setBotEnabled(boolean botEnabled) {
        this.botEnabled = botEnabled;
    }

    public List<String> getTriggerWords() {
        return triggerWords;
    }

    public void setTriggerWords(List<String> triggerWords) {
        this.triggerWords = triggerWords;
    }
}
