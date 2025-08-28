package com.unimib.workingspot.repository.user.authentication;

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.unimib.workingspot.model.User;
import com.unimib.workingspot.repository.user.IUserResponseCallback;

/**
 * Interface that extends IUserResponseCallback and provides additional callbacks
 * specific to authentication-related operations.
 * It is intended to be implemented by classes that handle authentication flows
 * and want to receive results from remote and federated login sources (e.g. Google).
 */
public interface UserAuthenticationResponseCallback extends IUserResponseCallback {

    /**
     * Called when remote authentication succeeds with a response message.
     *
     * @param response A success message or status string
     */
    void onSuccessFromRemoteAuthentication(String response);

    /**
     * Called when remote authentication succeeds and returns a User object.
     *
     * @param user Authenticated user details
     */
    void onSuccessFromRemoteAuthentication(User user);

    /**
     * Called when remote authentication fails.
     *
     * @param message An error message explaining the failure
     */
    void onFailureFromRemoteAuthentication(String message);

    /**
     * Called when retrieving Google credentials (e.g., ID token) succeeds.
     *
     * @param googleIdTokenCredential The Google credential object containing ID token
     */
    void onSuccessFromCredentialRetrieval(GoogleIdTokenCredential googleIdTokenCredential);

    /**
     * Called when retrieving Google credentials fails.
     *
     * @param message An error message explaining the failure
     */
    void onFailureFromCredentialRetrieval(String message);
}
