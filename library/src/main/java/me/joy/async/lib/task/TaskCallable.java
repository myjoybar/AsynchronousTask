package me.joy.async.lib.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import me.joy.async.lib.util.Util;


/**
 * Created by joybar on 2018/5/10.
 */

public abstract class TaskCallable<TProgress, TResult> implements Callable {


    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;
    protected String name;
    private static InternalHandler sHandler;

    private final AtomicBoolean mCancelled = new AtomicBoolean();

    public TaskCallable(String format, Object... args) {
        this.name = Util.format(format, args);

    }

    public TaskCallable() {
    }

    public final boolean isCancelled() {
        return mCancelled.get();
    }

    public final void cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
    }


    @Override
    public TResult call() {
        String oldName = Thread.currentThread().getName();
        TResult result = null;
        if (!TextUtils.isEmpty(name)) {
            Thread.currentThread().setName(name);
        }
        try {
            result = onDoInBackground();
        } catch (Throwable tr){
            mCancelled.set(true);
            throw tr;
        }  finally {
            Thread.currentThread().setName(oldName);
            postResult(result);
        }
        return result;

    }


    protected abstract TResult onDoInBackground();

    protected void onProduceProgressUpdate(TProgress... values) {

    }

    protected void onPostProduce(TResult result) {

    }

    protected final void publishProgress(TProgress... values) {
        if (!isCancelled()) {
            getMainHandler().obtainMessage(MESSAGE_POST_PROGRESS,
                    new TaskCallableResult<TProgress>(this, values)).sendToTarget();
        }
    }

    private void postResult(TResult result) {
        @SuppressWarnings("unchecked") Message message = getMainHandler().obtainMessage
                (MESSAGE_POST_RESULT, new TaskCallableResult<TResult>(this, result));
        message.sendToTarget();
    }

    private static class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }

        @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
        @Override
        public void handleMessage(Message msg) {
            TaskCallableResult result = (TaskCallableResult) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProduceProgressUpdate(result.mData);
                    break;
            }
        }
    }

    private void finish(TResult result) {
        onPostProduce(result);
    }


    private static Handler getMainHandler() {
        synchronized (TaskCallable.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler(Looper.getMainLooper());
            }
            return sHandler;
        }
    }

    @SuppressWarnings({"RawUseOfParameterizedType"})
    private static class TaskCallableResult<Data> {
        final TaskCallable mTask;
        final Data[] mData;

        TaskCallableResult(TaskCallable task, Data... data) {
            mTask = task;
            mData = data;
        }
    }

}
