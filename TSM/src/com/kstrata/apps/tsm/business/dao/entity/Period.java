package com.kstrata.apps.tsm.business.dao.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * Period entity. @author MyEclipse Persistence Tools
 */
@SuppressWarnings("rawtypes")
public class Period implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy");
		return dateFormat.format(dateFrom) + " - " + dateFormat.format(dateTo);
	}

	private Integer periodId;
	private Date dateFrom;
	private Date dateTo;
	private Set timesheets = new HashSet(0);
	// Constructors

	/** default constructor */
	public Period() {
	}

	/** minimal constructor */
	public Period(Date dateFrom, Date dateTo) {
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	/** full constructor */
	public Period(Date dateFrom, Date dateTo,
			Set timesheets) {
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.timesheets = timesheets;
	}
	
	public Integer getPeriodId() {
		return this.periodId;
	}

	public void setPeriodId(Integer periodId) {
		this.periodId = periodId;
	}

	public Date getDateFrom() {
		return this.dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return this.dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Set getTimesheets() {
		return this.timesheets;
	}

	public void setTimesheets(Set timesheets) {
		this.timesheets = timesheets;
	}

}
