package com.unimib.workingspot.repository.user.authentication;

import android.content.Context;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.lifecycle.MutableLiveData;
import com.unimib.workingspot.model.Result;

/**
 * Interface for managing user authentication operations including sign-in, sign-up,
 * anonymous authentication, password reset, and credential retrieval.
 */
public interface IUserAuthenticationRepository {

    /**
     * @return LiveData containing the authentication result of the user.
     */
    MutableLiveData<Result> getUser();

    /**
     * @return LiveData containing cached user data if available.
     */
    MutableLiveData<Result> getUserCache();

    /**
     * @return LiveData containing cached username data.
     */
    MutableLiveData<Result> getUsernameCache();

    /**
     * @return LiveData containing cached permission state.
     */
    MutableLiveData<Result> getPermissionCache();

    /**
     * @return LiveData indicating the result of a password reset request.
     */
    MutableLiveData<Result> getPasswordReset();

    /**
     * Retrieves the currently logged-in user from the authentication service.
     */
    void getLoggedUser();

    /**
     * Stores a key-value pair in the cache (e.g., username, permissions).
     *
     * @param key   The identifier for the cache item.
     * @param value The value to be stored.
     */
    void createInCacheResource(final String key, final String value);

    /**
     * Retrieves a resource from cache using the specified key.
     *
     * @param key The identifier for the cached resource.
     */
    void getInCacheResource(final String key);

    /**
     * Signs up a new user with email, password, and username.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @param username The user's chosen display name.
     */
    void signUp(final String email, final String password, final String username);

    /**
     * Signs up a user using an external token (e.g., Google, Facebook).
     *
     * @param token The OAuth token or external provider token.
     */
    void signUp(final String token);

    /**
     * Signs in a user using email and password.
     *
     * @param email    The user's email.
     * @param password The user's password.
     */
    void signIn(final String email, final String password);

    /**
     * Retrieves Google credentials for authentication via Credential Manager.
     *
     * @param credentialManager      The CredentialManager instance.
     * @param getCredentialRequest   The request specifying credential options.
     * @param context                The Android context used for the request.
     */
    void getGoogleCredential(CredentialManager credentialManager,
                             GetCredentialRequest getCredentialRequest,
                             Context context);

    /**
     * Signs in the user anonymously, without an email or password.
     * Useful for guest sessions or quick access.
     */
    void signUpAnonymously();

    /**
     * Sends a password reset email to the specified address.
     *
     * @param email The email address to send the reset instructions to.
     */
    void passwordReset(String email);
}
