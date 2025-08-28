package com.unimib.workingspot.source.work_place.concretes;
import static com.unimib.workingspot.util.constants.WorkPlacesConstants.REALTIME_DATABASE_BASE_URL;
import static com.unimib.workingspot.util.constants.WorkPlacesConstants.FIREBASE_SAVED_WORKPLACE_FETCH_TIME_UNTIL_TIMEOUT;
import static com.unimib.workingspot.util.constants.WorkPlacesConstants.FIREBASE_TIMEOUT_MESSAGE;
import static com.unimib.workingspot.util.constants.WorkPlacesConstants.FIREBASE_WORKPLACES_ROOT_LOCATION;
import static com.unimib.workingspot.util.constants.WorkPlacesConstants.FIREBASE_WORKPLACES_SAVED_ROOT_LOCATION;
import static com.unimib.workingspot.util.constants.WorkPlacesConstants.FIREBASE_WORKPLACES_USER_SAVED_LOCATION;
import static com.unimib.workingspot.util.constants.WorkPlacesConstants.FIREBASE_WORKPLACE_FETCH_TIME_UNTIL_TIMEOUT;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.source.work_place.abstracts.BaseWorkPlaceRemoteFirebaseDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Concrete implementation of {@link BaseWorkPlaceRemoteFirebaseDataSource} that interacts
 * with Firebase Realtime Database to manage {@link WorkPlace Workplaces} data remotely
 */
public class WorkPlaceRemoteFirebaseDataSource extends BaseWorkPlaceRemoteFirebaseDataSource {

    private final DatabaseReference workPlacesRef; // Workplaces firestore reference
    private final DatabaseReference savedRef; // User saved reference

