/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.utils.CommonMethods;

public class BaseActivity extends Activity {

	ServiApplication appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (ServiApplication) getApplicationContext();

	}

	@Override
	public void onBackPressed() {
		System.out.println("LOG...  LIST SIZE "
				+ appContext.getActivityListSize());
		try {

			appContext.getActivityList()
					.get(appContext.getActivityListSize() - 1)
					.putExtra(getResources().getString(R.string.back), "true");
			startActivity(appContext.getActivityList().get(
					appContext.getActivityListSize() - 1));
			finish();
			appContext.popActivity();
		} catch (Exception e) {
			appContext.clearActivityList();
			CommonMethods.openNewActivity(this, MenuActivity.class);
			finish();
		}
	}

	// To navigate to Menu screen from all the screens

	public void showMenuScreen(View view) {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
		finish();
	}

}
