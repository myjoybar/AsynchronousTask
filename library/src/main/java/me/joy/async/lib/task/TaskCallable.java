package me.joy.async.lib.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by joybar on 2018/5/10.
 */

public class TaskCallable<TProgress, TResult> implements Callable {


    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;
    private static InternalHandler sHandler;
    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private final AsynchronousTask asynchronousTask;


    public TaskCallable(AsynchronousTask asynchronousTask) {
        this.asynchronousTask = asynchronousTask;
    }

    public final boolean isCancelled() {
        return mCancelled.get();
    }

    public final void cancel() {
        mCancelled.set(true);
    }


    @Override
    public TResult call() {
        TResult result = null;
        try {
            result = (TResult) asynchronousTask.doInBackground();
        } catch (Throwable tr) {
            mCancelled.set(true);
            throw tr;
        } finally {
            postResult(result);
        }
        return result;

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
                    result.mAsynchronousTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

    private void finish(TResult result) {
        asynchronousTask.onPostExecute(result);
        asynchronousTask.promoteCalls();
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
        final TaskCallable mTask;
        final Data[] mData;
        final AsynchronousTask mAsynchronousTask;

        TaskCallableResult(AsynchronousTask asynchronousTask, TaskCallable task, Data... data) {
            mAsynchronousTask = asynchronousTask;
            mTask = task;
            mData = data;
        }
    }

}
