package com.aeo.mylensespro.dao;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.aeo.mylensespro.service.AlarmBroadcastReceiver;
import com.aeo.mylensespro.service.BootBroadcastReceiver;
import com.aeo.mylensespro.service.DailyAlarmBroadcastReceiver;
import com.aeo.mylensespro.util.Utility;
import com.aeo.mylensespro.vo.AlarmVO;
import com.aeo.mylensespro.vo.TimeLensesVO;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

public class AlarmDAO {

    private static String tableName = "alarm";
    private static Context context;

    private static final int ID_ALARM_LEFT = 1;
    private static final int ID_ALARM_RIGHT = 2;
    private static final int ID_ALARM_NEXT_DAY = 3;
    private static final int ID_ALARM_DAILY = 4;
    private static AlarmDAO instance;

    public static AlarmVO alarmVO;

    public static AlarmDAO getInstance(Context context) {
        if (instance == null) {
            return new AlarmDAO(context);
        }
        return instance;
    }

    public AlarmDAO(Context context) {
        AlarmDAO.context = context;
    }

    public void insert(AlarmVO vo) {
        ParseObject post = getParseObjectAlarm(vo);
        post.setACL(new ParseACL(ParseUser.getCurrentUser()));
        post.pinInBackground(tableName);
        post.saveEventually();

        if (Utility.isNetworkAvailable(context)) {
            post.saveInBackground();
        }

    }

    private boolean isFromPinNull() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.orderByDescending("createdAt");
        query.fromPin(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());

        try {
            return query.count() <= 0;
        } catch (com.parse.ParseException e) {
            return true;
        }
    }

    public void update(AlarmVO vo) {
        final int hour = vo.getHour();
        final int minute = vo.getMinute();
        final int remind_every_day = vo.getRemindEveryDay();
        final int days_before = vo.getDaysBefore();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);

        boolean isConnectionFast = Utility.isConnectionFast(context);
        boolean isNetworkAvailable = Utility.isNetworkAvailable(context);

        //Local
        if ((!isConnectionFast && !isFromPinNull()) || !isNetworkAvailable) {
            query.fromPin(tableName);
        }

        query.whereEqualTo("user_id", ParseUser.getCurrentUser());

        // Retrieve the object by id
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject post, ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data.
                    post.put("hour", hour);
                    post.put("minute", minute);
                    post.put("remind_every_day", remind_every_day);
                    post.put("days_before", days_before);

                    post.setACL(new ParseACL(ParseUser.getCurrentUser()));
                    post.saveEventually();
                    post.pinInBackground(tableName);

                    if (Utility.isNetworkAvailable(context)) {
                        post.saveInBackground();
                    }

//                    post.saveInBackground(new SaveCallback() {
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                // Saved successfully.
////                                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
//                            } else {
//                                // The save failed.
////                                Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
//                                Log.d(getClass().getSimpleName(), "alarm update error: " + e);
//                            }
//                        }
//                    });
                }
            }
        });

    }

    private ParseObject getParseObjectAlarm(AlarmVO vo) {
        ParseObject post = new ParseObject(tableName);
        post.put("user_id", ParseUser.getCurrentUser());
        post.put("hour", vo.getHour());
        post.put("minute", vo.getMinute());
        post.put("remind_every_day", vo.getRemindEveryDay());
        post.put("days_before", vo.getDaysBefore());

        return post;
    }

    public AlarmVO getAlarm() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.orderByDescending("createdAt");

        boolean isConnectionFast = Utility.isConnectionFast(context);
        boolean isNetworkAvailable = Utility.isNetworkAvailable(context);

        //Local
        if ((!isConnectionFast && !isFromPinNull()) || !isNetworkAvailable) {
            query.fromPin(tableName);
        }

        query.whereEqualTo("user_id", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> postList, ParseException e) {
                if (e == null) {
                    if (postList != null && postList.size() > 0) {
                        for (ParseObject post : postList) {
                            alarmVO = new AlarmVO();
                            alarmVO.setHour(post.getInt("hour"));
                            alarmVO.setMinute(post.getInt("minute"));
                            alarmVO.setDaysBefore(post.getInt("days_before"));
                            alarmVO.setRemindEveryDay(post.getInt("remind_every_day"));
                            post.saveEventually();
                        }
                        ParseObject.unpinAllInBackground(tableName);
                        ParseObject.pinAllInBackground(tableName, postList);
                    }
                } else {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                }
            }
        });

        return alarmVO;
    }

    public AlarmVO getAlarmNow() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.orderByDescending("createdAt");

        boolean isConnectionFast = Utility.isConnectionFast(context);
        boolean isNetworkAvailable = Utility.isNetworkAvailable(context);

        //Local
        if ((!isConnectionFast && !isFromPinNull()) || !isNetworkAvailable) {
            query.fromPin(tableName);
        }

        query.whereEqualTo("user_id", ParseUser.getCurrentUser());

        try {
            List<ParseObject> objects = query.find();
            if (objects != null && objects.size() > 0) {
                for (ParseObject post : objects) {
                    alarmVO = new AlarmVO();
                    alarmVO.setHour(post.getInt("hour"));
                    alarmVO.setMinute(post.getInt("minute"));
                    alarmVO.setDaysBefore(post.getInt("days_before"));
                    alarmVO.setRemindEveryDay(post.getInt("remind_every_day"));
                    post.saveEventually();
                }
                ParseObject.unpinAllInBackground(tableName);
                ParseObject.pinAllInBackground(tableName, objects);
            }

        } catch (ParseException e) {
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }
        return alarmVO;
    }

    public void setAlarm(TimeLensesVO timeLensesVO) {
        cancelAlarm();
        cancelAlarmDaily();

        TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
        Calendar[] calendars = timeLensesDAO.getDateAlarm(timeLensesVO);

        if (alarmVO == null) {
            alarmVO = new AlarmVO();
            alarmVO.setHour(0);
            alarmVO.setMinute(0);
            alarmVO.setDaysBefore(0);
        }

        Calendar calendarLeft = Calendar.getInstance();
        Calendar calendarRight = Calendar.getInstance();

        calendarLeft.set(Calendar.DAY_OF_MONTH,
                calendars[0].get(Calendar.DAY_OF_MONTH));
        calendarLeft.set(Calendar.MONTH, calendars[0].get(Calendar.MONTH));
        calendarLeft.set(Calendar.YEAR, calendars[0].get(Calendar.YEAR));
        calendarLeft.set(Calendar.HOUR_OF_DAY, alarmVO.getHour());
        calendarLeft.set(Calendar.MINUTE, alarmVO.getMinute());
        calendarLeft.set(Calendar.SECOND, 0);
        calendarLeft.set(Calendar.MILLISECOND, 0);

        calendarRight.set(Calendar.DAY_OF_MONTH,
                calendars[1].get(Calendar.DAY_OF_MONTH));
        calendarRight.set(Calendar.MONTH, calendars[1].get(Calendar.MONTH));
        calendarRight.set(Calendar.YEAR, calendars[1].get(Calendar.YEAR));
        calendarRight.set(Calendar.HOUR_OF_DAY, alarmVO.getHour());
        calendarRight.set(Calendar.MINUTE, alarmVO.getMinute());
        calendarRight.set(Calendar.SECOND, 0);
        calendarRight.set(Calendar.MILLISECOND, 0);

        // Seta as datas para dia(s) antes para a notificacao.
        int daysBefore = alarmVO.getDaysBefore() * (-1);
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
            Long[] daysToExpire = timeLensesDAO.getDaysToExpire(timeLensesVO);

            if (daysToExpire[0] > 0 || daysToExpire[1] > 0) {
                setAlarmManagerDaily(alarmVO.getHour(), alarmVO.getMinute());
            }
        }

        enableReceiverWhenBoot();
    }
