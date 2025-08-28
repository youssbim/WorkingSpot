package com.unimib.workingspot.ui.main.viewmodel.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.workingspot.repository.user.account.IUserAccountRepository;

/**
 * Factory class for creating UserAccountViewModel instances.
 * This factory allows passing a UserAccountRepository to the ViewModel's constructor.
 */
public class UserAccountViewModelFactory implements ViewModelProvider.Factory {

    /**
     Repository instance to be provided to the ViewModel
     */
    private final IUserAccountRepository userAccountRepository;

    /**
     * Constructor accepting a UserAccountRepository to be used by the ViewModel.
     *
     * @param userRepository The UserAccountRepository instance.
     */
    public UserAccountViewModelFactory(IUserAccountRepository userRepository) {
        this.userAccountRepository = userRepository;
    }

    /**
     * Creates and returns an instance of the requested ViewModel class.
     * This method casts the created ViewModel to the generic type T.
     *
     * @param modelClass The class of the ViewModel to be created.
     * @param <T>        The type of ViewModel.
     * @return An instance of the requested ViewModel.
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // Create UserAccountViewModel with the provided repository
        return (T) new UserAccountViewModel(userAccountRepository);
    }
}
