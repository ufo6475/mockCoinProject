package edu.skku.cs.finalproject.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.skku.cs.finalproject.R;
import edu.skku.cs.finalproject.model.HogaItemModel;

public class itemOrderAdapter extends BaseAdapter {
    Context context;
    ArrayList<HogaItemModel> HogaModels;
    String state;
    double opening_price;



    public itemOrderAdapter(Context mcontext,ArrayList<HogaItemModel>HogaModels,String state,double opening_price){
        this.HogaModels=HogaModels;
        this.context=mcontext;
        this.state=state;
        this.opening_price=opening_price;
    }

    @Override
    public int getCount() {
        return HogaModels.size()*2;
    }

    @Override
    public Object getItem(int i) {
        return HogaModels.get(i%HogaModels.size());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.element_order, viewGroup, false);
        }

        if(i<HogaModels.size()){
            TextView textMid1 = view.findViewById(R.id.elementOrderMid1Text);
            TextView textMid2 = view.findViewById(R.id.elementOrderMid2Text);
            LinearLayout linearMid = view.findViewById(R.id.elementOrderMid);


            if(state.contains("BTC")) {
                DecimalFormat formatter = new DecimalFormat("###,##0.00000000");
                textMid1.setText(formatter.format(Double.parseDouble(HogaModels.get(HogaModels.size()-i-1).getAsk_price())));
                textMid1.setTextSize(12);
            }
            else {
                DecimalFormat formatter = new DecimalFormat("###,##0.000");
                textMid1.setText(formatter.format(Double.parseDouble(HogaModels.get(HogaModels.size()-i-1).getAsk_price())));
                textMid1.setTextSize(16);
            };
            textMid2.setText(String.format("%.3f",(Double.parseDouble(HogaModels.get(HogaModels.size()-i-1).getAsk_price())-opening_price)*100/opening_price)+"%");

            if ((Double.parseDouble(HogaModels.get(i % HogaModels.size()).getAsk_price())-opening_price)*100/opening_price>=0){
                textMid2.setTextColor(Color.RED);
            }
            else{
                textMid2.setTextColor(Color.BLUE);
            }
            linearMid.setBackgroundColor(view.getResources().getColor(R.color.hogaBlue));

        }
        else{
            TextView textMid1 = view.findViewById(R.id.elementOrderMid1Text);
            TextView textMid2 = view.findViewById(R.id.elementOrderMid2Text);

            LinearLayout linearMid = view.findViewById(R.id.elementOrderMid);
            if(state.contains("BTC")) {
                DecimalFormat formatter = new DecimalFormat("###,##0.00000000");
                textMid1.setText(formatter.format(Double.parseDouble(HogaModels.get(i % HogaModels.size()).getBid_price())));
                textMid1.setTextSize(12);
            }
            else{
                DecimalFormat formatter = new DecimalFormat("###,##0.000");
                textMid1.setText(formatter.format(Double.parseDouble(HogaModels.get(i % HogaModels.size()).getBid_price())));
                textMid1.setTextSize(16);
            }
            textMid2.setText(String.format("%.3f",(Double.parseDouble(HogaModels.get(i % HogaModels.size()).getBid_price())-opening_price)*100/opening_price)+"%");

            if ((Double.parseDouble(HogaModels.get(i % HogaModels.size()).getBid_price())-opening_price)*100/opening_price>=0){
                textMid2.setTextColor(Color.RED);
            }
            else{
                textMid2.setTextColor(Color.BLUE);
            }
            linearMid.setBackgroundColor(view.getResources().getColor(R.color.hogaRed));


        }


        return view;
    }

    public void update(ArrayList<HogaItemModel> hogaItemModels) {
        this.HogaModels=hogaItemModels;
    }
}
