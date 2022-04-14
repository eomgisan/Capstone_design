package com.example.ex1;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ex1.dataStructure.UserFeature;
import com.example.ex1.weather.Weather3;
import com.example.ex1.weather.Weather7;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FeatureFragment extends Fragment {

    MainActivity activity;
    String TAG = "FeatureFragment";

    UserFeature userFeature;

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
        View rootview = inflater.inflate(R.layout.fragment_feature, container, false);

        userFeature = activity.userFeature;

        if(userFeature.isnull){

        }
        else{

        }

        return rootview;
    }
}