package com.kstrata.apps.tsm.business.uibeans;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.Role;
import com.kstrata.apps.tsm.business.dao.util.IEncryptAndDecrypt;
import com.kstrata.apps.tsm.business.service.EmployeeService;
import com.kstrata.apps.tsm.business.service.RoleService;

@ManagedBean(name="loginBean")
@SessionScoped
@Component
public class LoginBean implements Serializable{

	/**
	 * 
	 */
	@Resource
	@Autowired(required=true)
	private EmployeeService employeeService;
	
	@Resource
	@Autowired(required=true)
	private RoleService roleService;
	
	@Resource
	@Autowired(required=true)
	private IEncryptAndDecrypt encryptAndDecrypt;
	
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private String name;
	private String newpassword;
	private String retypepassword;
	private boolean changePasswordFlag = false;
	
	public String login() throws Exception {
		Employee employee = employeeService.getEmployee(username, password);
		if(employee != null){
			Role role = roleService.findById(employee.getRole().getRoleId());
			employee.setRole(role);
			if(employee.getPasswordChangeFlag()==null || employee.getPasswordChangeFlag().equals("Y")){
				changePasswordFlag = true;
				return "login";
			}
			return decideRole(employee);
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid Credentials"));
			return "invalid";
		}
	}
	public void forgotPassword() throws Exception{
		if(!username.equals(null) && !username.equals("")){
			Employee employee = employeeService.getEmployee(username);
			if(employee!=null){
				sendMail(employee.getEmpEmailid(), "Please find your password below \n" + encryptAndDecrypt.decrypt(employee.getEmpPassword()));
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Password has been sent to your EMail-ID: " + employee.getEmpEmailid()));
			}
			else
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User does not exist"));
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid User"));
		}
	}
	public boolean sendMail(String email, String text){
		SimpleMailMessage customeMailMessage = new org.springframework.mail.SimpleMailMessage();
		customeMailMessage.setFrom("admindesk@kstrata.com");
		customeMailMessage.setTo(email.trim());
		customeMailMessage.setSubject("Kstrata Timesheet Password");
		customeMailMessage.setText(text);
		SpringMailSender springMailSender = new SpringMailSender();
		boolean sent = springMailSender .sendMail(customeMailMessage);
		return sent;
	}
	private String decideRole(Employee employee) {
		name = employee.getEmpFirstName() + " " + employee.getEmpLastName();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.getExternalContext().getSessionMap().put("loggedUser", employee);
		changeFieldsInSessionBeans();
		if(employee.getRole().getRoleName().equals("admin")){
			return "loginadmin";
		}
		else{
			return "loginuser";
		}		
	}
	public String logoutAndChangePassword(){
		Employee employee = (Employee)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");
		employee.setPasswordChangeFlag("Y");
		employeeService.save(employee);
		changePasswordFlag = true;
		changeFieldsInSessionBeans();
		return "login";
	}
	private void changeFieldsInSessionBeans() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession httpSession = (HttpSession)facesContext.getExternalContext().getSession(false);
		TimesheetTemplateBean timesheetTemplateBean = (TimesheetTemplateBean) httpSession.getAttribute("timesheetTemplateBean");
		MainTemplateBean mainTemplateBean = (MainTemplateBean) httpSession.getAttribute("mainTemplateBean");
		if(timesheetTemplateBean!=null)timesheetTemplateBean.reset();
		if(mainTemplateBean!=null)mainTemplateBean.reset();
	}
	private void removeBeansFromSession(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.getExternalContext().getSessionMap().remove("loggedUser");
		HttpSession httpSession = (HttpSession)facesContext.getExternalContext().getSession(false);
		httpSession.removeAttribute("timesheetTemplateBean");
		httpSession.removeAttribute("mainTemplateBean");
		httpSession.removeAttribute("loginBean");
		httpSession.invalidate();
		httpSession = null;
	}
	public String changePasswordAndLogin() throws Exception{
		validatePasswords();
		Employee employee = (Employee)employeeService.getEmployee(username);
		if(employee!=null){
			if(password.equals(encryptAndDecrypt.decrypt(employee.getEmpPassword())) && validatePasswords()){
				employee.setEmpPassword(encryptAndDecrypt.encrypt(newpassword));
				employee.setPasswordChangeFlag("N");
				employeeService.save(employee);
			}else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid Credentials"));
				changePasswordFlag = true;
				return "invalid";
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Invalid User"));
			return "invalid";
		}
			
		return decideRole(employee);
	}
	private boolean validatePasswords() {
		// TODO Auto-generated method stub
		boolean validity = false;
		if(retypepassword!=null && retypepassword!="" && retypepassword.equals(newpassword))
			validity = true;
		return validity;
	}
	public String logout(){
		changePasswordFlag = false;
		removeBeansFromSession();
		return "logout";
	}
	public String getNewpassword() {
		return newpassword;
	}
	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}
	public String getRetypepassword() {
		return retypepassword;
	}
	public void setRetypepassword(String retypepassword) {
		this.retypepassword = retypepassword;
	}
	public boolean getChangePasswordFlag() {
		return changePasswordFlag;
	}
	public void setChangePasswordFlag(boolean changePasswordFlag) {
		this.changePasswordFlag = changePasswordFlag;
	}
	public void init(){
		
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public EmployeeService getEmployeeService() {
		return employeeService;
	}
	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}
	public RoleService getRoleService() {
		return roleService;
	}
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
