package com.example.ex1.decorator;

import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class MainDecorator2 implements DayViewDecorator {

    private HashSet<CalendarDay> dates;

    public MainDecorator2(Collection<CalendarDay> date){
        dates = new HashSet<>(date);
    }


    @Override
    public boolean shouldDecorate(CalendarDay day){


        return day != null && dates.contains(day);
    }
    @Override
    public void decorate(DayViewFacade view){
        view.addSpan(new DotSpan(5,Color.BLUE));
    }

}