package cn.cloudself.weexshop.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.Map;

/**
 * 通用工具
 * <p>
 * 吐司
 * 获取app版本
 *
 * @author HerbLuo
 * @author unascribed
 * @version 1.0.0.d
 *          <p>
 *          change logs:
 *          2017/4/8 HerbLuo 首次创建
 */
@SuppressWarnings("unused")
public class CommonUtils {

    private final static String PREDERENCE_NAME_DEFAULT = "APP_DEFAULT";

    /**
     * 吐司
     */
    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    public static void toastOnNonUiThread(final Context context, final String str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, str, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }

    public static void toastOnNonUiThreadx(final Context context,
                                          final CharSequence text) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * 版本名
     */
    public static String getVersionName(@NonNull Context context) {
        PackageInfo info = getPackageInfo(context);
        return info == null ? null : info.versionName;
    }

    /**
     * 版本号
     */
    public static Integer getVersionCode(@NonNull Context context) {
        PackageInfo info = getPackageInfo(context);
        return info == null ? null : info.versionCode;
    }

    private static PackageInfo getPackageInfo(@NonNull Context context) {
        PackageManager manager = context.getPackageManager();

        try {
            return manager
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * SharedPreference get
     */
    public static String getPreference(Context context, String name, String defaultStr) {
        return getPreferences(context).getString(name, defaultStr);
    }

    /**
     * SharedPreference get
     */
    public static int getPreference(Context context, String name, int defaultNum) {
        return getPreferences(context).getInt(name, defaultNum);
    }

    /**
     * SharedPreference get
     */
    public static long getPreference(Context context, String name, long defaultNum) {
        return getPreferences(context).getLong(name, defaultNum);
    }

    /**
     * SharedPreference get
     */
    public static float getPreference(Context context, String name, float defaultNum) {
        return getPreferences(context).getFloat(name, defaultNum);
    }

    /**
     * SharedPreference get
     */
    public static boolean getPreference(Context context, String name, boolean defaultBool) {
        return getPreferences(context).getBoolean(name, defaultBool);
    }

    /**
     * SharedPreference
     */
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREDERENCE_NAME_DEFAULT, Context.MODE_PRIVATE);
    }

    /**
     * SharedPreference save or update
     */
    public static void savePreference(Context context, String name, String value) {
        getPreferences(context).edit()
                .putString(name, value)
                .commit();
    }

    /**
     * SharedPreference save or update
     */
    public static void savePreference(Context context, String name, int value) {
        getPreferences(context).edit()
                .putInt(name, value)
                .commit();
    }

    /**
     * SharedPreference save or update
     */
    public static void savePreference(Context context, String name, long value) {
        getPreferences(context).edit()
                .putLong(name, value)
                .commit();
    }

    /**
     * SharedPreference save or update
     */
    public static void savePreference(Context context, String name, float value) {
        getPreferences(context).edit()
                .putFloat(name, value)
                .commit();
    }

    /**
     * SharedPreference save or update
     */
    public static void savePreference(Context context, String name, boolean value) {
        getPreferences(context).edit()
                .putBoolean(name, value)
                .commit();
    }

    /**
     * SharedPreference save or update
     *
     * @param context
     * @param map         Map&lt;String, Object>
     *                    Object 不允许为null
     *                    Object 允许为 Integer, Float, Long, Boolean, String
     *                    自动将Double转换为String
     * @param allowObject 启用该参数，自动将object转换为string
     */
    public static void savePreference(Context context, Map<String, Object> map, boolean allowObject) {
        if (map == null) {
            throw new IllegalArgumentException("map 不能为null");
        }

        SharedPreferences.Editor editor = getPreferences(context).edit();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object obj = entry.getValue();
            if (obj == null) {
                throw new IllegalArgumentException("map 中不允许出现null值");
            }

            if (obj instanceof Integer) {
                editor.putInt(entry.getKey(), (Integer) obj);
            } else if (obj instanceof Float) {
                editor.putFloat(entry.getKey(), (Float) obj);
            } else if (obj instanceof Long) {
                editor.putLong(entry.getKey(), (Long) obj);
            } else if (obj instanceof Boolean) {
                editor.putBoolean(entry.getKey(), (Boolean) obj);
            } else if (obj instanceof String) {
                editor.putString(entry.getKey(), (String) obj);
            } else if (obj instanceof Double) {
                editor.putString(entry.getKey(), obj.toString());

            } else if (allowObject) {
                editor.putString(entry.getKey(), obj.toString());
            } else {
                throw new IllegalArgumentException("无法识别的map.value类型");
            }
        }

        editor.commit();
    }


}
