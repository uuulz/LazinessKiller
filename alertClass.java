package whu.iss.lz.lazinesskiller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;

/**
 * Created by Administrator on 2016/6/3.
 */
public class alertClass extends AppCompatActivity {
    private int minute;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        minute = myBundle.getInt("minute");
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if(mediaPlayer==null){
            mediaPlayer=MediaPlayer.create(alertClass.this, uri);
            mediaPlayer.start();
        }
        AlertDialog.Builder bd=new AlertDialog.Builder(alertClass.this);
        bd.setTitle("Done");
        bd.setMessage("You worked for " + minute + " minutes!");
        CheckBox cb = new CheckBox(getApplicationContext());
        cb.setText("Current task done");
        bd.setView(cb);
        bd.setPositiveButton("Have some rest(5min)", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mediaPlayer != null && mediaPlayer.isPlaying() == true) {
                    mediaPlayer.stop();
                    mediaPlayer = null;

                    Intent startTimer = new Intent(alertClass.this,TimerClass.class);
                    Bundle selectedTime = new Bundle();
                    selectedTime.putInt("minute", 5);
                    startTimer.putExtras(selectedTime);
                    startActivity(startTimer);
                    alertClass.this.finish();
                }
            }
        });
        bd.setNegativeButton("Continue", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mediaPlayer!=null&&mediaPlayer.isPlaying()==true){
                    mediaPlayer.stop();
                    mediaPlayer=null;
                    Intent ret = new Intent(alertClass.this,MainActivity.class);
                    startActivity(ret);
                    alertClass.this.finish();
                }
            }
        });
        bd.show();
    }

}