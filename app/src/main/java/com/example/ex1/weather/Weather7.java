package com.example.ex1.weather;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Weather7 {
    private String[] TEMMAX = new String[8];
    private String[] TEMMIN = new String[8];
    private String[] RNAM = new String[8];
    private String[] RNPM = new String[8];
    private String[] WFAM = new String[8];
    private String[] WFPM = new String[8];

    public boolean isnull = true;

    public Weather7() {
    }

    public String getTEMMAX(int i) {
        return TEMMAX[i];
    }

    public void setTEMMAX(String TEMMAX, int i) {
        this.TEMMAX[i] = TEMMAX;
    }

    public String getTEMMIN(int i) {
        return TEMMIN[i];
    }

    public void setTEMMIN(String TEMMIN, int i) {
        this.TEMMIN[i] = TEMMIN;
    }

    public String getRNAM(int i) {
        return RNAM[i];
    }

    public void setRNAM(String RNAM, int i) {
        this.RNAM[i] = RNAM;
    }

    public String getRNPM(int i) {
        return RNPM[i];
    }

    public void setRNPM(String RNPM, int i) {
        this.RNPM[i] = RNPM;
    }


    public String getWFAM(int i) {
        return WFAM[i];
    }

    public void setWFAM(String WFAM, int i) {
        this.WFAM[i] = WFAM;
    }

    public String getWFPM(int i) {
        return WFPM[i];
    }

    public void setWFPM(String WFPM, int i) {
        this.WFPM[i] = WFPM;
    }



    private void lookUpWeather_tmp(int LocationCode, String date) throws IOException, JSONException{

        String regid = "";
        String type = "json";	//조회하고 싶은 type(json, xml 중 고름)

        switch (LocationCode) {
            case 0 :
                regid = "11B10101"; // 서울
            case 1:
                regid = "11B20201"; // 인천
            case 2:
                regid = "11B20601"; // 경기
            case 3:
                regid = "11D20501"; // 강원
            case 4:
                regid = "11C10301"; // 충청북도
            case 5:
                regid = "11C20401"; // 충청남도
            case 6:
                regid = "11F10201"; // 전라북도
            case 7:
                regid = "11F20401"; // 전라남도
            case 8:
                regid = "11H10701"; // 경상북도
            case 9:
                regid = "11H20201"; // 경상남도
            case 10:
                regid = "11G00201"; // 제주도
        }


        // 참고문서에 있는 url주소
        String apiUrl = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";
        // 홈페이지에서 받은 키
        String serviceKey = "DiRXnVHX3mXXqyA42gKEW2PgQX2EjVHi%2BT0OxfYZExhbkSAqT6iI4MoMlWVfLcK7d9rVQAbyT0wCnt%2BRPaWAQA%3D%3D";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /* 확인할 페이지 */
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /* 확인할 아이템 수 */
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));	/* 타입 */
        urlBuilder.append("&" + URLEncoder.encode("regId","UTF-8") + "=" + URLEncoder.encode(regid, "UTF-8")); /* 지역코드 */
        urlBuilder.append("&" + URLEncoder.encode("tmFc","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); /* 조회하고싶은 날짜*/

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();
        String result= sb.toString();

        //=======이 밑에 부터는 json에서 데이터 파싱해 오는 부분이다=====//

        // response 키를 가지고 데이터를 파싱
        JSONObject jsonObj_1 = new JSONObject(result);
        String response = jsonObj_1.getString("response");

        // response 로 부터 body 찾기
        JSONObject jsonObj_2 = new JSONObject(response);
        String body = jsonObj_2.getString("body");

        // body 로 부터 items 찾기
        JSONObject jsonObj_3 = new JSONObject(body);
        String items = jsonObj_3.getString("items");


        // items로 부터 itemlist 를 받기
        JSONObject jsonObj_4 = new JSONObject(items);
        String item = jsonObj_4.getString("item");

        JSONArray jsonArray = jsonObj_4.getJSONArray("item");
        jsonObj_4 = jsonArray.getJSONObject(0);
        for(int i=0; i<8; i++){
            String taMin = jsonObj_4.getString("taMin" + String.valueOf(i+3));
            String taMax = jsonObj_4.getString("taMax" + String.valueOf(i+3));
            this.setTEMMIN(taMin, i);
            this.setTEMMAX(taMax, i);
        }
    }

    private void lookUpWeather_sky(int LocationCode, String date) throws IOException, JSONException{

        String regid = "";
        String type = "json";	//조회하고 싶은 type(json, xml 중 고름)

        switch (LocationCode) {
            case 0 :
                regid = "11B00000"; // 서울
            case 1:
                regid = "11B00000"; // 인천
            case 2:
                regid = "11B00000"; // 경기
            case 3:
                regid = "11D10000"; // 강원
            case 4:
                regid = "11C10000"; // 충청북도
            case 5:
                regid = "11C20000"; // 충청남도
            case 6:
                regid = "11F10000"; // 전라북도
            case 7:
                regid = "11F20000"; // 전라남도
            case 8:
                regid = "11H10000"; // 경상북도
            case 9:
                regid = "11H20000"; // 경상남도
            case 10:
                regid = "11G00000"; // 제주도
        }


        // 참고문서에 있는 url주소
        String apiUrl = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst";
        // 홈페이지에서 받은 키
        String serviceKey = "DiRXnVHX3mXXqyA42gKEW2PgQX2EjVHi%2BT0OxfYZExhbkSAqT6iI4MoMlWVfLcK7d9rVQAbyT0wCnt%2BRPaWAQA%3D%3D";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /* 확인할 페이지 */
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /* 확인할 아이템 수 */
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));	/* 타입 */
        urlBuilder.append("&" + URLEncoder.encode("regId","UTF-8") + "=" + URLEncoder.encode(regid, "UTF-8")); /* 지역코드 */
        urlBuilder.append("&" + URLEncoder.encode("tmFc","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); /* 조회하고싶은 날짜*/

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();
        String result= sb.toString();

        //=======이 밑에 부터는 json에서 데이터 파싱해 오는 부분이다=====//

        // response 키를 가지고 데이터를 파싱
        JSONObject jsonObj_1 = new JSONObject(result);
        String response = jsonObj_1.getString("response");

        // response 로 부터 body 찾기
        JSONObject jsonObj_2 = new JSONObject(response);
        String body = jsonObj_2.getString("body");

        // body 로 부터 items 찾기
        JSONObject jsonObj_3 = new JSONObject(body);
        String items = jsonObj_3.getString("items");


        // items로 부터 itemlist 를 받기
        JSONObject jsonObj_4 = new JSONObject(items);
        String item = jsonObj_4.getString("item");


        JSONArray jsonArray = jsonObj_4.getJSONArray("item");
        JSONObject jsonObj = jsonArray.getJSONObject(0);


        for(int i=0; i<8; i++){
            if(i<5){
                String rnStAM = jsonObj.getString("rnSt" + String.valueOf(i+3) + "Am");
                String rnStPM = jsonObj.getString("rnSt" + String.valueOf(i+3) + "Pm");
                String wfAM = jsonObj.getString("wf" + String.valueOf(i+3) + "Am");
                String wfPM = jsonObj.getString("wf" + String.valueOf(i+3) + "Pm");
                this.setRNAM(rnStAM, i);
                this.setRNPM(rnStPM, i);
                this.setWFAM(wfAM, i);
                this.setWFPM(wfPM, i);
            }
            else{
                String rnSt = jsonObj.getString("rnSt" + String.valueOf(i+3));
                String wf = jsonObj.getString("wf" + String.valueOf(i+3));
                this.setRNAM(rnSt, i);
                this.setRNPM(rnSt, i);
                this.setWFAM(wf, i);
                this.setWFPM(wf, i);
            }
        }
    }

    public void lookUpWeather(int LocationCode, int Date, int Time) throws IOException, JSONException {

        Weather7[] weatherResult = new Weather7[] { new Weather7(), new Weather7(), new Weather7(),
                new Weather7(), new Weather7(), new Weather7(), new Weather7(), new Weather7()
        };


        String date = "";

        if(Time <600){
            date = String.valueOf(Date-1) + "1800";
        }
        else if(Time <2400){
            date = String.valueOf(Date) + "0600";
        }

        lookUpWeather_tmp(LocationCode, date);
        lookUpWeather_sky(LocationCode, date);
        Log.d("ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ","weather7 끝");
        isnull = false;
    }

}
