package com.unimib.workingspot.ui.main.viewmodel.work_place;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.unimib.workingspot.model.Consumable;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.repository.work_place.IWorkPlaceRepository;

/**
 * ViewModel for managing workplace-related data and communication with the repository.
 * This class acts as a bridge between the UI and the {@link IWorkPlaceRepository} implementation
 *  via {@link LiveData}.
 */
public class WorkPlaceViewModel extends ViewModel {

    private final IWorkPlaceRepository workPlaceRepository;

    // Livedata to expose to the UI
    private final MutableLiveData<Consumable<Result>> fetchResultLiveData;
    private final MutableLiveData<Consumable<Result>> fetchSavedResultLiveData;
    private final MutableLiveData<Consumable<Result>> createResultLiveData;
    private final MutableLiveData<Consumable<Result>> saveResultLiveData;
    private final MutableLiveData<Consumable<Result>> deleteResultLiveData;

    // Livedata observers
    private final Observer<Result> fetchWorkPlacesObserver;
    private final Observer<Result> fetchSavedWorkPlaceObserver;
    private final Observer<Result> saveObserver;
    private final Observer<Result> deleteObserver;
    private final Observer<Result> createObserver;

    // Flags for indicating if a livedata already has an observer attached to it
    private boolean fetchObserverAttached = false;
    private boolean fetchSavedObserverAttached = false;
    private boolean saveObserverAttached = false;
    private boolean deleteObserverAttached = false;
    private boolean createObserverAttached = false;


    /**
     * Constructor for initializing WorkPlaceViewModel
     * @param workPlaceRepository The workplace repository implementation
     */
    public WorkPlaceViewModel(IWorkPlaceRepository workPlaceRepository) {
        // Initializes the livedata
        this.workPlaceRepository = workPlaceRepository;
        this.saveResultLiveData = new MutableLiveData<>();
        this.deleteResultLiveData = new MutableLiveData<>();
        this.fetchResultLiveData = new MutableLiveData<>();
        this.fetchSavedResultLiveData = new MutableLiveData<>();
        this.createResultLiveData = new MutableLiveData<>();

        // Creates the observers
        fetchWorkPlacesObserver = result -> fetchResultLiveData.postValue(new Consumable<>(result));
        fetchSavedWorkPlaceObserver = result -> fetchSavedResultLiveData.postValue(new Consumable<>(result));
        saveObserver = result -> saveResultLiveData.postValue(new Consumable<>(result));
        deleteObserver = result -> deleteResultLiveData.postValue(new Consumable<>(result));
        createObserver = result -> createResultLiveData.postValue(new Consumable<>(result));
    }

    /**
     * Returns LiveData that will contain workplace fetching results.
     * @return A {@link LiveData} object that will hold the fetched workplaces.
     * The {@link Result} contained within the LiveData is wrapped in a {@link Consumable}
     * object to ensure that previously consumed data is not reused
     */
    public LiveData<Consumable<Result>> getWorkPlacesLiveData() {
        attachObserver(workPlaceRepository.getWorkPlaceLiveData(), fetchWorkPlacesObserver);
        fetchObserverAttached = true;
        return fetchResultLiveData;
    }

    /**
     * Returns LiveData that will contain saved workplace fetching results
     * @return A {@link LiveData} object that will hold the fetched saved workplaces.
     * The {@link Result} contained within the LiveData is wrapped in a {@link Consumable}
     * object to ensure that previously consumed data is not reused
     */
    public LiveData<Consumable<Result>> getSavedWorkPlaceLiveData() {
        attachObserver(workPlaceRepository.getSavedWorkPlaceLiveData(), fetchSavedWorkPlaceObserver);
        fetchSavedObserverAttached = true;
        return fetchSavedResultLiveData;
    }

    /**
     * Returns LiveData that will contain the result of the saving operation
     * @return A {@link LiveData} object that will hold the result of the saving operation.
     * The {@link Result} contained within the LiveData is wrapped in a {@link Consumable}
     * object to ensure that previously consumed data is not reused
     */
    public LiveData<Consumable<Result>> getSavedResultWorkPlaceLiveData() {
        attachObserver(workPlaceRepository.getSaveResultWorkPlaceLiveData(), saveObserver);
        saveObserverAttached = true;
        return saveResultLiveData;
    }

