package com.unimib.workingspot.ui.welcome.fragment;

import static com.unimib.workingspot.util.activity_launch_utils.LaunchUtils.launchActivity;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.ANONYMOUS_USER;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.AUTHENTICATION_USER_VIEW_MODEL_ERROR;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.HAS_SEEN_INTRO;
import static com.unimib.workingspot.util.constants.Constants.EMPTY;
import static com.unimib.workingspot.util.constants.Constants.USER_REPOSITORY_ERROR;
import static com.unimib.workingspot.util.user_managing_utils.CallBacksMessages.getMessage;
import static com.unimib.workingspot.util.user_managing_utils.text_fields.FieldCheckHandler.handleSignUpFields;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.workingspot.R;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.repository.user.authentication.IUserAuthenticationRepository;
import com.unimib.workingspot.ui.main.MainActivity;
import com.unimib.workingspot.ui.intro.IntroActivity;
import com.unimib.workingspot.ui.welcome.viewmodel.UserAuthenticationViewModel;
import com.unimib.workingspot.ui.welcome.viewmodel.UserAuthenticationViewModelFactory;
import com.unimib.workingspot.util.network.NetworkManagerSingleton;
import com.unimib.workingspot.util.network.NetworkState;
import com.unimib.workingspot.util.network.ui_methods.auth_methods.RegistrationMethods;
import com.unimib.workingspot.util.source.ServiceLocator;
import com.unimib.workingspot.util.credential_manager.GoogleCredentialManager;

import java.util.Objects;

public class RegistrationFragment extends Fragment {

    private static final String TAG = RegistrationFragment.class.getSimpleName();

    private UserAuthenticationViewModel userAuthenticationViewModel;
    private NetworkManagerSingleton networkManagerSingleton;

    public RegistrationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserAuthenticationRepository userRepository;
        try {
            userRepository = ServiceLocator.getInstance().getUserAuthenticationRepository(requireActivity().getApplication());
        } catch (Exception e) {
            Log.e(TAG, USER_REPOSITORY_ERROR + e.getMessage(), e);
            return;
        }

        try {
            userAuthenticationViewModel = new ViewModelProvider(requireActivity(), new UserAuthenticationViewModelFactory(userRepository)).get(UserAuthenticationViewModel.class);
        } catch (Exception e) {
            Log.e(TAG, AUTHENTICATION_USER_VIEW_MODEL_ERROR + e.getMessage(), e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, Boolean.FALSE);

        setUpSignUpObservers();

        networkManagerSingleton = NetworkManagerSingleton.getInstance(requireActivity().getApplication());
        networkManagerSingleton.registerNetworkCallback();
        networkManagerSingleton.getConnectionStatusLiveData().observe(getViewLifecycleOwner(), isConnected ->{
            if(isConnected == NetworkState.OFFLINE) {
                RegistrationMethods.manageRegistrationUI(view, Boolean.FALSE);
            }else{
                RegistrationMethods.manageRegistrationUI(view, Boolean.TRUE);
            }
        });

        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton googleSignInButton = view.findViewById(R.id.google_registration);
        GoogleCredentialManager googleCredentialManager = new GoogleCredentialManager();
        Pair<CredentialManager, GetCredentialRequest> credentialWidget = googleCredentialManager.createCredentialManager(requireActivity(), view);
        googleSignInButton.setOnClickListener(v -> asyncCredentialRetrieve(credentialWidget));

        MaterialButton loginAsGuestButton = view.findViewById(R.id.login_as_guest);
        loginAsGuestButton.setOnClickListener(v -> anonymousSignIn());

        MaterialButton loginButton = view.findViewById(R.id.login);
        loginButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_registrationsFragment_to_loginFragment));

        MaterialButton registrationButton = view.findViewById(R.id.registration);
        registrationButton.setOnClickListener(v -> {
            if (handleSignUpFields(requireActivity())){
                signUp();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkManagerSingleton.unregisterNetworkCallback();
    }

    /**
     * Signs up a user using entered email, password, and username.
     */
    private void signUp() {
        TextInputEditText emailField = requireView().findViewById(R.id.email_field);
        TextInputEditText passwordField = requireView().findViewById(R.id.password_field);
        TextInputEditText usernameField = requireView().findViewById(R.id.username_field);

        String email = Objects.toString(emailField.getText());
        String password = Objects.toString(passwordField.getText());
        String username = Objects.toString(usernameField.getText());
        userAuthenticationViewModel.signUp(email, password, username);
    }

    /**
     * Performs anonymous sign-in for guest users.
     */
    private void anonymousSignIn() {
        userAuthenticationViewModel.anonymousSignIn();
    }

    /**
     * Asynchronously retrieves Google credentials for sign-in.
     *
     * @param credentialWidget Pair of CredentialManager and GetCredentialRequest.
     */
    private void asyncCredentialRetrieve(Pair<CredentialManager, GetCredentialRequest> credentialWidget) {
        userAuthenticationViewModel.retrieveGoogleCredentials(credentialWidget.first, credentialWidget.second, requireContext());
    }

    /**
     * Observes username cache LiveData and navigates accordingly.
     */
    private void setUsernameCacheObserver(){
        userAuthenticationViewModel.getUsernameCachelLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                checkHasSeenIntro();
            } else {
                Navigation.findNavController(requireView()).navigate(R.id.action_automaticLoginFragment_to_registrationsFragment);
            }
        });
    }


    /**
     * Checks if the user has seen the introduction screen.
     */
    private void checkHasSeenIntro() {
        userAuthenticationViewModel.getCacheLiveData(HAS_SEEN_INTRO);
    }

    /**
     * Sets observer to watch sign-up results and react accordingly.
     */
    private void setSignUpObserver(){
        userAuthenticationViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;

            Result result = event.getContentIfNotHandled();
            if (result == null) return;

            if (result.isSuccess()){
                if(result instanceof Result.ResponseSuccess){
                    if(((Result.ResponseSuccess) result).getResponse().equals(ANONYMOUS_USER)){
                        checkHasSeenIntro();
                    }
                }
            }else if (result instanceof Result.Error) {
                String errorMessage = ((Result.Error) result).getErrorMessage();
                String convertedMessage = getMessage(errorMessage, requireActivity());
                Toast.makeText(requireContext(), convertedMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets observer for permission cache updates to navigate or launch activities.
     */
    private void setPermissionObserver(){
        userAuthenticationViewModel.getPermissionCacheLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                launchActivity(requireActivity(), requireContext(), MainActivity.class);
            }else{
                String errorMessage = ((Result.Error) result).getErrorMessage();
                if(errorMessage.equals(EMPTY)) {
                    launchActivity(requireActivity(), requireContext(), IntroActivity.class);
                }else {
                    Log.e(TAG, errorMessage);
                }
            }
        });
    }

    /**
     * Sets observer for user cache LiveData to handle navigation flow.
     */
    private void setUserCacheObserver(){
        userAuthenticationViewModel.getUserCachelLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                checkHasSeenIntro();
            } else {
                Navigation.findNavController(requireView()).navigate(R.id.action_automaticLoginFragment_to_registrationsFragment);
            }
        });
    }

    /**
     * Initializes all observers related to sign-up and user authentication.
     */
    private void setUpSignUpObservers(){
        setSignUpObserver();
        setPermissionObserver();
        setUserCacheObserver();
        setUsernameCacheObserver();
    }


}