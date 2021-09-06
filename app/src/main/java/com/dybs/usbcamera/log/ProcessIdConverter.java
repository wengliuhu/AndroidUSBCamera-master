package com.dybs.usbcamera.log;


import android.os.Process;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;


public class ProcessIdConverter extends ClassicConverter {
    public ProcessIdConverter() {
    }

    public String convert(ILoggingEvent event) {
        return nullStrToDefault(event.getMDCPropertyMap().get("pid"), String.valueOf(Process.myPid()));
    }

    /**
     * null Object to empty string
     * <p>
     * <pre>
     * nullStrToEmpty(null) = &quot;&quot;;
     * nullStrToEmpty(&quot;&quot;) = &quot;&quot;;
     * nullStrToEmpty(&quot;aa&quot;) = &quot;aa&quot;;
     * </pre>
     *
     * @param str
     * @return
     */
    public static String nullStrToEmpty(Object str) {
        return (str == null ? "" : (str instanceof String ? (String) str : str.toString()));
    }

    /**
     * <use default str>
     *
     * @param str        the str
     * @param defaultStr the default str
     * @return the string
     * @author: guoxiangxun
     * @date: Feb 28, 2018 9:22:48 PM
     * @version: v1.0
     */
    public static String nullStrToDefault(Object str, Object defaultStr) {
        defaultStr = nullStrToEmpty(defaultStr);

        return (str != null ? (str instanceof String ? (String) str : str.toString())
                : (defaultStr instanceof String ? (String) defaultStr : defaultStr.toString()));
    }
}
