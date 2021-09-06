package com.dybs.usbcamera.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author : yuanbingbing
 * @since : 2018/8/3 15:27
 */
public class SPUtil {

    private static final SimpleArrayMap<String, SPUtil> SP_UTILS_MAP = new SimpleArrayMap<>();
    private SharedPreferences sp;


    public static SPUtil getInstance() {
        return getInstance("", Context.MODE_PRIVATE);
    }


    public static SPUtil getInstance(final int mode) {
        return getInstance("", mode);
    }

    public static SPUtil getInstance(String spName) {
        return getInstance(spName, Context.MODE_PRIVATE);
    }


    public static SPUtil getInstance(String spName, final int mode) {
        if (isSpace(spName)) spName = "SPUtil";
        SPUtil SPUtil = SP_UTILS_MAP.get(spName);
        if (SPUtil == null) {
            SPUtil = new SPUtil(spName, mode);
            SP_UTILS_MAP.put(spName, SPUtil);
        }
        return SPUtil;
    }

    private SPUtil(final String spName) {
        sp = Utils.getApp().getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    private SPUtil(final String spName, final int mode) {
        sp = Utils.getApp().getSharedPreferences(spName, mode);
    }


    public void put(final String key, final String value) {
        put(key, value, false);
    }

    /**
     * @param key      键
     * @param value    值
     * @param isCommit True  {@link SharedPreferences.Editor#commit()},
     *                 false  {@link SharedPreferences.Editor#apply()}
     */
    public void put(final String key, final String value, final boolean isCommit) {
        if (isCommit) {
            sp.edit().putString(key, value).commit();
        } else {
            sp.edit().putString(key, value).apply();
        }
    }

    /**
     * 获取指定值
     *
     * @param key 键.
     * @return 默认为 “”
     */
    public String getString(@NonNull final String key) {
        return getString(key, "");
    }


    public String getString(@NonNull final String key, final String defaultValue) {
        return sp.getString(key, defaultValue);
    }


    public void put(@NonNull final String key, final int value) {
        put(key, value, false);
    }


    public void put(@NonNull final String key, final int value, final boolean isCommit) {
        if (isCommit) {
            sp.edit().putInt(key, value).commit();
        } else {
            sp.edit().putInt(key, value).apply();
        }
    }


    public int getInt(@NonNull final String key) {
        return getInt(key, -1);
    }


    public int getInt(@NonNull final String key, final int defaultValue) {
        return sp.getInt(key, defaultValue);
    }


    public void put(@NonNull final String key, final long value) {
        put(key, value, false);
    }


    public void put(@NonNull final String key, final long value, final boolean isCommit) {
        if (isCommit) {
            sp.edit().putLong(key, value).commit();
        } else {
            sp.edit().putLong(key, value).apply();
        }
    }


    public long getLong(@NonNull final String key) {
        return getLong(key, -1L);
    }


    public long getLong(@NonNull final String key, final long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public void put(@NonNull final String key, final float value) {
        put(key, value, false);
    }


    public void put(@NonNull final String key, final float value, final boolean isCommit) {
        if (isCommit) {
            sp.edit().putFloat(key, value).commit();
        } else {
            sp.edit().putFloat(key, value).apply();
        }
    }


    public float getFloat(@NonNull final String key) {
        return getFloat(key, -1f);
    }


    public float getFloat(@NonNull final String key, final float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }


    public void put(@NonNull final String key, final boolean value) {
        put(key, value, false);
    }


    public void put(@NonNull final String key, final boolean value, final boolean isCommit) {
        if (isCommit) {
            sp.edit().putBoolean(key, value).commit();
        } else {
            sp.edit().putBoolean(key, value).apply();
        }
    }


    public boolean getBoolean(@NonNull final String key) {
        return getBoolean(key, false);
    }


    public boolean getBoolean(@NonNull final String key, final boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void put(@NonNull final String key, final Set<String> value) {
        put(key, value, false);
    }


    public void put(@NonNull final String key,
                    final Set<String> value,
                    final boolean isCommit) {
        if (isCommit) {
            sp.edit().putStringSet(key, value).commit();
        } else {
            sp.edit().putStringSet(key, value).apply();
        }
    }


    public Set<String> getStringSet(@NonNull final String key) {
        return getStringSet(key, Collections.<String>emptySet());
    }


    public Set<String> getStringSet(@NonNull final String key,
                                    final Set<String> defaultValue) {
        return sp.getStringSet(key, defaultValue);
    }

    public Map<String, ?> getAll() {
        return sp.getAll();
    }


    public boolean contains(@NonNull final String key) {
        return sp.contains(key);
    }


    public void remove(@NonNull final String key) {
        remove(key, false);
    }


    public void remove(@NonNull final String key, final boolean isCommit) {
        if (isCommit) {
            sp.edit().remove(key).commit();
        } else {
            sp.edit().remove(key).apply();
        }
    }

    public void clear() {
        clear(false);
    }


    public void clear(final boolean isCommit) {
        if (isCommit) {
            sp.edit().clear().commit();
        } else {
            sp.edit().clear().apply();
        }
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    /**
     * 存 对象
     *
     * @param key
     * @param object
     */
    public void putObject(String key, Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            sp.edit().putString(key, objectVal).commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 取 对象
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getObject(String key, Class<T> clazz) {
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
