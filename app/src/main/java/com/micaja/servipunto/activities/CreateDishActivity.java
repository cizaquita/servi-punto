package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.adapters.SalesAdapter;
import com.micaja.servipunto.database.DBHandler;
import com.micaja.servipunto.database.dao.DishProductsDAO;
import com.micaja.servipunto.database.dao.DishesDAO;
import com.micaja.servipunto.database.dao.ProductDAO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DishProductsDTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.database.dto.DishesEditDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
import com.micaja.servipunto.dialog.CreateDisDialog;
import com.micaja.servipunto.servicehandler.AsynkTaskClass;
import com.micaja.servipunto.servicehandler.ServiceHandler;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.ConstantsEnum;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.JSONStatus;
import com.micaja.servipunto.utils.SalesCodes;

public class CreateDishActivity extends BaseActivity implements
		OnClickListener, android.content.DialogInterface.OnClickListener {
	private Button btnZero, btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix,
			btnSeven, btnEight, btnNine, btnBackClear, btnAllClear, btnSearch,
			btnEnter, btnSaveDish, btnCancelDish, btnBarcode;

	private EditText etxtBarcode;

	private TextView txtTotalItems, txtSubTotal, txtVat, txtTotalAmount,
			txtProductTit;
	private ListView lvSelectedProdu;
	private SalesAdapter salesAdapter;
	private List<DTO> selectedList, dishesList, productsList;
	private AlertDialog cancelDialog;
	private boolean isProductAdded, isProductExists;
	private String editDishId, barCode, screenMode = "";
	ServiApplication appContext;
	Intent intent;
	public SharedPreferences sharedpreferences;
	// private static String dishId;

	private JSONArray dishProJsonOBJ = new JSONArray();
	private Bundle bundle;
	private boolean isCreating;
	private String dishId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// intent = new Intent(this, OrdersActivity.class);
		intent = new Intent(this, CreateDishActivity.class);

		appContext = (ServiApplication) getApplicationContext();
		appContext.setSeletedProducts(new ArrayList<DTO>());
		sharedpreferences = getSharedPreferences(
				ServiApplication.MyPREFERENCES, Context.MODE_PRIVATE);
		ServiApplication.userName = sharedpreferences
				.getString("user_name", "");
		ServiApplication.store_id = sharedpreferences.getString("store_code",
				"");
		inItUI();

	}

	// Result of this method,registration for all UI views.
	private void inItUI() {
		setContentView(R.layout.create_dish);

		btnZero = (Button) findViewById(R.id.btn_SalesCalZero);
		btnOne = (Button) findViewById(R.id.btn_SalesCalOne);
		btnTwo = (Button) findViewById(R.id.btn_SalesCalTwo);
		btnThree = (Button) findViewById(R.id.btn_SalesCalThree);
		btnFour = (Button) findViewById(R.id.btn_SalesCalFour);
		btnFive = (Button) findViewById(R.id.btn_SalesCalFive);
		btnSix = (Button) findViewById(R.id.btn_SalesCalSix);
		btnSeven = (Button) findViewById(R.id.btn_SalesCalSeven);
		btnEight = (Button) findViewById(R.id.btn_SalesCalEight);
		btnNine = (Button) findViewById(R.id.btn_SalesCalNine);
		btnBackClear = (Button) findViewById(R.id.btn_SalesCalClearOne);
		btnAllClear = (Button) findViewById(R.id.btn_SalesCalClear);
		btnSearch = (Button) findViewById(R.id.btn_SalesCalSearch);
		btnEnter = (Button) findViewById(R.id.btn_SalesCalEnter);
		btnSaveDish = (Button) findViewById(R.id.btn_SaleEnd);
		btnCancelDish = (Button) findViewById(R.id.btn_SaleCancel);
		btnBarcode = (Button) findViewById(R.id.btn_SalesBarCode);

		etxtBarcode = (EditText) findViewById(R.id.etxt_SalesBarcode);

		txtProductTit = (TextView) findViewById(R.id.txt_ProductTitle);

		txtProductTit.setText(getResources().getString(R.string.dish_name));

		txtTotalItems = (TextView) findViewById(R.id.txt_TotalItems);
		txtSubTotal = (TextView) findViewById(R.id.txt_SubTotalAmt);
		txtVat = (TextView) findViewById(R.id.txt_Vat);
		txtTotalAmount = (TextView) findViewById(R.id.txt_TotalAmount);

		lvSelectedProdu = (ListView) findViewById(R.id.lv_SalesProduct);

		btnSaveDish.setText(getResources().getString(R.string.save));
		btnCancelDish.setText(getResources().getString(R.string.cancel));

		btnZero.setOnClickListener(this);
		btnOne.setOnClickListener(this);
		btnTwo.setOnClickListener(this);
		btnThree.setOnClickListener(this);
		btnFour.setOnClickListener(this);
		btnFive.setOnClickListener(this);
		btnSix.setOnClickListener(this);
		btnSeven.setOnClickListener(this);
		btnEight.setOnClickListener(this);
		btnNine.setOnClickListener(this);
		btnBackClear.setOnClickListener(this);
		btnAllClear.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnEnter.setOnClickListener(this);
		btnSaveDish.setOnClickListener(this);
		btnCancelDish.setOnClickListener(this);
		btnBarcode.setOnClickListener(this);

		etxtBarcode.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				etxtBarcode.setSingleLine(false);
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					etxtBarcode.setSingleLine(true);
					if (validateBarCode()) {
						ProductDTO clickedProduct = new ProductDTO();
						clickedProduct = ProductDAO
								.getInstance()
								.getRecordsByProductID(
										DBHandler.getDBObj(Constants.READABLE),
										etxtBarcode.getText().toString().trim());
						List<DTO> selectedProductsList = appContext
								.getSelectedProducts();
						boolean isExists = false;
						for (int i = 0; i < selectedProductsList.size(); i++) {
							SelectedProddutsDTO sDTO = (SelectedProddutsDTO) selectedProductsList
									.get(i);
							if (sDTO.getIdProduct().equals(
									clickedProduct.getBarcode())
									|| sDTO.getIdProduct().equals(
											clickedProduct.getProductId())) {
								int qty = 0;
								if (!sDTO.getQuantity().equals(""))
									qty = Integer.parseInt(sDTO.getQuantity()) + 1;

								clickedProduct.setQuantity(String.valueOf(qty));
								selectedProductsList.set(i,
										getSelectedProDTO(clickedProduct));

								isExists = true;
								break;
							}
						}
						if (!isExists) {
							clickedProduct.setQuantity("1");
							selectedProductsList
									.add(getSelectedProDTO(clickedProduct));
							appContext.setSeletedProducts(selectedProductsList);
						}
						updateList();

					} else {
						CommonMethods.displayAlert(true, getResources()
								.getString(R.string.alert), getResources()
								.getString(R.string.invalid_products_msg),
								getResources().getString(R.string.ok), "",
								CreateDishActivity.this, null, false);

					}
					etxtBarcode.getText().clear();
					isProductExists = false;
				}
				return false;
			}

		});
		loadUI();
		getDataFromDB();

	}

	private void getDataFromDB() {
		if (getIntent().getExtras() != null) {
			System.out.println("Dish ID :"
					+ getIntent().getExtras().getString(
							ConstantsEnum.EDIT_DISH.code()));
			dishesList = DishesDAO.getInstance().getRecordsWithValues(
					DBHandler.getDBObj(Constants.READABLE),
					"dish_id",
					getIntent().getExtras().getString(
							ConstantsEnum.EDIT_DISH.code()));
			productsList = DishProductsDAO.getInstance().getEditDishesRecords(
					DBHandler.getDBObj(Constants.READABLE),
					getIntent().getExtras().getString(
							ConstantsEnum.EDIT_DISH.code()));

			if (dishesList.size() != 0 && productsList.size() != 0)
				setDataToViews(productsList);

		} else
			isCreating = true;
	}

	private void loadUI() {
		if (getIntent().getExtras() != null)
			screenMode = "Edit";

		selectedList = appContext.getSelectedProducts();
		salesAdapter = new SalesAdapter(this, selectedList, uiHandler,
				ConstantsEnum.DISH.code(), ConstantsEnum.DISH.code(),
				screenMode, "CreateDish");
		lvSelectedProdu.setAdapter(salesAdapter);
		setValuesToPriceTag();
	}

	private void setDataToViews(List<DTO> list) {
		List<DTO> selectProList = new ArrayList<DTO>();
		for (int i = 0; i < list.size(); i++) {
			DishesEditDTO dto = (DishesEditDTO) list.get(i);

			SelectedProddutsDTO selectedProddutsDTO = new SelectedProddutsDTO();

			selectedProddutsDTO.setBarcode(dto.getProductId());
			selectedProddutsDTO.setIdProduct(dto.getProductId());
			selectedProddutsDTO.setName(dto.getProductName());
			selectedProddutsDTO.setPrice(dto.getPurchasePrice());
			selectedProddutsDTO.setSellPrice(dto.getSellingCostPerItem());
			selectedProddutsDTO.setQuantity(dto.getQuantity());
			selectedProddutsDTO.setUnits(dto.getUnits());
			selectedProddutsDTO.setVat(dto.getVat());
			selectedProddutsDTO.setIdDishProduct(dto.getDishProductId());

			selectProList.add(selectedProddutsDTO);
		}
		appContext.setSeletedProducts(selectProList);
		selectedList = appContext.getSelectedProducts();
		updateList();
		setValuesToPriceTag();
	}

	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			bundle = msg.getData();
			if (msg.arg1 == SalesCodes.EDIT.code()) {
				isProductAdded = true;
				updateList();
			} else if (msg.arg1 == SalesCodes.DELETE.code()) {
				isProductAdded = true;
				updateList();
			} else if (msg.arg1 == SalesCodes.DISH_CREATE.code())
				validateAndInsert(msg.getData());
		}
	};

	private void setValuesToPriceTag() {
		int totalItems = 0;
		double subTotal = 0, vat = 0;

		for (int i = 0; i < selectedList.size(); i++) {
			SelectedProddutsDTO dto = (SelectedProddutsDTO) selectedList.get(i);

			// ProductDetailsDTO dto = (ProductDetailsDTO)selectedList.get(i)

			totalItems += CommonMethods.getDoubleFormate(dto.getQuantity());

			subTotal += CommonMethods.getDoubleFormate(dto.getPrice())
					* CommonMethods.getDoubleFormate(dto.getQuantity());
			boolean isPrice = !"".equals(dto.getPrice())
					&& dto.getPrice() != null;
			boolean isVat = !"".equals(dto.getVat()) && dto.getVat() != null;

			if (isPrice && isVat)
				vat += (CommonMethods.getDoubleFormate(dto.getPrice()) / 100)
						* CommonMethods.getDoubleFormate(dto.getVat())
						* CommonMethods.getDoubleFormate(dto.getQuantity());

			appContext.setTotalAmount(subTotal + vat);
		}

		appContext.setTotalAmount(subTotal + vat);
		txtTotalItems.setText(getResources().getString(
				R.string.sales_total_items)
				+ " " + totalItems);
		txtSubTotal.setText(getResources().getString(R.string.sales_sub_total)
				+ " " + CommonMethods.getNumSeparator(subTotal));
		txtVat.setText(getResources().getString(R.string.sales_vat) + " "
				+ CommonMethods.getNumSeparator(vat));
		txtTotalAmount.setText(getResources().getString(
				R.string.sales_total_amount)
				+ " "
				+ CommonMethods.getNumSeparator(appContext.getTotalAmount()));
	}

	private void updateList() {
		System.out.println("selectedList :" + selectedList.size());
		selectedList = appContext.getSelectedProducts();
		salesAdapter.setListData(selectedList);
		salesAdapter.notifyDataSetChanged();

		setValuesToPriceTag();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_SalesCalZero:
			setVauleToBarcode("0");
			break;

		case R.id.btn_SalesCalOne:
			setVauleToBarcode("1");
			break;

		case R.id.btn_SalesCalTwo:
			setVauleToBarcode("2");
			break;

		case R.id.btn_SalesCalThree:
			setVauleToBarcode("3");
			break;

		case R.id.btn_SalesCalFour:
			setVauleToBarcode("4");
			break;

		case R.id.btn_SalesCalFive:
			setVauleToBarcode("5");
			break;

		case R.id.btn_SalesCalSix:
			setVauleToBarcode("6");
			break;

		case R.id.btn_SalesCalSeven:
			setVauleToBarcode("7");
			break;

		case R.id.btn_SalesCalEight:
			setVauleToBarcode("8");
			break;

		case R.id.btn_SalesCalNine:
			setVauleToBarcode("9");
			break;

		case R.id.btn_SalesCalClearOne:
			if (etxtBarcode.getText().toString().trim().length() != 0)
				removeLastDigit();
			break;

		case R.id.btn_SalesCalClear:
			etxtBarcode.getText().clear();
			etxtBarcode.setCursorVisible(true);
			etxtBarcode.setFocusable(true);
			isProductExists = false;
			etxtBarcode.setFocusableInTouchMode(true);
			etxtBarcode.requestFocus();
			break;

		case R.id.btn_SalesCalSearch:
			Intent intent = new Intent(CreateDishActivity.this,
					ProductSeleDishActivity.class);
			appContext.pushActivity(new Intent(this, CreateDishActivity.class));
			startActivityForResult(intent, SalesCodes.SELECT_PRODUCT.code());
			break;

		case R.id.btn_SalesCalEnter:
			if (validateBarCode()) {
				ProductDTO clickedProduct = new ProductDTO();
				clickedProduct = ProductDAO.getInstance()
						.getRecordsByProductID(
								DBHandler.getDBObj(Constants.READABLE),
								etxtBarcode.getText().toString().trim());

				List<DTO> selectedProductsList = appContext
						.getSelectedProducts();

				boolean isExists = false;
				for (int i = 0; i < selectedProductsList.size(); i++) {
					SelectedProddutsDTO sDTO = (SelectedProddutsDTO) selectedProductsList
							.get(i);
					if (sDTO.getIdProduct().equals(clickedProduct.getBarcode())
							|| sDTO.getIdProduct().equals(
									clickedProduct.getProductId())) {
						int qty = 0;
						if (!sDTO.getQuantity().equals(""))
							qty = Integer.parseInt(sDTO.getQuantity()) + 1;

						clickedProduct.setQuantity(String.valueOf(qty));
						selectedProductsList.set(i,
								getSelectedProDTO(clickedProduct));

						isExists = true;
						break;
					}
				}
				if (!isExists) {
					clickedProduct.setQuantity("1");
					selectedProductsList.add(getSelectedProDTO(clickedProduct));
					appContext.setSeletedProducts(selectedProductsList);
				}

				// list.add(getSelectedProDTO(dto));
				// appContext.setSeletedProducts(list);
				updateList();

			} else {
				etxtBarcode.getText().clear();

				CommonMethods
						.showcustomdialogbox(
								CreateDishActivity.this,
								getResources().getString(R.string.oops_errmsg),
								getResources().getString(
										R.string.invalid_products_msg), null);
			}
			break;
		case R.id.btn_SaleEnd:
			validateFields();
			break;
		case R.id.btn_SaleCancel:
			cancelDialog = CommonMethods.displayAlert(false, getResources()
					.getString(R.string.cancel_dish),
					getResources().getString(R.string.cancel_dish_msg),
					getResources().getString(R.string.yes), getResources()
							.getString(R.string.no), CreateDishActivity.this,
					CreateDishActivity.this, true);
			break;

		case R.id.btn_SalesBarCode:

			break;

		default:
			break;
		}
	}

	private DTO getSelectedProDTO(ProductDTO dto) {
		SelectedProddutsDTO selectedProddutsDTO = new SelectedProddutsDTO();

		selectedProddutsDTO.setBarcode(dto.getBarcode());
		selectedProddutsDTO.setIdProduct(dto.getProductId());
		selectedProddutsDTO.setName(dto.getName());
		selectedProddutsDTO.setPrice(dto.getPurchasePrice());
		selectedProddutsDTO.setQuantity(dto.getQuantity());
		selectedProddutsDTO.setSellPrice(dto.getSellingPrice());
		selectedProddutsDTO.setVat(dto.getVat());
		selectedProddutsDTO.setUnits(dto.getUom());

		return selectedProddutsDTO;
	}
	// Result of this method,product is exists or not in Product table
	private boolean validateBarCode() {
		barCode = etxtBarcode.getText().toString().trim();

		if (barCode.length() != 0) {
			int prodCount = ProductDAO.getInstance().isProductExists(
					DBHandler.getDBObj(Constants.READABLE), barCode);

			if (prodCount != 0)
				isProductExists = true;
			else
				isProductExists = false;
		}

		return isProductExists;
	}
	// This method using for Validation purpose
	private void validateFields() {
		if (appContext.getSelectedProducts().size() != 0) {
			if (appContext.getTotalAmount() > 0) {
				DishesDTO dishesDTO;
				if (getIntent().getExtras() != null) {
					dishesDTO = (DishesDTO) dishesList.get(0);
					editDishId = dishesDTO.getDishId();
				} else
					dishesDTO = new DishesDTO();

				CreateDisDialog finishDialog = new CreateDisDialog(
						CreateDishActivity.this, String.valueOf(appContext
								.getTotalAmount()), dishesDTO, uiHandler,
						isCreating);
				finishDialog.show();
			} else

				CommonMethods
						.showcustomdialogbox(
								CreateDishActivity.this,
								getResources().getString(R.string.oops_errmsg),
								getResources().getString(
										R.string.invalid_total_amount), null);
		} else
			CommonMethods.showcustomdialogbox(CreateDishActivity.this,
					getResources().getString(R.string.oops_errmsg),
					getResources().getString(R.string.select_products), null);

	}

	private DishesDTO getDishDetails() {
		DishesDTO dishesDTO = new DishesDTO();
		if (getIntent().getExtras() != null) {
			DishesDTO dto = (DishesDTO) dishesList.get(0);
			dishesDTO.setDishId(dto.getDishId());
			dishesDTO.setDishName(dto.getDishName());
			dishesDTO.setDishPrice(dto.getDishPrice());
			dishesDTO.setExpiryDays(dto.getExpiryDays());
			dishesDTO.setNoOfItems(dto.getNoOfItems());
			dishesDTO.setSellingCostperItem(dto.getSellingCostperItem());
			dishesDTO.setVat(dto.getVat());
		}

		return dishesDTO;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		appContext.popActivity();

		if (requestCode == SalesCodes.SELECT_PRODUCT.code()) {
			appContext.setScreen(null);
			isProductAdded = true;
			updateList();
		}
	}

	private void removeLastDigit() {
		if (etxtBarcode.getText().toString().trim().length() == 1) {
			etxtBarcode.getText().clear();
			etxtBarcode.setCursorVisible(true);
			etxtBarcode.setFocusable(true);
			isProductExists = false;
			etxtBarcode.setFocusableInTouchMode(true);
			etxtBarcode.requestFocus();
		} else {
			String barcode = etxtBarcode.getText().toString().trim();
			barcode = barcode.substring(0, barcode.length() - 1);
			etxtBarcode.setText(barcode);
		}
		etxtBarcode.setSelection(etxtBarcode.getText().length());
	}

	private void setVauleToBarcode(String enterValue) {
		String barcode = etxtBarcode.getText().toString().trim();

		if (barcode.length() == 0)
			etxtBarcode.setText(enterValue);
		else {
			barcode = barcode.concat(enterValue);
			etxtBarcode.setText(barcode);
		}
		etxtBarcode.setSelection(etxtBarcode.getText().length());
	}

	private void validateAndInsert(Bundle bundle) {
		if (getIntent().getExtras() != null)
			updateDishRecords(bundle);
		else
			insertRecords(bundle);
	}

	private void insertRecords(Bundle bundle) {
		if (DishesDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), getDishList(bundle)))
			insertDishProducts();
	}

	private void updateDishRecords(Bundle bundle) {
		if (DishesDAO.getInstance().update(
				DBHandler.getDBObj(Constants.WRITABLE),
				getDishList(bundle).get(0))) {
			if (isProductAdded)
				insertDishProducts();
			else
				navigateBackScreen("update");
		}
	}

	private void insertDishProducts() {
		if (DishProductsDAO.getInstance().insert(
				DBHandler.getDBObj(Constants.WRITABLE), getDishProductsList()))
			navigateBackScreen("insert");
	}

	private List<DTO> getDishProductsList() {
		List<DTO> list = new ArrayList<DTO>();

		List<DTO> seletedList = appContext.getSelectedProducts();
		if (isProductAdded && getIntent().getExtras() != null)
			dishId = editDishId;
		/*
		 * else { dishId = DishesDAO.getInstance().getDishID(
		 * DBHandler.getDBObj(Constants.READABLE)); }
		 */

		List<String> productsList = DishProductsDAO.getInstance()
				.getDishProductRecords(DBHandler.getDBObj(Constants.READABLE),
						dishId);
		for (int i = 0; i < seletedList.size(); i++) {
			SelectedProddutsDTO selectedProddutsDTO = (SelectedProddutsDTO) seletedList
					.get(i);
			if (!productsList.contains(selectedProddutsDTO.getIdDishProduct())) {
				String id = Dates.getSysDateinMilliSeconds() + "" + i;
				System.out.println("id :" + id);
				DishProductsDTO dishesDTO = new DishProductsDTO();
				dishesDTO.setDishProductId(id);
				dishesDTO.setDishId(dishId);
				dishesDTO.setProductId(selectedProddutsDTO.getIdProduct());
				dishesDTO.setQuantity(selectedProddutsDTO.getQuantity());
				dishesDTO.setSyncStatus(0);

				if (selectedProddutsDTO.getUnits() != null)
					dishesDTO.setUom(selectedProddutsDTO.getUnits());
				else
					dishesDTO.setUom("");

				list.add(dishesDTO);
			} else
				updateDishProduct(selectedProddutsDTO, dishId);
		}

		dishProJsonOBJ = getDishProducts(list);
		return list;
	}

	private JSONArray getDishProducts(List<DTO> list) {
		JSONArray jsonArray = new JSONArray();
		try {
			for (int i = 0; i < list.size(); i++) {
				DishProductsDTO dto = (DishProductsDTO) list.get(i);
				JSONObject jsonobj1 = new JSONObject();
				jsonobj1.put("dish_product_id",
						Long.parseLong(dto.getDishProductId()));
				jsonobj1.put("dish_id", Long.parseLong(dto.getDishId()));
				jsonobj1.put("barcode", dto.getProductId());
				jsonobj1.put("uom", dto.getUom());
				jsonobj1.put("quantity", dto.getQuantity());
				jsonobj1.put("store_code", ServiApplication.store_id);
				jsonArray.put(jsonobj1);
				dishId = dto.getDishId();
			}
			return jsonArray;
		} catch (Exception e) {

		}
		return jsonArray;
	}

	private void updateDishProduct(SelectedProddutsDTO selectedProddutsDTO,
			String dishId) {
		DishProductsDTO dishesDTO = new DishProductsDTO();
		dishesDTO.setDishProductId(selectedProddutsDTO.getIdDishProduct());
		dishesDTO.setDishId(dishId);
		dishesDTO.setProductId(selectedProddutsDTO.getIdProduct());
		dishesDTO.setQuantity(selectedProddutsDTO.getQuantity());
		dishesDTO.setSyncStatus(0);
		if (selectedProddutsDTO.getUnits() != null)
			dishesDTO.setUom(selectedProddutsDTO.getUnits());
		else
			dishesDTO.setUom("");

		DishProductsDAO.getInstance().update(
				DBHandler.getDBObj(Constants.WRITABLE), dishesDTO);
	}

	// Bind new dish product data to BindDataTo DishProduct DTO
	private List<DTO> getDishProUpdateList() {
		List<DTO> list = new ArrayList<DTO>();

		List<DTO> seletedList = appContext.getSelectedProducts();

		for (int i = 0; i < seletedList.size(); i++) {
			SelectedProddutsDTO selectedProddutsDTO = (SelectedProddutsDTO) seletedList
					.get(i);

			DishesEditDTO dishEditDTO = (DishesEditDTO) productsList.get(i);
			DishProductsDTO dishesDTO = new DishProductsDTO();

			dishesDTO.setDishProductId(dishEditDTO.getDishProductId());
			dishesDTO.setDishId(dishEditDTO.getDishId());
			dishesDTO.setProductId(selectedProddutsDTO.getIdProduct());
			dishesDTO.setQuantity(selectedProddutsDTO.getQuantity());
			dishesDTO.setSyncStatus(0);
			dishesDTO.setUom(selectedProddutsDTO.getUnits());

			list.add(dishesDTO);
		}
		return list;
	}

	// Bind new dish data to BindDataTo Dish DTO
	private List<DTO> getDishList(Bundle bundle) {
		List<DTO> list = new ArrayList<DTO>();

		DishesDTO dishesDTO = new DishesDTO();

		dishesDTO.setDishId(bundle.getString("dishID"));
		dishesDTO.setDishName(bundle.getString("dishName"));
		dishesDTO.setDishPrice(bundle.getString("totalAmount"));
		dishesDTO.setExpiryDays(bundle.getString("expiryDays"));
		dishesDTO.setNoOfItems(bundle.getString("noOfItems"));
		dishesDTO.setSellingCostperItem(bundle.getString("sellingPrice"));
		dishesDTO.setSyncStatus(0);
		dishesDTO.setVat(bundle.getString("vat"));

		if (screenMode.equalsIgnoreCase("Edit"))
			dishId = editDishId;
		else
			dishId = String.valueOf(Dates.getSysDateinMilliSeconds());

		dishesDTO.setDishId(dishId);

		list.add(dishesDTO);
		return list;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		if (dialog == cancelDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				appContext.setSeletedProducts(new ArrayList<DTO>());
				appContext.setSelectedProduct("");
				appContext.setTotalAmount(0);
				// navigateBackScreen("cancel");
				if (appContext.getActivityListSize() > 0) {
					appContext
							.getActivityList()
							.get(appContext.getActivityListSize() - 1)
							.putExtra(getResources().getString(R.string.back),
									"true");
					startActivity(appContext.getActivityList().get(
							appContext.getActivityListSize() - 1));
					finish();
					appContext.popActivity();
				}
			}

		}
	}

	// Result of this method,Create and Update Dish
	private void navigateBackScreen(String type) {
		if ("insert".equals(type)) {
			if (new AsynkTaskClass().callcentralserver("/dish/create", ""
					+ ServiceHandler.POST, getDishJSON().trim(),
					CreateDishActivity.this) != null) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					CommonMethods.showCustomToast(CreateDishActivity.this,
							getResources().getString(R.string.dish_insert_msg));
					appContext.setSeletedProducts(new ArrayList<DTO>());
					appContext.setMenusDishesList(new ArrayList<DTO>());
					appContext.setTotalAmount(0);

					List<DTO> list1 = DishesDAO.getInstance().getRecords(
							DBHandler.getDBObj(Constants.WRITABLE));
					DishesDTO ddto = (DishesDTO) list1.get(list1.size() - 1);
					ddto.setSyncStatus(1);
					DishesDAO.getInstance().update(
							DBHandler.getDBObj(Constants.WRITABLE), ddto);
					// CommonMethods.openNewActivity(CreateDishActivity.this,
					// MenusActivity.class);
					goBackScreen();
					this.finish();
				}
			} else {
				CommonMethods.showCustomToast(CreateDishActivity.this,
						getResources().getString(R.string.dish_insert_msg));

				appContext.setSeletedProducts(new ArrayList<DTO>());
				appContext.setMenusDishesList(new ArrayList<DTO>());
				appContext.setTotalAmount(0);

				// CommonMethods.openNewActivity(CreateDishActivity.this,
				// MenusActivity.class);
				goBackScreen();
				this.finish();
			}

		} else if ("update".equals(type)) {
			if (new AsynkTaskClass().callcentralserver("/dish/update", ""
					+ ServiceHandler.POST, getDishUpdateJSON().trim(),
					CreateDishActivity.this) != null) {
				if (new JSONStatus().status(ServiApplication.responds_feed) == 0) {
					CommonMethods.showCustomToast(CreateDishActivity.this,
							getResources().getString(R.string.dish_edit_msg));

					appContext.setSeletedProducts(new ArrayList<DTO>());
					appContext.setMenusDishesList(new ArrayList<DTO>());
					appContext.setTotalAmount(0);
					List<DTO> list = DishesDAO.getInstance()
							.getRecordsWithValues(
									DBHandler.getDBObj(Constants.READABLE),
									"dish_id", editDishId);
					DishesDTO ddto = (DishesDTO) list.get(0);
					ddto.setSyncStatus(1);
					DishesDAO.getInstance().update(
							DBHandler.getDBObj(Constants.WRITABLE), ddto);
					// CommonMethods.openNewActivity(CreateDishActivity.this,MenusActivity.class);
					goBackScreen();
					this.finish();
				}
			} else {
				CommonMethods.showCustomToast(CreateDishActivity.this,
						getResources().getString(R.string.dish_edit_msg));
				appContext.setSeletedProducts(new ArrayList<DTO>());
				appContext.setMenusDishesList(new ArrayList<DTO>());
				appContext.setTotalAmount(0);

				// CommonMethods.openNewActivity(CreateDishActivity.this,MenusActivity.class);
				goBackScreen();
				this.finish();
			}
		}
		this.finish();
	}

	private void goBackScreen() {

		if (appContext.getActivityListSize() > 0) {
			appContext.getActivityList()
					.get(appContext.getActivityListSize() - 1)
					.putExtra(getResources().getString(R.string.back), "true");
			startActivity(appContext.getActivityList().get(
					appContext.getActivityListSize() - 1));
			finish();
			appContext.popActivity();
		}
	}

	// This method using for Create Dish that time data passing
	// to the json objects
	private String getDishUpdateJSON() {
		List<DTO> list = DishesDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "dish_id", editDishId);
		DishesDTO dto = (DishesDTO) list.get(0);
		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("dish_id", Long.parseLong(dto.getDishId()));
			jsonobj.put("dish_name", dto.getDishName());
			jsonobj.put("dish_price",
					CommonMethods.getDoubleFormate(dto.getDishPrice()));
			jsonobj.put("no_of_items", dto.getNoOfItems());
			jsonobj.put("expiry_days", dto.getExpiryDays());
			jsonobj.put("vat", dto.getVat());
			jsonobj.put("selling_cost_per_item",
					CommonMethods.getDoubleFormate(dto.getSellingCostperItem()));
			jsonobj.put("store_code", ServiApplication.store_id);
			jsonobj.put("products", getProductJsonObj(dto.getDishId()));
			return jsonobj.toString();
		} catch (Exception e) {
			return jsonobj.toString();
		}
	}

	// This method using for Create and Update Dishproduct that time data
	// passing
	// to the json objects
	private JSONArray getProductJsonObj(String dishId2) {
		List<DTO> list = DishProductsDAO.getInstance().getRecordsWithValues(
				DBHandler.getDBObj(Constants.READABLE), "dish_id", dishId2);
		JSONArray jsonArray = new JSONArray();
		try {
			for (int i = 0; i < list.size(); i++) {
				DishProductsDTO dto = (DishProductsDTO) list.get(i);
				JSONObject jsonobj1 = new JSONObject();
				jsonobj1.put("dish_product_id",
						Long.parseLong(dto.getDishProductId()));
				jsonobj1.put("dish_id", Long.parseLong(dto.getDishId()));
				jsonobj1.put("barcode", dto.getProductId());
				jsonobj1.put("uom", dto.getUom());
				jsonobj1.put("quantity", dto.getQuantity());
				jsonobj1.put("store_code", ServiApplication.store_id);
				jsonArray.put(jsonobj1);
			}
			return jsonArray;
		} catch (Exception e) {
			return jsonArray;
		}
	}

	// This method using for Create Dish that time data passing
	// to the json objects
	private String getDishJSON() {

		List<DTO> dto = DishesDAO.getInstance().getRecords(
				DBHandler.getDBObj(Constants.READABLE));
		DishesDTO dishdto = (DishesDTO) dto.get(dto.size() - 1);

		JSONObject jsonobj = new JSONObject();
		try {
			jsonobj.put("dish_id", Long.parseLong(dishdto.getDishId()));
			jsonobj.put("dish_name", dishdto.getDishName());
			jsonobj.put("dish_price",
					CommonMethods.getDoubleFormate(dishdto.getDishPrice()));
			jsonobj.put("no_of_items", dishdto.getNoOfItems());
			jsonobj.put("expiry_days", dishdto.getExpiryDays());
			jsonobj.put("vat", dishdto.getVat());
			jsonobj.put("selling_cost_per_item", CommonMethods
					.getDoubleFormate(dishdto.getSellingCostperItem()));
			jsonobj.put("store_code", ServiApplication.store_id);
			try {
				jsonobj.put("products", dishProJsonOBJ);
			} catch (Exception e) {

			}
			return jsonobj.toString().trim();
		} catch (Exception e) {
			return jsonobj.toString().trim();
		}
	}

}
