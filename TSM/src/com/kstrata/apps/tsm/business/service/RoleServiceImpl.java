package com.kstrata.apps.tsm.business.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kstrata.apps.tsm.business.dao.RoleDAO;
import com.kstrata.apps.tsm.business.dao.entity.Role;

@Component
public class RoleServiceImpl implements RoleService, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4985758378980252417L;
	@Autowired
	private RoleDAO roleDAO;

	@Transactional(readOnly=false)
	public void save(Role transientInstance) {
		roleDAO.save(transientInstance);
	}

	@Transactional(readOnly=false)
	public void delete(Role persistentInstance) {
		roleDAO.delete(persistentInstance);
	}

	@Transactional(readOnly=true)
	public Role findById(Integer id) {
		return roleDAO.findById(id);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByExample(Role instance) {
		return roleDAO.findByExample(instance);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByProperty(String propertyName, Object value) {
		return roleDAO.findByProperty(propertyName, value);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByRoleName(Object roleName) {
		return roleDAO.findByRoleName(roleName);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findAll() {
		return roleDAO.findAll();
	}

	@Transactional(readOnly=false)
	public Role merge(Role detachedInstance) {
		return roleDAO.merge(detachedInstance);
	}

	@Transactional(readOnly=false)
	public void attachDirty(Role instance) {
		roleDAO.attachClean(instance);
	}

	@Transactional(readOnly=false)
	public void attachClean(Role instance) {
		roleDAO.attachClean(instance);
	}

	public void setRoleDAO(RoleDAO roleDAO) {
		this.roleDAO = roleDAO;
	}

}
