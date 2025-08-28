package com.unimib.workingspot.ui.main.fragments;

import static com.unimib.workingspot.util.constants.WorkPlacesConstants.GOOGLE_MAPS_API_LINK_FORMATTED;
import static com.unimib.workingspot.util.constants.Constants.USER;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.unimib.workingspot.R;
import com.unimib.workingspot.adapter.WorkPlaceAdapter;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.model.User;
import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.repository.user.account.IUserAccountRepository;
import com.unimib.workingspot.repository.work_place.IWorkPlaceRepository;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModel;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModelFactory;
import com.unimib.workingspot.ui.main.viewmodel.work_place.WorkPlaceViewModel;
import com.unimib.workingspot.ui.main.viewmodel.work_place.WorkPlaceViewModelFactory;
import com.unimib.workingspot.util.network.NetworkManagerSingleton;
import com.unimib.workingspot.util.network.NetworkState;
import com.unimib.workingspot.util.source.ServiceLocator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Fragment responsible for displaying and managing the list of workplaces
 * saved (favourite) by the currently logged-in user.
 *
 * <p>Key functionalities:
 * <ul>
 *     <li>Retrieve and display user's saved workplaces</li>
 *     <li>Handle navigation to selected workplace in Google Maps</li>
 *     <li>Enable toggling favorite status for each workplace</li>
 * </ul>
 *
 * <p>This class works with:
 * <ul>
 *     <li>{@link WorkPlaceViewModel} to manage workplace data</li>
 *     <li>{@link UserAccountViewModel} to obtain user credentials</li>
 *     <li>{@link WorkPlaceAdapter} for rendering workplace cards</li>
 * </ul>
 *
 * <p>Layout used: {@code fragment_saved_workplaces.xml}</p>
 */
public class SavedWorkPlaceFragment extends Fragment {

    /**
     * ViewModel for accessing user account data.
     */
    private UserAccountViewModel userAccountViewModel;

    /**
     * ViewModel for accessing and modifying workplace data.
     */
    private WorkPlaceViewModel workPlaceViewModel;

    /**
     * Adapter for populating the RecyclerView with saved workplaces.
     */
    private WorkPlaceAdapter workPlaceAdapter;

    /**
     * TextView shown when no saved workplaces are available.
     */
    private TextView tvNoFavorites;

    /**
     * List of workplaces currently displayed
     */
    private List<WorkPlace> savedWorkPlaces;


    /**
     * Network manager singleton for monitoring internet connectivity.
     */
    private NetworkManagerSingleton networkManagerSingleton;

    /**
     * Unique identifier of the current user.
     */
    private String UID;

    /**
     * Default empty constructor.
     */
    public SavedWorkPlaceFragment() {}


    /**
     * Initializes ViewModels and prepares lists.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViewModels();
        savedWorkPlaces = new ArrayList<>();
    }


    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * <p>This method inflates the layout for the saved workplaces fragment, sets up the RecyclerView,
     * initializes the adapter with item click and favorite button handlers, and
     * observes the network connectivity to load user data and set the adapter accordingly.</p>
     *
     * <p>The adapter handles two user actions:
     * <ul>
     *     <li>Clicking on a workplace card to open its location in Google Maps.</li>
     *     <li>Toggling the "favorite" state of a workplace to add or remove it from saved items,
     *         which updates the database accordingly.</li>
     * </ul>
     *
     * @param inflater The {@link LayoutInflater} object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root {@link View} of the inflated fragment layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_workplaces, container, false);

        RecyclerView workPlaceRecyclerView = view.findViewById(R.id.recycler_view_favorites);
        workPlaceRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        tvNoFavorites = view.findViewById(R.id.tv_no_favorites);


        networkManagerSingleton = NetworkManagerSingleton.getInstance(requireActivity().getApplication());
        networkManagerSingleton.registerNetworkCallback();
        networkManagerSingleton.getConnectionStatusLiveData().observe(getViewLifecycleOwner(), isConnected ->{
            initializeAdapter(isConnected != NetworkState.OFFLINE);
            workPlaceRecyclerView.setAdapter(workPlaceAdapter);
        });
        userAccountViewModel.getUser(USER);
        updateNoFavoritesVisibility();
        return view;
    }


    /**
     * Called immediately after {@link #onCreateView} has returned, and ensures further setup
     * of the fragment's UI, including managing the keyboard visibility and setting up search logic.
     *
     * <p>This method sets a listener for window insets to detect when the keyboard is shown or hidden.
     * Based on that, it hides or shows the bottom navigation bar accordingly.</p>
     *
     * <p>The method calls {@code setupObservers()} to initialize LiveData observation (implementation not shown)</p>
     *
     * @param view The View returned by {@link #onCreateView}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObservers();
    }
    /**
     * Opens Google Maps in the user's default browser or maps app,
     * showing directions to the selected workplace location.
     *
     * @param workPlace The workplace selected by the user.
     */
    private void openWorkPlaceInMaps(WorkPlace workPlace) {
        String uri = String.format(Locale.ENGLISH, GOOGLE_MAPS_API_LINK_FORMATTED,
                workPlace.getLatitude(), workPlace.getLongitude());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        requireContext().startActivity(intent);
    }

