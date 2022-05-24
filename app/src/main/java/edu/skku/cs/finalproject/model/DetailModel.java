package edu.skku.cs.finalproject.model;

import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;

public class DetailModel {
    public String userID;
    public String ID;
    public String name;
    public DetailPriceModel detailPriceModel;
    public ArrayList<HogaItemModel> hogaItemModels;

    public ArrayList<CandleModel> candleModels;
    public ArrayList<CandleEntry> candleEntries;



    public DetailModel(String userID,String id,String name) {
        this.ID=id;this.name=name;
        this.userID=userID;
        detailPriceModel=new DetailPriceModel();
        hogaItemModels =new ArrayList<HogaItemModel>();
        candleModels = new ArrayList<>();
        candleEntries=new ArrayList<>();
    }

}


