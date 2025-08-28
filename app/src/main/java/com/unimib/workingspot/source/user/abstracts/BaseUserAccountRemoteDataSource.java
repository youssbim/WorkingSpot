package com.unimib.workingspot.source.user.abstracts;

import com.unimib.workingspot.repository.user.account.UserAccountResponseCallback;

/**
 * Abstract class representing a remote data source for user account operations.
 * This class defines the contract for interacting with user account data remotely,
 * such as fetching the logged-in user info, logging out, and updating account details.
 */
public abstract class BaseUserAccountRemoteDataSource {
    /**
    * Callback interface to handle responses from user account operations
     */
    protected UserAccountResponseCallback userCallback;

    /**
     * Sets the callback to receive user account operation responses.
     * @param userCallback The callback instance
     */
    public void setUserCallback(UserAccountResponseCallback userCallback){
        this.userCallback = userCallback;
    }

    /**
     * Abstract method to retrieve the currently logged-in user.
     * Implementations should define how to fetch this user remotely.
     */
    public abstract void getLoggedUser();

    /**
     * Abstract method to log out the current user.
     * Implementations should define the logout procedure remotely.
     */
    public abstract void logout();

    /**
     * Abstract method to change the username of the current user.
     * @param username The new username to be set.
     */
    public abstract void changeUsername(final String username);

    /**
     * Abstract method to change the password for the current user.
     * @param email The email associated with the account (likely to identify the user).
     */
    public abstract void changePassword(final String email);

    /**
     * Abstract method to change the email address for the current user.
     * @param email The new email address to be set.
     */
    public abstract void changeEmail(final String email);
}
