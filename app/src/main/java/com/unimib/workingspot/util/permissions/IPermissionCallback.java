package com.unimib.workingspot.util.permissions;

public interface IPermissionCallback {

    /**
     * Callback for when the geo-localization permissions have been granted by the user
     */
    void onPermissionRequestSuccess();

    /**
     * Callback for when the geo-localization permissions have not been granted
     */
    void onPermissionRequestFailure();
}