    /**
     * Replaces the content of the adapter with a new filtered or updated list of workplaces.
     * Also updates the visibility of the "no favorites" message accordingly.
     *
     * @param filteredList The new list of workplaces to display.
     */
    public void updateAdapter(List<WorkPlace> filteredList) {
        int oldSize = savedWorkPlaces.size();
        savedWorkPlaces.clear();
        workPlaceAdapter.notifyItemRangeRemoved(0, oldSize);

        savedWorkPlaces.addAll(filteredList);
        workPlaceAdapter.notifyItemRangeInserted(0, savedWorkPlaces.size());

        updateNoFavoritesVisibility();
    }

    /**
     * Updates the visibility of the "no favorites" message based on the current state of the saved
     * workplaces list.
     *
     * <p>If {@code savedWorkPlaces} is empty, a TextView indicating that no favorites are available
     * is shown. Otherwise, it is hidden.</p>
     *
     *
     * <p>This method ensures that UI components reflect the presence or absence of data appropriately,
     * enhancing user feedback and interactivity.</p>
     *
     * Preconditions:
     * - {@code tvNoFavorites} must be non-null.
     *
     */
    private void updateNoFavoritesVisibility() {
        if (tvNoFavorites != null) {
            boolean noResults = savedWorkPlaces.isEmpty();
            if (noResults) {
                tvNoFavorites.setVisibility(View.VISIBLE);
            } else {
                tvNoFavorites.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Displays a short {@link Toast} message on the screen.
     *
     * @param message The message text to be shown in the toast.
     */
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Requests the latest list of saved workplaces from the {@link WorkPlaceViewModel}.
     *
     * <p>Triggers data refresh for the current user identified by {@code UID}.</p>
     */
    private void refreshWorkplaceList() {
        workPlaceViewModel.getSavedWorkPlaces(UID);
    }


    /**
     * Callback invoked when a workplace has been successfully deleted from the database.
     *
     * <p>Displays a success toast and triggers a refresh of the saved workplace list.</p>
     *
     * @param workPlace The {@link WorkPlace} that was successfully removed.
     */
    public void onWorkPlaceDatabaseDeleteSuccess(WorkPlace workPlace) {
        showToast(getString(R.string.workplace_deleted_successfully, workPlace.getName()));
        refreshWorkplaceList();
    }

    /**
     * Handles failures in accessing the workplace database operations.
     *
     * <p>Hides the loading shimmer view and shows an error view.
     * Additionally, displays a toast with the received error message.</p>
     */
    public void onWorkPlaceDatabaseFailure() {
        View root = requireView();
        root.findViewById(R.id.workspot_shimmer_view).setVisibility(View.GONE);
    }

    /**
     * Callback invoked when the retrieval of saved workplaces from the database is successful.
     * <p>
     * Updates the adapter and internal lists with the retrieved data.
     *
     * @param savedWorkPlaces The list of saved workplaces retrieved for the current user.
     */
    public void onSavedWorkPlaceDatabaseRetrieveSuccess(List<WorkPlace> savedWorkPlaces) {
        updateAdapter(savedWorkPlaces);

        View view = requireView();

        view.findViewById(R.id.workspot_shimmer_view).setVisibility(View.GONE);

        if(savedWorkPlaces.isEmpty()) {
            view.findViewById(R.id.recycler_view_favorites).setVisibility(View.GONE);
            tvNoFavorites.setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.recycler_view_favorites).setVisibility(View.VISIBLE);
            tvNoFavorites.setVisibility(View.GONE);
        }
    }

    /**
     * Called when the fragment is being destroyed.
     * <p>
     * Ensures that the network callback registered with {@link NetworkManagerSingleton} is
     * unregistered to prevent memory leaks and unnecessary updates after the fragment's lifecycle ends.
     */
    public void onDestroy() {
        super.onDestroy();
        networkManagerSingleton.unregisterNetworkCallback();
    }

    /**
     * Initializes the ViewModels required for the fragment.
     *
     * <p>Retrieves the activity and application instances,
     * creates the repositories via the ServiceLocator,
     * and finally instantiates the {@link UserAccountViewModel} and {@link WorkPlaceViewModel}
     * using their respective factories.</p>
     */
    private void initializeViewModels() {
        FragmentActivity activity = requireActivity();
        Application application = activity.getApplication();

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
     * Sets up LiveData observers for the fragment's ViewModels.
     *
     * <p>Observes changes related to workplace data and user account data:</p>
     * <ul>
     *   <li><b>Saved workplaces:</b> handles successful retrieval or failure events.</li>
     *   <li><b>Workplace save results:</b> handles success or failure when saving a workplace.</li>
     *   <li><b>Workplace removal results:</b> handles success or failure when removing a workplace.</li>
     *   <li><b>User account cache:</b> observes user data retrieval, extracts the user ID,
     *       and triggers loading of saved workplaces.</li>
     * </ul>
     */
    private void setupObservers() {

        workPlaceViewModel.getSavedWorkPlaceLiveData().observe(getViewLifecycleOwner(), resultEvent -> {
            Result result = resultEvent.getContentIfNotHandled();
            if (result instanceof Result.WorkPlaceSuccess saved) {
                List<WorkPlace> savedWorkPlaces = saved.getWorkPlaceList();
                onSavedWorkPlaceDatabaseRetrieveSuccess(savedWorkPlaces);
            } else if (result instanceof Result.Error) {
                onWorkPlaceDatabaseFailure();
            }
        });
        workPlaceViewModel.getSavedResultWorkPlaceLiveData().observe(getViewLifecycleOwner(), resultEvent -> {
            Result result = resultEvent.getContentIfNotHandled();
            if (result instanceof Result.WorkPlaceSuccess workPlaceSuccess) {
                List<WorkPlace> workPlaceList = workPlaceSuccess.getWorkPlaceList();
                if(!workPlaceList.isEmpty()) {
                    WorkPlace workPlace = workPlaceSuccess.getWorkPlaceList().get(0);
                    onWorkPlaceDatabaseDeleteSuccess(workPlace);
                }
            } else if (result instanceof Result.Error) {
                onWorkPlaceDatabaseFailure();
            }
        });
        workPlaceViewModel.getRemoveWorkPlaceLiveData().observe(getViewLifecycleOwner(), resultEvent -> {
            Result result = resultEvent.getContentIfNotHandled();
            if (result instanceof Result.WorkPlaceSuccess workPlaceSuccess) {
                List<WorkPlace> workPlaceList = workPlaceSuccess.getWorkPlaceList();
                if(!workPlaceList.isEmpty()) {
                    WorkPlace workPlace = workPlaceSuccess.getWorkPlaceList().get(0);
                    onWorkPlaceDatabaseDeleteSuccess(workPlace);
                }
            } else if (result instanceof Result.Error) {
                onWorkPlaceDatabaseFailure();
            }
        });

        userAccountViewModel.getCacheLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.ResponseSuccess responseSuccess) {
                User user = User.userFromString(responseSuccess.getResponse());
                UID = user.getUid();
                workPlaceViewModel.getSavedWorkPlaces(UID);
            } else if (result instanceof Result.Error) {
                onWorkPlaceDatabaseFailure();
            }
        });
    }




    /**
     * Initializes the WorkPlaceAdapter for the RecyclerView.
     * @param showFavouriteButton A boolean indicating whether the favorite button should be displayed.
     */
    private void initializeAdapter(boolean showFavouriteButton) {
        workPlaceAdapter = new WorkPlaceAdapter(R.layout.card_workplace, savedWorkPlaces, showFavouriteButton,
        new WorkPlaceAdapter.OnItemClickListener() {
            @Override
            public void onWorkPlaceItemClick(WorkPlace workPlace) {
                openWorkPlaceInMaps(workPlace);
            }

            @Override
            public void onFavouriteButtonPressed(int position) {
                WorkPlace wp = savedWorkPlaces.get(position);
                wp.setSaved(!wp.isSaved());
                if(wp.isSaved()){
                    workPlaceViewModel.saveWorkPlace(UID, wp);
                }
                else {
                    workPlaceViewModel.removeSavedWorkPlace(UID, wp);
                }
            }
        });
    }


}
