package me.joy.asynctask;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    public static String TAG = "AsynchronousTask";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) this.findViewById(R.id.tv);
        tv.setTextSize(15);
        tv.setText("Activity A");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivityB.launch(MainActivity.this);

            }
        },1*1000);


    }



}
