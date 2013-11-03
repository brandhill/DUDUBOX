package com.appcarrie.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallReceiver extends BroadcastReceiver {
	private static final String TAG = "CallReceiver";
	
	
	@Override
	public void onReceive(final Context context, Intent intent) {

        if(intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")
        	|| intent.getAction().equals("android.intent.action.PHONE_STATE")) {
        	
        	CallReceiverManager.getInstance().onReceive(context, intent);
        	
        }
        
	}
	
}
