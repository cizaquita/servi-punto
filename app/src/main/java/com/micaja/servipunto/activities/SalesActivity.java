package com.micaja.servipunto.activities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.citizen.sdk.ESCPOSConst;
import com.citizen.sdk.ESCPOSPrinter;
import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.SalesAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ClientDAO;
import com.micaja.servipunto.database.dao.ClientPaymentDAO;
import com.micaja.servipunto.database.dao.DeliveryDAO;
import com.micaja.servipunto.database.dao.DeliveryPaymentDAO;
import com.micaja.servipunto.database.dao.DishesDAO;
import com.micaja.servipunto.database.dao.GroupDAO;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.InventoryHistoryDAO;
import com.micaja.servipunto.database.dao.InvoiceDetailsDAO;
import com.micaja.servipunto.database.dao.LineDAO;
import com.micaja.servipunto.database.dao.MenuInventoryDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dao.SalesDAO;
import com.micaja.servipunto.database.dao.SalesDetailDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dao.TransaccionBoxDAO;
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.AddClientScreenData;
import com.micaja.servipunto.database.dto.ClientDTO;
import com.micaja.servipunto.database.dto.ClientPaymentsDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DeliveryDTO;
import com.micaja.servipunto.database.dto.DeliveryPaymentsDTO;
import com.micaja.servipunto.database.dto.GroupDTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.InventoryHistoryDTO;
import com.micaja.servipunto.database.dto.LineDTO;
import com.micaja.servipunto.database.dto.MenuInventoryDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SalesDTO;
import com.micaja.servipunto.database.dto.SalesDetailDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.database.dto.TransaccionBoxDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.DiscountDialog;
import com.micaja.servipunto.dialog.InvoiceAdjustDialog;
import com.micaja.servipunto.dialog.OnDialogSaveClickListener1;
import com.micaja.servipunto.dialog.SaleFinishDeliveryDialog;
import com.micaja.servipunto.dialog.SaleFinishDialog1;
import com.micaja.servipunto.print.PRTSDKApp;
import com.micaja.servipunto.service.SalesDetailsUpdateService;
import com.micaja.servipunto.service.SupplierUpdateService;
import com.micaja.servipunto.service.TransaccionBoxService;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;
import com.micaja.servipunto.utils.SalesCodes;
import com.micaja.servipunto.utils.SalesPrint;
import com.micaja.servipunto.utils.UpdateSynkStatus;
import com.micaja.servipunto.utils.Utils;
import com.micaja.servipunto.dialog.newSimpleProductDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


