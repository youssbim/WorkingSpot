package com.unimib.workingspot.repository.work_place;

import com.unimib.workingspot.model.WorkPlace;
import java.util.List;

/**
 * This interface defines the callbacks used to handle various
 * scenarios when fetching WorkPlace data from remote and local data sources
 */
public interface IWorkPlaceResponseCallback {
    // -------------------------------- Fetching callbacks --------------------------------------//
    /**
     * Callback for when workplaces are successfully fetched from the remote data source
     * @param workPlaceList the list of workplaces fetched from the remote data source
     */
    void onSuccessFetchWorkPlacesFromRemote(List<WorkPlace> workPlaceList);
    /**
     * Callback for when when workplaces are successfully fetched from the local data source
     * @param workPlaceList the list of workplaces fetched from the local data source
     */
    void onSuccessFetchWorkPlacesFromLocal(List<WorkPlace> workPlaceList);
    /**
     * Callback for when fetching workplaces from the remote data source fails
     * @param exception The exception detailing the error that occurred
     */
    void onFailureFetchWorkPlaceFromRemote(Exception exception);
    /**
     * Callback for when the saved workplace keys are successfully fetched from the remote data source
     * @param savedKeys The list of saved workplace keys fetched from the remote data source
     */
    void onSuccessFetchSavedKeysFromRemote(List<String> savedKeys);
    /**
     * Callback for when fetching saved workplace keys from the remote data source fails.
     * @param exception The exception detailing the error that occurred
     */
    void onFailureFetchSavedKeysFromRemote(Exception exception);
    /**
     * Callback for when saved workplaces are successfully fetched from the local data source
     * @param workPlaceList The list of saved workplaces fetched from the local data source
     */
    void onSuccessFetchSavedFromLocal(List<WorkPlace> workPlaceList);
    /**
     * Callback for when a workplace is successfully fetched from the Google Places API
     * @param workPlace The workplace fetched from the external source.
     */
    void onSuccessFetchWorkPlaceFromGoogle(WorkPlace workPlace);
    // -------------------------------- Create callbacks -----------------------------------------//
    /**
     * Callback for when a workplace is successfully created in the remote data source
     * @param workPlace The workplace that was successfully created in the remote data source
     */
    void onSuccessCreateFromRemote(WorkPlace workPlace);
    /**
     * Callback for when a workplace is successfully created in the local data source
     * @param workPlace The workplace that was successfully created in the remote data source
     */
    void onSuccessCreateFromLocal(WorkPlace workPlace);
    // -------------------------------- Save callbacks -------------------------------------------//
    /**
     * Callback when a workplace is successfully saved in the remote data source
     * @param workPlace The workplace that was successfully saved in the remote data source
     */
    void onSuccessSaveFromRemote(WorkPlace workPlace);
    /**
     * Called when a workplace is successfully saved in the local data source
     * @param workPlace The workplace that was successfully saved in the local data source
     */
    void onSuccessSaveFromLocal(WorkPlace workPlace);
    /**
     * Called when a list of workplaces is successfully saved in the local data source
     * @param workPlaceList The list of workplaces that was successfully saved in the local data source
     */
    void onSuccessSaveFromLocal(List<WorkPlace> workPlaceList);

    /**
     * Called when all saved workplaces have been successfully marked as saved in the local data source.
     * This callback indicates that the local update process is complete and the updated list
     * of saved workplaces can now be safely retrieved or used.
     */
    void onSetSavedCompleted();
    // -------------------------------- Delete callbacks -----------------------------------------//
    /**
     * Called when a saved workplace is successfully deleted from the remote data source
     * @param workPlace The workplace that was successfully deleted from the remote data source
     */
    void onSuccessDeleteSavedFromRemote(WorkPlace workPlace);
    /**
     * Called when a saved workplace is successfully deleted from the local data source
     * @param workPlace The workplace that was successfully deleted from the local data source
     */
    void onSuccessDeleteSavedFromLocal(WorkPlace workPlace);
    // -------------------------------- Generic failures callbacks -------------------------------//
    /**
     * Called when an error occurs in a remote operation
     * @param e The exception detailing the error that occurred
     */
    void onFailureFromRemote(Exception e);
    /**
     * Called when an error occurs in a local operation
     * @param e The exception detailing the error that occurred
     */
    void onFailureFromLocal(Exception e);


}
