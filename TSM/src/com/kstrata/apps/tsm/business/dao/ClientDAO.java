package com.kstrata.apps.tsm.business.dao;

import java.util.List;

import com.kstrata.apps.tsm.business.dao.entity.Client;

public interface ClientDAO {
	
	public void save(Client transientInstance);

	public void delete(Client persistentInstance);

	public Client findById(java.lang.Integer id);

	public List findByExample(Client instance);

	public List findByProperty(String propertyName, Object value);

	public List findByClientName(Object clientName);

	public List findAll();

	public Client merge(Client detachedInstance);

	public void attachDirty(Client instance);

	public void attachClean(Client instance);

}
