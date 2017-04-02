package com.kstrata.apps.tsm.business.dao.entity;

import java.util.HashSet;
import java.util.Set;


/**
 * Client entity. @author MyEclipse Persistence Tools
 */
@SuppressWarnings("rawtypes")
public class Client implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer clientId;
	private String clientName;
	private String shortName;
	private Integer lunchGraceFlag;
	private Integer lunchMinutes;
	private Integer graceMinutes;
	private Set projects = new HashSet(0);
	
	// Constructors

	/** default constructor */
	public Client() {
	}

	/** minimal constructor */
	public Client(String clientName) {
		this.clientName = clientName;
	}

	/** full constructor */
	public Client(String clientName, Set projects) {
		this.clientName = clientName;
		this.projects = projects;
	}
	
	// Property accessors

	public Integer getClientId() {
		return this.clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return this.clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	
	public Set getProjects() {
		return this.projects;
	}

	public void setProjects(Set projects) {
		this.projects = projects;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Integer getLunchGraceFlag() {
		return lunchGraceFlag;
	}

	public void setLunchGraceFlag(Integer lunchGraceFlag) {
		this.lunchGraceFlag = lunchGraceFlag;
	}

	public Integer getLunchMinutes() {
		return lunchMinutes;
	}

	public void setLunchMinutes(Integer lunchMinutes) {
		this.lunchMinutes = lunchMinutes;
	}

	public Integer getGraceMinutes() {
		return graceMinutes;
	}

	public void setGraceMinutes(Integer graceMinutes) {
		this.graceMinutes = graceMinutes;
	}

}
