package com.micaja.servipunto.adapters;

import android.content.Context;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;

import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.DeliveryDAO;
import com.micaja.servipunto.database.dao.DeliveryPaymentDAO;

import com.micaja.servipunto.database.dao.UserDetailsDAO;

import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DeliveryDTO;
import com.micaja.servipunto.database.dto.DeliveryHistoryDTO;
import com.micaja.servipunto.database.dto.DeliveryPaymentsDTO;

import com.micaja.servipunto.database.dto.UserDetailsDTO;

import com.micaja.servipunto.dialog.DeliveryInvoiceDetailsDialog;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.NetworkConnectivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DeliveryHistoryAdapter extends BaseAdapter {

	private Context context;
	private List<DeliveryHistoryDTO> deliveryHistoryList;
	private LayoutInflater inflater;
	private String DeliveryID;
	ServiApplication appContext;
	private Double amountPay;
	private int ih_req_count = 1;
	private String store_code;


	public DeliveryHistoryAdapter(Context context,
								  List<DeliveryHistoryDTO> deliveryHistoryDTO, String DeliveryId) {
		this.DeliveryID = DeliveryId;
		this.context = context;
		this. deliveryHistoryList = sortDeliveryHistoryList(deliveryHistoryDTO);
		appContext = (ServiApplication) context.getApplicationContext();
		UserDetailsDTO userDTO = UserDetailsDAO.getInstance()
				.getUserRecords(
						DBHandler.getDBObj(Constants.READABLE));
		store_code = userDTO.getNitShopId();

	}

	@Override
	public int getCount() {
		return  deliveryHistoryList.size();
	}

	public void setData(List<DeliveryHistoryDTO> deliveryResultsList) {
		this. deliveryHistoryList = sortDeliveryHistoryList(deliveryResultsList);
		notifyDataSetChanged();

	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.delivery_history_row, null);
		}


		final TextView date = (TextView) convertView
				.findViewById(R.id.txt_datetime);

		final TextView debut = (TextView) convertView
				.findViewById(R.id.txt_debut);
		final TextView pay = (TextView) convertView.findViewById(R.id.txt_pay);
		final TextView balance = (TextView) convertView
				.findViewById(R.id.txt_bal);
		final  TextView concept = (TextView) convertView.findViewById(R.id.txt_detail);


		 if ( deliveryHistoryList.get(position).getPaymentType()
				.equalsIgnoreCase("balence")) {
			date.setText(Dates.getDate_DD_MM_YYYY_HH_MM(deliveryHistoryList.get(position)
					.getDateTime()));
			debut.setText("");
			balance.setText( CommonMethods.getNumSeparator(deliveryHistoryList.get(position).getPay_amount()));
			pay.setText("");
			 concept.setText("Saldo");
		} else if ( deliveryHistoryList.get(position).getPaymentType()
				.equalsIgnoreCase("delivery")) {
			date.setText(Dates.getDate_DD_MM_YYYY_HH_MM(deliveryHistoryList.get(position)
					.getDateTime(),-5));
			debut.setText(  CommonMethods.getNumSeparator(deliveryHistoryList.get(position).getPay_amount())+"");
			balance.setText("");
			pay.setText("");
			 concept.setText("Dinero Base");
		}
		else{
			 date.setText(Dates.getDate_DD_MM_YYYY_HH_MM(deliveryHistoryList.get(position)
					 .getDateTime(), -5));
			 debut.setText( CommonMethods.getNumSeparator(deliveryHistoryList.get(position).getDeb_amount())+"");
			 pay.setText( CommonMethods.getNumSeparator(deliveryHistoryList.get(position).getPay_amount())+"");
			 if (deliveryHistoryList.get(position).getSale_id().equals("00")) {
				 balance.setText("");
				 concept.setText("Abono");
			 }else {
				 balance.setText(CommonMethods.getDoubleFormate(deliveryHistoryList.get(position).getDeb_amount())==0.00 ? "":"Pagar");
				 concept.setText("Entrega");
			 }
		 }


		debut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (deliveryHistoryList.get(position).getSale_id().equals("00")) {

				} else {
					if (NetworkConnectivity.netWorkAvailability(context)) {

						new GetInvoiceDetails().execute(""
										+ deliveryHistoryList.get(position).getSale_id(),
								store_code);
					} else {
						CommonMethods.showCustomToast(context,
								context.getString(R.string.no_wifi_adhoc));
					}
				}
			}
		});

		pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (deliveryHistoryList.get(position).getSale_id().equals("00")) {

				} else {
					if (NetworkConnectivity.netWorkAvailability(context)) {

						new GetInvoiceDetails().execute(""
										+ deliveryHistoryList.get(position).getSale_id(),
								store_code);
					} else {
						CommonMethods.showCustomToast(context,
								context.getString(R.string.no_wifi_adhoc));
					}
				}
			}
		});

		balance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!deliveryHistoryList.get(position).getSale_id().equals("") &&
						!deliveryHistoryList.get(position).getSale_id().equals("00") &&
						!deliveryHistoryList.get(position).getDeb_amount().equals("0.0")) {
					if (NetworkConnectivity.netWorkAvailability((context))) {


						double totalAmount = CommonMethods.getDoubleFormate(deliveryHistoryList.get(position).getDeb_amount()) +
								CommonMethods.getDoubleFormate(deliveryHistoryList.get(position).getPay_amount());
						amountPay = CommonMethods.getDoubleFormate(deliveryHistoryList.get(position).getDeb_amount());
						new PayInvoice().execute(""
										+ deliveryHistoryList.get(position).getSale_id(),
								store_code, "" + totalAmount);



					}
				}
			}
		});
		return convertView;
	}

	private class GetInvoiceDetails extends AsyncTask<String, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(context);
		String responds;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
		}

		@Override
		protected Void doInBackground(String... params) {
			responds = servicehandler.makeServiceCall(ServiApplication.URL
							+ "/invoice/details", ServiceHandler.POST,
					getJosn(params[0], params[1]));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.progressDialog.dismiss();
			try {
				JSONObject json = new JSONObject(responds.toString());
				if (json.getInt("status")==0) {

					DeliveryInvoiceDetailsDialog dialog = new DeliveryInvoiceDetailsDialog(
							context, responds.toString());
					dialog.show();
				} else {
					CommonMethods.showCustomToast(
							context,context.getResources().getString(
									R.string.invoice_status));
				}
			} catch (Exception e) {

			}
		}
	}

	private class PayInvoice extends AsyncTask<String, Void, Void> {

		ServiceHandler servicehandler = new ServiceHandler(context);
		String responds;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CommonMethods.showProgressDialog(
					context.getString(R.string.please_wait), context);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				responds = servicehandler.makeServiceCall(ServiApplication.URL
								+ "/delivery/pay-invoice", ServiceHandler.POST,
						getJosn(params[0], params[1], params[2]));
				return null;
			}
			catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.progressDialog.dismiss();
			try {
				JSONObject json = new JSONObject(responds.toString());
				if (json.getInt("status")==0) {
					List<DTO> list = new ArrayList<DTO>();
					DeliveryPaymentsDTO deliveryPaymentsDTO = new DeliveryPaymentsDTO();
					deliveryPaymentsDTO.setDelieryId(DeliveryID);
					deliveryPaymentsDTO.setIncomeType(ConstantsEnum.DELIVERY_PAY_INVOICE.code());
					deliveryPaymentsDTO.setDateTime(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
					deliveryPaymentsDTO.setSyncStatus(1);
					deliveryPaymentsDTO.setAmountPaid(amountPay.toString());
					deliveryPaymentsDTO.setPaymentType(ConstantsEnum.DELIVERY_PAYMENT.code());
					list.add(deliveryPaymentsDTO);
					DeliveryPaymentDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), list);
					reloadAdapter();

				} else {
					CommonMethods.showCustomToast(
							context,"Ha Ocurrido un error en el Pago de la Factura, Sincronice la Información");
				}
			} catch (Exception e) {
				e.printStackTrace();
				CommonMethods.showCustomToast(
						context, "Ha Ocurrido un error en el Pago de la Factura, Intente Más Tarde");

			}
		}
	}

	public String getJosn(String string, String string2) {
		JSONObject json = new JSONObject();
		try {
			json.put("invoice_number", string);
			json.put("store_code", string2);

		} catch (Exception e) {
		}
		return json.toString();
	}

	public String getJosn(String string, String string2, String string3) {
		JSONObject json = new JSONObject();
		try {
			json.put("invoice_no", string);
			json.put("store_code", string2);
			json.put("amount_paid", string3);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public void reloadAdapter() {

		if (NetworkConnectivity.netWorkAvailability(context)) {
			new GetInventoryHistory().execute();
		} else {
			CommonMethods.showCustomToast(context,
					"No Hay Conexión");
		}
	}

	public class GetInventoryHistory extends AsyncTask<Void, Void, Void> {

			ServiceHandler servicehandler = new ServiceHandler(
					context);
			String responds = null;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				CommonMethods.showProgressDialog("Por favor espere",
						context);
			}

			@Override
			protected Void doInBackground(Void... params) {


				responds = servicehandler.makeServiceCall(ServiApplication.URL
						+ "/delivery/historic-debt/" + DeliveryID +"/"+ store_code+ "?page="
						+ ih_req_count, ServiceHandler.GET);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				CommonMethods.dismissProgressDialog();
				if (ServiApplication.connectionTimeOutState) {
					if (new JSONStatus().status(responds) == 0) {
						try {
							JSONObject json = new JSONObject(responds);
							JSONObject json1 = json.getJSONObject("data");
							JSONObject pagenation = json1
									.getJSONObject("paginator");
							ih_req_count = pagenation.getInt("next_page");
							getInventoryInvoiceDTOData(responds);
							reloadAdapter();
						} catch (Exception e) {
							System.out.println("==========================");
							getInventoryInvoiceDTOData(responds);
							setData(deliveryHistoryList);

						}
					}
					else {
						CommonMethods.showCustomToast(context,"Error en presentar la información");
					}
				}
			}
		}

	private void getInventoryInvoiceDTOData(String responds2) {

		try {
			deliveryHistoryList.clear();
			JSONObject json = new JSONObject(responds2);
			JSONObject datajsonobj = json.getJSONObject("data");
			JSONObject customerJSONObj = datajsonobj.getJSONObject("delivery");

			DeliveryHistoryDTO deliveryHistoryDTO = new DeliveryHistoryDTO(
					"delivery", customerJSONObj.getString("initial_debts"),"",
					Dates.serverdateformateDateObj(customerJSONObj
							.getString("created_date")), "00");
			deliveryHistoryList.add(deliveryHistoryDTO);

			JSONObject balenceJSONObj = datajsonobj.getJSONObject("delivery");
			DeliveryHistoryDTO balenceJSONObjHistoryDto = new DeliveryHistoryDTO(
					"balence", balenceJSONObj.getString("balance_amount"),"",
					new Date(), "00");

			deliveryHistoryList.add(balenceJSONObjHistoryDto);


			JSONArray paymentsDataArray = datajsonobj
					.getJSONArray("payments_data");
			for (int i = 0; i < paymentsDataArray.length(); i++) {
				JSONObject paymentsDataJSONObj = paymentsDataArray
						.getJSONObject(i);
				try {
					DeliveryHistoryDTO paymentsDataDeliveryHistoryDto = new DeliveryHistoryDTO(
							"payments_data",
							paymentsDataJSONObj.getString("amount_paid"),"0.0",
							Dates.serverdateformateDateObj(paymentsDataJSONObj
									.getString("date_time")), ""
							+ paymentsDataJSONObj.getLong("invoice_no"));
					deliveryHistoryList.add(paymentsDataDeliveryHistoryDto);
				} catch (Exception e) {
					DeliveryHistoryDTO paymentsDataDeliveryHistoryDto = new DeliveryHistoryDTO(
							"payments_data",
							paymentsDataJSONObj.getString("amount_paid"),"",
							Dates.serverdateformateDateObj(paymentsDataJSONObj
									.getString("date_time")), "00");
					deliveryHistoryList.add(paymentsDataDeliveryHistoryDto);
				}

			}

			JSONArray lendDataArray = datajsonobj.getJSONArray("lend_data");
			for (int i = 0; i < lendDataArray.length(); i++) {
				JSONObject lendDataJSONObj = lendDataArray.getJSONObject(i);
				try {
					DeliveryHistoryDTO lendDataDeliveryHistoryDto = new DeliveryHistoryDTO(
							"lend_data","0.0", lendDataJSONObj.getString("amount"),
							Dates.serverdateformateDateObj(lendDataJSONObj
									.getString("date_time")), "" + lendDataJSONObj.getLong("invoice_no"));
					foundSaleID(lendDataDeliveryHistoryDto);



				} catch (Exception e) {
					DeliveryHistoryDTO paymentsDataDeliveryHistoryDto = new DeliveryHistoryDTO(
							"lend_data",
							"",lendDataJSONObj.getString("amount"),
							Dates.serverdateformateDateObj(lendDataJSONObj
									.getString("date_time")), "00");
					deliveryHistoryList.add(paymentsDataDeliveryHistoryDto);
				}

			}

		} catch (JSONException e) {
		}

		Log.v("varahalababu", "====count======" + deliveryHistoryList.size());
	}

	private void foundSaleID (DeliveryHistoryDTO deliveryHistoryDTO){

		DeliveryHistoryDTO deliveryHistoryDTO1;
		for (int i = 0; i < deliveryHistoryList.size(); i++){
			deliveryHistoryDTO1 = deliveryHistoryList.get(i);
			if (deliveryHistoryDTO.getSale_id().equals(deliveryHistoryDTO1.getSale_id())){
				deliveryHistoryDTO1.setDeb_amount(deliveryHistoryDTO.getDeb_amount());
				deliveryHistoryList.set(i,deliveryHistoryDTO1);
				break;
			}
		}
	}

	private List<DeliveryHistoryDTO> sortDeliveryHistoryList (List<DeliveryHistoryDTO> listHistory){
		Collections.sort(listHistory);
		DeliveryHistoryDTO balance = null,initialDebt = null;
		List<DeliveryHistoryDTO> deliveryHistoryDTOs = new ArrayList<DeliveryHistoryDTO>(listHistory);
		for (DeliveryHistoryDTO dto: listHistory){
			if(dto.getPaymentType().equals("balence")){
				deliveryHistoryDTOs.remove(dto);
				balance = dto;
			}
			else if (dto.getPaymentType().equals("delivery")){
				deliveryHistoryDTOs.remove(dto);
				initialDebt = dto;
			}

			if (initialDebt != null && balance != null)
				break;
		}
		deliveryHistoryDTOs.add(0,balance);
		deliveryHistoryDTOs.add(1,initialDebt);

		//Update amounts Delivery from Server
		DeliveryDTO deliveryDTO = DeliveryDAO.getInstance().getRecordsByDeliveryCedula(DBHandler.getDBObj(Constants.READABLE),DeliveryID);
		deliveryDTO.setBalanceAmount(balance.getPay_amount());
		deliveryDTO.setInitialDebt(initialDebt.getPay_amount());
		DeliveryDAO.getInstance().updateDeliveryData(DBHandler.getDBObj(Constants.WRITABLE), deliveryDTO);
	return deliveryHistoryDTOs;
	}
}

