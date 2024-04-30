## DingBOT


> 配置
```yaml
ding:
  bot-url: https://oapi.dingtalk.com/robot/send
  bot-key: SECaxxx
  bot-token: xxx
```

> 使用
引入maven依赖
```xml
    <dependency>
        <groupId>com.workplat</groupId>
        <artifactId>ding-bot</artifactId>
        <version>1.0</version>
    </dependency>
```
使用方法
```java
// 自动装配
@Autowired
private DingBotApi dingBotApi;
// 通过Spring上下文获取
DingBotApi dingBotApi = DingBean.getBean(DingBotApi.class);

dingBotApi.sendMarkdownMsg(xxx,xxx,xxx)
```

