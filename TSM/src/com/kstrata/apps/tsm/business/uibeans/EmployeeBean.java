package com.kstrata.apps.tsm.business.uibeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.EmployeeProject;
import com.kstrata.apps.tsm.business.dao.entity.Project;
import com.kstrata.apps.tsm.business.dao.entity.Role;
import com.kstrata.apps.tsm.business.dao.util.IEncryptAndDecrypt;
import com.kstrata.apps.tsm.business.service.ClientService;
import com.kstrata.apps.tsm.business.service.EmployeeProjectService;
import com.kstrata.apps.tsm.business.service.EmployeeService;
import com.kstrata.apps.tsm.business.service.ProjectService;

@SuppressWarnings("unchecked")
@ManagedBean(name = "employeeBean")
@ViewScoped
public class EmployeeBean implements Serializable {

	private static final long serialVersionUID = 1L;
	@Resource
	private EmployeeProjectService employeeProjectService;
	@Resource
	private EmployeeService employeeService;
	@Resource
	private ProjectService projectService;
	@Resource
	private ClientService clientService;
	
	@Resource
	private IEncryptAndDecrypt encryptAndDecrypt;

	private Employee employee = new Employee();
	private List<Project> projectrows = new ArrayList<Project>();
	private List<SelectItem> empIds = new ArrayList<SelectItem>();
	private Set<SelectItem> projectNames = new LinkedHashSet<SelectItem>();
	private Set<SelectItem> employeeActiveStates = new HashSet<SelectItem>();

	List<EmployeeProject> employeeProjects = new ArrayList<EmployeeProject>();
	private String empId;
	private String projectDesc;
	private Integer projectId;
	private String errorMessage;
	private boolean errorsPresent;
	List<Employee> employees = new ArrayList<Employee>();

	private boolean newMode = false;
	private boolean editMode = false;
	private boolean normalMode = true;
	private boolean editStatus = false;

	@PostConstruct
	public void init() {
		prepareEmpIds(null);
		prepareProjectNames();
		employeeActiveStates.add(new SelectItem("Y"));
		employeeActiveStates.add(new SelectItem("N"));
	}

	private void prepareProjectNames() {
		List<Project> projects = new ArrayList<Project>();
		projects = projectService.findActiveProjects();
		projectNames = new LinkedHashSet<SelectItem>();
		for (Project project : projects) {
			projectNames.add(new SelectItem(project.getProjectId(), 
					project.getProjectName() + " - " + project.getProjectDesc() + " ("
					+ project.getClient().getClientName() + ")"));
		}
		projectId = 0;
	}

	public void prepareEmpIds(Integer empId) {
		employees = new ArrayList<Employee>();
		empIds = new ArrayList<SelectItem>();
		employees = employeeService.findAll();
		this.empId = "";
		employee = null;
		for (Employee emp : employees) {
			empIds.add(new SelectItem(emp.getEmpId() + " - "
					+ emp.getEmpFirstName() + " "
					+ emp.getEmpLastName()));
			if (empId != null && emp.getEmpId().equals(empId)) {
				employee = emp;
				this.empId = String.valueOf(employee.getEmpId() + " - "
						+ employee.getEmpFirstName() + " "
						+ employee.getEmpLastName());
			}
		}
		empIds.add(new SelectItem(""));
	}

	public void reset() {
		newMode = false;
		editMode = false;
		normalMode = true;
		editStatus = false;
		errorsPresent = false;
		prepareEmpIds(null);
		prepareProjectNames();
		employee = null;
		projectrows = new ArrayList<Project>();
	}

	public void addNewEmployee() throws Exception {
		normalMode = false;
		newMode = true;
		editMode = false;
		editStatus = false;
		empId = "";
		projectrows = new ArrayList<Project>();
		employee = new Employee();
		employee.setActive("Y");
		employee.setPasswordChangeFlagBoolean(true);
		employee.setPasswordChangeFlag("Y");
		prepareProjectNames();
		errorsPresent = false;
	}

	public void editEmployee() {
		normalMode = false;
		newMode = false;
		editMode = true;
		errorsPresent = false;
	}

