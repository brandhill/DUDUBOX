package com.appcarrie.view;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class CallDialogRelativeLayout extends RelativeLayout {

	Context mContext;
	Service cds = null;
	
	public CallDialogRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public void setParent( Service service ) {
		cds = service;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.e("CallDialog", "onInterceptTouchEvent:" + ev.getAction());
		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("CallDialog", "onKeyDown:" + event.getKeyCode());
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent key){
		Log.e("CallDialog", "Key:" + key.getKeyCode());
		if ( cds != null && key.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			cds.stopSelf();
			
			try{
				WindowManager windowManager = (WindowManager) mContext.getSystemService(android.content.Context.WINDOW_SERVICE);
				windowManager.removeView(this);
			} catch(Exception e){}
			return true;
		}
		return super.dispatchKeyEvent(key);  
	}
	
	public void stopSelf() {
		if ( cds!=null)
			cds.stopSelf();
	}
}
