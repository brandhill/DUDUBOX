package com.appcarrie.utils;

import android.app.Activity;
import android.content.Context;


/**
 * the app analytics tool for "Google Analytics" v2 version
 * 
 * @author Shawn
 * 
 */

public class Analytics {
		
	
	public static  void init( Context context) {
//		EasyTracker.getInstance().setContext(context);
//		GoogleAnalytics myInstance = GoogleAnalytics.getInstance(context);
//		myInstance.setDebug(false);
	}
	
	public static void onStart(Activity activity) {
//		EasyTracker.getInstance().activityStart(activity);
	}
	
	public static void onStop(Activity activity) {
//		EasyTracker.getInstance().activityStop(activity);
	}
	
	public static void trackEvent(String category, String event, String label, long value) {
//		WLog.d("Analytics", category+" "+event+" "+label+" "+value);
//		Tracker track = EasyTracker.getTracker();
//		track.sendEvent( category, event, label, value);
	}
	
	public static void dispatch() {
//		EasyTracker.getInstance().dispatch();
	}
}