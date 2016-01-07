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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
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
        lensVO.setDateCreate(new Date());
        ParseObject post = getParseObjectLens(lensVO);
        post.setACL(new ParseACL(ParseUser.getCurrentUser()));
        post.saveEventually();
        post.pinInBackground(tableName);

//        post.saveInBackground();
        if (Utility.isNetworkAvailable(context)) {
            try {
                post.save();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
        }
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
        try {
            ParseObject content = query.getFirst();
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
            content.saveEventually();
            content.pinInBackground(tableName);

            if (Utility.isNetworkAvailable(context)) {
                content.save();
            }

        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            public void done(ParseObject content, com.parse.ParseException e) {
//                if (e == null) {
//                    content.put("date_left", date_left);
//                    content.put("date_right", date_right);
//                    content.put("expiration_left", expiration_left);
//                    content.put("expiration_right", expiration_right);
//                    content.put("type_left", type_left);
//                    content.put("type_right", type_right);
//                    content.put("num_days_not_used_left", num_days_not_used_left);
//                    content.put("num_days_not_used_right", num_days_not_used_right);
//                    content.put("in_use_left", in_use_left);
//                    content.put("in_use_right", in_use_right);
//                    content.put("qtd_left", qtd_left);
//                    content.put("qtd_right", qtd_right);
//
//                    content.setACL(new ParseACL(ParseUser.getCurrentUser()));
////                    content.pinInBackground();
//                    content.saveEventually();
//
//                    content.saveInBackground();
//                }
//            }
//        });
    }

    private ParseQuery getParseQuery(String idLens) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);

        //se não estiver online, utiliza base local
        if (!Utility.isNetworkAvailable(context)) {
//            query.fromLocalDatastore();
            query.fromPin(tableName);
        }/* else {
            //Tira da lista offline
            ParseObject.unpinAllInBackground();
        }*/

        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereEqualTo("lens_id", idLens);

        return query;
    }

    private ParseQuery getParseQuery() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.orderByDescending("date_create");

        //se não estiver online, utiliza base local
        if (!Utility.isNetworkAvailable(context)) {
//            query.fromLocalDatastore();
            query.fromPin(tableName);
        }

        query.whereEqualTo("user_id", ParseUser.getCurrentUser());

        return query;
    }

    private ParseObject getParseObjectLens(TimeLensesVO lensVO) {
        ParseObject content = new ParseObject(tableName);

//        if (!Utility.isNetworkAvailable(context)) {
            content.put("lens_id", lensVO.getId().replace("OFFLINE", ""));
//        }

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
        content.put("date_create", lensVO.getDateCreate());

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
                        content.saveEventually();
                        content.pinInBackground(tableName);

                        content.saveInBackground();
                    }
                }
            });
        }

        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        ContentValues content = new ContentValues();
//        if (timeLensesVO.getInUseLeft() == 1) {
//            content.put("num_days_not_used_left",
//                    timeLensesVO.getNumDaysNotUsedLeft() + 1);
//        }
//        if (timeLensesVO.getInUseRight() == 1) {
//            content.put("num_days_not_used_right",
//                    timeLensesVO.getNumDaysNotUsedRight() + 1);
//        }
//        return db.update(tableName, content, "id=?", new String[]{timeLensesVO
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
                    content.saveEventually();
                    content.pinInBackground(tableName);

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
                    content.unpinInBackground(tableName);
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
                timeLensesVO = setTimeLensesVODAO(parseObj);
//                parseObj.saveEventually();
            }
//            ParseObject.pinAllInBackground(list);

        } catch (com.parse.ParseException e) {
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }


