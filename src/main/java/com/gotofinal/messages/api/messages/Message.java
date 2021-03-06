/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016. Diorite (by Bartłomiej Mazur (aka GotoFinal))
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gotofinal.messages.api.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import com.gotofinal.messages.api.MessageReceiver;
import com.gotofinal.messages.api.MessagesAPI;
import com.gotofinal.messages.api.chat.ChatColor;
import com.gotofinal.messages.api.chat.component.BaseComponent;
import com.gotofinal.messages.api.chat.component.serialize.ComponentSerializer;
import com.gotofinal.messages.api.chat.placeholder.PlaceholderData;
import com.gotofinal.messages.api.chat.placeholder.PlaceholderType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@SuppressWarnings("ClassHasNoToStringMethod")
public abstract class Message
{
    /**
     * Instance of random for all messages.
     */
    protected static final Random random = new Random();
    /**
     * Owner api instance.
     */
    protected final MessagesAPI                                 api;
    /**
     * Used to store placeholders used in this message.
     */
    protected final Map<String, Collection<PlaceholderData<?>>> placeholders;

    /**
     * Construct new message with given placeholders.
     *
     * @param api          instance of api that owns this message.
     * @param placeholders placeholders used in this message.
     */
    protected Message(final MessagesAPI api, final Map<String, Collection<PlaceholderData<?>>> placeholders)
    {
        this.api = api;
        this.placeholders = placeholders;
    }

    /**
     * Load message for given map of locales and objects (string, array or collection), used
     * to load message from language files.
     *
     * @param objects map of locales and objects (string, array or collection).
     *
     * @return Loaded message.
     */
    static Message load(final MessagesAPI api, final Map<Locale, Object> objects)
    {
        if (objects.isEmpty())
        {
            throw new RuntimeException("No languages enabled... " + objects);
        }
        if (objects.size() == 1)
        {
            return load(api, objects.values().iterator().next());
        }
        final Map<Locale, String> mapA = new HashMap<>(objects.size());
        final Map<Locale, String[]> mapB = new HashMap<>(objects.size());
        for (final Entry<Locale, Object> entry : objects.entrySet())
        {
            final Locale locale = entry.getKey();
            final Object obj = entry.getValue();
            if (obj instanceof String)
            {
                mapA.put(locale, (String) obj);
                continue;
            }
            if (obj instanceof String[])
            {
                mapB.put(locale, (String[]) obj);
                continue;
            }
            if (obj instanceof Collection)
            {
                final Collection<?> col = ((Collection<?>) obj);
                final String[] array = new String[col.size()];
                int i = 0;
                for (final Object o : col)
                {
                    array[i++] = o.toString();
                }
                mapB.put(locale, array);
                continue;
            }
            mapA.put(locale, obj.toString());
        }
        if (mapB.isEmpty() && mapA.isEmpty())
        {
            throw new RuntimeException("No languages enabled... " + objects);
        }
        if (mapA.isEmpty())
        {
            return new LocalizedRandomMessage(api, mapB);
        }
        if (mapB.isEmpty())
        {
            return new LocalizedMessage(api, mapA);
        }
        return new LocalizedMixedMessage(api, mapA, mapB);
    }

    /**
     * Load message for given object (string, array or collection), used
     * to load message from language file (only when only one language is used).
     *
     * @param object string, array or collection of messages.
     *
     * @return Loaded message.
     */
    static Message load(final MessagesAPI api, final Object object)
    {
        if ((object instanceof Boolean) && ! ((Boolean) object))
        {
            return new DisabledMessage(api);
        }
        if (object instanceof String)
        {
            return new SimpleMessage(api, (String) object);
        }
        if (object instanceof String[])
        {
            return new SimpleRandomMessage(api, (String[]) object);
        }
        if (object instanceof Collection)
        {
            final Collection<?> col = ((Collection<?>) object);
            final String[] array = new String[col.size()];
            int i = 0;
            for (final Object o : col)
            {
                array[i++] = o.toString();
            }
            return new SimpleRandomMessage(api, array);
        }
        return new SimpleMessage(api, object.toString());
    }

