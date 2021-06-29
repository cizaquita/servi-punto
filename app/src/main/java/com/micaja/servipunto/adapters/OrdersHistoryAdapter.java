package com.micaja.servipunto.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;

public class OrdersHistoryAdapter extends BaseAdapter {

	private Context mContext;
	private List<DTO> productsList = new ArrayList<DTO>();
	private LayoutInflater inflater;

	public OrdersHistoryAdapter(Context mContext, List<DTO> productsList) {
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

		final TextView invoice = (TextView) convertView
				.findViewById(R.id.txt_invoicenumber);
		final TextView name = (TextView) convertView
				.findViewById(R.id.txt_ProductName);
		final TextView price = (TextView) convertView
				.findViewById(R.id.txt_ProductPrice);
		final TextView suggested = (TextView) convertView
				.findViewById(R.id.txt_ProductSuggest);
		final ImageView addToCart = (ImageView) convertView
				.findViewById(R.id.img_ProductCart);
		final TextView product = (TextView) convertView
				.findViewById(R.id.txt_Product);
		final View view_id = (View) convertView.findViewById(R.id.view_id);
		product.setVisibility(View.VISIBLE);
		invoice.setVisibility(View.VISIBLE);
		view_id.setVisibility(View.VISIBLE);

		addToCart.setVisibility(View.GONE);

		final ProductDTO dto = (ProductDTO) productsList.get(position);
		ProductDTO pdto = ProductDAO.getInstance().getRecordsByProductID(
				DBHandler.getDBObj(Constants.READABLE), dto.getName());
		if (pdto.getBarcode() != null) {
			name.setText(dto.getCreateDate());
			price.setText(pdto.getName());
			suggested.setText(dto.getPurchasePrice());
			invoice.setText(dto.getQuantity());
		} else {
			name.setText(dto.getCreateDate());
			price.setText(dto.getName());
			suggested.setText(dto.getPurchasePrice());
			invoice.setText(dto.getQuantity());
		}

		if (dto.getSellingPrice().contains("Yes")) {
			product.setText(mContext.getResources().getString(
					R.string.order_received));
		} else {
			product.setText(mContext.getResources().getString(
					R.string.in_progress));
		}
		
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("simple text", dto.getQuantity());
				clipboard.setPrimaryClip(clip);
				CommonMethods.showToast(mContext, mContext.getString(R.string.invoice_copyed));
				return false;
			}
		});
		return convertView;
	}

}
