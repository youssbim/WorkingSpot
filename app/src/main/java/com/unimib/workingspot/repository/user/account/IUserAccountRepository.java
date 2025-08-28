package com.unimib.workingspot.repository.user.account;

import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.lifecycle.MutableLiveData;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.repository.user.IUserResponseCallback;

/**
 * Interface representing the user account repository.
 * Provides methods to handle user account operations and exposes LiveData streams for observing user state.
 */
public interface IUserAccountRepository extends IUserResponseCallback {

    /**
     * Returns LiveData holding local data resource results, such as cached user data.
     * @return MutableLiveData containing local resource Result.
     */
    MutableLiveData<Result> getResourceLocalLiveData();

    /**
     * Returns LiveData holding remote data resource results, e.g., data fetched from a network.
     * @return MutableLiveData containing remote resource Result.
     */
    MutableLiveData<Result> getResourceRemoteLiveData();

    /**
     * Returns LiveData holding update operation results, such as profile update.
     * @return MutableLiveData containing update operation Result.
     */
    MutableLiveData<Result> getResourceUpdateLiveData();

    /**
     * Returns LiveData representing the currently logged-in user.
     * @return MutableLiveData containing user Result.
     */
    MutableLiveData<Result> getUserLiveData();

    /**
     * Initiates fetching the currently logged-in user information.
     */
    void getLoggedUser();

    /**
     * Logs out the current user.
     */
    void logout();

    /**
     * Retrieves a cached resource by the specified key.
     * @param key The key identifying the cached resource.
     */
    void getCacheResource(final String key);

    /**
     * Fetches the profile picture of the user.
     */
    void getProfilePicture();

    /**
     * Attempts to clear Google credentials asynchronously.
     *
     * @param credentialManager the credential manager instance
     * @param clearCredentialStateRequest the request for clearing credentials
     * @return MutableLiveData<Result> observing the operation's success or failure
     */
    MutableLiveData<Result> clearGoogleCredential(
            CredentialManager credentialManager,
            ClearCredentialStateRequest clearCredentialStateRequest
    );

    /**
     * Updates the username remotely.
     * @param username The new username to update.
     */
    void updateUsername(final String username);

    /**
     * Updates the email remotely.
     * @param email The new email to update.
     */
    void updateEmail(final String email);

    /**
     * Updates the password remotely by triggering a password reset email.
     * @param email The email address associated with the user account.
     */
    void updatePassword(final String email);

    /**
     * Updates the profile picture remotely.
     *
     * @param encodedProfilePhoto The profile photo encoded as a String.
     */
    void updateProfilePicture(String encodedProfilePhoto);
}
