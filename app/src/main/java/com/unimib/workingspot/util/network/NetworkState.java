package com.unimib.workingspot.util.network;

/**
 * Represents the possible network connectivity states.
 */
public enum NetworkState {
    /**
     * The device was already online at cold startup,
     *
     */
    DEFAULT,

    /**
     * No network connectivity.
     */
    OFFLINE,

    /**
     * Network connectivity is available.
     */
    ONLINE
}
