package com.aeo.mylensespro.dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.aeo.mylensespro.R;
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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TimeLensesDAO {

    public static String tableName = "lens";
    private static TimeLensesDAO instance;
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";
    public static final String BOTH = "BOTH";

    private Context context;

    public static TimeLensesVO timeLensesVO;
    public static List<TimeLensesVO> listTimeLensesVO;

    private List<ParseObject> listDeletedParseObj;

    public static TimeLensesDAO getInstance(Context context) {
        if (instance == null) {
            instance = new TimeLensesDAO(context);
        }
        return instance;
    }

    public TimeLensesDAO(Context context) {
        this.context = context;

        if (listDeletedParseObj == null) {
            listDeletedParseObj = new ArrayList<>();
        }
    }

    private ParseQuery getParseQuery(String idLens) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);

        boolean isConnectionFast = Utility.isConnectionFast(context);
        boolean isFromPinNull = isFromPinNull(idLens);
        boolean isNetworkAvailable = Utility.isNetworkAvailable(context);

        //se não estiver online, utiliza base local
        if ((!isConnectionFast && !isFromPinNull) || !isNetworkAvailable) {
            query.fromPin(tableName);
        }

        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereEqualTo("lens_id", idLens);

        return query;
    }

    private ParseQuery getParseQuery() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.orderByDescending("date_create");

        boolean isConnectionFast = Utility.isConnectionFast(context);
        boolean isFromPinNull = isFromPinNull(null);
        boolean isNetworkAvailable = Utility.isNetworkAvailable(context);

        //Local
        if ((!isConnectionFast && !isFromPinNull) || !isNetworkAvailable) {
            query.fromPin(tableName);
        }

        query.whereEqualTo("user_id", ParseUser.getCurrentUser());

        return query;
    }

    private boolean isFromPinNull(String idLens) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.orderByDescending("date_create");
        query.fromPin(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        if (idLens != null) {
            query.whereEqualTo("lens_id", idLens);
        }

        try {
            return query.count() <= 0;
        } catch (com.parse.ParseException e) {
            return true;
        }
    }

    private ParseObject getParseObjectLens(TimeLensesVO lensVO, boolean isOffline) {
        ParseObject content = new ParseObject(tableName);

//        if (isOffline) {
//            content.put("lens_id", lensVO.getId());
//        } else {
//            content.put("lens_id", lensVO.getId().replace("OFFLINE", ""));
//        }
        content.put("lens_id", lensVO.getId());

        content.put("user_id", ParseUser.getCurrentUser());
        content.put("date_left", Utility.formatDateToSqlite(lensVO.getDateLeft(), context));
        content.put("date_right", Utility.formatDateToSqlite(lensVO.getDateRight(), context));
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

                        if (/*Utility.isNetworkAvailable(context)
                                && */Utility.isConnectionFast(context)) {
                            content.saveInBackground();
                        }
                    }
                }
            });
        }

    }

    public void updateDaysNotUsed(final int days, final String side, String idLens) {
        ParseQuery<ParseObject> query = getParseQuery(idLens);

        // Retrieve the object by id
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject content, com.parse.ParseException e) {
                if (e == null) {

                    if (LEFT.equals(side)) {
                        content.put("num_days_not_used_left", days);
                    } else if (RIGHT.equals(side)) {
                        content.put("num_days_not_used_right", days);
                    } else {
                        content.put("num_days_not_used_left", days);
                        content.put("num_days_not_used_right", days);
                    }

                    content.setACL(new ParseACL(ParseUser.getCurrentUser()));
                    content.saveEventually();
                    content.pinInBackground(tableName);

                    if (/*Utility.isNetworkAvailable(context) &&*/ Utility.isConnectionFast(context)) {
                        content.saveInBackground();
                    }
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
            }

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
            if (list != null && list.size() > 0) {
                for (ParseObject parseObj : list) {
                    listTimeLensesVO.add(setTimeLensesVODAO(parseObj));
                    parseObj.saveEventually();
                }
                ParseObject.unpinAllInBackground(tableName);
                ParseObject.pinAllInBackground(tableName, list);
            }
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

    public List<TimeLensesVO> getListLensesLimit(List<TimeLensesVO> listTimeLensesVO) {
        ParseQuery query = getParseQuery();

        query.setLimit(10);

        int size = listTimeLensesVO == null ? 0 : listTimeLensesVO.size();
        query.setSkip(size);

        try {
            List<ParseObject> list = query.find();
            if (list != null && list.size() > 0) {
                for (ParseObject parseObj : list) {
                    listTimeLensesVO.add(setTimeLensesVODAO(parseObj));
                    parseObj.saveEventually();
                }
                ParseObject.unpinAllInBackground(tableName);
                ParseObject.pinAllInBackground(tableName, list);
            }
        } catch (com.parse.ParseException e) {
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }

        return listTimeLensesVO;
    }

    public void syncTimeLenses() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.fromPin(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
//        query.whereContains("lens_id", "OFFLINE");

        try {
            List<ParseObject> list = query.find();
            for (ParseObject obj : list) {
//                obj.put("lens_id", obj.getString("lens_id").replace("OFFLINE", ""));
                obj.put("lens_id", obj.getString("lens_id"));
                obj.setACL(new ParseACL(ParseUser.getCurrentUser()));
                obj.save();
            }

            for (ParseObject obj : listDeletedParseObj) {
                obj.delete();
            }

            listDeletedParseObj.clear();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    private TimeLensesVO setTimeLensesVODAO(ParseObject obj) {
        timeLensesVO = new TimeLensesVO();
        timeLensesVO.setObjectId(obj.getObjectId());
        timeLensesVO.setId(obj.getString("lens_id"));
        timeLensesVO.setDateLeft(Utility.formatDateDefault(obj.getString("date_left"), context));
        timeLensesVO.setDateRight(Utility.formatDateDefault(obj.getString("date_right"), context));
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
        return null;
    }

    public TimeLensesVO getLastLens() {
        ParseQuery<ParseObject> query = getParseQuery();

        try {
            ParseObject parseObj = query.getFirst();
            parseObj.pinInBackground(tableName);
            return setTimeLensesVODAO(parseObj);

        } catch (com.parse.ParseException e) {
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public Calendar[] getDatesToExpire(TimeLensesVO timeLensesVO) {
        Calendar[] calendars = getDateAlarm(timeLensesVO);

        Calendar dateExpLeft = Calendar.getInstance();
        Calendar dateExpRight = Calendar.getInstance();

        dateExpLeft.setTime(calendars[0].getTime());
        dateExpRight.setTime(calendars[1].getTime());

        return new Calendar[]{dateExpLeft, dateExpRight};
    }

    @SuppressLint("SimpleDateFormat")
    public Long[] getDaysToExpire(TimeLensesVO timeLensesVO) {
        long daysExpLeft = 0;
        long daysExpRight = 0;

        Calendar[] calendars = getDateAlarm(timeLensesVO);
        Calendar dateExpLeft = Calendar.getInstance();
        Calendar dateExpRight = Calendar.getInstance();

        dateExpLeft.setTime(calendars[0].getTime());
        dateExpRight.setTime(calendars[1].getTime());

        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.DAY_OF_MONTH, calendarToday.get(Calendar.DAY_OF_MONTH));
        calendarToday.set(Calendar.MONTH, calendarToday.get(Calendar.MONTH));
        calendarToday.set(Calendar.YEAR, calendarToday.get(Calendar.YEAR));
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.MILLISECOND, 0);

        daysExpLeft = dateExpLeft.getTimeInMillis() - calendarToday.getTimeInMillis();
        daysExpRight = dateExpRight.getTimeInMillis() - calendarToday.getTimeInMillis();

        return new Long[]{TimeUnit.DAYS.convert(daysExpLeft, TimeUnit.MILLISECONDS),
                TimeUnit.DAYS.convert(daysExpRight, TimeUnit.MILLISECONDS)};
    }

    public Long[] getDaysToExpire(Calendar dateExpLeft, Calendar dateExpRight) {
        long daysExpLeft = 0;
        long daysExpRight = 0;

        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.DAY_OF_MONTH, calendarToday.get(Calendar.DAY_OF_MONTH));
        calendarToday.set(Calendar.MONTH, calendarToday.get(Calendar.MONTH));
        calendarToday.set(Calendar.YEAR, calendarToday.get(Calendar.YEAR));
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.MILLISECOND, 0);

        daysExpLeft = dateExpLeft.getTimeInMillis() - calendarToday.getTimeInMillis();
        daysExpRight = dateExpRight.getTimeInMillis() - calendarToday.getTimeInMillis();

        return new Long[]{TimeUnit.DAYS.convert(daysExpLeft, TimeUnit.MILLISECONDS),
                TimeUnit.DAYS.convert(daysExpRight, TimeUnit.MILLISECONDS)};
    }

    @SuppressLint("SimpleDateFormat")
    public Calendar[] getDateAlarm(TimeLensesVO lensVO) {
        Calendar dateExpLeft = Calendar.getInstance();
        Calendar dateExpRight = Calendar.getInstance();

        String format = context.getResources().getString(R.string.locale);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        int totalDaysLeft = 0;
        int totalDaysRight = 0;

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

                    dateExpRight.setTime(dateFormat.parse(lensVO.getDateRight()));
                    int totalRight = totalDaysRight + dayNotUsedRight;
                    dateExpRight.add(Calendar.DATE, totalRight);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return new Calendar[]{dateExpLeft, dateExpRight};
    }

    public int[] getUnitsRemaining(TimeLensesVO timeLensesVO) {
        int unitsLeft = timeLensesVO.getQtdLeft();

        int unitsRight = timeLensesVO.getQtdRight();

        return new int[]{unitsLeft, unitsRight};
    }

    @SuppressLint("SimpleDateFormat")
    public void save(TimeLensesVO timeLensesVO) {
        TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);

        String idLens = timeLensesVO.getId();
        String objectId = timeLensesVO.getObjectId();

        if ((idLens == null || "".equals(idLens)) && (objectId == null || "".equals(objectId))) {
            timeLensesVO.setId(UUID.randomUUID().toString());
            timeLensesDAO.insert(timeLensesVO);
        } else {
            timeLensesDAO.update(timeLensesVO);
        }
/*
        String idLens = timeLensesVO.getObjectId();
        if (idLens != null && !idLens.contains("OFFLINE")) {
            if (!timeLensesVO.equals(timeLensesDAO.getById(idLens))) {
                timeLensesDAO.update(timeLensesVO);
            }
        } else {
            timeLensesDAO.insert(timeLensesVO);
        }
*/
    }

    public void insert(TimeLensesVO lensVO) {
        boolean isOffline = /*!Utility.isNetworkAvailable(context)
                ||*/ !Utility.isConnectionFast(context);

        lensVO.setDateCreate(new Date());

        ParseObject parseObject = getParseObjectLens(lensVO, isOffline);
        parseObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
        parseObject.saveEventually();
        parseObject.pinInBackground(tableName);

        if (!isOffline) {
            try {
                parseObject.save();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(TimeLensesVO lensVO) {

        final String date_left = Utility.formatDateToSqlite(lensVO.getDateLeft(), context);
        final String date_right = Utility.formatDateToSqlite(lensVO.getDateRight(), context);
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

            if (/*Utility.isNetworkAvailable(context) &&*/ Utility.isConnectionFast(context)) {
                content.save();
            }

        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    public void delete(String id) {
        ParseQuery<ParseObject> query = getParseQuery(id);

        try {
            ParseObject parseObject = query.getFirst();
            parseObject.unpin(tableName);
            parseObject.deleteEventually();

            listDeletedParseObj.add(parseObject);

            if (/*Utility.isNetworkAvailable(context) &&*/ Utility.isConnectionFast(context)) {
                parseObject.delete();
            }

        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

}
