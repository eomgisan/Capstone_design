package com.example.ex1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String TAG = "BluetoothActivity";

    Button ButtonPair;
    Button ButtonSearch;
    Button ButtonSearch2;
    Switch SwitchBluetooth;
    Button goToHome;
    TextView successText;
    TextView Status;


    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mbluetoothDevice;

    Set<BluetoothDevice> pairedDevices = new HashSet<>();
    Set<BluetoothDevice> unpairedDevices = new HashSet<>();

    List<String> pairedList = new ArrayList<>();
    List<String> unpairedList = new ArrayList<>();

    ArrayAdapter<String> adapter;

    // 블루투스 데이터 송수신 관련
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;
    InputStream inputStream;
    boolean paired = false;
    byte[] userUid;

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
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        SwitchBluetooth = findViewById(R.id.bluetoothSwitch);
        ButtonSearch = findViewById(R.id.searchButton);
        ButtonSearch2 = findViewById(R.id.searchButton2);
        ButtonPair = findViewById(R.id.pairbutton);
        goToHome = findViewById(R.id.goToHomeButton);
        Status = findViewById(R.id.statusText);
        successText = findViewById(R.id.successText);

        boolean isSuccess = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.disable();

        if(mBluetoothAdapter.isEnabled()){
            Status.setText("블루투스 권한 허용");
            SwitchBluetooth.setChecked(true);
        }


        SwitchBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                if (compoundButton.isChecked()){
                    blueToothOn(Status);

                }
                else {
                    Log.d("블루투스 프레그먼트","스위치 꺼짐");
                    mBluetoothAdapter.disable();
                    Status.setText("블루투스 권한 종료");

                }
            }
        });


        ButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SwitchBluetooth.isChecked()){
                    Log.d(TAG, "서치 버튼 동작");
                    selectDevice();
                    Status.setText("블루투스 연결 진행중....");
                }
                else{
                    startToast("위의 스위치를 눌러 블루투스를 먼저 활성화 시켜주세요!");
                }


            }
        });
        ButtonSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SwitchBluetooth.isChecked()) {
                    Log.d(TAG, "서치 버튼 동작");
                    selectUnpairedDevice();
                    Status.setText("블루투스 연결 진행중....");
                } else {
                    startToast("위의 스위치를 눌러 블루투스를 먼저 활성화 시켜주세요!");
                }
            }
        });


        ButtonPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 블루투스 동작
                sendUid(isSuccess);

                // 블루투스 정상적으로 동작했는지 확인하기
                if(isSuccess){
                    successText.setText("사용자 정보 전송 완료");
                }
                else{
                    successText.setText("빨래통이 통신에 준비가 되었는지 확인해주세요");
                    startToast("블루투스 연결을 다시 시도해주세요");
                }
            }
        });

        goToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BluetoothActivity.this, MainActivity.class));
            }
        });


    }
    private void sendData(String string){
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


    private ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.d(TAG,"블루투스 허용");
                        Status.setText("블루투스 권한 허용");
                        // 페어링할 것들 보기

                    }
                    else if (result.getResultCode() == Activity.RESULT_CANCELED){
                        Log.d(TAG,"블루투스 거절");

                        SwitchBluetooth.setChecked(false);

                    }
                }
            });


    // 블루투스 on 함수
    private void blueToothOn(TextView status) {
        if (mBluetoothAdapter == null) {
            Log.d(TAG,"BluetoothOn 함수 동작 - 블루투스어댑터 존재 X");
            startToast("블루투스 동작 불가능한 기기입니다.");
            status.setText("블루투스 동작 불가능한 기기입니다.");


        } else if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG,"BluetoothOn 함수 동작 - 블루투스어댑터 존재 ok, 활성화 이미 완료");
            startToast("이미 블루투스가 활성화 되어있습니다.");
            // 활성화가 이미 되어있는것으로 페어링 단계를 들어가야함
            status.setText("블루투스 권한 허용");


        } else {
            // 활성화 요청을 위해 ACTION_REQUEST_ENABLE 인텐트 객체 생성
            // 이는 시스템 액티비티를 화면을 띄워서 블루투스를 직접 할수 있다.
            Log.d(TAG,"BluetoothOn 함수 동작 - 블루투스어댑터 존재 ok, 활성화 시작");

            Intent intentBluetoothEnable = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityResult.launch(intentBluetoothEnable);

        }
    }

    //블루투스 리스트 출력 함수
    private void selectDevice(){

        // 페어링 된 객체 집합 불러오기
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        Log.d(TAG,"이미 페어링된 객체들" + pairedDevices.toString());

        // 안드로이드에 선언되어있는 alertdialog 사용, 목록 창 띄우는거임
        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
        builder.setTitle("기존 페어링 장치 중 선택");

        // 리스트 배열 만들기
        java.util.List<String> pairedList = new ArrayList<>();
        for(BluetoothDevice device : pairedDevices) {
            pairedList.add(device.getName());
        }



        final CharSequence[] devices = pairedList.toArray(new CharSequence[pairedList.size()]);

        // 빌더에 블루투스 연결할 장치들의 이름을 설정하고 onclicklistener 설정

        builder.setItems(devices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBluetoothAdapter.cancelDiscovery();
                paired = true;
                connectDevice(devices[which].toString(), paired);
                Log.d(TAG,devices[which].toString());

            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public void discoverOn(){                                              //Method for setting bluetooth on discoverable mode
        //for 5 minutes
        Intent discoverableIntent =
                new Intent(mBluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(mBluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, 1);
    }

    // 페어링되지 않은 기기 검색
    protected void selectUnpairedDevice() {

        discoverOn();
        // 이미 검색 중이라면 검색을 종료하고, 다시 검색 시작
        if(mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();


        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //연결 확인
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //연결 끊김 확인
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);    //기기 검색됨
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);   //기기 검색 시작
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //기기 검색 종료
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);

        registerReceiver(mReceiver, filter);



        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this); // 다이얼로그 생성
        builder.setTitle("페어링할 기기 탐색");

        adapter = new ArrayAdapter<>(BluetoothActivity.this,
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

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,action + "브로드캐스트 리시버 동작");
            // 블루투스 연결 기기들을 탐색할때 브로드 캐스트가 마구 날라오는게 그거를 잡는거임
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {

                Log.d("브로드캐스트 리시버에서 탐색된 것들 : ",action);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // 탐색된 장치가 연결이 안되어있다면 ( bond_bonded 가 연되었는지 확인하는거 ) device에 넣음
                if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if(device != null) {
                        add(device);
                    }
                }


            }
        }
    };



    private void add(BluetoothDevice device) {
        Log.d("지금",device.getName()+"탐색됨");
        if(!(pairedDevices.contains(device))) {
            if(unpairedDevices.add(device)) {
                unpairedList.add(device.getName());
            }
        }
        adapter.notifyDataSetChanged();
        Log.d("지금",device.getName()+"탐색됨");
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

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
            throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[] { int.class });
                return (BluetoothSocket) m.invoke(device, 1); }
            catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e); }
        }
        return device.createInsecureRfcommSocketToServiceRecord(BT_MODULE_UUID);
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
                        startToast("블루투스 연결 성공! 사용자 정보를 빨래통 전달해주세요");

                        // 데이터 수신 함수 호출
                        Log.d(TAG,"입출력 스트림 객체 생성 완료");

                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                } else { // 연결 오류나면
                    startToast("연결오류");
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
                    Log.d(TAG,mbluetoothDevice.getName());
                }
                else {    // 페어링되지 않은 기기라면
                    Log.d(TAG, "페어링 아님?");
                    mbluetoothDevice = getUnpairedDevice(selectedDeviceName);
                }

                BluetoothDevice device = mbluetoothDevice;
                /*
                try {

                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(BT_MODULE_UUID);
                    Log.d(TAG,bluetoothSocket.toString());

                    bluetoothSocket.connect(); 	  // 소켓 연결
                    Log.d(TAG,"소켓 생성 완료");
                    mHandler.sendEmptyMessage(1); // 핸들러에 메시지 1 보내기



                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"소켓연결 실패");
                    mHandler.sendEmptyMessage(-1); // 핸들러에 메시지 -1 보내기
                }

                 */
                try {
                    bluetoothSocket = createBluetoothSocket(device);
                } catch (Exception e) {Log.e("","Error creating socket");}

                try {
                    bluetoothSocket.connect();
                    Log.e("","Connected");
                    mHandler.sendEmptyMessage(1);
                } catch (IOException e) {
                    Log.e("",e.getMessage());
                    try {
                        Log.e("","trying fallback...");

                        bluetoothSocket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                        bluetoothSocket.connect();

                        Log.e("","Connected");
                        mHandler.sendEmptyMessage(1);
                    }

                    catch (Exception e2) {
                        Log.e("", "Couldn't establish Bluetooth connection!");
                        mHandler.sendEmptyMessage(-1);
                    }
                }
            }
        });
        thread.start(); // 별도 스레드 시작
    }

    protected void sendUid(boolean isSuccess){
        userUid = user.getUid().getBytes();
        Log.d(TAG,userUid.toString());
        try{
            outputStream.write(userUid);
            isSuccess = true;

            Log.d(TAG,"사용자 유아이디 전송 완료");
        }
        catch(Exception e){
            e.printStackTrace();
            startToast("데이터 전송 오류");
            isSuccess = false;
        }
    }

    public void startToast( String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }
}
