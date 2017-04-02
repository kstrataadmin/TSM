package com.kstrata.apps.tsm.business.dao;

import java.util.List;

import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Project;

public interface EmployeeProjectDAO {
	
	public void save(EmployeeProject transientInstance);

	public void insertEmployeeproject(Project project, Employee employee);

	public void removeEmployeeproject(Project project, Employee employee);
	
	public void deactivateEmployeeProjects(Project project);

	public void delete(EmployeeProject persistentInstance);

	public EmployeeProject findById(java.lang.Integer id);

	public List findByExample(EmployeeProject instance);

	public List<EmployeeProject> findEmployeeProjectByEmployee(Employee employee, List<Integer> projectIds);

	public List<EmployeeProject> findEmployeeProjectByEmployee(Employee employee);

	public List findByProperty(String propertyName, Object value);

	public EmployeeProject findEmployeeProject(Project project,
			Employee employee);

	public List findAll();

	public EmployeeProject merge(EmployeeProject detachedInstance);

	public void attachDirty(EmployeeProject instance);

	public void attachClean(EmployeeProject instance);

}
