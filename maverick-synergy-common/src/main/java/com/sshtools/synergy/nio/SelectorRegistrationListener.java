package com.sshtools.synergy.nio;

import java.io.IOException;
import java.nio.channels.*;

/**
 * Allows an object to receive notification that a registration has completed
 * on a {@link SelectorThread}.
 */
public interface SelectorRegistrationListener {

        /**
         * The registration completed and its selector added to an available
         * {@link SelectorThread}
         *
         * @param channel SelectableChannel
         * @param key SelectionKey
         * @param selectorThread SelectorThread
         * @throws IOException 
         */
        public void registrationCompleted(SelectableChannel channel,
                                    SelectionKey key,
                                    SelectorThread selectorThread) throws IOException;
}
