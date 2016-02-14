package com.gotofinal.messages.api;

import java.util.function.Function;

public interface ReceiverConverter<T> extends Function<T, MessageReceiver>
{
}
