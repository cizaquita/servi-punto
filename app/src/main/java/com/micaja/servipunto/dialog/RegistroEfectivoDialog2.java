package com.micaja.servipunto.dialog;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.RegistrodeEfectivoActivity;

public class RegistroEfectivoDialog2 extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private String type, serverresponds;
	private Context context;
	private Button btn_cashregisterCancel;
	private TextView txtv_errormsg;
	ServiApplication appContext;
	private Intent intent;
	Handler uiHandler;

	public RegistroEfectivoDialog2(Context context, String serverresponds,
			Handler uiHandler) {
		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
		this.serverresponds = serverresponds;
		this.uiHandler = uiHandler;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		intent = new Intent(context, RegistrodeEfectivoActivity.class);
		initUI();
	}

	private void initUI() {
		setContentView(R.layout.registro_efectivo_dialog2);
		txtv_errormsg = (TextView) findViewById(R.id.txtv_error_msg);
		btn_cashregisterCancel = (Button) findViewById(R.id.btn_cashregister_Cancel);
		btn_cashregisterCancel.setOnClickListener(this);
		try {
			JSONObject json = new JSONObject(serverresponds);
			txtv_errormsg.setText(json.getString("message"));
		} catch (Exception e) {

		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cashregister_Cancel:
			this.dismiss();
			Message msg = new Message();
			msg.arg1 = 142536;
			uiHandler.sendMessage(msg);
			break;
		default:
			break;
		}
	}

}
