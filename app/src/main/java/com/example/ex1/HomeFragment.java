package com.example.ex1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    // 데이터베이스 초기화
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "homeFragment";


    MainActivity activity;
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


// 여기에 동작부분 넣기
        mAuth = FirebaseAuth.getInstance();

        // 데이터베이스 users 콜렉션 안에서 사용자 회원별 uid document로 접속
        DocumentReference docRef = db.collection("laundrys").document(user.getUid());

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





        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}