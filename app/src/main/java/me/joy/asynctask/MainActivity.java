package me.joy.asynctask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import me.joy.async.lib.task.AsynchronousTask;


public class MainActivity extends AppCompatActivity {
    public static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testAsynchronousTasks();
    }


    private void testAsynchronousTasks() {

        for (int i = 0; i < 100; i++) {

            final int finalI = i;
            AsynchronousTask asynchronousTask = new AsynchronousTask<Integer, String>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Log.d(TAG, "执行前" + finalI);
                }

                @Override
                protected String doInBackground() {
                    Log.d(TAG, "子线程：" + getThreadName() + "后台执行" + finalI);
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
                    Log.d(TAG, getThreadName() + "执行进度=" + values[0]);
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    Log.d(TAG, getThreadName() + "执行结果=" + result);

                }

                @Override
                protected void onCancelled() {
                    super.onCancelled();
                    Log.d(TAG, getThreadName() + "任务取消");
                }
            };

            asynchronousTask.execute();
            if (i > 70) {
                boolean flag = asynchronousTask.cancel(false);
                Log.d(TAG, "i>90, 取消=" + flag);
            }
        }
    }

    String getThreadName() {
        return Thread.currentThread().getName() + ": ";
    }
}
