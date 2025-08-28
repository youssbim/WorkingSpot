package com.unimib.workingspot.source.user.abstracts;

import com.unimib.workingspot.repository.user.authentication.UserAuthenticationResponseCallback;

/**
 * Abstract class representing a remote data source for user authentication operations.
 * Defines methods for managing user sign-in, sign-up, and authentication state remotely.
 */
public abstract class BaseUserAuthenticationRemoteDataSource {

    /**
     * Callback interface to handle authentication responses and results
     */
    protected UserAuthenticationResponseCallback userCallback;

    /**
     * Sets the callback to receive authentication operation responses.
     * @param userCallback The callback instance
     */
    public void setUserCallback(UserAuthenticationResponseCallback userCallback){
        this.userCallback = userCallback;
    }

    /**
     * Abstract method to get the currently logged-in user.
     * Implementations should define how to retrieve this info from remote source.
     */
    public abstract void getLoggedUser();

    /**
     * Abstract method for user registration using email, password, and username.
     * @param email The user's email address
     * @param password The user's chosen password
     * @param username The desired username
     */
    public abstract void signUp(final String email, final String password, final String username);

    /**
     * Abstract method for user registration/sign-in via Google OAuth token.
     * @param token The OAuth token from Google authentication
     */
    public abstract void signUpWithGoogle(final String token);

    /**
     * Abstract method for user sign-in using email and password.
     * @param email The user's email address
     * @param password The user's password
     */
    public abstract void signIn(final String email, final String password);

    /**
     * Abstract method for anonymous sign-up/sign-in.
     * Useful for guest users without credentials.
     */
    public abstract void signUpAnonymously();

    /**
     * Abstract method to change the password associated with a given email.
     * @param email The user's email address for which to change the password
     */
    public abstract void changePassword(final String email);
}
