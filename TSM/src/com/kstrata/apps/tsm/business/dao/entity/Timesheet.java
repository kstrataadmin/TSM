package com.kstrata.apps.tsm.business.dao.entity;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Timesheet entity. @author MyEclipse Persistence Tools
 */
public class Timesheet implements java.io.Serializable {

	// Constructors

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer timesheetId;
	private Period period;
	private EmployeeProject employeeProject;
	private byte[] taskDesc;
	private byte[] comments;
	private Date startTime;
	private Date endTime;
	private Date taskDate;
	private BigDecimal total;
	private String status;
	private String mngrStatus;
	private String taskType;
	private String submitFlag;
	
	/** default constructor */
	public Timesheet() {
	}

	/** full constructor */
	public Timesheet(Period period, Integer timesheetId,
			EmployeeProject employeeProject, byte[] taskDesc, byte[] comments,
			Date startTime, Date endTime, Date taskDate,
			BigDecimal total, String status, String mngrStatus, String taskType) {
		this.period = period;
		this.timesheetId = timesheetId;
		this.employeeProject = employeeProject;
		this.taskDesc = taskDesc;
		this.startTime = startTime;
		this.endTime = endTime;
		this.taskDate = taskDate;
		this.total = total;
		this.status = status;
		this.mngrStatus = mngrStatus;
		this.comments = comments;
		this.taskType = taskType;
	}
	
	
	
	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public Period getPeriod() {
		return this.period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public EmployeeProject getEmployeeProject() {
		return this.employeeProject;
	}

	public void setEmployeeProject(EmployeeProject employeeProject) {
		this.employeeProject = employeeProject;
	}

	public byte[] getTaskDesc() {
		return this.taskDesc;
	}

	public void setTaskDesc(byte[] taskDesc) {
		this.taskDesc = taskDesc;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getTaskDate() {
		return this.taskDate;
	}

	public void setTaskDate(Date taskDate) {
		this.taskDate = taskDate;
	}

	public BigDecimal getTotal() {
		return this.total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMngrStatus() {
		return this.mngrStatus;
	}

	public void setMngrStatus(String mngrStatus) {
		this.mngrStatus = mngrStatus;
	}

	public Integer getTimesheetId() {
		return timesheetId;
	}

	public void setTimesheetId(Integer timesheetId) {
		this.timesheetId = timesheetId;
	}

	public byte[] getComments() {
		return comments;
	}

	public void setComments(byte[] comments) {
		this.comments = comments;
	}

	public String getSubmitFlag() {
		return submitFlag;
	}

	public void setSubmitFlag(String submitFlag) {
		this.submitFlag = submitFlag;
	}
}
