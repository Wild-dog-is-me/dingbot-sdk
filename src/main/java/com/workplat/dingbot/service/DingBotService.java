package com.workplat.dingbot.service;

import com.alibaba.fastjson.JSON;
import com.workplat.dingbot.api.DingBotApi;
import com.workplat.dingbot.common.ContentType;
import com.workplat.dingbot.common.DingContent;
import com.workplat.dingbot.configuration.DingBotConfig;
import com.workplat.dingbot.util.HttpUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Odin
 * @Date: 2024/4/29 14:12
 * @Description:
 */

@Service("dingBotApi")
public class DingBotService implements DingBotApi {

    private final static Logger logger = LoggerFactory.getLogger(DingBotService.class);

    @Autowired
    private DingBotConfig dingBotConfig;

    @Override
    public String sendMarkdownMsgTest(String botUrl, String botKey, String botToken, String content) {
        return sendMarkdownMsgTest(botUrl, botKey, botToken, content, null);
    }

    @Override
    public String sendMarkdownMsgTest(String botUrl, String botKey, String botToken, String content, List<String> mobileList) {
        Map<String, Object> markdownMap = new HashMap<>();
        markdownMap.put("title", "内容详情");
        markdownMap.put("text", content);
        return send(botUrl, botKey, botToken, ContentType.Markdown, markdownMap, true, mobileList);
    }

    @Override
    public String sendTextMsg(String content) {
        return sendTextMsg(content, null);
    }

    @Override
    public String sendTextMsg(String content, List<String> mobileList) {
        Map<String, Object> textMap = new HashMap<>();
        textMap.put("content", content);
        return send(dingBotConfig.getBotUrl(), dingBotConfig.getBotKey(), dingBotConfig.getBotToken(), ContentType.Text, textMap, false, mobileList);
    }

    @Override
    public String sendLinkMsg(String title, String content, String picUrl, String messageUrl) {
        return sendLinkMsg(title, content, picUrl, messageUrl, null);
    }

    @Override
    public String sendLinkMsg(String title, String content, String picUrl, String messageUrl, List<String> mobileList) {
        Map<String, Object> linksMap = new HashMap<>();
        linksMap.put("title", title);
        linksMap.put("text", content);
        linksMap.put("picUrl", picUrl);
        linksMap.put("messageUrl", messageUrl);
        return send(dingBotConfig.getBotUrl(), dingBotConfig.getBotKey(), dingBotConfig.getBotToken(), ContentType.Link, linksMap, true, mobileList);
    }

    @Override
    public String sendMarkdownMsg(String title, String content) {
        return sendMarkdownMsg(title, content, null);
    }

    @Override
    public String sendMarkdownMsg(String title, String content, List<String> mobileList) {
        Map<String, Object> markdownMap = new HashMap<>();
        markdownMap.put("title", title);
        markdownMap.put("text", content);
        return send(dingBotConfig.getBotUrl(), dingBotConfig.getBotKey(), dingBotConfig.getBotToken(), ContentType.Markdown, markdownMap, true, mobileList);
    }

    @Override
    public String sendActionCardMsg(String title, String content, String singleURL) {
        return sendActionCardMsg(title, content, singleURL, null);
    }

    @Override
    public String sendActionCardMsg(String title, String content, String singleURL, List<String> mobileList) {
        Map<String, Object> actionCardMap = new HashMap<>();
        actionCardMap.put("title", title);
        actionCardMap.put("text", content);
        actionCardMap.put("btnOrientation", "0");
        actionCardMap.put("singleTitle", "阅读全文");
        actionCardMap.put("singleURL", singleURL);
        return send(dingBotConfig.getBotUrl(), dingBotConfig.getBotKey(), dingBotConfig.getBotToken(), ContentType.ActionCard, actionCardMap, true, mobileList);
    }

    @Override
    public String sendFeedCardMsg(List<DingContent> dingContents) {
        return sendFeedCardMsg(dingContents, null);
    }

    @Override
    public String sendFeedCardMsg(List<DingContent> dingContents, List<String> mobileList) {
        // FeedCard类型
        Map<String, Object> feedCardMap = new HashMap<>();

        List<Map<String, String>> links = new ArrayList<>();
        for (DingContent dingContent : dingContents) {
            Map<String, String> link = new HashMap<>();
            link.put("title", dingContent.getTitle());
            link.put("messageURL", dingContent.getMsgUrl());
            link.put("picURL", dingContent.getPicUrl());
            links.add(link);
        }
        feedCardMap.put("links", links);
        return send(dingBotConfig.getBotUrl(), dingBotConfig.getBotKey(), dingBotConfig.getBotToken(), ContentType.FeedCard, feedCardMap, true, mobileList);
    }

    private String send(String botUrl, String botKey, String botToken, ContentType contentType, Map<String, Object> content, boolean isAtAll, List<String> mobileList) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sign = dingHmacSHA256(timestamp, botKey);
        String dingUrl = botUrl + "?access_token=" + botToken + "&timestamp=" + timestamp + "&sign=" + sign;

        Map<String, Object> atMap = new HashMap<>();
        //1.是否通知所有人
        atMap.put("isAtAll", !CollectionUtils.isEmpty(mobileList) ? false : isAtAll);
        //2.通知具体人的手机号码列表
        atMap.put("atMobiles", CollectionUtils.isEmpty(mobileList) ? new ArrayList<>() : mobileList);

        // 消息体内容
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("msgtype", contentType.getType());
        reqMap.put("at", atMap);
        reqMap.put(contentType.getType(), content);
        return HttpUtils.httpPost(dingUrl, JSON.toJSONString(reqMap));
    }

    /**
     * 钉钉自定义机器人安全设置
     * 把timestamp+"\n"+密钥当做签名字符串，使用HmacSHA256算法计算签名，然后进行Base64 encode，最后再把签名参数再进行urlEncode，得到最终的签名（需要使用UTF-8字符集）
     * @param secret
     * @return
     */
    private String dingHmacSHA256(String timestamp, String secret) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            return sign;
        } catch (Exception e) {
            logger.error("dingHmacSHA256加密失败", e);
        }
        return null;
    }
}
