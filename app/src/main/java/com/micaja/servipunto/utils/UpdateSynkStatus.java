package com.micaja.servipunto.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.SincronizarTransaccionesDAO;
import com.micaja.servipunto.database.dto.CashFlowDTO;
import com.micaja.servipunto.database.dto.ClientDTO;
import com.micaja.servipunto.database.dto.ClientPaymentsDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DeliveryDTO;
import com.micaja.servipunto.database.dto.DeliveryPaymentsDTO;
import com.micaja.servipunto.database.dto.DishProductsDTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.database.dto.InventoryAdjustmentDTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.InventoryHistoryDTO;
import com.micaja.servipunto.database.dto.LendmoneyDTO;
import com.micaja.servipunto.database.dto.MenuDTO;
import com.micaja.servipunto.database.dto.MenuDishesDTO;
import com.micaja.servipunto.database.dto.MenuInventoryAdjustmentDTO;
import com.micaja.servipunto.database.dto.MenuInventoryDTO;
import com.micaja.servipunto.database.dto.OrderDetailsDTO;
import com.micaja.servipunto.database.dto.OrdersDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SalesDTO;
import com.micaja.servipunto.database.dto.SalesDetailDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.database.dto.SupplierPaymentsDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;

public final class UpdateSynkStatus implements DTO {

	private List<DTO> lIst = new ArrayList<DTO>();
	private int tAbleType;

	public UpdateSynkStatus(List<DTO> list, int TableType) {
		this.lIst = list;
		this.tAbleType = TableType;
		updateLocalTable();
	}

	private void updateLocalTable() {

		if (tAbleType == ServiApplication.Client_Info) {
			ClientTableUpdate();
		} else if (tAbleType == ServiApplication.CashFlowList) {
			CashFlowTableUpdate();
		} else if (tAbleType == ServiApplication.DishProductsList) {
			DishProductsUpdate();
		} else if (tAbleType == ServiApplication.ClientPayList) {
			ClientPaymentUpdate();
		} else if (tAbleType == ServiApplication.dishlist) {
			DishUpdate();
		} else if (tAbleType == ServiApplication.menudishlist) {
			MenuDishUpdate();
		} else if (tAbleType == ServiApplication.inventory) {
			InventoryUpdate();
		}else if (tAbleType == ServiApplication.inventoryAdj) {
			InventoryAdjUpdate();
		}else if (tAbleType == ServiApplication.inventoryHistory) {
			InventoryHistoryUpdate();
		}else if (tAbleType == ServiApplication.lendMoney) {
			LendMoneyUpdate();
		}else if (tAbleType == ServiApplication.menu) {
			MenuUpdate();
		}else if (tAbleType == ServiApplication.menuInventory) {
			MenuInventoryUpdate();
		}else if (tAbleType == ServiApplication.menuInventoryAdj) {
			MenuInventoryAdjUpdate();
		}else if (tAbleType == ServiApplication.orderDetails) {
			OrderDetailsUpdate();
		}else if (tAbleType == ServiApplication.orders) {
			OrdersUpdate();
		}else if (tAbleType == ServiApplication.sales) {
			salesUpdate();
		}else if (tAbleType == ServiApplication.salesDetails) {
			salesDetailsUpdate();
		}else if (tAbleType == ServiApplication.clientpayments) {
			clientPaymentUpdate();
		}else if (tAbleType == ServiApplication.supplier) {
			supplierUpdate();
		}else if (tAbleType == ServiApplication.userTable) {
			userTableUpdate();
		}else if (tAbleType == ServiApplication.supplierpayments) {
			SupplierTableUpdate();
		}else if (tAbleType == ServiApplication.updateProducts) {
			updateProductTableUpdate();
		}else if (tAbleType == ServiApplication.updatepuntoredsync) {
			updatepuntoredsync();
		}else if (tAbleType == ServiApplication.deliveryPayments){
			deliveryPaymentUpdate();
		}else if (tAbleType == ServiApplication.Delivery_Info){
			DeliveryTableUpdate();
		}

	}

	private void updatepuntoredsync() {
		for (DTO dto : lIst) {
			SincronizarTransaccionesDAO.getInstance().updateServiTiendaSynck(DBHandler.getDBObj(Constants.WRITABLE), dto);
		}
		
	}

