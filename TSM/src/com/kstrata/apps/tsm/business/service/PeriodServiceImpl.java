package com.kstrata.apps.tsm.business.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kstrata.apps.tsm.business.dao.PeriodDAO;
import com.kstrata.apps.tsm.business.dao.entity.Period;

@Component
public class PeriodServiceImpl implements PeriodService, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 719829157072076426L;
	
	@Autowired
	private PeriodDAO periodDAO;

	@Transactional(readOnly=false)
	public void save(Period transientInstance) {
		periodDAO.save(transientInstance);
	}

	@Transactional(readOnly=false)
	public void delete(Period persistentInstance) {
		periodDAO.delete(persistentInstance);
	}

	@Transactional(readOnly=true)
	public Period findById(Integer id) {
		return periodDAO.findById(id);
	}

	@Transactional(readOnly=true)
	@SuppressWarnings("rawtypes")
	public List findByExample(Period instance) {
		return periodDAO.findByExample(instance);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findAll() {
		return periodDAO.findAll();
	}

	@Transactional(readOnly=false)
	public Period merge(Period detachedInstance) {
		return periodDAO.merge(detachedInstance);
	}

	@Transactional(readOnly=false)
	public void attachDirty(Period instance) {
		periodDAO.attachDirty(instance);
	}

	@Transactional(readOnly=false)
	public void attachClean(Period instance) {
		periodDAO.attachClean(instance);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByProperty(String propertyName, Object value) {
		return periodDAO.findByProperty(propertyName, value);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Integer getCountOfHolidaysByPeriod(Period period) {
		return periodDAO.getCountOfHolidaysByPeriod(period);
	}

	@Transactional(readOnly=true)
	public Period getPeriod(Date date) {
		return periodDAO.getPeriod(date);
	}

	@Transactional(readOnly=true)
	public List<Period> getPeriodsForDate(Date date) {
		return periodDAO.getPeriodsForDate(date);
	}

	@Transactional(readOnly=true)
	public Period getPeriod(Integer periodId) {
		return periodDAO.getPeriod(periodId);
	}

	public void setPeriodDAO(PeriodDAO periodDAO) {
		this.periodDAO = periodDAO;
	}

}