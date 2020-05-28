package com.example.imageframeapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
	ImageView sp;
	Handler h = new Handler( );

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_splash );

		sp = findViewById( R.id.sp );
		sp.animate( ).scaleX( 5.4f ).scaleY( 5.4f ).setDuration( 1000 );

		h.postDelayed( new Runnable( ) {
			@Override
			public void run( ) {
				Intent it = new Intent( Splash.this, MainActivity.class );
				startActivity( it );
				finish( );
			}
		}, 3000 );
	}
}