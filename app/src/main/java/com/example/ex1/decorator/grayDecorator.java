package com.example.ex1.decorator;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class grayDecorator implements DayViewDecorator {
    public final Calendar calendar= Calendar.getInstance();

    private CalendarDay date;
    public grayDecorator(){
        date = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day){

        return date != null && day.isBefore(date);
    }
    @Override
    public void decorate(DayViewFacade view){

        view.addSpan(new RelativeSizeSpan(0.7f));
        view.addSpan(new ForegroundColorSpan(Color.GRAY));
    }

}
