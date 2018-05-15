package me.joy.async.lib.pool;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by joybar on 2018/5/11.
 */

public class ThreadPoolWithCore {
	private static final String TAG = "ThreadPoolWithCore";
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	// We want at least 2 threads and at most 4 threads in the core pool,
	// preferring to have 1 less than the CPU count to avoid saturating
	// the CPU with background work
	private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
	private static final int KEEP_ALIVE_SECONDS = 30;

	private static ExecutorService executorService;

	private static RejectedExecutionHandler defaultHandler = new DiscardPolicyHandler();
	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "ThreadPoolWithCore #" + mCount.getAndIncrement());
		}
	};

	private static class DiscardPolicyHandler implements RejectedExecutionHandler {

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			Log.w(TAG, "ThreadPoolWithCore is full, discard this");
		}
	}

	private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);

	public static synchronized ExecutorService getExecutorService() {

		if (executorService == null) {
			executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory,
					defaultHandler);
		}
		return executorService;
	}


}
