package com.dybs.usbcamera.log;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

public class LogBackExEncoder extends PatternLayoutEncoder {
    public LogBackExEncoder() {
    }

    static {
        PatternLayout.defaultConverterMap.put("pid", ProcessIdConverter.class.getName());
        PatternLayout.defaultConverterMap.put("tid", ThreadIdConverter.class.getName());
    }
}