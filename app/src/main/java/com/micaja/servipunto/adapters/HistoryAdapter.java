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
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.HistoryDetailsDTO;

public class HistoryAdapter extends BaseAdapter {

	private Context context;
	private List<DTO> clientHistoryList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	ServiApplication appContext;

	public HistoryAdapter(Context context, List<DTO> clientHistoryList) {
		this.context = context;
		this.clientHistoryList = clientHistoryList;
		appContext = (ServiApplication) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		return clientHistoryList.size();
	}

	public void setData(List<DTO> clientResultsList) {
		this.clientHistoryList = clientResultsList;
		notifyDataSetChanged();

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
			convertView = inflater.inflate(R.layout.history_row, null);
		}

		final TextView date = (TextView) convertView
				.findViewById(R.id.txt_datetime);

		final TextView debut = (TextView) convertView
				.findViewById(R.id.txt_debut);
		final TextView pay = (TextView) convertView.findViewById(R.id.txt_pay);
		final TextView balance = (TextView) convertView
				.findViewById(R.id.txt_bal);

		final HistoryDetailsDTO dto = (HistoryDetailsDTO) clientHistoryList
				.get(position);

		date.setText(dto.getDate());
		debut.setText(dto.getInitialDebt());

		balance.setText(""
				+ (Integer.parseInt(dto.getBalance()) - Integer.parseInt(dto
						.getPay())));
		pay.setText(dto.getPay());
		return convertView;
	}

}
