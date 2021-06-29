package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;

public class DaviplatapaymentDialog1 extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;

	private ImageView img_close;
	private Button btn_reg_cancel, btn_reg_cnfm;
	private TextView txt_remove_the_silver, txt_get_the_money,
			txt_Value_removed;
	private String puntoDeVentaId, token, valorPago, clave;

	public DaviplatapaymentDialog1(Context context, String puntoDeVentaId,
			String token, String valorPago, String clave) {
		super(context);
		this.context = context;
		this.puntoDeVentaId = puntoDeVentaId;
		this.token = token;
		this.valorPago = valorPago;
		this.clave = clave;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.daviplata_data_confirm_dailog);

		loadUI();
	}

	private void loadUI() {

		img_close = (ImageView) findViewById(R.id.img_close);
		btn_reg_cancel = (Button) findViewById(R.id.btn_reg_cancel);
		btn_reg_cnfm = (Button) findViewById(R.id.btn_reg_cnfm);
		txt_remove_the_silver = (TextView) findViewById(R.id.txt_remove_the_silver);
		txt_get_the_money = (TextView) findViewById(R.id.txt_get_the_money);
		txt_Value_removed = (TextView) findViewById(R.id.txt_Value_removed);
		img_close.setOnClickListener(this);
		btn_reg_cancel.setOnClickListener(this);
		btn_reg_cnfm.setOnClickListener(this);

		try {
			txt_remove_the_silver.setText(puntoDeVentaId);
			txt_get_the_money.setText(token);
			txt_Value_removed.setText(valorPago);
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reg_cancel:
			this.dismiss();
			break;

		case R.id.btn_reg_cnfm:
			validateAndSave();

			break;

		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void validateAndSave() {
		this.dismiss();
		DaviplatapaymentDialog2 dialog = new DaviplatapaymentDialog2(context,
				puntoDeVentaId, token, valorPago, clave);
		dialog.show();
		dialog.setCancelable(false);
	}
}
