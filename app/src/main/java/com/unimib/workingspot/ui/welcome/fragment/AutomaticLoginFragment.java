package com.unimib.workingspot.ui.welcome.fragment;

import static com.unimib.workingspot.util.activity_launch_utils.LaunchUtils.launchActivity;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.AUTHENTICATION_USER_VIEW_MODEL_ERROR;
import static com.unimib.workingspot.util.constants.Constants.USER_REPOSITORY_ERROR;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.firebase.FirebaseApp;
import com.unimib.workingspot.R;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.repository.user.authentication.IUserAuthenticationRepository;
import com.unimib.workingspot.ui.main.MainActivity;
import com.unimib.workingspot.ui.welcome.viewmodel.UserAuthenticationViewModel;
import com.unimib.workingspot.ui.welcome.viewmodel.UserAuthenticationViewModelFactory;
import com.unimib.workingspot.util.source.ServiceLocator;

public class AutomaticLoginFragment extends Fragment {

    private static final String TAG = RegistrationFragment.class.getSimpleName();
    private UserAuthenticationViewModel userAuthenticationViewModel;

    public AutomaticLoginFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(requireContext());

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_automatic_login, container, Boolean.FALSE);

        setUpAutomaticSignInObservers();

        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        automaticSignIn();

    }

    /**
     * Sets up observers to listen for changes in the user authentication status.
     * Navigates to main activity on success, or registration on failure.
     */
    public void setUpAutomaticSignInObservers(){
        userAuthenticationViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;

            Result result = event.getContentIfNotHandled();
            if (result == null) return;

            if (result.isSuccess()) {
                launchActivity(requireActivity(), requireContext(), MainActivity.class);
            } else {
                Navigation.findNavController(requireView()).navigate(R.id.action_automaticLoginFragment_to_registrationsFragment);
            }
        });
    }

    /**
     * Initiates the automatic sign-in process.
     */
    public void automaticSignIn() {
        userAuthenticationViewModel.automaticSignUp();
    }
}