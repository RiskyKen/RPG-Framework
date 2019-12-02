package moe.plushie.rpg_framework.core.database;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import moe.plushie.rpg_framework.core.RPGFramework;
import net.minecraft.util.Util;

public class DatabaseScheduler {

    private final Queue<FutureTask<?>> scheduledTasks = Queues.<FutureTask<?>>newArrayDeque();

    public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule) {
        Validate.notNull(callableToSchedule);
        ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.<V>create(callableToSchedule);
        synchronized (this.scheduledTasks) {
            this.scheduledTasks.add(listenablefuturetask);
            return listenablefuturetask;
        }
    }

    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
        Validate.notNull(runnableToSchedule);
        return this.<Object>addScheduledTask(Executors.callable(runnableToSchedule));
    }

    private void tick() {
        synchronized (this.scheduledTasks) {
            while (!this.scheduledTasks.isEmpty()) {
                Util.runTask(this.scheduledTasks.poll(), RPGFramework.getLogger());
            }
        }
    }
}
