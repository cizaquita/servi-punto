package com.micaja.servipunto.servicehandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

import com.micaja.servipunto.R;
import com.micaja.servipunto.ServiApplication;
import com.micaja.servipunto.database.dto.AcceptEfectivoDTO;
import com.micaja.servipunto.database.dto.CellularProviderDto;
import com.micaja.servipunto.database.dto.ClientDTO;
import com.micaja.servipunto.database.dto.ClientHistoryDTO;
import com.micaja.servipunto.database.dto.ConsultaConvenioDTO;
import com.micaja.servipunto.database.dto.ConsultaConveniosDTO;
import com.micaja.servipunto.database.dto.ConsultarCiudadesDTO;
import com.micaja.servipunto.database.dto.ConsultarLoteriasDTO;
import com.micaja.servipunto.database.dto.ConsultarSeriesLoteriasDTO;
import com.micaja.servipunto.database.dto.ControlDispersionDTO;
import com.micaja.servipunto.database.dto.DTO;
import com.micaja.servipunto.database.dto.DamageTypeDTO;
import com.micaja.servipunto.database.dto.DaviplataNumberDeDocumento;
import com.micaja.servipunto.database.dto.DeliveryDTO;
import com.micaja.servipunto.database.dto.DishProductsDTO;
import com.micaja.servipunto.database.dto.DishesDTO;
import com.micaja.servipunto.database.dto.GroupDTO;
import com.micaja.servipunto.database.dto.InventoryAmountDTO;
import com.micaja.servipunto.database.dto.InventoryDTO;
import com.micaja.servipunto.database.dto.InventoryHistoryDTO;
import com.micaja.servipunto.database.dto.InventoryInvoiceDTO;
import com.micaja.servipunto.database.dto.LineDTO;
import com.micaja.servipunto.database.dto.Master_ProductDTO;
import com.micaja.servipunto.database.dto.MenuDTO;
import com.micaja.servipunto.database.dto.MenuDishesDTO;
import com.micaja.servipunto.database.dto.MenuInventoryDTO;
import com.micaja.servipunto.database.dto.MenuTypesDTO;
import com.micaja.servipunto.database.dto.OrderReceiveDTO;
import com.micaja.servipunto.database.dto.PagoDocumentDTO;
import com.micaja.servipunto.database.dto.PagoProductsDTO;
import com.micaja.servipunto.database.dto.PagoProgramsDTO;
import com.micaja.servipunto.database.dto.PagoProviderDTO;
import com.micaja.servipunto.database.dto.ProductDTO;
import com.micaja.servipunto.database.dto.PromotionsDTO;
import com.micaja.servipunto.database.dto.SalesDetailDTO;
import com.micaja.servipunto.database.dto.SupplierDTO;
import com.micaja.servipunto.database.dto.UserDetailsDTO;
import com.micaja.servipunto.database.dto.UserModuleIdDTO;
import com.micaja.servipunto.utils.CommonMethods;
import com.micaja.servipunto.utils.Constants;
import com.micaja.servipunto.utils.Dates;
import com.micaja.servipunto.utils.Utils;

//@TargetApi(Build.VERSION_CODES.KITKAT)
public class ParsingHandler {
	public String jsonarrayname(String jsonStr) {
		String ARRAY_NAME = null;
		JSONObject issueObj;
		try {

			issueObj = new JSONObject(jsonStr);
			Iterator iterator = issueObj.keys();
			while (iterator.hasNext()) {
				ARRAY_NAME = (String) iterator.next();
			}
		} catch (JSONException e) {
			ARRAY_NAME = jsonStr;
		}
		return ARRAY_NAME;

	}

	public int getValue(String jsonStr) {
		int value = 0;
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			value = jsonObj.optInt("result");
			ServiApplication.connectionTimeOutState = true;
		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return value;
	}

