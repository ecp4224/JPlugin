package com.jplugins.exception;

import java.io.IOException;

public class PluginLoadException extends IOException {
    private static final long serialVersionUID = 7622737835347849700L;

    public PluginLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
