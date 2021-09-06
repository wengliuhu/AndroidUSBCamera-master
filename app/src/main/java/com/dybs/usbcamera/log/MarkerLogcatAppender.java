package com.dybs.usbcamera.log;

import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class MarkerLogcatAppender extends LogcatAppender {
    public MarkerLogcatAppender() {
    }

    protected String getTag(ILoggingEvent event) {
        String tag = super.getTag(event);
        tag = "!!" + tag;
        return tag;
    }
}
