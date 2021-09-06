package com.dybs.usbcamera.bean;

import android.util.Log;

import com.dybs.usbcamera.utils.SPUtil;
import com.serenegiant.usb.UVCCamera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/8/30 10:01
 * Describe：
 */
public class ConfigBean implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(ConfigBean.class);
    public static final long serialVersionUID = 1l;
    // 亮度
    private int brightness = 50;
    // 对比度
    private int contrast = 50;
    // 缩放尺寸
    private int scale = 5;
    // 摄像头相关参数
    // 分辨率宽
    private int width = UVCCamera.DEFAULT_PREVIEW_WIDTH;
    private int height = UVCCamera.DEFAULT_PREVIEW_HEIGHT;
    private int minFrame = 1;
    private int maxFrame = 31;
    private int defultFrame = 30;
    private int frameInterval = 333333;
    private int format = UVCCamera.FRAME_FORMAT_MJPEG;
    // 是否灰度显示
    private boolean grayShow;

    private  ConfigBean(){
    }

    public static ConfigBean getInstance(){
        Object obj = SPUtil.getInstance().getObject("config", ConfigBean.class);
//        logger.debug("ConfigBean ;{}", obj);
        if (!(obj instanceof ConfigBean)){
            ConfigBean configBean = new ConfigBean();
            configBean.save();
            return configBean;
        }else{
            return (ConfigBean) obj;
        }
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
        save();
    }

    public int getContrast() {
        return contrast;
    }

    public void setContrast(int contrast) {
        this.contrast = contrast;
        save();
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
        save();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
//        save();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
//        save();
    }

    public int getMinFrame() {
        return minFrame;
    }

    public void setMinFrame(int minFrame) {
        this.minFrame = minFrame;
//        save();
    }

    public int getMaxFrame() {
        return maxFrame;
    }

    public void setMaxFrame(int maxFrame) {
        this.maxFrame = maxFrame;
//        save();
    }

    public int getDefultFrame() {
        return defultFrame;
    }

    public void setDefultFrame(int defultFrame) {
        this.defultFrame = defultFrame;
//        save();
    }

    public int getFrameInterval() {
        return frameInterval;
    }

    public void setFrameInterval(int frameInterval) {
        this.frameInterval = frameInterval;
//        save();
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
        save();
    }

    public boolean isGrayShow() {
        return grayShow;
    }

    public void setGrayShow(boolean grayShow) {
        this.grayShow = grayShow;
        save();
    }

    public void save(){
        SPUtil.getInstance().putObject("config", this);
        Log.d("kim", toString());
    }

    @Override
    public String toString() {
        return "ConfigBean{" +
                "brightness=" + brightness +
                ", contrast=" + contrast +
                ", scale=" + scale +
                ", width=" + width +
                ", height=" + height +
                ", minFrame=" + minFrame +
                ", maxFrame=" + maxFrame +
                ", defultFrame=" + defultFrame +
                ", frameInterval=" + frameInterval +
                ", format=" + format +
                ", grayShow=" + grayShow +
                '}';
    }
}
