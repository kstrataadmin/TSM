package com.kstrata.apps.tsm.business.uibeans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Project;
import com.kstrata.apps.tsm.business.dao.entity.Timesheet;
import com.kstrata.apps.tsm.business.model.UITimesheetRow;

@ManagedBean(name="overTimeBean")
@ViewScoped
public class OverTimeBean extends CommonBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@PostConstruct
	public void init() {
		super.init();
		if (otherPeriod.getPeriodId()>=81) {
			setDisplayOTCheckBox(true);
		}
	}
	
	public void updateTotal(UITimesheetRow uiTimesheetRow) {
		uiTimesheetRow.setErrorsPresent(false);
		calculateTotal(uiTimesheetRow);
	}
	
	public void calculateTotal(UITimesheetRow uiTimesheetRow) {
		setDisplayOTCheckBox(false);
		if (otherPeriod.getPeriodId()<81) {
			super.calculateTotal(uiTimesheetRow);
			return;
		}
		setDisplayOTCheckBox(true);
		validateFromTo(uiTimesheetRow);
		if (!uiTimesheetRow.isErrorsPresent()) {
			Long timeInMilliSec = uiTimesheetRow.getStartTime().getTime() - uiTimesheetRow.getEndTime().getTime();
			Client client = getProjectClientMap().get(uiTimesheetRow.getProjectId());
			
			if (client.getLunchGraceFlag()==1) {
				//DB Constants
				int lunchMin = client.getLunchMinutes();
				int graceMin = client.getGraceMinutes();
				
				//Business Constants
				Long five = new Long(5*60*60*1000);
				
				if (getHourMinFormat(timeInMilliSec).compareTo(getHourMinFormat(five)) == 1) {
					//Business Constants
					Long eightAndHalf = new Long((8*60*60*1000) + (30*60*1000));
					Long nine = new Long(9*60*60*1000);
					
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(uiTimesheetRow.getEndTime());
					calendar.add(Calendar.MINUTE, -lunchMin);
					if (getHourMinFormat(timeInMilliSec).compareTo(getHourMinFormat(eightAndHalf)) == 1) {
						if (getHourMinFormat(timeInMilliSec).compareTo(getHourMinFormat(nine)) == -1) {
							Calendar endTimeTemp = Calendar.getInstance();
							endTimeTemp.setTime(uiTimesheetRow.getStartTime());
							endTimeTemp.add(Calendar.HOUR, 8);
							calendar.setTime(endTimeTemp.getTime());
						} else {
							calendar.add(Calendar.MINUTE, -graceMin);
						}
					}
					timeInMilliSec = uiTimesheetRow.getStartTime().getTime() - calendar.getTime().getTime();
				}
			}
			BigDecimal actualTime = getHourMinFormat(timeInMilliSec);
			if (!uiTimesheetRow.isPublicHoliday()) {
				BigDecimal eightHours = getHourMinFormat(new Long(8*60*60*1000));
				if (actualTime.compareTo(eightHours) == 1) {
					actualTime = actualTime.subtract(eightHours);
				} else if (actualTime.compareTo(eightHours) == -1 || actualTime.compareTo(eightHours) == 0) {
					actualTime = actualTime.subtract(actualTime);
					uiTimesheetRow.setErrorMessage("No overtime found for selected date(s)");
					uiTimesheetRow.setErrorsPresent(true);
				}
			}
			uiTimesheetRow.setTotal(actualTime);
		}
		hideCommentsDescDiv();
	}
	
	public void saveTask(UITimesheetRow uiTimesheetRow) {
		uiTimesheetRow.setErrorMessage("");
		uiTimesheetRow.setErrorsPresent(false);
		validateTaskDate(uiTimesheetRow);
		validateFromTo(uiTimesheetRow);
		removeUnWantedContentFromDescriptionIfExists(uiTimesheetRow);
		validateDecription(uiTimesheetRow);
		validateRegularTasksExistance(uiTimesheetRow);
		validateForSimilarTaskExistence(uiTimesheetRow);
		hideCommentsDescDiv();
		calculateTotal(uiTimesheetRow);
		validateTotal(uiTimesheetRow);
		if(!uiTimesheetRow.isErrorsPresent()){
			Timesheet savedTimesheet = saveSingleTimesheet(uiTimesheetRow, "OT");
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
				uiTimesheetRow.setProjectclientEditMode(false);
				uiTimesheetRow.setNewTask(false);
				
				uiTimesheetRow.setTaskDate(savedTimesheet.getTaskDate());
				/*uiTimesheetRow.setFromhours(savedTimesheet.getStartTime().getHours());
				uiTimesheetRow.setFromminutes(savedTimesheet.getStartTime().getMinutes());
				uiTimesheetRow.setTohours(savedTimesheet.getEndTime().getHours());
				uiTimesheetRow.setTominutes(savedTimesheet.getEndTime().getMinutes());*/
				uiTimesheetRow.setDisplay("none");
				uiTimesheetRow.setCommentsDisplay("none");
				uiTimesheetRow.setComments(new String(savedTimesheet.getComments()));
				uiTimesheetRow.setTaskDesc(new String(savedTimesheet.getTaskDesc()));
				
				uiTimesheetRow.setTotal(savedTimesheet.getTotal());
				if(isAdminMode()){
					uiTimesheetRow.setShowApproveLink(true);
				}
			}
		}
	}
	
	private void validateRegularTasksExistance(UITimesheetRow uiTimesheetRow) {
		Project project = projectService.findById(uiTimesheetRow.getProjectId());
		EmployeeProject employeeProject = employeeProjectService.findEmployeeProject(project,employee);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(uiTimesheetRow.getStartTime());
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.AM_PM, Calendar.AM);
		  
		List<Timesheet> timesheets = timesheetService.findTimesheets(employeeProject.getEmpProjId(), calendar.getTime());

		if(timesheets.size()!=0) {
		} else {
			uiTimesheetRow.setErrorMessage("No tasks Found");
			uiTimesheetRow.setErrorsPresent(true);
		}
	}
	
	public void deleteTask(UITimesheetRow uiTimesheetRow){
		hideCommentsDescDiv();
		getTimesheetService().delete(uiTimesheetRow.getTimesheet());
		getTimesheetrows().remove(uiTimesheetRow);
	}
	
	public void prepareUITimesheetRows() {
		int periodCount = 0;
		prepareEmployeeProjects();
		for (EmployeeProject employeeProjecttemp : employeeProjects) {
			periodCount = periodCount + employeeProjecttemp.getTimesheets().size();
		}
		if (periodCount > 0) {
			for (EmployeeProject employeeProjecttemp : employeeProjects) {
				if (!projectNamesString.contains(employeeProjecttemp.getProject().getProjectDesc())) {
					projectNames.add(new SelectItem(employeeProjecttemp.getProject().getProjectId(), employeeProjecttemp
							.getProject().getProjectDesc()
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
		}
		if (timesheetService.getSubmitFlagOfTimesheetByPeriod(otherPeriod, employee)) {
			setAddTaskStatus(false);
		}
	}
	
	private void prepareEmployeeProjects() {
		employeeProjects = employeeProjectService.findEmployeeProjectByEmployee(getEmployee());
		for (EmployeeProject employeeProjecttemp : employeeProjects) {
			employeeProjecttemp.setTimesheets(timesheetService.findTimesheetsForOverTime(employeeProjecttemp));
		}
	}
}