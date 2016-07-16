package whu.iss.lz.lazinesskiller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener{

    Button startBtn;
    NumberPicker minutePicker;
    TextView tip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn = (Button)findViewById(R.id.startBtn);
        minutePicker = (NumberPicker)findViewById(R.id.minutePicker);
        tip = (TextView)findViewById(R.id.tipText);

        SharedPreferences sharedPreferences= getSharedPreferences("share", Activity.MODE_PRIVATE);
        String currentTask =sharedPreferences.getString("cTask", "");
        tip.setText("Current task:" + currentTask);

        if(currentTask == "")
            tip.setText("You can select a task from the task list!");

        String[] gaps = {"0","5","10","15","20","25","30"};
        minutePicker.setDisplayedValues(gaps);
        minutePicker.setMaxValue(gaps.length - 1);
        minutePicker.setMinValue(0);
        minutePicker.setValue(0);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.inflateMenu(R.menu.my_menu);
        toolBar.setTitle("Laziness killer");
        toolBar.setLogo(R.drawable.tomatoes);
        toolBar.setOnMenuItemClickListener(this);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int minute = Integer.parseInt(minutePicker.getDisplayedValues()[minutePicker.getValue()]);
                Intent startTimer = new Intent(MainActivity.this,TimerClass.class);
                Bundle selectedTime = new Bundle();
                selectedTime.putInt("minute", minute);
                startTimer.putExtras(selectedTime);
                startActivity(startTimer);
                MainActivity.this.finish();
            }
        });
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_history) {
            Intent intent = new Intent(MainActivity.this, DisplayHistoryRecords.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_task) {
            Intent intent = new Intent(MainActivity.this, DisplayUndoneRecords.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, aboutClass.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_add) {
            android.app.AlertDialog.Builder bd = new android.app.AlertDialog.Builder(MainActivity.this);
            bd.setTitle("Create task");
            bd.setMessage("Enter the name of the task:");
            final EditText et = new EditText(MainActivity.this);
            bd.setView(et);
            bd.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    recordUndone(et.getText().toString());
                }
            });
            bd.setPositiveButton("Cancel", null);
            bd.show();
            return true;
        }
        return false;
    }
    public void recordUndone(String s){
        SQLiteDatabase db;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.US);
        String storagePath = Environment.getExternalStorageDirectory().getPath();
        String myDbPath = storagePath + "/" + "undone";
        try {
            db = SQLiteDatabase.openDatabase(myDbPath, null,
                    SQLiteDatabase.CREATE_IF_NECESSARY);
            db.beginTransaction();
            try {
                db.execSQL("create table undone(" +
                        "title text" +
                        ");");
                db.setTransactionSuccessful();
            } catch (SQLException sqle) {
            } finally {
                db.endTransaction();
            }
            db.beginTransaction();
            try {
                db.execSQL("insert into undone values" +
                        "('"  +s +
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
