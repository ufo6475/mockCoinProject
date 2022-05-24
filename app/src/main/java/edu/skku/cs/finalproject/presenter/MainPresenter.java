package edu.skku.cs.finalproject.presenter;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import edu.skku.cs.finalproject.model.MainModel;
import edu.skku.cs.finalproject.model.RegisterModel;
import edu.skku.cs.finalproject.view.HomeActivity;
import edu.skku.cs.finalproject.view.MainActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainPresenter {
    MainActivity mainActivity;
    String serverurl = "";
    public MainPresenter(MainActivity mainActivity) {
        this.mainActivity=mainActivity;
    }

    public void loginClicked(String id, String passwd) {
        Intent intent= new Intent(mainActivity, HomeActivity.class);
        intent.putExtra("id",id);

        OkHttpClient client=new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(serverurl+"/login").newBuilder();
        MainModel mainModel = new MainModel(id,passwd);
        Gson gson =new Gson();
        String json =gson.toJson(mainModel,MainModel.class);
        String url =urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"),json)).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(myResponse);

                if(jsonObject.get("success").getAsString().equals("false")){
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mainActivity, "ID 혹은 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else{
                    Intent intent= new Intent(mainActivity, HomeActivity.class);
                    intent.putExtra("id",id);
                    mainActivity.startActivity(intent);
                }

            }
        });

    }
}