	private void updateProductTableUpdate() {
		for (DTO dto : lIst) {
			ProductDTO clientdto = (ProductDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE), clientdto, "tblm_product", "barcode", clientdto.getBarcode());
		}
	}

	private void SupplierTableUpdate() {
		for (DTO dto : lIst) {
			SupplierPaymentsDTO clientdto = (SupplierPaymentsDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_supplier_payments","supplier_payment_id",clientdto.getSupplierPaymentId());
		}
		
	}

	private void userTableUpdate() {

		for (DTO dto : lIst) {
			UserDetailsDTO clientdto = (UserDetailsDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tblm_user","user_id",clientdto.getUserId());
		}
	
		
	}

	private void supplierUpdate() {
		for (DTO dto : lIst) {
			SupplierDTO clientdto = (SupplierDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tblm_supplier","supplier_id",clientdto.getSupplierId());
		}
	}

	private void clientPaymentUpdate() {
		for (DTO dto : lIst) {
			ClientPaymentsDTO clientdto = (ClientPaymentsDTO) dto;
			clientdto.setSyncStatus(1);
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_client_payments","payment_id",clientdto.getPaymentId());
		}
	}

	private void deliveryPaymentUpdate() {
		for (DTO dto : lIst) {
			DeliveryPaymentsDTO deliveryPaymentsDTO = (DeliveryPaymentsDTO) dto;
			deliveryPaymentsDTO.setSyncStatus(1);
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),deliveryPaymentsDTO,"tbl_delivery_payments","payment_id",deliveryPaymentsDTO.getPaymentId());
		}
	}

	private void salesDetailsUpdate() {
		for (DTO dto : lIst) {
			SalesDetailDTO clientdto = (SalesDetailDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_sales_details","sale_id",clientdto.getSaleId());
		}
	}

	private void salesUpdate() {

		for (DTO dto : lIst) {
			SalesDTO clientdto = (SalesDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_sales","sale_id",clientdto.getSaleID());
		}
	
	}

	private void OrdersUpdate() {

		for (DTO dto : lIst) {
			OrdersDTO clientdto = (OrdersDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_orders","order_id",clientdto.getOrderId());
		}
	}

	private void OrderDetailsUpdate() {

		for (DTO dto : lIst) {
			OrderDetailsDTO clientdto = (OrderDetailsDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_order_details","order_details_id",clientdto.getOrderDetailsId());
		}
	
		
	}

	private void MenuInventoryAdjUpdate() {
		for (DTO dto : lIst) {
			MenuInventoryAdjustmentDTO clientdto = (MenuInventoryAdjustmentDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_menu_inventory_adjustment","menu_adjustment_id",clientdto.getMenuAdjustmentId());
		}
	}

	private void MenuInventoryUpdate() {

		for (DTO dto : lIst) {
			MenuInventoryDTO clientdto = (MenuInventoryDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_menu_inventory","menu_inventory_id",clientdto.getMenuInventoryId());
		}
	}

	private void MenuUpdate() {

		for (DTO dto : lIst) {
			MenuDTO clientdto = (MenuDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_menu","menu_id",clientdto.getMenuId());
		}
	}

	private void LendMoneyUpdate() {

		for (DTO dto : lIst) {
			LendmoneyDTO clientdto = (LendmoneyDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_lend_money","lend_id",clientdto.getLendId());
		}
	}

	private void InventoryHistoryUpdate() {

		for (DTO dto : lIst) {
			InventoryHistoryDTO clientdto = (InventoryHistoryDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_inventory_history","inventory_history_id",clientdto.getInventorHistoryId());
		}
		
	
		
	}

	private void InventoryAdjUpdate() {
		for (DTO dto : lIst) {
			InventoryAdjustmentDTO clientdto = (InventoryAdjustmentDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_inventory_adjustment","adjustment_id",clientdto.getAdjustmentId());
		}
		
	}

	private void InventoryUpdate() {

		for (DTO dto : lIst) {
			InventoryDTO clientdto = (InventoryDTO) dto;
			clientdto.setSyncStatus(1);
			updateDishSynkStatus(DBHandler.getDBObj(Constants.WRITABLE), clientdto);
		}

	}

	private void updateDishSynkStatus(SQLiteDatabase dbObject,
			InventoryDTO clientdto) {
		try {
			InventoryDTO dtoObj = clientdto;
			String whereCls = "inventory_id = '" + dtoObj.getInventoryId()
					+ "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", dtoObj.getSyncStatus());
			//cValues.put("quantity", dtoObj.getQuantity());
			//cValues.put("quantity_balance",dtoObj.getQuantityBalance());
			dbObject.update("tbl_inventory", cValues, whereCls, null);
		} catch (SQLException e) {
			Log.e("clientDAO  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
	}

	private void MenuDishUpdate() {

		for (DTO dto : lIst) {
			MenuDishesDTO clientdto = (MenuDishesDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_menu_dishes","menu_dishes_id",clientdto.getMenuDishesId());
			
		}
	}



	private void DishUpdate() {

		for (DTO dto : lIst) {
			DishesDTO clientdto = (DishesDTO) dto;
			clientdto.setSyncStatus(1);
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_dishes","dish_id",clientdto.getDishId());
			
		}

	}

	@SuppressWarnings("unused")
	private void updateDishSynkStatus(SQLiteDatabase dbObject,
			DishesDTO clientdto) {

		try {
			DishesDTO dtoObj = clientdto;
			String whereCls = "dish_id = '" + dtoObj.getDishId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tblm_client", cValues, whereCls, null);
		} catch (SQLException e) {
			Log.e("clientDAO  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}

	}

	private void ClientPaymentUpdate() {
		for (DTO dto : lIst) {
			ClientPaymentsDTO clientdto = (ClientPaymentsDTO) dto;
			clientdto.setSyncStatus(1);
			updateClientPaymentsSynkStatus(
					DBHandler.getDBObj(Constants.WRITABLE), clientdto);
		}

	}

	private void updateClientPaymentsSynkStatus(SQLiteDatabase dbObject,
			ClientPaymentsDTO clientdto) {
		try {
			ClientPaymentsDTO dtoObj = clientdto;
			String whereCls = "client_id = '" + dtoObj.getClientId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tblm_client", cValues, whereCls, null);
		} catch (SQLException e) {
			Log.e("clientDAO  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
	}

	private void DishProductsUpdate() {
		for (DTO dto : lIst) {
			DishProductsDTO clientdto = (DishProductsDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tbl_dish_products","dish_product_id",clientdto.getDishProductId());
		}

	}

	private void CashFlowTableUpdate() {
		for (DTO dto : lIst) {
			CashFlowDTO clientdto = (CashFlowDTO) dto;
			clientdto.setSyncStatus(1);
			updateCashFlowSynkStatus(DBHandler.getDBObj(Constants.WRITABLE),
					clientdto);
		}

	}

	private void updateCashFlowSynkStatus(SQLiteDatabase dbObject,
			CashFlowDTO clientdto) {
		try {
			CashFlowDTO dtoObj = clientdto;
			String whereCls = "cash_flow_id = '" + dtoObj.getCashFlowId() + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", dtoObj.getSyncStatus());
			dbObject.update("tbl_cash_flow", cValues, whereCls, null);
		} catch (SQLException e) {
			Log.e("clientDAO  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}

	}

	private void ClientTableUpdate() {
		for (DTO dto : lIst) {
			ClientDTO clientdto = (ClientDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),clientdto,"tblm_client","client_id",clientdto.getClientID());
			}
	}


	private void DeliveryTableUpdate() {
		for (DTO dto : lIst) {
			DeliveryDTO deliveryDTO = (DeliveryDTO) dto;
			updateSyncStatus(DBHandler.getDBObj(Constants.WRITABLE),deliveryDTO,"tbl_delivery","delivery_id",deliveryDTO.getDeliveryID());
		}
	}


	
	
	public boolean updateSyncStatus(SQLiteDatabase dbObject, DTO dto,String tableName,String primaryKey,String primarykeyvalue) {
		try {
			String whereCls = primaryKey+"="+"'" + primarykeyvalue + "'";
			ContentValues cValues = new ContentValues();
			cValues.put("sync_status", 1);
			dbObject.update(tableName, cValues, whereCls, null);
			return true;
		} catch (SQLException e) {
			Log.e("Update syntax  -- update Debt", e.getMessage());
		} finally {
			dbObject.close();
		}
		return true;
	}
}
