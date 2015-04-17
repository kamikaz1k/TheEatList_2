package com.imperialtechnologies.theeatlist_2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by kdandang on 4/13/2015.
 */
public class FoodItemLoader extends AsyncTaskLoader<HashMap<String, String>> {

    // We hold a reference to the Loader’s data here.
    private HashMap<String, String> mData;
    private String foodId;
    private DBTools dbTools;

    public FoodItemLoader(Context context, String input) {
        // Loaders may be used across multiple Activitys (assuming they aren't
        // bound to the LoaderManager), so NEVER hold a reference to the context
        // directly. Doing so will cause you to leak an entire Activity's context.
        // The superclass constructor will store a reference to the Application
        // Context instead, and can be retrieved with a call to getContext().
        super(context);

        //Search for the input as foodID
        foodId = input;
    }

    @Override
    public HashMap<String, String> loadInBackground() {
        // This method is called on a background thread and should generate a
        // new set of data to be delivered back to the client.
        dbTools = new DBTools(getContext());

        Log.d("FoodItemLoader", "Fetching getFoodItemDetails");
        mData = dbTools.getFoodItemDetails(foodId);
        Log.d("FoodItemLoader", "FoodItemDetails Hashmap retrieved");

        return mData;
    }
}
