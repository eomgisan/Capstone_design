package com.example.ex1.dataStructure;

public class Datas {

    private Double hum;
    private Double smell;
    private Double temp;
    private Double weight1;
    private Double weight2;



    public Datas(Double hum, Double smell, Double temp ,Double weight1, Double weight2){
        this.hum = hum;
        this.smell = smell;
        this.temp = temp;
        this.weight1 = weight1;
        this.weight2 = weight2;
    }

    public Double getWeight1() {
        return weight1;
    }

    public void setWeight1(Double weight1) {
        this.weight1 = weight1;
    }

    public Double getWeight2() {
        return weight2;
    }

    public void setWeight2(Double weight2) {
        this.weight2 = weight2;
    }

    public Double getSmell() {
        return smell;
    }

    public void setSmell(Double smell) {
        this.smell = smell;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getHum() {
        return hum;
    }

    public void setHum(Double hum) {
        this.hum = hum;
    }
}
