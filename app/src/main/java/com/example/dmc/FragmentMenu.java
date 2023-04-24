package com.example.dmc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentMenu extends Fragment {

    Button mButtonBullet,mButtonVideo,mButtonCoverage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_menu,container,false);
        mButtonBullet=v.findViewById(R.id.bullet_search_entry);
        mButtonVideo=v.findViewById(R.id.video_download_entry);
        mButtonCoverage=v.findViewById(R.id.coverage_download_entry);

        ActivityResultLauncher<Intent> entry=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

            }
        });
        mButtonBullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ActivitySearch.class);
                entry.launch(intent);
            }
        });
        mButtonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ActivityVideoDownload.class);
                entry.launch(intent);
            }
        });
        mButtonCoverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ActivityCoverage.class);
                entry.launch(intent);
            }
        });
        return v;
    }
}
