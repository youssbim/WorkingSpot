package com.unimib.workingspot.util.user_managing_utils.text_fields;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.unimib.workingspot.util.constants.Constants.EMPTY_STRING;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.workingspot.R;

/**
 * Handles the UI logic and interactions for editable text fields related to user account management,
 * specifically for managing the username editing and email change visibility within the profile screen.
 * <p>
 * This class controls the enabling/disabling of username editing, input validation, submission handling,
 * and visibility toggling of email change fields.
 * </p>
 */
public class TextFieldsHandler {
    public static void setUsernameLayoutFocus(View view, TextFieldWrapper usernameTextFieldWrapper, boolean enabled, int extensionMode) {
        // Enable or disable focus and editing capabilities for the username field
        usernameTextFieldWrapper.getLayout().setHintAnimationEnabled(enabled);
        usernameTextFieldWrapper.getEditText().setFocusableInTouchMode(enabled);
        usernameTextFieldWrapper.getEditText().setFocusable(enabled);
        usernameTextFieldWrapper.getEditText().setClickable(enabled);
        usernameTextFieldWrapper.getEditText().setCursorVisible(enabled);

        // Adjust submit button width accordingly
        ViewGroup.LayoutParams params = usernameTextFieldWrapper.getButton().getLayoutParams();
        params.width = extensionMode;
        usernameTextFieldWrapper.getButton().setLayoutParams(params);

        if (enabled) {
            // When enabled, focus the input, set hint and change button to "submit" mode
            usernameTextFieldWrapper.getEditText().requestFocus();
            usernameTextFieldWrapper.getLayout().setBoxStrokeWidth((int) view.getContext().getResources().getDimension(R.dimen.box_stroke_width_default));
            usernameTextFieldWrapper.getLayout().setHint(view.getContext().getResources().getString(R.string.username));
            usernameTextFieldWrapper.getButton().setText(R.string.submit);
            usernameTextFieldWrapper.getButton().setIcon(null);
        } else {
            // When disabled, clear focus, reset visuals, and show edit icon on button
            usernameTextFieldWrapper.getEditText().clearFocus();
            usernameTextFieldWrapper.getEditText().setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.layout_stroke));
            usernameTextFieldWrapper.getLayout().setHint(null);
            usernameTextFieldWrapper.getButton().setText(null);
            usernameTextFieldWrapper.getButton().setIcon(ContextCompat.getDrawable(view.getContext(), R.drawable.pencil_outline));
        }
    }

    /**
     * Controls the visibility of the email change input fields.
     *
     * @param visible {@code true} to show the email change layout, {@code false} to hide it and reset input
     */
    public static void emailChangeFieldChangeVisibility(View view, boolean visible){
        LinearLayout blockLayout = view.findViewById(R.id.information_change_layout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) blockLayout.getLayoutParams();
        LinearLayout emailSendLayout = view.findViewById(R.id.email_text_linear_layout);

        if(visible){
            // Show email change input fields and expand container width
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            emailSendLayout.setVisibility(VISIBLE);
        } else {
            // Hide email change input fields, reset error, and clear input text
            TextInputLayout emailLayout = view.findViewById(R.id.email_text_layout);
            emailLayout.setErrorEnabled(Boolean.FALSE);

            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            emailSendLayout.setVisibility(GONE);

            TextInputEditText emailText = view.findViewById(R.id.email_text);
            emailText.setText(EMPTY_STRING);
        }

        blockLayout.setLayoutParams(params);
    }
}
