package com.example.ex1;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ex1.dataStructure.UserFeature;
import com.example.ex1.weather.Weather3;
import com.example.ex1.weather.Weather7;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FeatureFragment extends Fragment {

    MainActivity activity;
    String TAG = "FeatureFragment";

    UserFeature userFeature;

    TextView averInc1;
    TextView averInc2;
    TextView averPeriod1;
    TextView averPeriod2;

    BarChart incChart1;

    BarChart periodChart1;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");



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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_feature, container, false);

        FirebaseFirestore db = activity.db;
        FirebaseUser user = activity.user;

        userFeature = activity.userFeature;
        averInc1 = rootview.findViewById(R.id.averInc1);
        averInc2 = rootview.findViewById(R.id.averInc2);
        averPeriod1 = rootview.findViewById(R.id.averPeriod1);
        averPeriod2 = rootview.findViewById(R.id.averPeriod2);

        incChart1 = rootview.findViewById(R.id.incChart1);
        periodChart1 = rootview.findViewById(R.id.periodChart1);

        BarData barDataP = new BarData();
        BarData barDataW = new BarData();

        if(userFeature.isnull){
            activity.startToast("아직 사용자의 빨래 정보가 데이터베이스에 없습니다!");
        }
        else{
            DocumentReference docRef = db.collection("datas").document(user.getUid());
            docRef.collection("featureAnalysis").document("period1").
                    get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if(task!=null){

                        Map map = document.getData();
                        if(map.size() != 0){
                                ArrayList<BarEntry> entries = new ArrayList<>();
                                List<String> keyList = new ArrayList<>(map.keySet());
                            for(int i =0; i<6 && i<keyList.size()-1 ; i++){
                                float now = Float.valueOf(keyList.get(i).toString());
                                float next = Float.valueOf(keyList.get(i+1).toString());

                                try{
                                    Date date1 = dateFormat.parse(keyList.get(i));
                                    Date date2 = dateFormat.parse(keyList.get(i+1));
                                    long day = (date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24);

                                    entries.add(new BarEntry(now,day));
                                    Log.d("지금",now +" p1 "+ day +"");
                                }catch(Exception e){
                                    Log.d("지금",keyList.get(i));
                                    Log.d("지금",keyList.get(i+1));
                                    Log.d("지금",e.toString());
                                }
                            }
                            BarDataSet barDataSetP1 = new BarDataSet(entries,"1번빨래통");
                            barDataSetP1.setColor(Color.RED);
                            barDataSetP1.setValueTextColor(Color.BLACK);
                            barDataSetP1.setValueTextSize(16f);

                            barDataP.addDataSet(barDataSetP1);

                        }

                    }
                    else{

                    }
                }
            });
            docRef.collection("featureAnalysis").document("period2").
                    get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if(task!=null){

                        Map map = document.getData();

                        if(map.size() != 0) {
                            ArrayList<BarEntry> entries = new ArrayList<>();
                            List<String> keyList = new ArrayList<>(map.keySet());
                            for (int i = 0; i < 6 && i < keyList.size() - 1; i++) {
                                float now = Float.valueOf(keyList.get(i));
                                float next = Float.valueOf(keyList.get(i + 1).toString());

                                try{
                                    Date date1 = dateFormat.parse(keyList.get(i));
                                    Date date2 = dateFormat.parse(keyList.get(i+1));
                                    long day = (date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24);

                                    entries.add(new BarEntry(now,day));
                                    Log.d("지금",now +" p2 "+ day +"");
                                }catch(Exception e){
                                    Log.d("지금",keyList.get(i));
                                    Log.d("지금",keyList.get(i+1));
                                    Log.d("지금",e.toString());
                                }
                            }
                            BarDataSet barDataSetP2 = new BarDataSet(entries, "2번빨래통");
                            barDataSetP2.setColor(Color.BLUE);
                            barDataSetP2.setValueTextColor(Color.BLACK);
                            barDataSetP2.setValueTextSize(16f);

                            barDataP.addDataSet(barDataSetP2);
                        }
                    }
                    else{

                    }
                }
            });


            docRef.collection("featureAnalysis").document("weight_increment1").
                    get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if(task!=null){

                        Map map = document.getData();
                        if(map.size() != 0) {
                            ArrayList<BarEntry> entries = new ArrayList<>();
                            List<String> keyList = new ArrayList<>(map.keySet());
                            for (int i = 0; i < 7 && i < keyList.size() - 1; i++) {
                                float now = Float.valueOf(keyList.get(i).toString());
                                float weight = Float.valueOf(String.valueOf(map.get(keyList.get(i))));
                                entries.add(new BarEntry(now, weight));
                                Log.d("지금",now +" w1 "+ weight +"");
                            }
                            BarDataSet barDataSetW1 = new BarDataSet(entries, "1번빨래통");
                            barDataSetW1.setColor(Color.RED);
                            barDataSetW1.setValueTextColor(Color.BLACK);
                            barDataSetW1.setValueTextSize(16f);


                            barDataW.addDataSet(barDataSetW1);
                        }

                    }


                    else{

                    }
                }
            });
            docRef.collection("featureAnalysis").document("weight_increment2").
                    get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if(task!=null){
                        Map map = document.getData();
                        if(map.size() != 0) {
                            ArrayList<BarEntry> entries = new ArrayList<>();
                            List<String> keyList = new ArrayList<>(map.keySet());
                            for (int i = 0; i < 7 && i < keyList.size() - 1; i++) {
                                float now = Float.valueOf(keyList.get(i));
                                float weight = Float.valueOf(String.valueOf(map.get(keyList.get(i))));
                                entries.add(new BarEntry(now, weight));
                                Log.d("지금",now +" w2 "+ weight +"");
                            }
                            BarDataSet barDataSetW1 = new BarDataSet(entries, "1번빨래통");
                            barDataSetW1.setColor(Color.RED);
                            barDataSetW1.setValueTextColor(Color.BLACK);
                            barDataSetW1.setValueTextSize(16f);

                            Log.d("지금",barDataP.getDataSetCount() + "ㅋㅋㅋㅋ" + barDataW.getDataSetCount());
                            barDataW.addDataSet(barDataSetW1);
                            Log.d("지금",barDataP.getDataSetCount() + "ㅋㅋㅋㅋ" + barDataW.getDataSetCount());
                        }
                    }
                    else{

                    }
                }
            });



        }
        Log.d("지금",barDataP.getDataSetCount() + "ㅋㅋㅋㅋ" + barDataW.getDataSetCount());

        if(barDataP.getDataSetCount() !=0){
            barDataP.setBarWidth(0.3f);
            periodChart1.animateY(5000);
            periodChart1.setTouchEnabled(false);
            periodChart1.setMaxVisibleValueCount(5);
            periodChart1.setData(barDataP);
            periodChart1.invalidate();
            Log.d("지금",barDataP.getDataSetCount() + "ㅋㅋㅋㅋ" + barDataW.getDataSetCount());
        }

        if(barDataW.getDataSetCount() != 0) {
            barDataW.setBarWidth(0.3f);
            incChart1.animateY(5000);
            periodChart1.setMaxVisibleValueCount(7);
            incChart1.setTouchEnabled(false);
            incChart1.setData(barDataW);
            incChart1.invalidate();
            Log.d("지금",barDataP.getDataSetCount() + "ㅋㅋㅋㅋ" + barDataW.getDataSetCount());
        }

        averInc1.setText(activity.userFeature.getAver_inc1()+"kg");
        averInc2.setText(activity.userFeature.getAver_inc2()+"kg");
        averPeriod1.setText(activity.userFeature.getPriod1()+"일");
        averPeriod2.setText(activity.userFeature.getPriod2()+"일");

        return rootview;
    }
}