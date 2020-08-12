package com.example.profiler;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Base64;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;

public class CommonClass {

    public static int takeFlags;
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
    public static Bitmap stringToBitmap(String b64) {
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
    }
}
