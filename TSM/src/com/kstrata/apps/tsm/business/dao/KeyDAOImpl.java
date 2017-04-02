package com.kstrata.apps.tsm.business.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kstrata.apps.tsm.business.dao.entity.Key;
import com.kstrata.apps.tsm.business.dao.hibernate.BaseDao;

public class KeyDAOImpl extends BaseDao implements KeyDAO, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(KeyDAOImpl.class);
	
	public void save(Key transientInstance) {
		log.debug("saving Key instance");
		try {
			super.getSession().saveOrUpdate(transientInstance);
			super.getSession().flush();
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}
	@SuppressWarnings("unchecked")
	public List<Key> findAll() {
		log.debug("finding all Key instances");
		try {
			String queryString = "from Key";
			Query queryObject = super.getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
}
