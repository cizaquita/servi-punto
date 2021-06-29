package com.micaja.servipunto.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.Efectivo_MenuDialog;
import com.micaja.servipunto.dialog.RegistroEfectivoDialog1;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;

public class RegistrodeEfectivoActivity extends BaseActivity implements
		OnClickListener {

	Button btn_acceptence_cancel, btn_acceptence_cnfm;
	private Context context;
	private EditText etxtefectivo_value, etxtefectivo_confirmvalue,
			etxtefectivo_key;
	private String value, confirm_value, key;
	private boolean isValid;
	private TextView txtProductName;
	private AcceptEfectivoDTO aedto = new AcceptEfectivoDTO();
	private SharedPreferences sharedpreferences;
	private Intent intent;
	UserDetailsDTO dto;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		intent = new Intent(this, RegistrodeEfectivoActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));

		initUI();
	}

	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 142536) {
				initUI();
			}
		}
	};

	// Result of this method,registration for all UI views.
	private void initUI() {
		setContentView(R.layout.registro_efectivo_activity);
		etxtefectivo_value = (EditText) findViewById(R.id.etxt_efectivo_value);
		etxtefectivo_confirmvalue = (EditText) findViewById(R.id.etxt_efectivo_confirmvalue);
		etxtefectivo_key = (EditText) findViewById(R.id.etxt_efectivo_key);
		btn_acceptence_cancel = (Button) findViewById(R.id.btn_save);
		btn_acceptence_cnfm = (Button) findViewById(R.id.btn_cancel);

		btn_acceptence_cancel.setOnClickListener(this);
		btn_acceptence_cnfm.setOnClickListener(this);

		txtProductName = (TextView) findViewById(R.id.txt_ProductName);
		txtProductName.setText(getResources().getString(
				R.string.registro_de_efectivo));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_save:
			validateAndSave();
			break;

		case R.id.btn_cancel:
			backButtonHandler();
			finish();
			break;

		default:
			break;
		}

	}

	public void onBackPressed() {
		this.finish();
		backButtonHandler();
		return;
	}

	private void backButtonHandler() {
		Efectivo_MenuDialog dialog = new Efectivo_MenuDialog(this);
		dialog.show();
	}

	// Result of this method, Validation confirmation Alert using stringBuffer
	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(RegistrodeEfectivoActivity.this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			RegistroEfectivoDialog1 dialog = new RegistroEfectivoDialog1(this,
					aedto, uiHandler);
			dialog.show();
			dialog.setCancelable(false);

		}
	}

	// This method using for Validation purpose
	private boolean validateFields(StringBuffer stringBuffer) {
		value = etxtefectivo_value.getText().toString().trim();
		confirm_value = etxtefectivo_confirmvalue.getText().toString().trim();
		key = etxtefectivo_key.getText().toString().trim();

		aedto.setAmount(value);
		aedto.setAmount(confirm_value);
		aedto.setPassword(key);

		isValid = true;
		if (value.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.Please_enter_value));

		}
		if (confirm_value.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources()
							.getString(R.string.dispersion_confrm_value));

		} else if (value.equals(confirm_value)) {

		} else {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.value_not_match));
		}
		if (key.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_keysell));
		} else if (key.length() < 6) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(
							R.string.please_enter_confirmValue));
		}

		return isValid;

	}
}