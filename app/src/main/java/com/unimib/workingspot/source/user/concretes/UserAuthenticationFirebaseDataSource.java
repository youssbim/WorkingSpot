package com.unimib.workingspot.source.user.concretes;

import static com.unimib.workingspot.util.constants.AuthenticationConstants.*;
import static com.unimib.workingspot.util.user_managing_utils.CallBacksMessages.getMessage;

import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.unimib.workingspot.model.User;
import com.unimib.workingspot.source.user.abstracts.BaseUserAuthenticationRemoteDataSource;
import com.unimib.workingspot.util.user_managing_utils.UserCommonFirebaseMethods;

/**
 * Concrete implementation of BaseUserAuthenticationRemoteDataSource using Firebase Authentication.
 * Handles user authentication operations such as sign-up, sign-in, Google sign-in, anonymous sign-in,
 * and password change by interacting with FirebaseAuth.
 */
public class UserAuthenticationFirebaseDataSource extends BaseUserAuthenticationRemoteDataSource {

    private static final String TAG = UserAuthenticationFirebaseDataSource.class.getSimpleName();

    /**
     Firebase Authentication instance to perform authentication-related operations
     */
    private final FirebaseAuth firebaseAuth;

    /**
     * Constructor initializes FirebaseAuth instance.
     */
    public UserAuthenticationFirebaseDataSource() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Retrieves the currently logged-in user by delegating to common Firebase methods utility.
     */
    @Override
    public void getLoggedUser() {
        UserCommonFirebaseMethods.getLoggedUser(FirebaseAuth.getInstance(), userCallback);
    }

    /**
     * Signs up a user anonymously.
     * Calls the Firebase API to sign in anonymously and informs the callback of success or failure.
     */
    public void signUpAnonymously(){
        firebaseAuth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    userCallback.onSuccessFromRemoteAuthentication(ANONYMOUS_USER);
                } else {
                    userCallback.onFailureFromRemoteAuthentication(getMessage(task.getException()));
                }
            } else {
                userCallback.onFailureFromRemoteAuthentication(getMessage(task.getException()));
            }
        });
    }

    /**
     * Signs up a user using email, password, and username.
     * On success, updates the username and returns a User model via callback.
     */
    @Override
    public void signUp(final String email, final String password, final String username) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // Update username after sign-up success
                    updateUsername(username);
                    // Notify success with new User instance
                    userCallback.onSuccessFromRemoteAuthentication(
                            new User(
                                    firebaseUser.getUid(),
                                    firebaseUser.getEmail(),
                                    username,
                                    null
                            )
                    );
                } else {
                    userCallback.onFailureFromRemoteAuthentication(getMessage(task.getException()));
                }
            } else {
                userCallback.onFailureFromRemoteAuthentication(getMessage(task.getException()));
            }
        });
    }

    /**
     * Signs in or signs up a user using a Google ID token.
     * Uses Firebase's GoogleAuthProvider to authenticate with the token.
     */
    @Override
    public void signUpWithGoogle(final String idToken) {
        if (idToken != null) {
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        userCallback.onSuccessFromRemoteAuthentication(
                                new User(
                                        firebaseUser.getUid(),
                                        firebaseUser.getEmail(),
                                        firebaseUser.getDisplayName(),
                                        null
                                )
                        );
                    } else {
                        userCallback.onFailureFromRemoteAuthentication(getMessage(task.getException()));
                    }
                } else {
                    Log.w(TAG, GOOGLE_CREDENTIAL_FAILURE, task.getException());
                    userCallback.onFailureFromRemoteAuthentication(getMessage(task.getException()));
                }
            });
        }
    }

    /**
     * Signs in a user using email and password.
     * On success, returns a User model via callback.
     */
    @Override
    public void signIn(final String email, final String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    userCallback.onSuccessFromRemoteAuthentication(
                            new User(
                                    firebaseUser.getUid(),
                                    firebaseUser.getEmail(),
                                    firebaseUser.getDisplayName(),
                                    null
                            )
                    );
                    Log.d(TAG, SIGN_IN_SUCCESSFUL_USER_ID + firebaseUser.getUid());
                } else {
                    userCallback.onFailureFromRemoteAuthentication(getMessage(task.getException()));
                }
            } else {
                userCallback.onFailureFromRemoteAuthentication(getMessage(task.getException()));
                Log.e(TAG, SIGN_IN_FAILED);
            }
        });
    }

    /**
     * Changes the password of the current user by delegating to a utility method.
     * @param email The user's email (usually to identify the account)
     */
    @Override
    public void changePassword(final String email) {
        UserCommonFirebaseMethods.changePassword(firebaseAuth, userCallback, email);
    }

    /**
     * Private helper method to update the username after account creation.
     * Delegates to utility method for the actual update.
     * @param username The username to update
     */
    private void updateUsername(final String username) {
        UserCommonFirebaseMethods.changeUsername(firebaseAuth, userCallback, username);
    }
}
