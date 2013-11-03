package com.appcarrie;

import android.app.Application;
import android.content.Intent;

import com.appcarrie.loader.ContentFetcher;
import com.appcarrie.receiver.CallStats;
import com.appcarrie.utils.Analytics;
import com.appcarrie.utils.YLog;

public class MyApplication extends Application {
	private static final String TAG = MyApplication.class.getSimpleName();

	public CallStats mCallStats;

	@Override
	public void onCreate() {
		super.onCreate();
		YLog.d(TAG, "MyApplication onCreate");
		
		Analytics.init(getApplicationContext());

		mCallStats = new CallStats(getApplicationContext());

	}

}
