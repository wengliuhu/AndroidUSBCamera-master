package com.dybs.usbcamera.log;

import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;

/**
 * Copyright:   Copyright(C) 2010-2015 KEDACOM LTD.
 * Project:     FaceRecognitionAccessControl
 * Module:      com.keda.base.log
 * Description: DESC
 * Author:      zhoubing
 * Createdate:  2020-02-27
 * Version:     V
 */
public class LBLoggerManager {
    private static LBLoggerManager instance = new LBLoggerManager();
    private LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    public LBLoggerManager() {
    }

    public static LBLoggerManager getInstance() {
        return instance;
    }

    public Logger registerLogger(String name, Level level, Appender... appender) {
        Logger logger = this.loggerContext.getLogger(name);
        logger.setAdditive(false);
        logger.setLevel(level);
        Appender[] var5 = appender;
        int var6 = appender.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Appender appender1 = var5[var7];
            logger.addAppender(appender1);
        }

        return logger;
    }

    public void modifyLoggerLevel(String name, Level level) {
        Logger logger = this.loggerContext.getLogger(name);
        logger.setLevel(level);
    }

    public void unregisterLogger(String name) {
        Logger logger = this.loggerContext.getLogger(name);
        logger.detachAndStopAllAppenders();
    }

    public void unregisterAllLogger() {
        List<Logger> loggerList = this.loggerContext.getLoggerList();
        Iterator var2 = loggerList.iterator();

        while(var2.hasNext()) {
            Logger logger = (Logger)var2.next();
            logger.detachAndStopAllAppenders();
        }

    }
}
