package edu.skku.cs.finalproject.model;

public class UserStockModel {
    public int state;
    public String name;
    public String id;
    public double buyPrice;
    public double curPrice;
    public String number;
    public UserStockModel(){
        this.state=0;
        this.id="";
        this.name="";
        this.buyPrice=0;
        this.curPrice=0;
        this.number="";
    }

}
