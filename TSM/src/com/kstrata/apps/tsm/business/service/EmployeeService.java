package com.kstrata.apps.tsm.business.service;

import java.util.List;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.entity.Employee;

@SuppressWarnings("rawtypes")
public interface EmployeeService {
	public Employee save(Employee employee);
	
	public Employee update(Employee employee);

	public void deactivateEmployee(Employee employee);

	public Integer createEmployee(Employee employee);

	public void delete(Employee persistentInstance);

	public Employee findById(java.lang.Integer id);

	public List findByExample(Employee instance);

	public List findByProperty(String propertyName, Object value);

	public List findByEmpFirstName(Object empFirstName);

	public List findByEmpLastName(Object empLastName);

	public List findByEmpDesignation(Object empDesignation);

	public List findByEmpEmailid(Object empEmailid);

	public List findByEmpUsername(Object empUsername);

	public List findByEmpPassword(Object empPassword);

	public List findByActive(Object active);

	public List findAll();

	public List<Employee> getActiveEmployees();

	public List<Employee> getEmployees(String string);

	public Employee getEmployee(Integer empId);

	public Employee getEmployee(String username);

	public Employee getEmployee(String username, String password)
			throws Exception;

	public Employee merge(Employee detachedInstance);

	public void attachDirty(Employee instance);

	public void attachClean(Employee instance);

	public List<Client> getReminderEnabledClients(Employee employee);

}
