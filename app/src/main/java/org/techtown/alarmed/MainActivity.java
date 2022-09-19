package org.techtown.alarmed;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    boolean flag = true;
    private CountDownTimer countDownTimer;
    private  long time = 0;
    private  long tempTime = 0;
    private  TextView duration;
    private  final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView currentTime = (TextView) findViewById(R.id.currentTime);
        duration = (TextView) findViewById(R.id.duration);
        final Button stopButton = (Button) findViewById(R.id.stopBtn);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarmmusic);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        startTimer();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }


        (new Thread(new Runnable() {

            @Override
            public void run() {
                while (flag == true)
                    try {

                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() // start actions in UI thread
                        {

                            @Override
                            public void run() {
                                currentTime.setText(getCurrentTime());

                            }


                        });
                    } catch (InterruptedException e) {
                        // ooops
                    }
            }
        })).start();
    }


    public String getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh : mm : ss");
        String getTime = dateFormat.format(date);
        return getTime;
    }

    public void startTimer() {
        tempTime = 10000;
        time = tempTime;

        countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tempTime = millisUntilFinished;
                updateDuration();


            }

            @Override
            public void onFinish() {
                timeOver();
            }
        }.start();
    }
    public void updateDuration() {


        int min = (int)tempTime/60000;
        int sec = (int)tempTime%60000/1000;

        String timeLeftText = "";
        if(min<10)
            timeLeftText +="0";
        timeLeftText += min + " : ";

        if (sec<10)
            timeLeftText += "0";
        timeLeftText += sec;

        duration.setText(timeLeftText);


    }
    public void onClick_stopBtn(View v) {
        mediaPlayer.stop();
        flag=false;
        finish();
    }

    public void timeOver() {
        mediaPlayer.stop();
        flag=false;
        String phoneNo = "01071924691";
        String sms = "날 깨워조!";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "전송완료!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        finish();
    }
}