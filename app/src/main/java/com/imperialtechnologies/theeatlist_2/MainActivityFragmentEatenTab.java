package com.imperialtechnologies.theeatlist_2;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivityFragmentEatenTab extends ListFragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
	
		View rootView = inflater.inflate(R.layout.activity_main_fragment_eaten, null);
		
		return rootView;
		
	}
	
	public void onViewCreated (View view, Bundle savedInstanceState){
		
		this.getListView().setOnItemClickListener(openEditFoodItem);
		
	}

	public OnItemClickListener openEditFoodItem = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							
			// When an item is clicked get the TextView
			TextView foodId = (TextView) view.findViewById(R.id.foodId);
			
			// Convert that contactId into a String
			String foodIdValue = foodId.getText().toString();	
			
			// Signals an intention to do something
			// getApplication() returns the application that owns
			// this activity
			Intent  theIntent = new Intent(getActivity(), EditFoodItem.class);
			
			// Put additional data in for EditFoodItem to use
			theIntent.putExtra("foodId", foodIdValue); 

			//Set list selector background
			Drawable listSelectorBackground = getResources().getDrawable(R.drawable.list_selector_background_pressed);		
			view.setBackground(listSelectorBackground);
			
			// Calls for EditFoodItem
			startActivity(theIntent); 
		}
		
	};
	
}