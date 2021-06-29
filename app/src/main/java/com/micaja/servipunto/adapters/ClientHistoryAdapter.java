package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
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
import com.micaja.servipunto.database.dao.UserDetailsDAO;
import com.micaja.servipunto.database.dto.ClientHistoryDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.dialog.ClientInvoiceDetailsDialog;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.NetworkConnectivity;

public class ClientHistoryAdapter extends BaseAdapter {

	private Context context;
	private List<ClientHistoryDTO> clientHistoryList;
	private LayoutInflater inflater;
	ServiApplication appContext;

	public ClientHistoryAdapter(Context context, List<ClientHistoryDTO> clientHistoryDTO) {
		this.context = context;
		this.clientHistoryList = clientHistoryDTO;
		appContext = (ServiApplication) context.getApplicationContext();
		// Se ordena ascendentemente la lista por la fecha a petición del requerimiento 4.3.2.4 D
		if(clientHistoryList != null && !clientHistoryList.isEmpty()){
			Collections.sort(clientHistoryList, new Comparator<ClientHistoryDTO>() {
				public int compare(ClientHistoryDTO a, ClientHistoryDTO b) {
					try {
						return (a.getDateTime().compareTo(b.getDateTime()));
					} catch (Exception e) {
						e.printStackTrace();
						return 0;
					}
				}
			});
		}
	}

	@Override
	public int getCount() {
		return clientHistoryList.size();
	}

	public void setData(List<ClientHistoryDTO> clientResultsList) {
		this.clientHistoryList = clientResultsList;
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
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.history_row, null);
		}

		final TextView date = (TextView) convertView.findViewById(R.id.txt_datetime);
		final TextView concept = (TextView) convertView.findViewById(R.id.txt_concept);
		final TextView debut = (TextView) convertView.findViewById(R.id.txt_debut);
		final TextView pay = (TextView) convertView.findViewById(R.id.txt_pay);
		final TextView balance = (TextView) convertView.findViewById(R.id.txt_bal);

		//date.setText(Dates.getStringDate(clientHistoryList.get(position).getDateTime()));
		String formatedDate = Dates.getDate_DD_MM_YYYY_HH_MM(clientHistoryList.get(position).getDateTime());
		date.setText(formatedDate);

		if (clientHistoryList.get(position).getPaymentType().equalsIgnoreCase("lend_data")) {
			concept.setText(context.getString(R.string.lbl_lend_money)); // Préstamo dinero
			//debut.setText(clientHistoryList.get(position).getAmount());
			debut.setText(CommonMethods.getNumSeparator(CommonMethods.getDoubleFormate(clientHistoryList.get(position).getAmount())));
			balance.setText("");
			pay.setText("");
		} else if (clientHistoryList.get(position).getPaymentType().equalsIgnoreCase("payments_data")) {
			concept.setText(context.getString(R.string.lbl_payment_payment)); // Abono/pago
			debut.setText("");
			balance.setText("");
			//pay.setText(clientHistoryList.get(position).getAmount());
			pay.setText(CommonMethods.getNumSeparator(CommonMethods.getDoubleFormate(clientHistoryList.get(position).getAmount())));
		} else if (clientHistoryList.get(position).getPaymentType().equalsIgnoreCase("balence")) {
			concept.setText(context.getString(R.string.lbl_purchase)); // Compra
			debut.setText("");
			//balance.setText(clientHistoryList.get(position).getAmount());
			balance.setText(CommonMethods.getNumSeparator(CommonMethods.getDoubleFormate(clientHistoryList.get(position).getAmount())));
			pay.setText("");
		} else if (clientHistoryList.get(position).getPaymentType().equalsIgnoreCase("customer")) {
			concept.setText(context.getString(R.string.lbl_initial_debt)); // Deuda inicial
			//debut.setText(clientHistoryList.get(position).getAmount());
			debut.setText(CommonMethods.getNumSeparator(CommonMethods.getDoubleFormate(clientHistoryList.get(position).getAmount())));
			balance.setText("");
			pay.setText("");
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (clientHistoryList.get(position).getSale_id().equals("00")) {

				} else {
					if (NetworkConnectivity.netWorkAvailability(context)) {
						UserDetailsDTO userDTO = UserDetailsDAO.getInstance()
								.getUserRecords(
										DBHandler.getDBObj(Constants.READABLE));
						new GetInvoiceDetails().execute(""
								+ clientHistoryList.get(position).getSale_id(),
								userDTO.getNitShopId());
					} else {
						CommonMethods.showCustomToast(context,
								context.getString(R.string.no_wifi_adhoc));
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

					ClientInvoiceDetailsDialog dialog = new ClientInvoiceDetailsDialog(
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

	public String getJosn(String string, String string2) {
		JSONObject json = new JSONObject();
		try {
			json.put("invoice_number", string);
			json.put("store_code", string2);
		} catch (Exception e) {
		}
		return json.toString();
	}

}