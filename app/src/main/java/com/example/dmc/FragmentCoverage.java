package com.example.dmc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.AsynchronousChannelGroup;

public class FragmentCoverage extends Fragment {
    private EditText mEditText;
    private Button mButton;
    private String url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_coverage,container,false);
        mEditText=v.findViewById(R.id.coverage_link);
        mButton=v.findViewById(R.id.coverage_go);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url= String.valueOf(mEditText.getText());
                hideKeyboard(getActivity());
                new DownloadCoverage().execute();
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

    private class DownloadCoverage extends AsyncTask<Void,Void,Void>{
        String s;

        @Override
        protected Void doInBackground(Void... voids) {
            Bitmap bitmap=null;
            try {
                Document doc=Jsoup.connect(url).get();
                System.out.println(doc);
                Elements temp=doc.select("head > meta[itemprop=image]");
                Element imageLinkAttr=temp.first();
                String imageLink=imageLinkAttr.attr("content");
                imageLink=imageLink.substring(0,imageLink.lastIndexOf("@"));
                imageLink="https:"+imageLink;

                InputStream input=new java.net.URL(imageLink).openStream();
                bitmap= BitmapFactory.decodeStream(input);

                Elements buffer=doc.select("head > title[data-vue-meta]");
                s=buffer.first().text();

                File directory=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File image=new File(directory,s+".jpg");
                System.out.println(image);
                FileOutputStream output=new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,output);

                input.close();
                output.close();
                Log.d("status","done");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Snackbar.make(getView(),s+".jpg SAVED!",2000).show();
        }
    }
}
