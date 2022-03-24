package com.example.ex1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.ex1.dataStructure.Setting;
import com.example.ex1.network.NetworkManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainAcitivity";

    // 바텀네비게이션바
    BottomNavigationView bottomNavigationView;

    String userName;
    String userPhoneNum;
    String userBirth;
    String userAddress;

    // 프레그먼트
    Fragment homeFM = new HomeFragment();
    Fragment settingFM = new SettingFragement();
    Fragment recommandFM = new RecommandFragment();
    Fragment featureFM = new FeatureFragment();
    Fragment bluetoothFM = new BluetoothFragment();

    // 블루투스
     // 블루투스 모드
    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;


     // 블루투스 어댑터
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mbluetoothDevice;

     // 블루투스 페어링 관련
    Set<BluetoothDevice> pairedDevices;
    boolean paired = false;

    Set<BluetoothDevice> unpairedDevices = new HashSet<>();
    List<String> unpairedList = new ArrayList<>();
    ArrayAdapter<String> adapter;

     // 블루투스 데이터 송수신 관련
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;
    InputStream inputStream;
    byte[] userUid;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // home 변수
    float weight1;
    float weight2;
    float smell;
    float vol1;
    float vol2;
    float temp;
    float humi;

    //setting 변수
    int laundryVol;
    int pongpong;
    int location;



// 블루투스 관련 함수

    protected void sendData(String string){
        byte[] data = string.getBytes();
        Log.d("blutoothSend",string);
        try{
            outputStream.write(data);
            Log.d(TAG,"블루투스 통신 성공");
        }
        catch(Exception e){
            e.printStackTrace();
            startToast("데이터 전송 오류");
        }
    }





    @Override
    protected void onDestroy() {
        try{
            inputStream.close();
            outputStream.close();
            bluetoothSocket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        FirebaseAuth.getInstance().signOut();
        //unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    // 메임함수 시작시 선언부분

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NetworkManager network = new NetworkManager(getApplicationContext());
        network.registerNetworkCallback();

        // Check network connection
        if (Variables.isNetworkConnected()){
            // Internet Connected
        }else{
            // Not Connected
        }

        // xml 설정
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.disable();
        startFirebase();

    }


    // 바텀 네이게이션바 선택관련 문자
    NavigationBarView.OnItemSelectedListener onItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()){
                case R.id.menu_recommand:
                    Log.d(TAG, "onNavigationItemSelected: recommand button clicked");

                    getSupportFragmentManager().popBackStack("recommand", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_ly, recommandFM,"recommand")
                            .setReorderingAllowed(true)
                            .addToBackStack("recommandFM")
                            .commitAllowingStateLoss();
                    Log.d(TAG,recommandFM.getTag());
                    return true;

                case R.id.menu_community:


                    Log.d(TAG, "onNavigationItemSelected: community button clicked");
                    return true;


                case R.id.menu_home:


                    Log.d(TAG, "onNavigationItemSelected: home button clicked");


                    getSupportFragmentManager().popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_ly, homeFM,"home")
                            .setReorderingAllowed(true)
                            .addToBackStack("homeFM")
                            .commitAllowingStateLoss();
                    Log.d(TAG,homeFM.getTag());

                    return true;
                case R.id.menu_setting:


                    Log.d(TAG, "onNavigationItemSelected: setting button clicked");

                    getSupportFragmentManager().popBackStack("setting", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_ly, settingFM,"setting")
                            .setReorderingAllowed(true)
                            .addToBackStack("settingFM")
                            .commitAllowingStateLoss();
                    Log.d(TAG,settingFM.getTag());
                    return true;

                case R.id.menu_feature:

                    Log.d(TAG, "onNavigationItemSelected: feature button clicked");

                    getSupportFragmentManager().popBackStack("feature", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_ly, featureFM,"feature")
                            .setReorderingAllowed(true)
                            .addToBackStack("featureFM")
                            .commitAllowingStateLoss();
                    Log.d(TAG,featureFM.getTag());
                    return true;
                    /*


                    return true;

                     */

                default:
                    return false;
            }
        }
    };









    // 프레그먼트 관련 함수

    public String getCurrentFragment(){
        String result = "";
        for(Fragment fragment: getSupportFragmentManager().getFragments()){
            if (fragment.isVisible()){
                result = fragment.getTag();
            }
        }
        return result;
    }







    // 바텀 네비게이션바 업데이트 함수


    public void updateBottomBar(){
        String tag = getCurrentFragment();
        Log.d(TAG,tag);

        if(tag.equals("recommand")){
            bottomNavigationView.getMenu().findItem(R.id.menu_recommand).setChecked(true);
        }
        else if(tag.equals("home") || tag.equals("bluetooth")){
            bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
        }
        else if (tag.equals("setting") ){
            bottomNavigationView.getMenu().findItem(R.id.menu_setting).setChecked(true);
        }
        else{
            //
        }
    }









    // 파이어베이스 사용자 회원정보 가져오기 ( 인적사항 )

    public void startFirebase(){
        // 현재 사용자 누군지 확인
//TODO : 와이파이 연결 없을때 즉 데이터 연결 없을때 비정상 종료 확인하기

        // 현재 로그인 여부를 확인해서 로그인 안되어있으면 로그인 화면으로 가는 코드
        if(user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else{
            // 데이터베이스 초기화 <- Firebase Firestore db에서 회원 정보를 가져올 예정
            FirebaseFirestore db = FirebaseFirestore.getInstance();


            // 데이터베이스 users 콜렉션 안에서 사용자 회원별 uid document로 접속
            DocumentReference docRef = db.collection("users").document(user.getUid());

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
                                // 데이터베이스에서 회원정보 가져오기 document에 저장됨
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                userName = document.getData().get("name").toString();
                                userPhoneNum = document.getData().get("phoneNum").toString();
                                userAddress = document.getData().get("address").toString();
                                userBirth = document.getData().get("birth").toString();

                            } else {
                                // 데이터베이스에 회원정보 없으면 회원정보 입력 화면 전환
                                Log.d(TAG, "No such document");
                                startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });



            // 세팅값 가져오기
            docRef = db.collection("setting").document(user.getUid());

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
                                // 데이터베이스에서 회원정보 가져오기 document에 저장됨
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                laundryVol = Integer.parseInt(document.getData().get("volume").toString());
                                location = Integer.parseInt(document.getData().get("location").toString());
                                pongpong = Integer.parseInt(document.getData().get("pongpong").toString());


                            } else {
                                // 데이터베이스에 회원정보 없으면 회원정보 입력 화면 전환
                                Log.d(TAG, "No such document");
                                laundryVol = 0;
                                location = 0;
                                pongpong = 0;

                                Setting setting = new Setting(location,pongpong,laundryVol);
                                if (user!=null) {

                                    db.collection("setting").document(user.getUid()).set(setting)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG,"설정정보 초기화 완료");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG,"설정정보 초기화 실패");
                                                }
                                            });
                                }

                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }








    // 뒤로가기시 앱 종료 함수

    long pressedTime = 0; //'뒤로가기' 버튼 클릭했을 때의 시간
    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
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
        else{
            super.onBackPressed();
            updateBottomBar();
        };


    }








    // 토스트 창을 띄우기 위한 함수
    public void startToast( String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }



}