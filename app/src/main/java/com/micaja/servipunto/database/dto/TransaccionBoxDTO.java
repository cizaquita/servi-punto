package com.micaja.servipunto.database.dto;

public class TransaccionBoxDTO implements DTO {

	private String transaccion_box_id;

	public String getTransaccion_box_id() {
		return transaccion_box_id;
	}

	public void setTransaccion_box_id(String transaccion_box_id) {
		this.transaccion_box_id = transaccion_box_id;
	}

	private String store_code;
	private String username;
	private String tipo_transction;
	private String module_name;
	private String transaction_type;
	private String amount;
	private String datetime;
	private Integer syncstatus;

	public String getStore_code() {
		return store_code;
	}

	public void setStore_code(String store_code) {
		this.store_code = store_code;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTipo_transction() {
		return tipo_transction;
	}

	public void setTipo_transction(String tipo_transction) {
		this.tipo_transction = tipo_transction;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public String getTransaction_type() {
		return transaction_type;
	}

	public void setTransaction_type(String transaction_type) {
		this.transaction_type = transaction_type;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public Integer getSyncstatus() {
		return syncstatus;
	}

	public void setSyncstatus(Integer syncstatus) {
		this.syncstatus = syncstatus;
	}
}
