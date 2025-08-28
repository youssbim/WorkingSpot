package com.unimib.workingspot.util.user_managing_utils.text_fields;

import static com.unimib.workingspot.util.user_managing_utils.text_fields.TextFieldsCheck.checkField;
import static com.unimib.workingspot.util.user_managing_utils.text_fields.TextFieldsCheck.checkPasswordCorrespond;
import static com.unimib.workingspot.util.user_managing_utils.text_fields.TextFieldsCheck.isPasswordValid;
import static com.unimib.workingspot.util.user_managing_utils.text_fields.TextFieldsCheck.validateBooleans;
import static com.unimib.workingspot.util.user_managing_utils.text_fields.TextFieldsCheck.validateMail;
import android.app.Activity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.workingspot.R;

import java.util.Objects;

/**
 * Utility class for validating and handling input fields related to user authentication.
 * This class provides static methods to validate email, username, password, and confirmation password
 * fields for both sign-up and sign-in forms within an Activity.
 */
public abstract class FieldCheckHandler {

    /**
     * Validates the given email field.
     * It first checks if the email field is non-empty, then validates the email format.
     *
     * @param activity The current Activity context used to access resources and UI elements.
     * @param validEmailLayout The TextInputLayout wrapping the email input field.
     * @param emailText The string content of the email field to validate.
     * @return true if the email is non-empty and properly formatted, false otherwise.
     */
    public  static boolean isEmailValid(Activity activity, TextInputLayout validEmailLayout, final String emailText){
        boolean validMailText = checkField(activity, validEmailLayout, emailText, activity.getString(R.string.email));
        if(validMailText){
            return validateMail(activity, validEmailLayout, emailText);
        }
        return Boolean.FALSE;
    }

    /**
     * Validates all the fields required for user sign-up: email, username, password, and password confirmation.
     * Each field is checked for presence and format correctness.
     *
     * @param activity The current Activity context used to access UI elements.
     * @return true if all sign-up fields are valid, false otherwise.
     */
    public static boolean handleSignUpFields(Activity activity){
        TextInputLayout validEmailLayout = activity.findViewById(R.id.email_field_wrapper);
        String emailText = Objects.requireNonNull(((TextInputEditText) activity.findViewById(R.id.email_field)).getText()).toString();
        TextInputLayout validUsernameLayout = activity.findViewById(R.id.username_field_wrapper);
        String usernameText = Objects.requireNonNull(((TextInputEditText) activity.findViewById(R.id.username_field)).getText()).toString();
        TextInputLayout validPasswordLayout = activity.findViewById(R.id.password_field_wrapper);
        String passwordText = Objects.requireNonNull(((TextInputEditText) activity.findViewById(R.id.password_field)).getText()).toString();
        TextInputLayout validConfirmationPasswordLayout = activity.findViewById(R.id.confirm_password_field_wrapper);
        String confirmationPasswordText = Objects.requireNonNull(((TextInputEditText) activity.findViewById(R.id.confirm_password_field)).getText()).toString();
        boolean validMailText;
        boolean validUsernameText;
        boolean validPasswordText;
        boolean validConfirmationPasswordText;

        validMailText = isEmailValid(activity, validEmailLayout, emailText);
        validUsernameText = checkField(activity, validUsernameLayout, usernameText, activity.getString(R.string.username));
        validPasswordText = Boolean.FALSE;
        if(checkField(activity, validPasswordLayout, passwordText, activity.getString(R.string.password))){
            validPasswordText = isPasswordValid(activity, validPasswordLayout, passwordText);
        }
        validConfirmationPasswordText = checkField(activity, validConfirmationPasswordLayout,
                confirmationPasswordText, activity.getString(R.string.confirmation_password));
        if(validConfirmationPasswordText){
            validConfirmationPasswordText = checkPasswordCorrespond(activity, validConfirmationPasswordLayout, passwordText, confirmationPasswordText);
        }

        return(validateBooleans(validMailText, validUsernameText, validPasswordText, validConfirmationPasswordText));
    }

    /**
     * Validates the email and password fields for user sign-in.
     * Both fields are checked for presence and correctness.
     *
     * @param activity The current Activity context used to access UI elements.
     * @return true if both email and password fields are valid, false otherwise.
     */
    public static boolean handleSignInFields(Activity activity){
        TextInputLayout validEmailLayout = activity.findViewById(R.id.email_field_wrapper);
        String emailText = Objects.requireNonNull(((TextInputEditText) activity.findViewById(R.id.email_field)).getText()).toString();
        TextInputLayout validPasswordLayout = activity.findViewById(R.id.password_field_wrapper);
        String passwordText = Objects.requireNonNull(((TextInputEditText) activity.findViewById(R.id.password_field)).getText()).toString();
        boolean validMailText;
        boolean validPasswordText;

        validMailText = checkField(activity, validEmailLayout, emailText, activity.getString(R.string.email));
        if(validMailText){
            validMailText = validateMail(activity, validEmailLayout, emailText);
        }
        validPasswordText = Boolean.FALSE;
        if(checkField(activity, validPasswordLayout, passwordText, activity.getString(R.string.password))){
            validPasswordText = isPasswordValid(activity, validPasswordLayout, passwordText);
        }

        return(validateBooleans(validMailText, validPasswordText));
    }
}
