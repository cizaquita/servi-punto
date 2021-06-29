package com.micaja.servipunto.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;

import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.CashFlowDAO;
import com.micaja.servipunto.database.dao.ClientDAO;
import com.micaja.servipunto.database.dao.ClientPaymentDAO;
import com.micaja.servipunto.database.dao.DeliveryDAO;
import com.micaja.servipunto.database.dao.DeliveryPaymentDAO;
import com.micaja.servipunto.database.dao.DishProductsDAO;
import com.micaja.servipunto.database.dao.DishesDAO;
import com.micaja.servipunto.database.dao.InventoryAdjustmentDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.InventoryHistoryDAO;
import com.micaja.servipunto.database.dao.LendDeliveryDAO;
import com.micaja.servipunto.database.dao.LendMoneyDAO;
import com.micaja.servipunto.database.dao.MenuDAO;
import com.micaja.servipunto.database.dao.MenuDishesDAO;
import com.micaja.servipunto.database.dao.MenuInventoryAdjustmentDAO;
import com.micaja.servipunto.database.dao.MenuInventoryDAO;
import com.micaja.servipunto.database.dao.OrderDetailsDAO;
import com.micaja.servipunto.database.dao.OrdersDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dao.SalesDAO;
import com.micaja.servipunto.database.dao.SalesDetailDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dao.SupplierPaymentsDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DeliveryPaymentsDTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;
import com.micaja.servipunto.utils.UpdateSynkStatus;

