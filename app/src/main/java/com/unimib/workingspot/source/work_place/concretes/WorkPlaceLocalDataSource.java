package com.unimib.workingspot.source.work_place.concretes;

import com.unimib.workingspot.database.work_place.WorkPlaceDAO;
import com.unimib.workingspot.database.work_place.WorkPlaceRoomDatabase;
import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.source.work_place.abstracts.BaseWorkPlaceLocalDataSource;

import java.util.List;
/**
 * Concrete implementation of {@link BaseWorkPlaceLocalDataSource} for interacting with
 * the local Room database
 */
public class WorkPlaceLocalDataSource extends BaseWorkPlaceLocalDataSource {

    private final WorkPlaceDAO workPlaceDAO;
    /**
     * Constructor to initialize WorkPlaceLocalDataSource
     * @param workPlaceRoomDatabase The Room database instance used to access the workplace data
     */
    public WorkPlaceLocalDataSource(WorkPlaceRoomDatabase workPlaceRoomDatabase) {
        this.workPlaceDAO = workPlaceRoomDatabase.workPlaceDAO();
    }
    /**
     * Fetches all workplace data from the local Room database asynchronously.
     * Concrete implementation of {@link BaseWorkPlaceLocalDataSource#getWorkPlaces()}
     */
    @Override
    public void getWorkPlaces() {
        WorkPlaceRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<WorkPlace> workPlaces = workPlaceDAO.getAll();
                callback.onSuccessFetchWorkPlacesFromLocal(workPlaces);
            } catch (Exception e) {
                callback.onFailureFromLocal(e);
            }
        });
    }
    /**
     * Saves a list of workplaces to the local Room database asynchronously.
     * Concrete implementation of {@link BaseWorkPlaceLocalDataSource#saveAllWorkPlaces(List)}
     * @param workPlaceList The list of workplaces to be saved
     */
    @Override
    public void saveAllWorkPlaces(List<WorkPlace> workPlaceList) {
        WorkPlaceRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                workPlaceDAO.insertAll(workPlaceList);
                callback.onSuccessSaveFromLocal(workPlaceList);
            } catch (Exception e) {
                workPlaceDAO.deleteEverything();
                callback.onFailureFromLocal(e);
            }
        });
    }
    /**
     * Updates asynchronously the "saved" status of a specific workplace in the local Room database,
     * and calls the appropriate callback according to the state
     *
     * @param workPlace The workplace to be updated
     * @param saved The new saved status to set (true = saved, false = not saved)
     */
    private void updateWorkPlaceSavedStatus(WorkPlace workPlace, boolean saved) {
        workPlace.setSaved(saved);
        WorkPlaceRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                workPlaceDAO.updateWorkPlace(workPlace);
                if (saved) {
                    callback.onSuccessSaveFromLocal(workPlace);
                } else {
                    callback.onSuccessDeleteSavedFromLocal(workPlace);
                }
            } catch (Exception e) {
                callback.onFailureFromLocal(e);
            }
        });
    }

    /**
     * Marks asynchronously a specific workplace as saved in the local Room database.
     * Concrete implementation of {@link BaseWorkPlaceLocalDataSource#setWorkPlaceAsSaved(WorkPlace)}.
     *
     * @param workPlace The workplace to be marked as saved
     */
    @Override
    public void setWorkPlaceAsSaved(WorkPlace workPlace) {
        updateWorkPlaceSavedStatus(workPlace, true);
    }

    /**
     * Marks asynchronously a specific workplace as not saved in the local Room database.
     * Concrete implementation of {@link BaseWorkPlaceLocalDataSource#setWorkPlaceAsNotSaved(WorkPlace)}.
     *
     * @param workPlace The workplace to be marked as not saved
     */
    @Override
    public void setWorkPlaceAsNotSaved(WorkPlace workPlace) {
        updateWorkPlaceSavedStatus(workPlace, false);
    }

    /**
     * Inserts a new workplace into the local Room database asynchronously.
     * Concrete implementation of {@link BaseWorkPlaceLocalDataSource#insertWorkPlace(WorkPlace)}
     * @param workPlace The workplace to be inserted
     */
    @Override
    public void insertWorkPlace(WorkPlace workPlace) {
        WorkPlaceRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                workPlaceDAO.insert(workPlace);
                callback.onSuccessCreateFromLocal(workPlace);
            } catch (Exception e) {
                callback.onFailureFromLocal(e);
            }
        });
    }
    /**
     * Retrieves all saved workplaces from the local database asynchronously.
     * Concrete implementation of {@link BaseWorkPlaceLocalDataSource#getWorkPlaces()}
     */
    @Override
    public void getSavedWorkPlaces() {
        WorkPlaceRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                callback.onSuccessFetchSavedFromLocal(workPlaceDAO.getSavedWorkPlaces());
            } catch (Exception e) {
                callback.onFailureFromLocal(e);
            }
        });
    }
    /**
     * Marks workplaces as saved based on a list of primary keys.
     * Concrete implementation of {@link BaseWorkPlaceLocalDataSource#setWorkplacesAsSaved(List)}
     * @param savedKeys A list of primary keys representing the unique identifiers for the
     *                  {@link WorkPlace WorkPlaces} to be set as saved
     */
    @Override
    public void setWorkplacesAsSaved(List<String> savedKeys) {
        WorkPlaceRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<WorkPlace> workPlaces = workPlaceDAO.getAll();
                for (WorkPlace workPlace : workPlaces) {
                    if (savedKeys.contains(workPlace.getFirebaseKey())) {
                        workPlace.setSaved(true);
                        workPlaceDAO.updateWorkPlace(workPlace);
                    }
                }

                callback.onSetSavedCompleted();

            } catch (Exception e) {

                callback.onFailureFromLocal(e);

            }
        });
    }



}
