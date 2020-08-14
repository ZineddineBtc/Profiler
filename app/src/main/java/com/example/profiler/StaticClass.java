package com.example.profiler;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.SystemClock;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.profiler.activities.all_data.AllDataActivity;
import com.example.profiler.activities.specific_data.ProfileActivity;
import com.example.profiler.adapters.NotificationPublisher;
import com.example.profiler.models.Profile;

import java.util.ArrayList;

public class StaticClass {

    public static int PICK_SINGLE_IMAGE = 1;
    public static int PICK_MULTIPLE_IMAGES = 2;
    public static int myProfileID = 1;
    public static String PROFILE_ID = "profile_id";
    public static String RECORD_ID = "record_id";
    public static String FROM = "from";
    public static String TO = "to";
    public static String SELECT_PROFILE = "select profile";
    public static String ALL_RECORDS = "all records";
    public static String PROFILE_RECORDS = "profile records";
    public static String PROFILE = "profile";
    public static String My_PROFILE = "my profile";
    public static String ACTION = "action";
    public static String CREATE = "create";
    public static String UPDATE = "update";
    public static long showErrorTV = 1500; // 1s
    /*public static Bitmap stringToBitmap(String b64) {
        if(b64==null){
            return null;
        }else{
            byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }
    }
    public static String drawableToString (Drawable drawable) {
        Bitmap bitmap;

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }*/
    public static void addDot(Context context, ArrayList<Bitmap> pagerImagesList,
                              int pagePosition, TextView[] dot, LinearLayout dotLayout) {
        dot = new TextView[pagerImagesList.size()];
        dotLayout.removeAllViews();
        for (int i = 0; i < dot.length; i++) {
            dot[i] = new TextView(context);
            dot[i].setText(Html.fromHtml("&#9673;"));
            dot[i].setTextSize(14);
            dot[i].setTextColor(context.getColor(R.color.dark_grey));
            dotLayout.addView(dot[i]);
        }
        dot[pagePosition].setTextColor(context.getColor(R.color.blue));
    }
    /*public void setAlarm(Context context, int id){

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent){
                button.setBackgroundColor(Color.RED);
                context.unregisterReceiver(this); // this = BroadcastReceiver, not Activity
            }
        };

        context.registerReceiver( receiver, new IntentFilter("com.blah.blah.somemessage") );
        PendingIntent pendingIntent = PendingIntent.getBroadcast( context
                , 0, new Intent("com.blah.blah.somemessage"), 0 );
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(manager)
                .set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000*5, pendingIntent);
    }*/
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void createReminder(Context context, Notification notification,
                                Calendar alarmCalendar) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long delay = alarmCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public static Notification getNotification(Context context, String profileName) {
        String channelId = "Reminders";
        PendingIntent newEntryActivityPendingIntent = PendingIntent.getActivity(
                context, 1, new Intent(context, AllDataActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        String message = "It's "+profileName+"'s birthday!";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, channelId)
                .setContentTitle(context.getString(R.string.birthday))
                .setContentText(message)
                .setTicker(context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_cake_white)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setContentIntent(newEntryActivityPendingIntent);
        return builder.build();
    }
}
