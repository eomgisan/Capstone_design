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

public class Weather3 {

    private String[] POP = new String[3];

    private String[] PTY = new String[3];

    private String[] REH = new String[3];

    private String[] SKY = new String[3];

    private String[] TMP = new String[3];

    private String[] WSD = new String[3];

    public boolean isnull = true;


    public Weather3() {

    }

    public String getPOP(int i) {
        return POP[i];
    }

    public void setPOP(String POP,int i) {
        this.POP[i] = POP;
    }

    public String getPTY(int i) {
        return PTY[i];
    }

    public void setPTY(String PTY, int i) {
        this.PTY[i] = PTY;
    }

    public String getREH(int i) {
        return REH[i];
    }

    public void setREH(String REH , int i) {
        this.REH[i] = REH;
    }

    public String getSKY(int i) {
        return SKY[i];
    }

    public void setSKY(String SKY, int i) {
        this.SKY[i] = SKY;
    }

    public String getTMP(int i) {
        return TMP[i];
    }

    public void setTMP(String TMP, int i) {
        this.TMP[i] = TMP;
    }


    public String getWSD(int i) {
        return WSD[i];
    }

    public void setWSD(String WSD, int i) {
        this.WSD[i] = WSD;
    }

    public void lookUpWeather(int locationCode, int date, int Time) throws IOException, JSONException {

        String nx = "0";	//위도
        String ny = "0";	//경도
        String baseDate = "YYYYMMDD";	//조회하고싶은 날짜
        String baseTime = "HHMM";	//조회하고싶은 시간
        String type = "json";	//조회하고 싶은 type(json, xml 중 고름)

        switch (locationCode) {
            case 0 :
                nx = "60";   ny = "127"; // 서울
            case 1:
                nx = "55";   ny = "124"; // 인천
            case 2:
                nx = "60";   ny = "120"; // 경기
            case 3:
                nx = "73";   ny = "134"; // 강원
            case 4:
                nx = "69";   ny = "107"; // 충청북도
            case 5:
                nx = "68";   ny = "100"; // 충청남도
            case 6:
                nx = "63";   ny = "89"; // 전라북도
            case 7:
                nx = "51";   ny = "67"; // 전라남도
            case 8:
                nx = "89";   ny = "91"; // 경상북도
            case 9:
                nx = "91";   ny = "77"; // 경상남도
            case 10:
                nx = "52";   ny = "38"; // 제주도
        }

        if(Time <600){
            baseDate = String.valueOf(date-1);
            baseTime = "2300";

        }
        else if(Time <2400){
            baseDate = String.valueOf(date);
            baseTime = "0500";

        }



//		참고문서에 있는 url주소
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
//      홈페이지에서 받은 키
        String serviceKey = "DiRXnVHX3mXXqyA42gKEW2PgQX2EjVHi%2BT0OxfYZExhbkSAqT6iI4MoMlWVfLcK7d9rVQAbyT0wCnt%2BRPaWAQA%3D%3D";


        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /* 확인할 아이템 수 */
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /* 확인할 아이템 수 */
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));	/* 타입 */
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /* 조회하고싶은 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); //경도
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); //위도

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

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
        Log.d("zzzzzzzzzz",result);
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
        JSONArray jsonArray = jsonObj_4.getJSONArray("item");



        for(int i=0;i<jsonArray.length();i++){
            jsonObj_4 = jsonArray.getJSONObject(i);
            String fcstDate = jsonObj_4.getString("fcstDate");
            String fcstTime = jsonObj_4.getString("fcstTime");
            String fcstValue = jsonObj_4.getString("fcstValue");
            String category = jsonObj_4.getString("category");

            /* 기준날 탐색 06시 기준 예보 탐색 */
            if(fcstDate.equals(String.valueOf(date)) && fcstTime.equals("0600")){

                if(category.equals("POP")){
                    // 강수확률 %값
                    this.setPOP(fcstValue,0);
                    Log.d("zzzzz","weather3 POP :" + this.POP[0]);
                }
                else if(category.equals("PTY")){
                    // 강수형태 코드값 ( 0 = 없음, 1 = 비, 2 = 비/눈, 3 = 눈 4 = 소나기 )
                    this.setPTY(fcstValue,0);
                    Log.d("zzzzz","weather3 PTY " + i + " : " + fcstValue);
                }
                else if(category.equals("REH")){
                    // 습도 %값
                    this.setREH(fcstValue,0);
                    Log.d("zzzzz","weather3 REH " + i + " : " + fcstValue);
                }
                else if(category.equals("SKY")){
                    // 날씨 코드값   ( 1 = 맑음, 3 = 구름많음, 4 = 흐림 )
                    this.setSKY(fcstValue,0);
                    Log.d("zzzzz","weather3 SKY " + i + " : " + fcstValue);
                }
                else if(category.equals("TMP")){
                    // 일 최저기온 C값
                    this.setTMP(fcstValue,0);
                    Log.d("zzzzz","weather3 TMP " + i + " : " + fcstValue);
                }

                else if(category.equals("WSD")){
                    // 풍속 m/s값 ( 4미만 = 약한바람, 4~9 = 나뭇잎 흔들림, 9~14 = 나뭇가지 흔들림, 14~ = 바람 매우 강함 )
                    this.setWSD(fcstValue,0);
                    Log.d("zzzzz","weather3 WSD " + i + " : " + fcstValue);
                }
            }

            /* 다음날 탐색 06시 기준 예보 탐색 */
            else if(fcstDate.equals(String.valueOf(date+1)) && fcstTime.equals("0600")){

                if(category.equals("POP")){
                    // 강수확률 %값
                    this.setPOP(fcstValue,1);
                }
                else if(category.equals("PTY")){
                    // 강수형태 코드값 ( 0 = 없음, 1 = 비, 2 = 비/눈, 3 = 눈 4 = 소나기 )
                    this.setPTY(fcstValue,1);
                }
                else if(category.equals("REH")){
                    // 습도 %값
                    this.setREH(fcstValue,1);
                }
                else if(category.equals("SKY")){
                    // 날씨 코드값   ( 1 = 맑음, 3 = 구름많음, 4 = 흐림 )
                    this.setSKY(fcstValue,1);
                }
                else if(category.equals("TMP")){
                    // 일 최저기온 C값
                    this.setTMP(fcstValue,1);
                }

                else if(category.equals("WSD")){
                    // 풍속 m/s값 ( 4미만 = 약한바람, 4~9 = 나뭇잎 흔들림, 9~14 = 나뭇가지 흔들림, 14~ = 바람 매우 강함 )
                    this.setWSD(fcstValue,1);
                }
            }

            /* 다다음날 탐색 06시 기준 예보 탐색 */
            else if(fcstDate.equals(String.valueOf(date+2)) && fcstTime.equals("0600")){

                if(category.equals("POP")){
                    // 강수확률 %값
                    this.setPOP(fcstValue,2);
                }
                else if(category.equals("PTY")){
                    // 강수형태 코드값 ( 0 = 없음, 1 = 비, 2 = 비/눈, 3 = 눈 4 = 소나기 )
                    this.setPTY(fcstValue,2);
                }
                else if(category.equals("REH")){
                    // 습도 %값
                    this.setREH(fcstValue,2);
                }
                else if(category.equals("SKY")){
                    // 날씨 코드값   ( 1 = 맑음, 3 = 구름많음, 4 = 흐림 )
                    this.setSKY(fcstValue,2);
                }
                else if(category.equals("TMP")){
                    // 일 최저기온 C값
                    this.setTMP(fcstValue,2);
                }

                else if(category.equals("WSD")){
                    // 풍속 m/s값 ( 4미만 = 약한바람, 4~9 = 나뭇잎 흔들림, 9~14 = 나뭇가지 흔들림, 14~ = 바람 매우 강함 )
                    this.setWSD(fcstValue,2);
                }
            }


        }
        Log.d("ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ","weather3 끝");
        isnull = false;
        Log.d("zzzzzzzzzzzz",this.getPOP(0));

    }

}


