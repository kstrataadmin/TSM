package com.kstrata.apps.tsm.business.dao.entity;

import java.util.HashSet;
import java.util.Set;


/**
 * Role entity. @author MyEclipse Persistence Tools
 */
@SuppressWarnings("rawtypes")
public class Role implements java.io.Serializable {

	// Constructors

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer roleId;
	private String roleName;
	private Set employees = new HashSet(0);
	
	/** default constructor */
	public Role() {
	}

	/** minimal constructor */
	public Role(String roleName) {
		this.roleName = roleName;
	}

	/** full constructor */
	public Role(String roleName, Set employees) {
		this.roleName = roleName;
		this.employees = employees;
	}
	public Integer getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Set getEmployees() {
		return this.employees;
	}

	public void setEmployees(Set employees) {
		this.employees = employees;
	}
}
