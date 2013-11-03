package com.appcarrie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appcarrie.loader.VoiceLoader;
import com.appcarrie.net.NetCallback;
import com.appcarrie.net.NetworkAPI;
import com.appcarrie.utils.Constants;
import com.appcarrie.utils.YLog;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private Context mContext;
	private SharedPreferences mPref;
	
	private TextView mMsgText;
	
	
	private Typeface mfont;
	private View mLoadingView;
	private View mMainGroup;

	private TextView type_weather;
	private TextView type_news;  
	private TextView type_stock;  
	private TextView type_joke;  
	private TextView type_astrology;  
	private TextView type_ad; 
	
	private HashMap< String, Boolean> mMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setWindowAnimations(0);

		setContentView(R.layout.main);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getActionBar().setCustomView(R.layout.actionbar);
		
		TextView title = (TextView) findViewById(R.id.action_title); 
		mfont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		Typeface fontitalic = Typeface.createFromAsset(getAssets(), "Roboto-LightItalic.ttf");  
		title.setTypeface(mfont);  
		 
		mContext = this;
		mPref = mContext.getSharedPreferences(Constants.PREF, 0);
		mMsgText = (TextView)findViewById(R.id.tv_loading);
		mLoadingView = findViewById(R.id.rl_loading);
		mMainGroup = findViewById(R.id.ll_main);
		
		TextView type_tile = (TextView) findViewById(R.id.tv_type_title);  
		TextView type_desc = (TextView) findViewById(R.id.tv_type_desc); 
		TextView type_sign = (TextView) findViewById(R.id.tv_type_sign); 
		type_weather = (TextView) findViewById(R.id.tv_type_weather);  
		type_news = (TextView) findViewById(R.id.tv_type_news);  
		type_stock = (TextView) findViewById(R.id.tv_type_stock);  
		type_joke = (TextView) findViewById(R.id.tv_type_joke);  
		type_astrology = (TextView) findViewById(R.id.tv_type_astrology);  
		type_ad = (TextView) findViewById(R.id.tv_type_ad); 
		type_tile.setTypeface(fontitalic);
		type_desc.setTypeface(fontitalic);
		type_sign.setTypeface(fontitalic);
		type_weather.setTypeface(mfont);
		type_news.setTypeface(mfont);
		type_stock.setTypeface(mfont);
		type_joke.setTypeface(mfont);
		type_astrology.setTypeface(mfont);
		type_ad.setTypeface(mfont);
		type_weather.setOnClickListener( new MyClickListener("weather"));
		type_news.setOnClickListener( new MyClickListener("world_news"));
		type_stock.setOnClickListener( new MyClickListener("money"));
		type_joke.setOnClickListener( new MyClickListener("joke"));
		type_astrology.setOnClickListener( astroClickListener);
		type_ad.setOnClickListener( new MyClickListener("ad"));
		
		
		mMap = new HashMap<String, Boolean>();
		
		new InitAsync().execute();
	}
	
	private OnClickListener astroClickListener = new OnClickListener() {
		
		private Dialog mDialog;

		private HashMap< String, Boolean> mAstroMap = new HashMap<String, Boolean>();
		
		@Override
		public void onClick(View v) {
			mAstroMap.clear();
			Iterator<?> iter = mMap.entrySet().iterator(); 
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    String key = (String)entry.getKey();
			    if ( key.startsWith("luck_"))
			    	mAstroMap.put(key, true);
			} 
			
			LayoutInflater inflater = getLayoutInflater();
			View dialogview = inflater.inflate(R.layout.main_astrodialog, null);
			
			TextView title = (TextView)dialogview.findViewById(R.id.dialog_title);
			LinearLayout content = (LinearLayout)dialogview.findViewById(R.id.dialog_content);
			Button confirm = (Button)dialogview.findViewById(R.id.dialog_confirm);

			title.setTypeface(mfont);
			final String[] astrology = getResources().getStringArray(R.array.type_astrology);
			for( int i=0 ; i<astrology.length ; i++ ) {
				View item = inflater.inflate(R.layout.main_astro_item, null);
				TextView text = (TextView)item.findViewById(R.id.tv_dialog);
				ImageView image = (ImageView)item.findViewById(R.id.iv_dialog);
				
				text.setText(astrology[i]);
				text.setTypeface(mfont);
				setImage(image, i);
				
				View select = item.findViewById(R.id.tb_dialog);
				if ( mAstroMap.get("luck_" + astrology[i]) != null )
					select.setSelected(true);
				
				select.setOnClickListener(new AstroOnClickListener("luck_" + astrology[i]));
				
				content.addView(item);
			}
			
			confirm.setTypeface(mfont);
			confirm.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					for ( int i=0 ; i< astrology.length ; i++ )
						mMap.remove("luck_" + astrology[i]);
					Iterator<?> iter2 = mAstroMap.entrySet().iterator(); 
					while (iter2.hasNext()) { 
					    Map.Entry entry = (Map.Entry) iter2.next(); 
					    String key = (String)entry.getKey();
				    	mMap.put(key, true);
					} 
					if (mAstroMap.size() == 0)
						type_astrology.setSelected(false);
					else {
						type_astrology.setSelected(true);
						JSONArray favorite_type = new JSONArray();
						
						Iterator<?> iter = mMap.entrySet().iterator(); 
						while (iter.hasNext()) { 
						    Map.Entry entry = (Map.Entry) iter.next(); 
						    Object key = entry.getKey();
						    favorite_type.put(key);
						} 
						mPref.edit().putString( Constants.FAVORITE_TYPE, favorite_type.toString()).commit();
						VoiceLoader.getInstance(mContext).initVoice();
					}
					if ( mDialog != null)
						mDialog.dismiss();
				}
			});
			
			mDialog = new Dialog(mContext, R.style.dialog);	
			mDialog.setContentView(dialogview);
			mDialog.show();
		}
		
		
		class AstroOnClickListener implements OnClickListener {

			private String key;
			
			public AstroOnClickListener( String key) {
				this.key = key;
			}
			
			@Override
			public void onClick(View v) {
				v.setSelected(!v.isSelected());
				if ( v.isSelected())
					mAstroMap.put( key, true);
				else
					mAstroMap.remove( key);
			}
			
		}
	};
	
	
	private void setImage( ImageView image, int index) {
		switch(index) {
		case 0:
			image.setImageResource(R.drawable.star_aries);
			break;
		case 1:
			image.setImageResource(R.drawable.star_taurus);
			break;
		case 2:
			image.setImageResource(R.drawable.star_gemini);
			break;
		case 3:
			image.setImageResource(R.drawable.star_cancer);
			break;
		case 4:
			image.setImageResource(R.drawable.star_leo);
			break;
		case 5:
			image.setImageResource(R.drawable.star_virgo);
			break;
		case 6:
			image.setImageResource(R.drawable.star_libra);
			break;
		case 7:
			image.setImageResource(R.drawable.star_scorpio);
			break;
		case 8:
			image.setImageResource(R.drawable.star_sagittariu);
			break;
		case 9:
			image.setImageResource(R.drawable.star_capricorn);
			break;
		case 10:
			image.setImageResource(R.drawable.star_aquarius);
			break;
		case 11:
			image.setImageResource(R.drawable.star_pisces);
			break;
		}
		
		
	}
	
	class MyClickListener implements OnClickListener {

		private String key;
		public MyClickListener( String key ) {
			this.key = key;
		}
		
		@Override
		public void onClick(View v) {
			v.setSelected(!v.isSelected());
			if ( v.isSelected())
				mMap.put( key, true);
			else
				mMap.remove( key);
			JSONArray favorite_type = new JSONArray();
			
			Iterator<?> iter = mMap.entrySet().iterator(); 
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    Object key = entry.getKey();
			    favorite_type.put(key);
			} 
			mPref.edit().putString( Constants.FAVORITE_TYPE, favorite_type.toString()).commit();
			VoiceLoader.getInstance(mContext).initVoice();
		}
	}
	
	private void initView() {
		try {
			JSONArray types = new JSONArray(mPref.getString(Constants.VOICE_TYPE, "[]"));
			JSONArray favorite = new JSONArray(mPref.getString(Constants.FAVORITE_TYPE, "[]"));
			
			for (int i = 0; i < favorite.length(); i++) {
				String type = (String) favorite.get(i);
				mMap.put( type, true);
				if ( type.startsWith("luck_"))
					type_astrology.setSelected(true);
			}
			
			if (mMap.get("weather") != null ) 
				type_weather.setSelected(true);
			if (mMap.get("world_news") != null ) 
				type_news.setSelected(true);
			if (mMap.get("money") != null ) 
				type_stock.setSelected(true);
			if (mMap.get("joke") != null ) 
				type_joke.setSelected(true);
			if (mMap.get("ad") != null ) 
				type_ad.setSelected(true);
			
			mLoadingView.setVisibility(View.GONE);
			mMainGroup.setVisibility(View.VISIBLE);
		} catch (JSONException e) {}
	}
	
	

	private class InitAsync extends AsyncTask<Void, String, Void>{
		
		private boolean firstTime;
		
		public InitAsync() {
			firstTime = mPref.getBoolean(Constants.FIRSTTIME, true);
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if ( firstTime ) {
					publishProgress(getString(R.string.init_gettype));
					NetworkAPI.getVoiceType(mContext, postGetType);
				} 
			} catch (Exception e) {}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... msg) {
			mMsgText.setText(msg[0]);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			initView();
		};
		
		
		NetCallback postGetType = new NetCallback() {
			
			@Override
			public void onComplete(int status, String re) {

				if (status == HttpStatus.SC_OK && re != null ) {
					try {
						JSONObject ret = new JSONObject(re);
						JSONArray types = ret.optJSONArray(Constants.JSON_TYPE);
						if ( types != null )
							mPref.edit()
								.putString(Constants.VOICE_TYPE, types.toString())
								.putBoolean(Constants.FIRSTTIME, false)
								.commit();
					} catch (JSONException e) {}
				} else {}
			}
		};
		
	}


}
