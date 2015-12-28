package com.aeo.mylensespro.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DailyAlarmBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		context.startService(new Intent("START_SERVICE_WEAR_DAILY"));
		Intent intent1 = new Intent(context, ServiceWearLens.class);
		context.startService(intent1);
	}

}
