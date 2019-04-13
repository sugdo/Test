package com.example.mp01;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String HTMLPageURL ="https://search.shopping.naver.com/detail/detail.nhn?nv_mid=18344345265&cat_id=50002334&frm=NVSHMDL&query=%EC%97%90%EC%96%B4%ED%8C%9F2&NaPm=ct%3Djudv66rs%7Cci%3D331156ad866f63753eb23589660f3448f719e6c0%7Ctr%3Dslsl%7Csn%3D95694%7Chk%3Dcadcfc8bb834b33233d569c984375994c62673a8";
    private TextView textViewHTMLDocument;
    private String HTMLContentInStringFormat="";
    NotificationManager notificationManager;
    PendingIntent intent;

    int cnt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewHTMLDocument = (TextView)findViewById(R.id.result);
        textViewHTMLDocument.setMovementMethod(new ScrollingMovementMethod());
        intent = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Button notificationButton = findViewById(R.id.pushNotification);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification();
            }
        });

        Button HTMLTitleButton = (Button)findViewById(R.id.send);
        HTMLTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println((cnt+1)+"번쨰 파싱");
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
                cnt++;
            }
        });
    }

    private  void notification(){
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background) // 아이콘 설정하지 않으면 오류남
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("tryWebParsing") // 제목 설정
                .setContentText("푸시푸시베이베맘을받아줘혜이") // 내용 설정
                .setTicker("위더비이에스티씨스타") // 상태바에 표시될 한줄 출력
                .setAutoCancel(true)
                .setContentIntent(intent);
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private class JsoupAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                Document doc = Jsoup.connect(HTMLPageURL).get();

                Elements titles= doc.select("strong.sjt");

                System.out.println("1-------------------------------------------------------------");
                for(Element e: titles){
                    System.out.println("title: " + e.text());
                    HTMLContentInStringFormat += e.text().trim() + "\n";
                }

                titles= doc.select("span.low_price em.num");
                System.out.println("2-------------------------------------------------------------");
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
            textViewHTMLDocument.setText(HTMLContentInStringFormat);
        }
    }
}