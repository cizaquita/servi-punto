/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto;

import java.util.ArrayList;
import java.util.List;

import com.epson.eposprint.Builder;
import com.epson.eposprint.Print;
import com.micaja.servipunto.activities.BaseActivity;
import com.micaja.servipunto.database.dto.AddClientScreenData;
import com.micaja.servipunto.database.dto.AddDeliveryScreenData;
import com.micaja.servipunto.database.dto.AddProductScreenData;
import com.micaja.servipunto.database.dto.ClientDTO;
import com.micaja.servipunto.database.dto.ClientHistoryDTO;
import com.micaja.servipunto.database.dto.ComisionDTO;
import com.micaja.servipunto.database.dto.ConsultaConvenioDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DaviplataNumberDeDocumento;
import com.micaja.servipunto.database.dto.DeliveryDTO;
import com.micaja.servipunto.database.dto.DeliveryHistoryDTO;
import com.micaja.servipunto.database.dto.OrderReceiveDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SalesDTO;
import com.micaja.servipunto.database.dto.SincronizarTransaccionesDTO;

import PRTAndroidSDK.PRTAndroidPrint;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;

public class ServiApplication extends Application {
	public static int Client_Info = 1;
	public static int Delivery_Info = 102;
	public static int CashFlowList = 2;
	public static int DishProductsList = 3;
	public static int ClientPayList = 4;
	public static int DeliveryPayList = 103;
	public static int dishlist = 5;
	public static int menudishlist = 6;
	public static int inventory = 7;
	public static int inventoryAdj = 8;
	public static int inventoryHistory = 9;
	public static int lendMoney = 10;
	public static int deliveryLendMoney = 101;
	public static int menu = 11;
	public static int menuInventory = 12;
	public static int menuInventoryAdj = 13;
	public static int orderDetails = 14;
	public static int orders = 15;
	public static int sales = 16;
	public static int salesDetails = 17;
	public static int clientpayments = 18;
	public static int deliveryPayments = 100;
	public static int supplier = 19;
	public static int userTable = 20;
	public static int supplierpayments = 21;
	public static int updateProducts = 22;
	public static int updatepuntoredsync = 23;

	public static int AES_GEN_HOR = 8;
	public static int local_data_speed = 0;

	public static String store_id;
	public static String res_user_id;
	public static boolean block_user_flage = true;

	public static boolean himp_print_flage = false;
	public static boolean flage_for_log_print = true;

	public static String responds_feed;
	public static String serverGenarateClientId;
	public static String userName;
	public static String ency_key_pass = "com.micaja.servipunto";
	public static Double purchase_price;
	public static boolean inventory_history_flag = false, add_sup_product = false;

	public static String CASH = "CASH";
	public static String PAYMENT = "PAYMENT";
	public static String DATAPHONE = "DATAPHONE";
	public static String BANK_CHECK = "BANK_CHECK";
	public static boolean shope_open_alert = false;
	public static boolean shale_menu = false;
	public static boolean token_flage = true;

	public static int alert_type;
	public static long promotion_id;
	public static long daviplata_valorPago_dev = 30000;
	public static int AutoSyncToBlink = 1800000;
	public static String SUPPLIER_PAYMENT_TYPE = CASH;
	/* puntored service connection time and promotion golory blink time */
	public static int timeToBlink = 3000;
	public static int service_connectiontimeout = 20000;

	public static String MyPREFERENCES = "com.micaja.servipunto.sharedpreferences";
	public static int login_session_count = 0;

	 //public static String URL = "http://4.servi-app.appspot.com";
	//public static String URL = "http://localhost:10080/";
	// public static String URL = "http://servi-app.appspot.com";
	// public static String URL = "https://servi-test.appspot.com";
	// public static String URL = "https://mycash-2.appspot.com";
	// public static String URL = "https://mycashbox-dev.appspot.com";
	// public static String URL = "https://micaja-project.appspot.com";
	// public static String URL = "https://uday-test.appspot.com";
	 public static String URL = "http://beta2.servi-test-saphety.appspot.com";
	// public static String URL = "https://adyntdemo.appspot.com";

