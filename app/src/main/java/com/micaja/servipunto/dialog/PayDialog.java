/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.dialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ClientDAO;
import com.micaja.servipunto.database.dao.ClientPaymentDAO;
import com.micaja.servipunto.database.dao.LendMoneyDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dao.SupplierPaymentsDAO;
import com.micaja.servipunto.database.dao.TransaccionBoxDAO;
import com.micaja.servipunto.database.dto.ClientDTO;
import com.micaja.servipunto.database.dto.ClientPaymentsDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.LendmoneyDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.database.dto.SupplierPaymentsDTO;
import com.micaja.servipunto.database.dto.TransaccionBoxDTO;
import com.micaja.servipunto.service.ClientUpdateService;
import com.micaja.servipunto.service.SupplierUpdateService;
import com.micaja.servipunto.service.TransaccionBoxService;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.DecimalInputFilter;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;
import com.micaja.servipunto.utils.RequestFiels;

public class PayDialog extends Dialog
		implements android.view.View.OnClickListener, android.content.DialogInterface.OnClickListener {

	private Context context;
	private EditText etxtbal;
	private Button btnSave, btnCancel;
	private ImageView imgClose;
	private TextView dialogTitle, txtBalance, txtName, txtTotalDebt;
	private String type;
	private SupplierDTO suppDto;

	private LinearLayout layoutReason, layoutName;
	private RelativeLayout layoutHeader;

	private ClientDTO clientDto;
	onSaveListener listener;
	public SharedPreferences sharedpreferences;

	public PayDialog(Context context) {
		super(context);
		this.context = context;
	}

	public interface onSaveListener {
		public void onSave();
	}

	public PayDialog(Context context, ClientDTO clientDto, String type) {
		super(context);

		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.userName = sharedpreferences.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code", "");
		this.context = context;
		this.clientDto = clientDto;
		this.type = type;

	}

	public PayDialog(Context context, SupplierDTO suppDto, String type) {
		super(context);
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.userName = sharedpreferences.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code", "");
		this.context = context;
		this.suppDto = suppDto;
		this.type = type;

	}

	public void setListener(onSaveListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.userName = sharedpreferences.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code", "");
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.cash_dialog);

		etxtbal = (EditText) findViewById(R.id.etxt_bal);
		layoutReason = (LinearLayout) findViewById(R.id.layout_reason);
		dialogTitle = (TextView) findViewById(R.id.txt_dialog_title);
		btnSave = (Button) findViewById(R.id.btn_dialog_save);
		btnCancel = (Button) findViewById(R.id.btn_dialog_cancel);
		txtBalance = (TextView) findViewById(R.id.txt_bal);
		imgClose = (ImageView) findViewById(R.id.img_close);

		layoutHeader = (RelativeLayout) findViewById(R.id.layout_header);

		layoutName = (LinearLayout) findViewById(R.id.layout_clientName);
		txtName = (TextView) findViewById(R.id.txt_Name);
		txtTotalDebt = (TextView) findViewById(R.id.txt_TotalDebt);

		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		imgClose.setOnClickListener(this);

		//etxtbal.setFilters(new InputFilter[] { new DecimalInputFilter(15, 2) });

		loadUI();

	}

	private void loadUI() {

		if (type.equals(ConstantsEnum.SUPPLIER_PAY.code())) {
			etxtbal.setText(suppDto.getBalanceAmount());
			dialogTitle.setText(context.getResources().getString(R.string.client_debt_pay));
			layoutHeader.setBackgroundColor(context.getResources().getColor(R.color.inventory_tab));
			btnSave.setBackgroundResource(R.drawable.cash_button);
			Log.e("Entro al otro if", suppDto.getBalanceAmount());
		} else {
				if (type.equals(ConstantsEnum.CLIENT_LEND.code())){
					dialogTitle.setText(context.getResources().getString(R.string.lendmoney));
					etxtbal.setText("");
					Log.e("Entro a prestar dinero", "");
				} else {
					dialogTitle.setText(context.getResources().getString(R.string.client_debt_pay));
					etxtbal.setText(CommonMethods.getNumSeparator(CommonMethods.getDoubleFormate(clientDto.getBalanceAmount())));
					Log.e("Entro a abonar", CommonMethods.getNumSeparator(CommonMethods.getDoubleFormate(clientDto.getBalanceAmount())));
			}

			layoutHeader.setBackgroundColor(context.getResources().getColor(R.color.customer_tab));
			txtName.setText(context.getResources().getString(R.string.client_name) + " " + clientDto.getName());
			txtTotalDebt.setText(context.getResources().getString(R.string.total_debt) + " "
					+ CommonMethods.getNumSeparator(CommonMethods.getDoubleFormate(clientDto.getBalanceAmount())));
		}

		txtBalance.setText(context.getResources().getString(R.string.txt_amount));
		layoutName.setVisibility(View.VISIBLE);
		layoutReason.setVisibility(View.GONE);

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_dialog_save:

			validateAndSave();

			break;

		case R.id.btn_dialog_cancel:
			this.dismiss();
			break;
		case R.id.img_close:

			this.dismiss();

			break;
		default:
			break;
		}
	}

	private void validateAndSave() {
		if (type.equals(ConstantsEnum.SUPPLIER_PAY.code()))
			updateSupplierRecordsInDB();
		else
			updateClientRecordsInDB();

	}

	private void updateSupplierRecordsInDB() {
		String bal = etxtbal.getText().toString().trim();

		if (type.equals(ConstantsEnum.SUPPLIER_PAY.code()) && (bal.length() != 0) && (!"0".equals(bal))
				&& (!".".equals(bal)) && (CommonMethods.getDoubleFormate(bal) <= CommonMethods
						.getDoubleFormate(suppDto.getBalanceAmount().toString()))) {
			if (SupplierPaymentsDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
					getSupplierPaymentData())) {
				getCallTransaccionBoxService(ServiApplication.Supplier_Payments_M_name,
						ServiApplication.Supplier_Payments_TipoTrans, ServiApplication.Supplier_Payments_PaymentType);
				if (SupplierDAO.getInstance().updateDebtAmount(DBHandler.getDBObj(Constants.WRITABLE),
						getSupplierdata(bal))) {
					if (suppDto.getSyncStatus().equals(1)) {
						supplierpayment();
					} else {
							supplierpayment();
					}
				} else {
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.not_added));

				}
			} else {
				CommonMethods.showCustomToast(context, context.getResources().getString(R.string.not_added));

			}

		} else {
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.pay_warn_msg));
		}

	}

