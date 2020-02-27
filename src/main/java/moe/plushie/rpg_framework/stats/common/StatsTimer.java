package moe.plushie.rpg_framework.stats.common;

public class StatsTimer {

    private final int[] history;
    private final IStatsResetCallback callback;
    private long lastTick = System.currentTimeMillis();
    private int count;

    public StatsTimer(int historyAmount, IStatsResetCallback callback) {
        history = new int[historyAmount];
        this.callback = callback;
    }

    public void begin() {
        lastTick = System.currentTimeMillis();
    }

    public void end() {
        long curTime = System.currentTimeMillis();
        history[count] = (int) (curTime - lastTick);
        lastTick = curTime;
        count++;
        if (count > history.length - 1) {
            count = 0;
            if (callback != null) {
                callback.statsReset(this);
            }
        }
    }

    public int getSum() {
        int sum = 0;
        for (int i = 0; i < history.length; i++) {
            sum += history[i];
        }
        return sum;
    }

    public float getAverage() {
        int sum = getSum();
        return (float) sum / (float) history.length;
    }

    public interface IStatsResetCallback {

        public void statsReset(StatsTimer statsTimer);
    }
}
