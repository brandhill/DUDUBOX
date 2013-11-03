package com.appcarrie.utils;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;


public class YLog {
	public static boolean isLog = false;
	
	public static void e ( String TAG, String msg) {
		if ( isLog && msg!=null ) {
			if (msg.length() > 3000) {
				int chunkCount = msg.length() / 3000; // integer division
				int i;
				for (i = 0; i < chunkCount; i++) {

					android.util.Log.e(TAG, msg.substring(3000 * i, 3000 * (i + 1)));
					android.util.Log.e(TAG, "---continue to print log---");
				}
				android.util.Log.e(TAG, msg.substring(3000 * i, msg.length()));
			} else {
				android.util.Log.e(TAG, msg);
			}
		}
	}
	
	public static void w ( String TAG, String msg) {
		if ( isLog && msg!=null ) {
			if (msg.length() > 3000) {
				int chunkCount = msg.length() / 3000; // integer division
				int i;
				for (i = 0; i < chunkCount; i++) {

					android.util.Log.w(TAG, msg.substring(3000 * i, 3000 * (i + 1)));
					android.util.Log.w(TAG, "---continue to print log---");
				}
				android.util.Log.w(TAG, msg.substring(3000 * i, msg.length()));
			} else {
				android.util.Log.w(TAG, msg);
			}
		}
	}
	
	public static void i ( String TAG, String msg) {
		if ( isLog && msg!=null ) {
			if (msg.length() > 3000) {
				int chunkCount = msg.length() / 3000; // integer division
				int i;
				for (i = 0; i < chunkCount; i++) {

					android.util.Log.i(TAG, msg.substring(3000 * i, 3000 * (i + 1)));
					android.util.Log.i(TAG, "---continue to print log---");
				}
				android.util.Log.i(TAG, msg.substring(3000 * i, msg.length()));
			} else {
				android.util.Log.i(TAG, msg);
			}
		}
	}
	
	public static void d(String TAG, String msg) {
		if ( isLog && msg!=null ) {
			if (msg.length() > 3000) {
				int chunkCount = msg.length() / 3000; // integer division
				int i;
				for (i = 0; i < chunkCount; i++) {

					android.util.Log.d(TAG, msg.substring(3000 * i, 3000 * (i + 1)));
					android.util.Log.d(TAG, "---continue to print log---");
				}
				android.util.Log.d(TAG, msg.substring(3000 * i, msg.length()));
			} else {
				android.util.Log.d(TAG, msg);
			}
		}
	}
	
	public static void toast(final Context context,final String msg){

		if ( isLog && msg!=null ) {
			(new Handler(Looper.getMainLooper())).post(new Runnable(){

				@Override
				public void run() {
					YToast t=YToast.makeText(context,"[Debug] "+msg, Toast.LENGTH_LONG);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
				}
				
			});
			
		}
	}
	
}
