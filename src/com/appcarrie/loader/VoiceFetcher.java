package com.appcarrie.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;

import com.appcarrie.net.ReturnObjectThread;
import com.appcarrie.utils.Constants;

public class VoiceFetcher extends ReturnObjectThread {

	private URL mUrl;
	private String mName;
	
	public VoiceFetcher(Context context, URL url, String filename) {
		super(context);
		mUrl = url;
		mName = filename;
		
        File dir = new File(Constants.STORAGE_PATH);
        if (!dir.exists())
        	dir.mkdir();
	}
	
	@Override
	public void run() {
	    try {
	        URLConnection conexion = mUrl.openConnection();
	        conexion.connect();

	        InputStream input = new BufferedInputStream( mUrl.openStream());
	        OutputStream output = new FileOutputStream( Constants.STORAGE_PATH+"/"+mName+".mp3");

	        byte data[] = new byte[1024];


			int count;
	        while ((count = input.read(data)) != -1) {
	            output.write(data, 0, count);
	        }
	        output.flush();
	        output.close();
	        input.close();
	        
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		
		
	}

}
