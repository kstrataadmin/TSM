package com.kstrata.apps.tsm.business.service;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kstrata.apps.tsm.business.dao.EmployeeDAO;
import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.util.IEncryptAndDecrypt;

@Component
public class EmployeeServiceImpl implements EmployeeService, Serializable {

	private static final long serialVersionUID = 7857541748017507398L;

	@Autowired
	private EmployeeDAO employeeDAO;
	
	@Resource
	@Autowired(required=true)
	private IEncryptAndDecrypt encryptAndDecrypt;

	@Transactional(readOnly = false)
	public Employee save(Employee employee) {
		return employeeDAO.save(employee);
	}
	
	@Transactional(readOnly = false)
	public Employee update(Employee employee) {
		return employeeDAO.update(employee);
	}

	@Transactional(readOnly = false)
	public void deactivateEmployee(Employee employee) {
		employeeDAO.deactivateEmployee(employee);
	}

	@Transactional(readOnly = false)
	public Integer createEmployee(Employee employee) {
		return employeeDAO.createEmployee(employee);
	}

	@Transactional(readOnly = false)
	public void delete(Employee persistentInstance) {
		employeeDAO.delete(persistentInstance);
	}

	@Transactional(readOnly = true)
	public Employee findById(Integer id) {
		return employeeDAO.findById(id);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List findByExample(Employee instance) {
		return employeeDAO.findByExample(instance);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List findByProperty(String propertyName, Object value) {
		return employeeDAO.findByProperty(propertyName, value);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List findByEmpFirstName(Object empFirstName) {
		return employeeDAO.findByEmpFirstName(empFirstName);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List findByEmpLastName(Object empLastName) {
		return employeeDAO.findByEmpLastName(empLastName);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List findByEmpDesignation(Object empDesignation) {
		return employeeDAO.findByEmpDesignation(empDesignation);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List findByEmpEmailid(Object empEmailid) {
		return employeeDAO.findByEmpEmailid(empEmailid);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List findByEmpUsername(Object empUsername) {
		return employeeDAO.findByEmpUsername(empUsername);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List findByEmpPassword(Object empPassword) {
		return employeeDAO.findByEmpPassword(empPassword);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List findByActive(Object active) {
		return employeeDAO.findByActive(active);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Client> getReminderEnabledClients(Employee employee) {
		return employeeDAO.getReminderEnabledClients(employee);
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List findAll() {
		return employeeDAO.findAll();
	}

	@Transactional(readOnly = true)
	public List<Employee> getActiveEmployees() {
		return employeeDAO.getActiveEmployees();
	}

	@Transactional(readOnly = true)
	public List<Employee> getEmployees(String string) {
		return employeeDAO.getEmployees(string);
	}

	@Transactional(readOnly = true)
	public Employee getEmployee(Integer empId) {
		return employeeDAO.getEmployee(empId);
	}

	@Transactional
	public Employee getEmployee(String username) {
		return employeeDAO.getEmployee(username);
	}

	@Transactional(readOnly = true)
	public Employee getEmployee(String username, String password) throws Exception {
		Employee employee = employeeDAO.getEmployee(username, password);
		if(employee!=null)
			if(!password.equals(encryptAndDecrypt.decrypt(employee.getEmpPassword())))
				employee = null;
		return employee;
	}

	@Transactional(readOnly = false)
	public Employee merge(Employee detachedInstance) {
		return employeeDAO.merge(detachedInstance);
	}

	@Transactional(readOnly = false)
	public void attachDirty(Employee instance) {
		employeeDAO.attachDirty(instance);
	}

	@Transactional(readOnly = false)
	public void attachClean(Employee instance) {
		employeeDAO.attachClean(instance);
	}

	public void setEmployeeDAO(EmployeeDAO employeeDAO) {
		this.employeeDAO = employeeDAO;
	}

}
