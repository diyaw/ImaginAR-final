package com.vesit.imaginar.one;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    ImageView bgapp;
    LinearLayout textsplash, texthome, menus;
    Animation frombottom;
    Button button_3d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);


        bgapp = (ImageView) findViewById(R.id.bgapp);

        textsplash = (LinearLayout) findViewById(R.id.textsplash);

        menus = (LinearLayout) findViewById(R.id.menus);

        bgapp.animate().translationY(-1900).setDuration(800).setStartDelay(300);

        textsplash.animate().translationY(140).alpha(0).setDuration(800).setStartDelay(300);


        menus.startAnimation(frombottom);


    }

    public void goTo3DCamera(View view)
    {
        Intent intent = new Intent(MainActivity.this, HelloSceneformActivity.class);
        startActivity(intent);
    }
}
