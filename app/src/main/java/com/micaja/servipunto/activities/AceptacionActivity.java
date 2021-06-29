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
import com.micaja.servipunto.dialog.AcceptanceDialog1;
import com.micaja.servipunto.dialog.Efectivo_MenuDialog;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;

public class AceptacionActivity extends BaseActivity implements OnClickListener {
	ServiApplication appContext;
	EditText edit_acceptence_value, edit_distribution_partner, ed_pin_dealer,
			ed_acceptence_key;
	Button btn_acceptence_cancel, btn_acceptence_cnfm;
	private String acceptValue, distPartner, pinDealer, acceptKey;
	private boolean isValid;
	private double value;
	private TextView txtProductName;
	private AcceptEfectivoDTO aedto = new AcceptEfectivoDTO();
	private SharedPreferences sharedpreferences;
	private Intent intent;
	UserDetailsDTO dto;
	private int count = 0;
	private Context context1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, AceptacionActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));

		InitUi();
		// onBackPressed();
	}

	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 142536) {
				InitUi();
			}
		}
	};

	public void showMenuScreen(View view) {
		appContext.clearActivityList();
		CommonMethods.openNewActivity(this, MenuActivity.class);
		finish();
	}

	private void InitUi() {
		setContentView(R.layout.acceptance_activity);
		edit_acceptence_value = (EditText) findViewById(R.id.edit_acceptence_value);
		edit_distribution_partner = (EditText) findViewById(R.id.edit_distribution_partner);
		ed_pin_dealer = (EditText) findViewById(R.id.ed_pin_dealer);
		ed_acceptence_key = (EditText) findViewById(R.id.ed_acceptence_key);
		btn_acceptence_cancel = (Button) findViewById(R.id.btn_acceptence_cancel);
		btn_acceptence_cnfm = (Button) findViewById(R.id.btn_acceptence_cnfm);
		btn_acceptence_cancel.setOnClickListener(this);
		btn_acceptence_cnfm.setOnClickListener(this);

		txtProductName = (TextView) findViewById(R.id.txt_ProductName);
		txtProductName.setText(getResources().getString(R.string.aceptacions));
		
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_acceptence_cnfm:
			validateAndSave();
			break;
		case R.id.btn_acceptence_cancel:
			backButtonHandler();
			finish();
			break;

		default:
			break;
		}
	}

	private void validateAndSave() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(AceptacionActivity.this,
					getResources().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			AcceptanceDialog1 dialog = new AcceptanceDialog1(this, aedto,
					uiHandler);
			dialog.show();
			dialog.setCancelable(false);
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

	private boolean validateFields(StringBuffer stringBuffer) {
		acceptValue = edit_acceptence_value.getText().toString().trim();
		acceptKey = ed_acceptence_key.getText().toString().trim();
		pinDealer = ed_pin_dealer.getText().toString().trim();
		distPartner = edit_distribution_partner.getText().toString().trim();
		isValid = true;

		aedto.setAmount(acceptValue);
		aedto.setDistribuidor(distPartner);
		aedto.setDiskey(pinDealer);
		aedto.setPassword(acceptKey);

		if (acceptValue.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.value_enter));
		} else if (Double.parseDouble(acceptValue) < 1000
				|| Double.parseDouble(acceptValue) > 50000000) {
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
		if (acceptKey.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.type_key));
		} else if (acceptKey.length() < 6) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.select_six_digits));
			isValid = false;
		} else {

		}
		return isValid;
	}

}
