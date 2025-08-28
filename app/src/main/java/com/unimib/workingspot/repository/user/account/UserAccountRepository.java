package com.unimib.workingspot.repository.user.account;
import static com.unimib.workingspot.util.constants.AccountConstants.LOGOUT_SUCCESS;
import static com.unimib.workingspot.util.constants.AccountConstants.PROFILE_PHOTO;
import static com.unimib.workingspot.util.constants.Constants.PASSWORD;
import static com.unimib.workingspot.util.constants.Constants.USER;
import static com.unimib.workingspot.util.constants.Constants.USERNAME;
import static com.unimib.workingspot.util.constants.Constants.USER_CLASS;
import static com.unimib.workingspot.util.constants.Constants.EMAIL;
import androidx.credentials.ClearCredentialStateRequest;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.lifecycle.MutableLiveData;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.model.User;
import com.unimib.workingspot.source.user.abstracts.BaseUserAccountRemoteDataSource;
import com.unimib.workingspot.source.user.abstracts.BaseUserLocalDataSource;
import com.unimib.workingspot.source.user.abstracts.BaseUserRemoteDataSource;
import com.unimib.workingspot.util.user_managing_utils.account.InitializationState;
import java.util.concurrent.Executors;

/**
 * Implementation of {@link IUserAccountRepository} and {@link UserAccountResponseCallback}.
 * <p>
 * This repository is responsible for managing all user account-related operations
 * by coordinating between remote and local data sources.
 * It serves as a bridge between the app's business logic and data access layers.
 * </p>
 *
 * <p>Supported functionalities include:</p>
 * <ul>
 *   <li>Fetching and updating user profile information (e.g., username, email, profile picture)</li>
 *   <li>Local user data caching and initialization state tracking</li>
 *   <li>Handling secure logout with Android Credential Manager</li>
 *   <li>Password reset operations</li>
 *   <li>Syncing user data across Firebase and local storage</li>
 * </ul>
 *
 * <p>
 * Dependencies include:
 * <ul>
 *   <li>{@link BaseUserLocalDataSource} for local cache and preferences</li>
 *   <li>{@link BaseUserRemoteDataSource} for Firebase remote operations</li>
 *   <li>{@link BaseUserAccountRemoteDataSource} for Firebase account-specific updates</li>
 * </ul>
 * </p>
 */

public class UserAccountRepository implements IUserAccountRepository, UserAccountResponseCallback {
    private final MutableLiveData<Result> resourceUpdateLiveData;
    private final MutableLiveData<Result> resourceLocalLiveData;
    private final MutableLiveData<Result> resourceRemoteLiveData;
    private final MutableLiveData<Result> userLiveData;
    private final BaseUserLocalDataSource userLocalDataSource;
    private final BaseUserRemoteDataSource userRemoteDataSource;

    private final BaseUserAccountRemoteDataSource userAccountRemoteDataSource;
    private InitializationState initializationState;

    /**
     * Constructs the UserAccountRepository with specified data sources.
     * @param userLocalDataSource local data source for user data
     * @param userRemoteDataSource remote data source for user data
     * @param userAccountRemoteDataSource remote account-specific data source
     */
    public UserAccountRepository(BaseUserLocalDataSource userLocalDataSource,
                                 BaseUserRemoteDataSource userRemoteDataSource,
                                 BaseUserAccountRemoteDataSource userAccountRemoteDataSource){
        resourceUpdateLiveData = new MutableLiveData<>();
        resourceLocalLiveData = new MutableLiveData<>();
        resourceRemoteLiveData = new MutableLiveData<>();
        userLiveData = new MutableLiveData<>();
        this.userLocalDataSource = userLocalDataSource;
        this.userRemoteDataSource = userRemoteDataSource;
        this.userAccountRemoteDataSource = userAccountRemoteDataSource;
        this.userLocalDataSource.setUserCallback(this);
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userAccountRemoteDataSource.setUserCallback(this);
        setInitializationState(InitializationState.INITIALIZING);
    }

    /** {@inheritDoc} */
    @Override
    public MutableLiveData<Result> getResourceLocalLiveData(){
        return resourceLocalLiveData;
    }

    /** {@inheritDoc} */
    @Override
    public MutableLiveData<Result> getResourceRemoteLiveData(){
        return resourceRemoteLiveData;
    }

    /** {@inheritDoc} */
    @Override
    public MutableLiveData<Result> getResourceUpdateLiveData(){
        return resourceUpdateLiveData;
    }

    /** {@inheritDoc} */
    @Override
    public MutableLiveData<Result> getUserLiveData(){
        return userLiveData;
    }

    /** {@inheritDoc} */
    @Override
    public void getLoggedUser() {
         userAccountRemoteDataSource.getLoggedUser();
    }

