package com.micaja.servipunto.activities;
//package com.micaja.servipunto.activities;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.micaja.servipunto.R;
//import com.micaja.servipunto.ServiApplication;
//import com.micaja.servipunto.adapters.MenusInvenAddAdapter;
//import com.micaja.servipunto.database.DBHandler;
//import com.micaja.servipunto.database.dao.DishProductsDAO;
//import com.micaja.servipunto.database.dao.DishesDAO;
//import com.micaja.servipunto.database.dto.DTO;
//import com.micaja.servipunto.database.dto.DishProductsDTO;
//import com.micaja.servipunto.database.dto.DishesDTO;
//import com.micaja.servipunto.database.dto.DishesEditDTO;
//import com.micaja.servipunto.database.dto.SelectedProddutsDTO;
//import com.micaja.servipunto.dialog.CreateDisDialog;
//import com.micaja.servipunto.dialog.InventoryDialog;
//import com.micaja.servipunto.utils.CommonMethods;
//import com.micaja.servipunto.utils.Constants;
//import com.micaja.servipunto.utils.SalesCodes;
//
//public class MenuInventoryAdd extends BaseActivity implements OnClickListener,android.content.DialogInterface.OnClickListener
//{
//	private Button btnZero, btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix,
//	btnSeven, btnEight, btnNine, btnBackClear, btnAllClear, btnSearch,
//	btnEnter, btnSaveDish, btnCancelDish, btnBarcode;
//
//	private EditText etxtBarcode;
//	
//	private TextView txtProductTit,txtUnitsTit;
//	
//	private ListView lvSelectedProdu;
//	private LinearLayout llCostDetails;
//	private MenusInvenAddAdapter inventoryAdapter;
//	private List<DTO> selectedList,dishesList,productsList;
//	private AlertDialog cancelDialog;
//	private boolean isProductAdded;
//	private String editDishId;
//	ServiApplication appContext;
//	
//	Intent intent;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) 
//	{
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		
//		intent 		= new Intent(this, OrdersActivity.class);
//		
//		appContext = (ServiApplication) getApplicationContext();
//		
//		inItUI();
//		
//	}
//
//	private void inItUI() 
//	{
//		setContentView(R.layout.create_dish);
//
//		btnZero 		= (Button) findViewById(R.id.btn_SalesCalZero);
//		btnOne 			= (Button) findViewById(R.id.btn_SalesCalOne);
//		btnTwo 			= (Button) findViewById(R.id.btn_SalesCalTwo);
//		btnThree 		= (Button) findViewById(R.id.btn_SalesCalThree);
//		btnFour 		= (Button) findViewById(R.id.btn_SalesCalFour);
//		btnFive 		= (Button) findViewById(R.id.btn_SalesCalFive);
//		btnSix 			= (Button) findViewById(R.id.btn_SalesCalSix);
//		btnSeven 		= (Button) findViewById(R.id.btn_SalesCalSeven);
//		btnEight 		= (Button) findViewById(R.id.btn_SalesCalEight);
//		btnNine 		= (Button) findViewById(R.id.btn_SalesCalNine);
//		btnBackClear 	= (Button) findViewById(R.id.btn_SalesCalClearOne);
//		btnAllClear 	= (Button) findViewById(R.id.btn_SalesCalClear);
//		btnSearch 		= (Button) findViewById(R.id.btn_SalesCalSearch);
//		btnEnter 		= (Button) findViewById(R.id.btn_SalesCalEnter);
//		btnSaveDish 	= (Button) findViewById(R.id.btn_SaleEnd);
//		btnCancelDish 	= (Button) findViewById(R.id.btn_SaleCancel);
//		btnBarcode 		= (Button) findViewById(R.id.btn_SalesBarCode);
//
//		etxtBarcode 	= (EditText) findViewById(R.id.etxt_SalesBarcode);
//
//		txtProductTit	= (TextView) findViewById(R.id.txt_ProductTitle);
//		txtUnitsTit		= (TextView) findViewById(R.id.txt_UnitsTitle);
//		llCostDetails	= (LinearLayout) findViewById(R.id.ll_CostDetails);
//		
//		txtProductTit.setText(getResources().getString(R.string.dish_name));
//		txtUnitsTit.setVisibility(View.GONE);
//		llCostDetails.setVisibility(View.INVISIBLE);
//
//		lvSelectedProdu = (ListView) findViewById(R.id.lv_SalesProduct);
//		
//		btnSaveDish.setText(getResources().getString(R.string.save));
//		btnCancelDish.setText(getResources().getString(R.string.cancel));
//
//		btnZero.setOnClickListener(this);
//		btnOne.setOnClickListener(this);
//		btnTwo.setOnClickListener(this);
//		btnThree.setOnClickListener(this);
//		btnFour.setOnClickListener(this);
//		btnFive.setOnClickListener(this);
//		btnSix.setOnClickListener(this);
//		btnSeven.setOnClickListener(this);
//		btnEight.setOnClickListener(this);
//		btnNine.setOnClickListener(this);
//		btnBackClear.setOnClickListener(this);
//		btnAllClear.setOnClickListener(this);
//		btnSearch.setOnClickListener(this);
//		btnEnter.setOnClickListener(this);
//		btnSaveDish.setOnClickListener(this);
//		btnCancelDish.setOnClickListener(this);
//		btnBarcode.setOnClickListener(this);
//
//		loadUI();
//		
//	}
//	private void loadUI() 
//	{
//		selectedList = appContext.getSelectedProducts();
//		inventoryAdapter = new MenusInvenAddAdapter(MenuInventoryAdd.this, selectedList, uiHandler);
//		lvSelectedProdu.setAdapter(inventoryAdapter);
//	}
//
//	private final Handler uiHandler = new Handler() 
//	{
//		@Override
//		public void handleMessage(Message msg) 
//		{
//			
//			if (msg.arg1 == SalesCodes.EDIT.code())
//				updateList();
//			else if (msg.arg1 == SalesCodes.DELETE.code())
//				updateList();
//			else if (msg.arg1 == SalesCodes.DISH_CREATE.code())
//				validateAndInsert(msg.getData());
//		}
//	};
//	
//	private void updateList()
//	{
//		selectedList	= appContext.getMenusDishesList();
//		inventoryAdapter.setListData(selectedList);
//		inventoryAdapter.notifyDataSetChanged();
//	}
//
//	@Override
//	public void onClick(View view) 
//	{
//		switch (view.getId()) 
//		{
//		case R.id.btn_SalesCalZero:
//			setVauleToBarcode("0");
//			break;
//
//		case R.id.btn_SalesCalOne:
//			setVauleToBarcode("1");
//			break;
//
//		case R.id.btn_SalesCalTwo:
//			setVauleToBarcode("2");
//			break;
//
//		case R.id.btn_SalesCalThree:
//			setVauleToBarcode("3");
//			break;
//
//		case R.id.btn_SalesCalFour:
//			setVauleToBarcode("4");
//			break;
//
//		case R.id.btn_SalesCalFive:
//			setVauleToBarcode("5");
//			break;
//
//		case R.id.btn_SalesCalSix:
//			setVauleToBarcode("6");
//			break;
//
//		case R.id.btn_SalesCalSeven:
//			setVauleToBarcode("7");
//			break;
//
//		case R.id.btn_SalesCalEight:
//			setVauleToBarcode("8");
//			break;
//
//		case R.id.btn_SalesCalNine:
//			setVauleToBarcode("9");
//			break;
//
//		case R.id.btn_SalesCalClearOne:
//			if (etxtBarcode.getText().toString().trim().length() != 0)
//				removeLastDigit();
//			break;
//
//		case R.id.btn_SalesCalClear:
//			etxtBarcode.setText("");
//			break;
//
//		case R.id.btn_SalesCalSearch:
//			Intent intent	= new Intent(MenuInventoryAdd.this, DishesSeleActivity.class);
//			startActivityForResult(intent, SalesCodes.SELECT_PRODUCT.code());
//			break;
//
//		case R.id.btn_SalesCalEnter:
//
//			break;
//
//		case R.id.btn_SaleEnd:
//			if (appContext.getTotalAmount() > 0) 
//			{
//				DishesDTO dishesDTO;
//				if(getIntent().getExtras() != null)
//				{
//					dishesDTO 	= (DishesDTO) dishesList.get(0);
//					editDishId	= dishesDTO.getDishId();
//				}
//				else
//					dishesDTO	= new DishesDTO();
//				
//				CreateDisDialog finishDialog = new CreateDisDialog(MenuInventoryAdd.this,String.valueOf(appContext.getTotalAmount()),dishesDTO, uiHandler,false);
//				finishDialog.show();
//			}
//			
//			break;
//		case R.id.btn_SaleCancel:
//			cancelDialog = CommonMethods.displayAlert(false, getResources().getString(R.string.cancel_dish), getResources().getString(R.string.cancel_dish_msg), getResources().getString(R.string.yes), getResources().getString(R.string.no), MenuInventoryAdd.this, MenuInventoryAdd.this, true);
//			break;
//
//		case R.id.btn_SalesBarCode:
//
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	private DishesDTO getDishDetails() 
//	{
//		DishesDTO 	dishesDTO = new DishesDTO();
//		if(getIntent().getExtras() != null)
//		{
//			DishesDTO dto = (DishesDTO) dishesList.get(0);
//			dishesDTO.setDishId(dto.getDishId());
//			dishesDTO.setDishName(dto.getDishName());
//			dishesDTO.setDishPrice(dto.getDishPrice());
//			dishesDTO.setExpiryDays(dto.getExpiryDays());
//			dishesDTO.setNoOfItems(dto.getNoOfItems());
//			dishesDTO.setSellingCostperItem(dto.getSellingCostperItem());
//			dishesDTO.setVat(dto.getVat());
//		}
//		
//		return dishesDTO;
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
//	{
//		super.onActivityResult(requestCode, resultCode, data);
//
//		if(requestCode == SalesCodes.SELECT_PRODUCT.code())
//		{
//		  isProductAdded = true;
//		  InventoryDialog dialog = new InventoryDialog(MenuInventoryAdd.this);
//		  dialog.show();
////		  updateList();
//		}
//	}
//
//	private void removeLastDigit() 
//	{
//		if (etxtBarcode.getText().toString().trim().length() == 1)
//			etxtBarcode.setText("");
//		else 
//		{
//			String barcode = etxtBarcode.getText().toString().trim();
//			barcode = barcode.substring(0, barcode.length() - 1);
//			etxtBarcode.setText(barcode);
//		}
//		etxtBarcode.setSelection(etxtBarcode.getText().length());
//	}
//
//	private void setVauleToBarcode(String enterValue) 
//	{
//		String barcode = etxtBarcode.getText().toString().trim();
//
//		if (barcode.length() == 0)
//			etxtBarcode.setText(enterValue);
//		else 
//		{
//			barcode = barcode.concat(enterValue);
//			etxtBarcode.setText(barcode);
//		}
//		etxtBarcode.setSelection(etxtBarcode.getText().length());
//	}
//	
//	private void validateAndInsert(Bundle bundle) 
//	{
//		if(getIntent().getExtras() != null)
//			updateDishRecords(bundle);
//		else
//			insertRecords(bundle);
//	}
//
//	private void insertRecords(Bundle bundle) 
//	{
//		if(DishesDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), getDishList(bundle)))
//			insertDishProducts();
//	}
//
//	private void updateDishRecords(Bundle bundle) 
//	{
//		if(DishesDAO.getInstance().update(DBHandler.getDBObj(Constants.WRITABLE), getDishList(bundle).get(0)))
//		{
//			if(isProductAdded)
//				insertDishProducts();
//			else
//				navigateBackScreen("update");
//		}
//	}
//	private void insertDishProducts() 
//	{
//		if(DishProductsDAO.getInstance().insert(DBHandler.getDBObj(Constants.WRITABLE), getDishProductsList()))
//			navigateBackScreen("insert");
//	}
//
//	private List<DTO> getDishProductsList() 
//	{
//		List<DTO> list	= new ArrayList<DTO>();
//		
//		List<DTO> seletedList	= appContext.getMenusDishesList();
//		
//		String dishId;
//		if(isProductAdded && getIntent().getExtras() != null)
//			dishId	= editDishId;
//		else
//			dishId	= DishesDAO.getInstance().getDishID(DBHandler.getDBObj(Constants.READABLE));
//		
//		List<String> productsList	= DishProductsDAO.getInstance().getDishProductRecords(DBHandler.getDBObj(Constants.READABLE), dishId);
//		
//		for(int i =0;i<seletedList.size();i++)
//		{
//			SelectedProddutsDTO selectedProddutsDTO	= (SelectedProddutsDTO) seletedList.get(i);
//			if(!productsList.contains(selectedProddutsDTO.getIdDishProduct()))
//			{
//				DishProductsDTO dishesDTO	= new DishProductsDTO();
//				dishesDTO.setDishId(dishId);
//				dishesDTO.setProductId(selectedProddutsDTO.getIdProduct());
//				dishesDTO.setQuantity(selectedProddutsDTO.getQuantity());
//				dishesDTO.setSyncStatus(0);
//				dishesDTO.setUom(selectedProddutsDTO.getUnits());
//				
//				list.add(dishesDTO);
//			}
//		}
//		return list;
//	}
//	
//	private List<DTO> getDishProUpdateList() 
//	{
//		List<DTO> list	= new ArrayList<DTO>();
//		
//		List<DTO> seletedList	= appContext.getSelectedProducts();
//		
//		for(int i =0;i<seletedList.size();i++)
//		{
//			SelectedProddutsDTO selectedProddutsDTO	= (SelectedProddutsDTO) seletedList.get(i);
//			
//			DishesEditDTO dishEditDTO = (DishesEditDTO) productsList.get(i);
//			DishProductsDTO dishesDTO	= new DishProductsDTO();
//				
//			dishesDTO.setDishProductId(dishEditDTO.getDishProductId());
//			dishesDTO.setDishId(dishEditDTO.getDishId());
//			dishesDTO.setProductId(selectedProddutsDTO.getIdProduct());
//			dishesDTO.setQuantity(selectedProddutsDTO.getQuantity());
//			dishesDTO.setSyncStatus(0);
//			dishesDTO.setUom(selectedProddutsDTO.getUnits());
//			
//			list.add(dishesDTO);
//		}
//		return list;
//	}
//
//	private List<DTO> getDishList(Bundle bundle) 
//	{
//		List<DTO> list	= new ArrayList<DTO>();
//		
//		DishesDTO dishesDTO	= new DishesDTO();
//		
//		dishesDTO.setDishId(bundle.getString("dishID"));
//		dishesDTO.setDishName(bundle.getString("dishName"));
//		dishesDTO.setDishPrice(bundle.getString("totalAmount"));
//		dishesDTO.setExpiryDays(bundle.getString("expiryDays"));
//		dishesDTO.setNoOfItems(bundle.getString("noOfItems"));
//		dishesDTO.setSellingCostperItem(bundle.getString("sellingPrice"));
//		dishesDTO.setSyncStatus(0);
//		dishesDTO.setVat(bundle.getString("vat"));
//		
//		list.add(dishesDTO);
//		
//		return list;
//	}
//
//	@Override
//	public void onClick(DialogInterface dialog, int which) 
//	{
//		dialog.dismiss();
//		if(dialog	== cancelDialog)
//		{
//			if(which	== DialogInterface.BUTTON_POSITIVE)
//			{
//				appContext.setSeletedProducts(new ArrayList<DTO>());
//				appContext.setSelectedProduct("");
//				appContext.setTotalAmount(0);
//				navigateBackScreen("cancel");
//			}
//		}
//	}
//	private void navigateBackScreen(String type) 
//	{
//		if("insert".equals(type)){
//			CommonMethods.showCustomToast(MenuInventoryAdd.this, getResources().getString(R.string.dish_insert_msg));
//		}else if("update".equals(type)){
//			CommonMethods.showCustomToast(MenuInventoryAdd.this, getResources().getString(R.string.dish_edit_msg));
//		}
//		appContext.setSeletedProducts(new ArrayList<DTO>());
//		appContext.setTotalAmount(0);
//		
//		CommonMethods.openNewActivity(MenuInventoryAdd.this, MenusActivity.class);
//		this.finish();
//	}
//}