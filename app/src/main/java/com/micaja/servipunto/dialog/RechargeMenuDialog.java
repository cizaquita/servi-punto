package com.micaja.servipunto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.activities.CollectionActivity;
import com.micaja.servipunto.activities.ConventionInformationActivity;
import com.micaja.servipunto.activities.PaquetigosActivity;
import com.micaja.servipunto.activities.RechargeActivity;
import com.micaja.servipunto.adapters.MenuGriedAdapter;

public class RechargeMenuDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Context context;
	private Button btnRecharge, btnPaquetig;
	private Intent intent;
	private ImageView imgClose;

	private GridView gridView_points;
	private String[] module_names;
	public Integer[] module_drawable;
	private boolean flage;
	private ServiApplication appContext;
	private TextView txt_ProductName;

	public RechargeMenuDialog(Context context, boolean b) {
		super(context);
		this.context = context;
		this.flage = b;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		appContext = (ServiApplication) context.getApplicationContext();
		initUI();

	}

	private void initUI() {
		setContentView(R.layout.rechargemenu_dialog);
		imgClose = (ImageView) findViewById(R.id.img_close);
		txt_ProductName = (TextView) findViewById(R.id.txt_ProductName);
		imgClose.setOnClickListener(this);

		if (flage) {
			module_names = new String[] {
					context.getResources().getString(R.string.recharge),
					context.getResources().getString(R.string.paquetigo) };
			module_drawable = new Integer[] { R.drawable.new_recharge_bg,
					R.drawable.new_recharge_bg };

			txt_ProductName.setText(context.getResources().getString(
					R.string.recharge));

		} else {
			module_names = new String[] {
					context.getResources().getString(R.string.recaudos),
					context.getResources().getString(R.string.convention_title) };

			module_drawable = new Integer[] { R.drawable.new_collection_bg,
					R.drawable.new_collection_bg };

			txt_ProductName.setText(context.getResources().getString(
					R.string.recaudos));
		}
		gridView_points = (GridView) findViewById(R.id.gridView_points);
		gridView_points.setAdapter(new MenuGriedAdapter(context, module_names,
				module_drawable, false));
		gridView_points.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (flage) {
					switch (position) {
					case 0:
						appContext.pushActivity(intent);
						intent = new Intent(context, RechargeActivity.class);
						context.startActivity(intent);
						break;
					case 1:
						appContext.pushActivity(intent);
						intent = new Intent(context, PaquetigosActivity.class);
						context.startActivity(intent);
						break;

					default:
						break;
					}
				} else {
					switch (position) {
					case 0:
						appContext.pushActivity(intent);
						intent = new Intent(context, CollectionActivity.class);
						context.startActivity(intent);
						break;
					case 1:
						appContext.pushActivity(intent);
						intent = new Intent(context,
								ConventionInformationActivity.class);
						context.startActivity(intent);
						break;

					default:
						break;
					}
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// case R.id.btn_recharge:
		// intent = new Intent(context, RechargeActivity.class);
		// context.startActivity(intent);
		//
		// break;
		//
		// case R.id.btn_paquetigos:
		// intent = new Intent(context, PaquetigosActivity.class);
		// context.startActivity(intent);

		case R.id.img_close:
			this.dismiss();

			break;

		default:
			break;
		}

	}
}
