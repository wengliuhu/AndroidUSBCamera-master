package com.dybs.usbcamera.application;

import android.app.Application;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.dybs.usbcamera.log.LogInstance;
import com.dybs.usbcamera.utils.CrashHandler;
import com.dybs.usbcamera.utils.TtsSpeaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyApplication extends Application {
    public static MyApplication mInstance;
    protected Logger mLogger = LoggerFactory.getLogger(this.getClass());

    // File Directory in sd card
    public static final String DIRECTORY_NAME = "USBCamera";

    @Override
    public void onCreate() {
        super.onCreate();
        LogInstance.getInstance().init();
        addCrashCallback();
        mInstance = this;
        TtsSpeaker.getInstance().init(this);
    }

    public static MyApplication getAPP(){
        return mInstance;
    }

    private void addCrashCallback() {
        CrashHandler.getInstance().init(this, new CrashHandler.CrashCallback() {
            @Override
            public void callback(Throwable ex) {
                ex.printStackTrace();
                Log.e(Application.class.getSimpleName(), "crash occur", ex);
                mLogger.error("addAppCrashCallback {}", ex);

                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        try {
                            Toast.makeText(MyApplication.this, "很抱歉,出现不可预知的异常.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mLogger.error("addAppCrashCallback toast error {}", e);
                        }

                        Looper.loop();
                    }
                }.start();
                SystemClock.sleep(2000);

                appQuit();
            }
        });
    }

    private void appQuit() {
        Process.killProcess(Process.myPid());
    }
}
