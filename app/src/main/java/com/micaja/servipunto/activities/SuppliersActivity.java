/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.SupplierAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.JSONStatus;

public class SuppliersActivity extends BaseActivity implements OnClickListener {

	private ListView lvSupplier;
	private List<DTO> supplierList = new ArrayList<DTO>();
	private SupplierAdapter adapter;
	private EditText etxtSearch;
	private ImageView imgSearchicon,imgLogoDeleteSyncRecord;
	private Button addSupplier;
	private ImageView imgCloseicon;

	private ServiApplication appContext;
	private Intent intent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, SuppliersActivity.class);
		inItUI();

	}

	private void inItUI() {

		setContentView(R.layout.suppliers_activity);

		etxtSearch = (EditText) findViewById(R.id.etxt_search);
		lvSupplier = (ListView) findViewById(R.id.lv_Supplier);
		imgSearchicon = (ImageView) findViewById(R.id.img_searchicon);
		imgCloseicon = (ImageView) findViewById(R.id.img_closeicon);

		addSupplier = (Button) findViewById(R.id.btn_Addsupplier);

		imgSearchicon.setOnClickListener(this);
		addSupplier.setOnClickListener(this);
		imgCloseicon.setOnClickListener(this);

		imgSearchicon.setVisibility(View.VISIBLE);

		lvSupplier.setClickable(false);

		//Harold
		imgLogoDeleteSyncRecord = (ImageView) findViewById(R.id.img_logo);
		imgLogoDeleteSyncRecord.setOnClickListener(this);

		etxtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				imgCloseicon.setVisibility(View.INVISIBLE);
				imgSearchicon.setVisibility(View.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		getDataFromDB();

	}

	private void getDataFromDB() {

		supplierList = SupplierDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));

		setDataToViews();
	}

	private void setDataToViews() {

		if (supplierList.size() > 0) {

			supplierList = SupplierDAO.getInstance().getRecords(
					DBHandler.getDBObj(Constants.READABLE));
			adapter = new SupplierAdapter(this, supplierList);
			lvSupplier.setAdapter(adapter);

		} else {

			CommonMethods.showCustomToast(this,
					getResources().getString(R.string.nodata));

		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.img_searchicon:

			refreshList();
			break;

		case R.id.img_closeicon:

			etxtSearch.setText("");
			getDataFromDB();
			break;

		case R.id.btn_Addsupplier:

			appContext.pushActivity(intent);
			Intent intent = new Intent(this, AddSupplierActivity.class);
			intent.putExtra(ConstantsEnum.SUPPLIER_MODE.code(),
					ConstantsEnum.ADD_SUPPLIER.code());
			startActivity(intent);
			break;


			case R.id.img_logo:

				final AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setTitle("Usted Borrará el Registro de Sincronización");
				builder.setMessage("Esta opción le ayudará a recuperar la información del Servidor\n" +
						"Utilicela en caso que haya Formateado o Cambiado la Tablet\n" +
						"Una vez borrado el registro, por favor cierre la tienda en el Modulo Abrir o Cerrar,\n" +
						"Salgase del aplicativo e Ingrese Usuario y Contraseña" +
						"Para que la información se descargue del servidor\n\n" +
						"¿Desea continuar?");
				builder.setCancelable(true);
				builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						new DeleteCatlogsyncRecord().execute();
					}
				});
				AlertDialog alert11 = builder.create();
				alert11.show();

				break;
		default:
			break;
		}

	}

	private void refreshList() {

		if (etxtSearch.length() > 2) {
			List<DTO> updatedList = new ArrayList<DTO>();
			updatedList = SupplierDAO.getInstance().getSearchRecords(
					DBHandler.getDBObj(Constants.READABLE),
					etxtSearch.getText().toString().trim());
			if (updatedList.size() != 0)
				lvSupplier.setAdapter(new SupplierAdapter(this, updatedList));
			else {
				lvSupplier.setAdapter(new SupplierAdapter(this, updatedList));
				CommonMethods.showCustomToast(SuppliersActivity.this,
						getResources().getString(R.string.nodata));
			}

		} else {
			CommonMethods.showCustomToast(this,
					getResources().getString(R.string.search_warning));
		}
		if (etxtSearch.length() > 0) {
			imgCloseicon.setVisibility(View.VISIBLE);
			imgSearchicon.setVisibility(View.INVISIBLE);
		}

	}

	private class DeleteCatlogsyncRecord extends AsyncTask<Void, Void, Void> {
		ServiceHandler servicehandler = new ServiceHandler(SuppliersActivity.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (CommonMethods.getInternetSpeed(SuppliersActivity.this) >= ServiApplication.local_data_speed) {
				SharedPreferences sharedpreferences;
				sharedpreferences = getSharedPreferences(
						ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
				String val_productDetails = servicehandler.makeServiceCall(
						ServiApplication.URL + "/catalog/clear/" + sharedpreferences.getString("store_code", ""),
						ServiceHandler.POST, null);
				if (new JSONStatus().status(val_productDetails) == 0) {
					CommonMethods.showCustomToast(SuppliersActivity.this, getResources().getString(R.string.store_remove));
				}
			} else {
				ServiApplication.connectionTimeOutState = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CommonMethods.progressDialog.dismiss();

		}
	}

}