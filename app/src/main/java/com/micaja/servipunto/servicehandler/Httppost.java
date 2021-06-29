/**
 * 
 * @author 
 * Ybrant Digital
 * Copyright (C) Ybrant Digital
 * http://www.ybrantdigital.com
 *
 */
package com.micaja.servipunto.servicehandler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class Httppost {

	private ArrayList<BasicNameValuePair> entityList;
	private DefaultHttpClient httpclient;
	private HttpPost httppost;
	private InputStream responseStream;
	private String responseStr;

	public void setURL(String url) {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		this.httpclient = new DefaultHttpClient(httpParameters);
		this.httppost = new HttpPost(url);

	}

	public void setEntityParams(Map<String, String> vars) {
		this.entityList = new ArrayList<BasicNameValuePair>();
		if (vars != null) {
			Set<String> keys = vars.keySet();
			for (String key : keys) {
				entityList.add(new BasicNameValuePair(key, vars.get(key)));
			}
		}
	}

	public void setEntities(Map<String, String> entities) {
		setEntityParams(entities);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(entityList));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void setStringEntity(String entity) {
		try {
			StringEntity stringEntity = new StringEntity(entity, "UTF-8");
			httppost.setEntity(stringEntity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void setHeaders(Map<String, String> headers) {
		if (headers != null) {
			Set<String> keys = headers.keySet();
			for (String key : keys) {
				httppost.setHeader(key, headers.get(key));
			}
		}
	}

	public InputStream execute() {
		InputStream is = null;
		try {
			/** Perform the actual HTTP POST */
			HttpResponse response = httpclient.execute(httppost);
			// if (response != null) {
			// if (response.getStatusLine().getStatusCode() == 204)
			// result = true;
			// }

			HttpEntity entity = response.getEntity();
			responseStream = entity.getContent();
			this.responseStr = convertStreamToString(responseStream);
			is = new FileInputStream(this.responseStr);
			// is.setCharacterStream(new StringReader(this.responseStr));
			// int statusCode = response.getStatusLine().getStatusCode();

		} catch (Exception e) {

			Log.e("Httppost --execute: ", e.getMessage());
		}
		return is;
	}

	// This is method is used to convert Input Stream to string
	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
}
