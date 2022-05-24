package edu.skku.cs.finalproject.presenter;

import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.CandleEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import edu.skku.cs.finalproject.DetailInterface;
import edu.skku.cs.finalproject.model.CandleModel;
import edu.skku.cs.finalproject.model.DetailModel;
import edu.skku.cs.finalproject.model.DetailPriceModel;
import edu.skku.cs.finalproject.model.HogaItemModel;
import edu.skku.cs.finalproject.model.HomeModel;
import edu.skku.cs.finalproject.model.ItemModel;
import edu.skku.cs.finalproject.model.MainModel;
import edu.skku.cs.finalproject.model.OrderModel;
import edu.skku.cs.finalproject.model.PreferModel;
import edu.skku.cs.finalproject.view.DetailActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailPresenter implements DetailInterface.presenter {

    DetailActivity detailActivity;
    public DetailModel detailModel;
    boolean hogaStartBool;
    boolean chartStartBool;
    boolean orderStartBool;
    Timer timer;
    int curChart=2;
    int numCandle=200;
    int totalCandle=500;
    int state=0;
    public boolean Prefer;
    public String userID;
    HomeModel homeModel;

    String serverurl="";

    public DetailPresenter(Intent intent, DetailActivity detailActivity) {
        hogaStartBool=true;
        chartStartBool=true;
        orderStartBool=true;
        userID= intent.getStringExtra("userID");
        String ID= intent.getStringExtra("ID");
        String name=intent.getStringExtra("name");
        String prefer=intent.getStringExtra("prefer");
        homeModel = (HomeModel) intent.getSerializableExtra("homeModel");
        Log.d("su",prefer);
        if(prefer.equals("true")){
            Prefer=true;
            detailActivity.changeStar(true);
        }
        else{
            Prefer=false;
        }
        this.detailActivity=detailActivity;
        detailModel = new DetailModel(userID,ID,name);
        getData(detailModel);
        initView(detailModel);
        
    }

    @Override
    public void initView(DetailModel detailModel) {
        detailActivity.setTitle(detailModel.name);
        detailActivity.setID(detailModel.ID);

    }


    @Override
    public void getData(DetailModel detailModel) {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updatePrice(detailModel.ID);
                updateHoga(detailModel.ID,state);
            }
        };
        timer=new Timer();
        timer.schedule(task,01,1001);

    }

    private void updateHoga(String parameter,int state) {
        OkHttpClient client=new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.upbit.com/v1/orderbook").newBuilder();
        urlBuilder.addQueryParameter("markets",parameter);
        String url =urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(detailModel.detailPriceModel.getOpening_price()==null){
                    return;
                }
                String myResponse = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(myResponse);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String orderbook_units =jsonObject.get("orderbook_units").toString();

                    JSONArray arrayUnits = new JSONArray(orderbook_units);
                    if(detailModel.hogaItemModels.size()==0){
                        for (int i=0;i<arrayUnits.length();i++){
                            JSONObject object = arrayUnits.getJSONObject(i);

                            HogaItemModel hogaItemModel = new HogaItemModel();
                            hogaItemModel.ask_price=object.get("ask_price").toString();
                            hogaItemModel.ask_size=object.get("ask_size").toString();
                            hogaItemModel.bid_price=object.get("bid_price").toString();
                            hogaItemModel.bid_size=object.get("bid_size").toString();
                            detailModel.hogaItemModels.add(hogaItemModel);
                        }
                    }
                    else {
                        for (int i = 0; i < arrayUnits.length(); i++) {
                            JSONObject object = arrayUnits.getJSONObject(i);

                            detailModel.hogaItemModels.get(i).ask_price = object.get("ask_price").toString();
                            detailModel.hogaItemModels.get(i).ask_size = object.get("ask_size").toString();
                            detailModel.hogaItemModels.get(i).bid_price = object.get("bid_price").toString();
                            detailModel.hogaItemModels.get(i).bid_size = object.get("bid_size").toString();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(state==0) {
                    detailActivity.updateHoga(detailModel.hogaItemModels, hogaStartBool, detailModel.ID.split("-")[0], Double.parseDouble(detailModel.detailPriceModel.getOpening_price()));
                    hogaStartBool=false;
                }
                else{
                    detailActivity.updateOrder(detailModel.hogaItemModels, orderStartBool, detailModel.ID.split("-")[0], Double.parseDouble(detailModel.detailPriceModel.getOpening_price()));
                    orderStartBool=false;
                }


            }
        });



    }

    public void updatePrice(String parameter) {
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
                String myResponse = response.body().string();
                myResponse=myResponse.substring(1,myResponse.length()-1);
                Log.d("detail",myResponse);

                Gson gson = new GsonBuilder().create();
                detailModel.detailPriceModel=gson.fromJson(myResponse, DetailPriceModel.class);
                detailActivity.updatePrice(detailModel.detailPriceModel.getTrade_price());

            }
        });
    }


    public void backPressed() {
        timer.cancel();
    }

    public void menu1Selected() {
        state=0;
        timer.cancel();
        hogaStartBool=true;
        getData(detailModel);
    }

    public void menu2Selected() {
        state=1;
        timer.cancel();
        chartStartBool=true;
        final float[] idx = {0};
        getCandle(detailModel,idx);
    }

    public void menu3Selected() {
        state=2;
        timer.cancel();
        orderStartBool=true;
        getData(detailModel);
    }

    private void getCandle(DetailModel detailModel, float[] idx) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder;
        if(curChart<4) {
             urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/minutes").newBuilder();
            if(curChart==0)
                urlBuilder.addPathSegment("1");
            else if(curChart==1)
                urlBuilder.addPathSegment("5");
            else if(curChart==2)
                urlBuilder.addPathSegment("15");
            else
                urlBuilder.addPathSegment("60");
        }
        else if(curChart==4){
            urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/days").newBuilder();
        }
        else if(curChart==5){
            urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/weeks").newBuilder();
        }
        else{
            urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/months").newBuilder();
        }
        urlBuilder.addQueryParameter("count",numCandle+"");
        urlBuilder.addQueryParameter("market", detailModel.ID);



        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String myResponse = response.body().string();

                try {
                    JSONArray jsonArray = new JSONArray(myResponse);
                    ArrayList<String> xAxisLabel = new ArrayList<>();
                    for (int i = 0; i <jsonArray.length() ; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CandleModel candleModel = new CandleModel();
                            candleModel.open = Float.parseFloat(jsonObject.getString("opening_price"));
                            candleModel.close = Float.parseFloat(jsonObject.getString("trade_price"));
                            candleModel.shadowHigh = Float.parseFloat(jsonObject.getString("high_price"));
                            candleModel.shadowLow = Float.parseFloat(jsonObject.getString("low_price"));
                            candleModel.createdAt = idx[0];
                            if(curChart<4) {
                                xAxisLabel.add(0, MillToDate(Long.parseLong(jsonObject.getString("timestamp"))).substring(11,19));
                            }
                            else{
                                xAxisLabel.add(0, MillToDate(Long.parseLong(jsonObject.getString("timestamp"))).substring(0,11));
                            }
                            idx[0] +=1;
                            detailModel.candleModels.add(candleModel);
                    }

                    if(idx[0]<totalCandle&&jsonArray.length()==numCandle){
                        getCandleAgain(detailModel,jsonArray.getJSONObject(jsonArray.length()-1).getString("candle_date_time_utc"),idx,xAxisLabel);
                    }
                    else{
                        float size=detailModel.candleModels.size();
                        for(int i=0;i<size;i++){
                            CandleModel curModel = detailModel.candleModels.get(i);
                            detailModel.candleEntries.add(0,new CandleEntry(size-curModel.createdAt-1,curModel.shadowHigh,curModel.shadowLow,curModel.open,curModel.close));
                        }
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                updatePrice(detailModel.ID);
                                updateCandle(detailModel,idx,xAxisLabel);
                            }
                        };
                        timer=new Timer();
                        timer.schedule(task,01,1001);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getCandleAgain(DetailModel detailModel, String candle_date_time_kst, float[] idx,ArrayList<String>xAxisLabel) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder;
        if(curChart<4) {
            urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/minutes").newBuilder();
            if(curChart==0)
                urlBuilder.addPathSegment("1");
            else if(curChart==1)
                urlBuilder.addPathSegment("5");
            else if(curChart==2)
                urlBuilder.addPathSegment("15");
            else
                urlBuilder.addPathSegment("60");
        }
        else if(curChart==4){
            urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/days").newBuilder();
        }
        else if(curChart==5){
            urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/weeks").newBuilder();
        }
        else{
            urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/months").newBuilder();
        }
        urlBuilder.addQueryParameter("count",numCandle+"");
        urlBuilder.addQueryParameter("market", detailModel.ID);
        urlBuilder.addQueryParameter("to",candle_date_time_kst+'Z');
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();;
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String myResponse = response.body().string();

                try {
                    JSONArray jsonArray = new JSONArray(myResponse);


                    for (int i = 1; i <jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        CandleModel candleModel = new CandleModel();
                        candleModel.open = Float.parseFloat(jsonObject.getString("opening_price"));
                        candleModel.close = Float.parseFloat(jsonObject.getString("trade_price"));
                        candleModel.shadowHigh = Float.parseFloat(jsonObject.getString("high_price"));
                        candleModel.shadowLow = Float.parseFloat(jsonObject.getString("low_price"));
                        candleModel.createdAt = idx[0];
                        if(curChart<4) {
                            xAxisLabel.add(0, MillToDate(Long.parseLong(jsonObject.getString("timestamp"))).substring(11,19));
                        }
                        else{
                            xAxisLabel.add(0, MillToDate(Long.parseLong(jsonObject.getString("timestamp"))).substring(0,11));
                        }
                        idx[0] +=1;
                        detailModel.candleModels.add(candleModel);
                    }
                    if(idx[0]<totalCandle&&jsonArray.length()==numCandle){
                        getCandleAgain(detailModel,jsonArray.getJSONObject(jsonArray.length()-1).getString("candle_date_time_utc"),idx,xAxisLabel);
                    }
                    else{

                        float size=detailModel.candleModels.size();
                        for(int i=0;i<size;i++){
                            CandleModel curModel = detailModel.candleModels.get(i);
                            detailModel.candleEntries.add(0,new CandleEntry(size-curModel.createdAt-1,curModel.shadowHigh,curModel.shadowLow,curModel.open,curModel.close));
                        }

                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                updatePrice(detailModel.ID);
                                updateCandle(detailModel,idx,xAxisLabel);
                            }
                        };
                        timer=new Timer();
                        timer.schedule(task,01,1001);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void updateCandle(DetailModel detailModel, float[] idx,ArrayList<String>xAxisLabel) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder;
        if(curChart<4) {
            urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/minutes").newBuilder();
            if(curChart==0)
                urlBuilder.addPathSegment("1");
            else if(curChart==1)
                urlBuilder.addPathSegment("5");
            else if(curChart==2)
                urlBuilder.addPathSegment("15");
            else
                urlBuilder.addPathSegment("60");
        }
        else if(curChart==4){
            urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/days").newBuilder();
        }
        else if(curChart==5){
            urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/weeks").newBuilder();
        }
        else{
            urlBuilder= HttpUrl.parse("https://api.upbit.com/v1/candles/months").newBuilder();
        }
        urlBuilder.addQueryParameter("market", detailModel.ID);
        String url = urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String myResponse = response.body().string();
                try {
                    if(detailModel.candleModels.size()==0)
                        return;
                    JSONArray jsonArray = new JSONArray(myResponse);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    float open = Float.parseFloat(jsonObject.getString("opening_price"));
                    float close = Float.parseFloat(jsonObject.getString("trade_price"));
                    float shadowHigh = Float.parseFloat(jsonObject.getString("high_price"));
                    float shadowLow = Float.parseFloat(jsonObject.getString("low_price"));
                    float createdAt = (int)idx[0]-1;

                    detailModel.candleEntries.get( (int)idx[0]-1).setOpen(open);
                    detailModel.candleEntries.get( (int)idx[0]-1).setClose(close);
                    detailModel.candleEntries.get( (int)idx[0]-1).setHigh(shadowHigh);
                    detailModel.candleEntries.get( (int)idx[0]-1).setLow(shadowLow);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                detailActivity.updateChart(detailModel.candleEntries,xAxisLabel);
            }
        });


    }

    public void minClicked(int curChart) {
        this.curChart=curChart;
        timer.cancel();
        final float[] idx = {0};
        detailModel.candleModels.clear();
        detailModel.candleEntries.clear();
        getCandle(detailModel,idx);
    }



    public void starBtnClicked() {
        OkHttpClient client=new OkHttpClient();
        HttpUrl.Builder urlBuilder;

        if(Prefer) {
            urlBuilder = HttpUrl.parse(serverurl + "/deleteprefer").newBuilder();
        }
        else{
            urlBuilder = HttpUrl.parse(serverurl + "/addprefer").newBuilder();
        }
        PreferModel preferModel = new PreferModel(detailModel.userID,detailModel.ID);
        Gson gson =new Gson();
        String json =gson.toJson(preferModel, PreferModel.class);
        String url =urlBuilder.build().toString();
        Log.d("su",url);
        Request req = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"),json)).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String myResponse = response.body().string();
                Log.d("su",myResponse);
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(myResponse);
                if (jsonObject.get("success").toString().equals("false"))
                    return;
                if(Prefer){
                    Prefer=false;
                }
                else{
                    Prefer=true;
                }
                detailActivity.changeStar(Prefer);
            }
        });
    }

    public void buy(String id, double cnt, double price) {
        OkHttpClient client=new OkHttpClient();
        HttpUrl.Builder urlBuilder;
        urlBuilder = HttpUrl.parse(serverurl + "/buy").newBuilder();

        OrderModel orderModel = new OrderModel(detailModel.userID,detailModel.ID,cnt,price);
        Log.d("id",detailModel.userID);
        Gson gson =new Gson();
        String json =gson.toJson(orderModel, OrderModel.class);
        String url =urlBuilder.build().toString();
        Log.d("buy",url);
        Request req = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"),json)).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String myResponse = response.body().string();
                Log.d("buy",myResponse);
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(myResponse);
                if (jsonObject.get("success").getAsString().equals("true")){
                    detailActivity.buyOK();
                }
                else {
                    detailActivity.buyNo();
                }
            }
        });

    }
    public void sell(String id, double cnt, double price) {
        OkHttpClient client=new OkHttpClient();
        HttpUrl.Builder urlBuilder;
        urlBuilder = HttpUrl.parse(serverurl + "/sell").newBuilder();

        OrderModel orderModel = new OrderModel(detailModel.userID,detailModel.ID,cnt,price);
        Log.d("id",detailModel.userID);
        Gson gson =new Gson();
        String json =gson.toJson(orderModel, OrderModel.class);
        String url =urlBuilder.build().toString();
        Log.d("sell",url);
        Request req = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"),json)).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String myResponse = response.body().string();
                Log.d("sell",myResponse);
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(myResponse);
                if (jsonObject.get("success").toString().equals("true")){
                    detailActivity.sellOk();
                }
                else{
                    detailActivity.sellNo();
                }
            }
        });

    }
    public String MillToDate(long mills) { String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date = (String) formatter.format(new Timestamp(mills));
        return date;
    }
}
