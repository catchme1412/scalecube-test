package com.appviewx.gossip;

import io.scalecube.config.ConfigRegistry;
import io.scalecube.config.ConfigRegistrySettings;
import io.scalecube.config.audit.Slf4JConfigEventListener;
import io.scalecube.config.source.ClassPathConfigSource;
import io.scalecube.config.source.SystemEnvironmentConfigSource;
import io.scalecube.config.source.SystemPropertiesConfigSource;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class AvxConfigBootstrap {

    private static final Pattern CONFIG_PATTERN = Pattern.compile("(.*)config(.*)?\\.properties");
    private static final Predicate<Path> PATH_PREDICATE =
            path -> CONFIG_PATTERN.matcher(path.toString()).matches();

    /**
     * ConfigRegistry method factory.
     *
     * @return configRegistry
     */
    public static ConfigRegistry configRegistry() {
        return ConfigRegistry.create(
                ConfigRegistrySettings.builder()
                        .addListener(new Slf4JConfigEventListener())
                        .addLastSource("sys_prop", new SystemPropertiesConfigSource())
                        .addLastSource("env_var", new SystemEnvironmentConfigSource())
                        .addLastSource("cp", new ClassPathConfigSource(PATH_PREDICATE))
                        .jmxEnabled(true)
                        .build());
    }
}
