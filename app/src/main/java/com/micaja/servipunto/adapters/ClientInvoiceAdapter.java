package com.micaja.servipunto.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SalesDetailDTO;
import com.micaja.servipunto.utils.Constants;

public class ClientInvoiceAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private ServiApplication appContext;
	private List<DTO> invoiceList;

	public ClientInvoiceAdapter(Context context, List<DTO> invoiceList) {
		this.mContext = context;
		this.invoiceList = invoiceList;

	}

	@Override
	public int getCount() {
		return invoiceList.size();
	}

	public void setData(List<DTO> invoiceList) {
		this.invoiceList = invoiceList;
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
		if (convertView == null)
			inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = inflater.inflate(R.layout.invoice_details_row, null);

		final TextView txtbarcode = (TextView) convertView
				.findViewById(R.id.txt_barcode);
		final TextView txtcount = (TextView) convertView
				.findViewById(R.id.txt_count);
		final TextView txtuom = (TextView) convertView
				.findViewById(R.id.txt_uom);

		final SalesDetailDTO dto = (SalesDetailDTO) invoiceList.get(position);
		
		ProductDTO productDto = ProductDAO.getInstance().getRecordsByProductID(
				DBHandler.getDBObj(Constants.READABLE), dto.getProductId());
		if (productDto.getName().length()>2) {
			txtbarcode.setText(productDto.getName());
		}else {
			txtbarcode.setText(dto.getProductId());
		}

		txtcount.setText(dto.getCount());
		txtuom.setText(dto.getUom());

		return convertView;
	}

}
