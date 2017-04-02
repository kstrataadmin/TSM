package com.kstrata.apps.tsm.business.service;

import java.util.List;

import com.kstrata.apps.tsm.business.dao.entity.Role;

public interface RoleService {

	public void save(Role transientInstance);

	public void delete(Role persistentInstance);

	public Role findById(java.lang.Integer id);

	public List findByExample(Role instance);

	public List findByProperty(String propertyName, Object value);

	public List findByRoleName(Object roleName);

	public List findAll();

	public Role merge(Role detachedInstance);

	public void attachDirty(Role instance);

	public void attachClean(Role instance);

}
