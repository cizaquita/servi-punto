package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ClientDAO;
import com.micaja.servipunto.database.dto.ClientDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.DecimalInputFilter;
import com.micaja.servipunto.utils.SalesCodes;

public class SaleFinishDeliveryDialog extends Dialog implements
		View.OnClickListener,
		DialogInterface.OnClickListener, TextWatcher {

	private Context context;
	private EditText etxtPayAmount, etxtDebtAmount, etxtInvoiceNum, etxtDocument, etxtPhone, etxtNameClient,
			etxtAddress;

	private Button btnDiscSaveCusmoter, btnDiscCancel,btnDiscSaveDelivery;
	private TextView txtTotalDebt, txtTotalPurchase, txtTitle, txtChange;
	private LinearLayout llInvoice, llChange;
	private ClientDTO localClientDTO;
	private double totalAmount;
	ServiApplication appContext;
	private Handler uiHandler;
	private double totalDebt;
	private String type;
	private boolean isValid;
	private ImageView imgClose;
	private int typeDeliveryTrans;

	public SaleFinishDeliveryDialog(Context context, double totalAmount,
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
		setContentView(R.layout.sale_delivery_finish_dialog);

		etxtPayAmount = (EditText) findViewById(R.id.etxt_PayAmount);
		etxtDebtAmount = (EditText) findViewById(R.id.etxt_DebtAmount);
		btnDiscSaveCusmoter = (Button) findViewById(R.id.btn_EndSaleSaveCustomer);
		btnDiscSaveDelivery = (Button) findViewById(R.id.btn_EndSaleSaveDelivery);
		btnDiscCancel = (Button) findViewById(R.id.btn_EndSaleCancel);
		txtTotalDebt = (TextView) findViewById(R.id.txt_TotalDebt);
		txtTotalPurchase = (TextView) findViewById(R.id.txt_TotalPurchase);
		txtTitle = (TextView) findViewById(R.id.txt_SalesFinishTitle);
		imgClose = (ImageView) findViewById(R.id.img_close);
		etxtInvoiceNum = (EditText) findViewById(R.id.etxt_InvoiceNum);
		llInvoice = (LinearLayout) findViewById(R.id.ll_invoiceNum);
		llChange = (LinearLayout) findViewById(R.id.ll_Change);
		txtChange = (TextView) findViewById(R.id.txt_Change);
		etxtNameClient = (EditText) findViewById(R.id.etxt_name_customer);
		etxtPhone = (EditText) findViewById(R.id.etxt_telephone_customer);
		etxtAddress = (EditText) findViewById(R.id.etxt_adresss_customer);
		etxtDocument = (EditText) findViewById(R.id.etxt_document_customer);

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

		btnDiscSaveCusmoter.setOnClickListener(this);
		btnDiscSaveDelivery.setOnClickListener(this);
		btnDiscCancel.setOnClickListener(this);
		imgClose.setOnClickListener(this);

		etxtPayAmount.setFilters(new InputFilter[] { new DecimalInputFilter(8,
				2) });

		etxtDebtAmount.setEnabled(false);

		etxtPayAmount.addTextChangedListener(this);

		if (appContext.getClientDTO() != null)
			loadClietToViws();

		etxtDocument.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					if (etxtDocument.length() > 0) {
						getDataFromClient(1,etxtDocument.getText()
								.toString().trim());
					}
				}
				return false;
			}
		});

		etxtDocument.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
				} else {
					if (etxtDocument.length() > 0
							&& etxtNameClient.getText().length() == 0) {
						getDataFromClient(1, etxtDocument.getText()
								.toString().trim());
					}
				}
			}
		});

		etxtPhone.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					if (etxtPhone.length() > 0) {
						getDataFromClient(2, etxtPhone.getText()
								.toString().trim());
					}
				}
				return false;
			}
		});

		etxtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
				} else {
					if (etxtPhone.length() > 0
							&& etxtNameClient.getText().length() == 0) {
						getDataFromClient(2, etxtPhone.getText()
								.toString().trim());
					}
				}
			}
		});


	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_EndSaleSaveCustomer:

			typeDeliveryTrans = 1;
			endSale();
			break;
		case R.id.btn_EndSaleSaveDelivery:
			typeDeliveryTrans = 2;
			endSale();
			break;
		case R.id.btn_EndSaleCancel:
			if (localClientDTO != null && appContext.getClientDTO() != null) {
				appContext.setClientDTO(null);
				totalDebt = 0;
			}
			this.dismiss();
			break;
		case R.id.img_close:

			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void loadClietToViws(){
		try {
			ClientDTO clientDTO = appContext.getClientDTO();
			etxtAddress.setText(clientDTO.getAddress());
			etxtPhone.setText(clientDTO.getTelephone());
			etxtDocument.setText(clientDTO.getCedula());
			etxtNameClient.setText(clientDTO.getName());
			etxtPayAmount.setFocusable(true);
			etxtPayAmount.requestFocus();
			InputMethodManager imm = (InputMethodManager) appContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(etxtPayAmount, InputMethodManager.SHOW_IMPLICIT);
			etxtPhone.setEnabled(false);
			etxtDocument.setEnabled(false);
			etxtPhone.setBackgroundResource(R.drawable.inventory_popup_input_filed_disable);
			etxtDocument.setBackgroundResource(R.drawable.inventory_popup_input_filed_disable);


		}
		catch (Exception e ) {
			e.printStackTrace();
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
			if (typeDeliveryTrans == 1)
				msg.arg1 = SalesCodes.LEND_TO_CUSTOMER.code();
			else
				msg.arg1 = SalesCodes.LEND_TO_DELIVERY.code();

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

	private void getDataFromClient(int documentOrPhone,String txt){
		ClientDTO clientDTO;
		if (documentOrPhone == 1)
			localClientDTO = ClientDAO.getInstance().getRecordsByClientCedula(DBHandler.getDBObj(Constants.READABLE),txt);
		else
			localClientDTO = ClientDAO.getInstance().getRecordsByClientPhone(DBHandler.getDBObj(Constants.READABLE),txt);
		if (localClientDTO.getClientID() != null){
			appContext.setClientDTO(localClientDTO);
			totalDebt = CommonMethods.getDoubleFormate(appContext.getClientDTO().getBalanceAmount());
			initUI();
		}

	}
}