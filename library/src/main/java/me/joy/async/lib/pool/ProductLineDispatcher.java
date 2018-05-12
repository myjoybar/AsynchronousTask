package me.joy.async.lib.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by joybar on 2018/5/11.
 */

public class ProductLineDispatcher {

    public ProductLineDispatcher() {

    }

    public static ProductLineDispatcher getInstance() {
        return ProductLineDispatcherHolder.INSTANCE;
    }

    private static class ProductLineDispatcherHolder {
        private static ProductLineDispatcher INSTANCE = new ProductLineDispatcher();
    }


    public synchronized void execute(Runnable runnable) {
        ProductLineThreadPool.getExecutorService().execute(runnable);
    }
    public synchronized void submit(Runnable callable) {
         ProductLineThreadPool.getExecutorService().submit(callable);
    }

    public synchronized Future submit(Callable callable) {
        return ProductLineThreadPool.getExecutorService().submit(callable);
    }


}
