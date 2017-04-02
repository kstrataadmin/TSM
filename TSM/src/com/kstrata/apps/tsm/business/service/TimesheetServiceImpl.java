package com.kstrata.apps.tsm.business.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kstrata.apps.tsm.business.dao.TimesheetDAO;
import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Period;
import com.kstrata.apps.tsm.business.dao.entity.Timesheet;

@Component
public class TimesheetServiceImpl implements TimesheetService, Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3231974870116398117L;
	
	@Autowired
	private TimesheetDAO timesheetDAO;

	@Transactional(readOnly=false)
	public Timesheet save(Timesheet transientInstance) {
		return timesheetDAO.save(transientInstance);
	}

	@Transactional(readOnly=false)
	public void approve(List<EmployeeProject> employeeProjects, Period period) {
		timesheetDAO.approve(employeeProjects, period);
	}

	@Transactional(readOnly=false)
	public void deny(List<EmployeeProject> employeeProjects, Period period) {
		timesheetDAO.deny(employeeProjects, period);
	}
	
	@Override
	@Transactional(readOnly=false)
	public void approvePeriod(Employee employee, Period otherPeriod) {
		timesheetDAO.approvePeriod(employee, otherPeriod);
	}

	@Override
	@Transactional(readOnly=false)
	public void denyPeriod(Employee employee, Period otherPeriod) {
		timesheetDAO.denyPeriod(employee, otherPeriod);
	}

	@Transactional(readOnly=false)
	public void delete(Timesheet persistentInstance) {
		timesheetDAO.delete(persistentInstance);
	}

	@Transactional(readOnly=true)
	public Timesheet findById(Integer id) {
		return timesheetDAO.findById(id);
	}

	@Transactional(readOnly=true)
	public List<Timesheet> findTimesheets(List<EmployeeProject> employeeProjects) {
		return timesheetDAO.findTimesheets(employeeProjects);
	}

	@Transactional(readOnly=true)
	public List<Timesheet> getTimesheetsByDateRange(
			List<Integer> employeeProjectIds, Date startDate, Date endDate) {
		return timesheetDAO.getTimesheetsByDateRange(employeeProjectIds,
				startDate, endDate);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByExample(Timesheet instance) {
		return timesheetDAO.findByExample(instance);
	}

	@Transactional(readOnly=true)
	public List<Timesheet> findTimesheets(List<EmployeeProject> employeeProjects, 
			Integer periodId, String approveStatus, String taskType) {
		return timesheetDAO.findTimesheets(employeeProjects, periodId, approveStatus, taskType);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List<Timesheet> convertToTimesheetList(List list) {
		return timesheetDAO.convertToTimesheetList(list);
	}
	@Transactional(readOnly=true)

	public List<Timesheet> findTimesheets(EmployeeProject employeeProjecttemp) {
		return timesheetDAO.findTimesheets(employeeProjecttemp);
	}
	
	@Transactional(readOnly=true)
	public List<Timesheet> findTimesheetsForOverTime(EmployeeProject employeeProjecttemp) {
		return timesheetDAO.findTimesheetsForOverTime(employeeProjecttemp);
	}

	@Transactional(readOnly=true)
	public List<Timesheet> findTimesheets(EmployeeProject employeeProject,
			Period period) {
		return timesheetDAO.findTimesheets(employeeProject, period);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByProperty(String propertyName, Object value) {
		return timesheetDAO.findByProperty(propertyName, value);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByTaskDesc(Object taskDesc) {
		return timesheetDAO.findByTaskDesc(taskDesc);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByTotal(Object total) {
		return timesheetDAO.findByTotal(total);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByStatus(Object status) {
		return timesheetDAO.findByStatus(status);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByMngrStatus(Object mngrStatus) {
		return timesheetDAO.findByMngrStatus(mngrStatus);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findAll() {
		return timesheetDAO.findAll();
	}
	
	@Override
	@Transactional(readOnly=true)
	public Map<String, Integer> getCountOfTimesheetsByPeriod(Period period, Employee employee) {
		return timesheetDAO.getCountOfTimesheetsByPeriod(period, employee);
	}
	
	@Override
	@Transactional(readOnly=true)
	public boolean getSubmitFlagOfTimesheetByPeriod(Period period, Employee employee) {
		return timesheetDAO.getSubmitFlagOfTimesheetByPeriod(period, employee);
	}
	
	@Override
	@Transactional(readOnly=false)
	public void submitAllTaskInPeriod(Period otherPeriod, Employee employee) {
		timesheetDAO.submitAllTaskInPeriod(otherPeriod, employee);
	}

	@Override
	@Transactional(readOnly=false)
	public void unSubmitAllTaskInPeriod(Period otherPeriod, Employee employee) {
		timesheetDAO.unSubmitAllTaskInPeriod(otherPeriod, employee);
	}
	
	@Override
	@Transactional(readOnly=false)
	public void unSubmitAllTaskInPeriod(Period period, List<EmployeeProject> employeeProjects) {
		timesheetDAO.unSubmitAllTaskInPeriod(period, employeeProjects);
	}
	
	@Override
	@Transactional(readOnly=false)
	public void submitAllTaskInPeriod(Period period, List<EmployeeProject> employeeProjects) {
		timesheetDAO.submitAllTaskInPeriod(period, employeeProjects);
	}

	@Transactional(readOnly=false)
	public Timesheet merge(Timesheet detachedInstance) {
		return timesheetDAO.merge(detachedInstance);
	}

	@Transactional(readOnly=false)
	public void attachDirty(Timesheet instance) {
		timesheetDAO.attachDirty(instance);
	}

	@Transactional(readOnly=false)
	public void attachClean(Timesheet instance) {
		timesheetDAO.attachClean(instance);
	}

	public void setTimesheetDAO(TimesheetDAO timesheetDAO) {
		this.timesheetDAO = timesheetDAO;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Timesheet> findTimesheets(Integer projectId, Date taskDate) {
		return timesheetDAO.findTimesheets(projectId, taskDate);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Timesheet> findTimesheetsForOverTime(EmployeeProject employeeProject, Date taskDate) {
		return timesheetDAO.findTimesheetsForOverTime(employeeProject, taskDate);
	}

}
