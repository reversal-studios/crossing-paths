package com.logandhillon.logangamelib.io;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.logandhillon.logangamelib.engine.LGLGameHandler;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Handles (de)serialization of save game data and (un)loading said data to the disk.
 *
 * @author Logan Dhillon
 */
public abstract class SaveFileManager<M extends Message> {
    private static final Logger LOG = LoggerContext.getContext().getLogger(SaveFileManager.class);

    private final File      file;
    private final String    prefix;
    private final Parser<M> parser;
    private final M         defaultConfig;

    /**
     * Creates a new user config manager with a custom filepath for the save file: the file that is managed by this
     * instance.
     *
     * @param prefix prefix of the save file (i.e.: [prefix].logangamelib.dat
     */
    public SaveFileManager(String prefix, Parser<M> parser) {
        this.file = LGLGameHandler.getInstance().getPathManager().getFile(prefix + ".logangamelib.dat");
        LOG.debug("Set managed file for \"{}\" save file to {}", prefix, file.getAbsolutePath());
        this.prefix = prefix;
        this.defaultConfig = getDefault();
        this.parser = parser;
    }

    /**
     * Saves the provided {@link Message} to the disk.
     *
     * @return user config that is now saved to disk
     *
     * @throws RuntimeException if the user config cannot be saved to disk
     */
    public M save(M config) {
        File parent = this.file.getParentFile();
        if (parent != null && !parent.exists()) {
            LOG.warn("Parent directory for \"{}\" save file file doesn't exist, creating folder(s)", prefix);
            //noinspection ResultOfMethodCallIgnored
            parent.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(this.file)) {
            LOG.info("Writing \"{}\" save file to {}", prefix, this.file.getAbsolutePath());
            config.writeTo(fos);
            return config;
        } catch (IOException e) {
            LOG.error("Failed to save \"{}\" save file to {}", prefix, file.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to load the {@link Message} from disk, returning the {@link SaveFileManager#defaultConfig} if it (a) fails
     * or (b) doesn't exist.
     *
     * @return user config from disk
     */
    public M load() {
        // if there is no save file, save the default and return
        if (!file.exists()) {
            LOG.warn("Saved user config doesn't exist, saving default config to disk");
            save(defaultConfig);
            return defaultConfig;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            return build(defaultConfig.toBuilder().mergeFrom(parser.parseFrom(fis)));
        } catch (IOException e) {
            LOG.error("Failed to load user configuration from {}", file.getAbsolutePath(), e);
            return defaultConfig;
        }
    }

    /**
     * Updates only the fields specified and saves the resulting config.
     *
     * @param partial the partial values, whatever is set here will be updated, otherwise it will remain the same.
     */
    public M update(M current, M partial) {
        LOG.debug("Updating {} save file with {}", prefix, partial.toString().trim());
        M merged = build(current.toBuilder().mergeFrom(partial));
        return save(merged);
    }

    protected abstract M getDefault();

    /**
     * Builds a working protobuf into a message.
     */
    @SuppressWarnings("unchecked")
    private M build(Message.Builder builder) {
        return (M)builder.build();
    }
}
