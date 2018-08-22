package me.joy.asynctask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import me.joy.async.lib.task.AsynchronousTask;
import me.joy.async.lib.util.ALog;


public class MainActivity extends AppCompatActivity {
	public static String TAG = "MainActivity";
	public static boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ALog.print("flag=" + flag);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(!flag){
					Intent intent = new Intent(MainActivity.this,MainActivity.class);
					startActivity(intent);
					flag = true;
				}else{
					testAsynchronousTasks();
				}
			}
		},1000);


	}



	private void testAsynchronousTasks() {

		for (int i = 0; i < 100; i++) {

			final int finalI = i;
			AsynchronousTask asynchronousTask = new AsynchronousTask<Integer, String>(this,20) {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					ALog.print("执行前" + finalI);
				}

				@Override
				protected String doInBackground() {
					ALog.print("子线程：" + getThreadName() + "后台执行" + finalI);
					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					this.publishProgress(finalI);
					return "A" + finalI + "  ";
				}

				@Override
				protected void onProgressUpdate(Integer... values) {
					super.onProgressUpdate(values);
					ALog.print(getThreadName() + "执行进度=" + values[0]);
				}

				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					ALog.print(getThreadName() + "执行结果=" + result);

				}

				@Override
				protected void onCancelled() {
					super.onCancelled();
					ALog.print(getThreadName() + "任务取消");
				}
			};
			asynchronousTask.execute();
			if (i > 70) {
				boolean flag = asynchronousTask.cancel(false);
				ALog.print("i>90, 取消=" + flag);
			}
		}
	}

	String getThreadName() {
		return Thread.currentThread().getName() + ": ";
	}
}
