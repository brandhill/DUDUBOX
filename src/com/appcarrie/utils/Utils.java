package com.appcarrie.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;


public class Utils {
	

	public static int fontSize;
	
	// 判斷是否有網路連線
	public static boolean hasWired(Context context) {
		boolean result = false;
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			result = false;
		} else {
			if (!info.isAvailable()) {
				result = false;
			} else {
				result = true;
			}
		}
		return result;
	}

	// 判斷wifi是否打開
	public static boolean isWifiOn(Context context) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (WifiManager.WIFI_STATE_ENABLED == wm.getWifiState()) {
			return true;
		} else {
			return false;
		}
	}

	// dp轉px
	public static int dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	// sp轉px
	public static float sp2px(Context context, Float sp) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return scaledDensity * sp;
	}

	// 判斷是否為 htc sense UI
	public static boolean isSenseUI() {
		try {
			Process ifc = Runtime.getRuntime().exec("getprop ro.build.sense.version");
			BufferedReader bis = new BufferedReader(new InputStreamReader(ifc.getInputStream()));
			String line = bis.readLine();

			if (line != null && !line.equals("")) {
				ifc.destroy();
				return true;
			}
			ifc.destroy();
		} catch (Exception e) {}
		return false;
	}
	
	public static String fixPhoneNumber(String phone) {
		if (phone == null) {
			return "";
		} else if (phone.startsWith("#31#")) {
			String sub = phone.substring(4);
			return sub;
		} else {
			return phone;
		}
	}

}
