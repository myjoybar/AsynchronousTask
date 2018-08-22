package me.joy.async.lib.lifecycle;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.lifecycle.joybar.lifecyclelistener.interfaces.LifecycleListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.joy.async.lib.task.AsynchronousTask;
import me.joy.async.lib.util.ALog;

/**
 * Created by joybar on 2018/7/18.
 */

public class LifecycleMonitor {

	private static Map<Context, List<AsynchronousTask>> requestManagerContextMap = new HashMap<>();
	private static Map<android.app.Fragment, List<AsynchronousTask>> requestManagerFragmentMap = new HashMap<>();
	private static Map<android.support.v4.app.Fragment, List<AsynchronousTask>> requestManagerV4FragmentMap = new HashMap<>();


	public static void addMonitor(Context context, AsynchronousTask asynchronousTask) {
		createLifeMonitor(context, asynchronousTask);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static void addMonitor(android.app.Fragment fragment, AsynchronousTask asynchronousTask) {
		createLifeMonitor(fragment, asynchronousTask);
	}

	public static void addMonitor(android.support.v4.app.Fragment fragment, AsynchronousTask asynchronousTask) {
		createLifeMonitor(fragment, asynchronousTask);
	}


	private static void createLifeMonitor(final Context context, final AsynchronousTask asynchronousTask) {
		if (requestManagerContextMap.containsKey(context)) {
			List<AsynchronousTask> requestManagerList = requestManagerContextMap.get(context);
			requestManagerList.add(asynchronousTask);
		} else {
			List<AsynchronousTask> requestManagerList = new ArrayList<>();
			requestManagerList.add(asynchronousTask);
			requestManagerContextMap.put(context, requestManagerList);
			LifecycleHelper.newInstance().registerLifecycleListener(context, new LifecycleListener() {
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
					if (requestManagerContextMap.containsKey(context)) {
						List<AsynchronousTask> requestManagerList = requestManagerContextMap.get(context);
						int count = requestManagerList.size();
						for (int i = 0; i < count; i++) {
							AsynchronousTask asyncTask = requestManagerList.get(i);
							if (null != asyncTask) {
								ALog.print("onDestroy");
								asyncTask.cancel(false);
							}
						}
						requestManagerContextMap.remove(context);

					}
				}
			});
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	private static void createLifeMonitor(final android.app.Fragment fragment, final AsynchronousTask asynchronousTask) {
		if (requestManagerFragmentMap.containsKey(fragment)) {
			List<AsynchronousTask> requestManagerList = requestManagerFragmentMap.get(fragment);
			requestManagerList.add(asynchronousTask);
		} else {
			List<AsynchronousTask> requestManagerList = new ArrayList<>();
			requestManagerList.add(asynchronousTask);
			requestManagerFragmentMap.put(fragment, requestManagerList);
			LifecycleHelper.newInstance().registerLifecycleListener(fragment, "android.app.Fragment", new LifecycleListener() {
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
					if (requestManagerFragmentMap.containsKey(fragment)) {
						List<AsynchronousTask> requestManagerList = requestManagerFragmentMap.get(fragment);
						int count = requestManagerList.size();
						for (int i = 0; i < count; i++) {
							AsynchronousTask asyncTask = requestManagerList.get(i);
							if (null != asyncTask) {
								ALog.print("onDestroy");
								asyncTask.cancel(false);
							}
						}
						requestManagerFragmentMap.remove(fragment);

					}
				}
			});
		}
	}

	private static void createLifeMonitor(final android.support.v4.app.Fragment fragment, final AsynchronousTask asynchronousTask) {
		if (requestManagerV4FragmentMap.containsKey(fragment)) {
			List<AsynchronousTask> requestManagerList = requestManagerV4FragmentMap.get(fragment);
			requestManagerList.add(asynchronousTask);
		} else {
			List<AsynchronousTask> requestManagerList = new ArrayList<>();
			requestManagerList.add(asynchronousTask);
			requestManagerV4FragmentMap.put(fragment, requestManagerList);
			LifecycleHelper.newInstance().registerLifecycleListener(fragment, "android.support.v4.app.Fragment", new LifecycleListener() {
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
					if (requestManagerV4FragmentMap.containsKey(fragment)) {
						List<AsynchronousTask> requestManagerList = requestManagerV4FragmentMap.get(fragment);
						int count = requestManagerList.size();
						for (int i = 0; i < count; i++) {
							AsynchronousTask asyncTask = requestManagerList.get(i);
							if (null != asyncTask) {
								ALog.print("onDestroy");
								asyncTask.cancel(false);
							}
						}
						requestManagerV4FragmentMap.remove(fragment);

					}
				}
			});
		}
	}
}
