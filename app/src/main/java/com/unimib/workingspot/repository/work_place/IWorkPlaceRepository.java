package com.unimib.workingspot.repository.work_place;

import androidx.lifecycle.LiveData;

import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.model.WorkPlace;

/**
 * This interface defines a generic interface for interacting with a WorkPlaceRepository.
 * It abstracts the operations for fetching, creating, saving, and removing workplaces.
 *
 */
public interface IWorkPlaceRepository {
    /**
     * Retrieves a list of workplaces from a remote source.
     */
    void fetchWorkPlaces();

    /**
     * Provides LiveData containing the result of workplace retrieval.
     *
     * @return {@link LiveData} with workplace retrieval result.
     */
    LiveData<Result> getWorkPlaceLiveData();

    /**
     * Fetches a list of saved workplaces for a specific user, identified by the given UID
     *
     * @param UID The unique identifier of the user
     */
    void fetchSavedWorkPlaces(String UID);
    /**
     * Creates a new workplace on the database
     *
     * @param name   The name of the workplace to be created
     * @param isOutside True if the place is outside, false otherwise
     */
    void createWorkPlace(String name, boolean isOutside);

    /**
     * Provides LiveData containing the result of creating a new workplace.
     *
     * @return {@link LiveData} with the create result.
     */
    LiveData<Result> getCreateWorkPlaceResultLiveData();


    /**
     * Provides LiveData containing the result of retrieving saved workplaces.
     *
     * @return {@link LiveData} with saved workplace retrieval result.
     */
    LiveData<Result> getSavedWorkPlaceLiveData();

    /**
     * Saves a workplace for a user
     *
     * @param UID       The unique identifier of the user
     * @param workPlace The {@link WorkPlace} to be saved
     */
    void saveWorkPlace(String UID, WorkPlace workPlace);

    /**
     * Provides LiveData containing the result of saving a workplace.
     *
     * @return {@link LiveData} with the save process result.
     */
    LiveData<Result> getSaveResultWorkPlaceLiveData();

    /**
     * Removes a previously saved workplace for a user
     *
     * @param UID       The unique identifier of the user
     * @param workPlace The {@link WorkPlace} to be removed
     */
    void removeSavedWorkPlace(String UID, WorkPlace workPlace);

    /**
     * Provides LiveData containing the result of removing a saved workplace.
     *
     * @return {@link LiveData} with the remove result.
     */
    LiveData<Result> getRemoveResultWorkPlaceLiveData();

}
