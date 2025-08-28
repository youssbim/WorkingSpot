package com.unimib.workingspot.ui.main.fragments;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.unimib.workingspot.R;
import com.unimib.workingspot.adapter.WorkPlaceAdapter;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.model.User;
import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.model.weather.Weather;
import com.unimib.workingspot.repository.user.account.IUserAccountRepository;
import com.unimib.workingspot.repository.weather.WeatherRepository;
import com.unimib.workingspot.repository.work_place.IWorkPlaceRepository;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModel;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModelFactory;
import com.unimib.workingspot.ui.main.viewmodel.weather.WeatherViewModel;
import com.unimib.workingspot.ui.main.viewmodel.weather.WeatherViewModelFactory;
import com.unimib.workingspot.ui.main.viewmodel.work_place.WorkPlaceViewModel;
import com.unimib.workingspot.ui.main.viewmodel.work_place.WorkPlaceViewModelFactory;
import com.unimib.workingspot.util.constants.WeatherConstants;
import com.unimib.workingspot.util.constants.WorkPlacesConstants;
import com.unimib.workingspot.util.data_store.DataStoreManagerSingleton;
import com.unimib.workingspot.util.network.NetworkManagerSingleton;
import com.unimib.workingspot.util.network.NetworkState;
import com.unimib.workingspot.util.permissions.IPermissionCallback;
import com.unimib.workingspot.util.source.ServiceLocator;
import com.unimib.workingspot.util.permissions.GeolocalizationPermissionsUtil;

import static com.unimib.workingspot.util.constants.Constants.*;
import static com.unimib.workingspot.util.constants.DataStoreConstants.DATASTORE_RESOURCE_NOT_FOUND;
import static com.unimib.workingspot.util.constants.DataStoreConstants.DATASTORE_WEATHER_LAST_UPDATED_KEY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * {@link Fragment} that represents the home screen in the application.
 * It displays weather information and workplace data
 */
public class HomeFragment extends Fragment implements IPermissionCallback {

    // Tag for debug purposes
    public static String TAG = HomeFragment.class.getName();

    // ViewModels for requesting data
    private WeatherViewModel weatherViewModel;
    private UserAccountViewModel userAccountViewModel;
    private WorkPlaceViewModel workPlaceViewModel;

    // Client for fetching current device location
    private FusedLocationProviderClient flpc;

    // Adapter for workplace data
    private WorkPlaceAdapter workPlaceAdapter;

    // Workplace list
    private List<WorkPlace> workPlaceList;

    // Workplace recycler view
    private RecyclerView workPlaceRecyclerView;

    // Activity result launcher for asking geo-localization permissions
    private ActivityResultLauncher<String[]> geolocalizationPermissionsActivityResultLauncher;

    // User UID for fetching saved workplaces
    private String UID;

    // Datastore manager for accessing the cache
    private DataStoreManagerSingleton dataStoreManager;

    // Last time the weather was updated
    private long lastUpdatedTime;

    // Network manager for reacting to change in connection status
    private NetworkManagerSingleton networkManager;

    // Current weather
    private Weather weather;

    // Flag that indicates if the device is currently connected to the network
    private boolean isConnected;

