package me.joy.async.lib.factory;


import java.util.HashMap;
import java.util.Map;

import me.joy.async.lib.task.AsynchronousTask;

/**
 * Created by joybar on 2018/5/11.
 */

public class AsyncFactory {
    public static String TAG = "AsyncFactory";
    public static final int MAX_ASYNC_REQUESTS = 64;
    public final static Map<AsynchronousTask,AsynchronousTask> RUNNING_ASYNC_REQUESTS = new HashMap<>();
    public final static  Map<AsynchronousTask,AsynchronousTask> READY_ASYNC_REQUESTS = new HashMap<>();

    public AsyncFactory() {

    }

    public static AsyncFactory getInstance() {
        return FactoryManagerHolder.INSTANCE;
    }


    private static class FactoryManagerHolder {
        private static AsyncFactory INSTANCE = new AsyncFactory();
    }


    public void produce(AsynchronousTask asynchronousTask) {
        asynchronousTask.produceWithCallable();
    }


}
