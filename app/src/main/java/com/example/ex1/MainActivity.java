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
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.example.ex1.dataStructure.Datas;
import com.example.ex1.dataStructure.UserFeature;
import com.example.ex1.dataStructure.UserInfo;
import com.example.ex1.network.NetworkManager;
import com.example.ex1.weather.Weather3;
import com.example.ex1.weather.Weather7;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import org.json.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainAcitivity";

    // 바텀네비게이션바
    BottomNavigationView bottomNavigationView;




    // 프레그먼트
    Fragment homeFM = new HomeFragment();
    Fragment settingFM = new SettingFragement();
    Fragment recommandFM = new RecommandFragment();
    Fragment featureFM = new FeatureFragment();
    Fragment communityFM = new CommunityFragment();
    Fragment bluetoothFM = new BluetoothFragment();

    // 블루투스


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

    FirebaseFirestore db;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    double vol1;
    double vol2;

    Datas datas = new Datas();
    UserInfo userInfo = new UserInfo();
    UserFeature userFeature = new UserFeature();


    Weather3 weather3 = new Weather3();
    Weather7 weather7 = new Weather7();
    HashSet<CalendarDay> RecommandDates1;
    HashSet<CalendarDay> RecommandDates2;
    boolean apiFinish = false;

    WebView webView;




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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.disable();


        //api로 날씨 데이터 가져오기
        new Thread(){
            public void run(){
                try{
                    long mNow;
                    Date mDate;
                    Log.d("zzzzzzzzzzzzz","zzzzzzzzzzz");
                    int date;
                    int Time;

                    SimpleDateFormat mFormat1 = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat mFormat2 = new SimpleDateFormat("hhmm");
                    mNow = System.currentTimeMillis();
                    mDate = new Date(mNow);

                    date = Integer.valueOf(mFormat1.format(mDate)).intValue();
                    Time = Integer.valueOf(mFormat2.format(mDate)).intValue();
                    Log.d("zzzzzzzzzzzzz","zzzzzzzzzzz");
                    weather3.lookUpWeather(0,date, Time);
                    weather7.lookUpWeather(0,date, Time);

                    // 여기에 그거 조정해서 그거 그거하기
                    // 추가 하는 방법 = RecommandDates.add(CalendarDay.from(year,month,day));

                    while(true){
                        if(weather3.isnull || weather7.isnull || datas.isnull){

                        }
                        else{
                            RecommandDates1 = weather(datas.getWeight1(),userFeature.getIdeal_w1(),userFeature.getAver_inc1());
                            RecommandDates2 = weather(datas.getWeight2(),userFeature.getIdeal_w2(),userFeature.getAver_inc2());
                            apiFinish = true;
                            Log.d("zzzzzzzzzzzzzzzzzzzzz","빨래날짜 추천 계산 완료");
                            break;
                        }
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }.start();


        startFirebase();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
    }

    public HashSet<CalendarDay> weather(double weight, double ideal_w, double averInc){

        HashSet<CalendarDay> recommand = new HashSet<CalendarDay>();

        CalendarDay result = CalendarDay.today();


        if(datas.getSmell() >10 || weight > 10){
            // 오늘 추천후 알아서 빨래하쎔 ㄱㄱ
            recommand.add(result);

            return recommand;
        }
        else{

            double dayafter = (ideal_w - weight) / averInc;

            double[] score = new double[11];


            int i = 0;


            for(i=0;i<11;i++){
                if(i<dayafter){
                    score[i] = (100/dayafter)*(i+1);
                }
                else{
                    score[i] = 80+(100/i-11);
                }

            }

            for(i=0;i<3;i++) {
                Log.d("zzz",i+"" + weather3.getPOP(i));
                score[i] -= Double.parseDouble(weather3.getPOP(i));

                if(weather3.getPTY(i) != "0"){
                    score[i] += 50;
                }

                if(weather3.getPTY(i) != "1"){
                    score[i] -= 30;
                }

                if(weather3.getPTY(i) != "2"){
                    score[i] -= 40;
                }

                if(weather3.getPTY(i) != "3"){
                    score[i] -= 50;
                }
                if(weather3.getPTY(i) != "4"){
                    score[i] -= 20;
                }
                score[i] += Double.parseDouble(weather3.getTMP(i));

            }
            for(i=3;i<11;i++){
                score[i] -= Double.parseDouble(weather7.getRNAM(i-3))/2;
                score[i] -= Double.parseDouble(weather7.getRNPM(i-3))/2;

                if(weather7.getWFAM(i-3) == "맑음" || weather7.getWFPM(i-3) == "맑음"){
                    score[i] += 25;

                }
            }

            int max = 0;

            for(i=1;i<11;i++){
                if(score[max]<score[i]){
                    max = i;
                }

            }
            for(i=0;i<11;i++){
                Log.d(TAG + "zzzzzzzzzzzzzzzzzzzzzzzz",score[i]+"");

            }

            result = CalendarDay.from(result.getYear(),result.getMonth(),result.getDay()+max);

            Log.d("지금", result.toString());

            recommand.add(result);

            return recommand;
        }
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

                    getSupportFragmentManager().popBackStack("community", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_ly, communityFM,"community")
                            .setReorderingAllowed(true)
                            .addToBackStack("communityFM")
                            .commitAllowingStateLoss();
                    Log.d(TAG,communityFM.getTag());

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
        else if (tag.equals("community")){
            bottomNavigationView.getMenu().findItem(R.id.menu_community).setChecked(true);
        }
        else if (tag.equals("feature")){
            bottomNavigationView.getMenu().findItem(R.id.menu_feature).setChecked(true);
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
            db = FirebaseFirestore.getInstance();


            // 센서데이터 가져오기
            DocumentReference docRef = db.collection("datas").document(user.getUid());

            // document에서 불러오는 위 쿠드가 수행 완료시 동작
            docRef.
                    get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                Double hum = Double.valueOf(document.getData().get("hum").toString());
                                Double smell = Double.valueOf(document.getData().get("smell").toString());
                                Double temp = Double.valueOf(document.getData().get("temp").toString());
                                Double weight1 = Double.valueOf(document.getData().get("weight1").toString());
                                Double weight2 = Double.valueOf(document.getData().get("weight2").toString());

                                datas = new Datas(hum, smell, temp, weight1, weight2);
                            }
                            else{
                                Log.d(TAG, "아직 센서값이 없습니다.", task.getException());
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed sensorData with1 ", task.getException());
                    }
                }
            });



            // 세팅값 가져오기


            docRef.collection("userinfo").document("userInfo")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                // DocumentSnapshot 자료형인 task에 결과를 저장
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // 정상적으로 동작 했을 경우 document로 복사
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            String address = document.getData().get("location").toString();
                            String detergentType = document.getData().get("detegentType").toString();
                            int laundryVol = Integer.valueOf(document.getData().get("laundryVol").toString());
                            String name = document.getData().get("name").toString();
                            String phoneNum = document.getData().get("phoneNum").toString();
                            String userId = document.getData().get("userId").toString();

                            userInfo = new UserInfo(address,detergentType,laundryVol,name,phoneNum,userId);
                        }
                        else{
                            Log.d(TAG, "아직 셋팅값이 없습니다.", task.getException());
                        }



                    } else {
                        Log.d(TAG, "get failed userSetting with2 ", task.getException());
                    }
                }
            });
            docRef.collection("userinfo").document("userFeature")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                // DocumentSnapshot 자료형인 task에 결과를 저장
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // 정상적으로 동작 했을 경우 document로 복사
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()){
                            Double priod1 = Double.valueOf(document.getData().get("averDay1").toString());
                            Double priod2 = Double.valueOf(document.getData().get("averDay2").toString());
                            Double averinc1 = Double.valueOf(document.getData().get("averIncWeight1").toString());
                            Double averinc2 = Double.valueOf(document.getData().get("averIncWeight2").toString());
                            Double ideal_w1 = Double.valueOf(document.getData().get("ideal_w1").toString());
                            Double ideal_w2 = Double.valueOf(document.getData().get("ideal_w2").toString());
                            String recommand1 = document.getData().get("recommand1").toString();
                            String recommand2 = document.getData().get("recommand2").toString();

                            userFeature = new UserFeature(averinc1,averinc2,priod1,priod2,ideal_w1,ideal_w2,recommand1,recommand2);
                        }
                        else{
                            Log.d(TAG, "아직 사용자 주기 파악이 안되었습니다.");

                        }

                    } else {
                        Log.d(TAG, "get failed userFeature with3 ", task.getException());
                    }
                }
            });
        }
    }









    // 뒤로가기시 앱 종료 함수

    long pressedTime = 0; //'뒤로가기' 버튼 클릭했을 때의 시간
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        }
        else{
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
    }








    // 토스트 창을 띄우기 위한 함수
    public void startToast( String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }



}