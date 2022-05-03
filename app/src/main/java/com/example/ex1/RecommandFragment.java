package com.example.ex1;

import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ex1.decorator.BoldDecorator;
import com.example.ex1.decorator.EventDecorator;
import com.example.ex1.decorator.MainDecorator;
import com.example.ex1.decorator.SaturdayDecorator;
import com.example.ex1.decorator.SundayDecorator;
import com.example.ex1.decorator.grayDecorator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;


public class RecommandFragment extends Fragment {

    MainActivity activity;

    FirebaseUser user;
    String TAG = "recommandFragment";


    String fname;
    String str;

    Button cha_Btn,del_Btn,save_Btn;
    TextView diaryTextView,textView2,textView3;
    EditText contextEditText;

    LinearLayout fragmentFrame;
    MaterialCalendarView calendarView;







    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//이 메소드가 호출될떄는 프래그먼트가 엑티비티위에 올라와있는거니깐 getActivity메소드로 엑티비티참조가능
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
//이제 더이상 엑티비티 참초가안됨
        activity = null;
    }

    public void  checkDay(int cYear,int cMonth,int cDay,String userID){

        fname=""+cYear+"-"+(cMonth+1)+"-"+cDay+".txt";//저장할 파일 이름설정
        FileInputStream fis = null;//FileStream fis 변수

        try{

            fis = activity.openFileInput(fname);


            byte[] fileData=new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            str=new String(fileData);

            if(textView2.getText()==null){

                textView2.setVisibility(View.INVISIBLE);
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
            }
            else{
                contextEditText.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.VISIBLE);
                textView2.setText(str);



                save_Btn.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.VISIBLE);
                del_Btn.setVisibility(View.VISIBLE);


            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay){
        FileOutputStream fos=null;

        try{

            String dir = activity.getFilesDir().getAbsolutePath();
            File f0 = new File(dir, readDay);
            boolean d0 = f0.delete();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveDiary(String readDay){
        FileOutputStream fos=null;

        try{
            fos=activity.openFileOutput(readDay, Context.MODE_PRIVATE);
            String content=contextEditText.getText().toString();
            fos.write((content).getBytes());
            fos.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void findEvent(ArrayList<CalendarDay> eventList) {
        eventList.clear();

        String dir = activity.getFilesDir().getAbsolutePath();;

        String[] mylist = activity.fileList();


        for(int i=0;i< mylist.length;i++){

            String[] dates = mylist[i].substring(0,mylist[i].length()-4).split("-");

            if(dates.length == 3 && dates[0].length() == 4 && dates[1].length() <3 &&dates[1].length() >0 && dates[2].length() <3 &&dates[2].length() >0){
                int year = Integer.parseInt(dates[0]);
                int month = Integer.parseInt(dates[1])-1;
                int day = Integer.parseInt(dates[2]);

                eventList.add(CalendarDay.from(year,month,day));
            }

        }
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fname = null;
        str = null;
        user = activity.user;

        View rootview = inflater.inflate(R.layout.fragment_recommand, container, false);



        ArrayList<CalendarDay> eventList = new ArrayList<>();

        findEvent(eventList);

        fragmentFrame=rootview.findViewById(R.id.fragmentFrame);
        diaryTextView=rootview.findViewById(R.id.diaryTextView);
        save_Btn=rootview.findViewById(R.id.save_Btn);
        del_Btn=rootview.findViewById(R.id.del_Btn);
        cha_Btn=rootview.findViewById(R.id.cha_Btn);
        textView2=rootview.findViewById(R.id.textView2);
        textView3=rootview.findViewById(R.id.textView3);
        contextEditText=rootview.findViewById(R.id.contextEditText);
        calendarView = rootview.findViewById(R.id.calendarview);

        calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new BoldDecorator(),
                new grayDecorator(),
                new EventDecorator(eventList)
        );

        if(activity.apiFinish == true){
            calendarView.addDecorator(new MainDecorator(activity.RecommandDates));
        }




        String userName = activity.userInfo.getName();
        textView3.setText(userName+"님의 월간 계획표");


        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {


                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                diaryTextView.setText(String.format("%d / %d / %d",date.getYear(),date.getMonth()+1,date.getDay()));
                contextEditText.setText("");
                checkDay(date.getYear(), date.getMonth(), date.getDay(), user.getUid());


            }
        });



        save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveDiary(fname);
                str=contextEditText.getText().toString();
                textView2.setText(str);
                save_Btn.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.VISIBLE);
                del_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.VISIBLE);
                calendarView.removeDecorator(new EventDecorator(eventList));
                findEvent(eventList);
                calendarView.addDecorator(new EventDecorator(eventList));

            }
        });

        cha_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                contextEditText.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                contextEditText.setText(str);

                save_Btn.setVisibility(View.VISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                textView2.setText(contextEditText.getText());
            }

        });
        del_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView2.setVisibility(View.INVISIBLE);
                contextEditText.setText("");
                contextEditText.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                removeDiary(fname);

            }
        });

        return rootview;
    }


}