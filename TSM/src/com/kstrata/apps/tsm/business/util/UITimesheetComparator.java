package com.kstrata.apps.tsm.business.util;

import java.util.Comparator;

import com.kstrata.apps.tsm.business.model.UITimesheetRow;

public class UITimesheetComparator implements Comparator<UITimesheetRow> {


	@Override
	public int compare(UITimesheetRow o1, UITimesheetRow o2) {
		return o1.getTimesheet().getStartTime().compareTo(o2.getTimesheet().getStartTime());
	}

}
