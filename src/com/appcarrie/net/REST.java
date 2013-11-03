package com.appcarrie.net;


import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Pair;

import com.appcarrie.utils.YLog;


public class REST {
	private final static String TAG = "REST";
	
	public static int post( Pair<String, String> [] header, JSONObject obj, String url, NetCallback callback) throws Exception {
		
		HttpClient httpclient = new DefaultHttpClient();
		
		HttpPost httppost;
		try {
			httppost = new HttpPost(url);
		} catch (Exception e) {
			if ( callback != null)
				callback.onComplete( HttpStatus.SC_BAD_REQUEST,null);
			return HttpStatus.SC_BAD_REQUEST;
		}

		if ( obj!=null ) {
			YLog.i( TAG, "Post send : " +url+" : "+ obj.toString().getBytes().length + " bytes ");
			StringEntity entity = new StringEntity(obj.toString(), HTTP.UTF_8);
			entity.setContentType("application/json; charset=utf-8");
			httppost.setEntity(entity);
		} else
			YLog.i( TAG, "Post send : " +url);
		
		for ( int i =0 ; header != null && i < header.length ; i++) 
			httppost.addHeader(header[i].first, header[i].second);
		HttpResponse resp = httpclient.execute(httppost);
		String result = null;
		if(resp.getEntity()!=null){
			result = EntityUtils.toString(resp.getEntity(),HTTP.UTF_8);
		}
		
		if(result!=null){
			YLog.i( TAG, "Post "+resp.getStatusLine().getStatusCode()+" : "+url+" : " + result.toString().getBytes().length + " bytes ");
		}else{
			YLog.i( TAG, "Post "+resp.getStatusLine().getStatusCode()+" : "+url);
		}
		if ( callback != null )
			callback.onComplete( resp.getStatusLine().getStatusCode(), result);
		httpclient.getConnectionManager().shutdown();
		return resp.getStatusLine().getStatusCode();
	}
	
	
	public static int put( Pair<String, String> [] header, JSONObject obj, String url) throws Exception {
		return put(header, obj, url, null);
	}
	
	public static int put( Pair<String, String> [] header, JSONObject obj, String url, NetCallback callback) throws Exception {

		HttpClient httpclient = new DefaultHttpClient();
		
		HttpPut method;
		try {
			method = new HttpPut(url);
		} catch (Exception e) {
			if ( callback != null)
				callback.onComplete( HttpStatus.SC_BAD_REQUEST,null);
			return HttpStatus.SC_BAD_REQUEST;
		}
		if (obj != null) {
			YLog.i( TAG, "Put send : " +url+" : "+ obj.toString().getBytes().length + " bytes ");
			StringEntity entity = new StringEntity(obj.toString(), HTTP.UTF_8);
			entity.setContentType("application/json; charset=utf-8");
			method.setEntity(entity);
		} else
			YLog.i( TAG, "Put send : " +url);
		for ( int i =0 ; header != null && i < header.length ; i++) 
			method.addHeader(header[i].first, header[i].second);
		HttpResponse resp = httpclient.execute(method);
		String result = null;
		if(resp.getEntity()!=null){
			result = EntityUtils.toString(resp.getEntity(),HTTP.UTF_8);
		}
		if(result!=null){
			YLog.i( TAG, "Put "+resp.getStatusLine().getStatusCode()+" : "+url+" : " + result.toString().getBytes().length + " bytes ");
		}else{
			YLog.i( TAG, "Put "+resp.getStatusLine().getStatusCode()+" : "+url);
		}
		if ( callback != null)
			callback.onComplete( resp.getStatusLine().getStatusCode(), result);

		httpclient.getConnectionManager().shutdown();
		return resp.getStatusLine().getStatusCode();
	}
	
	
	public static int get(Pair<String, String> [] header, String url,  NetCallback callback) throws SocketTimeoutException,Exception {	// michael throws SocketTimeoutException
		YLog.i( TAG, "Get send : " + url);
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 10*1000;
		int timeoutSocket = 20*1000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpGet method;
		try {
			method = new HttpGet(url);
		} catch (Exception e) {
			if ( callback != null)
				callback.onComplete( HttpStatus.SC_BAD_REQUEST,null);
			return HttpStatus.SC_BAD_REQUEST;
		}
		for ( int i =0 ; header != null && i < header.length; i++) 
			method.addHeader(header[i].first, header[i].second);
		HttpResponse resp = httpclient.execute(method);
		String result = null;
		if(resp.getEntity()!=null){
			result = EntityUtils.toString(resp.getEntity(),HTTP.UTF_8);
		}
		if(result!=null){
			YLog.i( TAG, "Get "+resp.getStatusLine().getStatusCode()+" : "+url+" : " + result.toString().getBytes().length + " bytes ");
		}else{
			YLog.i( TAG, "Get "+resp.getStatusLine().getStatusCode()+" : "+url);
		}
				if ( callback != null)
			callback.onComplete( resp.getStatusLine().getStatusCode(), result);
		httpclient.getConnectionManager().shutdown();
		return resp.getStatusLine().getStatusCode();
		
	}
	
	public static int delete(Pair<String, String> [] header, String url, NetCallback callback) throws SocketTimeoutException,Exception {	// michael throws SocketTimeoutException
		YLog.i( TAG, "Delete send : " + url);
		
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 10*1000;
		int timeoutSocket = 20*1000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpDelete method;
		try {
			method = new HttpDelete(url);
		} catch (Exception e) {
			if ( callback != null)
				callback.onComplete( HttpStatus.SC_BAD_REQUEST,null);
			return HttpStatus.SC_BAD_REQUEST;
		}	
		for ( int i =0 ; header != null &&  i < header.length ; i++) 
			method.addHeader(header[i].first, header[i].second);
		HttpResponse resp = httpclient.execute(method);
		String result = null;
		if(resp.getEntity()!=null){
			result = EntityUtils.toString(resp.getEntity(),HTTP.UTF_8);
		}
		if(result!=null){
			YLog.i( TAG, "Delete "+resp.getStatusLine().getStatusCode()+" : "+url+" : " + result.toString().getBytes().length + " bytes ");
		}else{
			YLog.i( TAG, "Delete "+resp.getStatusLine().getStatusCode()+" : "+url );
		}
		if ( callback != null )
			callback.onComplete( resp.getStatusLine().getStatusCode(), result);
		httpclient.getConnectionManager().shutdown();
		return resp.getStatusLine().getStatusCode();
	}
	
	public static int delete(Pair<String, String> [] header, String url) throws Exception {
		return delete ( header, url, null);
	}

}