public class SalesActivity extends BaseActivity
		implements OnClickListener, android.content.DialogInterface.OnClickListener, OnDialogSaveClickListener1 {

	private double subTotal = 0;
	private boolean isOnBackPressed = false;
	private Button btnZero, btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnNine,
			btnBackClear, btnAllClear, btnSearch, btnEnter, btnEndSale, btnCancelSale, btnNewClient, btnDiscount,
			btnInvoiceAdj, btnBarcode, btnUserSel;
	private EditText etxtBarcode;
	private TextView txtClientName, txtClientPhone, txtClientId, txtClientCredit;
	private TextView txtTotalItems, txtSubTotal, txtVat, txtDiscount, txtTotalAmount;
	private ListView lvSelectedProdu;
	private SalesAdapter salesAdapter;
	private AlertDialog cancelDialog, alertDialog;
	private String  fecha_inicial, fecha_final;

	private boolean isProductExists, isInvoiceAdj = false;

	private Spinner spnDelivery;
	private ArrayAdapter<String> deliveryAdapter;
	private boolean getCentralflage = false;

	//ServiApplication appContext;
	//Intent intent;

	private String invoiceNo, invoice_adjnum = null, print_total_amount, EPSON_sale_print_hedder_12;
	private ServiApplication appContext;
	private Intent intent;

	public SharedPreferences sharedpreferences;
	private UserDetailsDTO udto;
	public Dialog dialog;
	public SalesDTO saledto;

	public AlertDialog alertinvoice;
	//private String numbesrs = "";


	String numbesrs = "";
	public int transDelivery;
	public static String preciocompra;
	public static String precioventa;
	public static String nombre;
	public static ProductDTO productDto = new ProductDTO();
	private String name, barcode, purprice, sellprice, vat,
			supplier, // quantity,
			group, line, uom, subgroup, min_count_in_inventory,
			expiry_date = "", sup_id, discount, modifieddate;
	private boolean isValid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (ServiApplication) getApplicationContext();

		intent = new Intent(this, SalesActivity.class);
		sharedpreferences = getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		String[] days = sharedpreferences.getString("STORE_CONTACT_NUMBER", "").split(",");
		for (int i = 0; i < days.length; i++) {
			numbesrs = days[i];
		}
		udto = UserDetailsDAO.getInstance().getRecordsuser_name(DBHandler.getDBObj(Constants.READABLE),
				sharedpreferences.getString("user_name", ""));
		ServiApplication.shale_menu = true;
		ServiApplication.userName = udto.getName();
		fecha_inicial = Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM);
		inItUI();
	}

	private void inItUI() {
		setContentView(R.layout.sales);
		ServiApplication.allprinters_flage = false;
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
		spnDelivery = (Spinner) findViewById(R.id.spn_delivery);

		etxtBarcode = (EditText) findViewById(R.id.etxt_SalesBarcode);

		// etxtBarcode.setInputType(InputType.TYPE_NULL);

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
		// insertMenusData();

		etxtBarcode.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				etxtBarcode.setSingleLine(false);
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					etxtBarcode.setSingleLine(true);
					if (validateBarCode()) {

						SelectedProddutsDTO dto = new SelectedProddutsDTO();
						dto = InventoryDAO.getInstance().getProductBySearchID(DBHandler.getDBObj(Constants.READABLE),
								etxtBarcode.getText().toString().trim());
						List<DTO> selectedproducts = appContext.getSelectedProducts();
						if (selectedproducts == null) {
							selectedproducts = new ArrayList<DTO>();
							appContext.setSeletedProducts(selectedproducts);

						}
						boolean isExists = false;
						for (int i = 0; i < selectedproducts.size(); i++) {
							SelectedProddutsDTO prodDTO = (SelectedProddutsDTO) selectedproducts.get(i);
							String prodID = prodDTO.getIdProduct();
							if (prodID.equals(dto.getIdProduct())) {

								int qty = 0;
								if (!prodDTO.getQuantity().equals(""))
									qty = Integer.parseInt(prodDTO.getQuantity()) + 1;
								dto.setQuantity(String.valueOf(qty));
								dto.setPrice(dto.getSellPrice());
								selectedproducts.set(i, dto);

								isExists = true;
								break;
							}
						}
						if (!isExists) {
							dto.setQuantity("1");
							dto.setPrice(dto.getSellPrice());
							selectedproducts.add(dto);
						}
						loadUI();

					} else {
						if (etxtBarcode.getText().length() > 5) {
							getDataFromCentralserver(etxtBarcode.getText()
									.toString().trim());
						} else {
							CommonMethods.displayAlert(true, getResources().getString(R.string.alert),
									getResources().getString(R.string.noproduct_exist),
									getResources().getString(R.string.ok), "", SalesActivity.this, null, false);
						}
					}
					etxtBarcode.getText().clear();
					isProductExists = false;
					return true;
				}
				return false;
			}

		});
		loadUI();
		setTextToClientView();
		ServiApplication.setUiHandler(uiHandler);
	}

	@SuppressWarnings("unchecked")
	private void loadUI() {


		try {
			CommonMethods.dismissProgressDialog();
		} catch (Exception e) {
			// TODO: handle exception
		}

		etxtBarcode.getText().clear();
		etxtBarcode.setCursorVisible(true);
		etxtBarcode.setFocusable(true);
		etxtBarcode.setFocusableInTouchMode(true);
		etxtBarcode.requestFocus();

		btnEndSale.setText(getResources().getString(R.string.btn_add));
		btnCancelSale.setText(getResources().getString(R.string.cancel));

		if (appContext.getClientDTO() == null)
			txtClientName.setText(getResources().getString(R.string.sales_custome) + " "
					+ getResources().getString(R.string.cleint_occasional));

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		salesAdapter = new SalesAdapter(this, appContext.getSelectedProducts(), uiHandler, ConstantsEnum.SALES.code(),
				ConstantsEnum.SALES.code());
		lvSelectedProdu.setAdapter(salesAdapter);
		setValuesToPriceTag();
		populateSpinnerItems();
	}

	private void populateSpinnerItems() {

		deliveryAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getDelivery());
		deliveryAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnDelivery.setAdapter(deliveryAdapter);
	}

	private List<String> getDelivery() {
		List<DTO> deliveryList = DeliveryDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		List<String> list = new ArrayList<String>();
		Hashtable<String,String> deliveryTable = new Hashtable<String, String>();
		list.add(getResources().getString(R.string.select));

		for (DTO dto : deliveryList) {
			DeliveryDTO suppDto = (DeliveryDTO) dto;
			list.add(suppDto.getName() + " - " + suppDto.getCedula());
			deliveryTable.put(suppDto.getName() + " - " + suppDto.getCedula(),
					suppDto.getCedula());
		}

		return list;
	}

	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.EDIT.code())
				updateList();
			else if (msg.arg1 == SalesCodes.DISCOUNT.code())
				setValuesToPriceTag();
				// addDiscount();
			else if (msg.arg1 == SalesCodes.DELETE.code())
				updateList();
			else if (msg.arg1 == SalesCodes.INVOICE_ADJ.code())
				sendReqForInvoice(msg.obj.toString());
			else if (msg.arg1 == SalesCodes.INVOICE_ADJ_CANCEL.code())
				isInvoiceAdj = false;
			else if (msg.arg1 == SalesCodes.CLIENT_SALE_END.code()) {
				transDelivery = 0;
				String payments = (msg.obj).toString();
				insertAndUpdate(payments,0);

			}
			else if (msg.arg1 == SalesCodes.LEND_TO_CUSTOMER.code()) {
				transDelivery = 1;
				String payments = (msg.obj).toString();
				insertAndUpdate(payments,1);

			}
			else if (msg.arg1 == SalesCodes.LEND_TO_DELIVERY.code()) {
				transDelivery = 2;
				String payments = (msg.obj).toString();
				insertAndUpdate(payments,2);


			}
		}
	};

	/*
	@Params
	@typeTrans: Maneja el tipo de transacción que proviene de los UI, de venta
				0:No Tiene Delivery, pero si Cliente
				 1: Tiene Delivery, pero se carga al cliente.
				 2. Tiene Delivery, y se carga la deuda al Delivery
	 */

	private void insertAndUpdate(String payments,int typeTrans) {
		if (spnDelivery.getSelectedItemPosition() !=0) {
			String deliveryDocument = spnDelivery.getSelectedItem().toString().split("-")[1].trim();
			DeliveryDTO deliveryDTO = DeliveryDAO.getInstance().getRecordsByDeliveryCedula(DBHandler.getDBObj(Constants.READABLE), deliveryDocument);
			appContext.setDeliveryDTO(deliveryDTO);
		}
		if (!isInvoiceAdj)
			Utils.saleID = String.valueOf(Dates.getSysDateinMilliSeconds());
		/*
		 * Sales --> Insert Sales Details --> Insert Client Payments --> Insert
		 * 
		 * Inventory --> Update Menu Inventory --> Update clients --> Update
		 */


		if (isInvoiceAdj) {
			updateInvoiceInventory();
			deleteSaleRecords();
			deleteClientPayments();
		}
		if (typeTrans == 0)
			updateClientPayments(payments);
		else if (typeTrans == 1){
			updateClientPayments(payments);
			String paymentsTemp = 0.0+":"+CommonMethods.getDoubleFormate(payments.split(":")[0]);
			updateDeliveryPayments(paymentsTemp);
		}
		else if (typeTrans == 2) {
			updateDeliveryPayments(payments);
			String paymentsTemp = (CommonMethods.getDoubleFormate(payments.split(":")[0])+CommonMethods.getDoubleFormate(payments.split(":")[1]))+":"+0.0;
			updateClientPayments(paymentsTemp);
		}
		updateMenuAndDishInventory();
		validateAndInsert(payments);
		appContext.setDiscount(0);
	}

	private void updateClientPayments(String payments) {


		String payAmount = payments.split(":")[0];
		String debtAmount = payments.split(":")[1];

		List<DTO> clientList = new ArrayList<DTO>();
		ClientPaymentsDTO dto = new ClientPaymentsDTO();

		dto.setAmountPaid(payAmount);
		dto.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		dto.setIncomeType(ConstantsEnum.INCOME_TYPE_SALE.code());
		if (transDelivery == 2 || transDelivery == 1)
			dto.setPaymentType(ConstantsEnum.PAY_DELIVERY.code());
		else
			dto.setPaymentType(ConstantsEnum.PAYMENT_TYPE_CASH.code());
		dto.setSaleId(String.valueOf(Utils.saleID));
		dto.setSyncStatus(0);
		if (!txtClientName.getText().toString().trim().equals(getResources().getString(R.string.sales_custome) + " "
				+ getResources().getString(R.string.cleint_occasional))) {
			dto.setClientId(appContext.getClientDTO().getCedula());
		}
		clientList.add(dto);
		ClientPaymentDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), clientList);

		if (!txtClientName.getText().toString().trim().equals(getResources().getString(R.string.sales_custome) + " "
				+ getResources().getString(R.string.cleint_occasional)))
			updateClientData(debtAmount);

	}

	private void updateClientData(String debtAmount) {
		double amt = 0;
		ClientDTO clientdto = appContext.getClientDTO();

		if (isInvoiceAdj) {
			SalesDTO salesDTO = appContext.getSalesDTO();

			double balaFromSale = CommonMethods.getDoubleFormate(salesDTO.getNetAmount())
					- CommonMethods.getDoubleFormate(salesDTO.getAmountPaid());
			double realClientDebt = CommonMethods.getDoubleFormate(clientdto.getBalanceAmount()) - balaFromSale;
			amt = realClientDebt + CommonMethods.getDoubleFormate(debtAmount);
		} else
			amt = CommonMethods.getDoubleFormate(clientdto.getBalanceAmount())
					+ CommonMethods.getDoubleFormate(debtAmount);

		ClientDTO dto = new ClientDTO();

		dto.setCedula(clientdto.getCedula());
		dto.setBalanceAmount(String.valueOf(amt));
		dto.setLastPaymentDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		ClientDAO.getInstance().updateClientDebtAmount(DBHandler.getDBObj(Constants.WRITABLE), dto);
	}

	private void updateDeliveryPayments(String payments) {

		String payAmount = payments.split(":")[0];
		String debtAmount = payments.split(":")[1];
		DeliveryPaymentsDTO dto = new DeliveryPaymentsDTO();
		dto.setAmountPaid(payAmount);
		dto.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		dto.setIncomeType(ConstantsEnum.INCOME_TYPE_SALE.code());
		dto.setPaymentType(ConstantsEnum.PAYMENT_TYPE_CASH.code());
		dto.setSaleId(String.valueOf(Utils.saleID));
		dto.setSyncStatus(0);
		if (spnDelivery.getSelectedItemPosition()!=0) {
			updateDeliveryData(debtAmount);
			dto.setDelieryId(appContext.getDeliveryDTO().getCedula());
		}
		List<DTO> deliveryList = new ArrayList<DTO>();
		deliveryList.add(dto);
		DeliveryPaymentDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), deliveryList);

	}

	private void updateDeliveryData(String debtAmount) {
		double amt = 0;
		DeliveryDTO deliveryDTO = appContext.getDeliveryDTO();

		if (isInvoiceAdj) {
			SalesDTO salesDTO = appContext.getSalesDTO();
			double balanceFromSale = CommonMethods.getDoubleFormate(salesDTO.getNetAmount())
					- CommonMethods.getDoubleFormate(salesDTO.getAmountPaid());
			double realDeliveryDebt = CommonMethods.getDoubleFormate(deliveryDTO.getBalanceAmount()) - balanceFromSale;
			amt = realDeliveryDebt + CommonMethods.getDoubleFormate(debtAmount);
		} else
			amt = CommonMethods.getDoubleFormate(deliveryDTO.getBalanceAmount())
					+ CommonMethods.getDoubleFormate(debtAmount);

		DeliveryDTO dto = new DeliveryDTO();
		dto.setCedula(deliveryDTO.getCedula());
		dto.setBalanceAmount(String.valueOf(amt));
		dto.setLastPaymentDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		DeliveryDAO.getInstance().updateDeliveryDebtAmount(DBHandler.getDBObj(Constants.WRITABLE), dto);
	}

	private void updateMenuAndDishInventory() {
		for (int i = 0; i < appContext.getSelectedProducts().size(); i++) {
			SelectedProddutsDTO selectedDTO = (SelectedProddutsDTO) appContext.getSelectedProducts().get(i);
			if (selectedDTO.getProductType().equals(ConstantsEnum.PRODUCT_DISH.code())) {
				MenuInventoryDTO inDTO = MenuInventoryDAO.getInstance()
						.getRecordByDishID(DBHandler.getDBObj(Constants.READABLE), selectedDTO.getIdProduct());

				String qty = "";
				if (inDTO.getCount() != null && selectedDTO.getQuantity() != null)
					qty = String.valueOf(CommonMethods.getDoubleFormate(inDTO.getCount())
							- CommonMethods.getDoubleFormate(selectedDTO.getQuantity()));
				inDTO.setDishId(selectedDTO.getIdProduct());
				inDTO.setCount(qty);


				inDTO.setSyncStatus(0);
				MenuInventoryDAO.getInstance().updateMenuInventoryData(DBHandler.getDBObj(Constants.WRITABLE), inDTO);
				// MenuInventorySynk(inDTO);
			} else {
				InventoryDTO inDTO = InventoryDAO.getInstance()
						.getRecordByProductID(DBHandler.getDBObj(Constants.READABLE), selectedDTO.getIdProduct());
				String qty = "";
				if (inDTO.getQuantity() != null && selectedDTO.getQuantity() != null)
					qty = CommonMethods.UOMConversions(inDTO.getQuantity(), inDTO.getUom(), selectedDTO.getQuantity(),
							selectedDTO.getUnits(), "1", "");

				inDTO.setProductId(selectedDTO.getIdProduct());

				/*
				Harold: Determina si un producto proviene del Precargue para que sea tenido en cuenta
				en la Próxima Sincronización
				 */
				if (inDTO.getQuantity().equals("0"))
					updateNewProduct(selectedDTO);
				inDTO.setQuantity(qty);
				inDTO.setQuantityBalance(CommonMethods.getBalanceQuantity(selectedDTO.getBarcode(),"-"+selectedDTO.getQuantity()));
				inDTO.setUom(selectedDTO.getUnits());
				inDTO.setSyncStatus(0);
				InventoryDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), inDTO);

				List<DTO> list = new ArrayList<DTO>();
				InventoryHistoryDTO hDTO = new InventoryHistoryDTO();
				ProductDTO productDto = ProductDAO.getInstance()
						.getRecordsByProductID(DBHandler.getDBObj(Constants.READABLE), selectedDTO.getIdProduct());

				hDTO.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
				hDTO.setProductId(selectedDTO.getIdProduct());
				hDTO.setQuantity(qty);
				hDTO.setPrice(CommonMethods.getDoubleFormate(productDto.getSellingPrice()));
				hDTO.setUom(selectedDTO.getUnits());
				hDTO.setSyncStatus(Constants.FALSE);
				hDTO.setInvoiceNum(inDTO.getInventoryId());
				list.add(hDTO);
				InventoryHistoryDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), list);


			}
		}
	}

	private void validateAndInsert(String Payments) {
		if (SalesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), getSalesList(Payments))) {
			getCallTransaccionBoxService(ServiApplication.Sales_M_name, ServiApplication.Sales_TipoTrans,
					ServiApplication.Sales_PaymentType);
			if (SalesDetailDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
					getSalesDetailsList(Payments))) {
				if (synktocentralserver()) {
					try {
						CommonMethods.showCustomToast(SalesActivity.this,
								getResources().getString(R.string.sales_succ_msg));
					}catch (Exception e){
						e.printStackTrace();
					}
					appContext.setSeletedProducts(new ArrayList<DTO>());
					appContext.setClientDTO(null);
					appContext.setDeliveryDTO(null);
					appContext.setSalesDTO(null);
					Utils.setInvoiceList(null);
					invoiceNo = Utils.saleID;
					isInvoiceAdj = false;
					InvoiceDetailsDAO.getInstance().deleteAllRecords(DBHandler.getDBObj(Constants.WRITABLE));
					callInvoiceMethodDialog(Utils.saleID);
					Utils.saleID = "";
					//
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void callInvoiceMethodDialog(String saleID) {




		try {
			alertDialog = CommonMethods.displayAlert(false, getResources().getString(R.string.confirm), getResources().getString(R.string.msj_print_invoice),
					getResources().getString(R.string.yes), getResources().getString(R.string.no), SalesActivity.this, SalesActivity.this, true);
			//Se comenta la siguiente linea a petición del requerimiento 4.3.6.2 B
			//callPrintMethod(invoiceNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		appContext.setSeletedProducts(new ArrayList<DTO>());
		inItUI();

	}

	private void callPrintMethod(String saleid) throws UnsupportedEncodingException, NotFoundException {

		double subTotal, vat;

		List<DTO> list = SalesDetailDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
				"sale_id", saleid);
		for (DTO dto : list) {
			SalesDetailDTO s_dto = (SalesDetailDTO) dto;
		}
		@SuppressWarnings("static-access")
		String print_flage = "  " + getResources().getString(R.string.app_name) + "\n " + "        "
				+ getResources().getString(R.string.sale_print_hedder_2) + "  "
				+ new Dates().serverdateformate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM)) + "\n " + "         "
				+ getResources().getString(R.string.sale_print_hedder_3) + "  " + sharedpreferences.getString("store_name", "") + "\n  "
				+ "            " + getResources().getString(R.string.sale_print_hedder_4) + "  " + udto.getCedulaDocument()//udto.getNitShopId()
				+ "\n"
				//+ "       " 
				+ //getResources().getString(R.string.sale_print_hedder_5) + "  " + sharedpreferences.getString("telephone", "") + 
//				"\n"+
				getResources().getString(R.string.sale_print_line) + "\n" + "             " + saleid + "\n"
				+ getResources().getString(R.string.sale_print_line)
				+ getResources().getString(R.string.sale_print_hedder_9) + "       "
				+ getResources().getString(R.string.sale_print_hedder_10) + "       "
				+ getResources().getString(R.string.sale_print_hedder_11);

		String print_flage_eps = "             " + getResources().getString(R.string.sale_print_hedder_2) + ":  "
				+ Dates.getSysDate(Dates.DD_MM_YYYY_HH_MM) + "\n " + "        "
				+ getResources().getString(R.string.sale_print_hedder_3) + "  " + sharedpreferences.getString("store_name", "") + "\n  "
				+ "                    " + getResources().getString(R.string.sale_print_hedder_4) + "  "
				+ udto.getCedulaDocument()
				+ "\n"
				//+ "               "//udto.getNitShopId()
				+ //getResources().getString(R.string.sale_print_hedder_5) + "  " + sharedpreferences.getString("telephone", "") +
				//"\n"+
				getResources().getString(R.string.sale_print_line1) + "\n" + "                   " + saleid + "\n"
				+ getResources().getString(R.string.sale_print_line1)
				+ getResources().getString(R.string.sale_print_hedder_9) + "            "
				+ getResources().getString(R.string.sale_print_hedder_10) + "     "
				+ getResources().getString(R.string.sale_print_hedder_11);
		String product_info = "", product_info_eps = "", product_total_info = "", product_total_info_epson = "";

		for (int i = 0; i < list.size(); i++) {
			SalesDetailDTO dto = (SalesDetailDTO) list.get(i);
			String p_name;
			System.out.println(dto.getProductId());

			String price = "0.0";

			try {
				price = " " + Double.parseDouble(dto.getCount()) * Double.parseDouble(dto.getPrice());
			} catch (Exception e) {
				price = dto.getPrice();
			}


			if (dto.getProductId().length() > 0) {
				ProductDTO productDto = ProductDAO.getInstance()
						.getRecordsByProductID(DBHandler.getDBObj(Constants.READABLE), dto.getProductId());
				if (productDto.getName().length() > 20) {
					p_name = productDto.getName().substring(0, 20);
				} else {
					int ii = productDto.getName().length();
					p_name = productDto.getName();
					for (int j = 0; j < 20 - ii; j++) {
						p_name = p_name + " ";
					}
				}

				product_info = product_info + p_name + "     " + dto.getCount() + "     " + price + "\n";
				product_info_eps = product_info_eps + p_name + "        " + dto.getCount() + "         "
						+ price + "\n";
			} else {
				ProductDTO productDto = ProductDAO.getInstance()
						.getRecordsByProductID(DBHandler.getDBObj(Constants.READABLE), dto.getDishId());
				product_info = product_info + productDto.getName() + "     " + dto.getCount() + "     " + price
						+ "\n";

				product_info_eps = product_info_eps + productDto.getName() + "        " + dto.getCount() + "       "
						+ price + "\n";
			}
		}

		product_total_info = product_total_info + "";
		ServiApplication.print_flage = print_flage + "\n" + getResources().getString(R.string.sale_print_line) + "\n"
				+ product_info + "\n" + getResources().getString(R.string.sale_print_line) + "\n" + print_total_amount;

		product_total_info_epson = product_total_info_epson + "";
		ServiApplication.print_flage_eps = print_flage_eps + "\n" + getResources().getString(R.string.sale_print_line1)
				+ "\n" + product_info_eps + "\n" + getResources().getString(R.string.sale_print_line1) + "\n"
				+ EPSON_sale_print_hedder_12;
		ServiApplication.print_flage = ServiApplication.print_flage_eps;
		System.out.println(ServiApplication.print_flage_eps);
		ServiApplication.print_products = product_info;

		if (new SalesPrint(SalesActivity.this, ServiApplication.print_flage_eps).print()) {

		} else {
			int result;
			ESCPOSPrinter printer = new ESCPOSPrinter();
			UsbDevice usbdevice = null;
			printer.setContext(SalesActivity.this);
			result = printer.connect(ESCPOSConst.CMP_PORT_USB, usbdevice);
			if (ESCPOSConst.CMP_SUCCESS == result) {
				Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.print_logo);
				printer.printBitmap(icon, ESCPOSConst.CMP_ALIGNMENT_CENTER);
				printer.printText("\n\n" + CommonMethods.utf8string(ServiApplication.print_flage), ESCPOSConst.CMP_ALIGNMENT_LEFT,
						ESCPOSConst.CMP_FNT_DEFAULT | ESCPOSConst.CMP_FNT_BOLD,
						ESCPOSConst.CMP_TXT_1WIDTH | ESCPOSConst.CMP_TXT_1HEIGHT);
				printer.pageModePrint(ESCPOSConst.CMP_PM_NORMAL);
				printer.cutPaper(ESCPOSConst.CMP_CUT_PARTIAL_PREFEED);
				printer.openDrawer(ESCPOSConst.CMP_DRAWER_1, 1);
				result = printer.transactionPrint(ESCPOSConst.CMP_TP_NORMAL);
				printer.disconnect();
			} else {
				CommonMethods.showCustomToast(SalesActivity.this, getResources().getString(R.string.printer_errmsg));
			}
		}

		/* for eption printer */
