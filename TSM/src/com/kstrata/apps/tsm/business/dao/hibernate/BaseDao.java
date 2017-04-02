package com.kstrata.apps.tsm.business.dao.hibernate;

import java.io.Serializable;
import java.text.MessageFormat;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Base DAO class extended by all DAO classes. Contains utility methods
 * to fetch the Hibernate session and to retrieve the session factory.
 * 
 * @author 		Ramesh Bellamkonda, Docmation Inc.
 * @version 	1.0
 *
 */
public class BaseDao implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public BaseDao() {
		super();
	}
	
	/**
	 * Utility method to retrieve the Hibernate sesssion from the 
	 * Hibernate Session Factory
	 * 
	 * @return		Returns a Hibernate session
	 */
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * Retrieve the Hibernate Session Factory.
	 * 
	 * @return
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Property method to set the Hibernate Session Factory. This is set as a Spring Bean property.
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * Util method to parse and replace the query with any dynamic arguments passed.
	 * 
	 * @param queryLookupKey		String containing the query with dynamic params
	 * @param params				Values for the dynamic parameters passed as an array
	 * @return						Returns a well formed query string
	 */
	public String getQuery(String queryLookupKey, Object[] params) {
		return MessageFormat.format(queryLookupKey, params);
	}

	private SessionFactory sessionFactory;

}
