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

import com.example.ex1.dataStructure.Setting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SettingFragement extends Fragment {



    // 데이터베이스 초기화
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "settingFragment";

    //변수 선언부분
    MainActivity activity;
    EditText volume;
    RadioGroup pongpongGroup;
    Spinner spinner;
    Button goToUserInfo;
    Button complete;

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
        DocumentReference docRef = db.collection("setting").document(user.getUid());

        // document에서 불러오는 위 쿠드가 수행 완료시 동작
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            // DocumentSnapshot 자료형인 task에 결과를 저장
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    // 정상적으로 동작 했을 경우 document로 복사
                    DocumentSnapshot document = task.getResult();

                    if (document != null) {
                        // document가 비어있을경우 확인
                        if (document.exists()) {
                            // 데이터베이스에서 센서 정보 가져오기
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            volume.setText(String.valueOf(activity.laundryVol));
                            if(activity.pongpong == 0){
                                pongpongGroup.check(R.id.pongpong1);
                            }
                            else{
                                pongpongGroup.check(R.id.pongpong2);
                            }
                            spinner.setSelection(activity.location);


                        } else {
                            activity.startToast("설정 정보가 데이터 베이스에 없습니다.");
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

        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_setting,container,false);
        // Inflate the layout for this fragment

        volume = rootview.findViewById(R.id.volumeText);
        init();

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

        complete = rootview.findViewById(R.id.setting_complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                volume_set = Integer.parseInt(volume.getText().toString());

                activity.location = location_set;
                activity.laundryVol = volume_set;
                activity.pongpong = pongpong_set;

                // db저장
                Setting setting = new Setting(location_set,pongpong_set,volume_set);
                if (user!=null) {
                    db.collection("setting").document(user.getUid()).set(setting)
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
                                    Log.e(TAG,e.toString());
                                }
                            });
                }

            }
        });


        goToUserInfo = rootview.findViewById(R.id.goToUserInfo);
        goToUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, UserInfoActivity.class));
            }
        });

        return rootview;
    }
}