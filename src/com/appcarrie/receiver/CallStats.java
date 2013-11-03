package com.appcarrie.receiver;


import android.content.Context;
import android.content.SharedPreferences;

import com.appcarrie.utils.Constants;

public class CallStats {
	private static String TAG =  CallStats.class.getName();
	
	
	// Preference KEY!!
	private static final String key_stats_signal = "stats_signal";
	private static final String key_stats_idle = "stats_idle";
	private static final String key_waitingCall = "waitingCall";
	private static final String key_outgoingCall = "outgoingCall";
	private static final String key_endCall = "endCall";
	private static final String key_remote_phonenumber = "remote_phonenumber";
	private static final String key_remote_waitingnumber = "remote_waitingnumber";
	
	private long STATS_SIGNAL = -1;
	private long STATS_IDLE = -1;
	private boolean waitingCall = false;
	private boolean outgoingCall = false;
	private boolean endCall = false;
	private String remote_phonenumber = null;
	private String remote_waitingnumber = "";
	
	private Context mContext;
	private SharedPreferences mPref;
	
	public CallStats(Context c) {
		mPref = c.getSharedPreferences(Constants.PREF, 0);
		mContext = c;
		restorePref();
	}
	
	private void restorePref() {
		STATS_SIGNAL = mPref.getLong(key_stats_signal, -1);
		STATS_IDLE = mPref.getLong(key_stats_idle, -1);
		waitingCall = mPref.getBoolean(key_waitingCall, false);
		outgoingCall = mPref.getBoolean(key_outgoingCall, false);
		endCall = mPref.getBoolean(key_endCall, false);
		remote_phonenumber = mPref.getString(key_remote_phonenumber, null);	
		remote_waitingnumber = mPref.getString(key_remote_waitingnumber, "");
	}
	
	
	public void clearVars() {
		STATS_SIGNAL = -1;
		STATS_IDLE = -1;
		waitingCall = false;
		outgoingCall = false;
		endCall = false;
		remote_phonenumber = null;
		remote_waitingnumber = "";
		
		mPref.edit().remove(key_stats_signal)
					.remove(key_stats_idle)
					.remove(key_waitingCall)
					.remove(key_outgoingCall)
					.remove(key_endCall)
					.remove(key_remote_phonenumber)
					.commit();
	}
	
	public String getWaitingPhoneNumber() {
		return remote_waitingnumber;
	}

	public void setWaitingPhoneNumber(String phoneNumber) {
		if ( phoneNumber != null) {
			mPref.edit().putString( key_remote_waitingnumber, phoneNumber).commit();
			this.remote_waitingnumber = phoneNumber;
		}
	}

	public String getPhoneNumber() {
		return remote_phonenumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		if ( phoneNumber != null) {
			mPref.edit().putString( key_remote_phonenumber, phoneNumber).commit();
			this.remote_phonenumber = phoneNumber;
		}
	}
	
	public void setSTATS_SIGNAL(long sTATS_SIGNAL) {
		mPref.edit().putLong(key_stats_signal, sTATS_SIGNAL).commit();
		STATS_SIGNAL = sTATS_SIGNAL;
	}
	
	public boolean isSTATS_SIGNAL() {
		if ( STATS_SIGNAL == -1)
			return false;
		else
			return true;
	}

	public void setSTATS_IDLE(long sTATS_IDLE) {
		mPref.edit().putLong(key_stats_idle, sTATS_IDLE).commit();
		STATS_IDLE = sTATS_IDLE;
	}
	
	public boolean isSTATS_IDLE() {
		if ( STATS_IDLE == -1)
			return false;
		else
			return true;
	}
	
	
	
	public boolean isCallEnd() {
		return endCall;
	}
	
	public void setCallEnd(boolean endCall) {
		mPref.edit().putBoolean(key_endCall, endCall).commit();
		this.endCall = endCall;
	}
	
	
	public boolean isWaitingCall() {
		return waitingCall;
	}

	public void setWaitingCall(boolean waitingCall) {
		mPref.edit().putBoolean(key_waitingCall, waitingCall).commit();
		this.waitingCall = waitingCall;
	}
	
	public boolean isOutgoingCall() {
		return outgoingCall;
	}

	public void setOutgoingCall(boolean outgoingCall) {
		mPref.edit().putBoolean(key_outgoingCall, outgoingCall).commit();
		this.outgoingCall = outgoingCall;
	}
	
}