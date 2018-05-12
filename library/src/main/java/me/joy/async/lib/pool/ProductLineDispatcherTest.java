package me.joy.async.lib.pool;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import me.joy.async.lib.util.Util;


/**
 * Created by joybar on 2018/5/11.
 */

public class ProductLineDispatcherTest {

	private int maxRequests = 64;
	private ExecutorService executorService;

	public ProductLineDispatcherTest() {
	}

	public static ProductLineDispatcherTest getInstance() {
		return ProductLineDispatcherHolder.INSTANCE;
	}

	private static class ProductLineDispatcherHolder {
		private static ProductLineDispatcherTest INSTANCE = new ProductLineDispatcherTest();
	}

	/**
	 * Running asynchronous calls. Includes canceled calls that haven't finished yet.
	 */
	private final Deque<Runnable> runningAsyncCalls = new ArrayDeque<>();

	private synchronized ExecutorService executorService() {
		if (executorService == null) {
			AtomicInteger mCount = new AtomicInteger(1);
			executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), Util
					.threadFactory("AsyncTaskClient Dispatcher #" + mCount.getAndIncrement(),false));
		}
		return executorService;
	}

	private static ThreadFactory threadFactory(final String name, final boolean daemon) {
		return new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				Thread result = new Thread(runnable, name);
				result.setDaemon(daemon);
				return result;
			}
		};
	}

	public synchronized void execute(Runnable runnable) {
		if (runningAsyncCalls.size() < maxRequests) {
			runningAsyncCalls.add(runnable);
			executorService().execute(runnable);
		} else {
			runningAsyncCalls.add(runnable);
		}
	}
}
