package com.unimib.workingspot.source.work_place.abstracts;

import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.repository.work_place.IWorkPlaceResponseCallback;

import java.util.List;

/**
 *  Abstract class representing the local data source for workplace data.
 */
public abstract class BaseWorkPlaceLocalDataSource {

    protected IWorkPlaceResponseCallback callback;
    /**
     * Sets the workplace callback which will be used to notify the repository
     * @param callback The callback interface
     */
    public void setCallback(IWorkPlaceResponseCallback callback) {
        this.callback = callback;
    }
    /**
     * Abstract method for retrieving all the workplace data from the local database
     */
    public abstract void getWorkPlaces();
    /**
     * Abstract method for saving a list of workplaces to the local database
     * @param workPlaceList The list of workplaces to be saved
     */
    public abstract void saveAllWorkPlaces(List<WorkPlace> workPlaceList);
    /**
     * Abstract method for marking a single workplace as saved
     * @param workPlace The workplace to be saved
     */
    public abstract void setWorkPlaceAsSaved(WorkPlace workPlace);
    /**
     *  Abstract method for marking a list of workplaces as saved
     * @param savedKeys A list of primary keys representing the unique identifiers for the
     *                  {@link WorkPlace WorkPlaces} to be set as saved
     */
    public abstract void setWorkplacesAsSaved(List<String> savedKeys);
    /**
     * Abstract method for retrieving all saved workplaces from the local database
     */
    public abstract void getSavedWorkPlaces();
    /**
     * Abstract method for inserting a single workplace into the local database
     * @param workPlace The workplace to be inserted
     */
    public abstract void insertWorkPlace(WorkPlace workPlace);
    /**
     * Abstract method for marking a Workplace as no longer saved
     * @param workPlace The workplace to be set as "not saved"
     */
    public abstract void setWorkPlaceAsNotSaved(WorkPlace workPlace);
}
