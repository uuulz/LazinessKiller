package whu.iss.lz.lazinesskiller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

/**
 * Created by Administrator on 2016/6/2.
 */
public class TimerClass  extends AppCompatActivity {
        private int recLen;
        private int second;
        private int minute;
        private int totalSec;
        private float rate;
        private String m,s;
        private TextView txtView;
        private Button stopBtn;
    private String currentTask;
    private waterView waterView;

    public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            setContentView(R.layout.timer);
            txtView = (TextView)findViewById(R.id.currentWork);
            waterView = (waterView)findViewById(R.id.waterView);
            stopBtn = (Button)findViewById(R.id.stopBtn);

        SharedPreferences sharedPreferences= getSharedPreferences("share", Activity.MODE_PRIVATE);
        currentTask =sharedPreferences.getString("cTask", "");

        txtView.setText("Current task:" + currentTask);
        if(currentTask == "") {
            txtView.setText("Current task:None");
            currentTask = "Unnamed work";
        }
            Intent callingIntent = getIntent();
            Bundle myBundle = callingIntent.getExtras();
            totalSec = 60 * myBundle.getInt("minute");
        if(totalSec == 0)
            totalSec = 10;
            recLen = totalSec;
            waterView.setFlowNum("Start!");
            waterView.setmWaterLevel(1F);
            waterView.startWave();
            handler.postDelayed(runnable, 1000);

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder bd = new android.app.AlertDialog.Builder(TimerClass.this);
                bd.setTitle("Abort!");
                bd.setMessage("Abort halfway?");
                bd.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent ret = new Intent(TimerClass.this, MainActivity.class);
                        startActivity(ret);
                        TimerClass.this.finish();
                    }
                });
                bd.setNegativeButton("No", null);
                bd.show();
            }
        });
        }

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                recLen--;
                if(recLen >= 0){
                minute = recLen / 60;
                second = recLen % 60;

                m = Integer.toString(minute);
                s = Integer.toString(second);
                if(minute < 10)
                    m = "0" + minute;
                if(second < 10)
                    s = "0" + second;
                waterView.setFlowNum(m + ":" + s);
                rate = (float)recLen / totalSec;
                waterView.setmWaterLevel(rate);
                handler.postDelayed(this, 1000);
                }else{
                    recordData(currentTask ,totalSec / 60);
                    Intent alert = new Intent(TimerClass.this,alertClass.class);
                    Bundle data = new Bundle();
                    data.putInt("minute", totalSec / 60) ;
                    alert.putExtras(data);
                    startActivity(alert);
                    TimerClass.this.finish();
                }
            }

        };
    private void recordData(String str,int m){
        SQLiteDatabase db;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.US);
        String storagePath = Environment.getExternalStorageDirectory().getPath();
        String myDbPath = storagePath + "/" + "tomato";
        try {
            db = SQLiteDatabase.openDatabase(myDbPath, null,
                    SQLiteDatabase.CREATE_IF_NECESSARY);
            db.beginTransaction();
            try {
                db.execSQL("create table tomato(" +
                        "time text primary key," +
                        "title text," +
                        "duration text," +
                        "isDone text);");
                db.setTransactionSuccessful();
            } catch (SQLException sqle) {
            } finally {
                db.endTransaction();
            }
            db.beginTransaction();
            try {
                db.execSQL("insert into tomato values" +
                        "('" + sdf.format(new Date()) +
                        "','" +str +
                        "','" +m +"minutes" +
                        "','" +"Done" +
                        "')");
                db.setTransactionSuccessful();
            }catch(SQLException sqle) {
                Log.e("insert", "insert error:" + sqle.getMessage());
            }finally {
                db.endTransaction();
            }
            db.close();
        } catch (SQLiteException e) {

        }
    }
}
