package com.micaja.servipunto.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;

public class RecargasActivity extends Activity {
	private ServiApplication appContext;
	private Intent intent;
	WebView myWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		appContext = (ServiApplication) getApplicationContext();

		intent = new Intent(this, MenuActivity.class);

		initUI();
		
	}

	private void initUI() {
		setContentView(R.layout.activity_recharge);
		myWebView = (WebView) this.findViewById(R.id.webView);
		getDataFromDB();
	}

	private void getDataFromDB() {
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.requestFocus(View.FOCUS_DOWN);
		myWebView.getSettings().setBuiltInZoomControls(true);
		myWebView.getSettings().setDisplayZoomControls(false);
		myWebView.loadUrl("http://www.puntored.com.co/PuntoRed/");
		myWebView.setWebViewClient(new WebViewClient() {
			// evita que los enlaces se abran fuera nuestra app en el navegador
			// de android
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

		});

	}
}
