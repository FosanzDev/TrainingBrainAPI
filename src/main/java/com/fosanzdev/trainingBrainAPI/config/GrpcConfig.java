package com.fosanzdev.trainingBrainAPI.config;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.AuthInterceptor;
import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @Bean
    public GlobalServerInterceptorConfigurer globalInterceptorConfigurer() {
        return registry -> registry.add(new AuthInterceptor());
    }
}
