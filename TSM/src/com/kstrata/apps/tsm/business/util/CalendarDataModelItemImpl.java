package com.kstrata.apps.tsm.business.util;

import org.richfaces.model.CalendarDataModelItem;

public class CalendarDataModelItemImpl implements CalendarDataModelItem {

    //define attributes
    private int day;                        //day of the calendar
    private boolean enabled;                //disable or enable day
    private String styleClass;                //style class to display enable/disable button

    //--------------------------------------------------------------------
    // CONSTRUCTORs
    //--------------------------------------------------------------------
    /**
     * Create calendar item with default values
     */
    public CalendarDataModelItemImpl() {
        this.day = 0;
        this.enabled = false;
        this.styleClass = "disabled-class";
    }

    //--------------------------------------------------------------------
    // GETs
    //--------------------------------------------------------------------
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public int getDay() {
        return day;
    }

    @Override
    public String getStyleClass() {
        return styleClass;
    }

    /**
     * Return no special data
     */
    @Override
    public Object getData() {
        return null;
    }

    /**
     * Return no special tooltip
     */
    @Override
    public Object getToolTip() {
        return null;
    }

    /**
     * Return no special tooltip
     */
    @Override
    public boolean hasToolTip() {
        return false;
    }

    //--------------------------------------------------------------------
    // SETs
    //--------------------------------------------------------------------
    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

}