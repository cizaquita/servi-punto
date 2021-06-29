package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SupplierDTO;

public class OrdersSuppAdapter extends BaseAdapter {
	private Context mContext;
	private List<DTO> suppliersList = new ArrayList<DTO>();
	private LayoutInflater inflater;

	public OrdersSuppAdapter(Context mContext, List<DTO> suppliersList) {
		this.mContext = mContext;
		this.suppliersList = suppliersList;

	}

	@Override
	public int getCount() {
		return suppliersList.size();
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
		if (convertView == null)
			inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = inflater.inflate(R.layout.orders_supplier_row, null);

		final TextView supplierName = (TextView) convertView
				.findViewById(R.id.txt_SuppliersName);
		final TextView payment = (TextView) convertView
				.findViewById(R.id.txt_SuppliersPayment);
		final TextView date = (TextView) convertView
				.findViewById(R.id.txt_SuppliersDate);
		final ImageView pay = (ImageView) convertView
				.findViewById(R.id.img_SuppliersPay);
		final ImageView edit = (ImageView) convertView
				.findViewById(R.id.img_SuppliersEdit);

		payment.setVisibility(View.GONE);
		date.setVisibility(View.GONE);
		pay.setVisibility(View.GONE);
		edit.setVisibility(View.GONE);

		SupplierDTO dto = (SupplierDTO) suppliersList.get(position);

		supplierName.setText(dto.getName());

		return convertView;
	}

}
