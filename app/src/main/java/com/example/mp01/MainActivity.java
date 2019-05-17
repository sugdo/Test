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
import android.widget.LinearLayout;
import android.widget.TextView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/*
190517 윤승기릿
1. 이제 한글을 입력해도 런타임 에러가 안 뜸.
2. 아예 런타임에러가 안 뜸. 대신, 에러가 난 이유를 보여줌.
3. 앱 내의 논리적 에러 개선, 예를 들면 갤럭시버즈를 갈럭시버즈로 썻을 때.
4. 함께 찾아본 상품, 함께 살만한 상품 출력(오직 갤러리뷰에서만, 리스트뷰에서는 사이트에서 나오지가 않음.)
5. 그로 인한, UI변경. 함께 찾아본 상품,함께 살만한 상품 View 추가, Debug View제거(대신 Logcat을 이용해주세요.)
 */

public class MainActivity extends AppCompatActivity {

    //XML UI 선언
    EditText input;
    Button webParsing;
    TextView webParsingOutput;
    TextView webPasingSubList1;
    TextView webPasingSubList2;

    String HTMLPageURL ="https://search.naver.com/search.naver?ie=utf8&query=";
    String HTMLContentInStringFormat="";
    String subItemList1="";
    String subItemList2="";
    PendingIntent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //XML Inflation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("tag","onCreate()");

        //UI 끌당
        input = findViewById(R.id.input);
        webParsing = findViewById(R.id.webParsing);
        webParsingOutput = findViewById(R.id.webPasingOutputMain);
        webPasingSubList1 = findViewById(R.id.webPasingSubItemList1);
        webPasingSubList2 = findViewById(R.id.webPasingSubItemList2);
        webParsingOutput.setMovementMethod(new ScrollingMovementMethod());
        intent = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        webParsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag","onClick()");
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });
    }

    private class JsoupAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Log.d("tag","onPreExecute()");
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                Log.d("tag","doInBackground()");
                int price=-1;
                int lowPrice=99999999;
                String sample="";
                String sub="";
                String userQuery = input.getText().toString();
                String modifiedKeyword;

                HTMLPageURL+=userQuery;
                Document doc = Jsoup.connect(HTMLPageURL).get();
                Log.d("tag","\nHTMLPageURL(modified) : "+HTMLPageURL);


                //여기부터 리스트뷰, 여기에 걸리면 갤러리뷰엔 걸리지 않습니다.
                //최저가를 따고
                Elements titles= doc.select("div.info_price em.num" );
                //함꼐 ~한 상품을 땁니다.
                Elements subItems = doc.select("ul.product_list li.product_item div.info");
                //파싱한 데이타들을 전처리해주면서, 최저가를 찾습니다.
                for(Element e: titles){
                    sample = e.text();
                    sample = sample.replaceAll("\\,","");
                    price = Integer.parseInt(sample);
                    lowPrice = price;
                }
                //함께 찾아본 상품, 함께 살만한 상품 둘다 각각 5개씩 찾습니다.
                int subCount=0;
                //리스트뷰에서는 함께 찾아본 상품, 함께 살만한 상품을 지원합니다. 갤러리뷰에서는 그렇지 않지요
                for(Element e : subItems){
                    //함께 찾아본 상품
                    if(subCount<5) {
                        sub = e.text();
                        subItemList1+=sub+"\n";
                        subCount++;
                    }
                    //함께 살만한 상품
                    else {
                        sub = e.text();
                        subItemList2+=sub+"\n";
                    }
                }


                //여기가 갤러리뷰, 리스트뷰에 걸렸다면 이곳에 걸리지 않습니다.
                //최저가를 땁니다.
                titles= doc.select("em.price_num" );
                //전처리를 하면서, 최저가를 찾습니다.
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

                //혹시라도 검색어를 잘못 입력했다면, 잘못 입력된 검색어 말고 보통 어떤 올바른 검색어로 검색하는지 찾습니다.
                titles = doc.select("div.sp_keyword dd em");
                modifiedKeyword=titles.text();
                //검색어를 제대로 입력한 경우
                if(modifiedKeyword =="") {
                    Log.d("tag", "ModifiedKeyword(\"\") : " + modifiedKeyword);
                    HTMLContentInStringFormat = userQuery +"로 검색하니 "+lowPrice+"라는 값이 최저가로 나왔습니다.";
                }
                //검색어를 제대로 입력하지 않았지만, 검색하고 싶은 것을 어림 잡을 수 있는 경우
                if(modifiedKeyword != "") {
                    Log.d("tag", "ModifiedKeyword(not \"\") : " + modifiedKeyword);
                    HTMLContentInStringFormat = "검색어가 잘못 된 것 같네요, 하지만 "+modifiedKeyword+"로 검색하니 "+lowPrice+"라는 값이 최저가로 나왔습니다.";
                    if(lowPrice==99999999){
                        HTMLContentInStringFormat = "검색어가 잘못 된 것 같네요, "+modifiedKeyword +"로 검색해 보세요!";
                    }
                }
                //검색어를 제대로 입력하지도 않았고, 무엇을 검색하려는지 알 수 없을 경우.
                if(modifiedKeyword =="" & lowPrice == 99999999){
                    HTMLContentInStringFormat = "검색어가 잘못 된 것 같네요.";
                }

            }
            //Log메시지가 주석을 대신합니다.
            catch(NumberFormatException e001){
                Log.d("tag","이 오류는, 갤러리 뷰에서 가격들의 형식이 일괄적이지 않을때 일어납니다. 누구는 최저99,250원 이렇게 나오고 누구는 그냥 890원 이렇게 나올때지요");
                HTMLContentInStringFormat = "좀 더 구체적인 제품명으로 검색해 주세요";
                subItemList1="시간과 예산이";
                subItemList2="부족하다 이 말이야!";
            }catch(IOException e002){
                Log.d("tag","이 오류는, 인터넷 연결이 원활하지 않을때 일어납니다.");
                HTMLContentInStringFormat = "인터넷 연결이 원활하지 않으면 웹파싱이 어렵습니다.";
                subItemList1="LTE를 쓰던가";
                subItemList2="WIFI를 잡으라 이 말이야!";
            }catch(Exception e){
                Log.d("tag","무슨 에러일까? :"+e);
                HTMLContentInStringFormat = "이 에러는";
                subItemList1="테스트와 디버그를";
                subItemList2="충분히 하지못한 개발자 탓 입니다.";
            }
            return null;
        }

        @Override
        protected  void onPostExecute(Void result){
            //View에 내용을 전달하여 사용자에게 결과를 보여줍니다.
            webParsingOutput.setText(HTMLContentInStringFormat);
            webPasingSubList1.setText(subItemList1);
            webPasingSubList2.setText(subItemList2);
            //변수들을 초기화 시킵니다.
            HTMLPageURL = "https://search.naver.com/search.naver?ie=utf8&query=";
            HTMLContentInStringFormat = "";
            subItemList1="";
            subItemList2="";
        }
    }
}