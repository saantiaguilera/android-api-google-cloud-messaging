package com.santiago.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.santiago.gcm.entities.Push;

public abstract class PushNotification {

    private static final int ICON_SOURCE_EMPTY = 0;
    private static final int ICON_SOURCE_ID = 1;
    private static final int ICON_SOURCE_BM = 2;

    private Context context;
    private NotificationManager mNotificationManager;
    private Push push;

    private String title;
    private String msg;
    private int notificationId;
    private int IconId;
    private Bitmap IconBm;
    private int iconSource=ICON_SOURCE_EMPTY;
    private int colorLed;
    private boolean autoCancel = true;
    private Uri sound;
    private int priority = Notification.PRIORITY_DEFAULT;
    private long[] vibrationPattern;
    private PendingIntent intent;
    private NotificationCompat.Style style;

    private static long[] DEFAULT_VIBRATION_PATTERN = { 500,500,500 };

    public PushNotification(Context context) {
        this(context,null);
    }

    public PushNotification(Context context, Push push){
        this.context=context;
        this.push = push;

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        setColorLed(getDefaultLedColor());
        setSound(getDefaultRingtone());
        setVibrationPattern(getDefaultVibrationPattern());
        setPendingIntent(getDefaultPendingIntent());
        setIconId(getDefaultIconId());

        if(push!=null){
            setTitle(push.getTitle());
            setMsg(push.getMessage());
            setNotificationId((int) push.getId());

            setAdditionalParamsFromPush(push);
        }
    }

    protected Context getContext() {
        return context;
    }

    protected Push getPush() {
        return push;
    }

    protected Uri getDefaultRingtone(){
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    protected long[] getDefaultVibrationPattern(){
        return DEFAULT_VIBRATION_PATTERN;
    }

    protected PendingIntent getDefaultPendingIntent(){
        return null;
    }

    protected int getDefaultLedColor(){
        return Color.CYAN;
    }

    protected int getDefaultIconId() {
        return android.R.drawable.ic_notification_clear_all;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setIconId(int iconId) {
        IconId = iconId;
        iconSource = ICON_SOURCE_ID;
    }

    public void setIconBm(Bitmap iconBm) {
        IconBm = iconBm;
        iconSource = ICON_SOURCE_BM;
    }

    public void setColorLed(int colorLed) {
        this.colorLed = colorLed;
    }

    public void setVibrationPattern(long[] vibrationPattern) {
        this.vibrationPattern = vibrationPattern;
    }

    public void setSound(Uri sound) {
        this.sound = sound;
    }

    public void setAutoCancel(boolean autoCancel) {
        this.autoCancel = autoCancel;
    }

    public void setPendingIntent(PendingIntent intent) {
        this.intent = intent;
    }

    public void setHeadsUp(boolean headsUp) { this.priority = headsUp ? Notification.PRIORITY_MAX : Notification.PRIORITY_DEFAULT; }

    public void setStyle(NotificationCompat.Style style) {
        this.style = style;
    }

    /**
     * Implement for setting custom stuff from the push
     * @param push
     */
    protected abstract void setAdditionalParamsFromPush(Push push);

    /**
     * Send to the android os the new push notification
     */
    public void send(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        switch (iconSource) {
            case ICON_SOURCE_ID:
                mBuilder.setSmallIcon(IconId);
                break;

            case ICON_SOURCE_BM:
                mBuilder.setLargeIcon(IconBm);
                break;

            case ICON_SOURCE_EMPTY:
                mBuilder.setSmallIcon(getDefaultIconId());
                break;

            default:
                return;
        }

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(msg);
        mBuilder.setAutoCancel(autoCancel);
        mBuilder.setLights(colorLed, 500, 500);
        if(sound!=null)
            mBuilder.setSound(sound);
        if(vibrationPattern!=null)
            mBuilder.setVibrate(vibrationPattern);
        if(style!=null)
            mBuilder.setStyle(style);
        if(intent!=null)
            mBuilder.setContentIntent(intent);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mBuilder.setPriority(priority);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

}
