package com.gotofinal.messages.bungee;

import com.gotofinal.messages.api.MessageReceiver;
import com.gotofinal.messages.api.chat.component.BaseComponent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.CommandSender;

public class BungeeMessageReceiver implements MessageReceiver
{
    private final BungeeMessagesAPI api;
    private final CommandSender     sender;

    public BungeeMessageReceiver(final BungeeMessagesAPI api, final CommandSender sender)
    {
        this.api = api;
        this.sender = sender;
    }

    /**
     * Sends message to receiver.
     *
     * @param msg message to send.
     */
    @Override
    public void sendMessage(final BaseComponent msg)
    {
        this.sender.sendMessage(this.api.chatConverter.apply(msg));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("api", this.api).append("sender", this.sender).toString();
    }
}
