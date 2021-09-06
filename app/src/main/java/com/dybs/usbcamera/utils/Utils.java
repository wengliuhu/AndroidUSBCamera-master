package com.dybs.usbcamera.utils;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.dybs.usbcamera.application.MyApplication;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/8/30 9:58
 * Describe：
 */
public class Utils {

    public static boolean isEmpty(List list)
    {
        return list == null || list.size() == 0;
    }

    public static boolean isEmpty(byte[] bytes)
    {
        return bytes == null || bytes.length == 0;
    }

    public static boolean isEmpty(Object[] objects)
    {
        return objects == null || objects.length == 0;
    }


    public static boolean isEmpty(Map objects)
    {
        return objects == null || objects.size() == 0;
    }

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    public static void closeIO(Closeable... closeables)
    {
        if (closeables == null)
        {
            return;
        }
        try
        {
            for (Closeable closeable : closeables)
            {
                if (closeable != null)
                {
                    closeable.close();
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static Application getApp(){
        return MyApplication.getAPP();
    }

    /**
     * px转dp
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale =  context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转为px
     * @param context
     * @param dipValue dp数值
     * @return
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转sp
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * sp转px
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /*
     * 获取设备的屏幕高度（px）
     * */
    public  static int getScreenHeightPx(Context context){
        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display=windowManager.getDefaultDisplay();
        DisplayMetrics metrics=new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.heightPixels;
    }
    /*
     * 获取设备的屏幕高度（dp）
     * */
    public  static int getScreenHeightDp(Context context){
        return px2dp(context,getScreenHeightPx(context));
    }

    /*
     * 获取设备的屏幕宽度（px）
     * */
    public static int getScreenWidthPx(Context context){
        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display=windowManager.getDefaultDisplay();
        DisplayMetrics metrics=new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.widthPixels;
    }
    /*
     * 获取设备的屏幕宽度（dp）
     * */
    public  static int getScreenWidthDp(Context context){
        return px2dp(context,getScreenWidthPx(context));
    }

}
