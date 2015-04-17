package com.imperialtechnologies.theeatlist_2;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivityFragmentFriendsTab extends ListFragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
	
		View rootView = inflater.inflate(R.layout.activity_main_fragment_friends, null);
		
		return rootView;
		
	}
	
}