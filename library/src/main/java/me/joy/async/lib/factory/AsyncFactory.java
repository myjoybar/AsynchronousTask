package me.joy.async.lib.factory;


import me.joy.async.lib.task.AsynchronousTask;

/**
 * Created by joybar on 2018/5/11.
 */

public class AsyncFactory {
    public static String TAG = "AsyncFactory";

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
