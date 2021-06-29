package com.micaja.servipunto.dialog;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.DishProductsDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.MenuInventoryDAO;
import com.micaja.servipunto.database.dto.AddMenusInvenDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DishProductsDTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.MenuInventoryDTO;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.SalesCodes;

public class AddMenusInveDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private Context context;
	private Button btnDeleteYes, btnDeleteNo;
	ServiApplication appContext;
	private TextView txtTitle, txtMsg;
	private Handler uiHandler;
	private ArrayList<String> AddMenusInvenID = new ArrayList<String>();
	private ArrayList<String> InventoryID = new ArrayList<String>();

	public AddMenusInveDialog(Context context, Handler uiHandler) {
		super(context);
		this.context = context;
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
		setContentView(R.layout.sales_delete_dialog);

		btnDeleteYes = (Button) findViewById(R.id.btn_DeleteYes);
		btnDeleteNo = (Button) findViewById(R.id.btn_DeleteNo);
		txtTitle = (TextView) findViewById(R.id.txt_DeleteT);
		txtMsg = (TextView) findViewById(R.id.txt_Delete);

		txtTitle.setText(context.getResources().getString(
				R.string.menus_inven_add));
		txtMsg.setText(context.getResources().getString(
				R.string.menus_inven_add_msg));

		btnDeleteYes.setOnClickListener(this);
		btnDeleteNo.setOnClickListener(this);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_DeleteYes:
			this.dismiss();
			CommonMethods.showProgressDialog(
					context.getResources().getString(R.string.please_wait),
					context);
			List<DTO> list = appContext.getSelectedProducts();
			insertMenuInventory(list);
			break;

		case R.id.btn_DeleteNo:
			this.dismiss();
			break;

		default:
			break;
		}
	}

	private void insertMenuInventory(List<DTO> list) {
		List<DTO> insertList = new ArrayList<DTO>();
		List<DTO> updateList = new ArrayList<DTO>();

		List<DTO> menusInvenId = MenuInventoryDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));

		List<String> dishId = MenuInventoryDAO.getInstance()
				.getMenuInvenRecords(DBHandler.getDBObj(Constants.READABLE));

		for (int i = 0; i < list.size(); i++) {
			AddMenusInvenDTO menuDTO = (AddMenusInvenDTO) list.get(i);

			if (dishId.contains(menuDTO.getDishID())) {
				MenuInventoryDTO dto = new MenuInventoryDTO();
				int position = dishId.indexOf(menuDTO.getDishID());
				MenuInventoryDTO inventoryDTO = (MenuInventoryDTO) menusInvenId
						.get(position);

				double qty = CommonMethods.getDoubleFormate(menuDTO
						.getQuantity())
						+ CommonMethods.getDoubleFormate(inventoryDTO
								.getCount());

				dto.setMenuInventoryId(inventoryDTO.getMenuInventoryId());
				dto.setCount(String.valueOf(qty));
				dto.setDishId(menuDTO.getDishID());
				dto.setSyncStatus(0);

				updateList.add(dto);
			} else {
				MenuInventoryDTO dto = new MenuInventoryDTO();
				dto.setCount(menuDTO.getQuantity());
				dto.setDishId(menuDTO.getDishID());
				dto.setSyncStatus(0);

				insertList.add(dto);
			}
		}
		if (insertList.size() != 0)
			insertRecords(insertList, updateList);
		else if (updateList.size() != 0)
			updateMenu(updateList);
	}

	private void insertRecords(List<DTO> insertList, List<DTO> updateList) {
		if (MenuInventoryDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), insertList)) {
			List<DTO> list = MenuInventoryDAO.getInstance().getRecords(
					DBHandler.getDBObj(Constants.READABLE));
			MenuInventoryDTO menudto = (MenuInventoryDTO) list
					.get(list.size() - 1);
			AddMenusInvenID.add(menudto.getMenuInventoryId());
			updateMenu(updateList);
		}

	}

	private void updateMenu(List<DTO> updateList) {
		if (updateList.size() != 0) {
			for (int i = 0; i < updateList.size(); i++) {
				MenuInventoryDAO.getInstance().update(
						DBHandler.getDBObj(Constants.WRITABLE),
						updateList.get(i));
			}
		}

		updateProducts();

		this.dismiss();
	}

	private void updateProducts() {
		List<DTO> list = appContext.getSelectedProducts();

		for (int i = 0; i < list.size(); i++) {
			AddMenusInvenDTO addMenusInvenDTO = (AddMenusInvenDTO) list.get(i);

			List<DTO> dishesList = DishProductsDAO.getInstance()
					.getRecordsWithValues(
							DBHandler.getDBObj(Constants.READABLE), "dish_id",
							addMenusInvenDTO.getDishID());

			for (int j = 0; j < dishesList.size(); j++) {
				DishProductsDTO dishesDTO = (DishProductsDTO) dishesList.get(j);

				List<DTO> productsList = InventoryDAO.getInstance()
						.getRecordsWithValues(
								DBHandler.getDBObj(Constants.READABLE),
								"product_id", dishesDTO.getProductId());

				if (productsList.size() != 0) {
					InventoryDTO inventoryDTO = (InventoryDTO) productsList
							.get(0);
					InventoryID.add(inventoryDTO.getInventoryId());
					// Inventory Data
					String inventoryQty = inventoryDTO.getQuantity();
					String inventoryUOM = inventoryDTO.getUom();

					// Dish Product Data
					String dishProdQty = dishesDTO.getQuantity();
					String dishProdUOM = dishesDTO.getUom();

					// Selected Dish Product Data
					String slectedQty = addMenusInvenDTO.getQuantity();

					String finalQty = CommonMethods.UOMConversions(
							inventoryQty, inventoryUOM, dishProdQty,
							dishProdUOM, slectedQty, "");

					// double finalQty =
					// (Double.parseDouble(inventoryQty))-((Double.parseDouble(dishProdQty))*Double.parseDouble(slectedQty));

					inventoryDTO.setQuantity(finalQty);

					InventoryDAO.getInstance().update(
							DBHandler.getDBObj(Constants.READABLE),
							inventoryDTO);
				}
			}
		}
		sendMessage(AddMenusInvenID, InventoryID);
	}

	private void sendMessage(ArrayList<String> addMenusInvenID,
			ArrayList<String> inventoryID) {
		CommonMethods.dismissProgressDialog();
		if (SynkCenteralDb(addMenusInvenID, inventoryID)) {
			Message msg = new Message();
			msg.arg1 = SalesCodes.ADD_MENUS_INVENTORY.code();
			uiHandler.sendMessage(msg);
		}
	}

	private boolean SynkCenteralDb(ArrayList<String> addMenusInvenID,
			ArrayList<String> inventoryID) {
		if (new AsynkTaskClass().callcentralserver("/sync", ""
				+ ServiceHandler.POST, getJsonObjcet(addMenusInvenID), context) != null) {
			if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
				if (new AsynkTaskClass().callcentralserver("/sync", ""
						+ ServiceHandler.POST,
						getJsonObjcetInventory(inventoryID), context) != null) {
					if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
						if (new AsynkTaskClass().callcentralserver("/sync", ""
								+ ServiceHandler.POST,
								getJsonObjcetInventoryHistory(inventoryID),
								context) != null) {
							if (new JSONStatus()
									.status(ServiApplication.responds_feed) == 0) {
								updatesynckstatus(addMenusInvenID, inventoryID,
										inventoryID);
								return true;
							}
						}
					}
				}
			}
		}
		return true;
	}

	private void updatesynckstatus(ArrayList<String> addMenusInvenID2,
			ArrayList<String> inventoryID2, ArrayList<String> inventoryID3) {
		for (int i = 0; i < addMenusInvenID2.size(); i++) {
			List<DTO> productsList = MenuInventoryDAO.getInstance()
					.getRecordsWithValues(
							DBHandler.getDBObj(Constants.READABLE),
							"menu_inventory_id", addMenusInvenID2.get(i));
			MenuInventoryDTO inventoryDTO = (MenuInventoryDTO) productsList
					.get(0);
			inventoryDTO.setSyncStatus(1);
			MenuInventoryDAO.getInstance().update(
					DBHandler.getDBObj(Constants.READABLE), inventoryDTO);
		}
	}

	private String getJsonObjcetInventoryHistory(ArrayList<String> inventoryID2) {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("name", "inventory_history");
			jsonObject.put("type", "insert");
			JSONArray jsonarray = new JSONArray();
			for (int i = 0; i < inventoryID2.size(); i++) {
				List<DTO> productsList = InventoryDAO.getInstance()
						.getRecordsWithValues(
								DBHandler.getDBObj(Constants.READABLE),
								"inventory_id", inventoryID2.get(i));
				InventoryDTO inventoryDTO = (InventoryDTO) productsList.get(0);
				JSONObject jsonobj = new JSONObject();
				try {
					jsonobj.put("barcode", inventoryDTO.getProductId());
					jsonobj.put("quantity", inventoryDTO.getQuantity());
					jsonobj.put("uom", inventoryDTO.getUom());
					jsonobj.put("store_code", ServiApplication.store_id);
				} catch (NumberFormatException e) {
				} catch (JSONException e) {
				}
				jsonarray.put(jsonobj);
			}
			jsonObject.put("records", jsonarray);
			return jsonObject.toString();
		} catch (Exception e) {
			return jsonObject.toString();
		}

	}

	private String getJsonObjcetInventory(ArrayList<String> inventoryID2) {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("name", "inventory");
			jsonObject.put("type", "update");
			JSONArray jsonarray = new JSONArray();
			for (int i = 0; i < inventoryID2.size(); i++) {
				List<DTO> productsList = InventoryDAO.getInstance()
						.getRecordsWithValues(
								DBHandler.getDBObj(Constants.READABLE),
								"inventory_id", inventoryID2.get(i));
				InventoryDTO inventoryDTO = (InventoryDTO) productsList.get(0);
				JSONObject jsonobj = new JSONObject();
				try {
					jsonobj.put("inventory_id",
							Long.parseLong(inventoryDTO.getInventoryId()));
					jsonobj.put("barcode", inventoryDTO.getProductId());
					jsonobj.put("quantity", inventoryDTO.getQuantity());
					jsonobj.put("uom", inventoryDTO.getUom());
					jsonobj.put("store_code", ServiApplication.store_id);
				} catch (NumberFormatException e) {
				} catch (JSONException e) {
				}
				jsonarray.put(jsonobj);
			}
			jsonObject.put("records", jsonarray);
			JSONArray columns = new JSONArray();
			columns.put("inventory_id");
			jsonObject.put("columns", columns);
			return jsonObject.toString();
		} catch (Exception e) {
			return jsonObject.toString();
		}

	}

	private String getJsonObjcet(ArrayList<String> addMenusInvenID2) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("name", "menu_inventory");
			jsonObject.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("dish_id");
			columnarray.put("store_code");
			jsonObject.put("columns", columnarray);
			JSONArray jsonarray = new JSONArray();
			for (int i = 0; i < addMenusInvenID2.size(); i++) {
				List<DTO> productsList = MenuInventoryDAO.getInstance()
						.getRecordsWithValues(
								DBHandler.getDBObj(Constants.READABLE),
								"menu_inventory_id", addMenusInvenID2.get(i));
				MenuInventoryDTO inventoryDTO = (MenuInventoryDTO) productsList
						.get(0);
				JSONObject jsonobj = new JSONObject();
				try {
					jsonobj.put("menu_inventory_id",
							Long.parseLong(inventoryDTO.getMenuInventoryId()));
					jsonobj.put("count", inventoryDTO.getCount());
					jsonobj.put("dish_id",
							Long.parseLong(inventoryDTO.getDishId()));
					jsonobj.put("store_code", ServiApplication.store_id);
				} catch (NumberFormatException e) {
				} catch (JSONException e) {
				}
				jsonarray.put(jsonobj);
			}
			jsonObject.put("records", jsonarray);
			return jsonObject.toString();
		} catch (Exception e) {
			return jsonObject.toString();
		}
	}
}
