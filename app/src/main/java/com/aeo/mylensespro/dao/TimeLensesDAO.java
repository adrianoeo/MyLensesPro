package com.aeo.mylensespro.dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.aeo.mylensespro.util.Utility;
import com.aeo.mylensespro.vo.TimeLensesVO;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeLensesDAO {

    private static String tableName = "lens";
    private static TimeLensesDAO instance;
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";

    private Context context;

    public static TimeLensesVO timeLensesVO;
    public static List<TimeLensesVO> listTimeLensesVO;

    public static TimeLensesDAO getInstance(Context context) {
        if (instance == null) {
            instance = new TimeLensesDAO(context);
        }
        return instance;
    }

    public TimeLensesDAO(Context context) {
        this.context = context;
    }

    public void insert(TimeLensesVO lensVO) {
        ParseObject post = getParseObjectLens(lensVO);
        post.setACL(new ParseACL(ParseUser.getCurrentUser()));
        post.pinInBackground();
        post.saveEventually();

        post.saveInBackground();
    }

     public void update(TimeLensesVO lensVO) {

        final String date_left = Utility.formatDateToSqlite(lensVO.getDateLeft());
        final String date_right = Utility.formatDateToSqlite(lensVO.getDateRight());
        final int expiration_left = lensVO.getExpirationLeft();
        final int expiration_right = lensVO.getExpirationRight();
        final int type_left = lensVO.getTypeLeft();
        final int type_right = lensVO.getTypeRight();
        final int in_use_left = lensVO.getInUseLeft();
        final int in_use_right = lensVO.getInUseRight();
        final int qtd_left = lensVO.getQtdLeft();
        final int qtd_right = lensVO.getQtdRight();
        final int num_days_not_used_left = lensVO.getNumDaysNotUsedLeft();
        final int num_days_not_used_right = lensVO.getNumDaysNotUsedRight();

        ParseQuery<ParseObject> query = getParseQuery(lensVO.getId());

        // Retrieve the object by id
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject content, com.parse.ParseException e) {
                if (e == null) {
                    content.put("date_left", date_left);
                    content.put("date_right", date_right);
                    content.put("expiration_left", expiration_left);
                    content.put("expiration_right", expiration_right);
                    content.put("type_left", type_left);
                    content.put("type_right", type_right);
                    content.put("num_days_not_used_left", num_days_not_used_left);
                    content.put("num_days_not_used_right", num_days_not_used_right);
                    content.put("in_use_left", in_use_left);
                    content.put("in_use_right", in_use_right);
                    content.put("qtd_left", qtd_left);
                    content.put("qtd_right", qtd_right);

                    content.setACL(new ParseACL(ParseUser.getCurrentUser()));
                    content.pinInBackground();
                    content.saveEventually();

                    content.saveInBackground();
                }
            }
        });
    }

    private ParseQuery getParseQuery(String idLens) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);

        //se não estiver online, utiliza base local
        if (!Utility.isNetworkAvailable(context)) {
            query.fromLocalDatastore();
        }

        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereEqualTo("objectId", idLens);

        return query;
    }

    private ParseQuery getParseQuery() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.orderByDescending("createdAt");

        //se não estiver online, utiliza base local
        if (!Utility.isNetworkAvailable(context)) {
            query.fromLocalDatastore();
        }

        query.whereEqualTo("user_id", ParseUser.getCurrentUser());

        return query;
    }

    private ParseObject getParseObjectLens(TimeLensesVO lensVO) {
        ParseObject content = new ParseObject(tableName);
        content.put("user_id", ParseUser.getCurrentUser());
        content.put("date_left", Utility.formatDateToSqlite(lensVO.getDateLeft()));
        content.put("date_right", Utility.formatDateToSqlite(lensVO.getDateRight()));
        content.put("expiration_left", lensVO.getExpirationLeft());
        content.put("expiration_right", lensVO.getExpirationRight());
        content.put("type_left", lensVO.getTypeLeft());
        content.put("type_right", lensVO.getTypeRight());
        content.put("num_days_not_used_left", lensVO.getNumDaysNotUsedLeft());
        content.put("num_days_not_used_right", lensVO.getNumDaysNotUsedRight());
        content.put("in_use_left", lensVO.getInUseLeft());
        content.put("in_use_right", lensVO.getInUseRight());
        content.put("qtd_left", lensVO.getQtdLeft());
        content.put("qtd_right", lensVO.getQtdRight());

        return content;
    }

    public void incrementDaysNotUsed(TimeLensesVO lensVO) {

        if (lensVO.getInUseLeft() == 1 || lensVO.getInUseRight() == 1) {
            final int num_days_not_used_left = lensVO.getInUseLeft() == 1
                    ? lensVO.getNumDaysNotUsedLeft() + 1 : lensVO.getNumDaysNotUsedLeft();
            final int num_days_not_used_right = lensVO.getInUseRight() == 1
                    ? lensVO.getNumDaysNotUsedRight() + 1 : lensVO.getNumDaysNotUsedRight();

            ParseQuery<ParseObject> query = getParseQuery(lensVO.getId());

            // Retrieve the object by id
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject content, com.parse.ParseException e) {
                    if (e == null) {
                        content.put("num_days_not_used_left", num_days_not_used_left);
                        content.put("num_days_not_used_right", num_days_not_used_right);

                        content.setACL(new ParseACL(ParseUser.getCurrentUser()));
                        content.pinInBackground();
                        content.saveEventually();

                        content.saveInBackground();
                    }
                }
            });
        }

        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        ContentValues content = new ContentValues();
