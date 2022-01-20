package com.example.ex1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    // 시작전에 초기화 또는 선언할 변수들을 onCreate에 선언
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 파이어 베이스에 인증할 객체 생성 후 초기화
        mAuth = FirebaseAuth.getInstance();

        // 각 버튼별로 동작을 해야하므로 버튼을 xml파일에서 선언된것들을 가져옴
        findViewById(R.id.sendButton).setOnClickListener(onClickListener);
        findViewById(R.id.goToSignUp).setOnClickListener(onClickListener);
        findViewById(R.id.goToPassWordReset).setOnClickListener(onClickListener);
    }


    // 시작하면 수행하는것들
    @Override
    public void onStart() {
        super.onStart();
        // 현재의 인증 정보 객체를 FirebaseUser 형식의 변수인 currentUser로 선언
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // 만약 현제 유저가 로그인 되어있으면 reload
        if(currentUser != null){
        //    reload();
        }
    }

    // 버튼이 클릭될때 동작하는 것들
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.sendButton:
                    Login();
                    break;
                case R.id.goToSignUp:
                    // 회원가입 xml로 이동
                    startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                    break;

                case R.id.goToPassWordReset:
                    startActivity(new Intent(LoginActivity.this, PassWordResetActivity.class));
            }
        }
    };

    // 로그인 버튼 클릭시 동작하는 함수
    private void Login(){

        // 로그인xml 파일에 있는 이메일과 패스워드 텍스트를 가져옴
        String email = ((EditText)findViewById(R.id.loginEmail)).getText().toString();
        String password = ((EditText)findViewById(R.id.loginPassword)).getText().toString();

        // 이메일 또는 패스워드가 비어있는지 확인
        if(email.length()>0 && password.length() > 0 ){

            // 파이어베이스 인증 정보 객체에 이메일과 패스워드 받아서 인증 진행
            mAuth.signInWithEmailAndPassword(email, password)
                    // addonCompleteLister 함수를 통해 성공 여부 확인
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                startToast("로그인에 성공하였습니다.");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                }
                                //실패시 로직
                            }
                        }
                    });

        }else{
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }
    }
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
            // 앱 종료
            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기

            System.exit(0);
        }
    }


    // 토스트 창을 띄우기 위한 함수
    private void startToast( String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}