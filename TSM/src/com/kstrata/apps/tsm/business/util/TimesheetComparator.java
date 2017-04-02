package com.kstrata.apps.tsm.business.util;

import java.util.Comparator;

import com.kstrata.apps.tsm.business.model.JRTimesheet;

public class TimesheetComparator implements Comparator<JRTimesheet> {

	@Override
	public int compare(JRTimesheet o1, JRTimesheet o2) {
		return o1.getStartTime().compareTo(o2.getStartTime());
	}

}
