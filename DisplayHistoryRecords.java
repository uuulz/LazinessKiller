package whu.iss.lz.lazinesskiller;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/3.
 */

public class DisplayHistoryRecords extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener {

    SQLiteDatabase db;
    ListView recordListView;
    ArrayList<String> record = new ArrayList<>();
    Intent caller;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.record_layout);
        caller = getIntent();
        //toolbar
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("Previous records");
        toolBar.inflateMenu(R.menu.record_menu);
        toolBar.setLogo(R.drawable.tomatoes);
        toolBar.setOnMenuItemClickListener(this);

        recordListView = (ListView)findViewById(R.id.recordListView);
        recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> _av, View _v,
                                    int _index, long _id) {
                String st = record.get(_index).split(" ")[0];
                deleteRecord(st);
                Toast.makeText(getApplicationContext(), "Task deleted！",Toast.LENGTH_SHORT).show();
            }
        });
        String storagePath = Environment.getExternalStorageDirectory().getPath();
        String myDbPath = storagePath + "/" + "tomato";
        this.setTitle("History");
        try {
            openDatabase();
            getRecords();
            db.close();
            ArrayAdapter<String> adapterRecords =
                    new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, record);
            recordListView.setAdapter(adapterRecords);
        }catch (Exception e){
            Log.e("record", e.getMessage());
            finish();
        }
    }


    private void deleteRecord(String str){
        SQLiteDatabase db = null;
        String storagePath = Environment.getExternalStorageDirectory().getPath();
        String myDbPath = storagePath + "/" + "tomato";
        try {
            db = SQLiteDatabase.openDatabase(myDbPath, null,
                    SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL("delete from tomato where time='" +str + "');");
            db.setTransactionSuccessful();
        }catch(SQLException sqle) {
            Log.e("delete", "delete error:" + sqle.getMessage());
        }finally {
            db.endTransaction();
        }
        db.close();
    }

    public void openDatabase(){
        String storagePath = Environment.getExternalStorageDirectory().getPath();
        String myDbPath = storagePath  + "/" + "tomato";
        try{
            db = SQLiteDatabase.openDatabase(myDbPath,null,SQLiteDatabase.CREATE_IF_NECESSARY);
        }catch (SQLiteException sqle){
            Log.e("record",sqle.getMessage());
            finish();
        }
    }

    public void getRecords(){
        try {
            String mySQL = "select * from tomato";
            Cursor c = db.rawQuery(mySQL,null);
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                String cursorRow = new String();
                for (int i = 0; i < c.getColumnCount(); i++) {
                    cursorRow += c.getString(i);
                    if (i<c.getColumnCount()-1)
                        cursorRow += "\n";
                }
                Log.e("cursor",cursorRow);
                record.add(cursorRow);
            }
        } catch (Exception e) {
            Log.e("record",e.getMessage());
            finish();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Toast.makeText(getApplicationContext(), "Developed by Lai Zheng", Toast.LENGTH_LONG).show();
            return true;
        }
        else if (id == R.id.action_back) {
            Intent intent = new Intent(DisplayHistoryRecords.this, caller.getClass());
            startActivity(intent);
            return true;
        }
        return false;
    }
}
