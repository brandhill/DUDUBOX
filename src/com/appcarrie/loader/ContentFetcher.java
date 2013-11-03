package com.appcarrie.loader;

import java.util.Iterator;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.appcarrie.net.NetCallback;
import com.appcarrie.net.NetworkAPI;
import com.appcarrie.net.ReturnObjectThread;
import com.appcarrie.utils.Constants;
import com.appcarrie.utils.YLog;

public class ContentFetcher extends ReturnObjectThread {

	private static final int DEFAULT_VOICE_COUNT = 5;
	
	public ContentFetcher(Context context) {
		super(context);
	}


	@Override
	public void run() {
		JSONObject body = new JSONObject();
		try {
			JSONArray array = new JSONArray(mPref.getString(Constants.FAVORITE_TYPE, "[]"));
			YLog.e("Fetcher", array.toString(1));
			body.put(Constants.JSON_TYPE, array);
			if ( array.length()*2 < DEFAULT_VOICE_COUNT)
				body.put(Constants.JSON_AMOUNT, DEFAULT_VOICE_COUNT);
			else
				body.put(Constants.JSON_AMOUNT, array.length()*2);
			NetworkAPI.getVoice(mContext, body, mNetCallback);
		} catch (Exception e) {}
		
	}
	
	private NetCallback mNetCallback = new NetCallback() {

		@Override
		public void onComplete(int status, String re) {
			if ( status == HttpStatus.SC_OK && re != null ) {
				try {
					JSONObject voices =  new JSONObject(re);
					
					Iterator<?> keys = voices.keys();
			        while( keys.hasNext() ){
			            String key = (String)keys.next();
			            if( voices.get(key) instanceof JSONArray ){
			            	JSONArray array = (JSONArray) voices.get(key);
			            	for (int i = 0; i < array.length(); i++) {
			            		mReturnValue.put(array.get(i));
			                }
			            }
			        }
			        mPref.edit()
			        	.putInt(Constants.VOICE_COUNTER, 0)
			        	.putString(Constants.VOICE_DATA, mReturnValue.toString())
			        	.commit();
			        
				} catch (JSONException e) {}
			}
			
		}
	};
}
