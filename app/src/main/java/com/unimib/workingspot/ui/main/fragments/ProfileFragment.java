package com.unimib.workingspot.ui.main.fragments;

import static com.unimib.workingspot.util.activity_launch_utils.LaunchUtils.launchActivity;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.ANONYMOUS_USER;
import static com.unimib.workingspot.util.constants.Constants.ACCOUNT_USER_VIEW_MODEL_ERROR;
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
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.unimib.workingspot.R;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.repository.user.account.IUserAccountRepository;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModel;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModelFactory;
import com.unimib.workingspot.ui.welcome.WelcomeActivity;
import com.unimib.workingspot.util.source.ServiceLocator;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private UserAccountViewModel userAccountViewModel ;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserAccountRepository userRepository;
        try {
            userRepository = ServiceLocator.getInstance().getUserAccountRepository(requireActivity().getApplication());
        } catch (Exception e) {
            Log.e(TAG, USER_REPOSITORY_ERROR + e.getMessage(), e);
            return;
        }

        try {
            userAccountViewModel = new ViewModelProvider(requireActivity(), new UserAccountViewModelFactory(userRepository)).get(UserAccountViewModel.class);
        } catch (Exception e) {
            Log.e(TAG, ACCOUNT_USER_VIEW_MODEL_ERROR + e.getMessage(), e);
        }
    }

    /**
     * Inflates the fragment layout, observes user LiveData, and handles navigation logic
     * based on user authentication status.
     *
     * @param inflater the LayoutInflater to inflate views
     * @param container the parent ViewGroup
     * @param savedInstanceState the saved instance state bundle
     * @return the root View of the fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userAccountViewModel.getUserLiveData().observe(getViewLifecycleOwner(), result -> {
            NavController navController = Navigation.findNavController(view);
            NavDestination currentDestination = navController.getCurrentDestination();
            if (currentDestination != null && currentDestination.getId() == R.id.profileFragment) {
                if (result.isSuccess()) {
                    if (result instanceof Result.ResponseSuccess success) {
                        if (success.getResponse().equals(ANONYMOUS_USER)) {
                            if (Objects.requireNonNull(Navigation.findNavController(view).getCurrentDestination()).getId() != R.id.guestProfileFragment) {
                                Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_guestProfileFragment);
                            }
                        }
                    } else if (result instanceof Result.UserSuccess success) {
                        if (success.getUser() != null) {
                            if (Objects.requireNonNull(Navigation.findNavController(view).getCurrentDestination()).getId() != R.id.loggedProfileFragment) {
                                Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_loggedProfileFragment);
                            }
                        }
                    }
                } else {
                    launchActivity(requireActivity(), view.getContext(), WelcomeActivity.class);
                }
            }
        });

        return  view;
    }

    /**
     * Called after the view is created. Triggers fetching the logged user data.
     *
     * @param view the root View of the fragment
     * @param savedInstanceState the saved instance state bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userAccountViewModel.getLoggedUser();
    }
}
