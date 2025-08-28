    package com.unimib.workingspot.ui.welcome.fragment;

    import static com.unimib.workingspot.util.activity_launch_utils.LaunchUtils.launchActivity;
    import static com.unimib.workingspot.util.constants.AuthenticationConstants.ANONYMOUS_USER;
    import static com.unimib.workingspot.util.constants.AuthenticationConstants.HAS_SEEN_INTRO;
    import static com.unimib.workingspot.util.constants.Constants.EMPTY;
    import static com.unimib.workingspot.util.user_managing_utils.CallBacksMessages.getMessage;
    import static com.unimib.workingspot.util.user_managing_utils.text_fields.FieldCheckHandler.handleSignInFields;
    import static com.unimib.workingspot.util.constants.AuthenticationConstants.AUTHENTICATION_USER_VIEW_MODEL_ERROR;
    import static com.unimib.workingspot.util.constants.Constants.USER_REPOSITORY_ERROR;
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
    import com.unimib.workingspot.util.network.ui_methods.auth_methods.LoginMethods;
    import com.unimib.workingspot.util.credential_manager.GoogleCredentialManager;
    import com.unimib.workingspot.util.source.ServiceLocator;
    import com.unimib.workingspot.util.user_managing_utils.text_fields.FieldCheckHandler;
    import java.util.Objects;

    public class LoginFragment extends Fragment {

        private static final String TAG = RegistrationFragment.class.getSimpleName();

        private IUserAuthenticationRepository userRepository;
        private UserAuthenticationViewModel userAuthenticationViewModel;
        private NetworkManagerSingleton networkManagerSingleton;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            try {
                userRepository = ServiceLocator.getInstance().getUserAuthenticationRepository(requireActivity().getApplication());
            } catch (Exception e) {
                Log.e(TAG, USER_REPOSITORY_ERROR + e.getMessage(), e);
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
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_login, container, Boolean.FALSE);

            setUpSignInObservers();

            networkManagerSingleton = NetworkManagerSingleton.getInstance(requireActivity().getApplication());
            networkManagerSingleton.registerNetworkCallback();
            networkManagerSingleton.getConnectionStatusLiveData().observe(getViewLifecycleOwner(), isConnected ->{
                if(isConnected == NetworkState.OFFLINE) {
                    LoginMethods.manageLoginUI(view, Boolean.FALSE);
                }else{
                    LoginMethods.manageLoginUI(view, Boolean.TRUE);
                }
            });

            return  view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            MaterialButton googleSignIn = view.findViewById(R.id.google_login);

            GoogleCredentialManager googleCredentialManager = new GoogleCredentialManager();
            Pair<CredentialManager, GetCredentialRequest> credentialWidget = googleCredentialManager.createCredentialManager(requireActivity(), view);
            googleSignIn.setOnClickListener(v-> asyncCredentialRetrieve(credentialWidget));

            MaterialButton loginAsGuestButton = view.findViewById(R.id.login_as_guest);
            loginAsGuestButton.setOnClickListener(v -> anonymousSignIn());
            MaterialButton loginButton = view.findViewById(R.id.login);
            loginButton.setOnClickListener(v ->{
                    if(handleSignInFields(requireActivity())){
                        signIn();
                    }
            });

            MaterialButton passwordForgotten = view.findViewById(R.id.password_forgotten);
            passwordForgotten.setOnClickListener(v->{
                TextInputEditText emailInput = view.findViewById(R.id.email_field);
                String email = Objects.requireNonNull(emailInput.getText()).toString();
                if(FieldCheckHandler.isEmailValid(requireActivity(), view.findViewById(R.id.email_field_wrapper), email)) {
                        sendResetPasswordMail();
                }
            });

            MaterialButton registrationButton = view.findViewById(R.id.register);
            registrationButton.setOnClickListener(v-> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registrationsFragment));
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            networkManagerSingleton.unregisterNetworkCallback();
        }

        /**
         * Handles user sign-in using email and password fields.
         */
        private void signIn() {
            TextInputEditText emailField = requireView().findViewById(R.id.email_field);
            TextInputEditText passwordField = requireView().findViewById(R.id.password_field);

            String email = Objects.toString(emailField.getText());
            String password = Objects.toString(passwordField.getText());

            userAuthenticationViewModel.signIn(email, password);
        }

        /**
         * Sends a password reset email to the user.
         */
        private void sendResetPasswordMail(){
            TextInputEditText emailField = requireView().findViewById(R.id.email_field);
            if(emailField.getText() != null) {
                userAuthenticationViewModel.passwordReset(emailField.getText().toString());
            }
        }

        /**
         * Initiates anonymous sign-in for guest users.
         */
        private void anonymousSignIn() {
            userAuthenticationViewModel.anonymousSignIn();
        }

        /**
         * Retrieves Google credentials asynchronously.
         *
         * @param credentialWidget a pair containing CredentialManager and GetCredentialRequest
         */
        private void asyncCredentialRetrieve(Pair<CredentialManager, GetCredentialRequest> credentialWidget) {
            userAuthenticationViewModel.retrieveGoogleCredentials(credentialWidget.first, credentialWidget.second, requireContext());
        }

        /**
         * Checks whether the user has seen the intro screens.
         */
        private void checkHasSeenIntro() {
            userAuthenticationViewModel.getCacheLiveData(HAS_SEEN_INTRO);
        }

        /**
         * Sets observer to handle sign-in result updates.
         */
        private void setSignInObserver(){
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
                } else if (result instanceof Result.Error) {
                    String errorMessage = ((Result.Error) result).getErrorMessage();
                    String convertedMessage = getMessage(errorMessage, requireActivity());
                    Toast.makeText(requireContext(), convertedMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }


        /**
         * Sets observer to handle password reset result updates.
         */
        private void setPasswordResetObserver(){
            userAuthenticationViewModel.getPasswordResetLiveData().observe(getViewLifecycleOwner(), event -> {
                if (event == null) return;

                Result result = event.getContentIfNotHandled();
                if (result == null) return;

                if (result.isSuccess()) {
                    Toast.makeText(requireContext(), requireActivity().getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = ((Result.Error) result).getErrorMessage();
                    String convertedMessage = getMessage(errorMessage, requireActivity());
                    Toast.makeText(requireContext(), convertedMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMessage);

                }
            });
        }
        /**
         * Sets observer to handle permission cache updates and launches appropriate activity.
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
         * Sets observer to handle permission cache updates and launches appropriate activity.
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
         * Initializes all observers related to sign-in and authentication status.
         */
        private void setUpSignInObservers(){
            setSignInObserver();
            setPermissionObserver();
            setUserCacheObserver();
            setPasswordResetObserver();
        }

    }