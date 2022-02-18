package com.example.ex1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;


public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    // 데이터베이스 초기화
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "homeFragment";


    MainActivity activity;
    PieChart chart1;
    PieChart chart2;
    TextView weight1;
    TextView weight2;
    TextView temp;
    TextView humi;
    TextView smell;

    Button refresh;

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

    private void init(){
        // 데이터베이스 users 콜렉션 안에서 사용자 회원별 uid document로 접속
        DocumentReference docRef = db.collection("datas").document(user.getUid());

        // document에서 불러오는 위 쿠드가 수행 완료시 동작
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            // DocumentSnapshot 자료형인 task에 결과를 저장
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    // 정상적으로 동작 했을 경우 document로 복사
                    DocumentSnapshot document = task.getResult();

                    if(document != null) {
                        // document가 비어있을경우 확인
                        if (document.exists()) {
                            // 데이터베이스에서 센서 정보 가져오기
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());


                            activity.weight1 = Float.parseFloat(document.getData().get("weight1").toString());
                            activity.weight2 = Float.parseFloat(document.getData().get("weight2").toString());
                            activity.smell   = Float.parseFloat(document.getData().get("smell").toString());
                            activity.temp    = Float.parseFloat(document.getData().get("temp").toString());
                            activity.humi    = Float.parseFloat(document.getData().get("humi").toString());
                            activity.vol1    = Float.parseFloat(document.getData().get("vol1").toString());
                            activity.vol2    = Float.parseFloat(document.getData().get("vol2").toString());

                            weight1.setText(String.valueOf(activity.weight1));
                            weight2.setText(String.valueOf(activity.weight2));
                            temp.setText(String.valueOf(activity.temp));
                            humi.setText(String.valueOf(activity.humi));
                            smell.setText(String.valueOf(activity.smell));

                            chart1.addPieSlice(new PieModel("빨래통 1", activity.vol1, Color.parseColor("#CDA67F")));
                            chart1.addPieSlice(new PieModel("남은공간 1", 100-activity.vol1, Color.parseColor("#FE6DA8")));
                            chart2.addPieSlice(new PieModel("빨래통 2", activity.vol2, Color.parseColor("#CDA67F")));
                            chart2.addPieSlice(new PieModel("남은공간 2", 100-activity.vol2, Color.parseColor("#FE6DA8")));

                            chart1.startAnimation();
                            chart2.startAnimation();

                        } else {
                            // 데이터베이스에 회원정보 없으면 블루투스 프레그먼트 전환
                            Log.d(TAG, "No such document");

                            getActivity().getSupportFragmentManager()
                                    .popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_ly, new BluetoothFragment(),"bluetooth")
                                    .setReorderingAllowed(true)
                                    .addToBackStack("bluetoothFM")
                                    .commit();
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_home,container,false);

        chart1 = (PieChart) rootview.findViewById(R.id.piechart1);
        chart2 = (PieChart) rootview.findViewById(R.id.piechart2);
        weight1 = (TextView) rootview.findViewById(R.id.weight1);
        weight2 = (TextView) rootview.findViewById(R.id.weight2);
        temp = (TextView) rootview.findViewById(R.id.temp);
        humi = (TextView) rootview.findViewById(R.id.humi);
        smell = (TextView) rootview.findViewById(R.id.smell);

        refresh = (Button) rootview.findViewById(R.id.refreshBTN) ;

// 여기에 동작부분 넣기
        mAuth = FirebaseAuth.getInstance();

        init();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO : 블루투스 통신으로 재측정 하라는 신호 보내기
                activity.sendData();
                init();
            }
        });



        // Inflate the layout for this fragment
        return rootview;
    }
}