//	private boolean synkuserinfosup() {
//		if (new AsynkTaskClass().callcentralserver("/supplier/create", "" + ServiceHandler.POST,
//				makeSupplierjsonobj().toString().trim(), context) != null) {
//			if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
//				SupplierDTO dto = SupplierDAO.getInstance()
//						.getRecordsBySupplierID(DBHandler.getDBObj(Constants.WRITABLE), suppDto.getCedula());
//				dto.setSyncStatus(1);
//				SupplierDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), dto);
//				return true;
//			} else {
//				SupplierDTO dto = SupplierDAO.getInstance()
//						.getRecordsBySupplierID(DBHandler.getDBObj(Constants.WRITABLE), suppDto.getCedula());
//				SupplierDAO.getInstance().changesynckstatus(DBHandler.getDBObj(Constants.WRITABLE), dto);
//				return false;
//			}
//		} else {
//			SupplierDTO dto = SupplierDAO.getInstance().getRecordsBySupplierID(DBHandler.getDBObj(Constants.WRITABLE),
//					suppDto.getCedula());
//			SupplierDAO.getInstance().changesynckstatus(DBHandler.getDBObj(Constants.WRITABLE), dto);
//			return false;
//		}
//	}

	public void cReateSuplier() {
		if (new AsynkTaskClass().callcentralserver("/supplier/update", "" + ServiceHandler.POST,
				makeSupplierjsonobj().toString().trim(), context) != null) {
			if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
				supplierpayment();
			} else {
				supplierpayment();
			}
		}

	}

	public void supplierpayment() {
		SupplierDTO sdto=SupplierDAO.getInstance().getRecordsBySupplierID(DBHandler.getDBObj(Constants.WRITABLE),
				suppDto.getCedula());
		sdto.setSyncStatus(0);
		SupplierDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE),sdto);
		if (NetworkConnectivity.netWorkAvailability(context)) {
			
			new SupplierPayments().execute();
		} else {
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.no_wifi_adhoc));
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.update_msg));
			listener.onSave();
			dismiss();
		}
	}

	private class SupplierPayments extends AsyncTask<Void, Void, Void> {
		SupplierDTO supdto;
		private String bal = etxtbal.getText().toString().trim();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(context.getString(R.string.please_wait), context);
			supdto = SupplierDAO.getInstance().getRecordsBySupplierID(DBHandler.getDBObj(Constants.WRITABLE),
					suppDto.getCedula());
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (supdto.getSyncStatus() == 0) {
				List<DTO> supplierDetails = SupplierDAO.getInstance()
						.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "sync_status", "0");
				new ServiceHandler(context).makeServiceCall(ServiApplication.URL + "/sync", ServiceHandler.POST,
						new RequestFiels(context).addsupplier(supplierDetails));
			}
			ServiApplication.responds_feed = new ServiceHandler(context).makeServiceCall(
					ServiApplication.URL + "/supplier/post-payment", ServiceHandler.POST,
					makepayjsonobject_supplier(suppDto.getCedula().toString(),
							new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date()),
							bal));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.update_msg));
					listener.onSave();
					supdto.setSyncStatus(1);
					SupplierDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), suppDto);
					SupplierDAO.getInstance().updateDebtAmount(DBHandler.getDBObj(Constants.WRITABLE), suppDto);
					updatesupplier(supdto.getCedula());
					updatesupplierpayments();
					dismiss();
				} else {
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.update_msg));
					listener.onSave();
					SupplierDAO.getInstance().changesynckstatusasnzero(DBHandler.getDBObj(Constants.WRITABLE), supdto);
					dismiss();
				}
			} else {
				CommonMethods.showCustomToast(context, context.getResources().getString(R.string.update_msg));
				listener.onSave();
				SupplierDAO.getInstance().changesynckstatusasnzero(DBHandler.getDBObj(Constants.WRITABLE), supdto);
				dismiss();
			}
		}
	}

	private void updatesupplier(String supplierId) {
		ServiApplication.setSupplier = SupplierDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "cedula", supplierId);
		Intent intent = new Intent(context, SupplierUpdateService.class);
		context.startService(intent);
	}

	private SupplierDTO getSupplierdata(String balance) {
		Double amt = CommonMethods.getDoubleFormate(suppDto.getBalanceAmount())
				- CommonMethods.getDoubleFormate(balance);

		suppDto.setBalanceAmount(String.valueOf(amt));
		suppDto.setLastPaymentDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		return suppDto;
	}

	private List<DTO> getSupplierPaymentData() {

		List<DTO> supplierpaymentList = new ArrayList<DTO>();

		SupplierPaymentsDTO dto = new SupplierPaymentsDTO();
		dto.setSupplierId(suppDto.getCedula());
		dto.setAmountPaid(etxtbal.getText().toString().trim());
		dto.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		dto.setPaymentType(ServiApplication.SUPPLIER_PAYMENT_TYPE);
		dto.setPurchaseType(ConstantsEnum.SUPPLIER_PURCHASE_TYPE.code());
		dto.setSyncStatus(0);
		supplierpaymentList.add(dto);
		return supplierpaymentList;
	}

	private void updateClientRecordsInDB() {

		String bal = etxtbal.getText().toString().trim();

		if (type.equals(ConstantsEnum.CLIENT_PAY.code()) && (bal.length() != 0) && (!"0".equals(bal))
				&& (!".".equals(bal)) && (CommonMethods.getDoubleFormate(bal) <= CommonMethods
						.getDoubleFormate(clientDto.getBalanceAmount().toString()))) {
			if (ClientPaymentDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE),
					getClientPaymentsData())) {
				if (ClientDAO.getInstance().updateDebtAmount(DBHandler.getDBObj(Constants.WRITABLE), getClientData())) {
					if (clientDto.getSyncStatus().equals(1)) {
						postPaymentService();
					} else {
							postPaymentService();
					}
				} else {
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.not_added));

				}
			} else {
				CommonMethods.showCustomToast(context, context.getResources().getString(R.string.not_added));
			}

		} else if (type.equals(ConstantsEnum.CLIENT_LEND.code()) && (bal.length() != 0) && (!"0".equals(bal))
				&& (!".".equals(bal))) {
			dialogTitle.setText(context.getResources().getString(R.string.lendmoney));
			if (LendMoneyDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), getLendMoneyData())) {
				if (ClientDAO.getInstance().updateDebtAmount(DBHandler.getDBObj(Constants.WRITABLE),
						getClientLendData())) {
					if (clientDto.getSyncStatus().equals(1)) {
						lendMoneyService();
					} else {
							lendMoneyService();
					}
				} else {
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.not_added));
				}
			} else {
				CommonMethods.showCustomToast(context, context.getResources().getString(R.string.not_added));
			}

		} else {
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.pay_warn_msg));
		}
	}

	public void lendMoneyService() {
		ClientDTO cdto = ClientDAO.getInstance().getRecordsByClientCedula(DBHandler.getDBObj(Constants.READABLE),
				clientDto.getCedula());
		cdto.setSyncStatus(0);
		ClientDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE),cdto);

		if (NetworkConnectivity.netWorkAvailability(context)) {
			new CustomerLendMoney().execute();
		} else {
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.no_wifi_adhoc));
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.update_msg));
			dismiss();
			listener.onSave();
		}
	}

	private class CustomerLendMoney extends AsyncTask<Void, Void, Void> {
		ClientDTO cdto = ClientDAO.getInstance().getRecordsByClientCedula(DBHandler.getDBObj(Constants.READABLE),
				clientDto.getCedula());
		List<DTO> lislcient=new ArrayList<DTO>();
		private String bal = etxtbal.getText().toString().trim();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(context.getString(R.string.please_wait), context);
			lislcient.add(cdto);
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			new ServiceHandler(context).makeServiceCall(ServiApplication.URL + "/sync",
					ServiceHandler.POST, new RequestFiels(context).getClientTableData(lislcient));
			getCallTransaccionBoxService(ServiApplication.Customer_lend_Payments_M_name,
					ServiApplication.Customer_lendPayments_TipoTrans, ServiApplication.Customer_Payments_PaymentType);
			ServiApplication.responds_feed = new ServiceHandler(context).makeServiceCall(
					ServiApplication.URL + "/customer/lend-money", ServiceHandler.POST,
					makepayjsonobject(clientDto.getCedula().toString(),
							new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date()),
							bal));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
			if (ServiApplication.connectionTimeOutState) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.update_msg));
					listener.onSave();
					updateclient(cdto.getCedula());
					updatelendmoney();
					dismiss();
				} else {
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.update_msg));
					ClientDAO.getInstance().updateSynckstatus(DBHandler.getDBObj(Constants.WRITABLE), cdto);
					listener.onSave();
					dismiss();
				}
			} else {
				dismiss();
				listener.onSave();
			}
		}
	}

	private void postPaymentService() {
		
		ClientDTO cdto = ClientDAO.getInstance().getRecordsByClientCedula(DBHandler.getDBObj(Constants.READABLE),
				clientDto.getCedula());
		cdto.setSyncStatus(0);
		
		ClientDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE),cdto);
		
		if (NetworkConnectivity.netWorkAvailability(context)) {
			new CustomerPostPayments().execute();
		} else {
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.no_wifi_adhoc));
			CommonMethods.showCustomToast(context, context.getResources().getString(R.string.update_msg));
			listener.onSave();
			dismiss();
		}
	}

	private class CustomerPostPayments extends AsyncTask<Void, Void, Void> {
		ClientDTO cdto = ClientDAO.getInstance().getRecordsByClientCedula(DBHandler.getDBObj(Constants.READABLE),
				clientDto.getCedula());
		
		List<DTO> lislcient=new ArrayList<DTO>();
		private String bal = etxtbal.getText().toString().trim();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(context.getString(R.string.please_wait), context);
			lislcient.add(cdto);
		}

		@Override
		protected Void doInBackground(Void... params) {
			new ServiceHandler(context).makeServiceCall(ServiApplication.URL + "/sync",
					ServiceHandler.POST, new RequestFiels(context).getClientTableData(lislcient));
			getCallTransaccionBoxService(ServiApplication.Customer_Payments_M_name,
					ServiApplication.Customer_PostPayments_TipoTrans, ServiApplication.Customer_Payments_PaymentType);
			ServiApplication.responds_feed = new ServiceHandler(context).makeServiceCall(
					ServiApplication.URL + "/customer/post-payment", ServiceHandler.POST,
					makepayjsonobject(clientDto.getCedula().toString(),
							new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date()),
							bal));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.dismissProgressDialog();
			if (ServiApplication.connectionTimeOutState) {

				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.update_msg));
					listener.onSave();
					updateclient(cdto.getCedula());
					updateclientpostpayment();
					dismiss();
				} else {
					ClientDAO.getInstance().updateSynckstatus(DBHandler.getDBObj(Constants.WRITABLE), cdto);
					CommonMethods.showCustomToast(context, context.getResources().getString(R.string.update_msg));
					listener.onSave();
					dismiss();

				}
			} else {
				dismiss();
				listener.onSave();
			}
		}
	}

	private void updateclient(String cedula) {
		ServiApplication.setClicent = ClientDAO.getInstance()
				.getRecordsWithValues(DBHandler.getDBObj(Constants.READABLE), "cedula", cedula);
		Intent intent = new Intent(context, ClientUpdateService.class);
		context.startService(intent);

	}



	private ClientDTO getClientLendData() {

		Double amt = CommonMethods.getDoubleFormate(clientDto.getBalanceAmount())
				+ CommonMethods.getDoubleFormate(etxtbal.getText().toString());
		clientDto.setBalanceAmount(String.valueOf(amt));
		clientDto.setLastPaymentDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));

		return clientDto;
	}

	private List<DTO> getLendMoneyData() {

		List<DTO> clientLendList = new ArrayList<DTO>();

		LendmoneyDTO dto = new LendmoneyDTO();
		dto.setClientId(clientDto.getCedula());
		dto.setAmount(etxtbal.getText().toString());
		dto.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		dto.setSyncStatus(0);

		clientLendList.add(dto);
		return clientLendList;
	}

	private ClientDTO getClientData() {

		Double amt = CommonMethods.getDoubleFormate(clientDto.getBalanceAmount())
				- CommonMethods.getDoubleFormate(etxtbal.getText().toString());
		clientDto.setBalanceAmount(String.valueOf(amt));
		clientDto.setLastPaymentDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));

		return clientDto;
	}

	private List<DTO> getClientPaymentsData() {
		List<DTO> clientpaymentList = new ArrayList<DTO>();

		ClientPaymentsDTO dto = new ClientPaymentsDTO();
		dto.setClientId(clientDto.getCedula());
		dto.setAmountPaid(etxtbal.getText().toString());
		dto.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		dto.setIncomeType(ConstantsEnum.INCOME_TYPE_DEBIT.code());
		dto.setPaymentType(ConstantsEnum.PAYMENT_TYPE_CASH.code());
		dto.setSyncStatus(0);
		clientpaymentList.add(dto);
		return clientpaymentList;
	}

	private String makepayjsonobject(String customer_code, String payment_date, String amount) {
		JSONObject jsonobject = new JSONObject();

		try {
			jsonobject.put("customer_code", customer_code);
			jsonobject.put("payment_date", payment_date);
			jsonobject.put("amount", amount);
			jsonobject.put("payment_type", "cash");
			jsonobject.put("date_time", payment_date);
			jsonobject.put("created_by", ServiApplication.userName);
			jsonobject.put("store_code", ServiApplication.store_id);
			return jsonobject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String makepayjsonobject_supplier(String supplier_code, String payment_date, String amount) {
		JSONObject jsonobject = new JSONObject();
		try {
			jsonobject.put("supplier_code", supplier_code);
			jsonobject.put("payment_date", payment_date);
			jsonobject.put("amount", Double.parseDouble(amount));
			jsonobject.put("payment_type", ServiApplication.SUPPLIER_PAYMENT_TYPE);
			jsonobject.put("date_time", payment_date);
			jsonobject.put("created_by", ServiApplication.userName);
			jsonobject.put("store_code", ServiApplication.store_id);
			return jsonobject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String makejsonobj(ClientDTO clientDto2) {

		try {
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("customer_code", clientDto2.getCedula());
			jsonobj.put("name", clientDto2.getName());
			jsonobj.put("telephone", clientDto2.getTelephone());
			jsonobj.put("address", clientDto2.getAddress());
			jsonobj.put("email", clientDto2.getEmail());
			if (clientDto2.getGender().equals("0")) {
				jsonobj.put("gender", "Male");
			} else {
				jsonobj.put("gender", "Female");
			}
			jsonobj.put("modified_by", ServiApplication.userName);
			jsonobj.put("active_status", "true");
			jsonobj.put("balance_amount", CommonMethods.getDoubleFormate(clientDto2.getBalanceAmount()));
			jsonobj.put("store_code", ServiApplication.store_id);
			jsonobj.put("initial_debts", CommonMethods.getDoubleFormate(clientDto2.getInitialDebt()));
			return jsonobj.toString();
		} catch (Exception e) {
			return null;
		}
	}

	private String makeSupplierjsonobj() {

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("supplier_code", suppDto.getCedula());
			jsonobj.put("name", suppDto.getName());
			jsonobj.put("logo", suppDto.getLogo());
			jsonobj.put("address", suppDto.getAddress());
			jsonobj.put("telephone", suppDto.getContactTelephone());
			jsonobj.put("contact_name", suppDto.getContactName());
			jsonobj.put("contact_telephone", suppDto.getTelephone());
			jsonobj.put("visit_days", suppDto.getVisitDays());
			jsonobj.put("visit_frequency", suppDto.getVisitFrequency());
			jsonobj.put("active_status", "true");
			jsonobj.put("created_by", ServiApplication.userName);
			jsonobj.put("balance_amount", suppDto.getBalanceAmount());
			jsonobj.put("store_code", ServiApplication.store_id);
			return jsonobj.toString();
		} catch (JSONException e) {
			return null;
		}
	}

	private void updateclientpostpayment() {
		List<DTO> postpayments = getClientPaymentsData();
		ClientPaymentsDTO dto = (ClientPaymentsDTO) postpayments.get(0);
		ClientPaymentDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), dto);
	}

	private void updatelendmoney() {
		List<DTO> ldto = getLendMoneyData();
		LendmoneyDTO dto = (LendmoneyDTO) ldto.get(0);
		LendMoneyDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), dto);
	}

	private void updatesupplierpayments() {
		List<DTO> spdto = getSupplierPaymentData();
		SupplierPaymentsDTO dto = (SupplierPaymentsDTO) spdto.get(0);
		SupplierPaymentsDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), dto);
	}

	@SuppressWarnings("unused")
	private void getCallTransaccionBoxService(String module_nem, String tipo, String PaymentType) {
		List<DTO> dto = new ArrayList<DTO>();
		TransaccionBoxDTO tdto = new TransaccionBoxDTO();
		tdto.setAmount(etxtbal.getText().toString().trim());
		tdto.setModule_name(module_nem);
		tdto.setSyncstatus(0);
		tdto.setTipo_transction(tipo);
		tdto.setTransaction_type(PaymentType);
		tdto.setDatetime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
		tdto.setUsername(sharedpreferences.getString("user_name", ""));
		tdto.setStore_code(sharedpreferences.getString("store_code", ""));
		dto.add(tdto);
		if (TransaccionBoxDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), dto)) {
			if (CommonMethods.getInternetSpeed(context) >= ServiApplication.local_data_speed) {
				Intent intent = new Intent(context, TransaccionBoxService.class);
				context.startService(intent);
			} else {
				ServiApplication.connectionTimeOutState = false;
			}
		}
	}

}
