package me.joy.async.lib.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by joybar on 2018/5/11.
 */

public class ThreadPoolSelector {

    private ExecutorService executorService;

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public ThreadPoolSelector() {

    }

    public static ThreadPoolSelector getInstance() {
        return ProductLineDispatcherHolder.INSTANCE;
    }

    private static class ProductLineDispatcherHolder {
        private static ThreadPoolSelector INSTANCE = new ThreadPoolSelector();
    }


    public synchronized void execute(Runnable runnable) {
        getExecutorService().execute(runnable);
    }

    public synchronized void submit(Runnable callable) {
        getExecutorService().submit(callable);
    }

    public synchronized Future submit(Callable callable) {
        return getExecutorService().submit(callable);
    }


    ExecutorService getExecutorService() {
        if (null == executorService) {
            return ThreadPoolNoCore.executorService();
        }
        return executorService;
    }


}
