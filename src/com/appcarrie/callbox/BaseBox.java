package com.appcarrie.callbox;

import android.view.View;


public abstract class BaseBox {
	
	public abstract View getDialogView();
	
	
	public abstract int getHeight();
	
	public abstract void refresh( String content);
	

	public abstract void setMusicStatus( boolean status);
	public abstract void onButtonPlay( boolean isplay);
	public abstract void onButtonSkip();
	public abstract void onButtonBack();
	public abstract void onDialogClose();
	
}
