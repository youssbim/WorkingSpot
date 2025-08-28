package com.unimib.workingspot.source.user.concretes;

import static com.unimib.workingspot.util.constants.AccountConstants.EMPTY_PROFILE_PHOTO;
import static com.unimib.workingspot.util.constants.AccountConstants.PROFILE_PHOTO_DB_REF;
import static com.unimib.workingspot.util.constants.AccountConstants.SAVED;
import static com.unimib.workingspot.util.constants.WorkPlacesConstants.REALTIME_DATABASE_BASE_URL;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.workingspot.source.user.abstracts.BaseUserRemoteDataSource;
import com.unimib.workingspot.util.user_managing_utils.CallBacksMessages;

import java.util.Objects;

/**
 * Concrete implementation of BaseUserRemoteDataSource that interacts
 * with Firebase Realtime Database to handle user profile picture retrieval and storage.
 */
public class UserRealTimeDatabaseRemoteDataSource extends BaseUserRemoteDataSource {

    /**
     Reference to the Firebase Realtime Database path where user data is saved
     */
    private final DatabaseReference savedReference;
    /**
     General database root reference
     */
    private final DatabaseReference dbReference;

    /**
     Firebase Authentication instance to get the current user
     */
    private final FirebaseAuth firebaseAuth;

    /**
     * Constructor initializes Firebase Realtime Database references and FirebaseAuth.
     * Uses a specific database URL defined in constants.
     */
    public UserRealTimeDatabaseRemoteDataSource() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(REALTIME_DATABASE_BASE_URL);
        dbReference = database.getReference();  // Root reference of the database
        savedReference = database.getReference(SAVED);  // Reference to "saved" node
        firebaseAuth = FirebaseAuth.getInstance();  // Firebase Authentication instance
    }

    /**
     * Retrieves the encoded profile picture of the current user from the Realtime Database.
     * Uses a single value event listener to fetch the data once.
     * If the picture is missing, returns a default empty profile photo constant.
     * Notifies success or failure through userResponseCallback.
     */
    @Override
    public void getProfilePicture() {
        savedReference
                .child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()) // current user ID
                .child(PROFILE_PHOTO_DB_REF) // profile photo node
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String encodedProfilePhoto = snapshot.getValue(String.class);

                        // If no profile photo found, use default empty photo
                        if (encodedProfilePhoto == null) {
                            encodedProfilePhoto = EMPTY_PROFILE_PHOTO;
                        }

                        // Notify success with the encoded profile picture string
                        userResponseCallback.onSuccessFromRemote(encodedProfilePhoto);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Notify failure with error message if read cancelled or failed
                        userResponseCallback.onFailureFromRemote(error.getMessage());
                    }
                });
    }

    /**
     * Saves the encoded profile picture string for the current user in the Realtime Database.
     * Sets the value under "saved" node, then notifies success or failure via callback.
     */
    @Override
    public void saveProfilePicture(String encodedProfilePhoto) {
        dbReference.child(SAVED)
                .child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .child(PROFILE_PHOTO_DB_REF)
                .setValue(encodedProfilePhoto)
                .addOnSuccessListener(aVoid ->
                        userResponseCallback.onSuccessFromRemote(encodedProfilePhoto)
                )
                .addOnFailureListener(e ->
                        userResponseCallback.onFailureFromRemote(CallBacksMessages.getMessage(e))
                );
    }
}
