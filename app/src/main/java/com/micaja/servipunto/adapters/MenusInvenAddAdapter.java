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
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.dialog.DeleteDailog;
import com.micaja.servipunto.dialog.SalesCalculatorDialog;
import com.micaja.servipunto.utils.SalesEditTypes;

public class MenusInvenAddAdapter extends BaseAdapter {
	private Context mContext;
	private List<DTO> selectedList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	private String type;
	private Handler uiHandler;

	public MenusInvenAddAdapter(Context mContext, List<DTO> selectedList,
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
		final TextView dish = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaProduct);
		final TextView price = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaPrice);
		final TextView qty = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaQty);
		final TextView units = (TextView) convertView
				.findViewById(R.id.txt_SaleAdpaUnits);

		units.setVisibility(View.GONE);

		DishesDTO dto = (DishesDTO) selectedList.get(position);

		dish.setText(dto.getDishName());
		price.setText(dto.getSellingCostperItem());
		// qty.setText(dto.getQuantity());

		price.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectedProddutsDTO dto = (SelectedProddutsDTO) selectedList
						.get(position);

				String value = dto.getPrice();

				type = SalesEditTypes.PRICE.code();
				SalesCalculatorDialog dialog = new SalesCalculatorDialog(
						mContext, position, type, "", value, uiHandler);
				dialog.show();
			}
		});

		qty.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectedProddutsDTO dto = (SelectedProddutsDTO) selectedList
						.get(position);

				String value = dto.getQuantity();

				type = SalesEditTypes.QUANTITY.code();
				SalesCalculatorDialog dialog = new SalesCalculatorDialog(
						mContext, position, type, "", value, uiHandler);
				dialog.show();
			}
		});

		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteDailog dialog = new DeleteDailog(mContext, position, "",
						uiHandler);
				dialog.show();
			}
		});

		return convertView;
	}

}
