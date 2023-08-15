package com.hangu.task.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import java.util.Objects;
import org.slf4j.ILoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * @author wuzhenhong
 * @date 2023/8/9 16:34
 */
public class LogUtils {

    private static final Object LOCK = new Object();
    private static volatile ch.qos.logback.classic.Logger LOGGER;

    public static ch.qos.logback.classic.Logger getConsoleLogger(String loggerName) {

        if(Objects.nonNull(LOGGER)) {
            return LOGGER;
        }
        synchronized (LOCK) {
            if(Objects.nonNull(LOGGER)) {
                return LOGGER;
            }
            // 绑定日志实现，这里使用的是logback实现并解析配置，如果存在logback.xml，logback.groovy and so on 或者系统属性中有指定对应的路径
            ILoggerFactory loggerContext = StaticLoggerBinder.getSingleton().getLoggerFactory();

            /**
             * String fqcn, Logger logger, Level level, String message,
             *             Throwable throwable, Object[] argArray
             */
            ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) loggerContext.getLogger(loggerName);
            // 清除掉绑定时设置的appenders，因为在绑定日志实现的时候，会自动配置
            // 即使没有找到配置文件也会默认给ROOT logger 添加 ConsoleAppender
            logger.detachAndStopAllAppenders();

            Context context = (Context) loggerContext;

            // 咱们自己创建一个 ConsoleAppender
            ConsoleAppender consoleAppender = new ConsoleAppender();
            consoleAppender.setContext(context);
            // 日志输出编码器，比如设置字符集等操作，通过layout解析出来的日志字符串做进一步的处理
            PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder() {
                @Override
                public void setLayout(Layout<ILoggingEvent> layout) {
                    this.layout = layout;
                }
            };
            patternLayoutEncoder.setContext(context);

            // pattern 解析器，将 pattern 构建成用户能够读的日志
            PatternLayout patternLayout = new PatternLayout();
            patternLayout.setContext(context);
            // 设置格式化 pattern
            patternLayout.setPattern("%d{HH:mm:ss.SSS} %contextName [%thread] %-5level %logger{36} - %msg%n");
            patternLayout.start();

            patternLayoutEncoder.setLayout(patternLayout);
            patternLayoutEncoder.start();
            consoleAppender.setEncoder(patternLayoutEncoder);
            consoleAppender.setName("console");
            consoleAppender.start();

            logger.addAppender(consoleAppender);
            LOGGER = logger;
        }

        return LOGGER;
    }

}
