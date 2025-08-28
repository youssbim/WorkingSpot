package com.unimib.workingspot.util.activity_launch_utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Utility class for launching new activities in a standardized way.
 */
public abstract class LaunchUtils {

    /**
     * Launches a new activity, clearing the current task stack.
     *
     * @param currentActivity The current activity to be finished.
     * @param context The context used to start the new activity.
     * @param targetActivity The class of the activity to be launched.
     */
    public static void launchActivity(Activity currentActivity, Context context, Class<?> targetActivity) {
        Intent intent = new Intent(context, targetActivity);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        currentActivity.finish();
    }

    /**
     * Launches a new activity with a boolean extra, clearing the current task stack.
     *
     * @param currentActivity The current activity to be finished.
     * @param context The context used to start the new activity.
     * @param targetActivity The class of the activity to be launched.
     * @param extra The key for the boolean extra to be passed (set to true).
     */
    public static void launchActivity(Activity currentActivity, Context context, Class<?> targetActivity, String extra) {
        Intent intent = new Intent(context, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(extra, Boolean.TRUE);
        context.startActivity(intent);
        currentActivity.finish();
    }
}
