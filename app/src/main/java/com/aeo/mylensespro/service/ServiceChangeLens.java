package com.aeo.mylensespro.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.aeo.mylensespro.R;
import com.aeo.mylensespro.activity.MainActivity;
import com.aeo.mylensespro.dao.AlarmDAO;
import com.aeo.mylensespro.dao.TimeLensesDAO;
import com.aeo.mylensespro.vo.AlarmVO;
import com.aeo.mylensespro.vo.TimeLensesVO;

public class ServiceChangeLens extends Service implements Runnable {

    private final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(this).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {

        TimeLensesDAO dao = TimeLensesDAO.getInstance(this);

//		String idLenses = ListReplaceLensFragment.listLenses == null ? dao
//				.getLastIdLens() : ListReplaceLensFragment.listLenses.get(0)
//				.getId();

//		Long[] days = dao.getDaysToExpire(idLenses);

        TimeLensesVO timeLensesVO = dao.getLastLens();
        Long[] days = dao.getDaysToExpire(timeLensesVO);

        // Get notification of days before.
        AlarmDAO alarmDAO = AlarmDAO.getInstance(this);
        AlarmVO alarmVO = alarmDAO.getAlarmNow();

        if (alarmVO != null && timeLensesVO != null) {
            long daysBefore = alarmVO.getDaysBefore();

            long dayLeftEye = days[0] - daysBefore;
            long dayRightEye = days[1] - daysBefore;

            if (dayLeftEye <= 0 && dayRightEye <= 0) {
                notifyUser("B", timeLensesVO);
            } else if (dayLeftEye <= 0) {
                notifyUser("L", timeLensesVO);
            } else if (dayRightEye <= 0) {
                notifyUser("R", timeLensesVO);
            }
        }

    }

    @SuppressLint("NewApi")
    private void notifyUser(String eye, TimeLensesVO timeLensesVO) {
        String msgEye = null;
        if (eye.equals("L")) {
            msgEye = getString(R.string.msgNotificationLeftEye);
        } else if (eye.equals("R")) {
            msgEye = getString(R.string.msgNotificationRightEye);
        } else {
            msgEye = getString(R.string.msgNotificationBothEyes);
        }

        int[] units = TimeLensesDAO.getInstance(this).getUnitsRemaining(timeLensesVO);
        int unitsLeft = units[0];
        int unitsRight = units[1];

        StringBuilder sbMsg = new StringBuilder();
        sbMsg.append(getString(R.string.str_units_remaining)).append(": ")
                .append(unitsLeft < 0 ? 0 : unitsLeft).append(" ")
                .append(getString(R.string.str_left)).append(" - ")
                .append(unitsRight < 0 ? 0 : unitsRight).append(" ")
                .append(getString(R.string.str_right));

        setNotification(msgEye, sbMsg.toString());
        stopSelf();

        AlarmDAO.getInstance(this).setAlarmNextDay(timeLensesVO);
    }

    private void setNotification(String contentText, String subText) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);
        builder.setAutoCancel(true);
        builder.setContentTitle(getString(R.string.msgNotificationTitle));
        builder.setContentText(contentText);
        builder.setSubText(subText);
        builder.setSmallIcon(R.drawable.ic_visibility_white_36dp);
        Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(),
                R.drawable.ic_launcher);
        builder.setLargeIcon(icon);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        Intent buttonIntent = new Intent(this,
                NotificationBroadcastReceiver.class);
        buttonIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);

        PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 1,
                buttonIntent, 0);

        builder.addAction(R.drawable.ic_action_cancel_dark,
                getString(R.string.msg_cancel_notification), btPendingIntent);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(R.string.app_name, notification);

    }
}
