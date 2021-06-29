package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.SalesAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.InventoryHistoryDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dao.SupplierPaymentsDAO;
import com.micaja.servipunto.database.dao.TransaccionBoxDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.InventoryHistoryDTO;
import com.micaja.servipunto.database.dto.OrderReceiveDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.database.dto.SupplierPaymentsDTO;
import com.micaja.servipunto.database.dto.TransaccionBoxDTO;
import com.micaja.servipunto.dialog.OnDialogSaveClickListener1;
import com.micaja.servipunto.dialog.ProductDetailDialog1;
import com.micaja.servipunto.dialog.SaleFinishDialog1;
import com.micaja.servipunto.service.TransaccionBoxService;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;
import com.micaja.servipunto.utils.SalesCodes;
import com.micaja.servipunto.utils.SalesEditTypes;
import com.micaja.servipunto.utils.UpdateSynkStatus;

// no_wifi_adhoc was blocked line 511

public class ProductFinalActivity extends BaseActivity implements
		OnClickListener, android.content.DialogInterface.OnClickListener,
		OnDialogSaveClickListener1 {
	private LinearLayout llAddClient, llDiscount, llInvoice, llCalculator,
			llBarcode;
	private final String SCREEN_NAME = "Crop Protection";

	private Button btnZero, btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix,
			btnSeven, btnEight, btnNine, btnBackClear, btnAllClear, btnSearch,
			btnEnter, btnEndSale, btnCancelSale, btnNewClient, btnDiscount,
			btnInvoiceAdj, btnBarcode, btnUserSel;

	private EditText etxtBarcode;
	private TextView txtClientName, txtClientPhone, txtClientId,
			txtClientCredit;

	private TextView txtTotalItems, txtSubTotal, txtVat, txtDiscount,
			txtTotalAmount;

	private ListView lvSelectedProdu;
	private SalesAdapter salesAdapter;
	private List<DTO> selectedList = new ArrayList<DTO>();
	private AlertDialog cancelDialog;
	private final int PRODUCT_REQUEST_CODE = 100;
	private SupplierDTO suppdto = new SupplierDTO();
	private ServiApplication appContext;
	private Intent intent;
	private boolean isExists = false, isProductExists, isCall = true;
	private String dateTime, barCode;
	ArrayList<String> barCodesList = new ArrayList<String>();
	String invoiceNum;
	public SharedPreferences sharedpreferences;
	private SupplierDTO sdto = new SupplierDTO();
	private List<InventoryDTO> inventoryDTOforSup = new ArrayList<InventoryDTO>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		appContext = (ServiApplication) getApplicationContext();

		intent = new Intent(this, ProductFinalActivity.class);
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);

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
		btnNewClient = (Button) findViewById(R.id.btn_SaleAddCustomer);
		btnDiscount = (Button) findViewById(R.id.btn_SaleDiscount);
		btnInvoiceAdj = (Button) findViewById(R.id.btn_SaleInvoiceAdj);
		btnBarcode = (Button) findViewById(R.id.btn_SalesBarCode);
		btnUserSel = (Button) findViewById(R.id.btn_SalesUser);

		etxtBarcode = (EditText) findViewById(R.id.etxt_SalesBarcode);

		txtTotalItems = (TextView) findViewById(R.id.txt_TotalItems);
		txtSubTotal = (TextView) findViewById(R.id.txt_SubTotalAmt);
		txtVat = (TextView) findViewById(R.id.txt_Vat);
		txtDiscount = (TextView) findViewById(R.id.txt_Discount);
		txtTotalAmount = (TextView) findViewById(R.id.txt_TotalAmount);

		txtClientName = (TextView) findViewById(R.id.txt_SalesCustomerName);
		txtClientPhone = (TextView) findViewById(R.id.txt_SalesCustomerPhone);
		txtClientId = (TextView) findViewById(R.id.txt_SalesCustomerId);
		txtClientCredit = (TextView) findViewById(R.id.txt_SalesCustomerCredit);

		lvSelectedProdu = (ListView) findViewById(R.id.lv_SalesProduct);

		btnEndSale.setText(getResources().getString(R.string.add));
		btnCancelSale.setText(getResources().getString(R.string.cancel));

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
		btnNewClient.setOnClickListener(this);
		btnDiscount.setOnClickListener(this);
		btnInvoiceAdj.setOnClickListener(this);
		btnBarcode.setOnClickListener(this);
		btnUserSel.setOnClickListener(this);

		setInvisibleView();
		getSupplierData();
		if (ServiApplication.orederReceivedata.size() > 0) {
			orderReceive();
		}
		loadUI();

		// if existing bar-code
		etxtBarcode.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				etxtBarcode.setSingleLine(false);
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					etxtBarcode.setSingleLine(true);
					if (validateBarCode()
							&& suppliercodevalidat(etxtBarcode.getText()
									.toString().trim())) {
						ProductDTO dto = new ProductDTO();
						dto = ProductDAO.getInstance().getRecordsByProductID(
								DBHandler.getDBObj(Constants.READABLE),
								etxtBarcode.getText().toString().trim());
						appContext.setProductDTO(dto);
						ProductDetailDialog1 dialog = new ProductDetailDialog1(
								ProductFinalActivity.this);
						dialog.setOnDialogSaveListener(ProductFinalActivity.this);
						dialog.show();
					} else {
						CommonMethods.showcustomdialogbox(
								ProductFinalActivity.this, getResources()
										.getString(R.string.oops_errmsg),
								"No Product exists", null);

					}
					etxtBarcode.getText().clear();
					isProductExists = false;
				}
				return false;
			}
		});

	}

	protected boolean suppliercodevalidat(String string) {
		ProductDTO dto = ProductDAO.getInstance().getRecordsByProductID(
				DBHandler.getDBObj(Constants.READABLE), string);
		if (appContext.getSeletedSupplier().equals(dto.getSupplierId()))
			return true;
		else
			return false;
	}

	// gettig supplier data to DB
	private void getSupplierData() {

		suppdto = SupplierDAO.getInstance().getRecordsBySupplierID(
				DBHandler.getDBObj(Constants.READABLE),
				appContext.getSeletedSupplier());
		setTextToSupplierView();
	}

	private void loadUI() {
		btnEndSale.setText(getResources().getString(R.string.btn_add));
		btnCancelSale.setText(getResources().getString(R.string.cancel));

		selectedList = appContext.getSelectedProducts();
		salesAdapter = new SalesAdapter(this, selectedList, uiHandler,
				ConstantsEnum.DISH.code(), ConstantsEnum.DISH.code(), "",
				"CreateDish");
		lvSelectedProdu.setAdapter(salesAdapter);
		setValuesToPriceTag();
	}

	// Result of this method, invisible to the view
	private void setInvisibleView() {
		llAddClient = (LinearLayout) findViewById(R.id.ll_SalesAddCustomer);
		llDiscount = (LinearLayout) findViewById(R.id.ll_SalesDiscount);
		llInvoice = (LinearLayout) findViewById(R.id.ll_SalesInvoice);
		txtDiscount = (TextView) findViewById(R.id.txt_Discount);

		llAddClient.setVisibility(View.INVISIBLE);
		llDiscount.setVisibility(View.INVISIBLE);
		llInvoice.setVisibility(View.INVISIBLE);
		txtDiscount.setVisibility(View.INVISIBLE);
	}

	// This method using for Handler message to update the UI thread
	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.EDIT.code())
				updateList();
			else if (msg.arg1 == SalesCodes.DISCOUNT.code())
				addDiscount();
			else if (msg.arg1 == SalesCodes.DELETE.code())
				updateList();
			else if (msg.arg1 == SalesCodes.ADD_INVENTORY.code()) {
				CommonMethods.showProgressDialog(
						getString(R.string.please_wait),
						ProductFinalActivity.this);
				dateTime = Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM);
				String payments = (msg.obj).toString();
				updateProductData();
				updateInventoryData(payments);
				updateSupplierRecords(payments);
				CommonMethods.dismissProgressDialog();
				CommonMethods.openNewActivity(ProductFinalActivity.this,
						InventoryActivity.class);
				// inItUI();
			}
		}
	};

	// Result of this method, calculate Vat and subtotal
	private void setValuesToPriceTag() {
		int totalItems = 0;
		double subTotal = 0, vat = 0;

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

	// Result of this method, update supplier record
	protected void updateSupplierRecords(String payments) {

		String payAmount = payments.split(":")[0];
		String debtAmount = payments.split(":")[1];

		updateSupplierPayments(payAmount);
		Double amt = CommonMethods.getDoubleFormate(suppdto.getBalanceAmount())
				+ CommonMethods.getDoubleFormate(debtAmount);

		suppdto.setBalanceAmount(String.valueOf(amt));
		suppdto.setLastPaymentDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		if (SupplierDAO.getInstance().updateDebtAmount(
				DBHandler.getDBObj(Constants.WRITABLE), suppdto)) {
			CommonMethods.showCustomToast(this,
					getResources().getString(R.string.goods_received));
			appContext.setSeletedProducts(new ArrayList<DTO>());
			appContext.setProductDTO(null);
		}
		if (inventoryDTOforSup.size() > 0) {
			SynckSupplierandSupplierpayments(inventoryDTOforSup);
		} else {

		}

	}

	// Result of this method, update supplier payments
	private void updateSupplierPayments(String payAmount) {

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

	}

	// Result of this method, update inventory data
	protected void updateInventoryData(String payments) {
		invoiceNum = "";
		try {
			invoiceNum = payments.split(":")[2];
		} catch (Exception e) {
			invoiceNum = "";
		}
		barCodesList.clear();

		for (int i = 0; i < selectedList.size(); i++) {
			SelectedProddutsDTO selectedDTO = (SelectedProddutsDTO) selectedList
					.get(i);
			insertInventoryRecord(selectedDTO, invoiceNum);
		}
		makeJSONARRAY(barCodesList);
	}

	// Result of this method, to receive inventory to the product
	private void makeJSONARRAY(ArrayList<String> barCodesList2) {

		HashSet hs = new HashSet();
		hs.addAll(barCodesList2);
		barCodesList2.clear();
		barCodesList2.addAll(hs);

		List<InventoryDTO> inventoryDTO = new ArrayList<InventoryDTO>();
		for (int i = 0; i < barCodesList2.size(); i++) {
			List<DTO> dto = new ArrayList<DTO>();
			try {
				dto = InventoryDAO.getInstance().getRecordsWithValues(
						DBHandler.getDBObj(Constants.READABLE), "product_id",
						barCodesList2.get(i));
				inventoryDTO.add((InventoryDTO) dto.get(0));
			} catch (Exception e) {
			}

		}

		String resp = new AsynkTaskClass().callcentralserver("/inventory/receive", ""
						+ ServiceHandler.POST, makeJsonObject(inventoryDTO),
				ProductFinalActivity.this);
		if (resp != null) {
			if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {

				HashMap<String,Double> inventoryServer= new JSONStatus().data(resp);

				for(DTO dto: inventoryDTO){
					InventoryDTO inventoryDTOServer = (InventoryDTO) dto;
					inventoryDTOServer.setQuantityBalance(0.0);
					if (inventoryServer.containsKey(inventoryDTOServer.getProductId()))
						inventoryDTOServer.setQuantity(inventoryServer.get(inventoryDTOServer.getProductId()).toString());
					InventoryDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), inventoryDTOServer);
				}


				for (int i = 0; i < inventoryDTO.size(); i++) {
					InventoryDTO dto = InventoryDAO.getInstance()
							.getRecordByProductID(
									DBHandler.getDBObj(Constants.READABLE),
									inventoryDTO.get(i).getProductId());
					dto.setSyncStatus(1);
					InventoryDAO.getInstance().update(
							DBHandler.getDBObj(Constants.WRITABLE), dto);
				}

				List<DTO> inventoryHistorylist = InventoryHistoryDAO
						.getInstance().getRecordsWithValues(
								DBHandler.getDBObj(Constants.READABLE),
								"sync_status", "0");
				if (inventoryHistorylist.size() > 0) {

					new AsynkTaskClass()
							.callcentralserver(
									"/sync",
									"" + ServiceHandler.POST,
									new RequestFiels(ProductFinalActivity.this)
											.getInventoryHistoryTableData(inventoryHistorylist),
									ProductFinalActivity.this);
					if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
						new UpdateSynkStatus(inventoryHistorylist,
								ServiApplication.inventoryHistory);
					}
				}
				inventoryDTOforSup = inventoryDTO;
				sdto.setSyncStatus(1);
				SupplierDAO.getInstance().update(
						DBHandler.getDBObj(Constants.WRITABLE), sdto);
			}else{
				sdto.setSyncStatus(0);
				SupplierDAO.getInstance().update(
						DBHandler.getDBObj(Constants.WRITABLE), sdto);
			}
			

			List<DTO> products = new ArrayList<DTO>();
			for (int i = 0; i < barCodesList2.size(); i++) {
				ProductDTO products1 = ProductDAO.getInstance()
						.getRecordsByProductID(
								DBHandler.getDBObj(Constants.READABLE),
								barCodesList2.get(i));
				products.add(products1);
			}

			if (products.size() > 0) {

				new AsynkTaskClass().callcentralserver("/sync", ""
						+ ServiceHandler.POST, new RequestFiels(
						ProductFinalActivity.this)
						.getProductTableData(products),
						ProductFinalActivity.this);
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					new UpdateSynkStatus(products,
							ServiApplication.updateProducts);
				}
			}
		} else {
			for (int i = 0; i < barCodesList2.size(); i++) {
				ProductDTO products1 = ProductDAO.getInstance()
						.getRecordsByProductID(
								DBHandler.getDBObj(Constants.READABLE),
								barCodesList2.get(i));
				ProductDAO.getInstance().updateSynckDate(
						DBHandler.getDBObj(Constants.WRITABLE), products1);
			}
			suppdto.setSyncStatus(0);
			SupplierDAO.getInstance().updateDebtAmount(
					DBHandler.getDBObj(Constants.WRITABLE), suppdto);
			SupplierDAO.getInstance().update(
					DBHandler.getDBObj(Constants.WRITABLE), suppdto);

		}

	}

	// Result of this method, sync supplier_payments
	private void SynckSupplierandSupplierpayments(
			List<InventoryDTO> inventoryDTO) {
		String product_id = null;
		for (int i = 0; i < inventoryDTO.size(); i++) {
			product_id = inventoryDTO.get(i).getProductId();
		}

		if (NetworkConnectivity.netWorkAvailability(ProductFinalActivity.this)) {
			new SynckSup().execute(product_id);
		} else {
//			CommonMethods.showCustomToast(ProductFinalActivity.this,
//					getResources().getString(R.string.no_wifi_adhoc));

		}
	}

	// Result of this method, update inventory history
	private void insertDataIntoInvHistory(SelectedProddutsDTO dto,
			String invoiceNum) {
		InventoryDTO indto = InventoryDAO.getInstance().getInventoryValue(
				DBHandler.getDBObj(Constants.WRITABLE), dto.getBarcode());
		List<DTO> list = new ArrayList<DTO>();
		InventoryHistoryDTO hDTO = new InventoryHistoryDTO();
		hDTO.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		hDTO.setProductId(dto.getBarcode());
		hDTO.setQuantity(dto.getQuantity());
		hDTO.setPrice(CommonMethods.getDoubleFormate(dto.getPrice()));
		hDTO.setUom(dto.getUnits());
		hDTO.setSyncStatus(Constants.FALSE);
		if (invoiceNum.length() > 0||""!=invoiceNum) {
			hDTO.setInvoiceNum(invoiceNum);
		} else {
			hDTO.setInvoiceNum(indto.getInventoryId());
		}
		list.add(hDTO);
		InventoryHistoryDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), list);
	}

	public void insertInventoryRecord(SelectedProddutsDTO selectedDTO,
			String invoiceNum) {
		List<DTO> inventoryList = new ArrayList<DTO>();

		inventoryList = InventoryDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));

		if (inventoryList.size() == 0) {
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
				inDTO.setUom(selectedDTO.getUnits());
				inDTO.setQuantityBalance(CommonMethods.getBalanceQuantity(selectedDTO.getBarcode(),selectedDTO
						.getQuantity()));
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
			insertDataIntoInvHistory(selectedDTO, invoiceNum);
		}
	}

	// Result of this method, update product data
	protected void updateProductData() {

		selectedList = appContext.getSelectedProducts();

		for (int i = 0; i < selectedList.size(); i++) {
			SelectedProddutsDTO selectedDTO = (SelectedProddutsDTO) selectedList
					.get(i);
			ProductDAO.getInstance().updateProducts(
					DBHandler.getDBObj(Constants.WRITABLE), selectedDTO);
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
			Intent intent = new Intent(ProductFinalActivity.this,
					ProductsActivity.class);
			startActivityForResult(intent, PRODUCT_REQUEST_CODE);
			appContext.setScreen(SCREEN_NAME);
			appContext.setSeletedSupplier(suppdto.getCedula());

			break;

		case R.id.btn_SalesCalEnter:

			if (validateBarCode()) {
				ProductDTO dto = new ProductDTO();
				dto = ProductDAO.getInstance().getRecordsByProductID(
						DBHandler.getDBObj(Constants.READABLE),
						etxtBarcode.getText().toString().trim());
				appContext.setProductDTO(dto);

				ProductDetailDialog1 dialog = new ProductDetailDialog1(this);
				dialog.setOnDialogSaveListener(this);
				dialog.show();
			} else {
				CommonMethods.displayAlert(true,
						getResources().getString(R.string.alert),
						getResources().getString(R.string.noproduct_exist),
						getResources().getString(R.string.ok), "",
						ProductFinalActivity.this, null, false);

			}

			break;

		case R.id.btn_SaleEnd:
			if (appContext.getTotalAmount() > 0) {
				double debt = 0;
				// if (appContext.getClientDTO() != null)
				debt = CommonMethods.getDoubleFormate(suppdto
						.getBalanceAmount());

				SaleFinishDialog1 finishDialog = new SaleFinishDialog1(
						ProductFinalActivity.this, appContext.getTotalAmount(),
						debt, ConstantsEnum.ORDERS.code(), uiHandler);
				finishDialog.show();
				// appContext.setSeletedProducts(new ArrayList<DTO>());
			} else {
				CommonMethods.showCustomToast(ProductFinalActivity.this,
						getResources().getString(R.string.invalid_qty));

			}

			break;
		case R.id.btn_SaleCancel:
			cancelDialog = CommonMethods.displayAlert(false, getResources()
					.getString(R.string.cancel_sale),
					getResources().getString(R.string.sales_cancel_msg),
					getResources().getString(R.string.yes), getResources()
							.getString(R.string.no), ProductFinalActivity.this,
					ProductFinalActivity.this, true);
			break;

		case R.id.btn_SaleAddCustomer:
			// appContext.pushActivity(intent);
			// Intent intent = new Intent(this, AddClientActivity.class);
			// intent.putExtra(Constants.CLIENT_MODE, Constants.ADD_CLIENT);
			// startActivity(intent);
			break;

		case R.id.btn_SaleDiscount:

			break;

		case R.id.btn_SaleInvoiceAdj:

			break;

		case R.id.btn_SalesBarCode:

			break;

		default:
			break;
		}
	}

	// Result of this method, barcode validations
	private boolean validateBarCode() {
		barCode = etxtBarcode.getText().toString().trim();

		if (barCode.length() != 0) {
			int prodCount = ProductDAO.getInstance().isProductExists(
					DBHandler.getDBObj(Constants.READABLE), barCode);

			if (prodCount != 0)
				isProductExists = true;
			else
				isProductExists = false;
		}

		return isProductExists;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PRODUCT_REQUEST_CODE) {
			ProductDetailDialog1 dialog = new ProductDetailDialog1(this);
			dialog.setOnDialogSaveListener(this);
			dialog.show();
		}
	}

	private void removeLastDigit() {
		if (etxtBarcode.getText().toString().trim().length() == 1)
			etxtBarcode.getText().clear();
		else {
			String barcode = etxtBarcode.getText().toString().trim();
			barcode = barcode.substring(0, barcode.length() - 1);
			etxtBarcode.setText(barcode);
		}
		etxtBarcode.setSelection(etxtBarcode.getText().length());
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

	// Result of this method, add to discount
	private void addDiscount() {
		// txtDiscount.setText(getResources().getString(R.string.sales_discount)+
		// " " + appContext.getDiscount());
		txtTotalAmount.setText(getResources().getString(
				R.string.sales_total_amount)
				+ " " + appContext.getTotalAmount());
	}

	private void setTextToSupplierView() {
		txtClientName.setText(getResources().getString(R.string.suppliers)
				+ " : " + suppdto.getName());
		txtClientPhone.setText(getResources().getString(R.string.sales_phone)
				+ " " + suppdto.getTelephone());
		txtClientId.setText(getResources().getString(R.string.sales_id) + " "
				+ suppdto.getCedula());
		txtClientCredit.setText(getResources().getString(R.string.sales_credit)
				+ " "
				+ CommonMethods.getNumSeparator(CommonMethods
						.getDoubleFormate(suppdto.getBalanceAmount())));
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		if (dialog == cancelDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				appContext.setSeletedProducts(new ArrayList<DTO>());
				ServiApplication.orederReceivedata.clear();
				inItUI();
			}
		}
	}

	@Override
	public void onSaveClick() {
		loadUI();
	}

	// This method using for inventory receive product that time data passing
	// to the json objects
	private String makeJsonObject(List<InventoryDTO> inventoryDTO) {
		ServiApplication.userName = sharedpreferences
				.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code",
				"");

		JSONObject rootJSONOBJ = new JSONObject();
		JSONArray jsonarray = new JSONArray();
		String invoice_num = null;
		try {
			for (int i = 0; i < inventoryDTO.size(); i++) {
				JSONObject jsonobj = new JSONObject();
				if ("" != ServiApplication.store_id
						&& null != inventoryDTO.get(i).getProductId()) {
					invoice_num = inventoryDTO.get(i).getInventoryId();
					jsonobj.put("quantity", inventoryDTO.get(i).getQuantityBalance());
					jsonobj.put("store_code", ServiApplication.store_id);
					jsonobj.put("barcode", inventoryDTO.get(i).getProductId());
					jsonobj.put("inventory_id", Long.parseLong(inventoryDTO
							.get(i).getInventoryId()));
					jsonobj.put("uom", inventoryDTO.get(i).getUom());
					jsonarray.put(jsonobj);
				}
			}
			rootJSONOBJ.put("invoice_no", invoice_num);
			rootJSONOBJ.put("inventory_recs", jsonarray);
			return rootJSONOBJ.toString();

		} catch (Exception e) {
			return rootJSONOBJ.toString();
		}
	}

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
				new ServiceHandler(ProductFinalActivity.this).makeServiceCall(
						ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(ProductFinalActivity.this)
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

				if (new ServiceHandler(ProductFinalActivity.this)
						.makeServiceCall(
								ServiApplication.URL + "/sync",
								ServiceHandler.POST,
								new RequestFiels(ProductFinalActivity.this)
										.getSupplierPayTableData(supplierPaymentsDetails)) != null) {
					if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
						new UpdateSynkStatus(supplierPaymentsDetails,
								ServiApplication.supplierpayments);
					}
				}
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

	@Override
	protected void onResume() {
		super.onResume();
		if (CommonMethods.progressDialog != null)
			CommonMethods.dismissProgressDialog();
	}

	// Result of this method,invetory table inserting data to Transaccion table
	@SuppressWarnings("unused")
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
			if (CommonMethods.getInternetSpeed(ProductFinalActivity.this) > ServiApplication.local_data_speed) {
				Intent intent = new Intent(ProductFinalActivity.this,
						TransaccionBoxService.class);
				startService(intent);
			} else {
				ServiApplication.connectionTimeOutState = false;
			}
		}
	}

	private void orderReceive() {

		for (DTO dto : ServiApplication.orederReceivedata) {
			String vat = "0.0";
			OrderReceiveDTO Odto = (OrderReceiveDTO) dto;
			ProductDTO LocalproductDto = ProductDAO.getInstance()
					.getRecordsByProductID(
							DBHandler.getDBObj(Constants.READABLE),
							Odto.getBarcode());
			if (LocalproductDto.getVat().equals(""))
				vat = "0.0";

			SelectedProddutsDTO Sdto = new SelectedProddutsDTO();
			Sdto.setIdProduct(LocalproductDto.getProductId());
			Sdto.setBarcode(LocalproductDto.getBarcode());
			Sdto.setName(LocalproductDto.getName());
			Sdto.setPrice(LocalproductDto.getPurchasePrice());
			Sdto.setQuantity(Odto.getCount());
			Sdto.setVat(vat);
			Sdto.setExpiry_date("");
			if (LocalproductDto.getUom() != null)
				Sdto.setUnits(LocalproductDto.getUom().toString());
			Sdto.setSellPrice(LocalproductDto.getSellingPrice());
			boolean isExists = false;

			List<DTO> selectedproducts = appContext.getSelectedProducts();

			if (selectedproducts == null) {
				selectedproducts = new ArrayList<DTO>();
				appContext.setSeletedProducts(selectedproducts);
			}
			for (int i = 0; i < selectedproducts.size(); i++) {
				SelectedProddutsDTO prodDTO = (SelectedProddutsDTO) selectedproducts
						.get(i);
				String prodID = prodDTO.getIdProduct();
				if (prodID.equals(Sdto.getIdProduct())) {
					isExists = true;
					break;
				}
			}
			if (!isExists) {
				selectedproducts.add(Sdto);
			}
		}
		ServiApplication.orederReceivedata.clear();
		appContext.setProductDTO(null);
	}
}
