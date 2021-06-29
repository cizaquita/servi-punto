/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.ReportDetailsActivity;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.InventoryAmountDTO;
import com.micaja.servipunto.servicehandler.ParsingHandler;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ReportDetailsAdapter extends BaseAdapter {

	private Context context;
	private List<DTO> reportDetails = new ArrayList<DTO>();
	private LayoutInflater inflater;
	private int reportPosition;
	private TextView txt_quantitysold;

	public ReportDetailsAdapter(Context context, List<DTO> reportDetailsList,
			int reportPosition) {
		this.context = context;
		this.reportDetails = reportDetailsList;
		this.reportPosition = reportPosition;
	}

	public void setData(List<DTO> reportDetailsList) {
		this.reportDetails = reportDetailsList;
		notifyDataSetChanged();
	}

	public double getTotalDebt() {
		double totalDebt = 0;
		try {

			for (int i = 0; i < reportDetails.size(); i++) {
				InventoryAmountDTO dto = (InventoryAmountDTO) reportDetails.get(i);
				totalDebt += Double.parseDouble(dto.getselling());
			}
			return Double.parseDouble(CommonMethods.getNumSeparator(totalDebt));
		}catch (Exception e ){
			return totalDebt;
		}
	}

	public void refreshList() {
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return reportDetails.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.reports_detail_row, null);
		}

		final TextView quantity = (TextView) convertView
				.findViewById(R.id.txt_quantity);
		quantity.setVisibility(View.GONE);
		final TextView product = (TextView) convertView
				.findViewById(R.id.txt_product);
		final TextView profitability = (TextView) convertView
				.findViewById(R.id.txt_profitability);
		final TextView qtySold = (TextView) convertView
				.findViewById(R.id.txt_quantitysold);
		final TextView losers = (TextView) convertView
				.findViewById(R.id.txt_total_losers);
		final View rview = (View) convertView.findViewById(R.id.report_view);
		final View rview2 = (View) convertView.findViewById(R.id.report_view2);
		final TextView state = (TextView) convertView
				.findViewById(R.id.txt_state);

		final View rview3 = (View) convertView.findViewById(R.id.report_view3);
		final TextView operator = (TextView) convertView
				.findViewById(R.id.txt_operator);

		final View rview4 = (View) convertView.findViewById(R.id.report_view4);
		final TextView facturas = (TextView) convertView
				.findViewById(R.id.txt_facturas);

		final InventoryAmountDTO dto = (InventoryAmountDTO) reportDetails.get(position);

		if (reportPosition == 1) { // productos por rentabilidad
			// Se intercambia el orden de las columnas y se cambia el código del proveedor por unidades vendidas 4.3.7.1 A
			//product.setText(dto.getInventoryName());
			//profitability.setText(dto.getselling());
			profitability.setText(dto.getInventoryName()); // producto
			//product.setText(dto.getselling()); // unidades vendidas
			qtySold.setText(CommonMethods.getRoundedVal(Double.parseDouble(dto.getpurchase()))); // rentabilidad acumulada
			SharedPreferences sharedpreferences = context.getSharedPreferences(ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
			Log.e("fromDate -->", sharedpreferences.getString("fromDate", "no encontro fecha origen"));
			Log.e("toDate -->", sharedpreferences.getString("toDate", "no encontro fecha destino"));
			new GetReportDetails(3, sharedpreferences.getString("fromDate", ""), sharedpreferences.getString("toDate", ""), dto, product).execute();
		} else if (reportPosition == 2) {
			product.setText(dto.getInventoryName());
			profitability.setText(dto.getselling());
			qtySold.setText(CommonMethods.getRoundedVal(Double.parseDouble(dto
					.getpurchase())));
		} else if (reportPosition == 3) { // Producto más vendido
			// Se intercambia el nombre de las columnas a petición del requerimiento 4.3.7.2 B
			//product.setText(dto.getInventoryName());
			//profitability.setText(dto.getselling());
			profitability.setText(dto.getInventoryName()); // producto
			product.setText(dto.getselling()); // proveedor
			qtySold.setText(dto.getpurchase()); // cantidad
		} else if (reportPosition == 4) { // Día más rentable
			profitability.setText(Dates.formatDateToDD_MM_YYYY(dto.getpurchase())); // fecha
			Log.e("Fecha -->", Dates.formatDateToDD_MM_YYYY(dto.getpurchase()));
			product.setText(CommonMethods.getRoundedVal(Double.parseDouble(dto.getInventoryName()))); // rentabilidad
			//profitability.setText(Dates.formateyyyyMMdd(dto.getpurchase())); // Fecha
			qtySold.setVisibility(View.GONE);
		} else if (reportPosition == 5) { // Histórico de Caja
			// Se agrega la columna fecha a petición del requerimiento 4.3.7.4 B (columna quantity)
			quantity.setText(Dates.formatDateToDD_MM_YYYY(dto.getFechaHora()));
			quantity.setVisibility(View.VISIBLE);
			//product.setText(dto.getInventoryName());
			//profitability.setText(dto.getpurchase());
			// Se cambia el nombre de las columnas a petición del requerimiento 4.3.7.4 C
			product.setText(getConceptValue(dto.getpurchase().toLowerCase())); // concepto
			profitability.setText(getTypeValue(dto.getInventoryName().toLowerCase())); // tipo de movimiento
			qtySold.setText(CommonMethods.getRoundedVal(Double.parseDouble(dto.getselling())));
		} else if (reportPosition == 6) { // Cuentas por cobrar
			product.setText(dto.getInventoryName());
			profitability.setText(dto.getpurchase()); // Teléfono
			qtySold.setText(CommonMethods.getRoundedVal(Double.parseDouble(dto.getselling())));
			if((reportDetails.size() - 1) == position){ // se verifica si es el último registro para colocar el total
				profitability.setText(R.string.sale_print_hedder_14); // Total
				qtySold.setText(String.valueOf(getTotalDebt()));
			}
		} else if (reportPosition == 7) { // Cuentas por pagar
			product.setText(dto.getInventoryName());
			profitability.setText(dto.getpurchase());
			qtySold.setText(CommonMethods.getRoundedVal(Double.parseDouble(dto.getselling())));
			losers.setText(dto.getdebt_days());
			losers.setVisibility(View.VISIBLE);
			rview.setVisibility(View.VISIBLE);
			// requerimiento 4.3.7.6 A
			if((reportDetails.size() - 1) == position){ // se verifica si es el último registro para colocar el total
				profitability.setText(R.string.sale_print_hedder_14); // Total
				qtySold.setText(String.valueOf(getTotalDebt()));
			}
		} else if (reportPosition == 8) { // Valor del inventario en el establecimiento
			quantity.setText(dto.getQuantity());
			quantity.setVisibility(View.VISIBLE);
			product.setText(dto.getInventoryName());
			profitability.setText(CommonMethods.getRoundedVal(Double.parseDouble(dto.getpurchase())));
			qtySold.setText(CommonMethods.getRoundedVal(Double.parseDouble(dto.getselling())));
		} else if (reportPosition == 9) { // Productos vencidos caducados
			product.setText(dto.getInventoryName()); // Descripción del producto
			profitability.setText(dto.getpurchase());
			qtySold.setText(dto.getselling());
			losers.setText(CommonMethods.getRoundedVal(Double.parseDouble(dto.getdebt_days()))); // Valor total
			losers.setVisibility(View.VISIBLE);
			rview.setVisibility(View.VISIBLE);
		} else if (reportPosition == 10) {
			product.setText(dto.getdebt_days());
			profitability.setText(dto.getpurchase());
			qtySold.setText(dto.getInventoryName());
			losers.setText(CommonMethods.getRoundedVal(Double.parseDouble(dto
					.getselling())));
			losers.setVisibility(View.VISIBLE);
			rview.setVisibility(View.VISIBLE);
		} else if (reportPosition == 11) {
			product.setText(dto.getProduct());
			profitability.setText(dto.getQuantity());
			qtySold.setText(dto.getSumatoria());
		} else if (reportPosition == 12) {
			losers.setVisibility(View.VISIBLE);
			rview.setVisibility(View.VISIBLE);
			state.setVisibility(View.VISIBLE);
			rview2.setVisibility(View.VISIBLE);
			
			product.setText(dto.getIdTransaccion());
			profitability.setText(Dates.formateyyyyMMdd(dto.getFechaHora()));
			try {
				qtySold.setText(""+Math.round(Double.parseDouble(dto.getTipo())));
			} catch (Exception e) {
				qtySold.setText(dto.getTipo());
			}
			if (dto.getValor().equals("1")) {
				losers.setText(context.getString(R.string.sucess));
			}else {
				losers.setText(context.getString(R.string.rejected));
			}
			
			state.setText(dto.getEstado());
			
			
		} else if (reportPosition == 13) {
			qtySold.setVisibility(View.GONE);
			product.setText(dto.getdebt_days());
			profitability.setText(dto.getEstado());
		} else if (reportPosition == 14) {
			losers.setVisibility(View.VISIBLE);
			rview.setVisibility(View.VISIBLE);
			state.setVisibility(View.VISIBLE);
			rview2.setVisibility(View.VISIBLE);
			rview3.setVisibility(View.VISIBLE);
			operator.setVisibility(View.VISIBLE);
			product.setText(dto.getpurchase());
			profitability.setText(dto.getIdTransaccion());
			qtySold.setText(Dates.formateyyyyMMdd(dto.getFechaHora()));
			losers.setText(dto.getValor());
//			state.setText(dto.getEstado());
			operator.setText(dto.getProduct());
			
			
			if (dto.getEstado().equals("1")) {
				state.setText(context.getString(R.string.sucess));
			}else {
				state.setText(context.getString(R.string.rejected));
			}
		} else if (reportPosition == 15) {
			losers.setVisibility(View.VISIBLE);
			rview.setVisibility(View.VISIBLE);
			state.setVisibility(View.VISIBLE);
			rview2.setVisibility(View.VISIBLE);
			product.setText(dto.getIdTransaccion());
			profitability.setText(Dates.formateyyyyMMdd(dto.getFechaHora()));
			qtySold.setText(dto.getValor());
			losers.setText(dto.getEstado());
			state.setText(dto.getProduct());
			
			if (dto.getEstado().equals("1")) {
				losers.setText("Exitosa");
			}else{
				losers.setText("Rechazado");
			}

		} else if (reportPosition == 16) {
			losers.setVisibility(View.VISIBLE);
			rview.setVisibility(View.VISIBLE);
			state.setVisibility(View.VISIBLE);
			rview2.setVisibility(View.VISIBLE);
			rview3.setVisibility(View.VISIBLE);
			operator.setVisibility(View.VISIBLE);
			product.setText(dto.getIdTransaccion());
			profitability.setText(Dates.formateyyyyMMdd(dto.getFechaHora()));
			qtySold.setText(dto.getValor());
			//losers.setText(dto.getEstado());
			state.setText(dto.getdebt_days());
			operator.setText(dto.getpurchase());
			
			if (dto.getEstado().equals("1")) {
				losers.setText(context.getString(R.string.sucess));
			}else {
				losers.setText(context.getString(R.string.rejected));
			}

		} else if (reportPosition == 17) {
			losers.setVisibility(View.VISIBLE);
			rview.setVisibility(View.VISIBLE);
			state.setVisibility(View.VISIBLE);
			rview2.setVisibility(View.VISIBLE);
			rview3.setVisibility(View.VISIBLE);
			operator.setVisibility(View.VISIBLE);
			rview4.setVisibility(View.VISIBLE);
			facturas.setVisibility(View.VISIBLE);
			
			product.setText(dto.getIdTransaccion());
			profitability.setText(Dates.formateyyyyMMdd(dto.getFechaHora()));
			qtySold.setText(dto.getQuantity());
			losers.setText(dto.getpurchase());
			state.setText((dto.getselling()));
			operator.setText(dto.getValor());
			facturas.setText(dto.getEstado());

		} else if (reportPosition == 18) {
			losers.setVisibility(View.VISIBLE);
			rview.setVisibility(View.VISIBLE);
			state.setVisibility(View.VISIBLE);
			rview2.setVisibility(View.VISIBLE);
			rview3.setVisibility(View.VISIBLE);
			operator.setVisibility(View.VISIBLE);
			rview4.setVisibility(View.VISIBLE);
			facturas.setVisibility(View.VISIBLE);
			product.setText(dto.getIdTransaccion());
			profitability.setText(Dates.formateyyyyMMdd(dto.getFechaHora()));
			qtySold.setText(dto.getInventoryName());
			losers.setText(dto.getTipo());
			state.setText(dto.getSumatoria());
			operator.setText(dto.getValor());
			if (dto.getEstado().equals("1")) {
				facturas.setText("Exitosa");
			}else{
				facturas.setText("Rechazado");
			}
			

		} else if (reportPosition == 19) {
			losers.setVisibility(View.VISIBLE);
			rview.setVisibility(View.VISIBLE);
			state.setVisibility(View.VISIBLE);
			rview2.setVisibility(View.VISIBLE);
			product.setText(dto.getIdTransaccion());
			profitability.setText(Dates.formateyyyyMMdd(dto.getFechaHora()));
			qtySold.setText(dto.getProduct());
			losers.setText(dto.getValor());
			state.setText(dto.getEstado());
		}

		return convertView;
	}

	public String getTypeValue(String transactionType){
		String typeValue = transactionType.equalsIgnoreCase("la caja se abre") ? context.getString(R.string.box_opening) :
			transactionType.equalsIgnoreCase("pago de clientes") ? context.getString(R.string.pay_lend_client) :
			transactionType.equalsIgnoreCase("abono de efectivo") ? context.getString(R.string.deposit_to_box) :
			transactionType.equalsIgnoreCase("venta") ? context.getString(R.string.sale) :
			transactionType.equalsIgnoreCase("inventario") ? context.getString(R.string.provider_payment) :
			transactionType.equalsIgnoreCase("pago a proveedores") ? context.getString(R.string.provider_payment) :
			transactionType.equalsIgnoreCase("retiro de efectivo") ? context.getString(R.string.cash_withdrawal) :
			transactionType.equalsIgnoreCase("tienda cerrada") ? context.getString(R.string.box_closing) : "";
		return typeValue;
	}

	public String getConceptValue(String concept){
		String conceptValue = concept.equalsIgnoreCase("la caja se abre") ? context.getString(R.string.openingbal) :
			concept.equalsIgnoreCase("préstamo de plata a cliente") ? context.getString(R.string.client_lending) :
			concept.equalsIgnoreCase("abono del cliente a la deuda") ? context.getString(R.string.pay_to_debt) :
			concept.equalsIgnoreCase("abono de efectivo") ? context.getString(R.string.deposit_to_box) :
			concept.equalsIgnoreCase("venta") ? context.getString(R.string.sale) :
			concept.equalsIgnoreCase("inventario") ? context.getString(R.string.pay_to_cash) :
			concept.equalsIgnoreCase("pago a proveedores") ? context.getString(R.string.debt_payment) :
			concept.equalsIgnoreCase("retiro de efectivo") ? context.getString(R.string.cash_withdrawal) :
			concept.equalsIgnoreCase("tienda cerrada") ? context.getString(R.string.box_closing) : "";
		return conceptValue;
	}

	// Result of this method,report details data getting  from db
	public class GetReportDetails extends AsyncTask<String, Void, Void> {

		private int selectedReport;
		private String fromDate, toDate;
		private InventoryAmountDTO dto;
		private TextView cell;

		public GetReportDetails(int selectedReport, String fromDate, String toDate, InventoryAmountDTO dto, TextView cell){
			this.selectedReport = selectedReport;
			this.fromDate = fromDate;
			this.toDate = toDate;
			this.dto = dto;
			this.cell = cell;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			//CommonMethods.showProgressDialog(context.getString(R.string.please_wait), context);
			ServiApplication.responds_feed = new ServiceHandler(context).makeServiceCall(
				ServiApplication.URL + "/reports/" + Utils.report_options[selectedReport],
				ServiceHandler.POST, makejsonobj(selectedReport, fromDate, toDate));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (ServiApplication.connectionTimeOutState) {
				List<DTO> inventoryAmount = new ParsingHandler().getInventoryAmount(ServiApplication.responds_feed, selectedReport);
				if (inventoryAmount != null && !inventoryAmount.isEmpty()) { // producto mas vendido = 3
					for(int i = 0; i < inventoryAmount.size(); i++){
						InventoryAmountDTO inventoryValue = (InventoryAmountDTO) inventoryAmount.get(i);
						if(inventoryValue.getProduct().equals(dto.getselling())){ // es el mismo producto
							Log.e("producto -->", inventoryValue.getInventoryName() + " - unidades --> " + inventoryValue.getpurchase());
							cell.setText(inventoryValue.getpurchase()); // Se pone las unidades vendidas de ese producto
							//CommonMethods.progressDialog.dismiss();
						}
					}
				} else {
					Log.e("ERROR -->", context.getString(R.string.nodata));
				}
			}
		}
	}

	public String makejsonobj(int selectedReport, String fromDate, String toDate) {
		String filter_type = "all", filter_id = "0";
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("from_date", fromDate);
			toDate = Dates.addOneDayToTwoDate(toDate);
			if (selectedReport == 1) {
				jsonObject.put("filter_type", filter_type);
				jsonObject.put("filter_id", filter_id);
				jsonObject.put("to_date", toDate);
				jsonObject.put("code", ServiApplication.store_id);
			} else if (selectedReport == 5) {
				jsonObject.put("store_code", ServiApplication.store_id);
			} else {
				jsonObject.put("to_date", toDate);
				jsonObject.put("code", ServiApplication.store_id);
			}
			return jsonObject.toString();
		} catch (JSONException e) {
			return jsonObject.toString();
		}
	}

}
