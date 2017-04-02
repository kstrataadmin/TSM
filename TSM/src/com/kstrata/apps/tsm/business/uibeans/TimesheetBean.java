package com.kstrata.apps.tsm.business.uibeans;

import java.io.Serializable;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.kstrata.apps.tsm.business.dao.entity.Client;
import com.kstrata.apps.tsm.business.model.UITimesheetRow;

@ManagedBean(name = "timesheetBean")
@ViewScoped
public class TimesheetBean extends CommonBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void updateTotal(UITimesheetRow uiTimesheetRow) {
		uiTimesheetRow.setErrorsPresent(false);
		calculateTotal(uiTimesheetRow);
	}

	public void calculateTotal(UITimesheetRow uiTimesheetRow) {
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
			uiTimesheetRow.setTotal(getHourMinFormat(timeInMilliSec));
		}
		hideCommentsDescDiv();
	}
	
	/*@SuppressWarnings("unused")
	private void validateForOvertimeExistanceOnSave(
			UITimesheetRow uiTimesheetRow) {
		List<Timesheet> timesheets = getOvertimeTimesheets(uiTimesheetRow);
		BigDecimal total = null;
		if (timesheets.size() != 0) {
			total = uiTimesheetRow.getTotal();
			for (UITimesheetRow timesheetRow : getTimesheetrows()) {
				if (uiTimesheetRow.getTimesheet().getTaskDate()
						.equals(timesheetRow.getTimesheet().getTaskDate())
						&& !uiTimesheetRow
								.getTimesheet()
								.getTimesheetId()
								.equals(timesheetRow.getTimesheet()
										.getTimesheetId())) {
					total = total.add(timesheetRow.getTimesheet().getTotal());
				}
			}
			if (total.intValue() <= 9) {
				uiTimesheetRow.setErrorMessage("Overtime Reason Exists");
				uiTimesheetRow.setErrorsPresent(true);
			}
		}
	}*/
	
	public void deleteTask(UITimesheetRow uiTimesheetRow) {
		hideCommentsDescDiv();
		uiTimesheetRow.setErrorsPresent(false);
		validateForOvertimeReasonExistanceOnDelete(uiTimesheetRow);
		if (!uiTimesheetRow.isErrorsPresent()) {
			getTimesheetService().delete(uiTimesheetRow.getTimesheet());
			getTimesheetrows().remove(uiTimesheetRow);
		}
	}

}