//        if (lensVO.getInUseLeft() == 1) {
//            content.put("num_days_not_used_left",
//                    lensVO.getNumDaysNotUsedLeft() + 1);
//        }
//        if (lensVO.getInUseRight() == 1) {
//            content.put("num_days_not_used_right",
//                    lensVO.getNumDaysNotUsedRight() + 1);
//        }
//        return db.update(tableName, content, "id=?", new String[]{lensVO
//                .getId().toString()}) > 0;
    }

    public void updateDaysNotUsed(final int days, final String side, String idLens) {
        ParseQuery<ParseObject> query = getParseQuery(idLens);

        // Retrieve the object by id
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject content, com.parse.ParseException e) {
                if (e == null) {

                    if (LEFT.equals(side)) {
                        content.put("num_days_not_used_left", days);
                    } else {
                        content.put("num_days_not_used_right", days);
                    }

                    content.setACL(new ParseACL(ParseUser.getCurrentUser()));
                    content.pinInBackground();
                    content.saveEventually();

                    content.saveInBackground();
                }
            }
        });

//        synchronized (MainActivity.sDataLock) {
//            ContentValues content = new ContentValues();
//            if (LEFT.equals(side)) {
//                content.put("num_days_not_used_left", days);
//            } else {
//                content.put("num_days_not_used_right", days);
//            }
//
//            return db.update(tableName, content, "id=?",
//                    new String[]{String.valueOf(idLens)}) > 0;
//        }
    }

    public void delete(String id) {
        ParseQuery<ParseObject> query = getParseQuery(id);

        // Retrieve the object by id
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject content, com.parse.ParseException e) {
                if (e == null) {
                    content.deleteInBackground();
                }
            }
        });


    }

    public TimeLensesVO getById(String id) {
        ParseQuery<ParseObject> query = getParseQuery(id);
        TimeLensesVO timeLensesVO = null;

        try {
            List<ParseObject> list = query.find();
            for (ParseObject parseObj : list) {
                timeLensesVO = setTimeLensesVO(parseObj);
                parseObj.saveEventually();
            }
            ParseObject.pinAllInBackground(list);

        } catch (com.parse.ParseException e) {
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }


//        final TimeLensesVO[] timeLensesVO = {null};
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> postList, com.parse.ParseException e) {
//                if (e == null) {
//                    for (ParseObject parseObj : postList) {
//                        timeLensesVO[0] = setTimeLensesVO(parseObj);
//
//                        parseObj.saveEventually();
//                    }
//                    ParseObject.pinAllInBackground(postList);
//                } else {
//                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
//                }
//            }
//        });

        return timeLensesVO;
    }

    public List<TimeLensesVO> getListLens() {
//        List<TimeLensesVO> listVO = new ArrayList<>();
        ParseQuery query = getParseQuery();

        listTimeLensesVO = new ArrayList<>();
        try {
            List<ParseObject> list = query.find();
            for (ParseObject parseObj : list) {
                listTimeLensesVO.add(setTimeLensesVO(parseObj));
                parseObj.saveEventually();
            }
            ParseObject.pinAllInBackground(list);

        } catch (com.parse.ParseException e) {
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }

//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> postList, com.parse.ParseException e) {
//                if (e == null) {
//                    for (ParseObject parseObj : postList) {
//                        listVO.add(setTimeLensesVO(parseObj));
//
//                        parseObj.saveEventually();
//                    }
//                    ParseObject.pinAllInBackground(postList);
//                } else {
//                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
//                }
//            }
//        });


//        Cursor cursor = db.query(tableName, columns, null, null, null, null,
//                "id desc");
//
//        List<TimeLensesVO> listVO = new ArrayList<TimeLensesVO>();
//
//        while (cursor.moveToNext()) {
//            listVO.add(setTimeLensesVO(cursor));
//        }
        return listTimeLensesVO;
    }

    private TimeLensesVO setTimeLensesVO(ParseObject obj) {
        timeLensesVO = new TimeLensesVO();
        timeLensesVO.setId(obj.getObjectId());
        timeLensesVO.setDateLeft(Utility.formatDateDefault(obj.getString("date_left")));
        timeLensesVO.setDateRight(Utility.formatDateDefault(obj.getString("date_right")));
        timeLensesVO.setExpirationLeft(obj.getInt("expiration_left"));
        timeLensesVO.setExpirationRight(obj.getInt("expiration_right"));
        timeLensesVO.setTypeLeft(obj.getInt("type_left"));
        timeLensesVO.setTypeRight(obj.getInt("type_right"));
        timeLensesVO.setInUseLeft(obj.getInt("in_use_left"));
        timeLensesVO.setInUseRight(obj.getInt("in_use_right"));
        timeLensesVO.setNumDaysNotUsedLeft(obj.getInt("num_days_not_used_left"));
        timeLensesVO.setNumDaysNotUsedRight(obj.getInt("num_days_not_used_right"));
        timeLensesVO.setQtdLeft(obj.getInt("qtd_left"));
        timeLensesVO.setQtdRight(obj.getInt("qtd_right"));

        return timeLensesVO;
    }

    public String getLastIdLens() {
        ParseQuery<ParseObject> query = getParseQuery();

        try {
            ParseObject parseObj = query.getFirst();
            return parseObj.getObjectId();

        } catch (com.parse.ParseException e) {
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }

//        Cursor cursor = db.rawQuery("select max(id) from " + tableName, null);
//
//        if (cursor.moveToFirst()) {
//            return cursor.getInt(0);
//        }
        return null;
    }

    public TimeLensesVO getLastLens() {
        ParseQuery<ParseObject> query = getParseQuery();

        try {
            ParseObject parseObj = query.getFirst();
            return setTimeLensesVO(parseObj);

        } catch (com.parse.ParseException e) {
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }
//        Cursor cursor = db.rawQuery("select * from " + tableName
//                + " order by id desc limit 1", null);
//
//        if (cursor.moveToFirst()) {
//            return setTimeLensesVO(cursor);
//        }
//
        return null;
    }

