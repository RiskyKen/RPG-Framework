package moe.plushie.rpg_framework.stats.common;

public class StatsHistory {

    private final int shortAmount;
    private final int[] history;
    private final IStatsResetCallback callback;
    private long lastTick = System.currentTimeMillis();
    private int count;

    public StatsHistory(int shortAmount, int longAmountMultiplier, IStatsResetCallback callback) {
        this.shortAmount = shortAmount;
        history = new int[shortAmount * longAmountMultiplier];
        this.callback = callback;
    }

    public StatsHistory(int shortAmount, int longAmountMultiplier) {
        this(shortAmount, longAmountMultiplier, null);
    }

    public void begin() {
        lastTick = System.currentTimeMillis();
    }

    public void end() {
        long curTime = System.currentTimeMillis();
        history[count] = (int) (curTime - lastTick);
        lastTick = curTime;
        if (count % shortAmount == 1) {
            if (callback != null) {
                callback.statsReset(this);
            }
        }
        count++;
        if (count > history.length - 1) {
            count = 0;
        }
    }

    public void add(int[] values) {
        for (int i = 0; i < values.length; i++) {
            add(values[i]);
        }
    }

    public void add(int value) {
        history[count] = value;
        count++;
        if (count > history.length - 1) {
            count = 0;
        }
    }

    private int getSum(int[] value) {
        int sum = 0;
        for (int i = 0; i < value.length; i++) {
            sum += value[i];
        }
        return sum;
    }

    private float getAverage(int[] value) {
        int sum = getSum(value);
        return (float) sum / (float) value.length;
    }

    public float getAverageFull() {
        int sum = getSum(history);
        return (float) sum / (float) history.length;
    }

    public float getAverageShort() {
        int[] shortHistory = getShortHistory();
        int sum = getSum(shortHistory);
        return (float) sum / (float) shortHistory.length;
    }

    public int[] getShortHistory() {
        int[] sorted = new int[shortAmount];
        int[] full = getFullHistory();
        for (int i = 0; i < shortAmount; i++) {
            sorted[i] = full[full.length - shortAmount + i];
        }
        return sorted;
    }

    public int[] getFullHistory() {
        int[] historyCopy = history.clone();
        int[] sorted = new int[historyCopy.length];
        
        int index = 0;
        for (int i = count; i < historyCopy.length; i++) {
            sorted[index] = historyCopy[i];
            index++;
        }
        for (int i = 0; i < count; i++) {
            sorted[index] = historyCopy[i];
            index++;
        }
        return sorted;
    }

    public int[] getHistory() {
        return history.clone();
    }

    public void setHistory(int[] history) {
        for (int i = 0; i < history.length; i++) {
            this.history[i] = history[i];
        }
    }

    public interface IStatsResetCallback {

        public void statsReset(StatsHistory statsTimer);
    }
}
