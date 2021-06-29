package com.micaja.servipunto.servicehandler;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class VersionTask extends AsyncTask<String, String, String> {
	private Httpget get;
	private Handler handler;

	public VersionTask(Httpget get, Handler uiHandler) {
		this.get = get;
		this.handler = uiHandler;
	}

	@Override
	protected String doInBackground(String... arg0) {
		String res = null;
		res = get.execute();
		return res;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		Message msg = new Message();
		if (result == null)
			msg.arg1 = 0;
		else 
		{
			System.out.println("Response " + result);
			msg.arg1 = 1;
			msg.obj = result;
		}
		handler.sendMessage(msg);
	}
}