    /**
     * Constructor for initializing the WorkPlaceRemoteFirebaseDataSource
     */
    public WorkPlaceRemoteFirebaseDataSource() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(REALTIME_DATABASE_BASE_URL);
        workPlacesRef = database.getReference(FIREBASE_WORKPLACES_ROOT_LOCATION);
        savedRef = database.getReference(FIREBASE_WORKPLACES_SAVED_ROOT_LOCATION);
    }

    /**
     * Fetches all workplaces stored in Firebase. The fetch request will be cancelled if more than
     * {@link com.unimib.workingspot.util.constants.WorkPlacesConstants#FIREBASE_WORKPLACE_FETCH_TIME_UNTIL_TIMEOUT FIREBASE_WORKPLACE_FETCH_TIME_UNTIL_TIMEOUT}
     * seconds have passed.
     */
    @Override
    public void fetchWorkPlaces() {
        // Setup a timer to check for a timeout
        Timer timer = new Timer();

        // Create a listener for the events from Firebase
        ValueEventListener dataFetchListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot parent) {
                timer.cancel(); // Remove the timer
                List<WorkPlace> workPlaces = new ArrayList<>();
                for(DataSnapshot dataSnapshot : parent.getChildren()) {
                    WorkPlace workPlace = dataSnapshot.getValue(WorkPlace.class);
                    if(workPlace != null)
                        workPlaces.add(workPlace);
                }
                callback.onSuccessFetchWorkPlacesFromRemote(workPlaces);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                timer.cancel(); // Remove the timer
                callback.onFailureFetchWorkPlaceFromRemote(new Exception(error.getMessage()));
            }
        };
        // Add the listener
        workPlacesRef.addListenerForSingleValueEvent(dataFetchListener);

        // Creates a time task to handle the timeout event
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                workPlacesRef.removeEventListener(dataFetchListener);
                callback.onFailureFetchWorkPlaceFromRemote(new Exception(FIREBASE_TIMEOUT_MESSAGE));
            }
        };
        // Start the timer
        timer.schedule(timerTask, FIREBASE_WORKPLACE_FETCH_TIME_UNTIL_TIMEOUT);
    }
    /**
     * Creates a new workplace entry in Firebase. A firebase key is assigned to every
     * {@link WorkPlace} entity created on the database
     * @param workPlace The workplace to store remotely
     */
    @Override
    public void createWorkPlace(WorkPlace workPlace) {
        DatabaseReference newWorkPlaceRef = workPlacesRef.push();
        String key = newWorkPlaceRef.getKey();
        try{
            assert key != null;
            workPlace.setFirebaseKey(key);
            newWorkPlaceRef.setValue(workPlace)
                    .addOnSuccessListener(aVoid -> callback.onSuccessCreateFromRemote(workPlace))
                    .addOnFailureListener(e -> callback.onFailureFromRemote(e));
        } catch (Exception e) {
            callback.onFailureFromRemote(e);
        }
    }
    /**
     * Saves a workplace key under a user's saved list in Firebase
     * @param UID       The unique user identifier
     * @param workPlace The workplace to save
     */
    @Override
    public void saveWorkPlace(String UID, WorkPlace workPlace) {
        savedRef.child(UID).child(FIREBASE_WORKPLACES_USER_SAVED_LOCATION).push().setValue(workPlace.getFirebaseKey())
                .addOnSuccessListener(aVoid -> callback.onSuccessSaveFromRemote(workPlace))
                .addOnFailureListener(e -> callback.onFailureFromRemote(e));
    }
    /**
     * Removes a saved workplace from a user's saved list in Firebase
     * @param UID       The unique user identifier
     * @param workPlace The workplace to remove
     */
    @Override
    public void removeSavedWorkPlace(String UID, WorkPlace workPlace) {
        savedRef.child(UID).child(FIREBASE_WORKPLACES_USER_SAVED_LOCATION)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String keyValue = childSnapshot.getValue(String.class);
                                if (keyValue != null && keyValue.equals(workPlace.getFirebaseKey())) {
                                    childSnapshot.getRef().removeValue()
                                            .addOnSuccessListener(aVoid -> callback.onSuccessDeleteSavedFromRemote(workPlace))
                                            .addOnFailureListener(callback::onFailureFromRemote);
                                    return;
                                }
                            }
                        } catch(Exception e){
                            callback.onFailureFromRemote(e);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailureFromRemote(new Exception(error.getMessage()));
                    }
                });
    }

    /**
     * Retrieves all saved workplace keys for a specific user from Firebase.
     * The fetch request will be cancelled if more than
     * {@link com.unimib.workingspot.util.constants.WorkPlacesConstants#FIREBASE_SAVED_WORKPLACE_FETCH_TIME_UNTIL_TIMEOUT FIREBASE_SAVED_WORKPLACE_FETCH_TIME_UNTIL_TIMEOUT}
     * seconds have passed
     * @param UID The unique user identifier
     */
    @Override
    public void getSavedWorkPlaceKeys(String UID) {
        // Setup a timer to check for a timeout
        Timer timer = new Timer();

        // Create a listener for the events from Firebase
        ValueEventListener saveFetchListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot parent) {
                timer.cancel(); // Cancel the timer
                List<String> savedKeys = new ArrayList<>();
                for(DataSnapshot child : parent.getChildren()) {
                    String key = child.getValue(String.class);
                    if(key != null)
                        savedKeys.add(key);
                }
                callback.onSuccessFetchSavedKeysFromRemote(savedKeys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                timer.cancel(); // Cancel the timer
                callback.onFailureFromRemote(new Exception(error.getMessage()));
            }
        };
        savedRef.child(UID).child(FIREBASE_WORKPLACES_USER_SAVED_LOCATION).addListenerForSingleValueEvent(saveFetchListener);

        // Creates a time task to handle the timeout event
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                workPlacesRef.removeEventListener(saveFetchListener);
                callback.onFailureFetchSavedKeysFromRemote(new Exception(FIREBASE_TIMEOUT_MESSAGE));
            }
        };
        // Start the timer
        timer.schedule(timerTask, FIREBASE_SAVED_WORKPLACE_FETCH_TIME_UNTIL_TIMEOUT);
    }



}
