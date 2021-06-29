package com.micaja.servipunto.database.dto;

public class SincronizarTransaccionesDTO implements DTO {

	private String rowid;
	private String module;
	private String tipo_transaction;
	private String authorization_number;

	private String id_pdb_servitienda;
	private String transaction_datetime;
	private String transaction_value;
	private String status;
	private String creation_date;

	private String created_by;
	private String modified_date;

	private String modified_by;
	private String servitienda_synck_status; 
	private String punthored_synck_status,module_tipo_id;
	
	public String getModule_tipo_id() {
		return module_tipo_id;
	}
	public void setModule_tipo_id(String module_tipo_id) {
		this.module_tipo_id = module_tipo_id;
	}
	public String getRowid() {
		return rowid;
	}
	public void setRowid(String rowid) {
		this.rowid = rowid;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getTipo_transaction() {
		return tipo_transaction;
	}
	public void setTipo_transaction(String tipo_transaction) {
		this.tipo_transaction = tipo_transaction;
	}
	public String getAuthorization_number() {
		return authorization_number;
	}
	public void setAuthorization_number(String authorization_number) {
		this.authorization_number = authorization_number;
	}
	public String getId_pdb_servitienda() {
		return id_pdb_servitienda;
	}
	public void setId_pdb_servitienda(String id_pdb_servitienda) {
		this.id_pdb_servitienda = id_pdb_servitienda;
	}
	public String getTransaction_datetime() {
		return transaction_datetime;
	}
	public void setTransaction_datetime(String transaction_datetime) {
		this.transaction_datetime = transaction_datetime;
	}
	public String getTransaction_value() {
		return transaction_value;
	}
	public void setTransaction_value(String transaction_value) {
		this.transaction_value = transaction_value;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreation_date() {
		return creation_date;
	}
	public void setCreation_date(String creation_date) {
		this.creation_date = creation_date;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getModified_date() {
		return modified_date;
	}
	public void setModified_date(String modified_date) {
		this.modified_date = modified_date;
	}
	public String getModified_by() {
		return modified_by;
	}
	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}
	public String getServitienda_synck_status() {
		return servitienda_synck_status;
	}
	public void setServitienda_synck_status(String servitienda_synck_status) {
		this.servitienda_synck_status = servitienda_synck_status;
	}
	public String getPunthored_synck_status() {
		return punthored_synck_status;
	}
	public void setPunthored_synck_status(String punthored_synck_status) {
		this.punthored_synck_status = punthored_synck_status;
	}
	

}
