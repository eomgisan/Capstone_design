package com.example.ex1;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;


public class BluetoothFragment extends Fragment {
    String TAG = "BluetoothFragment";

    Button ButtonPair;
    Button ButtonSearch;
    Button ButtonSearch2;
    Switch SwitchBluetooth;
    Button goToHome;
    TextView successText;
    TextView Status;
    MainActivity activity;

    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    BluetoothSocket btSocket;
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
        activity.unregisterReceiver(mReceiver);
        activity = null;
    }





    private ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == activity.RESULT_OK) {
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
        if (activity.mBluetoothAdapter == null) {
            Log.d(TAG,"BluetoothOn 함수 동작 - 블루투스어댑터 존재 X");
            activity.startToast("블루투스 동작 불가능한 기기입니다.");
            status.setText("블루투스 동작 불가능한 기기입니다.");


        } else if (activity.mBluetoothAdapter.isEnabled()) {
            Log.d(TAG,"BluetoothOn 함수 동작 - 블루투스어댑터 존재 ok, 활성화 이미 완료");
            activity.startToast("이미 블루투스가 활성화 되어있습니다.");
            // 활성화가 이미 되어있는것으로 페어링 단계를 들어가야함
            status.setText("블루투스 권한 허용");


        } else {
            // 활성화 요청을 위해 ACTION_REQUEST_ENABLE 인텐트 객체 생성
            // 이는 시스템 액티비티를 화면을 띄워서 블루투스를 직접 할수 있다.
            Log.d(TAG,"BluetoothOn 함수 동작 - 블루투스어댑터 존재 ok, 활성화 시작");

            Intent intentBluetoothEnable = new Intent(activity.mBluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityResult.launch(intentBluetoothEnable);

        }
    }

    //블루투스 리스트 출력 함수
    private void selectDevice(){

        // 페어링 된 객체 집합 불러오기
        activity.pairedDevices = activity.mBluetoothAdapter.getBondedDevices();

        Log.d(TAG,"이미 페어링된 객체들" + activity.pairedDevices.toString());

        // 안드로이드에 선언되어있는 alertdialog 사용, 목록 창 띄우는거임
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("기존 페어링 장치 중 선택");

        // 리스트 배열 만들기
        java.util.List<String> pairedList = new ArrayList<>();
        for(BluetoothDevice device : activity.pairedDevices) {
            pairedList.add(device.getName());
        }



        final CharSequence[] devices = pairedList.toArray(new CharSequence[pairedList.size()]);

        // 빌더에 블루투스 연결할 장치들의 이름을 설정하고 onclicklistener 설정

        builder.setItems(devices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    activity.mBluetoothAdapter.cancelDiscovery();
                    activity.paired = true;
                    connectDevice(devices[which].toString(), activity.paired);
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
                new Intent(activity.mBluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(activity.mBluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, 1);
    }

    // 페어링되지 않은 기기 검색
    protected void selectUnpairedDevice() {

        discoverOn();
        // 이미 검색 중이라면 검색을 종료하고, 다시 검색 시작
        if(activity.mBluetoothAdapter.isDiscovering()) {
            activity.mBluetoothAdapter.cancelDiscovery();
        }
        activity.mBluetoothAdapter.startDiscovery();


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

        activity.registerReceiver(mReceiver, filter);



        AlertDialog.Builder builder = new AlertDialog.Builder(activity); // 다이얼로그 생성
        builder.setTitle("페어링할 기기 탐색");

        activity.adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_list_item_1, activity.unpairedList);

        builder.setAdapter(activity.adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.mBluetoothAdapter.cancelDiscovery();
                String name = activity.adapter.getItem(which);
                activity.unpairedList.remove(name);
                activity.paired = false;
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
        if(!(activity.pairedDevices.contains(device))) {
            if(activity.unpairedDevices.add(device)) {
                activity.unpairedList.add(device.getName());
            }
        }
        activity.adapter.notifyDataSetChanged();
        Log.d("지금",device.getName()+"탐색됨");
    }







    // 블루투스 페어링된 목록에서 디바이스 기기 가져오기
    protected BluetoothDevice getPairedDevice(String name) {
        BluetoothDevice selectedDevice = null;

        for(BluetoothDevice device : activity.pairedDevices) {
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

        for(BluetoothDevice device : activity.unpairedDevices) {
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
                final Method m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
                return (BluetoothSocket) m.invoke(device, 1); }
            catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e); }
        }
        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }




    private void connectDevice(String selectedDeviceName, boolean paired) {

        final Handler mHandler = new Handler() {	// 핸들러 객체 생성
            public void handleMessage(Message msg) {	// handleMessage() 메서드 재정의
                Log.d(TAG,"핸들러 생성");
                if(msg.what==1) {			// 받은 메시지가 1이라면
                    try{
                        Log.d(TAG,"인풋 아웃풋 스트림 생성");
                        // 입출력 스트림 객체 생성
                        activity.outputStream = activity.bluetoothSocket.getOutputStream();
                        activity.inputStream = activity.bluetoothSocket.getInputStream();
                        activity.startToast("블루투스 연결 성공! 사용자 정보를 빨래통 전달해주세요");

                        // 데이터 수신 함수 호출
                        Log.d(TAG,"입출력 스트림 객체 생성 완료");

                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                } else { // 연결 오류나면
                    activity.startToast("연결오류");
                    try {
                        activity.bluetoothSocket.close(); // 소켓 닫아주고 리소스 해제
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
                    activity.mbluetoothDevice = getPairedDevice(selectedDeviceName);
                    Log.d(TAG,activity.mbluetoothDevice.getName());
                }
                else {    // 페어링되지 않은 기기라면
                    Log.d(TAG, "페어링 아님?");
                    activity.mbluetoothDevice = getUnpairedDevice(selectedDeviceName);
                }
                // UUID 생성
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                BluetoothDevice device = activity.mbluetoothDevice;

                try {

                    activity.bluetoothSocket = createBluetoothSocket(activity.mbluetoothDevice);
                    activity.bluetoothSocket.connect(); 	  // 소켓 연결
                    Log.d(TAG,"소켓 생성 완료");
                    mHandler.sendEmptyMessage(1); // 핸들러에 메시지 1 보내기



                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"소켓연결 실패");
                    mHandler.sendEmptyMessage(-1); // 핸들러에 메시지 -1 보내기
                }
            }
        });
        thread.start(); // 별도 스레드 시작
    }

    protected void sendUid(boolean isSuccess){
        activity.userUid = activity.user.getUid().getBytes();
        Log.d(TAG,activity.userUid.toString());
        try{
            activity.outputStream.write(activity.userUid);
            isSuccess = true;

            Log.d(TAG,"사용자 유아이디 전송 완료");
        }
        catch(Exception e){
            e.printStackTrace();
            activity.startToast("데이터 전송 오류");
            isSuccess = false;
        }
    }







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_bluetooth,container,false);

        SwitchBluetooth = rootview.findViewById(R.id.bluetoothSwitch);
        ButtonSearch = rootview.findViewById(R.id.searchButton);
        ButtonSearch2 = rootview.findViewById(R.id.searchButton2);
        ButtonPair = rootview.findViewById(R.id.pairbutton);
        goToHome = rootview.findViewById(R.id.goToHomeButton);
        Status = rootview.findViewById(R.id.statusText);
        successText = rootview.findViewById(R.id.successText);


        boolean isSuccess = false;

        if(activity.mBluetoothAdapter.isEnabled()){
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
                    activity.mBluetoothAdapter.disable();
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
                    activity.startToast("위의 스위치를 눌러 블루투스를 먼저 활성화 시켜주세요!");
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
                    activity.startToast("위의 스위치를 눌러 블루투스를 먼저 활성화 시켜주세요!");
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
                    activity.startToast("블루투스 연결을 다시 시도해주세요");
                }
            }
        });

        goToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 정상적으로 동작했으면 홈 프레그먼트로 넘어간다
                getActivity().getSupportFragmentManager()
                        .popBackStack("bluetooth", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_ly, new HomeFragment(),"home")
                        .setReorderingAllowed(true)
                        .addToBackStack("homeFM")
                        .commit();
            }
        });


        return rootview;
    }
}

