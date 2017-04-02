package com.kstrata.apps.tsm.business.uibeans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="mainTemplateBean")
@SessionScoped
public class MainTemplateBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String summaryStatus = "selected";
	private String employeeStatus = "";
	private String projectStatus = "";
	private String noticeBoardStatus = "";
	private String reportsStatus = "";
	
	public String getSummaryStatus() {
		return summaryStatus;
	}
	public void setSummaryStatus(String summaryStatus) {
		this.summaryStatus = summaryStatus;
	}
	public String getEmployeeStatus() {
		return employeeStatus;
	}
	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}
	public String getProjectStatus() {
		return projectStatus;
	}
	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}
	public String getNoticeBoardStatus() {
		return noticeBoardStatus;
	}
	public void setNoticeBoardStatus(String noticeBoardStatus) {
		this.noticeBoardStatus = noticeBoardStatus;
	}
	public void reset(){
		setSummaryStatus("selected");
		setEmployeeStatus("");
		setProjectStatus("");
		setNoticeBoardStatus("");
		setReportsStatus("");
	}
	public String navigateToSummary(){
		setSummaryStatus("selected");
		setEmployeeStatus("");
		setProjectStatus("");
		setNoticeBoardStatus("");
		setReportsStatus("");
		return "toSummary";
	}
	public String navigateToEmployee(){
		setSummaryStatus("");
		setEmployeeStatus("selected");
		setProjectStatus("");
		setNoticeBoardStatus("");
		setReportsStatus("");
		return "toEmployee";
	}
	public String navigateToProject(){
		setSummaryStatus("");
		setEmployeeStatus("");
		setProjectStatus("selected");
		setNoticeBoardStatus("");
		setReportsStatus("");
		return "toProject";
	}
	public String navigateToNoticeBoard(){
		setSummaryStatus("");
		setEmployeeStatus("");
		setProjectStatus("");
		setReportsStatus("");
		setNoticeBoardStatus("selected");
		return "toNoticeBoard";
	}
	public String navigateToReports(){
		setSummaryStatus("");
		setEmployeeStatus("");
		setProjectStatus("");
		setReportsStatus("selected");
		setNoticeBoardStatus("");
		return "toReports";
	}
	public String getReportsStatus() {
		return reportsStatus;
	}
	public void setReportsStatus(String reportsStatus) {
		this.reportsStatus = reportsStatus;
	}
}
