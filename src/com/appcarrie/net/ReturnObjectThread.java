package com.appcarrie.net;


import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.appcarrie.utils.Constants;


public class ReturnObjectThread extends Thread {
	
	protected JSONArray mReturnValue;
	protected Context mContext;
	protected SharedPreferences mPref;
	
	public ReturnObjectThread(Context context){
		mContext = context;
		mPref = context.getSharedPreferences(Constants.PREF, 0);	
		mReturnValue = new JSONArray();
	}
	public JSONArray returnValue(){
		return mReturnValue;
	}
}
