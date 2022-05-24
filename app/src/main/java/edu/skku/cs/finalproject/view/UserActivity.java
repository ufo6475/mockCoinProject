package edu.skku.cs.finalproject.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import edu.skku.cs.finalproject.R;
import edu.skku.cs.finalproject.model.ItemModel;
import edu.skku.cs.finalproject.model.UserStockModel;
import edu.skku.cs.finalproject.presenter.UserPresenter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {

    UserPresenter userPresenter;
    boolean isContain;
    UserStockAdapter containAdapter;
    UserStockAdapter orderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Intent intent = getIntent();
        userPresenter = new UserPresenter(this,intent);


        ImageButton backBtn = findViewById(R.id.userBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPresenter.backPressed();
                UserActivity.super.onBackPressed();
            }
        });
        isContain=true;
        TextView containText = findViewById(R.id.userContainText);
        TextView orderText = findViewById(R.id.userOrderText);
        TextView curText =findViewById(R.id.userCurPrice);

        containText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                curText.setVisibility(View.VISIBLE);
                containText.setBackgroundColor(getColor(R.color.clicked_btn));
                orderText.setBackgroundColor(getColor(R.color.btn));
                isContain=true;
                userPresenter.containUpdate();
            }
        });
        orderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curText.setVisibility(View.INVISIBLE);

                orderText.setBackgroundColor(getColor(R.color.clicked_btn));
                containText.setBackgroundColor(getColor(R.color.btn));

                isContain=false;
                userPresenter.orderUpdate();

            }
        });
    }

    @Override
    public void onBackPressed(){
        userPresenter.backPressed();
        UserActivity.super.onBackPressed();
    }

    public void settitle(String id){
        TextView title= findViewById(R.id.userTitle);
        title.setText(id);
    }

    public void setAssetWon(String asset_won) {
        TextView assetWon = findViewById(R.id.userRemainWon);
        assetWon.setText(asset_won);
    }

    public void setAssetBit(String asset_bit) {
        TextView assetBit = findViewById(R.id.userRemainBit);
        assetBit.setText(asset_bit);
    }

    public void setAssetUsd(String asset_usd) {
        TextView assetUsd = findViewById(R.id.userRemainUsd);
        assetUsd.setText(asset_usd);
    }

    public void setAbleWon(String able_won) {
        TextView ableWon = findViewById(R.id.userOrderWon);
        ableWon.setText(able_won);
    }

    public void setAbleBit(String able_bit) {
        TextView ableBit = findViewById(R.id.userOrderBit);
        ableBit.setText(able_bit);
    }

    public void setAbleUsd(String able_usd) {
        TextView ableUsd = findViewById(R.id.userOrderUsd);
        ableUsd.setText(able_usd);
    }

    public void setTotalWon(String tmp) {
        TextView totalWon = findViewById(R.id.userTotalWon);
        totalWon.setText(tmp);
    }

    public void setTotalBit(String tmp) {
        TextView totalBit = findViewById(R.id.userTotalBit);
        totalBit.setText(tmp);
    }

    public void setTotalUsd(String tmp) {
        TextView totalUsd = findViewById(R.id.userTotalUsd);
        totalUsd.setText(tmp);
    }

    public void setWonPercent(String s) {
        TextView wonPercent = findViewById(R.id.userPercentWon);
        wonPercent.setText(s);
    }

    public void setBitPercent(String s) {
        TextView bitPercent = findViewById(R.id.userPercentBit);
        bitPercent.setText(s);
    }

    public void setUsdPercent(String s) {
        TextView usdPercent = findViewById(R.id.userPercentUsd);
        usdPercent.setText(s);
    }

    public void addAdapter(ArrayList<UserStockModel> userStocks,String userID) {
        ListView listView = findViewById(R.id.userListView);

        containAdapter= new UserStockAdapter(this,userStocks,userID);
        if(userStocks.size()>0&&userStocks.get(0).state>0){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int idx, long l) {
                    AlertDialog.Builder dig = new AlertDialog.Builder(UserActivity.this);
                    dig.setTitle("주문을 취소하시겠습니까?");
                    dig.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            OkHttpClient client1=new OkHttpClient();
                            HttpUrl.Builder urlBuilder1;
                            if(userStocks.get(idx).state==1) {
                                urlBuilder1 = HttpUrl.parse(userPresenter.serverurl + "/deletebuy").newBuilder();
                            }
                            else{
                                urlBuilder1 = HttpUrl.parse(userPresenter.serverurl + "/deletesell").newBuilder();
                            }
                            urlBuilder1.addQueryParameter("id",userID);
                            urlBuilder1.addQueryParameter("macketID",userStocks.get(idx).id);

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
                                    if(myResponse.contains("true")){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                userStocks.remove(idx);
                                                containAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                    else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(UserActivity.this,"주문 취소에 실패하였습니다",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                }
                            });



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
            });
        }
        listView.setAdapter(containAdapter);
        containAdapter.notifyDataSetChanged();

    }
}
