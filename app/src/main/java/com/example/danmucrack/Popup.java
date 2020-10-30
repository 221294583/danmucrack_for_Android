package com.example.danmucrack;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class Popup extends BottomSheetDialogFragment {

    ArrayList<String> uid;
    ArrayList<String> space;

    public Popup(ArrayList<String> uid,ArrayList<String> space) {
        this.uid = uid;
        this.space=space;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.popup_layout,container,false);

        RecyclerView temp_recycler=v.findViewById(R.id.popup_recycler_view);
        PopupAdapter adapter=new PopupAdapter(uid,space,getContext());
        temp_recycler.setAdapter(adapter);
        temp_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }
}
