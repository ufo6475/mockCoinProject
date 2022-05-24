package edu.skku.cs.finalproject.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.skku.cs.finalproject.R;
import edu.skku.cs.finalproject.model.ItemModel;

class itemViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ItemModel> ItemModels;


    public itemViewAdapter(Context mContext, ArrayList<ItemModel> ItemModels) {
        this.context = mContext;
        this.ItemModels = ItemModels;
    }

    public void update(ArrayList<ItemModel>nItemModels){
        this.ItemModels=nItemModels;
    }

    @Override
    public int getCount() {
        return ItemModels.size();
    }

    @Override
    public Object getItem(int i) {
        return ItemModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.element_market, viewGroup, false);
        }


        TextView marketName = view.findViewById(R.id.elementMarketName);
        TextView marketID = view.findViewById(R.id.elementMarketID);
        TextView marketCurPrice = view.findViewById(R.id.elementMarketCurPrice);
        TextView marketMount = view.findViewById(R.id.elementMarketMount);
        TextView marketUpDown = view.findViewById(R.id.elementMarketUpDown);
        TextView marketPercent = view.findViewById(R.id.elementMarketPercent);

        marketName.setText(ItemModels.get(i).name);
        marketID.setText(ItemModels.get(i).ID);
        if(ItemModels.get(i).name.length()>10){
            marketName.setTextSize(15);
        }
        else{
            marketName.setTextSize(20);
        }
        if(ItemModels.get(i).ID.contains("KRW")){
            DecimalFormat formatter = new DecimalFormat("###,###.##");
            marketCurPrice.setText(formatter.format(ItemModels.get(i).curPrice));
            DecimalFormat formatter2 = new DecimalFormat("###,###");
            marketMount.setText(formatter2.format( ItemModels.get(i).mount / 1000000) + "백만");
            marketUpDown.setText(String.format("%.0f", ItemModels.get(i).upDown));
            marketPercent.setText(String.format("%.3f", ItemModels.get(i).percent * 100) + "%");
            marketCurPrice.setTextSize(20);
            if(ItemModels.get(i).curPrice>=10000000){
                marketCurPrice.setTextSize(15);
            }
        }
        else if(ItemModels.get(i).ID.contains("BTC")){
            DecimalFormat formatter = new DecimalFormat("###,###.########");
            marketCurPrice.setText(formatter.format(ItemModels.get(i).curPrice));
            DecimalFormat formatter2 = new DecimalFormat("###,###.###");
            marketMount.setText(formatter2.format(ItemModels.get(i).mount ) );
            marketUpDown.setText(String.format("%.8f", ItemModels.get(i).upDown));
            marketPercent.setText(String.format("%.3f", ItemModels.get(i).percent * 100) + "%");
            marketCurPrice.setTextSize(15);
        }

        else{
            DecimalFormat formatter = new DecimalFormat("###,###.######");
            marketCurPrice.setText(formatter.format(ItemModels.get(i).curPrice));
            marketMount.setText(formatter.format( ItemModels.get(i).mount) );
            marketUpDown.setText(String.format("%.0f", ItemModels.get(i).upDown));
            marketPercent.setText(String.format("%.3f", ItemModels.get(i).percent * 100) + "%");
            marketCurPrice.setTextSize(15);
        }
        if (ItemModels.get(i).upDown < 0) {
            marketCurPrice.setTextColor(Color.BLUE);
            marketUpDown.setTextColor(Color.BLUE);
            marketPercent.setTextColor(Color.BLUE);
        }
        if (ItemModels.get(i).upDown > 0) {
            marketCurPrice.setTextColor(Color.RED);
            marketUpDown.setTextColor(Color.RED);
            marketPercent.setTextColor(Color.RED);
        }

        return view;
    }
}
