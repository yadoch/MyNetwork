package tw.com.abc.mynetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void test1(View view){
        try {
            URL url=new URL("");
            HttpURLConnection conn=(HttpURLConnection) url.getContent();
            conn.getContent();
            InputStream in =conn.getInputStream();
            BufferedReader br= new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine() !=null)){}

        }catch (Exception e){

        }


    }
    public void test2(View view){

    }
    public void test3(View view){

    }
}
