package com.gotofinal.messages.api;

import java.util.Locale;

import com.gotofinal.messages.api.chat.component.BaseComponent;

/**
 * Represent message receiver.
 */
public interface MessageReceiver
{
    /**
     * Sends message to receiver.
     *
     * @param msg message to send.
     */
    void sendMessage(BaseComponent msg);

    /**
     * Returns preferred locale of this receiver, may return null.
     *
     * @return preferred locale of this receiver, may return null.
     */
    default Locale getPreferredLocale()
    {
        return null;
    }
}
