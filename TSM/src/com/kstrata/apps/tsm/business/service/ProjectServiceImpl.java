package com.kstrata.apps.tsm.business.service;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kstrata.apps.tsm.business.dao.ProjectDAO;
import com.kstrata.apps.tsm.business.dao.entity.Project;

@Component
public class ProjectServiceImpl implements ProjectService, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8387621002911749202L;
	
	@Autowired
	private ProjectDAO projectDAO;

	public void setProjectDAO(ProjectDAO projectDAO) {
		this.projectDAO = projectDAO;
	}

	@Transactional(readOnly=false)
	public Project save(Project transientInstance) {
		return projectDAO.save(transientInstance);
	}

	@Transactional(readOnly=false)
	public void delete(Project persistentInstance) {
		projectDAO.delete(persistentInstance);
	}

	@Transactional(readOnly=true)
	public Project findById(Integer id) {
		return projectDAO.findById(id);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByExample(Project instance) {
		return projectDAO.findByExample(instance);
	}

	@Transactional(readOnly=true)
	public Project findProject(Project project) {
		return projectDAO.findProject(project);
	}

	@Transactional(readOnly=true)
	public List<Project> getProjects(Set<Integer> projectIds) {
		return projectDAO.getProjects(projectIds);
	}

	@Transactional(readOnly=true)
	public Project findProject(String propertyName, Object value) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByProperty(String propertyName, Object value) {
		return projectDAO.findByProperty(propertyName, value);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByProjectName(Object projectName) {
		return projectDAO.findByProjectName(projectName);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByProjectDesc(Object projectDesc) {
		return projectDAO.findByProjectDesc(projectDesc);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findAll() {
		return projectDAO.findAll();
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findActiveProjects() {
		return projectDAO.findActiveProjects();
	}

	@Transactional(readOnly=true)
	public List<Project> getActiveProjectsByClient(Integer clientId) {
		return projectDAO.getActiveProjectsByClient(clientId);
	}

	@Transactional(readOnly=false)
	public Project merge(Project detachedInstance) {
		return projectDAO.merge(detachedInstance);
	}

	@Transactional(readOnly=false)
	public void attachDirty(Project instance) {
		projectDAO.attachDirty(instance);
	}

	@Transactional(readOnly=false)
	public void attachClean(Project instance) {
		projectDAO.attachClean(instance);
	}

	@Transactional(readOnly=true)
	public List<Project> getProjects(String string) {
		return projectDAO.getProjects(string);
	}

	@Transactional(readOnly=false)
	public void deactivateProject(Project currentproject) {
		projectDAO.deactivateProject(currentproject);
	}

}
