package com.unimib.workingspot.source.user.abstracts;

import com.unimib.workingspot.repository.user.account.UserAccountResponseCallback;

/**
 * Abstract class representing a remote data source for user-related operations,
 * specifically focusing on profile picture management.
 */
public abstract class BaseUserRemoteDataSource {

    /**
     Callback interface to handle responses related to user account operations
     */
    protected UserAccountResponseCallback userResponseCallback;

    /**
     * Abstract method to retrieve the user's profile picture.
     * Implementations should define how to fetch the profile picture remotely.
     */
    public abstract void getProfilePicture();

    /**
     * Abstract method to save or update the user's profile picture.
     * @param encodedProfilePhoto The profile picture encoded as a String (e.g., Base64)
     * Implementations should define how to upload and store this picture remotely.
     */
    public abstract void saveProfilePicture(String encodedProfilePhoto);

    /**
     * Sets the callback to receive user account-related responses.
     * @param userResponseCallback The callback instance
     */
    public void setUserResponseCallback(UserAccountResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
}
