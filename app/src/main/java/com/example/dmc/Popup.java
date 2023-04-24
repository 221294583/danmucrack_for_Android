package com.example.dmc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Popup extends BottomSheetDialogFragment {
    private ArrayList<UID> mUIDs=new ArrayList<>();
    private String comment;
    private PopupAdapter mPopupAdapter;
    private RecyclerView recyclerView;

    public Popup(ArrayList<UID> u,String s){
        mUIDs=u;
        comment=s;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.popup_view,container,false);
        recyclerView=v.findViewById(R.id.popup_recyclerview);
        TextView popupComment=v.findViewById(R.id.popup_bullet_content);
        popupComment.setText(comment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI(mUIDs);
        new Validation(mUIDs).execute();
        return v;
    }

    private void updateUI(ArrayList<UID> us){
        mPopupAdapter=new PopupAdapter(us);
        recyclerView.setAdapter(mPopupAdapter);
    }

    private class PopupHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{
        //declare variables
        TextView popupUID,popupLINK;

        public PopupHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.popup_item_view,parent,false));
            //bind widgets and variables
            popupUID=itemView.findViewById(R.id.popup_uid);
            popupLINK=itemView.findViewById(R.id.popup_link);
            //itemView.setOnClickListener();
        }
        public void bind(UID u){
            //setText
            popupUID.setText(u.getUID());
            popupLINK.setText(u.getLINK());
        }
    }
    private class PopupAdapter extends RecyclerView.Adapter<PopupHolder>{
        private List<UID> temp;

        public PopupAdapter(List<UID> u){
            temp=u;
        }

        @NonNull
        @Override
        public PopupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new PopupHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PopupHolder holder, int position) {
            //Class className=ClassListName.get(position)
            UID buffer=temp.get(position);
            holder.bind(buffer);
        }

        @Override
        public int getItemCount() {
            return temp.size();//ClassList.size()
        }
    }

    private class Validation extends AsyncTask<Void,Void,Void>{
        private ArrayList<UID> toValid;
        private ArrayList<UID> afterValid;

        public Validation(ArrayList<UID> u){
            toValid=u;
            afterValid=new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                for (UID u:toValid){
                    Document document=Jsoup.connect("https://space.bilibili.com/"+u.getUID()).get();
                    Elements name=document.select("body > title");
                    if (!name.isEmpty()){
                        Element firstName=name.first();
                        String NameContent=firstName.text();
                        if (NameContent.lastIndexOf("的个人空间")!=0){
                            afterValid.add(new UID(NameContent.substring(0,NameContent.lastIndexOf("的个人空间")),u.getLINK()));
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            updateUI(afterValid);
        }
    }
}
