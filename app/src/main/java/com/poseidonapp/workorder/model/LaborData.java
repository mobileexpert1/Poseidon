package com.poseidonapp.workorder.model;

public class LaborData {

    String hrs;
    String labor;
    String rate;
    String amount;

    public String getHrs() {
        return hrs;
    }

    public void setHrs(String hrs) {
        this.hrs = hrs;
    }

    public String getLabor() {
        return labor;
    }

    public void setLabor(String labor) {
        this.labor = labor;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public double getAmount() {
        return Double.parseDouble(amount);
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
