package com.example.ex1;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import retrofit2.http.Url;


public class CommunityFragment extends Fragment {


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

        String url = "http://192.168.62.249:8090/pro30/member/login.do" + "?uid=" + activity.user.getUid();
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_community, container, false);

        activity.webView = rootview.findViewById(R.id.webView);
        activity.webView.setWebViewClient(new WebViewClient());  // 새 창 띄우기 않기
        activity.webView.setWebChromeClient(new WebChromeClient());


        activity.webView.getSettings().setLoadWithOverviewMode(true);  // WebView 화면크기에 맞추도록 설정 - setUseWideViewPort 와 같이 써야함
        activity.webView.getSettings().setUseWideViewPort(true);  // wide viewport 설정 - setLoadWithOverviewMode 와 같이 써야함

        activity.webView.getSettings().setSupportZoom(false);  // 줌 설정 여부
        activity.webView.getSettings().setBuiltInZoomControls(false);  // 줌 확대/축소 버튼 여부

        activity.webView.getSettings().setJavaScriptEnabled(true); // 자바스크립트 사용여부
        activity.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // javascript가 window.open()을 사용할 수 있도록 설정
        activity.webView.getSettings().setSupportMultipleWindows(true); // 멀티 윈도우 사
        activity.webView.getSettings().setDomStorageEnabled(true);  // 로컬 스토리지 (localStorage) 사용여부용 여부


        activity.webView.loadUrl(url);

        return rootview;
    }
}