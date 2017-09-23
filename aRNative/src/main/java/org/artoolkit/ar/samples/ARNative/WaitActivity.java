package org.artoolkit.ar.samples.ARNative;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by erunn on 2017-09-23.
 */

public class WaitActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wait);

        Button yaloButton = (Button)findViewById(R.id.button7);
//
        yaloButton.setOnClickListener(listener);


    }

    private View.OnClickListener listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()){
                case  R.id.button7 :
                    Intent toTime = new Intent(WaitActivity.this, MainActivity.class);
                    startActivity(toTime);
                    finish();
                    break;

                default:
                    break;
            }
        }
    };
}
