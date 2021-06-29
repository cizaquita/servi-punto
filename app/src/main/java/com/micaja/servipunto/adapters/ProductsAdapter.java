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
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.AddProductActivity;
import com.micaja.servipunto.activities.InventoryActivity;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.dialog.PayDialog.onSaveListener;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;

public class ProductsAdapter extends BaseAdapter implements onSaveListener {

	private Context context;
	private List<DTO> productResultsList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	ServiApplication appContext;
	onSaveListener listener;

	public ProductsAdapter(Context context, List<DTO> productResultsList) {
		this.context = context;
		this.productResultsList = productResultsList;
		appContext = (ServiApplication) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		return productResultsList.size();
	}

	public void setData(List<DTO> productResultsList) {
		this.productResultsList = productResultsList;
		notifyDataSetChanged();

	}

	@Override
	public Object getItem(int position) {
		return productResultsList.get(position);
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

			convertView = inflater.inflate(R.layout.products_row, null);
		}
		final TextView prodname = (TextView) convertView
				.findViewById(R.id.txt_productName);
		prodname.setOnClickListener(mItemClickListener);
		prodname.setTag(position);

		final TextView supplierName = (TextView) convertView
				.findViewById(R.id.txt_prod_supplier);

		final TextView qty = (TextView) convertView
				.findViewById(R.id.txt_prod_quantity);
		ImageView ivEdit = (ImageView) convertView
				.findViewById(R.id.img_prod_edit);

		final ProductDTO dto = (ProductDTO) productResultsList.get(position);

		prodname.setText(dto.getBarcode() + "-" + dto.getName());
		supplierName.setText(SupplierDAO.getInstance().getRecordBySupplierID(
				DBHandler.getDBObj(Constants.READABLE), dto.getSupplierId()));

		if (dto.getQuantity() != null)
			qty.setText(dto.getQuantity());
		else
			qty.setText("0");

		if (!"0".equals(dto.getProductFlag()))
			ivEdit.setVisibility(View.VISIBLE);

		ivEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				appContext.pushActivity(new Intent(context,
						InventoryActivity.class));
				Intent intent = new Intent(context, AddProductActivity.class);
				intent.putExtra(ConstantsEnum.PRODUCT_MODE.code(),
						ConstantsEnum.EDIT_PRODUCT.code());
				intent.putExtra(ConstantsEnum.PRODUCT_ID.code(),
						dto.getBarcode());
				context.startActivity(intent);

			}
		});
		return convertView;
	}

	private OnClickListener mItemClickListener;

	public void setItemListener(OnClickListener mListItemClickListener) {
		this.mItemClickListener = mListItemClickListener;
	}

	@Override
	public void onSave() {

		ProductsAdapter.this.notifyDataSetChanged();

	}

}
