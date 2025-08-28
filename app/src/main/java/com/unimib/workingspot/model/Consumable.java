package com.unimib.workingspot.model;

/**
 * Wrapper class to handle one-time events with LiveData,
 * preventing them from being consumed again after configuration changes or re-observations.
 *
 * @param <T> The type of data contained in the event (e.g., Result, String, etc.)
 */
public class Consumable<T> {
    private final T content;

    private boolean hasBeenHandled = Boolean.FALSE;

    /**
     * Constructor that sets the initial content of the event.
     *
     * @param content The data to encapsulate.
     */
    public Consumable(T content) {
        this.content = content;
    }

    /**
     * Returns the content only if it hasn't been handled yet.
     * The first call returns the content, subsequent calls return null.
     *
     * @return the content if not handled yet, otherwise null.
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = Boolean.TRUE;
            return content;
        }
    }

    /**
     * Returns the content regardless of whether it's been handled.
     * Useful for logging, debugging, or testing.
     *
     * @return the original content.
     */
    public T peekContent() {
        return content;
    }
}
