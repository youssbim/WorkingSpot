package com.unimib.workingspot.source.work_place.abstracts;

import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.repository.work_place.IWorkPlaceResponseCallback;

public abstract class BaseWorkPlaceRemoteFirebaseDataSource {

    protected IWorkPlaceResponseCallback callback;
    /**
     * Sets the callback which will be used to notify the repository
     * when data is fetched or saved remotely.
     *
     * @param callback The callback interface
     */
    public void setCallback(IWorkPlaceResponseCallback callback) {
        this.callback = callback;
    }

    /**
     * Abstract method for fetching a list of workplaces from the remote source
     */
    public abstract void fetchWorkPlaces();
    /**
     * Abstract method for creating a new workplace on the remote source
     * @param workPlace The workplace object that needs to be created
     */
    public abstract void createWorkPlace(WorkPlace workPlace);
    /**
     * Abstract method for saving a workplace for a specific user to the remote source
     * @param UID The unique identifier of the user
     * @param workPlace The workplace to be saved
     */
    public abstract void saveWorkPlace(String UID, WorkPlace workPlace);
    /**
     * Abstract method for fetching the keys of saved workplaces for a specific user from the remote source
     * @param UID The unique identifier of the user
     */
    public abstract void getSavedWorkPlaceKeys(String UID);
    /**
     * Abstract method to remove a saved workplace for a specific user on the remote source
     * @param UID The unique identifier of the user
     * @param workPlace he workplace to be removed from the saved list
     */
    public abstract void removeSavedWorkPlace(String UID, WorkPlace workPlace);

}
