package com.kstrata.apps.tsm.business.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kstrata.apps.tsm.business.dao.entity.Period;
import com.kstrata.apps.tsm.business.dao.hibernate.BaseDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * Period entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.kstrata.apps.tsm.business.dao.entity.Period
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings({"rawtypes"})
public class PeriodDAOImpl extends BaseDao implements PeriodDAO, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(PeriodDAOImpl.class);
	// property constants

	public void save(Period transientInstance) {
		log.debug("saving Period instance");
		try {
			super.getSession().saveOrUpdate(transientInstance);
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Period persistentInstance) {
		log.debug("deleting Period instance");
		try {
			super.getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Period findById(java.lang.Integer id) {
		log.debug("getting Period instance with id: " + id);
		try {
			Period instance = (Period) super.getSession().get("com.kstrata.apps.tsm.business.dao.entity.Period", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(Period instance) {
		log.debug("finding Period instance by example");
		try {
			List results = super.getSession().createCriteria("com.kstrata.apps.tsm.business.dao.entity.Period")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findAll() {
		log.debug("finding all Period instances");
		try {
			String queryString = "from Period";
			Query queryObject = super.getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Period merge(Period detachedInstance) {
		log.debug("merging Period instance");
		try {
			Period result = (Period) super.getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Period instance) {
		log.debug("attaching dirty Period instance");
		try {
			super.getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Period instance) {
		log.debug("attaching clean Period instance");
		try {
			super.getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Period instance with property: " + propertyName + ", value: " + value);
		try {
			String queryString = "from Period as model where model." + propertyName + "= ?";
			Query queryObject = super.getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public Period getPeriod(Date date) {
		Period period = new Period();
		log.debug("finding period for a given date");
		try {
			Query query = super.getSession().createSQLQuery("select * FROM Period WHERE :date BETWEEN DATE_FROM AND DATE_TO");
			query.setParameter("date", date);
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			List list = query.list();
			Map map = (Map) list.get(0);
			
			period.setDateFrom((Date)map.get("DATE_FROM"));
			period.setDateTo((Date)map.get("DATE_TO"));
			period.setPeriodId((Integer)map.get("PERIOD_ID"));
			
		} catch (RuntimeException re) {
			log.error("finding a period failed", re);
			throw re;
		}
		return period;
	}
	
	public List<Period> getPeriodsForDate(Date date) {
		List<Period> periodList = new ArrayList<Period>();
		log.debug("finding period for a given date");
		try {
			Calendar calendar = Calendar.getInstance();
			Query query = super.getSession().createSQLQuery("select * FROM Period WHERE DATE_FROM >=:date AND DATE_FROM <= :currentDate order by PERIOD_ID desc ");
			query.setParameter("date", date);
			query.setParameter("currentDate", calendar.getTime());
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			List list = query.list();
			for (int i = 0; i < list.size(); i++) {
				Period period = new Period();
				Map map = (Map) list.get(i);
				period.setDateFrom((Date)map.get("DATE_FROM"));
				period.setDateTo((Date)map.get("DATE_TO"));
				period.setPeriodId((Integer)map.get("PERIOD_ID"));
				periodList.add(period);
			}
		} catch (RuntimeException re) {
			log.error("finding a period failed", re);
			throw re;
		}
		return periodList;
	}

	public Period getPeriod(Integer periodId) {
		Period period = new Period();
		try {
			Query query = super.getSession().createSQLQuery("select * FROM Period WHERE PERIOD_ID="+periodId);
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			List list = query.list();
			Map map = (Map) list.get(0);
			period.setDateFrom((Date)map.get("DATE_FROM"));
			period.setDateTo((Date)map.get("DATE_TO"));
			period.setPeriodId((Integer)map.get("PERIOD_ID"));
			
		} catch (RuntimeException re) {
			log.error("finding a period failed", re);
			throw re;
		}
		return period;
	}
	
	@Override
	public Integer getCountOfHolidaysByPeriod(Period period) {
		Query query = super.getSession().createSQLQuery("SELECT COUNT(H.HOLIDAY_ID) FROM HOLIDAYS H, PERIOD P WHERE " +
			"H.HOLIDAY_DATE >= P.DATE_FROM AND H.HOLIDAY_DATE <= P.DATE_TO " +
			"AND P.PERIOD_ID=" + period.getPeriodId());
		return ((Long) query.list().get(0)).intValue() + 2;
	}

}