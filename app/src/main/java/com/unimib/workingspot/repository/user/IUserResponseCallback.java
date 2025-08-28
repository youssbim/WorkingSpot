package com.unimib.workingspot.repository.user;

import com.unimib.workingspot.model.User;

/**
 * Callback interface to handle user-related responses from data sources.
 * It defines the contract for delivering results of local and remote user operations.
 */
public interface IUserResponseCallback {

    /**
     * Called when a local user data operation completes successfully.
     *
     * @param response String representation of the successful response.
     */
    void onSuccessFromLocal(String response);

    /**
     * Called when a local user data operation fails.
     *
     * @param message Error message describing the failure.
     */
    void onFailureFromLocal(String message);

    /**
     * Called when a remote request for the currently logged-in user succeeds, returning a raw response string.
     *
     * @param response String response from the server.
     */
    void onSuccessFromLoggedUser(String response);

    /**
     * Called when a remote request for the currently logged-in user succeeds, returning a parsed User object.
     *
     * @param user The {@link User} object retrieved from the server.
     */
    void onSuccessFromLoggedUser(User user);

    /**
     * Called when a remote request for the currently logged-in user fails.
     *
     * @param message Error message describing the failure.
     */
    void onFailureFromLoggedUser(String message);

    /**
     * Called when a remote account update (e.g., email or username) completes successfully.
     *
     * @param response The updated value or confirmation string.
     */
    void onSuccessFromRemoteAccountUpdate(String response);

    /**
     * Called when a remote account update (e.g., email or username) fails.
     *
     * @param message Error message describing the failure.
     */
    void onFailureFromRemoteAccountUpdate(String message);

    /**
     * Called when a password reset operation is successful.
     */
    void onSuccessFromPasswordReset();

    /**
     * Called when a password reset operation fails.
     *
     * @param message Error message describing the failure.
     */
    void onFailureFromPasswordReset(String message);
}
