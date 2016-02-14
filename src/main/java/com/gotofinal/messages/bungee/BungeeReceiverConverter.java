package com.gotofinal.messages.bungee;

import com.gotofinal.messages.api.MessageReceiver;
import com.gotofinal.messages.api.ReceiverConverter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.CommandSender;

public class BungeeReceiverConverter implements ReceiverConverter<CommandSender>
{
    private final BungeeMessagesAPI api;

    public BungeeReceiverConverter(final BungeeMessagesAPI api)
    {
        this.api = api;
    }

    @Override
    public MessageReceiver apply(final CommandSender commandSender)
    {
        return new BungeeMessageReceiver(this.api, commandSender);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("api", this.api).toString();
    }
}
