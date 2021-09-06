package com.dybs.usbcamera.utils;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/8/24 11:04
 * Describe：
 */
public class GlUtil {
    static final String TAG = "GlUtil";

    /**
     * 链接到程序
     * @param mVertexshader
     * @param mFragmentshader
     * @return
     */
    public static int linkProgram(int mVertexshader, int mFragmentshader) {
        //创建程序对象
        int programId = GLES20.glCreateProgram();
        if (programId == 0) {
            Log.d(TAG, "创建program失败");
            return 0;
        }
        //依附着色器
        GLES20.glAttachShader(programId, mVertexshader);
        GLES20.glAttachShader(programId, mFragmentshader);
        //链接程序
        GLES20.glLinkProgram(programId);
        //检查链接状态
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        Log.d(TAG, "链接程序" + GLES20.glGetProgramInfoLog(programId));
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(programId);
            Log.d(TAG, "链接program失败");
            return 0;
        }

        return programId;

    }

    /**
     * 编译着色器脚本
     * @param type
     * @param source
     * @return
     */
    public static int compileShader(int type, String source) {
        //创建shader
        int shaderId = GLES20.glCreateShader(type);
        if (shaderId == 0) {
            Log.d(TAG, "创建shader失败");
            return 0;
        }
        //上传shader源码
        GLES20.glShaderSource(shaderId, source);
        //编译shader源代码
        GLES20.glCompileShader(shaderId);
        //取出编译结果
        int[] compileStatus = new int[1];
        //取出shaderId的编译状态并把他写入compileStatus的0索引
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        Log.d(TAG, GLES20.glGetShaderInfoLog(shaderId));

        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderId);
            Log.d(TAG, "创建shader失败");
            return 0;
        }

        return shaderId;
    }

    /**
     * 获取本地的着色器脚本
     * @param context
     * @param resouceId
     * @return
     */
    public static String readResoucetText(Context context, int resouceId) {
        StringBuffer body = new StringBuffer();

        try {
            InputStream inputStream = context.getResources().openRawResource(resouceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextline;
            while ((nextline = bufferedReader.readLine()) != null) {
                body.append(nextline);
                body.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return body.toString();
    }
}
