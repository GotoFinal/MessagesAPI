package com.gotofinal.messages.bungee;

import java.util.Iterator;
import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Logger;

import com.gotofinal.messages.BaseMessagesAPI;
import com.gotofinal.messages.api.ChatConverter;
import com.gotofinal.messages.api.MessageReceiver;
import com.gotofinal.messages.api.ReceiverConverter;
import com.gotofinal.messages.api.chat.component.BaseComponent;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMessagesAPI extends BaseMessagesAPI
{
    protected ChatConverter<net.md_5.bungee.api.chat.BaseComponent> chatConverter     = new BungeeChatConverter();
    protected ReceiverConverter<CommandSender>                      receiverConverter = new BungeeReceiverConverter(this);
    protected final Plugin plugin;

    public BungeeMessagesAPI(final Plugin plugin, final Locale... locales)
    {
        super(locales);
        this.plugin = plugin;
    }

    @Override
    public ChatConverter<net.md_5.bungee.api.chat.BaseComponent> getChatConverter()
    {
        return this.chatConverter;
    }

    @Override
    public ReceiverConverter<CommandSender> getReceiverConverter()
    {
        return this.receiverConverter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setChatConverter(final ChatConverter<?> chatConverter)
    {
        this.chatConverter = (ChatConverter<net.md_5.bungee.api.chat.BaseComponent>) chatConverter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setReceiverConverter(final ReceiverConverter<?> receiverConverter)
    {
        this.receiverConverter = (ReceiverConverter<CommandSender>) receiverConverter;
    }

    @Override
    public void broadcastMessage(final BaseComponent msg)
    {
        Bukkit.spigot().broadcast(this.chatConverter.apply(msg));
    }

    @Override
    public Iterable<MessageReceiver> getReceivers()
    {
        return new PlayerIterable(this.receiverConverter, this.plugin.getProxy().getPlayers());
    }

    @Override
    public Logger getLogger()
    {
        return this.plugin.getLogger();
    }

    private static final class PlayerIterable implements Iterable<MessageReceiver>
    {
        private final Function<CommandSender, MessageReceiver> func;
        private final Iterable<? extends CommandSender>        senders;

        private PlayerIterable(final Function<CommandSender, MessageReceiver> func, final Iterable<? extends CommandSender> senders)
        {
            this.func = func;
            this.senders = senders;
        }

        @Override
        public Iterator<MessageReceiver> iterator()
        {
            return new PlayerIterator(this.func, this.senders.iterator());
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("func", this.func).append("senders", this.senders).toString();
        }
    }

    private static final class PlayerIterator implements Iterator<MessageReceiver>
    {
        private final Function<CommandSender, MessageReceiver> func;
        private final Iterator<? extends CommandSender>        playerIterator;

        private PlayerIterator(final Function<CommandSender, MessageReceiver> func, final Iterator<? extends CommandSender> playerIterator)
        {
            this.func = func;
            this.playerIterator = playerIterator;
        }

        @Override
        public boolean hasNext()
        {
            return this.playerIterator.hasNext();
        }

        @Override
        public MessageReceiver next()
        {
            return this.func.apply(this.playerIterator.next());
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("func", this.func).append("playerIterator", this.playerIterator).toString();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("chatConverter", this.chatConverter).append("receiverConverter", this.receiverConverter).append("plugin", this.plugin).toString();
    }
}
