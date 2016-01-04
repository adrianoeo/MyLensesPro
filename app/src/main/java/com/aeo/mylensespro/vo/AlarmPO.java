package com.aeo.mylensespro.vo;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.UUID;

/**
 * Created by adriano on 30/12/2015.
 */
@ParseClassName("AlarmPO")
public class AlarmPO extends ParseObject {

    public int getHour() {
        return getInt("hour");
    }

    public void setHour(int hour) {
        put("hour", hour);
    }

    public int getMinute() {
        return getInt("minute");
    }

    public void setMinute(int minute) {
        put("minute", minute);
    }

    public int getDaysBefore() {
        return getInt("daysBefore");
    }

    public void setDaysBefore(int daysBefore) {
        put("daysBefore", daysBefore);
    }

    public int getRemindEveryDay() {
        return getInt("remindEveryDay");
    }

    public void setRemindEveryDay(int remindEveryDay) {
        put("remindEveryDay", remindEveryDay);
    }

    public boolean isDraft() {
        return getBoolean("isDraft");
    }

    public void setDraft(boolean isDraft) {
        put("isDraft", isDraft);
    }

    public void setUuidString() {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
    }

    public String getUuidString() {
        return getString("uuid");
    }

    public static ParseQuery<AlarmPO> getQuery() {
        return ParseQuery.getQuery(AlarmPO.class);
    }

}
