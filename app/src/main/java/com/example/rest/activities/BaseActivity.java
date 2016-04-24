package com.example.rest.activities;

import android.animation.Animator;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.rest.R;

// вспомогательный класс обший для ArtistActivity и CurrentArtistActivity. Может понадовится для разширения UI
public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    protected boolean isTablet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = getResources().getDisplayMetrics(); //определяет какой будет размер экрана программно
        isTablet = (metrics.widthPixels / metrics.density)>=600;
    }

    //добавляем Toolbar
    @Override
    public void setContentView(@LayoutRes int LayoutIResId) {
        super.setContentView(LayoutIResId);
        toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }
}
