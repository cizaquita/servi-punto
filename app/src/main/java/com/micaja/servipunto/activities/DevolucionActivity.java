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
import com.micaja.servipunto.dialog.DevolucionDialog1;
import com.micaja.servipunto.dialog.Efectivo_MenuDialog;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;

public class DevolucionActivity extends BaseActivity implements OnClickListener {
	ServiApplication appContext;
	EditText edit_return_value, edit_return_dist_partner,
			edit_return_pin_dealer, edit_return_key;
	Button btn_return_cancel, btn_return_cnfm;
	private String returnValue, distPartner, pinDealer, returnKey;
	private boolean isValid;
	private TextView txtProductName,txt_return_acceptance;
	private AcceptEfectivoDTO aedto = new AcceptEfectivoDTO();
	private SharedPreferences sharedpreferences;
	private Intent intent;
	UserDetailsDTO dto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, DevolucionActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		InitUi();

	}

	private void InitUi() {
		setContentView(R.layout.devolucion_activity);
		edit_return_value = (EditText) findViewById(R.id.edit_return_value);
		edit_return_dist_partner = (EditText) findViewById(R.id.edit_return_dist_partner);
		edit_return_pin_dealer = (EditText) findViewById(R.id.edit_return_pin_dealer);
		edit_return_key = (EditText) findViewById(R.id.edit_return_key);
		btn_return_cancel = (Button) findViewById(R.id.btn_return_cancel);
		btn_return_cnfm = (Button) findViewById(R.id.btn_return_cnfm);
		btn_return_cancel.setOnClickListener(this);
		btn_return_cnfm.setOnClickListener(this);

		txtProductName = (TextView) findViewById(R.id.txt_ProductName);
		txtProductName.setText(getResources().getString(R.string.devolucions));
		txt_return_acceptance=(TextView)findViewById(R.id.txt_return_acceptance);
		txt_return_acceptance.setText("Autenticar Devolución");
	}

	public void showMenuScreen(View view) {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
		finish();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return_cnfm:
			validateAndSave();
			break;
		case R.id.btn_return_cancel:
			backButtonHandler();
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
		backButtonHandler();
		return;
	}

	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 142536) {
				InitUi();
			}
		}
	};

	private void backButtonHandler() {
		Efectivo_MenuDialog dialog = new Efectivo_MenuDialog(this);
		dialog.show();
	}

	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(DevolucionActivity.this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			DevolucionDialog1 dialog = new DevolucionDialog1(this, aedto,
					uiHandler);
			dialog.show();
			dialog.setCancelable(false);
		}

	}

	private boolean validateFields(StringBuffer stringBuffer) {
		returnValue = edit_return_value.getText().toString().trim();
		returnKey = edit_return_key.getText().toString().trim();
		pinDealer = edit_return_pin_dealer.getText().toString().trim();
		distPartner = edit_return_dist_partner.getText().toString().trim();
		isValid = true;
		aedto.setAmount(returnValue);
		aedto.setDistribuidor(distPartner);
		aedto.setDiskey(pinDealer);
		aedto.setPassword(returnKey);

		if (returnValue.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.value_enter));

		} else if (Double.parseDouble(returnValue) < 1000
				|| Double.parseDouble(returnValue) > 50000000) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.min_and_max_value));
			isValid = false;
		}
		if (distPartner.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_dealer));
		}
		if (pinDealer.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.pin_distributor));
		} else if (pinDealer.length() < 4) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(
							R.string.distributor_pin_incorrect));
		}
		if (returnKey.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.type_key));
		} else if (returnKey.length() < 6) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.select_six_digits));
			isValid = false;
		}
		return isValid;
	}

}
