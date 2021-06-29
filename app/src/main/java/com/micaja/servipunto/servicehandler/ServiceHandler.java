package com.micaja.servipunto.servicehandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.utils.Constants;

public class ServiceHandler {
	static String response = null;
	public final static int GET = 1;
	public final static int POST = 2;
	public final static int PUT = 3;
	public final static int DELETE = 4;
	HttpEntity httpEntity = null;
	private int timeoutConnection = ServiApplication.service_connectiontimeout;
	private String TAG = "BABU";
	public SharedPreferences sharedpreferences;
	private UserDetailsDTO udto = new com.micaja.servipunto.database.dto.UserDetailsDTO();

	public ServiceHandler(Context context) {
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		udto = com.micaja.servipunto.database.dao.UserDetailsDAO.getInstance()
				.getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
						sharedpreferences.getString("user_name", ""));
	}

	public String makeServiceCall(String url, int method) {
		return this.makeServiceCall(url, method, null);
	}

	public String makeServiceCall(String url, int method, String params) {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutConnection);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse httpResponse = null;
			Log.d("Request Method", url);
			if (method == POST) {
				HttpPost httpPost = new HttpPost(url);
				if (ServiApplication.token_flage) {
					httpPost.addHeader("X-AUTHTOKEN", udto.getAuthtoken());
					Log.d("varahalababu", "varahalabbau" + udto.getAuthtoken());
				} else {
					ServiApplication.token_flage = true;
				}
				httpPost.setHeader("Accept-Encoding", "UTF-8");
				if (params != null) {
					Log.d("Request Method", params);
					StringEntity se = new StringEntity(params, "UTF-8");
					se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
							"application/json"));
					httpPost.setEntity(se);
				}
				httpResponse = httpClient.execute(httpPost);
			} else if (method == PUT) {
				HttpPut put = new HttpPut(url);
				if (ServiApplication.token_flage) {
					put.addHeader("X-AUTHTOKEN", udto.getAuthtoken());
					Log.d("varahalababu", "varahalabbau" + udto.getAuthtoken());
					// httpPost.addHeader("X-AUTHTOKEN", value);
				} else {
					ServiApplication.token_flage = true;
				}

				put.setHeader("Accept-Encoding", "UTF-8");
				Log.d("Request Method", params);
				if (params != null) {
					StringEntity se = new StringEntity(params, "UTF-8");
					se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
							"application/json"));
					put.setEntity(se);
				}
				httpResponse = httpClient.execute(put);
			} else if (method == GET) {
				if (params != null) {
					String paramString = URLEncoder.encode(url, "UTF-8");
					url += "?" + paramString;
				}
				HttpGet httpGet = new HttpGet(url);
				if (ServiApplication.token_flage) {
					httpGet.addHeader("X-AUTHTOKEN", udto.getAuthtoken());
					Log.d("varahalababu", "varahalabbau" + udto.getAuthtoken());
					// httpPost.addHeader("X-AUTHTOKEN", value);
				} else {
					ServiApplication.token_flage = true;
				}

				httpGet.setHeader("Accept-Encoding", "UTF-8");
				httpResponse = httpClient.execute(httpGet);
			} else if (method == DELETE) {
				if (params != null) {
					String paramString = URLEncoder.encode(url, "UTF-8");
					url += "?" + paramString;
				}
				HttpDelete httpdelete = new HttpDelete(url);
				httpResponse = httpClient.execute(httpdelete);
			}
			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);
			ServiApplication.connectionTimeOutState = true;
			Log.d(TAG, response);
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
