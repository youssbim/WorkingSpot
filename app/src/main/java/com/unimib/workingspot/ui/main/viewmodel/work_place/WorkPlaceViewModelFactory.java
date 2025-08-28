package com.unimib.workingspot.ui.main.viewmodel.work_place;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.workingspot.repository.work_place.IWorkPlaceRepository;
/**
 * Factory for creating instances of {@link WorkPlaceViewModel}.
 */
public class WorkPlaceViewModelFactory implements ViewModelProvider.Factory {

    private final IWorkPlaceRepository workPlaceRepository;

    /**
     * Constructor for initializing WorkPlaceViewModelFactory
     * @param workPlaceRepository The workplace repository implementation
     */
    public WorkPlaceViewModelFactory(IWorkPlaceRepository workPlaceRepository) {
        this.workPlaceRepository = workPlaceRepository;
    }

    /**
     * Creates a new instance of the specified {@link ViewModel} class
     * @param modelClass The {@code Class} object for the ViewModel to be created
     * @return An instance of {@link WorkPlaceViewModel}
     * @param <T> The ViewModel type
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WorkPlaceViewModel(workPlaceRepository);
    }

}
