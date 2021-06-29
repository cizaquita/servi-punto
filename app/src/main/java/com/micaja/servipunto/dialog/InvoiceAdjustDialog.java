/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
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
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.SalesCodes;

public class InvoiceAdjustDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private EditText etxtInvoiceNum;
	private Button btnSave, btnCancel;
	private ImageView imgClose;
	ServiApplication appContext;
	private OnDialogSaveClickListener1 litener;

	private Handler uiHandler;

	public InvoiceAdjustDialog(Context context, Handler uiHandler) {
		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
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

		setContentView(R.layout.invoice_adjust_dialog);

		etxtInvoiceNum = (EditText) findViewById(R.id.etxt_invoiceNum);

		btnSave = (Button) findViewById(R.id.btn_InvoiceSave);
		btnCancel = (Button) findViewById(R.id.btn_InvoiceCancel);
		imgClose = (ImageView) findViewById(R.id.img_close);
		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		imgClose.setOnClickListener(this);

	}

	public void setOnDialogSaveListener(OnDialogSaveClickListener1 litener) {
		this.litener = litener;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_InvoiceSave:
			if (etxtInvoiceNum.getText().toString().trim().length() == 0)
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.enter_invoice_num));
			else if ("0".equals(etxtInvoiceNum.getText().toString().trim())
					|| ".".equals(etxtInvoiceNum.getText().toString().trim()))
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.invalid_invoice_num));
			else
				sendMessage(SalesCodes.INVOICE_ADJ.code());
			break;

		case R.id.btn_InvoiceCancel:
			sendMessage(SalesCodes.INVOICE_ADJ_CANCEL.code());
			break;

		case R.id.img_close:
			sendMessage(SalesCodes.INVOICE_ADJ_CANCEL.code());
			break;

		default:
			break;
		}
	}

	private void sendMessage(int code) {
		this.dismiss();
		Message msg = new Message();
		msg.arg1 = code;
		msg.obj = etxtInvoiceNum.getText().toString().trim();
		uiHandler.sendMessage(msg);
	}
}