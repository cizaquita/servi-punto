package com.micaja.servipunto.utils;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.servicehandler.ServiceHandlerBadNetworkCheck;

import android.content.Context;
import android.os.AsyncTask;

public class RetrieveFeedTask extends AsyncTask<Void, Void, Void> {

	Context context;

	public RetrieveFeedTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	protected Void doInBackground(Void... params) {
		try {
			ServiceHandlerBadNetworkCheck servicehandler = new ServiceHandlerBadNetworkCheck(context);
			servicehandler.makeServiceCall(ServiApplication.SOAP_URL,ServiceHandlerBadNetworkCheck.GET);
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	protected void onPostExecute(String feed) {
	}

}
