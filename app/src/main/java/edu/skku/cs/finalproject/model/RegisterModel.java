package edu.skku.cs.finalproject.model;

public class RegisterModel {

    public String id;
    public String passwd;
    public String email;
    public String asset_won;
    public String asset_bit;
    public String asset_usd;

    public RegisterModel(String id, String passwd, String email, String won, String bit, String usd) {
        this.id=id;
        this.passwd=passwd;
        this.email=email;
        this.asset_won=won;
        this.asset_bit=bit;
        this.asset_usd=usd;
    }
}
