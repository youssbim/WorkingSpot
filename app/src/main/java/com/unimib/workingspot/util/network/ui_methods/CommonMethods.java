package com.unimib.workingspot.util.network.ui_methods;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class CommonMethods {

    /**
     * Enables or disables a list of MaterialButtons.
     *
     * @param buttons List of MaterialButton instances to enable or disable.
     * @param enabled True to enable buttons, false to disable.
     */
    public static void disableButtons(List<MaterialButton> buttons, boolean enabled){
        for (MaterialButton button : buttons) {
            if (button != null) {
                button.setEnabled(enabled);
            }
        }
    }

    /**
     * Enables or disables a list of TextInputLayouts.
     *
     * @param textLayouts List of TextInputLayout instances to enable or disable.
     * @param enabled True to enable layouts, false to disable.
     */
    public static void disableTextLayouts(List<TextInputLayout> textLayouts, boolean enabled){
        for (TextInputLayout textLayout : textLayouts) {
            if (textLayout != null) {
                textLayout.setEnabled(enabled);
            }
        }
    }

    /**
     * Enables or disables a list of TextInputEditTexts.
     *
     * @param textEdits List of TextInputEditText instances to enable or disable.
     * @param enabled True to enable text edits, false to disable.
     */
    public static void disableTextEdits(List<TextInputEditText> textEdits, boolean enabled){
        for (TextInputEditText textEdit : textEdits) {
            if (textEdit != null) {
                textEdit.setEnabled(enabled);
            }
        }
    }
}
