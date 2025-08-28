package com.unimib.workingspot.util.user_managing_utils.text_fields;

import static com.unimib.workingspot.util.constants.Constants.EMPTY_STRING;
import static com.unimib.workingspot.util.constants.AccountConstants.PASSWORD_MAX_LENGTH;
import static com.unimib.workingspot.util.constants.AccountConstants.PASSWORD_MIN_LENGTH;
import static com.unimib.workingspot.util.constants.Constants.PASSWORD_REGEX;
import  static com.unimib.workingspot.util.constants.Constants.PASSWORD;
import android.app.Activity;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.workingspot.R;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.RegexValidator;
import java.util.Objects;

/**
 * Provides utility methods for validating user input fields such as email, password,
 * and confirmation password within an Android Activity.
 * Each method returns a boolean indicating validity and sets error messages on the
 * corresponding TextInputLayout when validation fails.
 */
public abstract class TextFieldsCheck {


    /**
     * Checks whether a text field is non-null, non-empty, and not just whitespace.
     * Sets an error message on the TextInputLayout if the check fails.
     *
     * @param activity The Activity context for retrieving string resources.
     * @param textInputLayout The TextInputLayout associated with the input field.
     * @param textField The actual text input to check.
     * @param fieldNameReference The display name of the field used in error messages.
     * @return true if the field is valid (non-empty), false otherwise.
     */
    protected static boolean checkField(Activity activity, TextInputLayout textInputLayout, String textField, String fieldNameReference) {
        if(textField != null && !textField.isEmpty() && !textField.trim().isEmpty()){
            return setTextInputLayout(textInputLayout, EMPTY_STRING, Boolean.TRUE);
        }else{
            return setTextInputLayout(textInputLayout, fieldNameReference + activity.getString(R.string.reference_field_name_is_empty), Boolean.FALSE);
        }
    }


    /**
     * Validates the syntax of an email address.
     * Uses Apache Commons EmailValidator to verify proper email format.
     * Sets an error on the TextInputLayout if the email is invalid.
     *
     * @param activity The Activity context for retrieving string resources.
     * @param textInputLayout The TextInputLayout associated with the email field.
     * @param mailText The email string to validate.
     * @return true if the email is valid, false otherwise.
     */
    protected static boolean validateMail(Activity activity, TextInputLayout textInputLayout, String mailText) {
        if(EmailValidator.getInstance(Boolean.TRUE).isValid(mailText)){
            return setTextInputLayout(textInputLayout, EMPTY_STRING, Boolean.TRUE);
        }else{
            return setTextInputLayout(textInputLayout, activity.getString(R.string.not_valid), Boolean.FALSE);
        }
    }

    /**
     * Validates that the password meets length requirements and matches a specified regex pattern.
     * Sets an error on the TextInputLayout if the password is invalid.
     *
     * @param activity The Activity context for retrieving string resources.
     * @param textInputLayout The TextInputLayout associated with the password field.
     * @param password The password string to validate.
     * @return true if the password is valid, false otherwise.
     */
    protected static boolean isPasswordValid(Activity activity, TextInputLayout textInputLayout, final String password){
        if(password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH){
            return setTextInputLayout(textInputLayout, activity.getString(R.string.password_length_politics), Boolean.FALSE);
        }else {
            RegexValidator validator = new RegexValidator(PASSWORD_REGEX);
            if(validator.isValid(password)){
                return setTextInputLayout(textInputLayout, EMPTY_STRING, Boolean.TRUE);
            }else{
                return setTextInputLayout(textInputLayout, activity.getString(R.string.password_characters_politics), Boolean.FALSE);
            }
        }
    }

    /**
     * Checks if the password and confirmation password fields match exactly.
     * Sets an error on the TextInputLayout if they do not match.
     *
     * @param activity The Activity context for retrieving string resources.
     * @param textInputLayout The TextInputLayout associated with the confirmation password field.
     * @param password The original password string.
     * @param confirmationPassword The confirmation password string.
     * @return true if passwords match, false otherwise.
     */
    protected static boolean checkPasswordCorrespond(Activity activity, TextInputLayout textInputLayout, final String password, final String confirmationPassword) {
        if(password.equals(confirmationPassword)){
            return setTextInputLayout(textInputLayout, EMPTY_STRING, Boolean.TRUE);
        }else{
            return  setTextInputLayout(textInputLayout, activity.getString(R.string.passwords_dont_match), Boolean.FALSE);
        }
    }

    /**
     * Validates multiple boolean conditions.
     *
     * @param conditions Varargs of boolean conditions to check.
     * @return true if all conditions are true, false otherwise.
     */
    protected static boolean validateBooleans(boolean ... conditions) {
        for (boolean condition : conditions) {
            if (!condition) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Sets or clears the error message on the provided TextInputLayout.
     * Disables the error icon for password fields to avoid displaying it next to the error message.
     *
     * @param textInputLayout The TextInputLayout on which to set or clear the error.
     * @param errorText The error message to display if any.
     * @param setLayout true to clear error, false to set error.
     * @return The value of setLayout parameter (true if no error, false if error set).
     */
    protected static boolean setTextInputLayout(TextInputLayout textInputLayout, final String errorText, boolean setLayout){
        if(setLayout){
            textInputLayout.setErrorEnabled(Boolean.FALSE);
        }else{
            textInputLayout.setError(errorText);
            textInputLayout.setErrorEnabled(Boolean.TRUE);
            if(Objects.requireNonNull(textInputLayout.getHint()).toString().toLowerCase().contains(PASSWORD)) {
                textInputLayout.setErrorIconDrawable(null);
            }
        }
        return setLayout;
    }
}
