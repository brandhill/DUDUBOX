package com.appcarrie.net;

import org.json.JSONObject;

import android.content.Context;

import com.appcarrie.utils.Constants;


public class NetworkAPI {
	private final static String TAG = "NetworkAPI";
	
	
	private static final String DefaultGATE = Constants.GATEWAY;
			
	
	public static int sendLogin ( final Context mContext, final String username, final String password, final String csrftoken,
			final NetCallback callback) throws Exception {
		String url = DefaultGATE + "/accounts/login";
		JSONObject obj = new JSONObject();
		obj.put("username", username);
		obj.put("password", password);
		obj.put("csrfmiddlewaretoken", csrftoken);
		return REST.post( null, obj, url, callback);
	}
	
	public static int sendLogout ( final Context mContext, final NetCallback callback) throws Exception {
		String url = DefaultGATE + "/accounts/logout/";
		return REST.get( null, url, callback);
	}
	

	public static int getVoiceType ( final Context mContext, final NetCallback callback) throws Exception {
		String url = DefaultGATE + "/voice/type";
		return REST.get( null, url, callback);	
	}
	
	public static int getVoice ( final Context mContext, final JSONObject body, final NetCallback callback) throws Exception {
		String url = DefaultGATE + "/voice";
		return REST.post( null, body, url, callback);	
	}

}
