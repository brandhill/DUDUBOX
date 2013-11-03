package com.appcarrie.receiver;

import com.appcarrie.utils.YLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ButtonReceiver extends BroadcastReceiver {
	private static final String TAG = "ButtonReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		YLog.e(TAG, "Receiver! " + intent.getAction());
	}

}
