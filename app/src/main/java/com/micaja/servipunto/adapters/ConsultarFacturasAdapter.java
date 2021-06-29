package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.ClientHistoryDTO;
import com.micaja.servipunto.database.dto.ConsultarFacturasDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;

public class ConsultarFacturasAdapter extends BaseAdapter {

	private Context context;
	private List<ClientHistoryDTO> clientHistoryList = new ArrayList<ClientHistoryDTO>();
	private LayoutInflater inflater;
	ServiApplication appContext;
	ConsultarFacturasDTO consultarFacturasDTO;
	UserDetailsDTO udto;

	public ConsultarFacturasAdapter(Context context,
			ConsultarFacturasDTO consultarFacturasDTO, UserDetailsDTO udto) {
		this.context = context;
		this.consultarFacturasDTO = consultarFacturasDTO;
		this.udto = udto;
		appContext = (ServiApplication) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		return 15;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.consultarfacturas_list_row_items, null);
		}

		final TextView textView1 = (TextView) convertView
				.findViewById(R.id.textView1);

		final TextView textView2 = (TextView) convertView
				.findViewById(R.id.textView2);

		if (position == 0) {
			textView1.setText("ComercioId");
			textView2.setText(udto.getComercioId());
		} else if (position == 1) {
			textView1.setText("IdFactura");
			textView2.setText(consultarFacturasDTO.getIdFactura());
		} else if (position == 2) {
			textView1.setText("Saldo Inicial");
			textView2.setText("" + consultarFacturasDTO.getSaldoInicial());
		} else if (position == 3) {
			textView1.setText("Valor Causado");
			textView2.setText("" + consultarFacturasDTO.getValorCausado());
		} else if (position == 4) {
			textView1.setText("Aceptaciones");
			textView2.setText("" + consultarFacturasDTO.getAceptaciones());
		} else if (position == 5) {
			textView1.setText("Devoluciones");
			textView2.setText("" + consultarFacturasDTO.getDevoluciones());
		} else if (position == 6) {
			textView1.setText("Pagos");
			textView2.setText("" + consultarFacturasDTO.getPagos());
		} else if (position == 7) {
			textView1.setText("Comisiones");
			textView2.setText("" + consultarFacturasDTO.getComisiones());
		} else if (position == 8) {
			textView1.setText("Total Corte");
			textView2.setText("" + consultarFacturasDTO.getTotalCorte());
		} else if (position == 9) {
			textView1.setText("Abonos");
			textView2.setText("" + consultarFacturasDTO.getAbonos());
		} else if (position == 10) {
			textView1.setText("Creditos");
			textView2.setText("" + consultarFacturasDTO.getCreditos());
		} else if (position == 11) {
			textView1.setText("Debitos");
			textView2.setText("" + consultarFacturasDTO.getDebitos());
		} else if (position == 12) {
			textView1.setText("Otros Cargos");
			textView2.setText("" + consultarFacturasDTO.getOtrosCargos());
		} else if (position == 13) {
			textView1.setText("Saldo Total");
			textView2.setText("" + consultarFacturasDTO.getSaldoTotal());
		} else if (position == 14) {
			textView1.setText("Ventas");
			textView2.setText("" + consultarFacturasDTO.getVentas());
		}
		return convertView;
	}
}
