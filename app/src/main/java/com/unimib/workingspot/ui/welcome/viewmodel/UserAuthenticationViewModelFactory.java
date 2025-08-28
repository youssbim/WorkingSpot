package com.unimib.workingspot.ui.welcome.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.workingspot.repository.user.authentication.IUserAuthenticationRepository;

/**
 * Factory class to create instances of UserAuthenticationViewModel.
 * This factory enables passing a UserAuthenticationRepository to the ViewModel's constructor,
 * which is necessary since ViewModels with parameters require a custom factory.
 */
public class UserAuthenticationViewModelFactory implements ViewModelProvider.Factory {

    /**
     The repository to be provided to the ViewModel
     */
    private final IUserAuthenticationRepository userRepository;

    /**
     * Constructor accepting a UserAuthenticationRepository instance.
     *
     * @param userRepository The UserAuthenticationRepository instance to be injected.
     */
    public UserAuthenticationViewModelFactory(IUserAuthenticationRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates and returns an instance of the specified ViewModel class.
     * Casts the created ViewModel to the generic type T.
     *
     * @param modelClass The class of the ViewModel to create.
     * @param <T>        The type of ViewModel.
     * @return An instance of the requested ViewModel.
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // Create UserAuthenticationViewModel using the provided repository
        return (T) new UserAuthenticationViewModel(userRepository);
    }
}
