package me.joy.async.lib.lifecycle;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.lifecycle.joybar.lifecyclelistener.LifecycleManager;
import com.lifecycle.joybar.lifecyclelistener.interfaces.LifecycleListener;


/**
 * Created by joybar on 2017/6/30.
 */

public class LifecycleHelper {

	private static final String TAG = "LifecycleTestManager";

	private LifecycleHelper() {
	}

	public static LifecycleHelper newInstance() {
		LifecycleHelper lifecycleManager = new LifecycleHelper();
		return lifecycleManager;
	}

	public void registerLifecycleListener(Context context, LifecycleListener lifecycleListener) {
		LifecycleManager lifecycleManager = new LifecycleManager();
		lifecycleManager.registerLifecycleListener(context, lifecycleListener);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void registerLifecycleListener(android.app.Fragment fragment, final String fragmentTagName, LifecycleListener lifecycleListener) {
		LifecycleManager lifecycleManager = new LifecycleManager(fragmentTagName);
		lifecycleManager.registerLifecycleListener(fragment, lifecycleListener);
	}

	public void registerLifecycleListener(android.support.v4.app.Fragment fragment, final String fragmentTagName, LifecycleListener
			lifecycleListener) {
		LifecycleManager lifecycleManager = new LifecycleManager(fragmentTagName);
		lifecycleManager.registerLifecycleListener(fragment, lifecycleListener);
	}
}
