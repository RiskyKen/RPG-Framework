package moe.plushie.rpg_framework.core.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;

import moe.plushie.rpg_framework.core.database.driver.IDatabaseDriver;
import moe.plushie.rpg_framework.core.database.driver.SQLiteDriver;

public final class DatabaseManager {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);
    private static final IDatabaseDriver DATABASE_DRIVER = new SQLiteDriver();

    private DatabaseManager() {
    }

    public static <V> ListenableFutureTask<V> createTaskAndExecute(Callable<V> callable) {
        return createTaskAndExecute(callable, null);
    }

    public static <V> ListenableFutureTask<V> createTaskAndExecute(Callable<V> callable, @Nullable FutureCallback<V> callback) {
        ListenableFutureTask futureTask = ListenableFutureTask.<V>create(callable);
        if (callback != null) {
            Futures.addCallback(futureTask, callback);
        }
        DatabaseManager.EXECUTOR.execute(futureTask);
        return futureTask;
    }

    public static void createTaskAndExecute(Runnable runnable) {
        createTaskAndExecute(runnable, null);
    }

    public static void createTaskAndExecute(Runnable runnable, @Nullable FutureCallback<Void> callback) {
        ListenableFutureTask futureTask = ListenableFutureTask.<Void>create(runnable, null);
        if (callback != null) {
            Futures.addCallback(futureTask, callback);
        }
        DatabaseManager.EXECUTOR.execute(futureTask);
    }
    
    public static int getQueueSize() {
        return ((ThreadPoolExecutor)EXECUTOR).getQueue().size();
    }

    public static Connection getConnection(DatebaseTable table) throws SQLException {
        return DATABASE_DRIVER.getConnection(table);
    }
    
    public static int getLastInsertRow(Connection conn) throws SQLException {
        return DATABASE_DRIVER.getLastInsertRow(conn);
    }
}
