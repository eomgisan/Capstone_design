package com.example.ex1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainAcitivity";

    // 바텀네비게이션바
    BottomNavigationView bottomNavigationView;

    // 프레그먼트
    Fragment homeFM = new HomeFragment();
    Fragment settingFM = new SettingFragement();
    Fragment recommandFM = new RecommandFragment();

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






    // 메임함수 시작시 선언부분

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // xml 설정
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.disable();
        startFirebase();

    }



    // 블루투스 관련 내용

    // 블루투스 인텐트한거의 결과값에 따라 분기 설정
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG,"블루투스 허용");

                        // 페어링할 것들 보기

                    }
                    else if (result.getResultCode() == Activity.RESULT_CANCELED){
                        Log.d(TAG,"블루투스 거절");
                        finish();
                    }
                }
            });



     // 블루투스 on 함수
    protected void blueToothOn(TextView status) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "This device doesn't support bluetooth service", Toast.LENGTH_SHORT).show();
            status.setText("블루투스 동작 불가능한 기기입니다.");


        } else if (mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Already On", Toast.LENGTH_SHORT).show();
            // 활성화가 이미 되어있는것으로 페어링 단계를 들어가야함
            status.setText("블루투스 권한 허용");


        } else {
            // 활성화 요청을 위해 ACTION_REQUEST_ENABLE 인텐트 객체 생성
            // 이는 시스템 액티비티를 화면을 띄워서 블루투스를 직접 할수 있다.

            Intent intentBluetoothEnable = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityResult.launch(intentBluetoothEnable);

            if(mBluetoothAdapter.isEnabled()){
                status.setText("블루투스 권한 허용");
            }
            else{
                status.setText("블루투스 권한 거절");
            }
            // TODO : 여기 브로드캐스트 리시버 설정해서 그에 따른 status 변경 코드 넣기
        }
    }

     //블루투스 리스트 출력 함수
    protected void selectDevice(){

        // 페어링 된 객체 집합 불러오기
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("기존 페어링 장치 중 선택");

        // 리스트 배열 만들기
        List<String> pairedList = new ArrayList<>();
        for(BluetoothDevice device : pairedDevices) {
            pairedList.add(device.getName());
        }
        pairedList.add("취소");

        final CharSequence[] devices = pairedList.toArray(new CharSequence[pairedList.size()]);
        builder.setItems(devices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == pairedList.size()-1) {
                    selectUnpairedDevice();
                } else {
                    mBluetoothAdapter.cancelDiscovery();
                    paired = true;
                    connectDevice(devices[which].toString(), paired);
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();



    }

    protected final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if(device.getName() != null) {
                        add(device);
                    }
                }
            }
        }
    };


    protected void add(BluetoothDevice device) {
        if(!(pairedDevices.contains(device))) {
            if(unpairedDevices.add(device)) {
                unpairedList.add(device.getName());
            }
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(this, device.getName()+" 검색", Toast.LENGTH_SHORT).show();
    }
    // 이미 페어링된 블루투스 기기 검색
    protected void selectPairedDevice() {
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        AlertDialog.Builder builder = new AlertDialog.Builder(this); // 다이얼로그 생성
        builder.setTitle("기존 페어링 기기 연결");

        List<String> pairedList = new ArrayList<>(); // 페어링된 기기이름 목록
        for(BluetoothDevice device : pairedDevices) { // 페어링된 기기 집합에서
            pairedList.add(device.getName());        // 장치 이름 전부 기기목록에 추가
        }
        pairedList.add("취소"); // 취소버튼 추가

        final CharSequence[] devices = pairedList.toArray(new CharSequence[pairedList.size()]);
        builder.setItems(devices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == pairedList.size()-1) { // 맨 마지막 버튼이 취소 버튼
                    selectUnpairedDevice(); // 페어링되지 않은 기기 검색
                } else {
                    mBluetoothAdapter.cancelDiscovery();
                    paired = true;
                    connectDevice(devices[which].toString(), true); // 선택된 인덱스에 해당하는 기기 연결
                }
            }
        });
        builder.setCancelable(false);    // 배경을 선택하면 무력화되는 것을 막기 위함
        AlertDialog dialog = builder.create(); // 빌더로 다이얼로그 만들기
        dialog.show();   // 다이얼로그 시작
    }

    // 페어링되지 않은 기기 검색
    protected void selectUnpairedDevice() {
        // 이미 검색 중이라면 검색을 종료하고, 다시 검색 시작
        if(mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();

        AlertDialog.Builder builder = new AlertDialog.Builder(this); // 다이얼로그 생성
        builder.setTitle("페어링할 기기 탐색");

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, unpairedList);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBluetoothAdapter.cancelDiscovery();
                String name = adapter.getItem(which);
                unpairedList.remove(name);
                paired = false;
                connectDevice(name, false);
            }
        });
        AlertDialog dialog = builder.create(); // 빌더로 다이얼로그 만들기
        dialog.show();
    }

    // 블루투스 페어링된 목록에서 디바이스 기기 가져오기
    protected BluetoothDevice getPairedDevice(String name) {
        BluetoothDevice selectedDevice = null;

        for(BluetoothDevice device : pairedDevices) {
            if(name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }
    // 페어링되지 않은 기기 목록에서 디바이스 기기 가져오기
    protected BluetoothDevice getUnpairedDevice(String name) {
        BluetoothDevice selectedDevice = null;

        for(BluetoothDevice device : unpairedDevices) {
            if(name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }


    private void connectDevice(String selectedDeviceName, boolean paired) {
        final Handler mHandler = new Handler() {	// 핸들러 객체 생성
            public void handleMessage(Message msg) {	// handleMessage() 메서드 재정의
                Log.d(TAG,"핸들러 생성");
                if(msg.what==1) {			// 받은 메시지가 1이라면
                    try{
                        Log.d(TAG,"인풋 아웃풋 스트림 생성");
                        // 입출력 스트림 객체 생성
                        outputStream = bluetoothSocket.getOutputStream();
                        inputStream = bluetoothSocket.getInputStream();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                } else { // 연결 오류나면
                    Toast.makeText(getApplicationContext(), "연결 오류", Toast.LENGTH_SHORT).show();
                    try {
                        bluetoothSocket.close(); // 소켓 닫아주고 리소스 해제
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        // 별도 스레드 생성
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "스레드 생성");
                if (paired){  // 페어링된 기기라면
                    Log.d(TAG, "페어링 기기?");
                mbluetoothDevice = getPairedDevice(selectedDeviceName);
                }
                else {    // 페어링되지 않은 기기라면
                    Log.d(TAG, "페어링 아님?");
                    mbluetoothDevice = getUnpairedDevice(selectedDeviceName);
                }
                // UUID 생성
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                try {
                    bluetoothSocket = mbluetoothDevice.createRfcommSocketToServiceRecord(uuid); // 소켓 생성
                    bluetoothSocket.connect(); 	  // 소켓 연결
                    mHandler.sendEmptyMessage(1); // 핸들러에 메시지 1 보내기
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(-1); // 핸들러에 메시지 -1 보내기
                }
            }
        });
        thread.start(); // 별도 스레드 시작
    }

    protected void sendData(){
        userUid = user.getUid().getBytes();
        Log.d(TAG,userUid.toString());
        try{
            outputStream.write(userUid);
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
        unregisterReceiver(mReceiver);
        super.onDestroy();
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

                case R.id.menu_logout:
                    Log.d(TAG, "onNavigationItemSelected: logout button clicked");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("확인");
                    builder.setMessage("로그아웃 하시겠습니까?");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                }
                            });
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            });

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();

                    return true;

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
    // TODO : 2개 이전으로 가고있어지금

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
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
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