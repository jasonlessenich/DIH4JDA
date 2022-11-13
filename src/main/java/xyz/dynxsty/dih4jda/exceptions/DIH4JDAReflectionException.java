package xyz.dynxsty.dih4jda.exceptions;

/**
 * Exception that is thrown for everything that is related to reflection inside DIH4JDA.
 */
public class DIH4JDAReflectionException extends DIH4JDAException {
    /**
     * Creates a new instance of the {@link DIH4JDAReflectionException}.
     *
     * @param cause the {@link Throwable} that causes this exception.
     */
    public DIH4JDAReflectionException(Throwable cause) {
        super(cause);
    }
}
