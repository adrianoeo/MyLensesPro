package com.aeo.mylensespro.dao;

import android.content.Context;
import android.util.Log;

import com.aeo.mylensespro.util.Utility;
import com.aeo.mylensespro.vo.DataLensesVO;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class DataLensesDAO {

    private static String tableName = "data_lens";
    private static DataLensesDAO instance;
    private Context context;

//    public static DataLensesVO dataLensesVO;

    public static DataLensesDAO getInstance(Context context) {
        if (instance == null) {
            return new DataLensesDAO(context);
        }
        return instance;
    }

    public DataLensesDAO(Context context) {
        this.context = context;
    }

    public void insert(DataLensesVO vo) {
        boolean isOffline = !Utility.isNetworkAvailable(context);

        ParseObject parseObject = getParseObjectDataLens(vo, isOffline);
        parseObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
        parseObject.saveEventually();
        try {
            parseObject.unpin(tableName);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        parseObject.pinInBackground(tableName);

        if (!isOffline) {
            try {
                parseObject.save();
            } catch (com.parse.ParseException e) {
                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
            }
        }
    }

    public void update(DataLensesVO vo) {
        ParseQuery<ParseObject> query = getParseQuery(vo.getId());

        // Retrieve the object by id
        try {
            boolean isOffline = !Utility.isNetworkAvailable(context);
            ParseObject parseObject = query.getFirst();

            parseObject = setParseObject(parseObject, vo, isOffline);

            parseObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
            parseObject.saveEventually();
            parseObject.unpin(tableName);
            parseObject.pinInBackground(tableName);

            if (!isOffline) {
                parseObject.save();
            }

        } catch (com.parse.ParseException e) {
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }

    }

    private ParseQuery getParseQuery() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.orderByDescending("createdAt");

        //se não estiver online, utiliza base local
        if (!Utility.isNetworkAvailable(context)) {
            query.fromPin(tableName);
        }

        query.whereEqualTo("user_id", ParseUser.getCurrentUser());

        return query;
    }

    private ParseQuery getParseQuery(String id) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);

        //se não estiver online, utiliza base local
        if (!Utility.isNetworkAvailable(context)) {
            query.fromPin(tableName);
        }

        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereEqualTo("data_id", id);
        return query;
    }

    public DataLensesVO getLastDataLenses() {
        ParseQuery<ParseObject> query = getParseQuery();

        try {
            ParseObject parseObject = query.getFirst();

            if (parseObject != null) {
                parseObject.saveEventually();
                ParseObject.unpinAll(tableName);
                parseObject.pinInBackground(tableName);
                return setDataLensesVO(parseObject);
            }
        } catch (ParseException e) {
            try {
                ParseObject.unpinAll(tableName);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
        }
        return null;
    }

    public void getLastDataLensesAsync() {
        ParseQuery<ParseObject> query = getParseQuery();

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
//                dataLensesVO = setDataLensesVO(object);
            }
        });
    }

    private DataLensesVO setDataLensesVO(ParseObject obj) {
        DataLensesVO vo = null;
        if (obj != null) {
            vo = new DataLensesVO();
            vo.setId(obj.getString("data_id"));
            vo.setObjectId(obj.getObjectId());
            vo.setDescriptionLeft(obj.getString("description_left"));
            vo.setBrandLeft(obj.getString("brand_left"));
            vo.setTypeLeft(obj.getInt("type_left"));
            vo.setPowerLeft(obj.getInt("power_left"));
            vo.setCylinderLeft(obj.getInt("cylinder_left"));
            vo.setAxisLeft(obj.getInt("axis_left"));
            vo.setAddLeft(obj.getInt("add_left"));
            vo.setBuySiteLeft(obj.getString("buy_site_left"));
            vo.setDescriptionRight(obj.getString("description_right"));
            vo.setBrandRight(obj.getString("brand_right"));
            vo.setTypeRight(obj.getInt("type_right"));
            vo.setPowerRight(obj.getInt("power_right"));
            vo.setCylinderRight(obj.getInt("cylinder_right"));
            vo.setAxisRight(obj.getInt("axis_right"));
            vo.setAddRight(obj.getInt("add_right"));
            vo.setBuySiteRight(obj.getString("buy_site_right"));
            vo.setBcLeft(obj.getDouble("bc_left"));
            vo.setBcRight(obj.getDouble("bc_right"));
            vo.setDiaLeft(obj.getDouble("dia_left"));
            vo.setDiaRight(obj.getDouble("dia_right"));
        }
        return vo;
    }

    private ParseObject getParseObjectDataLens(DataLensesVO vo, boolean isOffline) {
        ParseObject parseObject = new ParseObject(tableName);

        return setParseObject(parseObject, vo, isOffline);
    }

    private ParseObject setParseObject(ParseObject parseObject, DataLensesVO vo, boolean isOffline) {

        if (isOffline) {
            parseObject.put("data_id", vo.getId());
        } else {
            parseObject.put("data_id", vo.getId().replace("OFFLINE", ""));
        }

        parseObject.put("user_id", ParseUser.getCurrentUser());
        parseObject.put("description_left", vo.getDescriptionLeft().trim());
        parseObject.put("brand_left", vo.getBrandLeft().trim());
        parseObject.put("type_left", vo.getTypeLeft());
        parseObject.put("power_left", vo.getPowerLeft());

        if (vo.getCylinderLeft() != null) {
            parseObject.put("cylinder_left", vo.getCylinderLeft());
        }
        if (vo.getAxisLeft() != null) {
            parseObject.put("axis_left", vo.getAxisLeft());
        }

        if (vo.getAddLeft() != null) {
            parseObject.put("add_left", vo.getAddLeft());
        }
        parseObject.put("buy_site_left", vo.getBuySiteLeft().trim());
        parseObject.put("description_right", vo.getDescriptionRight().trim());
        parseObject.put("brand_right", vo.getBrandRight().trim());
        parseObject.put("type_right", vo.getTypeRight());
        parseObject.put("power_right", vo.getPowerRight());
        parseObject.put("buy_site_right", vo.getBuySiteRight().trim());

        if (vo.getCylinderRight() != null) {
            parseObject.put("cylinder_right", vo.getCylinderRight());
        }
        if (vo.getAxisRight() != null) {
            parseObject.put("axis_right", vo.getAxisRight());
        }
        if (vo.getAddRight() != null) {
            parseObject.put("add_right", vo.getAddRight());
        }


        if (vo.getBcLeft() != null) {
            parseObject.put("bc_left", vo.getBcLeft());
        }
        if (vo.getBcRight() != null) {
            parseObject.put("bc_right", vo.getBcRight());
        }
        if (vo.getDiaLeft() != null) {
            parseObject.put("dia_left", vo.getDiaLeft());
        }
        if (vo.getDiaRight() != null) {
            parseObject.put("dia_right", vo.getDiaRight());
        }
        return parseObject;
    }

    public boolean syncDataLenses() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.fromPin(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
//        query.whereContains("data_id", "OFFLINE");

        try {
            ParseObject parseObject = query.getFirst();
            if (parseObject != null) {
                if (Utility.isNetworkAvailable(context)) {
                    parseObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
                    parseObject.save();
                    parseObject.unpin(tableName);
                    return true;
                }
            }
        } catch (com.parse.ParseException e) {
            return false;
        }
        return false;
    }
}
