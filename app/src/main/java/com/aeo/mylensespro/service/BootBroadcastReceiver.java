package com.aeo.mylensespro.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aeo.mylensespro.dao.AlarmDAO;
import com.aeo.mylensespro.dao.TimeLensesDAO;
import com.aeo.mylensespro.fragment.ListReplaceLensFragment;
import com.aeo.mylensespro.vo.AlarmVO;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

			int idLenses = ListReplaceLensFragment.listLenses == null ? TimeLensesDAO.getInstance(
					context).getLastIdLens() : ListReplaceLensFragment.listLenses
					.get(0).getId();

			AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
			alarmDAO.setAlarm(idLenses);
			
			//Daily notification
			AlarmVO alarmVO = alarmDAO.getAlarm();
			TimeLensesDAO timeLensesDAO = TimeLensesDAO.getInstance(context);
			if (alarmVO.getRemindEveryDay() == 1) {
				Long[] daysToExpire = timeLensesDAO.getDaysToExpire(idLenses);

				if (daysToExpire[0] > 0 || daysToExpire[1] > 0) {
					alarmDAO.setAlarmManagerDaily((int) alarmVO.getHour(), (int) alarmVO.getMinute());
				}
			}
		}
	}
}
