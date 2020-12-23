package ru.csi.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataPrice {

    private String product_code;
    private int number;
    private int depart;
    private Date beginD;
    private Date endD;
    private long value;

    public DataPrice(String product_code, int number, int depart, Date beginD, Date endD, long value) {
        this.product_code = product_code;
        this.number = number;
        this.depart = depart;
        this.beginD = beginD;
        this.endD = endD;
        this.value = value;
    }

    public DataPrice(long value, int number, int depart, String product_code) {
        this.product_code = product_code;
        this.number = number;
        this.depart = depart;
        this.value = value;
    }

    public String getProduct_code() {
        return product_code;
    }


    public int getNumber() {
        return number;
    }

    public int getDepart() {
        return depart;
    }

    public Date getBeginD() {
        return beginD;
    }

    public void setBeginD(Date beginD) {
        this.beginD = beginD;
    }

    public Date getEndD() {
        return endD;
    }

    public void setEndD(Date endD) {
        this.endD = endD;
    }

    public long getValue() {
        return value;
    }



    @Override
    public String toString() {
        String pattern = "dd.MM.yyyy HH:mm:ss";
        SimpleDateFormat ft = new SimpleDateFormat(pattern);
        return "PriceRes{" +
                "product_code='" + product_code + '\'' +
                ", number=" + number +
                ", depart=" + depart +
                ", beginD=" + ft.format(beginD) +
                ", endD=" + ft.format(endD) +
                ", value=" + value +
                '}';
    }
}
