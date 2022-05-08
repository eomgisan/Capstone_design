package com.example.ex1.dataStructure;

public class UserFeature {
    private double aver_inc1;
    private double aver_inc2;
    private double priod1;
    private double priod2;
    private double ideal_w1;



    private double ideal_w2;


    public boolean isnull;

    public UserFeature() {
        isnull = true;
    }

    public UserFeature(double aver_inc1, double aver_inc2, double priod1,double priod2, double ideal_w1, double ideal_w2) {
        this.aver_inc1 = aver_inc1;
        this.aver_inc2 = aver_inc2;
        this.priod1 = priod1;
        this.priod2 = priod2;

        this.ideal_w1 = ideal_w1;
        this.ideal_w2 = ideal_w2;


        isnull = false;
    }
    public double getIdeal_w1() {
        return ideal_w1;
    }

    public void setIdeal_w1(double ideal_w1) {
        this.ideal_w1 = ideal_w1;
    }

    public double getIdeal_w2() {
        return ideal_w2;
    }

    public void setIdeal_w2(double ideal_w2) {
        this.ideal_w2 = ideal_w2;
    }
    public double getAver_inc1() {
        return aver_inc1;
    }

    public void setAver_inc1(double aver_inc1) {
        this.aver_inc1 = aver_inc1;
    }

    public double getAver_inc2() {
        return aver_inc2;
    }

    public void setAver_inc2(double aver_inc2) {
        this.aver_inc2 = aver_inc2;
    }

    public double getPriod1() {
        return priod1;
    }

    public void setPriod1(double priod1) {
        this.priod1 = priod1;
    }

    public double getPriod2() {
        return priod2;
    }

    public void setPriod2(double priod2) {
        this.priod2 = priod2;
    }


}
