package com.kstrata.apps.tsm.business.dao.hibernate;

import java.io.Serializable;

import com.kstrata.apps.tsm.business.dao.util.HibernateSessionFactory;
import org.hibernate.Session;


/**
 * Data access object (DAO) for domain model
 * @author MyEclipse Persistence Tools
 */
public class BaseHibernateDAO implements IBaseHibernateDAO, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Session getSession() {
		return HibernateSessionFactory.getSession();
	}
}