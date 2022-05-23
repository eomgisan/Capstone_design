package com.example.ex1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;



public class HomeFragment extends Fragment {

    String TAG = "homeFragment";


    MainActivity activity;
    TextView usernameText;

    ImageView BinImage1;
    ImageView BinImage2;

    TextView weight1;
    TextView weight2;
    TextView temp;
    TextView hum;
    TextView smell;

    Button refresh;
    Button goToBlueTooth;
    Button logOut;

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

    private void init() {

        // document가 비어있을경우 확인


        if (activity.datas.isnull) {
            // 데이터베이스에 센서 정보 없으면 블루투스 프레그먼트 전환
            Log.d(TAG, "No such sensorData");

            startActivity(new Intent(activity, BluetoothActivity.class));

        } else {

            usernameText.setText(String.valueOf(activity.userInfo.getName() + "님의 빨래통 상태"));
            weight1.setText(String.valueOf(activity.datas.getWeight1()));
            weight2.setText(String.valueOf(activity.datas.getWeight2()));
            temp.setText(String.valueOf(activity.datas.getTemperature()));
            hum.setText(String.valueOf(activity.datas.getHumidity()));
            smell.setText(String.valueOf(activity.datas.getSmell()));

            if (activity.datas.getWeight1() < 0) {
                BinImage1.setImageResource(R.drawable.bin);
                BinImage1.setColorFilter(Color.rgb(0,255,0));
            } else if (activity.datas.getWeight1()  < 5) {
                BinImage1.setImageResource(R.drawable.bin);
                BinImage1.setColorFilter(Color.rgb(0,255,0));
            } else if (activity.datas.getWeight1()  < 8) {
                BinImage1.setImageResource(R.drawable.bin);
                BinImage1.setColorFilter(Color.rgb(255,255,0));;
            } else {
                BinImage1.setImageResource(R.drawable.bin);
                BinImage1.setColorFilter(Color.rgb(255,0,0));
            }

            if (activity.datas.getWeight2()  < 0) {
                BinImage2.setImageResource(R.drawable.bin);
                BinImage2.setColorFilter(Color.rgb(0,255,0));;
            } else if (activity.datas.getWeight2()  < 5) {
                BinImage2.setImageResource(R.drawable.bin);
                BinImage2.setColorFilter(Color.rgb(0,255,0));;
            } else if (activity.datas.getWeight2()  < 8) {
                BinImage2.setImageResource(R.drawable.bin);
                BinImage2.setColorFilter(Color.rgb(255,255,0));;
            } else {
                BinImage2.setImageResource(R.drawable.bin);
                BinImage2.setColorFilter(Color.rgb(255,0,0));;
            }

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_home,container,false);

        usernameText = (TextView) rootview.findViewById(R.id.usernameText);

        weight1 = (TextView) rootview.findViewById(R.id.weight1);
        weight2 = (TextView) rootview.findViewById(R.id.weight2);
        temp = (TextView) rootview.findViewById(R.id.temp);
        hum = (TextView) rootview.findViewById(R.id.humi);
        smell = (TextView) rootview.findViewById(R.id.smell);

        BinImage1 = (ImageView) rootview.findViewById(R.id.BinImage1);
        BinImage2 = (ImageView) rootview.findViewById(R.id.BinImage2);

        refresh = (Button) rootview.findViewById(R.id.refreshBTN) ;
        goToBlueTooth = rootview.findViewById(R.id.goTOBluetooth);
        logOut = rootview.findViewById(R.id.logOut);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                init();
            }
        }, 2000); // 0.5초후



        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // activity.sendData("재측정 신호!!!!!!!!!!!!!");
                activity.startFirebase();
                init();
                activity.RecommandDates1 = activity.weather(activity.datas.getWeight1(),activity.userFeature.getIdeal_w1(),activity.userFeature.getAver_inc1());
                activity.RecommandDates2 = activity.weather(activity.datas.getWeight2(),activity.userFeature.getIdeal_w2(),activity.userFeature.getAver_inc2());
                activity.startToast("데이터베이스 새로고침 완료");

            }
        });

        goToBlueTooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, BluetoothActivity.class));
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onNavigationItemSelected: logout button clicked");
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("확인");
                builder.setMessage("로그아웃 하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(activity, LoginActivity.class));

                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });





        // Inflate the layout for this fragment
        return rootview;
    }
}
