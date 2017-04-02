package com.kstrata.apps.tsm.business.dao.entity;

import javax.persistence.Basic;
import javax.persistence.Lob;

public class Key implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	@Lob @Basic
	private byte[] encKey;
	
	public Key(){
		super();
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}



	public byte[] getEncKey() {
		return encKey;
	}
	public void setEncKey(byte[] encKey) {
		this.encKey = encKey;
	}
	public Key(Integer id, byte[] encKey) {
		super();
		this.id = id;
		this.encKey = encKey;
	}

}