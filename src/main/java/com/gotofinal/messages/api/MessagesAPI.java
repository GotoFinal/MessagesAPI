package com.gotofinal.messages.api;

import java.util.Locale;
import java.util.logging.Logger;

import com.gotofinal.messages.api.chat.component.BaseComponent;
import com.gotofinal.messages.api.messages.MessageLoader;

/**
 * Every plugin should have own instance of this class.
 */
public interface MessagesAPI
{
    /**
     * Returns function that convert api base components to implementation ones.
     *
     * @return function that convert api base components to implementation ones.
     */
    ChatConverter<?> getChatConverter();

    /**
     * Set function that convert api base components to implementation ones.
     *
     * @param chatConverter function that convert api base components to implementation ones.
     */
    void setChatConverter(ChatConverter<?> chatConverter);

    /**
     * Returns function that convert implementation message receivers to {@link MessageReceiver} objects.
     *
     * @return function that convert implementation message receivers to {@link MessageReceiver} objects.
     */
    ReceiverConverter<?> getReceiverConverter();

    /**
     * Set function that convert implementation message receivers to {@link MessageReceiver} objects.
     *
     * @param receiverConverter function that convert implementation message receivers to {@link MessageReceiver} objects.
     */
    void setReceiverConverter(ReceiverConverter<?> receiverConverter);

    /**
     * Returns languages supported by this api instance.
     *
     * @return languages supported by this api instance.
     */
    Locale[] getLanguages();

    /**
     * Returns message loader for this api instance.
     *
     * @return message loader for this api instance.
     */
    MessageLoader getMessageLoader();

    /**
     * Sends message to all online players.
     *
     * @param msg message to send.
     */
    void broadcastMessage(BaseComponent msg);

    /**
     * Returns iterable of all receivers.
     *
     * @return iterable of all receivers.
     */
    Iterable<MessageReceiver> getReceivers();

    /**
     * Returns logger of this api instance.
     *
     * @return logger of this api instance.
     */
    Logger getLogger();
}
