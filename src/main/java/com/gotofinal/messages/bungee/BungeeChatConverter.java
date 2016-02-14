package com.gotofinal.messages.bungee;


import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.gotofinal.messages.api.ChatConverter;
import com.gotofinal.messages.api.UnsupportedChatElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class BungeeChatConverter implements ChatConverter<BaseComponent>
{
    private static final BaseComponent[] EMPTY = new BaseComponent[0];

    private final Map<com.gotofinal.messages.api.chat.ChatColor, ChatColor>                           colorMap       = new EnumMap<>(com.gotofinal.messages.api.chat.ChatColor.class);
    private final Map<com.gotofinal.messages.api.chat.component.ClickEvent.Action, ClickEvent.Action> clickActionMap = new EnumMap<>(com.gotofinal.messages.api.chat.component.ClickEvent.Action.class);
    private final Map<com.gotofinal.messages.api.chat.component.HoverEvent.Action, HoverEvent.Action> hoverActionMap = new EnumMap<>(com.gotofinal.messages.api.chat.component.HoverEvent.Action.class);

    private ChatColor getColor(final com.gotofinal.messages.api.chat.ChatColor color)
    {
        if (color == null)
        {
            return null;
        }
        return this.colorMap.get(color);
    }

    private ClickEvent.Action getAction(final com.gotofinal.messages.api.chat.component.ClickEvent.Action action)
    {
        if (action == null)
        {
            return null;
        }
        return this.clickActionMap.get(action);
    }

    private HoverEvent.Action getAction(final com.gotofinal.messages.api.chat.component.HoverEvent.Action action)
    {
        if (action == null)
        {
            return null;
        }
        return this.hoverActionMap.get(action);
    }

    {
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.BLACK, ChatColor.BLACK);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.DARK_BLUE, ChatColor.DARK_BLUE);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.DARK_GREEN, ChatColor.DARK_GREEN);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.DARK_AQUA, ChatColor.DARK_AQUA);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.DARK_RED, ChatColor.DARK_RED);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.DARK_PURPLE, ChatColor.DARK_PURPLE);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.GOLD, ChatColor.GOLD);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.GRAY, ChatColor.GRAY);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.DARK_GRAY, ChatColor.DARK_GRAY);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.BLUE, ChatColor.BLUE);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.GREEN, ChatColor.GREEN);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.AQUA, ChatColor.AQUA);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.RED, ChatColor.RED);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.LIGHT_PURPLE, ChatColor.LIGHT_PURPLE);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.YELLOW, ChatColor.YELLOW);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.WHITE, ChatColor.WHITE);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.MAGIC, ChatColor.MAGIC);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.BOLD, ChatColor.BOLD);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.STRIKETHROUGH, ChatColor.STRIKETHROUGH);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.UNDERLINE, ChatColor.UNDERLINE);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.ITALIC, ChatColor.ITALIC);
        this.colorMap.put(com.gotofinal.messages.api.chat.ChatColor.RESET, ChatColor.RESET);

        this.clickActionMap.put(com.gotofinal.messages.api.chat.component.ClickEvent.Action.OPEN_URL, ClickEvent.Action.OPEN_URL);
        this.clickActionMap.put(com.gotofinal.messages.api.chat.component.ClickEvent.Action.OPEN_FILE, ClickEvent.Action.OPEN_FILE);
        this.clickActionMap.put(com.gotofinal.messages.api.chat.component.ClickEvent.Action.RUN_COMMAND, ClickEvent.Action.RUN_COMMAND);
        this.clickActionMap.put(com.gotofinal.messages.api.chat.component.ClickEvent.Action.SUGGEST_COMMAND, ClickEvent.Action.SUGGEST_COMMAND);

        this.hoverActionMap.put(com.gotofinal.messages.api.chat.component.HoverEvent.Action.SHOW_TEXT, HoverEvent.Action.SHOW_TEXT);
        this.hoverActionMap.put(com.gotofinal.messages.api.chat.component.HoverEvent.Action.SHOW_ACHIEVEMENT, HoverEvent.Action.SHOW_ACHIEVEMENT);
        this.hoverActionMap.put(com.gotofinal.messages.api.chat.component.HoverEvent.Action.SHOW_ITEM, HoverEvent.Action.SHOW_ITEM);
    }

    public static void main(String[] args)
    {
        BungeeChatConverter c = new BungeeChatConverter();
    }

    @Override
    public BaseComponent apply(final com.gotofinal.messages.api.chat.component.BaseComponent baseComponent)
    {
        final BaseComponent cpy;
        if (baseComponent instanceof com.gotofinal.messages.api.chat.component.TextComponent)
        {
            cpy = new TextComponent(((com.gotofinal.messages.api.chat.component.TextComponent) baseComponent).getText());
        }
        else if (baseComponent instanceof com.gotofinal.messages.api.chat.component.TranslatableComponent)
        {
            final com.gotofinal.messages.api.chat.component.TranslatableComponent tmp = (com.gotofinal.messages.api.chat.component.TranslatableComponent) baseComponent;
            cpy = new TranslatableComponent(tmp.getTranslate(), tmp.getWith());
        }
        else
        {
            throw new UnsupportedChatElement(baseComponent.toString());
        }
        cpy.setBold(baseComponent.isBoldRaw());
        cpy.setItalic(baseComponent.isItalicRaw());
        cpy.setObfuscated(baseComponent.isObfuscatedRaw());
        cpy.setStrikethrough(baseComponent.isStrikethroughRaw());
        cpy.setUnderlined(baseComponent.isUnderlinedRaw());
        cpy.setColor(this.getColor(baseComponent.getColorRaw()));

        {
            final com.gotofinal.messages.api.chat.component.ClickEvent tmp = baseComponent.getClickEvent();
            if (tmp != null)
            {
                final ClickEvent cpyEvt = new ClickEvent(this.getAction(tmp.getAction()), tmp.getValue());
                cpy.setClickEvent(cpyEvt);
            }
        }
        {
            final com.gotofinal.messages.api.chat.component.HoverEvent tmp = baseComponent.getHoverEvent();
            if (tmp != null)
            {
                final HoverEvent cpyEvt = new HoverEvent(this.getAction(tmp.getAction()), this.apply(tmp.getValue()));
                cpy.setHoverEvent(cpyEvt);
            }
        }

        final List<com.gotofinal.messages.api.chat.component.BaseComponent> extra = baseComponent.getExtra();
        if ((extra == null) || extra.isEmpty())
        {
            return cpy;
        }
        for (final com.gotofinal.messages.api.chat.component.BaseComponent ext : extra)
        {
            cpy.addExtra(this.apply(ext));
        }
        return cpy;
    }

    public BaseComponent[] apply(final com.gotofinal.messages.api.chat.component.BaseComponent[] baseComponent)
    {
        if ((baseComponent == null) || (baseComponent.length == 0))
        {
            return EMPTY;
        }
        final BaseComponent[] result = new BaseComponent[baseComponent.length];
        for (int i = 0; i < baseComponent.length; i++)
        {
            result[i] = this.apply(baseComponent[i]);
        }
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("colorMap", this.colorMap).append("clickActionMap", this.clickActionMap).append("hoverActionMap", this.hoverActionMap).toString();
    }
}
