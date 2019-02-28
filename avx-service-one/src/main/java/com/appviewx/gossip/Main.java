package com.appviewx.gossip;

import io.scalecube.config.ConfigRegistry;
import io.scalecube.services.Microservices;
import io.scalecube.services.transport.api.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class Main {



    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String DECORATOR =
            "***********************************************************************";

    public static  void main(String args[]) throws InterruptedException {
        System.out.println("Starting...");
        //Read config
        ConfigRegistry configRegistry = AvxConfigBootstrap.configRegistry();

        AvxConfig config =
                configRegistry
                        .objectProperty("io.scalecube.services.examples", AvxConfig.class)
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
        Microservices.builder()
                .discovery(
                        options ->
                                options
                                        .seeds(
                                                Arrays.stream(config.seedAddresses())
                                                        .map(address -> Address.create(address.host(), address.port()))
                                                        .toArray(Address[]::new))
                                        .port(config.discoveryPort())
                                        .memberHost(config.memberHost())
                                        .memberPort(config.memberPort()))
                .transport(options -> options.numOfThreads(numOfThreads).port(config.servicePort()))
                .services(new AvxSaasServiceImpl("main"))
                .startAwait();
        //Wait for request

        Thread.currentThread().join();

    }


}
