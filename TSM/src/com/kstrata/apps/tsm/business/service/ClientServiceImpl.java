package com.kstrata.apps.tsm.business.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kstrata.apps.tsm.business.dao.ClientDAO;
import com.kstrata.apps.tsm.business.dao.entity.Client;

@Component
public class ClientServiceImpl implements ClientService, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7336800011849801949L;
	
	@Autowired
	private ClientDAO clientDAO;

	@Transactional(readOnly=false)
	public void save(Client transientInstance) {
		clientDAO.save(transientInstance);
	}

	@Transactional(readOnly=false)
	public void delete(Client persistentInstance) {
		clientDAO.delete(persistentInstance);
	}

	@Transactional(readOnly=true)
	public Client findById(Integer id) {
		return clientDAO.findById(id);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByExample(Client instance) {
		return clientDAO.findByExample(instance);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByProperty(String propertyName, Object value) {
		return clientDAO.findByProperty(propertyName, value);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByClientName(Object clientName) {
		return clientDAO.findByClientName(clientName);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findAll() {
		return clientDAO.findAll();
	}

	@Transactional(readOnly=false)
	public Client merge(Client detachedInstance) {
		return clientDAO.merge(detachedInstance);
	}

	@Transactional(readOnly=false)
	public void attachDirty(Client instance) {
		clientDAO.attachDirty(instance);
	}

	@Transactional(readOnly=false)
	public void attachClean(Client instance) {
		clientDAO.attachClean(instance);
	}

	public void setClientDAO(ClientDAO clientDAO) {
		this.clientDAO = clientDAO;
	}

}
