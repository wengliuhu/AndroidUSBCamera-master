package com.dybs.usbcamera.log;

import android.text.TextUtils;
import android.util.Log;

import com.dybs.usbcamera.BuildConfig;
import com.dybs.usbcamera.utils.FileUtils;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;

/**
 * Copyright:   Copyright(C) 2010-2015 KEDACOM LTD.
 * Project:     FaceRecognitionAccessControl
 * Module:      com.keda.base.log
 * Description: DESC
 * Author:      zhoubing
 * Createdate:  2020-02-27
 * Version:     V
 */
public class LogInstance {

//    public static final String DEFAULT_WORK_DIR = "/sdcard/Feelfull/TestDevices/";
    public static final String DEFAULT_WORK_DIR = FileUtils.getRootPath();

    public static final String DEFAULT_LOGS_RELATIVE_DIR = "logs/";

    private final static String LOGLEVEL_CONFIG = "log_level.ini";

    public static final String DEFAULT_CRASH_LOGGER = "CrashHandler";

    private String workDir = DEFAULT_WORK_DIR;

    private boolean LOGCAT_ENABLE = true;

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    private static LogInstance instance = new LogInstance();

    private LogInstance() {
    }

    public static LogInstance getInstance() {
        return instance;
    }

    public void init() {
        Log.d("LogInstance", BuildConfig.DEBUG+"");
        initDefaultLogger();

        Level logLevel = getLogLevel(readLogConfig());

        LBLoggerManager.getInstance().modifyLoggerLevel(Logger.ROOT_LOGGER_NAME, logLevel);
    }

    public Level getLogLevel(String levelStr) {

        Level level = Level.INFO;

        if (TextUtils.isEmpty(levelStr)) {
            level = getDefaultLevel();
        } else {
            if (levelStr.equalsIgnoreCase("error")) {
                level = Level.ERROR;
            } else if (levelStr.equalsIgnoreCase("info")) {
                level = Level.INFO;
            } else if (levelStr.equalsIgnoreCase("warn")) {
                level = Level.WARN;
            } else if (levelStr.equalsIgnoreCase("debug")) {
                level = Level.DEBUG;
            } else if (levelStr.equalsIgnoreCase("trace")) {
                level = Level.TRACE;
            } else {
                level = getDefaultLevel();
            }
        }

        return level;
    }

    private Level getDefaultLevel() {
        Level level;
        if (BuildConfig.DEBUG) {
            level = Level.DEBUG;
        } else {
            // release版本视为info级别
            //todo:DEBUG for bug test
            level = Level.DEBUG;
        }
        return level;
    }

    private String readLogConfig() {
        if (!TextUtils.isEmpty(workDir)) {

            String dir = workDir + "/" + DEFAULT_LOGS_RELATIVE_DIR;
            if(isFolderExist(dir)){
                String path = dir + LOGLEVEL_CONFIG;
                if (isFileExist(path)) {
                    StringBuilder stringBuilder = readFile2String(path, "UTF-8");

                    return stringBuilder.toString();
                }
            }
            else{
                makeDirs(dir);
            }
        }

        return null;
    }

    /**
     * 读取文件为String
     * @param filePath 文件路径
     * @param charsetName 编码格式，如"UTF-8"
     * @return
     */
    public static StringBuilder readFile2String(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        } finally {
            close(reader);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.d("FileUtil",e.toString());
            }
        }
    }


    /**
     * 判断文件是否存在
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * 判断文件夹是否存在
     * @param directoryPath
     * @return
     */
    public static boolean isFolderExist(String directoryPath) {
        if (TextUtils.isEmpty(directoryPath)) {
            return false;
        }
        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * 创建文件夹
     * @param filePath
     * @return
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    /**
     * 获取文件夹路径
     * @param filePath
     * @return
     */
    public static String getFolderName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int fp = filePath.lastIndexOf(File.separator);
        return (fp == -1) ? "" : filePath.substring(0, fp);
    }


    private void initDefaultLogger() {
        String pid = android.os.Process.myPid() + "";
        String logsDir = workDir + File.separator + DEFAULT_LOGS_RELATIVE_DIR;
        Level level = getDefaultLevel();

        Appender fileAppender = new DefaultAppenderConfigurator(logsDir, "all",null, true, pid).configure(new RollingFileAppender<ILoggingEvent>());
//        Appender fileCrashAppender = new DefaultAppenderConfigurator(logsDir, "crash", Level.ERROR, true, pid).configure(new RollingFileAppender<ILoggingEvent>());
        Appender fileCrashAppender = new DefaultAppenderConfigurator(logsDir, "crash",Level.ERROR, true, pid).configure(new RollingFileAppender<ILoggingEvent>());

        if(LOGCAT_ENABLE ){
            Appender logcatAppender = new DefaultLogcatAppenderConfigurator("logcat", true).configure(new MarkerLogcatAppender());

            LBLoggerManager.getInstance().registerLogger(Logger.ROOT_LOGGER_NAME, level, logcatAppender);

            LBLoggerManager.getInstance().registerLogger(Logger.ROOT_LOGGER_NAME, level, logcatAppender, fileAppender);
            LBLoggerManager.getInstance().registerLogger(DEFAULT_CRASH_LOGGER, Level.ERROR, logcatAppender, fileCrashAppender, fileAppender);

        }else{
            LBLoggerManager.getInstance().registerLogger(Logger.ROOT_LOGGER_NAME, level, fileAppender);
            LBLoggerManager.getInstance().registerLogger(DEFAULT_CRASH_LOGGER, Level.ERROR, fileCrashAppender, fileAppender);
        }
    }

    public void release(){
        LBLoggerManager.getInstance().unregisterAllLogger();
    }
}
