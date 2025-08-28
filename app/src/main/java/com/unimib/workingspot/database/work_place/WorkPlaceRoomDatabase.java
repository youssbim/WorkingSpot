package com.unimib.workingspot.database.work_place;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.util.constants.WorkPlacesConstants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {WorkPlace.class}, version = WorkPlacesConstants.WORKPLACE_DATABASE_VERSION)
public abstract class WorkPlaceRoomDatabase extends RoomDatabase {

    /**
     * Abstract method to retrieve the DAO (Data Access Object) for workplace data.
     * This DAO contains methods for interacting with the workplace data in the database.
     *
     * @return The WorkPlaceDao instance for database operations.
     */
    public abstract WorkPlaceDAO workPlaceDAO();

    private static volatile WorkPlaceRoomDatabase INSTANCE;

    /**
     * ExecutorService for background database operations.
     * It uses a fixed thread pool based on the number of available processors in the system.
     * This ensures that database operations are performed asynchronously and do not block
     * the main thread.
     */
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Returns the singleton instance of the database.
     * This method ensures that the database instance is created only once and is thread-safe.
     *
     * @param context The application context used to build the database.
     * @return The singleton instance of the WorkPlaceRoomDatabase.
     */
    public static WorkPlaceRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WorkPlaceRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    WorkPlaceRoomDatabase.class, WorkPlacesConstants.WORKPLACES_DB_NAME)
                            .fallbackToDestructiveMigration(true)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
