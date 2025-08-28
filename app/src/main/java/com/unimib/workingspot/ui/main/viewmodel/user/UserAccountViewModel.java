package com.unimib.workingspot.ui.main.viewmodel.user;

import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.unimib.workingspot.model.Consumable;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.repository.user.account.IUserAccountRepository;

public class UserAccountViewModel extends ViewModel {

    private final String TAG = UserAccountViewModel.class.getSimpleName();

    private final IUserAccountRepository userAccountRepository;
    private LiveData<Result> userLiveData;
    private LiveData<Result> cacheLiveData;
    private final MutableLiveData<Consumable<Result>> updateLiveData;
    private final MutableLiveData<Consumable<Result>> remoteLiveData;
    private LiveData<Result> logoutLiveData;
    private final Observer<Result> remoteObserver;
    private final Observer<Result> updateObserver;
    private final Observer<Result> logoutObserver;
    private boolean remoteObserverAdded = Boolean.FALSE;
    private boolean updateObserverAdded = Boolean.FALSE;

    /**
     * Constructs a UserAccountViewModel with the provided UserAccountRepository.
     *
     * @param userAccountRepository the user account repository to interact with data sources
     */
    public UserAccountViewModel(IUserAccountRepository userAccountRepository) {
        this.updateLiveData = new MutableLiveData<>();
        updateObserver = result -> updateLiveData.postValue(new Consumable<>(result));
        this.remoteLiveData = new MutableLiveData<>();
        remoteObserver = result -> remoteLiveData.postValue(new Consumable<>(result));
        logoutObserver =  result -> {  if (result.isSuccess()) {userAccountRepository.logout();}};
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Returns the LiveData representing the user result data.
     *
     * @return LiveData of Result representing the user data
     */
    public LiveData<Result> getUserLiveData() {
        if(userLiveData == null) {
            userLiveData = userAccountRepository.getUserLiveData();
        }
        return userLiveData;
    }

    /**
     * Returns the LiveData representing cached user data.
     *
     * @return LiveData of Result representing cached user data
     */
    public LiveData<Result> getCacheLiveData() {
        if(cacheLiveData == null) {
            cacheLiveData = userAccountRepository.getResourceLocalLiveData();
        }
        return cacheLiveData;
    }

    /**
     * Returns LiveData for remote updates wrapped in Consumable.
     * Adds an observer if not already added.
     *
     * @return LiveData of Consumable<Result> for remote data updates
     */
    public LiveData<Consumable<Result>> getRemoteLiveData() {
        if (!remoteObserverAdded) {
            remoteObserverAdded = Boolean.TRUE;
            userAccountRepository.getResourceRemoteLiveData().observeForever(remoteObserver);
        }
        return remoteLiveData;
    }


    /**
     * Returns LiveData for update events wrapped in Consumable.
     * Adds an observer if not already added.
     *
     * @return LiveData of Consumable<Result> for update events
     */
    public LiveData<Consumable<Result>> getUpdateLiveData() {
        if (!updateObserverAdded) {
            updateObserverAdded = Boolean.TRUE;
            userAccountRepository.getResourceUpdateLiveData().observeForever(updateObserver);
        }
        return updateLiveData;
    }

    /**
     * Initiates fetching the currently logged user.
     */
    public void getLoggedUser(){
        userAccountRepository.getLoggedUser();
    }

    /**
     * Performs logout operation using provided CredentialManager and ClearCredentialStateRequest.
     *
     * @param credentialManager the CredentialManager to clear credentials
     * @param clearCredentialStateRequest the request to clear credential state
     */
    public void logout(CredentialManager credentialManager, ClearCredentialStateRequest clearCredentialStateRequest){
        logoutLiveData = userAccountRepository.clearGoogleCredential(credentialManager, clearCredentialStateRequest);
        logoutLiveData.observeForever(logoutObserver);
    }


    /**
     * Retrieves cached user resource by key.
     *
     * @param key the key identifying the cached resource
     */
    public void getUser(final String key) {
        userAccountRepository.getCacheResource(key);
    }

    /**
     * Updates the username of the user.
     *
     * @param username the new username to set
     */
    public void setUsername(final String username) {
        userAccountRepository.updateUsername(username);
    }

    /**
     * Updates the email of the user.
     *
     * @param email the new email to set
     */
    public void setEmail(final String email) {
        userAccountRepository.updateEmail(email);
    }

    /**
     * Updates the password of the user.
     *
     * @param email the new password to set
     */
    public void setPassword(final String email) {
        userAccountRepository.updatePassword(email);
    }

    /**
     * Updates the profile picture of the user.
     *
     * @param encodedImage the new profile picture encoded as a String
     */
    public void updateProfilePicture(final String encodedImage) {
        userAccountRepository.updateProfilePicture(encodedImage);
    }

    /**
     * Cleans up observers when the ViewModel is cleared.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (remoteObserverAdded) {
            userAccountRepository.getResourceRemoteLiveData().removeObserver(remoteObserver);
        }
        if (updateObserverAdded) {
            userAccountRepository.getResourceUpdateLiveData().removeObserver(updateObserver);
        }
        if (logoutLiveData != null) {
            logoutLiveData.removeObserver(logoutObserver);
        }
    }
}
