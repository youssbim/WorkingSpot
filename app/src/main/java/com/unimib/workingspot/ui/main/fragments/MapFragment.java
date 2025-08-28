package com.unimib.workingspot.ui.main.fragments;

import com.unimib.workingspot.util.map_managing_utils.MapUtils;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.workingspot.R;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.model.User;
import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.repository.user.account.IUserAccountRepository;
import com.unimib.workingspot.repository.work_place.IWorkPlaceRepository;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModel;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModelFactory;
import com.unimib.workingspot.ui.main.viewmodel.work_place.WorkPlaceViewModel;
import com.unimib.workingspot.ui.main.viewmodel.work_place.WorkPlaceViewModelFactory;
import com.unimib.workingspot.util.bitmap.BitMapManager;
import com.unimib.workingspot.util.network.NetworkManagerSingleton;
import com.unimib.workingspot.util.network.NetworkState;
import com.unimib.workingspot.util.source.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

import static com.unimib.workingspot.util.constants.WorkPlacesConstants.GOOGLE_PLACES_API_KEY;
import static com.unimib.workingspot.util.constants.Constants.USER;


/**
 * A {@link Fragment} that displays a Google Map, allowing users to view existing
 * workplaces, add new ones, and manage their favorites.
 * This class primarily handles UI interactions and observes data changes from ViewModels.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private View rootView;
    private GoogleMap googleMap;
    private final LatLng MILAN_COORDINATES = new LatLng(45.4642, 9.1900); // Milan, because why not?
    private final List<WorkPlace> workPlaces = new ArrayList<>();
    private LatLng selectedLatLng;
    private String selectedName;
    private String selectedAddress;
    private boolean selectedOutside;
    private UserAccountViewModel userAccountViewModel;
    private WorkPlaceViewModel workPlaceViewModel;
    private ActivityResultLauncher<Intent> autocompleteLauncher;
    private String UID;
    private BitMapManager bitMapManager; // For handling those lovely bitmaps!
    private NetworkManagerSingleton networkManager;
    private List<WorkPlace> pendingWorkPlaces = null;


    /**
     * Called when the fragment is first created.
     * Here we initialize our ViewModels and the bitmap manager.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViewModels();
        bitMapManager = new BitMapManager();
        networkManager = NetworkManagerSingleton.getInstance(requireActivity().getApplication());
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is where we inflate our layout.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        FloatingActionButton fab = rootView.findViewById(R.id.add_location_button);

        // Registers network callbacks
        networkManager.registerNetworkCallback();
        networkManager.getConnectionStatusLiveData().observe(getViewLifecycleOwner(), networkState -> fab.setEnabled(networkState != NetworkState.OFFLINE));

        return rootView;
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned,
     * but before any saved state has been restored in to the view.
     * Set up UI elements, observers, and map initialization.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObservers();

        // Initialize Places SDK if it hasn't been already.
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), GOOGLE_PLACES_API_KEY);
        }

        // Get the user's UID to manage saved workplaces.
        userAccountViewModel.getUser(USER);

        // Launcher for the Google Places Autocomplete activity.
        // It handles the result of the place search.
        autocompleteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == Activity.RESULT_OK && data != null) {
                        Place place = Autocomplete.getPlaceFromIntent(data);
                        selectedLatLng = place.getLocation();
                        selectedName = place.getDisplayName();
                        selectedAddress = place.getFormattedAddress();
                        if (selectedLatLng != null && selectedName != null) {
                            showAddConfirmationDialogWrapper();
                        } else {
                            Toast.makeText(getContext(), R.string.location_details_incomplete, Toast.LENGTH_SHORT).show();
                        }
                    } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR && data != null) {
                        Status status = Autocomplete.getStatusFromIntent(data);
                        Toast.makeText(getContext(), getString(R.string.autocomplete_error) + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Get the map fragment and asynchronously prepare the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up the floating action button to launch the place search.
        FloatingActionButton fab = view.findViewById(R.id.add_location_button);
        fab.setOnClickListener(v -> MapUtils.prepareAndLaunchPlaceSearch(requireContext(), autocompleteLauncher, GOOGLE_PLACES_API_KEY));
    }

    /**
     * Called when the map is ready to be used. This is our entry point for map interactions.
     * @param map The {@link GoogleMap} object that is ready to be used.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MILAN_COORDINATES, 13));
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        setupListeners();

        // Now that map is ready, proceed
        if (pendingWorkPlaces != null) {
            onWorkPlaceDatabaseRetrieveSuccess(pendingWorkPlaces);
            pendingWorkPlaces = null;
        } else {
            workPlaceViewModel.getWorkPlaces();
        }
    }


    /**
     * Callback for when all workplaces are successfully retrieved from the database.
     * Clears existing workplaces and adds new markers.
     * @param workPlaceList A {@link List} of {@link WorkPlace} objects retrieved from the database.
     */
    public void onWorkPlaceDatabaseRetrieveSuccess(List<WorkPlace> workPlaceList) {
        if (googleMap == null) {
            // Map not ready, store the list for later use.
            pendingWorkPlaces = new ArrayList<>(workPlaceList);
            return;
        }

        workPlaces.clear();
        workPlaces.addAll(workPlaceList);

        for (WorkPlace wp : workPlaces) {
            MapUtils.addMapMarker(googleMap, wp, BitmapDescriptorFactory.HUE_VIOLET);
        }

        if (UID != null) {
            workPlaceViewModel.getSavedWorkPlaces(UID);
        }
    }

    /**
     * Callback for when the user's saved workplaces are successfully retrieved.
     * Updates the 'saved' status of existing workplaces.
     * @param savedWorkPlaces A {@link List} of {@link WorkPlace} objects that are saved by the user.
     */
    public void onSavedWorkPlaceDatabaseRetrieveSuccess(List<WorkPlace> savedWorkPlaces) {
        for (WorkPlace fav : savedWorkPlaces) {
            int index = workPlaces.indexOf(fav);
            if (index != -1) {
                workPlaces.get(index).setSaved(true);
            }
        }
    }

    /**
     * Callback for when a new workplace is successfully created in the database.
     * Adds a new marker for the newly created workplace.
     * @param workPlace The {@link WorkPlace} object that was just created.
     */
    public void onWorkPlaceDatabaseCreateSuccess(WorkPlace workPlace) {
        MapUtils.addMapMarker(googleMap, workPlace, BitmapDescriptorFactory.HUE_VIOLET);
    }

    /**
     * Callback for successful saving of a workplace.
     */
    public void onWorkPlaceDatabaseSaveSuccess() {}

    /**
     * Callback for successful deletion of a workplace.
     */
    public void onWorkPlaceDatabaseDeleteSuccess() {}

    /**
     * Callback for any database operation failure related to workplaces.
     * Displays a toast message to the user.
     */
    public void onWorkPlaceDatabaseFailure() {
        Toast.makeText(getContext(), getString(R.string.workplace_retrieve_error_message), Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets up listeners for map interactions, like marker clicks and map clicks.
     */
    private void setupListeners() {
        googleMap.setOnMarkerClickListener(marker -> {
            Object tag = marker.getTag();
            if (tag instanceof WorkPlace) {
                showCard((WorkPlace) tag); // Show detailed card for the workplace.
            } else {
                hideCard(); // Hide card if it's not a workplace marker.
            }
            return true;
        });
        googleMap.setOnMapClickListener(latLng -> hideCard()); // Hide card if user clicks anywhere on the map.
    }

    /**
     * Displays the detailed card for a specific {@link WorkPlace}.
     * Animates the floating action button to move out of the way.
     * @param wp The {@link WorkPlace} object to display details for.
     */
    private void showCard(WorkPlace wp) {
        View card = rootView.findViewById(R.id.map_card);
        card.setVisibility(View.VISIBLE);

        ((TextView) card.findViewById(R.id.card_title)).setText(wp.getName());
        ((TextView) card.findViewById(R.id.card_subtitle)).setText(wp.getAddress());

        ImageView img = card.findViewById(R.id.card_image);
        Bitmap bitmap = bitMapManager.decodeBitmap(wp.getB64PhotoEncoding());
        img.setImageBitmap(bitmap);
        if (bitmap == null) img.setImageResource(R.drawable.baseline_home_24); // Default image if no photo.

        ImageButton fav = card.findViewById(R.id.card_favorite);

        if(UID == null) {
            fav.setVisibility(View.GONE);
        } else {
            fav.setImageResource(wp.isSaved() ? R.drawable.baseline_favorite_24 : R.drawable.outline_favorite_border_24);
            fav.setOnClickListener(v -> {
                wp.setSaved(!wp.isSaved());
                fav.setImageResource(wp.isSaved() ? R.drawable.baseline_favorite_24 : R.drawable.outline_favorite_border_24);

                if (wp.isSaved()) {
                    workPlaceViewModel.saveWorkPlace(UID, wp);
                    Toast.makeText(getContext(), getString(R.string.favorite_added, wp.getName()), Toast.LENGTH_SHORT).show();
                } else {
                    workPlaceViewModel.removeSavedWorkPlace(UID, wp);
                    Toast.makeText(getContext(), getString(R.string.favorite_removed, wp.getName()), Toast.LENGTH_SHORT).show();
                }
            });
        }

        FloatingActionButton fab = rootView.findViewById(R.id.add_location_button);
        // Animate the FAB up so it doesn't overlap the card.
        card.post(() -> fab.animate().translationY(-(card.getHeight() + MapUtils.dpToPx(getResources(), 16))).setDuration(200).start());
    }

    /**
     * Hides the detailed workplace card and animates the floating action button back to its original position.
     */
    private void hideCard() {
        View card = rootView.findViewById(R.id.map_card);
        if (card != null) card.setVisibility(View.GONE);
        FloatingActionButton fab = rootView.findViewById(R.id.add_location_button);
        fab.animate().translationY(0).setDuration(200).start(); // Move the FAB back down.
    }

    /**
     * A wrapper method to call the {@code showAddConfirmationDialog} from {@link MapUtils}.
     * This keeps the fragment's code clean and delegates dialog logic.
     */
    private void showAddConfirmationDialogWrapper() {
        MapUtils.showAddConfirmationDialog(requireContext(), selectedName, selectedAddress, (name, address, outside) -> {
            selectedName = name;
            selectedAddress = address;
            selectedOutside = outside;
            workPlaceViewModel.createWorkPlace(selectedName, selectedOutside);
        });
    }

    private void initializeViewModels() {
        FragmentActivity activity = requireActivity();
        Application application = activity.getApplication();

        // Get our repositories using the ServiceLocator pattern.
        IUserAccountRepository userAccountRepository = ServiceLocator.getInstance().getUserAccountRepository(application);
        IWorkPlaceRepository workPlaceRepository = ServiceLocator.getInstance().getWorkPlaceRepository(application);

        userAccountViewModel = new ViewModelProvider(
                activity,
                new UserAccountViewModelFactory(userAccountRepository)).get(UserAccountViewModel.class);

        workPlaceViewModel = new ViewModelProvider(
                activity,
                new WorkPlaceViewModelFactory(workPlaceRepository)).get(WorkPlaceViewModel.class);
    }

    /**
     * Sets up observers for LiveData from ViewModel
     * This method reacts to data changes and updates the UI accordingly.
     */
    private void setupObservers() {
        // Observers for Workplace data:
        workPlaceViewModel.getWorkPlacesLiveData().observe(getViewLifecycleOwner(), resultConsumable -> {
            Result result = resultConsumable.getContentIfNotHandled();
            if (result instanceof Result.WorkPlaceSuccess workPlaceSuccess) {
                List<WorkPlace> workPlaces = workPlaceSuccess.getWorkPlaceList();
                onWorkPlaceDatabaseRetrieveSuccess(workPlaces);
            } else if (result instanceof Result.Error) {
                onWorkPlaceDatabaseFailure();
            }
        });
        workPlaceViewModel.getSavedWorkPlaceLiveData().observe(getViewLifecycleOwner(), resultConsumable -> {
            Result result = resultConsumable.getContentIfNotHandled();
            if (result instanceof Result.WorkPlaceSuccess saved) {
                List<WorkPlace> savedWorkPlaces = saved.getWorkPlaceList();
                onSavedWorkPlaceDatabaseRetrieveSuccess(savedWorkPlaces);
            } else if (result instanceof Result.Error) {
                onWorkPlaceDatabaseFailure();
            }
        });
        workPlaceViewModel.getSavedResultWorkPlaceLiveData().observe(getViewLifecycleOwner(), resultConsumable -> {
            Result result = resultConsumable.getContentIfNotHandled();
            if (result instanceof Result.WorkPlaceSuccess) {
                onWorkPlaceDatabaseSaveSuccess();
            } else if (result instanceof Result.Error) {
                onWorkPlaceDatabaseFailure();
            }
        });
        workPlaceViewModel.getRemoveWorkPlaceLiveData().observe(getViewLifecycleOwner(), resultConsumable -> {
            Result result = resultConsumable.getContentIfNotHandled();
            if (result instanceof Result.WorkPlaceSuccess) {
                onWorkPlaceDatabaseDeleteSuccess();
            } else if (result instanceof Result.Error) {
                onWorkPlaceDatabaseFailure();
            }
        });

        workPlaceViewModel.getCreateWorkPlaceLiveData().observe(getViewLifecycleOwner(), resultConsumable -> {
            Result result = resultConsumable.getContentIfNotHandled();
            if(result instanceof Result.WorkPlaceSuccess workPlaceSuccess) {
                List<WorkPlace> workPlaceList = workPlaceSuccess.getWorkPlaceList();
                if(!workPlaceList.isEmpty()) {
                    WorkPlace workPlace = workPlaceSuccess.getWorkPlaceList().get(0);
                    onWorkPlaceDatabaseCreateSuccess(workPlace);
                }
            } else if (result instanceof Result.Error)
                onWorkPlaceDatabaseFailure();
        });


        // Observer for User account data:
        userAccountViewModel.getCacheLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.ResponseSuccess responseSuccess) {
                User user = User.userFromString(responseSuccess.getResponse());
                UID = user.getUid();
            } else if (result instanceof Result.Error) {
                FloatingActionButton fab = rootView.findViewById(R.id.add_location_button);
                fab.setEnabled(false);
            }
        });
    }
}
