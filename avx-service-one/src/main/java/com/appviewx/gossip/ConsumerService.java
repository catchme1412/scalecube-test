package com.appviewx.gossip;

import io.scalecube.config.ConfigRegistry;
import io.scalecube.services.Microservices;
import io.scalecube.services.api.ServiceMessage;
import io.scalecube.services.discovery.api.ServiceDiscoveryEvent;
import io.scalecube.services.gateway.GatewayRunner;
import io.scalecube.services.transport.api.Address;
import org.apache.logging.log4j.Level;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Arrays;

public class ConsumerService {

    static {

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);
    private static final String DECORATOR =
            "***********************************************************************";
    static final String SERVICE_QUALIFIER = "/io.scalecube.Greetings/sayHello";

    public static void main(String args[]) throws InterruptedException {
        System.out.println("Starting consumer...");

        //Read config
        ConfigRegistry configRegistry = GatewayRunner.ConfigBootstrap.configRegistry();

        Main.Config config =
                configRegistry
                        .objectProperty("io.scalecube.services.examples", Main.Config.class)
                        .value()
                        .orElseThrow(() -> new IllegalStateException("Couldn't load config"));

// ScaleCube Node node with no members


        // Create microservice consumer
//        Microservices consumer = Microservices.builder()
//                .discovery(options -> options.seeds( Arrays.stream(config.seedAddresses())
//                        .map(address -> Address.create(address.host(), address.port()))
//                        .toArray(Address[]::new)))
//		.startAwait();

        // Create microservice consumer
        Microservices consumer = Microservices.builder()
                .discovery(options -> options.seeds( Arrays.stream(config.seedAddresses())
                        .map(address -> Address.create(address.host(), address.port()))

                        .toArray(Address[]::new)))

                .startAwait();


        // Create a ServiceMessage request with service qualifier and data
        ServiceMessage request =
                ServiceMessage.builder().qualifier(SERVICE_QUALIFIER).data("joe").build();
        // Execute the Greeting Service to emit a single Greeting response
        Publisher<ServiceMessage> publisher = consumer.call().create().requestOne(request, Object.class);

        consumer.discovery().listen().subscribe(e -> print(e));

        // Convert the Publisher using the Mono API which ensures it will emit 0 or 1 item.
        Mono.from(publisher).doOnNext(r -> {
            System.out.println("Next called");
        })
                .doOnError(r -> {
                    System.out.println("Some error thrown??????? " + r.getCause());
                })
                .subscribe(
                        r -> {
                            // handle service response
                            System.out.println("RESPONSE :::" + r);
                            Object greeting = r.data();
                            System.out.println(greeting.toString());
                        });


        for (int i = 0; i < 200 ; i++) {
            Thread.currentThread().sleep(1000);
            publisher = consumer.call().create().requestOne(request, Object.class);

            // Convert the Publisher using the Mono API which ensures it will emit 0 or 1 item.
            Mono.from(publisher).doOnNext(r -> {
                System.out.println("Next called");
            })
                    .doOnError(r -> {
                        System.out.println("Some error thrown??????? " + r.getCause());
                    })
//                    .cast(AvxResponse.class)
//                    .doOnCancel()
                    .subscribe(
                            r -> {
                                // handle service response
                                System.out.println("RESPONSE :::" + r);
//                                Object greeting = r.getMessage();
                                Object greeting = r.data();
                                System.out.println(greeting.toString());
                            });

        }

        Thread.currentThread().join();

//
//
//        //Get a handler to the service
//        // Get a proxy to the service API.
//        ServiceCall serviceCall = consumer.call().create();
//
//        // Creates a service proxy passing the service interface class
//        AvxService greetingService = serviceCall.api(AvxService.class);
//
//
//        //invoke the service
//
//        AvxResponse publisher = greetingService.sayHello("name").block();
//        System.out.println(publisher.getMessage());
////        ((Mono<AvxResponse>) publisher).doOnRequest(r ->{
////            System.out.println(r);
////        });
////        AvxResponse response =
////                (AvxResponse) Mono.from(publisher).subscribe(result -> {
////                    System.out.println(result.getMessage());
////                });
////
////        Flux<AvxResponse> stream = (Flux<AvxResponse>) greetingService.sayHello("rajesh")
////                .subscribe(onNext -> {
////                    System.out.println(onNext.getMessage());
////                });getMessage
////        //shutdown


    }
    private static void print(ServiceDiscoveryEvent e) {
        System.out.println("EVENT: " + e);
    }
}
