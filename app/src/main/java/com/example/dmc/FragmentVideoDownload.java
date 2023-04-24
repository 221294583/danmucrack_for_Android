package com.example.dmc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabWidget;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FragmentVideoDownload extends Fragment {
    private EditText mEditText;
    private Button mButton;
    private Spinner mSpinner;
    private CheckBox mCheckBox;
    private LinearLayout mLinearLayout;
    private ProgressBar progressBar;
    private TextView progressText;

    private String url;
    private Integer quality;
    private Boolean autoConvert;

    private final int[] qualityList={0,112,80,64,32,16};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_video_download,container,false);
        mEditText=v.findViewById(R.id.video_link);
        mButton=v.findViewById(R.id.video_go);
        mSpinner=v.findViewById(R.id.quality_choice);
        mCheckBox=v.findViewById(R.id.auto_convert);
        mLinearLayout=v.findViewById(R.id.video_download_layout);
        progressBar=v.findViewById(R.id.progress);
        progressText=v.findViewById(R.id.progress_text);
        autoConvert=true;

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getContext(),R.array.quality_choice_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                autoConvert=b;
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                quality=mSpinner.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                quality=0;
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url= String.valueOf(mEditText.getText());
                new dl().execute();
                hideKeyboard(getActivity());
            }
        });

        return v;
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

    private int getIndex(int res){
        for (int i=0;i<qualityList.length;i++){
            if(res==qualityList[i]){
                return i;
            }
        }
        return -1;
    }

    private class dl extends AsyncTask<Void,Void,Void>{

        String fileName;

        @SuppressLint("Range")
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ParseURL parseURL=new ParseURL(url,qualityList,quality,getActivity(),getView());
                ArrayList<Uri> links=parseURL.parse();
                Integer statusCode=parseURL.download(links);
                parseURL.postHandle(statusCode,autoConvert,progressBar,progressText);
            } catch (Exception e) {
                Snackbar.make(getView(),"FAILURE!!!RETRY!!!",5000).show();
                e.printStackTrace();
            }
            return null;
        }
    }
}
