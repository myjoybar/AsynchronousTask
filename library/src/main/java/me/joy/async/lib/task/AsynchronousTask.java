package me.joy.async.lib.task;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.lifecycle.joybar.lifecyclelistener.LifecycleManager;
import com.lifecycle.joybar.lifecyclelistener.interfaces.LifecycleListener;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.FutureTask;

import me.joy.async.lib.pool.ThreadPoolSelector;
import me.joy.async.lib.util.ALog;


/**
 * Created by joybar on 12/05/2018.
 */

public abstract class AsynchronousTask<TProgress, TResult> {
    private static final int MAX_ASYNC_TASKS = 64;
    private static final Map<AsynchronousTask, AsynchronousTask> RUNNING_ASYNC_TASKS = new
            HashMap<>();
    private static final Map<AsynchronousTask, AsynchronousTask> READY_ASYNC_TASKS = new
            HashMap<>();
    private static int MAX_TASK_NUMBER = MAX_ASYNC_TASKS;
    private FutureTask<TResult> mFuture;
    private TaskCallable mTaskCallable;
    private boolean isRemoved;
    private WeakReference<Activity> weakActivity;

    public AsynchronousTask(int taskNumber) {
        MAX_TASK_NUMBER = taskNumber;
    }


    public AsynchronousTask() {
        this(null);
    }

    /**
     * @param context
     * if context is not null, the task will be cancel when the activity onDestroy
     */
    public AsynchronousTask(Context context) {
        if (context != null) {
            registerLifecycleListener(context);
            if (!(context instanceof Application)) {
                if (context instanceof FragmentActivity) {
                    weakActivity = new WeakReference<Activity>((FragmentActivity) context);
                } else if (context instanceof Activity) {
                    weakActivity = new WeakReference<Activity>((Activity) context);
                }
            }
        }
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
        if (RUNNING_ASYNC_TASKS.containsValue(this)) {
            RUNNING_ASYNC_TASKS.remove(this);
        }
        if (READY_ASYNC_TASKS.containsValue(this)) {

            READY_ASYNC_TASKS.remove(this);
        }
        isRemoved = true;
        return flag;
    }


    synchronized public void execute() {
        if (RUNNING_ASYNC_TASKS.size() < MAX_TASK_NUMBER) {
            if (this.isRemoved) {
                return;
            }
            RUNNING_ASYNC_TASKS.put(this, this);
            this.onPreExecute();
            mTaskCallable = new TaskCallable<TProgress, TResult>(this);
            mFuture = new FutureTask(mTaskCallable);
            ThreadPoolSelector.getInstance().submit(mFuture);
        } else {
            READY_ASYNC_TASKS.put(this, this);
        }
    }


    protected void promoteTasks() {
        RUNNING_ASYNC_TASKS.remove(this);
        if (RUNNING_ASYNC_TASKS.size() >= MAX_TASK_NUMBER) {
            return;
        }
        Iterator<Map.Entry<AsynchronousTask, AsynchronousTask>> entries = READY_ASYNC_TASKS
                .entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<AsynchronousTask, AsynchronousTask> entry = entries.next();
            AsynchronousTask asynchronousTask = entry.getValue();
            entries.remove();
            if (asynchronousTask.isRemoved) {
                asynchronousTask.onCancelled();
                return;
            }
            RUNNING_ASYNC_TASKS.put(asynchronousTask, asynchronousTask);
            asynchronousTask.onPreExecute();
            asynchronousTask.mTaskCallable = new TaskCallable<TProgress, TResult>(asynchronousTask);
            mFuture = new FutureTask(asynchronousTask.mTaskCallable);
            ThreadPoolSelector.getInstance().submit(mFuture);
            if (RUNNING_ASYNC_TASKS.size() >= MAX_TASK_NUMBER) {
                return; // Reached max capacity.
            }
        }
    }

    private void registerLifecycleListener(Context context) {

        LifecycleManager lifecycleManager = new LifecycleManager();
        lifecycleManager.registerLifecycleListener(context, new LifecycleListener() {
            @Override
            public void onStart() {
                ALog.print("onStart");
            }

            @Override
            public void onResume() {
                ALog.print("onResume");
            }

            @Override
            public void onPause() {
                ALog.print("onPause");
            }

            @Override
            public void onStop() {
                ALog.print("onStop");
            }

            @Override
            public void onDestroy() {
                ALog.print("onDestroy");
                if (weakActivity != null) {
                    isRemoved = true;
                    // cancel the task
                    if (null != mTaskCallable) {
                        mTaskCallable.cancel();
                    }
                    // notify the task had cancelled
                    onCancelled();
                }
            }
        });
    }


}
