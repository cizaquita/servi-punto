package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.database.dto.ControlDispersionDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.dialog.ControlDispersionDialog1;
import com.micaja.servipunto.utils.CommonMethods;

public class ControlDispersionAdapter extends BaseAdapter {

	private Context context;
	private List<DTO> reportDetails = new ArrayList<DTO>();
	private LayoutInflater inflater;
	private int reportPosition;
	TextView txt_quantitysold;

	public ControlDispersionAdapter(Context context, List<DTO> reportDetailsList) {
		this.context = context;
		this.reportDetails = reportDetailsList;
	}

	public void setData(List<DTO> reportDetailsList) {
		this.reportDetails = reportDetailsList;
		notifyDataSetChanged();
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

		final TextView product = (TextView) convertView
				.findViewById(R.id.txt_product);
		final TextView profitability = (TextView) convertView
				.findViewById(R.id.txt_profitability);
		final TextView qtySold = (TextView) convertView
				.findViewById(R.id.txt_quantitysold);
		final TextView losers = (TextView) convertView
				.findViewById(R.id.txt_total_losers);
		final TextView facturas = (TextView) convertView
				.findViewById(R.id.txt_facturas);
		final TextView state = (TextView) convertView
				.findViewById(R.id.txt_state);
		final TextView operator = (TextView) convertView
				.findViewById(R.id.txt_operator);
		final TextView accept = (TextView) convertView
				.findViewById(R.id.txt_accept);
		final TextView reject = (TextView) convertView
				.findViewById(R.id.txt_reject);

		final View rview3 = (View) convertView.findViewById(R.id.report_view3);
		final View rview4 = (View) convertView.findViewById(R.id.report_view4);
		final View rview = (View) convertView.findViewById(R.id.report_view);
		final View rview2 = (View) convertView.findViewById(R.id.report_view2);
		final View report_view = (View) convertView
				.findViewById(R.id.report_view);
		final View rview5 = (View) convertView.findViewById(R.id.report_view5);
		final View rview6 = (View) convertView.findViewById(R.id.report_view6);

		final ControlDispersionDTO dto = (ControlDispersionDTO) reportDetails
				.get(position);

		product.setText(dto.getTipo());
		profitability.setText(CommonMethods.getRoundedVal(Double
				.parseDouble(dto.getValor())));
		qtySold.setText(dto.getDate());
		losers.setText(dto.getCentroacopioId());

		report_view.setVisibility(View.VISIBLE);
		rview2.setVisibility(View.VISIBLE);
		rview3.setVisibility(View.VISIBLE);
		rview4.setVisibility(View.VISIBLE);
		losers.setVisibility(View.VISIBLE);
		rview5.setVisibility(View.VISIBLE);
		rview6.setVisibility(View.VISIBLE);

		accept.setVisibility(View.VISIBLE);
		reject.setVisibility(View.VISIBLE);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// if (dto.getTipo().equalsIgnoreCase("ACEPTACION")) {
				// ControlDispersionDialog1 dialog = new
				// ControlDispersionDialog1(
				// context, dto);
				// dialog.show();
				// dialog.setCancelable(false);
				// } else {
				// ControlDispersionDialog3 dialog3 = new
				// ControlDispersionDialog3(
				// context, dto);
				// dialog3.show();
				// dialog3.setCancelable(false);
				// }
			}
		});
		accept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ControlDispersionDialog1 dialog = new ControlDispersionDialog1(
						context, dto, true);
				dialog.show();
				dialog.setCancelable(false);
			}
		});
		reject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ControlDispersionDialog3 dialog3 = new
				// ControlDispersionDialog3(
				// context, dto);
				// dialog3.show();
				// dialog3.setCancelable(false);
				ControlDispersionDialog1 dialog = new ControlDispersionDialog1(
						context, dto, false);
				dialog.show();
				dialog.setCancelable(false);
			}
		});
		return convertView;
	}

}
