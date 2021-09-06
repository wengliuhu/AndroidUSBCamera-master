package com.dybs.usbcamera.log;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.StatusPrinter;

public class DefaultLogcatAppenderConfigurator implements IAppenderConfigure<MarkerLogcatAppender> {
    private String name;
    private boolean useAsync;

    public DefaultLogcatAppenderConfigurator(String name) {
        this(name, false);
    }

    public DefaultLogcatAppenderConfigurator(String name, boolean useAsync) {
        this.name = "logcat";
        this.name = name;
        this.useAsync = useAsync;
    }

    public Appender configure(MarkerLogcatAppender component) {
        Context context = (Context) LoggerFactory.getILoggerFactory();
        StatusManager sm = context.getStatusManager();
        if (sm != null) {
            sm.add(new InfoStatus("Setting up default configuration.", context));
        }

        component.setContext(context);
        component.setName(this.name);
        PatternLayoutEncoder pl = new PatternLayoutEncoder();
        pl.setContext(context);
        pl.setPattern("[%thread]-%.-20480msg");
        pl.start();
        component.setEncoder(pl);
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
        return this.name;
    }
}