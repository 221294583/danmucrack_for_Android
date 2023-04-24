package com.example.dmc;

import androidx.fragment.app.Fragment;

public class ActivityVideoDownload extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new FragmentVideoDownload();
    }
}
