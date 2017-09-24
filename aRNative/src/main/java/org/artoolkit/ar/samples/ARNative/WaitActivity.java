package org.artoolkit.ar.samples.ARNative;

import android.app.Activity;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.net.URISyntaxException;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by erunn on 2017-09-23.
 */

public class WaitActivity extends AppCompatActivity {
    private Socket mSocket; //소켓 연결
    {
        try {
            mSocket = IO.socket("http://220.230.119.61:3000"); // 소켓 서버 주소
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    private TextView mTextView; // 메세지 수신확인 텍스트 뷰
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wait);

        Button yaloButton = (Button)findViewById(R.id.button7);
//
        yaloButton.setOnClickListener(listener);

        mTextView = (TextView)findViewById(R.id.leftTime);


        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE); // 내 ip 주소 받아오는부분
        DhcpInfo dhcpInfo = wm.getDhcpInfo() ;
        int serverIp = dhcpInfo.gateway;

        String ipAddress = String.format(
                "%d.%d.%d.%d",
                (serverIp & 0xff),
                (serverIp >> 8 & 0xff),
                (serverIp >> 16 & 0xff),
                (serverIp >> 24 & 0xff));

        mSocket.emit("join",ipAddress); // 소켓 서버에 조인 메세지 보내기
        mSocket.on("msg", onNewMessage); // 소켓 서버에서 메세지 수신
        mSocket.connect(); // 소켓 연결

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

    private Emitter.Listener onNewMessage = new Emitter.Listener() { //소켓 리스너 설정
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    Log.d("data", data.toString());
                    try {
                        username = data.getString("nickname");
                        message = data.getString("msg");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }

                    showMessage(username, message);
                }
            });

        }
    };

    private void showMessage(String username, String message){ //메세지 표시 : 이부분을 가지고 장난치면됨
        mTextView.setText("IP: "+ username + "\n msg : " + message);
        if(message.equals("start"))
            mTextView.setText("start");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }
}
