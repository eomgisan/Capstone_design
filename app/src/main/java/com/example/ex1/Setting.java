package com.example.ex1;

public class Setting {

    private int location;
    private int pongpong;
    private int volume;


    public Setting(int location, int pongpong, int volume){
        this.location = location;
        this.pongpong = pongpong;
        this.volume = volume;
    }

    public int getLocation(){
        return this.location;
    }
    public void setLocation(int location){
        this.location = location;
    }


    public int getPongpong(){
        return  this.pongpong;
    }
    public void setPongpong(int pongpong){
        this.pongpong = pongpong;
    }


    public int getVolume(){
        return  this.volume;
    }
    public void setVolume(int volume){
        this.volume = volume;
    }


}
