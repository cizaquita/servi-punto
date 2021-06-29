package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.micaja.servipunto.R;

public class ControlDispersionDialog6 extends Dialog implements OnClickListener {

	private Button btn_dispersion2leave;
	private Context context;

	public ControlDispersionDialog6(Context context) {

		super(context);
		this.context = context;

	}

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
		inItUI();
	}

	private void inItUI() {
		setContentView(R.layout.control_de_dispersiondialog3);
		btn_dispersion2leave = (Button) findViewById(R.id.btn_dispersion2_leave);
		btn_dispersion2leave.setOnClickListener(this);
		loadUI();

	}

	private void loadUI() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dispersion2_leave:
			this.dismiss();

			break;

		default:
			break;
		}

	}

}
