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

import com.example.ex1.dataStructure.Datas;
import com.example.ex1.dataStructure.UserFeature;
import com.example.ex1.dataStructure.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;


public class UserInfoFragment extends Fragment {

    FirebaseUser user;



    //변수 선언부분
    SignUpActivity activity;

    String TAG = "UserInfoFragment";

    EditText infoName;
    EditText infoPhoneNum;
    EditText volumeText;
    RadioGroup pongpongGroup;
    Button initUserInfo;
    Spinner spinner;



    int laundryVol;
    String detergentType;
    String location;
    String name;
    String phoneNum;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//이 메소드가 호출될떄는 프래그먼트가 엑티비티위에 올라와있는거니깐 getActivity메소드로 엑티비티참조가능
        activity = (SignUpActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
//이제 더이상 엑티비티 참초가안됨
        activity = null;
    }

    private void init(String location, String detergentType, int laundryVol, String name, String phoneNum){
        // 데이터베이스 users 콜렉션 안에서 사용자 회원별 uid document로 접속
        DocumentReference docRef = activity.db.collection("datas").document(user.getUid()).collection("userInfo").document("userInfo");
        UserInfo userInfo = new UserInfo(location,detergentType,laundryVol,name,phoneNum, activity.userId);
        docRef.set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.startToast(e.toString());
                    }
                });

        docRef = activity.db.collection("datas").document(user.getUid()).collection("userInfo").document("userFeature");
        UserFeature userFeature = new UserFeature(7.0,7.0,1.0,1.0,7.0,7.0, CalendarDay.today().toString(),CalendarDay.today().toString());
        docRef.set(userFeature)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.startToast(e.toString());
                    }
                });
        docRef = activity.db.collection("datas").document(user.getUid()).collection("featureAnalysis").document("period1");
        docRef.set(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.startToast(e.toString());
                    }
                });

        docRef = activity.db.collection("datas").document(user.getUid()).collection("featureAnalysis").document("period2");
        docRef.set(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.startToast(e.toString());
                    }
                });
        docRef = activity.db.collection("datas").document(user.getUid()).collection("featureAnalysis").document("preweight1");
        docRef.set(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.startToast(e.toString());
                    }
                });
        docRef = activity.db.collection("datas").document(user.getUid()).collection("featureAnalysis").document("weight_increment1");
        docRef.set(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.startToast(e.toString());
                    }
                });
        docRef = activity.db.collection("datas").document(user.getUid()).collection("featureAnalysis").document("weight_increment2");
        docRef.set(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        activity.startToast("로그인을 진행해 주세요.");
                        Intent intent = new Intent(activity, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.startToast(e.toString());
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_user_info, container, false);
        // Inflate the layout for this fragment

        infoName = rootview.findViewById(R.id.infoName);
        infoPhoneNum = rootview.findViewById(R.id.infoPhoneNum);
        volumeText = rootview.findViewById(R.id.volumeText);

        pongpongGroup = rootview.findViewById(R.id.pongpongGroup);
        pongpongGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.pongpong1){
                    detergentType = "액체세제";
                }
                else if(i == R.id.pongpong2){
                    detergentType = "고체세제";
                }
            }
        });

        spinner = rootview.findViewById(R.id.spinner_location);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.location_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0: location = "서울";
                    case 1: location = "인천";
                    case 2: location = "경기도";
                    case 3: location = "강원도";
                    case 4: location = "충청북도";
                    case 5: location = "충청남도";
                    case 6: location = "전라북도";
                    case 7: location = "전라남도";
                    case 8: location = "경상북도";
                    case 9: location = "경상남도";
                    case 10: location = "제주도";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initUserInfo = rootview.findViewById(R.id.initUserInfo);
        initUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(location !=null && detergentType != null){
                    name = infoName.getText().toString();
                    phoneNum = infoPhoneNum.getText().toString();
                    laundryVol = Integer.parseInt(volumeText.getText().toString());
                    init(location,detergentType,laundryVol,name,phoneNum);
                }
                else{
                    activity.startToast("모든 값을 입력해주세요.");
                }
            }
        });
        return rootview;
    }
}