package com.kstrata.apps.tsm.business.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.kstrata.apps.tsm.business.dao.KeyDAO;
import com.kstrata.apps.tsm.business.dao.entity.Key;

public class KeyServiceImpl implements KeyService, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8771773645993956023L;
	
	private KeyDAO keyDAO;
	
	@Override
	@Transactional
	public List<Key> findAll() {
		return keyDAO.findAll();
	}

	public KeyDAO getKeyDAO() {
		return keyDAO;
	}

	public void setKeyDAO(KeyDAO keyDAO) {
		this.keyDAO = keyDAO;
	}


}
