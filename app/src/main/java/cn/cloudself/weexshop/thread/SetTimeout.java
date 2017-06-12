package cn.cloudself.weexshop.thread;

/**
 * @author HerbLuo
 * @version 1.0.0.d
 *          <p>
 *          change logs:
 *          2017/6/9 HerbLuo 首次创建
 */
public class SetTimeout implements Runnable {

    private OnTimeout onTimeout;
    private long timeMill;

    public SetTimeout(OnTimeout onTimeout, long timeMill) {
        this.onTimeout = onTimeout;
        this.timeMill = timeMill;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(timeMill);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onTimeout.onTimeout();
    }

    public interface OnTimeout {
        void onTimeout();
    }

}
