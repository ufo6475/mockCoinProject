package edu.skku.cs.finalproject.presenter;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import edu.skku.cs.finalproject.model.RegisterModel;
import edu.skku.cs.finalproject.view.MainActivity;
import edu.skku.cs.finalproject.view.RegisterActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterPresenter {

    String serverurl="dev";

    public RegisterActivity registerActivity;

    public RegisterPresenter(RegisterActivity registerActivity) {

        this.registerActivity=registerActivity;

    }
    public void btnClicked(String id,String passwd, String email, String won, String bit, String usd){
        OkHttpClient client=new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(serverurl+"/register").newBuilder();
        RegisterModel registerModel = new RegisterModel(id,passwd,email,won,bit,usd);
        Gson gson =new Gson();
        String json =gson.toJson(registerModel,RegisterModel.class);
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
                Log.d("su",jsonObject.get("emailsuccess").getAsString());

                registerActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(jsonObject.get("idsuccess").getAsString().equals("false")){
                            Toast.makeText(registerActivity, "이미 존재하는 ID입니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(jsonObject.get("emailsuccess").getAsString().equals("false")){
                            Toast.makeText(registerActivity, "이미 존재하는 Email입니다.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(registerActivity, "회원가입 완료", Toast.LENGTH_SHORT).show();
                            registerActivity.registerFin();
                        }
                    }
                });
            }
        });
    }


}
