package com.fosanzdev.trainingBrainAPI.controllers.grpc;

import io.grpc.*;
import org.springframework.stereotype.Component;

@Component
public class AuthInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
    private static final Context.Key<String> AUTHORIZATION_CTX_KEY = Context.key("authorization");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String authorization = metadata.get(AUTHORIZATION_METADATA_KEY);

        Context context = Context.current().withValue(AUTHORIZATION_CTX_KEY, authorization);

        return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
    }

    public static String getAuthorization() {
        return AUTHORIZATION_CTX_KEY.get();
    }
}