    /** {@inheritDoc} */
    @Override
    public void getCacheResource(String key) {
        userLocalDataSource.getCacheResource(key);
    }

    /** {@inheritDoc} */
    @Override
    public void getProfilePicture() {
        userRemoteDataSource.getProfilePicture();
    }

    /** {@inheritDoc} */
    @Override
    public void updateProfilePicture(String encodedProfilePhoto) {
        userRemoteDataSource.saveProfilePicture(encodedProfilePhoto);
    }
    /** {@inheritDoc} */
    public void logout(){
        userLocalDataSource.clearCache();
        userLocalDataSource.deleteCacheResource(USER);
        userAccountRemoteDataSource.logout();
    }


    /** {@inheritDoc} */
    public MutableLiveData<Result> clearGoogleCredential(
            CredentialManager credentialManager,
            ClearCredentialStateRequest clearCredentialStateRequest
    ) {

        credentialManager.clearCredentialStateAsync(
                clearCredentialStateRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>(){
                    @Override
                    public void onResult(Void unused) {
                        onSuccessLogout();
                    }

                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        onFailureLogout(e.getMessage());
                    }
                });

        return userLiveData;
    }

    /** {@inheritDoc} */
    @Override
    public void updateUsername(final String username){
        userAccountRemoteDataSource.changeUsername(username);
    }

    /** {@inheritDoc} */
    @Override
    public void updateEmail(final String email) {
        userAccountRemoteDataSource.changeEmail(email);
    }

    /** {@inheritDoc} */
    @Override
    public void updatePassword(final String email){
        userAccountRemoteDataSource.changePassword(email);
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccessFromRemote(String response) {
        if(getInitializationState() == InitializationState.COMPLETED) {
            resourceUpdateLiveData.postValue(new Result.ResponseSuccess(PROFILE_PHOTO));
        }
        userLocalDataSource.updateProfilePhotoInCache(response);
    }

    /** {@inheritDoc} */
    @Override
    public void onFailureFromRemote(String message) {
        resourceRemoteLiveData.postValue(new Result.Error(message));
        getCacheResource(USER);
    }

    /** {@inheritDoc} */
    public void onSuccessFromRemoteAccountUpdate(String resource) {
        if(resource.equals(EMAIL)) {
            resourceUpdateLiveData.postValue(new Result.ResponseSuccess(EMAIL));
        }else{
            resourceUpdateLiveData.postValue(new Result.ResponseSuccess(USERNAME));
            userLocalDataSource.updateUsernameInCache(resource);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFailureFromRemoteAccountUpdate(String message) {
        resourceUpdateLiveData.postValue(new Result.Error(message));
        getCacheResource(USER);
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccessFromPasswordReset() {
        resourceUpdateLiveData.postValue(new Result.ResponseSuccess(PASSWORD));
    }

    /** {@inheritDoc} */
    @Override
    public void onFailureFromPasswordReset(String message) {
        resourceUpdateLiveData.postValue(new Result.Error(message));
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccessFromLocal(String response) {
        if(response.startsWith(USER_CLASS)) {
            User user = User.userFromString(response);
            if (user.getProfilePicture() == null) {
               setInitializationState(InitializationState.INITIALIZING);
                getProfilePicture();
            } else {
                setInitializationState(InitializationState.COMPLETED);
                resourceLocalLiveData.postValue(new Result.ResponseSuccess(response));
            }
        }else if(response.equals(PROFILE_PHOTO) || response.equals(USERNAME)){
            getCacheResource(USER);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFailureFromLocal(String message) {
        resourceLocalLiveData.postValue(new Result.Error(message));
    }

    /** {@inheritDoc} */

    @Override
    public void onSuccessFromLoggedUser(String response) {
        userLiveData.postValue(new Result.ResponseSuccess(response));
    }

    /** {@inheritDoc} */

    @Override
    public void onSuccessFromLoggedUser(User user) {
        userLiveData.postValue(new Result.UserSuccess(user));
    }

    /** {@inheritDoc} */

    @Override
    public void onFailureFromLoggedUser(String message) {
        userLiveData.postValue(new Result.Error(message)
        );
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccessLogout() {
        userLiveData.postValue(new Result.ResponseSuccess(LOGOUT_SUCCESS));
    }

    /** {@inheritDoc} */

    @Override
    public void onFailureLogout(String message) {
        userLiveData.postValue(new Result.Error(message));
    }


    /**
     * Returns the current initialization state.
     * @return current {@link InitializationState}
     */
    public InitializationState getInitializationState() {
        return initializationState;
    }

    /**
     * Sets the current initialization state.
     * @param initializationState the new {@link InitializationState} to set
     */
    public void setInitializationState(InitializationState initializationState) {
        this.initializationState = initializationState;
    }
}
