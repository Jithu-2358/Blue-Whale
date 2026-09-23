// exception/CommandNotFoundException.java
package com.bluewhale.exception;

public class CommandNotFoundException extends RuntimeException {
    public CommandNotFoundException(Long commandId) {
        super("Command not found: " + commandId);
    }
}