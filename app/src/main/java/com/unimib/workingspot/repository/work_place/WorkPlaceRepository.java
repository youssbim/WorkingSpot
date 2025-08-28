package com.unimib.workingspot.repository.work_place;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.source.work_place.abstracts.BaseWorkPlaceGoogleRemoteDataSource;
import com.unimib.workingspot.source.work_place.abstracts.BaseWorkPlaceLocalDataSource;
import com.unimib.workingspot.source.work_place.abstracts.BaseWorkPlaceRemoteFirebaseDataSource;

import java.util.List;

/**
 * This class is responsible for managing the retrieval of the workplaces. It first attempts to fetch
 * the data from the remote database; if it doesn't succeed, then it fetches the data from the local
 * Room database. This class implements a repository pattern and coordinates the two
 * WorkPlace data sources (local and remote) */
public class WorkPlaceRepository implements IWorkPlaceResponseCallback, IWorkPlaceRepository {

    // Data sources for fetching workplace data
    private final BaseWorkPlaceRemoteFirebaseDataSource workPlaceRemoteDataSource;
    private final BaseWorkPlaceLocalDataSource workPlaceLocalDataSource;
    private final BaseWorkPlaceGoogleRemoteDataSource workPlaceGoogleRemoteDataSource;

    // LiveData objects to post the workplaces data or errors
    private final MutableLiveData<Result> workPlacesMutableLiveData;
    private final MutableLiveData<Result> savedWorkPlacesMutableLiveData;
    private final MutableLiveData<Result> saveResultMutableLiveData;
    private final MutableLiveData<Result> createWorkPlaceMutableLiveData;
    private final MutableLiveData<Result> deleteResultMutableLiveData;

    /**
     * Constructor for initializing the WorkPlaceRepository with the remote and local data sources
     * @param workPlaceRemoteDataSource  Data source for fetching weather from the remote database
     * @param workPlaceLocalDataSource Data source for fetching weather from the local database
     */
    public WorkPlaceRepository(BaseWorkPlaceRemoteFirebaseDataSource workPlaceRemoteDataSource,
                               BaseWorkPlaceLocalDataSource workPlaceLocalDataSource,
                               BaseWorkPlaceGoogleRemoteDataSource workPlaceGoogleRemoteDataSource) {
        // Create live data
        this.workPlacesMutableLiveData = new MutableLiveData<>();
        this.savedWorkPlacesMutableLiveData = new MutableLiveData<>();
        this.deleteResultMutableLiveData = new MutableLiveData<>();
        this.createWorkPlaceMutableLiveData = new MutableLiveData<>();
        this.saveResultMutableLiveData = new MutableLiveData<>();

        // Assign the data sources to the repository
        this.workPlaceRemoteDataSource = workPlaceRemoteDataSource;
        this.workPlaceLocalDataSource = workPlaceLocalDataSource;
        this.workPlaceGoogleRemoteDataSource = workPlaceGoogleRemoteDataSource;
        // Setup the callbacks
        workPlaceRemoteDataSource.setCallback(this);
        workPlaceLocalDataSource.setCallback(this);
        workPlaceGoogleRemoteDataSource.setCallback(this);
    }
    /** {@inheritDoc} */
    @Override
    public void fetchWorkPlaces() {
        workPlaceRemoteDataSource.fetchWorkPlaces();
    }
    /** {@inheritDoc} */
    @Override
    public LiveData<Result> getWorkPlaceLiveData() {
        return this.workPlacesMutableLiveData;
    }
    /** {@inheritDoc} */
    @Override
    public void fetchSavedWorkPlaces(String UID) {
        workPlaceRemoteDataSource.getSavedWorkPlaceKeys(UID);
    }
    /** {@inheritDoc} */
    @Override
    public LiveData<Result> getSavedWorkPlaceLiveData() {
        return this.savedWorkPlacesMutableLiveData;
    }
    /** {@inheritDoc} */
    @Override
    public void saveWorkPlace(String UID, WorkPlace workPlace) {
        workPlaceRemoteDataSource.saveWorkPlace(UID, workPlace);
    }
    /** {@inheritDoc} */
    @Override
    public LiveData<Result> getSaveResultWorkPlaceLiveData() {
        return this.saveResultMutableLiveData;
    }
    /** {@inheritDoc} */
    @Override
    public LiveData<Result> getRemoveResultWorkPlaceLiveData() {
        return this.deleteResultMutableLiveData;
    }

