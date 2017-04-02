package com.kstrata.apps.tsm.business.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kstrata.apps.tsm.business.dao.entity.Project;
import com.kstrata.apps.tsm.business.dao.hibernate.BaseDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * Project entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.kstrata.apps.tsm.business.dao.entity.Project
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings({"rawtypes"})
public class ProjectDAOImpl extends BaseDao implements ProjectDAO, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ProjectDAOImpl.class);
	// property constants
	public static final String PROJECT_NAME = "projectName";
	public static final String PROJECT_DESC = "projectDesc";
	
	public synchronized Project save(Project transientInstance) {
		log.debug("saving Project instance");
		try {
			super.getSession().saveOrUpdate(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
		return transientInstance;
	}

	public synchronized void delete(Project persistentInstance) {
		log.debug("deleting Project instance");
		try {
			super.getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Project findById(java.lang.Integer id) {
		Project projectFound = null;
		log.debug("finding Project instance");
		try {
			String queryString = "select p from Project p JOIN FETCH p.client where p.projectId = "+id;
			Query queryObject = super.getSession().createQuery(queryString);
			List list = queryObject.list();
			if(list.size() != 0){
				projectFound = (Project) list.get(0);
			}
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
		return projectFound;
	}

	public List findByExample(Project instance) {
		log.debug("finding Project instance by example");
		try {
			List results = super.getSession().createCriteria("com.kstrata.apps.tsm.business.dao.entity.Project").add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	public Project findProject(Project project) {
		Project projectFound = null;
		log.debug("finding Project instance");
		try {
			String queryString = "select p from Project p JOIN FETCH p.client where" +
					" p.projectDesc = '" + project.getProjectDesc()+"' and p.client.clientId = " + project.getClient().getClientId();
			Query queryObject = super.getSession().createQuery(queryString);
			List list = queryObject.list();
			if(list.size() != 0){
				projectFound = (Project) list.get(0);
			}
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
		return projectFound;
	}
	@SuppressWarnings("unchecked")
	public List<Project> getProjects(Set<Integer> projectIds) {
		List<Project> projects = new ArrayList<Project>();
		String condition = "";
		if(projectIds.size() != 0){
			for(Integer projectId: projectIds){
				condition = condition + projectId + ",";
			}
			condition = condition.substring(0, condition.length() - 1);
			String queryString = "select p from Project p JOIN FETCH p.client where p.projectId IN (" + condition + ")";
			Query query = super.getSession().createQuery(queryString);
			List<Project> list = query.list();
			for(Project object: list){
				projects.add(object);
			}
		}
		return projects;
	}

	public Project findProject(String propertyName, Object value) {
		log.debug("finding Project instance with property: " + propertyName + ", value: " + value);
		try {
			Query query = super.getSession().createQuery("select p from Project p JOIN FETCH p.client where" +
					" AND p.projectDesc = '" + value +"'");
			List list = query.list();
			Project project = null;
			if(list.size() != 0){
				project = (Project) list.get(0);
			}
			
			return project;
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Project instance with property: " + propertyName + ", value: " + value);
		try {
			String queryString = "from Project as model where model." + propertyName + "= ?";
			Query queryObject = super.getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByProjectName(Object projectName) {
		return findByProperty(PROJECT_NAME, projectName);
	}

	public List findByProjectDesc(Object projectDesc) {
		return findByProperty(PROJECT_DESC, projectDesc);
	}

	@SuppressWarnings("unchecked")
	public List<Project> findAll() {
		log.debug("finding all Project instances");
		List<Project> projects = new ArrayList<Project>();
		try {
			String queryString = "select p from Project p JOIN FETCH p.client";
			Query query = super.getSession().createQuery(queryString);
			List<Project> list = query.list();
			for(Project object: list){
				projects.add(object);
			}
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
		return projects;
	}
	@SuppressWarnings("unchecked")
	public List findActiveProjects(){
		log.debug("finding all Project instances");
		List<Project> projects = new ArrayList<Project>();
		try {
			String queryString = "select p from Project p JOIN FETCH p.client where p.active='Y'";
			Query query = super.getSession().createQuery(queryString);
			List<Project> list = query.list();
			for(Project object: list){
				projects.add(object);
			}
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
		return projects;
	}
	@SuppressWarnings("unchecked")
	public List<Project> getActiveProjectsByClient(Integer clientId) {
		log.debug("finding all Project instances");
		List<Project> projects = new ArrayList<Project>();
		try {
			String queryString = "select p from Project p JOIN FETCH p.client where p.active='Y' " +
					"AND p.client.clientId=" + clientId;
			Query query = super.getSession().createQuery(queryString);
			List<Project> list = query.list();
			for(Project object: list){
				projects.add(object);
			}
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
		return projects;
	}
	public Project merge(Project detachedInstance) {
		log.debug("merging Project instance");
		try {
			Project result = (Project) super.getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Project instance) {
		log.debug("attaching dirty Project instance");
		try {
			super.getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Project instance) {
		log.debug("attaching clean Project instance");
		try {
			super.getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Project> getProjects(String string) {
		List<Project> projects = new ArrayList<Project>();
		try {
			Query queryObject = super.getSession().createQuery("select p from Project p JOIN FETCH p.client where " +
					"p.projectName LIKE '" + string + "%' OR p.projectDesc LIKE '" + string + "%'");
			List<Project> list = queryObject.list();
			for(Project object: list){
				projects.add(object);
			}
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
		return projects;
	}

	public void deactivateProject(Project currentproject) {
		try {
			currentproject = (Project) super.getSession().merge(currentproject);
			super.getSession().update(currentproject);
			super.getSession().flush();
			log.debug("deactivation successful");
		} catch (RuntimeException re) {
			log.error("deactivation failed", re);
			throw re;
		}
	}

}