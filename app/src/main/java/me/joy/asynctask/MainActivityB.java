package me.joy.asynctask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import me.joy.async.lib.task.AsynchronousTask;


public class MainActivityB extends AppCompatActivity {
    public static String TAG = "AsynchronousTask";


    public static void launch(Context context){
        Intent intent = new Intent(context,MainActivityB.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) this.findViewById(R.id.tv);
        tv.setTextSize(15);
        tv.setText("Activity B");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    testAsynchronousTasks();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },2*1000);

    }


    private void testAsynchronousTasks() throws InterruptedException {

        for (int i = 0; i < 1; i++) {


            final int finalI = i;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    AsynchronousTask asynchronousTask = new AsynchronousTask<Integer, String>(MainActivityB.this) {

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
                    if (finalI > 70) {
                        boolean flag = asynchronousTask.cancel(false);
                        Log.d(TAG, "i>90, 取消=" + flag);
                    }




                }
            },i*1000);


        }
    }

    String getThreadName() {
        return Thread.currentThread().getName() + ": ";
    }
}
