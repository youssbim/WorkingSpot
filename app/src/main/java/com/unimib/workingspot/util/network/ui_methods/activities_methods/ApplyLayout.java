package com.unimib.workingspot.util.network.ui_methods.activities_methods;

import static com.unimib.workingspot.util.constants.Constants.ONLINE_MESSAGE_DURATION_MS;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.unimib.workingspot.R;

/**
 * Utility class to apply layout changes on a TextView representing online/offline status.
 * It updates the text, background color, and visibility based on the online state.
 */
public class ApplyLayout {

    /**
     * Updates the appearance of the provided TextView to reflect online/offline status.
     *
     * @param context     The context used to access resources.
     * @param statusText  The TextView to update.
     * @param isOnline    True if the user/device is online, false otherwise.
     */
    public static void applyLayout(Context context, TextView statusText, boolean isOnline) {
        if (isOnline) {
            statusText.setText(R.string.online_text_view_message);
            statusText.setBackground(ContextCompat.getDrawable(context, R.color.md_theme_tertiary_highContrast));
            statusText.setVisibility(View.VISIBLE);
            statusText.postDelayed(() -> statusText.setVisibility(View.INVISIBLE), ONLINE_MESSAGE_DURATION_MS);
        } else {
            statusText.setText(R.string.offline_text_view_message);
            statusText.setBackground(ContextCompat.getDrawable(context, R.color.md_theme_error));
            statusText.setVisibility(View.VISIBLE);
        }
    }
}
