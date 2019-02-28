package com.appviewx.gossip;

import io.scalecube.config.ConfigRegistry;
import io.scalecube.config.ConfigRegistrySettings;
import io.scalecube.config.audit.Slf4JConfigEventListener;
import io.scalecube.config.source.ClassPathConfigSource;
import io.scalecube.config.source.SystemEnvironmentConfigSource;
import io.scalecube.config.source.SystemPropertiesConfigSource;
import io.scalecube.services.Microservices;
import io.scalecube.services.gateway.GatewayRunner;
import io.scalecube.services.transport.api.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MainCopy {

    static {

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(MainCopy.class);
    private static final String DECORATOR =
            "***********************************************************************";

    public static  void main(String args[]) throws InterruptedException {
        System.out.println("Starting...");
        //Read config
        ConfigRegistry configRegistry = GatewayRunner.ConfigBootstrap.configRegistry();

        Config config =
                configRegistry
                        .objectProperty("io.scalecube.services.examples", Config.class)
                        .value()
                        .orElseThrow(() -> new IllegalStateException("Couldn't load config"));


        LOGGER.info(DECORATOR);
        LOGGER.info("Starting Examples services on {}", config);
        LOGGER.info(DECORATOR);

        int numOfThreads =
                Optional.ofNullable(config.numOfThreads())
                        .orElse(Runtime.getRuntime().availableProcessors());
        LOGGER.info("Number of worker threads: " + numOfThreads);
        //Start the gateway
        //Register the service
        // Create microservice consumer
        Microservices consumer = Microservices.builder()
                .discovery(options -> options.seeds( Arrays.stream(config.seedAddresses())
                        .map(address -> Address.create(address.host(), address.port()))
                        .toArray(Address[]::new)))
                .services(new AvxSaasServiceImpl("mainCopy"))
                .startAwait();

        //Wait for request

        Thread.currentThread().join();

    }



    public static class Config {

        private int servicePort;
        private int discoveryPort;
        private Integer numOfThreads;
        private List<String> seeds;
        private String memberHost;
        private Integer memberPort;

        public int servicePort() {
            return servicePort;
        }

        public int discoveryPort() {
            return discoveryPort;
        }

        public Integer numOfThreads() {
            return numOfThreads;
        }

        public List<String> seeds() {
            return seeds;
        }

        /**
         * Returns seeds as an {@link Address}'s array.
         *
         * @return {@link Address}'s array
         */
        public Address[] seedAddresses() {
            return Optional.ofNullable(seeds())
                    .map(seeds -> seeds.stream().map(Address::from).toArray(Address[]::new))
                    .orElse(new Address[0]);
        }

        public String memberHost() {
            return memberHost;
        }

        public Integer memberPort() {
            return memberPort;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Config{");
            sb.append("servicePort=").append(servicePort);
            sb.append(", discoveryPort=").append(discoveryPort);
            sb.append(", numOfThreads=").append(numOfThreads);
            sb.append(", seeds=").append(seeds);
            sb.append(", memberHost=").append(memberHost);
            sb.append(", memberPort=").append(memberPort);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class ConfigBootstrap {

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
                            .jmxEnabled(false)
                            .build());
        }
    }
}
