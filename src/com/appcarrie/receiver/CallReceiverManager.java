package com.appcarrie.receiver;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.appcarrie.MyApplication;
import com.appcarrie.utils.Utils;
import com.appcarrie.utils.YLog;

public class CallReceiverManager {
	private static final String TAG = "CallReceiverManager";

	private static CallReceiverManager sCallReceiverManager;

	public static final int STATE_UNKNOWN = -1;
	public static final int STATE_IDLE = 0;
	public static final int STATE_RINGING = 1;
	public static final int STATE_OFFHOOK = 2;
	public static final int STATE_OUTGOING = 3;
	
	private CallReceiverManager() {}

	public static CallReceiverManager getInstance() {
		if (sCallReceiverManager == null) {
			sCallReceiverManager = new CallReceiverManager();
		}
		return sCallReceiverManager;
	}

	public synchronized void onReceive(final Context context, Intent intent) {
		CallStats callstats = ((MyApplication) (context.getApplicationContext())).mCallStats;

		Intent startIntent = new Intent(context, CallDialogService.class);
		
		if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
			// 去電
			YLog.e(TAG, "ACTION_NEW_OUTGOING_CALL");
			
			callstats.clearVars();
			callstats.setPhoneNumber(Utils.fixPhoneNumber(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)));
			callstats.setSTATS_SIGNAL(System.currentTimeMillis());
			callstats.setOutgoingCall(true);
			startIntent.putExtra("init", true);
		} else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			YLog.e(TAG, "EXTRA_STATE_RINGING");
			callstats.clearVars();
			callstats.setPhoneNumber(Utils.fixPhoneNumber(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)));
			callstats.setSTATS_SIGNAL(System.currentTimeMillis());
			callstats.setOutgoingCall(false);
			startIntent.putExtra("init", true);
		} else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {
			callstats.setSTATS_IDLE(System.currentTimeMillis());
		}
		
		String phonenumber = callstats.getPhoneNumber();
		if ( !callstats.isOutgoingCall() || 
			phonenumber == null || phonenumber.equals("") || phonenumber.matches("\\D*")) {
			
			YLog.e(TAG, "Stop service!!");
			Intent i = new Intent(context, CallDialogService.class);
			context.stopService(i);
			return;
		}
		YLog.e(TAG, "Start service!!");
		context.startService(startIntent);

	}
}
