package edu.skku.cs.finalproject.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.skku.cs.finalproject.DetailInterface;
import edu.skku.cs.finalproject.R;
import edu.skku.cs.finalproject.model.HogaItemModel;
import edu.skku.cs.finalproject.presenter.DetailPresenter;

public class DetailActivity extends AppCompatActivity implements DetailInterface.view {


    int curMenu=1;
    int chartid=0;
    int hogaid=0;
    int orderid=0;

    DetailPresenter detailPresenter;
    int btn_color;
    int clicked_btn_color;

    int cur_btn_id;

    int curViewId;

    String ID;

    boolean sell;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        detailPresenter = new DetailPresenter(intent,this);

        sell=false;
        ConstraintLayout mRootLayout = (ConstraintLayout) findViewById(R.id.detaillayout);
        ListView mListView = (ListView) new ListView(getApplicationContext());
        hogaid=View.generateViewId();
        mListView.setId(hogaid);
        curViewId=hogaid;
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,AbsListView.LayoutParams.WRAP_CONTENT);
        mRootLayout.addView(mListView,layoutParams);


        btn_color=getResources().getColor(R.color.btn);
        clicked_btn_color= getResources().getColor(R.color.clicked_btn);

        ImageButton backBtn = findViewById(R.id.itemBackButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailPresenter.backPressed();
                Intent intent1 =new Intent(getApplicationContext(),HomeActivity.class);
                intent1.putExtra("id",detailPresenter.userID);
                startActivity(intent1);
                DetailActivity.super.onBackPressed();
            }
        });

        TextView chartBtn = findViewById(R.id.itemMenu2);
        TextView hogaBtn = findViewById(R.id.itemMenu1);
        TextView orderBtn = findViewById(R.id.itemMenu3);
        ImageButton starBtn = findViewById(R.id.itemStarButton);


        starBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailPresenter.starBtnClicked();
            }
        });



        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartBtn.setBackgroundColor(btn_color);
                hogaBtn.setBackgroundColor(btn_color);
                orderBtn.setBackgroundColor(clicked_btn_color);
                if(curMenu==3){
                    return;
                }
                else{
                    curMenu=3;
                    View curView= mRootLayout.findViewById(curViewId);
                    mRootLayout.removeView(curView);
                    View orderView = getLayoutInflater().inflate(R.layout.list_order,mRootLayout,false);
                    orderid=View.generateViewId();
                    orderView.setId(orderid);
                    curViewId=orderid;
                    mRootLayout.addView(orderView);
                    detailPresenter.menu3Selected();

                    TextView sellBtn =orderView.findViewById(R.id.orderSellBtn);
                    TextView buyBtn=orderView.findViewById(R.id.orderBuyBtn);
                    Button sellOrBuyBtn =orderView.findViewById(R.id.orderBuySellBtn);

                    Button initBtn =orderView.findViewById(R.id.orderInitailBtn);
                    initBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditText numberText=orderView.findViewById(R.id.orderNumberText);
                            EditText editText=orderView.findViewById(R.id.orderPriceText);
                            editText.setText("");
                            numberText.setText("");
                        }
                    });

                    sellBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sell=true;
                            sellBtn.setBackgroundColor(getResources().getColor(R.color.hogaBlue));
                            buyBtn.setBackgroundColor(getResources().getColor(R.color.btn));
                            sellOrBuyBtn.setText("매도");

                        }
                    });
                    buyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sell=false;
                            sellBtn.setBackgroundColor(getResources().getColor(R.color.btn));
                            buyBtn.setBackgroundColor(getResources().getColor(R.color.hogaRed));
                            sellOrBuyBtn.setText("매수");
                        }
                    });

                    sellOrBuyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditText cntInput = orderView.findViewById(R.id.orderNumberText);
                            EditText priceInput=orderView.findViewById(R.id.orderPriceText);
                            try {
                                double cnt = Double.parseDouble(cntInput.getText().toString());
                                double price = Double.parseDouble(priceInput.getText().toString());
                                if(sell){
                                    detailPresenter.sell(ID,cnt,price);


                                }
                                else{
                                    detailPresenter.buy(ID,cnt,price);
                                }

                            }catch (Exception e){
                                Toast.makeText(DetailActivity.this, "잘못된 입력입니다.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

        hogaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartBtn.setBackgroundColor(btn_color);
                orderBtn.setBackgroundColor(btn_color);
                hogaBtn.setBackgroundColor(clicked_btn_color);
                if(curMenu==1)
                    return;
                else{

                    View curView= mRootLayout.findViewById(curViewId);
                    curMenu=1;
                    detailPresenter.menu1Selected();
                    mRootLayout.removeView(curView);
                    ListView mListView = (ListView) new ListView(getApplicationContext());
                    hogaid=View.generateViewId();
                    mListView.setId(hogaid);
                    curViewId=hogaid;
                    AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,AbsListView.LayoutParams.WRAP_CONTENT);
                    mRootLayout.addView(mListView,layoutParams);
                }
            }
        });

        chartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                cur_btn_id=R.id.chart15MinuteButton;
                hogaBtn.setBackgroundColor(btn_color);
                orderBtn.setBackgroundColor(btn_color);
                chartBtn.setBackgroundColor(clicked_btn_color);
                if(curMenu==2)
                    return;
                else{
                    curMenu=2;
                    detailPresenter.menu2Selected();
                    View curView= mRootLayout.findViewById(curViewId);
                    mRootLayout.removeView(curView);
                    View chartView = getLayoutInflater().inflate(R.layout.element_chart,mRootLayout,false);
                    chartid=View.generateViewId();
                    chartView.setId(chartid);
                    curViewId=chartid;
                    mRootLayout.addView(chartView);

                    TextView oneminBtn = chartView.findViewById(R.id.chart1MinuteButton);
                    TextView fiveminBtn = chartView.findViewById(R.id.chart5MinuteButton);
                    TextView fifteenminBtn = chartView.findViewById(R.id.chart15MinuteButton);
                    TextView hourBtn = chartView.findViewById(R.id.chart60MinuteButton);
                    TextView dayBtn = chartView.findViewById(R.id.chartDayButton);
                    TextView weekBtn = chartView.findViewById(R.id.chartWeekButton);
                    TextView monthBtn = chartView.findViewById(R.id.chartMonthButton);

                    oneminBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView curBtn = chartView.findViewById(cur_btn_id);
                            curBtn.setBackgroundColor(btn_color);
                            oneminBtn.setBackgroundColor(clicked_btn_color);
                            cur_btn_id=R.id.chart1MinuteButton;
                            detailPresenter.minClicked(0);
                        }
                    });

                    fiveminBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView curBtn = chartView.findViewById(cur_btn_id);
                            curBtn.setBackgroundColor(btn_color);
                            fiveminBtn.setBackgroundColor(clicked_btn_color);
                            cur_btn_id=R.id.chart5MinuteButton;
                            detailPresenter.minClicked(1);
                        }
                    });
                    fifteenminBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView curBtn = chartView.findViewById(cur_btn_id);
                            curBtn.setBackgroundColor(btn_color);
                            fifteenminBtn.setBackgroundColor(clicked_btn_color);
                            cur_btn_id=R.id.chart15MinuteButton;
                            detailPresenter.minClicked(2);
                        }
                    });
                    hourBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView curBtn = chartView.findViewById(cur_btn_id);
                            curBtn.setBackgroundColor(btn_color);
                            hourBtn.setBackgroundColor(clicked_btn_color);
                            cur_btn_id=R.id.chart60MinuteButton;
                            detailPresenter.minClicked(3);
                        }
                    });
                    dayBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView curBtn = chartView.findViewById(cur_btn_id);
                            curBtn.setBackgroundColor(btn_color);
                            dayBtn.setBackgroundColor(clicked_btn_color);
                            cur_btn_id=R.id.chartDayButton;
                            detailPresenter.minClicked(4);
                        }
                    });
                    weekBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView curBtn = chartView.findViewById(cur_btn_id);
                            curBtn.setBackgroundColor(btn_color);
                            weekBtn.setBackgroundColor(clicked_btn_color);
                            cur_btn_id=R.id.chartWeekButton;
                            detailPresenter.minClicked(5);
                        }
                    });
                    monthBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            TextView curBtn = chartView.findViewById(cur_btn_id);
                            curBtn.setBackgroundColor(btn_color);
                            monthBtn.setBackgroundColor(clicked_btn_color);
                            cur_btn_id=R.id.chartMonthButton;
                            detailPresenter.minClicked(6);
                        }
                    });

                }
            }
        });



    }



    @Override
    public void onBackPressed(){
        detailPresenter.backPressed();
        super.onBackPressed();
        Intent intent1 =new Intent(getApplicationContext(),HomeActivity.class);
        intent1.putExtra("id",detailPresenter.userID);
        startActivity(intent1);
        DetailActivity.super.onBackPressed();
    }

    @Override
    public void setTitle(String title){
        TextView titleText = findViewById(R.id.itemTitle);
        titleText.setText(title);
    }

    @Override
    public void setID(String ID){
        TextView IDText = findViewById(R.id.itemID);
        this.ID=ID;
        IDText.setText(ID);
    }

    @Override
    public void updatePrice(String curPrice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView priceText = findViewById(R.id.itemCurPrice);
                DecimalFormat formatter = new DecimalFormat("###,###.########");
                priceText.setText(formatter.format(Double.parseDouble(curPrice)));
            }
        });

    }

    public void updateHoga(ArrayList<HogaItemModel> hogaItemModels,boolean startBool,String state,double opening_price) {
        ListView listView = findViewById(hogaid);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (startBool) {
                        itemHogaAdapter itemHogaAdapter = new itemHogaAdapter(getApplicationContext(), hogaItemModels, state, opening_price);
                        listView.setAdapter(itemHogaAdapter);
                    } else {
                        itemHogaAdapter itemHogaAdapter = (edu.skku.cs.finalproject.view.itemHogaAdapter) listView.getAdapter();
                        itemHogaAdapter.update(hogaItemModels);
                        itemHogaAdapter.notifyDataSetChanged();
                    }
                }
                catch (Exception e){
                    return;
                }

            }
        });



    }

    public void updateChart(ArrayList<CandleEntry> entries,ArrayList<String>xAxisLabel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    View chartView = findViewById(chartid);
                    CandleStickChart chart = chartView.findViewById(R.id.chart);
                    CandleData candleData = chart.getCandleData();
                    if(candleData==null) {
                        CandleDataSet candleDataSet = new CandleDataSet(entries, "");
                        candleDataSet.setShadowColor(Color.LTGRAY);
                        candleDataSet.setShadowWidth(1F);
                        candleDataSet.setDecreasingColor(Color.BLUE);
                        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
                        candleDataSet.setIncreasingColor(Color.RED);
                        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
                        candleDataSet.setNeutralColor(Color.DKGRAY);
                        candleDataSet.setDrawValues(false);
                        candleDataSet.setHighLightColor(Color.TRANSPARENT);


                        chart.setHighlightPerDragEnabled(true);
                        chart.setDrawBorders(true);
                        chart.setBorderColor(Color.GRAY);
                        chart.setDragXEnabled(false);
                        chart.setDragYEnabled(false);


                        YAxis leftAxis = chart.getAxisLeft();
                        YAxis rightAxis = chart.getAxisRight();

                        chart.requestDisallowInterceptTouchEvent(true);


                        XAxis xAxis = chart.getXAxis();
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                if(value<100)
                                    return "";
                                return xAxisLabel.get((int)value);
                            }
                        });
                        xAxis.setDrawGridLines(false);

                        rightAxis.setTextColor(Color.WHITE);
                        xAxis.setGranularity(100f);
                        xAxis.setGranularityEnabled(true);
                        xAxis.setAvoidFirstLastClipping(true);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        chart.getLegend().setEnabled(false);

                        CandleData data = new CandleData(candleDataSet);
                        chart.setData(data);
                        chart.getDescription().setEnabled(false);
                        //chart.setVisibleXRange(5,5);
                        chart.invalidate();
                    }
                    else{
                        CandleDataSet candleDataSet = new CandleDataSet(entries, "");
                        candleDataSet.setShadowColor(Color.LTGRAY);
                        candleDataSet.setShadowWidth(1F);
                        candleDataSet.setDecreasingColor(Color.BLUE);
                        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
                        candleDataSet.setIncreasingColor(Color.RED);
                        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
                        candleDataSet.setNeutralColor(Color.DKGRAY);
                        candleDataSet.setDrawValues(false);
                        candleDataSet.setHighLightColor(Color.TRANSPARENT);

                        chart.setHighlightPerDragEnabled(true);
                        chart.setDrawBorders(true);
                        chart.setBorderColor(Color.GRAY);
                        chart.setDragXEnabled(false);
                        chart.setDragYEnabled(false);

                        YAxis leftAxis = chart.getAxisLeft();
                        YAxis rightAxis = chart.getAxisRight();

                        chart.requestDisallowInterceptTouchEvent(true);

                        XAxis xAxis = chart.getXAxis();
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                if(value<100 ||value>=xAxisLabel.size())
                                    return "";
                                return xAxisLabel.get((int)value);
                            }
                        });

                        xAxis.setDrawGridLines(false);
                        rightAxis.setTextColor(Color.WHITE);
                        xAxis.setGranularity(100f);

                        xAxis.setGranularityEnabled(true);
                        xAxis.setAvoidFirstLastClipping(true);
                        chart.getLegend().setEnabled(false);

                        //chart.setVisibleXRange(5,200);
                        chart.getDescription().setEnabled(false);
                        candleData.clearValues();
                        candleData.addDataSet(candleDataSet);
                        candleDataSet.notifyDataSetChanged();
                        chart.notifyDataSetChanged();
                        chart.invalidate();
                    }
                } catch (Exception e) {
                    return;
                }


            }
        });
    }

    public void updateOrder(ArrayList<HogaItemModel> hogaItemModels, boolean orderStartBool, String state, double opening_price) {
        try {
            View orderView = findViewById(orderid);
            ListView orderList = orderView.findViewById(R.id.orderListView);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (orderStartBool) {
                        itemOrderAdapter itemOrderAdapter = new itemOrderAdapter(getApplicationContext(), hogaItemModels, state, opening_price);
                        orderList.setAdapter(itemOrderAdapter);
                        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                if(i<orderList.getAdapter().getCount()/2){
                                    HogaItemModel curmodel = (HogaItemModel) orderList.getAdapter().getItem(orderList.getAdapter().getCount()-i-1);
                                    EditText editText=orderView.findViewById(R.id.orderPriceText);
                                    editText.setText(curmodel.ask_price);
                                }
                                else{
                                    HogaItemModel curmodel = (HogaItemModel) orderList.getAdapter().getItem(i);
                                    EditText editText=orderView.findViewById(R.id.orderPriceText);
                                    editText.setText(curmodel.bid_price);
                                }
                            }
                        });

                    } else {
                        if (orderList == null) {
                            return;
                        }
                        itemOrderAdapter itemOrderAdapter = (edu.skku.cs.finalproject.view.itemOrderAdapter) orderList.getAdapter();
                        itemOrderAdapter.update(hogaItemModels);
                        itemOrderAdapter.notifyDataSetChanged();
                    }

                }
            });
        }catch(Exception e) {
            return;
        }
    }

    public void changeStar(boolean b) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(b){
                    ImageButton starBtn = findViewById(R.id.itemStarButton);
                    starBtn.setImageResource(R.drawable.ic_baseline_star_24);
                }
                else{
                    ImageButton starBtn = findViewById(R.id.itemStarButton);
                    starBtn.setImageResource(R.drawable.ic_baseline_star_gray_24);
                }
            }
        });

    }

    public void sellNo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DetailActivity.this,"보유 주식 수가 부족합니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sellOk() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DetailActivity.this, "매도 주문을 접수했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void buyNo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DetailActivity.this,"보유 잔고가 부족합니다.",Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void buyOK() {
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            Toast.makeText(DetailActivity.this, "매수 주문을 접수했습니다.", Toast.LENGTH_SHORT).show();

        }
    });

    }
}