package com.kstrata.apps.tsm.business.service;

import java.util.Date;
import java.util.List;

import com.kstrata.apps.tsm.business.dao.entity.Period;

public interface PeriodService {

	public void save(Period transientInstance);

	public void delete(Period persistentInstance);

	public Period findById(java.lang.Integer id);

	public List findByExample(Period instance);

	public List findAll();

	public Period merge(Period detachedInstance);

	public void attachDirty(Period instance);

	public void attachClean(Period instance);

	public List findByProperty(String propertyName, Object value);

	public Period getPeriod(Date date);

	public List<Period> getPeriodsForDate(Date date);

	public Period getPeriod(Integer periodId);

	public Integer getCountOfHolidaysByPeriod(Period period);

}
