package com.example.mp01;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/*
* 190507 윤승기릿
* 변경사항 :
* parsing할 사이트를 naver shopping이 아닌, 그냥 naver에서 해 봄(반만 됨)
* 복잡한 것을 피하려고 notification, timer는 지웠음.
* 얻은것 : list형태로 나오는 아이템은 잘 나옴. 단, query가 정확할수록 좋은 값을 얻게됨. 이건 아가리를 잘 털어야겠다.
* */

public class MainActivity extends AppCompatActivity {

    //XML UI 선언
    EditText input;
    Button webParsing;
    TextView webParsingOutput;
    TextView debug;

    String HTMLPageURL ="https://search.naver.com/search.naver?query=";
    private String HTMLContentInStringFormat="";
    PendingIntent intent;
    String subOutput="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //XML Inflation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI 끌당
        input = findViewById(R.id.input);
        webParsing = findViewById(R.id.webParsing);
        webParsingOutput = findViewById(R.id.webPasingOutput);
        debug =findViewById(R.id.debug);

        //debugUI
        debug.setText("onCreate()");

        webParsingOutput.setMovementMethod(new ScrollingMovementMethod());
        intent = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        webParsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debug.setText("webParsing.onClick()");
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });
    }

    private class JsoupAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute(){
            debug.setText("JsoupAsyncTask.onPreExecute()");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params){
            debug.setText("JsoupAsyncTask.doInBackground()");
            try{
                int price;
                int lowPrice=99999999;
                String sample="";
                String sub="";
                String userQuery = input.getText().toString();
                HTMLPageURL+=userQuery;
                Log.d("tag","JsoupAsyncTask.doInBackground()-HTMLPageURL(modified) : "+HTMLPageURL);
                //이곳에 debug.setText(HTMLPageURL) 넣으면 펑! 하고 터져버림

                Document doc = Jsoup.connect(HTMLPageURL).get();

                //여기가 리스트뷰
                Elements titles= doc.select("div.info_price em.num" );
                Elements subItems = doc.select("ul.product_list li.product_item div.info");
                for(Element e: titles){
                    sample = e.text();
                    sample = sample.replaceAll("\\,","");
                    price = Integer.parseInt(sample);
                    lowPrice = price;
                }
                int subCount=0;
                for(Element e : subItems){
                    if(subCount<5) {
                        subCount++;
                    }
                    else {
                        sub = e.text();
                        subOutput= subOutput+sub + "\n";
                    }
                }


                //여기가 갤러리뷰
                titles= doc.select("em.price_num" );
                for(Element e: titles){
                    sample=e.text();
                    if (sample.startsWith("최저")){
                        int index = sample.indexOf("저");
                        sample = sample.substring(index+1,sample.length());
                        index = sample.indexOf("원");
                        sample = sample.substring(0,index);
                        sample = sample.replaceAll("\\,","");
                    }
                    price = Integer.parseInt(sample);
                    if(price<lowPrice){
                        lowPrice=price;
                    }
                }
                HTMLContentInStringFormat = userQuery +"/"+Integer.toString(lowPrice);
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected  void onPostExecute(Void result){
            webParsingOutput.setText(HTMLContentInStringFormat+"\n\n"+subOutput);
            HTMLPageURL = "https://search.naver.com/search.naver?query=";
            HTMLContentInStringFormat = "";
            subOutput="";
        }
    }
}