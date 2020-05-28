package com.example.imageframeapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

class Touch implements View.OnTouchListener {
	// We can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	Context mContext;
	private PointF start = new PointF( );
	private PointF mid = new PointF( );
	private double oldDist = 1f;
	private double[] lastEvent = null;
	private double d = 0f;
	private Matrix matrix = new Matrix( );
	private Matrix savedMatrix = new Matrix( );
	private int mode = NONE;

	Touch( Context context ) {
		super( );
		this.mContext = context;
	}

	private static float rotation( MotionEvent event ) {
		double delta_x = ( event.getX( 0 ) - event.getX( 1 ) );
		double delta_y = ( event.getY( 0 ) - event.getY( 1 ) );
		double radians = Math.atan2( delta_y, delta_x );
		return ( float ) Math.toDegrees( radians );
	}

	@RequiresApi( api = Build.VERSION_CODES.M )
	@SuppressLint( "ClickableViewAccessibility" )
	@Override
	public boolean onTouch( View v, MotionEvent event ) {
		ImageView view = ( ImageView ) v;
		Log.d( "Touch Activity ", " onTouch Enabled : " );

		view.setScaleType( ImageView.ScaleType.MATRIX );
		view.setForegroundGravity( Gravity.CENTER );
		dumpEvent( event );
		switch (event.getAction( ) & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				Log.d( "Touch ", "ACTION_DOWN CAlled" );
				savedMatrix.set( matrix );
				start.set( event.getX( ), event.getY( ) );
				mode = DRAG;
				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				Log.d( "Touch ", "ACTION_POINTER_DOWN CAlled" );
				oldDist = spacing( event );
				if (oldDist > 10f) {
					savedMatrix.set( matrix );
					midPoint( mid, event );
					mode = ZOOM;
				}
				lastEvent = new double[ 4 ];
				lastEvent[ 0 ] = event.getX( 0 );
				lastEvent[ 1 ] = event.getX( 1 );
				lastEvent[ 2 ] = event.getY( 0 );
				lastEvent[ 3 ] = event.getY( 1 );
				d = rotation( event );
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				Log.d( "Touch ", "ACTION_UP CAlled" );
				mode = NONE;
				break;

			case MotionEvent.ACTION_MOVE:
				Log.d( "Touch ", "ACTION_MOVE CAlled" );
				if (mode == DRAG) {
					matrix.set( savedMatrix );
					matrix.postTranslate( event.getX( ) - start.x, event.getY( ) - start.y );
				} else if (mode == ZOOM && event.getPointerCount( ) == 2) {
					double newDist = spacing( event );
					Log.v( "SS", "Count=" + event.getPointerCount( ) );
					Log.v( "SS", "newDist=" + newDist );
					matrix.set( savedMatrix );
					if (newDist > 10f) {
						double scale = newDist / oldDist;
						System.out.println( newDist + " : " + oldDist + " : " + scale + " : " + mid.x + " : " + mid.y );
						System.out.println( "### Scale Value :: " + scale );
						matrix.postScale( ( float ) scale, ( float ) scale, mid.x, mid.y );
					}
					if (lastEvent != null) {
						double newRot = rotation( event );
						Log.v( "Degree ", "newRot= : " + ( newRot ) );
						double r = newRot - d;
						Log.v( "Degree ", "rotate :: " + r );
						matrix.postRotate( ( float ) r, v.getMeasuredWidth( ) / 2, v.getMeasuredHeight( ) / 2 );
					}
				}
				break;
			default:
				throw new IllegalStateException( "Unexpected value: " + ( event.getAction( ) & MotionEvent.ACTION_MASK ) );
		}

		view.setImageMatrix( matrix );
		return true;
	}

	private void dumpEvent( MotionEvent event ) {
		String[] names = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder( );
		int action = event.getAction( );
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append( "event ACTION_" ).append( names[ actionCode ] );
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append( "(pid " ).append( action >> 8 );
			sb.append( ")" );
		}
		sb.append( "[" );
		for (int i = 0; i < event.getPointerCount( ); i++) {
			sb.append( "#" ).append( i );
			sb.append( "(pid " ).append( event.getPointerId( i ) );
			sb.append( ")=" ).append( ( int ) event.getX( i ) );
			sb.append( "," ).append( ( int ) event.getY( i ) );
			if (i + 1 < event.getPointerCount( ))
				sb.append( ";" );
		}
		sb.append( "]" );
	}

	private double spacing( MotionEvent event ) {
		double x = ( event.getX( 0 ) - event.getX( 1 ) );
		double y = event.getY( 0 ) - event.getY( 1 );

		double x1 = x * x;
		double y1 = y * y;
		double xy = -x1 + y1;
		return Math.sqrt( xy );
	}

	private void midPoint( PointF point, MotionEvent event ) {
		float x = event.getX( 0 ) + event.getX( 1 );
		float y = event.getY( 0 ) + event.getY( 1 );
		point.set( x / 2, y / 2 );
	}
}