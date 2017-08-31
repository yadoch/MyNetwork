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
import android.support.v4.app.ShareCompat;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.util.List;

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

        // sd 卡事前處理
        if (ContextCompat.checkSelfPermission(this,
                // 要判斷的條件(網路,外部儲存裝置....)
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            // no
            ActivityCompat.requestPermissions(this,
                    // 要判斷的條件(網路,外部儲存裝置....)
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }else {
            isPermissionOK = true;
            init();
        }
    }

    private void init(){
        if (!isPermissionOK) {
            finish();
        }else{
            go();
        }
        //Log.i("brad", "start");
    }

    private void  go(){
        sdroot= Environment.getExternalStorageDirectory();
        //1. 建立progressDialog物件
        progressDialog =new ProgressDialog(this);
        //2. 設定
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Downloading ......");
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
  // 抓圖片-必須透過Hander 把資料傳出
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

    // API展示-網頁轉PDF-檔案匯到電腦出現損壞的狀況,原因不明
    public void test3(View view){
        progressDialog.show();
        new Thread(){
            @Override
            public void run() {
                getWebPDF("http://pdfmyurl.com/?http://tw.yahoo.com");
            }
        }.start();

    }

    private void getWebPDF(String urlString){
        try {
            //2. 準備存入SD卡的檔案名稱
            savePDF=new File(sdroot,"myweb.pdf");
            FileOutputStream fout = new FileOutputStream(savePDF);

            //1.準備URL 連線
            URL url= new URL(urlString);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.connect();

            InputStream in=conn.getInputStream();
            byte[] buf =new byte[4096];
            int len = 0;
            while ((len = in.read(buf)) != -1){

                //3.連接輸出入管子
                Log.i("geoff","Len:"+len);
                fout.write(buf,0,len);
            }
            // flush 到SD的PDF檔案中
            fout.flush();
            fout.close();
            // 傳回 1 中止 progressDialog
            hander.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void test4(View view){
        new Thread(){
            @Override
            public void run() {
                super.run();
                getJSONString("http://opendata2.epa.gov.tw/AQX.json");
            }
        }.start();
    }

    private void getJSONString(String urlString){

        try {
            URL url= new URL(urlString);
            HttpURLConnection conn =(HttpURLConnection) url.openConnection();
            conn.connect();
            // 原始架構 InputStream --> InputStreamReader -->BufferedReader
            /*
            InputStream in = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(inputStreamReader);
            */
            //整合後
            BufferedReader br= new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String jsonString =br.readLine(); // json 的陣列只有一行
            // Log.i("geoff","JSON:"+jsonString);

            parseJSONString(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseJSONString(String json){
        try {
            JSONArray root = new JSONArray(json);
            //Log.i("geoff","Count:"+root.length());
            for (int i=0; i<root.length() ;i++){
                Log.i("geoff","i:"+i);
                //   取得row
                //Log.i("geoff",root.getJSONObject(i).toString());

                JSONObject row = root.getJSONObject(i);
                String country = row.getString("County");
                String siteName = row.getString("SiteName");
                String pm25 = row.getString("PM2.5");
                Log.i("geoff", country + ":" + siteName + ":" + pm25);


            }
        } catch (JSONException e) {
            //e.printStackTrace();
            Log.i("geoff",e.toString());
        }
    }

    public void test5(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.1.1.62/brad.php?cname=roger&tel=0934&birthday=2001-01-09");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    conn.getInputStream();  // 一定要加,才能完成寫入動作
                } catch (Exception e) {
                    //e.printStackTrace();
                    Log.i("geoff",e.toString());
                }
            }
        }.start();
    }

    public void test6(View view){

        new Thread(){
            @Override
            public void run() {
                try {
                    MultipartUtility mu = new MultipartUtility("http://10.1.1.62/brad02.php","UTF-8");
                    mu.addFormField("data1","1111");
                    mu.addFormField("data2","2222");
                    List<String> ret =mu.finish();
                    for (String line : ret ){
                        Log.i("geoff",line);
                    }
                } catch (IOException e) {

                    Log.i("geoff",e.toString());
                }
            }
        }.start();
    }

    private class UIHander extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    img.setImageBitmap(bmp);break;
                case 1:
                    // progressDialog 終止
                    progressDialog.dismiss();break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isPermissionOK = true;

            }
            init();
        }
    }
}

