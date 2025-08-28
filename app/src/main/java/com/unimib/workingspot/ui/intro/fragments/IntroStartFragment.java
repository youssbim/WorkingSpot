package com.unimib.workingspot.ui.intro.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.unimib.workingspot.R;

/**
 * {@link Fragment} that represents the initial screen of the application.
 * This fragment is displayed only when the application is started for the first time.
 */
public class IntroStartFragment extends Fragment {

    /**
     * Default constructor for HomeFragment
     */
    public IntroStartFragment() {
        // Required empty public constructor
    }

    /**
     * Called when the fragment is created
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the layout for this fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The fragment root view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_start, container, false);
    }

    /**
     * Called after the view has been created
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton startApplicationButton = view.findViewById(R.id.start_application_button);

        startApplicationButton.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_introStartFragment_to_requestGeolocalizationPermissionsFragment));
    }
}