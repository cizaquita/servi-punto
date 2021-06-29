package com.micaja.servipunto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.micaja.servipunto.R;

public class PromotionsViewActivity extends BaseActivity {
	private WebView promotion_webview;
	private Intent intent;
	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		inItUI();

	}
	// Result of this method,registration for all UI views.
	private void inItUI() {
		intent=getIntent();
		bundle=intent.getExtras();
		setContentView(R.layout.promotions_view_activity);
		promotion_webview = (WebView) findViewById(R.id.promotion_webview);
		promotion_webview.loadUrl(bundle.getString("URL"));

	}

}
