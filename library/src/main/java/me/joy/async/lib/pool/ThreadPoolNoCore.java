package me.joy.async.lib.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by joybar on 13/05/2018.
 */

public class ThreadPoolNoCore {
	private static final String TAG = "ThreadPoolNoCore";
	private static ExecutorService executorService;

	public static synchronized ExecutorService executorService() {
		if (executorService == null) {
			executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory
					(false));
		}
		return executorService;
	}

	private static ThreadFactory threadFactory(final boolean daemon) {
		final AtomicInteger mCount = new AtomicInteger(1);
		return new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				Thread result = new Thread(runnable, "ThreadPoolNoCore #" + mCount.getAndIncrement());
				result.setDaemon(daemon);
				return result;
			}
		};
	}
}
