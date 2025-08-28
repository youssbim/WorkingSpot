package com.unimib.workingspot.source.user.concretes;

import static com.unimib.workingspot.util.constants.AccountConstants.EMAIL_MODIFY_ERROR;
import static com.unimib.workingspot.util.constants.Constants.EMAIL;
import static com.unimib.workingspot.util.user_managing_utils.CallBacksMessages.getMessage;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.workingspot.source.user.abstracts.BaseUserAccountRemoteDataSource;
import com.unimib.workingspot.util.user_managing_utils.UserCommonFirebaseMethods;

/**
 * Concrete implementation of BaseUserAccountRemoteDataSource using Firebase Authentication.
 * Handles user account operations like getting the logged-in user, logout,
 * changing username, email, and password via Firebase services.
 */
public class UserAccountFirebaseDataSource extends BaseUserAccountRemoteDataSource {

    private static final String TAG = UserAccountFirebaseDataSource.class.getSimpleName();

    /**
     Firebase Authentication instance for user account management
     */
    private final FirebaseAuth firebaseAuth;

    /**
     * Constructor initializes FirebaseAuth instance.
     */
    public UserAccountFirebaseDataSource() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Retrieves the currently logged-in user using a utility method.
     */
    @Override
    public void getLoggedUser(){
        UserCommonFirebaseMethods.getLoggedUser(firebaseAuth, userCallback);
    }

    /**
     * Logs out the current user.
     * Adds an AuthStateListener to detect when the user is signed out, then triggers callback.
     */
    @Override
    public void logout() {
        firebaseAuth.signOut();
    }

    /**
     * Changes the username of the current user by delegating to a utility method.
     * @param username New username to be set
     */
    @Override
    public void changeUsername(String username) {
        UserCommonFirebaseMethods.changeUsername(firebaseAuth, userCallback, username);
    }

    /**
     * Changes the email of the current user.
     * Uses Firebase's verifyBeforeUpdateEmail to ensure re-authentication and verification.
     * @param email The new email address to update
     */
    @Override
    public void changeEmail(final String email) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.verifyBeforeUpdateEmail(email)
                    .addOnCompleteListener(emailTask -> {
                        if (emailTask.isSuccessful()) {
                            userCallback.onSuccessFromRemoteAccountUpdate(EMAIL);
                        } else {
                            userCallback.onFailureFromRemoteAccountUpdate(getMessage(emailTask.getException()));
                        }
                    });
        } else {
            userCallback.onFailureFromRemoteAccountUpdate(EMAIL_MODIFY_ERROR);
        }
    }

    /**
     * Changes the password of the current user by delegating to a utility method.
     * @param email The email of the user requesting password change (usually for identification)
     */
    @Override
    public void changePassword(final String email) {
        UserCommonFirebaseMethods.changePassword(firebaseAuth, userCallback, email);
    }
}
