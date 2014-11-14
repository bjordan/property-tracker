package org.barryjordan.propertytracker;

import org.barryjordan.propertytracker.config.ContextConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Sets up application context.
 */
public final class PropertyTracker {

    /**
     * Suppress instantiation.
     */
    private PropertyTracker() {
        throw new IllegalStateException();
    }

    /**
     * Application's main method.
     *
     * @param args Command line arguments.
     */
    public static void main(final String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ContextConfig.class);
        ctx.refresh();
        ctx.registerShutdownHook();
    }
}
