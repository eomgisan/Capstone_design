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

public class SignUpActivity  extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
        findViewById(R.id.goToLogin).setOnClickListener(onClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.signUpButton:
                    signUp();
                    break;
                case R.id.goToLogin:
                    goToLoginActivty();
                    break;
            }
        }
    };

    private void signUp(){

        String email = ((EditText)findViewById(R.id.loginEmail)).getText().toString();
        String password = ((EditText)findViewById(R.id.loginPassword)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.setPassword2)).getText().toString();

        if(email.length()>0 && password.length() > 0 && password.length() >0){


            if (password.equals(passwordCheck)) {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    startToast("회원가입에 성공하였습니다.");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //성공시 로직
                                } else {
                                    // If sign in fails, display a message to the user.
                                    if (task.getException() != null) {
                                        startToast(task.getException().toString());
                                    }
                                    //실패시 로직
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }else{
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }
    }

    private void startToast( String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void goToLoginActivty(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}