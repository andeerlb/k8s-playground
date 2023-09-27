package com.api.gateway.Gateway;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
	@Bean
	public HttpClient httpClient() {
		return HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
	}

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes().build();
	}

	public class CustomLoadBalancerConfiguration {

		@Bean
		ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment environment,
																LoadBalancerClientFactory loadBalancerClientFactory) {
			String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
			return new RandomLoadBalancer(loadBalancerClientFactory
					.getLazyProvider(name, ServiceInstanceListSupplier.class),
					name);
		}
	}
}
