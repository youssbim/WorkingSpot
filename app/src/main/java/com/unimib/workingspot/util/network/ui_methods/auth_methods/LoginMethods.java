package com.unimib.workingspot.util.network.ui_methods.auth_methods;

import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.workingspot.R;
import com.unimib.workingspot.util.network.ui_methods.CommonMethods;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class providing methods related to login UI management.
 */
public class LoginMethods {

    /**
     * Enables or disables UI components involved in the login process.
     * This method controls the enabled/disabled state of:
     * - Email and password input fields and their wrappers.
     * - Login-related buttons such as login, password recovery, Google login, and guest login.
     * @param view    The root view containing all the UI components to manage.
     * @param enabled If true, UI components are enabled; if false, they are disabled.
     */
    public static void manageLoginUI(View view, boolean enabled) {

        List<TextInputLayout> textInputLayouts = Arrays.asList(
                view.findViewById(R.id.email_field_wrapper),
                view.findViewById(R.id.password_field_wrapper)
        );

        List<TextInputEditText> textInputEditTexts = Arrays.asList(
                view.findViewById(R.id.email_field),
                view.findViewById(R.id.password_field)
        );

        List<MaterialButton> buttons = Arrays.asList(
                view.findViewById(R.id.login),
                view.findViewById(R.id.password_forgotten),
                view.findViewById(R.id.google_login),
                view.findViewById(R.id.login_as_guest)
        );

        CommonMethods.disableTextLayouts(textInputLayouts, enabled);
        CommonMethods.disableTextEdits(textInputEditTexts, enabled);
        CommonMethods.disableButtons(buttons, enabled);
    }
}
