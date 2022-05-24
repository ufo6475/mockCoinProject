package edu.skku.cs.finalproject.model;

import java.util.ArrayList;

public class UserModel {

    public String userID;
    public String asset_won;
    public String asset_bit;
    public String asset_usd;
    public String able_won;
    public String able_bit;
    public String able_usd;
    public String init_won;
    public String init_bit;
    public String init_usd;
    public ArrayList<UserStockModel> userContainStocks;
    public ArrayList<UserStockModel> userOrderStocks;

    public UserModel(String id){
        userContainStocks=new ArrayList<>();
        userOrderStocks=new ArrayList<>();
        userID=id;
        able_bit="";
        able_usd="";
        able_won="";
        asset_bit="";
        asset_usd="";
        asset_won="";
        init_won="";
        init_bit="";
        init_usd="";
    }

}