    /**
     * Get BaseComponent to send, may return null if message is disabled.
     *
     * @param lang language to use if possible.
     * @param data placeholder objects to use.
     *
     * @return BaseComponent to send or null if disabled.
     */
    public abstract BaseComponent get(Locale lang, MessageData... data);

    /**
     * Serialize all messages to given map, all base components are serialized to json string.
     *
     * @param map             map where all messages will be added.
     * @param defaultLanguage default locale of messages.
     * @param node            name for this message.
     *
     * @return this same mas as given.
     *
     * @see BaseComponent#canBeLegacy()
     */
    public abstract Map<Locale, Map<String, Object>> toMap(Map<Locale, Map<String, Object>> map, final Locale defaultLanguage, final String node);

    /**
     * Returns true if this message is enabled.
     * @return true if this message is enabled.
     */
    public boolean isEnabled()
    {
        return true;
    }

    /**
     * Final send message method, called from evey other method.
     *
     * @param target    target of message.
     * @param lang      language to use if possible.
     * @param component ready to send component.
     *
     * @return true if message was send.
     */
    public boolean handleMessage(final MessageReceiver target, final Locale lang, final BaseComponent component)
    {
        if (! this.isEnabled())
        {
            return false;
        }
        if (component == null)
        {
            return false;
        }
        target.sendMessage(component);
        return true;
    }

    /**
     * Try send this message to given {@link MessageReceiver}, if message is disabled method will just return false.
     *
     * @param target target of message.
     * @param lang   language to use if possible.
     * @param data   placeholder objects to use.
     *
     * @return true if message was send.
     */
    public boolean sendMessage(final MessageReceiver target, final Locale lang, final MessageData... data)
    {
        final BaseComponent msg = this.get(lang, data);
        return this.handleMessage(target, lang, msg);
    }

    /**
     * Try send this message to given {@link MessageReceiver}, if message is disabled method will just return false.
     *
     * @param target target of message.
     * @param data   placeholder objects to use.
     *
     * @return true if message was send.
     */
    public boolean sendMessage(final MessageReceiver target, final MessageData... data)
    {
        return this.sendMessage(target, target.getPreferredLocale(), data);
    }

    /**
     * Try broadcast this message (to all players), if message is disabled method will just return false.
     *
     * @param lang language to use if possible.
     * @param data placeholder objects to use.
     *
     * @return true if message was send.
     */
    public boolean broadcastStaticMessage(final Locale lang, final MessageData... data)
    {
        final BaseComponent msg = this.get(lang, data);
        return this.handleMessage(this.api.getBroadcastReceiver(), lang, msg);
    }

    /**
     * Try broadcast this message to selected comamnd senders, if message is disabled method will just return false.
     *
     * @param targets targets of message.
     * @param lang    language to use if possible.
     * @param data    placeholder objects to use.
     *
     * @return true if message was send.
     */
    public boolean broadcastStaticMessage(final Iterable<? extends MessageReceiver> targets, final Locale lang, final MessageData... data)
    {
        final BaseComponent msg = this.get(lang, data);
        if (msg == null)
        {
            return false;
        }
        targets.forEach(s -> this.handleMessage(s, lang, msg));
        return true;
    }

    /**
     * Try broadcast this message (to all players) in target player language if possible, if message is disabled method will just return false.
     *
     * @param lang default language to use if target don't have any.
     * @param data placeholder objects to use.
     *
     * @return true if message was send.
     */
    public boolean broadcastMessage(final Locale lang, final MessageData... data)
    {
        return this.broadcastMessage(this.api.getReceivers(), lang, data);
    }

