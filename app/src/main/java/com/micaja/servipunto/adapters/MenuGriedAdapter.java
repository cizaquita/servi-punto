package com.micaja.servipunto.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;

@SuppressLint("ResourceAsColor")
public class MenuGriedAdapter extends BaseAdapter {
	private Context mContext;
	// private List<DTO> inventoryList=new ArrayList<DTO>();
	private LayoutInflater inflater;
	private String[] module_names;
	private Integer[] module_drawable;
	private boolean flage;

	public MenuGriedAdapter(Context mContext, String[] module_names,
			Integer[] module_drawable, boolean b) {
		this.mContext = mContext;
		// this.inventoryList = inventoryList;
		this.module_names = module_names;
		this.module_drawable = module_drawable;
		this.flage = b;
	}

	@Override
	public int getCount() {
		return module_names.length;
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

		convertView = inflater.inflate(R.layout.home_menugriend_row, null);

		final TextView text = (TextView) convertView
				.findViewById(R.id.textView1);

		final ImageView image = (ImageView) convertView
				.findViewById(R.id.imageView1);
		text.setText(module_names[position]);

		image.setImageResource(module_drawable[position]);

		if (flage) {
			if (position == 4) {
				text.setTextColor(mContext.getResources().getColor(
						R.color.customer_tab));
			} else if (position == 5) {
				text.setTextColor(mContext.getResources().getColor(
						R.color.customer_tab));
			} else if (position == 10) {
				text.setTextColor(mContext.getResources().getColor(
						R.color.customer_tab));
			} else if (position == 11) {
				text.setTextColor(mContext.getResources().getColor(
						R.color.customer_tab));
			} else if (position == 17) {
				text.setTextColor(mContext.getResources().getColor(
						R.color.customer_tab));
			}
		}

		return convertView;
	}
}
