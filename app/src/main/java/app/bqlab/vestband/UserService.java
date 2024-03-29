package app.bqlab.vestband;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class UserService extends Service {
    public static String id;
    public static int degree, right, bad, rightTime, badTime, totalTime;
    public static boolean isConnected, isNotified;
    public static NotificationManager notificationManager;
    public static NotificationChannel notificationChannel;
    public static BluetoothDevice device;
    public static Thread thread;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isVersionOverOreo();
        String content = intent.getStringExtra("content");
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent p = PendingIntent.getActivity(this, 0, i, 0);
        Notification notification = new NotificationCompat.Builder(this, "알림")
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(p)
                .build();
        startForeground(1, notification);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isConnected) {
                    try {
                        isAngleCorrect(UserService.degree);
                        Log.d("Degree", Integer.toString(degree));
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });
        thread.start();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void isVersionOverOreo() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel("em", "긴급알림", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("긴급한 상황을 알립니다.");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void isAngleCorrect(int degree) {
        totalTime = getSharedPreferences("time", MODE_PRIVATE).getInt("total", 0) + 1;
        getSharedPreferences("time", MODE_PRIVATE).edit().putInt("total", totalTime).apply();
        if (degree > right - 10 && degree < right + 10) {
            rightTime = getSharedPreferences("time", MODE_PRIVATE).getInt("right", 0) + 1;
            getSharedPreferences("time", MODE_PRIVATE).edit().putInt("right", rightTime).apply();
        }
        if (degree > bad - 10 && degree < bad + 10) {
            badTime = getSharedPreferences("time", MODE_PRIVATE).getInt("bad", 0) + 1;
            getSharedPreferences("time", MODE_PRIVATE).edit().putInt("bad", badTime).apply();
            try {
                Thread.sleep(getSharedPreferences("setting", MODE_PRIVATE).getInt("notifyTime", 0) * 1000);
                getSharedPreferences("time", MODE_PRIVATE).edit().putInt("vibrate", badTime).apply();
                makeNotification();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else
            isNotified = false;
    }

    public void makeNotification() {
        Log.d("Bad", "Warning!!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.notify(0, new NotificationCompat.Builder(this, "em")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("알림")
                    .setContentText("현재 나쁜 자세를 취하고 있습니다.")
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build());
        } else {
            notificationManager.notify(0, new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("알림")
                    .setContentText("현재 나쁜 자세를 취하고 있습니다.")
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .build());
        }
    }
}