public class AutoSyncService extends Service {
	final Handler handler = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		callAutoSyncMethod();
	}

	private void callAutoSyncMethod() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(ServiApplication.AutoSyncToBlink);
				} catch (Exception e) {
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							callAutoSyncMethodService();
						} catch (Exception e) {

						}
					}
				});
				callAutoSyncMethod();
			}
		}).start();

	}

	public void callAutoSyncMethodService() {
		if (NetworkConnectivity.netWorkAvailability(AutoSyncService.this)) {
			new AutoSyncServer().execute();
		} else {
		}
	}

	class AutoSyncServer extends AsyncTask<Void, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(AutoSyncService.this);
		List<DTO> clientList = ClientDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");


		List<DTO> deliveryList = DeliveryDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		List<DTO> cashFlowList = CashFlowDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");
		List<DTO> dishProductsList = DishProductsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");
		List<DTO> clientPayList = ClientPaymentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> deliveryPayList = DeliveryPaymentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");
		List<DTO> dishlist = DishesDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");
		List<DTO> menudishlist = MenuDishesDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> inventorylist = InventoryDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> inventoryAdjlist = InventoryAdjustmentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> inventoryHistorylist = InventoryHistoryDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> lendmoney = LendMoneyDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		List<DTO> deliveryLendMoney = LendDeliveryDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		List<DTO> menus = MenuDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		List<DTO> menuInventorylist = MenuInventoryDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> menuInventoryAdjlist = MenuInventoryAdjustmentDAO
				.getInstance().getRecordsWithValues(
						DBHandler.getDBObj(Constants.READABLE), "sync_status",
						"0");

		List<DTO> orderdetails = OrderDetailsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> orders = OrdersDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		List<DTO> sales = SalesDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		List<DTO> salesDetails = SalesDetailDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> clientpaymentDetails = ClientPaymentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> deliverypaymentDetails = DeliveryPaymentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> supplierDetails = SupplierDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> userDetailDetails = UserDetailsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> supplierPaymentsDetails = SupplierPaymentsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		List<DTO> productDetails = ProductDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
						"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			if (productDetails.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_productDetails = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getProductTableData(productDetails));

					if (new JSONStatus().status(val_productDetails) == 0) {
						new UpdateSynkStatus(productDetails,
								ServiApplication.updateProducts);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (supplierPaymentsDetails.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_supplierpayments = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(AutoSyncService.this)
											.getSupplierPayTableData(supplierPaymentsDetails));

					if (new JSONStatus().status(val_supplierpayments) == 0) {
						new UpdateSynkStatus(supplierPaymentsDetails,
								ServiApplication.supplierpayments);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}

			}
			if (userDetailDetails.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_userDetailDetails = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(AutoSyncService.this)
											.getUserTableData(userDetailDetails));
					if (new JSONStatus().status(val_userDetailDetails) == 0) {
						new UpdateSynkStatus(userDetailDetails,
								ServiApplication.userTable);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (supplierDetails.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_supplierDetails = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(AutoSyncService.this)
											.getSuppliersDetailsData(supplierDetails));

					if (new JSONStatus().status(val_supplierDetails) == 0) {
						new UpdateSynkStatus(supplierDetails,
								ServiApplication.supplier);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}

			}
			if (clientpaymentDetails.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_clientpaymentDetails = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(AutoSyncService.this)
											.getPaymentDetailsData(clientpaymentDetails));
					if (new JSONStatus().status(val_clientpaymentDetails) == 0) {
						new UpdateSynkStatus(clientpaymentDetails,
								ServiApplication.clientpayments);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (deliverypaymentDetails.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_deliverypaymentDetails = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(AutoSyncService.this)
											.getPaymentDeliveryDetailsData(deliverypaymentDetails));
					if (new JSONStatus().status(val_deliverypaymentDetails) == 0) {
						new UpdateSynkStatus(deliverypaymentDetails,
								ServiApplication.deliveryPayments);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}
			if (salesDetails.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_salesDetails = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getSalesDetailsData(salesDetails));
					if (new JSONStatus().status(val_salesDetails) == 0) {
						new UpdateSynkStatus(salesDetails,
								ServiApplication.salesDetails);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (sales.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_sales = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this).getSalesData(sales));
					if (new JSONStatus().status(val_sales) == 0) {
						new UpdateSynkStatus(sales, ServiApplication.sales);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}
			if (orders.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_orders = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getOrdersTableData(orders));
					if (new JSONStatus().status(val_orders) == 0) {
						new UpdateSynkStatus(orders, ServiApplication.orders);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (orderdetails.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_orderdetails = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getOrderDetailsTableData(orderdetails));
					if (new JSONStatus().status(val_orderdetails) == 0) {
						new UpdateSynkStatus(orderdetails,
								ServiApplication.orderDetails);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (menuInventoryAdjlist.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_menuInventoryAdjlist = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(AutoSyncService.this)
											.getMenusInventoryAdjTableData(menuInventoryAdjlist));
					if (new JSONStatus().status(val_menuInventoryAdjlist) == 0) {
						new UpdateSynkStatus(menuInventoryAdjlist,
								ServiApplication.menuInventoryAdj);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (menuInventorylist.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_menuInventorylist = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(AutoSyncService.this)
											.getMenusInventoryTableData(menuInventorylist));
					if (new JSONStatus().status(val_menuInventorylist) == 0) {
						new UpdateSynkStatus(menuInventorylist,
								ServiApplication.menuInventory);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}
			if (menus.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_menus = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getMenusTableData(menus));
					if (new JSONStatus().status(val_menus) == 0) {
						new UpdateSynkStatus(menus, ServiApplication.menu);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (lendmoney.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_lendmoney = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getLendMoneyTableData(lendmoney));
					if (new JSONStatus().status(val_lendmoney) == 0) {
						new UpdateSynkStatus(lendmoney,
								ServiApplication.lendMoney);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (deliveryLendMoney.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_deliveryLendmoney = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getLendDeliveryTableData(deliveryLendMoney));
					if (new JSONStatus().status(val_deliveryLendmoney) == 0) {
						new UpdateSynkStatus(lendmoney,
								ServiApplication.deliveryLendMoney);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (inventoryHistorylist.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_inventoryHistorylist = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(AutoSyncService.this)
											.getInventoryHistoryTableData(inventoryHistorylist));
					if (new JSONStatus().status(val_inventoryHistorylist) == 0) {
						new UpdateSynkStatus(inventoryHistorylist,
								ServiApplication.inventoryHistory);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (inventoryAdjlist.size() > 0) {
				String val_inventoryAdjlist = servicehandler.makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(AutoSyncService.this)
								.getInventoryAdjTableData(inventoryAdjlist));
				if (new JSONStatus().status(val_inventoryAdjlist) == 0) {
					new UpdateSynkStatus(inventoryAdjlist,
							ServiApplication.inventoryAdj);
				}
			}

			if (inventorylist.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_inventorylist = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getInventoryTableData(inventorylist));
					if (new JSONStatus().status(val_inventorylist) == 0) {
						new UpdateSynkStatus(inventorylist,
								ServiApplication.inventory);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (menudishlist.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_menudishlist = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getMenuDishTableTableData(menudishlist));
					if (new JSONStatus().status(val_menudishlist) == 0) {
						new UpdateSynkStatus(menudishlist,
								ServiApplication.menudishlist);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (dishlist.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_dishlist = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getDishTableTableData(dishlist));
					if (new JSONStatus().status(val_dishlist) == 0) {
						new UpdateSynkStatus(dishlist,
								ServiApplication.dishlist);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (clientPayList.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_clientList = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getClientTableData(clientPayList));
					if (new JSONStatus().status(val_clientList) == 0) {
						new UpdateSynkStatus(clientPayList,
								ServiApplication.Client_Info);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (deliveryPayList.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_deliveryList = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getDeliveryTableData(deliveryPayList));
					if (new JSONStatus().status(val_deliveryList) == 0) {
						new UpdateSynkStatus(deliveryPayList,
								ServiApplication.Delivery_Info);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (clientList.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_clientList = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getClientTableData(clientList));
					if (new JSONStatus().status(val_clientList) == 0) {
						new UpdateSynkStatus(clientList,
								ServiApplication.Client_Info);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (deliveryList.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_deliveryList = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getDeliveryTableData(deliveryList));
					if (new JSONStatus().status(val_deliveryList) == 0) {

						//HAROLD

						new UpdateSynkStatus(deliveryList,
								ServiApplication.Delivery_Info);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (cashFlowList.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_cashFlowList = servicehandler.makeServiceCall(
							ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(
									AutoSyncService.this)
									.getCashFlowTableData(cashFlowList));
					if (new JSONStatus().status(val_cashFlowList) == 0) {
						new UpdateSynkStatus(cashFlowList,
								ServiApplication.CashFlowList);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}

			if (dishProductsList.size() > 0) {
				if (CommonMethods.getInternetSpeed(AutoSyncService.this) >= ServiApplication.local_data_speed) {
					String val_dishProductsList = servicehandler
							.makeServiceCall(
									ServiApplication.URL + "/sync",
									ServiceHandler.POST,
									new RequestFiels(AutoSyncService.this)
											.getDishProductsTableData(dishProductsList));
					if (new JSONStatus().status(val_dishProductsList) == 0) {
						new UpdateSynkStatus(dishProductsList,
								ServiApplication.DishProductsList);
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			clientList.clear();
			cashFlowList.clear();
			dishProductsList.clear();
			clientPayList.clear();
			deliveryLendMoney.clear();
			deliverypaymentDetails.clear();
			deliveryList.clear();
			deliveryPayList.clear();
			dishlist.clear();
			menudishlist.clear();
			inventorylist.clear();
			inventoryAdjlist.clear();
			inventoryHistorylist.clear();
			lendmoney.clear();
			menus.clear();
			menuInventorylist.clear();
			menuInventoryAdjlist.clear();
			orderdetails.clear();
			orders.clear();
			sales.clear();
			salesDetails.clear();
			clientpaymentDetails.clear();
			supplierDetails.clear();
			userDetailDetails.clear();
			supplierPaymentsDetails.clear();
			productDetails.clear();
			
		}
	}
}
