package whu.iss.lz.lazinesskiller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/6/22.
 */
public class aboutClass extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        Toast.makeText(getApplicationContext(), "Developed by Lai Zheng", Toast.LENGTH_LONG).show();

    }
}
