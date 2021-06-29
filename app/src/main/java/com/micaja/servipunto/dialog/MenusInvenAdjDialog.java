package com.micaja.servipunto.dialog;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.DamageTypeDAO;
import com.micaja.servipunto.database.dao.MenuInventoryAdjustmentDAO;
import com.micaja.servipunto.database.dao.MenuInventoryDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DamageTypeDTO;
import com.micaja.servipunto.database.dto.MenuInventoryAdjustmentDTO;
import com.micaja.servipunto.database.dto.MenuInventoryDTO;
import com.micaja.servipunto.database.dto.MenusInveDishesDTO;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.SalesCodes;

public class MenusInvenAdjDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private Button btnAdjustSave, btnAdjustCancel;
	private EditText etxtQty;
	private Spinner spnReason;
	private TextView txtDishName;
	private OnDialogSaveClickListener1 saveListener;
	private Hashtable<String, String> damageTypetable;
	private ArrayAdapter<String> damageTypeAdapter;
	private String reason, qty;
	private boolean isvalid;
	private AlertDialog saveDialog;
	private MenuInventoryAdjustmentDTO inventoryDTO;
	private DTO dtoFromAdap;
	private Handler uiHandler;
	private ImageView imgClose;
	// private MenuInventoryDTO menuInventoryDTO;
	private MenuInventoryDTO selectedDTO;

	public MenusInvenAdjDialog(Context context, DTO dtoFromAdap,
			Handler uiHandler) {

		super(context);
		this.context = context;
		this.dtoFromAdap = dtoFromAdap;
		this.uiHandler = uiHandler;
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

		setContentView(R.layout.menu_inventory_adj_dialog);

		btnAdjustSave = (Button) findViewById(R.id.btn_AdjustCancel);
		btnAdjustCancel = (Button) findViewById(R.id.btn_AdjustSave);
		etxtQty = (EditText) findViewById(R.id.etxt_adjustQuantity);
		spnReason = (Spinner) findViewById(R.id.spn_adjust_reason);
		txtDishName = (TextView) findViewById(R.id.txt_productlbl);
		imgClose = (ImageView) findViewById(R.id.img_close);

		btnAdjustSave.setOnClickListener(this);
		btnAdjustCancel.setOnClickListener(this);
		imgClose.setOnClickListener(this);

		prepareDTO();

		insertMasterTablesData();
		loadUI();

	}

	private void prepareDTO() {
		MenusInveDishesDTO menusInveDishesDTO = (MenusInveDishesDTO) dtoFromAdap;

		txtDishName.setText(context.getResources()
				.getString(R.string.dish_name)
				+ " : "
				+ menusInveDishesDTO.getDishName());

		selectedDTO = new MenuInventoryDTO();

		selectedDTO.setCount(menusInveDishesDTO.getCount());
		selectedDTO.setDishId(menusInveDishesDTO.getDishId());
		selectedDTO.setMenuInventoryId(menusInveDishesDTO.getMenuInventoryId());
	}

	private void loadUI() {
		populateDamageTypes();
		// populateUOM();
		getDataFromDB();
	}

	// Damage TYpes 
	private void insertMasterTablesData() {

		DamageTypeDTO ldto = new DamageTypeDTO();

		List<DTO> dlist = new ArrayList<DTO>();

		ldto.setDamageTypeId("1");
		ldto.setName("Expired");

		DamageTypeDTO ldto1 = new DamageTypeDTO();

		ldto1.setDamageTypeId("2");
		ldto1.setName("Breakable");

		dlist.add(ldto);
		dlist.add(ldto1);

		DamageTypeDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), dlist);

	}

	// Damage TYpes  based on options
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

		return list;
	}

	// Damage TYpes  dropdown
	private void populateDamageTypes() {

		damageTypeAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, getDamageTypes());
		damageTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnReason.setAdapter(damageTypeAdapter);
	}

	private void getDataFromDB() {
		inventoryDTO = MenuInventoryAdjustmentDAO.getInstance()
				.getRecordsByDishID(DBHandler.getDBObj(Constants.READABLE),
						selectedDTO.getDishId());
	}

	private MenuInventoryAdjustmentDTO getUpdatedData() {
		int qty = (Integer.valueOf(inventoryDTO.getCount()))
				+ (Integer.valueOf(etxtQty.getText().toString().trim()));
		inventoryDTO.setCount(String.valueOf(qty));
		return inventoryDTO;

	}

	// update inventory data
	private void updateInventoryData() {
		double qty = (CommonMethods.getDoubleFormate(selectedDTO.getCount()))
				- (CommonMethods.getDoubleFormate(etxtQty.getText().toString()
						.trim()));

		MenuInventoryDTO dto = new MenuInventoryDTO();
		dto.setMenuInventoryId(selectedDTO.getMenuInventoryId());
		dto.setDishId(selectedDTO.getDishId());
		dto.setCount(String.valueOf(qty));
		// dto.setUom("gm");
		dto.setSyncStatus(Constants.FALSE);

		if (MenuInventoryDAO.getInstance().update(
				DBHandler.getDBObj(Constants.WRITABLE), dto)) {
			if (SynkCentralServer(dto)) {
				CommonMethods.showToast(context, context.getResources()
						.getString(R.string.updated_successfully));
				sendMessage();
			}
		}

		// saveListener.onSaveClick();
		this.dismiss();

	}

	// data sync purpose
	private boolean SynkCentralServer(MenuInventoryDTO dto) {

		if (new AsynkTaskClass().callcentralserver("/sync", ""
				+ ServiceHandler.POST, makeMenuInventAdj(dto), context) != null) {

			if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
				if (new AsynkTaskClass().callcentralserver("/sync", ""
						+ ServiceHandler.POST, makeMenuInventUpdate(dto),
						context) != null) {
					if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
						updateMenuadj();
						return true;
					}
				}
			}
		} else {
			return true;
		}

		return true;
	}

	private void updateMenuadj() {
		MenuInventoryAdjustmentDTO mjdto = null;
		List<DTO> dto = MenuInventoryAdjustmentDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		for (DTO dto2 : dto) {
			mjdto = (MenuInventoryAdjustmentDTO) dto2;
		}
		mjdto.setSyncStatus(1);
		MenuInventoryAdjustmentDAO.getInstance().update(
				DBHandler.getDBObj(Constants.WRITABLE), mjdto);
	}

	// make a json object to menu_inventory
	private String makeMenuInventUpdate(MenuInventoryDTO dto) {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "menu_inventory");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("dish_id");
			columnarray.put("store_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonarray = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("menu_inventory_id",
					Long.parseLong(dto.getMenuInventoryId()));
			obj.put("dish_id", Long.parseLong(dto.getDishId()));
			obj.put("count", dto.getCount());
			obj.put("store_code", ServiApplication.store_id);
			jsonarray.put(obj);
			jsonobj.put("records", jsonarray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	// make a json object to menu_inventory_adjustment
	private String makeMenuInventAdj(MenuInventoryDTO dto) {
		JSONObject jsonobj = new JSONObject();
		String DishId = dto.getDishId();
		MenuInventoryAdjustmentDTO menuinventorydto = MenuInventoryAdjustmentDAO
				.getInstance().getRecordsByDishID(
						DBHandler.getDBObj(Constants.WRITABLE), DishId);
		try {
			jsonobj.put("name", "menu_inventory_adjustment");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("dish_id");
			jsonobj.put("columns", columnarray);
			JSONArray jsonarray = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("menu_adjustment_id",
					Long.parseLong(menuinventorydto.getMenuAdjustmentId()));
			obj.put("dish_id", Long.parseLong(menuinventorydto.getDishId()));
			obj.put("count", menuinventorydto.getCount());
			obj.put("store_code", ServiApplication.store_id);
			jsonarray.put(obj);
			jsonobj.put("records", jsonarray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	private void sendMessage() {
		Message msg = new Message();
		msg.arg1 = SalesCodes.EDIT.code();
		uiHandler.sendMessage(msg);
	}

	private List<DTO> getInsertData() {
		List<DTO> list = new ArrayList<DTO>();

		MenuInventoryAdjustmentDTO dto = new MenuInventoryAdjustmentDTO();
		dto.setMenuAdjustmentId(Dates.getSysDateinMilliSeconds() + "");
		dto.setDishId(selectedDTO.getDishId());
		dto.setCount(etxtQty.getText().toString().trim());
		// dto.setUom(uom);
		// dto.setDamageTypeId(damageTypetable.get(reason));
		dto.setSyncStatus(Constants.FALSE);
		// InventoryAdjustmentDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
		// list);

		list.add(dto);

		return list;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		if (dialog == saveDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				if (inventoryDTO.getDishId() == null) {
					if (MenuInventoryAdjustmentDAO.getInstance().insert(
							DBHandler.getDBObj(Constants.WRITABLE),
							getInsertData()))
						updateInventoryData();
				} else {
					if (MenuInventoryAdjustmentDAO.getInstance().update(
							DBHandler.getDBObj(Constants.WRITABLE),
							getUpdatedData()))
						updateInventoryData();
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
		case R.id.img_close:

			this.dismiss();
			break;

		default:
			break;
		}
	}

	// Result of this method, Validation confirmation Alert using stringBuffer
	private void validateAndsave() {

		if (!validationFields())
			CommonMethods.showToast(context,
					context.getResources().getString(R.string.validate_fileds));
		else if (CommonMethods.getDoubleFormate(qty) > CommonMethods
				.getDoubleFormate(selectedDTO.getCount()))
			CommonMethods.showToast(
					context,
					context.getResources().getString(
							R.string.inv_adjust_qty_msg));

		else
			saveDialog = CommonMethods.displayAlert(true, context
					.getResources().getString(R.string.confirm), context
					.getResources().getString(R.string.save_messgae), context
					.getResources().getString(R.string.yes), context
					.getResources().getString(R.string.no), context, this,
					false);
	}

	// This method using for Validation purpose
	private boolean validationFields() {
		isvalid = true;
		reason = spnReason.getSelectedItem().toString().trim();
		// uom = spnUOM.getSelectedItem().toString().trim();
		qty = etxtQty.getText().toString().trim();
		if (reason.equals(context.getResources().getString(R.string.select)))
			isvalid = false;
		/*
		 * if (uom.equals(context.getResources().getString(R.string.select)))
		 * isvalid = false;
		 */
		if (qty.length() == 0) {
			isvalid = false;
		}

		return isvalid;

	}

}
