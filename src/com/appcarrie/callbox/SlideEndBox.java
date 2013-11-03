package com.appcarrie.callbox;

import android.app.Service;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.appcarrie.MyApplication;
import com.appcarrie.R;
import com.appcarrie.receiver.CallStats;
import com.appcarrie.utils.Utils;
import com.appcarrie.utils.YLog;
import com.appcarrie.view.FloatingViewGroup;

public abstract class SlideEndBox extends BaseBox {
	private static final String TAG = SlideEndBox.class.getSimpleName();

	private FloatingViewGroup mDialog;
	
	private Service mService;
	private Context mContext;
	private WindowManager mWindowManager;
	private CallStats mStats;
	private String mPhoneNumber;

	private int mHeight;
	private boolean mIsAlive = false;

	private TextView mContent;
	private View mClose, mBack, mPlay, mSkip;
	
	public SlideEndBox(Service service) {
		mService = service;
		mContext = service;
		mWindowManager = (WindowManager) mContext.getSystemService(android.content.Context.WINDOW_SERVICE);
		mStats = ((MyApplication) mContext.getApplicationContext()).mCallStats;
		mPhoneNumber = mStats.getPhoneNumber();
		mDialog = (FloatingViewGroup) LayoutInflater.from(mContext).inflate(R.layout.slidedialog_end_layout, null);

		mHeight = Utils.dp2px(mContext, 500 + Utils.fontSize * 5);
		
		mContent = (TextView)mDialog.findViewById(R.id.tv_dialog_content);
		mClose = mDialog.findViewById(R.id.iv_dialog_close);
		mBack = mDialog.findViewById(R.id.iv_dialog_back);
		mPlay = mDialog.findViewById(R.id.iv_dialog_play);
		mSkip = mDialog.findViewById(R.id.iv_dialog_skip);
		
		Typeface font = Typeface.createFromAsset(service.getAssets(), "Roboto-Light.ttf");
		((TextView)mDialog.findViewById(R.id.tv_dialog_title)).setTypeface(font);  
		mContent.setTypeface(font);  
		
		mClose.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onDialogClose();
			}
		});
		
		mBack.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onButtonBack();
			}
		});
		
		mSkip.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onButtonSkip();
			}
		});
		
		mPlay.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setMusicStatus(!v.isSelected());
				onButtonPlay(v.isSelected());
			}
		});
		
		mIsAlive = true;
	}

	@Override
	public int getHeight() {
		return mHeight;
	}

	@Override
	public View getDialogView() {
		return mDialog;
	}

	public void stopService() {
		mIsAlive = false;
		mService.stopSelf();
		try {
			mWindowManager.removeView(mDialog);
		} catch (Exception e) {}
	}

	public void updateView(WindowManager.LayoutParams lp) {
		if (mIsAlive)
			mWindowManager.updateViewLayout(mDialog, lp);
	}

	@Override
	public void setMusicStatus(boolean status) {
		YLog.e(TAG, "set Play button " + status);
		mPlay.setSelected(status);
		if ( status )
			mPlay.setBackgroundResource(R.drawable.callend_pause_selector);
		else
			mPlay.setBackgroundResource(R.drawable.callend_play_selector);
	}

	@Override
	public void refresh(String content) {
		mContent.setText(content);
	}

}
