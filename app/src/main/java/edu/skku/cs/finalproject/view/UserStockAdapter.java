package edu.skku.cs.finalproject.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.skku.cs.finalproject.R;
import edu.skku.cs.finalproject.model.HogaItemModel;
import edu.skku.cs.finalproject.model.UserStockModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserStockAdapter extends BaseAdapter {
    Context context;
    ArrayList<UserStockModel> StockModels;
    String userID;



    public UserStockAdapter(Context mcontext,ArrayList<UserStockModel> StockModels,String userID){
        this.StockModels=StockModels;
        this.context=mcontext;
        this.userID=userID;
    }

    @Override
    public int getCount() {
        return StockModels.size();
    }

    @Override
    public Object getItem(int i) {
        return StockModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.element_stock, viewGroup, false);
        }
        TextView stockName= view.findViewById(R.id.stockName);
        TextView stockNumber=view.findViewById(R.id.stockNumber);
        TextView stockCurPrice=view.findViewById(R.id.stockCurPrice);
        TextView stockInitPrice=view.findViewById(R.id.stockInitPrice);


        DecimalFormat formatter = new DecimalFormat("###,###.######");
        if(StockModels.get(i).state==0){
            stockName.setText(StockModels.get(i).name);
            stockNumber.setText(StockModels.get(i).number);
            stockCurPrice.setText(formatter.format(StockModels.get(i).curPrice));
            stockInitPrice.setText(formatter.format(StockModels.get(i).buyPrice));

            stockCurPrice.setVisibility(View.VISIBLE);
            stockInitPrice.setBackgroundColor(Color.WHITE);
        }

        else if(StockModels.get(i).state==1){

            stockName.setText(StockModels.get(i).name);
            stockNumber.setText(StockModels.get(i).number);
            stockInitPrice.setText(formatter.format(StockModels.get(i).buyPrice));

            stockCurPrice.setVisibility(View.INVISIBLE);
            stockInitPrice.setBackgroundColor(Color.RED);
        }
        else{
            stockName.setText(StockModels.get(i).name);
            stockNumber.setText(StockModels.get(i).number);
            stockInitPrice.setText(formatter.format(StockModels.get(i).buyPrice));

            stockCurPrice.setVisibility(View.INVISIBLE);
            stockInitPrice.setBackgroundColor(Color.BLUE);
        }

        return view;
    }
    }
