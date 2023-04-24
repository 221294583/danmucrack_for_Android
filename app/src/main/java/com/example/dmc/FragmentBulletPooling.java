package com.example.dmc;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

public class FragmentBulletPooling extends Fragment {

    private EditText mEditText;
    private Button mButton;
    private RecyclerView pool;
    private String url=null;

    private BulletPool mBullets=new BulletPool();

    private BulletAdapter mBulletAdapter;

    private boolean stacked=false;

    private BaseCore bc=BaseCore.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url= getActivity().getIntent().getStringExtra("url");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_bullet_pooling,container,false);
        pool=v.findViewById(R.id.bullet_pooling_r_view);
        pool.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEditText=v.findViewById(R.id.bullet_to_search);
        mButton=v.findViewById(R.id.bullet_search_button);

        //when SEARCH button pressed, return to the basic list;else back to the previous activity
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (stacked){
                    UpdateUI(mBullets);
                    stacked=false;
                }else {
                    this.remove();
                    requireActivity().onBackPressed();
                }
            }
        });

        //set the search button method;
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stacked=true;
                BulletPool nbp=mBullets.find(String.valueOf(mEditText.getText()));
                UpdateUI(nbp);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View temp = getActivity().getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (temp == null) {
                    temp = new View(getActivity());
                }
                imm.hideSoftInputFromWindow(temp.getWindowToken(), 0);
            }
        });

        new ParsingURL().execute();
        return v;
    }

    //update recyclerview
    private void UpdateUI(BulletPool bulletPool){
        mBulletAdapter=new BulletAdapter(bulletPool);
        System.out.println("updating");
        System.out.println(mBulletAdapter.getItemCount());
        pool.setAdapter(mBulletAdapter);
    }

    private class BulletHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView BulletContent,BulletSender;
        private Bullet mBullet;

        public BulletHolder(LayoutInflater inflater,ViewGroup parent) {
            super(inflater.inflate(R.layout.bullet_item_view,parent,false));
            BulletContent=itemView.findViewById(R.id.bullet_content);
            BulletSender=itemView.findViewById(R.id.bullet_send_uid_before);
            itemView.setOnClickListener(this);
        }

        public void bind(Bullet bullet){
            mBullet=bullet;
            BulletContent.setText(mBullet.getComment());
            BulletSender.setText(mBullet.getUid());
        }

        @Override
        public void onClick(View view) {
            ArrayList<String> temp=new ArrayList<>();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                temp=bc.initBaseCore(mBullet.getUid());
            }
            ArrayList<UID> buffer=new ArrayList<>();
            for (String s:temp){
                System.out.println(s);
                buffer.add(new UID(s));
            }
            System.out.println(buffer.size());
            Popup popup=new Popup(buffer,mBullet.getComment());
            popup.show(((AppCompatActivity)getContext()).getSupportFragmentManager(),"popup");
            Snackbar.make(view,"clicked",500).show();
        }
    }

    private class BulletAdapter extends RecyclerView.Adapter<BulletHolder>{

        private BulletPool mBulletPool;

        public BulletAdapter(){
            mBulletPool=mBullets;
        }

        public BulletAdapter(BulletPool bp){
            mBulletPool=bp;
        }

        @NonNull
        @Override
        public BulletHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new BulletHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BulletHolder holder, int position) {
            Bullet bullet=mBulletPool.get(position);
            holder.bind(bullet);
        }

        @Override
        public int getItemCount() {
            return mBulletPool.size();
        }
    }

    private class ParsingURL extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document document=Jsoup.connect(url).get();
                //System.out.println(document.toString());
                Matcher m= Pattern.compile("(window\\.__INITIAL_STATE__=\\{[\\s|\\S]+?\\});\\(function").
                        matcher(document.html());
                m.find();
                String m_re=m.group();
                m_re=m_re.replaceFirst("window\\.__INITIAL_STATE__=","{\"window\\.__INITIAL_STATE__\":");
                m_re=m_re.substring(0,m_re.length()-10);
                m_re=m_re+"}";
                m_re.replace("\\s+","");

                JSONObject info=new JSONObject(m_re);
                Integer cid=info.getJSONObject("window.__INITIAL_STATE__").getJSONObject("videoData").getInt("cid");
                document=Jsoup.connect(String.format("https://comment.bilibili.com/%1$s.xml",cid.toString())).get();
                document=Jsoup.parse(document.html(),Parser.xmlParser());
                for (Element e:document.getElementsByTag("d")) {
                    mBullets.add(new Bullet(e.attr("p"),e.text()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            UpdateUI(mBullets);
        }
    }
}