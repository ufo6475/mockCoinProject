package edu.skku.cs.finalproject.model;

import java.io.Serializable;
import java.util.ArrayList;


public class HomeModel implements Serializable {

    public ArrayList<MarketModel> getKRWMarket() {
        return KRWMarket;
    }

    public ArrayList<MarketModel> getBTCMarket() {
        return BTCMarket;
    }

    public ArrayList<MarketModel> getUSDTMarket() {
        return USDTMarket;
    }

    private ArrayList<MarketModel> KRWMarket;
    private ArrayList<MarketModel> BTCMarket;
    private ArrayList<MarketModel> USDTMarket;

    public String KRW_parameter;
    public String BTC_parameter;
    public String USDT_parameter;

    public ArrayList<ItemModel> KRW_Models;
    public ArrayList<ItemModel> BTC_Models;
    public ArrayList<ItemModel> USDT_Models;

    public ArrayList<ItemModel> preferModel;
    public ArrayList<ItemModel> containModel;

    public ArrayList<String>containNumber;
    public ArrayList<Double>containInitPrice;
    public ArrayList<Double>containCurPrice;
    public String prefer_parameter;
    public String contain_parameter;

    public HomeModel(){
        KRWMarket = new ArrayList<>();
        BTCMarket = new ArrayList<>();
        USDTMarket = new ArrayList<>();
        KRW_parameter="";
        BTC_parameter="";
        USDT_parameter="";
        KRW_Models =new ArrayList<>();
        BTC_Models =new ArrayList<>();
        USDT_Models =new ArrayList<>();
        preferModel=new ArrayList<>();
        containModel=new ArrayList<>();
        containNumber=new ArrayList<>();
        containInitPrice=new ArrayList<>();
        containCurPrice=new ArrayList<>();
        prefer_parameter="";
        contain_parameter="";

    }



}



