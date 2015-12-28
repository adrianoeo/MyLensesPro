package com.aeo.mylensespro.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aeo.mylensespro.dao.TimeLensesDAO;

public class DailyNotificationBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if ("NO_ACTION".equals(action)) {
			TimeLensesDAO lensDAO = TimeLensesDAO.getInstance(context);
			lensDAO.incrementDaysNotUsed(lensDAO.getLastLens());
		}

		// Clean Daily Notification in action bar
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(9999);

	}
}
