package com.gotofinal.messages.api;

import com.gotofinal.messages.api.chat.component.BaseComponent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represent BroadcastReceiver, sending message to this receiver will send message to every player.
 */
public class BroadcastReceiver implements MessageReceiver
{
    private final MessagesAPI api;

    /**
     * Construct new BroadcastReceiver.
     *
     * @param api api for it.
     */
    public BroadcastReceiver(final MessagesAPI api)
    {
        this.api = api;
    }

    @Override
    public void sendMessage(final BaseComponent msg)
    {
        this.api.broadcastMessage(msg);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("api", this.api).toString();
    }
}