    /** {@inheritDoc} */
    @Override
    public void removeSavedWorkPlace(String UID, WorkPlace workPlace){
        workPlaceRemoteDataSource.removeSavedWorkPlace(UID, workPlace);
    }
    /** {@inheritDoc} */
    @Override
    public void createWorkPlace(String name, boolean isOutside) {
        workPlaceGoogleRemoteDataSource.findPlaceByName(name, isOutside);
    }
    /** {@inheritDoc} */
    @Override
    public LiveData<Result> getCreateWorkPlaceResultLiveData() {
        return this.createWorkPlaceMutableLiveData;
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessFetchWorkPlacesFromRemote(List<WorkPlace> workPlaceList) {
        workPlaceLocalDataSource.saveAllWorkPlaces(workPlaceList);
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessFetchWorkPlacesFromLocal(List<WorkPlace> workPlaceList) {
        Result.WorkPlaceSuccess result = new Result.WorkPlaceSuccess(workPlaceList);
        workPlacesMutableLiveData.postValue(result);
    }
    /** {@inheritDoc} */
    @Override
    public void onFailureFetchWorkPlaceFromRemote(Exception exception) {
        workPlaceLocalDataSource.getWorkPlaces();
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessFetchSavedKeysFromRemote(List<String> savedKeys) {
        workPlaceLocalDataSource.setWorkplacesAsSaved(savedKeys);
    }
    /** {@inheritDoc} */
    @Override
    public void onFailureFetchSavedKeysFromRemote(Exception exception) {
        workPlaceLocalDataSource.getSavedWorkPlaces();
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessFetchSavedFromLocal(List<WorkPlace> workPlaceList) {
        Result.WorkPlaceSuccess result = new Result.WorkPlaceSuccess(workPlaceList);
        savedWorkPlacesMutableLiveData.postValue(result);
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessFetchWorkPlaceFromGoogle(WorkPlace workPlace) {
        workPlaceRemoteDataSource.createWorkPlace(workPlace);
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessCreateFromRemote(WorkPlace workPlace) {
        workPlaceLocalDataSource.insertWorkPlace(workPlace);
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessCreateFromLocal(WorkPlace workPlace) {
        Result.WorkPlaceSuccess result = new Result.WorkPlaceSuccess(workPlace);
        createWorkPlaceMutableLiveData.postValue(result);
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessSaveFromRemote(WorkPlace workPlace) {
        workPlaceLocalDataSource.setWorkPlaceAsSaved(workPlace);
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessSaveFromLocal(WorkPlace workPlace) {
        Result.WorkPlaceSuccess workPlaceSuccess = new Result.WorkPlaceSuccess(workPlace);
        saveResultMutableLiveData.postValue(workPlaceSuccess);
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessSaveFromLocal(List<WorkPlace> workPlaceList) {
        Result.WorkPlaceSuccess result = new Result.WorkPlaceSuccess(workPlaceList);
        workPlacesMutableLiveData.postValue(result);
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessDeleteSavedFromRemote(WorkPlace workPlace) {
        workPlaceLocalDataSource.setWorkPlaceAsNotSaved(workPlace);
    }
    /** {@inheritDoc} */
    @Override
    public void onSuccessDeleteSavedFromLocal(WorkPlace workPlace) {
        Result.WorkPlaceSuccess workPlaceSuccess = new Result.WorkPlaceSuccess(workPlace);
        deleteResultMutableLiveData.postValue(workPlaceSuccess);
    }
    /** {@inheritDoc} */
    @Override
    public void onFailureFromRemote(Exception e) {
        Result.Error error = new Result.Error(e.getMessage());
        createWorkPlaceMutableLiveData.postValue(error);
        savedWorkPlacesMutableLiveData.postValue(error);
        workPlacesMutableLiveData.postValue(error);
        deleteResultMutableLiveData.postValue(error);
    }
    /** {@inheritDoc} */
    @Override
    public void onFailureFromLocal(Exception e) {
        Result.Error error = new Result.Error(e.getMessage());
        createWorkPlaceMutableLiveData.postValue(error);
        savedWorkPlacesMutableLiveData.postValue(error);
        workPlacesMutableLiveData.postValue(error);
        deleteResultMutableLiveData.postValue(error);
    }
    /** {@inheritDoc} */
    @Override
    public void onSetSavedCompleted() {
        workPlaceLocalDataSource.getSavedWorkPlaces();
    }

}
