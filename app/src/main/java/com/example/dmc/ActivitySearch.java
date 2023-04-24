package com.example.dmc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class ActivitySearch extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new FragmentSearch();
    }
}