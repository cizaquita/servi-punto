/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.MenuGriedAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.CashFlowDAO;
import com.micaja.servipunto.database.dao.ClientDAO;
import com.micaja.servipunto.database.dao.ClientPaymentDAO;
import com.micaja.servipunto.database.dao.DishProductsDAO;
import com.micaja.servipunto.database.dao.DishesDAO;
import com.micaja.servipunto.database.dao.GroupDAO;
import com.micaja.servipunto.database.dao.InventoryAdjustmentDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.InventoryHistoryDAO;
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
import com.micaja.servipunto.database.dao.UserModuleIDDAO;
import com.micaja.servipunto.database.dto.ConsultarFacturasDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.GroupDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.ChangeKeyDialog;
import com.micaja.servipunto.dialog.ConsultarfacturasDilog;
import com.micaja.servipunto.dialog.Efectivo_MenuDialog;
import com.micaja.servipunto.dialog.OopsErrorDialog;
import com.micaja.servipunto.dialog.OopsErrorDialogWithParam;
import com.micaja.servipunto.dialog.PointsDialog;
import com.micaja.servipunto.dialog.PromotionDialog;
import com.micaja.servipunto.dialog.RechargeMenuDialog;
import com.micaja.servipunto.dialog.ShopOpenDialog;
import com.micaja.servipunto.print.PRTSDKApp;
import com.micaja.servipunto.service.AutoSyncService;
import com.micaja.servipunto.service.KeyRegistrationService;
import com.micaja.servipunto.service.PullTheDataFromCentralServer;
import com.micaja.servipunto.service.TransaccionBoxService;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.AESTEST;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;
import com.micaja.servipunto.utils.UpdateSynkStatus;
import com.micaja.servipunto.utils.UserModuleCodes;
import com.micaja.servipunto.utils.Utils;

