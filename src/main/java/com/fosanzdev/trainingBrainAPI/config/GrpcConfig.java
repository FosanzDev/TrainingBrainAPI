package com.fosanzdev.trainingBrainAPI.config;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {
    /*
     * This class is used to configure the global interceptor for the gRPC server.
     * The interceptor is later used to capture authentication tokens from the
     * request headers and validate them.
     */

    @Bean
    public GlobalServerInterceptorConfigurer globalInterceptorConfigurer() {
        return registry -> registry.add(new AuthInterceptor());
    }
}
