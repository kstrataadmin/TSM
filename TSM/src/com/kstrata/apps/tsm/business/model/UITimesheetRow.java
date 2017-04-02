package com.kstrata.apps.tsm.business.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Project;
import com.kstrata.apps.tsm.business.dao.entity.Timesheet;

public class UITimesheetRow implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Timesheet timesheet;
	
	private boolean editMode;
	private boolean projectclientEditMode = false;
	
	private Date taskDate;
	private int fromhours;
	private int fromminutes;
	private int tohours;
	private int tominutes;
	private boolean publicHoliday = false;
	
	private Date startTime;
	private Date endTime;
	
	private String clientName;
	private String projectName;
	private String projectDesc;
	private Integer projectId;
	private Project project;
	private Set<Project> projects = new HashSet<Project>();
	private Set<Client> clients = new HashSet<Client>();
	private String comments;
	private String taskDesc;
	private BigDecimal total;
	private String mngrStatus;
	private String taskType;
	private String saveStatus;
	
	private String confirmStatus;
	private boolean newTask;
	private String approveStatus;
	
	private String periodName;
	private boolean checkStatus;
	private boolean checkBoxDisplay;
	private boolean showApprove;
	private boolean showDeny;
	private String display;
	private String commentsDisplay;
	private String errorMessage;
	private boolean editStatus;
	private boolean deleteStatus;
	private boolean errorsPresent = false;
	private boolean confirmAllStatus = false;
	private boolean showApproveLink = false;
	private boolean showDenyLink = false;
	private String shortComments;
	private String shortDesc;
	private List<EmployeeProject> employeeProjects;
	
	private boolean selectRowForApproval = false;
	
	// Comparator
    public static class TimesheetID implements Comparator<UITimesheetRow> {
        @Override
        public int compare(UITimesheetRow arg0, UITimesheetRow arg1) {
            return arg0.getTimesheet().getTimesheetId() - arg1.getTimesheet().getTimesheetId();
        }
    }

	public UITimesheetRow(Timesheet timesheettemp) {
		if(timesheettemp.getStatus().equals("true")){
			setConfirmStatus("Confirmed");
			setEditMode(false);
			setCheckBoxDisplay(true);
		}
		else{
			setEditMode(true);
			setConfirmStatus("UnConfirmed");
			setCheckBoxDisplay(false);
		}
		if(timesheettemp.getMngrStatus().equals("false"))
			setApproveStatus("Not Approved");
		else
			setApproveStatus("Approved");
		setTimesheet(timesheettemp);
	}
	
	
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public int getFromhours() {
		return fromhours;
	}
	public void setFromhours(int fromhours) {
		this.fromhours = fromhours;
	}
	public int getFromminutes() {
		return fromminutes;
	}
	public void setFromminutes(int fromminutes) {
		this.fromminutes = fromminutes;
	}
	public int getTohours() {
		return tohours;
	}
	public void setTohours(int tohours) {
		this.tohours = tohours;
	}
	public int getTominutes() {
		return tominutes;
	}
	public void setTominutes(int tominutes) {
		this.tominutes = tominutes;
	}
	public Set<Project> getProjects() {
		return projects;
	}
	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	public Set<Client> getClients() {
		return clients;
	}
	public void setClients(Set<Client> clients) {
		this.clients = clients;
	}
	public UITimesheetRow() {
		// TODO Auto-generated constructor stub
	}
	public boolean isCheckBoxDisplay() {
		return checkBoxDisplay;
	}
	public void setCheckBoxDisplay(boolean checkBoxDisplay) {
		this.checkBoxDisplay = checkBoxDisplay;
	}
	public Timesheet getTimesheet() {
		return timesheet;
	}
	public void setTimesheet(Timesheet timesheet) {
		this.timesheet = timesheet;
	}
	public String getConfirmStatus() {
		return confirmStatus;
	}
	public void setConfirmStatus(String confirmStatus) {
		this.confirmStatus = confirmStatus;
	}
	public String getApproveStatus() {
		return approveStatus;
	}
	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}
	public boolean isCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(boolean checkStatus) {
		this.checkStatus = checkStatus;
	}
	public boolean isEditMode() {
		return editMode;
	}
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
	public boolean isProjectclientEditMode() {
		return projectclientEditMode;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public void setProjectclientEditMode(boolean projectclientEditMode) {
		this.projectclientEditMode = projectclientEditMode;
	}
	public String getPeriodName() {
		return periodName;
	}
	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}

	public String getProjectDesc() {
		return projectDesc;
	}

	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getCommentsDisplay() {
		return commentsDisplay;
	}

	public void setCommentsDisplay(String commentsDisplay) {
		this.commentsDisplay = commentsDisplay;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isShowApprove() {
		return showApprove;
	}

	public void setShowApprove(boolean showApprove) {
		this.showApprove = showApprove;
	}

	public boolean isShowDeny() {
		return showDeny;
	}

	public void setShowDeny(boolean showDeny) {
		this.showDeny = showDeny;
	}

	public boolean isEditStatus() {
		return editStatus;
	}

	public void setEditStatus(boolean editStatus) {
		this.editStatus = editStatus;
	}

	public boolean isErrorsPresent() {
		return errorsPresent;
	}

	public void setErrorsPresent(boolean errorsPresent) {
		this.errorsPresent = errorsPresent;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public Date getTaskDate() {
		return taskDate;
	}

	public void setTaskDate(Date taskDate) {
		this.taskDate = taskDate;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getMngrStatus() {
		return mngrStatus;
	}

	public void setMngrStatus(String mngrStatus) {
		this.mngrStatus = mngrStatus;
	}

	public String getSaveStatus() {
		return saveStatus;
	}

	public void setSaveStatus(String saveStatus) {
		this.saveStatus = saveStatus;
	}

	public boolean isNewTask() {
		return newTask;
	}

	public void setNewTask(boolean newTask) {
		this.newTask = newTask;
	}

	public boolean isConfirmAllStatus() {
		return confirmAllStatus;
	}

	public void setConfirmAllStatus(boolean confirmAllStatus) {
		this.confirmAllStatus = confirmAllStatus;
	}

	public boolean isDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public List<EmployeeProject> getEmployeeProjects() {
		return employeeProjects;
	}

	public void setEmployeeProjects(List<EmployeeProject> employeeProjects) {
		this.employeeProjects = employeeProjects;
	}

	public boolean isShowApproveLink() {
		return showApproveLink;
	}

	public void setShowApproveLink(boolean showApproveLink) {
		this.showApproveLink = showApproveLink;
	}

	public boolean isShowDenyLink() {
		return showDenyLink;
	}

	public void setShowDenyLink(boolean showDenyLink) {
		this.showDenyLink = showDenyLink;
	}

	/**
	 * @return the shortComments
	 */
	public String getShortComments() {
		if (null != comments && comments.length() > 20) {
			shortComments = comments.substring(0,19)+"...";
		} else {
			shortComments = comments;
		}
		return shortComments;
	}

	/**
	 * @param shortComments the shortComments to set
	 */
	public void setShortComments(String shortComments) {
		this.shortComments = shortComments;
	}

	/**
	 * @return the shortDesc
	 */
	public String getShortDesc() {
		if (null != taskDesc && taskDesc.length() > 20) {
			shortDesc = taskDesc.substring(0,19)+"...";
		} else {
			shortDesc = taskDesc;
		}
		return shortDesc;
	}

	/**
	 * @param shortDesc the shortDesc to set
	 */
	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}
	public Date getStartTime() {
		return startTime;
	}


	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	public Date getEndTime() {
		return endTime;
	}


	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public boolean isPublicHoliday() {
		return publicHoliday;
	}


	public void setPublicHoliday(boolean publicHoliday) {
		this.publicHoliday = publicHoliday;
	}


	public boolean isSelectRowForApproval() {
		return selectRowForApproval;
	}


	public void setSelectRowForApproval(boolean selectRowForApproval) {
		this.selectRowForApproval = selectRowForApproval;
	}
}