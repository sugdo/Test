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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tag","onCreate()");
        //XML Inflation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI 끌당
        input = findViewById(R.id.input);
        webParsing = findViewById(R.id.webParsing);
        webParsingOutput = findViewById(R.id.webPasingOutput);
        debug =findViewById(R.id.debug);

        //debugUI
        Log.d("tag","현재 HTMLPageURL:"+HTMLPageURL);
        debug.setText("onCreate()");

        webParsingOutput.setMovementMethod(new ScrollingMovementMethod());
        intent = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        webParsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag","webParsing.onClick()");
                debug.setText("webParsing.onClick()");
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });
    }

    private class JsoupAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute(){
            Log.d("tag","JsoupAsyncTask.onPreExecute()");
            debug.setText("JsoupAsyncTask.onPreExecute()");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params){
            Log.d("tag","JsoupAsyncTask.doInBackground()");
            debug.setText("JsoupAsyncTask.doInBackground()");
            try{
                String userQuery = input.getText().toString();
                HTMLPageURL+=userQuery;
                Log.d("tag","JsoupAsyncTask.doInBackground()-HTMLPageURL(modified) : "+HTMLPageURL);
                //이곳에 debug.setText(HTMLPageURL) 넣으면 펑! 하고 터져버림

                Document doc = Jsoup.connect(HTMLPageURL).get();
                Log.d("tag","JsoupAsyncTask.doInBackground()-doc : "+doc);

                //div.info_price em.num
                /*
                * 리스트 형태 : 잘 나오긴 하는데, 가끔 터짐. 왠지 한글쓸때 잘 터지는것 같은데 다시 실행해 보면 잘 될때가 있음.
                * */

                //span.lowest em.price_num(확실치 않음)
                /*
                * 리스트 형태 말고 갤러리 형태로 나오는 아이템들은 값이 안 나옴. parsing할 태그를 바꿔야할듯.
                * */
                Elements titles= doc.select("div.info_price em.num" );
                for(Element e: titles){
                    System.out.println("title: " + e.text());
                    HTMLContentInStringFormat += e.text().trim() + "\n";
                }

            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected  void onPostExecute(Void result){
            Log.d("tag","JsoupAsyncTask.onPostExecute()");
            webParsingOutput.setText(HTMLContentInStringFormat);
            HTMLPageURL = "https://search.naver.com/search.naver?query=";
            HTMLContentInStringFormat = "";
            Log.d("tag","JsoupAsyncTask.onPostExecute()-HTMLPageURL : "+HTMLPageURL);
        }
    }
}