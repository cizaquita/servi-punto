package com.micaja.servipunto.servicehandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.utils.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ServiceHandlerBadNetworkCheck {

	static String response = null;
	public final static int GET = 1;
	public final static int POST = 2;
	public final static int PUT = 3;
	public final static int DELETE = 4;
	HttpEntity httpEntity = null;
	private int timeoutConnection = 3000;
	private String TAG = "BABU";
	public SharedPreferences sharedpreferences;
	private UserDetailsDTO udto = new UserDetailsDTO();
	public ServiceHandlerBadNetworkCheck(Context context) {
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		udto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
	}

	

	public String makeServiceCall(String url, int method) {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutConnection);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse httpResponse = null;
			 if (method == GET) {
				HttpGet httpGet = new HttpGet(url);
				httpResponse = httpClient.execute(httpGet);
			} 
			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);
			ServiApplication.connectionTimeOutState = true;
			Log.v("================varahalababu>>>>>>>>", ">>>>>>>>>>>");
		} catch (UnsupportedEncodingException e) {
			ServiApplication.connectionTimeOutState = false;
		} catch (ClientProtocolException e) {
			ServiApplication.connectionTimeOutState = false;
		} catch (IOException e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return response;
	}

}
