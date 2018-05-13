package me.joy.async.lib.task;


import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.FutureTask;

import me.joy.async.lib.factory.AsyncFactory;
import me.joy.async.lib.pool.ThreadPoolSelector;


/**
 * Created by joybar on 12/05/2018.
 */

public abstract class AsynchronousTask<TProgress, TResult> {

    private FutureTask<TResult> mFuture;
    TaskCallable mTaskCallable;
    boolean isRemoved;

    protected void onPreExecute() {

    }

    protected abstract TResult doInBackground();


    protected void onProgressUpdate(TProgress... values) {

    }

    protected void onPostExecute(TResult result) {

    }

    protected void onCancelled() {
    }

    public final void publishProgress(TProgress... values) {
        if (null != mTaskCallable) {
            mTaskCallable.publishProgress(values);
        }


    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        boolean flag = false;
        if (null != mFuture) {
            flag = mFuture.cancel(mayInterruptIfRunning);
            mTaskCallable.cancel();
            if (flag) {
                this.onCancelled();
            }
        }
        if (AsyncFactory.RUNNING_ASYNC_REQUESTS.containsValue(this)) {
            AsyncFactory.RUNNING_ASYNC_REQUESTS.remove(this);
        }
        if (AsyncFactory.READY_ASYNC_REQUESTS.containsValue(this)) {

            AsyncFactory.READY_ASYNC_REQUESTS.remove(this);
        }
        isRemoved = true;
        return flag;
    }


    synchronized public void produceWithCallable() {
        if (AsyncFactory.RUNNING_ASYNC_REQUESTS.size() < AsyncFactory.MAX_ASYNC_REQUESTS) {
            if (this.isRemoved) {
                return;
            }
            AsyncFactory.RUNNING_ASYNC_REQUESTS.put(this, this);
            this.onPreExecute();
            mTaskCallable = new TaskCallable<TProgress, TResult>(this);
            mFuture = new FutureTask(mTaskCallable);
            ThreadPoolSelector.getInstance().submit(mFuture);
        } else {
            AsyncFactory.READY_ASYNC_REQUESTS.put(this, this);
        }

    }


    public void promoteCalls() {
        AsyncFactory.RUNNING_ASYNC_REQUESTS.remove(this);
        if (AsyncFactory.RUNNING_ASYNC_REQUESTS.size() >= AsyncFactory.MAX_ASYNC_REQUESTS) {
            return;
        }


        Iterator<Map.Entry<AsynchronousTask, AsynchronousTask>> entries = AsyncFactory
                .READY_ASYNC_REQUESTS.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry<AsynchronousTask, AsynchronousTask> entry = entries.next();
            AsynchronousTask asynchronousTask = entry.getValue();
            entries.remove();
            if (asynchronousTask.isRemoved) {
                asynchronousTask.onCancelled();
                return;
            }
            AsyncFactory.RUNNING_ASYNC_REQUESTS.put(asynchronousTask, asynchronousTask);
            asynchronousTask.onPreExecute();
            asynchronousTask.mTaskCallable = new TaskCallable<TProgress, TResult>(asynchronousTask);
            mFuture = new FutureTask(asynchronousTask.mTaskCallable);
            ThreadPoolSelector.getInstance().submit(mFuture);
            if (AsyncFactory.RUNNING_ASYNC_REQUESTS.size() >= AsyncFactory.MAX_ASYNC_REQUESTS) {
                return; // Reached max capacity.
            }
        }
    }

}