	private ArrayList<Intent> activityList = new ArrayList<Intent>();
	private List<DTO> seletedProductsList = new ArrayList<DTO>();
	private List<DTO> orderSupplierList = new ArrayList<DTO>();
	private List<DTO> promotionlist = new ArrayList<DTO>();
	private List<DTO> menusDishesList = new ArrayList<DTO>();
	private String userId;
	private String screen = null;
	private String screenMode = null;
	private DTO dishDTO;
	private ProductDTO productDTO;
	private String seletedSupplier, selectedProduct = "", order_reciveinvoice;
	private SalesDTO salesDTO;
	public static boolean connectionTimeOutState = true;
	public static boolean backButton = false;
	private double discount, totalAmount;
	public AddClientScreenData mAddClientScreenData;
	public AddDeliveryScreenData mAddDeliveryScreenData;
	public AddProductScreenData mAddproductScreenData;
	public static Double reg_amt;
	private List<DTO> invoiceList;

	public static DaviplataNumberDeDocumento documetn = new DaviplataNumberDeDocumento();



	public static DaviplataNumberDeDocumento getDocumetn() {
		return documetn;
	}

	public static void setDocumetn(DaviplataNumberDeDocumento documetn) {
		ServiApplication.documetn = documetn;
	}

	public List<DTO> getinvoiceList() {
		return invoiceList;
	}

	public void setinvoiceList(List<DTO> invoiceList) {
		this.invoiceList = invoiceList;
	}

	public static Double getReg_amt() {
		return reg_amt;
	}

	public static void setReg_amt(Double reg_amt) {
		ServiApplication.reg_amt = reg_amt;
	}

	public static boolean galory_position = true;

	private List<DTO> inventoryHistoryList;

	public BaseActivity mClientActivityBackStack;

	public static List<ClientHistoryDTO> clientHistoryDTO = new ArrayList<ClientHistoryDTO>();
	public static List<DeliveryHistoryDTO> deliveryHistoryDTO = new ArrayList<DeliveryHistoryDTO>();
	public static List<OrderReceiveDTO> orederReceivedata = new ArrayList<OrderReceiveDTO>();

	private ClientDTO clientDTO;
	private DeliveryDTO deliveryDTO;
	private PRTAndroidPrint PRT = null;

	public List<DTO> getPromotionlist() {
		return promotionlist;
	}

	public void setPromotionlist(List<DTO> promotionlist) {
		this.promotionlist = promotionlist;
	}

	public PRTAndroidPrint getPRT() {
		return PRT;
	}

	public void setPRT(PRTAndroidPrint pRT) {
		PRT = pRT;
	}

	public String getScreenMode() {
		return screenMode;
	}

	public void setScreenMode(String screenMode) {
		this.screenMode = screenMode;
	}

	public ProductDTO getProductDTO() {
		return productDTO;
	}

