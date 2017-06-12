package cn.cloudself.weexshop.util.app;

/**
 * @author HerbLuo
 * @version 1.0.0.d
 *          <p>
 *          change logs:
 *          2017/6/9 HerbLuo 首次创建
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AppError {

    /**
     * 未知错误
     */
    public static final String UnknownError = "error0";

    /**
     * 网络异常（总）
     */
    public static final String NetworkError = "error4000";

    public static boolean isErrorCode(String str) {
        return str != null && str.startsWith("error");
    }

    public static void defaultErrorHandler(String error) {

    }

}
