package com.micaja.servipunto.utils;

import com.micaja.servipunto.ServiApplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectivity {
	public static int singalStenths = 0;
	public static String strength = null;

	private static final String MOBILE = "Mobile";
	private static final String WIFI = "wifi";

	/**
	 * Returns boolean. context argument is specifies context of activity or
	 * application context.
	 * 
	 * <p>
	 * This method to verify the availability of Internet on the device
	 * 
	 * @param context
	 *            context of application or activity
	 */

	public static boolean netWorkAvailability(final Context context) {
		boolean networkAvailability = false;

		final ConnectivityManager cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfos = cManager.getActiveNetworkInfo();

		if (networkInfos != null && networkInfos.isConnected()&& networkInfos.isAvailable()){
			new RetrieveFeedTask(context).execute();
			if (ServiApplication.connectionTimeOutState) {
				networkAvailability = true;
				ServiApplication.connectionTimeOutState=true;
			} else {
				ServiApplication.connectionTimeOutState=false;
				networkAvailability = false;
			}
		}

		return networkAvailability;
	}

	/**
	 * Returns network type. context argument is specifies context of activity
	 * or application context.
	 * 
	 * <p>
	 * This method to verify the availability of network type.
	 * 
	 * @param context
	 *            context of application or activity
	 */

	public static String netWorkType(final Context context) {
		String networkType = null;

		final ConnectivityManager cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo[] networkInfos = cManager.getAllNetworkInfo();

		for (int i = 0; i < networkInfos.length; i++) {
			if (networkInfos[i].getType() == ConnectivityManager.TYPE_MOBILE
					&& networkInfos[i].isAvailable()) {
				networkType = MOBILE;
			}

			if (networkInfos[i].getType() == ConnectivityManager.TYPE_WIFI
					&& networkInfos[i].isAvailable()) {
				networkType = WIFI;
			}
		}

		return networkType;
	}
}
