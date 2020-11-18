package com.example.danmucrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@SuppressWarnings("ALL")
public class ShowAll extends AppCompatActivity {
    private static final String TAG = "ShowAll";

    private ArrayList<Bullet> all_bullet=new ArrayList<>();
    private String url_to_vid;
    private String url_to_comment;
    private String url_type;
    private int episode;

    private ArrayList<Bullet> result_pool=new ArrayList<>();
    private String keyword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);
        Log.d(TAG, "onCreate: started");

        Intent intent=getIntent();
        url_to_vid=intent.getStringExtra("URL");
        url_type=intent.getStringExtra("type");
        episode=intent.getIntExtra("episode",1);

        new getRequest().execute();
        makeView(all_bullet);

        Button temp_button=(Button) findViewById(R.id.go_search_comment);
        EditText temp_to_search=(EditText) findViewById(R.id.search_keywords);
        temp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp_keyword=temp_to_search.getText().toString();
                StringBuilder sb=new StringBuilder();
                sb.append(".*");
                sb.append(temp_keyword);
                sb.append(".*");
                keyword=sb.toString();

                result_pool.clear();
                new seeThrough().execute();
                hideKeyboard(ShowAll.this);
            }
        });
        Button rewind=(Button) findViewById(R.id.back_to_all_bullets);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeView(all_bullet);
            }
        });

    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void makeView(ArrayList<Bullet> to_show){
        Log.d(TAG, "makeView: build recycler view");

        RecyclerView temp=findViewById(R.id.recycler_view);
        BulletAdapter adapter=new BulletAdapter(to_show,this);
        temp.setAdapter(adapter);
        temp.setLayoutManager(new LinearLayoutManager(this));
    }
    private class getRequest extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpURLConnection temp_connection=null;
            BufferedReader temp_reader=null;

            try{
                /*Document doc=Jsoup.connect(url_to_vid).get();
                String buf=doc.toString();
                System.out.println(buf);*/
                URL temp_url=new URL(url_to_vid);
                temp_connection=(HttpURLConnection) temp_url.openConnection();
                temp_connection.setRequestMethod("GET");
                temp_connection.setConnectTimeout(8000);
                temp_connection.setReadTimeout(8000);
                InputStream temp_stream=temp_connection.getInputStream();
                temp_reader=new BufferedReader(new InputStreamReader(temp_stream));
                StringBuilder temp_builder=new StringBuilder();
                String temp_line;
                while((temp_line=temp_reader.readLine())!=null){
                    temp_builder.append(temp_line);
                }
                String buf=temp_builder.toString();
                switch (url_type){
                    case "video":
                        System.out.println("THIS IS A VIDEO!!!");
                        String beginning="cid";
                        String ending="dimension";
                        String sub=buf.substring(buf.indexOf(beginning)+5,buf.indexOf(ending)-2);
                        StringBuilder u=new StringBuilder();
                        u.append("http://comment.bilibili.com/");
                        u.append(sub.substring(0,sub.indexOf(",")));
                        u.append(".xml");
                        url_to_comment=u.toString();
                        System.out.println("!!!!!!!!!!!"+url_to_comment);
                        break;
                    case "bangumi":
                        System.out.println("THIS IS A BANGUMI");
                        int initial=0;
                        ArrayList<String> map=new ArrayList<>();
                        for (int i=0;i<100;i++){
                            try {
                                map.add(buf.substring(buf.indexOf("cid",initial+3)+5,buf.indexOf(",",buf.indexOf("cid",initial+3))));
                            }
                            catch (Exception e){

                            }
                            initial=buf.indexOf("cid",initial+3);
                            if(initial==-1){
                                break;
                            }
                        }
                        System.out.println(map);
                        StringBuilder sb=new StringBuilder();
                        sb.append("http://comment.bilibili.com/");
                        sb.append(map.get(episode));
                        sb.append(".xml");
                        url_to_comment=sb.toString();
                        System.out.println(episode);
                        System.out.println(url_to_comment);
                        break;
                }
                /*String beginning="cid";
                String ending="dimension";
                String sub=temp_builder.toString().substring(temp_builder.toString().indexOf(beginning)+5,temp_builder.toString().indexOf(ending)-2);
                StringBuilder u=new StringBuilder();
                u.append("http://comment.bilibili.com/");
                u.append(sub.substring(0,sub.indexOf(",")));
                u.append(".xml");
                url_to_comment=u.toString();
                System.out.println("!!!!!!!!!!!"+url_to_comment);*/
            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                String marker=",";
                Document comment_doc=Jsoup.connect(url_to_comment).get();
                Elements comment_elements=comment_doc.select("d");
                for (Element i:comment_elements){
                    all_bullet.add(new Bullet(i.attr("p").substring(i.attr("p").lastIndexOf(marker,i.attr("p").lastIndexOf(marker)-1)+1,i.attr("p").lastIndexOf(marker)),i.text()));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            /*for (Bullet i:all_bullet){
                System.out.println(i.getUid()+i.getComment());
            }*/
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            makeView(all_bullet);
        }
    }
    private class seeThrough extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            for(Bullet i:all_bullet){
                if(Pattern.matches(keyword,i.getComment())){
                    result_pool.add(i);
                }
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            makeView(result_pool);
        }
    }
    /*private void getRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection temp_connection=null;
                BufferedReader temp_reader=null;
                HttpURLConnection buf_connection=null;
                BufferedReader buf_reader=null;
                try{
                    URL temp_url=new URL(url_to_vid);
                    temp_connection=(HttpURLConnection) temp_url.openConnection();
                    temp_connection.setRequestMethod("GET");
                    temp_connection.setConnectTimeout(8000);
                    temp_connection.setReadTimeout(8000);
                    InputStream temp_stream=temp_connection.getInputStream();
                    temp_reader=new BufferedReader(new InputStreamReader(temp_stream));
                    StringBuilder temp_builder=new StringBuilder();
                    String temp_line;
                    while((temp_line=temp_reader.readLine())!=null){
                        temp_builder.append(temp_line);
                    }
                    String beginning="cid";
                    String ending="dimension";
                    String sub=temp_builder.toString().substring(temp_builder.toString().indexOf(beginning)+5,temp_builder.toString().indexOf(ending)-2);
                    StringBuilder u=new StringBuilder();
                    u.append("http://comment.bilibili.com/");
                    u.append(sub.substring(0,sub.indexOf(",")));
                    u.append(".xml");
                    url_to_comment=u.toString();
                    System.out.println("!!!!!!!!!!!"+url_to_comment);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    String marker=",";
                    URL buf_url=new URL(url_to_comment);
                    buf_connection=(HttpURLConnection) buf_url.openConnection();
                    buf_connection.setRequestMethod("GET");
                    buf_connection.setConnectTimeout(8000);
                    buf_connection.setReadTimeout(8000);
                    InputStream buf_stream=buf_connection.getInputStream();
                    buf_reader=new BufferedReader(new InputStreamReader(buf_stream));
                    StringBuilder buf_builder=new StringBuilder();
                    String buf_line;
                    while ((buf_line=buf_reader.readLine())!=null){
                        buf_builder.append(buf_line);
                    }
                    System.out.println(buf_builder.toString());
                    Document comment_doc=Jsoup.parse(buf_builder.toString());
                    Document comment_doc=Jsoup.connect(url_to_comment).get();
                    Elements comment_elements=comment_doc.select("d");
                    for (Element i:comment_elements){
                        all_bullet.add(new Bullet(i.attr("p").substring(i.attr("p").lastIndexOf(marker,i.attr("p").lastIndexOf(marker)-1)+1,i.attr("p").lastIndexOf(marker)),i.text()));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println(all_bullet.size());
                for (Bullet i:all_bullet){
                    System.out.println(i.getUid()+i.getComment());
                }
            }
        }).start();
        makeView();
    }*/
}
