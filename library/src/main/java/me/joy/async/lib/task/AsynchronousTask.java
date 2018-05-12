package me.joy.async.lib.task;


import java.util.concurrent.FutureTask;

import me.joy.async.lib.pool.ProductLineDispatcher;


/**
 * Created by joybar on 12/05/2018.
 */

public abstract class AsynchronousTask<TProgress, TResult> {
    private FutureTask<TResult> mFuture;
    TaskCallable mTaskCallable;
    TaskRunnable mTaskRunnable;


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
        if(null!=mTaskCallable){
            mTaskCallable.publishProgress(values);
        }
        if(null!=mTaskRunnable){
            mTaskRunnable.publishProgress(values);
        }

    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        if (null != mFuture) {
            boolean flag = mFuture.cancel(mayInterruptIfRunning);
            if (flag) {
                this.onCancelled();
            }
            return flag;
        }
        return false;
    }


    public void produceWithCallable() {
        onPreExecute();

        mTaskCallable = new TaskCallable<TProgress, TResult>() {

            @Override
            protected TResult onDoInBackground() {
                return doInBackground();
            }

            @Override
            protected void onProduceProgressUpdate(TProgress[] values) {
                super.onProduceProgressUpdate(values);
                onProgressUpdate(values);
            }

            @Override
            protected void onPostProduce(TResult tResult) {
                super.onPostProduce(tResult);
                onPostExecute(tResult);
            }
        };
        mFuture = new FutureTask(mTaskCallable);
        ProductLineDispatcher.getInstance().submit(mFuture);
    }

    public void produceWithRunnable() {

        onPreExecute();
        mTaskRunnable = new TaskRunnable<TProgress, TResult>() {


            @Override
            protected TResult onDoInBackground() {
                return doInBackground();
            }

            @Override
            protected void onProduceProgressUpdate(TProgress[] values) {
                super.onProduceProgressUpdate(values);
                onProgressUpdate(values);
            }

            @Override
            protected void onPostProduce(TResult tResult) {
                super.onPostProduce(tResult);
                onPostExecute(tResult);
            }
        };
        mFuture = new FutureTask(mTaskRunnable, null);
        ProductLineDispatcher.getInstance().submit(mFuture);
    }

}
