package me.joy.async.lib.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by joybar on 2018/5/10.
 */

public class TaskRunnable<TProgress, TResult> implements Runnable {


    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;
    private String name;
    private static InternalHandler sHandler;
    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private final AsynchronousTask asynchronousTask;


    public TaskRunnable(AsynchronousTask asynchronousTask) {
        this.asynchronousTask = asynchronousTask;
    }


    public final boolean isCancelled() {
        return mCancelled.get();
    }

    public final void cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
    }

    @Override
    public void run() {
        String oldName = Thread.currentThread().getName();
        TResult result = null;
        if (!TextUtils.isEmpty(name)) {
            Thread.currentThread().setName(name);
        }
        try {
            result = (TResult) asynchronousTask.doInBackground();
        } catch (Throwable tr) {
            mCancelled.set(true);
            throw tr;
        } finally {
            Thread.currentThread().setName(oldName);
            mCancelled.set(true);
            postResult(result);
        }

    }


    protected final void publishProgress(TProgress... values) {
        if (!isCancelled()) {
            getMainHandler().obtainMessage(MESSAGE_POST_PROGRESS, new
                    TaskCallableResult<TProgress>(asynchronousTask, this, values)).sendToTarget();
        }
    }

    private void postResult(TResult result) {
        @SuppressWarnings("unchecked") Message message = getMainHandler().obtainMessage
                (MESSAGE_POST_RESULT, new TaskCallableResult<TResult>(asynchronousTask, this,
                        result));
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
                    result.mTask.asynchronousTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

    private void finish(TResult result) {
        if (!isCancelled()) {
            asynchronousTask.onPostExecute(result);
        }
    }


    private static Handler getMainHandler() {
        synchronized (AsynchronousTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler(Looper.getMainLooper());
            }
            return sHandler;
        }
    }

    @SuppressWarnings({"RawUseOfParameterizedType"})
    private static class TaskCallableResult<Data> {
        final TaskRunnable mTask;
        final Data[] mData;
        final AsynchronousTask mAsynchronousTask;

        TaskCallableResult(AsynchronousTask asynchronousTask, TaskRunnable task, Data... data) {
            mAsynchronousTask = asynchronousTask;
            mTask = task;
            mData = data;
        }
    }

}
