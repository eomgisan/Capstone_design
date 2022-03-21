package com.example.ex1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ex1.camera.CameraActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserInfoActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    // 데이터베이스 초기화
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // 파이어 스토리지에서 사진 가져오기 위해 선언
    FirebaseStorage storage = FirebaseStorage.getInstance();
    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.init_info_button).setOnClickListener(onClickListener);
        ImageView profileImage = (ImageView)findViewById(R.id.profileImage);
        profileImage.setOnClickListener(onClickListener);



        // 여기서 회원정보에 있는거 다 먼저 불러오기
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {

                        EditText name = (EditText)findViewById(R.id.infoName);
                        EditText phoneNum = (EditText)findViewById(R.id.infoPhoneNum);
                        EditText address = (EditText)findViewById(R.id.infoAddress);
                        EditText birth = (EditText)findViewById(R.id.infoBirth);
                        ImageView profileImage = (ImageView)findViewById(R.id.profileImage);


                        name.setText(document.getData().get("name").toString());
                        phoneNum.setText(document.getData().get("phoneNum").toString());
                        address.setText(document.getData().get("address").toString());
                        birth.setText(document.getData().get("birth").toString());

                        StorageReference imagesRef = storageRef.child("images/"+user.getUid()+"/profileImage");

                        if(imagesRef == null){
                            Log.d("TAG","프로필 사진 등록 요망");
                        }else{
                            imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(UserInfoActivity.this).load(uri).into(profileImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG","프로필 사진 불러오기 오류");
                                }
                            });
                        }


                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });



    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.init_info_button:
                    init();
                    break;
                case R.id.profileImage:
                    ImageView profileImage = (ImageView)findViewById(R.id.profileImage);
                    startToast("프로필 사진 촬영");
                    startActivity(new Intent(UserInfoActivity.this, CameraActivity.class));
            }
        }
    };

    private void init(){

        // 회원정보등록xml 파일에 있는 텍스트를 가져옴
        String name = ((EditText)findViewById(R.id.infoName)).getText().toString();
        String phoneNum = ((EditText)findViewById(R.id.infoPhoneNum)).getText().toString();
        String address = ((EditText)findViewById(R.id.infoAddress)).getText().toString();
        String birth = ((EditText)findViewById(R.id.infoBirth)).getText().toString();


        // 입력란 빈공간 확인
        if(phoneNum.length()>0 && name.length() >0 && address.length() >0 && birth.length() >0 ){

            UserInfo userInfo = new UserInfo(name,phoneNum,address,birth);
            if (user!=null) {

                db.collection("users").document(user.getUid()).set(userInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                startToast("회원정보 입력 성공");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startToast(e.toString());
                            }
                        });
            }

        }else{
            startToast("회원정보를 입력해주세요.");
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

    private void DownloadImg(int num){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("images/"+user.getUid()+"/profileImage");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

            }
        });
    }
    // 토스트 창을 띄우기 위한 함수
    private void startToast( String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}