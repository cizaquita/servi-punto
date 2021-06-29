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
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;

public class SalesInventoryAdapter extends BaseAdapter {

	private Context context;
	private List<DTO> inventoryResultsList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	private ServiApplication appContext;

	public SalesInventoryAdapter(Context context, List<DTO> inventoryResultsList) {
		this.context = context;
		this.inventoryResultsList = inventoryResultsList;
		appContext = (ServiApplication) context.getApplicationContext();

	}

	@Override
	public int getCount() {
		return inventoryResultsList.size();
	}

	public void setData(List<DTO> inventoryResultsList) {
		this.inventoryResultsList = inventoryResultsList;
		this.notifyDataSetChanged();
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

			convertView = inflater.inflate(R.layout.inventory_row, null);
		}

		final TextView productCode = (TextView) convertView
				.findViewById(R.id.txt_inv_prodcode);
		final TextView productName = (TextView) convertView
				.findViewById(R.id.txt_inv_prodname);

		final TextView qty = (TextView) convertView
				.findViewById(R.id.txt_inv_quantity);
		final TextView sellingprice = (TextView) convertView
				.findViewById(R.id.txt_inv_sellingprice);

		final TextView utilityValue = (TextView) convertView
				.findViewById(R.id.txt_inv_utility);
		final ImageView adjust = (ImageView) convertView
				.findViewById(R.id.img_inv_adjustment);

		adjust.setVisibility(View.GONE);
		final SelectedProddutsDTO inventory = (SelectedProddutsDTO) inventoryResultsList
				.get(position);

		productCode.setText(inventory.getIdProduct());
		productName.setText(inventory.getName());

		qty.setText(inventory.getQuantity());
		sellingprice.setText(inventory.getSellPrice());
		utilityValue.setText(inventory.getUtilityValue());

		return convertView;
	}

}
