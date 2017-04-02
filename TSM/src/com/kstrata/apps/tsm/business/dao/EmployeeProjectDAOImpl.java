package com.kstrata.apps.tsm.business.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Project;
import com.kstrata.apps.tsm.business.dao.hibernate.BaseDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * EmployeeProject entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.kstrata.apps.tsm.business.dao.entity.EmployeeProject
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings({"rawtypes"})
public class EmployeeProjectDAOImpl extends BaseDao implements EmployeeProjectDAO, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(EmployeeProjectDAOImpl.class);
	@Resource
	@Autowired
	private ClientDAOImpl clientDAO;
	// property constants

	public synchronized void save(EmployeeProject transientInstance) {
		log.debug("saving EmployeeProject instance");
		try {
			super.getSession().saveOrUpdate(transientInstance);
			super.getSession().flush();
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}
	public synchronized void insertEmployeeproject(Project project, Employee employee) {
		EmployeeProject employeeProject = findEmployeeProject(project, employee);
		employeeProject.setActive("Y");
		save(employeeProject);
	}
	public synchronized void removeEmployeeproject(Project project, Employee employee) {
		EmployeeProject employeeProject = findEmployeeProject(project, employee);
		employeeProject.setActive("N");
		save(employeeProject);
	}
	public synchronized void deactivateEmployeeProjects(Project project) {
		try {
			SQLQuery query = super.getSession().createSQLQuery("UPDATE EMPLOYEE_PROJECT SET ACTIVE='N' WHERE PROJECT_ID=" + project.getProjectId());
			query.executeUpdate();
		} catch (RuntimeException re) {
			throw re;
		}
	}
	public synchronized void delete(EmployeeProject persistentInstance) {
		log.debug("deleting EmployeeProject instance");
		try {
			super.getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public EmployeeProject findById(java.lang.Integer id) {
		log.debug("getting EmployeeProject instance with id: " + id);
		EmployeeProject employeeProject = null;
				
		Query query = super.getSession().createQuery("SELECT ep, e, pr, c FROM EmployeeProject ep, Employee e, Project pr, Client c " +
				"WHERE ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId " +
				"AND ep.empProjId=:id");
		query.setParameter("id", id);

		List list = query.list();
		if (!list.isEmpty()) {
			Object[] object = (Object[]) list.get(0);
			
			employeeProject = (EmployeeProject) object[0];
			Employee employee = (Employee) object[1];
			Project project = (Project) object[2];
			Client client = (Client) object[3];
			
			employeeProject.setEmployee(employee);
			project.setClient(client);
			employeeProject.setProject(project);
		}
		
		return employeeProject;
	}

	public List findByExample(EmployeeProject instance) {
		log.debug("finding EmployeeProject instance by example");
		try {
			List results = super.getSession().createCriteria("com.kstrata.apps.tsm.business.dao.entity.EmployeeProject")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	
	public List<EmployeeProject> findEmployeeProjectByEmployee(Employee employee, List<Integer> projectIds) {
		List<EmployeeProject> employeeProjects = new ArrayList<EmployeeProject>();
		String condition = "";
		if(projectIds.size() != 0){
			try {
				StringBuilder queryString = new StringBuilder("SELECT ep, e, pr, c FROM EmployeeProject ep, Employee e, Project pr, Client c ");
				queryString.append("WHERE ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId ");
				queryString.append("AND ep.employee.empId =").append(employee.getEmpId()).append(" AND ep.active='Y' AND ep.project.projectId IN ( ");
				for(Integer projectId: projectIds){
					condition = condition + "'" + projectId + "',";
				}
				condition = condition.substring(0, condition.length()-1);
				queryString.append(condition).append(" )");
				
				Query query = super.getSession().createQuery(queryString.toString());
				
				List list = query.list();
				for (Object object : list) {
					Object[] array = (Object[]) object;
					
					EmployeeProject employeeProject = (EmployeeProject) array[0];
					Employee employee1 = (Employee) array[1];
					Project project = (Project) array[2];
					Client client = (Client) array[3];
					
					employeeProject.setEmployee(employee1);
					project.setClient(client);
					employeeProject.setProject(project);
					
					employeeProjects.add(employeeProject);
				}
			} catch (RuntimeException re) {
				log.error("find by property name failed", re);
				throw re;
			}
		}
		return employeeProjects;
	}

	public List<EmployeeProject> findEmployeeProjectByEmployee(Employee employee){
		List<EmployeeProject> employeeProjects = new ArrayList<EmployeeProject>();
		try {
			StringBuilder queryString = new StringBuilder("SELECT ep, e, pr, c FROM EmployeeProject ep, Employee e, Project pr, Client c ");
			queryString.append("WHERE ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId ");
			queryString.append("AND ep.employee.empId=:empid AND ep.active='Y' order by c.clientName");
			
			Query query = super.getSession().createQuery(queryString.toString());
			query.setParameter("empid", employee.getEmpId());
			
			List list = query.list();
			for (Object object : list) {
				Object[] array = (Object[]) object;
				
				EmployeeProject employeeProject = (EmployeeProject) array[0];
				Employee employee1 = (Employee) array[1];
				Project project = (Project) array[2];
				Client client = (Client) array[3];
				
				employeeProject.setEmployee(employee1);
				project.setClient(client);
				employeeProject.setProject(project);
				
				employeeProjects.add(employeeProject);
			}
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
		return employeeProjects;
	}
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding EmployeeProject instance with property: " + propertyName + ", value: " + value);
		try {
			String queryString = "from EmployeeProject where "+ propertyName + "= ?";
			Query queryObject = super.getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public EmployeeProject findEmployeeProject(Project project,	Employee employee) {
		EmployeeProject employeeProject = null;
		log.debug("finding EmployeeProject using project and employee");
		try {
			String queryString = "SELECT ep, e, pr, c FROM EmployeeProject ep, Employee e, Project pr, Client c " +
					"WHERE ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId " +
					"AND ep.project.projectId=" + project.getProjectId() + " and ep.employee.empId=" + employee.getEmpId();
			Query queryObject = super.getSession().createQuery(queryString);
			List list = queryObject.list();
			if (!list.isEmpty()) {
				Object[] object = (Object[]) list.get(0);
				
				employeeProject = (EmployeeProject) object[0];
				Employee employee1 = (Employee) object[1];
				Project project1 = (Project) object[2];
				Client client = (Client) object[3];
				
				employeeProject.setEmployee(employee1);
				project1.setClient(client);
				employeeProject.setProject(project1);
			} else {
				employeeProject = new EmployeeProject(project, employee);
			}
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
		return employeeProject;
	}

	public List findAll() {
		log.debug("finding all EmployeeProject instances");
		List<EmployeeProject> employeeProjects = new ArrayList<EmployeeProject>();
		try {
			String queryString = "SELECT ep, e, pr, c FROM EmployeeProject ep, Employee e, Project pr, Client c " +
					"WHERE ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId";
			Query queryObject = super.getSession().createQuery(queryString);
			List list = queryObject.list();
			for (Object object : list) {
				Object[] array = (Object[]) object;
				
				EmployeeProject employeeProject = (EmployeeProject) array[0];
				Employee employee1 = (Employee) array[1];
				Project project = (Project) array[2];
				Client client = (Client) array[3];
				
				employeeProject.setEmployee(employee1);
				project.setClient(client);
				employeeProject.setProject(project);
				
				employeeProjects.add(employeeProject);
			}
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
		return employeeProjects;
	}

	public EmployeeProject merge(EmployeeProject detachedInstance) {
		log.debug("merging EmployeeProject instance");
		try {
			EmployeeProject result = (EmployeeProject) super.getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(EmployeeProject instance) {
		log.debug("attaching dirty EmployeeProject instance");
		try {
			super.getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(EmployeeProject instance) {
		log.debug("attaching clean EmployeeProject instance");
		try {
			super.getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	public ClientDAOImpl getClientDAO() {
		return clientDAO;
	}
	public void setClientDAO(ClientDAOImpl clientDAO) {
		this.clientDAO = clientDAO;
	}
}