package com.example.dmc;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseURL {
    private String url;
    private String typeMark;
    private String fileName;
    private int[] qualityList;
    private int quality;
    private FragmentActivity fa;
    private View v;
    private File directory;
    private ArrayList<String> fileNames=new ArrayList<>();

    public ParseURL(String s, int[] ql, int q, FragmentActivity fa, View v){
        this.url=s;
        this.qualityList=ql;
        this.quality=q;
        this.fa=fa;
        this.v=v;
        if (this.url.contains("bilibili")||this.url.contains("b23.tv")){
            this.typeMark="bilibili";
        }
    }

    private void makeDirectory(){
        this.directory=new File(Environment.getExternalStorageDirectory()+
                String.format("/Download/%1$sDownload/",typeMark));

        if (!this.directory.exists()){
            System.out.println(directory+" doesn't exist!");
            boolean success=directory.mkdirs();
            System.out.println(success);
        }
    }

    public ArrayList<Uri> parse() throws IOException, JSONException, NoSuchAlgorithmException {
        switch (typeMark){
            case "bilibili":
                return this.parseBilibili();
        }
        return null;
    }

    private ArrayList<Uri> parseBilibili() throws IOException, JSONException, NoSuchAlgorithmException {
        Document document= Jsoup.connect(url).get();
        Elements fName=document.select("head > title");
        fileName=fName.first().text();
        Matcher m= Pattern.compile("(window\\.__INITIAL_STATE__=\\{[\\s|\\S]+?\\});\\(function").
                matcher(document.html());
        m.find();
        String m_re=m.group();

        m_re=m_re.replaceFirst("window\\.__INITIAL_STATE__=","{\"window\\.__INITIAL_STATE__\":");
        m_re=m_re.substring(0,m_re.length()-10);
        m_re=m_re+"}";
        m_re.replace("\\s+","");

        JSONObject info=new JSONObject(m_re);
        Integer cid=null;
        if (url.contains("bangumi")){
            Pattern pattern=Pattern.compile("(ep)(\\d+)\\?");
            Matcher matcher=pattern.matcher(url);
            matcher.find();
            Integer cidEP=Integer.valueOf(matcher.group(2));
            JSONObject wis=info.getJSONObject("window.__INITIAL_STATE__");
            JSONObject mediaInfo=wis.getJSONObject("mediaInfo");
            JSONArray episodes=mediaInfo.getJSONArray("episodes");
            for (int i=0;i<episodes.length();i++){
                if (cidEP==episodes.getJSONObject(i).getInt("id")){
                    cid=episodes.getJSONObject(i).getInt("cid");
                }
            }
        }else {
            cid=info.getJSONObject("window.__INITIAL_STATE__").getJSONObject("videoData").getInt("cid");
        }
        ArrayList<String> urlList=new ArrayList<>();
        for (int i=0;i<qualityList.length;i++){
            urlList.add(InterfaceBili.interfaceLink(cid,qualityList[i==0 ? 1 : i]));
        }

        Matcher matcher=Pattern.compile("(window\\.__playinfo__=\\{[\\s|\\S]+?\\})</script>").
                matcher(document.html());
        matcher.find();
        String matcher_re=matcher.group();
        matcher_re=matcher_re.replaceFirst("window\\.__playinfo__=","{\"window\\.__playinfo__\":");
        matcher_re=matcher_re.substring(0,matcher_re.length()-9);
        matcher_re=matcher_re+"}";
        matcher_re.replace("\\s+","");
                /*Elements elements=document.select("head");
                Elements elements1=elements.select("head > style#setSizeStyle + script");
                Element element=elements1.first();*/
                /*String buffer=element.html();
                buffer=buffer.replaceFirst("window.__playinfo__=","{\"window.__playinfo__\":");
                buffer=buffer+"}";*/

        JSONObject playInfo=new JSONObject(matcher_re);
        playInfo=playInfo.getJSONObject("window.__playinfo__");
        playInfo=playInfo.getJSONObject("data");
        playInfo=playInfo.getJSONObject("dash");
        JSONArray linkStorage=playInfo.getJSONArray("video");
        boolean mark=true;
        int count=0;
        while (mark){
            try {
                JSONObject s=linkStorage.getJSONObject(count);
                int res=s.getInt("id");
                String link=s.getString("base_url");
                System.out.println("!!!!!:"+res+link);
                if (link.contains("hdnts")){
                    urlList.set(getIndex(res),link);
                }
            }catch (Exception e){
                mark=false;
                e.printStackTrace();
            }
            count++;
        }
        for (int i=0;i<urlList.size();i++){
            System.out.println(qualityList[i]+":"+urlList.get(i));
        }
        for (int i=0;i<urlList.size();i++){
            if (urlList.get(i).substring(0,20).contains("interface")){
                        /*
                        Document temp=Jsoup.connect(urlList.get(i)).ignoreContentType(true).get();
                        Elements elements2=temp.select("body");
                        Element element1=elements2.first();
                        String json_temp=element1.html();
                        json_temp=Jsoup.clean(json_temp, Safelist.basic());
                        json_temp= Parser.unescapeEntities(json_temp,true);
                        JSONObject converter=new JSONObject(json_temp);
                        JSONArray durl=converter.getJSONArray("durl");
                        urlList.set(i,durl.getJSONObject(0).getString("url"));*/
                urlList.set(i,"");
            }
        }
        for (int i=0;i<urlList.size();i++){
            System.out.println(qualityList[i]+":"+urlList.get(i));
        }

        Uri finalURL=null;
        for (int i=quality;i<urlList.size();i++){
            if (!urlList.get(i).equals("")){
                finalURL=Uri.parse(urlList.get(i).trim());
                break;
            }
        }
        System.out.println("finalURL"+finalURL.toString());

        linkStorage = playInfo.getJSONArray("audio");
        mark=true;
        count=0;
        String linkAudio=null;
        Uri finalURLAudio=null;
        while (mark){
            try {
                JSONObject audioAt=linkStorage.getJSONObject(count);
                linkAudio=audioAt.getString("baseUrl");
                if (linkAudio.contains("hdnts")){
                    System.out.println("!!!!!AUDIO:"+linkAudio);
                    finalURLAudio=Uri.parse(linkAudio.trim());
                    break;
                }
            }catch (Exception e){
                e.printStackTrace();
                mark=false;
            }
            count++;
        }
        ArrayList<Uri> result=new ArrayList<>();
        result.add(finalURL);
        result.add(finalURLAudio);
        return result;
    }

    public Integer download(ArrayList<Uri> links) throws InterruptedException {
        this.makeDirectory();
        switch (typeMark){
            case "bilibili":
                return this.downloadBilibili(links);
        }
        return null;
    }

    @SuppressLint("Range")
    private Integer downloadBilibili(ArrayList<Uri> links) throws InterruptedException {

        DownloadManager dmVideo=(DownloadManager) fa.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request reqVideo=new DownloadManager.Request(links.get(0));
        DownloadManager.Query queryVideo=new DownloadManager.Query();
        reqVideo.setDestinationUri(Uri.fromFile(new File(directory+"/"+fileName+"v.mp4")));
        //reqVideo.setDestinationInExternalFilesDir(getContext(), String.valueOf(directory),fileName+"v"+".mp4");
        //reqVideo.setDestinationInExternalPublicDir(String.valueOf(directory),fileName+"v"+".mp4");
        reqVideo.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long idVideo=dmVideo.enqueue(reqVideo);
        queryVideo.setFilterById(idVideo);

                /*DownloadManager.Request req=new DownloadManager.Request(finalURL);
                req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"dmctemp.mp4");
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                DownloadManager dm= (DownloadManager) fa.getSystemService(fa.DOWNLOAD_SERVICE);
                dm.enqueue(req);*/
        Snackbar.make(v,"video track is being downloaded!",2000).show();

        Thread.sleep(5000);

        DownloadManager dmAudio= (DownloadManager) fa.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request reqAudio=new DownloadManager.Request(links.get(1));
        DownloadManager.Query queryAudio=new DownloadManager.Query();
        reqAudio.setDestinationUri(Uri.fromFile(new File(directory+"/"+fileName+"s.mp4")));
        //reqAudio.setDestinationInExternalFilesDir(getContext(),String.valueOf(directory),fileName+"s"+".mp4");
        //reqAudio.setDestinationInExternalPublicDir(String.valueOf(directory),fileName+"s"+".mp4");
        reqAudio.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long idAudio=dmAudio.enqueue(reqAudio);
        queryAudio.setFilterById(idAudio);
        Snackbar.make(v,"sound track is being downloaded!",2000).show();

        Integer statusAudio=null;
        boolean finished=false;
        Integer statusCode=0;
        while (!finished){
            Cursor cVideo= dmVideo.query(queryVideo);
            cVideo.moveToFirst();
            Integer statusVideo=null;
            Cursor cAudio=dmAudio.query(queryAudio);
            cAudio.moveToFirst();

            statusVideo=cVideo.getInt(cVideo.getColumnIndex(DownloadManager.COLUMN_STATUS));
            statusAudio=cAudio.getInt(cAudio.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (statusVideo==DownloadManager.STATUS_FAILED||statusAudio==DownloadManager.STATUS_FAILED){
                finished=true;
                statusCode=-1;
            }
            if (statusVideo==DownloadManager.STATUS_SUCCESSFUL&&statusAudio==DownloadManager.STATUS_SUCCESSFUL){
                finished=true;
                statusCode=1;
            }
            cVideo.close();
            cAudio.close();
            Thread.sleep(2000);
            System.out.println(String.format("looped;statusV:%1$d;statusA:%2$d",statusVideo,statusAudio));
        }
        Cursor cVideo= dmVideo.query(queryVideo);
        Cursor cAudio= dmAudio.query(queryAudio);
        cVideo.moveToFirst();
        cAudio.moveToFirst();
        @SuppressLint("Range") String videoTrack=cVideo.getString(cVideo.getColumnIndex(DownloadManager.COLUMN_TITLE));
        @SuppressLint("Range") String audioTrack=cAudio.getString(cAudio.getColumnIndex(DownloadManager.COLUMN_TITLE));
        fileNames.add(videoTrack);
        fileNames.add(audioTrack);
        cVideo.close();
        cAudio.close();
        return statusCode;
    }

    public int postHandle(int statusCode, boolean autoExecution, ProgressBar pb, TextView pt){
        switch (typeMark){
            case "bilibili":
                return this.postHandleBilibili(statusCode,autoExecution,pb,pt);
        }
        return 0;
    }

    public int postHandleBilibili(int statusCode, boolean autoExecution, ProgressBar pb, TextView pt){
        if (statusCode==-1){
            Snackbar.make(v,"some of the download tasks failed!",2000).show();
        }
        if (statusCode==1&&autoExecution){
            Snackbar.make(v,"files will be converted automatically!",2000).show();

                    /*pb pb=new pb(getContext());
                    pb.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                    TextView progressText=new TextView(getContext());
                    progressText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                    */
            fa.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                            /*mLinearLayout.addView(pb);
                            mLinearLayout.addView(progressText);*/
                    pb.setVisibility(View.VISIBLE);
                    pt.setVisibility(View.VISIBLE);
                }
            });

            System.out.println(fileNames.get(0)+";"+fileNames.get(1));
            Stitch stitch=new Stitch(directory+"/"+fileNames.get(0),
                    directory+"/"+fileNames.get(1),
                    directory+"/"+fileName+".mp4",
                    pb,pt);
            stitch.sew(v);
            return 1;
        }
        if (statusCode==1&&(!autoExecution)){
            Snackbar.make(v,"files are downloaded successfully but will not be converted!",2000).show();
        }
        return 0;
    }

    private int getIndex(int res){
        for (int i=0;i<qualityList.length;i++){
            if(res==qualityList[i]){
                return i;
            }
        }
        return -1;
    }
}
