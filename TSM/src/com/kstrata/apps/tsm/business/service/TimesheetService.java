package com.kstrata.apps.tsm.business.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Period;
import com.kstrata.apps.tsm.business.dao.entity.Timesheet;

@SuppressWarnings("rawtypes")
public interface TimesheetService {

	public Timesheet save(Timesheet transientInstance);

	public void approve(List<EmployeeProject> employeeProjects, Period period);

	public void deny(List<EmployeeProject> employeeProjects, Period period);

	public void delete(Timesheet persistentInstance);

	public Timesheet findById(Integer id);

	public List<Timesheet> findTimesheets(List<EmployeeProject> employeeProjects);
	
	public List<Timesheet> findTimesheetsForOverTime(EmployeeProject employeeProjects);

	public List<Timesheet> getTimesheetsByDateRange(
			List<Integer> employeeProjectIds, Date startDate, Date endDate);

	public List findByExample(Timesheet instance);

	public List<Timesheet> findTimesheets(
			List<EmployeeProject> employeeProjects, Integer periodId,
			String approveStatus, String radioType);

	public List<Timesheet> convertToTimesheetList(List list);

	public List<Timesheet> findTimesheets(EmployeeProject employeeProjecttemp);

	public List<Timesheet> findTimesheets(EmployeeProject employeeProject,
			Period period);

	public List findByProperty(String propertyName, Object value);

	public List findByTaskDesc(Object taskDesc);

	public List findByTotal(Object total);

	public List findByStatus(Object status);

	public List findByMngrStatus(Object mngrStatus);

	public List findAll();

	public Timesheet merge(Timesheet detachedInstance);

	public void attachDirty(Timesheet instance);

	public void attachClean(Timesheet instance);

	public List<Timesheet> findTimesheets(Integer projectId, Date taskDate);

	public List<Timesheet> findTimesheetsForOverTime(EmployeeProject employeeProject, Date taskDate);

	public Map<String, Integer> getCountOfTimesheetsByPeriod(Period period, Employee employee);

	public boolean getSubmitFlagOfTimesheetByPeriod(Period period, Employee employee);

	public void submitAllTaskInPeriod(Period otherPeriod, Employee employee);

	public void unSubmitAllTaskInPeriod(Period otherPeriod, Employee employee);

	public void unSubmitAllTaskInPeriod(Period period, List<EmployeeProject> employeeProjects);

	public void submitAllTaskInPeriod(Period period, List<EmployeeProject> employeeProjects);

	public void approvePeriod(Employee employee, Period otherPeriod);

	public void denyPeriod(Employee employee, Period otherPeriod);
}