//		new PrintAsync().execute();
	}

	public class PrintAsync extends AsyncTask<Void, Void, Void> {

		private boolean flage;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			flage = false;
			ServiApplication.allprinters_flage = true;
		}

		@Override
		protected Void doInBackground(Void... params) {


			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (flage) {
				loadUI();
			} else {
				Intent serverIntent = new Intent(SalesActivity.this, PRTSDKApp.class);
				appContext.pushActivity(serverIntent);
				serverIntent.putExtra("babu", "print");
				startActivityForResult(serverIntent, 10);
			}
		}

	}

	private List<DTO> getSalesDetailsList(String payments) {
		List<DTO> list = new ArrayList<DTO>();
		List<DTO> selectedProdList;

		selectedProdList = appContext.getSelectedProducts();

		for (int i = 0; i < selectedProdList.size(); i++) {
			SalesDetailDTO detailsDTO = new SalesDetailDTO();

			SelectedProddutsDTO dto = (SelectedProddutsDTO) selectedProdList.get(i);
			/*
			Harold: Valida si el precio cambio y lo cambia en la BD
			 */
			validateIfSellPriceChange(dto);
			//END
			if (dto.getProductType().equals(ConstantsEnum.PRODUCT_DISH.code())) {
				detailsDTO.setSaleId(Utils.saleID);
				detailsDTO.setProductId("");
				detailsDTO.setCount(dto.getQuantity());
				detailsDTO.setUom(dto.getUnits());
				detailsDTO.setPrice(dto.getSellPrice());
				if (isInvoiceAdj)
					detailsDTO.setDishId(dto.getIdDishProduct());
				else
					detailsDTO.setDishId(dto.getBarcode());
				detailsDTO.setSyncStatus(0);
			} else // NON DISH ITEM
			{
				detailsDTO.setSaleId(Utils.saleID);
				detailsDTO.setProductId(dto.getIdProduct());
				detailsDTO.setCount(dto.getQuantity());
				detailsDTO.setUom(dto.getUnits());
				detailsDTO.setPrice(dto.getSellPrice());
				detailsDTO.setDishId("");
				detailsDTO.setSyncStatus(0);
			}

			list.add(detailsDTO);
		}
		return list;
	}

	/*
    Harold: Método que valida si el precio cambio y lo reescribe
     */
	private void validateIfSellPriceChange(SelectedProddutsDTO product) {
		double utility = 0.0, utilityTemp = 0.0;
		try {
			utilityTemp = CommonMethods.getDoubleFormate(product.getSellPrice()) - CommonMethods.getDoubleFormate(product.getPrice());
			utility = CommonMethods.getDoubleFormate(product.getUtilityValue());

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (utilityTemp != utility) {
			ProductDTO dtoo = ProductDAO.getInstance().getRecordsByBarcode(
					DBHandler.getDBObj(Constants.READABLE), product.getBarcode());
			dtoo.setSellingPrice(product.getSellPrice());
			dtoo.setSyncStatus(0);
			ProductDAO.getInstance().updateProductSynk(
					DBHandler.getDBObj(Constants.WRITABLE), dtoo);
		}
	}

	private void deleteSaleRecords() {
		SalesDTO salesDTO = appContext.getSalesDTO();

		SalesDAO.getInstance().delete(DBHandler.getDBObj(Constants.WRITABLE), salesDTO);

		List<DTO> list = SalesDetailDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
				"sale_id", salesDTO.getSaleID());

		if (list.size() != 0) {
			SalesDetailDTO salesDetailDTO = (SalesDetailDTO) list.get(0);
			SalesDetailDAO.getInstance().delete(DBHandler.getDBObj(Constants.WRITABLE), salesDetailDTO);
		}
	}

	private void deleteClientPayments() {
		SalesDTO salesDTO = appContext.getSalesDTO();
		ClientPaymentDAO.getInstance().deleteRecordsBySaleId(DBHandler.getDBObj(Constants.WRITABLE),
				salesDTO.getSaleID());
	}

	private void updateInvoiceInventory() {
		List<DTO> invoiceList = InvoiceDetailsDAO.getInstance().getRecords(DBHandler.getDBObj(Constants.READABLE));
		Utils.setInvoiceList(invoiceList);
		for (int invoice_i = 0; invoice_i < invoiceList.size(); invoice_i++) {
			SelectedProddutsDTO invoiceDTO = (SelectedProddutsDTO) invoiceList.get(invoice_i);
			if (invoiceDTO.getProductType().equals(ConstantsEnum.PRODUCT_DISH.code())) {
				MenuInventoryDTO inDTO = MenuInventoryDAO.getInstance()
						.getRecordByDishID(DBHandler.getDBObj(Constants.READABLE), invoiceDTO.getIdDishProduct());
				double finalqty = CommonMethods.getDoubleFormate(inDTO.getCount())
						+ CommonMethods.getDoubleFormate(invoiceDTO.getQuantity());
				updateMenuInventory(inDTO, invoiceDTO, String.valueOf(finalqty));
			} else {

				InventoryDTO inDTO = InventoryDAO.getInstance()
						.getRecordByProductID(DBHandler.getDBObj(Constants.READABLE), invoiceDTO.getIdProduct());
				String qty = "";
				if (!"".equals(invoiceDTO.getUnits()) && invoiceDTO.getUnits() != null)
					qty = getInvoiceUOMQty(invoiceDTO, inDTO);
				else
					qty = getInvoiceChangeQty(invoiceDTO, inDTO);
				updateInventory(inDTO, invoiceDTO, qty);
			}
		}
	}

	private boolean updateMenuInventory(MenuInventoryDTO inDTO, SelectedProddutsDTO invoiceDTO, String qty) {
		boolean flag = false;
		inDTO.setDishId(invoiceDTO.getIdDishProduct());
		inDTO.setCount(qty);
		inDTO.setSyncStatus(0);

		flag = MenuInventoryDAO.getInstance().updateMenuInventoryData(DBHandler.getDBObj(Constants.WRITABLE), inDTO);
		return flag;
	}

	private boolean updateInventory(InventoryDTO inDTO, SelectedProddutsDTO invoiceDTO, String qty) {
		boolean flag = false;
		if (inDTO.getInventoryId() != null) {
			inDTO.setProductId(invoiceDTO.getIdProduct());
			inDTO.setQuantityBalance(CommonMethods.getBalanceQuantity(invoiceDTO.getBarcode(),invoiceDTO
					.getQuantity()));
			inDTO.setQuantity(qty);
			inDTO.setUom(invoiceDTO.getUnits());
			inDTO.setSyncStatus(0);

			flag = InventoryDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), inDTO);
		} else {
			List<DTO> newList = new ArrayList<DTO>();
			newList.add(getInventoryDTO(invoiceDTO));
			flag = InventoryDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), newList);
		}
		return flag;
	}

	private String getInvoiceChangeQty(SelectedProddutsDTO invoiceDTO, InventoryDTO inDTO) {
		double finalqty = CommonMethods.getDoubleFormate(inDTO.getQuantity())
				+ CommonMethods.getDoubleFormate(invoiceDTO.getQuantity());
		return String.valueOf(finalqty);
	}

	private String getInvoiceUOMQty(SelectedProddutsDTO invoiceDTO, InventoryDTO inDTO) {
		String finalqty = "";
		if ("kg".equalsIgnoreCase(inDTO.getUom()) || "lt".equalsIgnoreCase(inDTO.getUom())) {
			if ("kg".equalsIgnoreCase(invoiceDTO.getUnits()) || "lt".equalsIgnoreCase(invoiceDTO.getUnits()))
				finalqty = getInvoiceChangeQty(invoiceDTO, inDTO);
			else if ("gm".equalsIgnoreCase(invoiceDTO.getUnits()) || "ml".equalsIgnoreCase(invoiceDTO.getUnits()))
				finalqty = String.valueOf(CommonMethods.getDoubleFormate(inDTO.getQuantity())
						+ (CommonMethods.getDoubleFormate(invoiceDTO.getQuantity()) * 0.001));
		} else if ("gm".equalsIgnoreCase(inDTO.getUom()) || "ml".equalsIgnoreCase(inDTO.getUom())) {
			if ("kg".equalsIgnoreCase(invoiceDTO.getUnits()) || "lt".equalsIgnoreCase(invoiceDTO.getUnits()))
				finalqty = String.valueOf(CommonMethods.getDoubleFormate(inDTO.getQuantity())
						+ (CommonMethods.getDoubleFormate(invoiceDTO.getQuantity()) * 1000));
			else if ("gm".equalsIgnoreCase(invoiceDTO.getUnits()) || "ml".equalsIgnoreCase(invoiceDTO.getUnits()))
				finalqty = getInvoiceChangeQty(invoiceDTO, inDTO);
		}
		return finalqty;
	}

	private DTO getInventoryDTO(SelectedProddutsDTO selectedDTO) {
		InventoryDTO dto = new InventoryDTO();

		dto.setProductId(selectedDTO.getIdProduct());
		dto.setQuantity(selectedDTO.getQuantity());
		dto.setQuantityBalance(0.0);
		dto.setSyncStatus(0);
		dto.setUom(selectedDTO.getUnits());

		return dto;
	}

	private boolean synktocentralserver() {

		List<DTO> saledto_list = SalesDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sale_id", Utils.saleID);
		SalesDTO saledto = null;
		for (DTO dto : saledto_list) {
			saledto = (SalesDTO) dto;
		}
		Log.v("Varahalababu", "Hello BABU=========>" + Utils.saleID);
		Log.v("Varahalababu", "Hello BABU=========>" + saledto.getSaleID());
		ServiApplication.sales_id = saledto.getSaleID();


		if (invoice_adjnum != null) {
			if (new AsynkTaskClass().callcentralserver("/invoice/update", "" + ServiceHandler.POST,
					makeJsonObject(saledto), SalesActivity.this) != null) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					invoice_adjnum = null;
					saledto.setSyncStatus(1);
					SalesDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), saledto);

					SalesDetailDAO.getInstance().updatesynck(DBHandler.getDBObj(Constants.READABLE),
							saledto.getSaleID());
					return true;
				}
			}
		} else {
			updatesales(saledto);
			return true;
		}

		return true;
	}

	private void updatesales(SalesDTO saledto) {
		this.saledto = saledto;

		//if (NetworkConnectivity.netWorkAvailability(SalesActivity.this))
		new SalesCreation().execute();
	}

	// mEnuInventoryID
	private String makeJsonObjectforInventoryHistory(InventoryDTO IDTO) {
		ServiApplication.userName = sharedpreferences.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code", "");
		JSONObject jsonObj = new JSONObject();
		try {
			if (null != IDTO.getProductId()) {
				jsonObj.put("name", "inventory_history");
				jsonObj.put("type", "insert");

				JSONArray jsonaarray = new JSONArray();
				JSONObject jsonObj2 = new JSONObject();
				jsonObj2.put("invoice_number", IDTO.getInventoryId());
				jsonObj2.put("barcode", IDTO.getProductId());
				jsonObj2.put("store_code", ServiApplication.store_id);
				jsonObj2.put("quantity", IDTO.getQuantity());
				jsonObj2.put("uom", IDTO.getUom());
				jsonaarray.put(jsonObj2);
				jsonObj.put("records", jsonaarray);
			}
			return jsonObj.toString();
		} catch (Exception e) {
			return jsonObj.toString();
		}

	}

	/* make json obj for products */
	private String makeJsonObjectforInventory(InventoryDTO IDTO) {
		ServiApplication.userName = sharedpreferences.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code", "");
		JSONObject jsonObj = new JSONObject();
		try {
			if (null != ServiApplication.store_id && null != IDTO.getProductId()) {
				jsonObj.put("name", "inventory");
				jsonObj.put("type", "update");
				JSONArray jsonaarray2 = new JSONArray();
				jsonaarray2.put("barcode");
				jsonaarray2.put("store_code");
				jsonObj.put("columns", jsonaarray2);
				JSONArray jsonaarray = new JSONArray();
				JSONObject jsonObj2 = new JSONObject();
				jsonObj2.put("inventory_id", Long.parseLong(IDTO.getInventoryId()));
				jsonObj2.put("barcode", IDTO.getProductId());
				jsonObj2.put("store_code", ServiApplication.store_id);
				jsonObj2.put("quantity", IDTO.getQuantity());
				jsonObj2.put("uom", IDTO.getUom());
				jsonaarray.put(jsonObj2);
				jsonObj.put("records", jsonaarray);
			}
			return jsonObj.toString();
		} catch (Exception e) {
			return jsonObj.toString();
		}
	}

	@SuppressWarnings("static-access")
	private String makeJsonObject(SalesDTO saleDto) {
		ServiApplication.userName = sharedpreferences.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code", "");
		JSONObject jsonobj1 = new JSONObject();
		try {
			JSONObject jsonobj = new JSONObject();
			if (null != ServiApplication.store_id) {
				jsonobj.put("invoice_number", saleDto.getInvoiceNumber());
				jsonobj.put("sale_id", saleDto.getSaleID());
				jsonobj.put("gross_amount", Double.parseDouble(saleDto.getGrossAmount().replace(",", "")));
				jsonobj.put("net_amount", CommonMethods.getDoubleFormate(saleDto.getNetAmount()));
				jsonobj.put("discount", saleDto.getDiscount());
				jsonobj.put("customer_code", saleDto.getClientId());
				jsonobj.put("amount_paid", CommonMethods.getDoubleFormate(saleDto.getAmountPaid()));
				jsonobj.put("created_by", ServiApplication.userName);
				jsonobj.put("payment_type", saleDto.getPaymentType());
				jsonobj.put("store_code", ServiApplication.store_id);

				try {
					jsonobj.put("fecha_inicial", new Dates().serverdateformate(fecha_inicial));
				} catch (Exception e) {
					jsonobj.put("fecha_inicial", new Dates().serverdateformate(fecha_inicial));
				}
				try {
					jsonobj.put("fecha_final", new Dates().serverdateformate(saleDto.getFecha_final()));
				} catch (Exception e) {
					jsonobj.put("fecha_final", new Dates().serverdateformate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM)));
				}

				jsonobj.put("date_time", new Dates().serverdateformate(saleDto.getDateTime()));
				jsonobj1.put("sales", jsonobj);
				jsonobj1.put("sales_details", getSalesInfo(saleDto.getInvoiceNumber()));

			}
			return jsonobj1.toString();
		} catch (Exception e) {
			return jsonobj1.toString();
		}
	}

	private Object getSalesInfo(String invoiceNumber) {
		List<DTO> list = SalesDetailDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE),
				"sale_id", invoiceNumber);
		JSONArray jsonarray = new JSONArray();
		try {
			for (int i = 0; i < list.size(); i++) {
				SalesDetailDTO dto = (SalesDetailDTO) list.get(i);
				CommonMethods.getpurchaseprice(dto);
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("sale_id", dto.getSaleId());
				try {
					jsonobj.put("barcode", dto.getProductId());
				} catch (Exception e) {

				}
				jsonobj.put("count", dto.getCount());
				jsonobj.put("created_by", ServiApplication.userName);
				try {
					jsonobj.put("uom", dto.getUom());
					jsonobj.put("dish_id", Long.parseLong(dto.getDishId()));
				} catch (Exception e) {

				}
				jsonobj.put("price", CommonMethods.getDoubleFormate(dto.getPrice()));

				jsonobj.put("store_code", ServiApplication.store_id);
				jsonobj.put("purchase_price", ServiApplication.purchase_price);
				jsonarray.put(jsonobj);
			}
			return jsonarray;
		} catch (Exception e) {
			Log.e("Sales Activity ", e.getMessage());
			return jsonarray;
		}
	}

	/*
	 * private String getSaleID() { return SalesDAO.getInstance().getSaleID(
	 * DBHandler.getDBObj(Constants.READABLE)); }
	 */

	private List<DTO> getSalesList(String payments) {
		fecha_final = Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM);
		List<DTO> list = new ArrayList<DTO>();

		SalesDTO saleDTO = new SalesDTO();

		saleDTO.setSaleID(Utils.saleID);
		saleDTO.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		saleDTO.setInvoiceNumber(Utils.saleID);

		System.out.println("Log: Making a sale: Gross: " + subTotal + " Net: " + appContext.getTotalAmount());

		saleDTO.setGrossAmount(String.valueOf(subTotal));
		saleDTO.setNetAmount(String.valueOf(appContext.getTotalAmount()));
		saleDTO.setPaymentType(ConstantsEnum.PAYMENT_TYPE_CASH.code());
		saleDTO.setAmountPaid(payments.split(":")[0]);
		saleDTO.setDiscount(String.valueOf(appContext.getDiscount()));
		saleDTO.setFecha_inicial(fecha_inicial);
		saleDTO.setFecha_final(fecha_final);
		saleDTO.setTrans_delivery(transDelivery);

		if(appContext.getDeliveryDTO() != null)
			if (appContext.getDeliveryDTO().getDeliveryID() != null)
				saleDTO.setDelivery_code(appContext.getDeliveryDTO().getCedula());
			else
				saleDTO.setDelivery_code("");
		else
			saleDTO.setDelivery_code("");


		if (appContext.getClientDTO() != null)
			saleDTO.setClientId(appContext.getClientDTO().getCedula());
		else
			saleDTO.setClientId("");

		if (appContext.getClientDTO() != null)
			saleDTO.setClientId(appContext.getClientDTO().getCedula());
		else
			saleDTO.setClientId("");

		saleDTO.setSyncStatus(0);

		list.add(saleDTO);

		return list;
	}



	private void MenuInventorySynk(MenuInventoryDTO inDTO) {
		new AsynkTaskClass().callcentralserver("/sync", "" + ServiceHandler.POST, makeJsonObjectforMenuInventory(inDTO),
				SalesActivity.this);
	}

	private String makeJsonObjectforMenuInventory(MenuInventoryDTO inDTO) {


		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("name", "menu_inventory");
			jsonObj.put("type", "update");
			JSONArray jsonaarray2 = new JSONArray();
			jsonaarray2.put("menu_inventory_id");
			jsonObj.put("columns", jsonaarray2);
			JSONArray jsonaarray = new JSONArray();
			JSONObject jsonObj2 = new JSONObject();
			jsonObj2.put("menu_inventory_id", Long.parseLong(inDTO.getMenuInventoryId()));
			jsonObj2.put("dish_id", Long.parseLong(inDTO.getDishId()));
			jsonObj2.put("store_code", ServiApplication.store_id);
			jsonObj2.put("count", inDTO.getCount());
			jsonaarray.put(jsonObj2);
			jsonObj.put("records", jsonaarray);
			return jsonObj.toString();
		} catch (Exception e) {
			return jsonObj.toString();
		}

	}

	private void InventorySynk(InventoryDTO inDTO) {

		if (new AsynkTaskClass().callcentralserver("/sync", "" + ServiceHandler.POST, makeJsonObjectforInventory(inDTO),
				SalesActivity.this) != null) {
			if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
				if (new AsynkTaskClass().callcentralserver("/sync", "" + ServiceHandler.POST,
						makeJsonObjectforInventoryHistory(inDTO), SalesActivity.this) != null) {
				}
			}
		}

	}

	private void setValuesToPriceTag() {
		int totalItems = 0;
		double vat = 0;
		subTotal = 0;
		double discount = 0;

		for (int i = 0; i < appContext.getSelectedProducts().size(); i++) {
			SelectedProddutsDTO dto = (SelectedProddutsDTO) appContext.getSelectedProducts().get(i);

			totalItems += CommonMethods.getDoubleFormate(dto.getQuantity());

			subTotal += CommonMethods.getDoubleFormate(dto.getSellPrice())
					* CommonMethods.getDoubleFormate(dto.getQuantity());

			vat += (CommonMethods.getDoubleFormate(dto.getSellPrice()) / 100)
					* CommonMethods.getDoubleFormate(dto.getVat()) * CommonMethods.getDoubleFormate(dto.getQuantity());
		}

		discount = appContext.getDiscount();

		double netAmount = (subTotal + vat) - discount;
		appContext.setTotalAmount(netAmount);
		// appContext.setTotalAmount(subTotal + vat);

		txtTotalItems.setText(getResources().getString(R.string.sales_total_items) + " " + totalItems);
		txtSubTotal.setText(
				getResources().getString(R.string.sales_sub_total) + " " + CommonMethods.getNumSeparator(subTotal));
		txtVat.setText(getResources().getString(R.string.sales_vat) + " " + CommonMethods.getNumSeparator(vat));
		txtDiscount.setText(getResources().getString(R.string.sales_discount) + " "
				+ CommonMethods.getNumSeparator(appContext.getDiscount()));

		txtTotalAmount.setText(getResources().getString(R.string.sales_total_amount) + " "
				+ CommonMethods.getNumSeparator(appContext.getTotalAmount()));

		print_total_amount = getResources().getString(R.string.sale_print_hedder_11) + "                       "
				+ subTotal + "\n" + getResources().getString(R.string.sale_print_hedder_12) + "                      "
				+ discount + "\n" + getResources().getString(R.string.sale_print_hedder_13)
				+ "                            " + vat + "\n" + getResources().getString(R.string.sale_print_line)
				+ "\n" + getResources().getString(R.string.sale_print_hedder_14) + "                          "
				+ appContext.getTotalAmount() + "\n" + getResources().getString(R.string.sale_print_line);

		String totalamount = " " + CommonMethods.getDoubleFormate("" + appContext.getTotalAmount());

		EPSON_sale_print_hedder_12 = getResources().getString(R.string.sale_print_hedder_11)
				+ "                              " + subTotal + "\n"
				+ getResources().getString(R.string.sale_print_hedder_12) + "                             " + discount
				+ "\n" + getResources().getString(R.string.sale_print_hedder_13) + "                                   "
				+ vat + "\n" + getResources().getString(R.string.sale_print_line1) + "\n"
				+ getResources().getString(R.string.sale_print_hedder_14) + "                                "
				+ totalamount + "\n" + getResources().getString(R.string.sale_print_line1);
	}

	private void updateList() {

		salesAdapter.setListData(appContext.getSelectedProducts());
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
				etxtBarcode.getText().clear();

				isProductExists = false;
				break;

			case R.id.btn_SalesCalSearch:
				appContext.pushActivity(intent);
				Intent prodIntent = new Intent(SalesActivity.this, SalesInventoryActivity.class);
				startActivityForResult(prodIntent, SalesCodes.PRODUCT_CODE.code());
				break;

			case R.id.btn_SalesCalEnter:


				if (validateBarCode()) {
					SelectedProddutsDTO dto = new SelectedProddutsDTO();
					dto = InventoryDAO.getInstance().getProductBySearchID(DBHandler.getDBObj(Constants.READABLE),
							etxtBarcode.getText().toString().trim());
					List<DTO> selectedproducts = appContext.getSelectedProducts();

					if (selectedproducts == null) {
						selectedproducts = new ArrayList<DTO>();
						appContext.setSeletedProducts(selectedproducts);

					}
					boolean isExists = false;
					for (int i = 0; i < selectedproducts.size(); i++) {
						SelectedProddutsDTO prodDTO = (SelectedProddutsDTO) selectedproducts.get(i);
						String prodID = prodDTO.getIdProduct();
						if (prodID.equals(dto.getIdProduct())) {

							int qty = 0;
							if (!prodDTO.getQuantity().equals(""))
								qty = Integer.parseInt(prodDTO.getQuantity()) + 1;
							dto.setQuantity(String.valueOf(qty));
							dto.setPrice(dto.getSellPrice());
							selectedproducts.set(i, dto);

							isExists = true;
							break;
						}
					}
					if (!isExists) {
						dto.setQuantity("1");
						dto.setPrice(dto.getSellPrice());
						selectedproducts.add(dto);
					}

					loadUI();

				} else {
					if (etxtBarcode.getText().toString().trim().length() != 0)
						getDataFromCentralserver(etxtBarcode.getText()
								.toString().trim());
						////////end and////////
					else
						CommonMethods.displayAlert(true, getResources().getString(R.string.alert),
								getResources().getString(R.string.noproduct_exist), getResources().getString(R.string.ok), "",
								SalesActivity.this, null, false);

				}
				break;

			case R.id.btn_SaleEnd:
				if (appContext.getTotalAmount() > 0) {
					double debt = 0;

					if (appContext.getClientDTO() != null)
						debt = CommonMethods.getDoubleFormate(appContext.getClientDTO().getBalanceAmount());

					if (spnDelivery.getSelectedItemPosition()!=0){
						SaleFinishDeliveryDialog finishDeliveryDialog = new SaleFinishDeliveryDialog(SalesActivity.this, appContext.getTotalAmount(),
								debt, ConstantsEnum.SALES.code(), uiHandler);
						finishDeliveryDialog.show();
						Window window = finishDeliveryDialog.getWindow();
						window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

					}
					else {
						SaleFinishDialog1 finishDialog = new SaleFinishDialog1(SalesActivity.this, appContext.getTotalAmount(),
								debt, ConstantsEnum.SALES.code(), uiHandler);
						finishDialog.show();
					}

				} else
					CommonMethods.showCustomToast(this, getResources().getString(R.string.select_qty));

				break;
			case R.id.btn_SaleCancel:
				isOnBackPressed = false;
				cancelDialog = CommonMethods.displayAlert(false, getResources().getString(R.string.cancel_sale),
						getResources().getString(R.string.sales_cancel_msg), getResources().getString(R.string.yes),
						getResources().getString(R.string.no), SalesActivity.this, SalesActivity.this, true);
				break;

			case R.id.btn_SaleAddCustomer:
				// appContext.pushActivity(intent);
				appContext.mAddClientScreenData = new AddClientScreenData();
				appContext.mAddClientScreenData.mScreenData = null;
				appContext.mAddClientScreenData.mBackStackClass = SalesActivity.class;
				Intent intent1 = new Intent(this, AddClientActivity.class);
				// intent.putExtra(Constants.CLIENT_MODE, Constants.ADD_CLIENT);
				startActivity(intent1);
				finish();
				break;

			case R.id.btn_SaleDiscount:
				if (appContext.getTotalAmount() > 0) {
					double debt = 0;
					if (appContext.getClientDTO() != null)
						debt = CommonMethods.getDoubleFormate(appContext.getClientDTO().getBalanceAmount());
					if (udto.getIs_admin().equals("Y")) {
						DiscountDialog discDialog = new DiscountDialog(SalesActivity.this, appContext.getTotalAmount(),
								debt, uiHandler);
						discDialog.show();
					} else if (udto.getIs_authorized().equals("Y")) {
						checkStoreAutontication(debt);
					} else {
						CommonMethods.showCustomToast(SalesActivity.this,
								getResources().getString(R.string.discount_errmsg));
					}
				}
				break;

			case R.id.btn_SaleInvoiceAdj:
				isInvoiceAdj = true;
				InvoiceAdjustDialog dialog = new InvoiceAdjustDialog(SalesActivity.this, uiHandler);
				dialog.setOnDialogSaveListener(this);
				dialog.show();
				break;

			case R.id.btn_SalesBarCode:
				break;

			case R.id.btn_SalesUser:
				appContext.pushActivity(intent);
				appContext.mClientActivityBackStack = SalesActivity.this;
				Intent userIntent = new Intent(SalesActivity.this, ClientsActivity.class);
				startActivityForResult(userIntent, SalesCodes.CLIENT_CODE.code());

				break;

			default:
				break;
		}
	}

	private void checkStoreAutontication(final double debt) {

		dialog = new Dialog(SalesActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.cash_dialog);
		final TextView txt_reason = (TextView) dialog.findViewById(R.id.txt_reason);
		final EditText etxt_reason = (EditText) dialog.findViewById(R.id.etxt_reason);
		final LinearLayout layout_value = (LinearLayout) dialog.findViewById(R.id.layout_value);
		layout_value.setVisibility(View.GONE);
		txt_reason.setText(SalesActivity.this.getString(R.string.store_pass));
		final TextView txt_dialog_title = (TextView) dialog.findViewById(R.id.txt_dialog_title);
		txt_dialog_title.setText(SalesActivity.this.getString(R.string.alert));

		final ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);

		img_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		// etxt_reason.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		int maxLength = 20;
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(maxLength);
		etxt_reason.setFilters(fArray);
		etxt_reason.setTransformationMethod(PasswordTransformationMethod.getInstance());
		etxt_reason.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (etxt_reason.getText().toString().contains(" ")) {
					CommonMethods.showCustomToast(SalesActivity.this,
							getResources().getString(R.string.no_spaces_allowed));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (etxt_reason.getText().toString().contains(" ")) {
					int lenght = etxt_reason.getText().toString().length();
					s.delete(lenght - 1, lenght);
				}
			}
		});
		final Button btn_dialog_save, btn_dialog_cancel;

		btn_dialog_save = (Button) dialog.findViewById(R.id.btn_dialog_save);
		btn_dialog_save.setText(SalesActivity.this.getString(R.string.go));

		btn_dialog_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (NetworkConnectivity.netWorkAvailability(SalesActivity.this)) {
					if (etxt_reason.getText().length() > 0) {
						new StorePasswordCheck().execute(etxt_reason.getText().toString(), "" + debt);
					} else {
						CommonMethods.showCustomToast(SalesActivity.this,
								getResources().getString(R.string.fileds_not_blank));
					}
				} else {
					CommonMethods.showCustomToast(SalesActivity.this,
							getResources().getString(R.string.discount_errmsg));
				}
			}
		});

		btn_dialog_cancel = (Button) dialog.findViewById(R.id.btn_dialog_cancel);

		btn_dialog_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		appContext.popActivity();
		if (requestCode == SalesCodes.CLIENT_CODE.code()) {
			setTextToClientView();
		} else if (requestCode == SalesCodes.PRODUCT_CODE.code()) {
			loadUI();
		} else if (requestCode == 10) {
			inItUI();
		}
	}

	private void removeLastDigit() {
		if (etxtBarcode.getText().toString().trim().length() == 1) {
			etxtBarcode.getText().clear();
			etxtBarcode.setCursorVisible(true);
			etxtBarcode.setFocusable(true);
			etxtBarcode.setFocusableInTouchMode(true);
			etxtBarcode.requestFocus();
			isProductExists = false;
		} else {
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

	private void setTextToClientView() {
		if (txtClientName.getText().toString().trim().equals(getResources().getString(R.string.sales_custome) + " "
				+ getResources().getString(R.string.cleint_occasional))) {

			txtClientPhone.setText(getResources().getString(R.string.sales_phone) + " ");
			txtClientId.setText(getResources().getString(R.string.sales_id) + " ");
			txtClientCredit.setText(getResources().getString(R.string.sales_credit) + " ");

		}
		ClientDTO customerDTO = appContext.getClientDTO();

		if (customerDTO == null) {
			return;
		}

		txtClientName.setText(getResources().getString(R.string.sales_custome) + " " + customerDTO.getName());
		txtClientPhone.setText(getResources().getString(R.string.sales_phone) + " " + customerDTO.getTelephone());
		txtClientId.setText(getResources().getString(R.string.sales_id) + " " + customerDTO.getCedula());
		txtClientCredit.setText(getResources().getString(R.string.sales_credit) + " "
				+ CommonMethods.getNumSeparator(CommonMethods.getDoubleFormate(customerDTO.getBalanceAmount())));
	}

	private boolean validateBarCode() {
		barcode = etxtBarcode.getText().toString().trim();

		if (barcode.length() != 0) {
			int prodCount = InventoryDAO.getInstance().isProductExists(DBHandler.getDBObj(Constants.READABLE), barcode);

			if (prodCount != 0)
				isProductExists = true;
			else
				isProductExists = false;
		}

		return isProductExists;
	}

	@Override
	public void onSaveClick() {
		loadUI();

	}

	/*
	 * Run Service for sale and sale details with invoice number.
	 */

	private void sendReqForInvoice(String invoiceNum) {
		JSONObject josnobj = new JSONObject();
		try {
			josnobj.put("invoice_number", invoiceNum);
			josnobj.put("store_code", ServiApplication.store_id);
		} catch (Exception e) {
		}

		if (NetworkConnectivity.netWorkAvailability(SalesActivity.this)) {
			new GetSalesDetails().execute(josnobj.toString(), "/invoice/details", invoiceNum);
		} else {
			CommonMethods.showCustomToast(SalesActivity.this, getResources().getString(R.string.no_wifi_adhoc));
		}

	}

	private void setClientDTO(String clientid) {
		String client = clientid;
		List<DTO> list = ClientDAO.getInstance().getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "cedula",
				client);

		if (list.size() != 0) {
			ClientDTO clientDTO = (ClientDTO) list.get(0);
			appContext.setClientDTO(clientDTO);
			setTextToClientView();
		}
	}

	private void getSalesDetails(String responds_feed) {
		List<DTO> list = new ArrayList<DTO>();
		List<DTO> utilList = new ArrayList<DTO>();
		try {
			JSONObject jsonobj = new JSONObject(ServiApplication.responds_feed);
			JSONObject datajsonobj = jsonobj.getJSONObject("data");
			JSONArray jsonarray = datajsonobj.getJSONArray("sales_details");
			ProductDTO product = null;
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject data = jsonarray.getJSONObject(i);
				SelectedProddutsDTO dto = new SelectedProddutsDTO();
				dto.setIdProduct(data.getString("barcode"));
				dto.setBarcode(data.getString("barcode"));

				if (!"".equals(data.getString("barcode")) && !"null".equals(data.getString("barcode"))) {
					product = ProductDAO.getInstance().getRecordsByProductID(DBHandler.getDBObj(Constants.READABLE),
							data.getString("barcode"));
					dto.setProductType(ConstantsEnum.PRODUCT_NON_DISH.code());
				}

				dto.setIdDishProduct(data.getString("dish_id"));
				if (!"".equals(data.getString("dish_id")) && !"null".equals(data.getString("dish_id"))
						&& !"0".equals(data.getString("dish_id"))) {
					product = DishesDAO.getInstance().getRecordsByDishID(DBHandler.getDBObj(Constants.READABLE),
							data.getString("dish_id"));
					dto.setIdProduct(data.getString("dish_id"));
					dto.setProductType(ConstantsEnum.PRODUCT_DISH.code());
				}
				dto.setSaleID(data.getString("sale_id"));
				dto.setActualQty(product.getQuantity());

				dto.setInventoryType(ConstantsEnum.INVOICE.code());

				dto.setName(product.getName());
				dto.setPrice("" + data.getDouble("price"));
				dto.setSellPrice("" + data.getDouble("price"));
				dto.setUnits("" + data.getString("uom"));
				if (product.getVat() != null) {
					dto.setVat(product.getVat());
				} else {
					dto.setVat("0");
				}
				dto.setQuantity(data.getString("count"));
				list.add(dto);
				utilList.add(dto);
			}
		} catch (Exception e) {
		}
		appContext.setSeletedProducts(list);
		// Utils.setInvoiceList(utilList);
		InvoiceDetailsDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), utilList);
		updateList();
	}

	// For Printing Receipt. Load printing html into webview then Capture the
	// Picture.
	// i/p PrintingHtml
	// Then Invoking Async Task to Print the receipt.

	// Async Task to Print Receipt

	/* get sales details */

	public class GetSalesDetails extends AsyncTask<String, Void, Void> {

		String invoice_num;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(SalesActivity.this.getString(R.string.please_wait), SalesActivity.this);
		}

		@Override
		protected Void doInBackground(String... params) {
			ServiApplication.responds_feed = new ServiceHandler(SalesActivity.this)
					.makeServiceCall(ServiApplication.URL + params[1], ServiceHandler.POST, params[0]);
			invoice_num = params[2];
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {

				if (ServiApplication.responds_feed != null) {
					if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
						invoice_adjnum = invoice_num;
						Utils.saleID = invoice_num;
						SalesDTO dto = new SalesDTO();
						try {
							JSONObject jsonobj = new JSONObject(ServiApplication.responds_feed);
							JSONObject datajsonobj = jsonobj.getJSONObject("data");
							JSONObject childJSOnOBJ = datajsonobj.getJSONObject("sales");
							dto.setSaleID(Utils.saleID);
							dto.setAmountPaid("" + childJSOnOBJ.getDouble("amount_paid"));
							dto.setClientId(childJSOnOBJ.getString("amount_paid"));
							dto.setDateTime(childJSOnOBJ.getString("date_time"));
							dto.setDiscount(childJSOnOBJ.getString("discount"));
							dto.setGrossAmount("" + childJSOnOBJ.getDouble("gross_amount"));
							dto.setInvoiceNumber(childJSOnOBJ.getString("invoice_number"));
							dto.setNetAmount("" + childJSOnOBJ.getDouble("net_amount"));
							dto.setPaymentType(childJSOnOBJ.getString("payment_type"));
							dto.setSyncStatus(0);
							appContext.setSalesDTO(dto);
							appContext.setDiscount(CommonMethods.getDoubleFormate(dto.getDiscount()));

							if (childJSOnOBJ.getString("customer_code") != null) {
								setClientDTO(childJSOnOBJ.getString("customer_code"));
							} else {
								setClientDTO("");
							}
							getSalesDetails(ServiApplication.responds_feed);
						} catch (Exception e) {
							isInvoiceAdj = false;
						}
					} else {
						isInvoiceAdj = false;
						CommonMethods.showCustomToast(SalesActivity.this,
								getResources().getString(R.string.enter_valid_value));
					}
				} else {
					isInvoiceAdj = false;
					CommonMethods.showCustomToast(SalesActivity.this,
							getResources().getString(R.string.enter_valid_value));
				}
			} else {
				isInvoiceAdj = false;
				CommonMethods.showCustomToast(SalesActivity.this, getResources().getString(R.string.enter_valid_value));
			}
			CommonMethods.dismissProgressDialog();
		}

	}

	private void getCallTransaccionBoxService(String module_nem, String tipo, String PaymentType) {

		Log.v("varahalababu", "varahalabau======test============= sales");
		List<DTO> list = SalesDAO.getInstance().getRecords(DBHandler.getDBObj(Constants.READABLE));
		SalesDTO saledto = (SalesDTO) list.get(list.size() - 1);
		List<DTO> dto = new ArrayList<DTO>();

		TransaccionBoxDTO tdto = new TransaccionBoxDTO();
		tdto.setAmount(saledto.getAmountPaid());
		tdto.setModule_name(module_nem);
		tdto.setSyncstatus(0);
		tdto.setTipo_transction(tipo);
		tdto.setTransaction_type(PaymentType);
		tdto.setDatetime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		tdto.setUsername(sharedpreferences.getString("user_name", ""));
		tdto.setStore_code(sharedpreferences.getString("store_code", ""));
		dto.add(tdto);

		if (saledto.getClientId().length() > 2) {
			TransaccionBoxDTO tdto1 = new TransaccionBoxDTO();
			tdto1.setAmount(saledto.getNetAmount());
			tdto1.setModule_name(ServiApplication.Customer_lend_Payments_M_name);
			tdto1.setSyncstatus(0);
			tdto1.setTipo_transction(ServiApplication.Customer_lendPayments_TipoTrans);
			tdto1.setTransaction_type(ServiApplication.Customer_Payments_PaymentType);
			tdto1.setDatetime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
			tdto1.setUsername(sharedpreferences.getString("user_name", ""));
			tdto1.setStore_code(sharedpreferences.getString("store_code", ""));
			dto.add(tdto1);
		}

		if (TransaccionBoxDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), dto)) {
			Intent intent = new Intent(SalesActivity.this, TransaccionBoxService.class);
			startService(intent);
		} else {
			Log.v("varahalababu", "varahalabau======test============= sales else transation");
		}
	}

	private class StorePasswordCheck extends AsyncTask<String, Void, Void> {
		ServiceHandler serviceHandler;
		private String result, reqjson;
		;
		private double debt = 0.0;

		@Override
		protected void onPreExecute() {
			CommonMethods.showProgressDialog(SalesActivity.this.getString(R.string.please_wait), SalesActivity.this);
			serviceHandler = new ServiceHandler(SalesActivity.this);

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				JSONObject json = new JSONObject();
				json.put("store_code", udto.getNitShopId());
				json.put("store_password", params[0]);
				debt = Double.parseDouble(params[1]);
				reqjson = json.toString();
				ServiApplication.responds_feed = serviceHandler.makeServiceCall(
						ServiApplication.URL + "/store/validate-password", serviceHandler.POST, reqjson);
			} catch (Exception e) {
				ServiApplication.connectionTimeOutState = false;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					dialog.dismiss();
					DiscountDialog discDialog = new DiscountDialog(SalesActivity.this, appContext.getTotalAmount(),
							debt, uiHandler);
					discDialog.show();
				} else {
					CommonMethods.showCustomToast(SalesActivity.this, getString(R.string.store_pass_error));
				}
			}
		}
	}

	private class SalesCreation extends AsyncTask<Void, Void, Void> {


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (saledto.getClientId().length() > 0) {
				ClientDTO dto = ClientDAO.getInstance().getRecordsByClientCedula(DBHandler.getDBObj(Constants.READABLE), saledto.getClientId());
				dto.setSyncStatus(0);
				ClientDAO.getInstance().updateSynckstatussuccess1(DBHandler.getDBObj(Constants.WRITABLE), dto);
			}
			if (saledto.getDelivery_code().length() > 0){
				DeliveryDTO deliveryDTO = DeliveryDAO.getInstance().getRecordsByDeliveryCedula(DBHandler.getDBObj(Constants.READABLE),saledto.getDelivery_code());
				deliveryDTO.setSyncStatus(0);
				DeliveryDAO.getInstance().updateSynckstatussuccess1(DBHandler.getDBObj(Constants.WRITABLE),deliveryDTO);
			}
			intent = new Intent(SalesActivity.this, SalesDetailsUpdateService.class);
			startService(intent);
		}
	}



	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		if (dialog == cancelDialog) {
			if(isOnBackPressed){
				if (which == DialogInterface.BUTTON_POSITIVE){
					isInvoiceAdj = false;
					appContext.setSeletedProducts(new ArrayList<DTO>());
					appContext.setClientDTO(null);
					inItUI();
					super.onBackPressed();
				}
			} else {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					isInvoiceAdj = false;
					appContext.setSeletedProducts(new ArrayList<DTO>());
					appContext.setClientDTO(null);
					inItUI();
				}
			}
		}
		// Se agrega un mensaje de alerta para imprimir factura a petición del requerimiento 4.3.6.2 B
		if(dialog == alertDialog){
			if(which == DialogInterface.BUTTON_POSITIVE){
				try {
					callPrintMethod(invoiceNo);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//Add Harold
	private void updateNewProduct(SelectedProddutsDTO selectedDTO) {
		ProductDTO productDTOTemp = ProductDAO.getInstance().getRecordsByBarcode(DBHandler.getDBObj(Constants.READABLE),
				selectedDTO.getIdProduct());
		productDTOTemp.setSyncStatus(0);
		SupplierDTO supplierDTOTemp = SupplierDAO.getInstance().getRecordsBySupplierID(DBHandler.getDBObj(Constants.READABLE),
				productDTOTemp.getSupplierId());
		supplierDTOTemp.setSyncStatus(0);
		ProductDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), productDTOTemp);
		SupplierDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), supplierDTOTemp);
	}

	////////add by Andres Diaz////////
	private void getDataFromCentralserver(String trim) {
		if (NetworkConnectivity.netWorkAvailability(SalesActivity.this)) {
			new GetProductMasterData().execute(trim);
		} else {
			CommonMethods.showCustomToast(SalesActivity.this,
					getResources().getString(R.string.no_wifi_adhoc));
			createEmptyProduct();

		}
	}

	private class GetProductMasterData extends AsyncTask<String, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(
				SalesActivity.this);
		String val_clientList;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(getString(R.string.please_wait),
					SalesActivity.this);
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
			} else {
				createEmptyProduct();
			}
			CommonMethods.dismissProgressDialog();
		}
	}

	private void setDataToViewsFromCentroalServer() {
		try {
			if (productDto.getSupplierId().length() > 0) {
				preciocompra = productDto.getPurchasePrice();
				precioventa = productDto.getSellingPrice();
				nombre = productDto.getName();
			}


			newSimpleProductDialog dialog = new newSimpleProductDialog(SalesActivity.this);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void createEmptyProduct() {
		productDto.setPurchasePrice("0.0");
		productDto.setSellingPrice("0.0");
		productDto.setName("");
		productDto.setBarcode(etxtBarcode.getText().toString().trim());
		productDto.setSupplierName("NO DISPONIBLE");
		productDto.setGroupId("0");
		productDto.setGroupName("0");
		productDto.setLineId("0");
		productDto.setLineName("0");
		productDto.setSubgroup("NO DISPONIBLE");
		productDto.setUom("");
		productDto.setSupplierId("14236");
		productDto.setVat("0");
		productDto.setQuantity("0");
		productDto.setSyncStatus(0);
		productDto.setMin_count_inventory("1");
		setDataToViewsFromCentroalServer();
	}

	public void validateAndSave() {

		StringBuffer stringBuffer = new StringBuffer();

		if (!validationFields(stringBuffer)){
			CommonMethods.displayAlert(true,
					getResources().getString(R.string.alert),
					stringBuffer.toString(),
					getResources().getString(R.string.ok), "",
					SalesActivity.this, null, false);}
		insertRecordInDB();
	}

	private boolean validationFields(StringBuffer stringBuffer) {

		name = newSimpleProductDialog.nombreProducto;
		barcode = productDto.getBarcode();
		purprice = newSimpleProductDialog.preciocompra;
		sellprice = newSimpleProductDialog.precioventa;
		vat = "0";
		supplier = productDto.getSupplierName();
		group = productDto.getGroupName();
		line = productDto.getLineName();
		uom = productDto.getUom()=="0"?"":"";
		subgroup =productDto.getSupplierName();
		min_count_in_inventory = "1";
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


	private boolean insertRecordInDB() {
		// barcode
		if (ProductDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), getDataFromViews())) {

			updateSupplierFromServer();

			//Add by Harold And Ivan
			List<DTO> temp = getDataFromViews();
			ProductDTO dtoHarold = (ProductDTO)temp.get(0);
			dtoHarold.getBarcode();
			SelectedProddutsDTO selectedProddutsDTO = new SelectedProddutsDTO();
			selectedProddutsDTO.setBarcode(dtoHarold.getBarcode());
			selectedProddutsDTO.setQuantity("0");
			selectedProddutsDTO.setUnits(dtoHarold.getUom());
			selectedProddutsDTO.setPrice(dtoHarold.getPurchasePrice());
			selectedProddutsDTO.setSellPrice(dtoHarold.getSellingPrice());
			ProductFinalActivity productFinalActivity = new ProductFinalActivity();
			productFinalActivity.insertInventoryRecord(selectedProddutsDTO,"000");

			return true;


		}
		return false;
	}

	//AddHarold

	private void updateSupplierFromServer() {
		try {

			List<DTO> ldto = new ArrayList<DTO>();

			SupplierDTO lsdto = SupplierDAO.getInstance()
					.getRecordsBySupplierID(
							DBHandler.getDBObj(Constants.READABLE),
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
				Intent intent = new Intent(SalesActivity.this,
						SupplierUpdateService.class);
				startService(intent);
				ldto.clear();
			}



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

		} catch (Exception e){
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
		prodDTO.setQuantity(" ");
		prodDTO.setPurchasePrice(purprice);
		prodDTO.setSellingPrice(sellprice);
		prodDTO.setVat(vat);

		prodDTO.setQuantity("0");
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

		productList.add(prodDTO);
		return productList;
	}

	// Se agrega este evento a petición del requerimiento 4.3.6.2 D
	@Override
	public void onBackPressed() {
		isOnBackPressed = true;
		if(appContext.getSelectedProducts() != null && !appContext.getSelectedProducts().isEmpty()){
			cancelDialog = CommonMethods.displayAlert(false, getResources().getString(R.string.reg_cnfrm),
					getResources().getString(R.string.msj_go_out_module), getResources().getString(R.string.yes),
					getResources().getString(R.string.no), SalesActivity.this, SalesActivity.this, true);
		} else {
			super.onBackPressed();
		}
	}
}
