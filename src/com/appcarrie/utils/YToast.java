package com.appcarrie.utils;

import com.appcarrie.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class YToast {

	private Context mContext;
	private String mString;
	private int mLength;
	private int mGravity;
	private int mGravityOffSetX;
	private int mGravityOffSetY;
	private Toast mToast;
	private YToast(Context context, String string, int length) {
		mContext=context;
		mString=string;
		mLength=length;
		mGravity=0;
	}
	public static YToast makeText(Context context, String string, int length) {
		
		return new YToast(context,string, length);
	}
	public static YToast makeText(Context context, int id, int length) {
		
		return new YToast(context,context.getString(id), length);
	}
	public void show(){
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		View layout = inflater.inflate(R.layout.toast,null);

		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(mString);

		mToast = new Toast(mContext.getApplicationContext());
		if(mGravity!=0){
			mToast.setGravity(mGravity, mGravityOffSetX, mGravityOffSetY);
		}
		mToast.setDuration(mLength);
		mToast.setView(layout);
		mToast.show();
		
	}
	public void cancel(){
		if(mToast!=null){
			mToast.cancel();
		}
	}
	public void setGravity(int gravity, int x, int y){
		mGravity=gravity;
		mGravityOffSetX=x;
		mGravityOffSetY=y;
	}

	
}
