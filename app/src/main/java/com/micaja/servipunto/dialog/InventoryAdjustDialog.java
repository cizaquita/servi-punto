/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.DamageTypeDAO;
import com.micaja.servipunto.database.dao.InventoryAdjustmentDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DamageTypeDTO;
import com.micaja.servipunto.database.dto.InventoryAdjustmentDTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.ProductDetailsDTO;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.SalesEditTypes;

public class InventoryAdjustDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private Button btnAdjustSave, btnAdjustCancel;
	private Spinner spnUOM;
	private EditText etxtQty;
	private Spinner spnReason;
	InventoryAdjustmentDTO invAdjustdto = new InventoryAdjustmentDTO();
	private ProductDetailsDTO productDetails = new ProductDetailsDTO();
	private ServiApplication appContext;
	private OnDialogSaveClickListener1 saveListener;
	private Hashtable<String, String> uomTable, damageTypetable;
	private ArrayAdapter<String> uomAdapter, damageTypeAdapter;
	private String reason, uom, qty, selectedQty;
	private boolean isvalid;
	private AlertDialog saveDialog;
	private TextView txtProductName;
	private String inventoryId, inventoryAdjustmentId;
	private LinearLayout llUOM;
	private SharedPreferences sharedpreferences;

	public InventoryAdjustDialog(Context context,
			ProductDetailsDTO productDetails) {

		super(context);
		this.context = context;
		this.productDetails = productDetails;
		appContext = (ServiApplication) context.getApplicationContext();
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.userName = sharedpreferences
				.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code","");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initUI();

	}

	public void setOnDialogSaveListener(OnDialogSaveClickListener1 saveListener) {
		this.saveListener = saveListener;

	}

	// Result of this method,registration for all UI views.
	private void initUI() {
		setContentView(R.layout.inventory_adjust_dialog);

		btnAdjustSave = (Button) findViewById(R.id.btn_AdjustCancel);
		btnAdjustCancel = (Button) findViewById(R.id.btn_AdjustSave);
		spnUOM = (Spinner) findViewById(R.id.spn_adjust_uom);
		etxtQty = (EditText) findViewById(R.id.etxt_adjustQuantity);
		spnReason = (Spinner) findViewById(R.id.spn_adjust_reason);
		txtProductName = (TextView) findViewById(R.id.txt_productlbl);

		btnAdjustSave.setOnClickListener(this);
		btnAdjustCancel.setOnClickListener(this);

		llUOM = (LinearLayout) findViewById(R.id.ll_uom);
		if ("".equals(productDetails.getUom()))
			llUOM.setVisibility(View.GONE);

		etxtQty.setText(productDetails.getQuantity());

		insertMasterTablesData();
		loadUI();

	}

	// load screen
	private void loadUI() {
		txtProductName.setText(context.getResources().getString(
				R.string.inv_prodrname)
				+ " : "
				+ productDetails.getName()
				+ " | "
				+ context.getResources().getString(R.string.quantity)
				+ " : "
				+ productDetails.getQuantity());
		populateDamageTypes();
		populateUOM();
		getDataFromDB();

	}

	// Damage TYpes
	private void insertMasterTablesData() {

		DamageTypeDTO ldto = new DamageTypeDTO();

		List<DTO> dlist = new ArrayList<DTO>();

		ldto.setDamageTypeId("1");
		ldto.setName(context.getResources().getString(R.string.expired));

		DamageTypeDTO ldto1 = new DamageTypeDTO();

		ldto1.setDamageTypeId("2");
		ldto1.setName(context.getResources().getString(R.string.breakable));

		DamageTypeDTO ldto2 = new DamageTypeDTO();

		ldto2.setDamageTypeId("3");
		ldto2.setName(context.getResources().getString(R.string.theft));

		DamageTypeDTO ldto3 = new DamageTypeDTO();

		ldto3.setDamageTypeId("4");
		ldto3.setName(context.getResources().getString(
				R.string.physical_recount));

		dlist.add(ldto);
		dlist.add(ldto1);
		dlist.add(ldto2);
		dlist.add(ldto3);

		DamageTypeDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), dlist);

	}

	// UOM spinner
	private void populateUOM() {
		uomAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, getUOMs());
		uomAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnUOM.setAdapter(uomAdapter);

		spnUOM.setSelection(getSelectedPosition(), true);
	}

	// get UOM based on position
	private int getSelectedPosition() {
		int position = 0;
		if ("kg".equalsIgnoreCase(productDetails.getUom()))
			position = 1;
		else if ("gm".equalsIgnoreCase(productDetails.getUom()))
			position = 2;
		else if ("lt".equalsIgnoreCase(productDetails.getUom()))
			position = 1;
		else if ("ml".equalsIgnoreCase(productDetails.getUom()))
			position = 2;
		return position;
	}

	private List<String> getUOMs() {
		List<String> list = new ArrayList<String>();

		uomTable = new Hashtable<String, String>();

		list.add(context.getResources().getString(R.string.select));

		if ("kg".equalsIgnoreCase(productDetails.getUom())
				|| "gm".equalsIgnoreCase(productDetails.getUom())) {
			list.add("kg");
			uomTable.put("kg", "1");
			list.add("gm");
			uomTable.put("gm", "2");
		} else {
			list.add("lt");
			uomTable.put("lt", "3");

			list.add("ml");
			uomTable.put("ml", "4");
		}

		return list;
	}

	// damage types based on position
	private List<String> getDamageTypes() {

		List<String> list = new ArrayList<String>();

		damageTypetable = new Hashtable<String, String>();

		list.add(context.getResources().getString(R.string.select));

		list.add(context.getResources().getString(R.string.expired));
		damageTypetable.put(context.getResources().getString(R.string.expired),
				"1");

		list.add(context.getResources().getString(R.string.breakable));
		damageTypetable.put(context.getResources()
				.getString(R.string.breakable), "2");

		list.add(context.getResources().getString(R.string.theft));
		damageTypetable.put(context.getResources().getString(R.string.theft),
				"3");

		list.add(context.getResources().getString(R.string.physical_recount));
		damageTypetable.put(
				context.getResources().getString(R.string.physical_recount),
				"4");
		/*
		 * for (DTO dto : listDamages) { DamageTypeDTO gDto = (DamageTypeDTO)
		 * dto; list.add(gDto.getName()); damageTypetable.put(gDto.getName(),
		 * gDto.getDamageTypeId());
		 * 
		 * }
		 */
		return list;
	}

	private void populateDamageTypes() {

		damageTypeAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, getDamageTypes());
		damageTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnReason.setAdapter(damageTypeAdapter);
	}

	private void getDataFromDB() {
		invAdjustdto = InventoryAdjustmentDAO.getInstance()
				.getRecordsByProductID(DBHandler.getDBObj(Constants.READABLE),
						productDetails.getProductCode());
	}

	// Inventory adjustment update data
	private InventoryAdjustmentDTO getUpdatedData() {
		/*
		 * Double quantity =
		 * (CommonMethods.getDoubleFormate(invAdjustdto.getQuantity())) -
		 * (CommonMethods.getDoubleFormate(qty));
		 */
		String adjQty = "";
		if (uom.equals(context.getResources().getString(R.string.select)))
			uom = uom;

		if ("".equals(uom)) {
			System.out.println("Indise If condition ********");
			System.out.println("Qty from dto : " + invAdjustdto.getQuantity());
			System.out
					.println("Qty from input : " + invAdjustdto.getQuantity());
			adjQty = String.valueOf(CommonMethods.getDoubleFormate(invAdjustdto
					.getQuantity())
					+ CommonMethods.getDoubleFormate(etxtQty.getText()
							.toString().trim()));
		} else {
			System.out.println("Indise else condition ********");
			adjQty = CommonMethods.UOMConversions(invAdjustdto.getQuantity(),
					invAdjustdto.getUom(), etxtQty.getText().toString().trim(),
					uom, "1", SalesEditTypes.INVOICE_ADJUSTMENT.code());
		}

		System.out.println("Qty :" + adjQty);

		invAdjustdto.setDamageTypeId(damageTypetable.get(reason));
		// invAdjustdto.setUom(uom);
		invAdjustdto.setQuantity(String.valueOf(adjQty));
		invAdjustdto.setSyncStatus(0);
		return invAdjustdto;

	}

	// update inventory data
	private void updateInventoryData() {

		/*
		 * Double quantity =
		 * (CommonMethods.getDoubleFormate(productDetails.getQuantity())) -
		 * (CommonMethods.getDoubleFormate(qty));
		 */

		List<DTO> invList = new ArrayList<DTO>();

		InventoryDTO dto = new InventoryDTO();
		dto.setProductId(productDetails.getProductCode());
		dto.setQuantity(String.valueOf(qty));
		String qtyValueForm = etxtQty.getText().toString().trim();
		dto.setQuantityBalance(CommonMethods.getBalanceQuantity(productDetails.getProductCode(), "-" + etxtQty.getText().toString().trim()));
		dto.setUom("gm");
		dto.setSyncStatus(Constants.FALSE);
		invList.add(dto);

		if (InventoryDAO.getInstance().update(
				DBHandler.getDBObj(Constants.WRITABLE), dto)) {
			if (synkdata(productDetails.getProductCode(), dto.getQuantityBalance().toString(),
					Constants.FALSE, "gm", reason)) {
				CommonMethods.showCustomToast(context, context.getResources()
						.getString(R.string.inventory_ady_update));
			}
		}
		saveListener.onSaveClick();
		this.dismiss();

	}

	// sync to Inventory and Inventory_adjustment tables
	private boolean synkdata(String productCode, String qty, int SyncStatu,
			String UMO, String reson) {

		List<DTO> invList = InventoryDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "product_id",
				productCode);
		InventoryDTO dtoInventory = null;
		for (int i = 0; i < invList.size(); i++) {
			dtoInventory = (InventoryDTO) invList.get(i);
			inventoryId = dtoInventory.getInventoryId();
		}
		String resp = new AsynkTaskClass().callcentralserver("/sync", ""
						+ ServiceHandler.POST,
				getInventoryObj(productCode, qty, SyncStatu, UMO), context);
		if (resp != null) {

			if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
				HashMap<String,Double> inventoryServer= new JSONStatus().data(resp);

				for(DTO dto: invList){
					InventoryDTO inventoryDTOServer = (InventoryDTO) dto;
					inventoryDTOServer.setQuantityBalance(0.0);
					if (inventoryServer.containsKey(inventoryDTOServer.getProductId()))
						inventoryDTOServer.setQuantity(inventoryServer.get(inventoryDTOServer.getProductId()).toString());
					InventoryDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), inventoryDTOServer);
				}


				if (new AsynkTaskClass().callcentralserver(
						"/sync",
						"" + ServiceHandler.POST,
						getInventoryAdjustmentObj(productCode, qty, SyncStatu,
								UMO, reson), context) != null) {
					if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
						InventoryAdjustmentDTO dto = InventoryAdjustmentDAO
								.getInstance().getRecordsByProductID(
										DBHandler.getDBObj(Constants.READABLE),
										productCode);
						InventoryAdjustmentDAO.getInstance().delete(
								DBHandler.getDBObj(Constants.READABLE), dto);
						return true;
					} else {
						return false;
					}
				}
			} else {
				return false;
			}

		}
		return false;
	}

	private String getInventoryAdjustmentObj(String productCode, String qty2,
			int syncStatu, String uMO, String reson) {
		
		InventoryAdjustmentDTO dto = InventoryAdjustmentDAO.getInstance()
				.getRecordsByProductID(DBHandler.getDBObj(Constants.READABLE),
						productCode);

		inventoryAdjustmentId = invAdjustdto.getAdjustmentId();

		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("name", "inventory_adjustment");
			jsonObj.put("type", "insert");
			JSONArray jsonaarray2 = new JSONArray();
			jsonaarray2.put("username");
			jsonObj.put("columns", jsonaarray2);
			JSONArray jsonaarray = new JSONArray();
			JSONObject jsonObj2 = new JSONObject();
			try {
				jsonObj2.put("adjustment_id", (dto.getAdjustmentId()));
			} catch (Exception e) {
			}
			jsonObj2.put("barcode", dto.getProductId());
			jsonObj2.put("damage_type_id",
					Long.parseLong(dto.getDamageTypeId()));
			jsonObj2.put("store_code", ServiApplication.store_id);
			jsonObj2.put("quantity", dto.getQuantity());
			jsonObj2.put("uom", uom); 
			jsonaarray.put(jsonObj2);
			jsonObj.put("records", jsonaarray);
			return jsonObj.toString();
		} catch (JSONException e) {

			return jsonObj.toString();
		}
	}

	private String getInventoryObj(String productCode, String qty2,
			int syncStatu, String uMO) {
		
		InventoryDTO dto = InventoryDAO.getInstance().getRecordByProductID(
				DBHandler.getDBObj(Constants.READABLE), productCode);

		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("name", "inventory");
			jsonObj.put("type", "update");

			JSONArray jsonaarray2 = new JSONArray();
			jsonaarray2.put("barcode");
			jsonaarray2.put("store_code");
			jsonObj.put("columns", jsonaarray2);

			JSONArray jsonaarray = new JSONArray();
			JSONObject jsonObj2 = new JSONObject();
			jsonObj2.put("inventory_id", Long.parseLong(inventoryId));
			jsonObj2.put("barcode", productCode);
			jsonObj2.put("store_code", ServiApplication.store_id);
			jsonObj2.put("quantity", qty2);
			jsonObj2.put("uom", dto.getUom());
			jsonaarray.put(jsonObj2);
			jsonObj.put("records", jsonaarray);
			return jsonObj.toString();
			
		} catch (JSONException e) {
			return jsonObj.toString();
		}
	}

	private List<DTO> getInsertData() {

		List<DTO> list = new ArrayList<DTO>();
		
		
		if (uom.equals(context.getResources().getString(R.string.select)))
			uom = "uom";

		System.out.println("UOM :" + uom);

		InventoryAdjustmentDTO dto = new InventoryAdjustmentDTO();
		dto.setAdjustmentId(Dates.getSysDateinMilliSeconds() + "");
		dto.setProductId(productDetails.getProductCode());
		dto.setQuantity(etxtQty.getText().toString().trim());
		dto.setUom(uom);
		dto.setDamageTypeId(damageTypetable.get(reason));
		dto.setSyncStatus(Constants.FALSE);
		list.add(dto);
		// InventoryAdjustmentDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),list);

		return list;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		if (dialog == saveDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				if (invAdjustdto.getProductId() == null) {
					if (InventoryAdjustmentDAO.getInstance().insert(
							DBHandler.getDBObj(Constants.WRITABLE),
							getInsertData())) {
						updateInventoryData();
					}
				} else {
					if (InventoryAdjustmentDAO.getInstance().update(
							DBHandler.getDBObj(Constants.WRITABLE),
							getUpdatedData())) {
						updateInventoryData();
					}
				}
			}
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btn_AdjustSave:
			validateAndsave();

			break;

		case R.id.btn_AdjustCancel:
			this.dismiss();

			break;

		default:
			break;
		}
	}

	private void validateAndsave() {
		if (!validationFields())
			CommonMethods.showCustomToast(context, context.getResources()
					.getString(R.string.validate_fileds));
		else if (validateQty())
			saveDialog = CommonMethods.displayAlert(true, context
					.getResources().getString(R.string.confirm), context
					.getResources().getString(R.string.save_messgae), context
					.getResources().getString(R.string.yes), context
					.getResources().getString(R.string.no), context, this,
					false);
	}

	private boolean validateQty() {
		System.out.println("Units :" + productDetails.getUom());
		boolean qtyFlag = false;
		if ("".equalsIgnoreCase(productDetails.getUom())) {
			qtyFlag = true;
			qty = String.valueOf(CommonMethods.getDoubleFormate(productDetails
					.getQuantity())
					- CommonMethods.getDoubleFormate(selectedQty));
		} else if (uom.equalsIgnoreCase(productDetails.getUom())) {
			if (showError(selectedQty, productDetails.getQuantity())) {
				qty = String.valueOf(CommonMethods.getRoundedVal(CommonMethods
						.getDoubleFormate(productDetails.getQuantity())
						- CommonMethods.getDoubleFormate(selectedQty)));
				qtyFlag = true;
			}
		} else {
			double qtyFromTable = 0;
			double finalQty = 0;
			if ("kg".equalsIgnoreCase(productDetails.getUom())
					&& "gm".equalsIgnoreCase(uom)
					|| "lt".equalsIgnoreCase(productDetails.getUom())
					&& "ml".equalsIgnoreCase(uom)) {
				qtyFromTable = CommonMethods.getDoubleFormate(productDetails
						.getQuantity()) * 1000;
				if (showError(selectedQty, String.valueOf(qtyFromTable))) {
					finalQty = qtyFromTable
							- CommonMethods.getDoubleFormate(selectedQty);
					qty = String.valueOf(CommonMethods
							.getRoundedVal(finalQty * 0.001));
					qtyFlag = true;
				}

			} else if ("gm".equalsIgnoreCase(productDetails.getUom())
					&& "kg".equalsIgnoreCase(uom)
					|| "ml".equalsIgnoreCase(productDetails.getUom())
					&& "lt".equalsIgnoreCase(uom)) {
				qtyFromTable = CommonMethods.getDoubleFormate(productDetails
						.getQuantity()) * 0.001;
				if (showError(selectedQty, String.valueOf(qtyFromTable))) {
					finalQty = qtyFromTable
							- CommonMethods.getDoubleFormate(selectedQty);
					qty = String.valueOf(CommonMethods
							.getRoundedVal(finalQty * 1000));
					qtyFlag = true;
				}
			}
		}
		return qtyFlag;
	}

	private boolean showError(String selectedQty, String qtyFromDTO) {
		boolean error = false;
		if (CommonMethods.getDoubleFormate(selectedQty) > CommonMethods
				.getDoubleFormate(qtyFromDTO))
			CommonMethods.showCustomToast(context, context.getResources()
					.getString(R.string.inv_adjust_qty_msg));
		else
			error = true;
		return error;
	}

	private boolean validationFields() {
		isvalid = true;
		reason = spnReason.getSelectedItem().toString().trim();
		uom = spnUOM.getSelectedItem().toString().trim();
		selectedQty = etxtQty.getText().toString().trim();

		if (reason.equals(context.getResources().getString(R.string.select)))
			isvalid = false;

		if (selectedQty.length() == 0)
			isvalid = false;

		if (!"".equals(productDetails.getUom())) {
			if (uom.equals(context.getResources().getString(R.string.select)))
				isvalid = false;
		}
		return isvalid;

	}

}
