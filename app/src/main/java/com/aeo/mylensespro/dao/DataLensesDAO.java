package com.aeo.mylensespro.dao;

import android.annotation.SuppressLint;
import android.content.Context;

import com.aeo.mylensespro.util.Utility;
import com.aeo.mylensespro.vo.DataLensesVO;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class DataLensesDAO {

    private static String tableName = "data_lens";
    private static DataLensesDAO instance;
    private Context context;

    public static DataLensesVO dataLensesVO;

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
        ParseObject post = getParseObjectDataLens(vo);
        post.setACL(new ParseACL(ParseUser.getCurrentUser()));
        post.saveEventually();
        post.pinInBackground(tableName);

        if (Utility.isNetworkAvailable(context)) {
            try {
                post.save();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(DataLensesVO vo) {
        ParseQuery<ParseObject> query = getParseQuery(vo.getId());

        // Retrieve the object by id
        try {
            ParseObject content = query.getFirst();

            content = setParseObject(content, vo);

            content.setACL(new ParseACL(ParseUser.getCurrentUser()));
            content.saveEventually();
            content.pinInBackground(tableName);

            if (Utility.isNetworkAvailable(context)) {
                content.save();
            }

        } catch (com.parse.ParseException e) {
            e.printStackTrace();
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
        query.whereEqualTo("objectId", id);
        return query;
    }

/*
    private ContentValues getContentValues(DataLensesVO vo) {
        ContentValues content = new ContentValues();
        content.put("description_left", vo.getDescriptionLeft().trim());
        content.put("brand_left", vo.getBrandLeft().trim());
        content.put("discard_type_left", vo.getDiscardTypeLeft());
        content.put("type_left", vo.getTypeLeft());
        content.put("power_left", vo.getPowerLeft());
        content.put("cylinder_left", vo.getCylinderLeft());
        content.put("axis_left", vo.getAxisLeft());
        content.put("add_left", vo.getAddLeft());
        content.put("buy_site_left", vo.getBuySiteLeft().trim());
        content.put("date_ini_left",
                Utility.formatDateToSqlite(vo.getDate_ini_left()));
        content.put("number_units_left", vo.getNumber_units_left());
        content.put("description_right", vo.getDescriptionRight().trim());
        content.put("brand_right", vo.getBrandRight().trim());
        content.put("discard_type_right", vo.getDiscardTypeRight());
        content.put("type_right", vo.getTypeRight());
        content.put("power_right", vo.getPowerRight());
        content.put("cylinder_right", vo.getCylinderRight());
        content.put("axis_right", vo.getAxisRight());
        content.put("add_right", vo.getAddRight());
        content.put("buy_site_right", vo.getBuySiteRight().trim());
        content.put("date_ini_right",
                Utility.formatDateToSqlite(vo.getDate_ini_right()));
        content.put("number_units_right", vo.getNumber_units_right());
        content.put("bc_left", vo.getBcLeft());
        content.put("bc_right", vo.getBcRight());
        content.put("dia_left", vo.getDiaLeft());
        content.put("dia_right", vo.getDiaRight());
        return content;
    }
*/

//    public DataLensesVO getById(String id) {
//        ParseQuery<ParseObject> query = getParseQuery(id);
//        DataLensesVO dataLensesVO = null;
//
//        try {
//            List<ParseObject> list = query.find();
//            for (ParseObject parseObj : list) {
//                dataLensesVO = setDataLensesVO(parseObj);
//            }
//        } catch (com.parse.ParseException e) {
//            Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
//        }
//
//        return dataLensesVO;
//    }

    public DataLensesVO getLastDataLenses() {
        ParseQuery<ParseObject> query = getParseQuery();

        try {
            ParseObject parseObject = query.getFirst();
            parseObject.saveEventually();
            ParseObject.unpinAllInBackground(tableName);
            parseObject.pinInBackground(tableName);
            return setDataLensesVO(parseObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getLastDataLensesAsync() {
        ParseQuery<ParseObject> query = getParseQuery();

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                dataLensesVO = setDataLensesVO(object);
            }
        });
    }

    private DataLensesVO setDataLensesVO(ParseObject obj) {
        DataLensesVO vo = null;
        if (obj != null) {
            vo = new DataLensesVO();
            vo.setId(obj.getString("data_id"));
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

//    public int getLastIdLens() {
//		Cursor rs = db.rawQuery("select max(id) from " + tableName, null);
//
//		if (rs.moveToFirst()) {
//			return rs.getInt(0);
//		}
//        return 0;
//    }

    @SuppressLint("SimpleDateFormat")
    public boolean updateDate(String column, int idLensesData, String date) {
//		synchronized (MainActivity.sDataLock) {
//			ContentValues content = new ContentValues();
//			content.put(column, date);
//
//			return db.update(tableName, content, "id=?",
//					new String[] { String.valueOf(idLensesData) }) > 0;
//		}
        return true;
    }

    private ParseObject getParseObjectDataLens(DataLensesVO vo) {
        ParseObject parseObject = new ParseObject(tableName);

        return setParseObject(parseObject, vo);
    }

    private ParseObject setParseObject(ParseObject parseObject, DataLensesVO vo) {
        parseObject.put("data_id", vo.getId().replace("OFFLINE", ""));

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

    public void syncDataLenses() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.fromPin(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereContains("data_id", "OFFLINE");

        try {
            List<ParseObject> list = query.find();
            for (ParseObject obj : list) {
                obj.put("data_id", obj.getString("data_id").replace("OFFLINE", ""));
                obj.setACL(new ParseACL(ParseUser.getCurrentUser()));
                obj.save();
            }
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }
}
