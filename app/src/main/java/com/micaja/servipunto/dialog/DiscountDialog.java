package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.DecimalInputFilter;
import com.micaja.servipunto.utils.SalesCodes;

public class DiscountDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private EditText etxtDiscValue;

	private Button btnDiscSave, btnDiscCancel;
	private TextView txtTotalDebt, txtTotalPurchase;

	private double totalAmount;
	ServiApplication appContext;
	private Handler uiHandler;
	private double totalDebt;
	private ImageView imgClose;

	public DiscountDialog(Context context, double totalAmount,
			double totalDebt, Handler uiHandler) {
		super(context);
		this.context = context;
		this.totalAmount = totalAmount;
		this.totalDebt = totalDebt;
		this.uiHandler = uiHandler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		appContext = (ServiApplication) context.getApplicationContext();

		initUI();
	}

	private void initUI() {
		setContentView(R.layout.discount_dialog);

		etxtDiscValue = (EditText) findViewById(R.id.etxt_DiscountDialog);
		btnDiscSave = (Button) findViewById(R.id.btn_DiscountSave);
		btnDiscCancel = (Button) findViewById(R.id.btn_DiscountCancel);
		txtTotalDebt = (TextView) findViewById(R.id.txt_TotalDebt);
		txtTotalPurchase = (TextView) findViewById(R.id.txt_TotalPurchase);
		imgClose = (ImageView) findViewById(R.id.img_close);

		txtTotalDebt.setText(context.getResources().getString(
				R.string.sales_total_debt)
				+ " " + CommonMethods.getNumSeparator(totalDebt));
		txtTotalPurchase.setText(context.getResources().getString(
				R.string.sales_total_purchase)
				+ " " + CommonMethods.getNumSeparator(totalAmount));

		btnDiscSave.setOnClickListener(this);
		btnDiscCancel.setOnClickListener(this);
		imgClose.setOnClickListener(this);

		if (appContext.getDiscount() != 0)
			etxtDiscValue.setText(String.valueOf(appContext.getDiscount()));

		etxtDiscValue.setFilters(new InputFilter[] { new DecimalInputFilter(6,
				2) });

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_DiscountSave:
			compreDiscount();
			break;

		case R.id.btn_DiscountCancel:
			this.dismiss();
			break;

		case R.id.img_close:
			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void compreDiscount() {
		String enteredDiscount = etxtDiscValue.getText().toString().trim();

		if (enteredDiscount.length() > 0 && !"0".equals(enteredDiscount)
				&& !".".equals(enteredDiscount)) {
			if (totalAmount >= CommonMethods.getDoubleFormate(enteredDiscount)) {
				// double appDiscount = appContext.getDiscount();
				// appDiscount +=
				// CommonMethods.getDoubleFormate(enteredDiscount);
				// totalAmount -=
				// CommonMethods.getDoubleFormate(enteredDiscount);
				appContext.setDiscount(CommonMethods
						.getDoubleFormate(enteredDiscount));
				// appContext.setTotalAmount(totalAmount);
				this.dismiss();
				sendMessage();
			} else
				CommonMethods.showToast(context, context.getResources()
						.getString(R.string.enter_valid_value));
		} else
			CommonMethods.showToast(context,
					context.getResources()
							.getString(R.string.enter_valid_value));
	}

	private void sendMessage() {
		Message msg = new Message();
		msg.arg1 = SalesCodes.DISCOUNT.code();
		uiHandler.sendMessage(msg);
	}
}