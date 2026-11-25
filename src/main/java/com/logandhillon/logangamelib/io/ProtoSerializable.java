package com.logandhillon.logangamelib.io;

import com.google.protobuf.Message;

/**
 * Interface for a class that can be serialized to a protobuf {@link Message}.
 *
 * @param <T> type of message to serialize to
 *
 * @author Logan Dhillon
 * @implNote it is recommended to create a {@code load(Message)} static method to convert Messages back into the
 * corresponding object.
 */
public interface ProtoSerializable<T extends Message> {
    /**
     * Serializes this instance to a protobuf {@link Message} of type {@code <T>}
     *
     * @return serialized protobuf message
     */
    T serialize();
}
