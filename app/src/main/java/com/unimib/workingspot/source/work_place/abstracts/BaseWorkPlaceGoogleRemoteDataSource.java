package com.unimib.workingspot.source.work_place.abstracts;

import android.content.Context;
import com.unimib.workingspot.repository.work_place.IWorkPlaceResponseCallback;


/**
 * Abstract base class representing the remote data source that interacts with Google Places API
 */
public abstract class BaseWorkPlaceGoogleRemoteDataSource {


    protected IWorkPlaceResponseCallback callback; // Callback interface
    protected Context context;
    /**
     * Sets the workplace callback which will be used to notify the repository
     * @param callback The callback interface
     */
    public void setCallback(IWorkPlaceResponseCallback callback) {
        this.callback = callback;
    }
    /**
     * Sets the context for the data source. This is needed to use the bitmap manager
     * @param context The context
     */
    public void setContext(Context context) {
        this.context = context;
    }
    /**
     * Abstract method to search for a workplace using name.
     * @param name    The name to search for.
     * @param isOutside  Flag indicating whether the place is outside or inside. Will be used to set the
     *                   "outside" flag for the {@link com.unimib.workingspot.model.WorkPlace Workplace}
     */
    public abstract void findPlaceByName(String name, boolean isOutside);


}
