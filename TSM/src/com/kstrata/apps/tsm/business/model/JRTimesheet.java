package com.kstrata.apps.tsm.business.model;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.io.IOUtils;

public class JRTimesheet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date startTime;
	private Date endTime;
	private String startTimeString;
	private String endTimeString;
	private String taskDateString;
	private String dateFromString;
	private String dateToString;
	private BigDecimal total;
	private byte[] taskDesc;
	private byte[] comments;
	private Date taskDate;
	private Date dateFrom;
	private Date dateTo;
	private String projectName;
	private String empFirstName;
	private String empLastName;
	private String empDesignation;
	private String clientName;
	private String ename;
	private String taskDescription;
	private String taskComments;
	private String employeeRef;
	
	private boolean showClient;
	/**
	 * @return the dateFrom
	 */
	public Date getDateFrom() {
		return dateFrom;
	}
	/**
	 * @param dateFrom the dateFrom to set
	 */
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	/**
	 * @return the dateTo
	 */
	public Date getDateTo() {
		return dateTo;
	}
	/**
	 * @param dateTo the dateTo to set
	 */
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	/**
	 * @return the total
	 */
	public BigDecimal getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	/**
	 * @return the taskDesc
	 */
	public byte[] getTaskDesc() {
		return taskDesc;
	}
	/**
	 * @param taskDesc the taskDesc to set
	 */
	public void setTaskDesc(byte[] taskDesc) {
		this.taskDesc = taskDesc;
	}
	/**
	 * @return the comments
	 */
	public byte[] getComments() {
		return comments;
	}
	/**
	 * @param comments the comments to set
	 */
	public void setComments(byte[] comments) {
		this.comments = comments;
	}
	/**
	 * @return the taskDate
	 */
	public Date getTaskDate() {
		return taskDate;
	}
	/**
	 * @param taskDate the taskDate to set
	 */
	public void setTaskDate(Date taskDate) {
		this.taskDate = taskDate;
	}
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	/**
	 * @return the empFirstName
	 */
	public String getEmpFirstName() {
		return empFirstName;
	}
	/**
	 * @param empFirstName the empFirstName to set
	 */
	public void setEmpFirstName(String empFirstName) {
		this.empFirstName = empFirstName;
	}
	/**
	 * @return the empLastName
	 */
	public String getEmpLastName() {
		return empLastName;
	}
	/**
	 * @param empLastName the empLastName to set
	 */
	public void setEmpLastName(String empLastName) {
		this.empLastName = empLastName;
	}
	/**
	 * @return the empDesignation
	 */
	public String getDesignation() {
		return empDesignation;
	}
	/**
	 * @param empDesignation the empDesignation to set
	 */
	public void setDesignation(String empDesignation) {
		this.empDesignation = empDesignation;
	}
	/**
	 * @return the clientName
	 */
	public String getClientName() {
		return clientName;
	}
	/**
	 * @param clientName the clientName to set
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	/**
	 * @return the showClient
	 */
	public boolean isShowClient() {
		return showClient;
	}
	/**
	 * @param showClient the showClient to set
	 */
	public void setShowClient(boolean showClient) {
		this.showClient = showClient;
	}
	/**
	 * @return the ename
	 */
	public String getEname() {
		ename = empFirstName + " " + empLastName; 
		return ename;
	}
	/**
	 * @param ename the ename to set
	 */
	public void setEname(String ename) {
		this.ename = ename;
	}
	/**
	 * @return the taskDesription
	 */
	public String getTaskDescription() {
		if (null != taskDesc) {
			try {
				String str = IOUtils.toString(taskDesc, "UTF-8");
				taskDescription = str;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return taskDescription;
	}
	/**
	 * @param taskDesription the taskDesription to set
	 */
	public void setTaskDescription(String taskDesription) {
		this.taskDescription = taskDesription;
	}
	/**
	 * @return the taskComments
	 */
	public String getTaskComments() {
		if (null != comments) {
			try {
				String str = IOUtils.toString(comments, "UTF-8");
				taskComments = str;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return taskComments;
	}
	/**
	 * @param taskComments the taskComments to set
	 */
	public void setTaskComments(String taskComments) {
		this.taskComments = taskComments;
	}
	/**
	 * @return the startTimeString
	 */
	public String getStartTimeString() {
		return startTimeString;
	}
	/**
	 * @param startTimeString the startTimeString to set
	 */
	public void setStartTimeString(String startTimeString) {
		this.startTimeString = startTimeString;
	}
	/**
	 * @return the endTimeString
	 */
	public String getEndTimeString() {
		return endTimeString;
	}
	/**
	 * @param endTimeString the endTimeString to set
	 */
	public void setEndTimeString(String endTimeString) {
		this.endTimeString = endTimeString;
	}
	/**
	 * @return the taskDateString
	 */
	public String getTaskDateString() {
		return taskDateString;
	}
	/**
	 * @param taskDateString the taskDateString to set
	 */
	public void setTaskDateString(String taskDateString) {
		this.taskDateString = taskDateString;
	}
	/**
	 * @return the dateFromString
	 */
	public String getDateFromString() {
		return dateFromString;
	}
	/**
	 * @param dateFromString the dateFromString to set
	 */
	public void setDateFromString(String dateFromString) {
		this.dateFromString = dateFromString;
	}
	/**
	 * @return the dateToString
	 */
	public String getDateToString() {
		return dateToString;
	}
	/**
	 * @param dateToString the dateToString to set
	 */
	public void setDateToString(String dateToString) {
		this.dateToString = dateToString;
	}
	/**
	 * @return the empDesignation
	 */
	public String getEmpDesignation() {
		return empDesignation;
	}
	/**
	 * @param empDesignation the empDesignation to set
	 */
	public void setEmpDesignation(String empDesignation) {
		this.empDesignation = empDesignation;
	}
	/**
	 * @return the employeeRef
	 */
	public String getEmployeeRef() {
		return employeeRef;
	}
	/**
	 * @param employeeRef the employeeRef to set
	 */
	public void setEmployeeRef(String employeeRef) {
		this.employeeRef = employeeRef;
	}

}
