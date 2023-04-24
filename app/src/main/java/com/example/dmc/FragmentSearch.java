package com.example.dmc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class FragmentSearch extends Fragment {

    private EditText mEditText;
    private Button mButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_search,container,false);
        mEditText=v.findViewById(R.id.url_to_search);
        mButton=v.findViewById(R.id.url_search_button);
        ActivityResultLauncher<Intent> pooling=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                    }
                });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ActivityBulletPooling.class);
                intent.putExtra("url",mEditText.getText().toString());
                pooling.launch(intent);
            }
        });

        return v;
    }
}
