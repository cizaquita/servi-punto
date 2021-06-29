/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.GroupDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.InventoryHistoryDAO;
import com.micaja.servipunto.database.dao.LineDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dao.SupplierPaymentsDAO;
import com.micaja.servipunto.database.dao.TransaccionBoxDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.GroupDTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.InventoryHistoryDTO;
import com.micaja.servipunto.database.dto.LineDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.database.dto.SupplierPaymentsDTO;
import com.micaja.servipunto.database.dto.TransaccionBoxDTO;
import com.micaja.servipunto.dialog.OnDialogSaveClickListener2;
import com.micaja.servipunto.dialog.SaleFinishDialog2;
import com.micaja.servipunto.service.InventoryDbSynckService;
import com.micaja.servipunto.service.SupplierUpdateService;
import com.micaja.servipunto.service.TransaccionBoxService;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.DecimalInputFilter;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;
import com.micaja.servipunto.utils.SalesCodes;
import com.micaja.servipunto.utils.SalesEditTypes;

public class AddProductActivity extends BaseActivity implements
		OnClickListener, android.content.DialogInterface.OnClickListener,
		OnDialogSaveClickListener2 {

	private EditText etxtBarcode, etxtprodName, etxtpurprice, etxtsellPrice,
			etxtprodVat, etxtGroup, etxtLine, etxtSubgroup, etxtMinQuantity,
			etxtDiscount;// etxtprodQuantity,

	private Button btnSubmit, btnCancel, btnaddSupplier;
	private AlertDialog saveDialog, cancelDialog;
	private boolean isValid, flage = true, getCentralflage = false;
	private String name, barcode, purprice, sellprice, vat,
			supplier, // quantity,
			group, line, uom, subgroup, min_count_in_inventory,
			expiry_date = "", sup_id, discount, modifieddate;
	private String screen = "", fecha_inicial, fecha_final;
	private Spinner spnUOM, spnSupplier;
	private ArrayAdapter<String> supplierAdapter;
	private ArrayAdapter<String> uomAdapter;

	private List<DTO> supplierList = new ArrayList<DTO>();

	private TextView txtScreenName;

	private Hashtable<String, String> supplierTable, uomTable;

	private String productCode = "";

	private ServiApplication appContext;
	private ProductDTO productDto = new ProductDTO();
	private Intent intent;

	public SharedPreferences sharedpreferences;
	private SupplierDTO suppdto = new SupplierDTO();
	private SupplierDTO sdto = new SupplierDTO();
	ArrayList<String> barCodesList = new ArrayList<String>();
	private boolean isProductExists, isExists = false, isCall = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, AddProductActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.userName = sharedpreferences
				.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code",
				"");
		fecha_inicial = Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM);
		inItUI();

	}

	private void inItUI() {

		setContentView(R.layout.add_product_activity);

		etxtprodName = (EditText) findViewById(R.id.etxt_prodName);
		// etxtprodQuantity = (EditText) findViewById(R.id.etxt_prodQuantity);
		etxtBarcode = (EditText) findViewById(R.id.etxt_prodCode);
		etxtpurprice = (EditText) findViewById(R.id.etxt_prodPurPrice);
		etxtsellPrice = (EditText) findViewById(R.id.etxt_prodSellPrice);
		etxtprodVat = (EditText) findViewById(R.id.etxt_prodVat);
		spnUOM = (Spinner) findViewById(R.id.spn_uom);
		spnSupplier = (Spinner) findViewById(R.id.spn_supplier);
		etxtGroup = (EditText) findViewById(R.id.etxt_group);
		etxtLine = (EditText) findViewById(R.id.etxt_line);
		txtScreenName = (TextView) findViewById(R.id.txt_screenname);
		btnSubmit = (Button) findViewById(R.id.btn_Submit);
		btnCancel = (Button) findViewById(R.id.btn_Cancel);
		btnaddSupplier = (Button) findViewById(R.id.btn_Addsupplier);

		etxtSubgroup = (EditText) findViewById(R.id.etxt_subgroup);
		etxtMinQuantity = (EditText) findViewById(R.id.etxt_prodMinAmount);
		etxtDiscount = (EditText) findViewById(R.id.etxt_discount);

		btnaddSupplier.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		etxtpurprice.setFilters(new InputFilter[] { new DecimalInputFilter() });
		etxtsellPrice
				.setFilters(new InputFilter[] { new DecimalInputFilter() });

		etxtBarcode.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
				} else {
					if (etxtBarcode.length() > 0
							&& etxtprodName.getText().length() == 0) {
						getDataFromCentralserver(etxtBarcode.getText()
								.toString().trim());
					}
				}
			}
		});

		etxtBarcode.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					if (etxtBarcode.length() > 0) {
						getDataFromCentralserver(etxtBarcode.getText()
								.toString().trim());
					}
				}
				return false;
			}
		});

		etxtprodVat.setText("0");
		loadUI();
	}

	private void loadUI() {

		screen = getIntent().getExtras().get(ConstantsEnum.PRODUCT_MODE.code())
				.toString();
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(20);
		etxtBarcode.setFilters(FilterArray);

		populateSpinnerItems();

		if (screen.equals(ConstantsEnum.EDIT_PRODUCT.code())) {

			txtScreenName.setText(getResources()
					.getString(R.string.editproduct));
			productCode = getIntent().getExtras().getString(
					ConstantsEnum.PRODUCT_ID.code());
			getDataFromDB();
		} else if (ServiApplication.add_sup_product) {
			getDataFromDB();
		} else {
			txtScreenName.setText(getResources()
					.getString(R.string.add_product));
		}

		etxtBarcode.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// if (actionId==EditorInfo.IME_ACTION_GO)

				if (actionId == KeyEvent.KEYCODE_ENTER) {
					getDataFromDB(etxtBarcode.getText().toString());
					return true;
				}
				return true;
			}

		});

	}

	private void getDataFromDB(String BARCODE) {
		productDto = ProductDAO.getInstance().getRecordsByProductID(
				DBHandler.getDBObj(Constants.READABLE), BARCODE);
		setDataToViews();

	}

	private void populateSpinnerItems() {

		supplierList = SupplierDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		supplierAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getProviders());
		supplierAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnSupplier.setAdapter(supplierAdapter);

		uomAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getUOMs());
		uomAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnUOM.setAdapter(uomAdapter);

	}

	private List<String> getUOMs() {

		List<String> list = new ArrayList<String>();

		uomTable = new Hashtable<String, String>();

		list.add(getResources().getString(R.string.select));
		list.add("kg");
		uomTable.put("kg", "1");
		list.add("gm");
		uomTable.put("gm", "2");

		list.add("lt");
		uomTable.put("lt", "3");

		list.add("ml");
		uomTable.put("ml", "4");

		return list;
	}

	// Line,Group
	private void insertMasterTablesData() {
		LineDTO ldto = new LineDTO();

		List<DTO> llist = new ArrayList<DTO>();

		ldto.setGroupId("1");
		ldto.setLineId("2");
		ldto.setName("Line1");
		LineDTO ldto1 = new LineDTO();
		ldto1.setGroupId("2");
		ldto1.setLineId("3");
		ldto1.setName("Line2");

		llist.add(ldto);
		llist.add(ldto1);

		List<DTO> glist = new ArrayList<DTO>();
		GroupDTO gdto = new GroupDTO();

		gdto.setGroupId("1");
		gdto.setName("Group1");
		GroupDTO gdto1 = new GroupDTO();

		gdto1.setGroupId("2");
		gdto1.setName("Group2");

		glist.add(gdto);
		glist.add(gdto1);

		GroupDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
				glist);
		LineDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
				llist);

	}

	private List<String> getOutProviders() {
		List<String> list = new ArrayList<String>();
		supplierTable = new Hashtable<String, String>();
		//Add Harold to return all supplier when supplier is new


		for (DTO dto : supplierList) {
			SupplierDTO suppDto = (SupplierDTO) dto;
			list.add(suppDto.getName() + " - " + suppDto.getCedula());
			supplierTable.put(suppDto.getName() + " - " + suppDto.getCedula(),suppDto.getCedula());
		}
		Collections.sort(list);

		list.add(0,productDto.getSupplierName() + " - "
				+ productDto.getSupplierId());
		//END

		return list;
	}

	private List<String> getProviders() {

		List<String> list = new ArrayList<String>();
		supplierTable = new Hashtable<String, String>();
		list.add(getResources().getString(R.string.select));

		for (DTO dto : supplierList) {
			SupplierDTO suppDto = (SupplierDTO) dto;
			list.add(suppDto.getName() + " - " + suppDto.getCedula());
			supplierTable.put(suppDto.getName() + " - " + suppDto.getCedula(),
					suppDto.getCedula());
		}

		return list;
	}

	private void getDataFromDB() {

		if (ServiApplication.add_sup_product) {
			ServiApplication.add_sup_product = false;
			productDto = ServiApplication.prodto;
			setDataToViews1();
		} else {
			productDto = ProductDAO.getInstance().getRecordsByProductID(
					DBHandler.getDBObj(Constants.READABLE), productCode);
			setDataToViews();
		}

	}

	private void setDataToViews1() {
		etxtprodName.setText(productDto.getName());
		etxtprodVat.setText(productDto.getVat());
		etxtpurprice.setText(productDto.getPurchasePrice());
		etxtsellPrice.setText(productDto.getSellingPrice());
		etxtprodVat.setText(productDto.getVat());
		etxtsellPrice.setText(productDto.getSellingPrice());
		etxtGroup.setText(productDto.getGroupId());
		etxtLine.setText(productDto.getLineId());
		etxtDiscount.setText(productDto.getDiscount());
		spnUOM.setSelection(uomAdapter.getPosition(productDto.getUom()));
		spnSupplier.setSelection(supplierAdapter.getPosition(CommonMethods
				.getKeyFromHash(supplierTable, productDto.getSupplierId())));

		if (productDto.getSupplierId() != "") {
			btnaddSupplier.setVisibility(View.INVISIBLE);
			btnaddSupplier.setClickable(false);
		} else {
			btnaddSupplier.setVisibility(View.VISIBLE);
			btnaddSupplier.setClickable(true);
		}
		if (flage) {
			etxtBarcode.setText(productDto.getBarcode());
		} else {
			etxtBarcode.setBackgroundResource(R.drawable.etxt_disable);
		}

	}

	private void setDataToViews() {

		etxtprodName.setText(productDto.getName());

		etxtprodVat.setText(productDto.getVat());

		etxtpurprice.setText(productDto.getPurchasePrice());
		etxtsellPrice.setText(productDto.getSellingPrice());

		// etxtprodQuantity.setText(productDto.getQuantity());
		etxtprodVat.setText(productDto.getVat());

		etxtsellPrice.setText(productDto.getSellingPrice());
		etxtGroup.setText(productDto.getGroupId());
		etxtLine.setText(productDto.getLineId());
		etxtSubgroup.setText(productDto.getSubgroup());
		etxtDiscount.setText(productDto.getDiscount());

		spnUOM.setSelection(uomAdapter.getPosition(productDto.getUom()));
		spnSupplier.setSelection(supplierAdapter.getPosition(CommonMethods
				.getKeyFromHash(supplierTable, productDto.getSupplierId())));

		btnaddSupplier.setVisibility(View.INVISIBLE);
		btnaddSupplier.setClickable(false);
		if (flage) {
			etxtBarcode.setText(productDto.getBarcode());
			etxtBarcode.setEnabled(false);
			etxtBarcode.setBackgroundResource(R.drawable.etxt_disable);
		} else {
			etxtBarcode.setBackgroundResource(R.drawable.etxt_disable);
		}

		etxtprodName.setText(productDto.getName());
		etxtprodName.setEnabled(false);
		etxtprodName.setBackgroundResource(R.drawable.etxt_disable);

		etxtGroup.setEnabled(false);
		etxtGroup.setBackgroundResource(R.drawable.etxt_disable);

		etxtLine.setEnabled(false);
		etxtLine.setBackgroundResource(R.drawable.etxt_disable);

		etxtSubgroup.setEnabled(false);
		etxtSubgroup.setBackgroundResource(R.drawable.etxt_disable);
		etxtMinQuantity.setText(productDto.getMin_count_inventory());
		expiry_date = productDto.getExpiry_date();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.etxt_prodName:
			if (etxtBarcode.length() > 0) {
				getDataFromCentralserver(etxtBarcode.getText().toString()
						.trim());
			}
			break;
		case R.id.etxt_prodPurPrice:
			if (etxtBarcode.length() > 0) {
				getDataFromCentralserver(etxtBarcode.getText().toString()
						.trim());
			}
			break;

		case R.id.btn_Submit:
			validateAndSave();
			break;

		case R.id.btn_Cancel:
			cancelDialog = CommonMethods.displayAlert(false, getResources()
					.getString(R.string.product_cancel), getResources()
					.getString(R.string.product_cancel_msg), getResources()
					.getString(R.string.yes),
					getResources().getString(R.string.no),
					AddProductActivity.this, AddProductActivity.this, true);
			break;
		case R.id.btn_Addsupplier:
			BindDataToProductDto();
			ServiApplication.add_sup_product = true;
			Intent intent = new Intent(this, AddSupplierActivity.class);
			intent.putExtra(ConstantsEnum.SUPPLIER_MODE.code(),
					ConstantsEnum.PRODUCT_SUPPLIER.code());
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	private void getDataFromCentralserver(String trim) {

		if (NetworkConnectivity.netWorkAvailability(AddProductActivity.this)) {
			new GetProductMasterData().execute(trim);
		} else {
			CommonMethods.showCustomToast(AddProductActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
		}


	}

	/* Add product from service call 4 */
	private class AddProductorUpdate extends AsyncTask<String, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(
				AddProductActivity.this);
		String val_clientList;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					AddProductActivity.this);
		}

		@Override
		protected Void doInBackground(String... params) {
			val_clientList = servicehandler.makeServiceCall(
					ServiApplication.URL + "/product/create",
					ServiceHandler.POST, makejsonobj(true));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (new JSONStatus().status(val_clientList) == 0) {
				if (ServiApplication.connectionTimeOutState) {

					
					appContext.popActivity();
					CommonMethods.startIntent(AddProductActivity.this,
							ProductsActivity.class);
					finish();
					
				} else {
					appContext.popActivity();
					CommonMethods.startIntent(AddProductActivity.this,
							ProductsActivity.class);
					finish();
				}
			} else {
				appContext.popActivity();
				CommonMethods.startIntent(AddProductActivity.this,
						ProductsActivity.class);
				finish();
			}
			CommonMethods.dismissProgressDialog();
			CommonMethods.showCustomToast(AddProductActivity.this,
					getResources().getString(R.string.added_success));
		}
	}

	/* getdata from service call 4 */
	private class GetProductMasterData extends AsyncTask<String, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(
				AddProductActivity.this);
		String val_clientList;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					AddProductActivity.this);
		}

		@Override
		protected Void doInBackground(String... params) {
			val_clientList = servicehandler.makeServiceCall(
					ServiApplication.URL + "/product-master/" + params[0],
					ServiceHandler.GET);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (new JSONStatus().status(val_clientList) == 0) {
				if (ServiApplication.connectionTimeOutState) {
					ProductDTO prodto = new ParsingHandler()
							.mAsterProductInfo(val_clientList);
					if (ServiApplication.connectionTimeOutState) {
						getCentralflage = true;
						productDto = prodto;
						setDataToViewsFromCentroalServer();
					}
				}
			}
			CommonMethods.dismissProgressDialog();
		}
	}

	private void setDataToViewsFromCentroalServer() {
		try {
			if (productDto.getSupplierId().length() > 0) {
				etxtprodVat.setText(productDto.getVat());
				etxtpurprice.setText(productDto.getPurchasePrice());
				etxtsellPrice.setText(productDto.getSellingPrice());
				etxtMinQuantity.setText(productDto.getMin_count_inventory());
				// etxtprodQuantity.setText(productDto.getQuantity());
				etxtprodVat.setText(productDto.getVat());
				etxtDiscount.setText(productDto.getDiscount());
				etxtsellPrice.setText(productDto.getSellingPrice());
				etxtGroup.setText(productDto.getGroupId());
				etxtGroup.setEnabled(false);
				etxtGroup.setBackgroundResource(R.drawable.etxt_disable);

				etxtSubgroup.setText(productDto.getSubgroup());
				etxtSubgroup.setEnabled(false);
				etxtSubgroup.setBackgroundResource(R.drawable.etxt_disable);

				etxtLine.setText(productDto.getLineId());
				etxtLine.setEnabled(false);
				etxtLine.setBackgroundResource(R.drawable.etxt_disable);

				spnUOM.setSelection(uomAdapter.getPosition(productDto.getUom()));
				spnSupplier.setSelection(supplierAdapter
						.getPosition(CommonMethods.getKeyFromHash(
								supplierTable, productDto.getSupplierId())));

				sup_id = productDto.getSupplierId();
				btnaddSupplier.setVisibility(View.INVISIBLE);
				//Harold: Cambiamos a true
				spnSupplier.setEnabled(true);
				//Se comenta la siguiente línea
				//spnSupplier.setBackgroundResource(R.drawable.etxt_disable);

				/*
				 * etxtprodQuantity.setText(productDto.getQuantity());
				 * etxtprodQuantity.setEnabled(false);
				 * etxtprodQuantity.setBackgroundResource
				 * (R.drawable.etxt_disable);
				 */
				if (flage) {
					etxtBarcode.setText(productDto.getBarcode());
					etxtBarcode.setEnabled(false);
					etxtBarcode.setBackgroundResource(R.drawable.etxt_disable);
				} else {
					etxtBarcode.setBackgroundResource(R.drawable.etxt_disable);
				}

				etxtprodName.setText(productDto.getName());
				//Harold: Cambiamos A True
				etxtprodName.setEnabled(true);
				//Comentamos las siguiente línea
				//etxtprodName.setBackgroundResource(R.drawable.etxt_disable);
				String value = CommonMethods.getKeyFromHash(supplierTable,
						productDto.getSupplierId());
				if (value != null) {

				} else {
					supplierAdapter = new ArrayAdapter<String>(this,
							android.R.layout.simple_spinner_item,
							getOutProviders());
					supplierAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spnSupplier.setAdapter(supplierAdapter);
					spnSupplier.setSelection(0);
				}

			}
		} catch (Exception e) {

		}
	}

	private boolean validateBarCode(StringBuffer stringbuffer) {
		barcode = etxtBarcode.getText().toString().trim();
		vat = etxtprodVat.getText().toString().trim();
		discount = etxtDiscount.getText().toString().trim();
		if ((screen.equals(ConstantsEnum.EDIT_PRODUCT.code())))
			isProductExists = false;
		else {

			if (barcode.length() != 0) {
				int prodCount = ProductDAO.getInstance().isProductExists(
						DBHandler.getDBObj(Constants.READABLE), barcode);

				if (prodCount != 0) {
					isProductExists = true;
					stringbuffer.append(getResources().getString(
							R.string.product_exists)
							+ "\n"
							+ getResources().getString(R.string.error_barcode)
							+ barcode);
				} else
					isProductExists = false;
			}
		}

		return isProductExists;
	}

	private void validateAndSave() {

		StringBuffer stringBuffer = new StringBuffer();

		if (validateBarCode(stringBuffer))
			CommonMethods.displayAlert(true,
					getResources().getString(R.string.alert),
					stringBuffer.toString(),
					getResources().getString(R.string.ok), "",
					AddProductActivity.this, null, false);
		else if (!validationFields(stringBuffer))
			CommonMethods.displayAlert(true,
					getResources().getString(R.string.alert),
					stringBuffer.toString(),
					getResources().getString(R.string.ok), "",
					AddProductActivity.this, null, false);

		else
			saveDialog = CommonMethods.displayAlert(true, getResources()
					.getString(R.string.confirm),
					getResources().getString(R.string.save_messgae),
					getResources().getString(R.string.yes), getResources()
							.getString(R.string.no), AddProductActivity.this,
					AddProductActivity.this, false);
	}

	private boolean validationFields(StringBuffer stringBuffer) {

		name = etxtprodName.getText().toString().trim();
		barcode = etxtBarcode.getText().toString().trim();
		// quantity = etxtprodQuantity.getText().toString().trim();
		purprice = etxtpurprice.getText().toString().trim();
		sellprice = etxtsellPrice.getText().toString().trim();
		vat = etxtprodVat.getText().toString().trim();
		supplier = spnSupplier.getSelectedItem().toString();
		group = etxtGroup.getText().toString().trim();
		line = etxtLine.getText().toString().trim();
		uom = spnUOM.getSelectedItem().toString();
		subgroup = etxtSubgroup.getText().toString().trim();
		min_count_in_inventory = etxtMinQuantity.getText().toString().trim();
		isValid = true;

		if (name.length() == 0) {
			isValid = false;
			stringBuffer.append(getResources().getString(R.string.enter_name));
		} else if ("0".equals(name) || ".".equals(name)) {
			isValid = false;
			stringBuffer
					.append(getResources().getString(R.string.invalid_name));
		}
		if (barcode.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_barcode));
		} else if ("0".equals(barcode) || ".".equals(barcode)) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invalid_barcode));
		}

		/*
		 * if (quantity.length() == 0) { isValid = false;
		 * stringBuffer.append("\n" +
		 * getResources().getString(R.string.enter_qty)); } else if
		 * (".".equals(quantity)) { isValid = false; stringBuffer.append("\n" +
		 * getResources().getString(R.string.invalid_qty)); }
		 */
		if (min_count_in_inventory.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invalid_qty));
		}

		if (purprice.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_purprice));
		} else if ("0".equals(purprice) || ".".equals(purprice)) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invalid_purchase));
		}

		if (sellprice.length() == 0) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.enter_selling_price));
		} else if ("0".equals(sellprice) || ".".equals(sellprice)) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invalid_selling));
		}

		if (CommonMethods.getDoubleFormate(purprice) > CommonMethods
				.getDoubleFormate(sellprice)) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.error_selling_price));
		}

		if (vat.length() == 0) {
			isValid = false;
			stringBuffer.append(getResources().getString(
					R.string.enter_validvat));

		} else if (".".equals(vat)) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.invalid_vat));
		} else if (vat.length() != 0 && !".".equals(vat)) {
			if (CommonMethods.getDoubleFormate(vat) > 100) {
				isValid = false;
				stringBuffer.append("\n"
						+ getResources().getString(R.string.vat_msg));
			}
		}
		if (supplier.equals(getResources().getString(R.string.select))) {
			isValid = false;
			stringBuffer.append("\n"
					+ getResources().getString(R.string.select_supplier));
		}

		return isValid;
	}

	protected boolean suppliercodevalidat(String string) {
		updateproductdb();
		BindDataToProductDto();
		ProductDTO dto = ProductDAO.getInstance().getRecordsByProductID(
				DBHandler.getDBObj(Constants.READABLE), string);
		if (appContext.getSeletedSupplier().equals(dto.getSupplierId()))
			return true;
		else
			return false;
	}

	private void getSupplierData() {
		// suppliercodevalidat();
		ProductDTO prodto = ProductDAO.getInstance().getRecordsByProductID(
				DBHandler.getDBObj(Constants.WRITABLE), barcode);
		suppdto = SupplierDAO.getInstance().getRecordsBySupplierID(
				DBHandler.getDBObj(Constants.READABLE), prodto.getSupplierId());

		Log.v("varalababu", "varalababu" + prodto.getSupplierId());

		// suppdto = SupplierDAO.getInstance().getRecordsBySupplierID(
		// DBHandler.getDBObj(Constants.READABLE),
		// appContext.getSeletedSupplier());

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		dialog.dismiss();
		if (dialog == saveDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {

				appContext.setProductDTO(getUpdatedDataFromViews());

				if (screen.equals(ConstantsEnum.EDIT_PRODUCT.code())) {
					if (updateRecordinDB()) {
						appContext.setScreen(null);
						CommonMethods.startIntent(AddProductActivity.this,
								ProductsActivity.class);
						finish();
						appContext.popActivity();

					}

				} else if (insertRecordInDB()) {
					// appContext.setScreen(null);
					// try {
					// appContext.popActivity();
					// } catch (Exception e) {
					//
					// }
					// CommonMethods.startIntent(AddProductActivity.this,
					// ProductsActivity.class);
					// finish();
					if (NetworkConnectivity
							.netWorkAvailability(AddProductActivity.this)) {
						new AddProductorUpdate().execute("add");
					} else {
						appContext.setScreen(null);
						CommonMethods.startIntent(AddProductActivity.this,
								ProductsActivity.class);
						finish();
						appContext.popActivity();
						CommonMethods.showCustomToast(AddProductActivity.this,
								getResources()
										.getString(R.string.no_wifi_adhoc));
					}

					// ProductDTO dto = new ProductDTO();
					// dto =
					// ProductDAO.getInstance().getRecordsByProductID(DBHandler.getDBObj(Constants.READABLE),
					// etxtBarcode.getText().toString().trim());
					// appContext.setProductDTO(dto);
					//
					// ProductDetailDialog2 dialog2 = new
					// ProductDetailDialog2(AddProductActivity.this);
					// dialog2.setOnDialogSaveListener(AddProductActivity.this);
					// dialog2.setOnDialogCancelListener(AddProductActivity.this);
					// dialog2.show();
				}

			}
		} else if (dialog == cancelDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				// appContext.setScreen(null);
				appContext.popActivity();
				CommonMethods.startIntent(this, ProductsActivity.class);
				finish();

			}
			try {
				CommonMethods.startIntent(AddProductActivity.this,
						appContext.mAddproductScreenData.mBackStackClass);
				finish();
			} catch (Exception e) {
			}
		}
	}

	private boolean updateRecordinDB() {
		if (ProductDAO.getInstance().update(
				DBHandler.getDBObj(Constants.WRITABLE),
				getUpdatedDataFromViews())) {
			if (new AsynkTaskClass().callcentralserver("/product/update", ""
					+ ServiceHandler.POST, makejsonobj(false),
					AddProductActivity.this) != null) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					CommonMethods.showCustomToast(AddProductActivity.this,
							getResources().getString(R.string.product_update));
					return true;
				} else {
					ProductDAO.getInstance().updateSynckStatusasone(
							DBHandler.getDBObj(Constants.WRITABLE), barcode);
					return true;
				}
			} else {
				ProductDAO.getInstance().updateSynckStatusasone(
						DBHandler.getDBObj(Constants.WRITABLE), barcode);
				CommonMethods.showCustomToast(AddProductActivity.this,
						getResources().getString(R.string.sucessfully_change));
				return true;
			}

		} else {
			CommonMethods.showCustomToast(AddProductActivity.this,
					getResources().getString(R.string.not_added));
		}

		return false;
	}

	private ProductDTO getUpdatedDataFromViews() {
		ProductDTO prodDTO = new ProductDTO();
		if (uom.equals(getResources().getString(R.string.select)))
			uom = "";

		prodDTO.setProductId(productDto.getProductId());
		prodDTO.setBarcode(barcode);
		prodDTO.setName(name);
		prodDTO.setQuantity("0");
		prodDTO.setPurchasePrice(purprice);
		prodDTO.setSellingPrice(sellprice);
		prodDTO.setVat(vat);
		prodDTO.setSupplierId(supplierTable.get(supplier));
		prodDTO.setGroupId(group);
		prodDTO.setLineId(line);
		prodDTO.setUom(uom);
		prodDTO.setCreateDate(productDto.getCreateDate());
		prodDTO.setModifiedDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		prodDTO.setProductFlag("0");
		prodDTO.setActiveStatus(Constants.FALSE);
		prodDTO.setSyncStatus(Constants.FALSE);
		prodDTO.setSubgroup(subgroup);
		prodDTO.setMin_count_inventory(min_count_in_inventory);
		prodDTO.setExpiry_date(expiry_date);
		prodDTO.setDiscount(discount);
		return prodDTO;
	}

	private boolean insertRecordInDB() {
		// barcode
		if (ProductDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), getDataFromViews())) {
			updateproductdb();
			/**
			 * Harold: Permite crear el producto en Inventario automáticamente
			 */
			List<DTO> temp = getDataFromViews();
			ProductDTO dtoTemp = (ProductDTO)temp.get(0);
			dtoTemp.getBarcode();
			SelectedProddutsDTO selectedProddutsDTO = new SelectedProddutsDTO();
			selectedProddutsDTO.setBarcode(dtoTemp.getBarcode());
			selectedProddutsDTO.setQuantity("0");
			selectedProddutsDTO.setUnits(dtoTemp.getUom());
			selectedProddutsDTO.setPrice(dtoTemp.getPurchasePrice());
			selectedProddutsDTO.setSellPrice(dtoTemp.getSellingPrice());
			ProductFinalActivity productFinalActivity = new ProductFinalActivity();
			productFinalActivity.insertInventoryRecord(selectedProddutsDTO,"000");
			//END

			return true;
			// if (new AsynkTaskClass().callcentralserver("/product/create", ""
			// + ServiceHandler.POST, makejsonobj(true),
			// AddProductActivity.this) != null) {
			// if (new JSONStatus().status(ServiApplication.responds_feed) == 0)
			// {
			// if (ServiApplication.connectionTimeOutState) {
			// ProductDTO dto = ProductDAO.getInstance()
			// .getRecordsByBarcode(DBHandler.getDBObj(Constants.WRITABLE),
			// barcode);
			// dto.setSyncStatus(1);
			// ProductDAO.getInstance().updateProductSynk(DBHandler.getDBObj(Constants.WRITABLE),
			// dto);
			// }
			// CommonMethods.showCustomToast(AddProductActivity.this,
			// getResources().getString(R.string.added_success));
			// return true;
			// } else {
			// ProductDAO.getInstance().updateSynckStatusasone(DBHandler.getDBObj(Constants.WRITABLE),
			// barcode);
			// return true;
			// }
			// } else {
			// ProductDAO.getInstance().updateSynckStatusasone(DBHandler.getDBObj(Constants.WRITABLE),
			// barcode);
			// return true;
			// }

		} else {
			CommonMethods.showCustomToast(AddProductActivity.this,
					getResources().getString(R.string.not_added));
		}
		return false;
	}

	private void updateproductdb() {
		try {

			List<DTO> ldto = new ArrayList<DTO>();

			SupplierDTO lsdto = SupplierDAO.getInstance()
					.getRecordsBySupplierID(
							DBHandler.getDBObj(Constants.WRITABLE),
							productDto.getSupplierId());
			if (productDto.getSupplierId().equals(lsdto.getCedula())) {
			} else {
				SupplierDTO sdto = new SupplierDTO();
				sdto.setCedula(productDto.getSupplierId());
				sdto.setName(productDto.getSupplierName());
				sdto.setVisitFrequency("" + 0);
				sdto.setBalanceAmount("0");
				sdto.setCreatedDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
				ldto.add(sdto);
				SupplierDAO.getInstance().insert(
						DBHandler.getDBObj(Constants.WRITABLE), ldto);
				ServiApplication.setSupplier = SupplierDAO.getInstance()
						.getRecordsWithValues(
								DBHandler.getDBObj(Constants.READABLE),
								"cedula", productDto.getSupplierId());
				for (DTO dto : ServiApplication.setSupplier) {
					SupplierDTO stdo = (SupplierDTO) dto;
				}
				Intent intent = new Intent(AddProductActivity.this,
						SupplierUpdateService.class);
				startService(intent);
				ldto.clear();
			}

			if (getCentralflage) {
				
				GroupDTO gdto = new GroupDTO();
				gdto.setGroupId(productDto.getGroupId());
				gdto.setName(productDto.getGroupName());
				ldto.add(gdto);
				GroupDAO.getInstance().insert(
						DBHandler.getDBObj(Constants.WRITABLE), ldto);
				ldto.clear();

				LineDTO linedto = new LineDTO();
				linedto.setLineId(productDto.getLineId());
				linedto.setName(productDto.getLineName());
				linedto.setGroupId(productDto.getGroupId());
				ldto.add(linedto);
				LineDAO.getInstance().insert(
						DBHandler.getDBObj(Constants.WRITABLE), ldto);
				ldto.clear();

				ProductDTO dtoo = ProductDAO.getInstance().getRecordsByBarcode(
						DBHandler.getDBObj(Constants.WRITABLE), barcode);
				dtoo.setSupplierId(productDto.getSupplierId());
				ProductDAO.getInstance().updateProductSynk(
						DBHandler.getDBObj(Constants.WRITABLE), dtoo);

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@SuppressWarnings("static-access")
	private List<DTO> getDataFromViews() {
		fecha_final = new Dates().serverdateformate(Dates
				.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		List<DTO> productList = new ArrayList<DTO>();

		if (uom.equals(getResources().getString(R.string.select)))
			uom = "";
		// uom = uomTable.get(uom);
		ProductDTO prodDTO = new ProductDTO();
		prodDTO.setBarcode(barcode);
		prodDTO.setName(name);
		prodDTO.setQuantity("0");
		prodDTO.setPurchasePrice(purprice);
		prodDTO.setSellingPrice(sellprice);
		prodDTO.setVat(vat);
		prodDTO.setSupplierId(supplierTable.get(supplier));
		prodDTO.setGroupId(group);
		prodDTO.setLineId(line);
		prodDTO.setUom(uom);
		prodDTO.setCreateDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		prodDTO.setActiveStatus(Constants.FALSE);
		prodDTO.setSyncStatus(Constants.FALSE);
		prodDTO.setProductFlag("0");
		prodDTO.setMin_count_inventory(min_count_in_inventory);
		prodDTO.setExpiry_date(expiry_date);
		prodDTO.setDiscount(discount);
		prodDTO.setFecha_inicial(fecha_inicial);
		prodDTO.setFecha_final(fecha_final);
		sup_id = supplierTable.get(supplier);
		productList.add(prodDTO);
		return productList;
	}

	@SuppressWarnings("static-access")
	private String makejsonobj(boolean b) {

		JSONObject jsonobj = new JSONObject();
		try {
			if (barcode.length() > 0 && ServiApplication.store_id.length() > 0) {
				ProductDTO prodto = ProductDAO
						.getInstance()
						.getRecordsByProductID(
								DBHandler.getDBObj(Constants.WRITABLE), barcode);
				jsonobj.put("barcode", prodto.getBarcode());
				jsonobj.put("name", prodto.getName());
				jsonobj.put("quantity", prodto.getQuantity());
				jsonobj.put("uom", prodto.getUom());
				jsonobj.put("purchase_price", CommonMethods
						.getDoubleFormate(prodto.getPurchasePrice()));
				jsonobj.put("selling_price", CommonMethods
						.getDoubleFormate(prodto.getSellingPrice()));
				jsonobj.put("group_id", prodto.getGroupId());
				jsonobj.put("vat", prodto.getVat());
				if (b) {
					jsonobj.put("created_by", ServiApplication.userName);
				} else {
					jsonobj.put("modified_by", ServiApplication.userName);
				}
				jsonobj.put("supplier_code", prodto.getSupplierId());
				jsonobj.put("line_id", line);
				if (productDto.getActiveStatus() != null) {
					jsonobj.put("active_status",
							"" + productDto.getActiveStatus());
				} else {
					jsonobj.put("active_status", "0");
				}
				jsonobj.put("sub_group", subgroup);
				jsonobj.put("min_count_inventory", min_count_in_inventory);
				try {
					jsonobj.put("expiry_date", new Dates()
							.serverdateformate(prodto.getExpiry_date()));
				} catch (Exception e) {

				}

				try {
					jsonobj.put("create_date", new Dates()
							.serverdateformate(prodto.getCreateDate()));
				} catch (Exception e) {
					jsonobj.put("create_date", new Dates()
							.serverdateformate(Dates
									.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
				}

				try {
					jsonobj.put("modified_date", new Dates()
							.serverdateformate(prodto.getModifiedDate()));
				} catch (Exception e) {
					jsonobj.put("create_date", new Dates()
							.serverdateformate(Dates
									.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
				}
				try {
					jsonobj.put("discount",
							Integer.parseInt(prodto.getDiscount()));
				} catch (Exception e) {
					jsonobj.put("discount", 0);
				}
				jsonobj.put("store_code", ServiApplication.store_id);
				try {
					jsonobj.put("fecha_inicial", new Dates()
							.serverdateformate(prodto.getFecha_inicial()));
				} catch (Exception e) {
					jsonobj.put("fecha_inicial", new Dates()
							.serverdateformate(Dates
									.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
				}
				try {
					jsonobj.put("fecha_final", new Dates()
							.serverdateformate(prodto.getFecha_final()));
				} catch (Exception e) {
					jsonobj.put("fecha_final", new Dates()
							.serverdateformate(Dates
									.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
				}
			}
			return jsonobj.toString();

		} catch (JSONException e) {
			return jsonobj.toString();
		}
	}

	public void BindDataToProductDto() {
		ProductDTO dto = new ProductDTO();
		name = etxtprodName.getText().toString().trim();
		barcode = etxtBarcode.getText().toString().trim();
		purprice = etxtpurprice.getText().toString().trim();
		sellprice = etxtsellPrice.getText().toString().trim();
		vat = etxtprodVat.getText().toString().trim();
		supplier = spnSupplier.getSelectedItem().toString();
		group = etxtGroup.getText().toString().trim();
		line = etxtLine.getText().toString().trim();
		uom = spnUOM.getSelectedItem().toString();
		discount = etxtDiscount.getText().toString().trim();
		dto.setName(name);
		dto.setBarcode(barcode);
		dto.setPurchasePrice(purprice);
		dto.setSellingPrice(sellprice);
		dto.setVat(vat);
		dto.setGroupId(group);
		dto.setLineId(line);
		dto.setUom(uom);
		dto.setDiscount(discount);
		ServiApplication.prodto = dto;

	}

	@Override
	public void onSaveClick() {

		getSupplierData();
		setValuesToPriceTag();
		if (appContext.getTotalAmount() > 0) {

			double debt = CommonMethods.getDoubleFormate(suppdto
					.getBalanceAmount());
			// appContext.setSeletedSupplier(suppdto.getCedula());
			// if (appContext.getClientDTO() != null)
			//
			SaleFinishDialog2 finishDialog = new SaleFinishDialog2(
					AddProductActivity.this, appContext.getTotalAmount(), debt,
					ConstantsEnum.ORDERS.code(), uiHandler);
			finishDialog.setOnDialogSaveListener(AddProductActivity.this);
			finishDialog.show();
		}

		// appContext.setSeletedProducts(new ArrayList<DTO>());

	}

	@Override
	public void onCancelClick() {

	}

	private void setValuesToPriceTag() {
		// getSupplierData();
		List<DTO> selectedList = null;
		int totalItems = 0;
		double subTotal = 0, vat = 0;
		selectedList = appContext.getSelectedProducts();

		for (int i = 0; i < selectedList.size(); i++) {
			SelectedProddutsDTO dto = (SelectedProddutsDTO) selectedList.get(i);

			totalItems += CommonMethods.getDoubleFormate(dto.getQuantity());

			subTotal += CommonMethods.getDoubleFormate(dto.getPrice())
					* CommonMethods.getDoubleFormate(dto.getQuantity());

			vat += (CommonMethods.getDoubleFormate(dto.getPrice()) / 100)
					* CommonMethods.getDoubleFormate(dto.getVat())
					* CommonMethods.getDoubleFormate(dto.getQuantity());
		}

		appContext.setTotalAmount(subTotal + vat);

	}

	// This method using for Handler message to update the UI thread
	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.EDIT.code()) {

			} else if (msg.arg1 == SalesCodes.DISCOUNT.code()) {

				// addDiscount();
			} else if (msg.arg1 == SalesCodes.DELETE.code()) {
			} else if (msg.arg1 == SalesCodes.ADD_INVENTORY.code()) {
				CommonMethods.showProgressDialog(
						getString(R.string.please_wait),
						AddProductActivity.this);
				String payments = (msg.obj).toString();
				updateInventoryData(payments);
				updateProductData();
				updateSupplierRecords(payments);
				CommonMethods.dismissProgressDialog();
				SynckTocentralserver();
				// inItUI();
			}
		}
	};

	protected void SynckTocentralserver() {
		stopService(new Intent(this, InventoryDbSynckService.class));
		intent = new Intent(this, InventoryDbSynckService.class);
		startService(intent);
		CommonMethods.openNewActivity(AddProductActivity.this,
				ProductsActivity.class);
	}

	protected void updateSupplierRecords(String payments) {

		String payAmount = payments.split(":")[0];
		String debtAmount = payments.split(":")[1];
		updateSupplierPayments(payAmount, debtAmount);

	}

	// Result of this method, updating supplier payments in inventory receive
	// time
	private void updateSupplierPayments(String payAmount, String debtAmount) {

		List<DTO> supplierpaymentList = new ArrayList<DTO>();

		SupplierPaymentsDTO dto = new SupplierPaymentsDTO();
		dto.setSupplierId(suppdto.getCedula());
		dto.setAmountPaid(payAmount);
		dto.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		dto.setPaymentType(ConstantsEnum.PAYMENT_TYPE_CASH.code());
		dto.setPurchaseType(ConstantsEnum.SUPPLIER_PURCHASE_TYPE.code());
		dto.setSyncStatus(0);
		supplierpaymentList.add(dto);
		SupplierPaymentsDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), supplierpaymentList);
		getCallTransaccionBoxService(ServiApplication.Inventory_M_name,
				ServiApplication.Inventory_TipoTrans,
				ServiApplication.Inventory_PaymentType, payAmount);
		Double bal, pay;
		try {
			bal = Double.parseDouble(suppdto.getBalanceAmount());
		} catch (Exception e) {
			bal = (double) 0;
		}
		try {
			pay = Double.parseDouble(debtAmount);
		} catch (Exception e) {
			pay = (double) 0;
		}

		suppdto.setBalanceAmount("" + (bal + pay));
		suppdto.setSyncStatus(0);
		SupplierDAO.getInstance().updateSupplier(
				DBHandler.getDBObj(Constants.WRITABLE), suppdto);
		SupplierDAO.getInstance().updateDebtAmount(
				DBHandler.getDBObj(Constants.WRITABLE), suppdto);

	}

	protected void updateProductData() {
		List<DTO> selectedList = appContext.getSelectedProducts();
		for (int i = 0; i < selectedList.size(); i++) {
			SelectedProddutsDTO selectedDTO = (SelectedProddutsDTO) selectedList
					.get(i);
			ProductDAO.getInstance().updateProducts(
					DBHandler.getDBObj(Constants.WRITABLE), selectedDTO);
			ProductDAO.getInstance().updateSynckStatusasone(
					DBHandler.getDBObj(Constants.WRITABLE),
					selectedDTO.getBarcode());

		}
	}

	// Result of this method, updating inventory in inventory receive time
	protected void updateInventoryData(String payments) {
		String invoiceNum;
		try {
			invoiceNum = payments.split(":")[2];
		} catch (Exception e) {
			invoiceNum = "";
		}
		List<DTO> selectedList = appContext.getSelectedProducts();

		for (int i = 0; i < selectedList.size(); i++) {
			SelectedProddutsDTO selectedDTO = (SelectedProddutsDTO) selectedList
					.get(i);
			insertInventoryRecord(selectedDTO, invoiceNum);
		}

	}

	private void insertInventoryRecord(SelectedProddutsDTO selectedDTO,
			String invoiceNum) {
		List<DTO> inventoryList = new ArrayList<DTO>();

		inventoryList = InventoryDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		if (inventoryList.size() == 0) {
			insertDataIntoInvHistory(selectedDTO, invoiceNum);
			List<DTO> invlist = new ArrayList<DTO>();
			InventoryDTO indd = new InventoryDTO();
			if (invoiceNum.length() > 0)
				indd.setInventoryId(invoiceNum);
			else
				indd.setInventoryId(Dates.getSysDateinMilliSeconds() + "");
			indd.setProductId(selectedDTO.getBarcode());
			indd.setQuantity(selectedDTO.getQuantity());
			indd.setUom(selectedDTO.getUnits());
			indd.setQuantityBalance(0.0);
			indd.setSyncStatus(0);
			invlist.add(indd);
			barCodesList.add(selectedDTO.getBarcode());
			InventoryDAO.getInstance().insert(
					DBHandler.getDBObj(Constants.WRITABLE), invlist);
			return;
		}
		for (DTO dto : inventoryList) {
			isExists = false;
			InventoryDTO inDTO = (InventoryDTO) dto;

			if (inDTO.getProductId().trim()
					.equals(selectedDTO.getBarcode().trim())) {
				insertDataIntoInvHistory(selectedDTO, invoiceNum);
				String qty = "";
				if (!"".equals(inDTO.getUom()) && inDTO.getUom() != null)
					qty = CommonMethods.UOMConversions(inDTO.getQuantity(),
							inDTO.getUom(), selectedDTO.getQuantity(),
							selectedDTO.getUnits(), "1",
							SalesEditTypes.INVOICE_ADJUSTMENT.code());
				else
					qty = String.valueOf(CommonMethods.getDoubleFormate(inDTO
							.getQuantity())
							+ CommonMethods.getDoubleFormate(selectedDTO
									.getQuantity()));
				inDTO.setProductId(selectedDTO.getBarcode());
				inDTO.setQuantity(qty);

				inDTO.setQuantityBalance(CommonMethods.getBalanceQuantity(selectedDTO.getBarcode(), selectedDTO
						.getQuantity()));
				inDTO.setQuantity(inDTO.getQuantity());
				inDTO.setUom(selectedDTO.getUnits());
				inDTO.setSyncStatus(0);
				barCodesList.add(selectedDTO.getBarcode());
				InventoryDAO.getInstance().update(
						DBHandler.getDBObj(Constants.WRITABLE), inDTO);
				isExists = true;
				return;

			}
		}
		if (!isExists) {
			List<DTO> invlist = new ArrayList<DTO>();
			InventoryDTO indd = new InventoryDTO();

			if (invoiceNum.length() > 0)
				indd.setInventoryId(invoiceNum);
			else
				indd.setInventoryId(Dates.getSysDateinMilliSeconds() + "");

			indd.setProductId(selectedDTO.getBarcode());
			indd.setQuantity(selectedDTO.getQuantity());
			indd.setQuantityBalance(0.0);
			indd.setUom(selectedDTO.getUnits());
			indd.setSyncStatus(0);
			invlist.add(indd);
			barCodesList.add(selectedDTO.getBarcode());
			InventoryDAO.getInstance().insert(
					DBHandler.getDBObj(Constants.WRITABLE), invlist);
			insertDataIntoInvHistory(selectedDTO, invoiceNum);
		}

	}

	private void insertDataIntoInvHistory(SelectedProddutsDTO dto,
			String invoiceNum) {
		List<DTO> list = new ArrayList<DTO>();

		InventoryDTO indto = InventoryDAO.getInstance().getInventoryValue(
				DBHandler.getDBObj(Constants.WRITABLE), dto.getBarcode());
		InventoryHistoryDTO hDTO = new InventoryHistoryDTO();
		Double price = 0.0;
		// price = Double.parseDouble(dto.getPrice()) *
		// Double.parseDouble(dto.getQuantity())
		// hDTO.setDateTime(dateTime);
		hDTO.setProductId(dto.getBarcode());
		hDTO.setQuantity(dto.getQuantity());
		hDTO.setPrice(CommonMethods.getDoubleFormate(dto.getPrice()));
		hDTO.setUom(dto.getUnits());
		hDTO.setSyncStatus(Constants.FALSE);
		hDTO.setInvoiceNum(indto.getInventoryId());
		hDTO.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		list.add(hDTO);
		InventoryHistoryDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), list);
	}

	@SuppressWarnings("unused")
	private void SynckSupplierandSupplierpayments(
			List<InventoryDTO> inventoryDTO) {
		String product_id = null;
		for (int i = 0; i < inventoryDTO.size(); i++) {
			product_id = inventoryDTO.get(i).getProductId();
		}

		if (NetworkConnectivity.netWorkAvailability(AddProductActivity.this)) {
			new SynckSup().execute(product_id);
		} else {
			CommonMethods.showCustomToast(AddProductActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));

		}
	}

	// Result of this service, Supplier and supplier-payments tales updating
	private class SynckSup extends AsyncTask<String, Void, Void> {

		String product_id;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			product_id = params[0];
			ProductDTO dto = ProductDAO.getInstance().getRecordsByProductID(
					DBHandler.getDBObj(Constants.READABLE), params[0]);
			SupplierDTO sdto = SupplierDAO.getInstance()
					.getRecordscedula(DBHandler.getDBObj(Constants.READABLE),
							dto.getSupplierId());

			if (isCall) {
				List<DTO> supplierDetailsDtos = new ArrayList<DTO>();
				supplierDetailsDtos.add(sdto);
				new ServiceHandler(AddProductActivity.this).makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(AddProductActivity.this)
								.getSuppliersDetailsData(supplierDetailsDtos));
			} else {
				CommonMethods.dismissProgressDialog();
				List<DTO> list = SupplierPaymentsDAO.getInstance().getRecords(
						DBHandler.getDBObj(Constants.READABLE));
				SupplierPaymentsDTO spdto = null;
				try {
					spdto = (SupplierPaymentsDTO) list.get(list.size() - 1);
				} catch (Exception e) {

				}
				List<DTO> supplierPaymentsDetails = new ArrayList<DTO>();
				supplierPaymentsDetails.add(spdto);

				new ServiceHandler(AddProductActivity.this)
						.makeServiceCall(
								ServiApplication.URL + "/sync",
								ServiceHandler.POST,
								new RequestFiels(AddProductActivity.this)
										.getSupplierPayTableData(supplierPaymentsDetails));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (isCall) {
				isCall = false;
				new SynckSup().execute(product_id);
			} else {
				isCall = true;
			}
		}
	}

	// Result of this method,inserting data to Transaccion table
	private void getCallTransaccionBoxService(String module_nem, String tipo,
			String PaymentType, String payAmount) {
		List<DTO> dto = new ArrayList<DTO>();
		TransaccionBoxDTO tdto = new TransaccionBoxDTO();
		tdto.setAmount(payAmount);
		tdto.setModule_name(module_nem);
		tdto.setSyncstatus(0);
		tdto.setTipo_transction(tipo);
		tdto.setTransaction_type(PaymentType);
		tdto.setDatetime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		tdto.setUsername(sharedpreferences.getString("user_name", ""));
		tdto.setStore_code(sharedpreferences.getString("store_code", ""));
		dto.add(tdto);
		if (TransaccionBoxDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), dto)) {
			if (CommonMethods.getInternetSpeed(AddProductActivity.this) > ServiApplication.local_data_speed) {
				stopService(new Intent(AddProductActivity.this,
						TransaccionBoxService.class));
				Intent intent = new Intent(AddProductActivity.this,
						TransaccionBoxService.class);
				startService(intent);
			} else {
				ServiApplication.connectionTimeOutState = false;
			}
		}
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(AddProductActivity.this, ProductsActivity.class);
		startActivity(intent);
		this.finish();
		
	}
}
