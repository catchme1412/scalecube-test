package com.appviewx.gossip;

import reactor.core.publisher.Mono;

public class AvxSaasServiceImpl implements AvxSaasService {
    public AvxSaasServiceImpl(String main) {
        super();
    }

    @Override
    public Mono<Object> invoke(Object payload) {
        return Mono.just("Invoked");
    }
}
