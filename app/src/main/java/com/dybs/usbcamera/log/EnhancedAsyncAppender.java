package com.dybs.usbcamera.log;

import android.os.Process;

import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class EnhancedAsyncAppender extends AsyncAppender {
    public EnhancedAsyncAppender() {
    }

    protected void preprocess(ILoggingEvent eventObject) {
        MDCAdapter mdc = MDC.getMDCAdapter();
        if (mdc.get("tid") == null) {
            mdc.put("tid", String.valueOf(Process.myTid()));
        }

        if (mdc.get("pid") == null) {
            mdc.put("pid", String.valueOf(Process.myPid()));
        }

        super.preprocess(eventObject);
    }
}
