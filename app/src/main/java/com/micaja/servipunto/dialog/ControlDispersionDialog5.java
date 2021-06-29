package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.utils.CommonMethods;

public class ControlDispersionDialog5 extends Dialog implements OnClickListener {
	Boolean Isvalid;
	private ImageView imgclose;
	private Context context;
	private EditText etxtdispesion_key;
	private TextView txtv_dispersinmssg;
	String key;
	private Button btndispersion_save, btndispersion_cancel;

	public ControlDispersionDialog5(Context context) {
		super(context);
		this.context = context;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		inItUI();
	}

	private void inItUI() {
		setContentView(R.layout.control_de_dispersiondialog1);
		etxtdispesion_key = (EditText) findViewById(R.id.etxt_dispesion_key);

		txtv_dispersinmssg = (TextView) findViewById(R.id.txtv_dispersin_mssg);

		btndispersion_save = (Button) findViewById(R.id.btn_dispersion_save);
		btndispersion_cancel = (Button) findViewById(R.id.btn_dispersion_cancel);
		imgclose = (ImageView) findViewById(R.id.img_close);
		btndispersion_save.setOnClickListener(this);
		btndispersion_cancel.setOnClickListener(this);
		imgclose.setOnClickListener(this);

		loadUi();

	}

	private void loadUi() {
		txtv_dispersinmssg.setText(context.getResources().getString(
				R.string.dispersion7_dialog_mssg)
				+ ":");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dispersion_save:
			validateAndSave();
			break;
		case R.id.btn_dispersion_cancel:
			this.dismiss();

			break;
		case R.id.img_close:
			this.dismiss();
		default:
			break;
		}

	}

	private void validateAndSave() {

		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer)) {
			CommonMethods.showcustomdialogbox(context,
					getContext().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
		} else {
			this.cancel();
			ControlDispersionDialog6 dialog8 = new ControlDispersionDialog6(
					context);
			dialog8.show();
			dialog8.setCancelable(false);
		}

	}

	private boolean validateFields(StringBuffer stringBuffer) {
		key = etxtdispesion_key.getText().toString().trim();
		Isvalid = true;
		if (key.length() == 0) {
			Isvalid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.Please_enter_key));
		} else {
			if (key.length() < 6) {
				Isvalid = false;
				stringBuffer.append("\n"
						+ context.getResources().getString(
								R.string.please_enter_confirmValue));
			}
		}
		return Isvalid;
	}
}
