package com.unimib.workingspot.ui.main.fragments;

import static android.view.View.GONE;
import static com.unimib.workingspot.util.constants.Constants.EMPTY_STRING;
import static com.unimib.workingspot.util.constants.Constants.ACCOUNT_USER_VIEW_MODEL_ERROR;
import static com.unimib.workingspot.util.constants.Constants.EMPTY;
import static com.unimib.workingspot.util.constants.Constants.NETWORK_ERROR;
import static com.unimib.workingspot.util.constants.Constants.SHOW_DIALOG;
import static com.unimib.workingspot.util.constants.Constants.USER;
import static com.unimib.workingspot.util.constants.Constants.USER_CLASS;
import static com.unimib.workingspot.util.constants.Constants.USER_REPOSITORY_ERROR;
import static com.unimib.workingspot.util.constants.Constants.EMAIL;
import static com.unimib.workingspot.util.user_managing_utils.CallBacksMessages.getMessage;
import static com.unimib.workingspot.util.user_managing_utils.text_fields.TextFieldsHandler.emailChangeFieldChangeVisibility;
import static com.unimib.workingspot.util.user_managing_utils.text_fields.TextFieldsHandler.setUsernameLayoutFocus;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.workingspot.R;
import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.model.User;
import com.unimib.workingspot.repository.user.account.IUserAccountRepository;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModel;
import com.unimib.workingspot.ui.main.viewmodel.user.UserAccountViewModelFactory;
import com.unimib.workingspot.ui.welcome.WelcomeActivity;
import com.unimib.workingspot.util.activity_launch_utils.LaunchUtils;
import com.unimib.workingspot.util.network.NetworkManagerSingleton;
import com.unimib.workingspot.util.network.NetworkState;
import com.unimib.workingspot.util.network.ui_methods.logged_profile_methods.LoggedProfileMethods;
import com.unimib.workingspot.util.user_managing_utils.CallBacksMessages;
import com.unimib.workingspot.util.bitmap.BitMapManager;
import com.unimib.workingspot.util.user_managing_utils.text_fields.FieldCheckHandler;
import com.unimib.workingspot.util.user_managing_utils.text_fields.TextFieldWrapper;
import com.unimib.workingspot.util.source.ServiceLocator;
import java.util.Objects;

public class LoggedProfileFragment extends Fragment {

    private static final String TAG = LoggedProfileFragment.class.getSimpleName();
    private UserAccountViewModel userAccountViewModel;

    private TextFieldWrapper textFieldWrapper;

    private String oldUsername;

    /**
     * Returns the old username before editing.
     *
     * @return the old username string
     */
    public String getOldUsername() {
        return oldUsername;
    }

    /**
     * Sets the old username to the specified value.
     *
     * @param oldUsername the previous username string
     */
    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

    private BitMapManager bitMapManager;
    private NetworkManagerSingleton networkManagerSingleton;

    public LoggedProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserAccountRepository userAccountRepository;

        try {
            userAccountRepository = ServiceLocator.getInstance().getUserAccountRepository(requireActivity().getApplication());
        } catch (Exception e) {
            Log.e(TAG, ACCOUNT_USER_VIEW_MODEL_ERROR + e.getMessage(), e);
            return;
        }

        try {
            userAccountViewModel = new ViewModelProvider(requireActivity(), new UserAccountViewModelFactory(userAccountRepository)).get(UserAccountViewModel.class);
        } catch (Exception e) {
            Log.e(TAG, USER_REPOSITORY_ERROR + e.getMessage(), e);
        }

