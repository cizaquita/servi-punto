package com.micaja.servipunto.activities;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.ProductsAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.InventoryDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dao.SupplierDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;

public class ProductsActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private Button btnAddproduct, btnFilter;
	private ListView lvProducts;
	private List<DTO> productList = new ArrayList<DTO>();
	private ProductsAdapter productAdapter;
	private EditText etxtSearch;
	private ImageView imgSearchicon, imgCloseicon,imgLogoLoadProduct;
	private TextView txtProductlistings;
	private ServiApplication appContext;
	private Intent intent;
	private ImageView img_filtericon;
	//Harold


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		appContext = (ServiApplication) getApplicationContext();
		intent = new Intent(this, ProductsActivity.class);
		inItUI();

	}

	// Result of this method,registration for all UI views.
	private void inItUI() {

		setContentView(R.layout.products_activity);

		etxtSearch = (EditText) findViewById(R.id.etxt_search);
		imgSearchicon = (ImageView) findViewById(R.id.img_searchicon);
		imgCloseicon = (ImageView) findViewById(R.id.img_closeicon);
		txtProductlistings = (TextView) findViewById(R.id.txt_ProductCount);

		btnAddproduct = (Button) findViewById(R.id.btn_createproduct);
		btnAddproduct.setOnClickListener(this);

		lvProducts = (ListView) findViewById(R.id.lv_Products);

		btnFilter = (Button) findViewById(R.id.btn_filter);
		btnFilter.setText(getResources().getString(R.string.spn_all));
		btnFilter.setOnClickListener(this);
		img_filtericon = (ImageView) findViewById(R.id.img_filtericon);

		imgSearchicon.setOnClickListener(this);
		imgCloseicon.setOnClickListener(this);

		//add HAROLD
		imgLogoLoadProduct = (ImageView) findViewById(R.id.img_logo);
		imgLogoLoadProduct.setOnClickListener(this);

		if (appContext.getScreen() != null) {
			lvProducts.setOnItemClickListener(this);
			getSupplierProducts();
			btnAddproduct.setVisibility(View.GONE);
//			imgSearchicon.setVisibility(View.GONE);
//			imgCloseicon.setVisibility(View.GONE);
//			etxtSearch.setVisibility(View.GONE);
			System.out.println("Babu===========Babu"+appContext.getSeletedSupplier());

		} else {
			// lvProducts.setEnabled(false);
			// lvProducts.setBackgroundColor(android.R.color.transparent);
			lvProducts.setOnItemClickListener(null);
			getDataFromDB();
		}
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

	}

	// Result of this method,getting data from db
	private void getDataFromDB() {

		productList = ProductDAO.getInstance().getRecordsWithInvenQty(
				DBHandler.getDBObj(Constants.READABLE));
		setDataToViews();
	}

	private void getSupplierProducts() {
		productList = ProductDAO.getInstance().getRecordsBySupplierID(
				DBHandler.getDBObj(Constants.READABLE),
				appContext.getSeletedSupplier());
		setDataToViews();
	}

	private void setDataToViews() {
		
		txtProductlistings.setText(getResources().getString(
				R.string.inv_productslist)
				+ " " + productList.size());

		productAdapter = new ProductsAdapter(this, productList);
		lvProducts.setAdapter(productAdapter);
		
//		productList = ProductDAO.getInstance().getRecordsBySupplierID(
//				DBHandler.getDBObj(Constants.READABLE),
//				appContext.getSeletedSupplier());
//		setDataToViews();
//
//		txtProductlistings.setText(getResources().getString(
//				R.string.inv_productslist)
//				+ " " + productList.size());
//
//		productAdapter = new ProductsAdapter(this, productList);
//		lvProducts.setAdapter(productAdapter);
//
//		productList = ProductDAO.getInstance().getRecordsWithInvenQty(
//				DBHandler.getDBObj(Constants.READABLE));
//
//		if (productList.size() > 0) {
//			productAdapter = new ProductsAdapter(this, productList);
//			productAdapter.setItemListener(mListItemClickListener);
//			lvProducts.setAdapter(productAdapter);
//			lvProducts.invalidateViews();
//		} else {
//			CommonMethods.showCustomToast(this,
//					getResources().getString(R.string.nodata));
//		}

	}


	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_createproduct:
			appContext.pushActivity(intent);
			Intent intent = new Intent(this, AddProductActivity.class);
			intent.putExtra(ConstantsEnum.PRODUCT_MODE.code(),
					ConstantsEnum.ADD_PRODUCT.code());
			startActivity(intent);
			finish();
			break;
		case R.id.img_searchicon:
			refreshList();
			break;

		case R.id.img_closeicon:
			etxtSearch.setText("");
			refreshList();
			break;
		case R.id.btn_filter:
			// showFilters();
			break;
			//Harold. Case que da la opción de Precargue
			case R.id.img_logo:
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Realizar un precargue de información");
				builder.setMessage("1. Asegurese de que tenga toda la información del Servidor\n" +
						"2. Si no está seguro Sincronice la información, borre el registro de sincronización en Proveedores,\n" +
						"cierre el establecimiento y vuelva a Ingresar Usuario Y Contraseña\n\n" +
						"¿Desea realizar el Precargue?");
				builder.setCancelable(true);
				builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}

				});
				builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						loadInformation();

					}
				});
				AlertDialog alert11 = builder.create();
				alert11.show();
				break;
		default:
			break;
		}
	}

	// Result of this method,product filter based on payment type
	private void showFilters() {
		final Dialog dialog = new Dialog(ProductsActivity.this);
		dialog.setContentView(R.layout.supplier_pay_clickevnt_dialog);
		dialog.setTitle(getResources().getString(R.string.product_filters));

		TextView text1 = (TextView) dialog.findViewById(R.id.txt_FilterCash);
		TextView text2 = (TextView) dialog
				.findViewById(R.id.txt_FilterDataphone);
		RelativeLayout layout1 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterCash);
		RelativeLayout layout2 = (RelativeLayout) dialog
				.findViewById(R.id.rel_FilterDataphone);
		final RadioButton radio1 = (RadioButton) dialog
				.findViewById(R.id.radio_Cash);
		final RadioButton radio2 = (RadioButton) dialog
				.findViewById(R.id.radio_FilterDataphone);
		RelativeLayout layout3 = (RelativeLayout) dialog
				.findViewById(R.id.rel_payment);
		layout3.setVisibility(View.GONE);

		text1.setText(getResources().getString(R.string.product_expire));
		text2.setText(getResources().getString(R.string.product_linventory));
		radio1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {

				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.product_expire));
					radio2.setChecked(false);
					dialog.dismiss();

				}
			}
		});
		radio2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				if (checked) {
					btnFilter.setText(getResources().getString(
							R.string.product_linventory));
					radio1.setChecked(false);
					dialog.dismiss();

				}
			}
		});
		layout1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio1.setChecked(true);
				radio2.setChecked(false);

			}
		});
		layout2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				radio2.setChecked(true);
				radio1.setChecked(false);

			}
		});
		dialog.show();

	}

	// Result of this method,Search the product based on Validations
	private void refreshList() {

		if (appContext.getScreen() != null) {
			try {
				if (etxtSearch.getText().toString().trim().length() > 2
						|| etxtSearch.getText().toString().trim().length() == 0) {
					List<DTO> updatedList = new ArrayList<DTO>();
					updatedList = ProductDAO.getInstance().getSearchRecordsbyinventorysupplier(DBHandler.getDBObj(Constants.READABLE),
							etxtSearch.getText().toString().trim(),appContext.getSeletedSupplier());
					if (updatedList.size() != 0)
						lvProducts.setAdapter(new ProductsAdapter(this, updatedList));
					else {
						CommonMethods.showCustomToast(ProductsActivity.this, getResources().getString(R.string.nodata));
						lvProducts.setAdapter(new ProductsAdapter(this, updatedList));
					}
					txtProductlistings
							.setText(getResources().getString(R.string.inv_productslist) + " " + updatedList.size());

				} else {
					CommonMethods.showCustomToast(this, getResources().getString(R.string.search_warning));
				}
				if (etxtSearch.getText().toString().trim().length() > 0) {
					imgCloseicon.setVisibility(View.VISIBLE);
					imgSearchicon.setVisibility(View.INVISIBLE);
				}

			} catch (Exception e) {
				CommonMethods.showCustomToast(this, getResources().getString(R.string.search_warning));
				imgCloseicon.setVisibility(View.INVISIBLE);
				imgSearchicon.setVisibility(View.INVISIBLE);
				etxtSearch.setText("");
			}
		
			
		} else {

			try {
				if (etxtSearch.getText().toString().trim().length() > 2
						|| etxtSearch.getText().toString().trim().length() == 0) {
					List<DTO> updatedList = new ArrayList<DTO>();
					updatedList = ProductDAO.getInstance().getSearchRecords(DBHandler.getDBObj(Constants.READABLE),
							etxtSearch.getText().toString().trim());
					if (updatedList.size() != 0)
						lvProducts.setAdapter(new ProductsAdapter(this, updatedList));
					else {
						CommonMethods.showCustomToast(ProductsActivity.this, getResources().getString(R.string.nodata));
						lvProducts.setAdapter(new ProductsAdapter(this, updatedList));
					}
					txtProductlistings
							.setText(getResources().getString(R.string.inv_productslist) + " " + updatedList.size());

				} else {
					CommonMethods.showCustomToast(this, getResources().getString(R.string.search_warning));
				}
				if (etxtSearch.getText().toString().trim().length() > 0) {
					imgCloseicon.setVisibility(View.VISIBLE);
					imgSearchicon.setVisibility(View.INVISIBLE);
				}

			} catch (Exception e) {
				CommonMethods.showCustomToast(this, getResources().getString(R.string.search_warning));
				imgCloseicon.setVisibility(View.INVISIBLE);
				imgSearchicon.setVisibility(View.INVISIBLE);
				etxtSearch.setText("");
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int position,
			long arg3) {

		if (appContext.getScreen() != null) {
			appContext.setScreen("");
			ProductDTO dto = (ProductDTO) productList.get(position);
			Intent intent = new Intent(ProductsActivity.this,
					ProductFinalActivity.class);
			appContext.setProductDTO(dto);
			setResult(1, intent);
			finish();
			appContext.popActivity();

		}

	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(ProductsActivity.this, InventoryActivity.class);
		startActivity(intent);
		this.finish();
		
	}


	//Add Harold, Método que carga la información
	private void loadInformation() {

		List<DTO> productsToInsert = loadProducts(appContext);
		List<DTO> suppliersToInsert = loadSuppliers(appContext);
		List<DTO> inventoryToInsert = loadInventory(appContext);

		if (productsToInsert == null || suppliersToInsert == null || inventoryToInsert == null)
			;
		else {

			ProductDAO.getInstance().insert(
					DBHandler.getDBObj(Constants.WRITABLE), productsToInsert);
			SupplierDAO.getInstance().insert(
					DBHandler.getDBObj(Constants.WRITABLE), suppliersToInsert);
			InventoryDAO.getInstance().insert(
					DBHandler.getDBObj(Constants.WRITABLE), inventoryToInsert);
			refreshList();
		}
	}

	//Add Harold, Carga productos, proveedores e Inventario
	private List<DTO> loadProducts(Context activity) {
		try {
			String tempProduct[];
			AssetManager am = activity.getAssets();
			InputStream is = am.open("Products");
			Scanner s = new Scanner(is).useDelimiter("\\n");
			List<DTO> productListTemp = new ArrayList<DTO>();
			ProductDTO productTemp = new ProductDTO();
			int prodCount;
			while (s.hasNext()) {
				tempProduct = s.next().split(";");
				prodCount = ProductDAO.getInstance().isProductExists(
						DBHandler.getDBObj(Constants.READABLE), tempProduct[0]);
				if (prodCount != 0) ;
				else {
					productTemp = new ProductDTO();
					productTemp.setProductId(tempProduct[0]);
					productTemp.setBarcode(tempProduct[0]);
					productTemp.setName(tempProduct[1]);
					productTemp.setQuantity("0");
					productTemp.setPurchasePrice(tempProduct[2]);
					productTemp.setSellingPrice(tempProduct[3]);
					productTemp.setVat("0");
					productTemp.setSupplierId(tempProduct[4]);
					productTemp.setGroupId("");
					productTemp.setLineId("");
					productTemp.setUom("");
					productTemp.setCreateDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
					productTemp.setModifiedDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
					productTemp.setProductFlag("1");
					productTemp.setActiveStatus(Constants.TRUE);
					productTemp.setSyncStatus(Constants.TRUE);
					productTemp.setSubgroup("");
					productTemp.setMin_count_inventory("1");
					productTemp.setExpiry_date("");
					productTemp.setDiscount("");
					productListTemp.add(productTemp);

				}
			}

			is.close();
			s.close();

			return productListTemp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}


	}

	private List<DTO> loadSuppliers(Context activity) {
		try {
			String tempSupplier[];
			AssetManager am = activity.getAssets();
			InputStream is = am.open("Suppliers");
			Scanner s = new Scanner(is).useDelimiter("\\n");
			List<DTO> supplierListTemp = new ArrayList<DTO>();
			SupplierDTO supDTO = new SupplierDTO();
			List<String> supplierIDsList = SupplierDAO.getInstance()
					.getSupplierIDs(
							DBHandler.getDBObj(Constants.READABLE));


			while (s.hasNext()) {
				tempSupplier = s.next().split(";");


				if (supplierIDsList.contains(tempSupplier[0])) ;
				else {
					supDTO = new SupplierDTO();
					supDTO.setSupplierId(tempSupplier[0]);
					supDTO.setCedula(tempSupplier[0]);
					supDTO.setName(tempSupplier[1]);
					supDTO.setAddress("");
					supDTO.setTelephone("");
					supDTO.setVisitFrequency("");
					supDTO.setVisitDays("");
					supDTO.setContactName("");
					supDTO.setContactTelephone("");
					supDTO.setModifiedDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
					supDTO.setSyncStatus(1);
					supplierListTemp.add(supDTO);
				}
			}


			is.close();
			s.close();
			return supplierListTemp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	private List<DTO> loadInventory(Context activity){
		try{
			String tempInventory[];
			AssetManager am = activity.getAssets();
			InputStream is = am.open("Products");
			Scanner s = new Scanner(is).useDelimiter("\\n");
			List<DTO> listTempInventory = new ArrayList<DTO>();
			InventoryDTO inventoryTempDTO ;
			List<String> inventoryIDs = InventoryDAO.getInstance()
					.getInventoryIDs(
							DBHandler.getDBObj(Constants.READABLE));
			int count = 0;
			Long id = Dates.getSysDateinMilliSeconds();

			while (s.hasNext()){
				tempInventory = s.next().split(";");


				if (inventoryIDs.contains(tempInventory[0]));
				else{
					inventoryTempDTO = new InventoryDTO();
					inventoryTempDTO.setInventoryId((id+count)+"");
					inventoryTempDTO.setProductId(tempInventory[0]);
					inventoryTempDTO.setQuantity("0");
					inventoryTempDTO.setQuantityBalance(0.0);
					inventoryTempDTO.setSyncStatus(1);
					inventoryTempDTO.setUom("");
					listTempInventory.add(inventoryTempDTO);
					count++;
				}
			}


			is.close();
			s.close();
			return listTempInventory;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}


}
