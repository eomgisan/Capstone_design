package com.example.ex1.dataStructure;

// 회원정보 데이터를 위한 객채 선언 파일
public class UserInfo {

    private String name;
    private String phoneNum;
    private String address;
    private String birth;

    // 추가적으로 빨래통 1, 빨래통 2, 세제 정보, 지역정보 선언해주기

    public UserInfo(String name, String phoneNum, String address, String birth){
        this.name = name;
        this.address = address;
        this.phoneNum = phoneNum;
        this.birth = birth;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }


    public String getAddress(){
        return  this.address;
    }
    public void setAddress(String address){
        this.address = address;
    }


    public String getPhoneNum(){
        return  this.phoneNum;
    }
    public void setPhoneNum(String phoneNum){
        this.phoneNum = phoneNum;
    }


    public String getBirth(){
        return  this.birth;
    }
    public void setBirth(String birth){
        this.birth = birth;
    }


}