package com.kstrata.apps.tsm.business.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kstrata.apps.tsm.business.dao.EmployeeProjectDAO;
import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Project;

@Component
public class EmployeeProjectServiceImpl implements EmployeeProjectService, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7719996352638595345L;
	@Autowired
	private EmployeeProjectDAO employeeProjectDAO;
	
	@Transactional(readOnly=false)
	public void save(EmployeeProject transientInstance) {
		employeeProjectDAO.save(transientInstance);
	}

	@Transactional(readOnly=false)
	public void insertEmployeeproject(Project project, Employee employee) {
		employeeProjectDAO.insertEmployeeproject(project, employee);
	}

	@Transactional(readOnly=false)
	public void removeEmployeeproject(Project project, Employee employee) {
		employeeProjectDAO.removeEmployeeproject(project, employee);
	}
	
	@Transactional(readOnly=false)
	public void deactivateEmployeeProjects(Project project) {
		employeeProjectDAO.deactivateEmployeeProjects(project);
	}

	@Transactional(readOnly=false)
	public void delete(EmployeeProject persistentInstance) {
		employeeProjectDAO.delete(persistentInstance);
	}

	@Transactional(readOnly=true)
	public EmployeeProject findById(Integer id) {
		return employeeProjectDAO.findById(id);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findByExample(EmployeeProject instance) {
		return employeeProjectDAO.findByExample(instance);
	}

	@Transactional(readOnly=true)
	public List<EmployeeProject> findEmployeeProjectByEmployee(
			Employee employee, List<Integer> projectIds) {
		return employeeProjectDAO.findEmployeeProjectByEmployee(employee, projectIds);
	}

	@Transactional(readOnly=true)
	public List<EmployeeProject> findEmployeeProjectByEmployee(Employee employee) {
		return employeeProjectDAO.findEmployeeProjectByEmployee(employee);
	}

	@Transactional(readOnly=true)
	@SuppressWarnings("rawtypes")
	public List findByProperty(String propertyName, Object value) {
		return employeeProjectDAO.findByProperty(propertyName, value);
	}

	@Transactional(readOnly=true)
	public EmployeeProject findEmployeeProject(Project project,
			Employee employee) {
		return employeeProjectDAO.findEmployeeProject(project, employee);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly=true)
	public List findAll() {
		return employeeProjectDAO.findAll();
	}

	@Transactional(readOnly=false)
	public EmployeeProject merge(EmployeeProject detachedInstance) {
		return employeeProjectDAO.merge(detachedInstance);
	}

	@Transactional(readOnly=false)
	public void attachDirty(EmployeeProject instance) {
		employeeProjectDAO.attachDirty(instance);
	}

	@Transactional(readOnly=false)
	public void attachClean(EmployeeProject instance) {
		employeeProjectDAO.attachClean(instance);
	}

	public void setEmployeeProjectDAO(EmployeeProjectDAO employeeProjectDAO) {
		this.employeeProjectDAO = employeeProjectDAO;
	}
	
	

}
