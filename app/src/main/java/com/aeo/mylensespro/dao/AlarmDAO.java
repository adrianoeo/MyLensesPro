package com.aeo.mylensespro.dao;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.aeo.mylensespro.service.AlarmBroadcastReceiver;
import com.aeo.mylensespro.service.BootBroadcastReceiver;
import com.aeo.mylensespro.service.DailyAlarmBroadcastReceiver;
import com.aeo.mylensespro.vo.AlarmVO;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmDAO {

    private static String tableName = "alarm";
    private static Context context;
    private static String[] columns = {"hour", "minute", "days_before",
            "remind_every_day"};
    //	private SQLiteDatabase db;
    private static final int ID_ALARM_LEFT = 1;
    private static final int ID_ALARM_RIGHT = 2;
    private static final int ID_ALARM_NEXT_DAY = 3;
    private static final int ID_ALARM_DAILY = 4;
    private static AlarmDAO instance;

    public static AlarmDAO getInstance(Context context) {
        if (instance == null) {
            return new AlarmDAO(context);
        }
        return instance;
    }

    public AlarmDAO(Context context) {
        AlarmDAO.context = context;
//		db = new DB(context).getWritableDatabase();
    }

    public boolean insert(AlarmVO vo) {
//		synchronized (MainActivity.sDataLock) {
//			mBackupManager.dataChanged();
//
//			return db.insert(tableName, null, getContentValues(vo)) > 0;
//		}

        return true;
    }

    public boolean update(AlarmVO vo) {
//		synchronized (MainActivity.sDataLock) {
//			mBackupManager.dataChanged();
//			return db.update(tableName, getContentValues(vo), null, null) > 0;
//		}

        return true;
    }

    private ContentValues getContentValues(AlarmVO vo) {
        ContentValues content = new ContentValues();
        content.put("hour", vo.getHour());
        content.put("minute", vo.getMinute());
        content.put("days_before", vo.getDaysBefore());
        content.put("remind_every_day", vo.getRemindEveryDay());

        return content;
    }

    public AlarmVO getAlarm() {
//		Cursor rs = db.query(tableName, columns, null, null, null, null, null);

        final AlarmVO[] vo = {new AlarmVO()};
//		if (rs.moveToFirst()) {
//			vo = new AlarmVO();
//			vo.setHour(rs.getInt(rs.getColumnIndex("hour")));
//			vo.setMinute(rs.getInt(rs.getColumnIndex("minute")));
//			vo.setDaysBefore(rs.getInt(rs.getColumnIndex("days_before")));
//			vo.setRemindEveryDay(rs.getInt(rs
//					.getColumnIndex("remind_every_day")));
//		}

        return vo[0].getHour() == 0 ? null : vo[0];
    }

    public void setAlarm(int idLens) {
        cancelAlarm();
        cancelAlarmDaily();

        TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
        Calendar[] calendars = timeLensesDAO.getDateAlarm(idLens);

        AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
        AlarmVO alarmVO = alarmDAO.getAlarm();

        if (alarmVO == null) {
            alarmVO = new AlarmVO();
            alarmVO.setHour(0L);
            alarmVO.setMinute(0L);
            alarmVO.setDaysBefore(0L);
        }

        Calendar calendarLeft = Calendar.getInstance();
        Calendar calendarRight = Calendar.getInstance();

        calendarLeft.set(Calendar.DAY_OF_MONTH,
                calendars[0].get(Calendar.DAY_OF_MONTH));
        calendarLeft.set(Calendar.MONTH, calendars[0].get(Calendar.MONTH));
        calendarLeft.set(Calendar.YEAR, calendars[0].get(Calendar.YEAR));
        calendarLeft.set(Calendar.HOUR_OF_DAY, (int) alarmVO.getHour());
        calendarLeft.set(Calendar.MINUTE, (int) alarmVO.getMinute());
        calendarLeft.set(Calendar.SECOND, 0);
        calendarLeft.set(Calendar.MILLISECOND, 0);

        calendarRight.set(Calendar.DAY_OF_MONTH,
                calendars[1].get(Calendar.DAY_OF_MONTH));
        calendarRight.set(Calendar.MONTH, calendars[1].get(Calendar.MONTH));
        calendarRight.set(Calendar.YEAR, calendars[1].get(Calendar.YEAR));
        calendarRight.set(Calendar.HOUR_OF_DAY, (int) alarmVO.getHour());
        calendarRight.set(Calendar.MINUTE, (int) alarmVO.getMinute());
        calendarRight.set(Calendar.SECOND, 0);
        calendarRight.set(Calendar.MILLISECOND, 0);

        // Seta as datas para dia(s) antes para a notificacao.
        int daysBefore = (int) alarmVO.getDaysBefore() * (-1);
        calendarLeft.add(Calendar.DATE, daysBefore);
        calendarRight.add(Calendar.DATE, daysBefore);

        // Se as datas das lentes esquerda e direita forem iguais seta apenas um
        // alarme, senao seta um para cada lente.
        if (calendarLeft.get(Calendar.DAY_OF_MONTH) == calendarRight
                .get(Calendar.DAY_OF_MONTH)
                && calendarLeft.get(Calendar.MONTH) == calendarRight
                .get(Calendar.MONTH)
                && calendarLeft.get(Calendar.YEAR) == calendarRight
                .get(Calendar.YEAR)) {

            setAlarmManager(ID_ALARM_LEFT, calendarLeft.getTimeInMillis());
        } else {
            setAlarmManager(ID_ALARM_LEFT, calendarLeft.getTimeInMillis());
            setAlarmManager(ID_ALARM_RIGHT, calendarRight.getTimeInMillis());
        }

        // Start daily notification if is checked and not expired
        if (alarmVO.getRemindEveryDay() == 1) {
            Long[] daysToExpire = timeLensesDAO.getDaysToExpire(timeLensesDAO
                    .getLastIdLens());

            if (daysToExpire[0] > 0 || daysToExpire[1] > 0) {
                setAlarmManagerDaily((int) alarmVO.getHour(), (int) alarmVO.getMinute());
            }
        }

        enableReceiverWhenBoot();
    }

    private void setAlarmManager(int idAlarm, long timeAlarm) {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                idAlarm, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManagerLeft = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManagerLeft.set(AlarmManager.RTC, timeAlarm, pendingIntent);
    }

    public void setAlarmManagerDaily(int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
//		calendar.add(Calendar.DATE, 1);

        String date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(calendar.getTime());

        Intent intent = new Intent(context, DailyAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                ID_ALARM_DAILY, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.RTC,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    public void cancelAlarm() {
        Intent intentLeft = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntentLeft = PendingIntent.getBroadcast(context,
                ID_ALARM_LEFT, intentLeft, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManagerLeft = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManagerLeft.cancel(pendingIntentLeft);

        Intent intentRight = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntentRight = PendingIntent.getBroadcast(context,
                ID_ALARM_RIGHT, intentRight, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManagerRight = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManagerRight.cancel(pendingIntentRight);

        Intent intentNexDay = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntentNextDay = PendingIntent.getBroadcast(
                context, ID_ALARM_NEXT_DAY, intentNexDay,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManagerNexDay = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManagerNexDay.cancel(pendingIntentNextDay);
    }

    public void cancelAlarmDaily() {
        Intent intent = new Intent(context, DailyAlarmBroadcastReceiver.class);
        PendingIntent pendingIntentLeft = PendingIntent.getBroadcast(context,
                ID_ALARM_DAILY, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntentLeft);
    }

    public void setAlarmNextDay(int idLens) {
        // Days to expire
        Long[] daysToExpire = TimeLensesDAO.getInstance(context).getDaysToExpire(
                idLens);

        Long daysToExpireLeft = daysToExpire[0];
        Long daysToExpireRight = daysToExpire[1];

        // Days before for notification
        AlarmVO alarmVO = AlarmDAO.getInstance(context).getAlarm();
        long daysBefore = alarmVO.getDaysBefore();
        long hour = alarmVO.getHour();
        long minute = alarmVO.getMinute();

        // Next day
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, (int) hour);
        today.set(Calendar.MINUTE, (int) minute);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        today.add(Calendar.DATE, 1);

        if (daysToExpireLeft - daysBefore <= 0
                || daysToExpireRight - daysBefore <= 0) {
            setAlarmManager(ID_ALARM_NEXT_DAY, today.getTimeInMillis());
            enableReceiverWhenBoot();
        }

    }

    private void enableReceiverWhenBoot() {
        // Enabling receiver when boot
        ComponentName receiver = new ComponentName(context,
                BootBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}