//        final TimeLensesVO[] timeLensesVO = {null};
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> postList, com.parse.ParseException e) {
//                if (e == null) {
//                    for (ParseObject parseObj : postList) {
//                        timeLensesVO[0] = setTimeLensesVODAO(parseObj);
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

    public List<TimeLensesVO> getListLenses() {
        ParseQuery query = getParseQuery();

        listTimeLensesVO = new LinkedList<>();
        try {
            List<ParseObject> list = query.find();
            for (ParseObject parseObj : list) {
                listTimeLensesVO.add(setTimeLensesVODAO(parseObj));
                parseObj.saveEventually();
            }
            ParseObject.unpinAllInBackground(tableName);
            ParseObject.pinAllInBackground(tableName, list);

        } catch (com.parse.ParseException e) {
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }

//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> postList, com.parse.ParseException e) {
//                if (e == null) {
//                    for (ParseObject parseObj : postList) {
//                        listVO.add(setTimeLensesVODAO(parseObj));
//
//                        parseObj.saveEventually();
//                    }
//                    ParseObject.pinAllInBackground(postList);
//                } else {
//                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
//                }
//            }
//        });

        return listTimeLensesVO;
    }

    public void syncTimeLenses() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.fromPin(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereContains("lens_id", "OFFLINE");

        try {
            List<ParseObject> list = query.find();
            for (ParseObject obj : list) {
                obj.put("lens_id", obj.getString("lens_id").replace("OFFLINE", ""));
                obj.setACL(new ParseACL(ParseUser.getCurrentUser()));
                obj.save();
            }
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    private TimeLensesVO setTimeLensesVODAO(ParseObject obj) {
        timeLensesVO = new TimeLensesVO();
        timeLensesVO.setObjectId(obj.getObjectId());
        timeLensesVO.setId(obj.getString("lens_id"));
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
            return parseObj.getString("lens_id");

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
            return setTimeLensesVODAO(parseObj);

        } catch (com.parse.ParseException e) {
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }
//        Cursor cursor = db.rawQuery("select * from " + tableName
//                + " order by id desc limit 1", null);
//
//        if (cursor.moveToFirst()) {
//            return setTimeLensesVODAO(cursor);
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

        TimeLensesVO timeLensesVO = getById(id);
        if (timeLensesVO != null) {
            int expirationLeft = timeLensesVO.getExpirationLeft();
            int expirationRight = timeLensesVO.getExpirationRight();
            int dayNotUsedLeft = timeLensesVO.getNumDaysNotUsedLeft();
            int dayNotUsedRight = timeLensesVO.getNumDaysNotUsedRight();

            try {
                if (timeLensesVO.getDateLeft() != null) {
                    if (timeLensesVO.getTypeLeft() == 0) {
                        totalDaysLeft = expirationLeft;
                    } else if (timeLensesVO.getTypeLeft() == 1) {
                        totalDaysLeft = expirationLeft * 30;
                    } else if (timeLensesVO.getTypeLeft() == 2) {
                        totalDaysLeft = expirationLeft * 365;
                    }
                    dateExpLeft.setTime(dateFormat.parse(timeLensesVO.getDateLeft()));
                    int totalLeft = totalDaysLeft + dayNotUsedLeft;
                    dateExpLeft.add(Calendar.DATE, totalLeft);
                }
                if (timeLensesVO.getDateRight() != null) {
                    if (timeLensesVO.getTypeRight() == 0) {
                        totalDaysRight = expirationRight;
                    } else if (timeLensesVO.getTypeRight() == 1) {
                        totalDaysRight = expirationRight * 30;
                    } else if (timeLensesVO.getTypeRight() == 2) {
                        totalDaysRight = expirationRight * 365;
                    }

                    dateExpRight
                            .setTime(dateFormat.parse(timeLensesVO.getDateRight()));
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

//        TimeLensesVO timeLensesVO = getById(id);
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

        String idLens = timeLensesVO.getObjectId();
        if (idLens != null && !idLens.contains("OFFLINE")) {
            if (!timeLensesVO.equals(timeLensesDAO.getById(idLens))) {
                timeLensesDAO.update(timeLensesVO);
//                alarmDAO.setAlarm(timeLensesVO);
            }
        } else {
            timeLensesDAO.insert(timeLensesVO);
//            alarmDAO.setAlarm(timeLensesVO);
        }
    }
}
