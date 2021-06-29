package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.utils.CommonMethods;

public class OopsErrorDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;
	private TextView txtErrmsg, errorMsg;
	private Button btnOK;
	ServiApplication appContext;

	public OopsErrorDialog(Context context) {
		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.oops_error_dialog);
		txtErrmsg = (TextView) findViewById(R.id.txt_errmsg);
		errorMsg = (TextView) findViewById(R.id.error_msg);
		btnOK = (Button) findViewById(R.id.btn_OK);
		btnOK.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_OK:
			appContext.clearActivityList();
			CommonMethods.openNewActivity(context, MenuActivity.class);

			break;

		default:
			break;
		}

	}

}
