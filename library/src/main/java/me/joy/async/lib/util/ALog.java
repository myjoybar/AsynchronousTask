package me.joy.async.lib.util;

import android.util.Log;

/**
 * Created by joybar on 2018/5/17.
 */

public class ALog {
	private static boolean enable = true;
	private static final String TAG = "AsynchronousTask";

	public static void setEnable(boolean enable) {
		ALog.enable = enable;
	}

	public static void print(String msg) {
		if (enable) {
			Log.d(TAG, msg);
		}
	}

}