/*
    @SuppressLint("SimpleDateFormat")
    public Long[] getDaysToExpire(String idLenses) {
        long daysExpLeft = 0;
        long daysExpRight = 0;

        Calendar[] calendars = getDateAlarm(idLenses);

        Calendar dateExpLeft = Calendar.getInstance();
        Calendar dateExpRight = Calendar.getInstance();

        dateExpLeft.setTime(calendars[0].getTime());
        dateExpRight.setTime(calendars[1].getTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date dateReplaceLeft = null;
        Date dateReplaceRight = null;
        Date dateToday = null;

        try {
            dateReplaceLeft = dateFormat.parse(new StringBuilder()
                    .append(dateExpLeft.get(Calendar.DAY_OF_MONTH))
                    .append("/")
                    .append(String.format("%02d",
                            (dateExpLeft.get(Calendar.MONTH) + 1))).append("/")
                    .append(dateExpLeft.get(Calendar.YEAR)).toString());

            dateReplaceRight = dateFormat.parse(new StringBuilder()
                    .append(dateExpRight.get(Calendar.DAY_OF_MONTH))
                    .append("/")
                    .append(String.format("%02d",
                            (dateExpRight.get(Calendar.MONTH) + 1)))
                    .append("/").append(dateExpRight.get(Calendar.YEAR))
                    .toString());

            dateToday = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long dayLeft = dateReplaceLeft.getTime();
        long dayRight = dateReplaceRight.getTime();
        long dayToday = dateToday.getTime();
        long index = (24 * 60 * 60 * 1000);

        daysExpLeft = (dayLeft - dayToday) / index;
        daysExpRight = (dayRight - dayToday) / index;

        return new Long[]{daysExpLeft, daysExpRight};
    }
*/

    @SuppressLint("SimpleDateFormat")
    public Long[] getDaysToExpire(TimeLensesVO timeLensesVO) {
        long daysExpLeft = 0;
        long daysExpRight = 0;

        Calendar[] calendars = getDateAlarm(timeLensesVO);

        Calendar dateExpLeft = Calendar.getInstance();
        Calendar dateExpRight = Calendar.getInstance();

        dateExpLeft.setTime(calendars[0].getTime());
        dateExpRight.setTime(calendars[1].getTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date dateReplaceLeft = null;
        Date dateReplaceRight = null;
        Date dateToday = null;

        try {
            dateReplaceLeft = dateFormat.parse(new StringBuilder()
                    .append(dateExpLeft.get(Calendar.DAY_OF_MONTH))
                    .append("/")
                    .append(String.format("%02d",
                            (dateExpLeft.get(Calendar.MONTH) + 1))).append("/")
                    .append(dateExpLeft.get(Calendar.YEAR)).toString());

            dateReplaceRight = dateFormat.parse(new StringBuilder()
                    .append(dateExpRight.get(Calendar.DAY_OF_MONTH))
                    .append("/")
                    .append(String.format("%02d",
                            (dateExpRight.get(Calendar.MONTH) + 1)))
                    .append("/").append(dateExpRight.get(Calendar.YEAR))
                    .toString());

            dateToday = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long dayLeft = dateReplaceLeft.getTime();
        long dayRight = dateReplaceRight.getTime();
        long dayToday = dateToday.getTime();
        long index = (24 * 60 * 60 * 1000);

        daysExpLeft = (dayLeft - dayToday) / index;
        daysExpRight = (dayRight - dayToday) / index;

        return new Long[]{daysExpLeft, daysExpRight};
    }

 /*   @SuppressLint("SimpleDateFormat")
    public Calendar[] getDateAlarm(String id) {
        Calendar dateExpLeft = Calendar.getInstance();
        Calendar dateExpRight = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        int totalDaysLeft = 0;
        int totalDaysRight = 0;

        TimeLensesVO lensVO = getById(id);
        if (lensVO != null) {
            int expirationLeft = lensVO.getExpirationLeft();
            int expirationRight = lensVO.getExpirationRight();
            int dayNotUsedLeft = lensVO.getNumDaysNotUsedLeft();
            int dayNotUsedRight = lensVO.getNumDaysNotUsedRight();

            try {
                if (lensVO.getDateLeft() != null) {
                    if (lensVO.getTypeLeft() == 0) {
                        totalDaysLeft = expirationLeft;
                    } else if (lensVO.getTypeLeft() == 1) {
                        totalDaysLeft = expirationLeft * 30;
                    } else if (lensVO.getTypeLeft() == 2) {
                        totalDaysLeft = expirationLeft * 365;
                    }
                    dateExpLeft.setTime(dateFormat.parse(lensVO.getDateLeft()));
                    int totalLeft = totalDaysLeft + dayNotUsedLeft;
                    dateExpLeft.add(Calendar.DATE, totalLeft);
                }
                if (lensVO.getDateRight() != null) {
                    if (lensVO.getTypeRight() == 0) {
                        totalDaysRight = expirationRight;
                    } else if (lensVO.getTypeRight() == 1) {
                        totalDaysRight = expirationRight * 30;
                    } else if (lensVO.getTypeRight() == 2) {
                        totalDaysRight = expirationRight * 365;
                    }

                    dateExpRight
                            .setTime(dateFormat.parse(lensVO.getDateRight()));
                    int totalRight = totalDaysRight + dayNotUsedRight;
                    dateExpRight.add(Calendar.DATE, totalRight);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return new Calendar[]{dateExpLeft, dateExpRight};
    }*/

   @SuppressLint("SimpleDateFormat")
    public Calendar[] getDateAlarm(TimeLensesVO lensVO) {
        Calendar dateExpLeft = Calendar.getInstance();
        Calendar dateExpRight = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        int totalDaysLeft = 0;
        int totalDaysRight = 0;

//        TimeLensesVO lensVO = getById(id);
        if (lensVO != null) {
            int expirationLeft = lensVO.getExpirationLeft();
            int expirationRight = lensVO.getExpirationRight();
            int dayNotUsedLeft = lensVO.getNumDaysNotUsedLeft();
            int dayNotUsedRight = lensVO.getNumDaysNotUsedRight();

            try {
                if (lensVO.getDateLeft() != null) {
                    if (lensVO.getTypeLeft() == 0) {
                        totalDaysLeft = expirationLeft;
                    } else if (lensVO.getTypeLeft() == 1) {
                        totalDaysLeft = expirationLeft * 30;
                    } else if (lensVO.getTypeLeft() == 2) {
                        totalDaysLeft = expirationLeft * 365;
                    }
                    dateExpLeft.setTime(dateFormat.parse(lensVO.getDateLeft()));
                    int totalLeft = totalDaysLeft + dayNotUsedLeft;
                    dateExpLeft.add(Calendar.DATE, totalLeft);
                }
                if (lensVO.getDateRight() != null) {
                    if (lensVO.getTypeRight() == 0) {
                        totalDaysRight = expirationRight;
                    } else if (lensVO.getTypeRight() == 1) {
                        totalDaysRight = expirationRight * 30;
                    } else if (lensVO.getTypeRight() == 2) {
                        totalDaysRight = expirationRight * 365;
                    }

                    dateExpRight
                            .setTime(dateFormat.parse(lensVO.getDateRight()));
                    int totalRight = totalDaysRight + dayNotUsedRight;
                    dateExpRight.add(Calendar.DATE, totalRight);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return new Calendar[]{dateExpLeft, dateExpRight};
    }

    /*public int[] getUnitsRemaining() {
        int unitsLeft = ListReplaceLensFragment.listLenses == null ? TimeLensesDAO.getInstance(
                context).getLastLens().getQtdLeft() : ListReplaceLensFragment.listLenses
                .get(0).getQtdLeft();

        int unitsRight = ListReplaceLensFragment.listLenses == null ? TimeLensesDAO.getInstance(
                context).getLastLens().getQtdRight() : ListReplaceLensFragment.listLenses
                .get(0).getQtdRight();

        return new int[]{unitsLeft, unitsRight};
    }
    */
    public int[] getUnitsRemaining(TimeLensesVO timeLensesVO) {
        int unitsLeft = timeLensesVO.getQtdLeft();

        int unitsRight = timeLensesVO.getQtdRight();

        return new int[]{unitsLeft, unitsRight};
    }

    @SuppressLint("SimpleDateFormat")
    public void save(TimeLensesVO timeLensesVO) {
        TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
		AlarmDAO alarmDAO = AlarmDAO.getInstance(context);

        String idLens = timeLensesVO.getId();
        if (idLens != null) {
            if (!timeLensesVO.equals(timeLensesDAO.getById(idLens))) {
                timeLensesDAO.update(timeLensesVO);
                alarmDAO.setAlarm(timeLensesVO);
            }
        } else {
            timeLensesDAO.insert(timeLensesVO);
            alarmDAO.setAlarm(timeLensesVO);
        }
    }
}
