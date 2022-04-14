package com.example.ex1.dataStructure;

// 회원정보 데이터를 위한 객채 선언 파일
public class UserInfo {

    private String name;
    private String phoneNum;
    private String location;
    private String detegentType;
    private int laundryVol;
    private String userId;

    public boolean isnull;

    public UserInfo() {
        isnull = true;
    }

    public UserInfo(String location, String detegentType, int laundryVol, String name, String phoneNum, String userId) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.location = location;
        this.detegentType = detegentType;
        this.laundryVol = laundryVol;
        this.userId = userId;
        isnull = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetegentType() {
        return this.detegentType;
    }

    public void setDetegentType(String detergentType) {
        this.detegentType = detergentType;
    }

    public int getLaundryVol() {
        return this.laundryVol;
    }

    public void setLaundryVol(int laundryVol) {
        this.laundryVol = laundryVol;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}