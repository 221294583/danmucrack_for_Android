package com.example.dmc;

import androidx.fragment.app.Fragment;

public class ActivityMenu extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        return new FragmentMenu();
    }
}
