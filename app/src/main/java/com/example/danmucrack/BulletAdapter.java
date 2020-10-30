package com.example.danmucrack;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class BulletAdapter extends RecyclerView.Adapter<BulletAdapter.ViewHolder>{

    private static final String TAG ="BulletAdapter" ;
    private ArrayList<Bullet> all_bullet=new ArrayList<>();
    private Context temp_context;

    private ArrayList<String> all_uid=new ArrayList<>();
    private ArrayList<String> all_space=new ArrayList<>();

    public BulletAdapter(ArrayList<Bullet> all_bullet, Context temp_context) {
        this.all_bullet = all_bullet;
        this.temp_context = temp_context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.general_item_layout,parent,false);
        ViewHolder h=new ViewHolder(v);
        return h;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.uid.setText(all_bullet.get(position).getUid());
        holder.comment.setText(all_bullet.get(position).getComment());
        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked");

                String space_root="https://space.bilibili.com/";
                all_space.clear();
                all_uid.clear();

                Toast.makeText(temp_context,all_bullet.get(position).getUid(),Toast.LENGTH_SHORT);
                BaseCore bc=new BaseCore();
                System.out.println(all_bullet.get(position).getUid());
                all_uid=bc.initBaseCore(all_bullet.get(position).getUid());
                for(String i:all_uid){
                    all_space.add(space_root+i);
                }
                Popup popup=new Popup(all_uid,all_space);
                popup.show(((AppCompatActivity)temp_context).getSupportFragmentManager(),"popup");
            }
        });
    }
    private class getInfo extends AsyncTask<Void,Void,String> {

        String portal;
        String name;

        public getInfo(String portal) {
            this.portal = portal;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Document doc=Jsoup.connect(portal).get();
                Elements ele=doc.select("meta[content]");
                Element sin=ele.get(ele.size()-1);
                name=sin.toString().substring(sin.toString().indexOf("content")+9,sin.toString().indexOf("，"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: the real count"+all_bullet.size());

        return all_bullet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView uid;
        TextView comment;
        RelativeLayout parent_layout;
        public ViewHolder(View itemView) {
            super(itemView);
            uid=itemView.findViewById(R.id.bullet_uid);
            comment=itemView.findViewById(R.id.bullet_comment);
            parent_layout=itemView.findViewById(R.id.parent_layout);
        }
    }
}
