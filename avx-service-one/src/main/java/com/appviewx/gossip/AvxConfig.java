package com.appviewx.gossip;

import io.scalecube.services.transport.api.Address;
import java.util.List;
import java.util.Optional;

public class AvxConfig {

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
        final StringBuilder sb = new StringBuilder("AvxConfig{");
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
