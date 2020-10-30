package com.example.danmucrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String url_type="video";
    int p_choice=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(p_choice);
        setContentView(R.layout.activity_main);
        Button button= (Button) findViewById(R.id.go_comment);
        EditText edit_text= (EditText) findViewById(R.id.url_in);
        RadioButton videoType= (RadioButton) findViewById(R.id.type_video);
        RadioButton bangumiType= (RadioButton) findViewById(R.id.type_bangumi);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlInput=edit_text.getText().toString();
                Toast.makeText(MainActivity.this,urlInput,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,ShowAll.class);
                intent.putExtra("type",url_type);
                intent.putExtra("URL",urlInput);
                intent.putExtra("episode",p_choice);
                startActivity(intent);
            }
        });

        Switch is_single=(Switch) findViewById(R.id.is_single_episode);

        is_single.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AlertDialog.Builder episode=new AlertDialog.Builder(MainActivity.this);
                    NumberPicker picker=new NumberPicker(MainActivity.this);//(NumberPicker) buttonView.findViewById(R.id.episode_picker);
                    picker.setMinValue(1);
                    picker.setMaxValue(30);
                    episode.setTitle("Choose Episode");
                    episode.setView(picker);
                    episode.setCancelable(false);
                    episode.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            p_choice=picker.getValue();
                            System.out.println(p_choice);
                        }
                    });
                    episode.setNegativeButton("Oops,butterfinger!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            p_choice=1;

                            is_single.setChecked(false);
                        }
                    });
                    episode.setView(picker);
                    episode.show();
                }
                else{

                }
            }
        });
    }

    public void radioButtonClicked(View view){
        RadioGroup group_1=(RadioGroup) findViewById(R.id.radio_group);
        switch (view.getId()){
            case R.id.type_video:
                url_type="video";
                System.out.println(url_type);
                break;
            case R.id.type_bangumi:
                url_type="bangumi";
                System.out.println(url_type);
                break;
            default:
                url_type="video";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.caution:
                AlertDialog.Builder caution=new AlertDialog.Builder(MainActivity.this);
                caution.setTitle("CAUTION!");
                caution.setMessage("For now, this app can only do inverse-crc32 calculation from 1-10^10.In case that bilibili uid is out of range ,please inform me at https://github.com/221294583 :)");
                caution.setCancelable(false);
                caution.setPositiveButton("I got that!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                caution.show();
                break;
            case R.id.about_me:
                AlertDialog.Builder about_me=new AlertDialog.Builder(MainActivity.this);
                about_me.setTitle("ABOUT ME");
                about_me.setMessage("My homepage: https://github.com/221294583 :)");
                about_me.setCancelable(false);
                about_me.setPositiveButton("I got that!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                about_me.show();
                break;
            case R.id.setting:
                Intent go_setting=new Intent(MainActivity.this,MakeChange.class);
                startActivity(go_setting);
        }
        return true;
    }
}