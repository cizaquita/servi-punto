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
import android.widget.Button;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.database.dto.AddMenusInvenDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.dialog.DeleteDailog;
import com.micaja.servipunto.dialog.SalesCalculatorDialog;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.SalesEditTypes;

public class MenuInvenAddAdapter extends BaseAdapter {
	private Context mContext;
	private List<DTO> selectedList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	private String type, screen;
	private Handler uiHandler;

	public MenuInvenAddAdapter(Context mContext, List<DTO> selectedList,
			Handler uiHandler) {
		this.mContext = mContext;
		this.selectedList = selectedList;
		this.uiHandler = uiHandler;
	}

	public void setListData(List<DTO> selectedList) {
		this.selectedList = selectedList;
	}

	@Override
	public int getCount() {
		return selectedList.size();
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

		convertView = inflater.inflate(R.layout.sales_row, null);

		final Button delete = (Button) convertView
				.findViewById(R.id.btn_SalesAdpaDelete);
		final TextView product = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaProduct);
		final TextView price = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaPrice);
		final TextView qty = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaQty);
		final TextView units = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaUnits);

		AddMenusInvenDTO dto = (AddMenusInvenDTO) selectedList.get(position);

		product.setText(dto.getDishName());
		price.setText(CommonMethods.getNumSeparator(CommonMethods
				.getDoubleFormate(dto.getSellingPrice())));
		qty.setText(dto.getQuantity());

		units.setVisibility(View.GONE);

		price.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddMenusInvenDTO dto = (AddMenusInvenDTO) selectedList
						.get(position);

				String value = dto.getSellingPrice();

				type = SalesEditTypes.PRICE.code();
				screen = SalesEditTypes.MENUS_INVENTORY.code();
				SalesCalculatorDialog dialog = new SalesCalculatorDialog(
						mContext, position, type, screen, value, uiHandler);
				dialog.show();
			}
		});

		qty.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddMenusInvenDTO dto = (AddMenusInvenDTO) selectedList
						.get(position);

				String value = dto.getQuantity();

				type = SalesEditTypes.QUANTITY.code();
				screen = SalesEditTypes.MENUS_INVENTORY.code();
				SalesCalculatorDialog dialog = new SalesCalculatorDialog(
						mContext, position, type, screen, value, uiHandler);
				dialog.show();
			}
		});

		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				screen = SalesEditTypes.MENUS_INVENTORY.code();

				DeleteDailog dialog = new DeleteDailog(mContext, position,
						screen, uiHandler);
				dialog.show();
			}
		});

		return convertView;
	}

}
