package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.SalesCodes;

public class ActualBalanceDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private EditText etxtActualBalance;
	private Button btnOK;
	private Handler uiHandler;
	private String actualBal;
	private ImageView imgClose;

	public ActualBalanceDialog(Context context, Handler uiHandler) {
		super(context);
		this.context = context;
		this.uiHandler = uiHandler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	// Result of this method,registration for all UI views.
	private void initUI() {

		setContentView(R.layout.actual_balance);
		etxtActualBalance = (EditText) findViewById(R.id.etxt_ActualBalance);
		btnOK = (Button) findViewById(R.id.btn_OK);
		imgClose = (ImageView) findViewById(R.id.img_close);
		btnOK.setOnClickListener(this);
		imgClose.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_OK:
			sendMessage();
			break;
		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void sendMessage() {
		if (!validate()) {
			this.dismiss();

			Message msg = new Message();
			msg.arg1 = SalesCodes.SHOP_CLOSE_ACTUAL.code();
			msg.obj = actualBal;
			uiHandler.sendMessage(msg);
		}
	}

	// Validation
	private boolean validate() {
		actualBal = etxtActualBalance.getText().toString().trim();

		if ("".equals(actualBal)) {
			CommonMethods.showCustomToast(context,
					context.getString(R.string.error_actual_balance));
			return true;
		}
		return false;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
	}
}