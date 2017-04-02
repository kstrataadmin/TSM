package com.kstrata.apps.tsm.business.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Lob;


/**
 * Employee entity. @author MyEclipse Persistence Tools
 */
@SuppressWarnings("rawtypes")
public class Employee implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer empId;
	private Role role;
	private String empFirstName;
	private String empLastName;
	private String empDesignation;
	private String empEmailid;
	private String empUsername;
	@Lob @Basic
	private byte[] empPassword;
	private String active;
	private String passwordChangeFlag;
	private String employeeRef;
	
	private String projectName;
	private boolean passwordChangeFlagBoolean;
	private String reminderEnable;
	
	private String saveStatus;
	
	private Set employeeProjects = new HashSet(0);

	// Constructors

	/** default constructor */
	public Employee() {
	}

	/** minimal constructor */
	public Employee(Role role, String empFirstName, String empLastName,
			String empDesignation, String empEmailid, String empUsername,
			byte[] empPassword, String active, Integer empId, String passwordChangeFlag, String employeeRef) {
		this.role = role;
		this.empFirstName = empFirstName;
		this.empLastName = empLastName;
		this.empDesignation = empDesignation;
		this.empEmailid = empEmailid;
		this.empUsername = empUsername;
		this.empPassword = empPassword;
		this.active = active;
		this.empId = empId;
		this.passwordChangeFlag = passwordChangeFlag;
		this.employeeRef = employeeRef;
	}

	/** full constructor */
	public Employee(Role role, String empFirstName, String empLastName,
			String empDesignation, String empEmailid, String empUsername,
			byte[] empPassword, String active, Set employeeProjects) {
		this.role = role;
		this.empFirstName = empFirstName;
		this.empLastName = empLastName;
		this.empDesignation = empDesignation;
		this.empEmailid = empEmailid;
		this.empUsername = empUsername;
		this.empPassword = empPassword;
		this.active = active;
		this.employeeProjects = employeeProjects;
	}
	
	public Integer getEmpId() {
		return this.empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getEmpFirstName() {
		return this.empFirstName;
	}

	public void setEmpFirstName(String empFirstName) {
		this.empFirstName = empFirstName;
	}

	public String getEmpLastName() {
		return this.empLastName;
	}

	public void setEmpLastName(String empLastName) {
		this.empLastName = empLastName;
	}

	public String getEmpDesignation() {
		return this.empDesignation;
	}

	public void setEmpDesignation(String empDesignation) {
		this.empDesignation = empDesignation;
	}

	public String getEmpEmailid() {
		return this.empEmailid;
	}

	public void setEmpEmailid(String empEmailid) {
		this.empEmailid = empEmailid;
	}

	public String getEmpUsername() {
		return this.empUsername;
	}

	public void setEmpUsername(String empUsername) {
		this.empUsername = empUsername;
	}

	public byte[] getEmpPassword() {
		return this.empPassword;
	}

	public void setEmpPassword(byte[] password) {
		this.empPassword = password;
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Set getEmployeeProjects() {
		return this.employeeProjects;
	}

	public void setEmployeeProjects(Set employeeProjects) {
		this.employeeProjects = employeeProjects;
	}

	public String getPasswordChangeFlag() {
		return passwordChangeFlag;
	}

	public void setPasswordChangeFlag(String passwordChangeFlag) {
		this.passwordChangeFlag = passwordChangeFlag;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public boolean isPasswordChangeFlagBoolean() {
		return passwordChangeFlagBoolean;
	}

	public void setPasswordChangeFlagBoolean(boolean passwordChangeFlagBoolean) {
		this.passwordChangeFlagBoolean = passwordChangeFlagBoolean;
	}

	public String getEmployeeRef() {
		return employeeRef;
	}

	public void setEmployeeRef(String employeeRef) {
		this.employeeRef = employeeRef;
	}

	public String getSaveStatus() {
		return saveStatus;
	}

	public void setSaveStatus(String saveStatus) {
		this.saveStatus = saveStatus;
	}

	public String getReminderEnable() {
		return reminderEnable;
	}

	public void setReminderEnable(String reminderEnable) {
		this.reminderEnable = reminderEnable;
	}
}
