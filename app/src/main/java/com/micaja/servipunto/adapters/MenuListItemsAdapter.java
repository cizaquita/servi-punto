package com.micaja.servipunto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;

public class MenuListItemsAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	ServiApplication appContext;
	private Integer[] lv_images;
	private String[] lv_names;

	public MenuListItemsAdapter(Context context, String[] lv_names,
			Integer[] lv_images) {
		this.context = context;
		this.lv_images = lv_images;
		this.lv_names = lv_names;
		appContext = (ServiApplication) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		return lv_images.length;
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
			convertView = inflater.inflate(R.layout.menu_list_row_item, null);
		}

		final TextView name = (TextView) convertView
				.findViewById(R.id.item_name);

		final ImageView image = (ImageView) convertView
				.findViewById(R.id.item_image);
		name.setText(lv_names[position]);
		image.setBackgroundResource(lv_images[position]);

		if (position == 0) {
			name.setPadding(10, 0, 5, 0);
		}
		return convertView;
	}

}
