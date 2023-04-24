package com.example.dmc;

import androidx.fragment.app.Fragment;

public class ActivityCoverage extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new FragmentCoverage();
    }
}
