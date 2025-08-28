package com.unimib.workingspot.util.network.ui_methods.logged_profile_methods;

import android.view.View;
import com.google.android.material.button.MaterialButton;
import com.unimib.workingspot.R;
import com.unimib.workingspot.util.network.ui_methods.CommonMethods;
import java.util.Arrays;
import java.util.List;

public class LoggedProfileMethods{

    /**
     * Enables or disables the profile-related buttons within the given view.
     *
     * @param view    The root view containing the profile UI buttons.
     * @param enabled True to enable buttons, false to disable them.
     */
    public static void setButtonsEnabled(View view, boolean enabled) {
        List<MaterialButton> buttons = Arrays.asList(
                view.findViewById(R.id.profile_picture),
                view.findViewById(R.id.submit_username_button),
                view.findViewById(R.id.change_email),
                view.findViewById(R.id.change_password)
        );

        CommonMethods.disableButtons(buttons, enabled);

    }
}
