package edu.skku.cs.finalproject.model;

import java.io.Serializable;

public class MarketModel implements Serializable {

    private String market;
    private String korean_name;
    private String english_name;
    private String market_warning;


    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getKorean_name() {
        return korean_name;
    }

    public void setKorean_name(String korean_name) {
        this.korean_name = korean_name;
    }

    public String getEnglish_name() {
        return english_name;
    }

    public void setEnglish_name(String english_name) {
        this.english_name = english_name;
    }

    public String getMarket_warning() {
        return market_warning;
    }

    public void setMarket_warning(String market_warning) {
        this.market_warning = market_warning;
    }

}
