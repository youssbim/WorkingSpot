package com.unimib.workingspot.util.network.ui_methods.auth_methods;

import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.workingspot.R;
import com.unimib.workingspot.util.network.ui_methods.CommonMethods;

import java.util.Arrays;
import java.util.List;

public class RegistrationMethods {

    /**
     * Enables or disables the registration UI components within the given view.
     *
     * @param view    The root view containing all the UI components to manage.
     * @param enabled If true, UI components are enabled; if false, they are disabled.
     */
    public static void manageRegistrationUI(View view, boolean enabled) {

        List<TextInputLayout> textInputLayouts = Arrays.asList(
                view.findViewById(R.id.email_field_wrapper),
                view.findViewById(R.id.username_field_wrapper),
                view.findViewById(R.id.password_field_wrapper),
                view.findViewById(R.id.confirm_password_field_wrapper)
        );

        List<TextInputEditText> textInputEditTexts = Arrays.asList(
                view.findViewById(R.id.email_field),
                view.findViewById(R.id.username_field),
                view.findViewById(R.id.password_field),
                view.findViewById(R.id.confirm_password_field)
        );

        List<MaterialButton> buttons = Arrays.asList(
                view.findViewById(R.id.registration),
                view.findViewById(R.id.google_registration),
                view.findViewById(R.id.login_as_guest)
        );

        CommonMethods.disableTextLayouts(textInputLayouts, enabled);
        CommonMethods.disableTextEdits(textInputEditTexts, enabled);
        CommonMethods.disableButtons(buttons, enabled);
    }
}
