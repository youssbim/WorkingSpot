package com.unimib.workingspot.database.work_place;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.unimib.workingspot.model.WorkPlace;

import java.util.List;
/**
 * Data Access Object (DAO) for accessing {@link WorkPlace} data from the Room database.
 */
@Dao
public interface WorkPlaceDAO {

    /**
     * Retrieves all work places from the database.
     * @return A list of all {@link WorkPlace} entries.
     */
    @Query("SELECT * FROM WorkPlace")
    List<WorkPlace> getAll();

    /**
     * Retrieves all saved work places from the database.
     * A saved workplace is defined by the field {@code saved = 1}.
     * @return A list of saved {@link WorkPlace} entries.
     */
    @Query("SELECT * FROM WorkPlace WHERE saved = 1")
    List<WorkPlace> getSavedWorkPlaces();

    /**
     * Inserts one or more work places into the database.
     * If a conflict occurs, the existing record will be replaced.
     * @param workPlaces One or more {@link WorkPlace} objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WorkPlace... workPlaces);

    /**
     * Inserts a list of work places into the database.
     * If a conflict occurs, the existing records will be replaced.
     * @param workPlaceList A list of {@link WorkPlace} objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<WorkPlace> workPlaceList);

    /**
     * Updates an existing work place in the database.
     * @param workPlace The {@link WorkPlace} object to update.
     */
    @Update
    void updateWorkPlace(WorkPlace workPlace);

    /**
     * Deletes all entries from the {@link WorkPlace} table.
     */
    @Query("DELETE from WorkPlace")
    void deleteEverything();

}
