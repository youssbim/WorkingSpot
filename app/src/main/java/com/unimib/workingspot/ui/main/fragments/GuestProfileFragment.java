package com.unimib.workingspot.ui.main.fragments;

import static com.unimib.workingspot.util.constants.Constants.ACCOUNT_USER_VIEW_MODEL_ERROR;
import static com.unimib.workingspot.util.constants.Constants.USER_REPOSITORY_ERROR;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.unimib.workingspot.R;
import com.unimib.workingspot.repository.user.account.IUserAccountRepository;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModel;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModelFactory;
import com.unimib.workingspot.ui.welcome.WelcomeActivity;
import com.unimib.workingspot.util.activity_launch_utils.LaunchUtils;
import com.unimib.workingspot.util.source.ServiceLocator;

/**
 * Fragment representing the guest user's profile.
 * Shows a disconnect button that logs the user out and redirects to the welcome screen.
 */
public class GuestProfileFragment extends Fragment {

    public GuestProfileFragment() {
        // Required empty public constructor
    }

    private final String TAG = GuestProfileFragment.class.getSimpleName();
    private IUserAccountRepository userAccountRepository;
    private UserAccountViewModel userAccountViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            userAccountRepository = ServiceLocator.getInstance().getUserAccountRepository(requireActivity().getApplication());
        } catch (Exception e) {
            Log.e(TAG, ACCOUNT_USER_VIEW_MODEL_ERROR + e.getMessage(), e);
        }

        try {
            userAccountViewModel = new ViewModelProvider(requireActivity(), new UserAccountViewModelFactory(userAccountRepository)).get(UserAccountViewModel.class);
        } catch (Exception e) {
            Log.e(TAG, USER_REPOSITORY_ERROR + e.getMessage(), e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guest_profile, container, Boolean.FALSE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton signOut = view.findViewById(R.id.disconnect);
        signOut.setOnClickListener(v -> {
            Toast.makeText(requireContext(), requireActivity().getString(R.string.guest_disconnection), Toast.LENGTH_SHORT).show();
            signOut();
        });
    }

    /**
     * Handles the sign-out logic for a guest user.
     * Clears credential state and redirects to the welcome activity.
     */
    private void signOut() {
        CredentialManager credentialManager = CredentialManager.create(requireView().getContext());
        userAccountViewModel.logout(credentialManager, new ClearCredentialStateRequest());
            LaunchUtils.launchActivity(requireActivity(), requireContext(), WelcomeActivity.class);
    }
}