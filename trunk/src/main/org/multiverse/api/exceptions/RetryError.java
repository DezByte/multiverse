package org.multiverse.api.exceptions;

import static java.lang.Boolean.parseBoolean;

/**
 * An Error dat indicates that a retry should be done.
 * <p/>
 * It is an error because it should not be caught by some exception handler. This is a
 * control flow regulating exception. Something that normally would be a very bad thing,
 * but adding custom control flow to a fixed language like Java is otherwise almost impossible
 * to do transparently.
 *
 * @author Peter Veentjer.
 */
public class RetryError extends Error {

    private final static boolean reuse = parseBoolean(System.getProperty(RetryError.class.getName(), "true"));

    public final static RetryError INSTANCE = new RetryError();

    public static RetryError create() {
        if (reuse) {
            return RetryError.INSTANCE;
        } else {
            return new RetryError();
        }
    }
}