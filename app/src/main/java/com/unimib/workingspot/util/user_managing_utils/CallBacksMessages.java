package com.unimib.workingspot.util.user_managing_utils;

import static com.unimib.workingspot.util.constants.AccountConstants.EMAIL_MODIFY_ERROR;
import static com.unimib.workingspot.util.constants.AccountConstants.PROFILE_PHOTO;
import static com.unimib.workingspot.util.constants.AccountConstants.PROFILE_PICTURE_MODIFY_ERROR;
import static com.unimib.workingspot.util.constants.AccountConstants.USERNAME_MODIFY_ERROR;
import static com.unimib.workingspot.util.constants.AccountConstants.USER_EXPIRED;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.CREDENTIAL_NOT_VALID;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.USER_COLLISION;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.WEAK_PASSWORD;
import static com.unimib.workingspot.util.constants.Constants.EMPTY_USERNAME_ERROR;
import static com.unimib.workingspot.util.constants.Constants.MAIL_INVALID;
import static com.unimib.workingspot.util.constants.Constants.NETWORK_ERROR;
import static com.unimib.workingspot.util.constants.Constants.PASSWORD;
import static com.unimib.workingspot.util.constants.Constants.SERVICE_UNAVAILABLE;
import static com.unimib.workingspot.util.constants.Constants.TOO_MANY_REQUEST;
import static com.unimib.workingspot.util.constants.Constants.UNEXPECTED_ERROR;
import static com.unimib.workingspot.util.constants.Constants.USERNAME;

import android.app.Activity;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.unimib.workingspot.R;

public abstract class CallBacksMessages {

    /**
     * Maps Firebase exceptions to standardized error message keys.
     * This helps abstract Firebase-specific exceptions into app-level constants.
     *
     * @param exception the caught Firebase exception
     * @return a string key representing the type of error
     */
    public static String getMessage(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return WEAK_PASSWORD;
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return CREDENTIAL_NOT_VALID;
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            return MAIL_INVALID;
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return USER_COLLISION;
        } else if (exception instanceof FirebaseAuthRecentLoginRequiredException) {
            return USER_EXPIRED;
        } else if (exception instanceof FirebaseNetworkException) {
            return NETWORK_ERROR;
        } else if (exception instanceof FirebaseTooManyRequestsException) {
            return TOO_MANY_REQUEST;
        } else if (exception instanceof FirebaseApiNotAvailableException) {
            return SERVICE_UNAVAILABLE;
        }
        return UNEXPECTED_ERROR;
    }

    /**
     * Converts error keys or message identifiers into user-friendly strings
     * fetched from Android string resources, for UI display.
     *
     * @param message a message key or constant
     * @param activity activity context used to fetch string resources
     * @return localized error message string
     */
    public static String getMessage(String message, Activity activity) {
        return switch (message) {
            case USERNAME_MODIFY_ERROR -> activity.getString(R.string.remote_username_update_error);
            case EMAIL_MODIFY_ERROR -> activity.getString(R.string.remote_email_update_error);
            case PROFILE_PICTURE_MODIFY_ERROR -> activity.getString(R.string.remote_profile_picture_update_error);
            case EMPTY_USERNAME_ERROR -> activity.getString(R.string.empty_username_error);
            case CREDENTIAL_NOT_VALID-> activity.getString(R.string.invalid_credentials_error);
            case WEAK_PASSWORD -> activity.getString(R.string.weak_password_error);
            case USER_COLLISION -> activity.getString(R.string.user_collision_error);
            case MAIL_INVALID -> activity.getString(R.string.invalid_user_error);
            case USER_EXPIRED -> activity.getString(R.string.user_login_expired);
            case NETWORK_ERROR -> activity.getString(R.string.network_error);
            case TOO_MANY_REQUEST -> activity.getString(R.string.too_many_requests_error);
            case SERVICE_UNAVAILABLE -> activity.getString(R.string.api_not_available_error);
            case USERNAME -> activity.getString(R.string.remote_username_correctly_updated);
            case PASSWORD -> activity.getString(R.string.email_sent);
            case PROFILE_PHOTO -> activity.getString(R.string.remote_profile_picture_correctly_updated);
            case UNEXPECTED_ERROR -> activity.getString(R.string.error_unexpected);
            default -> activity.getString(R.string.error_unexpected);
        };
    }
}
