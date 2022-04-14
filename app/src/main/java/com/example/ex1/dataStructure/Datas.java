package com.example.ex1.dataStructure;

public class Datas {

    private Double humidity;
    private Double smell;
    private Double temperature;
    private Double weight1;
    private Double weight2;

    public boolean isnull;

    public Datas() {
        isnull = true;
    }

    public Datas(Double humidity, Double smell, Double temperature, Double weight1, Double weight2) {
        this.humidity = humidity;
        this.smell = smell;
        this.temperature = temperature;
        this.weight1 = weight1;
        this.weight2 = weight2;
        isnull = false;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getSmell() {
        return smell;
    }

    public void setSmell(Double smell) {
        this.smell = smell;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
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
}
