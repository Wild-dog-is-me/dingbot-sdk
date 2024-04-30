package com.workplat.dingbot.configuration;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import com.workplat.dingbot.api.DingBotApi;
import com.workplat.dingbot.util.DingBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Odin
 * @Date: 2024/4/29 18:57
 * @Description:
 */
public class CusLogLogbackAppender extends AppenderBase {
    @Override
    protected void append(Object eventObject) {
        if(eventObject instanceof LoggingEvent){
            LoggingEvent loggingEvent = (LoggingEvent)eventObject;
            //拿到ThrowableProxy
            ThrowableProxy throwableProxy = (ThrowableProxy) loggingEvent.getThrowableProxy();
            if (Objects.nonNull(throwableProxy)) {
                //获取 throwable 顶级异常
                Throwable throwable = throwableProxy.getThrowable();
                //获取log的msg
                String formattedMessage = loggingEvent.getFormattedMessage();
                DingBotConfig dingbotConfig = DingBean.getBean(DingBotConfig.class);
                System.out.println("============="+formattedMessage+"=============");
                if (dingbotConfig.isBotEnabled()) {
                    DingBotApi dingBotApi = DingBean.getBean(DingBotApi.class);
                    List<String> triggerWords = dingbotConfig.getTriggerWords();
                    System.out.println("============="+triggerWords.toString()+"=============");
                    if (!CollectionUtils.isEmpty(triggerWords)) {
                        for (String word : triggerWords) {
                            if (formattedMessage != null && isContained(word, formattedMessage)) {
                                String active = DingBean.getEnvironment().getProperty("spring.profiles.active");
                                if (!Objects.isNull(active)) {
                                    System.out.println("=============" + active + "=============");
                                } else {
                                    System.out.println("=============" + "active is null" + "=============");
                                }
                                dingBotApi.sendTextMsg(active + "：" + formattedMessage + "\n" + throwable.toString(), null);
                                break;
                            }
                        }
                    }
                }
            }
        }
        super.doAppend(eventObject);
    }

    /** 正则方法支持 */
    private boolean isContained(String regex, String content) {
        if (StringUtils.isEmpty(regex) && StringUtils.isEmpty(content)) {
            return false;
        }
        try {
            Pattern p = Pattern.compile(regex);
            Matcher matcher = p.matcher(content);
            return matcher.find();
        } catch (Exception e) {
            return false;
        }
    }
}
