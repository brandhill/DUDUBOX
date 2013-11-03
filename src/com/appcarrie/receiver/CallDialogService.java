package com.appcarrie.receiver;

import org.json.JSONObject;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.appcarrie.MyApplication;
import com.appcarrie.R;
import com.appcarrie.callbox.BaseBox;
import com.appcarrie.callbox.SlideBox;
import com.appcarrie.callbox.SlideEndBox;
import com.appcarrie.loader.VoiceLoader;
import com.appcarrie.utils.Constants;
import com.appcarrie.utils.NotifyID;
import com.appcarrie.utils.Utils;
import com.appcarrie.utils.YLog;


public class CallDialogService extends Service {
	private static final String TAG = "CallDialogService";
	
	
	private Context mContext;
	private SharedPreferences mPref;
	public boolean mIsAlive=false;

	private BaseBox dialog;

	private WindowManager mWindowManager;
	private LayoutParams p;
	
	private MediaPlayer mMp;
	private JSONObject mVoice;
	
	private SettingsContentObserver mSettingsContentObserver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		YLog.e(TAG,"onCreate");
		mIsAlive=true;
		
		mContext = this;
		mPref = mContext.getSharedPreferences(Constants.PREF, 0);
		
		
		mSettingsContentObserver = new SettingsContentObserver( this, new Handler() ); 
		mMp = new MediaPlayer();
		mMp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMp.setOnCompletionListener( new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				if ( dialog!=null )
					dialog.setMusicStatus(false);
			}
		});
		
		mWindowManager = (WindowManager) mContext.getSystemService(android.content.Context.WINDOW_SERVICE);
		
		p = new LayoutParams();
		p.width = LayoutParams.FILL_PARENT;
		
		if ( Utils.isSenseUI() ) {
			p.type = LayoutParams.TYPE_SYSTEM_ERROR;
			p.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
		} else {
			p.type = LayoutParams.TYPE_SYSTEM_ALERT;
			p.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
		}
		p.windowAnimations = android.R.style.Animation_Toast;
		p.format = PixelFormat.TRANSPARENT;
		p.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		
		
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		YLog.e(TAG,"onStartCommand");

		CallStats callStats = ((MyApplication)getApplicationContext()).mCallStats;
		String phonenumber = callStats.getPhoneNumber();
		
		if ( intent == null ||  phonenumber == null ||  phonenumber.equals("") || callStats.isCallEnd()) {
			stopSelf();
			return START_NOT_STICKY;
		}
		
		Notification mNotification = new Notification( R.drawable.ic_launcher, getString( R.string.app_name), System.currentTimeMillis());
		mNotification.when |= System.currentTimeMillis();
		PendingIntent appIntent = PendingIntent.getActivity( getApplicationContext(), 0, intent, 0);
		mNotification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), getString(R.string.action_settings), appIntent);
		
		startForeground( NotifyID.CALL_CALLDIALOG, mNotification);
		
		mContext.getContentResolver().registerContentObserver( 
			    android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver );
		
		
		refreshView();
		
		
		YLog.e(TAG, "mPlayer:" + mMp.isPlaying());
		if ( !mMp.isPlaying() && callStats.isOutgoingCall() ) {
			try {
				mVoice = VoiceLoader.getInstance(mContext).feachVoice();
				if ( mVoice != null ) {
					Uri myUri = Uri.parse("file://"+mVoice.optString(Constants.JSON_SOUND));
					mMp.reset();
					mMp.setDataSource(mContext, myUri);
					mMp.prepare();
					mMp.start();
					if ( dialog!=null ) {
						dialog.refresh(mVoice.optString(Constants.JSON_DESC));
						dialog.setMusicStatus(true);
					}
				} else if ( dialog!=null ) {
					dialog.setMusicStatus(false);
				}
					
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		
		return START_NOT_STICKY;
	}	
	

	private void refreshView() {
		CallStats callStats = ((MyApplication)getApplicationContext()).mCallStats;
		
		try{
			mWindowManager.removeView(dialog.getDialogView());
		} catch(Exception e){}
		
		if ( callStats.isSTATS_IDLE() ) {
			dialog = new SlideEndBox( this) {
				
				@Override
				public void onDialogClose() {
					CallStats callStats = ((MyApplication)getApplicationContext()).mCallStats;
					callStats.setCallEnd(true);
					stopSelf();
				}

				@Override
				public void onButtonPlay(boolean isplay) {
					if ( !isplay && mMp.isPlaying()) {
						mMp.pause();
					} else if ( isplay ) {
						mMp.start();
					}
				}

				@Override
				public void onButtonSkip() {
					mVoice = VoiceLoader.getInstance(mContext).feachVoice();
					if ( mVoice != null ) {
						Uri myUri = Uri.parse("file://"+mVoice.optString(Constants.JSON_SOUND));
						
						try {
							mMp.reset();
							mMp.setDataSource(mContext, myUri);
							mMp.prepare();
							mMp.start();
							if ( dialog!=null ) {
								dialog.refresh(mVoice.optString(Constants.JSON_DESC));
								dialog.setMusicStatus(true);
							}
						} catch (Exception e) {}
					}
				}

				@Override
				public void onButtonBack() {
					mVoice = VoiceLoader.getInstance(mContext).backVoice();
					if ( mVoice != null ) {
						Uri myUri = Uri.parse("file://"+mVoice.optString(Constants.JSON_SOUND));
						try {
							mMp.reset();
							mMp.setDataSource(mContext, myUri);
							mMp.prepare();
							mMp.start();
							if ( dialog!=null ) {
								dialog.refresh(mVoice.optString(Constants.JSON_DESC));
								dialog.setMusicStatus(true);
							}
						} catch (Exception e) {}
					}
				}
			};
			
		} else {
			dialog = new SlideBox( this) {
				
				@Override
				public void onDialogClose() {
					CallStats callStats = ((MyApplication)getApplicationContext()).mCallStats;
					callStats.setCallEnd(true);
					stopSelf();
				}

				@Override
				public void onButtonPlay(boolean isplay) {
					
					if ( !isplay && mMp.isPlaying()) {
						mMp.pause();
					} else if ( isplay ) {
						mMp.start();
					}
				}

				@Override
				public void onButtonSkip() {
					mVoice = VoiceLoader.getInstance(mContext).feachVoice();
					Uri myUri = Uri.parse("file://"+mVoice.optString(Constants.JSON_SOUND));
					mMp.reset();
					try {
						mMp.setDataSource(mContext, myUri);
						mMp.prepare();
						mMp.start();
						if ( dialog!=null ) {
							dialog.refresh(mVoice.optString(Constants.JSON_DESC));
							dialog.setMusicStatus(true);
						}
					} catch (Exception e) {}
				}

				@Override
				public void onButtonBack() {
					mVoice = VoiceLoader.getInstance(mContext).backVoice();
					Uri myUri = Uri.parse("file://"+mVoice.optString(Constants.JSON_SOUND));
					mMp.reset();
					try {
						mMp.setDataSource(mContext, myUri);
						mMp.prepare();
						mMp.start();
						if ( dialog!=null ) {
							dialog.refresh(mVoice.optString(Constants.JSON_DESC));
							dialog.setMusicStatus(true);
						}
					} catch (Exception e) {}
				}
			};
		}
		
		
		if ( callStats.isCallEnd() == false ) {
			try {
				dialog.setMusicStatus(mMp.isPlaying());
				if ( mVoice != null )
					dialog.refresh(mVoice.optString(Constants.JSON_DESC));

				p.height = dialog.getHeight();
				mWindowManager.addView(dialog.getDialogView(), p);
	
			} catch (Exception e) {
				e.printStackTrace();
				stopSelf();
				return;
			}
		}
	}

	@Override
	public void onDestroy() {
		
		mContext.getContentResolver().unregisterContentObserver(mSettingsContentObserver);
		if ( mMp!=null && mMp.isPlaying() ) {
			mMp.stop();
		}
		stopForeground(true);
		if ( dialog!=null ) {
			try{
				mWindowManager.removeView(dialog.getDialogView());
			} catch(Exception e){}
		}
		mIsAlive=false;
		super.onDestroy();
	}
	
	
	public class SettingsContentObserver extends ContentObserver {
		
		int previousVolume;
	    Context context;

	    public SettingsContentObserver(Context c, Handler handler) {
	        super(handler);
	        context=c;

	        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	        previousVolume = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
	    }

	    @Override
	    public boolean deliverSelfNotifications() {
	        return super.deliverSelfNotifications();
	    }

	    @Override
	    public void onChange(boolean selfChange) {
	        super.onChange(selfChange);

	        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
	        int delta=previousVolume-currentVolume;

	        
	        if(delta>0) {
	            YLog.e( TAG, "Decreased");
	            previousVolume=currentVolume;
	        } else if(delta<0) {
	        	YLog.e( TAG, "Increased");
	            previousVolume=currentVolume;
	        }
	    }
	}
}
