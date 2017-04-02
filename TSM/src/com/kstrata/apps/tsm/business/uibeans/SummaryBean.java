package com.kstrata.apps.tsm.business.uibeans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Period;
import com.kstrata.apps.tsm.business.dao.entity.Timesheet;
import com.kstrata.apps.tsm.business.job.TimesheetReminder;
import com.kstrata.apps.tsm.business.model.UITimesheetRow;
import com.kstrata.apps.tsm.business.service.EmployeeProjectService;
import com.kstrata.apps.tsm.business.service.EmployeeService;
import com.kstrata.apps.tsm.business.service.PeriodService;
import com.kstrata.apps.tsm.business.service.TimesheetService;

@SuppressWarnings("deprecation")
@ManagedBean(name="summaryBean")
@ViewScoped
public class SummaryBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Resource
	private TimesheetService timesheetService;
	@Resource
	private EmployeeProjectService employeeProjectService;
	@Resource
	private PeriodService periodService;
	@Resource
	private EmployeeService employeeService;
	@Resource
	private TimesheetReminder timesheetReminder;
	
	private List<UITimesheetRow> uitimesheetRows = new ArrayList<UITimesheetRow>();
	private List<Timesheet> timesheets = new ArrayList<Timesheet>();
	private List<Period> availablePeriods = null;
	private List<Period> requiredPeriods;
	
	private Employee currentEmployee;
	
	private String approveStatus = "Not Approved";
	private Set<SelectItem> approveStatuses = null;
	private String periodName = "";
	private Set<SelectItem> periodNames;
	private Set<String> periodNamesString;
	private String employeeName = "";
	private Set<SelectItem> employeeNames;
	private List<Employee> employees;
	private List<Employee> requiredEmployees;
	
	private String taskType="RT";
	
	private boolean selectAllForApproval = false;
	
	@PostConstruct
	public void init(){
		prepareApproveStatuses();
		//prepareEmployeeNames();
		preparePeriodNames();
		prepareEmployees();
		prepareuitimesheetRowsFirstTime();
	}
	private void prepareEmployees(){
		employees = employeeService.getActiveEmployees();
	}
	
	public void selectAllForApproval() {
		if (selectAllForApproval) {
			for (UITimesheetRow row : uitimesheetRows) {
				row.setSelectRowForApproval(true);
			}
		} else {
			for (UITimesheetRow row : uitimesheetRows) {
				row.setSelectRowForApproval(false);
			}
		}
	}
	
	public void selectRowForApproval(UITimesheetRow timesheetRow) {
		if (!timesheetRow.isSelectRowForApproval()) {
			selectAllForApproval = false;
		}
	}
	
	public void approveSelectedRows() {
		for (UITimesheetRow row : uitimesheetRows) {
			if (row.isSelectRowForApproval()) {
				approveSingleRow(row);
			}
		}
		
		fetchTimesheets();
	}
	
	public void radioSelection(){
	    prepareUiTimesheetRows();
	}
	@SuppressWarnings("unchecked")
	private void preparePeriodNames() {
		availablePeriods = new ArrayList<Period>();
		for(Timesheet timesheet: (List<Timesheet>)timesheetService.findAll()){
			Period period = timesheet.getPeriod();
			if(!foundInAvailablePeriods(period))
				availablePeriods.add(period);
		}
		setPeriodName("");
		periodNames = new HashSet<SelectItem>();
		periodNames.add(new SelectItem(""));
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy");
		for(Period period: availablePeriods){
			periodNames.add(new SelectItem(dateFormat.format(period.getDateFrom()) + " TO " + dateFormat.format(period.getDateTo())));
		}
	}
	public void sendMailRemainder() {
		timesheetReminder.sendTimsheetReminder();
	}
	private void prepareApproveStatuses() {
		approveStatuses = new HashSet<SelectItem>();
		approveStatuses.add(new SelectItem("Not Approved"));
		approveStatuses.add(new SelectItem("Approved"));
	}
	private List<EmployeeProject> getEmployeeProjectsForEmployee(Employee employee){
		return (List<EmployeeProject>) employeeProjectService.findEmployeeProjectByEmployee(employee);
	}
	private void prepareuitimesheetRowsFirstTime() {
		initialize();
		requiredPeriods = availablePeriods;
		requiredEmployees = employees;
		prepareUiTimesheetRows();
	}
	private void fetchTimesheets() {
		initialize();
		if(getCurrentEmployee() != null ){
			requiredEmployees = new ArrayList<Employee>();
			requiredEmployees.add(getCurrentEmployee());
		}
		else
			requiredEmployees = employees;
		//StringTokenizer stringTokenizer = new StringTokenizer(periodName, "TO");
		if(periodName != ""){
			Period period = periodService.getPeriod(new Date(periodName.split("TO")[1].trim()));
			requiredPeriods = new ArrayList<Period>();
			requiredPeriods.add(period);
		}else{
			requiredPeriods = availablePeriods;
		}
		prepareUiTimesheetRows();
	}
	private void prepareUiTimesheetRows() {
		getUitimesheetRows().clear();
		for(Employee employee: requiredEmployees){
			List<EmployeeProject> employeeProjects = new ArrayList<EmployeeProject>();
			List<Integer[]> employeePeriods = new ArrayList<Integer[]>();
			employeeProjects = getEmployeeProjectsForEmployee(employee);
			if(employeeProjects.isEmpty())
				employeeProjects = null;
			for(Period period: requiredPeriods){
				Integer employeePeriod[] = new Integer[2];
				employeePeriod[0] = employee.getEmpId();
				employeePeriod[1] = period.getPeriodId();
				List<Timesheet> timesheets = new ArrayList<Timesheet>();
				timesheets = timesheetService.findTimesheets(employeeProjects, period.getPeriodId(), null, taskType);
				boolean added = false;
				for (Timesheet timesheet : timesheets) {
					if (!added) {
						UITimesheetRow uiTimesheetRow = new UITimesheetRow(timesheet);
						if (approveStatus.equals("Approved")) {
							for (Timesheet timesheetTemp1 : timesheets) {
								if(timesheetTemp1.getMngrStatus().equals("false")){
									uiTimesheetRow.setApproveStatus("Not Approved");
									break;
								} else {
									uiTimesheetRow.setApproveStatus("Approved");
								}
							}
							if (uiTimesheetRow.getApproveStatus().equals("Approved")) {
								uiTimesheetRow.setShowApprove(false);
								uiTimesheetRow.setShowDeny(true);
								if (!find(employeePeriods, employeePeriod)) {
									employeePeriods.add(employeePeriod);
									SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy");
									uiTimesheetRow.setPeriodName(dateFormat.format(period.getDateFrom()) + " TO " + dateFormat.format(period.getDateTo()));
									uiTimesheetRow.setEmployeeProjects(employeeProjects);
									getUitimesheetRows().add(uiTimesheetRow);
									added=true;
								}
							}else
								break;
						}
						if(approveStatus.equals("Not Approved")){
							for(Timesheet timesheetTemp1: timesheets){
								if(timesheetTemp1.getSubmitFlag().equals("N")){
									uiTimesheetRow.setConfirmStatus("UnConfirmed");
									break;
								}
							}
							for(Timesheet timesheetTemp1: timesheets){
								if(timesheetTemp1.getMngrStatus().equals("false")){
									uiTimesheetRow.setApproveStatus("Not Approved");
									break;
								}
							}
							if(uiTimesheetRow.getConfirmStatus().equals("UnConfirmed")){
								uiTimesheetRow.setShowApprove(false);
								uiTimesheetRow.setShowDeny(false);
							}else{
								for (Timesheet timesheetTemp1 : timesheets){
									if(timesheetTemp1.getMngrStatus().equals("false")){
										uiTimesheetRow.setShowApprove(true);
										uiTimesheetRow.setShowDeny(false);
										break;
									}else{
										uiTimesheetRow.setShowApprove(false);
										uiTimesheetRow.setShowDeny(true);
									}
								}
							}
							if(uiTimesheetRow.getApproveStatus().equals("Not Approved")){
								if(!find(employeePeriods, employeePeriod)){
									employeePeriods.add(employeePeriod);
									SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy");
									uiTimesheetRow.setPeriodName(dateFormat.format(period.getDateFrom()) + " TO " + dateFormat.format(period.getDateTo()));
									uiTimesheetRow.setEmployeeProjects(employeeProjects);
									getUitimesheetRows().add(uiTimesheetRow);
									added=true;
								}
							}else
								break;
						}
					}
				}
			}
		}
	}
	public List<Employee> autocomplete(final Object suggestion){
		return employeeService.getEmployees(suggestion.toString());
	}
	public void fetchTimesheetsByEmployee(Long waste){
		if(employeeName!=""){
			String[] splits = employeeName.split("-");
			Employee employee = employeeService.findById(Integer.parseInt(splits[1].trim()));
			setCurrentEmployee(employee);
		}
		else
			setCurrentEmployee(null);
		
		fetchTimesheets();
	}
	/*@SuppressWarnings({ "unchecked", "unused" })
	private void prepareEmployeeNames() {
		setEmployeeName("");
		employeeNames = new HashSet<SelectItem>();
		employeeNames.add(new SelectItem(""));
		Employee employeeExample = new Employee();
		employeeExample.setActive("Y");
		for(Employee employee: (List<Employee>)employeeDAO.findByExample(employeeExample)){
			employeeNames.add(new SelectItem(employee.getEmpFirstName() + " " + employee.getEmpLastName() + " - " + employee.getEmpId()));
		}
	}*/
	
	public String toTimesheet(UITimesheetRow uiTimesheetRow){
		FacesContext facesContext = FacesContext.getCurrentInstance();
        EmployeeProject employeeProject = (EmployeeProject) getEmployeeProjectService().findById(uiTimesheetRow.getTimesheet().getEmployeeProject().getEmpProjId());
        Employee employee = getEmployeeService().getEmployee(employeeProject.getEmployee().getEmpId());
        Period period = periodService.findById(uiTimesheetRow.getTimesheet().getPeriod().getPeriodId());
        facesContext.getExternalContext().getSessionMap().put("employeeForAdmin", employee);
        facesContext.getExternalContext().getSessionMap().put("periodForAdmin", period);
        facesContext.getExternalContext().getSessionMap().put("adminMode", "true");
        //timesheetBean.fromAdmin(uiTimesheetRow);
        if(taskType.equalsIgnoreCase("RT")) {
        	facesContext.getExternalContext().getSessionMap().put("overtime", "false");
        	return "toTimesheet";
        } else {
        	facesContext.getExternalContext().getSessionMap().put("overtime", "true");
        	return "toOvertime";
        }
	}
	public void fromTimesheet() {
		reset();
	}
	public void reset(){
		setEmployeeName("");
		setCurrentEmployee(null);
		setApproveStatus("Not Approved");
		setPeriodName("");
		prepareuitimesheetRowsFirstTime();
	}
	private boolean foundInAvailablePeriods(Period period) {
		boolean foundInAvailablePeriod = false;
		for(Period periodTemp: availablePeriods){
			if(period.getPeriodId().equals(periodTemp.getPeriodId())){
				foundInAvailablePeriod = true;
				break;
			}
		}
		return foundInAvailablePeriod;
	}
	public boolean find(List<Integer[]> employeePeriods, Integer employeePeriod[]){
		boolean found = false;
		for(Integer employeePeriodTemp[]: employeePeriods){
			if(employeePeriodTemp[0].equals(employeePeriod[0]) && employeePeriodTemp[1].equals(employeePeriod[1])){
				found = true;
				break;
			}
		}
		return found;
	}
	public void fetchTimesheetsByApproveStatus(){
		fetchTimesheets();
	}
	public void fetchTimesheetsByPeriod(){
		fetchTimesheets();
	}
	private void initialize() {
		setUitimesheetRows(new ArrayList<UITimesheetRow>());
	}
	public void approveLine(UITimesheetRow uiTimesheetRow){
		approveSingleRow(uiTimesheetRow);
		
		fetchTimesheets();
	}
	private void approveSingleRow(UITimesheetRow uiTimesheetRow) {
		timesheetService.approve(uiTimesheetRow.getEmployeeProjects(), uiTimesheetRow.getTimesheet().getPeriod());
	}
	public void unsubmitPeriod(UITimesheetRow uiTimesheetRow) {
		timesheetService.unSubmitAllTaskInPeriod(uiTimesheetRow.getTimesheet().getPeriod(), uiTimesheetRow.getEmployeeProjects());
		
		fetchTimesheets();
	}
	public void submitPeriod(UITimesheetRow uiTimesheetRow) {
		timesheetService.submitAllTaskInPeriod(uiTimesheetRow.getTimesheet().getPeriod(), uiTimesheetRow.getEmployeeProjects());
		
		fetchTimesheets();
	}
	public void denyLine(UITimesheetRow uiTimesheetRow){
		timesheetService.deny(uiTimesheetRow.getEmployeeProjects(), uiTimesheetRow.getTimesheet().getPeriod());
		
		fetchTimesheets();
	}
	
	public List<UITimesheetRow> getUitimesheetRows() {
		return uitimesheetRows;
	}
	public void setUitimesheetRows(List<UITimesheetRow> uitimesheetRows) {
		this.uitimesheetRows = uitimesheetRows;
	}
	public List<Timesheet> getTimesheets() {
		return timesheets;
	}
	public void setTimesheets(List<Timesheet> timesheets) {
		this.timesheets = timesheets;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	
	public TimesheetService getTimesheetService() {
		return timesheetService;
	}
	public void setTimesheetService(TimesheetService timesheetService) {
		this.timesheetService = timesheetService;
	}
	public EmployeeProjectService getEmployeeProjectService() {
		return employeeProjectService;
	}
	public void setEmployeeProjectService(
			EmployeeProjectService employeeProjectService) {
		this.employeeProjectService = employeeProjectService;
	}
	public PeriodService getPeriodService() {
		return periodService;
	}
	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}
	public EmployeeService getEmployeeService() {
		return employeeService;
	}
	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}
	public String getApproveStatus() {
		return approveStatus;
	}
	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}
	public Set<SelectItem> getApproveStatuses() {
		return approveStatuses;
	}
	public void setApproveStatuses(Set<SelectItem> approveStatuses) {
		this.approveStatuses = approveStatuses;
	}
	public String getPeriodName() {
		return periodName;
	}
	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}
	public Set<SelectItem> getPeriodNames() {
		return periodNames;
	}
	public void setPeriodNames(Set<SelectItem> periodNames) {
		this.periodNames = periodNames;
	}
	public Set<String> getPeriodNamesString() {
		return periodNamesString;
	}
	public void setPeriodNamesString(Set<String> periodNamesString) {
		this.periodNamesString = periodNamesString;
	}
	public List<Period> getAvailablePeriods() {
		return availablePeriods;
	}
	public void setAvailablePeriods(List<Period> availablePeriods) {
		this.availablePeriods = availablePeriods;
	}
	public Employee getCurrentEmployee() {
		return currentEmployee;
	}
	public void setCurrentEmployee(Employee currentEmployee) {
		this.currentEmployee = currentEmployee;
	}
	public Set<SelectItem> getEmployeeNames() {
		return employeeNames;
	}
	public void setEmployeeNames(Set<SelectItem> employeeNames) {
		this.employeeNames = employeeNames;
	}
	protected void finalize() throws Throwable {
		setUitimesheetRows(null);
	}
	public String gettaskType() {
		return taskType;
	}
	public void settaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public TimesheetReminder getTimesheetReminder() {
		return timesheetReminder;
	}
	public void setTimesheetReminder(TimesheetReminder timesheetReminder) {
		this.timesheetReminder = timesheetReminder;
	}
	public boolean isSelectAllForApproval() {
		return selectAllForApproval;
	}
	public void setSelectAllForApproval(boolean selectAllForApproval) {
		this.selectAllForApproval = selectAllForApproval;
	}
	
}