package com.lzz.dingdingsigntool;

import java.io.Serializable;

/**
 * @author lzz
 * @time 19-5-6 下午2:03
 */
public class WeekDayInfo implements Serializable {
    private String weekIndext;
    private int dataNumber;

    public String getWeekIndext() {
        return weekIndext;
    }

    public void setWeekIndext(String weekIndext) {
        this.weekIndext = weekIndext;
    }

    public int getDataNumber() {
        return dataNumber;
    }

    public void setDataNumber(int dataNumber) {
        this.dataNumber = dataNumber;
    }

    public WeekDayInfo(String weekIndext, int dataNumber) {
        this.weekIndext = weekIndext;
        this.dataNumber = dataNumber;
    }
}
