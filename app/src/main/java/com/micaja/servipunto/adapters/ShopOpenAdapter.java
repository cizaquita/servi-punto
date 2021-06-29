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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.database.dto.ClientDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.utils.CommonMethods;

public class ShopOpenAdapter extends BaseAdapter {

	private Context context;
	private List<DTO> shopOpenList = new ArrayList<DTO>();
	private LayoutInflater inflater;

	public ShopOpenAdapter(Context context, List<DTO> shopOpenList) {
		this.context = context;
		this.shopOpenList = shopOpenList;
	}

	@Override
	public int getCount() {
		return shopOpenList.size();
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

			convertView = inflater.inflate(R.layout.shopopen_row, null);
		}

		final TextView name = (TextView) convertView
				.findViewById(R.id.txt_shop_description);
		final TextView payment = (TextView) convertView
				.findViewById(R.id.txt_shop_income);
		final TextView expenses = (TextView) convertView
				.findViewById(R.id.txt_shop_expenses);

		ClientDTO dto = (ClientDTO) shopOpenList.get(position);

		name.setText(dto.getName());
		payment.setText("$"
				+ CommonMethods.getNumSeparator(CommonMethods
						.getDoubleFormate(dto.getBalanceAmount())));

		expenses.setText("$"
				+ CommonMethods.getNumSeparator(CommonMethods
						.getDoubleFormate(dto.getBalanceAmount())));

		return convertView;
	}
}
