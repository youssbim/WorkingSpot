package com.unimib.workingspot.util.data_store;

/**
 * Collection of callback interfaces used by DataStoreManagerSingleton
 * to handle asynchronous success and error events during DataStore operations.
 */
public interface DataStoreCallbacks {

    /**
     * Functional interface for handling success after a resource is created/stored.
     */
    @FunctionalInterface
    interface OnSuccessCreation {
        void onSuccess();
    }

    /**
     * Functional interface for handling success when a resource is retrieved.
     */
    @FunctionalInterface
    interface OnSuccessRetrieve {

     /**
      * @param resource The retrieved string value from the DataStore.
      */
        void onRetrieve(String resource);
    }

    /**
     * Functional interface for handling success when a resource is deleted.
     */
    @FunctionalInterface
    interface OnSuccessDelete {
        /**
         * @param resource The key of the deleted resource.
         */
        void onDelete(String resource);
    }

    /**
     * Functional interface for handling any failure during a DataStore operation.
     */
    @FunctionalInterface
    interface OnFailure {
        /**
         * @param throwable The exception that was thrown during the operation.
         */
        void onError(Throwable throwable);
    }
}
