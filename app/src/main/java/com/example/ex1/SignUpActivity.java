package com.example.ex1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ex1.dataStructure.Datas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

// 회원가입 엑티비티
public class SignUpActivity  extends AppCompatActivity {

    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public static final String TAG = "SignUpActivity";

    String userId;
    String loginEmail;
    String loginPassword;

    Fragment signUpFM = new SignUpFragment();
    Fragment userInfoFM = new UserInfoFragment();






    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getSupportFragmentManager().popBackStack("signUp", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.signUp_ly, signUpFM,"signUp")
                .setReorderingAllowed(true)
                .addToBackStack("signUpFM")
                .commitAllowingStateLoss();

    }

    public void goToSignUpFM(){
        getSupportFragmentManager()
                .popBackStack("userInfo", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.signUp_ly, userInfoFM,"userInfo")
                .setReorderingAllowed(true)
                .addToBackStack("userInfoFM")
                .commit();
    }


    public void startToast( String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // 뒤로가기시 앱 종료 함수

    long pressedTime = 0; //'뒤로가기' 버튼 클릭했을 때의 시간
    @Override
    public void onBackPressed() {
            //마지막으로 누른 '뒤로가기' 버튼 클릭 시간이 이전의 '뒤로가기' 버튼 클릭 시간과의 차이가 2초보다 크면
            if(System.currentTimeMillis() > pressedTime + 2000){
                //현재 시간을 pressedTime 에 저장
                pressedTime = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(),"한번 더 누르면 종료", Toast.LENGTH_SHORT).show();
            }

            //마지막 '뒤로가기' 버튼 클릭시간이 이전의 '뒤로가기' 버튼 클릭 시간과의 차이가 2초보다 작으면
            else{
                Toast.makeText(getApplicationContext(),"종료 완료", Toast.LENGTH_SHORT).show();
                if(user != null){
                    user.delete();
                }
                // 앱 종료
                moveTaskToBack(true); // 태스크를 백그라운드로 이동
                finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기

                System.exit(0);
            }
    }

}