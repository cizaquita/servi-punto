package com.micaja.servipunto.dialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.ProductsActivity;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.DecimalInputFilter;

public class ProductDetailDialog2 extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private Button btnProductSave, btnCancel;
	private EditText etxtQty, etxtVat, etxtPurchasePrice, etxtSalePrice,
			etxtUtility;
	ServiApplication appContext;
	private TextView txtPurchasePrice, txtProductNAme;
	private static TextView txtproductDuedate;
	private OnDialogSaveClickListener2 listener;
	ProductDTO prodDTO = new ProductDTO();
	private String purPrice, sellPrice, vat, qty, duedate;
	private boolean isValid;
	private ImageView imgClose;
	private static View dateView;

	public ProductDetailDialog2(Context context) {

		super(context);
		this.context = context;
		appContext = (ServiApplication) context.getApplicationContext();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}
	// Result of this method,registration for all UI views.
	@SuppressWarnings("static-access")
	private void initUI() {

		setContentView(R.layout.product_detail_dialog);
		etxtQty = (EditText) findViewById(R.id.etxt_product_qty);
		etxtVat = (EditText) findViewById(R.id.etxt_product_vat);

		etxtPurchasePrice = (EditText) findViewById(R.id.etxt_product_purprice);
		etxtSalePrice = (EditText) findViewById(R.id.etxt_product_saleprice);
		etxtUtility = (EditText) findViewById(R.id.etxt_product_utilitypercentage);
		txtPurchasePrice = (TextView) findViewById(R.id.txt_purchaseprice);
		txtProductNAme = (TextView) findViewById(R.id.txt_ProductName);
		btnProductSave = (Button) findViewById(R.id.btn_EndProductSave);
		btnCancel = (Button) findViewById(R.id.btn_EndProductCancel);
		imgClose = (ImageView) findViewById(R.id.img_close);

		txtproductDuedate = (TextView) findViewById(R.id.txt_productDuedate);
		txtproductDuedate.setOnClickListener(this);

		btnProductSave.setOnClickListener(this);
		imgClose.setOnClickListener(this);

		btnCancel.setOnClickListener(this);

		prodDTO = appContext.getProductDTO();
		etxtQty.setText(prodDTO.getQuantity());
		etxtVat.setText(prodDTO.getVat());
		etxtPurchasePrice.setText(prodDTO.getPurchasePrice());
		etxtSalePrice.setText(prodDTO.getSellingPrice());
		try {
			txtproductDuedate.setText(new Dates().formateyyyyMMdd(prodDTO
					.getExpiry_date()));
		} catch (Exception e) {
			txtproductDuedate.setText(prodDTO.getExpiry_date());
		}

		txtPurchasePrice.setText(context.getResources().getString(
				R.string.purchaseprice)
				+ " : "
				+ CommonMethods.getNumSeparator(CommonMethods
						.getDoubleFormate(prodDTO.getPurchasePrice()))
				+ "   | "
				+ context.getResources().getString(R.string.quantity)
				+ prodDTO.getQuantity()
				+ "   | "
				+ context.getResources().getString(R.string.vat)
				+ prodDTO.getVat());
		txtProductNAme.setText(prodDTO.getName());

		etxtUtility.setEnabled(false);

		updateUtilityValue();

		/*
		 * change Utility value on change of Selling price or Purchase price
		 */

		etxtPurchasePrice.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				updateUtilityValue();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		etxtSalePrice.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				updateUtilityValue();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		etxtPurchasePrice
				.setFilters(new InputFilter[] { new DecimalInputFilter() });
		etxtSalePrice
				.setFilters(new InputFilter[] { new DecimalInputFilter() });

	}

	private void updateUtilityValue() {
		double utilityValue = 0.0;
		try {
			utilityValue = CommonMethods.getDoubleFormate(etxtSalePrice
					.getText().toString().trim())
					- CommonMethods.getDoubleFormate(etxtPurchasePrice
							.getText().toString().trim());

			if (utilityValue > 0) {
				utilityValue = (utilityValue / CommonMethods
						.getDoubleFormate(etxtPurchasePrice.getText()
								.toString().trim())) * 100;

				if (Double.isInfinite(utilityValue)
						|| Double.isNaN(utilityValue))
					etxtUtility.setText("0.0");
				else
					etxtUtility.setText(""
							+ CommonMethods.getRoundedVal(utilityValue));
			} else
				etxtUtility.setText("0.0");
		} catch (Exception e) {
			System.out.println("Log: exception occured........");

			etxtUtility.setText("0.0");
		}
	}

	public void setOnDialogSaveListener(OnDialogSaveClickListener2 listener) {
		this.listener = listener;
	}

	public void setOnDialogCancelListener(OnDialogSaveClickListener2 listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View view) {
		dateView = view;
		DatePicker datePicker = new DatePicker();

		switch (view.getId()) {

		case R.id.btn_EndProductCancel:
			listener.onCancelClick();
			this.dismiss();
			CommonMethods.openNewActivity(context, ProductsActivity.class);

			break;

		case R.id.btn_EndProductSave:
			getSelectedProductData();
			break;

		case R.id.img_close:
			this.dismiss();
			CommonMethods.openNewActivity(context, ProductsActivity.class);
			break;
		case R.id.txt_productDuedate:
			dateView = view;
			datePicker.show(((Activity) context).getFragmentManager(),
					"datePicker");
			break;

		default:
			break;
		}
	}
	//Choose date from Date picker
	public static class DatePicker extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			final Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(android.widget.DatePicker view, int year,
				int monthOfYear, int dayOfMonth) {

			String day = String.valueOf(dayOfMonth);
			String mon = String.valueOf(monthOfYear + 1);

			if (day.length() == 1)
				day = 0 + day;
			if (mon.length() == 1)
				mon = 0 + mon;

			String date = year + "-" + mon + "-" + day;
			if (dateView.getId() == R.id.txt_productDuedate) {
				txtproductDuedate.setText(date);
			} else {
			}
		}
	}
	// Bind new product data to BindDataTo ProductDTO
	private void getDataFromViews() {

		if (vat.equals(""))
			vat = "0.0";

		Double utilityValue = 0.0;
		utilityValue = CommonMethods.getDoubleFormate(sellPrice)
				- CommonMethods.getDoubleFormate(purPrice);

		SelectedProddutsDTO dto = new SelectedProddutsDTO();

		dto.setIdProduct(prodDTO.getProductId());
		dto.setBarcode(prodDTO.getBarcode());
		dto.setName(prodDTO.getName());
		dto.setPrice(purPrice);
		dto.setQuantity(qty);
		dto.setVat(vat);
		dto.setExpiry_date(duedate);
		if (prodDTO.getUom() != null)
			dto.setUnits(prodDTO.getUom().toString());
		dto.setSellPrice(sellPrice);

		boolean isExists = false;
		utilityValue = (utilityValue / CommonMethods.getDoubleFormate(purPrice)) * 100;
		etxtUtility.setText(String.valueOf(utilityValue));

		List<DTO> selectedproducts = appContext.getSelectedProducts();

		if (selectedproducts == null) {
			selectedproducts = new ArrayList<DTO>();
			appContext.setSeletedProducts(selectedproducts);
		}
		for (int i = 0; i < selectedproducts.size(); i++) {
			SelectedProddutsDTO prodDTO = (SelectedProddutsDTO) selectedproducts
					.get(i);
			String prodID = prodDTO.getIdProduct();
			if (prodID.equals(dto.getIdProduct())) {
				isExists = true;
				break;
			}
		}
		if (!isExists) {
			selectedproducts.add(dto);
		}
		appContext.setProductDTO(null);
		listener.onSaveClick();
		// listener.onCancelClick();
		this.dismiss();

	}
	// This method using for Validation purpose
	private boolean validateFields(StringBuffer stringBuffer) {

		qty = etxtQty.getText().toString().trim();
		purPrice = etxtPurchasePrice.getText().toString().trim();
		sellPrice = etxtSalePrice.getText().toString().trim();
		vat = etxtVat.getText().toString().trim();
		duedate = txtproductDuedate.getText().toString().trim();

		isValid = true;

		if (qty.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(R.string.enter_qty));
		} else if (".".equals(qty) || "0".equals(qty)|| "00".equals(qty)|| "000".equals(qty)|| "0000".equals(qty)) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(R.string.invalid_qty));
		}

		if (purPrice.length() == 0) {
			isValid = false;
			stringBuffer
					.append("\n"
							+ context.getResources().getString(
									R.string.enter_purprice));
		} else if ("0".equals(purPrice) || ".".equals(purPrice)) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.invalid_purchase));
		}

		if (sellPrice.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources().getString(
							R.string.enter_selling_price));
		} else if ("0".equals(sellPrice) || ".".equals(sellPrice)) {
			isValid = false;
			stringBuffer.append("\n"
					+ context.getResources()
							.getString(R.string.invalid_selling));
		}

		if (vat.length() == 0) {
			isValid = false;
			stringBuffer.append(context.getResources().getString(
					R.string.enter_validvat));

		} else if (vat.length() != 0 && !".".equals(vat)) {
			if (CommonMethods.getDoubleFormate(vat) > 100) {
				isValid = false;
				stringBuffer.append("\n"
						+ context.getResources().getString(R.string.vat_msg));
			}
		}

		return isValid;

	}
	// Result of this method, Validation confirmation Alert using stringBuffer
	private void getSelectedProductData() {
		StringBuffer stringBuffer = new StringBuffer();
		if (!validateFields(stringBuffer))
			CommonMethods.displayAlert(true,
					context.getResources().getString(R.string.alert),
					stringBuffer.toString(),
					context.getResources().getString(R.string.ok), "", context,
					null, false);
		else if (CommonMethods.getDoubleFormate(purPrice) > CommonMethods
				.getDoubleFormate(sellPrice))
			CommonMethods.showToast(
					context,
					context.getResources().getString(
							R.string.error_selling_price));
		else
			getDataFromViews();
	}

}
