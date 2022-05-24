package edu.skku.cs.finalproject.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderModel {
    public String id;
    public String macketID;
    public String price;
    public String time;
    public String number;


    public OrderModel(String userID, String id, double cnt, double price) {
        this.id=userID;
        this.macketID=id;
        long now =System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.time=sdf.format(date);
        this.number=Double.toString(cnt);
        this.price=Double.toString(price);
    }
}
