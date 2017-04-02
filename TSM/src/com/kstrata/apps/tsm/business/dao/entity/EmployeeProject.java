package com.kstrata.apps.tsm.business.dao.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * EmployeeProject entity. @author MyEclipse Persistence Tools
 */
@SuppressWarnings("rawtypes")
public class EmployeeProject implements
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer empProjId;
	private String active;
	private Project project;
	private Employee employee;
	private List<Timesheet> timesheets = new ArrayList<Timesheet>();
	
	private Set<Timesheet> timesheetsSet = new HashSet<Timesheet>(0);
	
	// Constructors

	/** default constructor */
	public EmployeeProject() {
	}

	/** minimal constructor */
	public EmployeeProject(Project project, Employee employee) {
		this.project = project;
		this.employee = employee;
	}

	/** full constructor */
	public EmployeeProject(Project project, Employee employee, Set<Timesheet> timesheetsSet) {
		this.project = project;
		this.employee = employee;
		this.timesheetsSet = timesheetsSet;
	}
	
	public Integer getEmpProjId() {
		return this.empProjId;
	}

	public void setEmpProjId(Integer empProjId) {
		this.empProjId = empProjId;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Set getTimesheetsSet() {
		return this.timesheetsSet;
	}

	public void setTimesheetsSet(Set<Timesheet> timesheetsSet) {
		this.timesheetsSet = timesheetsSet;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public List<Timesheet> getTimesheets() {
		return timesheets;
	}

	public void setTimesheets(List<Timesheet> timesheets) {
		this.timesheets = timesheets;
	}
}
