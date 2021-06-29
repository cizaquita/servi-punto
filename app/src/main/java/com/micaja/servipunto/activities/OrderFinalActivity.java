package com.micaja.servipunto.activities;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.SalesAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.OrderDetailsDAO;
import com.micaja.servipunto.database.dao.OrdersDAO;
import com.micaja.servipunto.database.dao.SincronizarTransaccionesDAO;
import com.micaja.servipunto.database.dao.TransaccionBoxDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.OrderDetailsDTO;
import com.micaja.servipunto.database.dto.OrdersDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.database.dto.SincronizarTransaccionesDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.database.dto.TransaccionBoxDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.service.SincronizarTransacciones;
import com.micaja.servipunto.service.TransaccionBoxService;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.AES;
import com.micaja.servipunto.utils.AESTEST;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Data;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.GetDocumentObject;
import com.micaja.servipunto.utils.Header;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.MakeHeader;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.SalesCodes;

public class OrderFinalActivity extends BaseActivity implements
		OnClickListener, android.content.DialogInterface.OnClickListener {

	private LinearLayout llAddClient, llDiscount, llInvoice, llCalculator,
			llBarcode;
	private ListView lvFinalProducts;

	private Button btnZero, btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix,
			btnSeven, btnEight, btnNine, btnBackClear, btnAllClear, btnSearch,
			btnEnter, btnEndSale, btnCancelSale, btnBarcode, btnUserSel;
	private EditText etxtBarcode;
	private TextView txtClientName, txtClientPhone, txtClientId,
			txtClientCredit;
	private SalesAdapter salesAdapter;
	private double subTotal = 0;
	private TextView txtTotalItems, txtSubTotal, txtVat, txtDiscount,
			txtTotalAmount;

	private List<DTO> selectedList = new ArrayList<DTO>();
	private AlertDialog cancelDialog, endOrderDialog;
	Intent intent;
	private String orderInvoice, key, fecha_inicial, fecha_final;
	PuntoRedPaymentDilog prp;
	PuntoRedPaymentDilog1 prp1;
	public SharedPreferences sharedpreferences;
	UserDetailsDTO dto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, OrderFinalActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		dto = UserDetailsDAO.getInstance().getRecordsuser_name(
				DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		fecha_inicial = Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM);
		ServiApplication.shale_menu=false;
		inItUI();
	}

	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.sales);

		btnZero = (Button) findViewById(R.id.btn_SalesCalZero);
		btnOne = (Button) findViewById(R.id.btn_SalesCalOne);
		btnTwo = (Button) findViewById(R.id.btn_SalesCalTwo);
		btnThree = (Button) findViewById(R.id.btn_SalesCalThree);
		btnFour = (Button) findViewById(R.id.btn_SalesCalFour);
		btnFive = (Button) findViewById(R.id.btn_SalesCalFive);
		btnSix = (Button) findViewById(R.id.btn_SalesCalSix);
		btnSeven = (Button) findViewById(R.id.btn_SalesCalSeven);
		btnEight = (Button) findViewById(R.id.btn_SalesCalEight);
		btnNine = (Button) findViewById(R.id.btn_SalesCalNine);
		btnBackClear = (Button) findViewById(R.id.btn_SalesCalClearOne);
		btnAllClear = (Button) findViewById(R.id.btn_SalesCalClear);
		btnSearch = (Button) findViewById(R.id.btn_SalesCalSearch);
		btnEnter = (Button) findViewById(R.id.btn_SalesCalEnter);
		btnEndSale = (Button) findViewById(R.id.btn_SaleEnd);
		btnCancelSale = (Button) findViewById(R.id.btn_SaleCancel);
		btnBarcode = (Button) findViewById(R.id.btn_SalesBarCode);
		btnUserSel = (Button) findViewById(R.id.btn_SalesUser);

		etxtBarcode = (EditText) findViewById(R.id.etxt_SalesBarcode);

		txtTotalItems = (TextView) findViewById(R.id.txt_TotalItems);
		txtSubTotal = (TextView) findViewById(R.id.txt_SubTotalAmt);
		txtVat = (TextView) findViewById(R.id.txt_Vat);
		txtTotalAmount = (TextView) findViewById(R.id.txt_TotalAmount);

		txtClientName = (TextView) findViewById(R.id.txt_SalesCustomerName);
		txtClientPhone = (TextView) findViewById(R.id.txt_SalesCustomerPhone);
		txtClientId = (TextView) findViewById(R.id.txt_SalesCustomerId);
		txtClientCredit = (TextView) findViewById(R.id.txt_SalesCustomerCredit);

		lvFinalProducts = (ListView) findViewById(R.id.lv_SalesProduct);
		// receipt_wv = (WebView) findViewById(R.id.receipt_wv);

		btnZero.setOnClickListener(this);
		btnOne.setOnClickListener(this);
		btnTwo.setOnClickListener(this);
		btnThree.setOnClickListener(this);
		btnFour.setOnClickListener(this);
		btnFive.setOnClickListener(this);
		btnSix.setOnClickListener(this);
		btnSeven.setOnClickListener(this);
		btnEight.setOnClickListener(this);
		btnNine.setOnClickListener(this);
		btnBackClear.setOnClickListener(this);
		btnAllClear.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnEnter.setOnClickListener(this);
		btnEndSale.setOnClickListener(this);
		btnCancelSale.setOnClickListener(this);
		btnBarcode.setOnClickListener(this);
		btnUserSel.setOnClickListener(this);

		setInvisibleView();

		loadUI();
	}

	// Result of this method,loading screens
	private void loadUI() {
		selectedList = appContext.getSelectedProducts();
		System.out.println("Type in order Final :"
				+ ConstantsEnum.ORDERS.code());
		salesAdapter = new SalesAdapter(OrderFinalActivity.this, selectedList,
				uiHandler, ConstantsEnum.ORDERS.code(),
				ConstantsEnum.ORDERS.code(), "", "CreateDish");
		lvFinalProducts.setAdapter(salesAdapter);
		btnEndSale.setText(getResources().getString(R.string.order_end));
		btnCancelSale.setText(getResources().getString(R.string.order_cancel));
		setTextToClientView();
		setValuesToPriceTag();
	}

	// Result of this method,invisible to the views.
	private void setInvisibleView() {
		llAddClient = (LinearLayout) findViewById(R.id.ll_SalesAddCustomer);
		llDiscount = (LinearLayout) findViewById(R.id.ll_SalesDiscount);
		llInvoice = (LinearLayout) findViewById(R.id.ll_SalesInvoice);
		txtDiscount = (TextView) findViewById(R.id.txt_Discount);

		llCalculator = (LinearLayout) findViewById(R.id.ll_Calculator);
		llBarcode = (LinearLayout) findViewById(R.id.ll_Barcode);

		llAddClient.setVisibility(View.INVISIBLE);
		llDiscount.setVisibility(View.INVISIBLE);
		llInvoice.setVisibility(View.INVISIBLE);
		llCalculator.setVisibility(View.INVISIBLE);
		llBarcode.setVisibility(View.INVISIBLE);
		txtDiscount.setVisibility(View.INVISIBLE);
	}

	// This method using for Handler message to update the UI thread
	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.EDIT.code())
				updateList();
			else if (msg.arg1 == SalesCodes.DELETE.code())
				updateList();
			else if (msg.arg1 == SalesCodes.CLIENT_SALE_END.code())
				ShowpaymentDialog();
		}
	};

	// This method using for Validation purpose
	private void validateAndInsert() {
		if (OrdersDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), getOrdersList())) {
			getCallTransaccionBoxService(ServiApplication.Orders_M_name,
					ServiApplication.Orders_TipoTrans,
					ServiApplication.Orders_PaymentType);
			if (OrderDetailsDAO.getInstance().insert(
					DBHandler.getDBObj(Constants.WRITABLE),
					getOrderDetailsList())) {
				CommonMethods.showToast(OrderFinalActivity.this, getResources()
						.getString(R.string.goods_success));
				appContext.setSeletedProducts(new ArrayList<DTO>());
				appContext.setSeletedSupplier("");

				if (appContext.getActivityListSize() > 0) {
					appContext
							.getActivityList()
							.get(appContext.getActivityListSize() - 2)
							.putExtra(getResources().getString(R.string.back),
									"true");
					startActivity(appContext.getActivityList().get(
							appContext.getActivityListSize() - 2));
					finish();
					appContext.popActivity();
					appContext.popActivity();
					try {
						// If printer connection is already established then it
						// simply calls printReceipt method.
						if (appContext.getPRT() != null) {
							List<DTO> ordersList = OrdersDAO
									.getInstance()
									.getRecords(
											DBHandler
													.getDBObj(Constants.READABLE));
							OrdersDTO dto = (OrdersDTO) ordersList
									.get(ordersList.size() - 1);

							List<DTO> orderItemsList = OrderDetailsDAO
									.getInstance()
									.getRecordsWithValues(
											DBHandler
													.getDBObj(Constants.READABLE),
											"sale_id", dto.getOrderId());
							// printReceipt(CommonMethods
							// .generateHtmlForReceipt(
							// dto.getOrderId(),
							// getResources().getString(
							// R.string.app_name), "", "",
							// "", "", "", "", "", "", "", "",
							// orderItemsList));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	protected void ShowpaymentDialog() {
		Log.v("DUMP REQUEST", "varahalababu=====================" );
		callConsultaProveedoresPuntoRed();
	}

	protected void ShowpaymentDialog1() {
		prp = new PuntoRedPaymentDilog(OrderFinalActivity.this);
		prp.show();
		prp.setCancelable(false);

	}

	// Bind new order_details data to BindDataTo OrderDetails DTO
	private List<DTO> getOrderDetailsList() {
		List<DTO> list = new ArrayList<DTO>();

		List<DTO> selectedProdList = appContext.getSelectedProducts();

		for (int i = 0; i < selectedProdList.size(); i++) {
			OrderDetailsDTO detailsDTO = new OrderDetailsDTO();

			SelectedProddutsDTO dto = (SelectedProddutsDTO) selectedProdList
					.get(i);

			detailsDTO.setCount(dto.getQuantity());
			detailsDTO.setOrderId(orderInvoice);
			detailsDTO.setPrice(dto.getPrice());
			detailsDTO.setProductId(dto.getIdProduct());
			detailsDTO.setSyncStatus(0);
			detailsDTO.setUom(dto.getUnits());

			list.add(detailsDTO);
		}
		return list;
	}

	/*
	 * private String getOrderID() { return
	 * OrdersDAO.getInstance().getOrderID(DBHandler
	 * .getDBObj(Constants.READABLE)); }
	 */
	// This method using for Order Create
	private List<DTO> getOrdersList() {
		OrdersDTO ordersDTO = new OrdersDTO();

		if (new AsynkTaskClass().callcentralserver("/order/create", ""
				+ ServiceHandler.POST,
				makeOrdersJosondata(String.valueOf(subTotal)),
				OrderFinalActivity.this) != null) {

			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					List<DTO> list = new ArrayList<DTO>();
					List<DTO> supplierList = appContext.getOrderSupplierList();
					ordersDTO.setOrderId(orderInvoice);
					ordersDTO.setDateTime(Dates
							.getSysDate(Dates.YYYY_MM_DD_HH_MM_SS));
					ordersDTO.setDiscount("");
					ordersDTO.setGrossAmount(String.valueOf(subTotal));
					ordersDTO.setInvoiceNo(orderInvoice);
					ordersDTO.setIsOrderConfirmed(1);
					ordersDTO.setNetAmount(String.valueOf(appContext
							.getTotalAmount()));
					ordersDTO.setPaymentType("Cash");
					ordersDTO.setFecha_inicial(fecha_inicial);
					ordersDTO.setFecha_final(Dates
							.getSysDate(Dates.YYYY_MM_DD_HH_MM_SS));
					ordersDTO.setSupplierId(((SupplierDTO) supplierList
							.get(Integer.parseInt(appContext
									.getSeletedSupplier()))).getSupplierId());
					ordersDTO.setSyncStatus(1);
					list.add(ordersDTO);
					if (ServiApplication.promotion_bye_click_flage) {
						ServiApplication.promotion_bye_flage = true;
					}
					return list;
				}
			} else {
				return selectedList;
			}
		} else {
			return selectedList;
		}
		return selectedList;
	}

	// Result of this method,calculate Vat and subtotal
	private void setValuesToPriceTag() {
		int totalItems = 0;
		double vat = 0;
		subTotal = 0;
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
		txtTotalItems.setText(getResources().getString(
				R.string.sales_total_items)
				+ " " + totalItems);
		txtSubTotal.setText(getResources().getString(R.string.sales_sub_total)
				+ " " + CommonMethods.getNumSeparator(subTotal));
		txtVat.setText(getResources().getString(R.string.sales_vat) + " "
				+ CommonMethods.getNumSeparator(vat));
		txtTotalAmount.setText(getResources().getString(
				R.string.sales_total_amount)
				+ " "
				+ CommonMethods.getNumSeparator(appContext.getTotalAmount()));
	}

	private void setTextToClientView() {
		if (!"".equals(appContext.getSeletedSupplier())) {
			SupplierDTO supplierDTO = (SupplierDTO) appContext
					.getOrderSupplierList().get(
							Integer.parseInt(appContext.getSeletedSupplier()));
			txtClientName.setText(getResources().getString(
					R.string.order_supplier)
					+ " : " + supplierDTO.getName());
			txtClientPhone.setText(getResources().getString(
					R.string.order_phone)
					+ " " + supplierDTO.getTelephone());
			txtClientId.setText(getResources().getString(R.string.order_id)
					+ " " + supplierDTO.getCedula());
			txtClientCredit.setText(getResources().getString(
					R.string.order_credit)
					+ " "
					+ CommonMethods.getNumSeparator(CommonMethods
							.getDoubleFormate(supplierDTO.getBalanceAmount())));
		}
	}

	private void updateList() {
		salesAdapter.setListData(selectedList);
		salesAdapter.notifyDataSetChanged();

		setValuesToPriceTag();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_SalesCalZero:
			setVauleToBarcode("0");
			break;

		case R.id.btn_SalesCalOne:
			setVauleToBarcode("1");
			break;

		case R.id.btn_SalesCalTwo:
			setVauleToBarcode("2");
			break;

		case R.id.btn_SalesCalThree:
			setVauleToBarcode("3");
			break;

		case R.id.btn_SalesCalFour:
			setVauleToBarcode("4");
			break;

		case R.id.btn_SalesCalFive:
			setVauleToBarcode("5");
			break;

		case R.id.btn_SalesCalSix:
			setVauleToBarcode("6");
			break;

		case R.id.btn_SalesCalSeven:
			setVauleToBarcode("7");
			break;

		case R.id.btn_SalesCalEight:
			setVauleToBarcode("8");
			break;

		case R.id.btn_SalesCalNine:
			setVauleToBarcode("9");
			break;

		case R.id.btn_SalesCalClearOne:
			if (etxtBarcode.getText().toString().trim().length() != 0)
				removeLastDigit();
			break;

		case R.id.btn_SalesCalClear:
			etxtBarcode.setText("");
			break;

		case R.id.btn_SalesCalSearch:
			appContext.pushActivity(intent);
			CommonMethods.startIntent(OrderFinalActivity.this,
					OrdersProductsActivity.class);
			break;

		case R.id.btn_SalesCalEnter:

			break;

		case R.id.btn_SaleEnd:
			endOrderDialog = CommonMethods.displayAlert(false, getResources()
					.getString(R.string.order_end),
					getResources().getString(R.string.order_end_msg),
					getResources().getString(R.string.yes), getResources()
							.getString(R.string.no), OrderFinalActivity.this,
					OrderFinalActivity.this, true);

			break;
		case R.id.btn_SaleCancel:
			cancelDialog = CommonMethods.displayAlert(false, getResources()
					.getString(R.string.order_cancel), getResources()
					.getString(R.string.order_cancel_msg), getResources()
					.getString(R.string.yes),
					getResources().getString(R.string.no),
					OrderFinalActivity.this, OrderFinalActivity.this, true);

			break;

		case R.id.btn_SaleAddCustomer:
			// appContext.pushActivity(intent);
			CommonMethods.openNewActivity(OrderFinalActivity.this,
					OrdersProductsActivity.class);
			break;

		default:
			break;
		}
	}

	private void setVauleToBarcode(String enterValue) {
		String barcode = etxtBarcode.getText().toString().trim();

		if (barcode.length() == 0)
			etxtBarcode.setText(enterValue);
		else {
			barcode = barcode.concat(enterValue);
			etxtBarcode.setText(barcode);
		}
		etxtBarcode.setSelection(etxtBarcode.getText().length());
	}

	// Result of this method,clear the last digits to the product code
	private void removeLastDigit() {
		if (etxtBarcode.getText().toString().trim().length() == 1)
			etxtBarcode.setText("");
		else {
			String barcode = etxtBarcode.getText().toString().trim();
			barcode = barcode.substring(0, barcode.length() - 1);
			etxtBarcode.setText(barcode);
		}
		etxtBarcode.setSelection(etxtBarcode.getText().length());
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		if (dialog == cancelDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				appContext.setSeletedProducts(new ArrayList<DTO>());
				// this.finish();
				appContext.clearActivityList();
				CommonMethods.openNewActivity(OrderFinalActivity.this,
						OrdersProductsActivity.class);

			}
		} else if (dialog == endOrderDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				// validateAndInsert();
				ShowpaymentDialog();
			}
		}
	}

	// This method using for Create Order that time data passing
	// to the json objects
	@SuppressWarnings("static-access")
	public String makeOrdersJosondata(String subTotal2) {
		try {
			orderInvoice = Dates.getSysDateinMilliSeconds() + "";
			List<DTO> supplierList = appContext.getOrderSupplierList();
			JSONObject rootjsonobj = new JSONObject();
			JSONObject orders = new JSONObject();
			orders.put("supplier_code", ((SupplierDTO) supplierList.get(Integer
					.parseInt(appContext.getSeletedSupplier())))
					.getSupplierId());
			orders.put("order_id", Long.parseLong(orderInvoice));
			orders.put("invoice_no", orderInvoice);
			orders.put("net_amount", CommonMethods.getDoubleFormate(String
					.valueOf(appContext.getTotalAmount())));
			orders.put("discount", "");
			orders.put("gross_amount",
					CommonMethods.getDoubleFormate(String.valueOf(subTotal2)));
			orders.put("store_code",
					sharedpreferences.getString("store_code", ""));
			orders.put("created_by",
					sharedpreferences.getString("user_name", ""));
			orders.put("payment_type", "Cash");
			try {
				orders.put("fecha_inicial",
						new Dates().serverdateformate(fecha_inicial));
			} catch (Exception e) {
				orders.put("fecha_inicial",
						new Dates().serverdateformate(fecha_inicial));
			}
			try {
				orders.put("fecha_final", new Dates().serverdateformate(Dates
						.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
			} catch (Exception e) {
				orders.put("fecha_final", new Dates().serverdateformate(Dates
						.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
			}
			orders.put("date_time", new Dates()
					.serverdateformate(Dates
							.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
			orders.put("is_order_confirmed", "0");
			JSONArray json_order_details_array = new JSONArray();
			for (int i = 0; i < selectedList.size(); i++) {
				JSONObject jsonObjectOrderitems_ = new JSONObject();
				SelectedProddutsDTO dto = (SelectedProddutsDTO) selectedList
						.get(i);
				jsonObjectOrderitems_.put("barcode", dto.getBarcode());
				jsonObjectOrderitems_.put("count", dto.getQuantity());
				jsonObjectOrderitems_.put("uom", dto.getUnits());
				jsonObjectOrderitems_.put("price",
						CommonMethods.getDoubleFormate(dto.getPrice()));
				jsonObjectOrderitems_.put("store_code",
						ServiApplication.store_id);
				json_order_details_array.put(jsonObjectOrderitems_);
			}
			rootjsonobj.put("order_details", json_order_details_array);
			rootjsonobj.put("orders", orders);
			return rootjsonobj.toString();
		} catch (Exception e) {
		}
		return null;

	}

	// For Printing Receipt. Load printing html into webview then Capture the
	// Picture.
	// i/p PrintingHtml
	// Then Invoking Async Task to Print the receipt.
	// public void printReceipt(String receipt) {
	// receipt_wv.setWebViewClient(new WebViewClient() {
	//
	// @Override
	// public void onReceivedError(WebView view, int errorCode,
	// String description, String failingUrl) {
	// super.onReceivedError(view, errorCode, description, failingUrl);
	// }
	//
	// @Override
	// public void onPageStarted(WebView view, String url, Bitmap favicon) {
	// super.onPageStarted(view, url, favicon);
	// }
	//
	// @Override
	// public void onPageFinished(WebView view, String url) {
	// super.onPageFinished(view, url);
	// new PrintReceiptTask().execute();
	// }
	// });
	//
	// // receipt_wv.loadData(data, , encoding)
	// receipt_wv.loadData(receipt, "text/html", null);
	// }

	// Async Task to Print Receipt

	// public class PrintReceiptTask extends AsyncTask<Void, Void, Void> {
	// ProgressDialog printDialog;
	//
	// @Override
	// protected void onPreExecute() {
	// try {
	// printDialog = new ProgressDialog(appContext);
	// printDialog.show();
	// printDialog.setCancelable(false);
	// printDialog.setMessage("Printing Receipt. Please Wait...");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	// try {
	// Thread.sleep(1000);
	//
	// StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
	// .permitAll().build();
	// StrictMode.setThreadPolicy(policy);
	// Picture picture = receipt_wv.capturePicture();
	// if (picture.getWidth() > 0 && picture.getHeight() > 0) {
	// Bitmap bm = Bitmap.createBitmap(picture.getWidth(),
	// picture.getHeight(), Bitmap.Config.RGB_565);
	//
	// Canvas c = new Canvas(bm);
	// picture.draw(c);
	// appContext.getPRT().PRTPrintBitmap(bm, 1);
	// appContext.getPRT().PRTPaperCut(true);
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	// super.onPostExecute(result);
	// if (printDialog.isShowing()) {
	// printDialog.dismiss();
	// }
	// }
	// }

	class PuntoRedPaymentDilog extends Dialog implements
			android.view.View.OnClickListener {
		private ImageView img_close;
		private Button btn_reg_pagpuntored, btn_reg_cash;

		public PuntoRedPaymentDilog(Context context) {
			super(context);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			initUI();
		}

		private void initUI() {
			setContentView(R.layout.puntoredpayment_dialog);
			img_close = (ImageView) findViewById(R.id.img_close);
			btn_reg_pagpuntored = (Button) findViewById(R.id.btn_reg_pagpuntored);
			btn_reg_cash = (Button) findViewById(R.id.btn_reg_cash);
			img_close.setOnClickListener(this);
			btn_reg_pagpuntored.setOnClickListener(this);
			btn_reg_cash.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.img_close:
				prp.dismiss();
				break;

			case R.id.btn_reg_pagpuntored:
				prp.dismiss();
				callConsultaProveedoresPuntoRed1();
				break;
			case R.id.btn_reg_cash:
				prp.dismiss();
				validateAndInsert();
				break;
			default:
				break;
			}
		}

	}

	public void callConsultaProveedoresPuntoRed() {
		if (NetworkConnectivity.netWorkAvailability(OrderFinalActivity.this)) {
			new ConsultaProveedoresPuntoRed(dto.getUserName(),
					dto.getPassword()).execute();
		} else {
			validateAndInsert();
		}
	}

	public void callConsultaProveedoresPuntoRed1() {

		prp1 = new PuntoRedPaymentDilog1(OrderFinalActivity.this);
		prp1.show();
		prp1.setCancelable(false);

	}

	public class PuntoRedPaymentDilog1 extends Dialog implements
			android.view.View.OnClickListener {
		private ImageView img_close;
		private Button btn_reg_pagpuntored, btn_reg_cash;
		private EditText et_key, et_value;
		private TextView tv_changepass_title;

		public PuntoRedPaymentDilog1(Context context) {
			super(context);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			initUI();
		}

		private void initUI() {
			setContentView(R.layout.registerpagodialog);

			img_close = (ImageView) findViewById(R.id.img_close);
			et_key = (EditText) findViewById(R.id.et_key);
			et_key.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			et_key.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

			btn_reg_pagpuntored = (Button) findViewById(R.id.btn_EndSaleSave);
			btn_reg_cash = (Button) findViewById(R.id.btn_EndSaleCancel);
			et_value = (EditText) findViewById(R.id.et_value);
			et_value.setText("" + appContext.getTotalAmount());
			tv_changepass_title = (TextView) findViewById(R.id.tv_changepass_title);
			tv_changepass_title.setText(getResources().getString(
					R.string.information));
			img_close.setOnClickListener(this);
			et_value.setFocusable(false);
			img_close.setOnClickListener(this);
			btn_reg_pagpuntored.setOnClickListener(this);
			btn_reg_cash.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.img_close:
				prp1.dismiss();
				break;

			case R.id.btn_EndSaleSave:

				if (CommonMethods.keyvalidation(et_key.getText().toString()
						.trim())) {

					CommonMethods.showCustomToast(OrderFinalActivity.this,
							getResources().getString(R.string.enter_key));

				} else {
					prp1.dismiss();
					key = et_key.getText().toString().trim();
					callRecaudoProveedor();
				}

				break;
			case R.id.btn_EndSaleCancel:
				prp1.dismiss();
				break;
			default:
				break;
			}
		}

	}

	public void callRecaudoProveedor() {

		if (NetworkConnectivity.netWorkAvailability(OrderFinalActivity.this)) {
			new RecaudoProveedor(dto.getUserName(), dto.getPassword())
					.execute();
		} else {
			CommonMethods.showCustomToast(this,
					getResources().getString(R.string.no_wifi_adhoc));
		}
	}

	/* call Recharges confirmation */
	public class ConsultaProveedoresPuntoRed extends
			AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";

		private boolean flage = false, exception = false, exception1 = false;
		private String microwInsurenceAmount_value;
		private String encrypt_key;
		private String json, respondsdata;

		public ConsultaProveedoresPuntoRed(String email, String password) {
			this.mEmail = email;
			this.mPassword = password;
		}

		@Override
		public void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					OrderFinalActivity.this);
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences
					.getString("AutoGenKey", ""));
			json = getJsonObjforConsultaProveedoresPuntoRed();
		}

		@Override
		public Boolean doInBackground(Void... params) {
			try {
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);
				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(
								OrderFinalActivity.this,
								encrypt_key,
								ServiApplication.process_id_ConsultarConvenioProveedores,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json, "JSON"));// JSON_AES
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.v("DUMP REQUEST", "varahalababu=====================" + ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.d("DUMP RESPONSE", ht.responseDump);
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					respondsdata = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
					Log.v("DUMP REQUEST", "varahalababu=====================" + respondsdata);
				} else {
					respondsdata = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
					exception1 = true;
					Log.v("DUMP REQUEST", "varahalababu=====================" + respondsdata);
				}
			} catch (Exception e) {
				e.printStackTrace();
				exception = true;
				return false;
			}
			return true;
		}

		private final HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,
					ServiApplication.SOAP_URL, ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;
		}

		private final SoapSerializationEnvelope getSoapSerializationEnvelope(
				SoapObject request) {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.implicitTypes = true;
			envelope.setAddAdornments(true);
			envelope.setOutputSoapObject(request);
			return envelope;
		}

		@Override
		public void onPostExecute(final Boolean success) {
			CommonMethods.dismissProgressDialog();
			if (exception) {
				ShowpaymentDialog1();
//				Log.v("DUMP REQUEST", "varahalababu=====================" );
//				validateAndInsert();
			} else if (exception1) {
				ShowpaymentDialog1();
//				Log.v("DUMP REQUEST", "varahalababu=====================" );
//				validateAndInsert();
			} else {
				ShowpaymentDialog1();
			}
		}
	}

	/* Recharges json object */
	public String getJsonObjforConsultaProveedoresPuntoRed() {
		JSONObject jsonobj = new JSONObject();
		try {
			List<DTO> supplierList = appContext.getOrderSupplierList();
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("provId", ((SupplierDTO) supplierList.get(Integer
					.parseInt(appContext.getSeletedSupplier())))
					.getSupplierId());
			// jsonobj.put("provId", "1524");
			return jsonobj.toString();
		} catch (Exception e) {
		}
		return jsonobj.toString();
	}

	public class RecaudoProveedor extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private static final String SOAP_ACTION = "";
		private boolean exception = false, exception1 = false;
		private String encrypt_key, json, jsondata;

		public RecaudoProveedor(String email, String password) {
			mEmail = email;
			mPassword = password;
			encrypt_key = AES.encrypt(ServiApplication.puntho_pass,
					ServiApplication.AES_secret_key);
			SecretKeySpec key = AESTEST.AESkeyFromString(sharedpreferences
					.getString("AutoGenKey", ""));
			json = AESTEST.AESCrypt(getJsonObj(), key);
		}

		@Override
		protected void onPreExecute() {
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					OrderFinalActivity.this);
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String jsonvalue = getJsonObj();
				SoapObject request = new SoapObject(
						ServiApplication.SOAP_NameSpace,
						ServiApplication.SOAP_Method_Operators);

				request.addProperty(MakeHeader.makepropertyInfo1(MakeHeader
						.makeHeader(OrderFinalActivity.this, encrypt_key,
								ServiApplication.process_id_recaudoProveedor,
								ServiApplication.username, json)));
				request.addProperty(MakeHeader.makepropertyInfo2(json,
						"JSON_AES"));
				SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
				envelope.addMapping(null, "header", Header.class);
				envelope.addMapping(null, "data", Data.class);
				HttpTransportSE ht = getHttpTransportSE();
				ht.call(SOAP_ACTION, envelope);
				Log.v("DUMP REQUEST", "varahalababu" + ht.requestDump);
				SoapObject resultsString = (SoapObject) envelope.getResponse();
				Log.d("DUMP RESPONSE", ht.responseDump);
				if (new ParsingHandler()
						.getString(
								new GetDocumentObject()
										.getDocumentObj(ht.responseDump),
								"response", "state").contains("SUCCESS")) {
					jsondata = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");

				} else {
					jsondata = new ParsingHandler().getString(
							new GetDocumentObject()
									.getDocumentObj(ht.responseDump),
							"response", "data");
					exception1 = true;
				}
				Log.d("SOAP", resultsString.toString());
			} catch (Exception e) {
				exception = true;
				e.printStackTrace();
				return false;
			}
			return true;
		}

		private final HttpTransportSE getHttpTransportSE() {
			HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY,
					ServiApplication.SOAP_URL, ServiApplication.timeToBlink);
			ht.debug = true;
			return ht;
		}

		private final SoapSerializationEnvelope getSoapSerializationEnvelope(
				SoapObject request) {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.implicitTypes = true;
			envelope.setAddAdornments(true);
			envelope.setOutputSoapObject(request);
			return envelope;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			// TODO Auto-generated method stub
			CommonMethods.dismissProgressDialog();

			if (exception) {
				CommonMethods.showCustomToast(OrderFinalActivity.this,
						getResources().getString(R.string.oops_showmsg));
			} else if (exception1) {
				String value = AESTEST.AESDecrypt(jsondata, AESTEST
						.AESkeyFromString(sharedpreferences.getString(
								"AutoGenKey", "")));
				JSONObject json;
				try {
					json = new JSONObject(value);
					CommonMethods.showCustomToast(OrderFinalActivity.this,
							json.getString("message"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				try {
					String value = AESTEST.AESDecrypt(jsondata, AESTEST
							.AESkeyFromString(sharedpreferences.getString(
									"AutoGenKey", "")));
					JSONObject json = new JSONObject(value);

					CommonMethods.showCustomToast(OrderFinalActivity.this,
							json.getString("message"));

					SincronizarTransaccionesDTO dto = new SincronizarTransaccionesDTO();
					dto.setTipo_transaction(json.getString("idTransaccion"));
					dto.setCreation_date(Dates.currentdate());
					dto.setId_pdb_servitienda("");
					dto.setModule("recaudo proveedor");
					dto.setAuthorization_number("");
					dto.setTransaction_value("" + appContext.getTotalAmount());
					dto.setCreated_by(sharedpreferences.getString("user_name",
							""));
					dto.setModified_by(sharedpreferences.getString("user_name",
							""));
					dto.setModified_date(Dates.currentdate());
					dto.setPunthored_synck_status("0");
					dto.setServitienda_synck_status("0");
					dto.setStatus("true");
					dto.setTransaction_datetime(Dates.currentdate());
					dto.setRowid("");
					dto.setModule_tipo_id(ServiApplication.tipo_proveedor_acenta_id);
					List<DTO> list = new ArrayList<DTO>();
					list.add(dto);
					SincronizarTransaccionesDAO.getInstance().insert(
							DBHandler.getDBObj(Constants.WRITABLE), list);
					Intent intent = new Intent(OrderFinalActivity.this,
							SincronizarTransacciones.class);
					// intent.putExtra("ModuleName", "recarga");
					// intent.putExtra("Module_Tipo_id", "1");
					startService(intent);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				validateAndInsert();
			}
		}
	}

	public String getJsonObj() {
		try {
			JSONObject jsonobj = new JSONObject();
			List<DTO> supplierList = appContext.getOrderSupplierList();
			jsonobj.put("comercioId", dto.getComercioId());
			jsonobj.put("terminalId", dto.getTerminalId());
			jsonobj.put("puntoDeVentaId", dto.getPuntoredId());
			jsonobj.put("provId", ((SupplierDTO) supplierList.get(Integer
					.parseInt(appContext.getSeletedSupplier())))
					.getSupplierId());
			// jsonobj.put("provId", "1524");
			jsonobj.put("valorPedido", "" + (appContext.getTotalAmount() * 100));
			// jsonobj.put("valorPedido", "" + (100 * 100));
			jsonobj.put("numeroFactura", orderInvoice);
			jsonobj.put("password", key);
			return jsonobj.toString();
		} catch (Exception e) {
			return null;
		}
	}

	// Result of this method,inserting order details data to Transaccion table
	@SuppressWarnings("unused")
	private void getCallTransaccionBoxService(String module_nem, String tipo,
			String PaymentType) {
		List<DTO> dto = new ArrayList<DTO>();
		TransaccionBoxDTO tdto = new TransaccionBoxDTO();
		tdto.setAmount(String.valueOf(appContext.getTotalAmount()));
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
			if (CommonMethods.getInternetSpeed(OrderFinalActivity.this) >= ServiApplication.local_data_speed) {
				Intent intent = new Intent(OrderFinalActivity.this,
						TransaccionBoxService.class);
				startService(intent);
			} else {
				ServiApplication.connectionTimeOutState = false;
			}
		}
	}

}
