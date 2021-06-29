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
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.DishesDAO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.SalesCodes;

public class CreateDisDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private EditText etxtDishName, etxtNoOfItems, etxtSellingPrice,
			etxtExpiryDays, etxtVat;

	private Button btnDiscSave, btnDiscCancel;
	private TextView txtTitle;

	ServiApplication appContext;
	private Handler uiHandler;
	private String totalAmount;
	private DishesDTO dishesDTO;
	private String dishName, noOfItems, sellingPrice, expiryDays, vat;
	private String dishID = "";
	private ImageView imgClose;
	private boolean isCreating;

	public CreateDisDialog(Context context, String totalAmount,
			DishesDTO dishesDTO, Handler uiHandler, boolean isCreating) {
		super(context);
		this.context = context;
		this.totalAmount = totalAmount;
		this.dishesDTO = dishesDTO;
		this.uiHandler = uiHandler;
		this.isCreating = isCreating;
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
		setContentView(R.layout.create_dish_dialog);

		etxtDishName = (EditText) findViewById(R.id.etxt_CreateDishName);
		etxtNoOfItems = (EditText) findViewById(R.id.etxt_CreateNoOfItems);
		etxtSellingPrice = (EditText) findViewById(R.id.etxt_CreateSellingPrice);
		etxtExpiryDays = (EditText) findViewById(R.id.etxt_CreateExpiryDays);
		etxtVat = (EditText) findViewById(R.id.etxt_CreateVat);

		btnDiscSave = (Button) findViewById(R.id.btn_DishSave);
		btnDiscCancel = (Button) findViewById(R.id.btn_DishCancel);
		txtTitle = (TextView) findViewById(R.id.txt_CreateDishTitle);
		imgClose = (ImageView) findViewById(R.id.img_close);

		btnDiscSave.setOnClickListener(this);
		btnDiscCancel.setOnClickListener(this);
		imgClose.setOnClickListener(this);

		setDataToViews();

	}

	private void setDataToViews() {
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(50);
		etxtDishName.setFilters(FilterArray);
		if (dishesDTO.getDishId() != null && !"".equals(dishesDTO.getDishId())) {
			dishID = dishesDTO.getDishId();
			etxtDishName.setText(dishesDTO.getDishName());
			etxtExpiryDays.setText(dishesDTO.getExpiryDays());
			etxtNoOfItems.setText(dishesDTO.getNoOfItems());
			etxtSellingPrice.setText(dishesDTO.getSellingCostperItem());
			etxtVat.setText(dishesDTO.getVat());

			txtTitle.setText(context.getResources().getString(
					R.string.edit_Dish_titel));
		}

		if (!isCreating) {
			etxtDishName.setEnabled(false);
			etxtDishName.setBackgroundResource(R.drawable.etxt_disable);
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_DishSave:
			StringBuffer stringBuffer = new StringBuffer();
			if (!validate(stringBuffer))
				CommonMethods.displayAlert(true, context.getResources()
						.getString(R.string.fail), stringBuffer.toString(),
						context.getResources().getString(R.string.ok), "",
						context, null, false);
			else {
				this.dismiss();
				sendMessage();
			}

			break;

		case R.id.btn_DishCancel:
			this.dismiss();
			break;
		case R.id.img_close:

			this.dismiss();
			break;

		default:
			break;
		}
	}
	// Result of this method, Validation confirmation Alert using stringBuffer
	private boolean validate(StringBuffer stringBuffer) {
		dishName = etxtDishName.getText().toString().trim();
		noOfItems = etxtNoOfItems.getText().toString().trim();
		sellingPrice = etxtSellingPrice.getText().toString().trim();
		expiryDays = etxtExpiryDays.getText().toString().trim();
		vat = etxtVat.getText().toString().trim();

		boolean isValid = true;

		if (dishName.length() == 0 || "0".equals(dishName)) {
			isValid = false;
			stringBuffer.append(context.getResources().getString(
					R.string.enter_Dish_name));
		} else if ("0".equals(dishName) || ".".equals(dishName)) {
			isValid = false;
			stringBuffer.append(context.getResources().getString(
					R.string.invalid_Dish_name));
		} else if (isCreating) {
			String name = DishesDAO.getInstance().isDishNameExist(
					DBHandler.getDBObj(Constants.READABLE), dishName);

			if (!"".equalsIgnoreCase(name) && dishName.equalsIgnoreCase(name)) {
				isValid = false;
				stringBuffer.append(context.getResources().getString(
						R.string.dish_name_exist));
			}
		}

		if (noOfItems.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.enter_noof_items));
		} else if ("0".equals(noOfItems) || ".".equals(noOfItems)) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.invalid_noof_items));
		}

		if (sellingPrice.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.enter_selling_price));
		} else if ("0".equals(sellingPrice) || ".".equals(sellingPrice)) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.invalid_selling_price));
		}

		if (expiryDays.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.enter_expiry_days));
		} else if ("0".equals(expiryDays) || ".".equals(expiryDays)) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.invalid_expiry_days));
		}

		if (vat.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(R.string.enter_vat));
		} else if (".".equals(vat)) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(R.string.invalid_vat));
		} else if (vat.length() != 0 && !".".equals(vat)) {
			if (CommonMethods.getDoubleFormate(vat) > 100) {
				isValid = false;
				stringBuffer.append("\n"
						+ context.getResources().getString(R.string.vat_msg));
			}
		}

		return isValid;
	}

	private void sendMessage() {
		Bundle bundle = new Bundle();
		bundle.putString("dishID", dishID);
		bundle.putString("dishName", dishName);
		bundle.putString("expiryDays", expiryDays);
		bundle.putString("noOfItems", noOfItems);
		bundle.putString("sellingPrice", sellingPrice);
		bundle.putString("vat", vat);
		bundle.putString("totalAmount", totalAmount);

		Message msg = new Message();
		msg.arg1 = SalesCodes.DISH_CREATE.code();
		msg.setData(bundle);
		uiHandler.sendMessage(msg);
	}
}
