package com.example.fredrik.supersudoku.cameraimport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.fredrik.supersudoku.R;

public class CameraActivity extends AppCompatActivity {

    Camera2BasicFragment f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
//        f = (Camera2BasicFragment) getFragmentManager().getFragment(savedInstanceState, "asdf");

    }
}

