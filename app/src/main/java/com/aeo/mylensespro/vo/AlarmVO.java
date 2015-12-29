package com.aeo.mylensespro.vo;

public class AlarmVO {

	private long hour;
	private long minute;
	private long daysBefore;
	private long remindEveryDay;

	public AlarmVO() {}

	public long getHour() {
		return hour;
	}

	public void setHour(long hour) {
		this.hour = hour;
	}

	public long getMinute() {
		return minute;
	}

	public void setMinute(long minute) {
		this.minute = minute;
	}

	public long getDaysBefore() {
		return daysBefore;
	}

	public void setDaysBefore(long daysBefore) {
		this.daysBefore = daysBefore;
	}

	public long getRemindEveryDay() {
		return remindEveryDay;
	}

	public void setRemindEveryDay(long remindEveryDay) {
		this.remindEveryDay = remindEveryDay;
	}

}
