package com.micaja.servipunto.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.dialog.DaviplatapaymentDialog1;
import com.micaja.servipunto.utils.CommonMethods;

public class DaviPlataPaymentActivity extends Activity implements
		OnClickListener {

	ServiApplication appContext;
	EditText etxt_remove_the_silver, etxt_sent_remove_the_silver,
			etxt_value_to_remove, etxt_key_selling;
	Button btn_cellularCancel, btn_cellularSave;
	private boolean isValid;
	private String puntoDeVentaId, token, valorPago, clave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		initUI();
	}

	// Result of this method,registration for all UI views.
	private void initUI() {

		setContentView(R.layout.daviplata_payment);

		etxt_remove_the_silver = (EditText) findViewById(R.id.etxt_remove_the_silver);
		etxt_sent_remove_the_silver = (EditText) findViewById(R.id.etxt_sent_remove_the_silver);
		etxt_value_to_remove = (EditText) findViewById(R.id.etxt_value_to_remove);
		etxt_key_selling = (EditText) findViewById(R.id.etxt_key_selling);
		btn_cellularCancel = (Button) findViewById(R.id.btn_cellularCancel);
		btn_cellularSave = (Button) findViewById(R.id.btn_cellularSave);
		btn_cellularCancel.setOnClickListener(this);
		btn_cellularSave.setOnClickListener(this);
	}

	// move to menu screen
	public void showMenuScreen(View view) {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
		finish();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_cellularCancel:
			appContext.clearActivityList();
			CommonMethods.openNewActivity(this, MenuActivity.class);
			finish();
			break;

		case R.id.btn_cellularSave:
			validateAndSave();

			break;

		default:
			break;
		}
	}
	// Result of this method, Validation confirmation Alert using stringBuffer
	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(DaviPlataPaymentActivity.this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			DaviplatapaymentDialog1 dialog = new DaviplatapaymentDialog1(this,
					puntoDeVentaId, token, valorPago, clave);
			dialog.show();
			dialog.setCancelable(false);
		}
	}
	// This method using for Validation purpose
	public boolean validateFields(StringBuffer stringBuffer) {
		isValid = true;
		puntoDeVentaId = etxt_remove_the_silver.getText().toString().trim();
		token = etxt_sent_remove_the_silver.getText().toString().trim();
		valorPago = etxt_value_to_remove.getText().toString().trim();
		clave = etxt_key_selling.getText().toString().trim();
		Long value;
		try {
			value = Long.parseLong(valorPago)
					% ServiApplication.daviplata_valorPago_dev;
		} catch (Exception e) {
			value = (long) 1;
		}

		if (puntoDeVentaId.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.silver1_msg));

		} else if (puntoDeVentaId.length() == 10) {

		} else {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.silver1_msg));
		}

		if (token.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.silver2_msg));

		} else if (token.length() == 6) {

		} else {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.silver2_msg));
		}

		if (valorPago.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.removevalue_msg));
		}
		if (clave.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.davipalta_key));
		} else if (clave.length() < 6) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invaliddavipalta_key));
		}
		return isValid;
	}

}
