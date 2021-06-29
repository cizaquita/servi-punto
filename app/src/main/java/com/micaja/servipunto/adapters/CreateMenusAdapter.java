package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.utils.CommonMethods;

public class CreateMenusAdapter extends BaseAdapter {

	private Context mContext;
	private List<DTO> dishesList = new ArrayList<DTO>();
	private LayoutInflater inflater;

	public CreateMenusAdapter(Context mContext, List<DTO> dishesList) {
		this.mContext = mContext;
		this.dishesList = dishesList;
		// this.type = type;
	}

	@Override
	public int getCount() {
		return dishesList.size();
	}

	@Override
	public Object getItem(int position) {
		return dishesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = inflater.inflate(R.layout.create_menus_adapter, null);

		final TextView name = (TextView) convertView
				.findViewById(R.id.txt_MenusNameLeble);
		final TextView price = (TextView) convertView
				.findViewById(R.id.txt_MenusPriceLebel);

		DishesDTO dto = (DishesDTO) dishesList.get(position);
		name.setText(dto.getDishName());
		price.setText(CommonMethods.getNumSeparator(CommonMethods
				.getDoubleFormate(dto.getSellingCostperItem())));

		return convertView;
	}

}
