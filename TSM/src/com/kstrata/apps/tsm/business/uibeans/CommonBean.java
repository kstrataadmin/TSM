package com.kstrata.apps.tsm.business.uibeans;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;

import org.springframework.beans.BeanUtils;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Period;
import com.kstrata.apps.tsm.business.dao.entity.Project;
import com.kstrata.apps.tsm.business.dao.entity.Timesheet;
import com.kstrata.apps.tsm.business.model.JRTimesheet;
import com.kstrata.apps.tsm.business.model.UITimesheetRow;
import com.kstrata.apps.tsm.business.service.ClientService;
import com.kstrata.apps.tsm.business.service.EmployeeProjectService;
import com.kstrata.apps.tsm.business.service.EmployeeService;
import com.kstrata.apps.tsm.business.service.PeriodService;
import com.kstrata.apps.tsm.business.service.ProjectService;
import com.kstrata.apps.tsm.business.service.TimesheetService;
import com.kstrata.apps.tsm.business.util.CalendarDataModelImpl;
import com.kstrata.apps.tsm.business.util.SelectItemComparator;
import com.kstrata.apps.tsm.business.util.TimesheetComparator;
import com.kstrata.apps.tsm.business.util.UITimesheetComparator;

@SuppressWarnings("deprecation")
@ManagedBean(name = "commonBean")
@ViewScoped
public class CommonBean  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Resource
	protected EmployeeProjectService employeeProjectService;
	@Resource
	protected EmployeeService employeeService;
	@Resource
	protected TimesheetService timesheetService;
	@Resource
	protected ProjectService projectService;
	@Resource
	protected PeriodService periodService;
	@Resource
	protected ClientService clientService;
	
	protected Employee employee;
	protected CalendarDataModelImpl calendarModel;
	protected List<EmployeeProject> employeeProjects = new ArrayList<EmployeeProject>();
	protected boolean fromFetchTimesheets = false;
	
	protected Period currentPeriod;
	protected Period otherPeriod;
	protected String periodName;

	protected List<SelectItem> periodNames = new ArrayList<SelectItem>();
	protected Set<String> periodNamesString = new HashSet<String>();
	protected List<SelectItem> projectNames = null;
	protected Set<String> projectNamesString = null;
	protected boolean checkAllStatus;
	protected boolean addTaskStatus;

	protected boolean adminMode = false;
	protected boolean submitStatus = false;
	protected String taskDesc;

	protected Calendar start = Calendar.getInstance();
	protected Calendar end = Calendar.getInstance();
	protected UITimesheetRow tobedeleted = new UITimesheetRow();
	protected Period adminPeriod;
	protected Map<Integer, Client> projectClientMap = new HashMap<Integer, Client>();

	JasperPrint jasperPrint = null;
	protected String errorMessage;
	
	private List<UITimesheetRow> timesheetrows = null;
	private List<UITimesheetRow> newTimesheetrows = null;
	private boolean displayOTCheckBox;
	
	@PostConstruct
	public void init() {
		setTimesheetrows(new ArrayList<UITimesheetRow>());
		setNewTimesheetrows(new ArrayList<UITimesheetRow>());
		projectNames = new ArrayList<SelectItem>();
		projectNamesString = new HashSet<String>();

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String adminMode = (String) facesContext.getExternalContext().getSessionMap().get("adminMode");

		if (adminMode != null) {
			if (adminMode == "true") {
				employee = (Employee) facesContext.getExternalContext().getSessionMap().get("employeeForAdmin");
				setAdminMode(true);
				adminPeriod = (Period) facesContext.getExternalContext().getSessionMap().get("periodForAdmin");
				prepareTimesheetDataForFirstTime();
				
				fetchTimesheets();
			}
		} else {
			employee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");
			prepareTimesheetDataForFirstTime();
		}
	}
	
	public void initialize() {
		timesheetrows = null;
		projectNames = null;
		projectNamesString = null;
		setTimesheetrows(new ArrayList<UITimesheetRow>());
		projectNames = new ArrayList<SelectItem>();
		projectNamesString = new HashSet<String>();
	}
	
	public void approveTask(UITimesheetRow uiTimesheetRow) {
		hideCommentsDescDiv();
		Timesheet timesheet = uiTimesheetRow.getTimesheet();
		timesheet.setMngrStatus("true");
		timesheet = timesheetService.save(timesheet);

		for (UITimesheetRow timesheetRow : getTimesheetrows()) {
			if (timesheetRow.getTimesheet().getTimesheetId() != null
					&& timesheetRow.getTimesheet().getTimesheetId()
							.equals(timesheet.getTimesheetId())) {
				timesheetRow.setTimesheet(timesheet);
				timesheetRow.setEditStatus(false);
				timesheetRow.setDeleteStatus(false);
				timesheetRow.setApproveStatus("Approved");
				timesheetRow.setShowApproveLink(false);
				timesheetRow.setShowDenyLink(true);
			}
		}
	}
	
	public void denyTask(UITimesheetRow uiTimesheetRow) {
		hideCommentsDescDiv();
		Timesheet timesheet = uiTimesheetRow.getTimesheet();
		timesheet.setMngrStatus("false");
		timesheet = timesheetService.save(timesheet);

		for (UITimesheetRow timesheetRow : getTimesheetrows()) {
			if (timesheetRow.getTimesheet().getTimesheetId() != null
					&& timesheetRow.getTimesheet().getTimesheetId()
							.equals(timesheet.getTimesheetId())) {
				timesheetRow.setTimesheet(timesheet);
				timesheetRow.setEditStatus(true);
				timesheetRow.setDeleteStatus(true);
				timesheetRow.setApproveStatus("Not Approved");
				timesheetRow.setShowApproveLink(true);
				timesheetRow.setShowDenyLink(false);
			}
		}
	}
	
	protected void prepareTimesheetDataForFirstTime() {
		setAddTaskStatus(true);
		setCurrentPeriod();
		prepareUITimesheetRows();
		sortTimesheetrows();
	}
	
	public void prepareUITimesheetRows() {
		int periodCount = 0;
		prepareEmployeeProjects();
		for (EmployeeProject employeeProjecttemp : employeeProjects) {
			periodCount = periodCount + employeeProjecttemp.getTimesheets().size();
		}
		if (periodCount > 0) {
			Set<SelectItem> tempProjectNames = new HashSet<SelectItem>();
			for (EmployeeProject employeeProjecttemp : employeeProjects) {
				if (!projectNamesString.contains(employeeProjecttemp.getProject().getProjectDesc())) {
					tempProjectNames.add(new SelectItem(employeeProjecttemp.getProject().getProjectId(), employeeProjecttemp.getProject().getProjectDesc()
							+ " (" + employeeProjecttemp.getProject().getClient().getClientName() + ")"));
					projectNamesString.add(employeeProjecttemp.getProject().getProjectDesc());
					projectClientMap.put(employeeProjecttemp.getProject().getProjectId(), employeeProjecttemp.getProject().getClient());
				}
				List<Timesheet> timesheets = employeeProjecttemp.getTimesheets();

				for (Timesheet timesheet : timesheets) {
					if (adminMode) {
						addPeriodToUserPage(timesheet);
					}
					if (isFromFetchTimesheets()) {
						if (timesheet.getPeriod().getPeriodId().equals(getOtherPeriod().getPeriodId())) {
							createUITimesheetRowAndAddToTimesheetRows(timesheet, employeeProjecttemp);
						}
					} else {
						if (timesheet.getPeriod().getPeriodId().equals(getCurrentPeriod().getPeriodId())) {
							createUITimesheetRowAndAddToTimesheetRows(timesheet, employeeProjecttemp);
						}
					}
				}
			}
			convertProjectNamesToListAndSort(tempProjectNames);
		}
		if (timesheetService.getSubmitFlagOfTimesheetByPeriod(otherPeriod, employee)) {
			setAddTaskStatus(false);
			setSubmitStatus(true);
		} else {
			setSubmitStatus(false);
		}
	}
	
	private void convertProjectNamesToListAndSort(Set<SelectItem> tempProjectNames) {
		projectNames.addAll(tempProjectNames);
		Collections.sort(projectNames, new SelectItemComparator());
	}
	
	protected void validateTotal(UITimesheetRow uiTimesheetRow) {
		if (!uiTimesheetRow.isErrorsPresent()) {
			if (uiTimesheetRow.getTotal().compareTo(new BigDecimal(0)) != 1) {
				uiTimesheetRow.setErrorMessage("Total Hours can not be Zero");
				uiTimesheetRow.setErrorsPresent(true);
			}
		}
	}
	
	public void decideProjectclientEditMode(UITimesheetRow uiTimesheetRow) {
		List<Timesheet> timesheets = getOvertimeTimesheets(uiTimesheetRow);
		if (timesheets.size() != 0) {
			uiTimesheetRow.setProjectclientEditMode(false);
		} else {
			uiTimesheetRow.setProjectclientEditMode(true);
		}
	}
	
	public void validateForOvertimeReasonExistanceOnDelete(UITimesheetRow uiTimesheetRow) {
		List<Timesheet> timesheets = getOvertimeTimesheets(uiTimesheetRow);
		if (timesheets.size() != 0) {
			uiTimesheetRow.setErrorMessage("Overtime Reason Exists");
			uiTimesheetRow.setErrorsPresent(true);
		}
	}
	
	private List<Timesheet> getOvertimeTimesheets(UITimesheetRow uiTimesheetRow) {
		Project project = projectService.findById(uiTimesheetRow.getProjectId());
		EmployeeProject employeeProject = employeeProjectService.findEmployeeProject(project, employee);
		return timesheetService.findTimesheetsForOverTime(employeeProject, uiTimesheetRow.getTaskDate());
	}
	
	public void saveTask(UITimesheetRow uiTimesheetRow) {
		uiTimesheetRow.setErrorMessage("");
		uiTimesheetRow.setErrorsPresent(false);
		validateForSimilarTaskExistence(uiTimesheetRow);
		validateTaskDate(uiTimesheetRow);
		validateFromTo(uiTimesheetRow);
		removeUnWantedContentFromDescriptionIfExists(uiTimesheetRow);
		validateDecription(uiTimesheetRow);
		hideCommentsDescDiv();
		calculateTotal(uiTimesheetRow);
		validateTotal(uiTimesheetRow);
		if (!uiTimesheetRow.isErrorsPresent()) {
			Timesheet savedTimesheet = saveSingleTimesheet(uiTimesheetRow, "RT");
			if (!uiTimesheetRow.isNewTask()) {
				for (UITimesheetRow timesheetRow : getTimesheetrows()) {
					if (timesheetRow.getTimesheet().getTimesheetId() != null
							&& timesheetRow.getTimesheet().getTimesheetId().equals(savedTimesheet.getTimesheetId())) {
						timesheetRow.setTimesheet(savedTimesheet);
						timesheetRow.setSaveStatus("false");
						timesheetRow.setEditStatus(true);
						timesheetRow.setConfirmStatus("Confirmed");
						timesheetRow.setEditMode(false);
						if (isAdminMode()) {
							timesheetRow.setShowApproveLink(true);
						}
					}
				}
			} else {
				getNewTimesheetrows().remove(uiTimesheetRow);
				uiTimesheetRow.setTimesheet(savedTimesheet);
				uiTimesheetRow.setSaveStatus("false");
				uiTimesheetRow.setEditStatus(true);
				uiTimesheetRow.setDeleteStatus(true);
				uiTimesheetRow.setErrorMessage("");
				uiTimesheetRow.setConfirmStatus("Confirmed");
				uiTimesheetRow.setEditMode(false);
				//uiTimesheetRow.setProjectclientEditMode(false);
				uiTimesheetRow.setNewTask(false);

				uiTimesheetRow.setTaskDate(savedTimesheet.getTaskDate());
				uiTimesheetRow.setStartTime(savedTimesheet.getStartTime());
				uiTimesheetRow.setEndTime(savedTimesheet.getEndTime());
				
				uiTimesheetRow.setDisplay("none");
				uiTimesheetRow.setCommentsDisplay("none");
				uiTimesheetRow.setComments(new String(savedTimesheet.getComments()));
				uiTimesheetRow.setTaskDesc(new String(savedTimesheet.getTaskDesc()));
				
				uiTimesheetRow.setTotal(savedTimesheet.getTotal());
				if (isAdminMode()) {
					uiTimesheetRow.setShowApproveLink(true);
				}
			}
		}
	}
	
	protected void validateForSimilarTaskExistence(UITimesheetRow uiTimesheetRow) {
		if (otherPeriod.getPeriodId()>=70) {
			for (UITimesheetRow timesheetRow: getTimesheetrows()) {
				if (!timesheetRow.isNewTask() && !timesheetRow.getTimesheet().getTimesheetId().equals(uiTimesheetRow.getTimesheet().getTimesheetId())) {
					if (timesheetRow.getProjectId().equals(uiTimesheetRow.getProjectId()) && uiTimesheetRow.getTaskDate().compareTo(timesheetRow.getTaskDate()) == 0) {
						uiTimesheetRow.setErrorMessage("Single Task For Project On A Day");
						uiTimesheetRow.setErrorsPresent(true);
					}
				}
			}
		}
	}
	
	public void changeProject(UITimesheetRow uiTimesheetRow) {
		calculateTotal(uiTimesheetRow);
	}
	
	public void changeTaskDate(UITimesheetRow uiTimesheetRow) {
		uiTimesheetRow.setErrorsPresent(false);
		validateTaskDate(uiTimesheetRow);
		if (!uiTimesheetRow.isErrorsPresent()) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(uiTimesheetRow.getStartTime());
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.AM_PM, Calendar.AM);
			
			Calendar endTime = Calendar.getInstance();
			endTime.setTime(uiTimesheetRow.getEndTime());
			endTime.set(Calendar.DATE, calendar.get(Calendar.DATE));
			endTime.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
			endTime.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
			uiTimesheetRow.setEndTime(endTime.getTime());
			uiTimesheetRow.setTaskDate(calendar.getTime());
			calculateTotal(uiTimesheetRow);
		}
	}

	protected Timesheet saveSingleTimesheet(UITimesheetRow uiTimesheetRow, String taskType) {
		Timesheet timesheet = uiTimesheetRow.getTimesheet();
		Project project = new Project(uiTimesheetRow.getProjectId());
		Client client = projectClientMap.get(project.getProjectId());
		project.setClient(client);
		EmployeeProject employeeProject = new EmployeeProject();
		employeeProject = employeeProjectService.findEmployeeProject(project, getEmployee());
		Period period = periodService.getPeriod(uiTimesheetRow.getTaskDate());

		timesheet.setEmployeeProject(employeeProject);
		timesheet.setPeriod(period);
		timesheet.setStatus("true");
		timesheet.setSubmitFlag("N");
		timesheet.setMngrStatus("false");
		timesheet.setTaskType(taskType);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(uiTimesheetRow.getStartTime());
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.AM_PM, Calendar.AM);
		  
		timesheet.setTaskDate(calendar.getTime());
		timesheet.setEndTime(uiTimesheetRow.getEndTime());
		timesheet.setStartTime(uiTimesheetRow.getStartTime());
		
		timesheet.setComments(uiTimesheetRow.getComments().getBytes());
		timesheet.setTaskDesc(uiTimesheetRow.getTaskDesc().getBytes());
		timesheet.setTotal(uiTimesheetRow.getTotal());
		timesheet.setMngrStatus(uiTimesheetRow.getMngrStatus());

		return getTimesheetService().save(timesheet);
	}
	
	protected void validateFromTo(UITimesheetRow uiTimesheetRow) {
		if (!uiTimesheetRow.isErrorsPresent()) {
			setStartAndEndTimes(uiTimesheetRow);
			if (!(end.compareTo(start) > 0)) {
				uiTimesheetRow.setErrorMessage("Wrong End time");
				uiTimesheetRow.setErrorsPresent(true);
			}
		}
	}
	
	public void editAllTask() {
		hideCommentsDescDiv();
		for (UITimesheetRow uiTimesheetRow : getTimesheetrows()) {
			if (!uiTimesheetRow.isNewTask()
					&& "Confirmed".equalsIgnoreCase(uiTimesheetRow.getConfirmStatus())
					&& "false".equalsIgnoreCase(uiTimesheetRow.getMngrStatus())) {
				Timesheet timesheet = uiTimesheetRow.getTimesheet();
				uiTimesheetRow.setConfirmStatus("UnConfirmed");
				uiTimesheetRow.setSaveStatus("true");
				uiTimesheetRow.setEditStatus(false);
				uiTimesheetRow.setEditMode(true);
				if (isAdminMode()) {
					uiTimesheetRow.setShowApproveLink(false);
				}
				timesheet.setStatus("false");
				getTimesheetService().save(timesheet);
				decideProjectclientEditMode(uiTimesheetRow);
			}
		}
		// fetchTimesheets();
	}
	
	public void addTask() {
		UITimesheetRow uiTimesheetRow = new UITimesheetRow();
		uiTimesheetRow.setProjectclientEditMode(true);
		uiTimesheetRow.setConfirmStatus("UnConfirmed");
		uiTimesheetRow.setNewTask(true);
		uiTimesheetRow.setApproveStatus("Not Approved");
		uiTimesheetRow.setEditMode(true);
		
		uiTimesheetRow.setDisplay("none");
		uiTimesheetRow.setCommentsDisplay("none");
		uiTimesheetRow.setErrorMessage("");
		uiTimesheetRow.setEditStatus(false);
		uiTimesheetRow.setDeleteStatus(false);
		uiTimesheetRow.setComments("");
		uiTimesheetRow.setTaskDesc("");
		uiTimesheetRow.setTaskDate(otherPeriod.getDateFrom());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(otherPeriod.getDateFrom());
		calendar.set(Calendar.HOUR, 9);
		uiTimesheetRow.setStartTime(calendar.getTime());
		calendar.set(Calendar.HOUR, 17);
		calendar.set(Calendar.MINUTE, 30);
		uiTimesheetRow.setEndTime(calendar.getTime());
		uiTimesheetRow.setMngrStatus("false");
		uiTimesheetRow.setSaveStatus("true");

		Timesheet timesheet = new Timesheet();
		timesheet.setPeriod(getOtherPeriod());
		Client client = new Client();
		Project project = new Project();
		project.setClient(client);
		EmployeeProject employeeProject = new EmployeeProject(project,getEmployee());
		timesheet.setEmployeeProject(employeeProject);

		uiTimesheetRow.setTimesheet(timesheet);

		int i = 0;
		Set<SelectItem> tempProjectNames = new HashSet<SelectItem>();
		for (EmployeeProject employeeProjecttemp : employeeProjects) {
			if (!projectNamesString.contains(employeeProjecttemp.getProject().getProjectDesc())) {
				tempProjectNames.add(new SelectItem(employeeProjecttemp.getProject().getProjectId(), 
							employeeProjecttemp.getProject().getProjectDesc() + " ("
							+ employeeProjecttemp.getProject().getClient().getClientName() + ")"));
				projectNamesString.add(employeeProjecttemp.getProject().getProjectDesc());
				projectClientMap.put(employeeProjecttemp.getProject().getProjectId(), 
									employeeProjecttemp.getProject().getClient());
			}
			if (i == 0) {
				uiTimesheetRow.setProjectId(employeeProjecttemp.getProject().getProjectId());
				uiTimesheetRow.setProjectDesc(employeeProjecttemp.getProject().getProjectDesc());
				uiTimesheetRow.setClientName(employeeProjecttemp.getProject().getClient().getClientName());
				i = 1;
			}
			uiTimesheetRow.getClients().add(employeeProjecttemp.getProject().getClient());
			uiTimesheetRow.getProjects().add(employeeProjecttemp.getProject());
		}
		convertProjectNamesToListAndSort(tempProjectNames);
		hideCommentsDescDiv();
		calculateTotal(uiTimesheetRow);
		getTimesheetrows().add(uiTimesheetRow);
		getNewTimesheetrows().add(uiTimesheetRow);
	}
	
	public void duplicateTask(UITimesheetRow uiTimesheetRowOld) {
		UITimesheetRow uiTimesheetRow = new UITimesheetRow();
		uiTimesheetRow.setProjectclientEditMode(true);
		uiTimesheetRow.setConfirmStatus("UnConfirmed");
		uiTimesheetRow.setNewTask(true);
		uiTimesheetRow.setApproveStatus("Not Approved");
		uiTimesheetRow.setEditMode(true);
		
		uiTimesheetRow.setDisplay("none");
		uiTimesheetRow.setCommentsDisplay("none");
		uiTimesheetRow.setErrorMessage("");
		uiTimesheetRow.setEditStatus(false);
		uiTimesheetRow.setDeleteStatus(false);
		uiTimesheetRow.setComments("");
		uiTimesheetRow.setTaskDesc("");
		
		Calendar taskDate = Calendar.getInstance();
		Calendar startTime = Calendar.getInstance();
		Calendar endTime = Calendar.getInstance();
		taskDate.setTime(uiTimesheetRowOld.getTaskDate());
		startTime.setTime(uiTimesheetRowOld.getStartTime());
		endTime.setTime(uiTimesheetRowOld.getEndTime());
		taskDate.add(Calendar.DATE, 1);
		startTime.add(Calendar.DATE, 1);
		endTime.add(Calendar.DATE, 1);
		
		if (taskDate.getTime().after(otherPeriod.getDateTo())) {
			taskDate.add(Calendar.DATE, -1);
			startTime.add(Calendar.DATE, -1);
			endTime.add(Calendar.DATE, -1);
		}
		
		uiTimesheetRow.setTaskDate(taskDate.getTime());
		uiTimesheetRow.setStartTime(startTime.getTime());
		uiTimesheetRow.setEndTime(endTime.getTime());
		
		uiTimesheetRow.setTotal(uiTimesheetRowOld.getTotal());
		uiTimesheetRow.setMngrStatus("false");
		uiTimesheetRow.setSaveStatus("true");

		Timesheet timesheet = new Timesheet();
		timesheet.setPeriod(getOtherPeriod());
		Client client = new Client();
		Project project = new Project();
		project.setClient(client);
		EmployeeProject employeeProject = new EmployeeProject(project,getEmployee());
		timesheet.setEmployeeProject(employeeProject);

		uiTimesheetRow.setTimesheet(timesheet);

		int i = 0;
		Set<SelectItem> tempProjectNames = new HashSet<SelectItem>();
		for (EmployeeProject employeeProjecttemp : employeeProjects) {
			if (!projectNamesString.contains(employeeProjecttemp.getProject()
					.getProjectDesc())) {
				tempProjectNames.add(new SelectItem(employeeProjecttemp.getProject().getProjectId(), 
						employeeProjecttemp.getProject().getProjectDesc() + " (" + employeeProjecttemp.getProject().getClient().getClientName() + ")"));
				projectNamesString.add(employeeProjecttemp.getProject().getProjectDesc());
				projectClientMap.put(employeeProjecttemp.getProject().getProjectId(), employeeProjecttemp.getProject().getClient());
			}
			if (i == 0) {
				uiTimesheetRow.setProjectDesc(employeeProjecttemp.getProject().getProjectDesc());
				uiTimesheetRow.setClientName(employeeProjecttemp.getProject().getClient().getClientName());
				i = 1;
			}
			uiTimesheetRow.getClients().add(employeeProjecttemp.getProject().getClient());
			uiTimesheetRow.getProjects().add(employeeProjecttemp.getProject());
		}
		uiTimesheetRow.setProjectId(uiTimesheetRowOld.getProjectId());
		uiTimesheetRow.setProjectDesc(uiTimesheetRowOld.getProjectDesc());
		uiTimesheetRow.setClientName(uiTimesheetRowOld.getClientName());
		
		convertProjectNamesToListAndSort(tempProjectNames);
		hideCommentsDescDiv();
		getTimesheetrows().add(uiTimesheetRow);
		getNewTimesheetrows().add(uiTimesheetRow);
	}

	protected void validateTaskDate(UITimesheetRow uiTimesheetRow) {
		if (!uiTimesheetRow.isErrorsPresent()) {
			if (uiTimesheetRow.getStartTime()==null) {
				uiTimesheetRow.setErrorMessage("Invalid Start Time");
				uiTimesheetRow.setErrorsPresent(true);
			}
			if (uiTimesheetRow.getEndTime()==null) {
				uiTimesheetRow.setErrorMessage("Invalid End Time");
				uiTimesheetRow.setErrorsPresent(true);
			}
		}
	}

	protected void validateDecription(UITimesheetRow uiTimesheetRow) {
		if (!uiTimesheetRow.isErrorsPresent()) {
			if (uiTimesheetRow.getTaskDesc().trim().equals("")) {
				uiTimesheetRow.setErrorMessage("No Description");
				uiTimesheetRow.setErrorsPresent(true);
			}
		}
	}
	
	public void calculateTotal(UITimesheetRow uiTimesheetRow) {
		validateFromTo(uiTimesheetRow);
		
		if (!uiTimesheetRow.isErrorsPresent()) {
			Long timeInMilliSec = uiTimesheetRow.getStartTime().getTime() - uiTimesheetRow.getEndTime().getTime();
			uiTimesheetRow.setTotal(getHourMinFormat(timeInMilliSec));
		}
		hideCommentsDescDiv();
	}
	
	public BigDecimal getHourMinFormat (Long timeInMilliSec) {
		Long diffHours = new Long(timeInMilliSec / (60 * 60 * 1000));
		Long diffMins = new Long((timeInMilliSec) / (60 * 1000));
		if (Math.abs(diffMins % 60) < 10)
			return (new BigDecimal(Math.abs(diffHours) + ".0" + Math.abs(diffMins % 60)));
		else
			return (new BigDecimal(Math.abs(diffHours) + "." + Math.abs(diffMins % 60)));
	}
	
	public void submitAllTaskInPeriod() {
		confirmAllTask("RT");
		timesheetService.submitAllTaskInPeriod(otherPeriod, employee);
		
		fetchTimesheets();
	}
	
	public void unSubmitAllTaskInPeriod() {
		timesheetService.unSubmitAllTaskInPeriod(otherPeriod, employee);
		setAddTaskStatus(true);
		
		fetchTimesheets();
	}
	
	public void approvePeriod() {
		timesheetService.approvePeriod(employee, otherPeriod);
		
		fetchTimesheets();
	}
	
	public void denyPeriod() {
		timesheetService.denyPeriod(employee, otherPeriod);
		
		fetchTimesheets();
	}

	public void confirmAllTask(String taskType){
		hideCommentsDescDiv();
		for(UITimesheetRow uiTimesheetRow: getTimesheetrows()){
			if(uiTimesheetRow.getConfirmStatus().equals("UnConfirmed")){
				uiTimesheetRow.setErrorMessage("");
				uiTimesheetRow.setErrorsPresent(false);
				validateForSimilarTaskExistence(uiTimesheetRow);
				removeUnWantedContentFromDescriptionIfExists(uiTimesheetRow);
				validateTaskDate(uiTimesheetRow);
				validateFromTo(uiTimesheetRow);
				validateDecription(uiTimesheetRow);
				calculateTotal(uiTimesheetRow);
				validateTotal(uiTimesheetRow);
				if(! uiTimesheetRow.isErrorsPresent()){
					Timesheet savedTimesheet = saveSingleTimesheet(uiTimesheetRow, taskType);
					if(!uiTimesheetRow.isNewTask()){
						for(UITimesheetRow timesheetRow: getTimesheetrows()){
							if(timesheetRow.getTimesheet().getTimesheetId()!=null && 
									timesheetRow.getTimesheet().getTimesheetId().equals(savedTimesheet.getTimesheetId())){
								timesheetRow.setTimesheet(savedTimesheet);
								timesheetRow.setSaveStatus("false");
								timesheetRow.setEditStatus(true);
								timesheetRow.setConfirmStatus("Confirmed");
								timesheetRow.setEditMode(false);
								if(isAdminMode()){
									timesheetRow.setShowApproveLink(true);
								}
							}
						}
					}else{
						getNewTimesheetrows().remove(uiTimesheetRow);
						uiTimesheetRow.setTimesheet(savedTimesheet);
						uiTimesheetRow.setSaveStatus("false");
						uiTimesheetRow.setEditStatus(true);
						uiTimesheetRow.setDeleteStatus(true);
						uiTimesheetRow.setErrorMessage("");
						uiTimesheetRow.setConfirmStatus("Confirmed");
						uiTimesheetRow.setEditMode(false);
						//uiTimesheetRow.setProjectclientEditMode(false);
						uiTimesheetRow.setNewTask(false);
						
						uiTimesheetRow.setFromhours(savedTimesheet.getStartTime().getHours());
						uiTimesheetRow.setFromminutes(savedTimesheet.getStartTime().getMinutes());
						uiTimesheetRow.setTohours(savedTimesheet.getEndTime().getHours());
						uiTimesheetRow.setTominutes(savedTimesheet.getEndTime().getMinutes());
						uiTimesheetRow.setDisplay("none");
						uiTimesheetRow.setCommentsDisplay("none");
						uiTimesheetRow.setComments(new String(savedTimesheet.getComments()));
						uiTimesheetRow.setTaskDesc(new String(savedTimesheet.getTaskDesc()));
						uiTimesheetRow.setTaskDate(savedTimesheet.getTaskDate());
						uiTimesheetRow.setTotal(savedTimesheet.getTotal());
						uiTimesheetRow.setTaskType(savedTimesheet.getTaskType());
						if(isAdminMode()){
							uiTimesheetRow.setShowApproveLink(true);
						}
					}
				}
			}
		}
		//fetchTimesheets();
	}
	
	protected void removeUnWantedContentFromDescriptionIfExists(UITimesheetRow uiTimesheetRow) {
		Pattern iFramePattern = Pattern.compile("<iframe.*</iframe>", Pattern.DOTALL);
		Pattern whiteSpaceParagraphsPattern = Pattern.compile("<p>\\s*</p>", Pattern.DOTALL);
		Matcher matcher = iFramePattern.matcher(uiTimesheetRow.getTaskDesc());
	    while (matcher.find()) {
	      uiTimesheetRow.setTaskDesc(matcher.replaceAll(""));
	    }
	    Matcher  matcher1 = whiteSpaceParagraphsPattern.matcher(uiTimesheetRow.getTaskDesc());
	    while (matcher1.find()) {
	      uiTimesheetRow.setTaskDesc(matcher1.replaceAll(""));
	    }
	}
	
	public void sortTimesheetrows() {
		Collections.sort(getTimesheetrows(), new UITimesheetComparator());
	}
	
	private void prepareEmployeeProjects() {
		employeeProjects = employeeProjectService.findEmployeeProjectByEmployee(getEmployee());
		for (EmployeeProject employeeProjecttemp : employeeProjects) {
			employeeProjecttemp.setTimesheets(timesheetService.findTimesheets(employeeProjecttemp));
		}
	}
	
	
	
	protected void addPeriodToUserPage(Timesheet timesheet) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy");
		String periodStart = dateFormat.format(timesheet.getPeriod().getDateFrom());
		String periodEnd = dateFormat.format(timesheet.getPeriod().getDateTo());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(timesheet.getPeriod().getDateFrom());
		if (!periodNamesString.contains(periodStart + " TO " + periodEnd)) {
			periodNames.add(new SelectItem(periodStart + " TO " + periodEnd));
			periodNamesString.add(periodStart + " TO " + periodEnd);
		}
	}
	
	protected void createUITimesheetRowAndAddToTimesheetRows(Timesheet timesheet, EmployeeProject employeeProjecttemp) {
		UITimesheetRow uiTimesheetRow = new UITimesheetRow(timesheet);
		
		uiTimesheetRow.setStartTime(timesheet.getStartTime());
		uiTimesheetRow.setEndTime(timesheet.getEndTime());
		uiTimesheetRow.setTaskDate(timesheet.getTaskDate());
		
		//uiTimesheetRow.setProjectclientEditMode(false);
		uiTimesheetRow.getClients().add(employeeProjecttemp.getProject().getClient());
		uiTimesheetRow.getProjects().add(employeeProjecttemp.getProject());
		uiTimesheetRow.setProjectName(timesheet.getEmployeeProject().getProject().getProjectName());
		uiTimesheetRow.setClientName(timesheet.getEmployeeProject().getProject().getClient().getClientName());
		uiTimesheetRow.setProjectDesc(timesheet.getEmployeeProject().getProject().getProjectDesc());
		uiTimesheetRow.setProjectId(timesheet.getEmployeeProject().getProject().getProjectId());
		uiTimesheetRow.setDisplay("none");
		uiTimesheetRow.setCommentsDisplay("none");
		uiTimesheetRow.setErrorMessage("");
		uiTimesheetRow.setEditStatus(true);
		uiTimesheetRow.setComments(new String(timesheet.getComments()));
		uiTimesheetRow.setTaskDesc(new String(timesheet.getTaskDesc()));
		uiTimesheetRow.setTotal(timesheet.getTotal());
		uiTimesheetRow.setMngrStatus(timesheet.getMngrStatus());
		if (timesheet.getMngrStatus().equals("true")) {
			uiTimesheetRow.setSaveStatus("false");
			uiTimesheetRow.setEditStatus(false);
			uiTimesheetRow.setDeleteStatus(false);
			if (isAdminMode()) {
				uiTimesheetRow.setShowDenyLink(true);
			}
		} else {
			if (timesheet.getStatus().equals("true")) {
				uiTimesheetRow.setSaveStatus("false");
				uiTimesheetRow.setEditStatus(true);
				if (isAdminMode()) {
					uiTimesheetRow.setShowApproveLink(true);
				}
			} else {
				uiTimesheetRow.setSaveStatus("true");
				uiTimesheetRow.setEditStatus(false);
			}
			uiTimesheetRow.setDeleteStatus(true);
		}
		decideProjectclientEditMode(uiTimesheetRow);
		getTimesheetrows().add(uiTimesheetRow);
	}

	public void fetchTimesheets() {
		initialize();
		//StringTokenizer stringTokenizer = new StringTokenizer(periodName, "TO");
		setOtherPeriod(periodService.getPeriod(new Date(periodName.split("TO")[1].trim())));
		setDisplayOTCheckBox(false);
		if (otherPeriod.getPeriodId()>=81) {
			setDisplayOTCheckBox(true);
		}
		setFromFetchTimesheets(true);
		if (isAdminMode()
				|| otherPeriod.getPeriodId().equals(currentPeriod.getPeriodId())
				|| otherPeriod.getPeriodId().equals(currentPeriod.getPeriodId() - 1)
				|| otherPeriod.getPeriodId().equals(currentPeriod.getPeriodId() - 2)) {
			setAddTaskStatus(true);
		} else {
			setAddTaskStatus(false);
		}
		prepareUITimesheetRows();
		hideCommentsDescDiv();
		sortTimesheetrows();
		setFromFetchTimesheets(false);
	}
	
	public void generateReportForSingleEmployee() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext externalContext = fc.getExternalContext();
		String path = externalContext.getRealPath("/jasper");
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		response.setHeader("Content-disposition", "attachement; filename= " + periodName.replaceAll(" ", "-") + ".xls");
		response.setContentType("application/vnd.ms-excel");
		if (getTimesheetrows().size() != 0) {
			if (employee != null) {
				List<Timesheet> timesheets = new ArrayList<Timesheet>();
				for (UITimesheetRow uiTimesheetRow : getTimesheetrows()) {
					if (!uiTimesheetRow.isNewTask()) {
						timesheets.add(uiTimesheetRow.getTimesheet());
					}
				}
				if (timesheets.size() != 0) {
					try {
						prepareJP(new JRBeanArrayDataSource(convertToJRTimesheetRows(timesheets).toArray()), path);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		try {
			if (null == jasperPrint) {
				setErrorMessage("No Timesheets for selected period");
				return;
			}
			exportToXLS(response, fc);
		} catch (JRException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void prepareJP(JRBeanArrayDataSource dataSource, String path)
			throws Exception {
		Map map = new HashMap();
		map.put("SUBREPORT_DIR", path);
		jasperPrint = JasperFillManager.fillReport(new BufferedInputStream(
				new FileInputStream(new File(path + "/timesheet.jasper"))),
				null, dataSource);
	}
	
	private String getMapKey(Date taskDate) {
		String key = "";
		try {
			DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
			key = dateFormat.format(taskDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}
	
	private String formatDateShowingOnlyTime(Date date) {
		String s = null;
		try {
			DateFormat df = new SimpleDateFormat("HHmm 'hrs'");
			s = df.format(date);
		} catch (Exception e) {

		}
		return s;
	}

	private String formatDate(Date date) {
		String s = null;
		try {
			DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
			s = df.format(date);
		} catch (Exception e) {

		}
		return s;
	}
	
	private List<JRTimesheet> convertToJRTimesheetRows(
			List<Timesheet> timesheets) {
		List<JRTimesheet> jrTimeSheetList = new ArrayList<JRTimesheet>();
		Map<String, List<JRTimesheet>> map = new TreeMap<String, List<JRTimesheet>>();
		long timeDiff = 0L;
		for (Timesheet timesheet : timesheets) {
			String key = getMapKey(timesheet.getTaskDate());
			JRTimesheet jrTimesheet = new JRTimesheet();
			BeanUtils.copyProperties(timesheet, jrTimesheet);
			jrTimesheet.setDateFromString(formatDate(getOtherPeriod()
					.getDateFrom()));
			jrTimesheet
					.setDateToString(formatDate(getOtherPeriod().getDateTo()));
			jrTimesheet.setTaskDateString(formatDate(timesheet.getTaskDate()));

			jrTimesheet.setStartTimeString(formatDateShowingOnlyTime(timesheet
					.getStartTime()));
			jrTimesheet.setEndTimeString(formatDateShowingOnlyTime(timesheet
					.getEndTime()));

			jrTimesheet.setClientName(timesheet.getEmployeeProject()
					.getProject().getClient().getClientName());
			jrTimesheet.setProjectName(timesheet.getEmployeeProject()
					.getProject().getProjectName());
			jrTimesheet.setDesignation(timesheet.getEmployeeProject()
					.getEmployee().getEmpDesignation());
			jrTimesheet.setEmpFirstName(timesheet.getEmployeeProject()
					.getEmployee().getEmpFirstName());
			jrTimesheet.setEmpLastName(timesheet.getEmployeeProject()
					.getEmployee().getEmpLastName());
			jrTimesheet.setEmployeeRef(timesheet.getEmployeeProject()
					.getEmployee().getEmployeeRef());
			jrTimesheet.setShowClient(false);

			if (!map.containsKey(key)) {
				map.put(key, new ArrayList<JRTimesheet>());
				map.get(key).add(jrTimesheet);
			} else {
				timeDiff = 0L;
				List<JRTimesheet> jrTimesheets = map.get(key);
				Calendar start = Calendar.getInstance();
				Calendar end = Calendar.getInstance();
				jrTimesheets.add(jrTimesheet);
				for (JRTimesheet jrt : jrTimesheets) {
					start.setTime(jrt.getStartTime());
					end.setTime(jrt.getEndTime());
					timeDiff += (end.getTimeInMillis() - start
							.getTimeInMillis());
				}
				jrTimesheets.get(0).setTotal(calTotal(timeDiff));

			}

		}
		for (String taskDate : map.keySet()) {
			List<JRTimesheet> jrTimesheets = map.get(taskDate);
			int i = 0;
			for (JRTimesheet jrTimesheet : jrTimesheets) {
				if (i > 0) {
					jrTimesheet.setTotal(null);
				}
				i++;
				jrTimeSheetList.add(jrTimesheet);
			}
		}
		Collections.sort(jrTimeSheetList, new TimesheetComparator());
		return jrTimeSheetList;
	}
	
	private BigDecimal calTotal(long timeDiff) {
		Long diffHours = new Long((timeDiff) / (60 * 60 * 1000));
		Long diffMins = new Long((timeDiff) / (60 * 1000));
		if (Math.abs(diffMins % 60) < 10)
			return new BigDecimal(Math.abs(diffHours) + ".0"
					+ Math.abs(diffMins % 60));
		else
			return new BigDecimal(Math.abs(diffHours) + "."
					+ Math.abs(diffMins % 60));
	}

	private void exportToXLS(HttpServletResponse response, FacesContext fc)
			throws JRException, IOException {
		try {
			// Add the response headers
			if (jasperPrint != null) {
				ServletOutputStream servletOutputStream = response
						.getOutputStream();
				JRXlsExporter exporter = new JRXlsExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT,
						jasperPrint);
				exporter.setParameter(
						JRXlsAbstractExporterParameter.OUTPUT_STREAM,
						servletOutputStream);
				exporter.setParameter(
						JRXlsAbstractExporterParameter.IS_COLLAPSE_ROW_SPAN,
						Boolean.TRUE);
				exporter.setParameter(
						JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,
						Boolean.TRUE);
				exporter.setParameter(
						JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
						Boolean.TRUE);
				exporter.setParameter(
						JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET,
						Boolean.FALSE);
				exporter.setParameter(
						JRXlsAbstractExporterParameter.IS_DETECT_CELL_TYPE,
						Boolean.TRUE);
				/*
				 * exporter.setParameter(JRXlsAbstractExporterParameter.
				 * IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
				 * exporter.setParameter
				 * (JRXlsAbstractExporterParameter.IS_IGNORE_GRAPHICS,
				 * Boolean.TRUE);
				 */
				exporter.exportReport();
				servletOutputStream.flush();
				fc.responseComplete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JRException jEx) {
			jEx.printStackTrace();
		} catch (Exception jEx) {
			jEx.printStackTrace();
		} finally {
		}
	}

	public void showDescription(UITimesheetRow uiTimesheetRow) {
		hideCommentsDescDiv();
		uiTimesheetRow.setDisplay("block");
	}

	public void showComments(UITimesheetRow uiTimesheetRow) {
		hideCommentsDescDiv();
		uiTimesheetRow.setCommentsDisplay("block");
	}

	public void editTask(UITimesheetRow uiTimesheetRow) {
		Timesheet uitimesheet = uiTimesheetRow.getTimesheet();
		uitimesheet.setStatus("false");
		// fetchTimesheets();
		hideCommentsDescDiv();
		Timesheet timesheet = (getTimesheetService().save(uitimesheet));
		decideProjectclientEditMode(uiTimesheetRow);
		for (UITimesheetRow timesheetRow : getTimesheetrows()) {
			if (timesheetRow.getTimesheet().getTimesheetId() != null
					&& timesheetRow.getTimesheet().getTimesheetId()
							.equals(timesheet.getTimesheetId())) {
				timesheetRow.setTimesheet(timesheet);
				timesheetRow.setSaveStatus("true");
				timesheetRow.setEditStatus(false);
				timesheetRow.setConfirmStatus("UnConfirmed");
				timesheetRow.setEditMode(true);
				if (isAdminMode()) {
					timesheetRow.setShowApproveLink(false);
					timesheetRow.setShowDenyLink(false);
				}
			}
		}
	}
	
	public void hideCommentsDescDiv() {
		for (UITimesheetRow timesheetRow : getTimesheetrows()) {
			timesheetRow.setDisplay("none");
			timesheetRow.setCommentsDisplay("none");
		}
	}

	public void setStartAndEndTimes(UITimesheetRow uiTimesheetRow) {
		start.setTime(uiTimesheetRow.getStartTime());
		end.setTime(uiTimesheetRow.getEndTime());
	}
	
	public EmployeeProjectService getEmployeeProjectService() {
		return employeeProjectService;
	}

	public void setEmployeeProjectService(
			EmployeeProjectService employeeProjectService) {
		this.employeeProjectService = employeeProjectService;
	}

	public EmployeeService getEmployeeService() {
		return employeeService;
	}

	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public TimesheetService getTimesheetService() {
		return timesheetService;
	}

	public void setTimesheetService(TimesheetService timesheetService) {
		this.timesheetService = timesheetService;
	}

	public ProjectService getProjectService() {
		return projectService;
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	public PeriodService getPeriodService() {
		return periodService;
	}

	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}

	public ClientService getClientService() {
		return clientService;
	}

	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void setCalendarModel(CalendarDataModelImpl calendarModel) {
		this.calendarModel = calendarModel;
	}

	public List<EmployeeProject> getEmployeeProjects() {
		return employeeProjects;
	}

	public void setEmployeeProjects(List<EmployeeProject> employeeProjects) {
		this.employeeProjects = employeeProjects;
	}

	public boolean isFromFetchTimesheets() {
		return fromFetchTimesheets;
	}

	public void setFromFetchTimesheets(boolean fromFetchTimesheets) {
		this.fromFetchTimesheets = fromFetchTimesheets;
	}

	public Period getCurrentPeriod() {
		return currentPeriod;
	}

	public void setCurrentPeriod() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -100);
		Date currentDate = calendar.getTime();
		List<Period> periods = new ArrayList<Period>();
		if (adminMode == true) {
			this.currentPeriod = adminPeriod;
		} else {
			// this.currentPeriod = periodDAO.getPeriod(currentDate);
			periods = periodService.getPeriodsForDate(currentDate);
			currentPeriod = periods.get(0);
		}
		setOtherPeriod(this.currentPeriod);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy");
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentPeriod.getDateFrom());
		setPeriodName(dateFormat.format(currentPeriod.getDateFrom()) + " TO " + dateFormat.format(currentPeriod.getDateTo()));
		if (!adminMode) {
			for (Period period : periods) {
				periodNames.add(new SelectItem(dateFormat.format(period.getDateFrom()) + " TO " + dateFormat.format(period.getDateTo())));
			}
		}
		/*
		 * if(! periodNamesString.contains(periodName)){ periodNames.add(new
		 * SelectItem(periodName)); periodNamesString.add(periodName); }
		 */
	}

	public Period getOtherPeriod() {
		return otherPeriod;
	}

	public void setOtherPeriod(Period period) {
		this.otherPeriod = period;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy");
		Calendar cal = Calendar.getInstance();
		cal.setTime(otherPeriod.getDateFrom());
		setPeriodName(dateFormat.format(otherPeriod.getDateFrom()) + " TO " + dateFormat.format(otherPeriod.getDateTo()));
	}

	public String getPeriodName() {
		return periodName;
	}

	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}

	public List<SelectItem> getPeriodNames() {
		return periodNames;
	}

	public void setPeriodNames(List<SelectItem> periodNames) {
		this.periodNames = periodNames;
	}

	public Set<String> getPeriodNamesString() {
		return periodNamesString;
	}

	public void setPeriodNamesString(Set<String> periodNamesString) {
		this.periodNamesString = periodNamesString;
	}

	public List<SelectItem> getProjectNames() {
		return projectNames;
	}

	public void setProjectNames(List<SelectItem> projectNames) {
		this.projectNames = projectNames;
	}

	public Set<String> getProjectNamesString() {
		return projectNamesString;
	}

	public void setProjectNamesString(Set<String> projectNamesString) {
		this.projectNamesString = projectNamesString;
	}

	public boolean isCheckAllStatus() {
		return checkAllStatus;
	}

	public void setCheckAllStatus(boolean checkAllStatus) {
		this.checkAllStatus = checkAllStatus;
	}

	public boolean isAddTaskStatus() {
		return addTaskStatus;
	}

	public void setAddTaskStatus(boolean addTaskStatus) {
		this.addTaskStatus = addTaskStatus;
	}

	public boolean isAdminMode() {
		return adminMode;
	}

	public void setAdminMode(boolean adminMode) {
		this.adminMode = adminMode;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public Calendar getStart() {
		return start;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}

	public UITimesheetRow getTobedeleted() {
		return tobedeleted;
	}

	public void setTobedeleted(UITimesheetRow tobedeleted) {
		this.tobedeleted = tobedeleted;
	}

	public Period getAdminPeriod() {
		return adminPeriod;
	}

	public void setAdminPeriod(Period adminPeriod) {
		this.adminPeriod = adminPeriod;
	}

	public Map<Integer, Client> getProjectClientMap() {
		return projectClientMap;
	}

	public void setProjectClientMap(Map<Integer, Client> projectClientMap) {
		this.projectClientMap = projectClientMap;
	}

	public JasperPrint getJasperPrint() {
		return jasperPrint;
	}

	public void setJasperPrint(JasperPrint jasperPrint) {
		this.jasperPrint = jasperPrint;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public CalendarDataModelImpl getCalendarModel() {
		try {
			List<Date> dates = new ArrayList<Date>();
			Calendar c = Calendar.getInstance();
			c.setTime(getOtherPeriod().getDateFrom());
			while (!c.getTime().after(getOtherPeriod().getDateTo())) {
				dates.add(c.getTime());
				c.add(Calendar.DATE, 1);
			}
			calendarModel = new CalendarDataModelImpl(dates);
		}// end try
		catch (Exception ex) {
			ex.printStackTrace();
		}// end catch
		return calendarModel;
	}

	public List<UITimesheetRow> getTimesheetrows() {
		return timesheetrows;
	}

	public void setTimesheetrows(List<UITimesheetRow> timesheetrows) {
		this.timesheetrows = timesheetrows;
	}

	public List<UITimesheetRow> getNewTimesheetrows() {
		return newTimesheetrows;
	}

	public void setNewTimesheetrows(List<UITimesheetRow> newTimesheetrows) {
		this.newTimesheetrows = newTimesheetrows;
	}
	
	protected void finalize() throws Throwable { 
		timesheetrows = null;
		projectNames = null;
		projectNamesString = null;
	 }

	public boolean isDisplayOTCheckBox() {
		return displayOTCheckBox;
	}

	public void setDisplayOTCheckBox(boolean displayOTCheckBox) {
		this.displayOTCheckBox = displayOTCheckBox;
	}

	public boolean isSubmitStatus() {
		return submitStatus;
	}

	public void setSubmitStatus(boolean submitStatus) {
		this.submitStatus = submitStatus;
	}

}
