package edu.skku.cs.finalproject.model;

public class HogaItemModel {
    public String getAsk_price() {
        return ask_price;
    }

    public void setAsk_price(String ask_price) {
        this.ask_price = ask_price;
    }

    public String getBid_price() {
        return bid_price;
    }

    public void setBid_price(String bid_price) {
        this.bid_price = bid_price;
    }

    public String getAsk_size() {
        return ask_size;
    }

    public void setAsk_size(String ask_size) {
        this.ask_size = ask_size;
    }

    public String getBid_size() {
        return bid_size;
    }

    public void setBid_size(String bid_size) {
        this.bid_size = bid_size;
    }

    public String ask_price;
    public String bid_price;
    public String ask_size;
    public String bid_size;


}
