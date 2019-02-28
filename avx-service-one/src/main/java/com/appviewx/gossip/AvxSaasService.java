package com.appviewx.gossip;

import io.scalecube.services.annotations.Service;
import io.scalecube.services.annotations.ServiceMethod;
import reactor.core.publisher.Mono;

@Service("io.scalecube.Greetings")
public interface AvxSaasService {

    @ServiceMethod("sayHello")
    public Mono<Object> invoke(Object payload);
}
