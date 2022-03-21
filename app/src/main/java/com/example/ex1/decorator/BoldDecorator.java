package com.example.ex1.decorator;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class BoldDecorator implements DayViewDecorator {
    public final Calendar calendar= Calendar.getInstance();

    private CalendarDay date;
    public BoldDecorator(){
        date = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day){

        return date != null && day.isAfter(date);
    }
    @Override
    public void decorate(DayViewFacade view){
        view.addSpan(new StyleSpan(Typeface.BOLD));
    }

}
