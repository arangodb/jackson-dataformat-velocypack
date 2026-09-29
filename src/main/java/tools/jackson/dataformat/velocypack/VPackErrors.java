package tools.jackson.dataformat.velocypack;

import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.exc.StreamWriteException;

/**
 * Construction of failures shared by the low-level wire code.
 *
 * <p>Keeping this here is intentional: arithmetic and byte-layout helpers must
 * not expose {@link ArithmeticException}, array-bound exceptions, or a Java
 * parser exception to the eventual parser/generator.</p>
 */
final class VPackErrors {
    private VPackErrors() { }

    static StreamReadException malformed(String context, long offset, String detail) {
        return new StreamReadException(null, message(context, offset, detail), location(offset));
    }

    static StreamReadException malformed(String context, String detail) {
        return new StreamReadException(message(context, -1L, detail));
    }

    static StreamReadException input(String context, long offset, Throwable cause) {
        return new StreamReadException(null, message(context, offset,
                cause == null ? "input failure" : cause.getMessage()), location(offset), cause);
    }

    static StreamConstraintsException constraint(String context, long offset, String detail) {
        return new StreamConstraintsException(message(context, offset, detail), location(offset));
    }

    static StreamWriteException write(String context, String detail) {
        return new StreamWriteException(null, message(context, -1L, detail));
    }

    private static String message(String context, long offset, String detail) {
        StringBuilder message = new StringBuilder();
        if (context != null && !context.isBlank()) {
            message.append(context).append(": ");
        }
        message.append(detail);
        if (offset >= 0L) {
            message.append(" (offset ").append(offset).append(')');
        }
        return message.toString();
    }

    private static TokenStreamLocation location(long offset) {
        return new TokenStreamLocation(null, offset, -1L, -1, -1);
    }
}
