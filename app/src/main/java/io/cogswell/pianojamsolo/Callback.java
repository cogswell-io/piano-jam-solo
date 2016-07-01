package io.cogswell.pianojamsolo;

/**
 * Represents a callback with a typed argument.
 */
public interface Callback<T> {
    /**
     * The method which is called upon completion.
     *
     * @param arg the argument to the callback.
     */
    public void call(T arg);
}
