package com.kstrata.apps.tsm.business.uibeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.entity.Project;
import com.kstrata.apps.tsm.business.service.ClientService;
import com.kstrata.apps.tsm.business.service.EmployeeProjectService;
import com.kstrata.apps.tsm.business.service.ProjectService;

@ManagedBean(name = "projectBean")
@ViewScoped
public class ProjectBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Resource
	private ProjectService projectService;
	@Resource
	private ClientService clientService;
	@Resource
	private EmployeeProjectService employeeProjectService;

	private String projectName;
	private Integer projectID;
	private String projectDesc;
	private String clientName;
	private String active;
	private Set<SelectItem> clientNames;
	private Set<SelectItem> projectNames;
	private Set<SelectItem> projectActiveStates = new HashSet<SelectItem>();

	private boolean newMode = false;
	private boolean editMode = false;
	private boolean normalMode = true;
	private boolean editStatus = false;

	private Project currentproject;

	@PostConstruct
	public void init() {
		prepareClientNames();
		prepareProjectNames();
		projectActiveStates.add(new SelectItem("Y"));
		projectActiveStates.add(new SelectItem("N"));
	}

	@SuppressWarnings("unchecked")
	private void prepareProjectNames() {
		List<Project> projects = projectService.findAll();
		Collections.sort(projects, new Project.ProjectID());
		projectNames = new HashSet<SelectItem>();
		for (Project project : projects) {
			projectNames.add(new SelectItem(project.getProjectId(), project.getProjectName()));
		}
		projectName = "";
		projectID = 0;
		projectNames.add(new SelectItem(0, projectName));
	}

	@SuppressWarnings("unchecked")
	private void prepareClientNames() {
		List<Client> clients = new ArrayList<Client>();
		clients = clientService.findAll();
		clientNames = new HashSet<SelectItem>();
		for (Client client : clients) {
			clientNames.add(new SelectItem(client.getClientName()));
		}
		clientName = "";
		clientNames.add(new SelectItem(clientName));
	}

	public List<Project> autocomplete(final Object suggestion) {
		return projectService.getProjects(suggestion.toString());
	}

	public void fetchProject() {
		if (projectID != 0) {
			newMode = false;
			editMode = false;
			normalMode = true;
			editStatus = true;
			Project project = projectService.findById(projectID);
			setCurrentproject(project);
			setProjectName(project.getProjectName());
			setProjectDesc(project.getProjectDesc());
			setClientName(project.getClient().getClientName());
			setActive(project.getActive());
		} else {
			reset();
		}
	}

	public void addNewProject() {
		normalMode = false;
		newMode = true;
		editMode = false;
		editStatus = false;
		setCurrentproject(null);
		setProjectDesc("");
		setClientName("");
		setProjectName("");
	}

	public void editProject() {
		normalMode = false;
		newMode = false;
		editMode = true;
	}

	public void saveProject() {
		if (clientName != null && clientName != "") {
			Project project = null;
			if (newMode == true)
				project = new Project((Client) clientService.findByClientName(
						clientName).get(0), projectName, projectDesc, "Y");
			else {
				currentproject.setActive(active);
				currentproject.setProjectDesc(projectDesc);
				currentproject.setProjectName(projectName);
				currentproject.setClient((Client) clientService
						.findByClientName(clientName).get(0));
				project = currentproject;
			}
			project = projectService.save(project);
			newMode = false;
			editMode = false;
			normalMode = true;
			editStatus = true;
			prepareClientNames();
			prepareProjectNames();
			setProjectName(project.getProjectName());
			setProjectID(project.getProjectId());
			// projectNames.add(new
			// SelectItem(project.getProjectId(),project.getProjectName()));
			setProjectDesc(project.getProjectDesc());
			setClientName(project.getClient().getClientName());
			setActive(project.getActive());
			project = null;
		}
	}

	public void deleteProject() {
		// delete current project
		currentproject.setActive("N");
		projectService.deactivateProject(currentproject);
		employeeProjectService.deactivateEmployeeProjects(currentproject);
		setProjectName(currentproject.getProjectName());
		setProjectDesc(currentproject.getProjectDesc());
		setClientName(currentproject.getClient().getClientName());
		setActive(currentproject.getActive());
		normalMode = true;
		newMode = false;
		editMode = false;
	}

	public void reset() {
		newMode = false;
		editMode = false;
		normalMode = true;
		editStatus = false;
		projectDesc = "";
		projectName = "";
		projectID = 0;
		clientName = "";
		active = "";
	}

	// getters and setters
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectDesc() {
		return projectDesc;
	}

	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public Project getCurrentproject() {
		return currentproject;
	}

	public void setCurrentproject(Project currentproject) {
		this.currentproject = currentproject;
	}

	public ProjectService getProjectService() {
		return projectService;
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	public ClientService getClientService() {
		return clientService;
	}

	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	public boolean isNewMode() {
		return newMode;
	}

	public void setNewMode(boolean newMode) {
		this.newMode = newMode;
	}

	public boolean isNormalMode() {
		return normalMode;
	}

	public void setNormalMode(boolean normalMode) {
		this.normalMode = normalMode;
	}

	public boolean isEditStatus() {
		return editStatus;
	}

	public void setEditStatus(boolean editStatus) {
		this.editStatus = editStatus;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Set<SelectItem> getClientNames() {
		return clientNames;
	}

	public void setClientNames(Set<SelectItem> clientNames) {
		this.clientNames = clientNames;
	}

	public Set<SelectItem> getProjectNames() {
		return projectNames;
	}

	public void setProjectNames(Set<SelectItem> projectNames) {
		this.projectNames = projectNames;
	}

	public Integer getProjectID() {
		return projectID;
	}

	public void setProjectID(Integer projectID) {
		this.projectID = projectID;
	}

	protected void finalize() throws Throwable {
	}

	public Set<SelectItem> getProjectActiveStates() {
		return projectActiveStates;
	}

	public void setProjectActiveStates(Set<SelectItem> projectActiveStates) {
		this.projectActiveStates = projectActiveStates;
	}

}
