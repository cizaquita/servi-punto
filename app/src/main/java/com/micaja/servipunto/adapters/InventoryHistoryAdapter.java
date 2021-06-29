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
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.InventoryInvoiceDTO;
import com.micaja.servipunto.utils.CommonMethods;

public class InventoryHistoryAdapter extends BaseAdapter {
	private Context mContext;
	private List<DTO> inventoryList=new ArrayList<DTO>();
	private LayoutInflater inflater;

	public InventoryHistoryAdapter(Context mContext, List<DTO> inventoryList) {
		this.mContext = mContext;
		this.inventoryList = inventoryList;
	}

	@Override
	public int getCount() {
		return inventoryList.size();
	}

	@Override
	public Object getItem(int position) {
		return inventoryList.size();
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

		convertView = inflater.inflate(R.layout.inventory_history_row, null);

		final TextView invoiceNum = (TextView) convertView
				.findViewById(R.id.txt_HistoryInvoice);
		final TextView SupplierName = (TextView) convertView
				.findViewById(R.id.txt_HistorySupplier);
		final TextView amount = (TextView) convertView
				.findViewById(R.id.txt_HistoryAmount);

		InventoryInvoiceDTO dto = (InventoryInvoiceDTO) inventoryList
				.get(position);

		invoiceNum.setText(dto.getInvoiceNum());
		SupplierName.setText(dto.getSupplierName());
		// Se agregan las siguientes líneas a petición del requerimiento 4.3.4.5 D
		//amount.setText(dto.getTotalAmount());
		String totalAmount = CommonMethods.getNumSeparator(CommonMethods.getDoubleFormate(dto.getTotalAmount()));
		amount.setText(totalAmount);

		return convertView;
	}

}