/*
   public void setAlarm(String idLens) {
        cancelAlarm();
        cancelAlarmDaily();

        TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
        Calendar[] calendars = timeLensesDAO.getDateAlarm(idLens);

//        AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
//        AlarmVO alarmVO = alarmDAO.getAlarm();

        if (alarmVO == null) {
            alarmVO = new AlarmVO();
            alarmVO.setHour(0);
            alarmVO.setMinute(0);
            alarmVO.setDaysBefore(0);
        }

        Calendar calendarLeft = Calendar.getInstance();
        Calendar calendarRight = Calendar.getInstance();

        calendarLeft.set(Calendar.DAY_OF_MONTH,
                calendars[0].get(Calendar.DAY_OF_MONTH));
        calendarLeft.set(Calendar.MONTH, calendars[0].get(Calendar.MONTH));
        calendarLeft.set(Calendar.YEAR, calendars[0].get(Calendar.YEAR));
        calendarLeft.set(Calendar.HOUR_OF_DAY, alarmVO.getHour());
        calendarLeft.set(Calendar.MINUTE, alarmVO.getMinute());
        calendarLeft.set(Calendar.SECOND, 0);
        calendarLeft.set(Calendar.MILLISECOND, 0);

        calendarRight.set(Calendar.DAY_OF_MONTH,
                calendars[1].get(Calendar.DAY_OF_MONTH));
        calendarRight.set(Calendar.MONTH, calendars[1].get(Calendar.MONTH));
        calendarRight.set(Calendar.YEAR, calendars[1].get(Calendar.YEAR));
        calendarRight.set(Calendar.HOUR_OF_DAY, alarmVO.getHour());
        calendarRight.set(Calendar.MINUTE, alarmVO.getMinute());
        calendarRight.set(Calendar.SECOND, 0);
        calendarRight.set(Calendar.MILLISECOND, 0);

        // Seta as datas para dia(s) antes para a notificacao.
        int daysBefore = alarmVO.getDaysBefore() * (-1);
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
                setAlarmManagerDaily(alarmVO.getHour(), alarmVO.getMinute());
            }
        }

        enableReceiverWhenBoot();
    }
*/

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
        calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, 1);

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

    public void setAlarmNextDay(TimeLensesVO timeLensesVO) {
        TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);

//        TimeLensesVO timeLensesVO = timeLensesDAO.getLastLens();

        // Days to expire
        Long[] daysToExpire = timeLensesDAO.getDaysToExpire(timeLensesVO);

        Long daysToExpireLeft = daysToExpire[0];
        Long daysToExpireRight = daysToExpire[1];

        // Days before for notification
//        AlarmVO alarmVO = AlarmDAO.getInstance(context).getAlarm();
        int daysBefore = alarmVO.getDaysBefore();
        int hour = alarmVO.getHour();
        int minute = alarmVO.getMinute();

        // Next day
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, hour);
        today.set(Calendar.MINUTE, minute);
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
