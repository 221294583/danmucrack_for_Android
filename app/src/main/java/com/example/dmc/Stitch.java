package com.example.dmc;

import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.FFprobeKit;
import com.arthenica.ffmpegkit.FFprobeSession;
import com.arthenica.ffmpegkit.Log;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.Statistics;
import com.arthenica.ffmpegkit.StatisticsCallback;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class Stitch {

    private String videoTrack;
    private String soundTrack;
    private String outputPath;
    private ProgressBar progress;
    private TextView progressText;

    public Stitch(String videoTrack,String soundTrack,String outputPath,ProgressBar progressBar,TextView textView){
        this.videoTrack=videoTrack;
        this.soundTrack=soundTrack;
        this.outputPath=outputPath;
        this.progress=progressBar;
        this.progressText=textView;
    }

    public void sew(View v){
        String commandCount=String.format("-v error -select_streams v:0 -count_packets -show_entries stream=nb_read_packets -of csv=p=0 %1$s"
                ,videoTrack);
        FFprobeSession ffprobe= FFprobeKit.execute(commandCount);
        String framesString=ffprobe.getOutput();
        Integer frames=Integer.valueOf(framesString.trim());
        System.out.println("frames:"+frames);
        Integer[] fold={0,1,2,3,4,5,7,8,9};
        String commandSew=String.format("-i %1$s -i %2$s -y %3$s",this.videoTrack,this.soundTrack,this.outputPath);

        FFmpegSessionCompleteCallback endCallback=new FFmpegSessionCompleteCallback() {
            @Override
            public void apply(FFmpegSession session) {
                progress.setProgress(100);
                progressText.setText("DONE");
                Snackbar.make(v,String.format("the converted file is saved as %1$s",outputPath),5000).show();
                try {
                    Thread.sleep(5000);
                    progress.setVisibility(View.INVISIBLE);
                    progressText.setVisibility(View.INVISIBLE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        LogCallback logCallback=new LogCallback() {
            @Override
            public void apply(Log log) {
            }
        };

        StatisticsCallback statisticsCallback=new StatisticsCallback() {
            @Override
            public void apply(Statistics statistics) {
                int frameTemp=statistics.getVideoFrameNumber();
                if (frameTemp>((progress.getProgress()/10)*(frames/10.0))&&frameTemp<frames){
                    System.out.println("--------------");
                    System.out.println(statistics);
                    System.out.println(progress.getProgress());
                    progress.setProgress((progress.getProgress()/10+1)*10);
                    progressText.setText(String.format("%1$d%%",progress.getProgress()));
                }
            }
        };

        FFmpegSession ffmpeg= FFmpegKit.executeAsync(commandSew,endCallback,logCallback,statisticsCallback);
        List<Statistics> l=ffmpeg.getAllStatistics();
        for (Statistics s:l){
            System.out.println("_________________");
            System.out.println(s);
        }
    }
}