	public void setProductDTO(ProductDTO productDTO) {
		this.productDTO = productDTO;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void pushActivity(Intent intent) {
		activityList.add(intent);
	}

	public void popActivity() {
		activityList.remove(activityList.size() - 1);
	}

	public int getActivityListSize() {
		return activityList.size();
	}

	public void clearActivityList() {
		activityList.removeAll(activityList);
	}

	public ArrayList<Intent> getActivityList() {
		return activityList;
	}

	public void setSeletedProducts(List<DTO> selectedProducts) {
		this.seletedProductsList = selectedProducts;
	}

	public List<DTO> getSelectedProducts() {
		return seletedProductsList;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public ClientDTO getClientDTO() {
		return clientDTO;
	}

	public void setClientDTO(ClientDTO clientDTO) {
		this.clientDTO = clientDTO;
	}

	public void setDeliveryDTO(DeliveryDTO deliveryDTO) {
		this.deliveryDTO = deliveryDTO;
	}

	public DeliveryDTO getDeliveryDTO() {
		return deliveryDTO;
	}



	public List<DTO> getOrderSupplierList() {
		return orderSupplierList;
	}

	public void setOrderSupplierList(List<DTO> orderSupplierList) {
		this.orderSupplierList = orderSupplierList;
	}

	public String getSeletedSupplier() {
		return seletedSupplier;
	}

	public void setSeletedSupplier(String seletedSupplier) {
		this.seletedSupplier = seletedSupplier;
	}

	public List<DTO> getMenusDishesList() {
		return menusDishesList;
	}

	public void setMenusDishesList(List<DTO> menusDishesList) {
		this.menusDishesList = menusDishesList;
	}

	public String getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(String selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public String getScreen() {
		return screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public DTO getDishDTO() {
		return dishDTO;
	}

	public void setDishDTO(DTO dishDTO) {
		this.dishDTO = dishDTO;
	}

	public SalesDTO getSalesDTO() {
		return salesDTO;
	}

	public void setSalesDTO(SalesDTO salesDTO) {
		this.salesDTO = salesDTO;
	}

	public List<DTO> getInventoryHistoryList() {
		return inventoryHistoryList;
	}

	public void setInventoryHistoryList(List<DTO> inventoryHistoryList) {
		this.inventoryHistoryList = inventoryHistoryList;
	}

	public static List<DTO> dish_products = new ArrayList<DTO>();
	public static List<DTO> dishs = new ArrayList<DTO>();

	public static List<DTO> group = new ArrayList<DTO>();
	public static List<DTO> line = new ArrayList<DTO>();
	public static List<DTO> damage_type = new ArrayList<DTO>();

	public static List<DTO> products = new ArrayList<DTO>();
	public static List<DTO> clients = new ArrayList<DTO>();
	public static List<DTO> deliverys = new ArrayList<DTO>();
	public static List<DTO> suppliers = new ArrayList<DTO>();
	public static List<DTO> menus = new ArrayList<DTO>();
	public static List<DTO> menu_dishes = new ArrayList<DTO>();
	public static List<DTO> menu_inventory = new ArrayList<DTO>();
	public static List<DTO> menu_type = new ArrayList<DTO>();
	public static List<DTO> inventory_sync = new ArrayList<DTO>();

	public static List<DTO> user = new ArrayList<DTO>();

	public static boolean promotion_bye_flage = false;
	public static boolean promotion_bye_click_flage = false;
	public static List<DTO> setClicent = new ArrayList<DTO>();
	public static List<DTO> setSupplier = new ArrayList<DTO>();

	public static List<DTO> CellularProvider = new ArrayList<DTO>();

	public static List<DTO> PuntoredCredintials = new ArrayList<DTO>();

	/* process id */

	public static String process_id_sincronizarTransacciones = "202006";
	public static String process_id_get_pagoCodigoBarras = "202222";
	public static String process_id_get_consultaConvenio = "202221";
	public static String process_id_get_QueryrPaquetigos_Key = "202001";
	public static String process_id_get_symmetrical_Key = "100102";
	public static String process_id_SaldoPorCuentas = "202004";
	public static String process_id_consultarTransacciones = "202003";
	public static String process_id_consultarVentas = "202002";
	public static String process_id_recharge = "202208";
	public static String process_id_Paquetigos = "202209";
	public static String process_id_Change_key = "202211";
	public static String process_id_Microinsurance = "202213";
	public static String process_id_operators = "202001";
	public static String process_id_1b_reports = "202223";
	public static String process_id_retiropagoDaviplata = "202220";
	public static String process_id_Consulta_factura = "202219";
	public static String process_id_reg_pago = "202210";
	public static String process_id_ConsultarConvenioProveedores = "202005";

	public static String process_id_recaudoProveedor = "202212";
	public static String process_id_consultarPines = "202015";

	public static String process_id_Consulta_Convenio = "202235";// 202221
	public static String process_id_ventaPines = "202224";
	public static String process_id_consultarLoterias = "202016";

	public static String process_id_consultarSeriesLoterias = "202017";
	public static String process_id_VentaLoterias = "202225";
	public static String process_id_recaudos = "202222";
	public static String process_id_mpresionTirilla = "202007";
	public static String process_id_InfoLogin = "202233";

	public static String process_id_Aceptar_Dispersion = "202227";
	public static String process_id_Rechazar_Dispersion = "202234";
	public static String process_id_validarInfo_pago = "202029";
	public static String process_id_RealizarPago_con_processId = "202236";

	/* Tipo id */
	public static String Tipo_id_refills = "1";
	public static String Tipo_id_providers = "4";
	public static String Tipo_id_ConsultaPinesProveedores = "3";
	public static String Tipo_id_microinsurance = "9";
	public static String Tipo_id_Paquetigos = "8";
	public static String Tipo_id_tipoTransaccion = "20";

	public static String getTipo_id_tipoTransaccion() {
		return Tipo_id_tipoTransaccion;
	}

	public static void setTipo_id_tipoTransaccion(String tipo_id_tipoTransaccion) {
		Tipo_id_tipoTransaccion = tipo_id_tipoTransaccion;
	}

	/* punthored module tipo id's */
	public static String tipo_recarge_id = "1";
	public static String tipo_paquetigo_id = "2";
	public static String tipo_abono_acenta_id = "3";
	public static String tipo_proveedor_acenta_id = "4";
	public static String tipo_cashout_daviplata_id = "5";
	public static String tipo_recaudo_rbc_id = "6";
	public static String tipo_recaudo_cnr_id = "7";
	public static String tipo_loteries_id = "8";
	public static String tipo_pines_id = "9";
	public static String tipo_pago_id = "10";
	public static String tipo_cash_in_daviplata_id = "11";
	public static String tipo_cash_deposit_id = "12";
	public static String tipo_cash_withdraw_id = "13";
	public static String tipo_recarga_microinsurence = "16";

	/* punthored soap urls */
	public static String SOAP_version = "V1_1";
	public static String SOAP_token = "1234";
	public static String SOAP_trace = "1111";

	/* punthored soap urls */

	/* for brainwinner */
	// public static String username = "mauricio.suarez@brainwinner.com";//This
	// pass word for requesting server for authontcation
	//// public static String puntho_pass = "u4gkpdtS..ZrS+4$";//This pass word
	// for requesting server for authontcation
	// public static String AES_secret_key = "LgSBrAiNSeRvl234";//This AES_key
	// word for requesting server for authontcation
	// public static String puntho_pass =
	// "SjzlUhE+tJKcW07fvK/aynsQ48ih4R72CXQhlGHC46M=";

	/* for punthored */

	// public static String username = "brokerMiCaja";//This pass word for
	// requesting server for authontcation
	// public static String puntho_pass =
	// "k4uotXx9W0K/33HFcy9euw==";//"gWdBRuynjULAUW3Ubs8uZQ";//"k4uotXx9W0K/33HFcy9euw==";//k4uotXx9W0K/33HFcy9euw==//"gWdBRuynjULAUW3Ubs8uZQ==";//This
	// pass word for requesting server for authontcation
	// public static String AES_secret_key = "LgSBrAiNSeRvl234";//This AES_key
	// word for requesting server for authontcation

	/* for Production */

	public static String username = "brokerMiCaja";// This pass word for
													// requesting server for
													// authontcation
	public static String puntho_pass = "6exb8PQfymLu8XT92tFflg==";// This pass
																	// word for
																	// requesting
																	// server
																	// for
																	// authontcation
	public static String AES_secret_key = "LgSBrAiNSeRvl234";// This AES_key
																// word for
																// requesting
																// server for
																// authontcation

	// public static String SOAP_URL
	// ="http://190.27.239.3:8080/bwws/BWService";// This is for Development
	// environment
	// public static String SOAP_URL =
	// "http://190.27.239.5:8080/bwws/BWService";// This is for QA
	// public static String SOAP_URL
	// ="http://199.89.54.41:8080/bwws/BWService";// This is for Gigas Testing
	// purpush

	// public static String SOAP_URL =
	// "http://192.168.1.129:8080/bwws/BWService";// This is for test in local
	// environment

	// public static String SOAP_URL
	// ="http://192.168.0.18:8080/bwws/BWService";// This is for Development
	// environment

	// public static String SOAP_URL =
	// "http://199.89.54.41:8080/micaja/BWService";//This is for new Gigas

	// public static String SOAP_URL =
	// "http://190.27.239.5:8080/micaja/BWService";//This is for new QA

	// public static String SOAP_URL
	// ="http://192.168.3.27:8080/micaja/BWService";//This is Private Puntored
	// WS

	// public static String SOAP_URL
	// ="http://162.248.54.83:9080/micaja/BWService";//?wsdl This is for
	// puntored testing environment

	public static String SOAP_URL = "http://micaja.puntored.com.co:9080/micaja/BWService"; //This WS for  Productiom environment

	public static String SOAP_EnvelopeL = "http://schemas.xmlsoap.org/soap/envelope/";
	public static String SOAP_NameSpace = "http://bws.brainwinner.com/";
	public static String SOAP_Method_Operators = "erouted";
	public static String SOAP_Action = "http://bws.brainwinner.com/erouted";
	public static String Recharge_conform_data;
	public static String microseguros_conform_data;

	public static String getMicroseguros_conform_data() {
		return microseguros_conform_data;
	}

	public static void setMicroseguros_conform_data(String microseguros_conform_data) {
		ServiApplication.microseguros_conform_data = microseguros_conform_data;
	}

	public static String getRecharge_conform_data() {
		return Recharge_conform_data;
	}

	public static void setRecharge_conform_data(String recharge_conform_data) {
		Recharge_conform_data = recharge_conform_data;
	}

	public static ConsultaConvenioDTO ccdto;

	public static ConsultaConvenioDTO getCcdto() {
		return ccdto;
	}

	public static void setCcdto(ConsultaConvenioDTO ccdto) {
		ServiApplication.ccdto = ccdto;
	}

	public static SincronizarTransaccionesDTO stdto;

	public static SincronizarTransaccionesDTO getStdto() {
		return stdto;
	}

	public static void setStdto(SincronizarTransaccionesDTO stdto) {
		ServiApplication.stdto = stdto;
	}

	public static ProductDTO prodto;

	// TransaccionBox Tipoid's
	public static String Shop_Open_TipoTrans = "12";
	public static String Shop_Close_TipoTrans = "13";
	public static String Customer_PostPayments_TipoTrans = "15";
	public static String Customer_lendPayments_TipoTrans = "16";
	public static String Delivery_PostPayments_TipoTrans = "40";
	public static String Delivery_lendPayments_TipoTrans = "41";
	public static String Supplier_Payments_TipoTrans = "22";
	public static String Sales_TipoTrans = "22";
	public static String Cashflow_Withdraw_TipoTrans = "10";
	public static String Cashflow_Disposit_TipoTrans = "11";
	public static String Orders_TipoTrans = "18";
	public static String Inventory_TipoTrans = "22";

	// TransaccionBox Models Names
	public static String Shop_Open_M_name = "La caja se abre";
	public static String Shop_Close_M_name = "Tienda cerrada";
	public static String Customer_Payments_M_name = "Abono del cliente a la deuda";
	public static String Customer_lend_Payments_M_name = "Préstamo de plata a cliente";
	public static String Delivery_Payments_M_name = "Abono del Delivery a la deuda";
	public static String Delivery_lend_Payments_M_name = "Préstamo de dinero al Delivery";
	public static String Supplier_Payments_M_name = "Pago a proveedores";
	public static String Sales_M_name = "Venta";
	public static String Cashflow_Withdraw_M_name = "Retiro de efectivo";
	public static String Cashflow_Disposit_M_name = "Abono de efectivo";
	public static String Orders_M_name = "Pedido";
	public static String Inventory_M_name = "Inventario";

	// TransaccionBox PaymentType Names

	public static String Shop_Open_PaymentType = "La caja se abre";
	public static String Shop_Close_PaymentType = "Tienda cerrada";
	public static String Customer_Payments_PaymentType = "Pago de clientes";
	public static String Delivery_Payments_PaymentType = "Pago de Delivery";
	public static String Supplier_Payments_PaymentType = "Pago a proveedores";
	public static String Sales_PaymentType = "Venta";
	public static String Cashflow_Withdraw_PaymentType = "Retiro de efectivo";
	public static String Cashflow_Disposit_PaymentType = "Abono de efectivo";
	public static String Orders_PaymentType = "Pedido";
	public static String Inventory_PaymentType = "Inventario";

	/* ......... Phase2B process id's....... */

	public static String process_id_Devolucion = "202231";
	public static String process_id_RegistrarEfectivo = "202232";
	public static String process_id_AcceptEfectivo = "202230";
	public static String process_id_ConsultarCentrosAcopio = "202019";
	public static String process_id_AcceptAcopio = "202229";
	public static String process_id_ConsultarAceptaciones = "202025";

	public static String process_id_ConsultarTipoDocumento = "202022";
	public static String process_id_ConsultaCedula = "202023";
	public static String process_id_ConsultarComision = "202024";
	public static String process_id_ConsulterCiudades = "202021";
	public static String process_id_Comision = "202024";
	public static String process_id_girodaviplata = "202226";

	public static String process_id_paGoproviders = "202034";
	public static String process_id_paGoproduct = "202035";
	public static String process_id_paGoprogram = "202027";
	public static String process_id_paGoConsulta_de_campos_configurados = "202028";

	public static String process_id_consulta_de_la_OTP = "202237";

	public static int wrong_passcount = 0;
	public static String wrong_passtext = "08-Terminal invalido";
	/* epos printer */

	public static String openDeviceName = "192.168.192.168";
	public static int connectionType = Print.DEVTYPE_TCP;
	public static int printerModel = Builder.LANG_EN;
	public static String printerName = "TM-m10";

	/* epos printer */
	public static String print_flage = "";
	public static String print_products = "";
	public static String print_flage_eps = "";

	public String getOrder_reciveinvoice() {
		return order_reciveinvoice;
	}

	public void setOrder_reciveinvoice(String order_reciveinvoice) {
		this.order_reciveinvoice = order_reciveinvoice;
	}

	public static String printer_id;

	public static String getPrinter_id() {
		return printer_id;
	}

	public static void setPrinter_id(String printer_id) {
		ServiApplication.printer_id = printer_id;
	}

	public static int previus;
	public static int next;

	public static int getPrevius() {
		return previus;
	}

	public static void setPrevius(int previus) {
		ServiApplication.previus = previus;
	}

	public static int getNext() {
		return next;
	}

	public static void setNext(int next) {
		ServiApplication.next = next;
	}

	public static boolean order_product;

	public boolean getOrder_product() {
		return order_product;
	}

	public void setOrder_product(boolean order_product) {
		ServiApplication.order_product = order_product;
	}

	public static Handler uiHandler;

	public static Handler getUiHandler() {
		return uiHandler;
	}

	public static void setUiHandler(Handler uiHandler) {
		ServiApplication.uiHandler = uiHandler;
	}

	public static boolean allprinters_flage = false;

	public static String distribuidor;

	public static String getDistribuidor() {
		return distribuidor;
	}

	public static void setDistribuidor(String distribuidor) {
		ServiApplication.distribuidor = distribuidor;
	}

	public static ComisionDTO commDTO;

	public static ComisionDTO getCommDTO() {
		return commDTO;
	}

	public static void setCommDTO(ComisionDTO commDTO) {
		ServiApplication.commDTO = commDTO;
	}

	public static int downloded_count;
	public static int total_count;

	public static String downloded_table_name;

	public static int getDownloded_count() {
		return downloded_count;
	}

	public static void setDownloded_count(int downloded_count) {
		ServiApplication.downloded_count = downloded_count;
	}

	public static int getTotal_count() {
		return total_count;
	}

	public static void setTotal_count(int total_count) {
		ServiApplication.total_count = total_count;
	}

	public static String getDownloded_table_name() {
		return downloded_table_name;
	}

	public static void setDownloded_table_name(String downloded_table_name) {
		ServiApplication.downloded_table_name = downloded_table_name;
	}

	public static String sales_id;

	public static int product_downloded_count;
	public static int product_total_count;

	public static int supplier_downloded_count;
	public static int supplier_total_count;

	public static int client_downloded_count;
	public static int client_total_count;

	
	public static int getProduct_downloded_count() {
		return product_downloded_count;
	}

	public static void setProduct_downloded_count(int product_downloded_count) {
		ServiApplication.product_downloded_count = product_downloded_count;
	}

	public static int getProduct_total_count() {
		return product_total_count;
	}

	public static void setProduct_total_count(int product_total_count) {
		ServiApplication.product_total_count = product_total_count;
	}

	public static int getSupplier_downloded_count() {
		return supplier_downloded_count;
	}

	public static void setSupplier_downloded_count(int supplier_downloded_count) {
		ServiApplication.supplier_downloded_count = supplier_downloded_count;
	}

	public static int getSupplier_total_count() {
		return supplier_total_count;
	}

	public static void setSupplier_total_count(int supplier_total_count) {
		ServiApplication.supplier_total_count = supplier_total_count;
	}

	public static int getClient_downloded_count() {
		return client_downloded_count;
	}

	public static void setClient_downloded_count(int client_downloded_count) {
		ServiApplication.client_downloded_count = client_downloded_count;
	}

	public static int getClient_total_count() {
		return client_total_count;
	}

	public static void setClient_total_count(int client_total_count) {
		ServiApplication.client_total_count = client_total_count;
	}

}