public class MenuActivity extends BaseActivity
		implements OnClickListener, android.content.DialogInterface.OnClickListener {

	private ImageView ivChangepass, img_regpago, img_logo, imageView1;
	private ServiApplication appContext;
	private Intent intent;
	private int count = 0;
	private AlertDialog acceptDialog;
	private boolean isShopOpened, req_mauricio = true;
	private List<String> userModulesList;
	private UserDetailsDTO dto;
	private ImageView imgSync, imgDataPhone, imgPrinter, imgBarcode;
	private UserDetailsDTO userDTO = new UserDetailsDTO();
	public SharedPreferences sharedpreferences;
	public Editor editor;
	private TextView shope_open_close_msg;
	private GridView gridView1;
	private String[] module_names;
	public Integer[] module_drawable;
	private LinearLayout iv_menu_mycashbox, iv_menu_parent;
	Thread thread1;
	final Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedpreferences = getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		editor = sharedpreferences.edit();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(MenuActivity.this, MenuActivity.class);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		userDTO = dto;
		ServiApplication.add_sup_product = false;
		initUI();
		appContext.setOrder_reciveinvoice("");

	}

	private void initUI() {
		if (isShopOpen()) {
			module_names = new String[] { getResources().getString(R.string.sales),
					getResources().getString(R.string.recharge), getResources().getString(R.string.recaudos),
					getResources().getString(R.string.pines), getResources().getString(R.string.trainig),
					getResources().getString(R.string.shopopne),
					getResources().getString(R.string.Consulta_de_saldo_y_caja),
					getResources().getString(R.string.maneja_de_caja), getResources().getString(R.string.giros),
					getResources().getString(R.string.pago), getResources().getString(R.string.client),
					getResources().getString(R.string.reports), getResources().getString(R.string.inventory),
					getResources().getString(R.string.orders), getResources().getString(R.string.modulo_efectivo),
					getResources().getString(R.string.promotions), getResources().getString(R.string.suppliers),
					getResources().getString(R.string.lottery) };
		} else {
			module_names = new String[] { getResources().getString(R.string.sales),
					getResources().getString(R.string.recharge), getResources().getString(R.string.recaudos),
					getResources().getString(R.string.pines), getResources().getString(R.string.trainig),
					getResources().getString(R.string.shopclose),
					getResources().getString(R.string.Consulta_de_saldo_y_caja),
					getResources().getString(R.string.maneja_de_caja), getResources().getString(R.string.giros),
					getResources().getString(R.string.pago), getResources().getString(R.string.client),
					getResources().getString(R.string.reports), getResources().getString(R.string.inventory),
					getResources().getString(R.string.orders), getResources().getString(R.string.modulo_efectivo),
					getResources().getString(R.string.promotions), getResources().getString(R.string.suppliers),
					getResources().getString(R.string.lottery) };
		}

		module_drawable = new Integer[] { R.drawable.new_sales_bg, R.drawable.new_recharge_bg,
				R.drawable.new_collection_bg, R.drawable.new_pines_bg, R.drawable.new_trainig_bg,
				R.drawable.new_shopeopen_bg, R.drawable.new_balance_enquary_bg, R.drawable.new_cashmanagment_bg,
				R.drawable.new_pagos_bg, R.drawable.new_twits_bg, R.drawable.new_clients_bg, R.drawable.new_reports_bg,
				R.drawable.new_inventory_bg, R.drawable.new_orders_bg, R.drawable.new_paymentsto_suppliers_bg,
				R.drawable.new_promotions_bg, R.drawable.new_supplier_bg, R.drawable.new_points_bg };
		setContentView(R.layout.menuactivity);

		ivChangepass = (ImageView) findViewById(R.id.img_changepass);
		imgSync = (ImageView) findViewById(R.id.img_Sync);
		imgDataPhone = (ImageView) findViewById(R.id.img_DataCrad);
		imgPrinter = (ImageView) findViewById(R.id.img_Printer);
		imgBarcode = (ImageView) findViewById(R.id.img_Barcode);
		img_regpago = (ImageView) findViewById(R.id.img_regpago);
		img_regpago.setVisibility(View.GONE);
		img_logo = (ImageView) findViewById(R.id.img_logo);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		gridView1 = (GridView) findViewById(R.id.gridView1);

		iv_menu_mycashbox = (LinearLayout) findViewById(R.id.iv_menu_mycashbox);
		iv_menu_parent = (LinearLayout) findViewById(R.id.iv_menu_parent);

		ivChangepass.setOnClickListener(this);
		imgPrinter.setOnClickListener(this);
		imgDataPhone.setOnClickListener(this);
		imgSync.setOnClickListener(this);
		img_regpago.setOnClickListener(this);
		gridView1.setOnItemClickListener(click);
		imgDataPhone.setVisibility(View.GONE);
		// imgPrinter.setVisibility(View.GONE);

		imgPrinter.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {

				//editor.putString("printer_type", "");
				//editor.commit();

				appContext.pushActivity(intent);
				Intent serverIntent1 = new Intent(MenuActivity.this, PRTSDKApp.class);
				serverIntent1.putExtra("babu", "non_print_long");
				startActivity(serverIntent1);

				return false;
			}
		});

		ServiApplication.wrong_passcount = 0;
		if (ServiApplication.alert_type == 2) {
			ServiApplication.alert_type = 333;
			if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
				new SynckDataFromCentralServer().execute(sharedpreferences.getString("store_code", ""));
			} else {
				CommonMethods.showCustomToast(this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}

		// As per Mauricio / Harold request we was block this
		try {
			getDbblm();
		} catch (Exception e) {

		}

		getDataFromDB();

		if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {

			if (userDTO.getPuntoredId().length() > 4) {
				Intent intent = new Intent(MenuActivity.this, KeyRegistrationService.class);
				startService(intent);
			}
		}
		shopeOpenevents();
		CallToSynck();
		appContext.getOrderSupplierList().clear();

		if (sharedpreferences.getString("servi_db_flage", "").equals("1")) {
			pussAllDataToCentrealServer();
		}
		imageView1.setVisibility(View.VISIBLE);
		ServiApplication.himp_print_flage = false;
		// CommonMethods.drawer_test(MenuActivity.this);

		// new PrintAsync().execute();

	}



	private void getDbblm() {

		GroupDTO dto = GroupDAO.getInstance().DBLMByGroup_id(DBHandler.getDBObj(Constants.WRITABLE), "1");
		// if (dto.getName().equals("babu")) {
		editor.putString("servi_db_flage", "0");// if you add this functinality
												// once again should add 1
		editor.commit();
		dto.setName("varahalababu");
		GroupDAO.getInstance().DBLMupdate(DBHandler.getDBObj(Constants.WRITABLE), dto);

		ServiApplication.setUiHandler(handler);
	}

	OnItemClickListener click = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			ServiApplication.connectionTimeOutState = true;
			switch (arg2) {
			case 0:
				if (userModulesList.contains(UserModuleCodes.SALES.code())) {
					if (isShopOpen()) {
						if (req_mauricio) {
							appContext.setSeletedProducts(new ArrayList<DTO>());
							appContext.pushActivity(intent);
							CommonMethods.openNewActivity(MenuActivity.this, SalesActivity.class);
							// CommonMethods.showCustomToast(MenuActivity.this,
							// getResources().getString(R.string.user_module_msg));
						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}
					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 1:
				if (userDTO.getPuntoredId().length() > 4) {
					if (isShopOpen()) {
						if (req_mauricio) {
							RechargeMenuDialog dialog = new RechargeMenuDialog(MenuActivity.this, true);
							dialog.show();
							dialog.setCancelable(false);
						} else {
							RechargeMenuDialog dialog = new RechargeMenuDialog(MenuActivity.this, true);
							dialog.show();
							dialog.setCancelable(false);
						}
					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 2:

				if (userDTO.getPuntoredId().length() > 4) {
					if (isShopOpen()) {
						if (req_mauricio) {
							RechargeMenuDialog dialog = new RechargeMenuDialog(MenuActivity.this, false);
							dialog.show();
							dialog.setCancelable(false);
							// CommonMethods.showCustomToast(MenuActivity.this,
							// getResources().getString(R.string.user_module_msg));
						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}

					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 3:
				if (req_mauricio) {
					pinesEvent();

				} else {
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				}

				break;
			case 4:
				// CommonMethods.showCustomToast(MenuActivity.this, "" + arg2);
				if (req_mauricio) {
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				} else {
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				}

				break;
			case 5:
				if (userModulesList.contains(UserModuleCodes.SHOP_OPEN_CLOSE.code())) {
					appContext.pushActivity(intent);
					if (req_mauricio) {
						checkisShopClosed();
					} else {
						checkisShopClosed();
					}

				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 6:
				// CommonMethods.showCustomToast(MenuActivity.this, "" + arg2);
				if (userModulesList.contains(UserModuleCodes.CASH.code())) {
					if (isShopOpen()) {
						if (req_mauricio) {

							Intent intent = new Intent(MenuActivity.this, ReportDetailsActivity1.class);
							intent.putExtra("SELECTED_REPORT", 13);
							appContext.pushActivity(new Intent(MenuActivity.this, ReportsActivity.class));
							startActivity(intent);

						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}

					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));

				break;
			case 7:
				if (userModulesList.contains(UserModuleCodes.CASH.code())) {
					if (isShopOpen()) {

						if (req_mauricio) {
							appContext.pushActivity(intent);
							CommonMethods.openNewActivity(MenuActivity.this, CashActivity.class);
						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}

					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));

				break;
			case 8:
				if (userModulesList.contains(UserModuleCodes.SALES.code())) {
					if (isShopOpen()) {
						if (req_mauricio) {
							appContext.setSeletedProducts(new ArrayList<DTO>());
							appContext.pushActivity(intent);
							CommonMethods.openNewActivity(MenuActivity.this, DeliveryActivity.class);

						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}
					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 9:
				if (userDTO.getPuntoredId().length() > 4) {
					if (isShopOpen()) {

						if (req_mauricio) {
							appContext.pushActivity(intent);
							PointsDialog dialog = new PointsDialog(MenuActivity.this, true);
							dialog.show();
							dialog.setCancelable(false);

							// CommonMethods.showCustomToast(MenuActivity.this,
							// getResources().getString(R.string.user_module_msg));

						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}

					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 10:
				if (userModulesList.contains(UserModuleCodes.CLIENT.code())) {
					if (isShopOpen()) {

						if (req_mauricio) {
							appContext.pushActivity(intent);
							appContext.mClientActivityBackStack = MenuActivity.this;
							CommonMethods.startIntent(MenuActivity.this, ClientsActivity.class);
							// CommonMethods.showCustomToast(MenuActivity.this,
							// getResources().getString(R.string.user_module_msg));
						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}
					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 11:
				if (userModulesList.contains(UserModuleCodes.REPORTS.code())) {
					if (isShopOpen()) {

						if (req_mauricio) {
							appContext.pushActivity(intent);
							CommonMethods.startIntent(MenuActivity.this, ReportsActivity.class);
						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}

					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 12:
				if (userModulesList.contains(UserModuleCodes.INVENTORY.code())) {
					if (isShopOpen()) {

						if (req_mauricio) {
							appContext.setSeletedProducts(new ArrayList<DTO>());
							appContext.pushActivity(intent);
							CommonMethods.openNewActivity(MenuActivity.this, InventoryActivity.class);
							// CommonMethods.showCustomToast(MenuActivity.this,
							// getResources().getString(R.string.user_module_msg));

						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}

					} else
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 13:
				if (userModulesList.contains(UserModuleCodes.ORDERS.code())) {
					if (isShopOpen()) {

						if (req_mauricio) {
							appContext.setSeletedProducts(new ArrayList<DTO>());
							appContext.pushActivity(intent);
							CommonMethods.openNewActivity(MenuActivity.this, OrdersActivity.class);
							// CommonMethods.showCustomToast(MenuActivity.this,
							// getResources().getString(R.string.user_module_msg));
						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}

					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 14:
				if (userModulesList.contains(UserModuleCodes.PROMOTIONS.code())) {
					if (isShopOpen()) {
						if (req_mauricio) {
							Efectivo_MenuDialog dialog = new Efectivo_MenuDialog(MenuActivity.this);
							dialog.show();
							dialog.setCancelable(false);
						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}
					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 15:
				if (userModulesList.contains(UserModuleCodes.PROMOTIONS.code())) {
					if (isShopOpen()) {
						if (req_mauricio) {
							appContext.pushActivity(intent);
							appContext.setPromotionlist(new ArrayList<DTO>());
							CommonMethods.openNewActivity(MenuActivity.this, PromotionsActivity.class);
							// CommonMethods.showCustomToast(MenuActivity.this,
							// getResources().getString(R.string.user_module_msg));
						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}
					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 16:
				if (userModulesList.contains(UserModuleCodes.SUPPLIERS.code())) {
					if (isShopOpen()) {

						if (req_mauricio) {
							appContext.pushActivity(intent);
							CommonMethods.openNewActivity(MenuActivity.this, SuppliersActivity.class);
							// CommonMethods.showCustomToast(MenuActivity.this,
							// getResources().getString(R.string.user_module_msg));
						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}

					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;
			case 17:
				if (userDTO.getPuntoredId().length() > 4) {
					if (isShopOpen()) {

						if (req_mauricio) {
							appContext.pushActivity(intent);
							intent = new Intent(MenuActivity.this, LotterySalesActivity.class);
							startActivity(intent);
							// CommonMethods.showCustomToast(MenuActivity.this,
							// getResources().getString(R.string.user_module_msg));

						} else {
							CommonMethods.showCustomToast(MenuActivity.this,
									getResources().getString(R.string.user_module_msg));
						}

					} else {
						CommonMethods.showCustomToast(MenuActivity.this,
								getResources().getString(R.string.shopOpen_msg));
					}
				} else
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				break;

			default:
				break;
			}
		}
	};

	private void CallToSynck() {
		try {
			stopService(new Intent(this, AutoSyncService.class));
			intent = new Intent(this, AutoSyncService.class);
			startService(intent);

			stopService(new Intent(this, TransaccionBoxService.class));
			intent = new Intent(this, TransaccionBoxService.class);
			startService(intent);
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("deprecation")
	private void shopeOpenevents() {

		final Integer[] lv_images = { R.drawable.menuitem_menus_bg, R.drawable.menuitem_cabincalls_bg,
				R.drawable.menuitem_lotterysales_bg, R.drawable.menuitem_convention_bg,
				R.drawable.menuitem_collection_bg, R.drawable.menuitem_pines_bg, R.drawable.menuitem_lotterysales_bg, // phase2
																														// modules
																														// code
				R.drawable.menuitem_cabincalls_bg, R.drawable.menuitem_microinsurance_bg,
				R.drawable.menuitem_microcredit_bg, R.drawable.menuitem_cabincalls_bg };

		final String[] lv_names = { getResources().getString(R.string.menus),
				getResources().getString(R.string.DaviPlata_Payment), getResources().getString(R.string.lottery_sales),
				getResources().getString(R.string.convention_info), getResources().getString(R.string.recaudos),
				getResources().getString(R.string.pines), getResources().getString(R.string.c_banking), // phase2
																										// modules
																										// code
				getResources().getString(R.string.cabin_calls), getResources().getString(R.string.micro_insurance),
				getResources().getString(R.string.micro_credit), getResources().getString(R.string.modulo_efectivo) };
	}

	protected void moduloEfectivo() {
		if (isShopOpen()) {
			appContext.pushActivity(intent);
			Efectivo_MenuDialog dialogs = new Efectivo_MenuDialog(this);
			dialogs.show();
			dialogs.setCancelable(false);

		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
		}

	}

	protected void pinesEvent() {
		if (isShopOpen()) {
			if (userDTO.getPuntoredId().length() > 4) {
				appContext.pushActivity(intent);
				CommonMethods.startIntent(MenuActivity.this, PinesActivity.class);
			} else {

				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.user_module_msg));
			}

		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
		}
	}

	protected void daviPlataPayment() {
		if (isShopOpen()) {

			if (userDTO.getPuntoredId().length() > 4) {
				appContext.pushActivity(intent);
				CommonMethods.openNewActivity(MenuActivity.this, DaviPlataPaymentActivity.class);
			} else {

				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.user_module_msg));
			}

		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
		}
	}

	protected void microCreditevent() {
		if (isShopOpen()) {

		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
		}

	}

	protected void microInsuranceevent() {
		if (isShopOpen()) {

		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
		}
	}

	protected void cabinCallsevent() {
		if (isShopOpen()) {

		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
		}
	}

	protected void corresponentevent() {
		if (isShopOpen()) {
			if (userDTO.getPuntoredId().length() > 4) {
				appContext.pushActivity(intent);
				CommonMethods.startIntent(MenuActivity.this, DepositoDaviplataActivity.class);
			}

			else {
				CommonMethods.showCustomToast(this, getResources().getString(R.string.user_module_msg));
			}
		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
		}
	}

	protected void lotterySales() {

		if (isShopOpen()) {

			if (userDTO.getPuntoredId().length() > 4) {
				appContext.pushActivity(intent);
				CommonMethods.startIntent(MenuActivity.this, LotterySalesActivity.class);
			} else {

				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.user_module_msg));
			}

		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
		}
	}

	protected void convenInfo() {
		if (isShopOpen()) {
			if (userDTO.getPuntoredId().length() > 4) {
				appContext.pushActivity(intent);
				CommonMethods.startIntent(MenuActivity.this, ConventionInformationActivity.class);
			} else {

				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.user_module_msg));
			}

		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
		}
	}

	protected void menusevent() {

		if (userModulesList.contains(UserModuleCodes.MENUS.code())) {
			if (isShopOpen()) {
				appContext.pushActivity(intent);
				CommonMethods.startIntent(MenuActivity.this, MenusActivity.class);
				this.finish();
			} else {
				CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
			}
		} else
			CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.user_module_msg));

	}

	protected void collectionEvent() {
		if (isShopOpen()) {
			if (userDTO.getPuntoredId().length() > 4) {
				appContext.pushActivity(intent);
				CommonMethods.startIntent(MenuActivity.this, CollectionActivity.class);
			} else {
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.user_module_msg));
			}
		} else {
			CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		ServiApplication.userName = sharedpreferences.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code", "");
	}

	@Override
	public void onClick(View v) {
		clearCon3textData();
		ServiApplication.connectionTimeOutState = true;
		switch (v.getId()) {

		case R.id.img_regpago:
			if (isShopOpen()) {
				if (userDTO.getPuntoredId().length() > 4) {
					// appContext.pushActivity(intent);
					// RegisterPagos dialogs = new RegisterPagos(this);
					// dialogs.show();
					// dialogs.setCancelable(false);
					calRegpagowebservice1();
				} else {
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				}
			} else
				CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
			break;

		case R.id.img_changepass:
			if (isShopOpen()) {
				if (userDTO.getPuntoredId().length() > 4) {
					appContext.pushActivity(intent);
					ChangeKeyDialog dialogs = new ChangeKeyDialog(this);
					dialogs.show();
					dialogs.setCancelable(false);
				} else {
					CommonMethods.showCustomToast(MenuActivity.this,
							getResources().getString(R.string.user_module_msg));
				}
			} else
				CommonMethods.showCustomToast(this, getResources().getString(R.string.shopOpen_msg));
			break;

		case R.id.img_Sync:
			ServiApplication.store_id = sharedpreferences.getString("store_code", "");
			pussAllDataToCentrealServer();
			break;

		case R.id.img_DataCrad:

			Intent serverIntent = new Intent(appContext, DeviceListActivity.class);
			serverIntent.putExtra("isFrom", "DataPhone");
			startActivityForResult(serverIntent, 20);

			break;

		case R.id.img_Printer:

			 //appContext.pushActivity(intent);
			 //CommonMethods.openNewActivity(MenuActivity.this,
			 //PRTSettingsActivity.class);

			appContext.pushActivity(intent);
			Intent serverIntent1 = new Intent(MenuActivity.this, PRTSDKApp.class);
			serverIntent1.putExtra("babu", "non_print");
			//serverIntent1.putExtra("babu", "");
			startActivity(serverIntent1);

			break;

		case R.id.img_Barcode:

			break;
		case R.id.img_Recharges:

			appContext.pushActivity(intent);
			CommonMethods.openNewActivity(MenuActivity.this, RecargasActivity.class);

			break;

		// case R.id.img_Icon_Chat:
		// TwnelUtils
		// .navigateToChat(
		// MenuActivity.this,
		// "servirestaurante",
		// "com.micaja.servipunto",
		// "com.micaja.servipunto.activities.MenuActivity",
		// true,
		// "Chatea gratis con nosotros descargando Twnel",
		// "1.) Da click en \"Siguiente\".\n"
		// + "2.) Inicia DescargaTwnel en PlayStore\n"
		// + "3.) Comunicate gratis con Servirestaurante todos los dÃ­as.",
		// "Siguiente");
		// break;

		default:
			break;
		}
	}

	private void calRegpagowebservice1() {
		if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
			new CalRegPagoWebservice1().execute();
		} else {
			CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	/****************************** Synk ***************************/
	private void pussAllDataToCentrealServer() {

		new ClientListSyncservice().execute();

	}

	/*************************************
	 * Synk end
	 **********************************/

	private void clearCon3textData() {
		appContext.setClientDTO(null);
		appContext.setDiscount(0);
		appContext.setDishDTO(null);
		appContext.setMenusDishesList(new ArrayList<DTO>());
		appContext.setOrderSupplierList(new ArrayList<DTO>());
		appContext.setProductDTO(null);
		appContext.setSalesDTO(null);
		appContext.setSeletedProducts(new ArrayList<DTO>());
		appContext.setTotalAmount(0);
		Utils.setInvoiceList(null);
		appContext.setInventoryHistoryList(new ArrayList<DTO>());
	}

	private boolean isShopOpen() {
		if (userDTO.getIsClosed() == Constants.FALSE && userDTO.getSyncStatus() == Constants.TRUE)
			return true;
		else
			return false;
	}

	private void checkisShopClosed() {
		System.out.println("Is Closed :" + userDTO.getIsClosed());
		System.out.println("Is status :" + userDTO.getSyncStatus());
		if (userDTO.getIsClosed() == Constants.TRUE && userDTO.getSyncStatus() == Constants.TRUE) {
			if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
				ShopOpenDialog dialog = new ShopOpenDialog(this, userDTO);
				dialog.show();
			} else {
				CommonMethods.showcustomdialogbox(MenuActivity.this, getResources().getString(R.string.oops_errmsg),
						getResources().getString(R.string.network_sync_msg), null);
			}
		} else if (userDTO.getIsClosed() == Constants.FALSE && userDTO.getSyncStatus() == Constants.TRUE) {
			CommonMethods.startIntent(MenuActivity.this, ShopOpenCloseActivity.class);

		} else if (userDTO.getIsClosed() == Constants.TRUE && userDTO.getSyncStatus() == Constants.FALSE) {

			if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
				updateRecordInDB();
				ShopOpenDialog dialog = new ShopOpenDialog(this, userDTO);
				dialog.show();
			} else {
				CommonMethods.showcustomdialogbox(MenuActivity.this, getResources().getString(R.string.oops_errmsg),
						getResources().getString(R.string.network_sync_msg), null);
			}
		} else {
			if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
				ShopOpenDialog dialog = new ShopOpenDialog(this, userDTO);
				dialog.show();
			} else {
				CommonMethods.showcustomdialogbox(MenuActivity.this, getResources().getString(R.string.oops_errmsg),
						getResources().getString(R.string.network_sync_msg), null);
			}
		}

	}

	private void getDataFromDB() {
		userDTO = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		userModulesList = UserModuleIDDAO.getInstance().getUserModuleCodes(DBHandler.getDBObj(Constants.READABLE));
		gridView1.setAdapter(new MenuGriedAdapter(MenuActivity.this, module_names, module_drawable, true));
		iv_menu_mycashbox.setVisibility(View.VISIBLE);

		if (userDTO.getPuntoredId().length() > 2) {
			img_logo.setBackgroundResource(R.drawable.menu_logo);
		} else {
			img_logo.setBackgroundResource(R.drawable.menu_logo_old);
		}

	}

	private boolean updateRecordInDB() {
		CashFlowDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
		ClientPaymentDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
		LendMoneyDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
		InventoryHistoryDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));

		userDTO.setIsClosed(Constants.TRUE);
		userDTO.setOpeningBalance(userDTO.getOpeningBalance());
		userDTO.setCloseDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		userDTO.setSyncStatus(Constants.TRUE);

		if (UserDetailsDAO.getInstance().updateShopOpenInfoDB(DBHandler.getDBObj(Constants.WRITABLE), userDTO)) {
			CommonMethods.showToast(this, getResources().getString(R.string.update_msg));
			return true;
		} else
			CommonMethods.showToast(this, getResources().getString(R.string.not_added));
		return false;
	}

	@Override
	public void onBackPressed() {
		if (count == 0) {
			CommonMethods.showToast(this, getResources().getString(R.string.exitmsg));
			count++;
		} else {
			appContext.clearActivityList();
			List<DTO> dto = UserDetailsDAO.getInstance().getRecords(DBHandler.getDBObj(Constants.READABLE));
			UserDetailsDTO userdao = (UserDetailsDTO) dto.get(0);
			if (userdao.getIsClosed() == 1) {
			} else {
				appContext.backButton = true;
			}
			Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			this.finish();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			switch (resultCode) {
			case 20:
				String strBTAddress;
				String strIsConnected = data.getExtras().getString("is_connected");
				if (strIsConnected.equals("NO")) {
					Toast.makeText(appContext, R.string.connecterr, Toast.LENGTH_SHORT).show();
					return;
				} else {
					strBTAddress = data.getExtras().getString("BTAddress");
					if (strBTAddress == null) {
						return;
					} else if (!strBTAddress.contains(":")) {
						return;
					} else if (strBTAddress.length() != 17) {
						return;
					}

					if (!appContext.getPRT().OpenPort(strBTAddress)) {
						Toast.makeText(appContext, R.string.connecterr, Toast.LENGTH_SHORT).show();
						return;
					} else {
						Toast.makeText(appContext, R.string.connected, Toast.LENGTH_SHORT).show();
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/* sync service call */
	/* Clients sync service call 1 */
	private class ClientListSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);
		List<DTO> clientList = ClientDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
				"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), MenuActivity.this);
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (clientList.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_clientList = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(MenuActivity.this).getClientTableData(clientList));
					if (new JSONStatus().status(val_clientList) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(clientList, ServiApplication.Client_Info);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}

			} else {
				Log.i("varun", "Hello Babu Petla=========" + "clientList");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new CashFlowListSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* CashFlowListSync service call 2 */
	private class CashFlowListSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);
		List<DTO> cashFlowList = CashFlowDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
				"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (cashFlowList.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_cashFlowList = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getCashFlowTableData(cashFlowList));
					if (new JSONStatus().status(val_cashFlowList) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(cashFlowList, ServiApplication.CashFlowList);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}

			} else {
				Log.i("varun", "Hello Babu Petla=========" + "cashFlowList");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new DishProductsListSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* DishProductsListSync service call 3 */
	private class DishProductsListSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);
		List<DTO> dishProductsList = DishProductsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (dishProductsList.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_dishProductsList = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getDishProductsTableData(dishProductsList));
					if (new JSONStatus().status(val_dishProductsList) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(dishProductsList, ServiApplication.DishProductsList);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "dishProductsList");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new ClientPayListSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* ClientPayments sync service call 4 */
	private class ClientPayListSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);
		List<DTO> clientPayList = ClientPaymentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (clientPayList.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_clientList = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(MenuActivity.this).getClientTableData(clientPayList));
					if (new JSONStatus().status(val_clientList) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(clientPayList, ServiApplication.Client_Info);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "clientPayList");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new DishSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* DishSyncservice sync service call 5 */
	private class DishSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		List<DTO> dishlist = DishesDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
				"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (dishlist.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_dishlist = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(MenuActivity.this).getDishTableTableData(dishlist));
					if (new JSONStatus().status(val_dishlist) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(dishlist, ServiApplication.dishlist);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "dishlist");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new MenudishSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* Menu dish sync service call 6 */
	private class MenudishSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);
		List<DTO> menudishlist = MenuDishesDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (menudishlist.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
				String val_menudishlist = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
						ServiceHandler.POST,
						new RequestFiels(MenuActivity.this).getMenuDishTableTableData(menudishlist));
				if (new JSONStatus().status(val_menudishlist) == 0) {
					if (ServiApplication.connectionTimeOutState) {
						new UpdateSynkStatus(menudishlist, ServiApplication.menudishlist);
					}
				}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "menudishlist");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new InventorySyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* Inventory sync service call 7 */
	private class InventorySyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		List<DTO> inventorylist = InventoryDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (inventorylist.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_inventorylist = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getInventoryTableData(inventorylist));
					if (new JSONStatus().status(val_inventorylist) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(inventorylist, ServiApplication.inventory);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "inventorylist");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new InventoryAdjlistSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}

		}
	}

	/* InventoryAdj sync service call 8 */
	private class InventoryAdjlistSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);
		List<DTO> inventoryAdjlist = InventoryAdjustmentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (inventoryAdjlist.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_inventoryAdjlist = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getInventoryAdjTableData(inventoryAdjlist));
					if (new JSONStatus().status(val_inventoryAdjlist) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(inventoryAdjlist, ServiApplication.inventoryAdj);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "inventoryAdjlist");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new InventoryHistorySyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* InventoryHistory sync service call 9 */
	private class InventoryHistorySyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);
		List<DTO> inventoryHistorylist = InventoryHistoryDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (inventoryHistorylist.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {

					String val_inventoryHistorylist = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getInventoryHistoryTableData(inventoryHistorylist));
					if (new JSONStatus().status(val_inventoryHistorylist) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(inventoryHistorylist, ServiApplication.inventoryHistory);
						}
					}

				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "inventoryHistorylist");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new LendmoneySyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* Lend money sync service call 10 */
	private class LendmoneySyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		List<DTO> lendmoney = LendMoneyDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
				"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (lendmoney.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_lendmoney = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(MenuActivity.this).getLendMoneyTableData(lendmoney));
					if (new JSONStatus().status(val_lendmoney) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(lendmoney, ServiApplication.lendMoney);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "lendmoney");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new MenusSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* Menus sync service call 11 */
	private class MenusSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		List<DTO> menus = MenuDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
				"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			if (menus.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_menus = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(MenuActivity.this).getMenusTableData(menus));
					if (new JSONStatus().status(val_menus) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(menus, ServiApplication.menu);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "menus");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new MenuInventorySyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* MenuInventorySyncservice sync service call 12 */
	private class MenuInventorySyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		List<DTO> menuInventorylist = MenuInventoryDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (menuInventorylist.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_menuInventorylist = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getMenusInventoryTableData(menuInventorylist));
					if (new JSONStatus().status(val_menuInventorylist) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(menuInventorylist, ServiApplication.menuInventory);
						}
					}

				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "menuInventorylist");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new MenuInventoryAdjlistSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* MenuInventoryAdjlist sync service call 13 */
	private class MenuInventoryAdjlistSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		List<DTO> menuInventoryAdjlist = MenuInventoryAdjustmentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (menuInventoryAdjlist.size() > 0) {

				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {

					String val_menuInventoryAdjlist = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getMenusInventoryAdjTableData(menuInventoryAdjlist));
					if (new JSONStatus().status(val_menuInventoryAdjlist) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(menuInventoryAdjlist, ServiApplication.menuInventoryAdj);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "menuInventoryAdjlist");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new OrderDetailsSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* OrderDetails sync service call 14 */
	private class OrderDetailsSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		List<DTO> orderdetails = OrderDetailsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (orderdetails.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_orderdetails = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getOrderDetailsTableData(orderdetails));
					if (new JSONStatus().status(val_orderdetails) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(orderdetails, ServiApplication.orderDetails);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "orderdetails");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new OrdersSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* Orders sync service call 15 */
	private class OrdersSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);
		List<DTO> orders = OrdersDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
				"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (orders.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_orders = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(MenuActivity.this).getOrdersTableData(orders));
					if (new JSONStatus().status(val_orders) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(orders, ServiApplication.orders);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "orders");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new SalesSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* Sales sync service call 16 */
	private class SalesSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		List<DTO> sales = SalesDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
				"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (sales.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_sales = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(MenuActivity.this).getSalesData(sales));
					if (new JSONStatus().status(val_sales) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(sales, ServiApplication.sales);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "sales");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new SalesDetailsSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* Sales Details sync service call 17 */
	private class SalesDetailsSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		List<DTO> salesDetails = SalesDetailDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (salesDetails.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_salesDetails = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST, new RequestFiels(MenuActivity.this).getSalesDetailsData(salesDetails));
					if (new JSONStatus().status(val_salesDetails) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(salesDetails, ServiApplication.salesDetails);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "salesDetails");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new ClientPaymentDetails().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}

		}
	}

	/* Client Payment Details sync service call 18 */
	private class ClientPaymentDetails extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);
		List<DTO> clientpaymentDetails = ClientPaymentDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (clientpaymentDetails.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_clientpaymentDetails = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getPaymentDetailsData(clientpaymentDetails));
					if (new JSONStatus().status(val_clientpaymentDetails) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(clientpaymentDetails, ServiApplication.clientpayments);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "clientpaymentDetails");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new SupplierDetailsSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* Supplier Details sync service call 19 */
	private class SupplierDetailsSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		List<DTO> supplierDetails = SupplierDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (supplierDetails.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_supplierDetails = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getSuppliersDetailsData(supplierDetails));

					if (new JSONStatus().status(val_supplierDetails) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(supplierDetails, ServiApplication.supplier);
						}
					}

				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "supplierDetails");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new SupplierPaymentsDetailsSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* Supplier Payments Details sync service call 20 */
	private class SupplierPaymentsDetailsSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		List<DTO> supplierPaymentsDetails = SupplierPaymentsDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (supplierPaymentsDetails.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_supplierpayments = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getSupplierPayTableData(supplierPaymentsDetails));

					if (new JSONStatus().status(val_supplierpayments) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(supplierPaymentsDetails, ServiApplication.supplierpayments);
						}
					}

				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "supplierPaymentsDetails");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				new ProductDetailsSyncservice().execute();
			} else {
				CommonMethods.dismissProgressDialog();
				CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
			}
		}
	}

	/* Products sync service call 21 */
	private class ProductDetailsSyncservice extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);
		List<DTO> productDetails = ProductDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
				"sync_status", "0");

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (productDetails.size() > 0) {
				if (NetworkConnectivity.netWorkAvailability(MenuActivity.this)) {
					String val_productDetails = servicehandler.makeServiceCall(ServiApplication.URL + "/sync",
							ServiceHandler.POST,
							new RequestFiels(MenuActivity.this).getProductTableData(productDetails));
					if (new JSONStatus().status(val_productDetails) == 0) {
						if (ServiApplication.connectionTimeOutState) {
							new UpdateSynkStatus(productDetails, ServiApplication.updateProducts);
						}
					}
				} else {
					ServiApplication.connectionTimeOutState = false;
				}
			} else {
				Log.i("varun", "Hello Babu Petla=========" + "productDetails");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (sharedpreferences.getString("servi_db_flage", "").equals("1")) {
				if (ServiApplication.connectionTimeOutState) {
					new DeleteCatlogsyncRecord().execute();
				} else {
					CommonMethods.dismissProgressDialog();
					CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.no_wifi_adhoc));
				}
			} else {
				CommonMethods.progressDialog.dismiss();
				PushDataFromCentralServer();
			}
		}
	}

	/* DeleteCatlogsyncRecord sync service call 21 */
	private class DeleteCatlogsyncRecord extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(MenuActivity.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (CommonMethods.getInternetSpeed(MenuActivity.this) >= ServiApplication.local_data_speed) {
				String val_productDetails = servicehandler.makeServiceCall(
						ServiApplication.URL + "/catalog/clear/" + sharedpreferences.getString("store_code", ""),
						ServiceHandler.POST, null);
				if (new JSONStatus().status(val_productDetails) == 0) {
					CommonMethods.showCustomToast(MenuActivity.this, getResources().getString(R.string.store_remove));
				}
			} else {
				ServiApplication.connectionTimeOutState = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.progressDialog.dismiss();
			PushDataFromCentralServer();
		}
	}

	public void PushDataFromCentralServer() {
		if (sharedpreferences.getString("servi_db_flage", "").equals("1")) {
			CommonMethods.showProgressDialog(getString(R.string.db_cleanup), MenuActivity.this);
			CashFlowDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			ClientPaymentDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			InventoryHistoryDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			MenuInventoryDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			SupplierPaymentsDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			MenuInventoryAdjustmentDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			OrderDetailsDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			OrdersDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			SalesDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			SalesDetailDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			ProductDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			ClientDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			SupplierDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			InventoryDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
			List<DTO> dto = new ArrayList<DTO>();
			editor.putString("servi_db_flage", "0");
			editor.commit();
			thread1 = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(6000);
					} catch (Exception e) {
					}
					handler.post(new Runnable() {
						@Override
						public void run() {
							backbuttonevent();
						}
					});
				}
			});
			thread1.start();
		} else {
			if (ServiApplication.connectionTimeOutState) {
				
				CommonMethods.showCustomAlertWithOneButton(MenuActivity.this, getResources().getString(R.string.alert), getResources().getString(R.string.back_sync), null);
				
				Intent intent = new Intent(MenuActivity.this, PullTheDataFromCentralServer.class);
				startService(intent);
				
//				new SyncDataFromServerChangedClass(MenuActivity.this, 1);
//				new SyncDataFromServer(MenuActivity.this, 1);
			}
		}
	}

	private void backbuttonevent() {

		appContext.clearActivityList();
		List<DTO> dto = UserDetailsDAO.getInstance().getRecords(DBHandler.getDBObj(Constants.READABLE));
		UserDetailsDTO userdao = (UserDetailsDTO) dto.get(0);
		userdao.setIsClosed(1);
		userdao.setSyncStatus(1);
		UserDetailsDAO.getInstance().update(DBHandler.getDBObj(Constants.READABLE), userdao);
		UserDetailsDTO userdao1 = (UserDetailsDTO) dto.get(0);
		if (userdao1.getIsClosed() == 1) {
		} else {
			appContext.backButton = true;
		}
		Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		this.finish();
	}

	public class SynckDataFromCentralServer extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			CommonMethods.showProgressDialog(getString(R.string.please_wait), MenuActivity.this);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {

			ServiApplication.responds_feed = new ServiceHandler(MenuActivity.this)
					.makeServiceCall(ServiApplication.URL + "/store/openpromo/" + params[0], ServiceHandler.GET);
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {

					try {
						JSONObject jsonobj = new JSONObject(ServiApplication.responds_feed);
						JSONObject promojsonobj = jsonobj.getJSONObject("promo");
						editor.putString("promo_image_url", promojsonobj.getString("image_url"));
						editor.putString("promo_video_url", promojsonobj.getString("video_url"));
						try {
							editor.putString("promo_promotion_des", promojsonobj.getString("promotion_des"));
						} catch (Exception e) {
							editor.putString("promo_promotion_des", "");
						}
						editor.putLong("promoid", promojsonobj.getLong("promoid"));
						editor.commit();
					} catch (Exception e) {
					}
				} else {
					editor.putString("promo_image_url", "");
					editor.putString("promo_video_url", "");
					try {
						editor.putString("promo_promotion_des", "");
					} catch (Exception e) {
						editor.putString("promo_promotion_des", "");
					}
					editor.putLong("promoid", 00);
					editor.commit();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.progressDialog.dismiss();
			PromotionDialog dialog = new PromotionDialog(MenuActivity.this,
					sharedpreferences.getString("promo_image_url", "") + "0",
					sharedpreferences.getString("promo_video_url", ""),
					sharedpreferences.getString("promo_promotion_des", ""), sharedpreferences.getLong("promoid", 0), "",
					1);
			if (sharedpreferences.getString("promo_image_url", "").length() > 4
					|| sharedpreferences.getString("promo_video_url", "").length() > 4) {
				dialog.show();
				CommonMethods.click_promo(MenuActivity.this);
			}
			dialog.setCancelable(false);
			ServiApplication.promotion_id = sharedpreferences.getLong("promoid", 0);
			ServiApplication.shope_open_alert = false;
		}

	}

	/* call microseguros */
	private class CalRegPagoWebservice1 extends AsyncTask<Void, Void, Void> {

		private static final String SOAP_ACTION = "";
		private boolean flage = false, exception = false;
		private String microwInsurenceAmount_value;
		private String json, jsondata;
		private String encrypt_key;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait), MenuActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass, ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(getJsonObjforRgb1(), key);
			// json=getJsonObjforRgb1();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				SoapObject request = new SoapObject(ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader.makeHeader(MenuActivity.this, encrypt_key,
						ServiApplication.process_id_Consulta_factura, ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON_AES"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.d("DUMP REQUEST", ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				if (new ParsingHandler()
						.getString(new GetDocumentObject().getDocumentObj(ht.responseDump), "response", "state")
						.contains("SUCCESS")) {
					jsondata = new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
							"response", "data");
				} else {
					jsondata = new ParsingHandler().getString(new GetDocumentObject().getDocumentObj(ht.responseDump),
							"response", "data");
				}
				Log.d("DUMP RESPONSE", ht.responseDump);
				Log.d("SOAP", resultsString.toString());
			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
				return null;
			}
			return null;
		}

		private final HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, ServiApplication.SOAP_URL,
					ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;
		}

		private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.implicitTypes = true;
			envelope.setAddAdornments(true);
			envelope.setOutputSoapObject(request);
			return envelope;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();

			if (exception) {
				appContext.pushActivity(intent);
				OopsErrorDialog dialog = new OopsErrorDialog(MenuActivity.this);
				dialog.show();
				dialog.setCancelable(false);
			} else {
				Log.v("varahalababu", "varahlababu" + jsondata);
				JSONObject json;
				try {
					json = new JSONObject(AESTEST.AESDecrypt(jsondata,
							AESTEST.AESkeyFromString(sharedpreferences.getString("AutoGenKey", ""))));
					if (json.getBoolean("estado")) {
						ConsultarFacturasDTO dto = new ConsultarFacturasDTO();
						dto.setAbonos(json.getLong("abonos"));
						dto.setAceptaciones(json.getLong("aceptaciones"));
						dto.setComercioId(dto.getComercioId());
						dto.setComisiones(json.getLong("comisiones"));
						dto.setCreditos(json.getLong("creditos"));
						dto.setDebitos(json.getLong("debitos"));
						dto.setDevoluciones(json.getLong("devoluciones"));
						dto.setIdFactura(json.getString("idFactura"));
						dto.setOtrosCargos(json.getLong("otrosCargos"));
						dto.setPagos(json.getLong("pagos"));
						dto.setSaldoInicial(json.getLong("saldoInicial"));
						dto.setSaldoTotal(json.getLong("saldoTotal"));
						dto.setTotalCorte(json.getLong("totalCorte"));
						dto.setValorCausado(json.getLong("valorCausado"));
						dto.setVentas(json.getDouble("ventas"));
						ConsultarfacturasDilog dialog = new ConsultarfacturasDilog(MenuActivity.this, dto);
						dialog.show();
						dialog.setCancelable(false);
					} else {
						OopsErrorDialogWithParam dialog = new OopsErrorDialogWithParam(MenuActivity.this,
								"No tiene Facturas pendientes de pago");
						dialog.show();
						dialog.setCancelable(false);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public String getJsonObjforRgb1() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

}