    /**
     * Try broadcast this message to selected comamnd senders in target sender language if possible, if message is disabled method will just return false.
     *
     * @param targets targets of message.
     * @param lang    default language to use if target don't have any.
     * @param data    placeholder objects to use.
     *
     * @return true if message was send.
     */
    public boolean broadcastMessage(final Iterable<? extends MessageReceiver> targets, Locale lang, final MessageData... data)
    {
        if (lang == null)
        {
            lang = this.api.getLanguages()[0];
        }
        final Map<Locale, BaseComponent> groups = new HashMap<>(this.api.getLanguages().length);
        boolean anyMsgSent = false;
        for (final MessageReceiver target : targets)
        {
            Locale locale = target.getPreferredLocale();
            if (locale == null)
            {
                locale = this.api.getLanguages()[0];
            }
            BaseComponent msg;
            if (groups.containsKey(locale))
            {
                msg = groups.get(locale);
            }
            else
            {
                msg = this.get(locale, data);
                groups.put(locale, msg);
            }
            if (msg == null)
            {
                if (groups.containsKey(lang))
                {
                    msg = groups.get(lang);
                }
                else
                {
                    msg = this.get(lang, data);
                    groups.put(lang, msg);
                }
                if (msg == null)
                {
                    continue;
                }
            }
            if (this.handleMessage(target, lang, msg))
            {
                anyMsgSent = true;
            }
        }
        return anyMsgSent;
    }

    /**
     * Try broadcast this message (to all players) in target player language if possible, if message is disabled method will just return false.
     *
     * @param data placeholder objects to use.
     *
     * @return true if message was send.
     */
    public boolean broadcastMessage(final MessageData... data)
    {
        return this.broadcastMessage(this.api.getReceivers(), null, data);
    }

    /**
     * Try broadcast this message to selected comamnd senders in target sender language if possible, if message is disabled method will just return false.
     *
     * @param targets targets of message.
     * @param data    placeholder objects to use.
     *
     * @return true if message was send.
     */
    public boolean broadcastMessage(final Iterable<? extends MessageReceiver> targets, final MessageData... data)
    {
        return this.broadcastMessage(targets, null, data);
    }

    /**
     * Represent placeholder data, name of object and object instance.
     */
    public static class MessageData
    {
        private final String name;
        private final Object object;

        /**
         * Construct new message placeholder data, with given name and object.
         *
         * @param name   name of placeholder object.
         * @param object object instance used in placeholder.
         */
        public MessageData(final String name, final Object object)
        {
            this.name = name;
            this.object = object;
        }

