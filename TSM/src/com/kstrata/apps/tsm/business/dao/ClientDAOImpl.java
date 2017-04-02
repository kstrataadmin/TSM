package com.kstrata.apps.tsm.business.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.hibernate.BaseDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * Client entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.kstrata.apps.tsm.business.dao.entity.Client
 * @author MyEclipse Persistence Tools
 */

@SuppressWarnings({"rawtypes"})
public class ClientDAOImpl extends BaseDao implements ClientDAO, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ClientDAOImpl.class);
	// property constants
	public static final String CLIENT_NAME = "clientName";

	public void save(Client transientInstance) {
		log.debug("saving Client instance");
		try {
			super.getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Client persistentInstance) {
		log.debug("deleting Client instance");
		try {
			super.getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Client findById(java.lang.Integer id) {
		log.debug("getting Client instance with id: " + id);
		try {
			SQLQuery query = super.getSession().createSQLQuery("select * from Client client where client.CLIENT_ID=:id");
			query.setParameter("id", id);
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			List list = query.list();
			Map map = (Map) list.get(0);
			Client client = new Client();
			client.setClientId((Integer) map.get("CLIENT_ID"));
			client.setClientName((String) map.get("CLIENT_NAME"));
			client.setShortName((String)map.get("SHORTNAME"));
			client.setGraceMinutes((Integer)map.get("GRACE_MINUTES"));
			client.setLunchMinutes((Integer)map.get("LUNCH_MINUTES"));
			client.setLunchGraceFlag((Integer)map.get("LUNCHGRACE_FLAG"));
			
			return client;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(Client instance) {
		log.debug("finding Client instance by example");
		try {
			List results = super.getSession().createCriteria("com.kstrata.apps.tsm.business.dao.entity.Client").add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Client instance with property: " + propertyName + ", value: " + value);
		try {
			String queryString = "from Client as model where model." + propertyName + "= ?";
			Query queryObject = super.getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByClientName(Object clientName) {
		return findByProperty(CLIENT_NAME, clientName);
	}

	public List findAll() {
		log.debug("finding all Client instances");
		try {
			String queryString = "from Client";
			Query queryObject = super.getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Client merge(Client detachedInstance) {
		log.debug("merging Client instance");
		try {
			Client result = (Client) super.getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Client instance) {
		log.debug("attaching dirty Client instance");
		try {
			super.getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Client instance) {
		log.debug("attaching clean Client instance");
		try {
			super.getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}