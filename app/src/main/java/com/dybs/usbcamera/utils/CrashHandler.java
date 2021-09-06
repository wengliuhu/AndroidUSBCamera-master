package com.dybs.usbcamera.utils;

import android.content.Context;
import android.os.Build;


import com.dybs.usbcamera.BuildConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright:   Copyright(C) 2010-2015 KEDACOM LTD.
 * Project:     GreetingMachine
 * Module:      com.keda.greetingmachine.crash
 * Description: DESC
 * Author:      zhoubing
 * Createdate:  2019-09-12
 * Version:     V
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler{

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final String newline = System.getProperty("line.separator");

    private Thread.UncaughtExceptionHandler nDefaultHandler;
    private Context nContext;

    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private CrashCallback callback;

    private CrashHandler() {
    }

    private static class CrashHandlerInstance {

        private static CrashHandler handler = new CrashHandler();

    }


    public static CrashHandler getInstance() {
        return CrashHandlerInstance.handler;
    }


    public void init(Context context, CrashCallback callback) {
        nContext = context;
        // 获取系统默认的UncaughtException处理器
        nDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

        this.callback = callback;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        logger.info("crash nDefaultHandler {},ex {}", nDefaultHandler, ex);

        if (ex == null && this.nDefaultHandler != null) {
            this.nDefaultHandler.uncaughtException(thread, ex);
        } else {
            this.handleException(thread, ex);
        }
        logger.debug("handleException callback");
        if (this.callback != null) {
            this.callback.callback(ex);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     */
    private boolean handleException(Thread thread, Throwable ex) {
        if (ex == null)
            return false;
        logger.debug("handleException");
        try {
            saveCrashInfoFile(thread, ex);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("handleException ", e);
        }

        return true;
    }

    public String versionInfo(){
        StringBuffer detail = new StringBuffer();

        detail.append(newline);
        detail.append("versionName:  " + BuildConfig.VERSION_NAME);
        detail.append(newline);
        detail.append("versionCode:  " + BuildConfig.VERSION_CODE);
        detail.append(newline);

        return detail.toString();
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     * @throws Exception
     */
    private void saveCrashInfoFile(Thread thread, Throwable ex) throws Exception {
        logger.debug("saveCrashInfoFile ex {}", ex);

        StringBuffer sb = new StringBuffer();
        try {

            sb.append("-----------------------" + formatter.format(new Date()) + " ----------------------------");
            sb.append(this.newline);
            if (null != thread) {
                sb.append("Thread: ").append(thread.getName()).append(" - ").append(thread.getId());
                sb.append(this.newline);
            }

            String detail = deviceDetail();
            sb.append("DeviceInfo: "+detail.toString());

            String versionInfo = versionInfo();
            sb.append("AppVersion: " + versionInfo);

            sb.append("ErrorInfo: ");
            sb.append(this.newline);
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);

            for (Throwable cause = ex.getCause(); cause != null; cause = cause.getCause()) {
                cause.printStackTrace(printWriter);
            }

            printWriter.flush();
            printWriter.close();

            String result = writer.toString();
            sb.append(result);
            sb.append(this.newline);
            sb.append(this.newline);

            logger.error("{}{}", this.newline, sb.toString());

        } catch (Exception e) {
            logger.debug("an error occured while writing file...", e);
            sb.append("an error occured while writing file...\r\n");
        }
    }

    private String deviceDetail() {
        StringBuffer detail = new StringBuffer();

        detail.append(newline);

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                detail.append(field.getName() + ":  " + field.get(null).toString());
                detail.append(newline);
            } catch (Exception e) {
                logger.debug("an error occured when collect crash info", e);
            }
        }

        return detail.toString();
    }

    public interface CrashCallback {
        void callback(Throwable ex);
    }
}