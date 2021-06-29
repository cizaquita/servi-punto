/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "ServiRestaurante.db";
	private final static int DATABASE_VERSION = 7;

	private final String USER_DETAILS = "CREATE TABLE tblm_user (user_id TEXT,nit_shop_id TEXT,user_name TEXT  primary key,password TEXT,name TEXT,company_name TEXT,imei TEXT,registration_date TEXT,last_login TEXT,cedula_document TEXT,opening_balance TEXT,opening_date_time TEXT,is_closed TEXT,close_date_time TEXT,sync_status TEXT,actual_balance TEXT,terminal_Id TEXT,puntored_Id TEXT,comercio_Id TEXT,system_Id TEXT,is_admin TEXT,is_authorized TEXT,email TEXT ,authtoken TEXT)";
	private final String CLIENT = "CREATE TABLE tblm_client(client_id TEXT primary key,cedula TEXT,name TEXT,telephone TEXT,address TEXT,email TEXT,gender TEXT,active_status TEXT,initial_debt TEXT,balance_amount TEXT,last_payment_date TEXT,created_date TEXT,modified_date TEXT,sync_status TEXT)";
	private final String CLIENT_PAYMENTS = "CREATE TABLE tbl_client_payments(payment_id TEXT primary key,client_id TEXT,payment_type TEXT,amount_paid TEXT,date_time TEXT,sale_id TEXT,income_type TEXT,sync_status TEXT)";
	private final String LEND_MONEY = "CREATE TABLE tbl_lend_money(lend_id TEXT primary key,client_id TEXT,amount TEXT,date_time TEXT ,sync_status TEXT)";

	private final String DELIVERY = "CREATE TABLE tbl_delivery(delivery_id TEXT primary key,cedula TEXT,name TEXT,telephone TEXT,address TEXT,email TEXT,gender TEXT,active_status TEXT,initial_debt TEXT,balance_amount TEXT,last_payment_date TEXT,created_date TEXT,modified_date TEXT,sync_status TEXT)";
	private final String DELIVERY_PAYMENTS = "CREATE TABLE tbl_delivery_payments(payment_id TEXT primary key,delivery_id TEXT,payment_type TEXT,amount_paid TEXT,date_time TEXT,sale_id TEXT,income_type TEXT,sync_status TEXT)";
	private final String LEND_DELIVERY = "CREATE TABLE tbl_lend_delivery(lend_id TEXT primary key,delivery_id TEXT,amount TEXT,date_time TEXT ,sync_status TEXT)";

	private final String DAMAGE_TYPES = "CREATE TABLE tblm_damage_type(damage_type_id integer primary key,name text)";

	private final String MASTER_PRODUCT = "CREATE TABLE tblm_master_product (product_id TEXT primary key,barcode TEXT,name TEXT,quantity TEXT,uom TEXT,purchase_price TEXT,selling_price TEXT,group_id TEXT,vat TEXT,supplier_id TEXT,line_id TEXT,active_status TEXT,create_date TEXT,modified_date TEXT,productFlag TEXT,sync_status TEXT)";

	private final String PRODUCT = "CREATE TABLE tblm_product (product_id TEXT PRIMARY KEY,barcode TEXT UNIQUE,name TEXT,quantity TEXT,uom TEXT,purchase_price TEXT,selling_price TEXT,group_id TEXT,vat TEXT,supplier_id TEXT,line_id TEXT,active_status TEXT,create_date TEXT,modified_date TEXT,productFlag TEXT,sync_status TEXT,sub_group TEXT,min_count_inventory TEXT,expiry_date TEXT,discount TEXT,fecha_inicial TEXT,fecha_final TEXT)";
	private final String PAYMENT = "CREATE TABLE PAYMENT (idPayment TEXT primary key,idCustomer TEXT,amountPaid TEXT,dateTime TEXT,paymentType TEXT,syncStatus TEXT)";
	private final String INVENTORY = "CREATE TABLE tbl_inventory(inventory_id TEXT PRIMARY KEY, product_id text UNIQUE, quantity text, uom text, sync_status integer, quantity_balance REAL)";
	private final String SALES = "CREATE TABLE tbl_sales(sale_id text primary key,invoice_number text,gross_amount text,net_amount text,discount text,client_id text, delivery_code TEXT, trans_delivery INTEGER, amount_paid text,payment_type text,date_time text,sync_status integer, fecha_inicial TEXT, fecha_final TEXT)";
	private final String SALES_DETAILS = "CREATE TABLE tbl_sales_details(sales_details_id  integer primary key,sale_id text,product_id text,count text,uom text,price text,dish_id text,sync_status integer)";
	private final String INVENTORY_ADJUSTMENT = "CREATE TABLE tbl_inventory_adjustment(adjustment_id TEXT primary key , product_id text, damage_type_id text, quantity text, uom text, sync_status integer)";
	private final String GROUP = "CREATE TABLE tblm_group(group_id TEXT  primary key , name TEXT)";
	private final String LINE = "CREATE TABLE tblm_line(line_id TEXT  primary key,name TEXT,group_id TEXT)";
	private final String SUPPLIER = "CREATE TABLE tblm_supplier (supplier_id TEXT PRIMARY KEY,cedula TEXT UNIQUE,name TEXT,logo TEXT,address TEXT,telephone TEXT,contact_name TEXT,contact_telephone TEXT,visit_days TEXT,visit_frequency TEXT,active_status TEXT,balance_amount TEXT,last_payment_date TEXT,created_date TEXT,modified_date TEXT,sync_status TEXT)";
	private final String SUPPLIER_PAYMENTS = "CREATE TABLE tbl_supplier_payments (supplier_payment_id TEXT primary key,supplier_id TEXT,amount_paid TEXT,payment_type TEXT,date_time TEXT,purchase_type TEXT,sync_status TEXT)";

	private final String CASH_FLOW = "CREATE TABLE tbl_cash_flow (cash_flow_id TEXT primary key,amount TEXT,amount_type TEXT,reason TEXT,date_time TEXT,sync_status TEXT)";
	private final String INVENTORY_HISTORY = "CREATE TABLE tbl_inventory_history (inventory_history_id TEXT primary key,product_id TEXT,quantity TEXT,uom TEXT,price integer,date_time TEXT,sync_status TEXT,invoice_no TEXT)";

	private final String ORDERS = "CREATE TABLE tbl_orders(order_id text primary key, supplier_id text, invoice_no text, gross_amount text, net_amount text, discount text, payment_type text, is_order_confirmed text, date_time text, sync_status integer, fecha_inicial TEXT, fecha_final TEXT)";
	private final String ORDERS_DETAILS = "CREATE TABLE tbl_order_details(order_details_id integer primary key autoincrement, order_id text, product_id text, count text, uom text, price text, sync_status integer)";

	private final String DISHES = "CREATE TABLE tbl_dishes(dish_id TEXT primary key, dish_name text, dish_price text, no_of_items text, expiry_days text, vat text, selling_cost_per_item text, sync_status integer)";
	private final String DISH_PRODUCTS = "CREATE TABLE tbl_dish_products( dish_product_id TEXT primary key, dish_id text, product_id text, uom text, quantity text, sync_status integer)";

	private final String MENU = "CREATE TABLE tbl_menu(menu_id TEXT primary key, name text, menu_type_id text, start_date text, end_date text, week_days text, sync_status integer)";
	private final String MENU_DISHES = "CREATE TABLE tbl_menu_dishes(menu_dishes_id TEXT primary key, menu_id text, dish_id text, sync_status integer)";

	private final String MENU_INVENTORY = "CREATE TABLE tbl_menu_inventory(menu_inventory_id TEXT primary key , dish_id text, count text, sync_status integer)";

	private final String MENU_INVENTORY_ADJ = "CREATE TABLE tbl_menu_inventory_adjustment(menu_adjustment_id TEXT primary key, dish_id text, count text, sync_status integer)";

	private final String MENU_TYPES = "CREATE TABLE tbl_menu_types(menu_type_id integer primary key autoincrement, name text,sync_status text)";

	private final String USER_MODULEID = "Create table UserModules(userModuleId integer primary key autoincrement, moduleName text, moduleCode text)";

	private final String INVOICE_DETAILS = "Create table tbl_invoice_details(invoice_details_id integer primary key autoincrement, sale_id text, barcode text,dish_id text,count text,uom text,price text,product_type text)";

	private final String CELLULAR_PROVIDER = "Create table tbl_cellular_provider(cellularId text , description text, min_amount text,max_amount text,multiplier text)";

	private final String PUNTORED_CREDINTIALS = "Create table tbl_puntored_credintials(terminalId text , puntoredId text, comercioId text,systemId text,userName text unique)";

	private final String SINCRONIZAR_TRANSACCIONES = "Create table tbl_sincronizar_transaccion(rowid text , module text, tipo_transaction text,authorization_number text,id_pdb_servitienda text,transaction_datetime text,transaction_value text,status text,creation_date text,created_by text,modified_date text,modified_by text,servitienda_synck_status text, punthored_synck_status text, module_tipo_id text)";

	private final String TRANSACCION_BOX = "Create table tbl_transaccion_box(transaccion_box_id text, store_code text, user_name text,tipo_trans text,module_name text,transaccion_type text,amount text,date_time text,sync_status integer)";
	private final String PROMOTION = "Create table tbl_promotion(image_url TEXT,name TEXT,video_url TEXT, store_code TEXT, supplier_code TEXT, supplier_name TEXT, value TEXT, where_to_show TEXT,  end_date TEXT, start_date TEXT, promotion_des TEXT, promoid NUMERIC,orderval NUMERIC)";
	private final String DBYNC = "CREATE TABLE tblm_Dbyc(group_id TEXT  unique , name TEXT)";
	public Database(Context context) {
		// String s = context.getExternalFilesDir(null).getAbsolutePath()+"/"+DATABASE_NAME;
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		//super(context, context.getExternalFilesDir(null).getAbsolutePath()+"/"+DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase dbObj) {
		dbObj.execSQL(USER_DETAILS);
		dbObj.execSQL(CLIENT);
		dbObj.execSQL(CLIENT_PAYMENTS);
		dbObj.execSQL(CASH_FLOW);
		dbObj.execSQL(LEND_MONEY);
		dbObj.execSQL(MASTER_PRODUCT);
		dbObj.execSQL(PRODUCT);
		dbObj.execSQL(DAMAGE_TYPES);
		dbObj.execSQL(PAYMENT);
		dbObj.execSQL(INVENTORY);
		dbObj.execSQL(INVENTORY_HISTORY);

		dbObj.execSQL(SALES);
		dbObj.execSQL(SALES_DETAILS);
		dbObj.execSQL(INVENTORY_ADJUSTMENT);
		dbObj.execSQL(GROUP);
		dbObj.execSQL(LINE);
		dbObj.execSQL(SUPPLIER);
		dbObj.execSQL(SUPPLIER_PAYMENTS);

		dbObj.execSQL(ORDERS);
		dbObj.execSQL(ORDERS_DETAILS);

		dbObj.execSQL(DISHES);
		dbObj.execSQL(DISH_PRODUCTS);
		dbObj.execSQL(MENU);
		dbObj.execSQL(MENU_DISHES);
		dbObj.execSQL(MENU_INVENTORY);
		dbObj.execSQL(MENU_INVENTORY_ADJ);
		dbObj.execSQL(MENU_TYPES);

		dbObj.execSQL(USER_MODULEID);
		dbObj.execSQL(INVOICE_DETAILS);

		dbObj.execSQL(CELLULAR_PROVIDER);
		dbObj.execSQL(PUNTORED_CREDINTIALS);
		dbObj.execSQL(SINCRONIZAR_TRANSACCIONES);
		dbObj.execSQL(TRANSACCION_BOX);
//		dbObj.execSQL(PROMOTION);
		dbObj.execSQL(DELIVERY);
		dbObj.execSQL(DELIVERY_PAYMENTS);
		dbObj.execSQL(LEND_DELIVERY);
		dbObj.execSQL(DBYNC);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("oldVersion :" + oldVersion);
		System.out.println("newVersion :" + newVersion);
		if (oldVersion < newVersion) {
			switch (newVersion) {
			case 2:
				// For New column in existing tables
				db.execSQL("ALTER TABLE tblm_user ADD COLUMN actual_balance TEXT");
				db.execSQL("ALTER TABLE tbl_inventory_history ADD COLUMN invoice_no TEXT");

				// For New Table
				db.execSQL("Create table IF NOT EXISTS tbl_invoice_details(invoice_details_id integer primary key autoincrement, sale_id text, barcode text,dish_id text,count text,uom text,price text,product_type text)");

				// Change Data type for column
				alterInventory(db);
				alterInventoryAdj(db);
				alterOrders(db);
				alterDishes(db);
				alterDishProducts(db);
				alterMenus(db);
				alterMenusDishes(db);
				alterMenusInventory(db);
				alterMenusInventoryAdj(db);
				break;
			case 6:
				// For New column in existing tables
//				db.execSQL("ALTER TABLE tblm_user ADD COLUMN actual_balance TEXT");
//				db.execSQL("ALTER TABLE tbl_inventory_history ADD COLUMN invoice_no TEXT");
//
//				// For New Table
//				db.execSQL("Create table IF NOT EXISTS tbl_invoice_details(invoice_details_id integer primary key autoincrement, sale_id text, barcode text,dish_id text,count text,uom text,price text,product_type text)");
//				// Change Data type for column
				alterUser(db);
				alterProduct(db);
				alterSales(db);
				alterOrder(db);
//				db.execSQL("Create table tbl_promotion(image_url TEXT,name TEXT,video_url TEXT, store_code TEXT, supplier_code TEXT, supplier_name TEXT, value TEXT, where_to_show TEXT,  end_date TEXT, start_date TEXT, promotion_des TEXT, promoid NUMERIC,orderval NUMERIC)");
//				db.execSQL("ALTER TABLE tbl_inventory_history ADD COLUMN invoice_no TEXT");
				// For New Table
				db.execSQL("Create table IF NOT EXISTS tbl_invoice_details(invoice_details_id integer primary key autoincrement, sale_id text, barcode text,dish_id text,count text,uom text,price text,product_type text)");

				// Change Data type for column
				alterInventory(db);
				alterInventoryAdj(db);
				alterOrders(db);
				alterDishes(db);
				alterDishProducts(db);
				alterMenus(db);
				alterMenusDishes(db);
				alterMenusInventory(db);
				alterMenusInventoryAdj(db);
//				db.execSQL("Create table tblm_Dbyc(group_id TEXT  unique , name TEXT)");
				break;
			
			default:
				break;
			}
		}
	}

	private void alterUser(SQLiteDatabase db) {
		db.execSQL("ALTER TABLE tblm_user RENAME TO tmp");
		db.execSQL("CREATE TABLE tblm_user(user_id TEXT,nit_shop_id TEXT,user_name TEXT  primary key,password TEXT,name TEXT,company_name TEXT,imei TEXT,registration_date TEXT,last_login TEXT,cedula_document TEXT,opening_balance TEXT,opening_date_time TEXT,is_closed TEXT,close_date_time TEXT,sync_status TEXT,actual_balance TEXT,terminal_Id TEXT,puntored_Id TEXT,comercio_Id TEXT,system_Id TEXT,is_admin TEXT,is_authorized TEXT,email TEXT ,authtoken TEXT)");
		db.execSQL("INSERT INTO tblm_user(user_id,nit_shop_id,user_name,password,name,company_name,imei,registration_date,last_login,cedula_document,opening_balance,opening_date_time,is_closed,close_date_time,sync_status,actual_balance,is_admin,is_authorized,authtoken)  SELECT user_id,nit_shop_id,user_name,password,name,company_name,imei,registration_date,last_login,cedula_document,opening_balance,opening_date_time,is_closed,close_date_time,sync_status,actual_balance,is_admin,is_authorized,authtoken FROM tmp");
		db.execSQL("DROP TABLE tmp");
	}

	private void alterProduct(SQLiteDatabase db) {

		db.execSQL("ALTER TABLE tblm_product RENAME TO tmp");
		db.execSQL("CREATE TABLE tblm_product(product_id TEXT primary key,barcode TEXT,name TEXT,quantity TEXT,uom TEXT,purchase_price TEXT,selling_price TEXT,group_id TEXT,vat TEXT,supplier_id TEXT,line_id TEXT,active_status TEXT,create_date TEXT,modified_date TEXT,productFlag TEXT,sync_status TEXT,sub_group TEXT,min_count_inventory TEXT,expiry_date TEXT,discount TEXT,fecha_inicial TEXT,fecha_final TEXT)");
		db.execSQL("INSERT INTO tblm_product(product_id,barcode,name,quantity,uom,purchase_price,selling_price,group_id,vat,supplier_id,line_id,active_status,create_date,modified_date,productFlag,sync_status)  SELECT product_id,barcode,name,quantity,uom,purchase_price,selling_price,group_id,vat,supplier_id,line_id,active_status,create_date,modified_date,productFlag,sync_status FROM tmp");
		db.execSQL("DROP TABLE tmp");
	}

	private void alterSales(SQLiteDatabase db) {

		db.execSQL("ALTER TABLE tbl_sales RENAME TO tmp");
		db.execSQL("CREATE TABLE tbl_sales(sale_id text primary key,invoice_number text,gross_amount text,net_amount text,discount text,client_id text,amount_paid text,payment_type text,date_time text,sync_status integer, fecha_inicial text, fecha_final text)");
		db.execSQL("INSERT INTO tbl_sales(sale_id,invoice_number,gross_amount,net_amount,discount,client_id,amount_paid,payment_type,date_time,sync_status)  SELECT sale_id,invoice_number,gross_amount,net_amount,discount,client_id,amount_paid,payment_type,date_time,sync_status FROM tmp");
		db.execSQL("DROP TABLE tmp");
	}

	private void alterOrder(SQLiteDatabase db) {
		db.execSQL("ALTER TABLE tbl_orders RENAME TO tmp");
		db.execSQL("CREATE TABLE tbl_orders(order_id text primary key, supplier_id text, invoice_no text, gross_amount text, net_amount text, discount text, payment_type text, is_order_confirmed text, date_time text, sync_status integer, fecha_inicial TEXT, fecha_final TEXT)");
		db.execSQL("INSERT INTO tbl_orders(order_id,supplier_id,invoice_no,gross_amount,net_amount,discount,payment_type,is_order_confirmed,date_time,sync_status)  SELECT order_id,supplier_id,invoice_no,gross_amount,net_amount,discount,payment_type,is_order_confirmed,date_time,sync_status FROM tmp");
		db.execSQL("DROP TABLE tmp");

	}

	private void alterMenusInventoryAdj(SQLiteDatabase db) {
		System.out.println("Inside alterMenusInventoryAdj table");
		db.execSQL("ALTER TABLE tbl_menu_inventory_adjustment RENAME TO tmp");
		db.execSQL("CREATE TABLE tbl_menu_inventory_adjustment(menu_adjustment_id TEXT primary key, dish_id text, count text, sync_status integer)");

		db.execSQL("INSERT INTO tbl_menu_inventory_adjustment(menu_adjustment_id,dish_id,count,sync_status)  SELECT menu_adjustment_id,dish_id,count,sync_status FROM tmp");
		db.execSQL("DROP TABLE tmp");
	}

	private void alterMenusInventory(SQLiteDatabase db) {
		System.out.println("Inside alterMenusInventory table");
		db.execSQL("ALTER TABLE tbl_menu_inventory RENAME TO tmp");
		db.execSQL("CREATE TABLE tbl_menu_inventory(menu_inventory_id TEXT primary key, dish_id text, count text, sync_status integer)");

		db.execSQL("INSERT INTO tbl_menu_inventory(menu_inventory_id,dish_id,count,sync_status)  SELECT menu_inventory_id,dish_id,count,sync_status FROM tmp");

		db.execSQL("DROP TABLE tmp");

	}

	private void alterMenusDishes(SQLiteDatabase db) {
		System.out.println("Inside alterMenusDishes table");
		db.execSQL("ALTER TABLE tbl_menu_dishes RENAME TO tmp");
		db.execSQL("CREATE TABLE tbl_menu_dishes(menu_dishes_id TEXT primary key, menu_id text, dish_id text, sync_status integer)");

		db.execSQL("INSERT INTO tbl_menu_dishes(menu_dishes_id,menu_id,dish_id,sync_status)  SELECT menu_dishes_id,menu_id,dish_id,sync_status FROM tmp");

		db.execSQL("DROP TABLE tmp");
	}

	private void alterMenus(SQLiteDatabase db) {
		System.out.println("Inside alterMenus table");
		db.execSQL("ALTER TABLE tbl_menu RENAME TO tmp");
		db.execSQL("CREATE TABLE tbl_menu(menu_id TEXT primary key, name text, menu_type_id text, start_date text, end_date text, week_days text, sync_status integer)");

		db.execSQL("INSERT INTO tbl_menu(menu_id,name,menu_type_id,start_date,end_date,week_days,sync_status)  SELECT menu_id,name,menu_type_id,start_date,end_date,week_days,sync_status FROM tmp");

		db.execSQL("DROP TABLE tmp");
	}

	private void alterDishProducts(SQLiteDatabase db) {
		System.out.println("Inside alterDishProducts table");
		db.execSQL("ALTER TABLE tbl_dish_products RENAME TO tmp");
		db.execSQL("CREATE TABLE tbl_dish_products(dish_product_id TEXT primary key, dish_id text, product_id text, uom text, quantity text, sync_status integer)");

		db.execSQL("INSERT INTO tbl_dish_products(dish_product_id,dish_id,product_id,uom,quantity,sync_status)  SELECT dish_product_id,dish_id,product_id,uom,quantity,sync_status  FROM tmp");

		db.execSQL("DROP TABLE tmp");
	}

	private void alterDishes(SQLiteDatabase db) {
		System.out.println("Inside alterDishes table");
		db.execSQL("ALTER TABLE tbl_dishes RENAME TO tmp");
		db.execSQL("CREATE TABLE tbl_dishes(dish_id TEXT primary key, dish_name text, dish_price text, no_of_items text, expiry_days text, vat text, selling_cost_per_item text, sync_status integer)");

		db.execSQL("INSERT INTO tbl_dishes(dish_id,dish_name,dish_price,no_of_items,expiry_days,vat,selling_cost_per_item,sync_status)  SELECT dish_id,dish_name,dish_price,no_of_items,expiry_days,vat,selling_cost_per_item,sync_status  FROM tmp");
		db.execSQL("DROP TABLE tmp");
	}

	private void alterOrders(SQLiteDatabase db) {
		System.out.println("Inside alterOrders table");
		db.execSQL("ALTER TABLE tbl_orders RENAME TO tmp");
		db.execSQL("CREATE TABLE tbl_orders(order_id TEXT primary key, supplier_id text, invoice_no text, gross_amount text, net_amount text, discount text, payment_type text, is_order_confirmed text, date_time text, sync_status integer)");

		db.execSQL("INSERT INTO tbl_orders(order_id,supplier_id,invoice_no,gross_amount,net_amount,discount,payment_type,is_order_confirmed,date_time,sync_status)  SELECT order_id,supplier_id,invoice_no,gross_amount,net_amount,discount,payment_type,is_order_confirmed,date_time,sync_status  FROM tmp");
		db.execSQL("DROP TABLE tmp");
	}

	private void alterInventoryAdj(SQLiteDatabase db) {
		System.out.println("Inside alterInventoryAdj table");
		db.execSQL("ALTER TABLE tbl_inventory_adjustment RENAME TO tmp");
		db.execSQL("CREATE TABLE tbl_inventory_adjustment(adjustment_id TEXT primary key, product_id text, damage_type_id text, quantity text, uom text, sync_status integer)");

		db.execSQL("INSERT INTO tbl_inventory_adjustment(adjustment_id,product_id,damage_type_id,quantity,uom,sync_status)  SELECT adjustment_id,product_id,damage_type_id,quantity,uom,sync_status  FROM tmp");
		db.execSQL("DROP TABLE tmp");
	}

	private void alterInventory(SQLiteDatabase db) {
		System.out.println("Inside alter inventory table");
		db.execSQL("ALTER TABLE tbl_inventory RENAME TO tmp");
		db.execSQL("CREATE TABLE tbl_inventory(inventory_id TEXT primary key, product_id text, quantity text, uom text, sync_status integer)");

		db.execSQL("INSERT INTO tbl_inventory(inventory_id,product_id,quantity,uom,sync_status)  SELECT inventory_id,product_id,quantity,uom,sync_status  FROM tmp");
		db.execSQL("DROP TABLE tmp");
	}
}