    /**
     * Default constructor for HomeFragment
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    //-----------------------------------Fragment methods-----------------------------------------//

    /**
     * Called when the fragment is created. Initializes the ViewModels and other components
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViewModels();
        flpc = LocationServices
                .getFusedLocationProviderClient(requireActivity());
        geolocalizationPermissionsActivityResultLauncher =
                GeolocalizationPermissionsUtil.registerForLocationPermissionRequest(this, this);

        dataStoreManager = DataStoreManagerSingleton.getInstance(requireActivity().getApplication(), DATASTORE);

        networkManager = NetworkManagerSingleton.getInstance(requireActivity().getApplication());

        workPlaceList = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Inflates the layout for this fragment and sets up UI elements.
     * Checks location permissions and initiates weather data fetch if permissions are granted
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The root view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflates the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.loading_screen).setVisibility(View.VISIBLE);

        // Registers network callbacks
        networkManager.registerNetworkCallback();

        // Initializes observer for connection status changes
        networkManager.getConnectionStatusLiveData().observe(getViewLifecycleOwner(),
                networkState -> {
                    isConnected = networkState != NetworkState.OFFLINE;
                    initializeWorkPlaceAdapter(isConnected);
                });

        if (!GeolocalizationPermissionsUtil.hasLocationPermissions(requireContext())) {
            setupRequestLocalizationPermissionsLayout(view);
        }

        // Initializes recycler view
        workPlaceRecyclerView = view.findViewById(R.id.workspot_recycler_view);
        workPlaceRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Retrieves the current User
        userAccountViewModel.getUser(USER);

        // Fetches the workplaces
        workPlaceViewModel.getWorkPlaces();
        return view;
    }

    /**
     * Called after the view has been created
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION})
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(GeolocalizationPermissionsUtil.hasLocationPermissions(requireContext()))
            getWeatherLastUpdatedTime();
        setupObservers();
    }

    /**
     * Called when the view is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        networkManager.unregisterNetworkCallback();
    }


    //-----------------------------UI handling methods and callbacks------------------------------//

    /**
     * Sets up the UI layout for requesting location permissions.
     * This layout is visible only when the application does not have geo-localization permissions
     * @param view The root view of the fragment
     */
    private void setupRequestLocalizationPermissionsLayout(View view) {

        // Sets to visible the button for requesting permissions
        view.findViewById(R.id.request_location_permission_layout).setVisibility(View.VISIBLE);

        // Sets to invisible the weather data layout
        view.findViewById(R.id.weather_grid_layout).setVisibility(View.GONE);

        // Initializes the button for requesting geo-localization permissions
        MaterialButton requestGeolocalizationPermissionsButton =
                view.findViewById(R.id.request_location_permission_button);

        requestGeolocalizationPermissionsButton.setOnClickListener(
                v -> geolocalizationPermissionsActivityResultLauncher.launch(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
        }));
        // Removes the loading screen
        view.findViewById(R.id.loading_screen).setVisibility(View.GONE);
    }

    /**
     * Handles successful fetching of weather data and updates the UI accordingly
     */
    private void onWeatherFetchSuccess() {
        // Gets the view
        View view = requireView();

        // Finds UI components
        TextView climateTypeText = view.findViewById(R.id.weather_type_text);
        TextView temperatureText = view.findViewById(R.id.weather_degrees_text);
        ImageView weatherImage = view.findViewById(R.id.weather_image_view);
        GridLayout weatherGridLayout = view.findViewById(R.id.weather_grid_layout);

        // Initializes the weather UI:
        climateTypeText.setText(weather.getWeatherCondition()
                .getCondition());
        temperatureText.setText(getString(R.string.degrees_text, weather.getTemperature()));

        weatherGridLayout.setBackgroundColor(getResources()
                .getColor(R.color.md_theme_primaryContainer, requireActivity().getTheme()));


        // The weather images are listed in descending order in "weather_icon_level_list". This
        // initializes the image level based on the current weather condition.
        // If it's nighttime, the level is incremented to use the night version
        // of the corresponding weather image.
        int level = weather.getWeatherCondition().getCode();
        if (weather.isDay() != 1)
            level++;
        weatherImage.setImageLevel(level);

        // Removes the loading screen and makes the weather information layout visible
        view.findViewById(R.id.request_location_permission_layout).setVisibility(View.GONE);
        view.findViewById(R.id.weather_grid_layout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.weather_degrees_text).setVisibility(View.VISIBLE); // In case of previous error
        view.findViewById(R.id.loading_screen).setVisibility(View.GONE);
    }

    /**
     * Handles failure in fetching weather data and updates the UI accordingly
     */
    private void onWeatherFetchFailure() {
        // Gets the view
        View view = requireView();

        // Finds UI components
        GridLayout weatherGridLayout = view.findViewById(R.id.weather_grid_layout);
        TextView temperatureText = view.findViewById(R.id.weather_degrees_text);
        TextView climateTypeText = view.findViewById(R.id.weather_type_text);

        // Changes background to error color
        weatherGridLayout.setBackgroundColor(getResources()
                .getColor(R.color.md_theme_error, requireActivity().getTheme()));

        // Removes the temperature text
        temperatureText.setVisibility(View.GONE);

        // Displays the error text
        climateTypeText.setText(WeatherConstants.WEATHER_API_FAILED_TO_FETCH);

        // Removes the loading screen and makes error layout visible
        view.findViewById(R.id.request_location_permission_layout).setVisibility(View.GONE);
        view.findViewById(R.id.weather_grid_layout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.loading_screen).setVisibility(View.GONE);
    }

    /**
     * Called when location permissions are successfully granted
     */
    @Override
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION})
    public void onPermissionRequestSuccess() {
        // Gets the view
        View view = requireView();

        // Makes invisible the request permission layout and makes visible the weather information layout
        view.findViewById(R.id.loading_screen).setVisibility(View.VISIBLE);
        view.findViewById(R.id.request_location_permission_layout).setVisibility(View.GONE);
        view.findViewById(R.id.weather_grid_layout).setVisibility(View.VISIBLE);

        // Starts the weather fetching process
        getWeatherLastUpdatedTime();
    }

    /**
     * Called when location permission request fails or is canceled
     */
    @Override
    public void onPermissionRequestFailure() {
        showToast(getString(R.string.geolocalization_permission_request_canceled_failed));
    }

    /**
     * Handles the success of workplace data fetching. Updates the UI to display the workplaces
     */
    private void onWorkPlaceFetchSuccess() {
        // Modifies the list according to the current weather
        if(weather != null)
            applyWeatherFilter();
        else
            initializeWorkPlaceAdapter(isConnected);

        // Notifies the adapter that new items have been inserted into the list
        workPlaceAdapter.notifyItemRangeInserted(0, workPlaceList.size());

        // Removes shimmer layout and displays the workplaces cards
        requireView().findViewById(R.id.failed_to_load_workplaces_layout).setVisibility(View.GONE);
        requireView().findViewById(R.id.workspot_shimmer_view).setVisibility(View.GONE);
        requireView().findViewById(R.id.workspot_recycler_view).setVisibility(View.VISIBLE);

        // If the user is logged in, starts fetching their saved workplaces
        if(UID != null)
            workPlaceViewModel.getSavedWorkPlaces(UID);
    }

    /**
     * Handles failure in workplace data fetching and updates the UI accordingly
     */
    private void onFailureFetchWorkPlace() {
        // Removes shimmer layout and displays the error layout
        requireView().findViewById(R.id.workspot_shimmer_view).setVisibility(View.GONE);
        requireView().findViewById(R.id.failed_to_load_workplaces_layout).setVisibility(View.VISIBLE);
    }

    /**
     * Shows a toast message with the provided text
     * @param message The message to display in the toast
     */
    private void showToast(String message) {



        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles success in fetching saved workplaces and updates the UI to mark them as saved
     * @param savedWorkPlaces List of saved workplaces
     */
    private void onSavedWorkPlaceFetchSuccess(List<WorkPlace> savedWorkPlaces) {
        for(WorkPlace workPlace : savedWorkPlaces) {
            int position = workPlaceList.indexOf(workPlace);
            if(position != -1) {
                workPlaceList.get(position).setSaved(true);
                workPlaceAdapter.notifyItemChanged(position);
            }
        }

    }
    //--------------------------------------- Util methods ---------------------------------------//
    /**
     * Initializes the ViewModels with the necessary repositories
     */
    private void initializeViewModels() {
        FragmentActivity activity = requireActivity();
        Application application = activity.getApplication();

        // Init repositories
        WeatherRepository weatherRepository = ServiceLocator.getInstance().getWeatherRepository(application);
        IUserAccountRepository userAccountRepository = ServiceLocator.getInstance().getUserAccountRepository(application);
        IWorkPlaceRepository workPlaceRepository = ServiceLocator.getInstance().getWorkPlaceRepository(application);

        // Init ViewModels
        weatherViewModel = new ViewModelProvider(
                activity,
                new WeatherViewModelFactory(weatherRepository)).get(WeatherViewModel.class);

        userAccountViewModel = new ViewModelProvider(
                activity,
                new UserAccountViewModelFactory(userAccountRepository)).get(UserAccountViewModel.class);

        workPlaceViewModel = new ViewModelProvider(
                activity,
                new WorkPlaceViewModelFactory(workPlaceRepository)).get(WorkPlaceViewModel.class);
    }

    /**
     * Sets up observers for the ViewModels of this fragment.
     */
    private void setupObservers() {
        // Workplace observers:
        workPlaceViewModel.getWorkPlacesLiveData().observe(getViewLifecycleOwner(), resultEvent -> {
            Result result = resultEvent.getContentIfNotHandled();
            if (result instanceof Result.WorkPlaceSuccess workPlaceSuccess) {
                workPlaceList.clear();
                workPlaceList.addAll(workPlaceSuccess.getWorkPlaceList());
                onWorkPlaceFetchSuccess();
            } else if (result instanceof Result.Error) {
                onFailureFetchWorkPlace();
            }
        });
        workPlaceViewModel.getSavedWorkPlaceLiveData().observe(getViewLifecycleOwner(), resultEvent -> {
            Result result = resultEvent.getContentIfNotHandled();
            if (result instanceof Result.WorkPlaceSuccess saved) {
                List<WorkPlace> savedWorkPlaces = saved.getWorkPlaceList();
                onSavedWorkPlaceFetchSuccess(savedWorkPlaces);
            } else if (result instanceof Result.Error) {
                showToast(getString(R.string.failed_to_fetch_saved));
            }
        });
        workPlaceViewModel.getSavedResultWorkPlaceLiveData().observe(getViewLifecycleOwner(), resultEvent -> {
            Result result = resultEvent.getContentIfNotHandled();
            if (result instanceof Result.WorkPlaceSuccess workPlaceSuccess) {
                List<WorkPlace> workPlaceList = workPlaceSuccess.getWorkPlaceList();
                if(!workPlaceList.isEmpty()) {
                    WorkPlace workPlace = workPlaceSuccess.getWorkPlaceList().get(0);
                    showToast(getString(R.string.favorite_added, workPlace.getName()));
                }
            } else if (result instanceof Result.Error error) {
                showToast(getString(R.string.error_message_toast, error.getErrorMessage()));
            }
        });
        workPlaceViewModel.getRemoveWorkPlaceLiveData().observe(getViewLifecycleOwner(), resultEvent -> {
            Result result = resultEvent.getContentIfNotHandled();
            if (result instanceof Result.WorkPlaceSuccess workPlaceSuccess) {
                List<WorkPlace> workPlaceList = workPlaceSuccess.getWorkPlaceList();
                if(!workPlaceList.isEmpty()) {
                    WorkPlace workPlace = workPlaceSuccess.getWorkPlaceList().get(0);
                    showToast(getString(R.string.favorite_removed, workPlace.getName()));
                }
            } else if (result instanceof Result.Error error) {
                showToast(getString(R.string.error_message_toast, error.getErrorMessage()));
            }
        });
        // User observer:
        userAccountViewModel.getCacheLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.ResponseSuccess responseSuccess) {
                User user = User.userFromString(responseSuccess.getResponse());
                UID = user.getUid();
            }
        });
    }


    /**
     * Retrieves the last updated time for weather data from cache.
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION})
    private void getWeatherLastUpdatedTime() {
        dataStoreManager.getResource(DATASTORE_WEATHER_LAST_UPDATED_KEY, resource -> {
            if (resource.equals(DATASTORE_RESOURCE_NOT_FOUND)) {
                lastUpdatedTime = -1;
            } else {
                lastUpdatedTime = Long.parseLong(resource);
            }
            getLocationData();
        }, throwable ->
                showToast(getString(R.string.error_message_toast, throwable.getMessage())));
    }

    /**
     * Retrieves the last known location of the device
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION})
    private void getLocationData() {
        flpc.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if(location != null) {
                getWeatherData(location.getLatitude(), location.getLongitude());
            }
        }).addOnFailureListener(e -> showToast(getString(R.string.error_message_toast, e.getMessage())));
    }

    /**
     * Fetches weather data
     * @param latitude The latitude of the location for which to fetch weather data
     * @param longitude he longitude of the location for which to fetch weather data
     */
    private void getWeatherData(double latitude, double longitude) {
        weatherViewModel.getWeather(latitude, longitude, lastUpdatedTime).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.WeatherSuccess weatherSuccess) {
                weather = weatherSuccess.getData().getCurrentWeather();
                onWeatherFetchSuccess();
            } else if (result instanceof Result.Error) {
                onWeatherFetchFailure();
            }
        });
    }

    /**
     * Initializes the WorkPlaceAdapter for the RecyclerView.
     * @param shouldShowFavoriteButton A boolean indicating whether the favorite button should be displayed.
     */
    private void initializeWorkPlaceAdapter(boolean shouldShowFavoriteButton) {
        // Setups the adapter
        workPlaceAdapter = new WorkPlaceAdapter(R.layout.card_workplace, workPlaceList, shouldShowFavoriteButton,
                new WorkPlaceAdapter.OnItemClickListener() {
            @Override
            public void onWorkPlaceItemClick(WorkPlace workPlace) {
                // Launches google maps with the destination set to the coordinates of the workplace
                String URI = String.format(Locale.ENGLISH, WorkPlacesConstants.GOOGLE_MAPS_API_LINK_FORMATTED, workPlace.getLatitude(), workPlace.getLongitude());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URI));
                requireContext().startActivity(intent);
            }
            @Override
            public void onFavouriteButtonPressed(int position) {
                WorkPlace workPlace = workPlaceList.get(position);
                if(UID == null) {
                    showToast(getString(R.string.guests_cannot_save_workplaces));
                } else if (!workPlace.isSaved()) {
                    workPlaceViewModel.saveWorkPlace(UID, workPlace);
                } else {
                    workPlaceViewModel.removeSavedWorkPlace(UID, workPlace);
                }
            }
        });
        workPlaceRecyclerView.setAdapter(workPlaceAdapter);
    }

    /**
     * Filters workplaces based on the current weather condition
     * Outdoors workplaces are recommended if there is a clear sky and the temperature is greater or
     * equal to 22 degrees Celsius
     */
    private void applyWeatherFilter() {
        boolean shouldRecommendOutdoors = false;
        int code = weather.getWeatherCondition().getCode();

        // If there is a clear sky and the temperature is greater o equal to 22 degrees Celsius
        if(code >= 1000 && code <= 1003 && weather.getTemperature() >= 22)
            shouldRecommendOutdoors = true;

        // Additional list to avoid concurrency problems
        List<WorkPlace> filteredWorkplaces = new ArrayList<>(workPlaceList);
        for(WorkPlace workPlace: workPlaceList) {
            if(!shouldRecommendOutdoors && workPlace.isOutside())
                filteredWorkplaces.remove(workPlace);
        }
        workPlaceList.clear();
        workPlaceList.addAll(filteredWorkplaces);
        initializeWorkPlaceAdapter(isConnected);
    }
}