        bitMapManager = new BitMapManager();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logged_profile, container, Boolean.FALSE);

        MaterialButton profilePictureButton = view.findViewById(R.id.profile_picture);

        textFieldWrapper = new TextFieldWrapper(
                view.findViewById(R.id.username_text_layout),
                view.findViewById(R.id.user_name_text),
                view.findViewById(R.id.submit_username_button));

        TextView emailText = view.findViewById(R.id.user_mail);
        LinearLayout layout = view.findViewById(R.id.email_text_linear_layout);
        layout.setVisibility(GONE);

        setLoggedObservers(textFieldWrapper.getEditText(), emailText, profilePictureButton, bitMapManager);

        networkManagerSingleton = NetworkManagerSingleton.getInstance(requireActivity().getApplication());
        networkManagerSingleton.registerNetworkCallback();
        networkManagerSingleton.getConnectionStatusLiveData().observe(getViewLifecycleOwner(), isConnected ->{
            if(isConnected == NetworkState.OFFLINE) {
                LoggedProfileMethods.setButtonsEnabled(view, Boolean.FALSE);
            }else{
                LoggedProfileMethods.setButtonsEnabled(view, Boolean.TRUE);
            }
        });
        pickMediaLauncher = createImagePicker(requireContext());

        profilePictureButton.setOnClickListener(v -> pickMediaLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        ));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userAccountViewModel.getUser(USER);

        textFieldWrapper.getEditText().setClickable(Boolean.FALSE);
        textFieldWrapper.getEditText().setFocusable(Boolean.FALSE);
        textFieldWrapper.getEditText().setFocusableInTouchMode(Boolean.FALSE);

        showUserNameField();

        MaterialButton changeEmailButton = view.findViewById(R.id.change_email);
        changeEmailButton.setOnClickListener(v -> emailChangeFieldChangeVisibility(requireView(), Boolean.TRUE));

        MaterialButton submitEmailButton = view.findViewById(R.id.submit_email_button);
        submitEmailButton.setOnClickListener(v->{
            TextInputEditText emailInput = view.findViewById(R.id.email_text);
            String email = Objects.requireNonNull(emailInput.getText()).toString();
            if(FieldCheckHandler.isEmailValid(requireActivity(), view.findViewById(R.id.email_text_layout), email)) {
                updateEmail(email);
                emailChangeFieldChangeVisibility(requireView(), Boolean.FALSE);
                signOut(SHOW_DIALOG);
            }
        });

        MaterialButton undoEmailButton = view.findViewById(R.id.undo_email_button);
        undoEmailButton.setOnClickListener(v-> emailChangeFieldChangeVisibility(requireView(), Boolean.FALSE));

        MaterialButton changePasswordButton = view.findViewById(R.id.change_password);
        changePasswordButton.setOnClickListener(v -> {
            String email = ((TextView) view.findViewById(R.id.user_mail)).getText().toString();
            updatePassword(email);
        });

        MaterialButton signOut = view.findViewById(R.id.disconnect);
        signOut.setOnClickListener(v-> {
            Toast.makeText(requireContext(), requireActivity().getString(R.string.user_disconnection), Toast.LENGTH_SHORT).show();
            signOut(null);
        });
    }

    /**
     * Creates an image picker for profile picture selection.
     *
     * @param context the context
     * @return the configured image picker launcher
     */
    private ActivityResultLauncher<PickVisualMediaRequest> createImagePicker(Context context) {
        return registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if(uri == null){
                return;
            }
            bitMapManager.createBitmap(context, uri, bitmap ->
                    bitMapManager.createCircularDrawable(context, bitmap,drawable -> updateProfilePicture(
                                    bitMapManager.encodeBitmap(requireContext(), bitmap, R.mipmap.default_profile_picture)),
                                    R.mipmap.default_profile_picture), R.mipmap.default_profile_picture);
        });
    }

    /**
     * Sets LiveData observers for cache, remote, and update results.
     *
     * @param usernameText         the username input field
     * @param emailText            the email display field
     * @param profilePictureButton the profile picture button
     * @param manager              the bitmap manager
     */
    private void setLoggedObservers(TextInputEditText usernameText, TextView emailText, MaterialButton profilePictureButton, BitMapManager manager) {
        setCacheObserver(profilePictureButton, usernameText, emailText, manager);
        setRemoteObserver();
        setUpdateObserver();
    }

    /**
     * Handles the submission of the username field.
     * <p>
     * Validates that the username is not empty, reverts to old username if empty,
     * hides the keyboard
     * </p>
     */
    private void handleTextSubmission() {
        // Hide the soft keyboard when submitting
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
        }

        TextInputEditText editText = textFieldWrapper.getEditText();
        if (editText.getText() != null) {
            String newUsername = editText.getText().toString().trim();
            if (newUsername.isEmpty()) {
                // Prevent empty username submission, revert to old username and show error toast
                editText.setText(getOldUsername());
                setOldUsername(EMPTY_STRING);
                Toast.makeText(requireContext(), R.string.empty_username_error, Toast.LENGTH_SHORT).show();
            } else {
                // Valid username entered: update through userAccountMethods
                updateUsername(newUsername);
            }
        }

        // Exit edit mode after submission attempt
        setUsernameLayoutFocus(requireView(), textFieldWrapper,Boolean.FALSE, 0);
    }

    /**
     * Initializes and manages the username text field UI, including toggling edit mode,
     * handling submission via button or keyboard action, and managing focus changes.
     * <p>
     * Sets up click listeners and focus listeners to control when the user can edit their username,
     * submit it, and validate empty submissions.
     * </p>
     */
    public void showUserNameField() {

        // Initially disable username field editing
        setUsernameLayoutFocus(requireView(), textFieldWrapper,Boolean.FALSE, 0);

        // Button click toggles between edit mode and submit action
        textFieldWrapper.getButton().setOnClickListener(v -> {
            if(textFieldWrapper.getButton().getIcon() == null) {
                // If no icon, we are in edit mode - submit the input
                handleTextSubmission();
                setUsernameLayoutFocus(requireView(), textFieldWrapper,Boolean.FALSE, 0);
            } else {
                // Otherwise, enter edit mode: save old username and show keyboard
                if(textFieldWrapper.getEditText().getText() != null) {
                    setOldUsername(textFieldWrapper.getEditText().getText().toString());
                }
                setUsernameLayoutFocus(requireView(), textFieldWrapper, Boolean.TRUE, ViewGroup.LayoutParams.WRAP_CONTENT);
                InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.showSoftInput(textFieldWrapper.getEditText(), InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        // Handle keyboard "Done" or Enter key to submit username
        textFieldWrapper.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                handleTextSubmission();
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        });

        // If username field loses focus, submit changes if old username was set
        textFieldWrapper.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && oldUsername != null && !oldUsername.equals(EMPTY_STRING)){
                handleTextSubmission();
            }
        });
    }

    /**
     * Observes cached user data and updates the UI with profile info.
     *
     * @param profilePicture the profile picture button
     * @param usernameText   the username text field
     * @param emailText      the email display
     * @param manager        the bitmap manager
     */
    private void setCacheObserver(MaterialButton profilePicture, TextInputEditText usernameText, TextView emailText, BitMapManager manager) {
        userAccountViewModel.getCacheLiveData()
                .observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.ResponseSuccess) {
                        String response = ((Result.ResponseSuccess) result).getResponse();
                        if (response != null && !response.isEmpty() && !response.equals(EMPTY)) {
                            Log.d(TAG, response);
                            if (response.startsWith(USER_CLASS)) {
                                User user = User.userFromString(response);
                                setUpProfileInformation(user, usernameText, emailText, profilePicture, manager);
                            }
                        }
                    }else{
                        String response = ((Result.Error) result).getErrorMessage();
                        String convertedMessage = getMessage(response, requireActivity());
                        Toast.makeText(requireContext(), convertedMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Observes remote ViewModel responses and shows appropriate feedback.
     */
    private void setRemoteObserver() {
        userAccountViewModel.getRemoteLiveData()
                .observe(getViewLifecycleOwner(), event -> {
                    if (event == null) return;

                    Result result = event.getContentIfNotHandled();
                    if (result == null) return;

                    if (result instanceof Result.ResponseSuccess) {
                        String response = ((Result.ResponseSuccess) result).getResponse();
                        if (response != null && !response.isEmpty()) {
                            String convertedMessage = CallBacksMessages.getMessage(response, requireActivity());
                            Toast.makeText(requireContext(), convertedMessage, Toast.LENGTH_SHORT).show();
                        }
                    } else if (result instanceof Result.Error) {
                        String errorMessage = ((Result.Error) result).getErrorMessage();
                        if (!NETWORK_ERROR.equals(errorMessage)) {
                            String convertedMessage = getMessage(errorMessage, requireActivity());
                            Toast.makeText(requireContext(), convertedMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Observes update responses and handles result display logic.
     */
    private void setUpdateObserver() {
        userAccountViewModel.getUpdateLiveData().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;

            Result result = event.getContentIfNotHandled();

            if (result == null) return;

            if (result instanceof Result.ResponseSuccess) {
                String message = ((Result.ResponseSuccess) result).getResponse();
                if(!message.equals(EMAIL)){
                    String convertedMessage = CallBacksMessages.getMessage(message, requireActivity());
                    Toast.makeText(requireContext(), convertedMessage, Toast.LENGTH_SHORT).show();
                }
            } else if (result instanceof Result.Error) {
                String errorMessage = ((Result.Error) result).getErrorMessage();
                if (!errorMessage.equals(NETWORK_ERROR)) {
                    String convertedMessage = getMessage(errorMessage, requireActivity());
                    Toast.makeText(requireContext(), convertedMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Updates the UI with the user's data (username, email, profile picture).
     *
     * @param user            the user object
     * @param usernameText    the username field
     * @param emailText       the email field
     * @param profilePicture  the profile picture button
     * @param manager         the bitmap manager
     */
    private void setUpProfileInformation(User user, TextInputEditText usernameText, TextView emailText, MaterialButton profilePicture, BitMapManager manager) {
        usernameText.setText(user.getUsername());
        emailText.setText(user.getEmail());
        setProfilePictureDrawable(profilePicture, user.getProfilePicture(), manager);
    }

    /**
     * Sets the profile picture on the UI using a circular drawable.
     *
     * @param materialButton the profile picture button
     * @param encodedBitmap  the base64 encoded bitmap string
     * @param manager        the bitmap manager
     */
    private void setProfilePictureDrawable(MaterialButton materialButton, String encodedBitmap, BitMapManager manager) {
        Bitmap bitmap = manager.decodeBitmap(encodedBitmap);
        manager.createCircularDrawable(requireContext(), bitmap, drawable -> {
            materialButton.setIcon(drawable);
            materialButton.requestLayout();
        }, R.mipmap.default_profile_picture);
    }

    /**
     * Updates the user's username through the ViewModel.
     *
     * @param username the new username
     */
    private void updateUsername(final String username) {
        userAccountViewModel.setUsername(username);
    }


    /**
     * Updates the user's email address.
     *
     * @param email the new email
     */
    private void updateEmail(final String email) {
        userAccountViewModel.setEmail(email);
    }


    /**
     * Initiates a password reset for the given email address.
     *
     * @param email the user's email address
     */
    private void updatePassword(final String email) {
        if (email != null && !email.isEmpty()) {
            userAccountViewModel.setPassword(email);
        }
    }

    /**
     * Updates the user's profile picture in the ViewModel.
     *
     * @param encodedImage the base64 encoded image
     */
    private void updateProfilePicture(final String encodedImage) {
        userAccountViewModel.updateProfilePicture(encodedImage);
    }

    /**
     * Logs out the user, clears credentials, and navigates to the WelcomeActivity.
     *
     * @param extra optional extra parameter for activity transition
     */
    private void signOut(String extra) {
        CredentialManager credentialManager = CredentialManager.create(requireView().getContext());
        userAccountViewModel.logout(credentialManager, new ClearCredentialStateRequest());
        if (extra != null) {
            LaunchUtils.launchActivity(requireActivity(), requireContext(), WelcomeActivity.class, extra);
        } else {
            LaunchUtils.launchActivity(requireActivity(), requireContext(), WelcomeActivity.class);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkManagerSingleton.unregisterNetworkCallback();
    }
}