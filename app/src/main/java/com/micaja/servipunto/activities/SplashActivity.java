/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.GroupDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.GroupDTO;
import com.micaja.servipunto.servicehandler.Httpget;
import com.micaja.servipunto.servicehandler.VersionTask;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.DownloadAPK;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.Utils;

public class SplashActivity extends Activity {
	private boolean isScreenPaused = false;
	SharedPreferences sharedpreferences;
	Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
		editor = sharedpreferences.edit();
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Utils.context = getApplicationContext();

		setContentView(R.layout.splash);
		
		List<DTO> dishList = new ArrayList<DTO>();

		changeLanduage();

		if (NetworkConnectivity.netWorkAvailability(SplashActivity.this)) {
			deleteAPKFileFromSDCard();
			getVersionCode();
			// splashThread();
		} else {
			CommonMethods.showCustomToast(SplashActivity.this, getResources()
					.getString(R.string.network_msg));
			splashThread();
		}
		GroupDTO gDTO=new GroupDTO();
		gDTO.setGroupId("1");
		gDTO.setName("babu");
		dishList.add(gDTO);
		GroupDAO.getInstance().DBLMinsert(
				DBHandler.getDBObj(Constants.WRITABLE), dishList);
		
		if (sharedpreferences.getString("servi_db_flage", "")=="0") {
			
		}else {
			editor.putString("servi_db_flage", "1");
			editor.commit();
		}

	}

	private void changeLanduage() {
		Locale locale = new Locale("es");
		Locale.setDefault(locale);
		Configuration config1 = new Configuration();
		config1.locale = locale;
		getBaseContext().getResources().updateConfiguration(config1,
				getBaseContext().getResources().getDisplayMetrics());
	}

	private void splashThread() {
		Thread splashThread = new Thread() {
			@Override
			public void run() {
				super.run();

				try {
					sleep(Constants.SPLASH_DELAY);
				} catch (Exception e) {
					Log.e("Exception in Splash Thread: ", e.getMessage()
							.toString());
				} finally {
					if (!isScreenPaused) {
						CommonMethods.openNewActivity(SplashActivity.this,
								LoginActivity.class);
					} else {
						SplashActivity.this.finish();
					}
				}
			}
		};

		splashThread.start();
	}

	@Override
	protected void onPause() {
		super.onPause();

		isScreenPaused = true;
	}

	private void getVersionCode() {
		Httpget get = new Httpget();
		get.setURL(ServiApplication.URL + Constants.VERSION_CODE + "?appName="
				+ Constants.SERVIRESTAURENT_APK_NAME);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Constants.HEADER_KEY_CONTENT_TYPE,
				Constants.HEADER_VAL_CONTENT_TYPE);
		get.setHttpHeaders(headers);
		CommonMethods.showProgressDialog(
				getResources().getString(R.string.version_msg),
				SplashActivity.this);

		VersionTask asyncTask = new VersionTask(get, handler);
		asyncTask.execute();

	}

	private void validateAPK(int versionCode) {
		if (validateAPKVersion(versionCode)) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ "/Download");
			if (!dir.exists())
				dir.mkdir();

			if (NetworkConnectivity.netWorkAvailability(SplashActivity.this)) {
				CommonMethods.showProgressDialog(
						getResources().getString(R.string.download_msg),
						SplashActivity.this);

				DownloadAPK downloadAPKTask = new DownloadAPK(
						Environment.getExternalStorageDirectory() + "/Download",
						Constants.SERVIRESTAURENT_APK_NAME, handler);
				downloadAPKTask.execute(SplashActivity.this);
			} else {
				CommonMethods.showCustomToast(SplashActivity.this,
						getResources().getString(R.string.network_msg));
				splashThread();
			}

		} else
			splashThread();
	}

	private boolean validateAPKVersion(int newVersion) {
		try {
			int currentVersion = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionCode;

			System.out.println("New version :" + newVersion);
			System.out.println("currentVersion :" + currentVersion);

			if (newVersion > currentVersion)
				return true;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	private boolean validateSDCardSpace(long apkSize) {
		long diskSpace = CommonMethods.getAvailableExternalMemorySize();

		if (diskSpace > apkSize) {
			return true;
		}

		return false;
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (CommonMethods.progressDialog.isShowing())
				CommonMethods.progressDialog.dismiss();

			if (msg.arg1 == 0) {
				CommonMethods.showCustomToast(SplashActivity.this,
						getResources().getString(R.string.version_error));
				if (CommonMethods.checkDBExist(SplashActivity.this))
					splashThread();
			} else if (msg.arg1 == 1) {
				try {
					JSONObject jsonObj = new JSONObject((String) msg.obj);
					int status = Integer.parseInt(jsonObj.getString("status"));
					if (status == 0)
						validateAPK(Integer.parseInt(jsonObj
								.getString("version")));
					else
						splashThread();

				} catch (JSONException e) {
					e.printStackTrace();
					splashThread();
				}
			} else if (msg.arg1 == 3) {

				String res = (String) msg.obj;
				if ("success".equals(res)) {
					CommonMethods.reInstallAPK(SplashActivity.this);
				} else {
					deleteAPKFileFromSDCard();
					splashThread();
				}
			}
		}
	};

	private void deleteAPKFileFromSDCard() {
		String fileName = Environment.getExternalStorageDirectory()
				+ "/Download/" + Constants.SERVIRESTAURENT_APK_NAME;
		File file = new File(fileName);
		if (file.exists())
			file.delete();
	}
}
