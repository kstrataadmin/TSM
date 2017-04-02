package com.kstrata.apps.tsm.business.job;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.mail.SimpleMailMessage;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.dao.entity.Employee;
import com.kstrata.apps.tsm.business.dao.entity.Period;
import com.kstrata.apps.tsm.business.service.EmployeeService;
import com.kstrata.apps.tsm.business.service.PeriodService;
import com.kstrata.apps.tsm.business.service.TimesheetService;
import com.kstrata.apps.tsm.business.uibeans.SpringMailSender;

public class TimesheetReminder {
	
	@Resource
	protected EmployeeService employeeService;
	@Resource
	protected TimesheetService timesheetService;
	@Resource
	protected PeriodService periodService;
	
	public TimesheetReminder() {
	}

	public void sendTimsheetReminder() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -100);
		Date currentDate = calendar.getTime();
		List<Period> periods = new ArrayList<Period>();
		periods = periodService.getPeriodsForDate(currentDate);
		
		List<Employee> employees = employeeService.getActiveEmployees();
		for (Employee employee : employees) {
			if (employee.getReminderEnable().equalsIgnoreCase("TRUE")) {
				List<String> messages = new ArrayList<String>();
				
				/*boolean submitFlag = timesheetService.getSubmitFlagOfTimesheetByPeriod(periods.get(0), employee);
				if (!submitFlag) {
					messages.add("Please Submit Timesheet for Period " + periods.get(0).toString());
				}*/
				
				List<Client> clients = employeeService.getReminderEnabledClients(employee);
				
				if (periods.get(1).getPeriodId() >= 125 && !clients.isEmpty()) {
					boolean submitFlag = timesheetService.getSubmitFlagOfTimesheetByPeriod(periods.get(1), employee);
					if (!submitFlag) {
						messages.add(periods.get(1).toString());
					}
				}
				
				if (periods.get(2).getPeriodId() >= 125 && !clients.isEmpty()) {
					boolean submitFlag = timesheetService.getSubmitFlagOfTimesheetByPeriod(periods.get(2), employee);
					if (!submitFlag) {
						messages.add(periods.get(2).toString());
					}
				}
				
				if (!messages.isEmpty()) {
					sendMailReminder(employee, messages);
					System.out.println("Mail Reminder Sent To - " + employee.getEmpUsername());
				}
			}
		}
	}

	private boolean sendMailReminder(Employee employee, List<String> messages) {
		SimpleMailMessage customeMailMessage = new org.springframework.mail.SimpleMailMessage();
		customeMailMessage.setFrom("admindesk@kstrata.com");
		customeMailMessage.setTo(employee.getEmpEmailid().trim());
		customeMailMessage.setCc("ravik@kstrata.com");
		customeMailMessage.setSubject("Important: Timesheet Not Submitted");
		String text = "<div style='color:#0078D0;font-family: verdana;font-size: 12px;'>Hi " + employee.getEmpFirstName() + ",<br/><br/>Please complete and submit timesheet for following week(s)<br/><ul>";
		for (String message : messages) {
			message = "<li>" + message + "</li>";
			text = text + message;
		}
		text = text + "</ul><br/><br/>" + "To Login to TSM please <a target='_blank' href='http://tsm.kstrata.com'>click here</a> <br/><br/><div style='font-size:10px'>*This is system generated message, do not reply*</div><br/><br/>" + "Regards,<br/>Timesheet Admin<br/>KSTRATA IT Solutions Pvt Ltd</div>";
		customeMailMessage.setText(text);
		SpringMailSender springMailSender = new SpringMailSender();
		return springMailSender.sendMail(customeMailMessage);
	}

	@SuppressWarnings("unused")
	private Integer getWorkingDaysForPeriod(Period period) {
		Integer holidaysInPeriod = periodService.getCountOfHolidaysByPeriod(period);
		Integer workingDays = 7 - holidaysInPeriod;
		return workingDays < 0 ? 0 : workingDays;
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

	public PeriodService getPeriodService() {
		return periodService;
	}

	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}
	
}