        /**
         * Simpler costructor for this object. <br>
         * Construct new message placeholder data, with given name and object.
         *
         * @param name   name of placeholder object.
         * @param object object instance used in placeholder.
         *
         * @return new instance of {@link MessageData}
         */
        public static MessageData e(final String name, final Object object)
        {
            return new MessageData(name, object);
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("object", this.object).toString();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BaseComponent replace(BaseComponent component, final Map<String, Collection<PlaceholderData<?>>> placeholders, final MessageData... data)
    {
        component = PlaceholderType.replaceGlobal(component, placeholders);
        for (final MessageData d : data)
        {
            final Collection<PlaceholderData<?>> placeholderDatas = placeholders.get(d.name);
            if (placeholderDatas == null)
            {
                continue;
            }
            for (final PlaceholderData placeholderData : placeholderDatas)
            {
                placeholderData.replace(component, d.object);
            }
        }
        return component;
    }

    private static class DisabledMessage extends Message
    {
        DisabledMessage(final MessagesAPI api)
        {
            super(api, Collections.emptyMap());
        }

        @Override
        public boolean isEnabled()
        {
            return false;
        }

        @Override
        public BaseComponent get(final Locale lang, final MessageData... data)
        {
            return null;
        }

        @Override
        public Map<Locale, Map<String, Object>> toMap(final Map<Locale, Map<String, Object>> map, final Locale defaultLanguage, final String node)
        {
            Map<String, Object> msgMap = map.get(defaultLanguage);
            if (msgMap == null)
            {
                msgMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                map.put(defaultLanguage, msgMap);
            }
            msgMap.put(node, false);
            return map;
        }

        @Override
        public String toString()
        {
            return "false";
        }
    }

    private static class SimpleMessage extends Message
    {
        private final BaseComponent msg;

        SimpleMessage(final MessagesAPI api, final String msg)
        {
            super(api, PlaceholderData.parseString(api, msg, true));
            this.msg = (msg == null) ? null : ComponentSerializer.safeParse(msg, '&');
        }

        @Override
        public BaseComponent get(final Locale lang, final MessageData... data)
        {
            if (this.msg == null)
            {
                return null;
            }
            return replace(this.msg, this.placeholders, data);
        }

        @Override
        public Map<Locale, Map<String, Object>> toMap(final Map<Locale, Map<String, Object>> map, final Locale defaultLanguage, final String node)
        {
            Map<String, Object> msgMap = map.get(defaultLanguage);
            if (msgMap == null)
            {
                msgMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                map.put(defaultLanguage, msgMap);
            }
            msgMap.put(node, this.msg.canBeLegacy() ? ChatColor.removeColorCodesInString('&', this.msg.toLegacyText()) : ComponentSerializer.toString(this.msg));
            return map;
        }

        @Override
        public String toString()
        {
            if (this.msg == null)
            {
                return "";
            }
            return this.msg.toPlainText();
        }
    }

    private static class SimpleRandomMessage extends Message
    {
        private final BaseComponent[] msg;

        SimpleRandomMessage(final MessagesAPI api, final String[] msg)
        {
            super(api, PlaceholderData.parseString(api, (msg == null) ? null : StringUtils.join(msg), true));
            this.msg = (msg == null) ? null : ComponentSerializer.safeParse(msg, '&');
        }

        @Override
        public BaseComponent get(final Locale lang, final MessageData... data)
        {
            if ((this.msg == null) || (this.msg.length == 0))
            {
                return null;
            }
            return replace(this.msg[random.nextInt(this.msg.length)], this.placeholders, data);
        }

        @Override
        public Map<Locale, Map<String, Object>> toMap(final Map<Locale, Map<String, Object>> map, final Locale defaultLanguage, final String node)
        {
            Map<String, Object> msgMap = map.get(defaultLanguage);
            if (msgMap == null)
            {
                msgMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                map.put(defaultLanguage, msgMap);
            }
            final Collection<String> list = new ArrayList<>(this.msg.length);
            for (final BaseComponent component : this.msg)
            {
                list.add(component.canBeLegacy() ? ChatColor.removeColorCodesInString('&', component.toLegacyText()) : ComponentSerializer.toString(component));
            }
            msgMap.put(node, list);
            return map;
        }

        @Override
        public String toString()
        {
            if (this.msg.length == 0)
            {
                return "";
            }
            return this.msg[0].toPlainText();
        }
    }


    private static class LocalizedMessage extends Message
    {
        private final Map<Locale, BaseComponent> msg;

        LocalizedMessage(final MessagesAPI api, final Map<Locale, String> msg)
        {
            super(api, PlaceholderData.parseString(api, (msg == null) ? null : StringUtils.join(msg.values(), ' '), true));
            if (msg == null)
            {
                this.msg = null;
                return;
            }
            this.msg = new HashMap<>(msg.size(), .1f);
            for (final Entry<Locale, String> entry : msg.entrySet())
            {
                this.msg.put(entry.getKey(), ComponentSerializer.safeParse(entry.getValue(), '&'));
            }
        }

        @Override
        public BaseComponent get(final Locale lang, final MessageData... data)
        {
            if ((this.msg == null) || this.msg.isEmpty())
            {
                return null;
            }
            final BaseComponent selected;
            if (lang == null)
            {
                selected = this.msg.get(this.api.getLanguages()[0]);
            }
            else
            {
                selected = this.msg.getOrDefault(lang, this.msg.get(this.api.getLanguages()[0]));
            }
            if (selected == null)
            {
                return null;
            }
            return replace(selected, this.placeholders, data);
        }

        @Override
        public Map<Locale, Map<String, Object>> toMap(final Map<Locale, Map<String, Object>> map, final Locale defaultLanguage, final String node)
        {
            return Message.toMap1(this.msg, map, node);
        }

        @Override
        public String toString()
        {
            if ((this.msg == null) || this.msg.isEmpty())
            {
                return "";
            }
            final BaseComponent msg = this.msg.get(this.api.getLanguages()[0]);
            if (msg == null)
            {
                return "";
            }
            return msg.toPlainText();
        }
    }

    private static class LocalizedRandomMessage extends Message
    {
        private final Map<Locale, BaseComponent[]> msg;

        private static String joinString(final Map<Locale, String[]> msg)
        {
            if ((msg == null) || msg.isEmpty())
            {
                return null;
            }
            return msg.values().stream().map(StringUtils::join).reduce((a, b) -> a + b).orElse(null);
        }

        LocalizedRandomMessage(final MessagesAPI api, final Map<Locale, String[]> msg)
        {
            super(api, PlaceholderData.parseString(api, joinString(msg), true));
            if (msg == null)
            {
                this.msg = null;
                return;
            }
            this.msg = new HashMap<>(msg.size(), .1f);
            for (final Entry<Locale, String[]> entry : msg.entrySet())
            {
                this.msg.put(entry.getKey(), ComponentSerializer.safeParse(entry.getValue(), '&'));
            }
        }

        @Override
        public BaseComponent get(final Locale lang, final MessageData... data)
        {
            if ((this.msg == null) || this.msg.isEmpty())
            {
                return null;
            }
            final BaseComponent[] selected;
            if (lang == null)
            {
                selected = this.msg.get(this.api.getLanguages()[0]);
            }
            else
            {
                selected = this.msg.getOrDefault(lang, this.msg.get(this.api.getLanguages()[0]));
            }
            if ((selected == null) || (selected.length == 0))
            {
                return null;
            }
            return replace(selected[random.nextInt(selected.length)], this.placeholders, data);
        }

        @Override
        public Map<Locale, Map<String, Object>> toMap(final Map<Locale, Map<String, Object>> map, final Locale defaultLanguage, final String node)
        {
            return Message.toMap2(this.msg, map, node);
        }

        @Override
        public String toString()
        {
            if ((this.msg == null) || this.msg.isEmpty())
            {
                return "";
            }
            final BaseComponent[] msg = this.msg.get(this.api.getLanguages()[0]);
            if ((msg == null) || (msg.length == 0))
            {
                return "";
            }
            return msg[0].toPlainText();
        }
    }

    private static class LocalizedMixedMessage extends Message
    {
        private final Map<Locale, BaseComponent>   msg1;
        private final Map<Locale, BaseComponent[]> msg2;

        private static String joinString(final Map<Locale, String> msg1, final Map<Locale, String[]> msg2)
        {
            final String msg1Str = (msg1 == null) ? null : StringUtils.join(msg1.values(), ' ');
            final String msg2Str = LocalizedRandomMessage.joinString(msg2);
            return (msg1Str == null) ? msg2Str : (msg1Str + ((msg2Str == null) ? "" : msg2Str));
        }

        LocalizedMixedMessage(final MessagesAPI api, final Map<Locale, String> msg1, final Map<Locale, String[]> msg2)
        {
            super(api, PlaceholderData.parseString(api, joinString(msg1, msg2), true));
            if (msg1 == null)
            {
                this.msg1 = null;
            }
            else
            {
                this.msg1 = new HashMap<>(msg1.size(), .1f);
                for (final Entry<Locale, String> entry : msg1.entrySet())
                {
                    this.msg1.put(entry.getKey(), ComponentSerializer.safeParse(entry.getValue(), '&'));
                }
            }
            if (msg2 == null)
            {
                this.msg2 = null;
            }
            else
            {
                this.msg2 = new HashMap<>(msg2.size(), .1f);
                for (final Entry<Locale, String[]> entry : msg2.entrySet())
                {
                    this.msg2.put(entry.getKey(), ComponentSerializer.safeParse(entry.getValue(), '&'));
                }
            }
        }

        @Override
        public BaseComponent get(Locale lang, final MessageData... data)
        {
            if (lang == null)
            {
                lang = this.api.getLanguages()[0];
            }
            {
                BaseComponent selected = null;
                if ((this.msg1 != null) && ! this.msg1.isEmpty())
                {
                    selected = this.msg1.getOrDefault(lang, this.msg1.get(this.api.getLanguages()[0]));
                }
                if (selected != null)
                {
                    return replace(selected, this.placeholders, data);
                }
            }
            if ((this.msg2 == null) || this.msg2.isEmpty())
            {
                return null;
            }
            final BaseComponent[] selected = this.msg2.getOrDefault(lang, this.msg2.get(this.api.getLanguages()[0]));
            if ((selected == null) || (selected.length == 0))
            {
                return null;
            }
            return replace(selected[random.nextInt(selected.length)], this.placeholders, data);
        }

        @Override
        public Map<Locale, Map<String, Object>> toMap(final Map<Locale, Map<String, Object>> map, final Locale defaultLanguage, final String node)
        {
            Message.toMap1(this.msg1, map, node);
            Message.toMap2(this.msg2, map, node);
            return map;
        }

        @Override
        public String toString()
        {
            if ((this.msg1 == null) || this.msg1.isEmpty())
            {
                if ((this.msg2 == null) || this.msg2.isEmpty())
                {
                    return "";
                }
                final BaseComponent[] msg = this.msg2.get(this.api.getLanguages()[0]);
                if ((msg == null) || (msg.length == 0))
                {
                    return "";
                }
                return msg[0].toPlainText();
            }
            final BaseComponent msg = this.msg1.get(this.api.getLanguages()[0]);
            if (msg == null)
            {
                return "";
            }
            return msg.toPlainText();
        }
    }

    private static Map<Locale, Map<String, Object>> toMap1(final Map<Locale, BaseComponent> source, final Map<Locale, Map<String, Object>> map, final String node)
    {
        for (final Entry<Locale, BaseComponent> entry : source.entrySet())
        {
            final Locale locale = entry.getKey();
            final BaseComponent msg = entry.getValue();
            Map<String, Object> msgMap = map.get(locale);
            if (msgMap == null)
            {
                msgMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                map.put(locale, msgMap);
            }
            msgMap.put(node, msg.canBeLegacy() ? ChatColor.removeColorCodesInString('&', msg.toLegacyText()) : ComponentSerializer.toString(msg));
        }
        return map;
    }

    private static Map<Locale, Map<String, Object>> toMap2(final Map<Locale, BaseComponent[]> source, final Map<Locale, Map<String, Object>> map, final String node)
    {
        for (final Entry<Locale, BaseComponent[]> entry : source.entrySet())
        {
            final Locale locale = entry.getKey();
            final BaseComponent[] msgs = entry.getValue();
            Map<String, Object> msgMap = map.get(locale);
            if (msgMap == null)
            {
                msgMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                map.put(locale, msgMap);
            }
            final Collection<String> list = new ArrayList<>(msgs.length);
            for (final BaseComponent component : msgs)
            {
                list.add(component.canBeLegacy() ? ChatColor.removeColorCodesInString('&', component.toLegacyText()) : ComponentSerializer.toString(component));
            }
            msgMap.put(node, list);
        }
        return map;
    }
}
