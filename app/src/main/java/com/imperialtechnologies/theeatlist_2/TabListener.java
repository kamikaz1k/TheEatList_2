package com.imperialtechnologies.theeatlist_2;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.widget.Toast;


public class TabListener implements ActionBar.TabListener {

	Fragment fragment;
	
	public TabListener(Fragment fragment){
		
		this.fragment = fragment;
		
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		
		ft.replace(R.id.mainTable, fragment);
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		
		ft.remove(fragment);
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
		Toast.makeText(this.fragment.getActivity(), "Reselected!", Toast.LENGTH_SHORT).show();
		
	}

}
