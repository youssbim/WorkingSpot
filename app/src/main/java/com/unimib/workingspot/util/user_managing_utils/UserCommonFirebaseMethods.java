package com.unimib.workingspot.util.user_managing_utils;

import static com.unimib.workingspot.util.constants.AccountConstants.USERNAME_MODIFY_ERROR;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.ANONYMOUS_USER;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.USER_NOT_LOGGED;
import static com.unimib.workingspot.util.constants.Constants.EMPTY_USERNAME_ERROR;
import static com.unimib.workingspot.util.user_managing_utils.CallBacksMessages.getMessage;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.unimib.workingspot.model.User;
import com.unimib.workingspot.repository.user.IUserResponseCallback;

public abstract class UserCommonFirebaseMethods {

    /**
     * Sends a password reset email using FirebaseAuth.
     * Calls back with success or error messages.
     *
     * @param firebaseAuth Firebase authentication instance
     * @param userCallback callback to communicate success/failure
     * @param email user's email to send reset link
     */
    public static void changePassword(FirebaseAuth firebaseAuth, IUserResponseCallback userCallback, final String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task->{
                    if (task.isSuccessful()) {
                        userCallback.onSuccessFromPasswordReset();
                    }else{
                        userCallback.onFailureFromPasswordReset(getMessage(task.getException()));
                    }
                });
    }

    /**
     * Updates the user's display name (username) in Firebase.
     * Validates non-empty username, updates profile, and invokes callbacks.
     *
     * @param firebaseAuth Firebase authentication instance
     * @param userCallback callback to communicate success/failure
     * @param username new username to set
     */
    public static void changeUsername(FirebaseAuth firebaseAuth, IUserResponseCallback userCallback, final String username) {
        if(username.isEmpty()){
            userCallback.onFailureFromRemoteAccountUpdate(EMPTY_USERNAME_ERROR);
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
        .setDisplayName(username)
        .build();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
        user.updateProfile(profileUpdates)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userCallback.onSuccessFromRemoteAccountUpdate(username);
                } else {
                    userCallback.onFailureFromRemoteAccountUpdate(getMessage(task.getException()));
                }
            });
        }else{
        userCallback.onFailureFromRemoteAccountUpdate(USERNAME_MODIFY_ERROR);
        }
    }

    /**
     * Retrieves the currently logged-in Firebase user and converts it into the app's User model.
     * Handles anonymous users and null user cases with appropriate callbacks.
     *
     * @param firebaseAuth Firebase authentication instance
     * @param userCallback callback to communicate the logged user or failure
     */
    public static void getLoggedUser(FirebaseAuth firebaseAuth, IUserResponseCallback userCallback){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            userCallback.onFailureFromLoggedUser(USER_NOT_LOGGED);
        }else if(firebaseUser.isAnonymous()){
            userCallback.onSuccessFromLoggedUser(ANONYMOUS_USER);
        }else{
            userCallback.onSuccessFromLoggedUser(new User(
                    firebaseUser.getUid(),
                    firebaseUser.getEmail(),
                    firebaseUser.getDisplayName(),
                    firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null)
            );
        }
    }
}
