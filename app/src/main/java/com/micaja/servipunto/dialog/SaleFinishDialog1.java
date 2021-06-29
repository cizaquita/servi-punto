package com.micaja.servipunto.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.DecimalInputFilter;
import com.micaja.servipunto.utils.SalesCodes;

import java.util.ArrayList;

public class SaleFinishDialog1 extends Dialog implements
		android.view.View.OnClickListener, TextWatcher {

	private Context context;
	private EditText etxtPayAmount, etxtDebtAmount, etxtInvoiceNum;
	private Button btnDiscSave, btnDiscCancel;
	private TextView txtTotalDebt, txtTotalPurchase, txtTitle, txtChange;
	private LinearLayout llInvoice, llChange;
	private LinearLayout llDebt;
	private double totalAmount;
	private ServiApplication appContext;
	private Handler uiHandler;
	private double totalDebt;
	private String type;
	private boolean isValid;
	private ImageView imgClose;

	public SaleFinishDialog1(Context context, double totalAmount,
			double totalDebt, String type, Handler uiHandler) {
		super(context);
		this.context = context;
		this.totalAmount = totalAmount;
		this.totalDebt = totalDebt;
		this.uiHandler = uiHandler;
		this.type = type;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		appContext = (ServiApplication) context.getApplicationContext();

		initUI();
	}

	// Result of this method,registration for all UI views.
	private void initUI() {
		setContentView(R.layout.sale_finish_dialog);

		etxtPayAmount = (EditText) findViewById(R.id.etxt_PayAmount);
		etxtDebtAmount = (EditText) findViewById(R.id.etxt_DebtAmount);
		btnDiscSave = (Button) findViewById(R.id.btn_EndSaleSave);
		btnDiscCancel = (Button) findViewById(R.id.btn_EndSaleCancel);
		txtTotalDebt = (TextView) findViewById(R.id.txt_TotalDebt);
		txtTotalPurchase = (TextView) findViewById(R.id.txt_TotalPurchase);
		txtTitle = (TextView) findViewById(R.id.txt_SalesFinishTitle);
		imgClose = (ImageView) findViewById(R.id.img_close);
		etxtInvoiceNum = (EditText) findViewById(R.id.etxt_InvoiceNum);

		llDebt = (LinearLayout) findViewById(R.id.ll_editTextDebt);
		llInvoice = (LinearLayout) findViewById(R.id.ll_invoiceNum);
		llChange = (LinearLayout) findViewById(R.id.ll_Change);
		txtChange = (TextView) findViewById(R.id.txt_Change);

		if (type.equals(ConstantsEnum.SALES.code())) {
			llChange.setVisibility(View.VISIBLE);
			txtTitle.setText(context.getResources()
					.getString(R.string.end_sale));
		} else if (type.equals(ConstantsEnum.ORDERS.code())) {
			txtTitle.setText(context.getResources().getString(
					R.string.inventory_add));
			llInvoice.setVisibility(View.VISIBLE);
			try {
				if (appContext.getOrder_reciveinvoice().length()>1) {
					etxtInvoiceNum.setText(appContext.getOrder_reciveinvoice());
					etxtInvoiceNum.setFocusable(false);
					etxtInvoiceNum.setEnabled(false);
				}
			} catch (Exception e) {
			}
		}

		txtTotalDebt.setText(context.getResources().getString(
				R.string.sales_total_debt)
				+ " " + CommonMethods.getNumSeparator(totalDebt) + "");
		txtTotalPurchase.setText(context.getResources().getString(
				R.string.sales_total_purchase)
				+ " " + CommonMethods.getNumSeparator(totalAmount) + "");

		btnDiscSave.setOnClickListener(this);
		btnDiscCancel.setOnClickListener(this);
		imgClose.setOnClickListener(this);
		etxtPayAmount.setText(CommonMethods.getNumSeparator(totalAmount));
		etxtPayAmount.setFilters(new InputFilter[] { new DecimalInputFilter(8,
				2) });
		// etxtDebtAmount.setFilters(new InputFilter[] {new
		// DecimalInputFilter(8,2)});

		if (appContext.getClientDTO() == null
				&& type.equals(ConstantsEnum.SALES.code()))
			llDebt.setVisibility(View.GONE);

		etxtDebtAmount.setEnabled(false);

		etxtPayAmount.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_EndSaleSave:
			endSale();
			break;

		case R.id.btn_EndSaleCancel:
			this.dismiss();
			break;
		case R.id.img_close:

			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void endSale() {
		String payAmount = etxtPayAmount.getText().toString().trim();
		String debtAmount = etxtDebtAmount.getText().toString().trim();
		String invoiceNum = etxtInvoiceNum.getText().toString().trim();
		double total = 0;
		StringBuffer stringBuffer = new StringBuffer();
		if (validateFields(stringBuffer, payAmount, debtAmount, invoiceNum)) {
			total = CommonMethods.getDoubleFormate(payAmount)
					+ CommonMethods.getDoubleFormate(debtAmount);

			if (totalAmount <= CommonMethods.getDoubleFormate(payAmount)) {
				this.dismiss();
				sendMessage(String.valueOf(totalAmount), "0", invoiceNum);
			} else if (totalAmount == total) {
				this.dismiss();
				sendMessage(payAmount, debtAmount, invoiceNum);
			} else
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.invalid_sales_payment_msg));

		} else
			CommonMethods.showcustomdialogbox(context,
					getContext().getString(R.string.oops_errmsg),
					stringBuffer.toString(), null);
	}

	// Result of this method, Validation confirmation Alert using stringBuffer
	private boolean validateFields(StringBuffer stringBuffer, String payAmount,
			String debtAmount, String invoiceNum) {
		isValid = true;

		if (appContext.getClientDTO() == null
				&& type.equals(ConstantsEnum.SALES.code())) {
			if (payAmount.length() == 0) {
				isValid = false;
				stringBuffer.append("\n"
						+ context.getResources().getString(
						R.string.enter_pay_amount));
			} else if (".".equals(payAmount)) {
				isValid = false;
				stringBuffer.append("\n"
						+ context.getResources().getString(
						R.string.invalid_pay_amount));
			}

			else if (totalAmount > CommonMethods.getDoubleFormate(etxtPayAmount
					.getText().toString().trim())) {
				isValid = false;
				stringBuffer.append("\n"
						+ context.getResources().getString(
								R.string.occa_pay_amount));
			}
		} else {
			if (payAmount.length() == 0) {
				isValid = false;
				stringBuffer.append("\n"
						+ context.getResources().getString(
								R.string.enter_pay_amount));
			} else if (".".equals(payAmount)) {
				isValid = false;
				stringBuffer.append("\n"
						+ context.getResources().getString(
								R.string.invalid_pay_amount));
			}

			if (debtAmount.length() == 0) {
				isValid = false;
				stringBuffer.append("\n"
						+ context.getResources().getString(
								R.string.enter_debt_amount));
			} else if (".".equals(debtAmount)) {
				isValid = false;
				stringBuffer.append("\n"
						+ context.getResources().getString(
								R.string.invalid_debt_amount));
			}
			if (totalAmount < CommonMethods.getDoubleFormate(etxtPayAmount.getText().toString().trim())) {
				isValid = false;
				stringBuffer.append("\n" + context.getResources().getString(R.string.msj_amount_exceed_totalamount));
			}

		}

		return isValid;
	}

	private void sendMessage(String payAmount, String debtAmount,
			String invoiceNum) {

		Message msg = new Message();

		if (type.equals(ConstantsEnum.ORDERS.code())) {
			msg.arg1 = SalesCodes.ADD_INVENTORY.code();
			msg.obj = payAmount + ":" + debtAmount + ":" + invoiceNum;
		} else {
			msg.arg1 = SalesCodes.CLIENT_SALE_END.code();
			msg.obj = payAmount + ":" + debtAmount;
		}

		uiHandler.sendMessage(msg);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable string) {
		String pay = etxtPayAmount.getText().toString().trim();
		if (!"".equals(pay) && !".".equals(pay)) {
			if (totalAmount < CommonMethods.getDoubleFormate(pay)) {
				// CommonMethods.showCustomToast(context,
				// context.getResources().getString(R.string.invalid_payment_msg));
				double bal = CommonMethods.getDoubleFormate(pay) - totalAmount;
				txtChange.setText(CommonMethods.getRoundedVal(bal) + "");
				etxtDebtAmount.setText("0");
			} else {
				txtChange.setText("0");
				if (totalAmount > CommonMethods.getDoubleFormate(pay)) {
					double bal = totalAmount
							- CommonMethods.getDoubleFormate(pay);
					etxtDebtAmount.setText(CommonMethods.getRoundedVal(bal)
							+ "");

				} else if (totalAmount == CommonMethods.getDoubleFormate(pay)) {
					etxtDebtAmount.setText("0");
					txtChange.setText("0");
				}

			}
		} else {
			txtChange.setText("0");
			etxtDebtAmount.setText(totalAmount + "");
		}

	}

}