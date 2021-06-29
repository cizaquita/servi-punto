package com.micaja.servipunto.dialog;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.AddMenusInvenDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.SalesCodes;

public class MenusInventoryDailog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener, TextWatcher {

	private Context context;
	private EditText etxtQuntity, etxtPurchageCost, etxtSellingPrice, etxtVat;

	private Button btnDishAdd, btnCancel;
	private TextView txtNoOfItems, txtTotalItems;

	String purchagePrice, sellingPrice, quntity, vat, totalItems;

	private DTO dishDTO;
	private ImageView imgClose;
	ServiApplication appContext;
	private Handler uiHandler;

	public MenusInventoryDailog(Context context, DTO dishDTO, Handler uiHandler) {
		super(context);
		this.context = context;
		this.dishDTO = dishDTO;
		this.uiHandler = uiHandler;
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
		setContentView(R.layout.menu_inventory_dialog);

		etxtQuntity = (EditText) findViewById(R.id.etxt_MenusInvenQty);
		etxtPurchageCost = (EditText) findViewById(R.id.etxt_MakingCost);
		etxtSellingPrice = (EditText) findViewById(R.id.etxt_SellingCost);
		etxtVat = (EditText) findViewById(R.id.etxt_ManusInvaVat);
		imgClose = (ImageView) findViewById(R.id.img_close);
		txtNoOfItems = (TextView) findViewById(R.id.txt_NoOfItemsValue);
		txtTotalItems = (TextView) findViewById(R.id.txt_MenusInvTotalItem);

		btnDishAdd = (Button) findViewById(R.id.btn_EndSaleSave);
		btnCancel = (Button) findViewById(R.id.btn_EndSaleCancel);

		btnDishAdd.setText(context.getResources().getString(
				R.string.menus_add_dish));

		DishesDTO dto = (DishesDTO) dishDTO;

		etxtQuntity.setText("1");
		etxtPurchageCost.setText(dto.getDishPrice());
		etxtSellingPrice.setText(dto.getSellingCostperItem());
		etxtVat.setText(dto.getVat());
		txtNoOfItems.setText(dto.getNoOfItems());
		txtTotalItems.setText(String.valueOf(Integer.parseInt(dto
				.getNoOfItems()) * 1));

		btnDishAdd.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		etxtQuntity.addTextChangedListener(this);
		imgClose.setOnClickListener(this);

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_EndSaleSave:
			StringBuffer stringBuffer = new StringBuffer();
			if (!validate(stringBuffer))
				CommonMethods.showcustomdialogbox(context, getContext()
						.getString(R.string.oops_errmsg), stringBuffer
						.toString(), null);
			else {
				this.dismiss();
				prepareDTO();

			}
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

	private void prepareDTO() {
		List<DTO> list = appContext.getSelectedProducts();

		AddMenusInvenDTO dto = new AddMenusInvenDTO();
		for (int i = 0; i < list.size(); i++) {
			AddMenusInvenDTO invenDTO = (AddMenusInvenDTO) list.get(i);
			if (invenDTO.getDishID().equals(((DishesDTO) dishDTO).getDishId()))
				list.remove(i);
		}

		if ("".equals(vat))
			vat = "0";

		dto.setDishID(((DishesDTO) dishDTO).getDishId());
		dto.setDishName(((DishesDTO) dishDTO).getDishName());
		dto.setExpiryDays(((DishesDTO) dishDTO).getExpiryDays());
		dto.setNoOfItems(((DishesDTO) dishDTO).getNoOfItems());
		dto.setPurchagePrice(purchagePrice);
		dto.setQuantity(quntity);
		dto.setSellingPrice(sellingPrice);
		dto.setTotalItems(totalItems);
		dto.setVat(vat);

		list.add(dto);

		appContext.setSeletedProducts(list);

		sendMessage();
	}

	// This method using for Validation purpose
	private boolean validate(StringBuffer stringBuffer) {
		purchagePrice = etxtPurchageCost.getText().toString().trim();
		sellingPrice = etxtSellingPrice.getText().toString().trim();
		quntity = etxtQuntity.getText().toString().trim();
		vat = etxtVat.getText().toString().trim();
		totalItems = txtTotalItems.getText().toString().trim();

		boolean isValid = true;

		if (purchagePrice.length() == 0) {
			isValid = false;
			stringBuffer.append(context.getResources().getString(
					R.string.enter_making_cost));
		} else if ("0".equals(purchagePrice)) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.invalid_making_cost));
		}

		if (sellingPrice.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.menus_inven_selling_cost));
		} else if ("0".equals(sellingPrice)) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.invalid_selling_cost));
		}

		if (quntity.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(R.string.enter_qty));
		} else if ("0".equals(quntity)) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(R.string.invalid_qty));
		}

		/*
		 * if(vat.length() == 0) { isValid = false;
		 * stringBuffer.append("\n"+context
		 * .getResources().getString(R.string.menus_inven_vat)); } else
		 * if("0".equals(vat)) { isValid = false;
		 * stringBuffer.append("\n"+context
		 * .getResources().getString(R.string.invalid_vat)); }
		 */
		return isValid;
	}

	private void sendMessage() {
		Message msg = new Message();
		msg.arg1 = SalesCodes.CLIENT_SALE_END.code();
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
	public void afterTextChanged(Editable text) {
		String qty = etxtQuntity.getText().toString().trim();
		String noOfItems = txtNoOfItems.getText().toString().trim();

		if (!"".equals(qty) && !"".equals(noOfItems)) {
			try {
				txtTotalItems.setText((Integer.parseInt(qty) * Integer
						.parseInt(noOfItems)) + "");
			} catch (Exception e) {
				CommonMethods.showToast(context, "Invalid Quantity");
				etxtQuntity.setText("1");
				txtTotalItems.setText(1 * Integer.parseInt(noOfItems) + "");

			}

		}
	}
}