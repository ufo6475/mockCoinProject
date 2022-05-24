package edu.skku.cs.finalproject.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import edu.skku.cs.finalproject.HomeInterface;
import edu.skku.cs.finalproject.R;
import edu.skku.cs.finalproject.presenter.HomePresenter;
import edu.skku.cs.finalproject.model.ItemModel;

public class HomeActivity extends AppCompatActivity implements HomeInterface.view {

    HomePresenter homePresenter;
    TextView curBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent=getIntent();
        String id=intent.getStringExtra("id");

        homePresenter = new HomePresenter(this,id);

        TextView KRWbtn = (TextView)findViewById(R.id.homeKRWText);
        TextView BTCbtn = (TextView)findViewById(R.id.homeBTCText);
        TextView USDTbtn = (TextView)findViewById(R.id.homeUSDTText);
        TextView preferBtn=(TextView)findViewById(R.id.homeInterText);
        TextView containBtn= findViewById(R.id.homeContainText);
        curBtn=KRWbtn;

        KRWbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curBtn.setBackgroundColor(getResources().getColor(R.color.btn));
                KRWbtn.setBackgroundColor(getResources().getColor(R.color.clicked_btn));
                curBtn=KRWbtn;
                homePresenter.KRWClicked();
            }
        });
        BTCbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curBtn.setBackgroundColor(getResources().getColor(R.color.btn));

                BTCbtn.setBackgroundColor(getResources().getColor(R.color.clicked_btn));
                curBtn=BTCbtn;
                homePresenter.BTCClicked();
            }
        });
        USDTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curBtn.setBackgroundColor(getResources().getColor(R.color.btn));

                USDTbtn.setBackgroundColor(getResources().getColor(R.color.clicked_btn));
                curBtn=USDTbtn;
                homePresenter.USDTClicked();
            }
        });
        preferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curBtn.setBackgroundColor(getResources().getColor(R.color.btn));

                preferBtn.setBackgroundColor(getResources().getColor(R.color.clicked_btn));
                curBtn=preferBtn;
                homePresenter.preferClicked();
            }
        });
        containBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curBtn.setBackgroundColor(getResources().getColor(R.color.btn));

                containBtn.setBackgroundColor(getResources().getColor(R.color.clicked_btn));
                curBtn=containBtn;
                homePresenter.containClicked();
            }
        });



        ImageButton backBtn = (ImageButton)findViewById(R.id.homeBackButton);
        ImageButton searchBtn =(ImageButton) findViewById(R.id.homeSearchButton);
        ImageButton userBtn =findViewById(R.id.homeuserBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homePresenter.searchPressed();
            }
        });
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homePresenter.userPressed();
            }
        });

        ListView itemList = findViewById(R.id.homeListView);
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ItemModel curItem = (ItemModel) itemList.getAdapter().getItem(i);
                homePresenter.itemClicked(curItem);
            }
        });



    }
    @Override
    public void onBackPressed(){
        homePresenter.backPressed();
        super.onBackPressed();
    }

    @Override
    public void setItems(ArrayList<ItemModel> itemModels){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = findViewById(R.id.homeListView);
                itemViewAdapter itemViewAdapter=new itemViewAdapter(getApplicationContext(),itemModels);
                listView.setAdapter(itemViewAdapter);
            }
        });
    }

    public void updateItems(ArrayList<ItemModel> itemModels,String state){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = findViewById(R.id.homeListView);
                itemViewAdapter itemViewAdapter= (edu.skku.cs.finalproject.view.itemViewAdapter) listView.getAdapter();
                itemViewAdapter.update(itemModels);
                itemViewAdapter.notifyDataSetChanged();
                TextView Title = (TextView) findViewById(R.id.homeTitle);
                Title.setText(state);
            }
        });
    }
}




