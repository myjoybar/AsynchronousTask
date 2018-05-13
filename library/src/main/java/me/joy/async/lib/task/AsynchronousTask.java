package me.joy.async.lib.task;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import me.joy.async.lib.pool.ProductLineDispatcher;


/**
 * Created by joybar on 12/05/2018.
 */

public abstract class AsynchronousTask<TProgress, TResult> {
    private FutureTask<TResult> mFuture;
    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;
    private InternalHandler sHandler;
    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private TaskCallable mTaskCallable;

    public AsynchronousTask() {

    }

    protected void onPreExecute() {

    }

    protected abstract TResult doInBackground();


    protected void onProgressUpdate(TProgress... values) {

    }

    protected void onPostExecute(TResult result) {

    }

    protected void onCancelled() {
    }


    private void createCallable() {
        if (mTaskCallable == null) {
            mTaskCallable = new TaskCallable() {
                @Override
                public TResult call() {
                    TResult result = null;
                    try {
                        result = (TResult) doInBackground();
                    } catch (Throwable tr) {
                        mCancelled.set(true);
                        throw tr;
                    } finally {
                        postResult(result);
                    }
                    return result;

                }
            };
        }

    }

    public void produceWithCallable() {
        onPreExecute();
        createCallable();
        mFuture = new FutureTask(mTaskCallable);
        ProductLineDispatcher.getInstance().submit(mFuture);
    }


    private Handler getMainHandler() {
        synchronized (AsynchronousTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler(Looper.getMainLooper());
            }
            return sHandler;
        }
    }


    public final boolean isCancelled() {
        return mCancelled.get();
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
        if (null != mFuture) {
            boolean flag = mFuture.cancel(mayInterruptIfRunning);
            if (flag) {
                this.onCancelled();
            }
            return flag;
        }
        return false;
    }

    protected final void publishProgress(TProgress... values) {
        if (!isCancelled()) {
            getMainHandler().obtainMessage(MESSAGE_POST_PROGRESS, new
                    TaskCallableResult<TProgress>(this, values)).sendToTarget();
        }
    }

    private void postResult(TResult result) {
        @SuppressWarnings("unchecked") Message message = getMainHandler().obtainMessage
                (MESSAGE_POST_RESULT, new TaskCallableResult<TResult>(this, result));
        message.sendToTarget();
    }


    private void finish(TResult result) {
        onPostExecute(result);
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
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

    public abstract static class TaskCallable<TProgress, TResult> implements Callable {

    }

    @SuppressWarnings({"RawUseOfParameterizedType"})
    private static class TaskCallableResult<Data> {
        final AsynchronousTask mTask;
        final Data[] mData;

        TaskCallableResult(AsynchronousTask task, Data... data) {
            mTask = task;
            mData = data;
        }
    }


}