	public void deleteEmployee() {
		if (empId != "") {
			employee.setActive("N");
			employeeService.deactivateEmployee(employee);
			normalMode = true;
			newMode = false;
			editMode = false;
			editStatus = true;
			errorsPresent = false;
		}
	}

	public void saveEmployee() throws Exception {
		if (newMode == true) {
			if (employee.getEmpId() == null || employee.getEmpId() == 0) {
				errorsPresent = true;
				errorMessage = "Please Enter ID For Employee";
			} else {
				Role role = new Role();
				role.setRoleId(2);
				role.setRoleName("user");
				employee.setRole(role);
				employee.setEmpPassword((byte[]) encryptAndDecrypt.encrypt("kstrata123"));
				Integer empId = employeeService.createEmployee(employee);
				if (empId != null) {
					normalMode = true;
					newMode = false;
					editMode = false;
					editStatus = true;
					prepareEmpIds(empId);
					prepareProjectNames();
					errorsPresent = false;
				} else {
					errorsPresent = true;
					errorMessage = "Employee exists with this ID";
				}
			}
		} else {
			employee = employeeService.update(employee);
			if (employee != null && "SUCCESS".equalsIgnoreCase(employee.getSaveStatus())) {
				normalMode = true;
				newMode = false;
				editMode = false;
				editStatus = true;
				prepareEmpIds(employee.getEmpId());
				prepareProjectNames();
				errorsPresent = false;
			} else {
				errorsPresent = true;
				errorMessage = "Update Failed, Please check username,email-ID";
			}
		}
	}

	public void fetchEmployee() {
		if (empId != "") {
			employee = employeeService.getEmployee(Integer.parseInt(empId.split("-")[0].trim()));
			editStatus = true;
			prepareProjectrows();
		} else {
			reset();
		}
	}

	private void prepareProjectrows() {
		employeeProjects = new ArrayList<EmployeeProject>();
		employeeProjects = employeeProjectService.findEmployeeProjectByEmployee(employee);
		Set<Integer> projectIds = new HashSet<Integer>();
		for (EmployeeProject employeeProject : employeeProjects) {
			projectIds.add(employeeProject.getProject().getProjectId());
		}
		projectrows = projectService.getProjects(projectIds);
	}

	public void addProjectToEmployee() {
		if (projectId != 0 && employee != null) {
			Project project = (Project) projectService.findById(projectId);
			employeeProjectService.insertEmployeeproject(project, employee);
			prepareProjectrows();
		}
	}

	public void removeProjectForEmployee(Project project) {
		if (employee != null) {
			employeeProjectService.removeEmployeeproject(project, employee);
		}
		prepareProjectrows();
	}

	public EmployeeProjectService getEmployeeProjectService() {
		return employeeProjectService;
	}

	public void setEmployeeProjectService(EmployeeProjectService employeeProjectService) {
		this.employeeProjectService = employeeProjectService;
	}

	public EmployeeService getEmployeeService() {
		return employeeService;
	}

	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
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

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<SelectItem> getEmpIds() {
		return empIds;
	}

	public void setEmpIds(List<SelectItem> empIds) {
		this.empIds = empIds;
	}

	public Set<SelectItem> getProjectNames() {
		return projectNames;
	}

	public void setProjectNames(Set<SelectItem> projectNames) {
		this.projectNames = projectNames;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getProjectDesc() {
		return projectDesc;
	}

	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	public List<Project> getProjectrows() {
		return projectrows;
	}

	public void setProjectrows(List<Project> projectrows) {
		this.projectrows = projectrows;
	}

	public boolean isNewMode() {
		return newMode;
	}

	public void setNewMode(boolean newMode) {
		this.newMode = newMode;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isErrorsPresent() {
		return errorsPresent;
	}

	public void setErrorsPresent(boolean errorsPresent) {
		this.errorsPresent = errorsPresent;
	}

	protected void finalize() throws Throwable {
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Set<SelectItem> getEmployeeActiveStates() {
		return employeeActiveStates;
	}

	public void setEmployeeActiveStates(Set<SelectItem> employeeActiveStates) {
		this.employeeActiveStates = employeeActiveStates;
	}
}
