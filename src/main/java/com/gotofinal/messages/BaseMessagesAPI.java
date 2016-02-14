package com.gotofinal.messages;

import java.util.Locale;

import com.gotofinal.messages.api.MessagesAPI;
import com.gotofinal.messages.api.messages.MessageLoader;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class BaseMessagesAPI implements MessagesAPI
{
    protected final MessageLoader loader = new MessageLoader(this);

    protected Locale[] locales;

    public BaseMessagesAPI(final Locale... locales)
    {
        if (locales.length == 0)
        {
            this.locales = new Locale[]{Locale.ENGLISH};
        }
        else
        {
            this.locales = locales;
        }
    }

    /**
     * Returns languages supported by this api instance.
     *
     * @return languages supported by this api instance.
     */
    @Override
    public Locale[] getLanguages()
    {
        return this.locales;
    }

    /**
     * Sets array of languages supported by this api instance.
     *
     * @param locales languages supported by this api instance.
     */
    public void setLanguages(final Locale[] locales)
    {
        this.locales = locales;
    }

    /**
     * Returns message loader for this api instance.
     *
     * @return message loader for this api instance.
     */
    @Override
    public MessageLoader getMessageLoader()
    {
        return this.loader;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("loader", this.loader).append("locales", this.locales).toString();
    }
}
