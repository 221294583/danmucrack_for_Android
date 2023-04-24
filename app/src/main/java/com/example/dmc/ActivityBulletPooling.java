package com.example.dmc;

import androidx.fragment.app.Fragment;

public class ActivityBulletPooling extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new FragmentBulletPooling();
    }
}
