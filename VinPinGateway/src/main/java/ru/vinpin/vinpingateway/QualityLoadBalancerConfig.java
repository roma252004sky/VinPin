package ru.vinpin.vinpingateway;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;


@LoadBalancerClients({
        @LoadBalancerClient(name = "front",configuration = InstanceSupplier.class),
        @LoadBalancerClient(name = "grafana",configuration = InstanceSupplier.class),
        @LoadBalancerClient(name = "main-app",configuration = InstanceSupplier.class),
        @LoadBalancerClient(name = "integration",configuration = InstanceSupplier.class)
})
@Configuration
public class QualityLoadBalancerConfig {
}
