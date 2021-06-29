/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.dialog;

import java.util.List;

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

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.AddMenusInvenDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.SalesCodes;
import com.micaja.servipunto.utils.SalesEditTypes;

public class SalesCalculatorDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	EditText etxtEnteredValue;

	private Button btnZero, btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix,
			btnSeven, btnEight, btnNine, btnBackClear, btnAllClear, btnSearch,
			btnEnter, btnCancel;

	private int position;
	private String type, selValue, screen;
	ServiApplication appContext;
	private Handler uiHandler;

	public SalesCalculatorDialog(Context context, int position, String type,
			String screen, String selValue, Handler uiHandler) {
		super(context);
		this.context = context;
		this.position = position;
		this.type = type;
		this.screen = screen;
		this.selValue = selValue;
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
		setContentView(R.layout.sales_calcu_dialog);

		btnZero = (Button) findViewById(R.id.btn_DialogCalZero);
		btnOne = (Button) findViewById(R.id.btn_DialogCalOne);
		btnTwo = (Button) findViewById(R.id.btn_DialogCalTwo);
		btnThree = (Button) findViewById(R.id.btn_DialogCalThree);
		btnFour = (Button) findViewById(R.id.btn_DialogCalFour);
		btnFive = (Button) findViewById(R.id.btn_DialogCalFive);
		btnSix = (Button) findViewById(R.id.btn_DialogCalSix);
		btnSeven = (Button) findViewById(R.id.btn_DialogCalSeven);
		btnEight = (Button) findViewById(R.id.btn_DialogCalEight);
		btnNine = (Button) findViewById(R.id.btn_DialogCalNine);
		btnBackClear = (Button) findViewById(R.id.btn_DialogCalClearOne);
		btnAllClear = (Button) findViewById(R.id.btn_DialogCalClear);
		btnSearch = (Button) findViewById(R.id.btn_DialogCalSearch);
		btnEnter = (Button) findViewById(R.id.btn_DialogCalEnter);
		btnCancel = (Button) findViewById(R.id.btn_DialogCancel);

		etxtEnteredValue = (EditText) findViewById(R.id.etxt_DialogBarcode);

		btnZero.setOnClickListener(this);
		btnOne.setOnClickListener(this);
		btnTwo.setOnClickListener(this);
		btnThree.setOnClickListener(this);
		btnFour.setOnClickListener(this);
		btnFive.setOnClickListener(this);
		btnSix.setOnClickListener(this);
		btnSeven.setOnClickListener(this);
		btnEight.setOnClickListener(this);
		btnNine.setOnClickListener(this);
		btnBackClear.setOnClickListener(this);
		btnAllClear.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnEnter.setOnClickListener(this);

		btnCancel.setOnClickListener(this);

		etxtEnteredValue.setText(selValue);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_DialogCalZero:
			setVauleToBarcode("0");
			break;

		case R.id.btn_DialogCalOne:
			setVauleToBarcode("1");
			break;

		case R.id.btn_DialogCalTwo:
			setVauleToBarcode("2");
			break;

		case R.id.btn_DialogCalThree:
			setVauleToBarcode("3");
			break;

		case R.id.btn_DialogCalFour:
			setVauleToBarcode("4");
			break;

		case R.id.btn_DialogCalFive:
			setVauleToBarcode("5");
			break;

		case R.id.btn_DialogCalSix:
			setVauleToBarcode("6");
			break;

		case R.id.btn_DialogCalSeven:
			setVauleToBarcode("7");
			break;

		case R.id.btn_DialogCalEight:
			setVauleToBarcode("8");
			break;

		case R.id.btn_DialogCalNine:
			setVauleToBarcode("9");
			break;

		case R.id.btn_DialogCalClearOne:
			if (etxtEnteredValue.getText().toString().trim().length() != 0)
				removeLastDigit();
			break;

		case R.id.btn_DialogCalClear:
			etxtEnteredValue.setText("");
			break;

		case R.id.btn_DialogCalSearch:
			break;

		case R.id.btn_DialogCalEnter:
			changeValueInList();
			break;

		case R.id.btn_DialogCancel:
			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void changeValueInList() {
		String value = etxtEnteredValue.getText().toString().trim();
		SelectedProddutsDTO selectedDTO;
		AddMenusInvenDTO invenDTO;
		if (value.length() > 0 && !"0".equals(value)) {
			List<DTO> list = appContext.getSelectedProducts();

			if (screen.equals(SalesEditTypes.MENUS_INVENTORY.code())) {
				invenDTO = (AddMenusInvenDTO) list.get(position);
				if (type.equals(SalesEditTypes.PRICE.code()))
					invenDTO.setSellingPrice(value);
				else if (type.equals(SalesEditTypes.QUANTITY.code()))
					invenDTO.setQuantity(value);

				list.set(position, invenDTO);
			} else {
				selectedDTO = (SelectedProddutsDTO) list.get(position);

				if (type.equals(SalesEditTypes.PRICE.code())) {
					if (ServiApplication.shale_menu) {
						selectedDTO.setSellPrice(value);
					} else {
						selectedDTO.setPrice(value);
					}
				} else if (type.equals(SalesEditTypes.QUANTITY.code())) {
					selectedDTO.setQuantity(value);
				} else if (type.equals(SalesEditTypes.UNITS.code()))
					selectedDTO.setUnits(value);

				list.set(position, selectedDTO);
			}

			appContext.setSeletedProducts(list);

			this.dismiss();

			sendMessage();
		} else
			CommonMethods.showToast(context,
					context.getResources()
							.getString(R.string.enter_valid_value));
	}

	private void sendMessage() {
		Message msg = new Message();
		msg.arg1 = SalesCodes.EDIT.code();
		uiHandler.sendMessage(msg);
	}

	private void removeLastDigit() {
		if (etxtEnteredValue.getText().toString().trim().length() == 1)
			etxtEnteredValue.setText("");
		else {
			String barcode = etxtEnteredValue.getText().toString().trim();
			barcode = barcode.substring(0, barcode.length() - 1);
			etxtEnteredValue.setText(barcode);
		}
		etxtEnteredValue.setSelection(etxtEnteredValue.getText().length());
	}

	private void setVauleToBarcode(String enterValue) {
		String barcode = etxtEnteredValue.getText().toString().trim();

		if (barcode.length() == 0)
			etxtEnteredValue.setText(enterValue);
		else {
			barcode = barcode.concat(enterValue);
			etxtEnteredValue.setText(barcode);
		}
		etxtEnteredValue.setSelection(etxtEnteredValue.getText().length());
	}
}