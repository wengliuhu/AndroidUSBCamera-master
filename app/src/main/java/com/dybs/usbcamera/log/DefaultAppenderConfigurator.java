package com.dybs.usbcamera.log;



import org.slf4j.LoggerFactory;

import java.io.File;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.StatusPrinter;


public class DefaultAppenderConfigurator implements IAppenderConfigure<RollingFileAppender<ILoggingEvent>> {
    private String name;
    private String dir;
    private Level level;
    private boolean useAsync;

    public DefaultAppenderConfigurator(String dir, String name) {
        this(dir, name, (Level) null);
    }

    public DefaultAppenderConfigurator(String dir, String name, Level level) {
        this(dir, name, level, false);
    }

    public DefaultAppenderConfigurator(String dir, String name, Level level, boolean useAsync) {
        this(dir, name, level, useAsync, (String) null);
    }

    public DefaultAppenderConfigurator(String dir, String name, Level level, boolean useAsync, String pid) {
        this.name = null;
        this.dir = dir;
        this.name = name;
        this.level = level;
        this.useAsync = useAsync;
    }

    public Appender configure(RollingFileAppender<ILoggingEvent> component) {
        Context context = (Context) LoggerFactory.getILoggerFactory();
        component.setAppend(true);
        component.setContext(context);
//        component.setFile((new File(this.dir, this.name+"."+new SimpleDateFormat("yyyyMMdd").format(KedaConfig.getInstance().getSystemTimeMillis()) + ".log")).getPath());
        component.setFile((new File(this.dir, this.name + ".log")).getPath());

        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy();

//        rollingPolicy.setFileNamePattern((new File(this.dir, this.name + ".%d{yyyy-MM-dd}.%i" + ".log")).getPath());
        rollingPolicy.setFileNamePattern((new File(this.dir, this.name + "-%d{yyyy-MM-dd}.%i" + ".log.zip")).getPath());

        rollingPolicy.setMaxHistory(7);
        rollingPolicy.setTotalSizeCap(FileSize.valueOf("10MB"));
        rollingPolicy.setParent(component);
        rollingPolicy.setContext(context);

        SizeAndTimeBasedFNATP<ILoggingEvent> sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP();
        sizeAndTimeBasedFNATP.setMaxFileSize(FileSize.valueOf("10MB"));
        sizeAndTimeBasedFNATP.setContext(context);
        sizeAndTimeBasedFNATP.setTimeBasedRollingPolicy(rollingPolicy);

        rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(sizeAndTimeBasedFNATP);
        rollingPolicy.start();
        sizeAndTimeBasedFNATP.start();
        component.setRollingPolicy(rollingPolicy);

        if (this.level != null) {
            LevelFilter levelFilter = new LevelFilter();
            levelFilter.setLevel(this.level);
            levelFilter.setOnMatch(FilterReply.ACCEPT);
            levelFilter.setOnMismatch(FilterReply.DENY);
            levelFilter.start();
            component.addFilter(levelFilter);
        }

        PatternLayoutEncoder encoder = new LogBackExEncoder();
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread]/[%pid:%tid] %-5level %logger{36} - %.-20480msg%n");
        encoder.setContext(context);
        encoder.start();
        component.setEncoder(encoder);
        component.start();
        if (this.useAsync) {
            AsyncAppender asyncAppender = new EnhancedAsyncAppender();
            asyncAppender.setContext(context);
            asyncAppender.setName("ASYNC");
            asyncAppender.setIncludeCallerData(false);
            asyncAppender.setDiscardingThreshold(0);
            asyncAppender.setQueueSize(256);
            asyncAppender.addAppender(component);
            asyncAppender.start();
            StatusPrinter.print(context);
            return asyncAppender;
        } else {
            StatusPrinter.print(context);
            return component;
        }
    }

    public String getName() {
        return this.getName();
    }
}
