package ru.vinpin.vinpingateway;

import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

class InstanceSupplier {
    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier(ConfigurableApplicationContext context) {
        ServiceInstanceListSupplier supplier =  ServiceInstanceListSupplier
                .builder()
                .withBlockingDiscoveryClient()
                .withZonePreference()
                .build(context);
        return supplier;
    }
}
