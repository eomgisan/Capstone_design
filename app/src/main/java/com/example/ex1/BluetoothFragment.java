package com.example.ex1;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class BluetoothFragment extends Fragment {


    Button bluetoothBT;

    MainActivity activity;
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
        activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_bluetooth,container,false);
        bluetoothBT = rootview.findViewById(R.id.bluetoothButton);
        bluetoothBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 블루투스 동작


                // 블루투스 정상적으로 동작했는지 확인하기
                if(true){

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
                else{
                    activity.startToast("블루투스 연결에 실패하였습니다 다시 시도해주세요");
                }
            }
        });

        return rootview;
    }
}

