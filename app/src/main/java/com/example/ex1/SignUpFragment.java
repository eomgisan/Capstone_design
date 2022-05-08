package com.example.ex1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ex1.dataStructure.Datas;
import com.example.ex1.dataStructure.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUpFragment extends Fragment {


    //변수 선언부분
    SignUpActivity activity;

    String TAG = "SignUpFragment";

    EditText loginEmail;
    EditText userId;
    EditText loginPassword;
    EditText loginPassword2;

    Button goToUserInfo;

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

    private void checkID(String LoginEmail, String LoginPassword, String LoginPassword2,String UserId){

        DocumentReference docRef = activity.db.collection("datas").document("ID");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            // DocumentSnapshot 자료형인 task에 결과를 저장
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                        try{
                            Log.d("zzzzzz",document.getData().get(UserId).toString());
                            activity.startToast("아이디가 중복되었습니다. 다시입력해주세요");
                        }
                        catch(Exception e){
                            if(LoginEmail.length()>0 && LoginPassword.length() > 0 && LoginPassword.length() >0){


                                if (LoginPassword.equals(LoginPassword2)) {

                                    activity.mAuth.createUserWithEmailAndPassword(LoginEmail, LoginPassword)
                                            .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        // Sign in success, update UI with the signed-in user's information

                                                        activity.startToast("회원가입에 성공하였습니다.");
                                                        //성공시 로직

                                                        activity.user = activity.mAuth.getCurrentUser();
                                                        activity.userId = UserId;
                                                        activity.loginEmail = LoginEmail;
                                                        activity.loginPassword = LoginPassword;

                                                        DocumentReference docRef = activity.db.collection("datas").document("ID");
                                                        docRef.update(UserId,"id");

                                                        Handler mHandler = new Handler();
                                                        mHandler.postDelayed(new Runnable() {
                                                            public void run() {
                                                                activity.goToSignUpFM();
                                                            }
                                                        }, 1000); // 0.5초후

                                                        // 프레그먼트 전환



                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        if (task.getException() != null) {
                                                            activity.startToast(task.getException().toString());
                                                        }
                                                        //실패시 로직
                                                    }
                                                }
                                            });
                                } else {
                                    activity.startToast("비밀번호가 일치하지 않습니다.");
                                }
                            }else{
                                activity.startToast("이메일 또는 비밀번호를 입력해주세요.");
                            }
                        }
                    }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_sign_up, container, false);

        loginEmail = rootview.findViewById(R.id.loginEmail);
        userId = rootview.findViewById(R.id.userId);
        loginPassword = rootview.findViewById(R.id.loginPassword);
        loginPassword2 = rootview.findViewById(R.id.loginPassword2);
        goToUserInfo = rootview.findViewById(R.id.goToUserInfo);

        goToUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkID(loginEmail.getText().toString(),loginPassword.getText().toString()
                        ,loginPassword2.getText().toString(),userId.getText().toString());
            }
        });



        return rootview;
    }
}