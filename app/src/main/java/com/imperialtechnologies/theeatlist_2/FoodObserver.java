package com.imperialtechnologies.theeatlist_2;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by kdandang on 4/10/2015.
 */
public class FoodObserver implements Observer {
    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link java.util.Observable} object.
     * @param data       the data passed to {@link java.util.Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {

        //Test GitHub push
    }
}