	public List<DTO> getCustomersData(String respondsFeed) {
		List<DTO> clientList = new ArrayList<DTO>();
		try {
			JSONObject jsonobject = new JSONObject(respondsFeed);
			JSONArray arrayData = jsonobject.getJSONArray("data");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getClient = arrayData.getJSONObject(i);
				ClientDTO clientDTO = new ClientDTO();
				clientDTO.setActiveStatus(0);
				clientDTO.setAddress(getClient.getString("address"));
				clientDTO.setBalanceAmount("" + getClient.getDouble("balance_amount"));
				clientDTO.setCedula(getClient.getString("customer_code"));
				clientDTO.setClientID("");
				clientDTO.setCreatedDate(getClient.getString("created_date"));
				clientDTO.setEmail(getClient.getString("email"));
				clientDTO.setGender(getClient.getString("gender"));
				clientDTO.setInitialDebt("" + getClient.getDouble("initial_debts"));
				clientDTO.setLastPaymentDate(getClient.getString("last_payment_date"));
				clientDTO.setModifiedDate(getClient.getString("modified_date"));
				clientDTO.setName(getClient.getString("name"));
				clientDTO.setSyncStatus(1);
				clientDTO.setTelephone(getClient.getString("telephone"));
				clientList.add(clientDTO);
			}

		} catch (Exception e) {
		}
		return clientList;
	}

	public List<DTO> getDeliverysData(String respondsFeed) {
		List<DTO> clientList = new ArrayList<DTO>();
		try {
			JSONObject jsonobject = new JSONObject(respondsFeed);
			JSONArray arrayData = jsonobject.getJSONArray("data");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getDelivery = arrayData.getJSONObject(i);
				DeliveryDTO deliveryDTO = new DeliveryDTO();
				deliveryDTO.setActiveStatus(0);
				deliveryDTO.setAddress(getDelivery.getString("address"));
				deliveryDTO.setBalanceAmount("" + getDelivery.getDouble("balance_amount"));
				deliveryDTO.setCedula(getDelivery.getString("delivery_code"));
				deliveryDTO.setDeliveryID("");
				deliveryDTO.setCreatedDate(getDelivery.getString("created_date"));
				deliveryDTO.setEmail(getDelivery.getString("email"));
				deliveryDTO.setGender(getDelivery.getString("gender"));
				deliveryDTO.setInitialDebt("" + getDelivery.getDouble("initial_debts"));
				deliveryDTO.setLastPaymentDate(getDelivery.getString("last_payment_date"));
				deliveryDTO.setModifiedDate(getDelivery.getString("modified_date"));
				deliveryDTO.setName(getDelivery.getString("name"));
				deliveryDTO.setSyncStatus(1);
				deliveryDTO.setTelephone(getDelivery.getString("telephone"));
				clientList.add(deliveryDTO);
			}

		} catch (Exception e) {
		}
		return clientList;
	}

	/* get sup catlog */

	public List<DTO> getSuppliersData(String respondsFeed) {

		List<DTO> suppliersList = new ArrayList<DTO>();
		try {
			JSONObject jsonobject = new JSONObject(respondsFeed);
			JSONArray arrayData = jsonobject.getJSONArray("data");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getSupplier = arrayData.getJSONObject(i);
				SupplierDTO supplierDTO = new SupplierDTO();
				supplierDTO.setActiveStatus(1);
				supplierDTO.setSupplierId(getSupplier.getString("supplier_code"));
				supplierDTO.setName(getSupplier.getString("name"));
				supplierDTO.setAddress(getSupplier.getString("address"));
				if (getSupplier.getString("balance_amount").contains("null")) {
					supplierDTO.setBalanceAmount("0");
				} else {
					supplierDTO.setBalanceAmount("" + getSupplier.getLong("balance_amount"));
				}
				supplierDTO.setCedula(getSupplier.getString("supplier_code"));
				supplierDTO.setContactName(getSupplier.getString("contact_name"));
				supplierDTO.setContactTelephone(getSupplier.getString("contact_telephone"));
				supplierDTO.setCreatedDate(getSupplier.getString("created_date"));
				supplierDTO.setLastPaymentDate(getSupplier.getString("last_payment_date"));
				supplierDTO.setLogo(getSupplier.getString("logo"));
				supplierDTO.setModifiedDate(getSupplier.getString("modified_date"));
				supplierDTO.setSyncStatus(1);
				supplierDTO.setTelephone(getSupplier.getString("telephone"));
				supplierDTO.setVisitDays(getSupplier.getString("visit_days"));
				supplierDTO.setVisitFrequency(getSupplier.getString("visit_frequency"));
				try {
					Integer.parseInt(getSupplier.getString("visit_frequency"));
				} catch (Exception e) {
					try {
						supplierDTO.setVisitFrequency(getSupplier.getString("active_status"));
					} catch (Exception e2) {
						supplierDTO.setVisitFrequency("0");
					}
					supplierDTO.setVisitDays(getSupplier.getString("visit_frequency"));
					supplierDTO.setTelephone(getSupplier.getString("visit_days"));
					supplierDTO.setContactName(getSupplier.getString("contact_telephone"));
					supplierDTO.setContactTelephone(getSupplier.getString("contact_name"));
				}

				try {
					JSONArray productArray = getSupplier.getJSONArray("products");
					List<DTO> productList = new ArrayList<DTO>();
					for (int j = 0; j < productArray.length(); j++) {
						JSONObject productRow = productArray.getJSONObject(j);
						ProductDTO productDTO = new ProductDTO();
						try {
							productDTO.setProductId(productRow.getString("barcode"));
							productDTO.setModifiedDate(productRow.getString("modified_date"));
							productDTO.setCreateDate(productRow.getString("create_date"));
							productDTO.setName(productRow.getString("name"));
							productDTO.setSupplierId(productRow.getString("supplier_code"));
							productDTO.setBarcode(productRow.getString("barcode"));
							productDTO.setLineId(productRow.getString("line_id"));

							if (productRow.getString("uom").equals("0")) {
								productDTO.setUom("");
							} else {
								productDTO.setUom(productRow.getString("uom"));
							}
							productDTO.setSellingPrice("" + productRow.getDouble("selling_price"));
							productDTO.setGroupId(productRow.getString("group_id"));
							productDTO.setPurchasePrice("" + productRow.getDouble("purchase_price"));
							productDTO.setVat(productRow.getString("vat"));
							productDTO.setQuantity("" + productRow.getLong("quantity"));
							try {
								if ("" != productRow.getString("barcode")) {
									productList.add(productDTO);
								}
							} catch (Exception e) {
							}
						} catch (Exception e) {

						}
					}
					supplierDTO.setProductsList(productList);
				} catch (Exception e) {
				}

				if ("" != getSupplier.getString("supplier_code")) {
					suppliersList.add(supplierDTO);
				}

			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}

		return suppliersList;
	}

	/* get-by-promotion */

	public List<DTO> get_by_promotion(String respondsFeed, String Supplier_code) {

		List<DTO> suppliersList = new ArrayList<DTO>();
		try {
			SupplierDTO supplierDTO = new SupplierDTO();
			supplierDTO.setActiveStatus(0);
			supplierDTO.setSupplierId(Supplier_code);
			supplierDTO.setName("");
			supplierDTO.setAddress("");
			supplierDTO.setBalanceAmount("0");
			supplierDTO.setCedula(Supplier_code);
			supplierDTO.setContactName("");
			supplierDTO.setContactTelephone("");
			supplierDTO.setCreatedDate("");
			supplierDTO.setLastPaymentDate("");
			supplierDTO.setLogo("");
			supplierDTO.setModifiedDate("");
			supplierDTO.setSyncStatus(0);
			supplierDTO.setTelephone("");
			supplierDTO.setVisitDays("");
			supplierDTO.setVisitFrequency("");
			try {
				JSONObject jsonobject = new JSONObject(respondsFeed);
				JSONArray arrayData = jsonobject.getJSONArray("data");
				List<DTO> productList = new ArrayList<DTO>();
				for (int j = 0; j < arrayData.length(); j++) {
					JSONObject productRow = arrayData.getJSONObject(j);
					ProductDTO productDTO = new ProductDTO();
					try {
						productDTO.setProductId(productRow.getString("barcode"));
						productDTO.setModifiedDate(productRow.getString("modified_date"));
						productDTO.setCreateDate(productRow.getString("create_date"));
						productDTO.setName(productRow.getString("name"));
						productDTO.setSupplierId(productRow.getString("supplier_code"));
						productDTO.setBarcode(productRow.getString("barcode"));
						productDTO.setLineId(productRow.getString("line_id"));

						if (productRow.getString("uom").equals("0")) {
							productDTO.setUom("");
						} else {
							productDTO.setUom(productRow.getString("uom"));
						}
						productDTO.setSellingPrice("" + productRow.getDouble("selling_price"));
						productDTO.setGroupId(productRow.getString("group_id"));
						productDTO.setPurchasePrice("" + productRow.getDouble("purchase_price"));
						productDTO.setVat(productRow.getString("vat"));
						productDTO.setQuantity("" + productRow.getLong("quantity"));
						productList.add(productDTO);
					} catch (Exception e) {
					}
				}
				supplierDTO.setProductsList(productList);
				suppliersList.add(supplierDTO);
			} catch (Exception e) {
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}

		return suppliersList;
	}

	public List<DTO> getHomeFeedSuppliersData(String respondsFeed) {

		List<DTO> suppliersList = new ArrayList<DTO>();
		try {
			JSONObject jsonobject = new JSONObject(respondsFeed);
			JSONArray arrayData = jsonobject.getJSONArray("suppliers");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getSupplier = arrayData.getJSONObject(i);
				SupplierDTO supplierDTO = new SupplierDTO();
				// if (getSupplier.getBoolean("active_status")) {
				// supplierDTO.setActiveStatus(0);
				// } else {
				// supplierDTO.setActiveStatus(1);
				// }
				supplierDTO.setActiveStatus(0);
				supplierDTO.setAddress(getSupplier.getString("address"));
				if (getSupplier.getString("balance_amount").contains("null")) {
					supplierDTO.setBalanceAmount("0");
				} else {
					supplierDTO.setBalanceAmount("" + getSupplier.getLong("balance_amount"));
				}
				supplierDTO.setCedula(getSupplier.getString("supplier_code"));
				supplierDTO.setContactName(getSupplier.getString("contact_name"));
				supplierDTO.setContactTelephone(getSupplier.getString("contact_telephone"));
				supplierDTO.setCreatedDate(getSupplier.getString("created_date"));
				supplierDTO.setLastPaymentDate(getSupplier.getString("last_payment_date"));
				supplierDTO.setLogo(getSupplier.getString("logo"));
				supplierDTO.setModifiedDate(getSupplier.getString("modified_date"));
				supplierDTO.setName(getSupplier.getString("name"));
				supplierDTO.setSupplierId(getSupplier.getString("supplier_code"));
				supplierDTO.setSyncStatus(1);
				supplierDTO.setTelephone(getSupplier.getString("telephone"));
				supplierDTO.setVisitDays(getSupplier.getString("visit_days"));
				supplierDTO.setVisitFrequency(getSupplier.getString("visit_frequency"));
				try {
					JSONArray productArray = getSupplier.getJSONArray("products");
					List<DTO> productList = new ArrayList<DTO>();
					for (int j = 0; j < productArray.length(); j++) {
						JSONObject productRow = productArray.getJSONObject(j);
						ProductDTO productDTO = new ProductDTO();
						productDTO.setProductId(productRow.getString("barcode"));
						productDTO.setModifiedDate(productRow.getString("modified_date"));
						productDTO.setCreateDate(productRow.getString("create_date"));
						productDTO.setName(productRow.getString("name"));
						productDTO.setSupplierId(productRow.getString("supplier_code"));
						productDTO.setBarcode(productRow.getString("barcode"));
						// productDTO.setActiveStatus(productRow.getString("active_status"));
						productDTO.setLineId(productRow.getString("line_id"));
						productDTO.setUom(productRow.getString("uom"));
						productDTO.setSellingPrice("" + productRow.getDouble("selling_price"));
						productDTO.setGroupId(productRow.getString("group_id"));
						productDTO.setPurchasePrice("" + productRow.getDouble("purchase_price"));
						productDTO.setVat(productRow.getString("vat"));
						productDTO.setQuantity("" + productRow.getLong("quantity"));
						productList.add(productDTO);
					}

					supplierDTO.setProductsList(productList);
					suppliersList.add(supplierDTO);
				} catch (Exception e) {
				}

			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}

		return suppliersList;
	}

	public List<DTO> getPromotionsData(String respondsFeed) {
		List<DTO> promotions = new ArrayList<DTO>();

		try {
			JSONObject jsonobject = new JSONObject(respondsFeed);
			JSONArray arrayData = jsonobject.getJSONArray("data");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getpromotion = arrayData.getJSONObject(i);
				PromotionsDTO promotionItem = new PromotionsDTO();
				promotionItem.setImage_url(getpromotion.getString("image_url") + "0");
				promotionItem.setName(getpromotion.getString("name"));
				promotionItem.setStore_code("");
				promotionItem.setSupplier_code(getpromotion.getString("supplier_code"));

				try {
					promotionItem.setSupplier_name(getpromotion.getString("supplier_name"));
				} catch (Exception e) {
					promotionItem.setSupplier_name("");
				}

				promotionItem.setEnd_date(Dates.formateyyyyMMdd(getpromotion.getString("end_date")));
				promotionItem.setStart_date(Dates.formateyyyyMMdd(getpromotion.getString("start_date")));
				try {
					promotionItem.setPromoid(Long.parseLong(getpromotion.getString("display_order")));
				} catch (Exception e) {
					promotionItem.setPromoid(0);
				}
				if (getpromotion.getString("value").equals("null")) {
					promotionItem.setValue("0");
				} else {
					promotionItem.setValue(getpromotion.getString("value"));
				}
				try {
					promotionItem.setPromotion_des(getpromotion.getString("promotion_des"));
				} catch (Exception e) {
					promotionItem.setPromotion_des("");
				}
				promotionItem.setVideo_url(getpromotion.getString("video_url"));
				try {
					JSONArray json = getpromotion.getJSONArray("display_options");
					// Log.v("varahlababu", "varahlababu"+json);
					promotionItem.setWhere_to_show("" + json);
				} catch (Exception e) {
					promotionItem.setWhere_to_show("");
				}
				try {
					promotionItem.setPromoid(getpromotion.getLong("promoid"));
				} catch (Exception e) {
					promotionItem.setPromoid(0);
				}
				if (getpromotion.getString("active").equals("Y")) {
					try {
						Date date_two = Dates.serverdateformateDateObj(getpromotion.getString("end_date"));
						Date current_date = new Date();
						if (date_two.after(current_date) || (current_date.getDate() == date_two.getDate())) {
							promotions.add(promotionItem);
						}
					} catch (Exception e) {

					}
				}

			}
		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return promotions;
	}

	public List<DTO> getPromotionsDatawitharray(String respondsFeed) {
		List<DTO> promotions = new ArrayList<DTO>();

		try {
			JSONArray arrayData = new JSONArray(respondsFeed);
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getpromotion = arrayData.getJSONObject(i);
				PromotionsDTO promotionItem = new PromotionsDTO();
				promotionItem.setImage_url(getpromotion.getString("image_url"));
				promotionItem.setName(getpromotion.getString("name"));
				promotionItem.setSupplier_code(getpromotion.getString("supplier_code"));
				try {
					promotionItem.setSupplier_name(getpromotion.getString("supplier_name"));
				} catch (Exception e) {
					promotionItem.setSupplier_name("");
				}
				promotionItem.setEnd_date(Dates.formateyyyyMMdd(getpromotion.getString("end_date")));
				promotionItem.setStart_date(Dates.formateyyyyMMdd(getpromotion.getString("start_date")));
				promotionItem.setPromoid(getpromotion.getLong("promoid"));
				try {
					promotionItem.setPromotion_des(getpromotion.getString("promotion_des"));
				} catch (Exception e) {
					promotionItem.setPromotion_des("");
				}
				promotionItem.setVideo_url(getpromotion.getString("video_url"));

				if (getpromotion.getString("value").equals("null")) {
					promotionItem.setValue("0");
				} else {
					promotionItem.setValue(getpromotion.getString("value"));
				}

				if (getpromotion.getString("active").equals("Y")) {
					try {
						Date date_two = Dates.serverdateformateDateObj(getpromotion.getString("end_date"));
						Date current_date = new Date();
						if (date_two.after(current_date) || (current_date.getDate() == date_two.getDate())) {
							promotions.add(promotionItem);
						}
					} catch (Exception e) {

					}
				}
			}
		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return promotions;
	}

	public List<DTO> getProductData(String respondsFeed) {

		List<DTO> productList = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject productINFO = dataJSONARRAy.getJSONObject(i);
				ProductDTO productDTO = new ProductDTO();
				productDTO.setModifiedDate(productINFO.getString("modified_date"));
				productDTO.setCreateDate(productINFO.getString("create_date"));
				productDTO.setName(productINFO.getString("name"));
				productDTO.setUom(productINFO.getString("uom"));
				productDTO.setSupplierId(productINFO.getString("supplier_code"));
				productDTO.setBarcode(productINFO.getString("barcode"));
				productDTO.setLineId("" + productINFO.getString("line_id"));
				productDTO.setActiveStatus(0);
				productDTO.setSellingPrice("" + productINFO.getDouble("selling_price"));
				try {
					productDTO.setQuantity("" + productINFO.getLong("quantity"));
				} catch (Exception e) {
					productDTO.setQuantity("0");
				}
				productDTO.setGroupId("" + productINFO.getString("group_id"));
				productDTO.setPurchasePrice("" + productINFO.getDouble("purchase_price"));
				productDTO.setVat(productINFO.getString("vat"));
				productDTO.setProductId(productINFO.getString("barcode"));
				productDTO.setProductFlag(productINFO.getString("product_source"));
				productDTO.setExpiry_date(productINFO.getString("expiry_date"));
				productDTO.setDiscount(productINFO.getString("discount"));
				// try {
				// productDTO.setMin_count_inventory(productINFO
				// .getString("min_count_inventory"));
				// } catch (Exception e) {
				// productDTO.setMin_count_inventory("0");
				// }
				if (productINFO.get("min_count_inventory").equals("null")) {
					productDTO.setMin_count_inventory("0");

				} else {
					productDTO.setMin_count_inventory(productINFO.getString("min_count_inventory"));
				}
				productDTO.setSyncStatus(1);
				productDTO.setFecha_inicial(productINFO.getString("fecha_inicial"));
				productDTO.setFecha_final(productINFO.getString("fecha_final"));
				try {
					if ("" != productINFO.getString("barcode") && "" != productINFO.getString("supplier_code")) {
						productList.add(productDTO);
					}
				} catch (Exception e) {

				}
				if (productINFO.get("discount").equals("null")) {
					productDTO.setDiscount("0");

				} else {
					productDTO.setDiscount(productINFO.getString("discount"));
				}

			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return productList;
	}

	public List<DTO> getInventoryData(String respondsFeed) {

		List<DTO> list = new ArrayList<DTO>();
		try {
			JSONObject JSONObject = new JSONObject(respondsFeed);
			JSONArray JSONArrayobj = JSONObject.getJSONArray("data");
			for (int i = 0; i < JSONArrayobj.length(); i++) {
				InventoryDTO inventoryDTO = new InventoryDTO();
				JSONObject dataObjcet = JSONArrayobj.getJSONObject(i);
				inventoryDTO.setProductId(dataObjcet.getString("barcode"));
				inventoryDTO.setQuantity(dataObjcet.getString("quantity"));
				inventoryDTO.setUom(dataObjcet.getString("uom"));
				inventoryDTO.setSyncStatus(0);
				list.add(inventoryDTO);
			}
		} catch (JSONException e) {
			return list;
		}
		return list;
	}

	public List<DTO> getInventoryhistoryData(String responds_feed) {
		List<DTO> list = new ArrayList<DTO>();
		try {
			JSONObject JSONObject = new JSONObject(responds_feed);
			JSONArray JSONArrayobj = JSONObject.getJSONArray("data");
			for (int i = 0; i < JSONArrayobj.length(); i++) {
				InventoryHistoryDTO inventoryhistoryDTO = new InventoryHistoryDTO();
				JSONObject dataObjcet = JSONArrayobj.getJSONObject(i);
				inventoryhistoryDTO.setDateTime(dataObjcet.getString("create_date"));
				// inventoryhistoryDTO.setInventorHistoryId(dataObjcet.getString("create_date"));
				if (dataObjcet.getString("selling_price") != null) {
					inventoryhistoryDTO.setPrice(Double.parseDouble(dataObjcet.getString("selling_price")));
				}
				inventoryhistoryDTO.setProductId(dataObjcet.getString("barcode"));
				inventoryhistoryDTO.setQuantity(dataObjcet.getString("quantity"));
				inventoryhistoryDTO.setSyncStatus(0);
				inventoryhistoryDTO.setUom(dataObjcet.getString("uom"));
				list.add(inventoryhistoryDTO);
			}
		} catch (JSONException e) {
			return list;
		}
		return list;
	}

	public List<DTO> getInventoryAmount(String respondsFeed, int Position) {
		List<DTO> inventoryamount = new ArrayList<DTO>();
		try {
			if (Position == 1) { // productos por rentabilidad
				JSONArray arrayData = new JSONArray(respondsFeed);
				for (int i = 0; i < arrayData.length(); i++) {
					JSONObject getinventoryvalue = arrayData.getJSONObject(i);
					InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
					try {
						JSONObject jsonobj = getinventoryvalue.getJSONObject("product");
						inventoryValue.setInventoryName(jsonobj.getString("name")); // producto
						//inventoryValue.setselling(jsonobj.getString("supplier_code"));
						inventoryValue.setselling(jsonobj.getString("barcode")); // unidades vendidas
						inventoryValue.setpurchases(getinventoryvalue.getString("profit")); // rentabilidad acumulada
						inventoryamount.add(inventoryValue);
					} catch (Exception e) {
					}
				}

			} else if (Position == 2) {
				JSONArray arrayData = new JSONArray(respondsFeed);
				for (int i = 0; i < arrayData.length(); i++) {
					JSONObject getinventoryvalue = arrayData.getJSONObject(i);
					InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
					try {
						inventoryValue.setpurchases(getinventoryvalue.getString("profit"));
						JSONObject jsonobj = getinventoryvalue.getJSONObject("supplier");
						inventoryValue.setInventoryName(jsonobj.getString("name"));
						inventoryValue.setselling(jsonobj.getString("supplier_code"));
						inventoryamount.add(inventoryValue);
					} catch (Exception e) {

					}

				}

			} else if (Position == 3) { // Producto más vendido
				JSONArray arrayData = new JSONArray(respondsFeed);
				for (int i = 0; i < arrayData.length(); i++) {
					JSONObject getinventoryvalue = arrayData.getJSONObject(i);
					InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
					try {
						JSONObject jsonobj = getinventoryvalue.getJSONObject("product");
						inventoryValue.setInventoryName(jsonobj.getString("name")); // producto
						// Solo se envía el nombre del proveedor a petición del requerimiento 4.3.7.2 B
						//String supcode = jsonobj.getString("supplier_code") + "-" + getinventoryvalue.getString("supplier_name");
						inventoryValue.setselling(getinventoryvalue.getString("supplier_name")); // proveedor
						inventoryValue.setpurchases("" + getinventoryvalue.getInt("count")); // cantidad
						inventoryValue.setProduct(jsonobj.getString("barcode")); // codigo de barras
						inventoryamount.add(inventoryValue);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} else if (Position == 4) {

				JSONArray arrayData = new JSONArray(respondsFeed);
				for (int i = 0; i < arrayData.length(); i++) {
					JSONObject getinventoryvalue = arrayData.getJSONObject(i);
					InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
					try {
						if (getinventoryvalue.getString("profit").equals("0")) {

						} else {
							inventoryValue.setInventoryName(getinventoryvalue.getString("profit"));
							inventoryValue.setpurchases(getinventoryvalue.getString("date"));
							inventoryamount.add(inventoryValue);
						}

					} catch (Exception e) {

					}
				}

			} else if (Position == 5) { // Histórico de Caja
				JSONArray arrayData = new JSONArray(respondsFeed);
				for (int i = 0; i < arrayData.length(); i++) {
					JSONObject getinventoryvalue = arrayData.getJSONObject(i);
					InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
					try {
						inventoryValue.setInventoryName(getinventoryvalue.getString("transaction_type"));
						inventoryValue.setpurchases(getinventoryvalue.getString("module_name"));
						inventoryValue.setselling("" + getinventoryvalue.getLong("amount"));
						inventoryValue.setFechaHora(getinventoryvalue.getString("datetime"));
						inventoryamount.add(inventoryValue);
					} catch (Exception e) {
					}

				}

			} else if (Position == 6) { // Cuentas por cobrar

				JSONArray arrayData = new JSONArray(respondsFeed);
				// Se ordena la lista alfabéticamente según la llave a petición del requerimiento 4.3.7.5 A
				//for (int i = 0; i < arrayData.length(); i++) {
				JSONArray sortedArrayData = Utils.sortJsonArray(arrayData, "name");
				for (int i = 0; i < sortedArrayData.length(); i++) {
					//JSONObject getinventoryvalue = arrayData.getJSONObject(i);
					JSONObject getinventoryvalue = sortedArrayData.getJSONObject(i);
					try {
						// Solo se agregan los clientes cuya deuda es mayor a cero requerimiento 4.3.7.5 B
						System.out.println(CommonMethods.getDoubleFormate(getinventoryvalue.getString("balance_amount")));
						if(CommonMethods.getDoubleFormate(getinventoryvalue.getString("balance_amount")) > 0){
							InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
							inventoryValue.setInventoryName(getinventoryvalue.getString("name"));
							//inventoryValue.setpurchases(getinventoryvalue.getString("customer_code"));
							// Se retorna el telefono a petición del requerimiento 4.3.7.5 C
							inventoryValue.setpurchases(getinventoryvalue.getString("telephone"));
							inventoryValue.setselling(getinventoryvalue.getString("balance_amount"));
							inventoryamount.add(inventoryValue);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} else if (Position == 7) { // Cuentas por pagar
				JSONArray arrayData = new JSONArray(respondsFeed);
				for (int i = 0; i < arrayData.length(); i++) {
					JSONObject getinventoryvalue = arrayData.getJSONObject(i);
					InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
					try {
						inventoryValue.setInventoryName(getinventoryvalue.getString("name"));
						inventoryValue.setpurchases(getinventoryvalue.getString("supplier_code"));
						inventoryValue.setselling(getinventoryvalue.getString("balance_amount"));
						// inventoryValue.setdebt_days(""
						// + getinventoryvalue.getLong("days_debt"));
						String create_date = getinventoryvalue.getString("last_payment_date");
						String current_date = Dates.currentdate2();
						Log.v("date_two==========", create_date);
						Log.v("current_date==========", current_date);
						int diff_date = Dates.getDifferenceInDays(create_date, current_date);
						Log.v("diff_date==========", "" + diff_date);
						inventoryValue.setdebt_days("" + diff_date);
						if (getinventoryvalue.getDouble("balance_amount") == 0.0) {
							// No se agregan
						} else {
							inventoryamount.add(inventoryValue);
						}
					} catch (Exception e) {
					}

				}

			} else if (Position == 8) { // Valor del inventario en el establecimiento
				JSONArray arrayData = new JSONArray(respondsFeed);
				JSONArray sortedArrayData = Utils.sortJsonArray(arrayData, "product_name");
				//for (int i = 0; i < arrayData.length(); i++) {
				// Se ordena la lista alfabéticamente según el nombre del producto a petición del requerimiento 4.3.7.7 C
				for (int i = 0; i < sortedArrayData.length(); i++) {
					//JSONObject getinventoryvalue = arrayData.getJSONObject(i);
					JSONObject getinventoryvalue = sortedArrayData.getJSONObject(i);
					InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
					try {
						inventoryValue.setInventoryName(getinventoryvalue.getString("product_name"));
						if (getinventoryvalue.getString("purchase").contains("-")) {
							inventoryValue.setpurchases("0");
						} else {
							inventoryValue.setpurchases(getinventoryvalue.getString("purchase"));
						}
						if (getinventoryvalue.getString("selling").contains("-")) {
							inventoryValue.setselling("0");
						} else {
							inventoryValue.setselling(getinventoryvalue.getString("selling"));
						}
						inventoryValue.setQuantity(getinventoryvalue.getString("quantity"));
						inventoryamount.add(inventoryValue);
					} catch (Exception e) {
					}
				}
			} else if (Position == 9) { // Productos vencidos caducados
				JSONArray arrayData = new JSONArray(respondsFeed);
				// Se ordena la lista alfabéticamente según el nombre del producto a petición del requerimiento 4.3.7.8 C
				JSONArray sortedArrayData = Utils.sortJsonArray(arrayData, "name");
				//for (int i = 0; i < arrayData.length(); i++) {
				for (int i = 0; i < sortedArrayData.length(); i++) {
					JSONObject getinventoryvalue = sortedArrayData.getJSONObject(i);
					// Solo se agregan los productos cuya cantidad es mayor a cero requerimiento 4.3.7.8 D
					if(Integer.parseInt(getinventoryvalue.getString("quantity")) > 0){
						InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
						try {
							inventoryValue.setInventoryName(getinventoryvalue.getString("barcode"));
							inventoryValue.setpurchases(getinventoryvalue.getString("name"));
							inventoryValue.setselling(getinventoryvalue.getString("quantity"));
							inventoryValue.setdebt_days("" + (Double.parseDouble(getinventoryvalue.getString("quantity"))
									* getinventoryvalue.getDouble("selling_price")));
							if (getinventoryvalue.getString("name").contains("null")) {
								inventoryValue.setpurchases("");
							} else {
								inventoryValue.setpurchases(getinventoryvalue.getString("name"));
							}
							if (getinventoryvalue.getString("quantity").equals("0")
									|| getinventoryvalue.getString("quantity").equals("0.00")) {
							} else {
								inventoryamount.add(inventoryValue);
							}
							// inventoryamount.add(inventoryValue);
						} catch (Exception e) {
						}
					}
				}

			} else if (Position == 10) {
				JSONArray arrayData = new JSONArray(respondsFeed);
				for (int i = 0; i < arrayData.length(); i++) {
					JSONObject getinventoryvalue = arrayData.getJSONObject(i);
					InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
					try {
						inventoryValue.setInventoryName("" + getinventoryvalue.getDouble("total_sales"));
						inventoryValue.setpurchases("" + getinventoryvalue.getDouble("sold_items"));
						String s = "" + getinventoryvalue.getDouble("profitability");
						if (s.contains("-")) {
							inventoryValue.setselling("0");
						} else {
							inventoryValue.setselling(s);
						}
						inventoryValue.setdebt_days(getinventoryvalue.getString("product_name") + "-"
								+ getinventoryvalue.getString("barcode"));
						inventoryamount.add(inventoryValue);
					} catch (Exception e) {

					}

				}
			} else if (Position == 11) {

			}

		} catch (JSONException e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return inventoryamount;

	}

	public List<DTO> getModulesData(String respondsFeed) {

		List<DTO> productList = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("modules");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject productINFO = dataJSONARRAy.getJSONObject(i);
				UserModuleIdDTO userModuleIdDTO = new UserModuleIdDTO();
				userModuleIdDTO.setModuleCode(productINFO.getString("module_code"));
				userModuleIdDTO.setModuleName(productINFO.getString("name"));
				productList.add(userModuleIdDTO);
			}
			return productList;

		} catch (Exception e) {
			return productList;
		}
	}

	public ArrayList<ClientHistoryDTO> clientHistoryDTO(String resp) {
		ArrayList<ClientHistoryDTO> historyDetails = new ArrayList<ClientHistoryDTO>();
		try {
			JSONObject json = new JSONObject(resp);
			JSONObject datajsonobj = json.getJSONObject("data");
			JSONObject customerJSONObj = datajsonobj.getJSONObject("customer");

			ClientHistoryDTO clientHistoryDto = new ClientHistoryDTO("customer",
					customerJSONObj.getString("initial_debts"),
					Dates.serverdateformateDateObj(customerJSONObj.getString("created_date")), "00");
			historyDetails.add(clientHistoryDto);

			JSONObject balenceJSONObj = datajsonobj.getJSONObject("customer");
			ClientHistoryDTO balenceJSONObjHistoryDto = new ClientHistoryDTO("balence",
					balenceJSONObj.getString("balance_amount"), new Date(), "00");

			historyDetails.add(balenceJSONObjHistoryDto);

			JSONArray paymentsDataArray = datajsonobj.getJSONArray("payments_data");
			for (int i = 0; i < paymentsDataArray.length(); i++) {
				JSONObject paymentsDataJSONObj = paymentsDataArray.getJSONObject(i);
				try {
					ClientHistoryDTO paymentsDataClientHistoryDto = new ClientHistoryDTO("payments_data",
							paymentsDataJSONObj.getString("amount_paid"),
							Dates.serverdateformateDateObj(paymentsDataJSONObj.getString("date_time")),
							"" + paymentsDataJSONObj.getLong("sale_id"));
					historyDetails.add(paymentsDataClientHistoryDto);
				} catch (Exception e) {
					ClientHistoryDTO paymentsDataClientHistoryDto = new ClientHistoryDTO("payments_data",
							paymentsDataJSONObj.getString("amount_paid"),
							Dates.serverdateformateDateObj(paymentsDataJSONObj.getString("date_time")), "00");
					historyDetails.add(paymentsDataClientHistoryDto);
				}

			}

			JSONArray lendDataArray = datajsonobj.getJSONArray("lend_data");
			for (int i = 0; i < lendDataArray.length(); i++) {
				JSONObject lendDataJSONObj = lendDataArray.getJSONObject(i);
				ClientHistoryDTO lendDataClientHistoryDto = new ClientHistoryDTO("lend_data",
						lendDataJSONObj.getString("amount"),
						Dates.serverdateformateDateObj(lendDataJSONObj.getString("date_time")), "");
				historyDetails.add(lendDataClientHistoryDto);
			}
			return historyDetails;

		} catch (JSONException e) {
			return historyDetails;
		}
	}

	public List<DTO> getOrdersHistoricData(String respondsFeed) {

		List<DTO> productList = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				try {
					JSONObject productINFO = dataJSONARRAy.getJSONObject(i);

					JSONArray order_pro_jsonarray = productINFO.getJSONArray("order_details");
					for (int j = 0; j < order_pro_jsonarray.length(); j++) {
						JSONObject fproductINFO = order_pro_jsonarray.getJSONObject(j);
						ProductDTO userModuleIdDTO = new ProductDTO();
						try {
							userModuleIdDTO.setCreateDate(Dates.changeFormate(productINFO.getString("date_time")));
						} catch (Exception e) {
							userModuleIdDTO.setCreateDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
						}
						userModuleIdDTO.setName(fproductINFO.getString("barcode"));
						userModuleIdDTO.setPurchasePrice("" + fproductINFO.getDouble("price"));
						userModuleIdDTO.setSellingPrice(productINFO.getString("is_order_confirmed"));
						userModuleIdDTO.setQuantity(productINFO.getString("invoice_no"));
						productList.add(userModuleIdDTO);
					}
				} catch (Exception e) {
				}
			}
			return productList;
		} catch (Exception e) {
			return productList;
		}
	}

	public List<DTO> getMasterProductData(String responds_feed) {

		List<DTO> productList = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(responds_feed);
			// JSONObject projsonobj = rootJSONObj.getJSONObject("products");
			//
			// JSONObject jsonobject = new JSONObject(respondsFeed);
			// JSONArray arrayData = jsonobject.getJSONArray("customers");

			// JSONObject dataJSONObj = rootJSONObj.getJSONObject("products");
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("master_products");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject productINFO = dataJSONARRAy.getJSONObject(i);
				Master_ProductDTO productDTO = new Master_ProductDTO();
				productDTO.setModifiedDate(productINFO.getString("modified_date"));
				productDTO.setCreateDate(productINFO.getString("create_date"));
				productDTO.setName(productINFO.getString("name"));
				productDTO.setUom(productINFO.getString("uom"));
				productDTO.setSupplierId(productINFO.getString("supplier_code"));
				productDTO.setBarcode(productINFO.getString("barcode"));
				productDTO.setLineId("" + productINFO.getString("line_id"));
				productDTO.setActiveStatus(0);
				productDTO.setSellingPrice("" + productINFO.getDouble("selling_price"));
				productDTO.setQuantity("" + productINFO.getLong("quantity"));
				productDTO.setGroupId("" + productINFO.getString("group_id"));
				productDTO.setPurchasePrice("" + productINFO.getDouble("purchase_price"));
				productDTO.setVat(productINFO.getString("vat"));
				productDTO.setProductId(productINFO.getString("barcode"));
				productDTO.setProductFlag(productINFO.getString("product_source"));
				productDTO.setSyncStatus(0);
				productList.add(productDTO);
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return productList;
	}

	public ProductDTO mAsterProductInfo(String responds_feed) {
		ProductDTO pro_DTO = new ProductDTO();
		try {
			JSONObject rootJSONObj = new JSONObject(responds_feed);
			JSONObject productINFO = rootJSONObj.getJSONObject("data");

			if (productINFO.getString("supplier_code").length() > 2
					&& productINFO.getString("supplier_code") != "null") {
				try {
					pro_DTO.setModifiedDate(productINFO.getString("modified_date"));
					pro_DTO.setCreateDate(productINFO.getString("create_date"));
				} catch (Exception e) {
					pro_DTO.setModifiedDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
					pro_DTO.setCreateDate(Dates.getSysDate(Dates.YYYY_MM_DD_HH_MM));
				}
				pro_DTO.setName(productINFO.getString("name"));
				pro_DTO.setUom(productINFO.getString("uom"));
				pro_DTO.setSupplierId(productINFO.getString("supplier_code"));
				pro_DTO.setBarcode(productINFO.getString("barcode"));
				pro_DTO.setLineId("" + productINFO.getString("line_id"));
				pro_DTO.setGroupId("" + productINFO.getString("group_id"));

				pro_DTO.setActiveStatus(0);
				try {
					pro_DTO.setQuantity("" + productINFO.getLong("quantity"));
				} catch (Exception e) {
					pro_DTO.setQuantity("0");
				}
				try {
					pro_DTO.setSellingPrice("" + productINFO.getDouble("selling_price"));
				} catch (Exception e) {
					pro_DTO.setSellingPrice("0");
				}

				try {
					pro_DTO.setPurchasePrice("" + productINFO.getDouble("purchase_price"));
				} catch (Exception e) {
					pro_DTO.setPurchasePrice("0");
				}
				pro_DTO.setVat(productINFO.getString("vat"));
				pro_DTO.setProductFlag(productINFO.getString("product_source"));
				pro_DTO.setSyncStatus(0);
				try {
					pro_DTO.setSupplierName(productINFO.getString("supplier_name"));
				} catch (Exception e) {
					pro_DTO.setSupplierName("");
				}
				try {
					pro_DTO.setSubgroup(productINFO.getString("sub_group"));
				} catch (Exception e) {
					pro_DTO.setSubgroup("");
				}

				try {
					pro_DTO.setMin_count_inventory(productINFO.getString("min_count_inventory"));
				} catch (Exception e) {
					pro_DTO.setMin_count_inventory("0");
				}
				pro_DTO.setGroupName(productINFO.getString("group_name"));
				pro_DTO.setLineName(productINFO.getString("line_name"));
			} else {
			}
			return pro_DTO;
		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return pro_DTO;
	}

	public List<DTO> getmenus(String respondsFeed) {

		List<DTO> menuslist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject productINFO = dataJSONARRAy.getJSONObject(i);
				MenuDTO productDTO = new MenuDTO();
				productDTO.setMenuId("" + productINFO.getLong("menu_id"));

				productDTO.setEndDate(productINFO.getString("end_date"));
				productDTO.setMenuTypeId("" + productINFO.getLong("menu_type_id"));
				productDTO.setName(productINFO.getString("name"));
				productDTO.setStartDate(productINFO.getString("start_date"));
				productDTO.setSyncStatus(1);
				productDTO.setWeekDays(productINFO.getString("week_days"));
				menuslist.add(productDTO);
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return menuslist;
	}

	public List<DTO> getmenusDishs(String respondsFeed) {

		List<DTO> menuslist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject productINFO = dataJSONARRAy.getJSONObject(i);
				MenuDishesDTO productDTO = new MenuDishesDTO();
				productDTO.setDishId(" " + productINFO.getLong("dish_id"));
				productDTO.setMenuDishesId(" " + productINFO.getLong("menu_dishes_id"));
				productDTO.setMenuId(" " + productINFO.getLong("menu_id"));
				productDTO.setSyncStatus(1);
				menuslist.add(productDTO);
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return menuslist;
	}

	public List<DTO> getmenuInventory(String respondsFeed) {

		List<DTO> menuslist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject productINFO = dataJSONARRAy.getJSONObject(i);
				MenuInventoryDTO productDTO = new MenuInventoryDTO();
				productDTO.setDishId(" " + productINFO.getLong("dish_id"));
				productDTO.setMenuInventoryId(" " + productINFO.getLong("menu_inventory_id"));
				productDTO.setCount(productINFO.getString("count"));
				productDTO.setSyncStatus(1);
				menuslist.add(productDTO);
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return menuslist;
	}

	public List<DTO> getinventory(String respondsFeed) {

		List<DTO> inventorylist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject productINFO = dataJSONARRAy.getJSONObject(i);
				InventoryDTO productDTO = new InventoryDTO();
				productDTO.setInventoryId("" + productINFO.getLong("inventory_id"));
				productDTO.setProductId(productINFO.getString("barcode"));
				productDTO.setUom(productINFO.getString("uom"));
				productDTO.setQuantity(productINFO.getString("quantity"));
				productDTO.setSyncStatus(1);
				if (null != productINFO.getString("barcode")) {
					inventorylist.add(productDTO);
				}
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return inventorylist;
	}

	public List<DTO> getdishes(String respondsFeed) {

		List<DTO> dishlist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject dishINFO = dataJSONARRAy.getJSONObject(i);
				DishesDTO dishesDTO = new DishesDTO();
				dishesDTO.setDishId("" + dishINFO.getLong("dish_id"));
				dishesDTO.setDishName(dishINFO.getString("dish_name"));
				dishesDTO.setDishPrice("" + dishINFO.getDouble("dish_price"));
				dishesDTO.setNoOfItems(dishINFO.getString("no_of_items"));
				dishesDTO.setExpiryDays(dishINFO.getString("expiry_days"));
				dishesDTO.setVat(dishINFO.getString("vat"));
				dishesDTO.setSellingCostperItem("" + dishINFO.getDouble("selling_cost_per_item"));
				dishesDTO.setSyncStatus(1);
				dishlist.add(dishesDTO);
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return dishlist;
	}

	public List<DTO> getdish_products(String respondsFeed) {

		List<DTO> dishproducts = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject dishproductINFO = dataJSONARRAy.getJSONObject(i);
				DishProductsDTO dishproductDto = new DishProductsDTO();
				try {
					dishproductDto.setDishProductId("" + dishproductINFO.getLong("dish_product_id"));
				} catch (Exception e) {
					dishproductDto.setDishProductId("");
				}
				dishproductDto.setDishId("" + dishproductINFO.getLong("dish_id"));
				dishproductDto.setProductId(dishproductINFO.getString("barcode"));
				dishproductDto.setUom(dishproductINFO.getString("uom"));
				dishproductDto.setSyncStatus(1);
				dishproductDto.setQuantity(dishproductINFO.getString("quantity"));
				dishproducts.add(dishproductDto);
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return dishproducts;
	}

	public List<DTO> getgroup(String respondsFeed) {

		List<DTO> grouplist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject groupINFO = dataJSONARRAy.getJSONObject(i);
				GroupDTO groupDTO = new GroupDTO();
				groupDTO.setGroupId(groupINFO.getString("group_id"));
				groupDTO.setName(groupINFO.getString("name"));
				grouplist.add(groupDTO);
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return grouplist;
	}

	public List<DTO> getline(String respondsFeed) {

		List<DTO> linelist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject lineINFO = dataJSONARRAy.getJSONObject(i);
				LineDTO lineDTO = new LineDTO();
				lineDTO.setLineId(lineINFO.getString("line_id"));
				lineDTO.setName(lineINFO.getString("name"));
				lineDTO.setGroupId(lineINFO.getString("group_id"));
				linelist.add(lineDTO);
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return linelist;
	}

	public List<DTO> getdamage_type(String respondsFeed) {

		List<DTO> damage_typelist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject damage_typeINFO = dataJSONARRAy.getJSONObject(i);
				DamageTypeDTO damagetypeDTO = new DamageTypeDTO();
				damagetypeDTO.setDamageTypeId("" + damage_typeINFO.getLong("damage_type_id"));
				damagetypeDTO.setName(damage_typeINFO.getString("name"));
				damage_typelist.add(damagetypeDTO);
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return damage_typelist;
	}

	public List<DTO> getmenusTypes(String respondsFeed) {

		List<DTO> menutypeslist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject menutypesINFO = dataJSONARRAy.getJSONObject(i);
				MenuTypesDTO menutypesDTO = new MenuTypesDTO();
				menutypesDTO.setMenuTypeId("" + menutypesINFO.getLong("menu_type_id"));
				menutypesDTO.setName(menutypesINFO.getString("name"));
				menutypesDTO.setSyncStatus(1);
				menutypeslist.add(menutypesDTO);
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return menutypeslist;
	}

	public List<DTO> getuser(String respondsFeed) {

		List<DTO> menutypeslist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int i = 0; i < dataJSONARRAy.length(); i++) {
				JSONObject jsonuserJsonObject = dataJSONARRAy.getJSONObject(i);
				UserDetailsDTO userDetails = new UserDetailsDTO();
				userDetails.setCedulaDocument(jsonuserJsonObject.getString("cedula_document"));
				userDetails.setCloseDateTime(jsonuserJsonObject.getString("closing_time"));
				userDetails.setCompanyName(jsonuserJsonObject.getString("company_name"));
				userDetails.setImei(jsonuserJsonObject.getString("imei"));
				userDetails.setIsClosed(Constants.TRUE);
				userDetails.setLastLogin(jsonuserJsonObject.getString("last_login"));
				userDetails.setName(jsonuserJsonObject.getString("name"));
				userDetails.setNitShopId(jsonuserJsonObject.getString("store_code"));
				userDetails.setOpeningBalance(Dates.currentdate());
				userDetails.setOpeningDateTime(jsonuserJsonObject.getString("opening_date_time"));
				userDetails.setPassword(jsonuserJsonObject.getString("password"));
				userDetails.setRegistrationDate(jsonuserJsonObject.getString("registration_date"));
				userDetails.setSyncStatus(Constants.FALSE);
				userDetails.setUserId(jsonuserJsonObject.getString("user_id"));
				userDetails.setUserName(jsonuserJsonObject.getString("username"));
				menutypeslist.add(userDetails);
			}

		} catch (Exception e) {
			ServiApplication.connectionTimeOutState = false;
		}
		return menutypeslist;
	}

	public List<DTO> getInventoryInvoiceDTOData(String responds) {
		List<DTO> menutypeslist = new ArrayList<DTO>();
		try {
			JSONObject jsonobj = new JSONObject(responds);
			JSONArray data = jsonobj.getJSONArray("data");
			for (int i = 0; i < data.length(); i++) {
				JSONObject data_jsonobj = data.getJSONObject(i);
				InventoryInvoiceDTO iid = new InventoryInvoiceDTO();
				iid.setInvoiceNum(data_jsonobj.getString("invoice_number"));
				iid.setSupplierName(data_jsonobj.getString("supplier_name"));
				iid.setTotalAmount(" " + data_jsonobj.getLong("total_amt"));
				List<DTO> list = new ArrayList<DTO>();
				try {
					JSONArray productarray = data_jsonobj.getJSONArray("products");
					for (int j = 0; j < productarray.length(); j++) {
						ProductDTO dto = new ProductDTO();
						JSONObject pjsonobj = productarray.getJSONObject(j);
						dto.setBarcode(pjsonobj.getString("barcode"));
						dto.setProductId(pjsonobj.getString("barcode"));
						dto.setName(pjsonobj.getString("name"));
						dto.setSupplierId(pjsonobj.getString("supplier_code"));
						dto.setQuantity(pjsonobj.getString("quantity"));
						dto.setSellingPrice("" + pjsonobj.getDouble("selling_price"));
						list.add(dto);
					}
				} catch (Exception e) {
				}
				iid.setProductsList(list);
				menutypeslist.add(iid);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return menutypeslist;
	}

	public List<DTO> getDataFromAssert(String respondsFeed) {
		List<DTO> cellularList = new ArrayList<DTO>();
		try {
			JSONArray arrayData = new JSONArray(respondsFeed);
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getinventoryvalue = arrayData.getJSONObject(i);
				CellularProviderDto cellular = new CellularProviderDto();
				cellular.setCellularId(getinventoryvalue.getString("cellular_id"));
				cellular.setDescription(getinventoryvalue.getString("description"));
				cellular.setMinAmount(getinventoryvalue.getString("min_amount"));
				cellular.setMaxAmount(getinventoryvalue.getString("max_amount"));
				cellular.setMultiplier(getinventoryvalue.getString("multiplier"));
				cellularList.add(cellular);
			}
		} catch (Exception e) {
		}
		return cellularList;

	}

	public String getString(Document document, String parent, String child) {

		String title = null;
		try {
			NodeList nodes = document.getElementsByTagName(parent);
			for (int i = 0; i < nodes.getLength(); i++) {
				Node element = nodes.item(i);
				NodeList childList = element.getChildNodes();
				for (int j = 0; j < childList.getLength(); j++) {
					Node childElement = childList.item(j);
					String nodeName = childElement.getNodeName();
					if (nodeName.equalsIgnoreCase(child)) {
						title = childElement.getFirstChild().getNodeValue();

					}
				}
			}
		} catch (Exception e) {
			title = "";
		} finally {
		}
		return title;
	}

	public List<DTO> getProvidersList(String string, Context context, int j) {
		List<DTO> cellularList = new ArrayList<DTO>();
		try {
			JSONObject rootjasonobj = new JSONObject(string);
			JSONArray arrayData = rootjasonobj.getJSONArray("products");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getinventoryvalue = arrayData.getJSONObject(i);
				if (i == 0) {
					CellularProviderDto cellular1 = new CellularProviderDto();
					if (j == 1) {
						cellular1.setProductName(context.getResources().getString(R.string.select_operator_option));
					} else if (j == 2) {
						cellular1.setProductName(context.getResources().getString(R.string.select_operator_paquetigos));
					}
					cellular1.setProductNumber("000");
					cellular1.setPrintable(false);
					cellular1.setValor("00");
					cellularList.add(cellular1);
				}

				CellularProviderDto cellular = new CellularProviderDto();
				cellular.setProductName(getinventoryvalue.getString("productName"));
				cellular.setProductNumber(getinventoryvalue.getString("productNumber"));
				cellular.setPrintable(getinventoryvalue.getBoolean("printable"));
				cellular.setValor(getinventoryvalue.getString("valor"));
				cellularList.add(cellular);
			}
		} catch (Exception e) {
		}
		return cellularList;
	}

	public List<DTO> getconsultarVentas(String string) {
		List<DTO> ct = new ArrayList<DTO>();
		try {
			JSONObject rootjasonobj = new JSONObject(string);
			JSONArray arrayData = rootjasonobj.getJSONArray("consolidados");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getinventoryvalue = arrayData.getJSONObject(i);
				InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
				inventoryValue.setProduct(getinventoryvalue.getString("producto"));
				inventoryValue.setQuantity(getinventoryvalue.getString("cantidad"));
				inventoryValue.setSumatoria(getinventoryvalue.getString("sumatoria"));
				ct.add(inventoryValue);
			}
		} catch (Exception e) {
		}
		return ct;
	}

	public List<DTO> getConsultarTransacciones(String string) {
		List<DTO> ct = new ArrayList<DTO>();
		try {
			JSONObject rootjasonobj = new JSONObject(string);
			JSONArray arrayData = rootjasonobj.getJSONArray("transacciones");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getinventoryvalue = arrayData.getJSONObject(i);
				InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
				inventoryValue.setIdTransaccion(getinventoryvalue.getString("idTransaccion"));
				inventoryValue.setFechaHora(getinventoryvalue.getString("fechaHora"));
				inventoryValue.setTipo(getinventoryvalue.getString("tipo"));
				inventoryValue.setValor(getinventoryvalue.getString("valor"));
				inventoryValue.setEstado(getinventoryvalue.getString("estado"));
				ct.add(inventoryValue);
			}
		} catch (Exception e) {
		}
		return ct;
	}

	public List<DTO> getSaldoporCuentas(String string) {
		List<DTO> ct = new ArrayList<DTO>();
		try {
			InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
			JSONObject jsonobj = new JSONObject(string);

			try {
				inventoryValue.setdebt_days(CommonMethods
						.getNumSeparator(CommonMethods.getDoubleFormate(jsonobj.getString("cupoDisponibleCashIn"))));
			} catch (Exception e) {
				inventoryValue.setdebt_days(jsonobj.getString("cupoDisponibleCashIn"));
			}
			try {
				inventoryValue.setEstado(CommonMethods
						.getNumSeparator(CommonMethods.getDoubleFormate(jsonobj.getString("cupoDisponibleCashOut"))));
			} catch (Exception e) {
				inventoryValue.setEstado(jsonobj.getString("cupoDisponibleCashOut"));
			}

			ct.add(inventoryValue);
		} catch (Exception e) {
		}
		return ct;
	}

	public List<DTO> getProductoReportsData(String string) {
		List<DTO> ct = new ArrayList<DTO>();
		try {
			JSONObject rootjasonobj = new JSONObject(string);
			JSONArray arrayData = rootjasonobj.getJSONArray("reportes");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getinventoryvalue = arrayData.getJSONObject(i);
				InventoryAmountDTO inventoryValue = new InventoryAmountDTO();
				inventoryValue.setpurchases(getinventoryvalue.getString("numero")); //
				inventoryValue.setIdTransaccion(getinventoryvalue.getString("id_transaccion"));
				inventoryValue.setFechaHora(getinventoryvalue.getString("fecha"));
				inventoryValue.setValor(getinventoryvalue.getString("valor"));
				inventoryValue.setEstado(getinventoryvalue.getString("estado"));
				inventoryValue.setProduct(getinventoryvalue.getString("producto"));
				inventoryValue.setdebt_days(getinventoryvalue.getString("paquetigo"));
				inventoryValue.setQuantity(getinventoryvalue.getString("loteria"));
				inventoryValue.setselling(getinventoryvalue.getString("cedula")); //
				inventoryValue.setTipo(getinventoryvalue.getString("referencia1"));
				inventoryValue.setSumatoria(getinventoryvalue.getString("referencia2"));
				inventoryValue.setInventoryName(getinventoryvalue.getString("convenio")); //
				ct.add(inventoryValue);
			}
		} catch (Exception e) {
		}
		return ct;
	}

	public ConsultaConvenioDTO getConsultaConvenioDTOData(String vale) {
		ConsultaConvenioDTO dto = new ConsultaConvenioDTO();
		try {
			JSONObject rootjasonobj = new JSONObject(vale);
			dto.setCodigoConvenio(rootjasonobj.getString("codigoConvenio"));
			dto.setCodigoEntidad(rootjasonobj.getString("codigoEntidad"));
			dto.setCodigoOficina(rootjasonobj.getString("codigoOficina"));
			dto.setCodigoProceso(rootjasonobj.getString("codigoProceso"));
			dto.setControlRef1(rootjasonobj.getString("controlRef1"));
			dto.setControlRef2(rootjasonobj.getString("controlRef2"));
			dto.setConvId(rootjasonobj.getString("convId"));
			dto.setCuentaDestino(rootjasonobj.getString("cuentaDestino"));
			dto.setCuentaRecaudadora(rootjasonobj.getString("cuentaRecaudadora"));
			dto.setDescripcion(rootjasonobj.getString("descripcion"));
			dto.setEstado(rootjasonobj.getBoolean("estado"));
			dto.setFecha(rootjasonobj.getString("fecha"));
			dto.setFormato(rootjasonobj.getString("formato"));
			dto.setIac(rootjasonobj.getString("iac"));
			dto.setId(rootjasonobj.getString("id"));
			dto.setMensaje(rootjasonobj.getString("mensaje"));
			dto.setNombreReferencia1(rootjasonobj.getString("nombreReferencia1"));
			dto.setNombreReferencia2(rootjasonobj.getString("nombreReferencia2"));
			dto.setProdCodigo(rootjasonobj.getString("prodCodigo"));
			dto.setReferencia1(rootjasonobj.getString("referencia1"));
			dto.setReferencia2(rootjasonobj.getString("referencia2"));
			dto.setTipoCuentaDestino(rootjasonobj.getString("tipoCuentaDestino"));
			dto.setTipoCuentaRecaudadora(rootjasonobj.getString("tipoCuentaRecaudadora"));
			dto.setValidaFecha(rootjasonobj.getBoolean("validaFecha"));
			dto.setValor(rootjasonobj.getString("valor"));
			return dto;
		} catch (Exception e) {
			return null;
		}
	}

	public List<DTO> getPinesProvidersList(String string, boolean b, Context context) {
		List<DTO> cellularList = new ArrayList<DTO>();
		try {
			JSONObject rootjasonobj = new JSONObject(string);
			JSONArray arrayData = rootjasonobj.getJSONArray("products");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getinventoryvalue = arrayData.getJSONObject(i);
				if (i == 0) {
					CellularProviderDto cellular = new CellularProviderDto();
					if (b) {
						cellular.setProductName(context.getString(R.string.select_operator_pines));
					} else {
						cellular.setProductName(context.getString(R.string.select_product_pines));
					}

					cellular.setProductNumber("0");
					cellular.setPrintable(false);
					cellular.setValor("00");
					cellularList.add(cellular);
				}
				CellularProviderDto cellular1 = new CellularProviderDto();
				cellular1.setProductName(getinventoryvalue.getString("productName"));
				cellular1.setProductNumber(getinventoryvalue.getString("productNumber"));
				cellular1.setPrintable(getinventoryvalue.getBoolean("printable"));
				cellular1.setValor(getinventoryvalue.getString("valor"));
				cellularList.add(cellular1);

			}
		} catch (Exception e) {
		}
		return cellularList;
	}

	public ConsultaConveniosDTO getconsultaConvenios(String vale) {
		ConsultaConveniosDTO dto = new ConsultaConveniosDTO();

		try {
			JSONObject json = new JSONObject(vale);
			dto.setCodigoConvenio(json.getString("codigoConvenio"));
			dto.setCodigoEntidad(json.getString("codigoEntidad"));
			dto.setCodigoOficina(json.getString("codigoOficina"));
			dto.setCodigoProceso(json.getString("codigoProceso"));
			dto.setControlRef1(json.getString("controlRef1"));
			dto.setControlRef2(json.getString("controlRef2"));
			dto.setConvId(json.getString("convId"));
			dto.setCuentaDestino(json.getString("cuentaDestino"));
			dto.setCuentaRecaudadora(json.getString("cuentaRecaudadora"));
			dto.setData(json.getString("data"));
			dto.setDescripcion(json.getString("descripcion"));
			dto.setEstado(json.getBoolean("estado"));
			dto.setFecha(json.getString("fecha"));
			dto.setFormato(json.getString("formato"));
			dto.setIac(json.getString("iac"));
			dto.setId(json.getString("id"));
			dto.setMensaje(json.getString("mensaje"));
			dto.setMessage(json.getString("message"));
			dto.setNombreReferencia1(json.getString("nombreReferencia1"));
			dto.setNombreReferencia2(json.getString("nombreReferencia2"));
			dto.setProdCodigo(json.getString("prodCodigo"));
			dto.setReferencia1(json.getString("referencia1"));
			dto.setReferencia2(json.getString("referencia2"));
			dto.setState(json.getBoolean("state"));
			dto.setTipoCuentaDestino(json.getString("tipoCuentaDestino"));
			dto.setTipoCuentaRecaudadora(json.getString("tipoCuentaRecaudadora"));
			dto.setValidaFecha(json.getBoolean("validaFecha"));
			dto.setValor(json.getString("valor"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dto;
	}

	public List<DTO> getConsultarLoterias(String string, Context context) {
		List<DTO> cellularList = new ArrayList<DTO>();
		try {
			JSONObject rootjasonobj = new JSONObject(string);
			JSONArray arrayData = rootjasonobj.getJSONArray("products");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getinventoryvalue = arrayData.getJSONObject(i);

				if (i == 0) {
					ConsultarLoteriasDTO cellular1 = new ConsultarLoteriasDTO();
					cellular1.setDateClose("");
					cellular1.setLoteFraction("");
					cellular1.setLoteID("");
					cellular1.setLoteName(context.getString(R.string.slect_lotery_option));
					cellular1.setLoteNumberLong("");
					cellular1.setLoteSerieLong("");
					cellular1.setSorteo("");
					cellular1.setValor("");
					cellularList.add(cellular1);
				}

				ConsultarLoteriasDTO cellular1 = new ConsultarLoteriasDTO();
				cellular1.setDateClose(getinventoryvalue.getString("dateClose"));
				cellular1.setLoteFraction(getinventoryvalue.getString("loteFraction"));
				cellular1.setLoteID(getinventoryvalue.getString("loteID"));
				cellular1.setLoteName(getinventoryvalue.getString("loteName"));
				cellular1.setLoteNumberLong(getinventoryvalue.getString("loteNumberLong"));
				cellular1.setLoteSerieLong(getinventoryvalue.getString("loteSerieLong"));
				cellular1.setSorteo(getinventoryvalue.getString("sorteo"));
				cellular1.setValor(getinventoryvalue.getString("valor"));
				cellularList.add(cellular1);

			}
		} catch (Exception e) {
		}
		return cellularList;
	}

	public List<DTO> getConsultarSeriesLoteriasDTO(String string, String string2, String string3, String string4,
			String string5) {
		List<DTO> cellularList = new ArrayList<DTO>();
		try {
			JSONObject rootjasonobj = new JSONObject(string);
			JSONArray arrayData = rootjasonobj.getJSONArray("products");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getinventoryvalue = arrayData.getJSONObject(i);
				ConsultarSeriesLoteriasDTO cellular1 = new ConsultarSeriesLoteriasDTO();
				cellular1.setFractions(getinventoryvalue.getString("fractions"));
				cellular1.setSerie(getinventoryvalue.getString("serie"));
				cellular1.setComparison_string(
						string3 + ":" + string2 + " " + string4 + ":" + getinventoryvalue.getString("serie") + " "
								+ string5 + ":" + getinventoryvalue.getString("fractions"));
				cellularList.add(cellular1);
			}
		} catch (Exception e) {
		}
		return cellularList;
	}

	public List<DTO> getConsultarCentrosAcopioList(String string, Context context) {
		List<DTO> acopioList = new ArrayList<DTO>();
		try {
			JSONObject rootjasonobj = new JSONObject(string);
			JSONArray arrayData = rootjasonobj.getJSONArray("centros");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getinventoryvalue = arrayData.getJSONObject(i);
				if (i == 0) {
					AcceptEfectivoDTO cellular1 = new AcceptEfectivoDTO();
					cellular1.setIdCentroAcopio("");
					cellular1.setDistribuidor(context.getResources().getString(R.string.select_operator_acopio));
					acopioList.add(cellular1);
				}
				AcceptEfectivoDTO cellular1 = new AcceptEfectivoDTO();
				cellular1.setIdCentroAcopio(getinventoryvalue.getString("id"));
				cellular1.setDistribuidor(getinventoryvalue.getString("name"));
				acopioList.add(cellular1);
			}
		} catch (Exception e) {
		}
		return acopioList;
	}

	public List<DTO> getControlDispersion(String string, Context context) {
		List<DTO> acopioList = new ArrayList<DTO>();
		try {
			JSONObject json = new JSONObject(string);
			JSONArray jsonarray = json.getJSONArray("products");
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject ojson = jsonarray.getJSONObject(i);
				ControlDispersionDTO dto = new ControlDispersionDTO();
				dto.setCentroacopioId(ojson.getString("centroacopioId"));
				dto.setDate(ojson.getString("date"));
				dto.setProgramacionId(ojson.getString("programacionId"));
				dto.setTipo(ojson.getString("tipo"));
				dto.setValor(ojson.getString("valor"));
				acopioList.add(dto);
			}
		} catch (Exception e) {
		}
		return acopioList;

	}

	public List<DTO> getTipoDiposts(String string, Context context) {
		List<DTO> tipoList = new ArrayList<DTO>();
		try {
			JSONObject rootjasonobj = new JSONObject(string);
			JSONArray arrayData = rootjasonobj.getJSONArray("products");
			for (int i = 0; i < arrayData.length(); i++) {
				JSONObject getinventoryvalue = arrayData.getJSONObject(i);

				if (i == 0) {
					AcceptEfectivoDTO cellular1 = new AcceptEfectivoDTO();
					cellular1.setIdCentroAcopio("");
					cellular1.setDistribuidor(context.getString(R.string.select_tipo_option));
					tipoList.add(cellular1);
				}
				AcceptEfectivoDTO cellular1 = new AcceptEfectivoDTO();
				cellular1.setIdCentroAcopio(getinventoryvalue.getString("id"));
				cellular1.setDistribuidor(getinventoryvalue.getString("descripcion"));
				tipoList.add(cellular1);
			}
		} catch (Exception e) {
		}
		return tipoList;
	}

	public DaviplataNumberDeDocumento getDaviDocument(String jsonres, String document_number) {

		DaviplataNumberDeDocumento documetn = new DaviplataNumberDeDocumento();
		try {
			JSONObject json = new JSONObject(jsonres);
			documetn.setApellidos(json.getString("apellidos"));
			documetn.setCedula(json.getString("cedula"));
			documetn.setCelular(json.getString("celular"));
			documetn.setCiudad(json.getString("ciudad"));
			documetn.setDireccion(json.getString("direccion"));
			documetn.setNombres(json.getString("nombres"));
			documetn.setDocumento(document_number);
			documetn.setFechaExpedicion(json.getString("fechaExpedicion"));
		} catch (Exception e) {

		}
		return documetn;

	}

	public AcceptEfectivoDTO getAcceptEfectivo(String string) {
		AcceptEfectivoDTO adto = new AcceptEfectivoDTO();
		try {

			JSONObject jsonobj = new JSONObject(string);
			adto.setIdCentroAcopio(jsonobj.getString("transactionId"));
			adto.setValor(jsonobj.getString("fecha"));
		} catch (Exception e) {
		}
		return adto;
	}

	public AcceptEfectivoDTO getDevolution(String string) {
		AcceptEfectivoDTO adto = new AcceptEfectivoDTO();
		try {
			JSONObject jsonobj = new JSONObject(string);
			adto.setIdCentroAcopio(jsonobj.getString("transactionId"));
			adto.setValor(jsonobj.getString("fecharegistro"));
		} catch (Exception e) {
		}
		return adto;
	}

	public List<DTO> getCustomerInvoiceDetails(String respondsFeed) {

		List<DTO> invoicelist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			System.out.println(respondsFeed.toString());
			JSONObject dataJSONARRAy = rootJSONObj.getJSONObject("data");
			JSONArray order_pro_jsonarray = dataJSONARRAy.getJSONArray("sales_details");
			System.out.println(order_pro_jsonarray.toString());
			for (int j = 0; j < order_pro_jsonarray.length(); j++) {
				SalesDetailDTO saleDetailsDto = new SalesDetailDTO();
				JSONObject fproductINFO = order_pro_jsonarray.getJSONObject(j);
				saleDetailsDto.setProductId(fproductINFO.getString("barcode"));
				saleDetailsDto.setCount(fproductINFO.getString("count"));
				saleDetailsDto.setUom("" + (Double.parseDouble(fproductINFO.getString("count"))
						* Double.parseDouble(fproductINFO.getString("price"))));
				invoicelist.add(saleDetailsDto);
			}
			return invoicelist;
		} catch (Exception e) {
			return invoicelist;
		}
	}

	public List<OrderReceiveDTO> getProductList(String respondsFeed) {

		ArrayList<OrderReceiveDTO> invoicelist = new ArrayList<OrderReceiveDTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("data");
			for (int j = 0; j < dataJSONARRAy.length(); j++) {
				OrderReceiveDTO dto = new OrderReceiveDTO();
				JSONObject fproductINFO = dataJSONARRAy.getJSONObject(j);
				dto.setBarcode(fproductINFO.getString("barcode"));
				dto.setCount(fproductINFO.getString("count"));
				dto.setPrice(fproductINFO.getDouble("price"));
				invoicelist.add(dto);
			}

		} catch (Exception e) {
		}
		for (int i = 0; i < invoicelist.size(); i++) {
		}
		return invoicelist;
	}

	public List<DTO> getPagoProviders(String respondsFeed, Context context, int jl) {

		ArrayList<DTO> invoicelist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("products");
			for (int j = 0; j < dataJSONARRAy.length(); j++) {
				if (j == 0) {
					PagoProviderDTO dto = new PagoProviderDTO();
					dto.setId("0");
					dto.setProvDesc(context.getResources().getString(R.string.pago_providers_pay_suppliers));
					invoicelist.add(dto);
				}

				PagoProviderDTO dto = new PagoProviderDTO();
				JSONObject fproductINFO = dataJSONARRAy.getJSONObject(j);
				dto.setId(fproductINFO.getString("id"));
				dto.setProvDesc(fproductINFO.getString("provDesc"));
				invoicelist.add(dto);
			}

		} catch (Exception e) {
		}
		return invoicelist;
	}

	public List<DTO> getPagoProducts(String respondsFeed, Context context, int jl) {

		ArrayList<DTO> invoicelist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("products");
			for (int j = 0; j < dataJSONARRAy.length(); j++) {
				if (j == 0) {
					PagoProductsDTO dto = new PagoProductsDTO();
					dto.setAttempts("0");
					dto.setAutorizer("0");
					dto.setDocRequired("0");
					dto.setEmail("0");
					dto.setOtp("0");
					dto.setProdCode("0");
					dto.setSms("0");
					dto.setTicket("0");
					dto.setValidility("0");
					dto.setProdDesc(context.getResources().getString(R.string.pago_providers_pay_product));
					dto.setValuetype("0");
					dto.setEmailTxt("0");
					invoicelist.add(dto);
				}

				PagoProductsDTO dto = new PagoProductsDTO();
				JSONObject fproductINFO = dataJSONARRAy.getJSONObject(j);
				dto.setAttempts(fproductINFO.getString("attempts"));
				dto.setAutorizer(fproductINFO.getString("autorizer"));
				dto.setDocRequired(fproductINFO.getString("docRequired"));
				dto.setEmail(fproductINFO.getString("email"));
				dto.setOtp(fproductINFO.getString("otp"));
				dto.setProdCode(fproductINFO.getString("prodCode"));
				dto.setSms(fproductINFO.getString("sms"));
				dto.setTicket(fproductINFO.getString("ticket"));
				dto.setValidility(fproductINFO.getString("validility"));
				dto.setProdDesc(fproductINFO.getString("prodDesc"));
				dto.setValuetype(fproductINFO.getString("valuetype"));
				dto.setEmailTxt(fproductINFO.getString("emailTxt"));
				invoicelist.add(dto);
			}

		} catch (Exception e) {
		}
		return invoicelist;
	}

	public List<DTO> getPagoPrograms(String respondsFeed, Context context, int jl) {

		ArrayList<DTO> invoicelist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("products");
			for (int j = 0; j < dataJSONARRAy.length(); j++) {
				if (j == 0) {
					PagoProgramsDTO dto = new PagoProgramsDTO();
					dto.setName(context.getResources().getString(R.string.pago_providers_pay_program));
					dto.setProgId("0");
					dto.setState("0");
					invoicelist.add(dto);
				}

				PagoProgramsDTO dto = new PagoProgramsDTO();
				JSONObject fproductINFO = dataJSONARRAy.getJSONObject(j);
				dto.setName(fproductINFO.getString("name"));
				dto.setProgId(fproductINFO.getString("progId"));
				dto.setState(fproductINFO.getString("state"));
				invoicelist.add(dto);
			}

		} catch (Exception e) {
		}
		return invoicelist;
	}

	public List<DTO> getpago_document_dato(String respondsFeed, Context context, int jl) {

		ArrayList<DTO> invoicelist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(respondsFeed);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("campo1");
			for (int j = 0; j < dataJSONARRAy.length(); j++) {
//				if (j == 0) {
//					PagoDocumentDTO dto = new PagoDocumentDTO();
//					dto.setName(context.getResources().getString(R.string.pago_providers_pay_document));
//					dto.setField("0");
//					dto.setFieldvalidation("0");
//					dto.setMaxlength("0");
//					dto.setMinlength("0");
//					dto.setType("0");
//					invoicelist.add(dto);
//				}
				JSONArray jsonArray = dataJSONARRAy.getJSONArray(j);

				for (int i = 0; i < jsonArray.length(); i++) {
					PagoDocumentDTO dto = new PagoDocumentDTO();
					JSONObject fproductINFO = jsonArray.getJSONObject(i);
					dto.setName(fproductINFO.getString("name"));
					dto.setField(fproductINFO.getString("field"));
					dto.setFieldvalidation(fproductINFO.getString("fieldvalidation"));
					dto.setMaxlength(fproductINFO.getString("maxlength"));
					dto.setMinlength(fproductINFO.getString("minlength"));
					dto.setType(fproductINFO.getString("type"));
					invoicelist.add(dto);
				}

			}

		} catch (Exception e) {
		}
		return invoicelist;
	}

	public List<DTO> ConsultarCiudadesdataget(String json_responds) {

		ArrayList<DTO> invoicelist = new ArrayList<DTO>();
		try {
			JSONObject rootJSONObj = new JSONObject(json_responds);
			JSONArray dataJSONARRAy = rootJSONObj.getJSONArray("products");
			for (int j = 0; j < dataJSONARRAy.length(); j++) {
				ConsultarCiudadesDTO dto = new ConsultarCiudadesDTO();
				JSONObject fproductINFO = dataJSONARRAy.getJSONObject(j);
				dto.setDepto_id(fproductINFO.getString("depto_id"));
				dto.setDescripcion(fproductINFO.getString("descripcion"));
				invoicelist.add(dto);
			}

		} catch (Exception e) {
		}
		for (int i = 0; i < invoicelist.size(); i++) {
			ConsultarCiudadesDTO dto=(ConsultarCiudadesDTO) invoicelist.get(i);
			Log.v("varahalababu", "babu============>"+dto.getDescripcion()+"=========="+dto.getDepto_id());
		}
		return invoicelist;
	}

}
