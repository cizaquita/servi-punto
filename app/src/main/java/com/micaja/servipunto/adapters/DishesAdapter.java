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
import com.micaja.servipunto.activities.CreateDishActivity;
import com.micaja.servipunto.activities.CreateMenusActivity;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.database.dto.MenuDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.ConstantsEnum;

public class DishesAdapter extends BaseAdapter {

	private Context mContext;
	private List<DTO> dishesList = new ArrayList<DTO>();
	private LayoutInflater inflater;
	private String type;
	private DishesDTO dishDTO;
	private MenuDTO menuDTO;
	private ServiApplication appContext;
	private Intent mIntent;

	public DishesAdapter(Context mContext, List<DTO> dishesList, String type,
			ServiApplication appContext, Intent intent) {
		this.mContext = mContext;
		this.dishesList = dishesList;
		this.type = type;
		this.appContext = appContext;
		this.mIntent = intent;
	}

	public void setListData(List<DTO> selectedList) {
		this.dishesList = selectedList;
	}

	@Override
	public int getCount() {

		return dishesList.size();
	}

	@Override
	public Object getItem(int position) {
		return dishesList.get(position);
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
		convertView = inflater.inflate(R.layout.dishes_row, null);
		final TextView name = (TextView) convertView
				.findViewById(R.id.txt_DishName);
		final TextView price = (TextView) convertView
				.findViewById(R.id.txt_DishPrice);
		final ImageView edit = (ImageView) convertView
				.findViewById(R.id.img_DishEdit);

		if (type.equals(ConstantsEnum.VIEW_MENUS.code())) {
			menuDTO = (MenuDTO) dishesList.get(position);

			name.setText(menuDTO.getName());
			switch (Integer.parseInt(menuDTO.getMenuTypeId())) {
			case 0:
				price.setText(mContext.getResources().getString(
						R.string.menus_types_daily));
				break;

			case 1:
				price.setText(mContext.getResources().getString(
						R.string.menus_types_weekly));
				break;

			case 2:
				String dates = mContext.getResources().getString(
						R.string.menus_types_Occasionally)
						+ "("
						+ menuDTO.getStartDate()
						+ " - "
						+ menuDTO.getEndDate() + ")";
				price.setText(dates);
				break;

			default:
				break;
			}
		} else {
			dishDTO = (DishesDTO) dishesList.get(position);
			name.setText(dishDTO.getDishName());
			price.setText(CommonMethods.getNumSeparator(CommonMethods
					.getDoubleFormate(dishDTO.getSellingCostperItem())));
		}

		edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				if (type.equals(ConstantsEnum.VIEW_DISH.code())) {
					String selectedDishId = ((DishesDTO) dishesList
							.get(position)).getDishId();
					intent = new Intent(mContext, CreateDishActivity.class);
					intent.putExtra(ConstantsEnum.EDIT_DISH.code(),
							selectedDishId);
				} else {
					String selectedMenuId = ((MenuDTO) dishesList.get(position))
							.getMenuId();

					intent = new Intent(mContext, CreateMenusActivity.class);
					intent.putExtra(ConstantsEnum.EDIT_DISH.code(),
							selectedMenuId);
				}

				appContext.pushActivity(mIntent);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

}
