package com.appcarrie.loader;

import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.appcarrie.net.ReturnObjectThread;
import com.appcarrie.utils.Constants;
import com.appcarrie.utils.YLog;

public class VoiceLoader {
	private static final String TAG = VoiceLoader.class.getSimpleName();

	private Context mContext;
	private SharedPreferences mPref;
	private int refershCounter = 0;
	
	private JSONArray mCacheList;

	private int mMusicSelect = 0;
	
	private static VoiceLoader sNumberInfoLoader;
	
	private VoiceLoader(Context context) {
		mContext = context;
		mPref = context.getSharedPreferences(Constants.PREF, 0);
		String voices = mPref.getString(Constants.VOICE_DATA, "[]");

		
		try {
			mCacheList = new JSONArray(voices);
		} catch (JSONException e) {
			mCacheList = new JSONArray();
		}
		refershCounter = mPref.getInt(Constants.VOICE_COUNTER, 0);
		
        
	}

	public static VoiceLoader getInstance(Context context) {
		if (sNumberInfoLoader == null) {
			sNumberInfoLoader = new VoiceLoader(context.getApplicationContext());
		}
		return sNumberInfoLoader;
	}

	public void initVoice() {
//		if ( mCacheList.length() <= 0)
			refreshVoice();
	}
	
	public JSONObject backVoice() {
		if ( mCacheList.length() != 0) {
	        mMusicSelect--;
	        if ( mMusicSelect == -1)
	        	mMusicSelect=mCacheList.length()-1;
			refershCounter++;
			mPref.edit().putInt(Constants.VOICE_COUNTER, refershCounter).commit();
			if ( refershCounter == mCacheList.length())
				refreshVoice();
		} else
			refreshVoice();
		JSONObject ret = null;
		try {
			ret = (JSONObject) mCacheList.get(mMusicSelect);
			ret.put(Constants.JSON_SOUND, Constants.STORAGE_PATH+"/"+mMusicSelect+".mp3");
		} catch (JSONException e) {}
		return ret;
	}
	
	public JSONObject feachVoice() {
		if ( mCacheList.length() != 0) {
			mMusicSelect++;
	        if ( mMusicSelect == mCacheList.length())
	        	mMusicSelect=0;
			refershCounter++;
			mPref.edit().putInt(Constants.VOICE_COUNTER, refershCounter).commit();
			if ( refershCounter == mCacheList.length())
				refreshVoice();
		} else
			refreshVoice();
		JSONObject ret = null;
		try {
			ret = (JSONObject) mCacheList.get(mMusicSelect);
			ret.put(Constants.JSON_SOUND, Constants.STORAGE_PATH+"/"+mMusicSelect+".mp3");
		} catch (JSONException e) {}
		return ret;
	}

	public void refreshVoice() {
		new Thread( new Runnable() {
			
			@Override
			public void run() {
				ReturnObjectThread fetcher = new ContentFetcher(mContext);
				fetcher.start();
				try {
					fetcher.join();
				} catch (InterruptedException e) {}
				
				mCacheList = fetcher.returnValue();
				for ( int i=0 ; i<mCacheList.length() ; i++ ) {
					try {
						
						JSONObject obj = (JSONObject)mCacheList.get(i);
						String url = obj.optString(Constants.JSON_SOUND);
						YLog.e(TAG, "Download:" + url);
						if ( !url.equals(""))
							new VoiceFetcher(mContext, new URL(url), String.valueOf(i)).start();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				mPref.edit().putString(Constants.VOICE_DATA, mCacheList.toString()).commit();
			}
		}).start();
		
		
	}
}
