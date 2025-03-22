package com.ejt.demo.server.mbean;

import java.beans.ConstructorProperties;
import java.text.DateFormatSymbols;

public class NestedTestData {

    private String[] months = new DateFormatSymbols().getMonths();
    private String[] weekdays = new DateFormatSymbols().getWeekdays();
    private String[] eras = new DateFormatSymbols().getEras();

    public NestedTestData() {
    }

    @ConstructorProperties({"months", "weekdays", "eras"})
    public NestedTestData(String[] months, String[] weekdays, String[] eras) {
        this.months = months;
        this.weekdays = weekdays;
        this.eras = eras;
    }

    public String[] getMonths() {
        return months;
    }

    public String[] getWeekdays() {
        return weekdays;
    }

    public String[] getEras() {
        return eras;
    }
}
