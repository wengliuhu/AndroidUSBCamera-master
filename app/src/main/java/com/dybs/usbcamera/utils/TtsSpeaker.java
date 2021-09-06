package com.dybs.usbcamera.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;

/**
 *
 */
public class TtsSpeaker{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private TextToSpeech mTextToSpeech;
    private static TtsSpeaker mSpeaker = null;

    HashMap ttsOptions = new HashMap<>();

    public void init(Context context){

        logger.debug("init...");
        init(context,Locale.CHINESE);
    }

    private void init(Context context, final Locale locale){
        if(null == ttsOptions){
            ttsOptions = new HashMap<>();
            ttsOptions.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utterance");
        }

        if(locale!=null){

            release();

            mTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {

                        new Thread(){
                            @Override
                            public void run() {
                                super.run();

                                int result = mTextToSpeech.setLanguage(locale);

                                logger.debug("setLanguage result {}", result);

                                if (result == TextToSpeech.LANG_MISSING_DATA
                                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                    logger.error("setLanguage result is LANG_NOT_SUPPORTED or LANG_MISSING_DATA,result:{}", result);
                                    release();
                                } else {
                                    // 设置音调，值越大声音越尖（女生），值越小则变成男声,1. 0是常规
                                    mTextToSpeech.setPitch(1.0f);
                                    //设定语速 ，默认1.0正常语速
                                    mTextToSpeech.setSpeechRate(1f);

                                    mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                        @Override
                                        public void onStart(String utteranceId) {
                                            logger.debug("-------onStart----" + utteranceId);
                                        }

                                        @Override
                                        public void onDone(String utteranceId) {
                                            logger.debug("-------onDone----" + utteranceId);
                                        }

                                        @Override
                                        public void onError(String utteranceId) {
                                        }

                                        @Override
                                        public void onError(String utteranceId, int errorCode) {
                                            super.onError(utteranceId, errorCode);
                                            logger.error("-------onError---- utteranceId:{},errorCode:{}", utteranceId, errorCode);
                                        }
                                    });
                                }
                            }
                        }.start();
                    } else {
                        logger.error("onInit error status {}", status);
                        release();
                    }
                }
            });
        }
    }

    private TtsSpeaker() {
    }

    /**
     * @param message
     * @param
     */
    public void addMessage(String message) {
        if (mTextToSpeech == null) return;

//        logger.debug("addMessage begin speak");

        mTextToSpeech.speak(message, TextToSpeech.QUEUE_ADD, ttsOptions);

//        logger.debug("addMessage end speak");
    }

    /**
     * @param message
     * @param
     */
    public void addMessageFlush(String message) {
        logger.debug("addMessage begin speak:" + message);
        if (mTextToSpeech == null) return;
        mTextToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, ttsOptions);

        //        logger.debug("addMessage end speak");
    }

    public static TtsSpeaker getInstance() {
        if (mSpeaker == null) {
            synchronized (TtsSpeaker.class) {
                if (mSpeaker == null) {
                    mSpeaker = new TtsSpeaker();
                }
            }
        }

        return mSpeaker;
    }

    public void release() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
            mTextToSpeech=null;
        }
    }
}
