package tw.com.abc.mynetwork;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageView img;
    private Bitmap bmp;
    private UIHander hander;
    private boolean isPermissionOK;
    private File sdroot, savePDF;
    private ProgressDialog progressDialog;
    private String line;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img=(ImageView) findViewById(R.id.img);
        hander=new UIHander();

    }
    // 基本語法展示,驗證要於Thread()
    public void test1(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url=new URL("http://www.tcca.org.tw");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream in=conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    while ((line =br.readLine()) != null){
                        Log.i("geoff",line);
                    }
                    in.close();

                } catch (Exception e) {
                    //e.printStackTrace();
                    Log.i("geoff",e.toString());
                }
            }
        }.start();

    }
  // 抓圖片
    public void test2(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url=new URL("http://www.iii.org.tw/assets/images/information-news/image004.jpg");

                    // URL url=new URL("http://www.tcca.org.tw/t_03.jpg");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream in=conn.getInputStream();

                    // 2. 建立bmp 存放 來自網路取得的圖片
                    bmp = BitmapFactory.decodeStream(in);
                    // 1.放到img 元件中-透過Headle
                    // img.setImageBitmap(bmp);  //不能直接呼叫前景的img
                    // hander.sendEmptyMessage(0);
                    hander.sendEmptyMessage(0);  // 傳入任意數值,主要是給hander 內部的判斷是做來源判斷

                    in.close();

                } catch (Exception e) {
                    //e.printStackTrace();
                    Log.i("geoff",e.toString());
                }
            }
        }.start();

    }

    private class UIHander extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            img.setImageBitmap(bmp);

        }
    }
}

