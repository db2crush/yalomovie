package org.artoolkit.ar.samples.ARNative;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by erunn on 2017-09-23.
 */
class dataSmi {
    long time;
    String text;

    dataSmi(long time, String text) {
        this.time = time;
        this.text = text;
    }

    public long gettime() {
        return time;
    }

    public String gettext() {
        return text;
    }
}


public class MainActivity extends AppCompatActivity implements GyroDetector.ISwingDetectListener{
    private TextView mTextView;
    private GyroDetector mGyroDetector;
    private float degree = 0f;

    final static String SAMPLE_VIDEO_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    VideoView videoView;
    //    SeekBar seekBar;
    Handler updateHandler = new Handler();
    String path;
    private boolean useSmi;
    private int countSmi;
    private ArrayList<dataSmi> parsedSmi;
    private Button btn;
    private CountDownTimer cdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        EditText tvURL = (EditText) findViewById(R.id.etVieoURL);
        tvURL.setText(SAMPLE_VIDEO_URL);

        videoView = (VideoView) findViewById(R.id.videoView);

        mTextView = (TextView)findViewById(R.id.text);
        mGyroDetector = new GyroDetector(this);

        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        } else {
            path = Environment.MEDIA_UNMOUNTED;
        }

        File smiFile = new File(path + "/lyric/lyric.smi");
        if (smiFile.isFile() && smiFile.canRead()) {
            useSmi = true;
            parsedSmi = new ArrayList<dataSmi>();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        new FileInputStream(new File(smiFile.toString())), "MS949"));

                String s;
                long time = -1;
                String text = null;
                boolean smistart = false;

                while ((s = in.readLine()) != null) {
                    if (s.contains("<SYNC")) {
                        smistart = true;
                        if (time != -1) {
                            parsedSmi.add(new dataSmi(time, text));
                        }
                        time = Integer.parseInt(s.substring(s.indexOf("=") + 1, s.indexOf(">")));
                        text = s.substring(s.indexOf(">") + 1, s.length());
                        text = text.substring(text.indexOf(">") + 1, text.length());
                    } else {
                        if (smistart == true) {
                            text += s;
                        }
                    }
                }
                in.close();
            } catch (IOException e) {
                System.err.println(e);
                e.printStackTrace();
            }
        } else {
            useSmi = false;
        }
//        seekBar = (SeekBar) findViewById(R.id.seekBar);
        cdt = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                btn = (Button)findViewById(R.id.ptnLoad);
                btn.performClick();
            }
        };
        cdt.start();
        Toast.makeText(getApplicationContext(), "10초 뒤 자막 재생", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onResume(){
        super.onResume();

        if(mGyroDetector !=null){
            mGyroDetector.startSensor();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGyroDetector != null) {
            mGyroDetector.stopSensor();
        }
    }


    @Override
    public void onChanged(float gz) {
        degree += (gz / 1.75f);

        if(Math.abs(gz) > 0.175f){
            mTextView.setRotation(degree);
            Log.d("gz", String.valueOf(gz));
        }
//         mTextView.setText(String.valueOf(degree));

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_DOWN || (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER)){
            mTextView.setRotation(0);
            degree = 0;
        }
        return super.dispatchKeyEvent(event);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void loadVideo(View view) {
        //Sample video URL : http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_2mb.mp4
        EditText tvURL = (EditText) findViewById(R.id.etVieoURL);
        Button btn = (Button) findViewById(R.id.ptnLoad);
        btn.setVisibility(View.INVISIBLE);
        String url = tvURL.getText().toString();
        Toast.makeText(getApplicationContext(), "1초 뒤 자막 재생..", Toast.LENGTH_LONG).show();
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();

        // 토스트 다이얼로그를 이용하여 버퍼링중임을 알린다.
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {

                                        @Override
                                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                            switch (what) {
                                                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                                    // Progress Diaglog 출력
                                                    Toast.makeText(getApplicationContext(), "1초 뒤 자막 재생..", Toast.LENGTH_LONG).show();
                                                    break;
                                                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                                    // Progress Dialog 삭제
                                                    Toast.makeText(getApplicationContext(), "싱크 조절 완료 \n 재생시작", Toast.LENGTH_LONG).show();
                                                    videoView.start();
                                                    break;
                                            }
                                            return false;
                                        }
                                    }

        );

        // 플레이 준비가 되면, seekBar와 PlayTime을 세팅하고 플레이를 한다.
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                if (useSmi == true) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    Thread.sleep(300);
                                    handler.sendMessage(handler.obtainMessage());

                                }
                            } catch (Throwable t) {

                            }
                        }
                    }).start();
                }
                long finalTime = videoView.getDuration();
                TextView tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);
                tvTotalTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );
//                seekBar.setMax((int) finalTime);
//                seekBar.setProgress(0);
                updateHandler.postDelayed(updateVideoTime, 100);
                //Toast Box
                Toast.makeText(getApplicationContext(), "싱크 조절 완료 \n 자막 재생 시작", Toast.LENGTH_SHORT).show();
                videoView.setAlpha(0f);
            }
        });

    }

    public void playVideo(View view) {
        videoView.requestFocus();
        videoView.start();

    }

    public void pauseVideo(View view) {
        videoView.pause();
    }

    // seekBar를 이동시키기 위한 쓰레드 객체
    // 100ms 마다 viewView의 플레이 상태를 체크하여, seekBar를 업데이트 한다.
    private Runnable updateVideoTime = new Runnable() {
        public void run() {
            long currentPosition = videoView.getCurrentPosition();
//            seekBar.setProgress((int) currentPosition);
            updateHandler.postDelayed(this, 100);

        }
    };

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            countSmi = getSyncIndex(videoView.getCurrentPosition());
            mTextView.setText(Html.fromHtml(parsedSmi.get(countSmi).gettext()));
        }
    };

    public int getSyncIndex(long playTime) {
        int I = 0, m, h = parsedSmi.size();

        while (I <= h) {
            m = (I + h) / 2;
            if (parsedSmi.get(m).gettime() <= playTime && playTime < parsedSmi.get(m + 1).gettime()) {
                return m;
            }
            if (playTime > parsedSmi.get(m + 1).gettime()) {
                I = m + 1;
            } else {
                h = m - 1;
            }
        }
        return 0;
    }


}
