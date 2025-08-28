package com.unimib.workingspot.repository.user.authentication;
import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.GOOGLE_CREDENTIAL_ERROR;
import static com.unimib.workingspot.util.constants.Constants.PASSWORD;
import static com.unimib.workingspot.util.constants.Constants.USER;
import static com.unimib.workingspot.util.constants.Constants.USERNAME;
import android.content.Context;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.lifecycle.MutableLiveData;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.model.User;
import com.unimib.workingspot.source.user.abstracts.BaseUserAuthenticationRemoteDataSource;
import com.unimib.workingspot.source.user.abstracts.BaseUserLocalDataSource;
import java.util.concurrent.Executors;

/**
 * Implementation of {@link IUserAuthenticationRepository} and {@link UserAuthenticationResponseCallback}.
 * <p>
 * This repository acts as the central point for managing all user authentication-related logic
 * by integrating remote and local data sources.
 * It serves as the connection between authentication flows and the data handling layer.
 * </p>
 *
 * <p>Supported functionalities include:</p>
 * <ul>
 *   <li>Firebase Email/Password authentication</li>
 *   <li>Google Sign-In integration via Android Credential Manager</li>
 *   <li>Anonymous login for guest access</li>
 *   <li>Password reset via email</li>
 *   <li>Storing and retrieving authentication state and user data locally</li>
 * </ul>
 * <p>Dependencies include:</p>
 * <ul>
 *   <li>{@link BaseUserAuthenticationRemoteDataSource} for Firebase Authentication operations</li>
 *   <li>{@link BaseUserLocalDataSource} for caching user credentials and session state locally</li>
 * </ul>
 */


public class UserAuthenticationRepository implements IUserAuthenticationRepository, UserAuthenticationResponseCallback {


    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> cacheUserMutableLiveData;
    private final MutableLiveData<Result> cacheUsernameMutableLiveData;
    private final MutableLiveData<Result> cachePermissionMutableLiveData;
    private final MutableLiveData<Result> passwordResetMutableLiveData;
    private final BaseUserLocalDataSource userLocalDataSource;

    private final  BaseUserAuthenticationRemoteDataSource userAuthenticationRemoteDataSource;

    /**
     * Constructor initializes the LiveData and binds callbacks to data sources.
     */
    public UserAuthenticationRepository(BaseUserLocalDataSource userLocalDataSource,
                                        BaseUserAuthenticationRemoteDataSource userAuthenticationRemoteDataSource){
        userMutableLiveData = new MutableLiveData<>();
        cacheUserMutableLiveData = new MutableLiveData<>();
        cacheUsernameMutableLiveData = new MutableLiveData<>();
        cachePermissionMutableLiveData = new MutableLiveData<>();
        passwordResetMutableLiveData = new MutableLiveData<>();
        this.userLocalDataSource = userLocalDataSource;
        this.userAuthenticationRemoteDataSource = userAuthenticationRemoteDataSource;
        this.userLocalDataSource.setUserCallback(this);
        this.userAuthenticationRemoteDataSource.setUserCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(){
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserCache(){
        return cacheUserMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUsernameCache(){
        return cacheUsernameMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getPermissionCache(){
        return cachePermissionMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getPasswordReset(){
        return passwordResetMutableLiveData;
    }

    /** {@inheritDoc} */
    @Override
    public void  getLoggedUser() {
        userAuthenticationRemoteDataSource.getLoggedUser();
    }

    /** {@inheritDoc} */
    @Override
    public void createInCacheResource(final String key, final String value) {
        userLocalDataSource.setCacheResource(key, value);
    }

    /** {@inheritDoc} */

    @Override
    public void getInCacheResource(final String key) {
        userLocalDataSource.getCacheResource(key);
    }

    /** {@inheritDoc} */
    @Override
    public void  signUp(final String email, final String password, final String username) {
        userAuthenticationRemoteDataSource.signUp(email, password, username);
    }

    /** {@inheritDoc} */

    @Override
    public void  signUp(final String token) {
        userAuthenticationRemoteDataSource.signUpWithGoogle(token);
    }

    /** {@inheritDoc} */

    @Override
    public void  signIn(final String email, final String password) {
        userAuthenticationRemoteDataSource.signIn(email, password);
    }

    /** {@inheritDoc} */
    @Override
    public void signUpAnonymously() {
        userAuthenticationRemoteDataSource.signUpAnonymously();
    }

    /** {@inheritDoc} */
    @Override
    public void passwordReset(String email) {
        userAuthenticationRemoteDataSource.changePassword(email);
    }

    /** {@inheritDoc} */
    @Override
    public void getGoogleCredential(
            CredentialManager credentialManager,
            GetCredentialRequest getCredentialRequest,
            Context context
    ) {

        credentialManager.getCredentialAsync(
                context,
                getCredentialRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse response) {
                        Credential credential = response.getCredential();
                        if (credential instanceof CustomCredential custom
                                && credential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                            GoogleIdTokenCredential googleIdTokenCredential =
                                    GoogleIdTokenCredential.createFrom(custom.getData());
                            onSuccessFromCredentialRetrieval(googleIdTokenCredential);
                        } else {
                            onFailureFromCredentialRetrieval(GOOGLE_CREDENTIAL_ERROR);
                        }
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException error) {
                        onFailureFromCredentialRetrieval(error.getMessage());
                    }
                });
    }

    /** {@inheritDoc} */

    @Override
    public void onSuccessFromRemoteAuthentication(User user) {
        createInCacheResource(USER, user.toString());
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccessFromRemoteAuthentication(String response) {
        userMutableLiveData.postValue(new Result.ResponseSuccess(response));
    }


    /** {@inheritDoc} */
    @Override
    public void onFailureFromRemoteAuthentication(String message) {
        userMutableLiveData.postValue(new Result.Error(message));
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccessFromCredentialRetrieval(GoogleIdTokenCredential googleIdTokenCredential) {
        if(googleIdTokenCredential != null) {
            String idToken = googleIdTokenCredential.getIdToken();
            signUp(idToken);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFailureFromCredentialRetrieval(String message) {
        userMutableLiveData.postValue(new Result.Error(message));
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccessFromPasswordReset() {
        passwordResetMutableLiveData.postValue(new Result.ResponseSuccess(PASSWORD));
    }

    /** {@inheritDoc} */
    @Override
    public void onFailureFromPasswordReset(String message) {
        passwordResetMutableLiveData.postValue(new Result.Error(message));
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccessFromLocal(String response) {
        if(response.equals(USER)) {
            cacheUserMutableLiveData.postValue(new Result.ResponseSuccess(response));
        }else{
            cachePermissionMutableLiveData.postValue(new Result.ResponseSuccess(response));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFailureFromLocal(String message) {
        if (message.equals(USERNAME)) {
            cacheUsernameMutableLiveData.postValue(new Result.Error(message));
        } else {
            cachePermissionMutableLiveData.postValue(new Result.Error(message));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccessFromLoggedUser(String response) {
        userMutableLiveData.postValue(new Result.ResponseSuccess(response));
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccessFromLoggedUser(User user) {
        userMutableLiveData.postValue(new Result.UserSuccess(user));
    }

    /** {@inheritDoc} */

    @Override
    public void onFailureFromLoggedUser(String message) {
        userMutableLiveData.postValue(new Result.Error(message)
        );
    }

    /** {@inheritDoc} */

    @Override
    public void onSuccessFromRemoteAccountUpdate(String response) {

    }

    /** {@inheritDoc} */
    @Override
    public void onFailureFromRemoteAccountUpdate(String message) {

    }
}
