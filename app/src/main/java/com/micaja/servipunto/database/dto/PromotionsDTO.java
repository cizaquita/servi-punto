package com.micaja.servipunto.database.dto;

public class PromotionsDTO implements DTO {

	private String image_url;
	private String name;
	private String video_url;
	private String store_code;
	private String supplier_code;
	private String supplier_name;
	private String value;
	private String where_to_show;
	private String end_date;
	private String start_date;
	private String promotion_des;
	private long promoid;
	private long order_promotion;
	
	public long getOrder_promotion() {
		return order_promotion;
	}
	public void setOrder_promotion(long order_promotion) {
		this.order_promotion = order_promotion;
	}
	
	public String getWhere_to_show() {
		return where_to_show;
	}
	public void setWhere_to_show(String where_to_show) {
		this.where_to_show = where_to_show;
	}

	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getSupplier_name() {
		return supplier_name;
	}
	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}
	public long getPromoid() {
		return promoid;
	}
	public void setPromoid(long promoid) {
		this.promoid = promoid;
	}
	public String getPromotion_des() {
		return promotion_des;
	}
	public void setPromotion_des(String promotion_des) {
		this.promotion_des = promotion_des;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	
	
	
	
	
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVideo_url() {
		return video_url;
	}
	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}
	public String getStore_code() {
		return store_code;
	}
	public void setStore_code(String store_code) {
		this.store_code = store_code;
	}
	public String getSupplier_code() {
		return supplier_code;
	}
	public void setSupplier_code(String supplier_code) {
		this.supplier_code = supplier_code;
	}
	

}
