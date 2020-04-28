package com.example.imageframeapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

	final int RC_AUDIO = 124;
	String[] perms = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE };
	ImageView base, fr, gallery;
	ByteArrayOutputStream baos;
	AlertDialog alertDialog;
	Button select, save;
	Bitmap bitmap, bit;
	Canvas canvas;
	String text;
	Handler h;
	byte[] b;

	@SuppressLint( "ClickableViewAccessibility" )
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		methodRequiresTwoPermission( );
		base = findViewById( R.id.base );
		select = findViewById( R.id.select );
		save = findViewById( R.id.save );
		fr = findViewById( R.id.fr );
		h = new Handler( );

		bitmap = BitmapFactory.decodeResource( getResources( ), R.drawable.frame_1 );

		select.setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick( View v13 ) {
				save.animate( ).alpha( 1f ).setDuration( 500 );
				AlertDialog.Builder ld = new AlertDialog.Builder( Objects.requireNonNull( MainActivity.this ) );
				LayoutInflater inflater1 = MainActivity.this.getLayoutInflater( );
				@SuppressLint( "InflateParams" )
				View dialogView = inflater1.inflate( R.layout.custom_img, null );
				ld.setView( dialogView );
				alertDialog = ld.create( );
				alertDialog.show( );
				gallery = alertDialog.findViewById( R.id.gallery );

				assert gallery != null;
				gallery.setOnClickListener( new View.OnClickListener( ) {
					@Override
					public void onClick( View v1 ) {
						Intent it = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
						Log.d( "MainActivity", "Gallery Loaded ." );
						Toast.makeText( MainActivity.this, "Gallery Selected" + bitmap, Toast.LENGTH_SHORT ).show( );
						MainActivity.this.startActivityForResult( it, 1 );
					}
				} );

			}
		} );

		base.setOnTouchListener( new Touch( this ) );

		save.setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick( View v ) {
				bit = bitmap.copy( Bitmap.Config.ARGB_8888, true );
				canvas = new Canvas( bit );
				Log.d( "MainActivity", "Canvas Size" + canvas );
				Toast.makeText( MainActivity.this, "Save Selected", Toast.LENGTH_SHORT ).show( );
				MainActivity.this.SaveImage( );
			}
		} );
	}

	@Override
	public void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
		assert data != null;
		Uri uri = data.getData( );
		try {
			bitmap = MediaStore.Images.Media.getBitmap( Objects.requireNonNull( this ).getContentResolver( ), uri );
		} catch (IOException e) {
			e.printStackTrace( );
			Toast.makeText( this, "Image Selected" + e.getMessage( ), Toast.LENGTH_SHORT ).show( );
			Log.d( "MainActivity", "Image Loaded ." );
		}
		base.setImageBitmap( bitmap );
		alertDialog.dismiss( );

		super.onActivityResult( requestCode, resultCode, data );
	}

//	public void setText( ) {
//		Toast.makeText( this, "Text from Box : " + text, Toast.LENGTH_SHORT ).show( );
//		Log.d( "MainActivity", "Text to Show : " + text );
//		Paint paint = new Paint( );
//		paint.setStyle( Paint.Style.FILL );
//		paint.setColor( getResources( ).getColor( android.R.color.white ) );
//		paint.setTextSize( 20 );
//		paint.setStrokeWidth( 10 );
//		canvas.drawText( " " + text, 15, bitmap.getHeight( ) - 10, paint );
//		base.setImageBitmap( bitmap );
//	}

	private void SaveImage( ) {
		String root = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES ).toString( );
		File myDir = new File( root + "/saved" );
		myDir.mkdirs( );
		Log.d( "MainActivity", "Folder Created: " + myDir );
		int n = 100;
		Random generator = new Random( );
		n = generator.nextInt( n );
		String fname = "Image-" + n + ".jpg";
		Log.d( "MainActivity", "File name : " + fname );
		File file = new File( myDir, fname );
		if (file.exists( )) file.delete( );
		try {
			FileOutputStream out = new FileOutputStream( file );
			bit.compress( Bitmap.CompressFormat.JPEG, 90, out );
			out.flush( );
			out.close( );
		} catch (Exception e) {
			e.printStackTrace( );
		}
		MediaScannerConnection.scanFile( this, new String[]{ file.toString( ) }, null,
				new MediaScannerConnection.OnScanCompletedListener( ) {
					public void onScanCompleted( String path, Uri uri ) {
						Log.i( "ExternalStorage", "Scanned " + path + ":" );
						Log.i( "ExternalStorage", "-> uri=" + uri );
					}
				} );
	}

	@Override
	public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
		super.onRequestPermissionsResult( requestCode, permissions, grantResults );

		// Forward results to EasyPermissions
		EasyPermissions.onRequestPermissionsResult( requestCode, permissions, grantResults, this );
	}

	@AfterPermissionGranted( RC_AUDIO )
	private void methodRequiresTwoPermission( ) {
		if (EasyPermissions.hasPermissions( this, perms )) {

		} else {
			// Do not have permissions, request them now
			EasyPermissions.requestPermissions( this, "Permission", RC_AUDIO, perms );
		}
	}

}