package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.MenusInveDishesDTO;
import com.micaja.servipunto.dialog.MenusInvenAdjDialog;

public class MenusInventoryAdapter extends BaseAdapter {

	private Context mContext;
	private List<DTO> inventoryList = new ArrayList<DTO>();
	private LayoutInflater inflater;

	private Handler uiHandler;

	public MenusInventoryAdapter(Context mContext, List<DTO> inventoryList,
			Handler uiHandler) {
		this.mContext = mContext;
		this.uiHandler = uiHandler;
		this.inventoryList = inventoryList;
	}

	public void setListData(List<DTO> selectedList) {
		this.inventoryList = selectedList;
	}

	@Override
	public int getCount() {
		return inventoryList.size();
	}

	@Override
	public Object getItem(int position) {
		return inventoryList.get(position);
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

		convertView = inflater.inflate(R.layout.menus_invantory_adap, null);

		final TextView name = (TextView) convertView
				.findViewById(R.id.txt_MenusNameLeble);
		final TextView price = (TextView) convertView
				.findViewById(R.id.txt_MenusPriceLebel);
		final ImageView edit = (ImageView) convertView
				.findViewById(R.id.img_DishEdit);

		MenusInveDishesDTO dto = (MenusInveDishesDTO) inventoryList
				.get(position);
		name.setText(dto.getDishName());
		price.setText(dto.getCount());

		edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MenusInveDishesDTO dto = (MenusInveDishesDTO) inventoryList
						.get(position);
				MenusInvenAdjDialog adjustDialog = new MenusInvenAdjDialog(
						mContext, dto, uiHandler);
				adjustDialog.show();
			}
		});

		return convertView;
	}

}
