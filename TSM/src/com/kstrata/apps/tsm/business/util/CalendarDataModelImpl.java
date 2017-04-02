package com.kstrata.apps.tsm.business.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.richfaces.model.CalendarDataModel;
import org.richfaces.model.CalendarDataModelItem;

public class CalendarDataModelImpl implements CalendarDataModel {

    //define attributes
    private CalendarDataModelItem[] items;                    //item of dates
    private List<Date> dates;                                //available dates

    //--------------------------------------------------------------------
    // CONSTRUCTORs
    //--------------------------------------------------------------------
    /**
     * Create calendar with list of dates to be enabled
     * @param dates List of dates to be enabled
     */
    public CalendarDataModelImpl(List<Date> dates){
        this.dates = dates != null ? dates : new ArrayList<Date>();
    }

    //--------------------------------------------------------------------
    // BASEs
    //--------------------------------------------------------------------
    /**
     * Retrieve list of dates to be presented in te rich:calendar component
     * @param dateArray list of dates that are supposed to be presented
     */
    @Override
    public CalendarDataModelItem[] getData(Date[] dateArray) {

        if (dateArray == null)return null;                                //no date for list of dates

        items = new CalendarDataModelItem[dateArray.length];
        for(int i = 0; i < dateArray.length; i++)
            items[i] = createDataModelItem(dateArray[i]);
        return items;

    }

    /**
     * Define no tooltip for any date
     * @param date the date to retrieve the tooltip
     */
    @Override
    public Object getToolTip(Date date) {
        return null;
    }

    //--------------------------------------------------------------------
    // UTILs
    //--------------------------------------------------------------------
    /**
     * Create a date to be presented
     * @param date the current date
     * @return CalendarDataModelItem date to be presented
     */
    protected CalendarDataModelItem createDataModelItem(Date date) {

        CalendarDataModelItemImpl item = new CalendarDataModelItemImpl();    //create the item
        Calendar calendar = Calendar.getInstance();                            //date calendar
        calendar.setTime(date);                                                //bind calendar to current date
        item.setDay(calendar.get(Calendar.DAY_OF_MONTH));                    //set current day of the month

        //run over all valid dates
        for(Date _date : dates){

            Calendar _calendar = Calendar.getInstance();                    //date calendar
            _calendar.setTime(_date);                                        //bind calendar to valid date

            //check if date shall be enabled
            if(_calendar.get(Calendar.YEAR) != calendar.get(Calendar.YEAR))continue;
            if(_calendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH))continue;
            if(_calendar.get(Calendar.DAY_OF_MONTH) != calendar.get(Calendar.DAY_OF_MONTH))continue;

            item.setEnabled(true);                                            //enable date
            item.setStyleClass("enabled-class");                            //enable styleClass

        }//end for
        return (CalendarDataModelItem)item;
    }
}
