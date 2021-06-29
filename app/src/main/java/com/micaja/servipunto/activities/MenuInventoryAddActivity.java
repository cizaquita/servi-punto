package com.micaja.servipunto.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.micaja.servipunto.adapters.MenuInvenAddAdapter;
import com.micaja.servipunto.database.dto.AddMenusInvenDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.dialog.AddMenusInveDialog;
import com.micaja.servipunto.dialog.MenusInventoryDailog;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.SalesCodes;

public class MenuInventoryAddActivity extends BaseActivity implements
		OnClickListener, android.content.DialogInterface.OnClickListener {
	private Button btnZero, btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix,
			btnSeven, btnEight, btnNine, btnBackClear, btnAllClear, btnSearch,
			btnEnter, btnSaveDish, btnCancelDish, btnBarcode;

	private EditText etxtBarcode;

	private TextView txtTotalItems, txtSubTotal, txtVat, txtTotalAmount,
			txtProductTit, txtUnitsTit;;

	private ListView lvSelectedProdu;
	private MenuInvenAddAdapter menusInvenAdapter;
	private List<DTO> selectedList = new ArrayList<DTO>();
	private AlertDialog cancelDialog;
	ServiApplication appContext;

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		intent = new Intent(this, OrdersActivity.class);

		appContext = (ServiApplication) getApplicationContext();

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
		txtUnitsTit = (TextView) findViewById(R.id.txt_UnitsTitle);

		txtProductTit.setText(getResources().getString(R.string.dish_name));
		txtUnitsTit.setVisibility(View.GONE);

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
				etxtBarcode.setSingleLine(false);
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					etxtBarcode.setSingleLine(true);
					etxtBarcode.getText().clear();
				} else {

				}

				return false;
			}
		});

		loadUI();
	}

	private void loadUI() {
		selectedList = appContext.getSelectedProducts();
		menusInvenAdapter = new MenuInvenAddAdapter(this, selectedList,
				uiHandler);
		lvSelectedProdu.setAdapter(menusInvenAdapter);
		setValuesToPriceTag();
	}

	// This method using for Handler message to update the UI thread
	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SalesCodes.EDIT.code())
				updateList();
			else if (msg.arg1 == SalesCodes.DELETE.code())
				updateList();
			else if (msg.arg1 == SalesCodes.CLIENT_SALE_END.code())
				updateList();
			else if (msg.arg1 == SalesCodes.ADD_MENUS_INVENTORY.code())
				navigateBackScreen("");
		}
	};

	private void setValuesToPriceTag() {
		int totalItems = 0;
		double subTotal = 0, vat = 0;

		for (int i = 0; i < selectedList.size(); i++) {
			AddMenusInvenDTO dto = (AddMenusInvenDTO) selectedList.get(i);

			// ProductDetailsDTO dto = (ProductDetailsDTO)selectedList.get(i)

			totalItems += CommonMethods.getDoubleFormate(dto.getQuantity());

			subTotal += CommonMethods.getDoubleFormate(dto.getSellingPrice())
					* CommonMethods.getDoubleFormate(dto.getQuantity());
			boolean isPrice = !"".equals(dto.getSellingPrice())
					&& dto.getSellingPrice() != null;
			boolean isVat = (!"".equals(dto.getVat()) && dto.getVat() != null);
			if (isPrice && isVat)
				vat += (CommonMethods.getDoubleFormate(dto.getSellingPrice()) / 100)
						* CommonMethods.getDoubleFormate(dto.getVat())
						* CommonMethods.getDoubleFormate(dto.getQuantity());
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
		selectedList = appContext.getSelectedProducts();
		menusInvenAdapter.setListData(selectedList);
		menusInvenAdapter.notifyDataSetChanged();
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
			etxtBarcode.setText("");
			break;

		case R.id.btn_SalesCalSearch:
			Intent intent = new Intent(MenuInventoryAddActivity.this,
					DishesSeleActivity.class);
			intent.putExtra("Menus Iventory", "Menus Iventory");
			startActivityForResult(intent, SalesCodes.SELECT_PRODUCT.code());
			break;

		case R.id.btn_SalesCalEnter:

			break;

		case R.id.btn_SaleEnd:
			if (appContext.getTotalAmount() > 0) {
				AddMenusInveDialog dialog = new AddMenusInveDialog(
						MenuInventoryAddActivity.this, uiHandler);
				dialog.show();
			}

			break;
		case R.id.btn_SaleCancel:
			cancelDialog = CommonMethods.displayAlert(false, getResources()
					.getString(R.string.cancel_dish),
					getResources().getString(R.string.cancel_dish_msg),
					getResources().getString(R.string.yes), getResources()
							.getString(R.string.no),
					MenuInventoryAddActivity.this,
					MenuInventoryAddActivity.this, true);

			break;

		case R.id.btn_SalesBarCode:

			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SalesCodes.SELECT_PRODUCT.code()) {
			DTO dto = appContext.getDishDTO();
			MenusInventoryDailog dialog = new MenusInventoryDailog(
					MenuInventoryAddActivity.this, dto, uiHandler);
			dialog.show();
		}
	}

	private void removeLastDigit() {
		if (etxtBarcode.getText().toString().trim().length() == 1)
			etxtBarcode.setText("");
		else {
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

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		if (dialog == cancelDialog) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				appContext.setSeletedProducts(new ArrayList<DTO>());
				appContext.setSelectedProduct("");
				appContext.setTotalAmount(0);
				navigateBackScreen("cancel");
			}
		}
	}

	// Result of this method,navigate back button screen
	private void navigateBackScreen(String type) {
		if (!"cancel".equals(type)) {
			CommonMethods.showCustomToast(MenuInventoryAddActivity.this,
					getResources().getString(R.string.menu_inven_succ_msg));
		}
		appContext.setSeletedProducts(new ArrayList<DTO>());
		appContext.setTotalAmount(0);
		appContext.setDishDTO(null);

		// CommonMethods.openNewActivity(MenuInventoryAddActivity.this,MenusActivity.class);
		if (appContext.getActivityListSize() > 0) {
			appContext.getActivityList()
					.get(appContext.getActivityListSize() - 1)
					.putExtra(getResources().getString(R.string.back), "true");
			startActivity(appContext.getActivityList().get(
					appContext.getActivityListSize() - 1));
			finish();
			appContext.popActivity();
		}
		this.finish();
	}

}
