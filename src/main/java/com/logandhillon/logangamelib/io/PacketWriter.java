package com.logandhillon.logangamelib.io;

import com.logandhillon.fptgame.networking.GamePacket;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * A packet writer internally uses a {@link PrintWriter}, but instead of using Strings to communicate, it uses a
 * {@link GamePacket}.
 * <p>
 * Such packet is then serialized and written to the output stream {using {@link PacketWriter#send(GamePacket)}}
 * <p>
 * This class it {@link AutoCloseable}, and should be used with a try-with-resources statement.
 *
 * @author Logan Dhillon
 */
public class PacketWriter implements AutoCloseable {
    private static final Logger LOG = LoggerContext.getContext().getLogger(PacketWriter.class);

    private final DataOutputStream out;

    /**
     * Creates the internal print writer from an output stream
     *
     * @param out the output stream to write to
     */
    public PacketWriter(OutputStream out) {
        this.out = new DataOutputStream(out);
    }

    /**
     * Serializes and writes a packet to the output stream, effectively "sending" it.
     *
     * @param packet the {@link GamePacket} to send.
     */
    public void send(GamePacket packet) {
        LOG.debug("Sending {} packet", packet.type());

        try {
            byte[] data = packet.serialize();
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds a new NULL packet using the specified type and sends it.
     *
     * @param packetType the packet type to send
     */
    public void send(GamePacket.Type packetType) {
        send(new GamePacket(packetType));
    }

    /**
     * Closes the internal {@link PrintWriter}.
     */
    @Override
    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
