package com.kstrata.apps.tsm.business.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.Role;
import com.kstrata.apps.tsm.business.dao.hibernate.BaseDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * Employee entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.kstrata.apps.tsm.business.dao.entity.Employee
 * 
 */
@SuppressWarnings({"rawtypes"})
public class EmployeeDAOImpl extends BaseDao implements EmployeeDAO, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(EmployeeDAOImpl.class);
	// property constants
	public static final String EMP_FIRST_NAME = "empFirstName";
	public static final String EMP_LAST_NAME = "empLastName";
	public static final String EMP_DESIGNATION = "empDesignation";
	public static final String EMP_EMAILID = "empEmailid";
	public static final String EMP_USERNAME = "empUsername";
	public static final String EMP_PASSWORD = "empPassword";
	public static final String ACTIVE = "active";

	public synchronized Employee save(Employee employee) {
		log.debug("saving Employee instance");
		try {
			super.getSession().saveOrUpdate(employee);
			employee.setSaveStatus("SUCCESS");
			log.debug("save successful");
		} catch (Exception re) {
			log.error("save failed", re);
		}
		return employee;
	}
	
	public synchronized Employee update(Employee employee) {
		log.debug("updating Employee instance");
		try {
			super.getSession().update(employee);
			employee.setSaveStatus("SUCCESS");
			log.debug("upadte successful");
		} catch (Exception re) {
			log.error("update failed", re);
		}
		return employee;
	}
	
	public synchronized void deactivateEmployee(Employee employee) {
		try {
			super.getSession().update(employee);
			log.debug("deactivation successful");
		} catch (RuntimeException re) {
			log.error("deactivation failed", re);
			throw re;
		}
	}
	public synchronized Integer createEmployee(Employee employee) {
		boolean created = false;
		Employee employeeExisting = findById(employee.getEmpId());
		if(employeeExisting == null && !employee.getEmpId().equals(null)){
			try {
				super.getSession().save(employee);
				created = true;
				log.debug("save successful");
			} catch (Exception re) {
				System.out.println("save failed" + re);
			}
		}
		return created ? employee.getEmpId() : null;
	}
	public synchronized void delete(Employee persistentInstance) {
		log.debug("deleting Employee instance");
		try {
			super.getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Employee findById(java.lang.Integer id) {
		log.debug("getting Employee instance with id: " + id);
		try {
			Employee instance = (Employee) super.getSession().get("com.kstrata.apps.tsm.business.dao.entity.Employee", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(Employee instance) {
		log.debug("finding Employee instance by example");
		try {
			List results = super.getSession().createCriteria("com.kstrata.apps.tsm.business.dao.entity.Employee")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Employee instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Employee as model where model." + propertyName + "= ?";
			Query queryObject = super.getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByEmpFirstName(Object empFirstName) {
		return findByProperty(EMP_FIRST_NAME, empFirstName);
	}

	public List findByEmpLastName(Object empLastName) {
		return findByProperty(EMP_LAST_NAME, empLastName);
	}

	public List findByEmpDesignation(Object empDesignation) {
		return findByProperty(EMP_DESIGNATION, empDesignation);
	}

	public List findByEmpEmailid(Object empEmailid) {
		return findByProperty(EMP_EMAILID, empEmailid);
	}

	public List findByEmpUsername(Object empUsername) {
		return findByProperty(EMP_USERNAME, empUsername);
	}

	public List findByEmpPassword(Object empPassword) {
		return findByProperty(EMP_PASSWORD, empPassword);
	}

	public List findByActive(Object active) {
		return findByProperty(ACTIVE, active);
	}

	public List findAll() {
		log.debug("finding all Employee instances");
		List<Employee> employees = new ArrayList<Employee>();
		try {
			Query query = super.getSession().createQuery("select e, r.roleName, r.roleId from Employee e INNER JOIN FETCH e.role as r");
			List list = query.list();
			for (Object object : list) {
				Object[] array = (Object[]) object;
				Employee employee = (Employee) array[0];
				Role role = new Role();
				role.setRoleName((String) array[1]);
				role.setRoleId((Integer) array[2]);
				employee.setRole(role);
				
				employees.add(employee);
			}
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
		return employees;
	}
	public List<Employee> getActiveEmployees() {
		List<Employee> employees = new ArrayList<Employee>();
		try {
			Query query = super.getSession().createQuery("select e, r.roleName, r.roleId from Employee e INNER JOIN FETCH e.role as r " +
					"WHERE e.active='Y' AND e.role.roleId=2");
			List list = query.list();
			for (Object object : list) {
				Object[] array = (Object[]) object;
				Employee employee = (Employee) array[0];
				Role role = new Role();
				role.setRoleName((String) array[1]);
				role.setRoleId((Integer) array[2]);
				employee.setRole(role);
				
				employees.add(employee);
			}
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
		return employees;
	}
	public List<Employee> getEmployees(String string) {
		List<Employee> employees = new ArrayList<Employee>();
		try {
			Query query = super.getSession().createQuery("select e, r.roleName, r.roleId from Employee e INNER JOIN FETCH e.role as r " +
					"where e.empFirstName LIKE '"+string+"%' OR e.empLastName LIKE '"+string+"%'");
			List list = query.list();
			for (Object object : list) {
				Object[] array = (Object[]) object;
				Employee employee = (Employee) array[0];
				Role role = new Role();
				role.setRoleName((String) array[1]);
				role.setRoleId((Integer) array[2]);
				employee.setRole(role);
				
				employees.add(employee);
			}
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
		return employees;
	}
	public Employee getEmployee(Integer empId) {
		Employee employee = null;
		Query query = super.getSession().createQuery("select e, r.roleName, r.roleId from Employee e " +
				"INNER JOIN FETCH e.role as r where e.empId=:empId");
		query.setParameter("empId", empId);
		List list = query.list();
		if(!list.isEmpty()){
			Object[] array = (Object[]) list.get(0);
			employee = (Employee) array[0];
			Role role = new Role();
			role.setRoleName((String) array[1]);
			role.setRoleId((Integer) array[2]);
			employee.setRole(role);
		}
		return employee;
	}
	public Employee getEmployee(String username) {
		Employee employee = null;
		log.debug("finding an Employee existence");
		try {
			Query query = super.getSession().createQuery("select e, r.roleName, r.roleId from Employee e " +
					"INNER JOIN FETCH e.role as r where e.empUsername=:username AND e.active='Y'");
			query.setParameter("username", username);
			List list = query.list();
			if(!list.isEmpty()){
				Object[] array = (Object[]) list.get(0);
				employee = (Employee) array[0];
				Role role = new Role();
				role.setRoleName((String) array[1]);
				role.setRoleId((Integer) array[2]);
				employee.setRole(role);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return employee;
	}
	
	@Override
	public List<Client> getReminderEnabledClients(Employee employee) {
		List<Client> clients = new ArrayList<Client>();
		String queryString = "SELECT DISTINCT C.* FROM CLIENT C, PROJECT P, EMPLOYEE_PROJECT EP " +
				"WHERE P.CLIENT_ID=C.CLIENT_ID AND EP.PROJECT_ID=P.PROJECT_ID AND C.TIMESHEET_REMINDER_ENABLE='Y' AND P.ACTIVE='Y' AND EP.EMP_ID=" + employee.getEmpId();
		Query query = super.getSession().createSQLQuery(queryString);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List list = query.list();
		for (Object obj : list) {
			Map map = (Map) obj;
			Client client = new Client();
			client.setClientId((Integer) map.get("CLIENT_ID"));
			client.setClientName((String) map.get("CLIENT_NAME"));
			client.setShortName((String)map.get("SHORTNAME"));
			clients.add(client);
		}
				
		return clients;
	}
	
	public Employee getEmployee(String username, String password) throws Exception {
		Employee employee = null;
		log.debug("finding an Employee existence");
		try {
			Object object = getEmployee(username);
			if (object != null) {
				employee = (Employee) object;
			}
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
		return employee;
	}
	public Employee merge(Employee detachedInstance) {
		log.debug("merging Employee instance");
		try {
			Employee result = (Employee) super.getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Employee instance) {
		log.debug("attaching dirty Employee instance");
		try {
			super.getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Employee instance) {
		log.debug("attaching clean Employee instance");
		try {
			super.getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}