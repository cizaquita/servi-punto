package com.micaja.servipunto.dialog;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.MenuActivity;
import com.micaja.servipunto.database.dto.ConsultarFacturasDTO;
import com.micaja.servipunto.utils.CommonMethods;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PaymentInAdvance extends Dialog implements android.view.View.OnClickListener {

	private Context context;
	private TextView txtErrmsg, errorMsg,txt_dialog_title;
	private Button btnOK, btn_cancil;
	ServiApplication appContext;
	String Value;
	ConsultarFacturasDTO dto;

	public PaymentInAdvance(Context context, ConsultarFacturasDTO dto, String Value) {
		super(context);
		this.context = context;
		this.dto = dto;
		appContext = (ServiApplication) context.getApplicationContext();
		this.Value = Value;
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
		txt_dialog_title = (TextView) findViewById(R.id.txt_dialog_title);
		btnOK = (Button) findViewById(R.id.btn_OK);
		btn_cancil = (Button) findViewById(R.id.btn_cancil);
		btn_cancil.setOnClickListener(this);
		btn_cancil.setVisibility(View.VISIBLE);
		btnOK.setOnClickListener(this);
		errorMsg.setText(Value);
		if (Value.contains("No tiene facturas")) {
			txt_dialog_title.setVisibility(View.INVISIBLE);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_OK:
			this.dismiss();
			RegisterPagos dialog = new RegisterPagos(context, dto.getSaldoTotal(), false);
			dialog.show();
			dialog.setCancelable(false);

			break;
		case R.id.btn_cancil:
			appContext.clearActivityList();
			CommonMethods.openNewActivity(context, MenuActivity.class);
			break;
		default:
			break;
		}

	}

}
