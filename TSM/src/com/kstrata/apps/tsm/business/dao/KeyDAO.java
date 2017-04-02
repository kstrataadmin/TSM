package com.kstrata.apps.tsm.business.dao;

import java.util.List;

import com.kstrata.apps.tsm.business.dao.entity.Key;

public interface KeyDAO {

	public void save(Key transientInstance);
	public List<Key> findAll();
	
}
