package com.example.ex1;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class SettingFragement extends Fragment {




    String TAG = "settingFragment";

    //변수 선언부분
    MainActivity activity;
    EditText volume;
    RadioGroup pongpongGroup;
    Spinner spinner;

    // 데이터베이스 초기화
    FirebaseUser user;
    FirebaseFirestore db;

    Button uploadSetting;

    int volume_set;
    int location_set;
    int pongpong_set;






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
        volume.setText(String.valueOf(activity.userInfo.getLaundryVol()));

        if(activity.userInfo.getDetegentType() == "고체세제"){
            pongpongGroup.check(R.id.pongpong1);
        }
        else{
            pongpongGroup.check(R.id.pongpong2);
        }


        if(activity.userInfo.getLocation()=="서울"){
            location_set = 0;
        }
        else if(activity.userInfo.getLocation()=="인천"){
            location_set = 1;
        }
        else if(activity.userInfo.getLocation()=="경기도"){
            location_set = 2;
        }
        else if(activity.userInfo.getLocation()=="강원도"){
            location_set = 3;
        }
        else if(activity.userInfo.getLocation()=="충청북도"){
            location_set = 4;
        }
        else if(activity.userInfo.getLocation()=="충청남도"){
            location_set = 5;
        }
        else if(activity.userInfo.getLocation()=="전라북도"){
            location_set = 6;
        }
        else if(activity.userInfo.getLocation()=="전라남도"){
            location_set = 7;
        }
        else if(activity.userInfo.getLocation()=="경상북도"){
            location_set = 8;
        }
        else if(activity.userInfo.getLocation()=="경상남도"){
            location_set = 9;
        }
        else if(activity.userInfo.getLocation()=="제주도"){
            location_set = 10;
        }

        spinner.setSelection(location_set);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_setting,container,false);
        // Inflate the layout for this fragment

        user = activity.user;
        db = activity.db;

        volume = rootview.findViewById(R.id.volumeText);


        spinner = rootview.findViewById(R.id.spinner_location);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.location_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                location_set = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        pongpongGroup = rootview.findViewById(R.id.pongpongGroup);
        pongpongGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.pongpong1){
                    pongpong_set = 0;
                }
                else if(i == R.id.pongpong2){
                    pongpong_set = 1;
                }
            }
        });

        init();

        uploadSetting = rootview.findViewById(R.id.uploadSetting);
        uploadSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                volume_set = Integer.parseInt(volume.getText().toString());


                // db저장
                activity.userInfo.setLaundryVol(volume_set);

                if (pongpong_set == 0) {
                    activity.userInfo.setDetegentType("고체세제");
                } else {
                    activity.userInfo.setDetegentType("액체세제");
                }

                switch (location_set) {
                    case 0:
                        activity.userInfo.setLocation("서울");
                    case 1:
                        activity.userInfo.setLocation("인천");
                    case 2:
                        activity.userInfo.setLocation("경기도");
                    case 3:
                        activity.userInfo.setLocation("강원도");
                    case 4:
                        activity.userInfo.setLocation("충청북도");
                    case 5:
                        activity.userInfo.setLocation("충청남도");
                    case 6:
                        activity.userInfo.setLocation("전라북도");
                    case 7:
                        activity.userInfo.setLocation("전라남도");
                    case 8:
                        activity.userInfo.setLocation("경상북도");
                    case 9:
                        activity.userInfo.setLocation("경상남도");
                    case 10:
                        activity.userInfo.setLocation("제주도");
                }

                db.collection("datas").document(user.getUid())
                        .collection("userinfo").document("userInfo").set(activity.userInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                activity.startToast("개인 설정 정보 저장 완료");
                                activity.bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                                activity.updateBottomBar();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                activity.startToast("개인 설정 정보 저장 실패");
                                Log.e(TAG, e.toString());
                            }
                        });
            }
        });
        return rootview;
    }
}