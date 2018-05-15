package me.joy.async.lib.task;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.FutureTask;

import me.joy.async.lib.pool.ThreadPoolSelector;


/**
 * Created by joybar on 12/05/2018.
 */

public abstract class AsynchronousTask<TProgress, TResult> {
	private static final int MAX_ASYNC_REQUESTS = 64;
	private final static Map<AsynchronousTask, AsynchronousTask> RUNNING_ASYNC_REQUESTS = new HashMap<>();
	private final static Map<AsynchronousTask, AsynchronousTask> READY_ASYNC_REQUESTS = new HashMap<>();

	private FutureTask<TResult> mFuture;
	private TaskCallable mTaskCallable;
	private boolean isRemoved;

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
		if (RUNNING_ASYNC_REQUESTS.containsValue(this)) {
			RUNNING_ASYNC_REQUESTS.remove(this);
		}
		if (READY_ASYNC_REQUESTS.containsValue(this)) {

			READY_ASYNC_REQUESTS.remove(this);
		}
		isRemoved = true;
		return flag;
	}


	synchronized public void execute() {
		if (RUNNING_ASYNC_REQUESTS.size() < MAX_ASYNC_REQUESTS) {
			if (this.isRemoved) {
				return;
			}
			RUNNING_ASYNC_REQUESTS.put(this, this);
			this.onPreExecute();
			mTaskCallable = new TaskCallable<TProgress, TResult>(this);
			mFuture = new FutureTask(mTaskCallable);
			ThreadPoolSelector.getInstance().submit(mFuture);
		} else {
			READY_ASYNC_REQUESTS.put(this, this);
		}
	}


	protected void promoteCalls() {
		RUNNING_ASYNC_REQUESTS.remove(this);
		if (RUNNING_ASYNC_REQUESTS.size() >= MAX_ASYNC_REQUESTS) {
			return;
		}
		Iterator<Map.Entry<AsynchronousTask, AsynchronousTask>> entries = READY_ASYNC_REQUESTS.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<AsynchronousTask, AsynchronousTask> entry = entries.next();
			AsynchronousTask asynchronousTask = entry.getValue();
			entries.remove();
			if (asynchronousTask.isRemoved) {
				asynchronousTask.onCancelled();
				return;
			}
			RUNNING_ASYNC_REQUESTS.put(asynchronousTask, asynchronousTask);
			asynchronousTask.onPreExecute();
			asynchronousTask.mTaskCallable = new TaskCallable<TProgress, TResult>(asynchronousTask);
			mFuture = new FutureTask(asynchronousTask.mTaskCallable);
			ThreadPoolSelector.getInstance().submit(mFuture);
			if (RUNNING_ASYNC_REQUESTS.size() >= MAX_ASYNC_REQUESTS) {
				return; // Reached max capacity.
			}
		}
	}

}
