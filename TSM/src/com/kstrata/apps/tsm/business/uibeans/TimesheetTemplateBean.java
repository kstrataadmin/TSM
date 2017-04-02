package com.kstrata.apps.tsm.business.uibeans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name="timesheetTemplateBean")
@RequestScoped
public class TimesheetTemplateBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String timesheetStatus = "selected";
	private String overTimeStatus="";
	
	private FacesContext facesContext;
	
	@PostConstruct
	public void init() {
		facesContext = FacesContext.getCurrentInstance();
		String overtime = (String) facesContext.getExternalContext().getSessionMap().get("overtime");
		if (overtime!=null) {
			if (overtime=="true") {
				setOverTimeStatus("selected");
				setTimesheetStatus("");
			} else {
				setOverTimeStatus("");
				setTimesheetStatus("selected");
			}
		}
	}
	public void reset(){
		setTimesheetStatus("selected");
		setOverTimeStatus("");
	}

	public String toAdministration(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.getExternalContext().getSessionMap().remove("adminMode");
		facesContext.getExternalContext().getSessionMap().remove("employeeForAdmin");
		facesContext.getExternalContext().getSessionMap().remove("periodForAdmin");
		facesContext.getExternalContext().getSessionMap().remove("overtime");
		return "toSummary";
	}
	
	public String getOverTimeStatus() {
		return overTimeStatus;
	}
	
	public void setOverTimeStatus(String overTimeStatus) {
		this.overTimeStatus = overTimeStatus;
	}
	
	public String navigateToTimesheet(){
		facesContext.getExternalContext().getSessionMap().put("overtime", "false");
		setTimesheetStatus("selected");
		setOverTimeStatus("");
		return "toTimesheet";
	}
	
	public String navigateToOverTime(){
		facesContext.getExternalContext().getSessionMap().put("overtime", "true");
		setOverTimeStatus("selected");
		setTimesheetStatus("");
		return "toOvertime";
	}
	
	public String getTimesheetStatus() {
		return timesheetStatus;
	}
	
	public void setTimesheetStatus(String timesheetStatus) {
		this.timesheetStatus = timesheetStatus;
	}
}
