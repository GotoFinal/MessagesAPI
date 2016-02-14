package com.gotofinal.messages.api;

import java.util.function.Function;

import com.gotofinal.messages.api.chat.component.BaseComponent;

public interface ChatConverter<T> extends Function<BaseComponent, T>
{
}
