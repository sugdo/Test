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
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private String HTMLPageURL ="https://search.shopping.naver.com/detail/detail.nhn?nv_mid=18344345265&cat_id=50002334&frm=NVSHMDL&query=아이패드&NaPm=ct%3Djudv66rs%7Cci%3D331156ad866f63753eb23589660f3448f719e6c0%7Ctr%3Dslsl%7Csn%3D95694%7Chk%3Dcadcfc8bb834b33233d569c984375994c62673a8";
    private String HTMLContentInStringFormat="";
    NotificationManager notificationManager;
    PendingIntent intent;

    int someVal=4000;
    int cnt=0;

    EditText edGreenBox;
    TextView tvPinkBox;
    Button buYellowBox;
    TextView tvBlueBox;
    Button buPurpleBox;
    ImageButton timerImage;
    TextView tvRedBox;

    public int getSomeVal(){
        return someVal;
    }

    public void setSomeVal(int someVal) {
        this.someVal = someVal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edGreenBox = findViewById(R.id.edGreenBox);
        tvPinkBox = findViewById(R.id.tvPinkBox);
        buYellowBox = findViewById(R.id.buYellowBox);
        tvBlueBox = findViewById(R.id.tvBlueBox);
        buPurpleBox = findViewById(R.id.buPurpleBox);
        timerImage = findViewById(R.id.timerImage);
        tvRedBox = findViewById(R.id.tvRedBox);


        tvBlueBox.setMovementMethod(new ScrollingMovementMethod());
        intent = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        //시계 오래 클릭하면 메뉴달고
        registerForContextMenu(timerImage);

        //웹파싱버튼
        buYellowBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String whatUserSaid = edGreenBox.getText().toString();
                Log.d("태그",whatUserSaid);
                //왠지 이거는 안 된다.
                //tvPinkBox.setText(whatUserSaid);
                System.out.println((cnt+1)+"번쨰 파싱");
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
                cnt++;
            }
        });

        //알림버튼
        buPurpleBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification();
            }
        });

        TimerTask tt2 = new TimerTask() {
            @Override
            public void run() {
                Log.d("태그","현재 someVal : "+someVal);
            }
        };
        Timer timer2 = new Timer();
        timer2.schedule(tt2,0,1000);

        TimerTask tt = new TimerTask(){
            @Override
            public void run(){
                Log.d("태그","타이머가 Run을 실행함.");
                buYellowBox.callOnClick();
            }
        };
        Timer timer = new Timer();
        timer.schedule(tt, 10000, getSomeVal());
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
            tvBlueBox.setText(HTMLContentInStringFormat);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,v,menuInfo);
        menu.setHeaderTitle("타이머 설정");
        menu.add(0,1,1,"5초");
        menu.add(0,2,2,"1분");
        menu.add(0,3,3,"10분");
        menu.add(0,4,4,"1시간");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        int choice = item.getItemId();
        if(choice == 1){
            setSomeVal(5000);
        }
        if(choice == 2){
            setSomeVal(60000);
        }
        if(choice == 3){
            setSomeVal(600000);
        }
        if(choice == 4){
            setSomeVal(3600000);
        }
        Toast.makeText(this,"someVal : "+someVal,Toast.LENGTH_SHORT ).show();
        String s = Integer.toString(someVal);
        tvRedBox.setText(s);
        return true;
    }
}