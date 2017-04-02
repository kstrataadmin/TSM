package com.kstrata.apps.tsm.business.service;

import java.util.List;
import java.util.Set;

import com.kstrata.apps.tsm.business.dao.ClientDAOImpl;
import com.kstrata.apps.tsm.business.dao.entity.Project;

public interface ProjectService {

	public Project save(Project transientInstance);

	public void delete(Project persistentInstance);

	public Project findById(java.lang.Integer id);

	public List findByExample(Project instance);

	public Project findProject(Project project);

	public List<Project> getProjects(Set<Integer> projectIds);

	public Project findProject(String propertyName, Object value);

	public List findByProperty(String propertyName, Object value);

	public List findByProjectName(Object projectName);

	public List findByProjectDesc(Object projectDesc);

	public List findAll();

	public List findActiveProjects();

	public List<Project> getActiveProjectsByClient(Integer clientId);

	public Project merge(Project detachedInstance);

	public void attachDirty(Project instance);

	public void attachClean(Project instance);

	public List<Project> getProjects(String string);

	public void deactivateProject(Project currentproject);
}
