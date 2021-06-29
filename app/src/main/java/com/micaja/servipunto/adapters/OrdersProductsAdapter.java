package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.dialog.OrdersQtyDialog;

public class OrdersProductsAdapter extends BaseAdapter {

	private Context mContext;
	private List<DTO> productsList = new ArrayList<DTO>();
	private LayoutInflater inflater;

	public OrdersProductsAdapter(Context mContext, List<DTO> productsList) {
		this.mContext = mContext;
		this.productsList = productsList;
	}

	@Override
	public int getCount() {

		return productsList.size();
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

		convertView = inflater.inflate(R.layout.orders_products_row, null);

		final TextView name = (TextView) convertView
				.findViewById(R.id.txt_ProductName);
		final TextView price = (TextView) convertView
				.findViewById(R.id.txt_ProductPrice);
		final TextView suggested = (TextView) convertView
				.findViewById(R.id.txt_ProductSuggest);
		final ImageView addToCart = (ImageView) convertView
				.findViewById(R.id.img_ProductCart);

		ProductDTO dto = (ProductDTO) productsList.get(position);

		name.setText(dto.getName());
		price.setText(dto.getPurchasePrice());
		suggested.setText(dto.getSellingPrice());

		addToCart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OrdersQtyDialog dialog = new OrdersQtyDialog(mContext,
						position, productsList);
				dialog.show();
			}
		});

		return convertView;
	}

}
