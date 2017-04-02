package com.kstrata.apps.tsm.business.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Period;
import com.kstrata.apps.tsm.business.dao.entity.Project;
import com.kstrata.apps.tsm.business.dao.entity.Timesheet;
import com.kstrata.apps.tsm.business.dao.hibernate.BaseDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * Timesheet entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.kstrata.apps.tsm.business.dao.entity.Timesheet
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings({"rawtypes"})
public class TimesheetDAOImpl extends BaseDao implements TimesheetDAO, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(TimesheetDAOImpl.class);
	// property constants
	public static final String TASK_DESC = "taskDesc";
	public static final String TOTAL = "total";
	public static final String STATUS = "status";
	public static final String MNGR_STATUS = "mngrStatus";

	public synchronized Timesheet save(Timesheet transientInstance) {
		try {
			super.getSession().saveOrUpdate(transientInstance);
			super.getSession().flush();
		} catch (RuntimeException re) {
			throw re;
		}
		return transientInstance;
	}
	public void approve(List<EmployeeProject> employeeProjects, Period period) {
		List<Timesheet> timesheets = findTimesheets(employeeProjects, period.getPeriodId(), null);
		for(Timesheet timesheet : timesheets){
			timesheet.setMngrStatus("true");
			save(timesheet);
		}
	}
	public void deny(List<EmployeeProject> employeeProjects, Period period) {
		List<Timesheet> timesheets = findTimesheets(employeeProjects, period.getPeriodId(), null);
		for(Timesheet timesheet : timesheets){
			timesheet.setMngrStatus("false");
			timesheet.setSubmitFlag("N");
			save(timesheet);
		}
	}
	@Override
	public void approvePeriod(Employee employee, Period otherPeriod) {
		try {
			SQLQuery query = super.getSession().createSQLQuery("UPDATE TIMESHEET T SET T.MNGR_STATUS='true'"
					+ " WHERE T.PERIOD_ID=" + otherPeriod.getPeriodId()
					+ " AND T.EMP_PROJECT_ID IN (SELECT EMP_PROJ_ID FROM EMPLOYEE_PROJECT WHERE EMP_ID=" + employee.getEmpId() + ")");
			query.executeUpdate();
		} catch (RuntimeException re) {
			throw re;
		}
	}
	@Override
	public void denyPeriod(Employee employee, Period otherPeriod) {
		try {
			SQLQuery query = super.getSession().createSQLQuery("UPDATE TIMESHEET T SET T.MNGR_STATUS='false', T.SUBMIT_FLAG='N'"
					+ " WHERE T.PERIOD_ID=" + otherPeriod.getPeriodId()
					+ " AND T.EMP_PROJECT_ID IN (SELECT EMP_PROJ_ID FROM EMPLOYEE_PROJECT WHERE EMP_ID=" + employee.getEmpId() + ")");
			query.executeUpdate();
			
		} catch (RuntimeException re) {
			throw re;
		}
	}
	public synchronized void delete(Timesheet persistentInstance) {
		log.debug("deleting Timesheet instance");
		try {
			//persistentInstance = (Timesheet) super.getSession().merge(persistentInstance);
			String queryString = "DELETE FROM timesheet WHERE TIMESHEET_ID="+persistentInstance.getTimesheetId();
			SQLQuery queryObject = super.getSession().createSQLQuery(queryString);
			queryObject.executeUpdate();
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Timesheet findById(Integer id) {
		log.debug("getting Timesheet instance with id: " + id);
		try {
			Timesheet instance = (Timesheet) super.getSession().get("com.kstrata.apps.tsm.business.dao.entity.Timesheet", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Timesheet> findTimesheets(List<EmployeeProject> employeeProjects) {
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		String ids = new String();
		for(EmployeeProject employeeProject: employeeProjects){
			ids = ids + (employeeProject.getEmpProjId()+",");
		}
		ids = ids.substring(0, ids.length()-1);
		String queryString = "from Timesheet where EMP_PROJECT_ID IN ("+ ids + ")";
		Query query = super.getSession().createQuery(queryString);
		timesheets = query.list();
		return timesheets;
	}
	public List<Timesheet> getTimesheetsByDateRange(List<Integer> employeeProjectIds, Date startDate, Date endDate) {
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		String ids = "";
		
		StringBuilder queryString = new StringBuilder("SELECT ts, ep, p, e, pr, c ");
		queryString.append(" FROM Timesheet ts, EmployeeProject ep, Period p, Employee e, Project pr, Client c ");
		queryString.append("WHERE ts.employeeProject.empProjId = ep.empProjId AND ts.period.periodId = p.periodId " +
				"AND ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId AND (ts.taskDate BETWEEN ");
		queryString.append(":startDate").append(" AND ");
		queryString.append(":endDate").append(" ) AND ts.employeeProject.empProjId IN ( ");
		for(Integer employeeProjectId: employeeProjectIds){
			ids = ids+"'"+employeeProjectId+"',";
		}
		ids = ids.substring(0, ids.length()-1);
		queryString.append(ids).append(" ) order by ts.startTime");
		
		Query query = super.getSession().createQuery(queryString.toString());
		query.setParameter("startDate", DateUtils.truncate(startDate, Calendar.DATE));
		query.setParameter("endDate", DateUtils.truncate(endDate, Calendar.DATE));
		List list = query.list();
		timesheets = convertToTimesheetList(list);
		return timesheets;
	}
	public List findByExample(Timesheet instance) {
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		
		Query query = super.getSession().createQuery("SELECT ts, ep, p, e, pr, c FROM Timesheet ts, EmployeeProject ep, Period p, Employee e, Project pr, Client c " +
				"WHERE ts.employeeProject.empProjId = ep.empProjId AND ts.period.periodId = p.periodId " +
				"AND ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId " +
				"AND ts.mngrStatus = :mngrstatus");
		query.setParameter("mngrstatus", instance.getMngrStatus());
		List list = query.list();
		timesheets = convertToTimesheetList(list);
		return timesheets;
	}
	public List<Timesheet> findTimesheets(List<EmployeeProject> employeeProjects, Integer periodId, String approveStatus) {
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		String employeeProjectIdCondition = new String();
		String approveStatusCondition = new String();
		String periodIdCondition = new String();
		String ids = new String();
		if(employeeProjects!=null){
			if(employeeProjects.size() != 0){
				for(EmployeeProject employeeProject: employeeProjects){
					ids = ids + (employeeProject.getEmpProjId()+",");
				}
				ids = ids.substring(0, ids.length()-1);
				employeeProjectIdCondition = " AND ts.employeeProject.empProjId IN (" + ids + ")";
			}
		}
		else
			return timesheets;
		if(approveStatus != "" && approveStatus!=null){
			approveStatusCondition = " AND ts.mngrStatus = '" + (approveStatus.equals("Not Approved") ? false : true) + "'";
		}
		if(periodId != null)
			periodIdCondition = " AND ts.period.periodId = " + periodId;
		
		String queryString = "SELECT ts, ep, p, e, pr, c FROM Timesheet ts, EmployeeProject ep, Period p, Employee e, Project pr, Client c " +
				"WHERE ts.employeeProject.empProjId = ep.empProjId AND ts.period.periodId = p.periodId " +
				"AND ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId " 
				+ periodIdCondition + employeeProjectIdCondition + approveStatusCondition;
		
		Query query = super.getSession().createQuery(queryString);
		List list = query.list();
		timesheets = convertToTimesheetList(list);
		return timesheets;
	}
	public List<Timesheet> findTimesheets(List<EmployeeProject> employeeProjects, Integer periodId, String approveStatus, String taskType) {
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		String employeeProjectIdCondition = new String();
		String approveStatusCondition = new String();
		String periodIdCondition = new String();
		String ids = new String();
		if(employeeProjects!=null){
			if(employeeProjects.size() != 0){
				for(EmployeeProject employeeProject: employeeProjects){
					ids = ids + (employeeProject.getEmpProjId()+",");
				}
				ids = ids.substring(0, ids.length()-1);
				employeeProjectIdCondition = " AND ts.employeeProject.empProjId IN (" + ids + ")";
			}
		}
		else
			return timesheets;
		if(approveStatus != "" && approveStatus!=null){
			approveStatusCondition = " AND ts.mngrStatus = '" + (approveStatus.equals("Not Approved") ? false : true) + "'";
		}
		if(periodId != null)
			periodIdCondition = " AND ts.period.periodId = " + periodId;
		
		String queryString = "SELECT ts, ep, p, e, pr, c FROM Timesheet ts, EmployeeProject ep, Period p, Employee e, Project pr, Client c " +
				"WHERE ts.employeeProject.empProjId = ep.empProjId AND ts.period.periodId = p.periodId " +
				"AND ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId " +
				"AND ts.taskType=? " + periodIdCondition + employeeProjectIdCondition + approveStatusCondition;
		Query query = super.getSession().createQuery(queryString);
		query.setParameter(0, taskType);
		
		List list = query.list();
		timesheets = convertToTimesheetList(list);
		return timesheets;
	}

	public List<Timesheet> convertToTimesheetList(List list){
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		for(int i=0;i<list.size();i++){
			Object[] array = (Object[]) list.get(i);
			//ts, ep, p, e, pr, c 
			Timesheet timesheet = (Timesheet) array[0] ;
			EmployeeProject employeeProject = (EmployeeProject) array[1] ;
			Period period = (Period) array[2];
			Employee employee = (Employee) array[3];
			Project project = (Project) array[4];
			Client client = (Client) array[5];
			
			project.setClient(client);
			employeeProject.setProject(project);
			employeeProject.setEmployee(employee);
			timesheet.setPeriod(period);
			timesheet.setEmployeeProject(employeeProject);
			
			timesheets.add(timesheet);
		}
		
		return timesheets;
	}
	public List<Timesheet> findTimesheets(EmployeeProject employeeProjecttemp) {
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		String queryString = "SELECT ts, ep, p, e, pr, c FROM Timesheet ts, EmployeeProject ep, Period p, Employee e, Project pr, Client c " +
				"WHERE ts.employeeProject.empProjId = ep.empProjId AND ts.period.periodId = p.periodId " +
				"AND ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId " +
				"AND ts.taskType='RT' AND ts.employeeProject.empProjId=" + employeeProjecttemp.getEmpProjId();
		Query query = super.getSession().createQuery(queryString);
		List list = query.list();
		timesheets = convertToTimesheetList(list);
		
		return timesheets;
	}
	public List<Timesheet> findTimesheetsForOverTime(EmployeeProject employeeProjecttemp) {
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		String queryString = "SELECT ts, ep, p, e, pr, c FROM Timesheet ts, EmployeeProject ep, Period p, Employee e, Project pr, Client c " +
				"WHERE ts.employeeProject.empProjId = ep.empProjId AND ts.period.periodId = p.periodId " +
				"AND ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId " +
				"AND ts.taskType='OT' AND ts.employeeProject.empProjId=" + employeeProjecttemp.getEmpProjId();
		Query query = super.getSession().createQuery(queryString);
		List list = query.list();
		timesheets = convertToTimesheetList(list);
		
		return timesheets;
	}
	@Override
	public List<Timesheet> findTimesheetsForOverTime(EmployeeProject employeeProjecttemp, Date taskDate) {
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		String queryString = "SELECT ts, ep, p, e, pr, c FROM Timesheet ts, EmployeeProject ep, Period p, Employee e, Project pr, Client c " +
				"WHERE ts.employeeProject.empProjId = ep.empProjId AND ts.period.periodId = p.periodId " +
				"AND ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId " +
				"AND ts.taskType='OT' AND ts.employeeProject.empProjId=" + employeeProjecttemp.getEmpProjId() + " AND ts.taskDate=:taskDate";
		
		Query query = super.getSession().createQuery(queryString);
		query.setParameter("taskDate", DateUtils.truncate(taskDate, Calendar.DATE));
		
		List list = query.list();
		timesheets = (convertToTimesheetList(list));
		
		return timesheets;
	}
	@SuppressWarnings("unchecked")
	public List<Timesheet> findTimesheets(EmployeeProject employeeProject, Period period) {
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		String queryString = "from Timesheet ts where ts.employeeProject.empProjId=" + employeeProject.getEmpProjId()
				+ " AND ts.period.periodId=" + period.getPeriodId();
		
		Query query = super.getSession().createQuery(queryString);
		timesheets = query.list();
		return timesheets;
	}
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Timesheet instance with property: " + propertyName + ", value: " + value);
		try {
			String queryString = "from Timesheet as model where model." + propertyName + "= ?";
			Query queryObject = super.getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByTaskDesc(Object taskDesc) {
		return findByProperty(TASK_DESC, taskDesc);
	}

	public List findByTotal(Object total) {
		return findByProperty(TOTAL, total);
	}

	public List findByStatus(Object status) {
		return findByProperty(STATUS, status);
	}

	public List findByMngrStatus(Object mngrStatus) {
		return findByProperty(MNGR_STATUS, mngrStatus);
	}

	public List findAll() {
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		String queryString = "SELECT ts, ep, p, e, pr, c FROM Timesheet ts, EmployeeProject ep, Period p, Employee e, Project pr, Client c " +
				"WHERE ts.employeeProject.empProjId = ep.empProjId AND ts.period.periodId = p.periodId " +
				"AND ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId ";
		Query query = super.getSession().createQuery(queryString);
		List list = query.list();
		timesheets = (convertToTimesheetList(list));
		
		return timesheets;
	}

	public Timesheet merge(Timesheet detachedInstance) {
		log.debug("merging Timesheet instance");
		try {
			Timesheet result = (Timesheet) super.getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Timesheet instance) {
		log.debug("attaching dirty Timesheet instance");
		try {
			super.getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Timesheet instance) {
		log.debug("attaching clean Timesheet instance");
		try {
			super.getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	@Override
	public List<Timesheet> findTimesheets(Integer empprojectId, Date taskDate) {
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		String queryString = "SELECT ts, ep, p, e, pr, c FROM Timesheet ts, EmployeeProject ep, Period p, Employee e, Project pr, Client c " +
				"WHERE ts.employeeProject.empProjId = ep.empProjId AND ts.period.periodId = p.periodId " +
				"AND ep.employee.empId = e.empId AND ep.project.projectId = pr.projectId AND pr.client.clientId = c.clientId " +
				"AND ts.employeeProject.empProjId =" + empprojectId + " AND ts.taskDate=?";
		Query query = super.getSession().createQuery(queryString);
		query.setParameter(0, taskDate);
		List list = query.list();
		timesheets = (convertToTimesheetList(list));
		
		return timesheets;
	}
	
	@Override
	public boolean getSubmitFlagOfTimesheetByPeriod(Period period, Employee employee) {
		SQLQuery query = super.getSession().createSQLQuery("SELECT DISTINCT T.TIMESHEET_ID FROM TIMESHEET T, EMPLOYEE_PROJECT EP, PROJECT P, CLIENT C"
				+ " WHERE T.PERIOD_ID=" + period.getPeriodId()
				+ " AND EP.EMP_ID=" + employee.getEmpId()
				+ " AND T.SUBMIT_FLAG='Y' AND T.EMP_PROJECT_ID=EP.EMP_PROJ_ID AND EP.PROJECT_ID = P.PROJECT_ID AND P.CLIENT_ID = C.CLIENT_ID AND T.TASK_TYPE='RT' AND C.TIMESHEET_REMINDER_ENABLE='Y'");
		return (!query.list().isEmpty()) ? true : false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Integer> getCountOfTimesheetsByPeriod(Period period, Employee employee) {
		SQLQuery query = super.getSession().createSQLQuery("SELECT COUNT(DISTINCT T.TASK_DATE), C.SHORTNAME FROM TIMESHEET T, EMPLOYEE_PROJECT EP, PROJECT P, CLIENT C WHERE"
				+ " NOT EXISTS (SELECT 1 FROM HOLIDAYS WHERE HOLIDAY_DATE = T.TASK_DATE)"
				+ " AND T.PERIOD_ID=" + period.getPeriodId()
				+ " AND EP.EMP_ID=" + employee.getEmpId()
				+ " AND T.EMP_PROJECT_ID=EP.EMP_PROJ_ID AND EP.PROJECT_ID = P.PROJECT_ID AND P.CLIENT_ID = C.CLIENT_ID AND T.TASK_TYPE='RT' GROUP BY C.SHORTNAME");
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<Object[]> list = query.list();
		for (Object[] objectArray : list) {
			map.put((String) objectArray[1], (Integer) objectArray[0]);
		}
		return map;
	}
	@Override
	public void submitAllTaskInPeriod(Period otherPeriod, Employee employee) {
		try {
			SQLQuery query = super.getSession().createSQLQuery("UPDATE TIMESHEET T SET T.SUBMIT_FLAG='Y'"
					+ " WHERE T.PERIOD_ID=" + otherPeriod.getPeriodId()
					+ " AND T.EMP_PROJECT_ID IN (SELECT EMP_PROJ_ID FROM EMPLOYEE_PROJECT WHERE EMP_ID=" + employee.getEmpId() + ")");
			query.executeUpdate();
			
		} catch (RuntimeException re) {
			throw re;
		}
	}
	@Override
	public void unSubmitAllTaskInPeriod(Period otherPeriod, Employee employee) {
		try {
			SQLQuery query = super.getSession().createSQLQuery("UPDATE TIMESHEET T SET T.SUBMIT_FLAG='N'"
					+ " WHERE T.PERIOD_ID=" + otherPeriod.getPeriodId()
					+ " AND T.EMP_PROJECT_ID IN (SELECT EMP_PROJ_ID FROM EMPLOYEE_PROJECT WHERE EMP_ID=" + employee.getEmpId() + ")");
			query.executeUpdate();
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	@Override
	public void unSubmitAllTaskInPeriod(Period period, List<EmployeeProject> employeeProjects) {
		List<Timesheet> timesheets = findTimesheets(employeeProjects, period.getPeriodId(), null);
		for(Timesheet timesheet : timesheets){
			timesheet.setSubmitFlag("N");
			save(timesheet);
		}
	}
	
	@Override
	public void submitAllTaskInPeriod(Period period, List<EmployeeProject> employeeProjects) {
		List<Timesheet> timesheets = findTimesheets(employeeProjects, period.getPeriodId(), null);
		for(Timesheet timesheet : timesheets){
			timesheet.setSubmitFlag("Y");
			timesheet.setStatus("true");
			save(timesheet);
		}
	}
}