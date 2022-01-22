package com.example.ex1;

// 회원정보 데이터를 위한 객채 선언 파일
public class UserInfo {

    private String name;
    private String phoneNum;
    private String address;
    private String birth;

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