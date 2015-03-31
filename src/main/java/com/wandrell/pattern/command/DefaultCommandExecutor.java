/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 the original author or authors.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.wandrell.pattern.command;

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link CommandExecutor} interface. This
 * executor just calls the received command's {@code execute()} method, and
 * returns any value if needed.
 * <p>
 * There are no additional operations, such as dependency injections or context
 * configuration. The commands are executed as soon as they are received.
 * <p>
 * This executor is meant for handling only the simplest commands. Anything more
 * fancy than just executing a method would require it's own implementation of
 * the {@code CommandExecutor} interface.
 * <p>
 * Exceptions thrown by the commands are handled in a very simple way. They are
 * caught, logged, then thrown again. The only special case is that if they are
 * not an instance of {@code RuntimeException} then they are wrapped into this
 * class before being thrown again.
 * 
 * @author Bernardo Martínez Garrido
 */
public final class DefaultCommandExecutor implements CommandExecutor {

    /**
     * The logger used for logging exceptions thrown by the commands.
     */
    private static final Logger LOGGER = LoggerFactory
                                               .getLogger(DefaultCommandExecutor.class);

    /**
     * Returns the logger being used to log exceptions thrown by commands.
     * 
     * @return the logger being used
     */
    private static final Logger getLogger() {
        return LOGGER;
    }

    /**
     * Constructs a {@code DefaultCommandExecutor}.
     */
    public DefaultCommandExecutor() {
        super();
    }

    /**
     * Executes the received {@link Command}.
     * <p>
     * Any exception thrown by the command is caught, logged, then thrown again.
     * All these exceptions will be an instance of {@code RuntimeException}, or
     * be wrapped by it, when they are thrown out of this method.
     * 
     * @param command
     *            the {@code Command} to be executed
     */
    @Override
    public final void execute(final Command command) {
        checkNotNull(command, "Received a null pointer as command");

        try {
            command.execute();
        } catch (final RuntimeException exception) {
            // RuntimeExceptions are just thrown after logging
            getLogger().error(exception.getMessage());
            throw exception;
        } catch (final Exception exception) {
            // Other exceptions are wrapped after logging
            getLogger().error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    /**
     * Executes the received {@link ResultCommand} and returns the value
     * generated by this operation.
     * <p>
     * Any exception thrown by the command is caught, logged, then thrown again.
     * All these exceptions will be an instance of {@code RuntimeException}, or
     * be wrapped by it, when they are thrown out of this method.
     * 
     * @param command
     *            the {@code ResultCommand} to be executed
     * @param <V>
     *            the command's return type
     * @return the command's return value
     */
    @Override
    public final <V> V execute(final ResultCommand<V> command) {
        checkNotNull(command, "Received a null pointer as command");

        execute((Command) command);

        return command.getResult();
    }

    /**
     * Undoes the received {@link Command}.
     * <p>
     * Any exception thrown by the command is caught, logged, then thrown again.
     * All these exceptions will be an instance of {@code RuntimeException}, or
     * be wrapped by it, when they are thrown out of this method.
     * 
     * @param command
     *            the {@code Command} to be executed
     */
    @Override
    public void undo(UndoCommand command) {
        try {
            command.undo();
        } catch (final RuntimeException exception) {
            // RuntimeExceptions are just thrown after logging
            getLogger().error(exception.getMessage());
            throw exception;
        } catch (final Exception exception) {
            // Other exceptions are wrapped after logging
            getLogger().error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

}
