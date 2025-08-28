package com.unimib.workingspot.repository.user.account;

import com.unimib.workingspot.repository.user.IUserResponseCallback;

/**
 * Extended callback interface for handling additional user account operations.
 * Includes remote interaction outcomes and logout-specific events.
 */
public interface UserAccountResponseCallback extends IUserResponseCallback {

    /**
     * Called when a remote operation (e.g., fetching profile picture) completes successfully.
     *
     * @param response The result or response string from the remote data source.
     */
    void onSuccessFromRemote(final String response);

    /**
     * Called when a remote operation (e.g., fetching profile picture) fails.
     *
     * @param message Error message describing the failure.
     */
    void onFailureFromRemote(final String message);

    /**
     * Called when the logout operation is successful.
     * This may include clearing local cache and/or remote sign-out.
     */
    void onSuccessLogout();

    /**
     * Called when the logout operation fails.
     *
     * @param message Error message describing the logout failure.
     */
    void onFailureLogout(final String message);
}
