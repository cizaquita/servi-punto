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
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SuggestedOrderDTO;

public class OrdersSuggestedAdapter extends BaseAdapter {

	private Context mContext;
	private List<DTO> suggestedList = new ArrayList<DTO>();
	private LayoutInflater inflater;

	public OrdersSuggestedAdapter(Context mContext, List<DTO> productsList) {
		this.mContext = mContext;
		this.suggestedList = productsList;
	}

	@Override
	public int getCount() {

		return suggestedList.size();
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
		if (convertView == null)
			inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = inflater.inflate(R.layout.orders_suggest_row, null);

		final TextView name = (TextView) convertView
				.findViewById(R.id.txt_OrderSuggName);
		final TextView price = (TextView) convertView
				.findViewById(R.id.txt_OrderSuggPrice);
		final TextView qty = (TextView) convertView
				.findViewById(R.id.txt_OrderSuggQty);
		final TextView units = (TextView) convertView
				.findViewById(R.id.txt_OrderSuggUnits);

		SuggestedOrderDTO dto = (SuggestedOrderDTO) suggestedList.get(position);

		name.setText(dto.getName());
		price.setText(dto.getPurchasePrice());
		units.setText(dto.getUom());
		try {
			qty.setText(""
					+ (Double.parseDouble(dto.getMin_count_inventory()) - Double
							.parseDouble(dto.getQuantity())));
		} catch (Exception e) {
			qty.setText("");
		}

		return convertView;
	}

}
