package com.unimib.workingspot.util.user_managing_utils.text_fields;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class TextFieldWrapper {
    private final TextInputLayout layout;
    private final TextInputEditText editText;
    private final MaterialButton button;

    public TextFieldWrapper(TextInputLayout layout, TextInputEditText editText, MaterialButton button) {
        this.layout = layout;
        this.editText = editText;
        this.button = button;
    }

    public TextInputLayout getLayout() {
        return layout;
    }

    public MaterialButton getButton() {
        return button;
    }

    public TextInputEditText getEditText() {
        return editText;
    }
}
