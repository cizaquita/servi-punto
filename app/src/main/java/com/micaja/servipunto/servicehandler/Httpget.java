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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;

import android.util.Log;

public class Httpget {
	private DefaultHttpClient httpclient;
	private HttpGet httpget;
	// private InputStream conteudo;
	private String responseBody;
	// private boolean result=false;
	private BasicHttpParams params;

	public void setURL(String uri) {
		this.httpclient = new DefaultHttpClient();
		this.httpget = new HttpGet(uri);

	}

	public void parameterHttp(Map<String, String> vars) {
		params = new BasicHttpParams();
		if (vars != null) {
			Set<String> keys = vars.keySet();
			for (String key : keys) {
				params.setParameter(key, vars.get(key));
			}
		}
	}

	public void setParams(Map<String, String> paramsMap) {
		parameterHttp(paramsMap);

		try {
			httpget.setParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// httpget.setHeader("Accept", "application/json"); // or
	// application/jsonrequest
	// httpget.setHeader("Content-Type", "application/json");
	// http://stackoverflow.com/questions/9805300/using-httpget-returning-complete-html-code
	public void setHttpHeaders(Map<String, String> vars) {

		if (vars != null) {
			Set<String> keys = vars.keySet();
			for (String key : keys) {
				httpget.setHeader(key, vars.get(key));
			}
		}
	}

	public String execute() {
		InputSource is = null;
		try {
			HttpResponse response = httpclient.execute(httpget);
			responseBody = EntityUtils.toString(response.getEntity());

			is = new InputSource();
			is.setCharacterStream(new StringReader(this.responseBody));
			// int statusCode = response.getStatusLine().getStatusCode();

		} catch (Exception e) {

			Log.e("Httpget --getRespons():", e.getMessage());

		}
		return responseBody;
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
