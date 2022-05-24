package edu.skku.cs.finalproject.presenter;

import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.skku.cs.finalproject.R;
import edu.skku.cs.finalproject.model.HomeModel;
import edu.skku.cs.finalproject.model.ItemModel;
import edu.skku.cs.finalproject.model.UserModel;
import edu.skku.cs.finalproject.model.UserStockModel;
import edu.skku.cs.finalproject.view.HomeActivity;
import edu.skku.cs.finalproject.view.UserActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserPresenter {

    public String serverurl="hv";
    UserActivity userActivity;
    UserModel userModel;
    HomeModel homeModel;

    public UserPresenter(UserActivity userActivity, Intent intent) {

        this.userActivity=userActivity;
        String id= intent.getStringExtra("id");
        userModel =new UserModel(id);
        homeModel = (HomeModel) intent.getSerializableExtra("homeModel");

        OkHttpClient client=new OkHttpClient();
        HttpUrl.Builder urlBuilder;
        urlBuilder = HttpUrl.parse(serverurl + "/getuser").newBuilder();

        urlBuilder.addQueryParameter("id",id);
        String url =urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String myResponse = response.body().string();
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(myResponse);
                String tt = jsonObject.get("ret").toString();
                String[] tmp = tt.substring(1, tt.length() - 1).split(",");
                userModel.asset_won = tmp[0];
                userModel.asset_bit = tmp[1];
                userModel.asset_usd = tmp[2];
                userModel.able_won = tmp[3];
                userModel.able_bit = tmp[4];
                userModel.able_usd = tmp[5];
                userModel.init_won = tmp[6];
                userModel.init_bit = tmp[7];
                userModel.init_usd = tmp[8];

                userActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        DecimalFormat formatter = new DecimalFormat("###,###.##");
                        userActivity.setAssetWon(formatter.format(Double.parseDouble(userModel.asset_won)));
                        userActivity.setAssetBit(formatter.format(Double.parseDouble(userModel.asset_bit)));
                        userActivity.setAssetUsd(formatter.format(Double.parseDouble(userModel.asset_usd)));
                        userActivity.setAbleWon(formatter.format(Double.parseDouble(userModel.able_won)));
                        userActivity.setAbleBit(formatter.format(Double.parseDouble(userModel.able_bit)));
                        userActivity.setAbleUsd(formatter.format(Double.parseDouble(userModel.able_usd)));
                    }
                });

                if (!homeModel.contain_parameter.equals("")) {
                    OkHttpClient client1 = new OkHttpClient();
                    HttpUrl.Builder urlBuilder1 = HttpUrl.parse("https://api.upbit.com/v1/ticker").newBuilder();
                    urlBuilder1.addQueryParameter("markets", homeModel.contain_parameter);
                    String url1 = urlBuilder1.build().toString();
                    Request req1 = new Request.Builder().url(url1).build();
                    client1.newCall(req1).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            final String myResponse = response.body().string();
                            Log.d("curprice",myResponse);
                            try {
                                JSONArray jsonArray = new JSONArray(myResponse);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    homeModel.containCurPrice.add(Double.parseDouble(jsonObject.getString("trade_price")));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            double total_won = Double.parseDouble(userModel.asset_won);
                            double total_bit = Double.parseDouble(userModel.asset_bit);
                            double total_usd = Double.parseDouble(userModel.asset_usd);

                            for (int i = 0; i < homeModel.containModel.size(); i++) {
                                ItemModel tmp = homeModel.containModel.get(i);
                                if (tmp.ID.contains("KRW")) {
                                    total_won += homeModel.containCurPrice.get(i) * Double.parseDouble(homeModel.containNumber.get(i));
                                } else if (tmp.ID.contains("BTC")) {
                                    total_bit += homeModel.containCurPrice.get(i) * Double.parseDouble(homeModel.containNumber.get(i));
                                } else {
                                    total_usd += homeModel.containCurPrice.get(i) * Double.parseDouble(homeModel.containNumber.get(i));
                                }
                            }
                            double finalTotal_won = total_won;
                            double finalTotal_bit = total_bit;
                            double finalTotal_usd = total_usd;
                            userActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    DecimalFormat formatter = new DecimalFormat("###,###.##");
                                    userActivity.setTotalWon(formatter.format(finalTotal_won));
                                    userActivity.setTotalBit(formatter.format(finalTotal_bit));
                                    userActivity.setTotalUsd(formatter.format(finalTotal_usd));

                                    userActivity.setWonPercent(formatter.format(100 * (finalTotal_won - Double.parseDouble(userModel.init_won)) / Double.parseDouble(userModel.init_won)) + "%");
                                    userActivity.setBitPercent(formatter.format(100 * (finalTotal_bit - Double.parseDouble(userModel.init_bit)) / Double.parseDouble(userModel.init_bit)) + "%");
                                    userActivity.setUsdPercent(formatter.format(100 * (finalTotal_usd - Double.parseDouble(userModel.init_usd)) / Double.parseDouble(userModel.init_usd)) + "%");

                                    containStart();
                                    orderStart();
                                }
                            });
                        }
                    });
                }
                else{

                    userActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            double total_won = Double.parseDouble(userModel.asset_won);
                            double total_bit = Double.parseDouble(userModel.asset_bit);
                            double total_usd = Double.parseDouble(userModel.asset_usd);

                            DecimalFormat formatter = new DecimalFormat("###,###.##");
                            userActivity.setTotalWon(formatter.format(total_won));
                            userActivity.setTotalBit(formatter.format(total_bit));
                            userActivity.setTotalUsd(formatter.format(total_usd));

                            userActivity.setWonPercent(formatter.format(100 * (total_won - Double.parseDouble(userModel.init_won)) / Double.parseDouble(userModel.init_won)) + "%");
                            userActivity.setBitPercent(formatter.format(100 * (total_bit - Double.parseDouble(userModel.init_bit)) / Double.parseDouble(userModel.init_bit)) + "%");
                            userActivity.setUsdPercent(formatter.format(100 * (total_usd - Double.parseDouble(userModel.init_usd)) / Double.parseDouble(userModel.init_usd)) + "%");

                            containStart();
                            orderStart();

                        }
                    });
                }
            }
        });
        userActivity.settitle(id);
    }

    public void backPressed() {
        Intent intent = new Intent(userActivity, HomeActivity.class);
        intent.putExtra("id",userModel.userID);
        homeModel= (HomeModel) intent.getSerializableExtra("homeModel");
        userActivity.startActivity(intent);
    }


    public void containUpdate(){
        userActivity.addAdapter(userModel.userContainStocks,userModel.userID);
    }
    public void orderUpdate(){
        userActivity.addAdapter(userModel.userOrderStocks,userModel.userID);

    }

    public void containStart() {

        for(int i=0;i<homeModel.containInitPrice.size();i++) {
            UserStockModel userStockModel = new UserStockModel();
            userStockModel.state=0;
            userStockModel.name=homeModel.containModel.get(i).name;
            userStockModel.id=homeModel.containModel.get(i).ID;
            userStockModel.curPrice=homeModel.containCurPrice.get(i);
            userStockModel.buyPrice=homeModel.containInitPrice.get(i);
            userStockModel.number=homeModel.containNumber.get(i);
            userModel.userContainStocks.add(userStockModel);
        }
    }

    public void orderStart() {
        OkHttpClient client=new OkHttpClient();
        HttpUrl.Builder urlBuilder;
        urlBuilder = HttpUrl.parse(serverurl + "/getbuy").newBuilder();

        urlBuilder.addQueryParameter("id",userModel.userID);
        String url =urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String myResponse=response.body().string();
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(myResponse);
                if(jsonObject.get("success").getAsString().equals("false")){
                    return;
                }
                String IDs =jsonObject.get("macketID").getAsString();
                String numbers=jsonObject.get("number").getAsString();
                String prices=jsonObject.get("price").getAsString();

                String[] IDArray=IDs.split(",");
                String[] numberArray=numbers.split(",");
                String[] priceArray=prices.split(",");

                for(int i=0;i< IDArray.length;i++){
                    UserStockModel userStockModel = new UserStockModel();
                    userStockModel.state=1;
                    userStockModel.id=IDArray[i];
                    userStockModel.buyPrice=Double.parseDouble(priceArray[i]);
                    userStockModel.number=numberArray[i];
                    if(IDArray[i].contains("KRW")){
                        for(int j=0;j<homeModel.getKRWMarket().size();j++){

                            Log.d("KRW",homeModel.getKRWMarket().get(j).getMarket());
                            if(homeModel.getKRWMarket().get(j).getMarket().equals(IDArray[i])){
                                userStockModel.name=homeModel.getKRWMarket().get(j).getKorean_name();
                                break;
                            }
                        }
                    }
                    else if(IDArray[i].contains("BTC")){
                        for(int j=0;j<homeModel.getBTCMarket().size();j++){
                            if(homeModel.getBTCMarket().get(j).getMarket().equals(IDArray[i])){
                                userStockModel.name=homeModel.getBTCMarket().get(j).getKorean_name();
                                break;
                            }
                        }
                    }
                    else {
                        for (int j = 0; j < homeModel.getUSDTMarket().size(); j++) {
                            if (homeModel.getUSDTMarket().get(j).getMarket().equals(IDArray[i])) {
                                userStockModel.name = homeModel.getUSDTMarket().get(j).getKorean_name();
                                break;
                            }
                        }
                    }
                    userModel.userOrderStocks.add(userStockModel);
                }
            }
        });
        OkHttpClient client1=new OkHttpClient();
        HttpUrl.Builder urlBuilder1;
        urlBuilder1 = HttpUrl.parse(serverurl + "/getsell").newBuilder();

        urlBuilder1.addQueryParameter("id",userModel.userID);
        String url1 =urlBuilder1.build().toString();
        Request req1 = new Request.Builder().url(url1).build();

        client1.newCall(req1).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String myResponse=response.body().string();
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(myResponse);
                if(jsonObject.get("success").getAsString().equals("false")){
                    return;
                }
                String IDs =jsonObject.get("macketID").getAsString();
                String numbers=jsonObject.get("number").getAsString();
                String prices=jsonObject.get("price").getAsString();

                String[] IDArray=IDs.split(",");
                String[] numberArray=numbers.split(",");
                String[] priceArray=prices.split(",");

                for(int i=0;i< IDArray.length;i++){
                    UserStockModel userStockModel = new UserStockModel();
                    userStockModel.state=2;
                    userStockModel.buyPrice=Double.parseDouble(priceArray[i]);
                    userStockModel.number=numberArray[i];

                    if(IDArray[i].contains("KRW")){
                        for(int j=0;j<homeModel.getKRWMarket().size();j++){
                            if(homeModel.getKRWMarket().get(j).getMarket().equals(IDArray[i])){
                                userStockModel.name=homeModel.getKRWMarket().get(j).getKorean_name();
                                break;
                            }
                        }
                    }
                    else if(IDArray[i].contains("BTC")){
                        for(int j=0;j<homeModel.getBTCMarket().size();j++){
                            if(homeModel.getBTCMarket().get(j).getMarket().equals(IDArray[i])){
                                userStockModel.name=homeModel.getBTCMarket().get(j).getKorean_name();
                                break;
                            }
                        }
                    }
                    else {
                        for (int j = 0; j < homeModel.getUSDTMarket().size(); j++) {
                            if (homeModel.getUSDTMarket().get(j).getMarket().equals(IDArray[i])) {
                                userStockModel.name = homeModel.getUSDTMarket().get(j).getKorean_name();
                                break;
                            }
                        }
                    }
                    userModel.userOrderStocks.add(userStockModel);
                }
            }
        });

    }
}
