package com.example.danmucrack;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.ViewHolder>{

    private ArrayList<String> uid=new ArrayList<>();
    private ArrayList<String> space=new ArrayList<>();
    private Context my_context;

    public PopupAdapter(ArrayList<String> uid, ArrayList<String> space, Context my_context) {
        this.uid = uid;
        this.space = space;
        this.my_context = my_context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_item_layout,parent,false);
        ViewHolder h=new ViewHolder(v);
        return h;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.uid.setText(uid.get(position));
        holder.space.setText(Html.fromHtml(space.get(position)));
    }

    @Override
    public int getItemCount() {
        return uid.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView uid;
        TextView space;
        RelativeLayout popup_parent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uid=itemView.findViewById(R.id.popup_uid);
            space=itemView.findViewById(R.id.popup_space);
            popup_parent=itemView.findViewById(R.id.parent_popup);
        }
    }
}
