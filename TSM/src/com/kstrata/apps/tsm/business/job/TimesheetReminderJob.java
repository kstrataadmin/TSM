package com.kstrata.apps.tsm.business.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class TimesheetReminderJob extends QuartzJobBean {

	private TimesheetReminder timesheetReminder;
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		timesheetReminder.sendTimsheetReminder();
	}

	public TimesheetReminder getTimesheetReminder() {
		return timesheetReminder;
	}
	public void setTimesheetReminder(TimesheetReminder TimesheetReminder) {
		this.timesheetReminder = TimesheetReminder;
	}
}
