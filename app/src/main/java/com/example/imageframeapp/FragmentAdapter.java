package com.example.imageframeapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FragmentAdapter extends FragmentStatePagerAdapter {
	FragmentAdapter( @NonNull FragmentManager fm ) {
		super( fm );
	}

	@NonNull
	@Override
	public Fragment getItem( int position ) {
		switch (position) {
			case 0:
				return new FrameOne( );
			case 1:
				return new FrameTwo( );
			default:
				return null;
		}
	}

	@Override
	public int getCount( ) {
		return 2;
	}
}
