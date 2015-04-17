package com.imperialtechnologies.theeatlist_2;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityFragmentTest extends ListFragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
	
		View rootView = inflater.inflate(R.layout.activity_main_fragment_test1, null);
		
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
            Drawable original = view.getBackground();
			Drawable listSelectorBackground = getResources().getDrawable(R.drawable.list_selector_background_pressed);		
			view.setBackground(listSelectorBackground);
			
			// Calls for EditFoodItem
			//startActivity(theIntent);
            //TODO - if a fragment starts activity for result, does it also accept it back?
            //TODO - handle onActivityResult for EditFoodItem
            //TODO - unselect list item after clicking

            startActivityForResult(theIntent, MainActivity.EDIT_FOOD_ITEM);

		}
		
	};

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        Toast.makeText(getActivity().getApplicationContext(),"Refresh View Please!",Toast.LENGTH_SHORT).show();

    }

}