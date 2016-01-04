package com.aeo.mylensespro.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aeo.mylensespro.dao.AlarmDAO;
import com.aeo.mylensespro.dao.TimeLensesDAO;
import com.aeo.mylensespro.vo.AlarmVO;
import com.aeo.mylensespro.vo.TimeLensesVO;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//			String idLenses = ListReplaceLensFragment.listLenses == null ? TimeLensesDAO.getInstance(
//					context).getLastIdLens() : ListReplaceLensFragment.listLenses
//					.get(0).getId();

			TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
			TimeLensesVO timeLensesVO = timeLensesDAO.getLastLens();

			AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
			alarmDAO.setAlarm(timeLensesVO);
			
			//Daily notification
			AlarmVO alarmVO = alarmDAO.getAlarmNow();

			if (alarmVO.getRemindEveryDay() == 1) {
				Long[] daysToExpire = timeLensesDAO.getDaysToExpire(timeLensesVO);

				if (daysToExpire[0] > 0 || daysToExpire[1] > 0) {
					alarmDAO.setAlarmManagerDaily(alarmVO.getHour(), alarmVO.getMinute());
				}
			}
		}
	}
}
