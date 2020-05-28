package com.example.imageframeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.Random;

public class FrameOne extends Fragment {

	ImageView base, fr, gallery;
	AlertDialog alertDialog;
	ImageButton delete, save;
	Bitmap bitmap;
	FrameLayout frameOuter;
	int n = 0;

	FrameOne( ) {
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

		View v = inflater.inflate( R.layout.fragment_frame_one, container, false );

		base = v.findViewById( R.id.base );
		delete = v.findViewById( R.id.delete );
		save = v.findViewById( R.id.save );
		frameOuter = v.findViewById( R.id.frameOuter );
		fr = v.findViewById( R.id.fr );

		base.setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick( View v ) {
				if (n == 0) {
					save.animate( ).alpha( 1f ).setDuration( 500 );
					AlertDialog.Builder ld = new AlertDialog.Builder( Objects.requireNonNull( getContext( ) ) );
					LayoutInflater inflater1 = getActivity( ).getLayoutInflater( );
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
							Toast.makeText( getContext( ), "Gallery Selected" + bitmap, Toast.LENGTH_SHORT ).show( );
							getActivity( ).startActivityForResult( it, 1 );
							n++;
						}
					} );
				} else {
					base.setOnTouchListener( new Touch( getContext( ) ) );
				}
			}
		} );
		return v;
	}

	private void savef( Bitmap bit ) {

		String root = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES ).toString( );
		File myDir = new File( root + "/saved" );
		myDir.mkdirs( );
		Log.d( "MainActivity", "Folder Created: " + myDir );
		int n = 100;
		Random generator = new Random( );
		n = generator.nextInt( n );
		String fname = "IMG-" + n + ".jpg";
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

		MediaScannerConnection.scanFile( getContext( ), new String[]{ file.toString( ) }, null, new MediaScannerConnection.OnScanCompletedListener( ) {
			public void onScanCompleted( String path, Uri uri ) {
				Log.i( "ExternalStorage", "Scanned " + path + ":" );
				Log.i( "ExternalStorage", "-> uri=" + uri );
			}
		} );
	}
}
