package edu.skku.cs.finalproject.presenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import edu.skku.cs.finalproject.HomeInterface;
import edu.skku.cs.finalproject.model.HomeModel;
import edu.skku.cs.finalproject.model.MarketModel;
import edu.skku.cs.finalproject.model.ItemModel;
import edu.skku.cs.finalproject.view.HomeActivity;
import edu.skku.cs.finalproject.view.DetailActivity;
import edu.skku.cs.finalproject.view.UserActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePresenter implements HomeInterface.presenter {

    public HomeModel homeModel;
    String cur_parameter;
    ArrayList<ItemModel> cur_models;
    String cur_state;
    public HomeActivity homeActivity;
    Timer timer;
    String userId;

    String serverurl="h";


    @Override
    public void getModel(HomeActivity homeActivity){
        homeModel = new HomeModel();
        OkHttpClient client=new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.upbit.com/v1/market/all").newBuilder();
        urlBuilder.addQueryParameter("isDetails","true");
        String url =urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(myResponse);
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        MarketModel item = new MarketModel();
                        item.setMarket(jsonObject.getString("market"));
                        item.setEnglish_name(jsonObject.getString("english_name"));
                        item.setKorean_name(jsonObject.getString("korean_name"));
                        item.setMarket_warning(jsonObject.getString("market_warning"));

                        if(item.getMarket().split("-")[0].equals("KRW")) {
                            homeModel.getKRWMarket().add(item);
                        }
                        else if(item.getMarket().split("-")[0].equals("BTC")) {
                            homeModel.getBTCMarket().add(item);
                        }
                        else {
                            homeModel.getUSDTMarket().add(item);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                OkHttpClient client3=new OkHttpClient();
                HttpUrl.Builder urlBuilder3 = HttpUrl.parse(serverurl+"/prefer").newBuilder();
                urlBuilder3.addQueryParameter("id",userId);
                String url3 =urlBuilder3.build().toString();
                Request req3 = new Request.Builder().url(url3).build();
                client3.newCall(req3).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String myResponse = response.body().string();

                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = (JsonObject) jsonParser.parse(myResponse);
                        Log.d("su",jsonObject.get("success").getAsString());
                        if(jsonObject.get("success").getAsString().equals("false"))
                            return;
                        homeModel.prefer_parameter=jsonObject.get("prefer").getAsString();
                        OkHttpClient client1=new OkHttpClient();
                        HttpUrl.Builder urlBuilder1 = HttpUrl.parse("https://api.upbit.com/v1/ticker").newBuilder();
                        urlBuilder1.addQueryParameter("markets",homeModel.prefer_parameter);
                        String url1 =urlBuilder1.build().toString();
                        Log.d("su",url1);
                        Request req1 = new Request.Builder().url(url1).build();
                        client1.newCall(req1).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                final String myResponse = response.body().string();
                                try {
                                    JSONArray jsonArray = new JSONArray(myResponse);
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        ItemModel itemModel= new ItemModel();
                                        itemModel.ID=jsonObject.getString("market");
                                        if(itemModel.ID.contains("KRW")){
                                            for(int j=0;j<homeModel.getKRWMarket().size();j++){
                                                if(homeModel.getKRWMarket().get(j).getMarket().equals(itemModel.ID)){
                                                    itemModel.name=homeModel.getKRWMarket().get(j).getKorean_name();
                                                }
                                            }
                                        }
                                        else if(itemModel.ID.contains("BTC")){
                                            for(int j=0;j<homeModel.getBTCMarket().size();j++){
                                                if(homeModel.getBTCMarket().get(j).getMarket().equals(itemModel.ID)){
                                                    itemModel.name=homeModel.getBTCMarket().get(j).getKorean_name();
                                                }
                                            }
                                        }
                                        else{
                                            for(int j=0;j<homeModel.getUSDTMarket().size();j++) {
                                                if (homeModel.getUSDTMarket().get(j).getMarket().equals(itemModel.ID)) {
                                                    itemModel.name = homeModel.getUSDTMarket().get(j).getKorean_name();
                                                }
                                            }
                                        }

                                        itemModel.curPrice=Double.parseDouble(jsonObject.getString("trade_price"));
                                        itemModel.upDown=Double.parseDouble(jsonObject.getString("signed_change_price"));
                                        itemModel.mount=Double.parseDouble(jsonObject.getString("acc_trade_price_24h"));
                                        itemModel.percent=Double.parseDouble(jsonObject.getString("signed_change_rate"));
                                        homeModel.preferModel.add(itemModel);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }
                });
                OkHttpClient client2 = new OkHttpClient();
                HttpUrl.Builder urlBuilder2 = HttpUrl.parse(serverurl + "/contain").newBuilder();
                urlBuilder2.addQueryParameter("id", userId);
                String url2 = urlBuilder2.build().toString();
                Request req2 = new Request.Builder().url(url2).build();
                client2.newCall(req2).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String myResponse=response.body().string();

                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = (JsonObject) jsonParser.parse(myResponse);
                        if(jsonObject.get("success").getAsString().equals("false"))
                            return;
                        String tmp=jsonObject.get("macketID").toString();
                        homeModel.contain_parameter=tmp.substring(1,tmp.length()-1);
                        String tt=jsonObject.get("number").getAsString();
                        String tt2=jsonObject.get("price").getAsString();
                        String[] tmp_number = tt.split(",");
                        String[] tmp_price =tt2.split(",");


                        for(int i=0;i<tmp_number.length;i++){
                            homeModel.containInitPrice.add(Double.parseDouble(tmp_price[i]));
                            homeModel.containNumber.add(tmp_number[i]);
                        }
                        OkHttpClient client1=new OkHttpClient();
                        HttpUrl.Builder urlBuilder1 = HttpUrl.parse("https://api.upbit.com/v1/ticker").newBuilder();
                        urlBuilder1.addQueryParameter("markets", homeModel.contain_parameter);

                        String url1 =urlBuilder1.build().toString();
                        Log.d("url",url1);
                        Request req1 = new Request.Builder().url(url1).build();
                        client1.newCall(req1).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                final String myResponse = response.body().string();
                                try {
                                    JSONArray jsonArray = new JSONArray(myResponse);
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        ItemModel itemModel= new ItemModel();
                                        itemModel.ID=jsonObject.getString("market");
                                        if(itemModel.ID.contains("KRW")){
                                            for(int j=0;j<homeModel.getKRWMarket().size();j++){
                                                if(homeModel.getKRWMarket().get(j).getMarket().equals(itemModel.ID)) {
                                                    itemModel.name = homeModel.getKRWMarket().get(j).getKorean_name();
                                                }
                                            }
                                        }
                                        else if(itemModel.ID.contains("BTC")){
                                            for(int j=0;j<homeModel.getBTCMarket().size();j++){
                                                if(homeModel.getBTCMarket().get(j).getMarket().equals(itemModel.ID)){
                                                    itemModel.name=homeModel.getBTCMarket().get(j).getKorean_name();
                                                }
                                            }
                                        }
                                        else{
                                            for(int j=0;j<homeModel.getUSDTMarket().size();j++) {
                                                if (homeModel.getUSDTMarket().get(j).getMarket().equals(itemModel.ID)) {
                                                    itemModel.name = homeModel.getUSDTMarket().get(j).getKorean_name();
                                                }
                                            }
                                        }
                                        itemModel.curPrice=Double.parseDouble(jsonObject.getString("trade_price"));
                                        itemModel.upDown=Double.parseDouble(jsonObject.getString("signed_change_price"));
                                        itemModel.mount=Double.parseDouble(jsonObject.getString("acc_trade_price_24h"));
                                        itemModel.percent=Double.parseDouble(jsonObject.getString("signed_change_rate"));
                                        homeModel.containModel.add(itemModel);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }
                }
                );


                for(int i=0;i<homeModel.getKRWMarket().size()-1;i++) {
                    ItemModel nItem = new ItemModel();
                    nItem.ID=homeModel.getKRWMarket().get(i).getMarket();
                    nItem.name=homeModel.getKRWMarket().get(i).getKorean_name();
                    homeModel.KRW_Models.add(nItem);
                    homeModel.KRW_parameter = homeModel.KRW_parameter + homeModel.getKRWMarket().get(i).getMarket() + ",";
                }
                ItemModel nItem = new ItemModel();
                nItem.ID=homeModel.getKRWMarket().get(homeModel.getKRWMarket().size()-1).getMarket();
                nItem.name=homeModel.getKRWMarket().get(homeModel.getKRWMarket().size()-1).getKorean_name();
                homeModel.KRW_Models.add(nItem);
                homeModel.KRW_parameter=homeModel.KRW_parameter+homeModel.getKRWMarket().get(homeModel.getKRWMarket().size()-1).getMarket();

                for(int i=0;i<homeModel.getBTCMarket().size()-1;i++) {
                    ItemModel BTCItem = new ItemModel();
                    BTCItem.ID=homeModel.getBTCMarket().get(i).getMarket();
                    BTCItem.name=homeModel.getBTCMarket().get(i).getKorean_name();
                    homeModel.BTC_Models.add(BTCItem);
                    homeModel.BTC_parameter = homeModel.BTC_parameter + homeModel.getBTCMarket().get(i).getMarket() + ",";
                }
                ItemModel BTCItem = new ItemModel();
                BTCItem.ID=homeModel.getBTCMarket().get(homeModel.getBTCMarket().size()-1).getMarket();
                BTCItem.name=homeModel.getBTCMarket().get(homeModel.getBTCMarket().size()-1).getKorean_name();
                homeModel.BTC_Models.add(BTCItem);
                homeModel.BTC_parameter=homeModel.BTC_parameter+homeModel.getBTCMarket().get(homeModel.getBTCMarket().size()-1).getMarket();

                for(int i=0;i<homeModel.getUSDTMarket().size()-1;i++) {
                    ItemModel USDTItem = new ItemModel();
                    USDTItem.ID=homeModel.getUSDTMarket().get(i).getMarket();
                    USDTItem.name=homeModel.getUSDTMarket().get(i).getKorean_name();
                    homeModel.USDT_Models.add(USDTItem);
                    homeModel.USDT_parameter = homeModel.USDT_parameter + homeModel.getUSDTMarket().get(i).getMarket() + ",";
                }
                ItemModel USDTItem = new ItemModel();
                USDTItem.ID=homeModel.getUSDTMarket().get(homeModel.getUSDTMarket().size()-1).getMarket();
                USDTItem.name=homeModel.getUSDTMarket().get(homeModel.getUSDTMarket().size()-1).getKorean_name();
                homeModel.USDT_Models.add(USDTItem);
                homeModel.USDT_parameter=homeModel.USDT_parameter+homeModel.getUSDTMarket().get(homeModel.getUSDTMarket().size()-1).getMarket();


                cur_parameter=homeModel.KRW_parameter;
                cur_models=homeModel.KRW_Models;
                cur_state="KRW";


                OkHttpClient client1=new OkHttpClient();
                HttpUrl.Builder urlBuilder1 = HttpUrl.parse("https://api.upbit.com/v1/ticker").newBuilder();
                urlBuilder1.addQueryParameter("markets",cur_parameter);
                String url1 =urlBuilder1.build().toString();
                Request req1 = new Request.Builder().url(url1).build();
                client1.newCall(req1).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        final String myResponse = response.body().string();
                        try {
                            JSONArray jsonArray = new JSONArray(myResponse);
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                cur_models.get(i).curPrice=Double.parseDouble(jsonObject.getString("trade_price"));
                                cur_models.get(i).upDown=Double.parseDouble(jsonObject.getString("signed_change_price"));
                                cur_models.get(i).mount=Double.parseDouble(jsonObject.getString("acc_trade_price_24h"));
                                cur_models.get(i).percent=Double.parseDouble(jsonObject.getString("signed_change_rate"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        homeActivity.setItems(cur_models);
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                updateItems(homeActivity,cur_parameter,cur_models,cur_state);
                            }
                        };
                        timer=new Timer();
                        timer.schedule(task,01,1001);

                    }
                });
            }
        });
    }



    @Override
    public void updateItems(HomeActivity homeActivity, String parameter, ArrayList<ItemModel> ItemModels, String cur_state) {

        OkHttpClient client1=new OkHttpClient();
        HttpUrl.Builder urlBuilder1 = HttpUrl.parse("https://api.upbit.com/v1/ticker").newBuilder();
        urlBuilder1.addQueryParameter("markets",parameter);
        String url1 =urlBuilder1.build().toString();
        Request req1 = new Request.Builder().url(url1).build();
        client1.newCall(req1).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(myResponse);
                    //Log.d("su",myResponse);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ItemModels.get(i).curPrice=Double.parseDouble(jsonObject.getString("trade_price"));
                        ItemModels.get(i).upDown=Double.parseDouble(jsonObject.getString("signed_change_price"));
                        ItemModels.get(i).mount=Double.parseDouble(jsonObject.getString("acc_trade_price_24h"));
                        ItemModels.get(i).percent=Double.parseDouble(jsonObject.getString("signed_change_rate"));
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                homeActivity.updateItems(ItemModels,cur_state);
            }
        });


        OkHttpClient client2 = new OkHttpClient();
        HttpUrl.Builder urlBuilder2 = HttpUrl.parse(serverurl + "/contain").newBuilder();
        urlBuilder2.addQueryParameter("id", userId);
        String url2 = urlBuilder2.build().toString();
        Request req2 = new Request.Builder().url(url2).build();
        client2.newCall(req2).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String myResponse=response.body().string();
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(myResponse);
                if(jsonObject.get("success").getAsString().equals("false"))
                    return;
                String tmp=jsonObject.get("macketID").toString();
                homeModel.contain_parameter=tmp.substring(1,tmp.length()-1);
                String t1= jsonObject.get("number").toString();
                String t2=jsonObject.get("price").toString();
                String[] tmp_number =t1.substring(1,t1.length()-1).split(",");
                String[] tmp_price =t2.substring(1,t2.length()-1).split(",");
                homeModel.containInitPrice.clear();
                homeModel.containNumber.clear();
                for(int i=0;i<tmp_number.length;i++){
                    homeModel.containInitPrice.add(Double.parseDouble(tmp_price[i]));
                    homeModel.containNumber.add(tmp_number[i]);
                }
                OkHttpClient client1=new OkHttpClient();
                HttpUrl.Builder urlBuilder1 = HttpUrl.parse("https://api.upbit.com/v1/ticker").newBuilder();
                urlBuilder1.addQueryParameter("markets", homeModel.contain_parameter);

                String url1 =urlBuilder1.build().toString();
                Log.d("url",url1);
                Request req1 = new Request.Builder().url(url1).build();
                client1.newCall(req1).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        final String myResponse = response.body().string();
                        try {
                            JSONArray jsonArray = new JSONArray(myResponse);
                            homeModel.containModel.clear();
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ItemModel itemModel= new ItemModel();
                                itemModel.ID=jsonObject.getString("market");
                                if(itemModel.ID.contains("KRW")){
                                    for(int j=0;j<homeModel.getKRWMarket().size();j++){
                                        if(homeModel.getKRWMarket().get(j).getMarket().equals(itemModel.ID)) {
                                            itemModel.name = homeModel.getKRWMarket().get(j).getKorean_name();
                                        }
                                    }
                                }
                                else if(itemModel.ID.contains("BTC")){
                                    for(int j=0;j<homeModel.getBTCMarket().size();j++){
                                        if(homeModel.getBTCMarket().get(j).getMarket().equals(itemModel.ID)){
                                            itemModel.name=homeModel.getBTCMarket().get(j).getKorean_name();
                                        }
                                    }
                                }
                                else{
                                    for(int j=0;j<homeModel.getUSDTMarket().size();j++) {
                                        if (homeModel.getUSDTMarket().get(j).getMarket().equals(itemModel.ID)) {
                                            itemModel.name = homeModel.getUSDTMarket().get(j).getKorean_name();
                                        }
                                    }
                                }
                                itemModel.curPrice=Double.parseDouble(jsonObject.getString("trade_price"));
                                itemModel.upDown=Double.parseDouble(jsonObject.getString("signed_change_price"));
                                itemModel.mount=Double.parseDouble(jsonObject.getString("acc_trade_price_24h"));
                                itemModel.percent=Double.parseDouble(jsonObject.getString("signed_change_rate"));
                                homeModel.containModel.add(itemModel);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
    });
    }

    @Override
    public void backPressed(){
        timer.cancel();

    }

    @Override
    public void KRWClicked(){
        this.cur_models=homeModel.KRW_Models;
        this.cur_parameter=homeModel.KRW_parameter;
        this.cur_state="KRW";
        homeActivity.setItems(cur_models);
    }
    @Override
    public void BTCClicked(){
        this.cur_models=homeModel.BTC_Models;
        this.cur_parameter=homeModel.BTC_parameter;
        this.cur_state="BTC";
        homeActivity.setItems(cur_models);
    }
    @Override
    public void USDTClicked(){
        this.cur_models=homeModel.USDT_Models;
        this.cur_parameter=homeModel.USDT_parameter;
        this.cur_state="USDT";
        homeActivity.setItems(cur_models);
    }

    @Override
    public void searchPressed() {
        EditText searchText = new EditText(homeActivity);

        AlertDialog.Builder dig = new AlertDialog.Builder(homeActivity);
        dig.setTitle("종목 검색");
        dig.setView(searchText);
        this.cur_state="종목 검색";
        dig.setPositiveButton("검색", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<ItemModel> searchModel = new ArrayList<>();
                String search  = searchText.getText().toString();
                String searchParameter="";
                for (int j=0;j<cur_models.size();j++){
                    if(cur_models.get(j).name.contains(search)){
                        searchModel.add(cur_models.get(j));
                        searchParameter=searchParameter+cur_models.get(j).ID+",";
                    }
                }
                if(searchModel.size()>0) {
                    searchParameter = searchParameter.substring(0, searchParameter.length() - 1);
                    cur_models = searchModel;
                    cur_parameter = searchParameter;
                }
                else{
                    Toast.makeText(homeActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                dialogInterface.dismiss();
            }
        });
        dig.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dig.show();
    }

    @Override
    public void itemClicked(ItemModel curItem) {
        Intent intent = new Intent(homeActivity, DetailActivity.class);
        intent.putExtra("ID",curItem.ID);
        intent.putExtra("name",curItem.name);
        if(homeModel.prefer_parameter.contains(curItem.ID)){
            intent.putExtra("prefer","true");
        }
        else{
            intent.putExtra("prefer","false");
        }
        intent.putExtra("userID",userId);
        intent.putExtra("homeModel",homeModel);

        timer.cancel();
        homeActivity.startActivity(intent);
        homeActivity.finish();
    }


    public HomePresenter(HomeActivity homeActivity, String id){
        this.homeActivity=homeActivity;
        this.userId=id;
        getModel(homeActivity);

    }


    public void preferClicked() {
        cur_state="관심 종목";
        cur_models=homeModel.preferModel;
        cur_parameter=homeModel.prefer_parameter;
        Log.d("su",cur_parameter);

    }


    public void containClicked() {
        cur_state = "보유 종목";
        cur_models=homeModel.containModel;
        cur_parameter= homeModel.contain_parameter;
    }

    public void userPressed() {
        Intent intent1 = new Intent(homeActivity, UserActivity.class);
        intent1.putExtra("id",userId);
        intent1.putExtra("homeModel",homeModel);
        timer.cancel();
        homeActivity.startActivity(intent1);
        homeActivity.finish();
    }
}

