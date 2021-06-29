package com.micaja.servipunto.utils;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.DishesDAO;
import com.micaja.servipunto.database.dao.OrderDetailsDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dao.SalesDetailDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
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
import com.micaja.servipunto.database.dto.LendDeliveryDTO;
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
import com.micaja.servipunto.database.dto.SincronizarTransaccionesDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.database.dto.SupplierPaymentsDTO;
import com.micaja.servipunto.database.dto.TransaccionBoxDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;

public class RequestFiels {
	SharedPreferences sharedpreferences;

	public RequestFiels(Context context) {
		sharedpreferences = context.getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.userName = sharedpreferences
				.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code",
				"");
	}

	public String addsupplier(List<DTO> supplierDetails) {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "supplier");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("supplier_code");
			columnarray.put("store_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < supplierDetails.size(); i++) {
				SupplierDTO clientdto = (SupplierDTO) supplierDetails.get(i);
				JSONObject jso_client_obj = new JSONObject();
				// jso_client_obj.put("supplier_id", clientdto.getSupplierId());
				jso_client_obj.put("supplier_code", clientdto.getCedula());
				jso_client_obj.put("name", clientdto.getName());
				try {
					jso_client_obj.put("logo", clientdto.getLogo());
					jso_client_obj.put("balance_amount",
							Double.parseDouble(clientdto.getBalanceAmount()));

				} catch (Exception e) {
				}
				jso_client_obj.put("address", clientdto.getAddress());
				jso_client_obj.put("telephone", clientdto.getTelephone());
				jso_client_obj.put("contact_name", clientdto.getContactName());
				jso_client_obj.put("contact_telephone",
						clientdto.getContactTelephone());
				jso_client_obj.put("visit_days", clientdto.getVisitDays());
				try {
					jso_client_obj.put("visit_frequency",
							clientdto.getVisitFrequency());
					jso_client_obj.put("last_payment_date", Dates
							.serverdateformate(clientdto.getLastPaymentDate()));
					jso_client_obj
							.put("created_date", Dates
									.serverdateformate(clientdto
											.getCreatedDate()));
					jso_client_obj.put("modified_date", Dates
							.serverdateformate(clientdto.getModifiedDate()));
				} catch (Exception e) {
				}
				jso_client_obj.put("created_by", ServiApplication.userName);
				jso_client_obj.put("modified_by", ServiApplication.userName);
				jso_client_obj.put("active_status",
						"" + clientdto.getActiveStatus());
				jso_client_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	public String getInventoryTableData(List<DTO> clientPayList) {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "inventory");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("barcode");
			columnarray.put("store_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < clientPayList.size(); i++) {
				InventoryDTO clientdto = (InventoryDTO) clientPayList.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("inventory_id",
						Long.parseLong(clientdto.getInventoryId()));
				jso_client_obj.put("barcode", clientdto.getProductId());
				jso_client_obj.put("quantity", clientdto.getQuantityBalance().toString());
				jso_client_obj.put("store_code", ServiApplication.store_id);
				jso_client_obj.put("uom", clientdto.getUom());
				jso_client_obj.put("modified_by", ServiApplication.userName);

				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}

			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	public String getMenuDishTableTableData(List<DTO> clientPayList) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "menu_dishes");
			jsonobj.put("type", "insert");
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < clientPayList.size(); i++) {
				MenuDishesDTO clientdto = (MenuDishesDTO) clientPayList.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("dish_id",
						Long.parseLong(clientdto.getDishId()));
				jso_client_obj.put("menu_dishes_id",
						Long.parseLong(clientdto.getMenuDishesId()));
				jso_client_obj.put("menu_id",
						Long.parseLong(clientdto.getMenuId()));
				jso_client_obj.put("store_code", ServiApplication.store_id);
				jso_client_obj.put("created_by", ServiApplication.userName);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	/* Dish synk */
	public String getDishTableTableData(List<DTO> clientPayList) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "dishes");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("dish_id");
			columnarray.put("store_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < clientPayList.size(); i++) {
				DishesDTO clientdto = (DishesDTO) clientPayList.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("dish_id",
						Long.parseLong(clientdto.getDishId()));
				jso_client_obj.put("dish_name", clientdto.getDishName());
				jso_client_obj.put("dish_price",
						Double.parseDouble(clientdto.getDishPrice()));
				jso_client_obj.put("no_of_items", clientdto.getNoOfItems());
				jso_client_obj.put("expiry_days", clientdto.getExpiryDays());
				try {
					jso_client_obj.put("vat", clientdto.getVat());
				} catch (Exception e) {
				}
				jso_client_obj.put("store_code", ServiApplication.store_id);
				jso_client_obj.put("selling_cost_per_item",
						Double.parseDouble(clientdto.getSellingCostperItem()));
				jso_client_obj.put("modified_by", ServiApplication.userName);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	/* ClientPayList synk */
	public String getClientPayListTableData(List<DTO> clientPayList) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "customer_payments");
			jsonobj.put("type", "insert");
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < clientPayList.size(); i++) {
				ClientPaymentsDTO clientdto = (ClientPaymentsDTO) clientPayList
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("customer_code",
						Long.parseLong(clientdto.getClientId()));
				jso_client_obj.put("payment_type",
						clientdto.getPaymentType());
				jso_client_obj.put("amount_paid",
						Double.parseDouble(clientdto.getAmountPaid().replace(",","")));
				jso_client_obj.put("date_time", clientdto.getDateTime());
				try {
					jso_client_obj.put("sale_id", clientdto.getSaleId().equals("")?"0":clientdto.getSaleId());
				} catch (Exception e) {
				}
				jso_client_obj.put("incoming_type", clientdto.getIncomeType());
				jso_client_obj.put("store_code", ServiApplication.store_id);
				jso_client_obj.put("created_by", ServiApplication.userName);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return jsonobj.toString();
		}
	}

	public String getDeliveryPayListTableData(List<DTO> deliveryPayList) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "dish_products");
			jsonobj.put("type", "insert");
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < deliveryPayList.size(); i++) {
				DeliveryPaymentsDTO deliverydto = (DeliveryPaymentsDTO) deliveryPayList
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("delivery_code",
						Long.parseLong(deliverydto.getDeliveryId()));
				jso_client_obj.put("payment_type",
						Double.parseDouble(deliverydto.getPaymentType()));
				jso_client_obj.put("amount_paid",
						Double.parseDouble(deliverydto.getAmountPaid()));
				jso_client_obj.put("date_time", deliverydto.getDateTime());
				try {
					jso_client_obj.put("sale_id", deliverydto.getSaleId());
				} catch (Exception e) {
				}
				jso_client_obj.put("incoming_type", deliverydto.getIncomeType());
				jso_client_obj.put("store_code", ServiApplication.store_id);
				jso_client_obj.put("created_by", ServiApplication.userName);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	/* Dish products synk */
	public String getDishProductsTableData(List<DTO> dishProductsList) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "dish_products");
			jsonobj.put("type", "insert");
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < dishProductsList.size(); i++) {
				DishProductsDTO clientdto = (DishProductsDTO) dishProductsList
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("dish_product_id",
						Long.parseLong(clientdto.getDishProductId()));
				jso_client_obj.put("dish_id",
						Double.parseDouble(clientdto.getDishId()));
				jso_client_obj.put("barcode", clientdto.getProductId());
				jso_client_obj.put("uom", clientdto.getUom());
				jso_client_obj.put("quantity", clientdto.getQuantity());
				jso_client_obj.put("store_code", ServiApplication.store_id);
				jso_client_obj.put("created_by", ServiApplication.userName);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	/* Cash Flow synk */
	public String getCashFlowTableData(List<DTO> cashFlowList) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "cash_flow");
			jsonobj.put("type", "insert");
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < cashFlowList.size(); i++) {
				CashFlowDTO clientdto = (CashFlowDTO) cashFlowList.get(i);
				JSONObject jso_client_obj = new JSONObject();
				// jso_client_obj.put("cash_flow_id",
				// clientdto.getCashFlowId());
				jso_client_obj.put("cash_flow_id", clientdto.getCashFlowId());
				jso_client_obj.put("amount",
						Double.parseDouble(clientdto.getAmount()));
				jso_client_obj.put("amount_type", clientdto.getAmountType());
				jso_client_obj.put("reason", clientdto.getReason());
				try {
					jso_client_obj.put("datetime",
							Dates.serverdateformate(clientdto.getDateTime()));
				} catch (Exception e) {

				}
				jso_client_obj.put("created_by", ServiApplication.userName);
				jso_client_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	/* client table synk */
	public String getClientTableData(List<DTO> clientList) {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "customer");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("customer_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < clientList.size(); i++) {
				ClientDTO clientdto = (ClientDTO) clientList.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("customer_code", clientdto.getCedula());
				jso_client_obj.put("name", clientdto.getName());
				jso_client_obj.put("telephone", clientdto.getTelephone());
				jso_client_obj.put("address", clientdto.getAddress());
				jso_client_obj.put("email", clientdto.getEmail());
				jso_client_obj.put("gender", clientdto.getGender());
				jso_client_obj.put("active_status",
						"" + clientdto.getActiveStatus());
				jso_client_obj.put("balance_amount",
						Double.parseDouble(clientdto.getBalanceAmount()));
				try {
					jso_client_obj
							.put("created_date", Dates
									.serverdateformate(clientdto
											.getCreatedDate()));
				} catch (Exception e) {
				}
				jso_client_obj.put("modified_by", ServiApplication.userName);
				try {
					jso_client_obj.put("last_payment_date", Dates
							.serverdateformate(clientdto.getLastPaymentDate()));
				} catch (Exception e) {

				}
				try {
					jso_client_obj.put("modified_date", Dates
							.serverdateformate(clientdto.getModifiedDate()));
				} catch (Exception e) {
				}
				jso_client_obj.put("store_code", ServiApplication.store_id);
				jso_client_obj.put("initial_debts",
						Double.parseDouble(clientdto.getInitialDebt()));
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	public String getDeliveryTableData(List<DTO> delivery) {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "delivery");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("delivery_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < delivery.size(); i++) {
				DeliveryDTO deliveryDTO = (DeliveryDTO) delivery.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("delivery_code", deliveryDTO.getCedula());
				jso_client_obj.put("name", deliveryDTO.getName());
				jso_client_obj.put("telephone", deliveryDTO.getTelephone());
				jso_client_obj.put("address", deliveryDTO.getAddress());
				jso_client_obj.put("email", deliveryDTO.getEmail());
				jso_client_obj.put("gender", deliveryDTO.getGender());
				jso_client_obj.put("active_status",
						"" + deliveryDTO.getActiveStatus());
				jso_client_obj.put("balance_amount",
						Double.parseDouble(deliveryDTO.getBalanceAmount()));
				try {
					jso_client_obj
							.put("created_date", Dates
									.serverdateformate(deliveryDTO
											.getCreatedDate()));
				} catch (Exception e) {
				}
				jso_client_obj.put("modified_by", ServiApplication.userName);
				try {
					jso_client_obj.put("last_payment_date", Dates
							.serverdateformate(deliveryDTO.getLastPaymentDate()));
				} catch (Exception e) {

				}
				try {
					jso_client_obj.put("modified_date", Dates
							.serverdateformate(deliveryDTO.getModifiedDate()));
				} catch (Exception e) {
				}
				jso_client_obj.put("store_code", ServiApplication.store_id);
				jso_client_obj.put("initial_debts",
						Double.parseDouble(deliveryDTO.getInitialDebt()));
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	public String getInventoryAdjTableData(List<DTO> clientList) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "inventory_adjustment");
			jsonobj.put("type", "insert");
			// JSONArray columnarray = new JSONArray();
			// columnarray.put("adjustment_id");
			// jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < clientList.size(); i++) {
				InventoryAdjustmentDTO clientdto = (InventoryAdjustmentDTO) clientList
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj
						.put("adjustment_id", clientdto.getAdjustmentId());
				jso_client_obj.put("barcode", clientdto.getProductId());
				jso_client_obj.put("damage_type_id",
						Long.parseLong(clientdto.getDamageTypeId()));
				jso_client_obj.put("quantity", clientdto.getQuantity());
				jso_client_obj.put("uom", clientdto.getUom());
				jso_client_obj.put("created_by", ServiApplication.userName);
				jso_client_obj.put("store_code", ServiApplication.store_id);

				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	public String getInventoryHistoryTableData(List<DTO> clientList) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "inventory_history");
			jsonobj.put("type", "insert");

			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < clientList.size(); i++) {
				InventoryHistoryDTO clientdto = (InventoryHistoryDTO) clientList
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("barcode", clientdto.getProductId());
				jso_client_obj.put("quantity", clientdto.getQuantity());
				try {
					jso_client_obj.put("date_time",Dates.serverdateformate(clientdto.getDateTime()));
				} catch (Exception e) {

				}
				jso_client_obj.put("created_by", ServiApplication.userName);

				jso_client_obj.put("invoice_number", clientdto.getInvoiceNum());

				jso_client_obj.put("uom", clientdto.getUom());
				jso_client_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	public String getLendMoneyTableData(List<DTO> lendmoney) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "lend_money");
			jsonobj.put("type", "insert");

			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < lendmoney.size(); i++) {
				LendmoneyDTO clientdto = (LendmoneyDTO) lendmoney.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("customer_code", clientdto.getClientId());
				jso_client_obj.put("amount",
						Double.parseDouble(clientdto.getAmount()));
				jso_client_obj.put("date_time",
						Dates.serverdateformate(clientdto.getDateTime()));
				jso_client_obj.put("created_by", ServiApplication.userName);
				jso_client_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	public String getLendDeliveryTableData(List<DTO> lenddelivery) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "lend_delivery");
			jsonobj.put("type", "insert");

			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < lenddelivery.size(); i++) {
				LendDeliveryDTO deliverydto = (LendDeliveryDTO) lenddelivery.get(i);
				JSONObject jso_delivery_obj = new JSONObject();
				jso_delivery_obj.put("delivery_code", deliverydto.getDeliveryId());
				jso_delivery_obj.put("amount",
						Double.parseDouble(deliverydto.getAmount()));
				jso_delivery_obj.put("date_time",
						Dates.serverdateformate(deliverydto.getDateTime()));
				jso_delivery_obj.put("created_by", ServiApplication.userName);
				jso_delivery_obj.put("store_code", ServiApplication.store_id);

				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_delivery_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	public String getMenusTableData(List<DTO> menus) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "menu");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("menu_id");
			columnarray.put("store_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < menus.size(); i++) {
				MenuDTO clientdto = (MenuDTO) menus.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("menu_id",
						Long.parseLong(clientdto.getMenuId()));
				jso_client_obj.put("name", clientdto.getName());
				jso_client_obj.put("menu_type_id",
						Long.parseLong(clientdto.getMenuTypeId()));
				jso_client_obj.put("store_code", ServiApplication.store_id);
				jso_client_obj.put("modified_by", ServiApplication.userName);
				jso_client_obj.put("week_days", clientdto.getWeekDays());
				try {
					jso_client_obj.put("start_date",
							Dates.serverdateformate(clientdto.getStartDate()));
					jso_client_obj.put("end_date",
							Dates.serverdateformate(clientdto.getEndDate()));
				} catch (Exception e) {

				}
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	public String getMenusInventoryTableData(List<DTO> menuInventorylist) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "menu_inventory");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("dish_id");
			columnarray.put("store_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < menuInventorylist.size(); i++) {
				MenuInventoryDTO clientdto = (MenuInventoryDTO) menuInventorylist
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("menu_inventory_id",
						Long.parseLong(clientdto.getMenuInventoryId()));
				jso_client_obj.put("dish_id",
						Long.parseLong(clientdto.getDishId()));
				jso_client_obj.put("count", clientdto.getCount());
				jso_client_obj.put("modified_by", ServiApplication.userName);
				jso_client_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	public String getMenusInventoryAdjTableData(List<DTO> menuInventoryAdjlist) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "menu_inventory_adjustment");
			jsonobj.put("type", "insert");
			// JSONArray columnarray = new JSONArray();
			// columnarray.put("adjustment_id");
			// jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < menuInventoryAdjlist.size(); i++) {
				MenuInventoryAdjustmentDTO clientdto = (MenuInventoryAdjustmentDTO) menuInventoryAdjlist
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("menu_adjustment_id",
						Long.parseLong(clientdto.getMenuAdjustmentId()));
				jso_client_obj.put("dish_id",
						Long.parseLong(clientdto.getDishId()));
				jso_client_obj.put("created_by", ServiApplication.userName);
				jso_client_obj.put("count", clientdto.getCount());
				jso_client_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	public String getOrderDetailsTableData(List<DTO> orderdetails) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "order_details");
			jsonobj.put("type", "insert");
			// JSONArray columnarray = new JSONArray();
			// columnarray.put("adjustment_id");
			// jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < orderdetails.size(); i++) {
				OrderDetailsDTO clientdto = (OrderDetailsDTO) orderdetails
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("order_id",
						Long.parseLong(clientdto.getOrderId()));
				jso_client_obj.put("barcode", clientdto.getProductId());
				jso_client_obj.put("count", clientdto.getCount());
				jso_client_obj.put("uom", clientdto.getUom());
				jso_client_obj.put("price",
						Double.parseDouble(clientdto.getPrice()));
				jso_client_obj.put("created_by", ServiApplication.userName);
				jso_client_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	@SuppressWarnings("static-access")
	public String getOrdersTableData(List<DTO> orders) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "orders");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("order_id");
			columnarray.put("store_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < orders.size(); i++) {
				OrdersDTO clientdto = (OrdersDTO) orders.get(i);
				JSONObject order = new JSONObject();

				JSONObject jso_client_obj = new JSONObject();

				jso_client_obj.put("order_id",
						Long.parseLong(clientdto.getOrderId()));
				jso_client_obj.put("supplier_code", clientdto.getSupplierId());
				jso_client_obj.put("invoice_no", clientdto.getInvoiceNo());

				jso_client_obj.put("gross_amount",
						Double.parseDouble(clientdto.getGrossAmount()));
				jso_client_obj.put("net_amount",
						Double.parseDouble(clientdto.getNetAmount()));
				try {
					jso_client_obj.put("discount", clientdto.getDiscount());
				} catch (Exception e) {
					jso_client_obj.put("discount", "0");
				}
				try {
					jso_client_obj.put("fecha_inicial", new Dates()
							.serverdateformate(clientdto.getFecha_inicial()));
				} catch (Exception e) {
					jso_client_obj.put("fecha_inicial", new Dates()
							.serverdateformate(Dates
									.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
				}
				try {
					jso_client_obj.put("fecha_final", new Dates()
							.serverdateformate(clientdto.getFecha_final()));
				} catch (Exception e) {
					jso_client_obj.put("fecha_final", new Dates()
							.serverdateformate(Dates
									.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
				}
				jso_client_obj.put("payment_type", clientdto.getPaymentType());
				jso_client_obj.put("is_order_confirmed",
						"" + clientdto.getIsOrderConfirmed());
				jso_client_obj.put("date_time",
						Dates.datetostring(clientdto.getDateTime()));
				jso_client_obj.put("modified_by", ServiApplication.userName);
				jso_client_obj.put("store_code", ServiApplication.store_id);

				order.put("orders", jso_client_obj);
				order.put("order_details",
						getJsonOrderDetailsArray(clientdto.getOrderId()));

				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	private JSONArray getJsonOrderDetailsArray(String orderId) {
		JSONArray jsonarray = new JSONArray();
		List<DTO> orderdetails = OrderDetailsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");
		try {
			for (DTO dto : orderdetails) {
				OrderDetailsDTO orderDetailsDto = (OrderDetailsDTO) dto;
				if (orderDetailsDto.getOrderId().equals(orderId)) {
					JSONObject jso_client_obj = new JSONObject();
					jso_client_obj.put("order_id",
							Long.parseLong(orderDetailsDto.getOrderId()));
					jso_client_obj.put("barcode",
							orderDetailsDto.getProductId());
					jso_client_obj.put("count", orderDetailsDto.getCount());
					jso_client_obj.put("uom", orderDetailsDto.getUom());
					jso_client_obj.put("price",
							Double.parseDouble(orderDetailsDto.getPrice()));
					jso_client_obj.put("created_by", ServiApplication.userName);
					jso_client_obj.put("store_code", ServiApplication.store_id);
					if (ServiApplication.store_id.length() >= 1
							&& ServiApplication.userName.length() >= 1) {
						jsonarray.put(jso_client_obj);
					}
				}
			}
		} catch (Exception e) {
		}
		return jsonarray;
	}

	@SuppressWarnings("static-access")
	public String getSalesData(List<DTO> sales) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "sales");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("sale_id");
			columnarray.put("store_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < sales.size(); i++) {
				SalesDTO clientdto = (SalesDTO) sales.get(i);

				JSONObject sales1 = new JSONObject();

				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("sale_id", clientdto.getSaleID());
				jso_client_obj.put("invoice_number",
						clientdto.getInvoiceNumber());
				jso_client_obj.put("gross_amount",
						Double.parseDouble(clientdto.getGrossAmount()));
				jso_client_obj.put("net_amount",
						Double.parseDouble(clientdto.getNetAmount()));
				jso_client_obj.put("discount", clientdto.getDiscount());
				try {
					jso_client_obj
							.put("customer_code", clientdto.getClientId());
				} catch (Exception e) {

				}

				try {
					jso_client_obj
							.put("delivery_code", clientdto.getDelivery_code());
				} catch (Exception e) {

				}
				jso_client_obj.put("trans_delivery" , clientdto.getTrans_delivery());

				try {
					jso_client_obj.put("fecha_inicial", new Dates()
							.serverdateformate(clientdto.getFecha_inicial()));
				} catch (Exception e) {
					jso_client_obj.put("fecha_inicial", new Dates()
							.serverdateformate(Dates
									.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
				}
				try {
					jso_client_obj.put("fecha_final", new Dates()
							.serverdateformate(clientdto.getFecha_final()));
				} catch (Exception e) {
					jso_client_obj.put("fecha_final", new Dates()
							.serverdateformate(Dates
									.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
				}
				jso_client_obj.put("payment_type", clientdto.getPaymentType());
				jso_client_obj.put("modified_by", ServiApplication.userName);
				jso_client_obj.put("amount_paid",
						Double.parseDouble(clientdto.getAmountPaid()));
				try {
					jso_client_obj.put("date_time",
							Dates.datetostring(clientdto.getDateTime()));
				} catch (Exception e) {

				}
				jso_client_obj.put("store_code", ServiApplication.store_id);

				sales1.put("sales", jso_client_obj);


				sales1.put("sales_details","");
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(sales1);
				}

			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	private JSONArray getSales_details(String saleID) {
		JSONArray jsonarray = new JSONArray();
		List<DTO> salesDetails = SalesDetailDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");
		try {
			for (DTO dto : salesDetails) {
				SalesDetailDTO clientdto = (SalesDetailDTO) dto;
				if (clientdto.getSaleId().equals(saleID)) {
					JSONObject jso_client_obj = new JSONObject();
					CommonMethods.getpurchaseprice(clientdto);
					jso_client_obj.put("sale_id", clientdto.getSaleId());
					try {
						jso_client_obj.put("barcode", clientdto.getProductId());
						ProductDTO pdto=ProductDAO.getInstance().getRecordsByProductID(DBHandler.getDBObj(Constants.READABLE), clientdto.getProductId());
						jso_client_obj.put("purchase_price",pdto.getPurchasePrice());
					} catch (Exception e) {
					}
					jso_client_obj.put("count", clientdto.getCount());
					jso_client_obj.put("price",
							Double.parseDouble(clientdto.getPrice()));
					jso_client_obj.put("uom", clientdto.getUom());
					try {
						jso_client_obj.put("dish_id",
								Long.parseLong(clientdto.getDishId()));
						DishesDTO ddto=DishesDAO.getInstance().getRecordsByDish_id(DBHandler.getDBObj(Constants.READABLE), clientdto.getDishId());
						jso_client_obj.put("purchase_price",ddto.getDishPrice());
					} catch (Exception e) {

					}
					jso_client_obj.put("store_code", ServiApplication.store_id);
					jso_client_obj.put("created_by", ServiApplication.userName);
					if (ServiApplication.store_id.length() >= 1
							&& ServiApplication.userName.length() >= 1) {
						jsonarray.put(jso_client_obj);
					}
				}
			}
		} catch (Exception e) {
		}
		return jsonarray;
	}

	public String getSalesDetailsData(List<DTO> salesDetails) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "sales_details");
			jsonobj.put("type", "insert");
			// JSONArray columnarray = new JSONArray();
			// columnarray.put("adjustment_id");
			// jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < salesDetails.size(); i++) {
				SalesDetailDTO clientdto = (SalesDetailDTO) salesDetails.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("sale_id", clientdto.getSaleId());
				jso_client_obj.put("barcode", clientdto.getProductId());
				jso_client_obj.put("count", clientdto.getCount());
				jso_client_obj.put("price",
						Double.parseDouble(clientdto.getPrice()));

				try {
					jso_client_obj
							.put("purchase_price",
									ProductDAO
											.getInstance()
											.getRecordsByBarcode(
													DBHandler
															.getDBObj(Constants.READABLE),
													clientdto.getProductId())
											.getPurchasePrice());
				} catch (Exception e) {
				}
				jso_client_obj.put("uom", clientdto.getUom());
				try {
					jso_client_obj.put("dish_id",
							Long.parseLong(clientdto.getDishId()));
					jso_client_obj
							.put("purchase_price",
									DishesDAO
											.getInstance()
											.getRecordsByDish_id(
													DBHandler
															.getDBObj(Constants.READABLE),
													clientdto.getDishId())
											.getDishPrice());

				} catch (Exception e) {
				}
				jso_client_obj.put("store_code", ServiApplication.store_id);
				jso_client_obj.put("created_by", ServiApplication.userName);

				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}

			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	public String getPaymentDetailsData(List<DTO> paymentDetails) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "customer_payments");
			jsonobj.put("type", "insert");
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < paymentDetails.size(); i++) {
				ClientPaymentsDTO clientdto = (ClientPaymentsDTO) paymentDetails
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("customer_code", clientdto.getClientId());
				jso_client_obj.put("payment_type", clientdto.getPaymentType());
				jso_client_obj.put("amount_paid",
						Double.parseDouble(clientdto.getAmountPaid()));
				jso_client_obj.put("created_by", ServiApplication.userName);
				try {
					jso_client_obj.put("date_time",
							Dates.serverdateformate(clientdto.getDateTime()));
					if (clientdto.getSaleId().length()>0) {
						jso_client_obj.put("sale_id", clientdto.getSaleId());
					}else{
						jso_client_obj.put("sale_id", "0");
					}
					
					
				} catch (Exception e) {
				}
				jso_client_obj.put("incoming_type", clientdto.getIncomeType());
				jso_client_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	public String getPaymentDeliveryDetailsData(List<DTO> paymentDetails) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "delivery_payments");
			jsonobj.put("type", "insert");
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < paymentDetails.size(); i++) {
				DeliveryPaymentsDTO deliverydto = (DeliveryPaymentsDTO) paymentDetails
						.get(i);
				JSONObject jso_delivery_obj = new JSONObject();
				jso_delivery_obj.put("delivery_code", deliverydto.getDeliveryId());
				jso_delivery_obj.put("payment_type", deliverydto.getPaymentType());
				jso_delivery_obj.put("amount_paid",
						Double.parseDouble(deliverydto.getAmountPaid()));
				jso_delivery_obj.put("created_by", ServiApplication.userName);
				try {
					jso_delivery_obj.put("date_time",
							Dates.serverdateformate(deliverydto.getDateTime()));
					if (deliverydto.getSaleId().length()>0) {
						jso_delivery_obj.put("invoice_no", deliverydto.getSaleId());
					}else{
						jso_delivery_obj.put("invoice_no", "0");
					}


				} catch (Exception e) {
				}
				jso_delivery_obj.put("incoming_type", deliverydto.getIncomeType());
				jso_delivery_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_delivery_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	public String getSuppliersDetailsData(List<DTO> supplierDetails) {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "supplier");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("supplier_code");
			columnarray.put("store_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < supplierDetails.size(); i++) {
				SupplierDTO clientdto = (SupplierDTO) supplierDetails.get(i);
				JSONObject jso_client_obj = new JSONObject();
				// jso_client_obj.put("supplier_id", clientdto.getSupplierId());
				jso_client_obj.put("supplier_code", clientdto.getCedula());
				jso_client_obj.put("name", clientdto.getName());
				try {
					jso_client_obj.put("logo", clientdto.getLogo());
					jso_client_obj.put("balance_amount",
							Double.parseDouble(clientdto.getBalanceAmount()));

				} catch (Exception e) {
				}
				jso_client_obj.put("address", clientdto.getAddress());
				jso_client_obj.put("telephone", clientdto.getTelephone());
				jso_client_obj.put("contact_name", clientdto.getContactName());
				jso_client_obj.put("contact_telephone",
						clientdto.getContactTelephone());
				jso_client_obj.put("visit_days", clientdto.getVisitDays());
				try {
					jso_client_obj.put("visit_frequency",
							clientdto.getVisitFrequency());
				} catch (Exception e) {
				}
				try {
					jso_client_obj
							.put("created_date", Dates
									.serverdateformate(clientdto
											.getCreatedDate()));
				} catch (Exception e) {

				}

				try {
					jso_client_obj.put("modified_date", Dates
							.serverdateformate(clientdto.getModifiedDate()));
				} catch (Exception e) {

				}

				try {
					jso_client_obj.put("last_payment_date", Dates
							.serverdateformate(clientdto.getLastPaymentDate()));
				} catch (Exception e) {

				}

				jso_client_obj.put("modified_by", ServiApplication.userName);
				jso_client_obj.put("active_status",
						"" + clientdto.getActiveStatus());
				jso_client_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	public String getUserTableData(List<DTO> cashFlowList) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "user");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("username");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < cashFlowList.size(); i++) {
				UserDetailsDTO clientdto = (UserDetailsDTO) cashFlowList.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("name", clientdto.getName());
				jso_client_obj.put("username", clientdto.getUserName());
				try {
					jso_client_obj.put("opening_date_time", Dates
							.serverdateformate(clientdto.getOpeningDateTime()));
				} catch (Exception e) {

				}
				try {
					jso_client_obj.put("closing_time", Dates
							.serverdateformate(clientdto.getCloseDateTime()));
				} catch (Exception e) {
					// TODO: handle exception
				}

				jso_client_obj.put("last_login", clientdto.getLastLogin());
				try {
					jso_client_obj.put("opening_balance",
							Double.parseDouble(clientdto.getOpeningBalance()));
					jso_client_obj.put("actual_balance",
							Double.parseDouble(clientdto.getActualBalance()));
				} catch (Exception e) {

				}
				jso_client_obj.put("isclosed", "1");
				jso_client_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	public String getMoveToMenuData(List<DTO> userDetails) {
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "catalog_data_sync");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("username");
			columnarray.put("last_sync_date");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < userDetails.size(); i++) {
				UserDetailsDTO userdto = (UserDetailsDTO) userDetails.get(i);
				JSONObject jso_user_obj = new JSONObject();
				jso_user_obj.put("username", userdto.getUserName());
				jso_user_obj.put("sync_status", "1");
				jso_user_obj.put("store_code", ServiApplication.store_id);
				jso_user_obj.put("last_sync_date", Dates.currentdate());
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_user_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonobj.toString();
	}

	public String getSupplierPayTableData(List<DTO> supplierPaymentsDetails) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "supplier_payments");
			jsonobj.put("type", "insert");
			// JSONArray columnarray = new JSONArray();
			// columnarray.put("adjustment_id");
			// jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < supplierPaymentsDetails.size(); i++) {
				SupplierPaymentsDTO clientdto = (SupplierPaymentsDTO) supplierPaymentsDetails
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("supplier_code", clientdto.getSupplierId());
				jso_client_obj.put("payment_type", clientdto.getPaymentType());
				jso_client_obj.put("created_by", ServiApplication.userName);
				jso_client_obj.put("amount_paid",
						Double.parseDouble(clientdto.getAmountPaid()));
				try {
					jso_client_obj.put("date_time",
							Dates.serverdateformate(clientdto.getDateTime()));
					jso_client_obj.put("purchase_type",
							clientdto.getPurchaseType());
				} catch (Exception e) {
				}
				jso_client_obj.put("store_code", ServiApplication.store_id);
				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}

			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	@SuppressWarnings("static-access")
	public String getProductTableData(List<DTO> productDetails) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "product");
			jsonobj.put("type", "update");
			JSONArray columnarray = new JSONArray();
			columnarray.put("barcode");
			columnarray.put("store_code");
			jsonobj.put("columns", columnarray);
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < productDetails.size(); i++) {
				ProductDTO clientdto = (ProductDTO) productDetails.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("barcode", clientdto.getBarcode());
				jso_client_obj.put("name", clientdto.getName());
				jso_client_obj.put("quantity", clientdto.getQuantity());
				jso_client_obj.put("uom", clientdto.getUom());
				jso_client_obj.put("purchase_price",
						Double.parseDouble(clientdto.getPurchasePrice()));
				jso_client_obj.put("selling_price",
						Double.parseDouble(clientdto.getSellingPrice()));
				jso_client_obj.put("group_id", clientdto.getGroupId());
				jso_client_obj.put("supplier_code", clientdto.getSupplierId());
				jso_client_obj.put("line_id", clientdto.getLineId());
				jso_client_obj.put("modified_by", ServiApplication.userName);
				jso_client_obj.put("active_status",
						"" + clientdto.getActiveStatus());
				jso_client_obj.put("active_status",
						"" + clientdto.getActiveStatus());
				try {
					jso_client_obj
							.put("expiry_date", Dates
									.serverdateformate(clientdto
											.getExpiry_date()));
				} catch (Exception e) {

				}
				try {
					jso_client_obj.put("create_date",
							Dates.serverdateformate(clientdto.getCreateDate()));
					jso_client_obj.put("vat", clientdto.getVat());

				} catch (Exception e) {
				}
				try {
					jso_client_obj.put("modified_date", Dates
							.serverdateformate(clientdto.getModifiedDate()));
				} catch (Exception e) {

				}
				try {
					jso_client_obj.put("discount",
							Integer.parseInt(clientdto.getDiscount()));
				} catch (Exception e) {
					jso_client_obj.put("discount", 0);
				}
				// jso_client_obj.put("desc",""+clientdto.getDe);
				jso_client_obj.put("min_count_inventory",
						clientdto.getMin_count_inventory());
				jso_client_obj.put("store_code", ServiApplication.store_id);
				jso_client_obj.put("sync_status", clientdto.getSyncStatus());
				try {
					jso_client_obj.put("fecha_inicial", new Dates()
							.serverdateformate(clientdto.getFecha_inicial()));
				} catch (Exception e) {
					jso_client_obj.put("fecha_inicial", new Dates()
							.serverdateformate(Dates
									.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
				}
				try {
					jso_client_obj.put("fecha_final", new Dates()
							.serverdateformate(clientdto.getFecha_final()));
				} catch (Exception e) {
					jso_client_obj.put("fecha_final", new Dates()
							.serverdateformate(Dates
									.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
				}

				if (ServiApplication.store_id.length() >= 1
						&& ServiApplication.userName.length() >= 1) {
					jsonRecordsArray.put(jso_client_obj);
				}
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	public String getSincronizar(List<DTO> punthored_soncronizar) {
		UserDetailsDTO dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "puntored_transaction");
			jsonobj.put("type", "insert");
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < punthored_soncronizar.size(); i++) {
				SincronizarTransaccionesDTO sdto = (SincronizarTransaccionesDTO) punthored_soncronizar
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("rowid", Long.parseLong(sdto.getRowid()));
				jso_client_obj.put("module", sdto.getModule());
				jso_client_obj.put("tipo_transaction",
						sdto.getTipo_transaction());
				jso_client_obj.put("authorization_number",
						sdto.getAuthorization_number());

				jso_client_obj.put("id_commercio", dto.getComercioId());
				jso_client_obj.put("id_pdv", dto.getPuntoredId());
				jso_client_obj.put("id_terminal", dto.getTerminalId());
				jso_client_obj.put("id_pdb_servitienda",
						sdto.getId_pdb_servitienda());

				jso_client_obj
						.put("transaction_datetime", Dates
								.serverdateformate(sdto
										.getTransaction_datetime()));
				jso_client_obj.put("transaction_value",
						Double.parseDouble(sdto.getTransaction_value()));
				jso_client_obj.put("status", sdto.getStatus());
				jso_client_obj.put("creation_date",
						Dates.serverdateformate(sdto.getCreation_date()));

				jso_client_obj.put("created_by", sdto.getCreated_by());
				jso_client_obj.put("modified_date",
						Dates.serverdateformate(sdto.getModified_date()));
				jso_client_obj.put("modified_by", sdto.getModified_by());
				jsonRecordsArray.put(jso_client_obj);
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	public String TransaccionBox(List<DTO> TransaccionBoxDetails) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("name", "transaction_box");
			jsonobj.put("type", "insert");
			JSONArray jsonRecordsArray = new JSONArray();
			for (int i = 0; i < TransaccionBoxDetails.size(); i++) {
				TransaccionBoxDTO clientdto = (TransaccionBoxDTO) TransaccionBoxDetails
						.get(i);
				JSONObject jso_client_obj = new JSONObject();
				jso_client_obj.put("store_code", clientdto.getStore_code());
				jso_client_obj.put("username", clientdto.getUsername());
				jso_client_obj.put("tipo_transction",
						clientdto.getTipo_transction());
				jso_client_obj.put("module_name", clientdto.getModule_name());
				jso_client_obj.put("transaction_type",
						clientdto.getTransaction_type());
				jso_client_obj.put("amount", clientdto.getAmount().replace(",",""));
				try {
					jso_client_obj.put("datetime",
							Dates.serverdateformate(clientdto.getDatetime()));
				} catch (Exception e) {
				}
				jsonRecordsArray.put(jso_client_obj);
			}
			jsonobj.put("records", jsonRecordsArray);
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}

	public String PayInvoice(String invoice_no, String store_code, double amountpay ) {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("store_code", store_code);
			jsonobj.put("amount_paid", amountpay);
			jsonobj.put("invoice_no",invoice_no );
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}

	}
}
