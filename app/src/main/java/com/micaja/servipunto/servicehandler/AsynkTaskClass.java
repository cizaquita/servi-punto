package com.micaja.servipunto.servicehandler;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class AsynkTaskClass {

	public String flage;
	public Context mContext;

	public String callcentralserver(String url, String type, String jsonobj,
			Context context) {
		mContext = context;
		try {
			if (NetworkConnectivity.netWorkAvailability(mContext)) {
				CommonMethods.showProgressDialog(
						mContext.getString(R.string.please_wait), mContext);
				return new CallCentralServer().execute(
						ServiApplication.URL + url, type.trim(), jsonobj).get();
			} else {
				CommonMethods.showCustomToast(mContext, mContext.getResources()
						.getString(R.string.no_wifi_adhoc));
				return null;
			}
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}
		return flage;
	}

	private class CallCentralServer extends AsyncTask<String, Void, String> {
		ServiceHandler servicehandler = new ServiceHandler(mContext);
		String responds;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

		}

		@Override
		protected String doInBackground(String... params) {

			if (Integer.parseInt(params[1]) == ServiceHandler.GET) {
				responds = servicehandler.makeServiceCall(params[0],
						ServiceHandler.GET);
				if (ServiApplication.connectionTimeOutState) {
					ServiApplication.responds_feed = responds;
					CommonMethods.progressDialog.dismiss();
					return responds;
				} else {
					ServiApplication.responds_feed = "";
					CommonMethods.progressDialog.dismiss();
					return "";
				}
			} else if (Integer.parseInt(params[1]) == ServiceHandler.POST) {
				responds = servicehandler.makeServiceCall(params[0],
						ServiceHandler.POST, params[2]);
				if (ServiApplication.connectionTimeOutState) {
					ServiApplication.responds_feed = responds;
					CommonMethods.progressDialog.dismiss();
					return responds;
				} else {
					ServiApplication.responds_feed = "";
					CommonMethods.progressDialog.dismiss();
					return "";
				}
			} else if (Integer.parseInt(params[1]) == ServiceHandler.DELETE) {

				responds = servicehandler.makeServiceCall(params[0],
						ServiceHandler.DELETE);
				if (ServiApplication.connectionTimeOutState) {
					ServiApplication.responds_feed = responds;
					CommonMethods.progressDialog.dismiss();
					return responds;
				} else {
					ServiApplication.responds_feed = "";
					CommonMethods.progressDialog.dismiss();
					return "";
				}
			} else if (Integer.parseInt(params[1]) == ServiceHandler.PUT) {
				responds = servicehandler.makeServiceCall(params[0],
						ServiceHandler.PUT, params[2]);
				if (ServiApplication.connectionTimeOutState) {
					ServiApplication.responds_feed = responds;
					CommonMethods.progressDialog.dismiss();
					return responds;
				} else {
					ServiApplication.responds_feed = "";
					CommonMethods.progressDialog.dismiss();
					return "";
				}
			}
			return responds;
		}
	}

}
