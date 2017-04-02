package com.kstrata.apps.tsm.business.dao.entity;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * Project entity. @author MyEclipse Persistence Tools
 */
@SuppressWarnings("rawtypes")
public class Project implements java.io.Serializable {

	/**
	 * 
	 */
	public static class ProjectID implements Comparator<Project> {
        @Override
        public int compare(Project arg0, Project arg1) {
            return arg0.getProjectId() - arg1.getProjectId();
        }
    }
	private static final long serialVersionUID = 1L;
	private Client client;
	private Integer projectId;
	private String projectName;
	private String projectDesc;
	private String active;
	private Set employeeProjects = new HashSet(0);
	// Constructors

	/** default constructor */
	public Project() {
	}
	public Project(Integer projectId) {
		this.projectId = projectId;
	}

	/** minimal constructor */
	public Project(Client client, String projectName) {
		this.client = client;
		this.projectName = projectName;
	}
	public Project(Client client, String projectName, String projectDesc, String active) {
		this.client = client;
		this.projectName = projectName;
		this.projectDesc = projectDesc;
		this.active = active;
	}

	/** full constructor */
	public Project(Client client, String projectName, String projectDesc, String active,
			Set employeeProjects) {
		this.client = client;
		this.projectName = projectName;
		this.projectDesc = projectDesc;
		this.active = active;
		this.employeeProjects = employeeProjects;
	}
	public Integer getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Client getClient() {
		return this.client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectDesc() {
		return this.projectDesc;
	}

	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	public Set getEmployeeProjects() {
		return this.employeeProjects;
	}

	public void setEmployeeProjects(Set employeeProjects) {
		this.employeeProjects = employeeProjects;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}
}