    /**
     * Returns LiveData that will contain the result of the remove from saved operation
     * @return A {@link LiveData} object that will hold the result of the remove operation.
     * The {@link Result} contained within the LiveData is wrapped in a {@link Consumable}
     * object to ensure that previously consumed data is not reused
     */
    public LiveData<Consumable<Result>> getRemoveWorkPlaceLiveData() {
        attachObserver(workPlaceRepository.getRemoveResultWorkPlaceLiveData(), deleteObserver);
        deleteObserverAttached = true;
        return deleteResultLiveData;
    }

    /**
     * Returns LiveData that will contain the result of the create operation
     * @return A {@link LiveData} object that will hold the result of the create operation.
     * The {@link Result} contained within the LiveData is wrapped in a {@link Consumable}
     * object to ensure that previously consumed data is not reused
     */
    public LiveData<Consumable<Result>> getCreateWorkPlaceLiveData() {
        attachObserver(workPlaceRepository.getCreateWorkPlaceResultLiveData(), createObserver);
        createObserverAttached = true;
        return createResultLiveData;
    }

    /**
     * Initiates workplace fetching from the repository
     */
    public void getWorkPlaces() {
        workPlaceRepository.fetchWorkPlaces();
    }

    /**
     * Initiates fetching saved workplaces from the repository
     * @param UID The UID of the user for whom to fetch the saved workplaces
     */
    public void getSavedWorkPlaces(String UID) {
        workPlaceRepository.fetchSavedWorkPlaces(UID);
    }

    /**
     * Initiates the saving operation for a {@link WorkPlace}
     * @param UID The UID of the user for whom to save the workplace
     * @param workPlace The WorkPlace to save
     */
    public void saveWorkPlace(String UID, WorkPlace workPlace) {
        workPlaceRepository.saveWorkPlace(UID, workPlace);
    }

    /**
     * Initiates the remove from saved operation for a {@link WorkPlace}
     * @param UID The UID of the user for whom to remove the saved workplace
     * @param workPlace The WorkPlace to remove from the saved list
     */
    public void removeSavedWorkPlace(String UID, WorkPlace workPlace) {
        workPlaceRepository.removeSavedWorkPlace(UID, workPlace);
    }

    /**
     * Initiates the operation that will create a new {@link WorkPlace}
     * @param name The name of the workplace
     * @param isOutside Whether the workplace is outdoors or not
     */
    public void createWorkPlace(String name, boolean isOutside) {
        workPlaceRepository.createWorkPlace(name, isOutside);
    }

    /**
     * Attaches a permanent {@link Observer} to {@link LiveData} if it doesn't already have one.
     * @param liveData The LiveData object to observe
     * @param observer The Observer to be attached
     */
    private void attachObserver(LiveData<Result> liveData, Observer<Result> observer) {
        if(!liveData.hasObservers())
            liveData.observeForever(observer);
    }

    /**
     * Cleans up observers when the ViewModel is cleared. This prevents memory leaks when the
     * LifeCycleOwner to which the ViewModel is tied gets destroyed
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if(fetchObserverAttached)
            workPlaceRepository.getWorkPlaceLiveData().removeObserver(fetchWorkPlacesObserver);
        if(fetchSavedObserverAttached)
            workPlaceRepository.getSavedWorkPlaceLiveData().removeObserver(fetchSavedWorkPlaceObserver);
        if(saveObserverAttached)
            workPlaceRepository.getSaveResultWorkPlaceLiveData().removeObserver(saveObserver);
        if(deleteObserverAttached)
            workPlaceRepository.getRemoveResultWorkPlaceLiveData().removeObserver(deleteObserver);
        if(createObserverAttached)
            workPlaceRepository.getCreateWorkPlaceResultLiveData().removeObserver(createObserver);
    }
}
