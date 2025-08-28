package com.unimib.workingspot.ui.welcome.viewmodel;

import android.content.Context;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.unimib.workingspot.model.Consumable;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.repository.user.authentication.IUserAuthenticationRepository;

public class UserAuthenticationViewModel extends ViewModel {
    private final String TAG = UserAuthenticationViewModel.class.getSimpleName();
    private final IUserAuthenticationRepository userAuthenticationRepository;

    private final MutableLiveData<Consumable<Result>> userAuthenticationLiveData;
    private LiveData<Result> userCacheLiveData;
    private LiveData<Result> usernameCacheLiveData;
    private LiveData<Result> permissionCacheLiveData;
    private final MutableLiveData<Consumable<Result>> passwordResetLiveData;

    private final Observer<Result> authenticationObserver;
    private final Observer<Result> passwordResetObserver;
    private boolean authenticationObserverAdded = Boolean.FALSE;
    private boolean passwordResetObserverAdded = Boolean.FALSE;

    /**
     * ViewModel class responsible for managing user authentication logic and data.
     * Acts as an intermediary between the UI and the UserAuthenticationRepository.
     */
    public UserAuthenticationViewModel(IUserAuthenticationRepository userAuthenticationRepository) {
        this.userAuthenticationLiveData = new MutableLiveData<>();
        authenticationObserver = result -> userAuthenticationLiveData.postValue(new Consumable<>(result));
        this.passwordResetLiveData = new MutableLiveData<>();
        passwordResetObserver = result -> passwordResetLiveData.postValue(new Consumable<>(result));
        this.userAuthenticationRepository = userAuthenticationRepository;
    }

    /**
     * Returns LiveData for observing user authentication events.
     * Adds an observer to the repository if not already added.
     *
     * @return LiveData wrapping authentication event results.
     */
    public LiveData<Consumable<Result>> getUserMutableLiveData() {
        if (!authenticationObserverAdded) {
            authenticationObserverAdded = Boolean.TRUE;
            userAuthenticationRepository.getUser().observeForever(authenticationObserver);
        }
        return userAuthenticationLiveData;
    }

    /**
     * Returns LiveData containing cached user data results.
     *
     * @return LiveData of cached user results.
     */
    public LiveData<Result> getUserCachelLiveData(){
        if(userCacheLiveData == null){
            userCacheLiveData = userAuthenticationRepository.getUserCache();
        }
        return userCacheLiveData;
    }

    /**
     * Returns LiveData containing cached username data results.
     *
     * @return LiveData of cached username results.
     */
    public LiveData<Result> getUsernameCachelLiveData(){
        if(usernameCacheLiveData == null){
            usernameCacheLiveData = userAuthenticationRepository.getUsernameCache();
        }
        return usernameCacheLiveData;
    }

    /**
     * Returns LiveData containing cached permission data results.
     *
     * @return LiveData of cached permission results.
     */
    public LiveData<Result> getPermissionCacheLiveData(){
        if(permissionCacheLiveData == null){
            permissionCacheLiveData = userAuthenticationRepository.getPermissionCache();
        }
        return permissionCacheLiveData;
    }

    /**
     * Returns LiveData for observing password reset events.
     * Adds an observer to the repository if not already added.
     *
     * @return LiveData wrapping password reset event results.
     */
    public LiveData<Consumable<Result>> getPasswordResetLiveData(){
        if (!passwordResetObserverAdded) {
            passwordResetObserverAdded = Boolean.TRUE;
            userAuthenticationRepository.getPasswordReset().observeForever(passwordResetObserver);
        }
        return passwordResetLiveData;
    }

    /**
     * Triggers retrieval of cached resource associated with the provided key.
     *
     * @param key Key identifying the cached resource.
     */
   public void getCacheLiveData(final String key) {
        userAuthenticationRepository.getInCacheResource(key);
   }

    /**
     * Initiates retrieval of Google credentials asynchronously.
     *
     * @param credentialManager     CredentialManager instance.
     * @param getCredentialRequest  Credential request parameters.
     * @param context               Android context.
     */
    public void retrieveGoogleCredentials(CredentialManager credentialManager, GetCredentialRequest getCredentialRequest, Context context){
        userAuthenticationRepository.getGoogleCredential(credentialManager, getCredentialRequest, context);
    }

    /**
     * Performs user sign-up with the provided email, password, and username.
     *
     * @param email    User's email address.
     * @param password User's chosen password.
     * @param username User's chosen username.
     */
    public void signUp(
            final String email, final String password, final String username) {
        userAuthenticationRepository.signUp(email, password, username);
    }

    /**
     * Performs user sign-in with the provided email and password.
     *
     * @param email    User's email address.
     * @param password User's password.
     */
    public void signIn(
            final String email, final String password) {
        userAuthenticationRepository.signIn(email, password);
    }

    /**
     * Automatically attempts to sign in the currently logged-in user.
     */
    public void automaticSignUp(){
        userAuthenticationRepository.getLoggedUser();
    }

    /**
     * Performs anonymous sign-in for users opting to skip authentication.
     */
    public void anonymousSignIn(){
        userAuthenticationRepository.signUpAnonymously();
    }

    /**
     * Initiates a password reset process for the provided email.
     *
     * @param email Email address to send password reset instructions.
     */
    public void passwordReset(final String email){
        userAuthenticationRepository.passwordReset(email);
    }

    /**
     * Cleans up observers when ViewModel is cleared.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (authenticationObserverAdded) {
            userAuthenticationRepository.getUser().removeObserver(authenticationObserver);
        }
        if (passwordResetObserverAdded) {
            userAuthenticationRepository.getPasswordReset().removeObserver(passwordResetObserver);
        }
    }
}
